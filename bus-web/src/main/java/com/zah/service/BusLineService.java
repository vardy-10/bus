package com.zah.service;

public interface BusLineService {
	public String showBusLineList(int limit, int page, String line_name);

	public String deleteBusLine(String line_id);

	public String editBusLine(String line_name, String line_type, String line_id);

}
