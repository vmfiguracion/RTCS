package udpsystemrpc;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * SYSC 3303 Assignment 3
 * 
 * Client class for a three part system that communicates with each other using UDP.
 * @author Valerie Figuracion 
 * @since February 25, 2020
 */
public class Client extends Thread{
	
	byte msg[], reply[];
	byte b0 = 0; byte b1 = 1; byte b2 = 2; byte b5 = 5; byte b13 = 13;
	
	String name = "Client";
	
	Packet packet = new Packet();
	
	public Client() {
		//Construct the socket
		packet.constructSocket(12);
	}
	
	/**
	 * Sends a read request to the Intermediate
	 * @param filename Name of the text file.
	 * @param modeName Name of the mode.
	 */
	public synchronized byte[] readRequest(String fileName, String modeName){
		
		System.out.println("Client is requesting to read file.");
		try {
			//Make and send packet
			packet.send(packet.dataPacket(fileName, modeName, b1), 23, name);
			//Get a reply from the intermediate
			reply = packet.receivePacket(name);
			whichReply(reply);
			
			notifyAll();	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reply;	
	}
	
	/**
	 * Sends a write request to the Intermediate
	 * @param filename Name of the text file.
	 * @param modeName Name of the mode.
	 */
	public synchronized byte[] writeRequest(String fileName, String modeName){
		System.out.println("Client is requesting to write to file.");
		
		try {
			//Make and send packet
			packet.send(packet.dataPacket(fileName, modeName, b2), 23, name);
			//Get a reply from the intermediate
			reply = packet.receivePacket(name);
			whichReply(reply);
			
			notifyAll();	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reply;
	}
		
	/**
	 * Sends an invalid request. 
	 * @param filename Name of the text file.
	 * @param modeName Name of the mode.
	 */
	public synchronized byte[] sendFail(String fileName, String modeName) {
		System.out.println("Client is sending an invalid request.");
		
		try {
			//Make and send packet
			packet.send(packet.dataPacket(fileName, modeName, b13), 23, name);
			//Get a reply from the intermediate
			reply = packet.receivePacket(name);
			whichReply(reply);
			
			notifyAll();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reply;
	}
	
	/**
	 * Sends a request to get the ack packets from the server
	 */
	public synchronized void ackRequest() {
		System.out.println("Sending Request");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(9);
		byte[] ackReq = os.toByteArray();
		packet.send(ackReq, 23, name);	
	}
	
	/**
	 * Determines if the reply is the acknowledgement or reply from intermediate
	 * @param reply
	 */
	public void whichReply(byte[] reply) {
		if (reply[0] == 5) {
			System.out.println("Intermediate recieved packet.");
		}else{
			System.out.println(Arrays.toString(reply));	
		}
	}
	
	/**
	 * Receives packet.
	 */
	public void rcvPkt() {
		while(true) {
			ackRequest();
			try {
				byte[] recv = packet.receivePacket(name);
				whichReply(recv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Runs the Client class.
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = new Client();
		c.readRequest("hello.txt", "octet");
		c.writeRequest("Write to me", "neTasCII");
		c.readRequest("oh hello there.txt", "nEtasCii");
		c.writeRequest("2 fast 4 u", "OCTET");
		c.readRequest("hello.txt", "octet");
		c.writeRequest("Write to me", "neTasCII");
		c.readRequest("oh hello there.txt", "nEtasCii");
		c.writeRequest("2 fast 4 u", "OCTET");
		c.readRequest("oh hello there.txt", "nEtasCii");
		c.writeRequest("2 fast 4 u", "OCTET");
		c.sendFail("failure", "you are");
		c.rcvPkt();
	}
}
