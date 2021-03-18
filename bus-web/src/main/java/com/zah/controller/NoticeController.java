package com.zah.controller;

import com.zah.service.impl.NoticeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage")
public class NoticeController {
	@Autowired
	NoticeServiceImpl noticeServiceImpl;

	// 跳转页面
	@RequestMapping("noticeShow")
	public String noticeShow(Model model) {
		return noticeServiceImpl.noticeShow(model);
	}
	@RequestMapping("noticeUpdate")
	public String noticeUpdate(Model model, String release_time, String admin_id) {
		return noticeServiceImpl.noticeUpdate(model, release_time, admin_id);
	}

	@RequestMapping("noticeAdd")
	public String noticeAdd(Model model) {
		return noticeServiceImpl.noticeAdd(model);
	}

	// ajax调用接口
	@RequestMapping("showNotice")
	@ResponseBody
	public String showNotice(int limit, int page, String username) {
		return noticeServiceImpl.showNotice(limit, page, username);

	}

	@RequestMapping("addNotice")
	@ResponseBody
	public String addNotice(String notice_content, HttpSession session) {
		return noticeServiceImpl.addNotice(notice_content, session);
	}

	@RequestMapping("deleteNotice")
	@ResponseBody
	public String deleteNotice(String release_time, String admin_id) {
		return noticeServiceImpl.deleteNotice(release_time, admin_id);
	}

	@RequestMapping("updateNotice")
	@ResponseBody
	public String updateNotice(String release_time, String notice_content, String admin_id) {
		return noticeServiceImpl.updateNotice(release_time, notice_content, admin_id);
	}
}
