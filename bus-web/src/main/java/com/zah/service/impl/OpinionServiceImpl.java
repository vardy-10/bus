package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.OpinionDao;
import com.zah.entity.Opinion;
import com.zah.service.OpinionService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpinionServiceImpl implements OpinionService {
	@Autowired
	OpinionDao opinionDao;
	@Autowired
	PublicService publicService;

	public String opinionShow(Model model) {
		if (!PublicService.isRole("school")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}

		return "bus/busOpinionShow";
	}

	// 展示意见
	public String showOpinion(int limit, int page, String submit_time) {
		Map<String, Object> opinionMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("school")) {
				opinionMap.put("msg", "无权限");
				opinionMap.put("code", 1);
				return new JSONObject(opinionMap).toString();
			}

			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(submit_time)) {
				whereMap.put("submit_time", submit_time);

			}
			otherMap.put("column", "passenger_id,submit_time,opinion_content");
			otherMap.put("order", "passenger_id DESC");
			otherMap.put("limit", (page - 1) * limit + "," + limit);
			int count = opinionDao.getCountBy(submit_time);
			List<Opinion> opinion = new ArrayList<Opinion>();

			if (count > 0) {

				opinion = opinionDao.select(otherMap, whereMap);
				opinionMap.put("msg", "获取成功");
				opinionMap.put("code", 0);
				opinionMap.put("count", count);
				opinionMap.put("data", opinion);

			} else {
				opinionMap.put("msg", "无数据");
				opinionMap.put("code", 1);
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			opinionMap.put("msg", "系统错误");
			opinionMap.put("code", 1);
		}
		return new JSONObject(opinionMap).toString();
	}

	// 删除意见
	public String deleteOpinion(String passenger_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			// 权限判断
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// 接收提交并判断
			if (passenger_id == null) {
				map.put("message", "请选择要删除的意见");
				return new JSONObject(map).toString();
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			otherMap.put("column", passenger_id);
			whereMap.put("passenger_id", passenger_id);
			List<Opinion> OpinionList = opinionDao.select(otherMap, whereMap);
			if (OpinionList.size() <= 0) {
				map.put("message", "意见不存在或已删除");
				return new JSONObject(map).toString();
			}
			int result = opinionDao.delete(whereMap);
			if (result <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除乘客编号为【" + passenger_id + "的意见" + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	//微信公众号添加意见
	public int addWx(Opinion opinion) {
		int result = opinionDao.insertWx(opinion.getSubmit_time(), opinion.getPassenger_id(), opinion.getOpinion_content());
		return result;
	}
	
	
	
}
