package com.lts.web.api.service;

import java.util.ArrayList;
import java.util.List;

import com.lts.api.model.CollectorBean;
import com.lts.web.api.model.Device;

public interface DeviceDataService {
	List<Device> findAll();

	ArrayList<CollectorBean> pollDeviceData();

}
