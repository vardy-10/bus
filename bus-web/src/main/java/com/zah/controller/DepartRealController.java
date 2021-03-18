package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.DepartRealServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage")
public class DepartRealController {

	@Autowired
	DepartRealServiceImpl departRealServiceImpl;
	public static final String DEPARTREALLOCK = "";

	@RequestMapping("departRealShow")

	public String departRealShow(String line_id, Model model) {
		return departRealServiceImpl.departRealShow(model, line_id);
	}

	@RequestMapping("departRealAdd")
	public String departRealAdd(Model model, String line_id, String depart_dir, String date, String time) {

		return departRealServiceImpl.departRealAdd(model, line_id, depart_dir, date, time);
	}

	@RequestMapping("departRealUpdate")
	public String departRealUpdate(Model model, String line_id, String depart_dir, String date, String time,
                                   String vehicle_number, String driver_name, String original_vehicle_id, String original_driver_id) {
		return departRealServiceImpl.departRealUpdate(model, line_id, depart_dir, date, time, original_vehicle_id,
				original_driver_id);
	}

	@RequestMapping("addDepartReal")
	@ResponseBody
	public String addDepartReal(Model model, String date, String time, String depart_dir, String line_id,
                                String vehicle_id, String driver_id) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (DepartRealController.DEPARTREALLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return departRealServiceImpl.addDepartReal(model, date, time, line_id, vehicle_id, driver_id,
						depart_dir);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("deleteDepartReal")
	@ResponseBody
	public String deleteDepartReal(String date, String time, String line_id, String depart_dir, String vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (DepartRealController.DEPARTREALLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return departRealServiceImpl.deleteDepartReal(date, time, line_id, depart_dir, vehicle_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("updateDepartReal")
	@ResponseBody
	public String updateDepartReal(String line_id, Model model, String date, String time, String vehicle_id,
                                   String driver_id, String depart_dir, String seat_num, String original_vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (DepartRealController.DEPARTREALLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return departRealServiceImpl.updateDepartReal(model, line_id, time, date, vehicle_id, driver_id,
						depart_dir, original_vehicle_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("showDepartReal")
	@ResponseBody
	public String showDepartReal(String line_id, Model model, String date) {
		return departRealServiceImpl.showDepartReal(model, line_id, date);
	}

}
