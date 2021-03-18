package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.ShiftsPlanDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ShiftsPlanServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	ShiftsPlanDao shiftsPlanDao;
	@Autowired
	PublicService publicService;

	// 运营计划展示页 sbus_shifts_plan
	public String shiftsPlanShow(Model model, String act) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nextMonday = sdf.format(Function.getInstance().getNextMonday(new Date()));
			model.addAttribute("nextMonday", nextMonday);
			model.addAttribute("act", act);
			return "bus/busOperatePlanShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	public String ShiftsPlanAdd(Model model) {
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
			return "bus/busOperatePlanAdd";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String shiftsPlanUpdate(Model model, String shifts_date, String shifts_number) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(shifts_date)) {
				model.addAttribute("message", "请选择要编辑的排班日期");
				return "admin/error";
			}
			if (StringUtils.isEmpty(shifts_number)) {
				model.addAttribute("message", "请选择要编辑的班次");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_date", shifts_date);
			param.put("shifts_number", shifts_number);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			List<Map<String, Object>> shiftsPlanList = commonDao.query(
					"select shifts_date,shifts_number,DATE_FORMAT(arrive_time,\"%H:%i\") as arrive_time,driver_id,vehicle_id from sbus_shifts_plan where shifts_date=#{param.shifts_date} and shifts_number=#{param.shifts_number}",
					param);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("driver_id", shiftsPlanList.get(0).get("driver_id"));
			model.addAttribute("vehicle_id", shiftsPlanList.get(0).get("vehicle_id"));
			model.addAttribute("arrive_time", shiftsPlanList.get(0).get("arrive_time"));
			model.addAttribute("shifts_date", shiftsPlanList.get(0).get("shifts_date"));
			model.addAttribute("shifts_number", shiftsPlanList.get(0).get("shifts_number"));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busOperatePlanUpdate";
	}

	public String shiftsPlanConfirm(Model model, String[] shifts_dates, String[] shifts_numbers) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			model.addAttribute("shifts_date", shifts_dates);
			model.addAttribute("shifts_number", shifts_numbers);
			return "bus/busPublishConfirm";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	public String showShiftsPlan(int limit, int page, String shifts_date, String startTime, String endTime,
			String driver_name, String line_name, String up_origin_name, String vehicle_number) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				messageMap.put("msg", "无权限");
				messageMap.put("code", 1);
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (StringUtils.isEmpty(shifts_date)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				shifts_date = sdf.format(Function.getInstance().getNextMonday(new Date()));
			}
			columnMap.put("shifts_date", shifts_date);
			if (!StringUtils.isEmpty(startTime)) {
				columnMap.put("startTime", startTime);
			}
			if (!StringUtils.isEmpty(endTime)) {
				columnMap.put("endTime", endTime);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			int count = shiftsPlanDao.getShiftsPlanCount(columnMap);
			List<Map<String, Object>> shiftsPlanList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				shiftsPlanList = shiftsPlanDao.getShiftsPlanList(columnMap, (page - 1) * limit, limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", shiftsPlanList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();
	}

	public String addShiftsAdd(String shifts_date, String shifts_number, String line_id, String depart_time,
			String arrive_time, String driver_id, String vehicle_id) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "排班日期不能为空");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("yyyy-MM-dd", shifts_date) == null) {
				map.put("message", "排班日期格式错误");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_number) || !ValidateUtil.getInstance().isUserName(shifts_number)
					|| shifts_number.length() > 10) {
				map.put("message", "班次不能为空且只能由10位以内的数字字母下划线组成");
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
			param.put("shifts_date", shifts_date);
			param.put("shifts_number", shifts_number);
			param.put("shifts_date", shifts_date);
			param.put("line_id", line_id);
			param.put("depart_time", depart_time);
			param.put("arrive_time", arrive_time);

			List<Map<String, Object>> isRepeat_Index = commonDao.query(
					"select shifts_date from sbus_shifts_plan where depart_time=#{param.depart_time} and line_id=#{param.line_id} and shifts_date=#{param.shifts_date}",
					param);
			if (isRepeat_Index.size() > 0) {
				map.put("message", "选择线路在排班日期已有重复的发车时间");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isRepeat_PK = commonDao.query(
					"select shifts_date from sbus_shifts_plan where shifts_date=#{param.shifts_date}and shifts_number=#{param.shifts_number}",
					param);
			if (isRepeat_PK.size() > 0) {
				map.put("message", "选择排班日期已有重复的班次");
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
					"insert into sbus_shifts_plan(shifts_date,shifts_number,line_id,depart_time,arrive_time,driver_id,vehicle_id)values(#{param.shifts_date},#{param.shifts_number},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.driver_id},#{param.vehicle_id})",
					param);
			if (insert == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加【" + shifts_date + "】的排班计划【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String updateShiftsPlan(String shifts_date, String shifts_number, String driver_id, String vehicle_id,
			String arrive_time) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "请选择要编辑的排班日期");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_number)) {
				map.put("message", "请选择要编辑的班次");
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
			if (StringUtils.isEmpty(vehicle_id)) {
				vehicle_id = "0";
			}
			if (StringUtils.isEmpty(driver_id)) {
				driver_id = "0";
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_number", shifts_number);
			param.put("shifts_date", shifts_date);
			param.put("arrive_time", arrive_time);
			param.put("vehicle_id", vehicle_id);
			param.put("driver_id", driver_id);
			List<Map<String, Object>> shiftsList = commonDao.query(
					"select shifts_date from sbus_shifts_plan where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
					param);
			if (shiftsList.size() <= 0) {
				map.put("message", "排班计划不存在或已删除");
				return new JSONObject(map).toString();
			}
			int updateOpratePlan = commonDao.execute(
					"update sbus_shifts_plan set arrive_time=#{param.arrive_time},driver_id=#{param.driver_id},vehicle_id=#{param.vehicle_id} where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
					param);
			if (updateOpratePlan <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 编辑成功
			map.put("message", "操作成功");
			publicService.addLog("编辑【" + shifts_date + "】的排班计划【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteShiftsPlan(String shifts_number, String shifts_date) {
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
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "请选择排班日期");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_number", shifts_number);
			param.put("shifts_date", shifts_date);
			List<Map<String, Object>> shiftsPlanList = commonDao.query(
					"select shifts_number from sbus_shifts_plan where shifts_number=#{param.shifts_number} and  shifts_date=#{param.shifts_date} ",
					param);
			if (shiftsPlanList.size() <= 0) {
				map.put("message", "排班计划不存在或已删除");
				return new JSONObject(map).toString();
			}
			int delete = commonDao.execute(
					"delete from sbus_shifts_plan where shifts_number=#{param.shifts_number} and  shifts_date=#{param.shifts_date}",
					param);
			if (delete <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除【" + shifts_date + "】的排班计划【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String importTmpl(String[] shifts_numbers, String shifts_date) {

		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (shifts_numbers == null || shifts_numbers.length == 0) {
				map.put("message", "请选择导入班次");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "请选择要导入到的日期");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("yyyy-MM-dd", shifts_date) == null) {
				map.put("message", "导入到的日期格式错误");
				return new JSONObject(map).toString();
			}
			Set<String> shiftsSet = new HashSet<String>();
			for (int i = 0; i < shifts_numbers.length; i++) {
				shiftsSet.add(shifts_numbers[i]);
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_date", shifts_date);
			List<Map<String, Object>> tmplList = new ArrayList<>();
			for (String shifts_number : shiftsSet) {
				param.put("shifts_number", shifts_number);
				List<Map<String, Object>> tmplList1 = commonDao.query(
						"select shifts_number,line_id,depart_time,arrive_time,vehicle_id,driver_id from sbus_shifts_tmpl where shifts_number=#{param.shifts_number}",
						param);
				if (tmplList1.size() <= 0) {
					map.put("message", "班次不存在或已删除");
					return new JSONObject(map).toString();
				}
				List<Map<String, Object>> isRepeat_Plan = commonDao.query(
						"select shifts_number from sbus_shifts_plan where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
						param);
				if (isRepeat_Plan.size() > 0) {
					map.put("message", "【" + shifts_date + "】【" + shifts_number + "】排班计划已存在");
					return new JSONObject(map).toString();
				}
				param.put("line_id", tmplList1.get(0).get("line_id").toString());
				param.put("depart_time", tmplList1.get(0).get("depart_time").toString());
				List<Map<String, Object>> isRepeat_Index = commonDao.query(
						"select shifts_number from sbus_shifts_plan where  shifts_date=#{param.shifts_date} and line_id=#{param.line_id} and depart_time=#{param.depart_time}",
						param);
				if (isRepeat_Index.size() > 0) {
					map.put("message", "【" + shifts_date + "】【" + shifts_number + "】排班计划已存在");
					return new JSONObject(map).toString();
				}
				tmplList.add(tmplList1.get(0));
			}
			for (int i = 0; i < tmplList.size(); i++) {
				param.put("shifts_number", String.valueOf(tmplList.get(i).get("shifts_number")));
				param.put("line_id", String.valueOf(tmplList.get(i).get("line_id")));
				param.put("depart_time", String.valueOf(tmplList.get(i).get("depart_time")));
				param.put("arrive_time", String.valueOf(tmplList.get(i).get("arrive_time")));
				param.put("vehicle_id", String.valueOf(tmplList.get(i).get("vehicle_id")));
				param.put("driver_id", String.valueOf(tmplList.get(i).get("driver_id")));
				int importTmpl = commonDao.execute(
						"insert into sbus_shifts_plan(shifts_date,shifts_number,line_id,depart_time,arrive_time,driver_id,vehicle_id)values(#{param.shifts_date},#{param.shifts_number},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.driver_id},#{param.vehicle_id})",
						param);
				if (importTmpl <= 0) {
					throw new RuntimeException("发生异常！");
				}
				publicService.addLog("导入【" + String.valueOf(tmplList.get(i).get("shifts_number")) + "】班次的排班模板到【"
						+ shifts_date + "】的排班计划中");
			}
			map.put("success", true);
			map.put("message", "操作成功");

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String copyShiftsPlan(String[] shifts_numbers, String[] shifts_dates, String shifts_date) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (shifts_numbers == null || shifts_numbers.length == 0) {
				map.put("message", "请选择拷贝的班次");
				return new JSONObject(map).toString();
			}
			if (shifts_dates == null || shifts_dates.length == 0) {
				map.put("message", "请选择拷贝的排班日期");
				return new JSONObject(map).toString();
			}
			if (shifts_numbers.length != shifts_dates.length) {
				map.put("message", "班次和排班日期不对应");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "请选择要拷贝到的日期");
				return new JSONObject(map).toString();
			}
			if (Function.getInstance().date("yyyy-MM-dd", shifts_date) == null) {
				map.put("message", "拷贝到的日期格式错误");
				return new JSONObject(map).toString();
			}

			Set<Map<String, String>> shiftsSet = new HashSet<>();
			for (int i = 0; i < shifts_dates.length; i++) {
				Map<String, String> map1 = new HashMap<>();
				map1.put("date", shifts_dates[i]);
				map1.put("number", shifts_numbers[i]);
				shiftsSet.add(map1);
			}

			Map<String, String> param = new HashMap<>();
			param.put("shifts_date", shifts_date);
			// 遍历所选排班计划
			List<Map<String, Object>> shiftsPlanList = new ArrayList<>();
			for (Map<String, String> map1 : shiftsSet) {
				param.put("shifts_numbers", map1.get("number"));
				param.put("shifts_dates", map1.get("date"));
				List<Map<String, Object>> shiftsPlanList1 = commonDao.query(
						"select shifts_number,shifts_date,line_id,depart_time,arrive_time,vehicle_id,driver_id from sbus_shifts_plan where shifts_number=#{param.shifts_numbers} and shifts_date=#{param.shifts_dates}",
						param);
				if (shiftsPlanList1.size() <= 0) {
					map.put("message", "排班计划不存在或已删除");
					return new JSONObject(map).toString();
				}
				List<Map<String, Object>> isRepeat = commonDao.query(
						"select shifts_number from sbus_shifts_plan where shifts_number=#{param.shifts_numbers} and shifts_date=#{param.shifts_date} ",
						param);
				if (isRepeat.size() > 0) {
					map.put("message", "【" + shifts_date + "】【" + map1.get("number") + "】排班计划已存在");
					return new JSONObject(map).toString();
				}
				param.put("line_id", shiftsPlanList1.get(0).get("line_id").toString());
				param.put("depart_time", shiftsPlanList1.get(0).get("depart_time").toString());
				List<Map<String, Object>> isRepeat_Index = commonDao.query(
						"select shifts_number from sbus_shifts_plan where  shifts_date=#{param.shifts_date} and line_id=#{param.line_id} and depart_time=#{param.depart_time}",
						param);
				if (isRepeat_Index.size() > 0) {
					map.put("message", "【" + shifts_date + "】【" + map1.get("number") + "】排班计划已存在");
					return new JSONObject(map).toString();
				}
				shiftsPlanList.add(shiftsPlanList1.get(0));
			}
			for (int i = 0; i < shifts_numbers.length; i++) {
				param.put("shiftsNumber", String.valueOf(shiftsPlanList.get(i).get("shifts_number")));
				param.put("line_id", String.valueOf(shiftsPlanList.get(i).get("line_id")));
				param.put("depart_time", String.valueOf(shiftsPlanList.get(i).get("depart_time")));
				param.put("arrive_time", String.valueOf(shiftsPlanList.get(i).get("arrive_time")));
				param.put("vehicle_id", String.valueOf(shiftsPlanList.get(i).get("vehicle_id")));
				param.put("driver_id", String.valueOf(shiftsPlanList.get(i).get("driver_id")));
				int copyPlan = commonDao.execute(
						"insert into sbus_shifts_plan (shifts_date,shifts_number,line_id,depart_time,arrive_time,driver_id,vehicle_id) values (#{param.shifts_date},#{param.shiftsNumber},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.driver_id},#{param.vehicle_id})",
						param);
				if (copyPlan <= 0) {
					map.put("message", "操作失败");
					throw new RuntimeException("发生异常！");
				}
				publicService.addLog("拷贝【" + shiftsPlanList.get(i).get("shifts_date") + "】【"
						+ shiftsPlanList.get(i).get("shifts_number") + "】班次的排班计划到【" + shifts_date + "】的排班计划中");
			}
			map.put("success", true);
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String publishShiftsPlan(String[] shifts_numbers, String[] shifts_dates) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (shifts_numbers == null || shifts_numbers.length == 0) {
				map.put("message", "请选择发布的班次");
				return new JSONObject(map).toString();
			}
			if (shifts_dates == null || shifts_dates.length == 0) {
				map.put("message", "请选择发布的排班日期");
				return new JSONObject(map).toString();
			}
			if (shifts_numbers.length != shifts_dates.length) {
				map.put("message", "班次和排班日期不对应");
				return new JSONObject(map).toString();
			}
			// 去除提交的重复信息
			Set<Map<String, String>> shiftsSet = new HashSet<>();
			for (int i = 0; i < shifts_dates.length; i++) {
				Map<String, String> map1 = new HashMap<>();
				map1.put("date", shifts_dates[i]);
				map1.put("number", shifts_numbers[i]);
				shiftsSet.add(map1);
			}

			// 遍历判断
			Set<Map<String, String>> vehicleSet = new HashSet<>();
			Set<Map<String, String>> driverSet = new HashSet<>();
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> shiftsPlanList = new ArrayList<>();
			for (Map<String, String> map1 : shiftsSet) {
				System.out.println("aaaaaaaaaaaaaaaaaa");
				param.put("shifts_numbers", map1.get("number"));
				param.put("shifts_dates", map1.get("date"));
				// 排班计划必须存在
				List<Map<String, Object>> shiftsPlanList1 = commonDao.query(
						"select shifts_number,shifts_date,line_id,depart_time,arrive_time,vehicle_id,driver_id from sbus_shifts_plan where shifts_number=#{param.shifts_numbers} and shifts_date=#{param.shifts_dates}",
						param);
				if (shiftsPlanList1.size() <= 0) {
					map.put("message", "排班计划不存在或已删除");
					return new JSONObject(map).toString();
				}

				param.put("driver_id", String.valueOf(shiftsPlanList1.get(0).get("driver_id")));
				param.put("vehicle_id", String.valueOf(shiftsPlanList1.get(0).get("vehicle_id")));
				param.put("depart_time", String.valueOf(shiftsPlanList1.get(0).get("depart_time")));
				// 排班计划必须选择司机和车辆
				if (String.valueOf(shiftsPlanList1.get(0).get("driver_id")).equals("0")) {
					map.put("message", "排班计划发布必须选择班次的司机");
					return new JSONObject(map).toString();
				}
				if (String.valueOf(shiftsPlanList1.get(0).get("vehicle_id")).equals("0")) {
					map.put("message", "排班计划发布必须选择班次的车辆");
					return new JSONObject(map).toString();
				}

				Map<String, String> vehicleMap = new HashMap<>();
				vehicleMap.put("shifts_date", map1.get("date"));
				vehicleMap.put("depart_time", String.valueOf(shiftsPlanList1.get(0).get("depart_time")));
				vehicleMap.put("vehicle_id", String.valueOf(shiftsPlanList1.get(0).get("vehicle_id")));
				// 同一时间选择的排班计划中不能存在重复车辆和司机
				if (vehicleSet.contains(vehicleMap)) {
					map.put("message", "【" + map1.get("date") + "】【" + shiftsPlanList1.get(0).get("depart_time")
							+ "】的排班计划中存在重复车辆");
					return new JSONObject(map).toString();
				}
				vehicleSet.add(vehicleMap);
				Map<String, String> driverMap = new HashMap<>();
				driverMap.put("shifts_date", map1.get("date"));
				driverMap.put("depart_time", String.valueOf(shiftsPlanList1.get(0).get("depart_time")));
				driverMap.put("driver_id", String.valueOf(shiftsPlanList1.get(0).get("driver_id")));
				if (driverSet.contains(driverMap)) {
					map.put("message", "【" + map1.get("date") + "】【" + shiftsPlanList1.get(0).get("depart_time")
							+ "】的排班计划中存在重复司机");
					return new JSONObject(map).toString();
				}
				driverSet.add(driverMap);
				// 同一时间选择的排班计划中不能和选择的排班日程中存在重复车辆和司机
				List<Map<String, Object>> isDriverRepeat = commonDao.query(
						"select d.driver_name from sbus_shifts_real r left join sbus_driver d on d.driver_id=r.driver_id where r.shifts_date=#{param.shifts_dates} and r.depart_time=#{param.depart_time} and r.driver_id=#{param.driver_id} limit 1",
						param);
				if (isDriverRepeat.size() > 0) {
					map.put("message", "【" + map1.get("date") + "】【" + shiftsPlanList1.get(0).get("depart_time")
							+ "】的排班日程中【" + isDriverRepeat.get(0).get("driver_name") + "】司机已存在");
					return new JSONObject(map).toString();
				}
				List<Map<String, Object>> isVehicleRepeat = commonDao.query(
						"select v.vehicle_number from sbus_shifts_real r left join sbus_vehicle v on v.vehicle_id=r.vehicle_id where r.shifts_date=#{param.shifts_dates} and r.depart_time=#{param.depart_time} and r.vehicle_id=#{param.vehicle_id} limit 1",
						param);
				if (isVehicleRepeat.size() > 0) {
					map.put("message", "【" + map1.get("date") + "】【" + shiftsPlanList1.get(0).get("depart_time")
							+ "】的排班日程中【" + isVehicleRepeat.get(0).get("vehicle_number") + "】车辆已存在");
					return new JSONObject(map).toString();
				}
				param.put("line_id", String.valueOf(shiftsPlanList1.get(0).get("line_id")));
				param.put("depart_time", String.valueOf(shiftsPlanList1.get(0).get("depart_time")));
				List<Map<String, Object>> isShiftsRealExit = commonDao.query(
						"select shifts_number from sbus_shifts_real where shifts_number=#{param.shifts_numbers} and shifts_date=#{param.shifts_dates}",
						param);
				if (isShiftsRealExit.size() > 0) {
					map.put("message", "【" + map1.get("date") + "】【" + map1.get("number") + "】排班日程已存在");
					return new JSONObject(map).toString();
				}
				List<Map<String, Object>> isRepeat_Index = commonDao.query(
						"select shifts_number from sbus_shifts_real where shifts_date=#{param.shifts_dates} and line_id=#{param.line_id} and depart_time=#{param.depart_time}",
						param);
				if (isRepeat_Index.size() > 0) {
					map.put("message", "【" + map1.get("date") + "】【" + map1.get("number") + "】排班日程已存在");
					return new JSONObject(map).toString();
				}
				shiftsPlanList.add(shiftsPlanList1.get(0));
			}
			// 遍历发布
			for (int i = 0; i < shifts_numbers.length; i++) {
				param.put("shifts_date", String.valueOf(shiftsPlanList.get(i).get("shifts_date")));
				param.put("shifts_number", String.valueOf(shiftsPlanList.get(i).get("shifts_number")));
				param.put("line_id", String.valueOf(shiftsPlanList.get(i).get("line_id")));
				param.put("depart_time", String.valueOf(shiftsPlanList.get(i).get("depart_time")));
				param.put("arrive_time", String.valueOf(shiftsPlanList.get(i).get("arrive_time")));
				param.put("vehicle_id", String.valueOf(shiftsPlanList.get(i).get("vehicle_id")));
				param.put("driver_id", String.valueOf(shiftsPlanList.get(i).get("driver_id")));
				int publishShiftsPlan = commonDao.execute(
						"insert into sbus_shifts_real (shifts_date,shifts_number,line_id,depart_time,arrive_time,driver_id,vehicle_id) values (#{param.shifts_date},#{param.shifts_number},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.driver_id},#{param.vehicle_id})",
						param);
				if (publishShiftsPlan <= 0) {
					throw new RuntimeException("发生异常！");
				}
				publicService.addLog("发布【" + shiftsPlanList.get(i).get("shifts_dates") + "】【"
						+ shiftsPlanList.get(i).get("shifts_numbers") + "】班次的排班计划到排班日程中");
			}
			map.put("success", true);
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}
}
