package com.lts.web.api.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts.api.model.CollectorBean;
import com.lts.core.ruptela.FmPro3Data;
import com.lts.core.ruptela.GpsData;
import com.lts.web.api.util.ConfigConstants;

public class JsonMapExample {

	public static void main(String[] args) {
		
		
		ConfigConstants.packetcount = ConfigConstants.packetcount+1;
		System.out.println(ConfigConstants.packetcount);

		try {

			ObjectMapper mapper = new ObjectMapper();
			String json = "{\"imei\":\"865733023621949\",\"devicetype\":\"ruptela\",\"decoded\":\"AD4AAxNhLVfjPQEAAVfPwmAAABvgGDwOstGkGE1l/gYAAAsHBAUAAgEbDQMAAh1g6h4AAAKWAACkEUEBJGtkAFXM\"}";
			// LinkedBlockingDeque<CollectorBean> map = new
			// LinkedBlockingDeque<CollectorBean>();
			// Map<CollectorBean> map = new CollectorBean();
			CollectorBean cc = new CollectorBean();
			// convert JSON string to Map
			cc = mapper.readValue(json, new TypeReference<CollectorBean>() {
			});

			System.out.println(cc);
			
			DataInputStream clientSocketDis = new DataInputStream(new ByteArrayInputStream(cc.getDecoded()));
			FmPro3Data initialData = new FmPro3Data(clientSocketDis);
			initialData.read();
			String imeiNo = initialData.getImeiString();
			GpsData aa = initialData.getGpsData();
			System.out.println(imeiNo+ " "+ aa.toString());

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
