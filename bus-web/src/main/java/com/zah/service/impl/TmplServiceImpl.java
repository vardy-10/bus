package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.TmplDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TmplServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	TmplDao tmplDao;
	@Autowired
	PublicService publicService;

	// 线路班次展示页 sbus_shifts_tmpl
	public String tmplShow(Model model, String act) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			model.addAttribute("act", act);
			return "bus/busFlightLineShow";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String showTmpl(int limit, int page, String shifts_group, String shifts_number, String line_name,
			String up_origin_name) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				messageMap.put("msg", "无权限");
				messageMap.put("code", 1);
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(shifts_group)) {
				columnMap.put("shifts_group", shifts_group);
			}
			if (!StringUtils.isEmpty(shifts_number)) {

				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			int count = tmplDao.getTmplCount(columnMap);
			List<Map<String, Object>> tmplList = new ArrayList<Map<String, Object>>();

			if (count > 0) {
				tmplList = tmplDao.getTmplList(columnMap, (page - 1) * limit, limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", tmplList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();

	}

	public String tmplAdd(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> lineList = commonDao.query("select line_name,line_id from sbus_line", param);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle ", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("lineList", lineList);
			return "bus/busFlightLineAdd";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String tmplUpdate(Model model, String shifts_number) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (shifts_number == null) {
				model.addAttribute("message", "请选择要编辑的班次");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_number", shifts_number);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			List<Map<String, Object>> lineList = commonDao.query("select line_name,line_id from sbus_line", param);
			List<Map<String, Object>> tmplList = commonDao.query(
					"select s.shifts_number,s.shifts_group,s.line_id,DATE_FORMAT(s.depart_time,\"%H:%i\") as depart_time,DATE_FORMAT(s.arrive_time,\"%H:%i\") as arrive_time,s.vehicle_id,s.driver_id,s.remark,d.driver_id,v.vehicle_id from sbus_shifts_tmpl s left join sbus_driver d on s.driver_id=d.driver_id left join sbus_vehicle v on s.vehicle_id=v.vehicle_id  where shifts_number=#{param.shifts_number}",
					param);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("lineList", lineList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("driver_id", tmplList.get(0).get("driver_id"));
			model.addAttribute("vehicle_id", tmplList.get(0).get("vehicle_id"));
			model.addAttribute("line_id", tmplList.get(0).get("line_id"));
			model.addAttribute("shifts_group", tmplList.get(0).get("shifts_group"));
			model.addAttribute("depart_time", tmplList.get(0).get("depart_time"));
			model.addAttribute("arrive_time", tmplList.get(0).get("arrive_time"));
			model.addAttribute("remark", tmplList.get(0).get("remark"));
			model.addAttribute("shifts_number", shifts_number);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busFlightLineUpdate";
	}

	public String addTmpl(String shifts_number, String shifts_group, String line_id, String depart_time,
			String arrive_time, String vehicle_id, String driver_id, String remark) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_number) || !ValidateUtil.getInstance().isUserName(shifts_number)
					|| shifts_number.length() > 10) {
				map.put("message", "班次不能为空且只能由10位以内的数字字母下划线组成");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_group) || shifts_number.length() > 10) {
				map.put("message", "模板分组不能为空且只能10以内");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择班次线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_time)) {
				map.put("message", "发车时间不能为空");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("HH:mm", depart_time) == null) {
				map.put("message", "发车时间格式错误");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(arrive_time)) {
				map.put("message", "预计到达时间不能为空");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("HH:mm", arrive_time) == null) {
				map.put("message", "预计到达时间格式错误");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			if (StringUtils.isEmpty(vehicle_id)) {
				vehicle_id = "0";
			}
			param.put("vehicle_id", vehicle_id);
			if (StringUtils.isEmpty(driver_id)) {
				driver_id = "0";
			}
			param.put("driver_id", driver_id);
			if (!StringUtils.isEmpty(remark)) {
				if (remark.length() > 50) {
					map.put("message", "备注长度不能超过50位");
					return new JSONObject(map).toString();
				}
			} else {
				remark = "";
			}
			param.put("remark", remark);
			param.put("shifts_number", shifts_number);
			List<Map<String, Object>> isRepeatNumber = commonDao.query(
					"select shifts_number from sbus_shifts_tmpl where shifts_number=#{param.shifts_number}", param);
			if (isRepeatNumber.size() > 0) {
				map.put("message", "班次不能重复");
				return new JSONObject(map).toString();
			}
			param.put("shifts_group", shifts_group);
			param.put("line_id", line_id);
			param.put("depart_time", depart_time);
			param.put("arrive_time", arrive_time);
			List<Map<String, Object>> isRepeat = commonDao.query(
					"select shifts_number from sbus_shifts_tmpl where depart_time=#{param.depart_time} and line_id=#{param.line_id}",
					param);
			if (isRepeat.size() > 0) {
				map.put("message", "选择线路已有重复的发车时间");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> lineList = commonDao
					.query("select line_id from sbus_line where line_id=#{param.line_id}", param);
			if (lineList.size() == 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (!driver_id.equals("0")) {
				List<Map<String, Object>> driverList = commonDao
						.query("select driver_id from sbus_driver where driver_id=#{param.driver_id}", param);
				if (driverList.size() == 0) {
					map.put("message", "司机不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			if (!vehicle_id.equals("0")) {
				List<Map<String, Object>> vehicleList = commonDao
						.query("select vehicle_id from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
				if (vehicleList.size() == 0) {
					map.put("message", "车辆不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			int insert = commonDao.execute(
					"insert into sbus_shifts_tmpl (shifts_number,shifts_group,line_id,depart_time,arrive_time,vehicle_id,driver_id,remark)values(#{param.shifts_number},#{param.shifts_group},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.vehicle_id},#{param.driver_id},#{param.remark})",
					param);
			if (insert == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加排班模板【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteTmpl(String shifts_number) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_number)) {
				map.put("message", "请选择班次");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_number", shifts_number);
			List<Map<String, Object>> tmplList = commonDao.query(
					"select shifts_number from sbus_shifts_tmpl where shifts_number=#{param.shifts_number}", param);
			if (tmplList.size() <= 0) {
				map.put("message", "班次不存在或已删除");
				return new JSONObject(map).toString();
			}
			int delete = commonDao.execute("delete from sbus_shifts_tmpl where shifts_number=#{param.shifts_number}",
					param);
			if (delete <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除排班模板【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String updateTmpl(String shifts_number, String shifts_group, String line_id, String depart_time,
			String arrive_time, String vehicle_id, String driver_id, String remark, String shifts_number_before) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_number) || !ValidateUtil.getInstance().isUserName(shifts_number)
					|| shifts_number.length() > 10) {
				map.put("message", "班次不能为空且只能由10位以内的数字字母下划线组成");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_group) || shifts_number.length() > 10) {
				map.put("message", "模板分组不能为空且只能10以内");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "线路编号不能为空");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_time)) {
				map.put("message", "发车时间不能为空");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("HH:mm", depart_time) == null) {
				map.put("message", "发车时间格式错误");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(arrive_time)) {
				map.put("message", "预计到达时间不能为空");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("HH:mm", arrive_time) == null) {
				map.put("message", "预计到达时间格式错误");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			if (!StringUtils.isEmpty(remark)) {
				if (remark.length() > 50) {
					map.put("message", "备注长度不能超过50");
					return new JSONObject(map).toString();
				}
			} else {
				remark = "";
			}
			param.put("remark", remark);
			if (StringUtils.isEmpty(vehicle_id)) {
				vehicle_id = "0";
			}
			if (StringUtils.isEmpty(driver_id)) {
				driver_id = "0";
			}
			param.put("shifts_number", shifts_number);
			param.put("shifts_group", shifts_group);
			param.put("line_id", line_id);
			param.put("depart_time", depart_time);
			param.put("arrive_time", arrive_time);
			param.put("vehicle_id", vehicle_id);
			param.put("driver_id", driver_id);
			param.put("remark", remark);
			param.put("shifts_number_before", shifts_number_before);
			List<Map<String, Object>> tmplList = commonDao.query(
					"select line_id,DATE_FORMAT(depart_time,\"%H:%i\")as depart_time from sbus_shifts_tmpl where shifts_number=#{param.shifts_number_before}",
					param);
			if (tmplList.size() <= 0) {
				map.put("message", "班次不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (!shifts_number.equals(shifts_number_before)) {
				List<Map<String, Object>> isRepeat = commonDao.query(
						"select shifts_number from sbus_shifts_tmpl where shifts_number=#{param.shifts_number}", param);
				if (isRepeat.size() > 0) {
					map.put("message", "班次不能重复");
					return new JSONObject(map).toString();
				}
			}
			if (!line_id.equals(String.valueOf(tmplList.get(0).get("line_id")))
					|| !depart_time.equals(tmplList.get(0).get("depart_time"))) {
				List<Map<String, Object>> isRepeat = commonDao.query(
						"select shifts_number from sbus_shifts_tmpl where depart_time=#{param.depart_time} and line_id=#{param.line_id}",
						param);
				if (isRepeat.size() > 0) {
					map.put("message", "选择线路已有重复的发车时间");
					return new JSONObject(map).toString();
				}
			}

			List<Map<String, Object>> lineList = commonDao
					.query("select line_id from sbus_line where line_id=#{param.line_id}", param);
			if (lineList.size() == 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}

			if (!driver_id.equals("0")) {
				List<Map<String, Object>> driverList = commonDao
						.query("select driver_id from sbus_driver where driver_id=#{param.driver_id}", param);
				if (driverList.size() == 0) {
					map.put("message", "司机不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			if (!vehicle_id.equals("0")) {
				List<Map<String, Object>> vehicleList = commonDao
						.query("select vehicle_id from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
				if (vehicleList.size() == 0) {
					map.put("message", "车辆不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			int updateTmpl = commonDao.execute(
					"update sbus_shifts_tmpl set shifts_number=#{param.shifts_number},shifts_group=#{param.shifts_group},line_id=#{param.line_id},depart_time=#{param.depart_time},arrive_time=#{param.arrive_time},vehicle_id=#{param.vehicle_id},driver_id=#{param.driver_id},remark=#{param.remark} where shifts_number=#{param.shifts_number_before} ",
					param);
			if (updateTmpl <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 编辑成功
			map.put("message", "操作成功");
			publicService.addLog("編輯排班模板【" + shifts_number_before + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");

		}
		return new JSONObject(map).toString();
	}
}
