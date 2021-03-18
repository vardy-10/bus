package com.zah.entity;

public class Role {
private String role_code;
private String role_name;
private Integer show_order;
public Role(String role_code, String role_name, Integer show_order) {
	this.role_code = role_code;
	this.role_name = role_name;
	this.show_order = show_order;
}
public String getRole_code() {
	return role_code;
}
public void setRole_code(String role_code) {
	this.role_code = role_code;
}
public String getRole_name() {
	return role_name;
}
public void setRole_name(String role_name) {
	this.role_name = role_name;
}
public Integer getShow_order() {
	return show_order;
}
public void setShow_order(Integer show_order) {
	this.show_order = show_order;
}


}
