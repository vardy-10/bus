package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.BusVehicleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("manage")
public class BusVehicleController {
	@Autowired
	BusVehicleServiceImpl busVehicleServiceImpl;

	@RequestMapping("busVehicleShow")
	public String busVehicleShow(Model model, String line_id) {

		return busVehicleServiceImpl.busVehicleShow(model, line_id);
	}

	@RequestMapping("busVehicleShowOne")
	public String busVehicleShowOne(Model model, String vehicle_id) {

		return busVehicleServiceImpl.busVehicleShowOne(model, vehicle_id);
	}

	@RequestMapping("busVehicleUpdate")
	public String busVehicleUpdate(Model model, String vehicle_id) {

		return busVehicleServiceImpl.busVehicleUpdate(model, vehicle_id);
	}

	@RequestMapping("busVehicleAdd")

	public String busVehicleAdd(Model model) {

		return busVehicleServiceImpl.busVehicleAdd(model);
	}

	@RequestMapping("showBusVehicle")
	@ResponseBody
	public String showBusVehicle(int limit, int page, String vehicle_number, String license_plate, String line_id) {

		return busVehicleServiceImpl.showBusVehicleList(limit, page, vehicle_number, license_plate, line_id);

	}

	@RequestMapping("addBusVehicle")
	@ResponseBody
	public String addBusVehicle(String line_id, String vehicle_number, String license_plate, String seat_num,
			String device1_number, String device2_number) {
		return busVehicleServiceImpl.addBusVehicleList(line_id, vehicle_number, license_plate, seat_num, device1_number,
				device2_number);

	}

	@RequestMapping("deleteBusVehicle")
	@ResponseBody
	public String deleteBusVehicle(String vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return busVehicleServiceImpl.deleteBusVehicleList(vehicle_id);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	@RequestMapping("updateBusVehicle")
	@ResponseBody
	public String updateBusVehicle(String vehicle_id, String line_id, String vehicle_number, String license_plate,
			String seat_num, String device1_number, String device2_number) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Long timeStart = System.currentTimeMillis();
			synchronized (MonitorController.SHIFTSLOCK) {
				if (System.currentTimeMillis() - timeStart > 10000) {
					map.put("message", "操作失效");
				}
				return busVehicleServiceImpl.updateBusVehicle(vehicle_id, line_id, vehicle_number, license_plate,
						seat_num, device1_number, device2_number);
			}
		} catch (Exception e) {
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

}
