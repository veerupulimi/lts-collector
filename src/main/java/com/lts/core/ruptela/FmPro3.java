package com.lts.core.ruptela;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.jboss.logging.Logger;

public class FmPro3 extends Codec {
	private static final Logger logger = Logger.getLogger(FmPro3.class);

	public FmPro3(DatagramPacket packet, DatagramSocket udpSocket) {
		super(udpSocket);
	}

	public FmPro3(DatagramSocket udpSocket) {
		super(udpSocket);
	}

	public FmPro3(Socket socket) {
		super(socket);
	}

	@Override
	void tcpProtocol(Socket socket) {
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			FmPro3Data data = new FmPro3Data(in);
			data.read();
			writeToDataBase(data);
			out.write(data.getResponsePacket());
		} catch (Exception ioe) {
			logger.info("Input stream error.");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			ioe.printStackTrace(pw);
			pw.flush();
			sw.flush();
			String s = sw.toString();
			logger.info("Stack trace: " + s);
		} finally {
			try {
				logger.info("Closing socket...");
				socket.close();
			} catch (IOException ioex) {
				logger.error("Can't close socket !");
			}
		}
	}

	@Override
	void udpProtocol() {

		while (true) {
			try {
				logger.info("Waiting for data...");
				this.udpPacket = new DatagramPacket(new byte[2048], 2048);
				this.udpSocket.receive(udpPacket);
				byte[] buffer = new byte[udpPacket.getLength()];
				System.arraycopy(udpPacket.getData(), udpPacket.getOffset(),
						buffer, 0, buffer.length);
				DataInputStream in = new DataInputStream(
						new ByteArrayInputStream(buffer));
				FmPro3Data data = new FmPro3Data(in);
				data.read();
				if (data.getCommand() == 1) {
					logger.info("GPS DATA received.");
					if (data.getStatus()) {
						logger.info("GPS DATA correct, writing to database.");
						writeToDataBase(data);
					}
					logger.info("Sending response for GPS DATA.");
					this.udpPacket.setData(data.getResponsePacket());
					this.udpSocket.send(this.udpPacket);
				}
			} catch (Exception ex) {
				logger.error(
						"Can't send datagram packet to: "
								+ udpPacket.getAddress() + " port: "
								+ udpPacket.getPort(), ex);
			}
		}

	}

	@Override
	void parser(ArrayList<String> strings) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/*
	 * Change this method to write data to database.
	 */
	private void writeToDataBase(FmPro3Data data) {
		long imei = data.getImei();
		int gpsElementCount = (int) data.getGpsData().getNumberOfrecords();
		// Loping for each received gps element
		GpsElement[] elements = data.getGpsData().getGpsElements();
		for (int i = 0; i != gpsElementCount; i++) {
			/* IMPORTANT */
			/*
			 * Change method bellow to write data to database. Now only printing
			 * data to std.
			 */
			// main.Driver.writter.writeToDB(getDateTimeFormat(elements[i].getTimestamp()),getDateTimeFormat(elements[i].getTimestamp()),
			// imei, new Long(imei).toString(), elements[i].getLatitude(),
			// elements[i].getLongitude(), elements[i].getInputs(),
			// elements[i].getAltitude(), elements[i].getSatelites(),
			// elements[i].getSpeed(), elements[i].getIOElement().getEventId(),
			// elements[i].getAngle(), (int)elements[i].getPriority(), "");
			logger.info("Received: " + elements[i].getTimestamp() + " "
					+ elements[i].getTimestamp() + " " + imei + " "
					+ new Long(imei).toString() + " "
					+ elements[i].getLatitude() + " "
					+ elements[i].getLongitude() + " "
					+ elements[i].getInputs() + " " + elements[i].getAltitude()
					+ " " + elements[i].getSatelites() + " "
					+ elements[i].getSpeed() + " "
					+ elements[i].getIOElement().getEventId() + " "
					+ elements[i].getAngle() + " "
					+ (int) elements[i].getPriority());
		}
	}
}
