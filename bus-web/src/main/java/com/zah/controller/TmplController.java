package com.zah.controller;

import com.zah.service.impl.TmplServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("manage")
public class TmplController {
	@Autowired
	TmplServiceImpl tmplServiceImpl;

	
	
	
	@RequestMapping("tmplShow")
	public String tmplShow(Model model, String act) {
		return tmplServiceImpl.tmplShow(model, act);
	}

	@RequestMapping("tmplUpdate")
	public String tmplUpdate(Model model, String shifts_number) {
		return tmplServiceImpl.tmplUpdate(model, shifts_number);
	}

	@RequestMapping("tmplAdd")
	public String tmplAdd(Model model) {
		return tmplServiceImpl.tmplAdd(model);
	}

	@RequestMapping("showTmpl")
	@ResponseBody
	public String showTmpl(int limit, int page, String shifts_group, String shifts_number, String line_name,
			String up_origin_name) {
		return tmplServiceImpl.showTmpl(limit, page, shifts_group, shifts_number, line_name, up_origin_name);
	}

	@RequestMapping("addTmpl")
	@ResponseBody
	public String addTmpl(String shifts_number, String shifts_group, String line_id, String depart_time,
			String arrive_time, String vehicle_id, String driver_id, String remark) {
		return tmplServiceImpl.addTmpl(shifts_number, shifts_group, line_id, depart_time, arrive_time, vehicle_id,
				driver_id, remark);
	}

	@RequestMapping("deleteTmpl")
	@ResponseBody
	public String deleteTmpl(String shifts_number) {
		return tmplServiceImpl.deleteTmpl(shifts_number);
	}

	@RequestMapping("updateTmpl")
	@ResponseBody
	public String updateTmpl(String shifts_number, String shifts_group, String line_id, String depart_time,
			String arrive_time, String vehicle_id, String driver_id, String remark, String shifts_number_before) {
		return tmplServiceImpl.updateTmpl(shifts_number, shifts_group, line_id, depart_time, arrive_time, vehicle_id,
				driver_id, remark, shifts_number_before);
	}

}
