package com.zah.controller;

import com.zah.service.impl.OperateReportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage")
public class OperationReportController {
	@Autowired
	OperateReportServiceImpl operateReportServiceImpl;

	@RequestMapping("operateDataShow")
	public String operateDataShow(Model model, String act) {
		return operateReportServiceImpl.operateDataShow(model, act);
	}

	@RequestMapping("showOperateData")
	@ResponseBody
	public String showOperateData(int limit, int page, String startDate, String endDate, String driver_name,
			String line_name, String up_origin_name, String vehicle_number, String shifts_number) {
		return operateReportServiceImpl.showOperateData(limit, page, startDate, endDate, driver_name, line_name,
				up_origin_name, vehicle_number, shifts_number);
	}

	@RequestMapping("exportToExcel")
	@ResponseBody
	public String exportToExcel(String startDate, String endDate, String driver_name, String line_name,
                                String up_origin_name, String vehicle_number, String shifts_number, HttpSession session, String act,
                                HttpServletResponse response) {
		return operateReportServiceImpl.exportToExcel(startDate, endDate, driver_name, line_name, up_origin_name, vehicle_number, shifts_number, session, act, response);
	}

}
