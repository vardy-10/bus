package com.zah.entity;

public class LoginInfo {
private int admin_id;
private int type;
private String username;
private String password;
private String name;
private String role_list;
private String role_name;
private String show_order;
public String getRole_name() {
	return role_name;
}
public void setRole_name(String role_name) {
	this.role_name = role_name;
}
public String getShow_order() {
	return show_order;
}
public void setShow_order(String show_order) {
	this.show_order = show_order;
}
public int getAdmin_id() {
	return admin_id;
}
public void setAdmin_id(int admin_id) {
	this.admin_id = admin_id;
}
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getRole_list() {
	return role_list;
}
public void setRole_list(String role_list) {
	this.role_list = role_list;
}

}
