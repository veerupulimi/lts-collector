package com.lts.core.handler;


public enum Devices {

	TELTONIKA("teltonika", 5422, TeltonikaDeviceHandler.class), RUPTELA("ruptela", 5420,
			RuptelaDeviceHandler.class);

	private final String deviceType;
	private final int portNumber;
	private final Class deviceHandlerClass;

	Devices(String deviceType, int portNumber, Class deviceHandlerClass) {
		this.portNumber = portNumber;
		this.deviceHandlerClass = deviceHandlerClass;
		this.deviceType = deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public Class getDeviceHandlerClass() {
		return deviceHandlerClass;
	}
}
