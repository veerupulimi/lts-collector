package com.lts.core.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lts.api.model.CollectorBean;
import com.lts.core.handler.Devices;
import com.lts.core.handler.RuptelaDeviceHandler;
import com.lts.core.handler.TeltonikaDeviceHandler;
import com.lts.web.api.util.ConfigConstants;

public class ListenerManager {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ListenerManager.class);
	// Singleton object, used globally with static getListenerManager().
	private static ListenerManager listenerManager = new ListenerManager();

	// private static Map<String, TeltonikaDeviceHandler>
	// teltonikaDeviceHandlerMap;
	public static Map<String, TeltonikaDeviceHandler> teltonikaDeviceHandlerMap;
	public static Map<String, RuptelaDeviceHandler> ruptelaDeviceHandlerMap;
	private static LinkedBlockingDeque<CollectorBean> listenerQueue;

	ListenerServer deviceTCPIPListener;

	private ListenerManager() {

		try {
			// initialize all required queue in this private constructor.
			deviceTCPIPListener = new ListenerServer();
			init();
			teltonikaDeviceHandlerMap = new HashMap<String, TeltonikaDeviceHandler>();
			ruptelaDeviceHandlerMap = new HashMap<String, RuptelaDeviceHandler>();
			listenerQueue = new LinkedBlockingDeque<CollectorBean>();

		} catch (Exception ex) {
			LOGGER.error("Exception in ListenerStarter " + ex.getMessage());
		}
	}

	/**
	 * Access the only[singleton] ListenerStarter object.
	 * 
	 * @return ListenerManager
	 */
	public static ListenerManager getListenerManager() {
		return listenerManager;
	}

	public void addToQueue(CollectorBean listenerHash) {
		try {
			listenerQueue.put(listenerHash);
			LOGGER.debug("Queue Size " + listenerQueue.size());
		} catch (InterruptedException e) {
			LOGGER.error("Exception in addToQueue " + e.getMessage());
		}
	}

	public int getListenerQueueCount() {
		return listenerQueue.size();
	}

	public ArrayList<CollectorBean> pollDeviceData() {
		ArrayList<CollectorBean> al = null;
		try {
			al = new ArrayList<CollectorBean>();
			// Check Queue has data
			if (listenerQueue != null) {
				listenerQueue.drainTo(al, ConfigConstants.MAX_BATCH_SIZE);
				return al;
			}
		} catch (Exception e) {
			LOGGER.error("Exception in polling Queue " + e.getMessage());
		}

		return al;

	}

	// public void addToTeltonikaDeviceHandlerMap(String deviceImei) {
	// try {
	// teltonikaDeviceHandlerMap.put(device
	// } catch (InterruptedException e) {
	// LOGGER.error("Exception in addToQueue " + e.getMessage());
	// }
	// }

	@PostConstruct
	private void init() {
		Devices[] DEVICE_TYPES_USED = { Devices.TELTONIKA, Devices.RUPTELA };
		deviceTCPIPListener.startServer(DEVICE_TYPES_USED, "ltms_sa");
		LOGGER.info("LTS Auto Started Successfully @" + new Date());
	}

	@PreDestroy
	private void destroy() {
		deviceTCPIPListener.stopServer("ltms_sa");
		LOGGER.info("LTS Auto Stopped Successfully @" + new Date());
	}
}