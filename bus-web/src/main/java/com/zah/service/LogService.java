package com.zah.service;



public interface LogService {
	public String showLogList(int limit, int page, String username, String startTime, String endTime);

	public String deleteLog(String startTime, String endTime, String username);
}
