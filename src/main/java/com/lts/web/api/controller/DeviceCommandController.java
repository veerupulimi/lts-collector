/* COPYRIGHT (C) 2016 LTS. All Rights Reserved. */

package com.lts.web.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lts.api.model.HandlerCommand;
import com.lts.web.api.util.PathConstants;

/**
 * @author veeru
 * 
 */
@RestController
@RequestMapping(value = PathConstants.SECURITY_BASE_PATH)
public class DeviceCommandController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DeviceCommandController.class);

	@RequestMapping(value = PathConstants.DEVICE_COMMAND, method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sendCommand(
			@RequestBody HandlerCommand handlerCommand) throws Throwable {
		LOGGER.info("/device/command [POST]");

		String commandResp = "Updated command";
		commandResp = DeviceSendCommand.getSendCommand(handlerCommand);
		return new ResponseEntity<>(commandResp, HttpStatus.OK);
	}

}
