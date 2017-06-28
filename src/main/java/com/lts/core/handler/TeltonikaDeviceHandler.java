package com.lts.core.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lts.api.model.CollectorBean;
import com.lts.core.listener.ListenerManager;
import com.lts.core.teltonika.AvlData;
import com.lts.core.teltonika.Codec12Format;
import com.lts.core.teltonika.CodecStore;
import com.lts.core.teltonika.LTSByteWrapper;
import com.lts.core.teltonika.Tools;

public class TeltonikaDeviceHandler extends DeviceHandler {

	/**
	 * Description of TeltonikaDeviceProtocolHandler
	 * 
	 * Description ----------- The DeviceProtocalHandlerSelecter identifies the
	 * make of the device by analyzing the first character of the received data.
	 * If it is found to be Teltonika device,this class handles the received
	 * socket.
	 */
	// private static final Logger LOGGER = Logger.getLogger("listener");
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TeltonikaDeviceHandler.class);
	private static String commandStatus;
	private static final String STR_FMECO3 = "FMEco3", STR_PRO3 = "FMPro3";

	public void handleDevice() {
		LOGGER.info("Entered Teltonika five mins Handle Device:" + new Date());
		DataInputStream clientSocketDis = null;
		DataOutputStream dos = null;
		byte[] packet = null;
		AvlData decoder = null;
		String command = null;
		int commandSize = 0;
		AvlData[] decoded = null;
		try {
			clientSocket.setSoTimeout(300000);
			clientSocketDis = new DataInputStream(clientSocket.getInputStream());
			String imeiNo = clientSocketDis.readUTF();
			dos = new DataOutputStream(clientSocket.getOutputStream());

			dos.writeBoolean(true);

			super.deviceImei = imeiNo;
			// ListenerManager.getListenerManager().addToTeltonikaDeviceHandlerMap(deviceImei);

			ListenerManager.teltonikaDeviceHandlerMap.put(deviceImei, this);
			while (true) {
				packet = LTSByteWrapper.unwrapFromStream(clientSocketDis);

				if (packet == null) {
					LOGGER.info("Data Packet Null. Closing connection: "
							+ clientSocket);
					return;
				} else {
					insertService(packet, packet.length, imeiNo);
					decoder = CodecStore.getInstance().getSuitableCodec(packet);
					if (decoder == null) {
						LOGGER.info("Unknown packet format: "
								+ Tools.bufferToHex(packet));
						dos.writeInt(0);
					} else {
						LOGGER.info("Codec found: " + decoder);
						decoded = decoder.decode(packet);
						LOGGER.info(new Date() + ": Received records:"
								+ decoded.length);
						dos.writeInt(decoded.length);
					}
				}
				LOGGER.info("bytes length inside while(isDCRunning) @"
						+ new Date() + "=" + packet.length + " codec "
						+ packet[0]);

				// if (packet[0] == 0x0C) {
				// command = Tools.bufferToHex(packet);
				// LOGGER.info("Received response : " + command);
				// byte[] bytes = { packet[3], packet[4], packet[5], packet[6]
				// };
				// commandSize = Codec12Format.byteArrayToInt(bytes) * 2;
				// commandStatus = Codec12Format
				// .hexStringToASCIIString(command.substring(14,
				// 14 + commandSize));
				// } else {
				//
				// decoder = CodecStore.getInstance().getSuitableCodec(packet);
				// if (decoder == null) {
				// LOGGER.info("Unknown packet format: "
				// + Tools.bufferToHex(packet));
				// dos.writeInt(0);
				// } else {
				// LOGGER.info("Codec found: " + decoder);
				// decoded = decoder.decode(packet);
				// LOGGER.info(new Date() + ": Received records:"
				// + decoded.length);
				// dos.writeInt(decoded.length);
				// LOGGER.info("Device notified of the number of records received");
				// LOGGER.debug("Queue Size ");
				// //insertService(decoded, packet.length, imeiNo);
				// }
				// }
			}
		} catch (SocketTimeoutException e) {
			LOGGER.info("SocketTimeoutExceptiontion while receiving the Message "
					+ e);
		} catch (Exception e) {
			LOGGER.info("Exception while receiving the Message " + e);
		} finally {
			if (deviceImei != null) {
				ListenerManager.teltonikaDeviceHandlerMap.remove(deviceImei);
			}
			cleanUpSockets(clientSocket, clientSocketDis, dos);
			packet = null;
			decoder = null;
			command = null;
			decoded = null;
			LOGGER.info("DeviceCommunicatorThread:DeviceCommunicator Completed");
		}
	}

	@SuppressWarnings("unchecked")
	private void insertService(byte[] avlDataArray, long byteTrx, String imeiNo) {
		CollectorBean cb = new CollectorBean();
		try {
			cb.setImei(imeiNo);
			cb.setDecoded(avlDataArray);
			cb.setDevicetype(Devices.TELTONIKA.getDeviceType());

			ListenerManager.getListenerManager().addToQueue(cb);
			// ListenerStarter.listenerQueue.put(listenerHash);
		} catch (Exception e) {
			LOGGER.info("Exception while persisting data :: " + e);
		} finally {
			// jo = null;
		}
	}

	public String sendCommand(String password, String model, String command,
			Socket concoxDeviceSocket) {
		String result = null;
		String restoreCommand = null, serverCommand = null;
		byte[] buffer = null;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(concoxDeviceSocket.getOutputStream());
			commandStatus = null;
			if (command.equalsIgnoreCase("cutOffEngine")) {
				restoreCommand = "setdigout 10\r\n";
				if (model.equalsIgnoreCase("FM2200")) {
					restoreCommand = "#SET OUT=1,0\r\n";
				}
			} else if (command.equalsIgnoreCase("restoreEngine")) {
				restoreCommand = "setdigout 00\r\n";
				if (model.equalsIgnoreCase("FM2200")) {
					restoreCommand = "#SET OUT=0,0\r\n";
				}
			}
			serverCommand = Codec12Format.encode(restoreCommand);
			// Equivalent Hex code for '$'
			buffer = Codec12Format.hexStringToByteArray(serverCommand);
			out.write(buffer, 0, buffer.length);
			LOGGER.info("Command sent successfully" + serverCommand);
			int i = 0;
			while (i < 30) {
				if (commandStatus != null) {
					result = commandStatus;
					break;
				} else {
					Thread.sleep(1000);
				}
				i++;
			}
		} catch (Exception e) {
			LOGGER.info("SendCommand : " + e);
		} finally {
			buffer = null;
			restoreCommand = null;
			serverCommand = null;
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					LOGGER.info("DataOutputStream : " + e);
				}
			}
		}
		return result;
	}
}