package com.zah.controller;

import com.zah.service.impl.VehiclePositionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("manage")
public class VehiclePositonController {
	@Autowired
	VehiclePositionImpl vehiclePositionImpl;

	@RequestMapping("vehiclePositionShow")
	public String busVehicleShow(Model model) {
		long nowTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		model.addAttribute("startTime", sdf.format(new Date(nowTime - 86400 * 1000)));
		return vehiclePositionImpl.vehiclePositionShow(model);
	}

	@RequestMapping("showVehiclePosition")
	@ResponseBody
	public String showVehiclePosition(int limit, int page, String vehicle_number, String startTime, String endTime) {
		return vehiclePositionImpl.showVehiclePosition(limit, page, vehicle_number, startTime, endTime);
	}

	@RequestMapping("showMap")
	public String deleteVehiclePosition(Model model, String log_time) {

		return vehiclePositionImpl.showMap(model, log_time);
	}

}
