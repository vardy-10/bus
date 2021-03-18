package com.zah.entity;

public class Notice {
	private int admin_id;
	private int release_time;
	private String notice_content;
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(int admin_id) {
		this.admin_id = admin_id;
	}

	public int getRelease_time() {
		return release_time;
	}

	public void setRelease_time(int release_time) {
		this.release_time = release_time;
	}

	public String getNotice_content() {
		return notice_content;
	}

	public void setNotice_content(String notice_content) {
		this.notice_content = notice_content;
	}
}
