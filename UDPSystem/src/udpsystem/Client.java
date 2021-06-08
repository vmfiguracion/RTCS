package udpsystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * SYSC 3303 Assignment 2
 * 
 * Client class for a three part system that communicates with each other using UDP.
 * @author Valerie Figuracion
 * @since February 4, 2020
 */
public class Client {
	
	DatagramPacket sendPacket, receivePacket; //Initialize packets
	DatagramSocket sendSocket, receiveSocket; //Initialize sockets
	
	byte msg[];
	byte b0 = 0;
	byte b1 = 1;
	byte b2 = 2;
	
	public Client() {
		//Construct the socket
		try {
			receiveSocket = new DatagramSocket(12); //Listens to port 12 for data from the IntermediateHost
		} catch(SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Sends a read request to the IntermediateHost
	 * @param filename Name of the text file.
	 * @param modeName Name of the mode.
	 */
	public void readRequest(String filename, String modeName){
		System.out.println("Client is requesting to read file.");
		
		byte file[] = filename.getBytes();
		byte mode[] = modeName.getBytes();
		
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			//First two bytes of the request
			outputStream.write(b0);
			outputStream.write(b1);
			//Filename converted from string to bytes
			outputStream.write(file);
			//Byte 0 buffer
			outputStream.write(b0);
			//Mode converted from string to bytes
			outputStream.write(mode);
			//Byte 0 buffer
			outputStream.write(b0);
			
			System.out.println("Client is packaging packet.");
			byte msg[] = outputStream.toByteArray();
			
			sendSocket = new DatagramSocket();
			System.out.println("Client is sending packet. \n");
			//Form the DatagramPacket
			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 23);
			//Send the DatagramPacket
			sendSocket.send(sendPacket);
			sendSocket.disconnect();
						
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Sends a write request to the IntermediateHost
	 * @param filename Name of the text file.
	 * @param modeName Name of the mode.
	 */
	public void writeRequest(String filename, String modeName){
		System.out.println("Client is requesting to write to file.");
		
		byte file[] = filename.getBytes();
		byte mode[] = modeName.getBytes();
		
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			//First two bytes of the request
			outputStream.write(b0);
			outputStream.write(b2);
			//Filename converted from string to bytes
			outputStream.write(file);
			//Byte 0 buffer
			outputStream.write(b0);
			//Mode converted from string to bytes
			outputStream.write(mode);
			//Byte 0 buffer
			outputStream.write(b0);
			
			System.out.println("Client is packaging packet.");
			byte msg[] = outputStream.toByteArray();
			
			sendSocket = new DatagramSocket();
			System.out.println("Client is sending packet. \n");
			//Form the DatagramPacket
			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 23);
			//Send the DatagramPacket
			sendSocket.send(sendPacket);	
						
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Receives acknowledgement from IntermediateHost.
	 */
	public void receivePacket() {
		//Always listening to port 12
		while (true) {
			byte msgrcv[] = new byte [100]; //Initialize a byte array with size 100.
			receivePacket = new DatagramPacket (msgrcv, msgrcv.length);
						
			try {
				System.out.println("Client is waiting for acknowledgement packet...");
				receiveSocket.receive(receivePacket);
				
				//Reduce the byte array size to the length of the packet received.
				byte msg[] = new byte[receivePacket.getLength()];
				
				for (int i = 0; i < msg.length; i++) {
					msg[i] = msgrcv[i];
				}
				
				System.out.println("Packet received from " + receivePacket.getAddress() + " at " + receivePacket.getPort());
				
				//Prints the array in bytes
				System.out.println("Message in bytes: " + Arrays.toString(msg));
				//Prints the array in strings
				String s = new String(msg);
				System.out.println("Message in String: " + s + "\n");
			
			}catch(IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * Sends an invalid request. 
	 */
	public void sendFail() {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			//First two bytes of the request
			outputStream.write(b0);
			outputStream.write(13);
			//Byte 0 buffer
			outputStream.write(b0);
			//Byte 0 buffer
			outputStream.write(b0);
			
			System.out.println("Client is packaging packet.");
			byte msg[] = outputStream.toByteArray();
			
			sendSocket = new DatagramSocket();
			System.out.println("Client is sending packet. \n");
			//Form the DatagramPacket
			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 23);
			//Send the DatagramPacket
			sendSocket.send(sendPacket);	
						
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
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
		c.sendFail();
		c.receivePacket();
	}
}
