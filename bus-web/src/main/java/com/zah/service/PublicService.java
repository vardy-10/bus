package com.zah.service;


import com.zah.dao.AdminLogDao;
import com.zah.entity.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class PublicService {

	@Autowired
	private AdminLogDao adminLogDao;

	public static boolean isRole(String code) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LoginInfo loginInfo = (LoginInfo) request.getSession().getAttribute("loginInfo");
		if (loginInfo.getType() == 1) {
			return true;
		}
		if (("," + loginInfo.getRole_list() + ",").contains("," + code + ",")) {
			return true;
		}
		return false;
	}

	public int addLog(String log_info) {
	
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LoginInfo loginInfo = (LoginInfo) request.getSession().getAttribute("loginInfo");
		String admin_id = String.valueOf(loginInfo.getAdmin_id());
		return adminLogDao.addAdminLog(admin_id, log_info, String.valueOf(System.currentTimeMillis() / 1000));

	}

}
