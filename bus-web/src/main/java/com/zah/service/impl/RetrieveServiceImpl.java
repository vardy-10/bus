package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.RetrieveDao;
import com.zah.entity.LoginInfo;
import com.zah.entity.Retrieve;
import com.zah.service.PublicService;
import com.zah.service.RetrieveService;
import com.zah.thread.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RetrieveServiceImpl implements RetrieveService {

	@Autowired
	RetrieveDao retrieveDao;
	@Autowired
	PublicService publicService;
	@Autowired
	CommonDao commonDao;

	// 展示页面
	public String retrieveShow(Model model) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			return "bus/busRetrieveShow";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 添加页面
	public String retrieveAdd(Model model) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			return "bus/busRetrieveAdd";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 更新页面
	public String retrieveUpdate(Model model, String release_time, String admin_id) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (release_time == null || admin_id == null) {
				model.addAttribute("message", "请选择要编辑的失物招领");
				return "admin/error";
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			otherMap.put("column", "retrieve_content,release_time,admin_id");
			whereMap.put("release_time", release_time);
			whereMap.put("admin_id", admin_id);
			List<Retrieve> RetrieveList = retrieveDao.select(otherMap, whereMap);
			if (RetrieveList.size() <= 0) {
				model.addAttribute("message", "失物招领信息不存在或已删除");
				return "admin/error";
			}

			Retrieve retrieve = RetrieveList.get(0);
			model.addAttribute("retrieve", retrieve);
			return "bus/busRetrieveUpdate";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	// ajax调用展示数据
	public String showRetrieve(int limit, int page, String username) {
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

			int count = retrieveDao.getCount(columnMap);

			if (count > 0) {
				List<Retrieve> retrieveList = retrieveDao.selectRetrieve(columnMap, (page - 1) * limit, limit);

				Map.put("msg", "获取成功");
				Map.put("code", 0);
				Map.put("count", count);
				Map.put("data", retrieveList);

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

	public String addRetrieve(String retrieve_content, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			if (retrieve_content == null || retrieve_content.length() <= 0 || retrieve_content.length() > 1000) {
				map.put("message", "请输入1000以内的失物招领内容");
				return new JSONObject(map).toString();
			}
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			int adminId = loginInfo.getAdmin_id();
			long realseTime = System.currentTimeMillis() / 1000;
			Map<String, String> setMap = new HashMap<>();
			setMap.put("release_time", String.valueOf(realseTime));
			setMap.put("admin_id", String.valueOf(adminId));
			setMap.put("retrieve_content", retrieve_content);
			int insert = retrieveDao.insert(setMap);
			if (insert >= 1) {
				map.put("success", true);
				map.put("message", "操作成功");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = sdf.format(new Date(((long) realseTime) * 1000));
				publicService.addLog("添加失物招领【" + time + "】");
			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteRetrieve(String release_time, String admin_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (release_time == null || admin_id == null) {
				map.put("message", "请选择要删除的失物招领");
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
			List<Map<String, Object>> RetrieveList = commonDao.query(
					"SELECT a.username FROM sbus_retrieve r LEFT JOIN sbus_admin a on a.admin_id=r.admin_id where r.release_time=#{param.release_time} and r.admin_id=#{param.admin_id}",
					param);
			if (RetrieveList.size() <= 0) {
				map.put("message", "失物招领信息不存在或已删除");
				return new JSONObject(map).toString();
			}

			int result = retrieveDao.delete(whereMap);
			if (result <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date(Long.parseLong(release_time) * 1000));
			publicService.addLog("删除" + "【" + RetrieveList.get(0).get("username") + "】于【" + time + "】发布的失物招领");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	public String updateRetrive(String release_time, String retrieve_content, String admin_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			if (release_time == null || admin_id == null) {
				map.put("message", "请选择要编辑的失物招领信息");
				return new JSONObject(map).toString();
			}
			if (retrieve_content == null || retrieve_content.length() <= 0 || retrieve_content.length() > 1000) {
				map.put("message", "请输入1000以内的失物招领内容");
				return new JSONObject(map).toString();
			}

			Map<String, String> setMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			whereMap.put("release_time", release_time);
			whereMap.put("admin_id", admin_id);
			List<Map<String, Object>> RetrieveList = commonDao.query(
					"SELECT a.username FROM sbus_retrieve r LEFT JOIN sbus_admin a on a.admin_id=r.admin_id where r.release_time=#{param.release_time} and r.admin_id=#{param.admin_id}",
					whereMap);
			if (RetrieveList.size() <= 0) {
				map.put("message", "失物招领信息不存在或已删除");
				return new JSONObject(map).toString();
			}
			setMap.put("retrieve_content", retrieve_content);
			int update = retrieveDao.update(setMap, whereMap);
			if (update <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date(Long.parseLong(release_time) * 1000));
			publicService.addLog("编辑" + "【" + RetrieveList.get(0).get("username") + "】于【" + time + "】发布的失物招领");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}
	
	//微信公众号展示
	public String selectWx() {
		Map<String, Object> map = new HashMap<String, Object>();
		int count = retrieveDao.getCount(new HashMap<>());
		if (count > 0) {
			map.put("msg", "获取成功");
			map.put("code", 0);
			map.put("count", count);
			map.put("data", retrieveDao.selectWx());
		} else {
			map.put("msg", "无数据");
			map.put("code", 1);
		}
		return new JSONObject(map).toJSONString();
	}
	

}
