package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.NoticeDao;
import com.zah.entity.LoginInfo;
import com.zah.entity.Notice;
import com.zah.service.NoticeService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NoticeServiceImpl implements NoticeService {
	@Autowired
	CommonDao commonDao;
	@Autowired
	NoticeDao noticeDao;
	@Autowired
	PublicService publicService;

	// 展示页面
	public String noticeShow(Model model) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			return "bus/busNoticeShow";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 添加页面
	public String noticeAdd(Model model) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			return "bus/busNoticeAdd";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 更新页面
	public String noticeUpdate(Model model, String release_time, String admin_id) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (release_time == null || admin_id == null) {
				model.addAttribute("message", "请选择要编辑的通知公告");
				return "admin/error";
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			otherMap.put("column", "notice_content,release_time,admin_id");
			whereMap.put("release_time", release_time);
			whereMap.put("admin_id", admin_id);
			List<Notice> NoticeList = noticeDao.select(otherMap, whereMap);
			if (NoticeList.size() <= 0) {
				model.addAttribute("message", "通知公告信息不存在或已删除");
				return "admin/error";
			}

			Notice notice = NoticeList.get(0);
			model.addAttribute("notice", notice);
			return "bus/busNoticeUpdate";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	// ajax调用展示数据
	public String showNotice(int limit, int page, String username) {
		Map<String, Object> Map = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("school")) {
				Map.put("msg", "无权限");
				Map.put("code", 1);
				return new JSONObject(Map).toString();
			}
			Map<String, String> columnMap = new HashMap<>();
			if (!StringUtils.isEmpty(username)) {
				columnMap.put("username", username);
			}
			int count = noticeDao.getCount(columnMap);
			if (count > 0) {
				List<Notice> noticeList = noticeDao.selectNotice(columnMap, (page - 1) * limit, limit);

				Map.put("msg", "获取成功");
				Map.put("code", 0);
				Map.put("count", count);
				Map.put("data", noticeList);

			} else {
				Map.put("msg", "无数据");
				Map.put("code", 1);
			}

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			Map.put("msg", "系统错误");
			Map.put("code", 1);
			return new JSONObject(Map).toString();
		}

		return new JSONObject(Map).toString();

	}

	public String addNotice(String notice_content, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			if (notice_content == null || notice_content.length() <= 0 || notice_content.length() > 1000) {
				map.put("message", "请输入1000以内的通知公告内容");
				return new JSONObject(map).toString();
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			int adminId = loginInfo.getAdmin_id();
			long realseTime = System.currentTimeMillis() / 1000;
			Map<String, String> setMap = new HashMap<>();
			setMap.put("release_time", String.valueOf(realseTime));
			setMap.put("admin_id", String.valueOf(adminId));
			setMap.put("notice_content", notice_content);
			int insert = noticeDao.insert(setMap);
			if (insert >= 1) {
				map.put("success", true);
				map.put("message", "操作成功");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = sdf.format(new Date(((long) realseTime) * 1000));
				publicService.addLog("添加通知公告【" + time + "】");
			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteNotice(String release_time, String admin_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (release_time == null || admin_id == null) {
				map.put("message", "请选择要删除的通知公告");
				return new JSONObject(map).toString();
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			otherMap.put("column", "admin_id");
			whereMap.put("release_time", release_time);
			whereMap.put("admin_id", admin_id);

			Map<String, String> param = new HashMap<>();
			param.put("release_time", release_time);
			param.put("admin_id", admin_id);
			List<Map<String, Object>> NoticeList = commonDao.query(
					"SELECT a.username FROM sbus_notice r LEFT JOIN sbus_admin a on a.admin_id=r.admin_id where r.release_time=#{param.release_time} and r.admin_id=#{param.admin_id}",
					param);
			if (NoticeList.size() <= 0) {
				map.put("message", "通知公告信息不存在或已删除");
				return new JSONObject(map).toString();
			}

			int result = noticeDao.delete(whereMap);
			if (result <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date(Long.parseLong(release_time) * 1000));
			publicService.addLog("删除" + "【" + NoticeList.get(0).get("username") + "】于【" + time + "】发布的通知公告");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	public String updateNotice(String release_time, String notice_content, String admin_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			if (release_time == null || admin_id == null) {
				map.put("message", "请选择要编辑的通知公告信息");
				return new JSONObject(map).toString();
			}
			if (notice_content == null || notice_content.length() <= 0 || notice_content.length() > 1000) {
				map.put("message", "请输入1000以内的通知公告内容");
				return new JSONObject(map).toString();
			}

			Map<String, String> setMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			whereMap.put("release_time", release_time);
			whereMap.put("admin_id", admin_id);
			List<Map<String, Object>> NoticeList = commonDao.query(
					"SELECT a.username FROM sbus_notice r LEFT JOIN sbus_admin a on a.admin_id=r.admin_id where r.release_time=#{param.release_time} and r.admin_id=#{param.admin_id}",
					whereMap);
			if (NoticeList.size() <= 0) {
				map.put("message", "通知公告信息不存在或已删除");
				return new JSONObject(map).toString();
			}
			setMap.put("notice_content", notice_content);
			int update = noticeDao.update(setMap, whereMap);
			if (update <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date(Long.parseLong(release_time) * 1000));
			publicService.addLog("编辑" + "【" + NoticeList.get(0).get("username") + "】于【" + time + "】发布的通知公告");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}
	
	//微信公众号展示
	public String selectWx() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Notice> noticeList = new ArrayList<>();
		int count = noticeDao.getCount(new HashMap<>());
		if (count > 0) {
			noticeList = noticeDao.selectWx();
			map.put("msg", "获取成功");
			map.put("code", 0);
			map.put("count", count);
			map.put("data", noticeList);
		} else {
			map.put("msg", "无数据");
			map.put("code", 1);
		}
		return new JSONObject(map).toJSONString();
	}
	
	

}
