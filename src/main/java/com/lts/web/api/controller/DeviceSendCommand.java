package com.lts.web.api.controller;

import org.jboss.logging.Logger;

import com.lts.api.model.HandlerCommand;
import com.lts.core.handler.RuptelaDeviceHandler;
import com.lts.core.handler.TeltonikaDeviceHandler;
import com.lts.core.listener.ListenerManager;

/**
 * Manager to do common activities for any agent. Activities such as send first
 * request, send counter values are defined here.
 * 
 * @author Ramkumar
 * 
 */
public class DeviceSendCommand {
	private static final Logger LOGGER = Logger.getLogger("listener");

	/**
	 * Constructor which initializes the HashMap which holds the counter-set and
	 * the ArrayList which holds the counter-sets
	 */
	public DeviceSendCommand() {

	}

	public static String getSendCommand(HandlerCommand handlerCommand)
			throws Throwable {
		TeltonikaDeviceHandler tdh = null;
		RuptelaDeviceHandler rdh = null;
		String commandResp = null, imeiNo = null, manufacturer = null, model = null, command = null, pass = null;
		try {

			imeiNo = handlerCommand.getImei();
			manufacturer = handlerCommand.getManufacturer();
			model = handlerCommand.getModel();
			command = handlerCommand.getCommand();
			pass = handlerCommand.getPass();

			if (manufacturer.equalsIgnoreCase("Teltonika")) {
				tdh = ListenerManager.teltonikaDeviceHandlerMap.get(imeiNo);
				if (tdh != null) {
					commandResp = tdh.sendCommand(pass, model, command,
							tdh.getClientSocket());
				}
			} else if (manufacturer.equalsIgnoreCase("Ruptela")) {
				rdh = ListenerManager.ruptelaDeviceHandlerMap.get(imeiNo);
				if (rdh != null) {
					commandResp = rdh.sendCommand(pass, command,
							rdh.getClientSocket());
				}
			}

		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			tdh = null;
			rdh = null;
			imeiNo = null;
			manufacturer = null;
			model = null;
			command = null;
			pass = null;
		}
		return commandResp;
	}

}
