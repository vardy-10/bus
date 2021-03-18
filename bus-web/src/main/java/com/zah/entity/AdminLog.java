package com.zah.entity;

public class AdminLog {
	private long log_id;
	private int admin_id;
	private String log_info;
	private int log_time;

	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long log_id) {
		this.log_id = log_id;
	}

	public int getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(int admin_id) {
		this.admin_id = admin_id;
	}

	public String getLog_info() {
		return log_info;
	}

	public void setLog_info(String log_info) {
		this.log_info = log_info;
	}

	public int getLog_time() {
		return log_time;
	}

	public void setLog_time(int log_time) {
		this.log_time = log_time;
	}
}
