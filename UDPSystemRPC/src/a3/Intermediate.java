package udpsystemrpc;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * SYSC 3303 Assignment 3
 * 
 * Intermediate Host class for a three part system that communicates with each other using UDP.
 * @author Valerie Figuracion
 * @since February 25, 2020
 */

public class Intermediate extends Thread{
	
	ArrayList<byte[]> dataQ = new ArrayList<byte[]>();
	ArrayList<byte[]> ackQ = new ArrayList<byte[]>();
	ArrayList<byte[]> clientRequestQ = new ArrayList<byte[]>();
	ArrayList<byte[]> serverRequestQ = new ArrayList<byte[]>();
	
	String name = "Intermediate";	
	
	Packet packet = new Packet();
	
	byte[] pkg;
	byte[] reply;
	
	public Intermediate() {
		packet.constructSocket(23);
		SendToServer sts = new SendToServer();
		SendToClient stc = new SendToClient();
		sts.start();
		stc.start();
	}
	
	@Override
	public synchronized void run() {	
		while(true) {
			try {
				//Receive packet from either Client or Server							
				pkg = packet.receivePacket(name);
				
				//Request from Server
				if (pkg[0] == 8) {
					serverRequestQ.add(pkg);
					System.out.println(Arrays.toString(serverRequestQ.get(0)));
				//Request from Client
				}else if (pkg[0] == 9) {
					clientRequestQ.add(pkg);
					System.out.println(Arrays.toString(clientRequestQ.get(0)));
				}
				//Send reply that the intermediate received data
				else {						
					//Acknowledgement from Server
					if (pkg[1] == 3 || pkg[1] == 4  || pkg[1] == 0) {
						ackQ.add(pkg);
						packet.send(makeReply(), 69, name);
					//Data from Client
					}else{
						dataQ.add(pkg);	
						packet.send(makeReply(), 12, name);
					}
				}
				notifyAll();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * Sends the packet received to its appropriate destination.
	 * @param pkt Packet received by the Intermediate
	 * @return Byte array with the reply
	 */
	public byte[] makeReply() {
		byte reply_code[];
		byte b5 = 5;
		
		ByteArrayOutputStream OS = new ByteArrayOutputStream();
		
		System.out.println("Sending reply...");
		
		OS.write(b5);
		reply_code = OS.toByteArray();
		
		return reply_code;
		
	}
	
	/**
	 * Inner class to send data to the server
	 */
	class SendToServer extends Thread{
		@Override
		public void run() {
			while (true) {
				try {
					//If array is not empty
					if (serverRequestQ.get(0) != null) {
						//Send first data in the queue to the server.
						packet.send(dataQ.get(0), 69, name);
						//Remove that data and remove the request from the server
						//Will reduce the amount of requests from the server
						dataQ.remove(0); serverRequestQ.remove(0);
					}
					notifyAll();
				} catch(Exception e) {}
			}
		}
	}
	
	/**
	 * Inner class to send acknowledgement to the client
	 */
	class SendToClient extends Thread{
		@Override
		public void run() {
			while (true) {
				try {
					//If array is not empty
					if (clientRequestQ.size() != 0) {
						//Send first ack in the queue to the client.
						packet.send(ackQ.get(0), 12, name);
						//Remove that ack and remove the request from the client
						//Will reduce the amount of requests from the client
						ackQ.remove(0); clientRequestQ.remove(0);
					}
					notifyAll();
				} catch(Exception e) {}
			}
		}
	}
	
	/**
	 * Main method of Intermediate class.
	 * @param args
	 */
	public static void main(String[] args) {
		Intermediate i = new Intermediate();
		i.start();
	}
}
