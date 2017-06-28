package com.lts.web.api.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lts.api.model.CollectorBean;
import com.lts.core.listener.ListenerManager;
import com.lts.web.api.service.DeviceDataService;
import com.lts.web.api.util.PathConstants;

@RestController
@RequestMapping(value = PathConstants.SECURITY_BASE_PATH)
public class DeviceDataController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DeviceDataController.class);
	@Autowired()
	private DeviceDataService deviceDataService;

	@RequestMapping(value = PathConstants.DEVICEDATA, method = RequestMethod.GET)
	// @Consumes(MediaType.APPLICATION_JSON)
	public ResponseEntity<ArrayList<CollectorBean>> getDeviceData() {
		LOGGER.info("/devicedata [GET]");
		return new ResponseEntity<>(deviceDataService.pollDeviceData(),
				HttpStatus.OK);
	}

	@RequestMapping(value = PathConstants.PACKETCOUNT, method = RequestMethod.GET)
	public ResponseEntity<Object> getPacketCount() {
		LOGGER.info("/PacketCount [GET]");
		return new ResponseEntity<Object>(ListenerManager.getListenerManager()
				.getListenerQueueCount(), HttpStatus.OK);

	}
}
