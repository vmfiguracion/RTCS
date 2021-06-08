package udpsystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * SYSC 3303 Assignment 2
 * 
 * Intermediate Host class for a three part system that communicates with each other using UDP.
 * @author Valerie Figuracion
 * @since February 4, 2020
 */
public class IntermediateHost {
	DatagramPacket sendPacket, receivePacket; //Initialize packets
	DatagramSocket receiveSocket; //Initialize sockets
	
	public IntermediateHost() {
		//Construct the socket
		try {
			receiveSocket = new DatagramSocket(23); //Listens to port 23 for data packet from either Client or Server
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Receives packet from Client or Server
	 */
	public void receivePacket() {
		//Always listening to port 23
		while(true) {
			//Initializes a byte array that can receive up to 100 bytes
			byte msgrcv[] = new byte [100];
			receivePacket = new DatagramPacket(msgrcv, msgrcv.length);
						
			try {
				System.out.println("Intermediate host is waiting for packet...");
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
				
				//Uses the sendPacket method to send the received packet to the appropriate destination.
				sendPacket(msg);
			
			}catch(IOException e) {
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
	 * Sends the packet received to its appropriate destination.
	 * @param pkt Packet received by the IntermediateHost using receivePacket()
	 */
	public void sendPacket(byte[] pkt) {
		if (pkt[1] == 3 || pkt[1] == 4  || pkt[1] == 0) {
			//Packet is from Server	
			System.out.println("Sending acknowledgement...");
			
			//Packaging packet for sending
			try {
				//Initialize sendSocket
				DatagramSocket sendSocket = new DatagramSocket();
				//Form DatagramPacket
				sendPacket = new DatagramPacket (pkt, pkt.length, InetAddress.getLocalHost(), 12);
				//Send DatagramPacket
				sendSocket.send(sendPacket);
				//Prints that the packet has been sent
				System.out.println("Acknowledgement sent.\n");
				//Close socket
				sendSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		} else {
			//Packet is from Client
			System.out.println("Sending request...");
			
			//Packaging packet for sending
			try {
				//Initialize sendSocket
				DatagramSocket sendSocket = new DatagramSocket();
				//Form DatagramPacket
				sendPacket = new DatagramPacket (pkt, pkt.length, InetAddress.getLocalHost(), 69);
				//Send DatagramPacket
				sendSocket.send(sendPacket);
				//Prints that the packet has been sent.
				System.out.println("Request sent.\n");
				//Close socket
				sendSocket.close();
			}catch (IOException e){
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * Main method of IntermediateHost class.
	 * @param args
	 */
	public static void main(String[] args) {
		IntermediateHost h = new IntermediateHost();
		h.receivePacket();
	}
}
