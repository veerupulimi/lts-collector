package com.lts.web.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lts.api.model.CollectorBean;
import com.lts.web.api.dao.DeviceDao;
import com.lts.web.api.model.Device;
import com.lts.web.api.service.DeviceDataService;

@Service
public class DeviceDataServiceImpl implements DeviceDataService {

	@Autowired
	private DeviceDao deviceDao;

	@Override
	public List<Device> findAll() {
		return deviceDao.findAll();
	}

	@Override
	public ArrayList<CollectorBean> pollDeviceData() {
		return deviceDao.pollDeviceData();
	}

}
