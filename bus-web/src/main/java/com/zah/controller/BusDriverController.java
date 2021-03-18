package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.BusDriverServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("manage")
public class BusDriverController {
	@Autowired
	BusDriverServiceImpl busDriverServiceImpl;

	@RequestMapping("busDriverShow")
	public String busDriverShow(Model model) {
		return busDriverServiceImpl.busDriverShow(model);
	}

	@RequestMapping("busDriverAdd")
	public String busDriverAdd(Model model) {
		return busDriverServiceImpl.busDriverAdd(model);
	}

	@RequestMapping("busDriverUpdate")
	public String busDriverUpdate(Model model, String driver_id) {
		return busDriverServiceImpl.busDriverUpdate(model, driver_id);
	}

	@RequestMapping("deletebusDriver")
	@ResponseBody
	public String deleteBusDriver(String driver_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return busDriverServiceImpl.deleteBusDriver(driver_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("showBusDriver")
	@ResponseBody
	public String showBusDriver(int limit, int page, String driver_name, String driver_number) {
		return busDriverServiceImpl.showBusDriver(limit, page, driver_name, driver_number);

	}

	@RequestMapping("addBusDriver")
	@ResponseBody
	public String addBusDriver(String driver_number, String driver_name, String driver_sex, String driver_phone,
			String driver_identity) {
		return busDriverServiceImpl.addBusDriver(driver_number, driver_name, driver_sex, driver_phone, driver_identity);
	}

	@RequestMapping("updateBusDriver")
	@ResponseBody
	public String updateBusDriver(String driver_id, String driver_number, String driver_name, String driver_sex,
			String driver_phone, String driver_identity) {
		return busDriverServiceImpl.updateBusDriver(driver_id, driver_number, driver_name, driver_sex, driver_phone,
				driver_identity);
	}

}
