package com.lts.web.api.dao;

import java.util.ArrayList;
import java.util.List;

import com.lts.api.model.CollectorBean;
import com.lts.web.api.model.Device;

public interface DeviceDao {

	List<Device> findAll();

	ArrayList<CollectorBean> pollDeviceData();

}
