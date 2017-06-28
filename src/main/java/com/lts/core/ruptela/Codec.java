package com.lts.core.ruptela;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class Codec implements Runnable{
	protected DatagramPacket udpPacket;
	protected DatagramSocket udpSocket;
	protected Socket socket;
	protected boolean isUdp = false;
	protected boolean isTcp = false;
	
	public Codec(Socket socket) {
		this.socket = socket;
		this.isTcp = true;
		new Thread(this).start();
	}
	
	public Codec(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
		this.isUdp = true;
		new Thread(this).start();
	}
	
	abstract void tcpProtocol(Socket socket); 
	abstract void udpProtocol();
	abstract void parser(ArrayList<String> strings);
	protected void sendToWriter(){
		System.out.println("Sending data to writer");
	}
    public void run(){
                try{
		if(this.isTcp){
			tcpProtocol(this.socket);
		}
		else if (this.isUdp){
			udpProtocol();
		}
                }catch(ThreadDeath td){
                    System.out.println("Thread death detected.");
                    td.printStackTrace();
                }
	}
}
