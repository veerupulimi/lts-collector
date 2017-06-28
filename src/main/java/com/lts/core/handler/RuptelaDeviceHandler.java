package com.lts.core.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.jboss.logging.Logger;

import com.lts.api.model.CollectorBean;
import com.lts.core.listener.ListenerManager;
import com.lts.core.ruptela.FmPro3Data;

public class RuptelaDeviceHandler extends DeviceHandler {

	private static final Logger LOGGER = Logger.getLogger("listener");
	private static String commandStatus;

	// private VehicleData vdata;

	@Override
	protected void handleDevice() {
		LOGGER.info("Entered Ruptela five mins Handle Device:" + new Date());
		DataInputStream clientSocketDis = null;
		DataOutputStream dos = null;
		String imeiNo = "empty";
		FmPro3Data data = null;
		ByteArrayOutputStream baos = null;
		try {
			// read() call on the InputStream associated with this Socket will
			// block for only this amount of time.
			// If the timeout expires, a java.net.SocketTimeoutException is
			// raised
			clientSocket.setSoTimeout(300000);
			// Returns an input stream for this socket.

			dos = new DataOutputStream(clientSocket.getOutputStream());

			// Returns an input stream for this socket.
			InputStream is = clientSocket.getInputStream();
			clientSocketDis = new DataInputStream(is);
			dos = new DataOutputStream(clientSocket.getOutputStream());
			FmPro3Data initialData = new FmPro3Data(clientSocketDis);
			initialData.read();

			imeiNo = initialData.getImeiString();
			/*
			 * Since Ruptela Converter Gives 14 digits imeiNo as 15 digits
			 * imeiNo we are adding trailing 0. As changing imeiNo (i.e removing
			 * trailing 0's) of all installed devices is difficult we proceed
			 * with this approach.
			 */
			if (imeiNo.length() < 15 && imeiNo.startsWith("1")) {
				imeiNo = "0" + imeiNo;
			}

			super.deviceImei = imeiNo;
			ListenerManager.ruptelaDeviceHandlerMap.put(deviceImei, this);

			dos.write(initialData.getResponsePacket());
			// insertService(initialData, initialData.getLenght(), imeiNo);

			insertService(toByteArray(is, baos), imeiNo);

			while (true) {
				data = new FmPro3Data(clientSocketDis);
				data.read();

				if (data.getCommand() == 7) {
					commandStatus = data.getResponseForCommand();
					continue;
				}
				dos.write(data.getResponsePacket());
				insertService(toByteArray(is, baos), imeiNo);
			}
		} catch (SocketTimeoutException e) {
			LOGGER.error("SocketTimeoutExceptiontion while receiving the Message "
					+ e);
		} catch (Exception e) {
			LOGGER.error("Exception while receiving the Message " + e);
		} finally {
			if (deviceImei != null) {
				ListenerManager.ruptelaDeviceHandlerMap.remove(deviceImei);
			}
			cleanUpSockets(clientSocket, clientSocketDis, dos);
			imeiNo = null;
			data = null;
			LOGGER.info("DeviceCommunicatorThread:DeviceCommunicator Completed");
		}
	}

	private void insertService(byte[] data, String imeiNo) {
		CollectorBean cb = new CollectorBean();
		try {
			cb.setImei(imeiNo);
			cb.setDecoded(data);
			cb.setDevicetype(Devices.RUPTELA.getDeviceType());

			ListenerManager.getListenerManager().addToQueue(cb);

		} catch (Exception e) {
			LOGGER.error("Exception while persisting data :: " + e);
		} finally {

		}
	}

	public String sendCommand(String password, String command,
			Socket concoxDeviceSocket) {
		String result = null, gprsCommand = null;
		byte[] responseBytes = null;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(concoxDeviceSocket.getOutputStream());
			if (command.equalsIgnoreCase("cutOffEngine")) {
				gprsCommand = FmPro3Data.executeCommand(" setio 0,1");
				/* Converting response text from String to array of bytes */
				responseBytes = FmPro3Data.hexStringToByteArray(gprsCommand);
				/* Sending response message to the device */
				out.write(responseBytes, 0, responseBytes.length);
				LOGGER.info("Exection in progress..." + gprsCommand);
			} else if (command.equalsIgnoreCase("restoreEngine")) {
				gprsCommand = FmPro3Data.executeCommand(" setio 1,1");
				/* Converting response text from String to array of bytes */
				responseBytes = FmPro3Data.hexStringToByteArray(gprsCommand);
				/* Sending response message to the device */
				out.write(responseBytes, 0, responseBytes.length);
				LOGGER.info("Exection in progress..." + gprsCommand);
			} else {
				gprsCommand = FmPro3Data.executeCommand(command);
				/* Converting response text from String to array of bytes */
				responseBytes = FmPro3Data.hexStringToByteArray(gprsCommand);
				/* Sending response message to the device */
				out.write(responseBytes, 0, responseBytes.length);
				LOGGER.info("Exection in progress..." + gprsCommand);
			}
			int i = 0;
			while (i < 90) {
				if (commandStatus != null) {
					result = commandStatus;
					commandStatus = null;
					break;
				} else {
					Thread.sleep(1000);
				}
				i++;
			}
		} catch (Exception e) {
			LOGGER.error("SendCommand : " + e);
		} finally {
			gprsCommand = null;
			responseBytes = null;
			// if (out != null) {
			// try {
			// out.close();
			// out = null;
			// } catch (IOException e) {
			// LOGGER.error("DataOutputStream : " + e);
			// }
			// }
		}
		return result;
	}

	public static byte[] toByteArray(InputStream is, ByteArrayOutputStream baos)
			throws IOException {
		baos = new ByteArrayOutputStream();
		int reads = is.read();
		while (reads != -1) {
			baos.write(reads);
			reads = is.read();
		}
		return baos.toByteArray();
	}
}
