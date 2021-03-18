package com.zah.entity;

public class BusLine {
	private int line_id;
	private String line_name;
	private int line_type;
	private String up_origin_name;
	private String up_terminal_name;
	private String down_origin_name;
	private String down_terminal_name;
	private String up_lines_point;
	private String down_lines_point;

	public int getLine_id() {
		return line_id;
	}

	public void setLine_id(int line_id) {
		this.line_id = line_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public int getLine_type() {
		return line_type;
	}

	public void setLine_type(int line_type) {
		this.line_type = line_type;
	}

	public String getUp_origin_name() {
		return up_origin_name;
	}

	public void setUp_origin_name(String up_origin_name) {
		this.up_origin_name = up_origin_name;
	}

	public String getUp_terminal_name() {
		return up_terminal_name;
	}

	public void setUp_terminal_name(String up_terminal_name) {
		this.up_terminal_name = up_terminal_name;
	}

	public String getDown_origin_name() {
		return down_origin_name;
	}

	public void setDown_origin_name(String down_origin_name) {
		this.down_origin_name = down_origin_name;
	}

	public String getDown_terminal_name() {
		return down_terminal_name;
	}

	public void setDown_terminal_name(String down_terminal_name) {
		this.down_terminal_name = down_terminal_name;
	}

	public String getUp_lines_point() {
		return up_lines_point;
	}

	public void setUp_lines_point(String up_lines_point) {
		this.up_lines_point = up_lines_point;
	}

	public String getDown_lines_point() {
		return down_lines_point;
	}

	public void setDown_lines_point(String down_lines_point) {
		this.down_lines_point = down_lines_point;
	}
}
