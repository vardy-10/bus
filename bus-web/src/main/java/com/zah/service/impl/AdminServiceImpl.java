package com.zah.service.impl;


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.AdminDao;
import com.zah.entity.AdminUser;
import com.zah.entity.LoginInfo;
import com.zah.entity.Role;
import com.zah.service.AdminService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminDao adminDao;
	@Autowired
	PublicService publicService;

	public String toadd(Model model) {
		if (!PublicService.isRole("system")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}

		return "admin/add";
	}

	public String changePass(Model model) {
		if (!PublicService.isRole("system")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "admin/changePass";
	}

	public String adminManageShow(Model model) {
		if (!PublicService.isRole("system")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}

		return "admin/show";
	}

	public String aboutMe(Model model, HttpSession session) {
		LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
		if (loginInfo.getType() != 1) {
			List<Role> roleList = adminDao.selectRoleInfo();
			model.addAttribute("list", roleList);// 角色信息存入model
		}
		return "admin/aboutMe";

	}

	public String toupp(String admin_id, Model model, HttpSession session) {
		try {
			if (!PublicService.isRole("system")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			if (admin_id == null) {
				model.addAttribute("message", "请选择要查看的管理员");
				return "admin/error";
			}
			List<AdminUser> oneAdminList = adminDao.selectInfoByKey("admin_id", admin_id, "*");
			if (oneAdminList.size() <= 0) {
				model.addAttribute("message", "管理员不存在或已删除");
				return "admin/error";
			}
			AdminUser adminUser = oneAdminList.get(0);
			if (adminUser.getType() != 2) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			if (adminUser.getAdmin_id() == loginInfo.getAdmin_id()) {
				model.addAttribute("message", "无权限");
				return "admin/error";

			}
			List<Role> roleList = adminDao.selectRoleInfo();
			model.addAttribute("list", roleList);// 角色信息存入model
			model.addAttribute("oneAdminList", adminUser);
			return "admin/upp";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	public String addAdmin(String username, String password, String repassword, String name, String[] role_list) {
		Map<String, Object> map = new HashMap<String, Object>();

		StringBuilder newOperates = new StringBuilder();

		try {
			map.put("success", false);
			if (!PublicService.isRole("system")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(username) || !ValidateUtil.getInstance().isUserName(username)
					|| username.length() > 20) {
				map.put("message", "请输入20位以内的账号，且必须由字母或数字组成！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(password) || password.length() < 6) {
				map.put("message", "请输入六位及以上的密码！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(repassword)) {
				map.put("message", "请输入确认密码！");
				return new JSONObject(map).toString();
			}
			if (!password.equals(repassword)) {
				map.put("message", "密码与确认密码不一致！");
				return new JSONObject(map).toString();
			}
			if (name.length() > 20) {
				map.put("message", "请输入20位以内的名称！");
				return new JSONObject(map).toString();
			}
			if (role_list != null) {
				for (int i = 0; i < role_list.length; i++) {
					newOperates.append("," + role_list[i]);
				}
			}
			String role_list_str = null;
			if (newOperates.length() > 0) {
				role_list_str = newOperates.substring(1);
			} else {
				role_list_str = newOperates.toString();
			}

			List<AdminUser> adminList = adminDao.selectInfoByKey("username", username, "admin_id");
			if (!adminList.isEmpty()) {
				map.put("message", "您输入的账号已存在，请重新输入！");
				return new JSONObject(map).toString();
			}
			int type = adminDao.addAdmin(username, Function.getInstance().SHA1(password), name, role_list_str);

			if (type > 0) {
				map.put("success", true);
				map.put("message", "添加成功！");
				publicService.addLog("添加管理员【" + username + "】");
			} else {
				map.put("message", "添加失败！");
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}

		return new JSONObject(map).toString();
	}

	public String editAdmin(String username, String password, String repassword, String name, String[] role_list,
			String admin_id, HttpSession session) {

		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder newOperates = new StringBuilder();
		map.put("success", false);
		try {
			if (!PublicService.isRole("system")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (admin_id == null) {
				map.put("message", "请选择要编辑的管理员");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(password)) {
				if (password.length() < 6) {
					map.put("message", "请输入六位及以上的密码！");
					return new JSONObject(map).toString();
				}
				if (StringUtils.isEmpty(repassword)) {
					map.put("message", "请输入确认密码！");
					return new JSONObject(map).toString();
				}
				if (!password.equals(repassword)) {
					map.put("message", "密码与确认密码不一致！");
					return new JSONObject(map).toString();
				}
			}
			if (name.length() > 20) {
				map.put("message", "请输入20位以内的名称！");
				return new JSONObject(map).toString();
			}
			if (role_list != null) {
				for (int i = 0; i < role_list.length; i++) {
					newOperates.append("," + role_list[i]);
				}
			}
			String role_list_str = null;
			if (newOperates.length() > 0) {
				role_list_str = newOperates.substring(1);
			} else {
				role_list_str = newOperates.toString();
			}

			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			List<AdminUser> oneAdminList = adminDao.selectInfoByKey("admin_id", admin_id, "type,username");
			if (oneAdminList.size() <= 0) {
				map.put("message", "管理员不存在或已删除");
				return new JSONObject(map).toString();
			}
			AdminUser adminUser = oneAdminList.get(0);
			if (adminUser.getType() != 2) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (admin_id.equals(String.valueOf(loginInfo.getAdmin_id()))) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			int result;
			if (!StringUtils.isEmpty(password)) {
				result = adminDao.updateAdminInfo(Function.getInstance().SHA1(password), name, role_list_str, admin_id);
			} else {
				result = adminDao.updateAdminInfo("", name, role_list_str, admin_id);
			}
			if (result > 0) {
				map.put("success", true);
				map.put("message", "编辑成功！");

				publicService.addLog("编辑管理员【" + adminUser.getUsername() + "】");
				return new JSONObject(map).toString();

			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String toShowOne(String admin_id, Model model, HttpSession session) {
		try {
			if (!PublicService.isRole("system")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			if (admin_id == null) {
				model.addAttribute("message", "请选择要查看的管理员");
				return "admin/error";
			}
			List<AdminUser> oneAdminList = adminDao.selectInfoByKey("admin_id", admin_id, "*");
			if (oneAdminList.size() <= 0) {
				model.addAttribute("message", "管理员不存在或已删除");
				return "admin/error";
			}
			AdminUser adminUser = oneAdminList.get(0);
			if (adminUser.getType() != 2) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			if (adminUser.getAdmin_id() == loginInfo.getAdmin_id()) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			List<Role> roleList = adminDao.selectRoleInfo();
			model.addAttribute("list", roleList);// 角色信息存入model
			model.addAttribute("oneAdminList", adminUser);
			return "admin/showOne";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String showAdminList(int limit, int page, String username, HttpSession session) {
		Map<String, Object> adminListMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("system")) {
				adminListMap.put("msg", "无权限");
				adminListMap.put("code", 1);
				return new JSONObject(adminListMap).toString();
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			Map<String, String> columnMap = new HashMap<String, String>();
			columnMap.put("admin_id", String.valueOf(loginInfo.getAdmin_id()));
			if (!StringUtils.isEmpty(username)) {
				columnMap.put("username", username);
			}
			int count = adminDao.getAdminListCount(columnMap);
			List<AdminUser> adminList = new ArrayList<AdminUser>();
			if (count > 0) {
				adminList = adminDao.getAdminList(columnMap, (page - 1) * limit, limit);
			}
			adminListMap.put("msg", "获取成功");
			adminListMap.put("code", 0);
			adminListMap.put("count", count);
			adminListMap.put("data", adminList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			adminListMap.put("msg", "系统错误");
			adminListMap.put("code", 1);
			return new JSONObject(adminListMap).toString();
		}
		JSONObject json = new JSONObject(adminListMap);
		return json.toString();

	}

	public String deleteAdmin(String admin_id, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {

			if (!PublicService.isRole("system")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (admin_id == null) {
				map.put("message", "请选择要删除的管理员");
				return new JSONObject(map).toString();
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			String adminId = String.valueOf(admin_id);
			List<AdminUser> oneAdminList = adminDao.selectInfoByKey("admin_id", adminId, "type,username");
			if (oneAdminList.size() <= 0) {
				map.put("message", "系统管理员不存在或已删除");
				return new JSONObject(map).toString();
			}
			AdminUser adminUser = oneAdminList.get(0);
			if (adminUser.getType() != 2) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (admin_id.equals(String.valueOf(loginInfo.getAdmin_id()))) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			int result = adminDao.deleteAdmin(adminId);

			if (result > 0) {
				map.put("success", true);
				map.put("message", "操作成功");
				publicService.addLog("删除管理员【" + adminUser.getUsername() + "】");
			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {

			Start.projectLog.writeError(e);
			map.put("message", "系统错误");

		}

		return new JSONObject(map).toString();

	}

	@Override
	public List<AdminUser> selectInfoByKey(String key, String value, String columns) {

		return adminDao.selectInfoByKey(key, value, columns);

	}

	@Override
	public int updatePasswordbyId(String admin_id, String password) {

		return adminDao.updatePasswordbyId(admin_id, password);
	}

	public List<Role> selectRoleInfo() {

		return adminDao.selectRoleInfo();
	}

}
