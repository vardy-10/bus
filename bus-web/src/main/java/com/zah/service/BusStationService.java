package com.zah.service;

import org.springframework.ui.Model;

public interface BusStationService {
	public String showBusStationList(int limit, int page, String station_name, String line_id);

	public String deleteBusStation(String station_id);

	public String editBusStation(String station_name, String station_dir, String station_order, String radius,
                                 String station_id);

	public String addBusStation(String line_id, String station_dir, String radius, String station_name,
                                String station_order);
	public String busStationUpdate(Model model, String station_id);
	


}
