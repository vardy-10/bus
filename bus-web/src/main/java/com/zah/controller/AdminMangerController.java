package com.zah.controller;

import com.zah.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage")
public class AdminMangerController {
	@Autowired
	private AdminServiceImpl adminServiceImpl;

	@RequestMapping("aboutMe")
	public String aboutMe(Model model, HttpSession session) {
		return adminServiceImpl.aboutMe(model, session);
	}

	@RequestMapping("changePass")
	public String changePass(Model model) {

		return adminServiceImpl.changePass(model);
	}

	@RequestMapping("show")
	public String adminManageShow(Model model) {
		return adminServiceImpl.adminManageShow(model);
	}

	@RequestMapping("toadd")
	public String toadd(Model model) {
		return adminServiceImpl.toadd(model);
	}

	@RequestMapping("/addAdmin")
	@ResponseBody
	public String addAdmin(String username, String password, String repassword, String name, String[] role_list) {
		return adminServiceImpl.addAdmin(username, password, repassword, name, role_list);
	}

	@RequestMapping("toupp")
	public String toupp(String admin_id, Model model, HttpSession session) {
		return adminServiceImpl.toupp(admin_id, model, session);

	}

	@RequestMapping("editAdminInfo")
	@ResponseBody
	public String editAdmin(String username, String password, String repassword, String name, String[] role_list,
			String admin_id, HttpSession session) {

		return adminServiceImpl.editAdmin(username, password, repassword, name, role_list, admin_id, session);
	}

	@RequestMapping("toShowOne")
	public String toShowOne(String admin_id, Model model, HttpSession session) {
		return adminServiceImpl.toShowOne(admin_id, model, session);
	}

	@RequestMapping("showAdminList")
	@ResponseBody
	public String showAdminList(int limit, int page, String username, HttpSession session) {

		return adminServiceImpl.showAdminList(limit, page, username, session);

	}

	@RequestMapping("deleteAdmin")
	@ResponseBody
	public String deleteAdmin(String admin_id, HttpSession session) {

		return adminServiceImpl.deleteAdmin(admin_id, session);

	}

}
