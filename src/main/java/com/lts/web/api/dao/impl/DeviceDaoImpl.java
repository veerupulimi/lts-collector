package com.lts.web.api.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lts.api.model.CollectorBean;
import com.lts.core.listener.ListenerManager;
import com.lts.web.api.dao.DeviceDao;
import com.lts.web.api.model.Device;

@Repository("deviceDao")
public class DeviceDaoImpl implements DeviceDao {

	private static List<Device> devices;
	private static Integer id = 1;
	static {
		devices = new ArrayList<>(Arrays.asList(new Device("foo", 50, "baz"),
				new Device("foo2", 30, "baz2")));
		for (Device device : devices) {
			device.setId(id);
			id++;
		}
	}

	@Override
	public List<Device> findAll() {
		return devices;
	}

	@Override
	public ArrayList<CollectorBean> pollDeviceData() {
		return ListenerManager.getListenerManager().pollDeviceData();
	}

}
