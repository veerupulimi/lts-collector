package com.lts.api.model;

/**
 * 
 * @author veeru
 * 
 */
public class CollectorBean {
	private String imei;
	private String devicetype;

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public byte[] getDecoded() {
		return decoded;
	}

	public void setDecoded(byte[] decoded) {
		this.decoded = decoded;
	}

	private byte[] decoded;

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
	}

}
