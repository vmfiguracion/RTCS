package udpsystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * SYSC 3303 Assignment 2
 * 
 * Server class for a three part system that communicates with each other using UDP.
 * @author Valerie Figuracion
 * @since February 4, 2020
 */
public class Server {
	
	DatagramPacket sendPacket, receivePacket; //Initialize packets
	DatagramSocket receiveSocket; //Initialize sockets
	
	public Server() {
		//Construct the socket
		try {
			receiveSocket = new DatagramSocket(69); //Listens to port 69 for data packet from IntermediateHost
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Receives requests from IntermediateHost
	 */
	public void receivePacket() {
		//Always listening to port 23
		while(true) {
			//Initializes a byte array that can receive up to 100 bytes
			byte msgrcv[] = new byte [100];
			receivePacket = new DatagramPacket(msgrcv, msgrcv.length);
						
			try {
				System.out.println("Server is waiting for packet...");
				receiveSocket.receive(receivePacket);
				
				//Reduce the byte array size to the length of the packet received.
				byte msg[] = new byte[receivePacket.getLength()];
				for (int i = 0; i < msg.length; i++) {
					msg[i] = msgrcv[i];
				}
				
				System.out.println("Packet received from " + receivePacket.getAddress() + " at " + receivePacket.getPort() + "\n");
				//Prints the array in bytes
				System.out.println("Message in bytes: " + Arrays.toString(msg));
				//Prints the array in strings
				String s = new String(msg);
				System.out.println("Message in String: " + s + "\n");
				
				//Uses the sendAck method to send the received packet to the IntermediateHost.
				sendAck(msg);
			}catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			//Slow down by 1 second
			try {
		          Thread.sleep(1000);
			} catch (InterruptedException e ) {
		          e.printStackTrace();
		          System.exit(1);
	     	}
		}
	}
	
	/**
	 * Sends the appropriate acknowledgement packet to the IntermediateHost
	 * @param pkt The packet received from the IntermediateHost
	 * @throws Exception If the request is invalid this exception is thrown
	 */
	public void sendAck(byte[] pkt) throws Exception {
		System.out.println("Sending acknowledement...");
		ByteArrayOutputStream OS = new ByteArrayOutputStream();
		
		switch(pkt[1]){
		//When package received is a read request
		case 1: pkt[1] = 1;
			OS.write(0); OS.write(3); OS.write(0); OS.write(1);
			break;
		//When package received is a write request
		case 2: pkt[2] = 2;
			OS.write(0); OS.write(4); OS.write(0); OS.write(0);
			break;
		//When package received is an invalid request.
		default:
			throw new Exception("Request was invalid.");
		}
		
		byte ack[] = OS.toByteArray();
		System.out.println("Request was " + Arrays.toString(ack));	
		
		//Send the acknowledgement back to IntermediateHost
		try {
			//Initialize sendSocket
			DatagramSocket sendSocket = new DatagramSocket();
			//Form DatagramPacket
			sendPacket = new DatagramPacket(ack, ack.length, InetAddress.getLocalHost(), 23);
			//Send DatagramPacket
			sendSocket.send(sendPacket);
			//Prints that the packet has been sent
			System.out.println("Acknowledgment sent.\n");		
			//Close socket after use
			sendSocket.close();
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Main method of Server class.
	 * @param args
	 */
	public static void main(String[] args) {
		Server s = new Server();
		s.receivePacket();
	}
}
