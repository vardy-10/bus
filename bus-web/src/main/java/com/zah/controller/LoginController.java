package com.zah.controller;

import com.zah.service.impl.LoginServiceImpl;
import com.zah.thread.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage")
public class LoginController {
	@Autowired
	LoginServiceImpl loginServiceImpl;

	@RequestMapping("")
	public String login() {
		return "admin/login";
	}

	@RequestMapping("index")
	public String index() {

		return "admin/index";
	}

	@RequestMapping("/login")
	@ResponseBody
	public Map<String, Object> loginCheck(String username, String password, HttpSession session, Model model) {
		return loginServiceImpl.loginCheck(username, password, session, model);

	}

	@RequestMapping("/editPassword")
	@ResponseBody
	public Map<String, Object> editPassword(String oldPassword, String password, String repassword,
			HttpSession session) {
		return loginServiceImpl.editPassword(oldPassword, password, repassword, session);

	}

	@RequestMapping("/exitSystem")
	public String exitSystem(HttpSession session) {

		session.removeAttribute("loginInfo");// 清除session
		try {
			return "redirect:/manage";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
		}
		return null;

	}

}
