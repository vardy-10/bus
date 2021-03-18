package com.zah.service.impl;

import com.zah.entity.AdminUser;
import com.zah.entity.LoginInfo;
import com.zah.service.LoginService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
	@Autowired
	AdminServiceImpl adminServiceImpl;
	@Autowired
	PublicService publicService;

	@Override
	public Map<String, Object> editPassword(String oldPassword, String password, String repassword,
			HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			String adminId = String.valueOf(loginInfo.getAdmin_id());
			List<AdminUser> adminList = adminServiceImpl.selectInfoByKey("admin_id", adminId, "password");
			if (adminList.size() <= 0) {
				map.put("message", "用户不存在或已删除！");
				return map;
			}
			if (StringUtils.isEmpty(oldPassword)) {
				map.put("message", "请填写原密码!");
				return null;
			}
			if (StringUtils.isEmpty(password)) {
				map.put("message", "请填写新密码！");
				return map;
			}
			if (StringUtils.isEmpty(repassword)) {
				map.put("message", "请填写确认密码！");
				return map;
			}
			if (!password.equals(repassword)) {
				map.put("message", "新密码与确认密码不一致！");
				return map;
			}

			AdminUser pwd = adminList.get(0);
			if (!Function.getInstance().SHA1(oldPassword).equals(pwd.getPassword())) {
				map.put("message", "原密码错误！");
				return map;
			}
			int result = adminServiceImpl.updatePasswordbyId(adminId, Function.getInstance().SHA1(password));
			if (result <= 0) {
				map.put("message", "操作失败！");
			}

			map.put("success", true);

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误！");
		}

		return map;
	}

	@Override
	public Map<String, Object> loginCheck(String username, String password, HttpSession session, Model model) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (StringUtils.isEmpty(username) || username.length() > 20) {
				map.put("message", "请输入20位以内的用户名！");
				return map;
			}
			if (StringUtils.isEmpty(password)) {
				map.put("message", "请输入密码！");
				return map;
			}

			List<AdminUser> adminList = adminServiceImpl.selectInfoByKey("username", username, "*");

			if (adminList.size() <= 0) {
				map.put("message", "用户不存在！");
				return map;
			}
			AdminUser adminUser = adminList.get(0);
			if (!Function.getInstance().SHA1(password).equals(adminUser.getPassword())) {
				map.put("message", "密码错误！");
				return map;
			}
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setAdmin_id(adminUser.getAdmin_id());
			loginInfo.setUsername(adminUser.getUsername());
			loginInfo.setName(adminUser.getName());
			loginInfo.setRole_list(adminUser.getRole_list());
			loginInfo.setType(adminUser.getType());
			session.setAttribute("loginInfo", loginInfo);// 登录信息存session
			map.put("success", true);
			publicService.addLog("管理员【" + adminUser.getUsername() + "】" + "登录");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误！");

		}
		return map;

	}

}
