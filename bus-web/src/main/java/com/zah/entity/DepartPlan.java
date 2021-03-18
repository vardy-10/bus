package com.zah.entity;

public class DepartPlan {
	private int line_id;
	private int depart_dir;
	private int depart_week;
	private int vehicle_id;
	private int driver_id;
	private String depart_time;

	public int getLine_id() {
		return line_id;
	}

	public void setLine_id(int line_id) {
		this.line_id = line_id;
	}

	public int getDepart_dir() {
		return depart_dir;
	}

	public void setDepart_dir(int depart_dir) {
		this.depart_dir = depart_dir;
	}

	public int getDepart_week() {
		return depart_week;
	}

	public void setDepart_week(int depart_week) {
		this.depart_week = depart_week;
	}

	public int getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public int getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(int driver_id) {
		this.driver_id = driver_id;
	}

	public String getDepart_time() {
		return depart_time;
	}

	public void setDepart_time(String depart_time) {
		this.depart_time = depart_time;
	}
}
