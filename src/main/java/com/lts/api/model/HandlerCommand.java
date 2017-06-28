/* COPYRIGHT (C) 2016 LTS. All Rights Reserved. */

package com.lts.api.model;

/**
 * @author veeru
 *
 */
public class HandlerCommand {
	
	private String imei;
	private String manufacturer;
	private String model;
	private String command;
	private String pass;
	
	
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	

}
