package com.zah.service;

import java.util.Map;

public interface InterfaceService {
	public String updateVehicleT2Info(Map<String, String> paramMap);

	public int updateVehiclePlaceInfo(String machineId, String lng, String lat, String time);

}
