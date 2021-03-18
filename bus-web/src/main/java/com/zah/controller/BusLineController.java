package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.BusLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("manage")
public class BusLineController {
	@Autowired
	BusLineServiceImpl busLineServiceImpl;

	@RequestMapping("busLineShow")
	public String busLineShow(Model model) {
		return busLineServiceImpl.busLineShow(model);
	}

	@RequestMapping("busLineUpdate")
	public String busLineUpdate(String line_id, Model model) {
		return busLineServiceImpl.busLineUpdate(line_id, model);
	}

	@RequestMapping("busLineAdd")
	public String busLineAdd(Model model) {
		return busLineServiceImpl.busLineAdd(model);
	}

	@RequestMapping("busLineList")
	@ResponseBody
	public String busLineList(int limit, int page, String line_name) {
		return busLineServiceImpl.showBusLineList(limit, page, line_name);
	}

	@RequestMapping("deleteBusLine")
	@ResponseBody
	public String deleteBusLine(String line_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return busLineServiceImpl.deleteBusLine(line_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("addBusLine")
	@ResponseBody
	public String addBusLine(String line_name, String line_type) {
		return busLineServiceImpl.addBusLine(line_name, line_type);
	}

	@RequestMapping("updateBusLine")

	public String updateBusLine(String line_name, String line_type, String line_id, Model model) {
		return busLineServiceImpl.busLineUpdate(line_id, model);

	}

	@RequestMapping("editBusLine")
	@ResponseBody
	public String editBusLine(String line_name, String line_type, String line_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (BusStationController.BUSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				synchronized (MonitorController.SHIFTSLOCK) {
					if (System.currentTimeMillis() - timeStart > 10000) {
						map.put("message", "操作失效");
					}
					return busLineServiceImpl.editBusLine(line_name, line_type, line_id);
				}
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	@RequestMapping("toBusStation")
	public String toBusStation(String line_id, Model model) {

		return busLineServiceImpl.toBusStation(line_id, model);

	}

	@RequestMapping("importLinePoint")
	public String importLinePoint(String line_id, Model model) {
		return busLineServiceImpl.importLinePoint(line_id, model);
	}

	@RequestMapping("trackLineImport")
	@ResponseBody
	public String trackLineImport(String line_id, MultipartFile file) {
		return busLineServiceImpl.trackLineImport(file,line_id);
	}
}
