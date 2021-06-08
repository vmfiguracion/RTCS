package udpsystemrpc;

import java.io.ByteArrayOutputStream;

/**
 * SYSC 3303 Assignment 3
 * 
 * Server class for a three part system that communicates with each other using UDP.
 * Server now sends a request to get package from intermediate rather than waiting.
 * @author Valerie Figuracion
 * @since February 25, 2020
 */
public class Server extends Thread{
	
	String name = "Server";
	Packet packet = new Packet();
	
	public Server() {
		//Construct the socket
		packet.constructSocket(69);
	}

	/**
	 * 
	 */
	public void dataRequest() {
		try {
			System.out.println("Sending request for data.");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(8);
			byte[] dataReq = os.toByteArray();
			packet.send(dataReq, 23, name);
			
		}catch(Exception e) {}
	}
		
	/**
	 * Receives requests from Intermediate and immediately sends an acknowledgement
	 */
	public synchronized void rcvPkt() {
		while(true) {
			dataRequest();
			try {
				byte[] recv = packet.receivePacket(name);
				
				if (recv[0] == 5) {
					System.out.println("Intermediate recieved packet.");
				//If the packet received is the data
				}else {
					//Send the acknowledgement packet
					packet.send(packet.acknowledgementPacket(recv),23,name);
				}
				notifyAll();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * Main method of Server class.
	 * @param args
	 */
	public static void main(String[] args) {
		Server s = new Server();
		s.rcvPkt();
	}
}
