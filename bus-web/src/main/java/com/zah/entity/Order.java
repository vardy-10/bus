package com.zah.entity;

import java.util.Date;

public class Order {
	
	private long order_id;
	private String order_sn;
	private double price;
	private int passenger_type;
	private int auth_type;
	private int passenger_id;
	private Date shifts_date;
	private String shifts_number;
	private int create_time;
	private int order_state;
	private String refund_sn;
	private int refund_time;
	private int use_time;
	public long getOrder_id() {
		return order_id;
	}
	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getPassenger_type() {
		return passenger_type;
	}
	public void setPassenger_type(int passenger_type) {
		this.passenger_type = passenger_type;
	}
	public int getAuth_type() {
		return auth_type;
	}
	public void setAuth_type(int auth_type) {
		this.auth_type = auth_type;
	}
	public int getPassenger_id() {
		return passenger_id;
	}
	public void setPassenger_id(int passenger_id) {
		this.passenger_id = passenger_id;
	}
	public Date getShifts_date() {
		return shifts_date;
	}
	public void setShifts_date(Date shifts_date) {
		this.shifts_date = shifts_date;
	}
	public String getShifts_number() {
		return shifts_number;
	}
	public void setShifts_number(String shifts_number) {
		this.shifts_number = shifts_number;
	}
	public int getCreate_time() {
		return create_time;
	}
	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}
	public int getOrder_state() {
		return order_state;
	}
	public void setOrder_state(int order_state) {
		this.order_state = order_state;
	}
	public String getRefund_sn() {
		return refund_sn;
	}
	public void setRefund_sn(String refund_sn) {
		this.refund_sn = refund_sn;
	}
	public int getRefund_time() {
		return refund_time;
	}
	public void setRefund_time(int refund_time) {
		this.refund_time = refund_time;
	}
	public int getUse_time() {
		return use_time;
	}
	public void setUse_time(int use_time) {
		this.use_time = use_time;
	}
}
