package com.zah.entity;

import java.math.BigDecimal;

public class BusStation {
	private int station_id;
	private int line_id;
	private int station_dir;
	private int station_order;
	private String station_name;
	private double longitude;
	private double latitude;
	private int radius;
	public int getStation_id() {
		return station_id;
	}
	public void setStation_id(int station_id) {
		this.station_id = station_id;
	}
	public int getLine_id() {
		return line_id;
	}
	public void setLine_id(int line_id) {
		this.line_id = line_id;
	}
	public int getStation_dir() {
		return station_dir;
	}
	public void setStation_dir(int station_dir) {
		this.station_dir = station_dir;
	}
	public int getStation_order() {
		return station_order;
	}
	public void setStation_order(int station_order) {
		this.station_order = station_order;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
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
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public BigDecimal getFront_distance() {
		return front_distance;
	}
	public void setFront_distance(BigDecimal front_distance) {
		this.front_distance = front_distance;
	}
	public BigDecimal getStation_distance() {
		return station_distance;
	}
	public void setStation_distance(BigDecimal station_distance) {
		this.station_distance = station_distance;
	}
	private BigDecimal front_distance;
	private BigDecimal station_distance;

}
