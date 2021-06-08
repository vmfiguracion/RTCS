package udpsystemrpc;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * SYSC 3303 Assignment 3
 * 
 * Helper class for packaging and sending packets.
 * @author Valerie Figuracion 
 * @since February 25, 2020
 */

public class Packet {
	
	DatagramPacket sendPacket, receivePacket; //Initialize packets
	DatagramSocket sendSocket, receiveSocket; //Initialize sockets
		
	/**
	 * Constructs the initial socket
	 * @param port Port for the socket to bind to
	 */
	public void constructSocket(int port) {
		try {
			receiveSocket = new DatagramSocket(port); //Listens to port for data from the IntermediateHost
		} catch(SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Packages a data packet 
	 * @param fileName Name of the text file.
	 * @param modeName Name of the mode.
	 * @param request The type of request being sent (ie read(1) or write(2))
	 */
	public byte[] dataPacket(String fileName, String modeName, byte request) {
		byte file[] = fileName.getBytes();
		byte mode[] = modeName.getBytes();
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			//First two bytes of the request
			os.write(0); os.write(request);
			//Filename converted from string to bytes
			os.write(file);
			//Byte 0 buffer
			os.write(0);
			//Mode converted from string to bytes
			os.write(mode);
			//Byte 0 buffer
			os.write(0);
			
			System.out.println("Client is packaging packet.");
			byte msg[] = os.toByteArray();
			return msg;
			
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}	
	
	/**
	 * Packages an acknowledgement packet
	 * @param pkt Package received from Intermediate
	 * @return the byte array of the acknowledgement
	 * @throws Exception If the request is invalid this exception is thrown
	 */
	public byte[] acknowledgementPacket(byte pkt[]) throws Exception {
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
		return ack;
	}

	
	/**
	 * Packet received from Intermediate
	 * @param name Name of the class receiving the packet.
	 * @return the byte array of the packet received
	 */
	public byte[] receivePacket(String name) {
		//Initializes a byte array that can receive up to 100 bytes
		byte msgrcv[] = new byte [100];
		receivePacket = new DatagramPacket(msgrcv, msgrcv.length);
					
		try {
			System.out.println(name + " is waiting for packet...");
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
		
			return msg;
			
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
		return null;
	}
	
	public synchronized void send(byte[] outData, int port, String name) {
		DatagramPacket sendPacket;
		
		try {
			
			//Initialize sendSocket
			DatagramSocket sendSocket = new DatagramSocket();
			System.out.println(name + " is sending packet.");
			
			//Form DatagramPacket
			sendPacket = new DatagramPacket(outData, outData.length, InetAddress.getLocalHost(), port);
			
			//Send DatagramPacket
			sendSocket.send(sendPacket);
			
			//Prints that the packet has been sent
			System.out.println("Packet sent.\n");
			
			//Close socket after use
			sendSocket.close();
			notifyAll();
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} 		
	}
}
