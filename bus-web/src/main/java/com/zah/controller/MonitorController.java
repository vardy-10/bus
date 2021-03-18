package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.MonitorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage")
public class MonitorController {
	@Autowired
	MonitorServiceImpl monitorServiceImpl;
	public static final String SHIFTSLOCK = "";

	@RequestMapping("flightLineMonitor")
	public String flightLineMonitor(Model model) {
		return monitorServiceImpl.flightLineMonitor(model);
	}

	@RequestMapping("operatePlanDispatchAdd")
	public String operatePlanDispatchAdd(Model model) {
		return monitorServiceImpl.operatePlanDispatchAdd(model);
	}

	@RequestMapping("operatePlanMonitorShow")
	public String operatePlanMonitorShow(Model model) {
		return monitorServiceImpl.operatePlanMonitorShow(model);
	}

	@RequestMapping("operatePlanDispatchShow")
	public String operatePlanDispatchShow(Model model) {
		return monitorServiceImpl.operatePlanDispatchShow(model);
	}

	@RequestMapping("operatePlanDispatchUpdate")
	public String operatePlanDispatchUpdate(Model model, String shifts_date, String shifts_number) {
		return monitorServiceImpl.operatePlanDispatchUpdate(model, shifts_date, shifts_number);
	}

	@RequestMapping("getlinePoint")
	@ResponseBody
	public String getlinePoint(String line_id) {
		return monitorServiceImpl.getlinePoint(line_id);
	}

	@RequestMapping("getLngLat")
	@ResponseBody
	public String getLngLat(String line_id) {
		return monitorServiceImpl.getLngLat(line_id);
	}

	@RequestMapping("showOperatePlanMonitor")
	@ResponseBody
	public String showOperatePlanMonitor(String shifts_date, String shifts_number, String driver_name, String line_id,
			String up_origin_name, String vehicle_number) {
		return monitorServiceImpl.showOperatePlanMonitor(shifts_date, shifts_number, line_id);
	}

	@RequestMapping("showOperatePlanDispatch")
	@ResponseBody
	public String showOperatePlanDispatch(int limit, int page, String shifts_date, String startTime, String endTime,
			String driver_name, String line_name, String up_origin_name, String vehicle_number) {
		return monitorServiceImpl.showOperatePlanDispatch(limit, page, shifts_date, startTime, endTime, driver_name,
				line_name, up_origin_name, vehicle_number);
	}

	@RequestMapping("updateOperatePlanDispatch")
	@ResponseBody
	public String updateOperatePlanDispatch(String shifts_date, String shifts_number, String arrive_time,
			String driver_id, String vehicle_id,String depart_time) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return monitorServiceImpl.updateOperatePlanDispatch(shifts_date, shifts_number, arrive_time, driver_id,
						vehicle_id,depart_time);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("addOperatePlanDispatch")
	@ResponseBody
	public String addOperatePlanDispatch(String shifts_date, String shifts_number, String line_id, String depart_time,
			String arrive_time, String driver_id, String vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return monitorServiceImpl.addOperatePlanDispatch(shifts_date, shifts_number, line_id, depart_time,
						arrive_time, driver_id, vehicle_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@RequestMapping("deleteOperatePlanDispatch")
	@ResponseBody
	public String deleteOperatePlanDispatch(String shifts_number, String shifts_date) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
					return new JSONObject(map).toString();
				}
				return monitorServiceImpl.deleteOperatePlanDispatch(shifts_number, shifts_date);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

}
