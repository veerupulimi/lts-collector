package com.lts.core.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lts.core.handler.DeviceHandler;
import com.lts.core.handler.Devices;

public class ListenerServerThread extends Thread {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ListenerServerThread.class);

	private ServerSocket serverSocket = null;
	private Devices deviceType = null;

	ListenerServerThread(ServerSocket serverSocket, Devices deviceType) {
		this.serverSocket = serverSocket;
		this.deviceType = deviceType;
		this.setName("My::ServerStarterThread" + "::" + this.getName());
	}

	public void run() {
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				LOGGER.info("IO Exception accepting the client socket "
						+ e.getMessage());
				break;
			}

			DeviceHandler deviceHandler = null;
			try {
				deviceHandler = (DeviceHandler) (deviceType
						.getDeviceHandlerClass().newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			deviceHandler.setClientSocket(clientSocket);
			deviceHandler.start();// start as Thread
		}
	}

}