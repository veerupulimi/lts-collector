package com.lts.core.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class DeviceHandler extends Thread {

	protected String deviceImei;
	protected Socket clientSocket;

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		handleDevice();
	}

	protected abstract void handleDevice();

	protected void cleanUpSockets(Socket clientSocket,
			DataInputStream clientSocketDis, DataOutputStream clientSocketDos) {
		try {
			if (clientSocketDis != null) {
				clientSocketDis.close();
				clientSocketDis = null;
			}
		} catch (IOException ie) {
			clientSocketDis = null;
		}

		try {
			if (clientSocketDos != null) {
				clientSocketDos.close();
				clientSocketDos = null;
			}
		} catch (IOException ie) {
			clientSocketDos = null;
		}

		try {
			if (clientSocket != null) {
				clientSocket.close();
				clientSocket = null;
			}
		} catch (IOException ie) {
			clientSocket = null;
		}
	}

}
