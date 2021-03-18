package com.zah.controller;

import com.zah.service.impl.RetrieveServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage")
public class RetrieveController {
	@Autowired
	RetrieveServiceImpl retrieveServiceImpl;

	// 跳转页面
	@RequestMapping("retrieveShow")
	public String retrieveShow(Model model) {
		return retrieveServiceImpl.retrieveShow(model);
	}

	@RequestMapping("retriveUpdate")
	public String retriveUpdate(Model model, String release_time, String admin_id) {
		return retrieveServiceImpl.retrieveUpdate(model, release_time, admin_id);
	}

	@RequestMapping("retrieveAdd")
	public String retrieveAdd(Model model) {
		return retrieveServiceImpl.retrieveAdd(model);
	}

	// ajax调用接口
	@RequestMapping("showRetrieve")
	@ResponseBody
	public String showBusRetrieve(int limit, int page, String username) {
		return retrieveServiceImpl.showRetrieve(limit, page, username);

	}

	@RequestMapping("addRetrieve")
	@ResponseBody
	public String addRetrieve(String retrieve_content, HttpSession session) {
		return retrieveServiceImpl.addRetrieve(retrieve_content, session);
	}

	@RequestMapping("deleteRetrive")
	@ResponseBody
	public String deleteRetrieve(String release_time, String admin_id) {
		return retrieveServiceImpl.deleteRetrieve(release_time, admin_id);
	}

	@RequestMapping("updateRetrive")
	@ResponseBody
	public String updateRetrive(String release_time, String retrieve_content, String admin_id) {
		return retrieveServiceImpl.updateRetrive(release_time, retrieve_content, admin_id);
	}
}
