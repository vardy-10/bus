package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.AdminLogDao;
import com.zah.entity.AdminLog;
import com.zah.service.LogService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {
	@Autowired
	AdminLogDao adminLogDao;
	@Autowired
	PublicService publicService;

	public String showSystmLog(Model model) {
		if (!PublicService.isRole("system")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "admin/log";

	}

	public String showLogList(int limit, int page, String username, String startTime, String endTime) {

		Map<String, Object> adminLogMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("system")) {
				adminLogMap.put("msg", "无权限");
				adminLogMap.put("code", 1);
				return new JSONObject(adminLogMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();

			if (!StringUtils.isEmpty(username)) {
				columnMap.put("username", username);
			}
			if (!StringUtils.isEmpty(startTime)) {
				long time = Function.getInstance().timeStrToSeconds(startTime, "yyyy-MM-dd HH:mm:ss");

				if (time == -1) {
					adminLogMap.put("message", "输入正确的开始时间！");
					return new JSONObject(adminLogMap).toString();
				}
				columnMap.put("startTime", String.valueOf(time));
			}
			if (!StringUtils.isEmpty(endTime)) {
				long time = Function.getInstance().timeStrToSeconds(endTime, "yyyy-MM-dd HH:mm:ss");
				if (time == -1) {
					adminLogMap.put("message", "输入正确的结束时间！");
					return new JSONObject(adminLogMap).toString();
				}
				columnMap.put("endTime", String.valueOf(time));
			}
			int count = adminLogDao.getAdminLogCount(columnMap);
			List<AdminLog> adminLogList = new ArrayList<AdminLog>();
			if (count > 0) {
				adminLogList = adminLogDao.getAdminLogList(columnMap, (page - 1) * limit, limit);
			}
			adminLogMap.put("msg", "获取成功");
			adminLogMap.put("code", 0);
			adminLogMap.put("count", count);
			adminLogMap.put("data", adminLogList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			adminLogMap.put("msg", "系统错误");
			adminLogMap.put("code", 1);
			return new JSONObject(adminLogMap).toString();
		}
		return new JSONObject(adminLogMap).toString();

	}

	public String deleteLog(String startTime, String endTime, String username) {
		Map<String, Object> logMap = new HashMap<>();

		logMap.put("success", false);
		try {
			if (!PublicService.isRole("system")) {
				logMap.put("message", "无权限");
				return new JSONObject(logMap).toString();
			}

			if (StringUtils.isEmpty(username) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
				logMap.put("message", "最少输入一个选项！");
				return new JSONObject(logMap).toString();
			}
			Map<String, String> columnMap = new HashMap<>();

			if (!StringUtils.isEmpty(username)) {
				columnMap.put("username", username);
			}

			if (!StringUtils.isEmpty(startTime)) {
				long time = Function.getInstance().timeStrToSeconds(startTime, "yyyy-MM-dd HH:mm:ss");
				if (time == -1) {
					logMap.put("message", "输入正确的开始时间！");
					return new JSONObject(logMap).toString();
				}
				columnMap.put("startTime", String.valueOf(time));

			}
			if (!StringUtils.isEmpty(endTime)) {
				long time = Function.getInstance().timeStrToSeconds(endTime, "yyyy-MM-dd HH:mm:ss");
				if (time == -1) {
					logMap.put("message", "输入正确的结束时间！");
					return new JSONObject(logMap).toString();
				}
				columnMap.put("endTime", String.valueOf(time));
			} else {// 没有填结束时间时，默认到当前时间
				columnMap.put("log_time_end", String.valueOf(Function.getInstance().getNowTimestamp()));
			}
			int count = adminLogDao.clearUserLogBy(columnMap);

			logMap.put("success", true);
			logMap.put("message", "添加成功！");
			if (count > 0) {
				publicService.addLog("删除" + count + "条系统管理员日志记录");
			}
			return new JSONObject(logMap).toString();

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			logMap.put("message", "系统错误！");
		}
		return new JSONObject(logMap).toString();

	}

}
