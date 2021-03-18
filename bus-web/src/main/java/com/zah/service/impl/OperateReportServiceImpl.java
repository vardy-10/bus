package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.OperateDataDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.ExcelUtil;
import com.zah.util.Function;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OperateReportServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	OperateDataDao operateDataDao;

	public String operateDataShow(Model model, String act) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (!"data".equals(act) && !"statistics".equals(act)) {
				model.addAttribute("message", "操作异常");
				return "admin/error";
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String startDate = sdf.format(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			String endDate = sdf2.format(date);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			model.addAttribute("act", act);
			return "bus/busOperateDataShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String showOperateData(int limit, int page, String startDate, String endDate, String driver_name,
			String line_name, String up_origin_name, String vehicle_number, String shifts_number) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				messageMap.put("msg", "无权限");
				messageMap.put("code", 1);
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(startDate)
					&& Function.getInstance().date("yyyy-MM-dd HH:mm:ss", startDate) != null) {
				String[] startDateArr = startDate.split(" ");
				columnMap.put("start_date", startDateArr[0]);
				columnMap.put("start_time", startDateArr[1]);
			}
			if (!StringUtils.isEmpty(endDate) && Function.getInstance().date("yyyy-MM-dd HH:mm:ss", endDate) != null) {
				String[] endDateArr = endDate.split(" ");
				columnMap.put("end_date", endDateArr[0]);
				columnMap.put("end_time", endDateArr[1]);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			int count = operateDataDao.getOperateDataCount(columnMap);
			List<Map<String, Object>> operateDataList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				operateDataList = operateDataDao.getOperateDataListByLimit(columnMap, (page - 1) * limit, limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", operateDataList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();
	}

	public String exportToExcel(String startDate, String endDate, String driver_name, String line_name,
                                String up_origin_name, String vehicle_number, String shifts_number, HttpSession session, String act,
                                HttpServletResponse response) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				messageMap.put("message", "无权限");
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(startDate)
					&& Function.getInstance().date("yyyy-MM-dd HH:mm:ss", startDate) != null) {
				String[] startDateArr = startDate.split(" ");
				columnMap.put("start_date", startDateArr[0]);
				columnMap.put("start_time", startDateArr[1]);
			}
			if (!StringUtils.isEmpty(endDate) && Function.getInstance().date("yyyy-MM-dd HH:mm:ss", endDate) != null) {
				String[] endDateArr = endDate.split(" ");
				columnMap.put("end_date", endDateArr[0]);
				columnMap.put("end_time", endDateArr[1]);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			List<Map<String, Object>> operateDataList = operateDataDao.getOperateDataList(columnMap);
			List<int[]> mergeList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
			String time = sdf.format(new Date());
			if ("data".equals(act)) {
				String[] strExcTitle = { "日  期", "班  次", "线  路", "发车时间", "始发站", "预计到达时间", "终点站", "司  机", "车  辆",
						"路  程（km）" };
				String[] strExcCols = { "shifts_date", "shifts_number", "line_name", "depart_time", "up_origin_name",
						"arrive_time", "up_terminal_name", "driver_name", "vehicle_number", "up_total_distance" };
				int[] colsWidth = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
				SXSSFWorkbook wb = ExcelUtil.generateExcel("sheet1", strExcTitle, strExcCols, colsWidth,
						operateDataList, mergeList, false);
				String fileName = time + ".xlsx"; // 格式：登录者帐号_年月日时分秒
				ExcelUtil.excelDownload(getResponse(response), fileName, wb);
			} else if ("statistics".equals(act)) {
				String[] strExcTitle = { "日  期", "班  次", "线  路", "发车时间", "始发站", "预计到达时间", "终点站", "司  机", "车  辆", "教工数量",
						"学生数量", "上座率" };
				String[] strExcCols = { "shifts_date", "shifts_number", "line_name", "depart_time", "up_origin_name",
						"arrive_time", "up_terminal_name", "driver_name", "vehicle_number", "teacher_num",
						"student_num", "seat_rate" };
				int[] colsWidth = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
				SXSSFWorkbook wb = ExcelUtil.generateExcel("sheet1", strExcTitle, strExcCols, colsWidth,
						operateDataList, mergeList, false);
				String fileName = time + ".xlsx"; // 格式：登录者帐号_年月日时分秒
				ExcelUtil.excelDownload(getResponse(response), fileName, wb);
			} else {
				messageMap.put("message", "操作异常");
				return new JSONObject(messageMap).toString();
			}
			messageMap.put("success", true);
			messageMap.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("message", "系统错误");
		}
		return new JSONObject(messageMap).toString();
	}

	public HttpServletResponse getResponse(HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		return response;
	}
}
