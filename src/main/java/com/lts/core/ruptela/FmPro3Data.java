package com.lts.core.ruptela;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.jboss.logging.Logger;

public class FmPro3Data {
	private int lenght = 0;
	private long imei = 0;
	private byte command = 0;
	private int crc = 0;
	private GpsData gpsData = new GpsData();
	private DataInputStream in = null;
	private byte responseFlag = 0x01;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private static final Logger logger = Logger.getLogger(FmPro3Data.class);
	private boolean status = true;
	private String responseForCommand = null;
	private String rawdata = null;

	public FmPro3Data(DataInputStream in) {
		this.in = in;
	}

	public GpsData getGpsData() {
		return gpsData;
	}

	public void setGpsData(GpsData gpsData) {
		this.gpsData = gpsData;
	}

	public int getCommand() {
		return (int) command;
	}

	public String getImeiString() {
		return Long.toString(this.imei);
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(short crc) {
		this.crc = crc;
	}

	public long getImei() {
		return imei;
	}

	public void setImei(long imei) {
		this.imei = imei;
	}

	public int getLenght() {
		return lenght;
	}

	public void setLenght(short lenght) {
		this.lenght = lenght;
	}

	public String getResponseForCommand() {
		return responseForCommand;
	}

	public void setResponseForCommand(String responseForCommand) {
		this.responseForCommand = responseForCommand;
	}

	public String getRawData() {
		return rawdata;
	}

	public void setRawData(String rawdata) {
		this.rawdata = rawdata;
	}

	public void read() throws IOException {
		this.lenght = this.in.readShort();
		this.in = new DataInputStream(new LoggingInputStream(this.baos, in));
		this.imei = this.in.readLong();
		this.command = this.in.readByte();
		// Checking what command received
		if ((int) this.command == 1) {
			this.gpsData.read(this.in);
		} else if ((int) this.command == 7) {
			byte[] response = new byte[this.lenght - 9];
			this.in.readFully(response);
			this.responseForCommand = asciiBytesToString(response);
			logger.warn("Device's response: " + this.responseForCommand);
		} else {
			logger.warn("Unknown command: " + this.command);
		}

		int calculatedCrc = (int) CRC16.crc_16_rec(baos.toByteArray());
		this.rawdata = byteArrayToString(baos.toByteArray());
		logger.info("Raw Data : " + this.rawdata);
		byte[] crcBuffer = new byte[2];
		crcBuffer[0] = in.readByte();
		crcBuffer[1] = in.readByte();
		char anUnsignedShort = 0;
		int firstByte = (0x000000FF & ((int) crcBuffer[0]));
		int secondByte = (0x000000FF & ((int) crcBuffer[1]));
		anUnsignedShort = (char) (firstByte << 8 | secondByte);
		this.crc = anUnsignedShort;
		if (calculatedCrc != crc) {
			logger.error("Crc mismatch, received: " + crc + " calculated: "
					+ calculatedCrc);
			this.responseFlag = 0x00;
			this.status = false;
		} else {
			logger.info("CRC OK");
		}
	}

	public byte[] getResponsePacket() {
		byte[] response = new byte[6];
		response[0] = 0x00;
		response[1] = 0x02;
		response[2] = 0x64;
		response[3] = this.responseFlag;

		byte[] crcCalculate = new byte[2];
		crcCalculate[0] = response[2];
		crcCalculate[1] = response[3];
		byte[] crc16Bytes = crc16(crcCalculate);
		response[4] = crc16Bytes[0];
		response[5] = crc16Bytes[1];
		return response;
	}

	private static byte[] crc16(byte[] buffer) {
		int crc16 = CRC16.crc_16_rec(buffer);
		byte[] crc16Bytes = new byte[4];
		// Spliting crc16 from int to byte array
		crc16Bytes[0] = (byte) (crc16 >> 24);
		crc16Bytes[1] = (byte) ((crc16 << 8) >> 24);
		crc16Bytes[2] = (byte) ((crc16 << 16) >> 24);
		crc16Bytes[3] = (byte) ((crc16 << 24) >> 24);
		byte[] returnbuff = new byte[2];
		returnbuff[0] = crc16Bytes[2];
		returnbuff[1] = crc16Bytes[3];
		return returnbuff;
	}

	@Override
	public String toString() {
		String data = new String();
		data += "\nLenght: " + this.lenght;
		data += "\nImei: " + this.imei;
		data += "\nCommand: " + this.command;
		data += gpsData.toString();
		data += "\nCrc: " + this.crc;
		return data;
	}

	public boolean getStatus() {
		return status;
	}

	public static String executeCommand(String smsCommand) {
		StringBuilder length = new StringBuilder();
		String commandId = "6C";
		String hexCommand = asciiToHex("12345" + smsCommand);
		length.append(commandId);
		length.append(hexCommand);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%04x", length.length() / 2));
		sb.append(commandId);
		sb.append(hexCommand);
		byte[] crcCalculate = hexStringToByteArray(sb.substring(4, sb.length()));
		byte[] crc16Bytes = crc16(crcCalculate);
		sb.append(Integer.toHexString(crc16Bytes[0] & 0xFF));
		sb.append(Integer.toHexString(crc16Bytes[1] & 0xFF));
		return sb.toString();
	}

	/* Converts the string in hexadecimal format to array of bytes */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String asciiToHex(String ascii) {
		StringBuilder hex = new StringBuilder();
		for (int i = 0; i < ascii.length(); i++) {
			hex.append(Integer.toHexString(ascii.charAt(i)));
		}
		return hex.toString();
	}

	public static String asciiBytesToString(byte[] bytes) {
		if ((bytes == null) || (bytes.length == 0)) {
			return "";
		}

		char[] result = new char[bytes.length];

		for (int i = 0; i < bytes.length; i++) {
			result[i] = (char) bytes[i];
		}

		return new String(result);
	}

	public static String byteArrayToString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
}
