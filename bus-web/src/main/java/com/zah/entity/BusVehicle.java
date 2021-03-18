package com.zah.entity;

import java.math.BigDecimal;

public class BusVehicle {
	private int vehicle_id;
	private int line_id;
	private String vehicle_number;
	private String license_plate;
	private int seat_num;
	private int is_enable;
	private String device1_number;
	private String device2_number;
	private String line_name;

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public int getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public int getLine_id() {
		return line_id;
	}

	public void setLine_id(int line_id) {
		this.line_id = line_id;
	}

	public String getVehicle_number() {
		return vehicle_number;
	}

	public void setVehicle_number(String vehicle_number) {
		this.vehicle_number = vehicle_number;
	}

	public String getLicense_plate() {
		return license_plate;
	}

	public void setLicense_plate(String license_plate) {
		this.license_plate = license_plate;
	}

	public int getSeat_num() {
		return seat_num;
	}

	public void setSeat_num(int seat_num) {
		this.seat_num = seat_num;
	}

	public int getIs_enable() {
		return is_enable;
	}

	public void setIs_enable(int is_enable) {
		this.is_enable = is_enable;
	}

	public String getDevice1_number() {
		return device1_number;
	}

	public void setDevice1_number(String device1_number) {
		this.device1_number = device1_number;
	}

	public String getDevice2_number() {
		return device2_number;
	}

	public void setDevice2_number(String device2_number) {
		this.device2_number = device2_number;
	}

	public int getLog_time() {
		return log_time;
	}

	public void setLog_time(int log_time) {
		this.log_time = log_time;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getIs_in_origin() {
		return is_in_origin;
	}

	public void setIs_in_origin(int is_in_origin) {
		this.is_in_origin = is_in_origin;
	}

	public int getIs_in_line() {
		return is_in_line;
	}

	public void setIs_in_line(int is_in_line) {
		this.is_in_line = is_in_line;
	}

	public BigDecimal getVehicle_distance() {
		return vehicle_distance;
	}

	public void setVehicle_distance(BigDecimal vehicle_distance) {
		this.vehicle_distance = vehicle_distance;
	}

	private int log_time;
	private double longitude;
	private double latitude;
	private int direction;
	private int is_in_origin;
	private int is_in_line;
	private BigDecimal vehicle_distance;
}
