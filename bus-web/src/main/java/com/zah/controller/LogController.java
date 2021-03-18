package com.zah.controller;

import com.zah.dao.AdminLogDao;
import com.zah.service.PublicService;
import com.zah.service.impl.LogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("manage")
public class LogController {
	@Autowired
	LogServiceImpl logServiceImpl;
	@Autowired
	AdminLogDao adminLogDao;
	@Autowired
	PublicService publicService;

	@RequestMapping("showLog")
	public String showSystmLog(Model model) {

		return logServiceImpl.showSystmLog(model);

	}

	@RequestMapping("showLogList")
	@ResponseBody
	public String showLogList(int limit, int page, String username, String startTime, String endTime) {

		return logServiceImpl.showLogList(limit, page, username, startTime, endTime);

	}

	@RequestMapping("deleteLog")
	@ResponseBody
	public String deleteLog(String startTime, String endTime, String username) {

		return logServiceImpl.deleteLog(startTime, endTime, username);

	}

	@RequestMapping("homePage")
	public String homePage() {

		return "admin/homePage";

	}

	@RequestMapping("aboutUs")
	public String aboutUs() {

		return "admin/aboutUs";

	}
}
