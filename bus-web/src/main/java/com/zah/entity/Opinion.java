package com.zah.entity;

public class Opinion {
	private int passenger_id;
	private long submit_time;
	private String opinion_content;
	public int getPassenger_id() {
		return passenger_id;
	}
	public void setPassenger_id(int passenger_id) {
		this.passenger_id = passenger_id;
	}
	public long getSubmit_time() {
		return submit_time;
	}
	public void setSubmit_time(long submit_time) {
		this.submit_time = submit_time;
	}
	public String getOpinion_content() {
		return opinion_content;
	}
	public void setOpinion_content(String opinion_content) {
		this.opinion_content = opinion_content;
	}
}
