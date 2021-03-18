package com.zah.controller;

import com.zah.service.impl.DepartPlanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/manage")
@Controller
public class DepartPlanController {
	@Autowired
	DepartPlanServiceImpl departPlanServiceImpl;

	@RequestMapping("departPlanShow")
	public String departPlanShow(Model model, String line_id) {

		return departPlanServiceImpl.departPlanShow(model, line_id);

	}

	@RequestMapping("departPlanAdd")
	public String departPlanAdd(Model model, String line_id, String depart_week, String depart_dir, String time) {
		return departPlanServiceImpl.departPlanAdd(model, line_id, depart_week, depart_dir, time);

	}

	@RequestMapping("departPlanUpdate")
	public String departPlanUpdate(Model model, String line_id, String depart_dir, String depart_week,
                                   String depart_time, String vehicle_number, String driver_name, String original_vehicle_id,
                                   String original_driver_id) {
		return departPlanServiceImpl.departPlanUpdate(model, line_id, depart_dir, depart_week, depart_time,
				original_vehicle_id, original_driver_id);
	}

	@RequestMapping("showDepartPlan")
	@ResponseBody
	public String showDepartPlan(String line_id, Model model) {
		return departPlanServiceImpl.showDepartPlan(model, line_id);

	}

	@RequestMapping("deleteDepartPlan")
	@ResponseBody
	public String deleteDepartPlan(String line_id, String depart_dir, String vehicle_id, String time,
			String depart_week) {
		return departPlanServiceImpl.deleteDepartPlan(time, vehicle_id, line_id, depart_dir, depart_week);
	}

	@RequestMapping("addDepartPlan")
	@ResponseBody
	public String addDepartPlan(Model model, String time, String line_id, String vehicle_id, String driver_id,
                                String depart_dir, String depart_week) {
		return departPlanServiceImpl.addDepartPlan(model, time, line_id, vehicle_id, driver_id, depart_dir,
				depart_week);
	}

	@RequestMapping("updateDepartPlan")
	@ResponseBody
	public String updateDepartPlan(String line_id, Model model, String depart_week, String depart_time,
                                   String vehicle_id, String driver_id, String depart_dir, String seat_num, String original_vehicle_id) {
		return departPlanServiceImpl.updateDepartPlan(model, line_id, depart_time, depart_week, vehicle_id, driver_id, depart_dir, original_vehicle_id);
	}

}
