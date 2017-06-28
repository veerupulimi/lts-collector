package com.lts.core.teltonika;

public class Codec12Format {

	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static String asciiToHex(String ascii) {
		StringBuilder hex = new StringBuilder();
		for (int i = 0; i < ascii.length(); i++) {
			hex.append(Integer.toHexString(ascii.charAt(i)).length() < 2 ? '0' + Integer
					.toHexString(ascii.charAt(i)) : Integer.toHexString(ascii
					.charAt(i)));
		}
		return hex.toString();
	}

	public static String hexStringToASCIIString(String hexCode) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hexCode.length() - 1; i += 2) {
			// grab the hex in pairs
			String output = hexCode.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);
		}
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String prepareCommand(String gprsCommand) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		String preamble = "00000000";
		temp.append("0C").append("01").append("05");
		String command = asciiToHex(gprsCommand);
		String commandLength = String.format("%08x", command.length() / 2);
		String crc = String.format("%08x", LTSByteWrapper
				.getCrc(hexStringToByteArray((temp.append(commandLength)
						.append(command).append("01")).toString())));
		String packetLength = String.format("%08x", temp.length() / 2);
		sb.append(preamble).append(packetLength).append(temp).append(crc);
		return sb.toString();
	}

	public static String encode(String command) {
		String gprsCommand = prepareCommand(command);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < gprsCommand.length(); i = i + 2) {
			// builder.append("$").append(gprsCommand.substring(i, i + 2));
			builder.append(gprsCommand.substring(i, i + 2));
		}
		return builder.toString();
	}
}
