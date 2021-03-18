package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.BusStationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("manage")
public class BusStationController {
	@Autowired
	BusStationServiceImpl busStationServiceImpl;
	public static final String BUSLOCK = "";

	// 跳转编辑车站页
	@RequestMapping("busStationUpdate")
	public String busStationUpdate(Model model, String station_id) {
		return busStationServiceImpl.busStationUpdate(model, station_id);
	}

	// 跳转添加车站页
	@RequestMapping("busStationAdd")
	public String busStationAdd(Model model, String line_id) {
		model.addAttribute("line_id", line_id);
		return busStationServiceImpl.busStationAdd(model, line_id);
	}

	// 跳转展示车站页
	@RequestMapping("busStationShow")
	public String busStationShow(String line_id, Model model) {
		return busStationServiceImpl.busStationShow(line_id, model);
	}

	// ajax请求展示车站页信息
	@RequestMapping("busStationList")
	@ResponseBody
	public String busStationList(int limit, int page, String station_name, String line_id) {
		return busStationServiceImpl.showBusStationList(limit, page, station_name, line_id);
	}

	// 删除车站
	@RequestMapping("deleteBusStation")
	@ResponseBody
	public String deleteBusStation(String station_id) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (BusStationController.BUSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return busStationServiceImpl.deleteBusStation(station_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("addBusStation")
	@ResponseBody
	public String addBusStation(String line_id, String station_dir, String radius, String station_name,
			String station_order) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (BusStationController.BUSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return busStationServiceImpl.addBusStation(line_id, station_dir, radius, station_name, station_order);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("editBusStation")
	@ResponseBody
	public String Station(String radius, String station_name, String station_dir, String station_id,
			String station_order_hidden, String station_order) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (BusStationController.BUSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return busStationServiceImpl.editBusStation(station_name, station_dir, station_order, radius,
						station_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}
}
