package com.zah.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface LoginService {
	public Map<String, Object> editPassword(String oldPassword, String password, String repassword,
                                            HttpSession session);

	public Map<String, Object> loginCheck(String username, String password, HttpSession session, Model model);
}
