package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.ShiftsPlanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("manage")
@Controller
public class ShiftsPlanController {
	@Autowired
	ShiftsPlanServiceImpl shiftsPlanServiceImpl;
	public static final String SHIFTSLOCK = "";

	@RequestMapping("shiftsPlanShow")
	public String shiftsPlanShow(Model model, String act) {
		return shiftsPlanServiceImpl.shiftsPlanShow(model, act);
	}

	@RequestMapping("shiftsPlanAdd")
	public String shiftsPlanAdd(Model model) {
		return shiftsPlanServiceImpl.ShiftsPlanAdd(model);
	}

	@RequestMapping("shiftsPlanUpdate")
	public String shiftsPlanUpdate(Model model, String shifts_date, String shifts_number) {
		return shiftsPlanServiceImpl.shiftsPlanUpdate(model, shifts_date, shifts_number);
	}

	@RequestMapping("shiftsPlanConfirm")
	public String shiftsPlanConfirm(Model model, String[] shifts_numbers, String[] shifts_dates) {
		return shiftsPlanServiceImpl.shiftsPlanConfirm(model, shifts_dates, shifts_numbers);
	}

	@RequestMapping("showShiftsPlan")
	@ResponseBody
	public String showShiftsPlan(int limit, int page, String shifts_date, String startTime, String endTime,
			String driver_name, String line_name, String up_origin_name, String vehicle_number) {
		return shiftsPlanServiceImpl.showShiftsPlan(limit, page, shifts_date, startTime, endTime, driver_name,
				line_name, up_origin_name, vehicle_number);
	}

	@RequestMapping("addShiftsPlan")
	@ResponseBody
	public String addShiftsPlan(String shifts_date, String shifts_number, String line_id, String depart_time,
			String arrive_time, String driver_id, String vehicle_id) {
		return shiftsPlanServiceImpl.addShiftsAdd(shifts_date, shifts_number, line_id, depart_time, arrive_time,
				driver_id, vehicle_id);
	}

	@RequestMapping("updateShiftsPlan")
	@ResponseBody
	public String updateShiftsPlan(String shifts_date, String shifts_number, String driver_id, String vehicle_id,
			String arrive_time) {
		return shiftsPlanServiceImpl.updateShiftsPlan(shifts_date, shifts_number, driver_id, vehicle_id, arrive_time);
	}

	@RequestMapping("deleteShiftsPlan")
	@ResponseBody
	public String deleteShiftsPlan(String shifts_number, String shifts_date) {
		return shiftsPlanServiceImpl.deleteShiftsPlan(shifts_number, shifts_date);
	}

	@RequestMapping("importTmpl")
	@ResponseBody
	public String importTmpl(String[] shifts_numbers, String shifts_date) {
		return shiftsPlanServiceImpl.importTmpl(shifts_numbers, shifts_date);
	}

	@RequestMapping("copyShiftsPlan")
	@ResponseBody
	public String copyShiftsPlan(String[] shifts_numbers, String[] shifts_dates, String shifts_date) {
		return shiftsPlanServiceImpl.copyShiftsPlan(shifts_numbers, shifts_dates, shifts_date);
	}

	@RequestMapping("publishShiftsPlan")
	@ResponseBody
	public String publishShiftsPlan(String[] shifts_numbers, String[] shifts_dates) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return shiftsPlanServiceImpl.publishShiftsPlan(shifts_numbers, shifts_dates);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

}
