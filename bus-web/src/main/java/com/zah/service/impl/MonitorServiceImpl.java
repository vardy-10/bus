package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.OperatePlanMonitorDao;
import com.zah.dao.ShiftsPlanDao;
import com.zah.service.PublicService;
import com.zah.thread.BusPlaceAndShifts;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MonitorServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	OperatePlanMonitorDao operatePlanMonitorDao;
	@Autowired
	ShiftsPlanDao shiftsPlanDao;
	@Autowired
	PublicService publicService;

	// 班车监控页加载
	public String flightLineMonitor(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<String, String>();
			List<Map<String, Object>> lineList = commonDao.query("select line_id,line_name from sbus_line", param);
			model.addAttribute("lineList", lineList);
			return "bus/busFlightLineMonitor";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 运营监控页加载
	public String operatePlanMonitorShow(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> lineList = commonDao.query("select line_name,line_id from sbus_line", param);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(date);
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String nowTime = sdf.format(date);
			model.addAttribute("nowTime", nowTime);
			model.addAttribute("nowDate", nowDate);
			model.addAttribute("lineList", lineList);
			return "bus/busOperatePlanMonitorShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 运营调度页加载
	public String operatePlanDispatchShow(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(new Date());
			model.addAttribute("nowDate", nowDate);
			return "bus/busOperatePlanDispatchShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 运营调度添加页加载
	public String operatePlanDispatchAdd(Model model) {
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
			return "bus/busOperatePlanDispatchAdd";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 运营调度更新页加载
	public String operatePlanDispatchUpdate(Model model, String shifts_date, String shifts_number) {
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
			List<Map<String, Object>> shiftsReal = commonDao.query(
					"select shifts_number,line_id,driver_id,vehicle_id,DATE_FORMAT(depart_time,\"%H:%i\") as depart_time,DATE_FORMAT(arrive_time,\"%H:%i\") as arrive_time from sbus_shifts_real where shifts_date=#{param.shifts_date} and shifts_number=#{param.shifts_number}",
					param);
			if (shiftsReal.size() <= 0) {
				model.addAttribute("message", "排班日程不存在或已删除");
				return "admin/error";
			}
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("shifts_date", shifts_date);
			model.addAttribute("shifts_number", shifts_number);
			model.addAttribute("driver_id", shiftsReal.get(0).get("driver_id"));
			model.addAttribute("vehicle_id", shiftsReal.get(0).get("vehicle_id"));
			model.addAttribute("arrive_time", shiftsReal.get(0).get("arrive_time"));
			model.addAttribute("depart_time", shiftsReal.get(0).get("depart_time"));
			return "bus/busOperatePlanDispatchUpdate";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 班车监控-线路信息查询
	public String getlinePoint(String line_id) {
		int vehicleRefreshTime = 5000;// 车辆刷新时间5000毫秒
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择线路");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			List<Map<String, Object>> linesPoint = commonDao
					.query("select up_lines_point from sbus_line where line_id=#{param.line_id}", param);
			if (linesPoint.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			map.put("up_lines_point", linesPoint.get(0).get("up_lines_point"));
			map.put("vehicleRefreshTime", vehicleRefreshTime);
			map.put("success", true);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 班车监控-获取动态车辆位置信息
	public String getLngLat(String line_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择线路");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			long time = System.currentTimeMillis() / 1000 - 3 * 60;
			param.put("time", String.valueOf(time));
			List<Map<String, Object>> vehicleLngLat = commonDao.query(
					"select vehicle_number,longitude,latitude,(CASE WHEN log_time<#{param.time} then 1 when log_time>#{param.time} then 0 END)as is_online from sbus_vehicle where line_id=#{param.line_id}",
					param);
			map.put("vehicleLngLat", vehicleLngLat);
			map.put("success", true);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 运营监控-数据查询
	public String showOperatePlanMonitor(String shifts_date, String shifts_number, String line_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (StringUtils.isEmpty(shifts_date)) {
				map.put("message", "请选择排班日期");
				return new JSONObject(map).toString();
			}
			columnMap.put("shifts_date", shifts_date);
			if (StringUtils.isEmpty(shifts_number) && StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班班次或线路");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(line_id)) {
				columnMap.put("line_id", line_id);
			}

			List<Map<String, Object>> operatePlan = operatePlanMonitorDao.getOperatePlanList(columnMap);
			List<Map<String, Object>> operateReal = operatePlanMonitorDao.getOperateRealList(columnMap);
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> configList = commonDao
					.query("select depart_delay_minute,vehicle_wait_minute from sbus_config", param);
			if (configList.size() <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowTime = sdf.format(new Date());
			map.put("nowTime", nowTime);
			map.put("dataPlan", operatePlan);
			map.put("dataReal", operateReal);
			map.put("configMap", configList.get(0));
			map.put("success", true);
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 运营调度-数据查询
	public String showOperatePlanDispatch(int limit, int page, String shifts_date, String startTime, String endTime,
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
				shifts_date = sdf.format(new Date());
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
			int count = operatePlanMonitorDao.getOperateRealCount(columnMap);
			List<Map<String, Object>> operatePlanDispatchList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				operatePlanDispatchList = operatePlanMonitorDao.getOperateRealListByLimit(columnMap, (page - 1) * limit,
						limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", operatePlanDispatchList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();
	}

	// 添加运营调度
	public String addOperatePlanDispatch(String shifts_date, String shifts_number, String line_id, String depart_time,
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
			if (StringUtils.isEmpty(driver_id) || driver_id.equals("0")) {
				map.put("message", "请选择司机");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				map.put("message", "请选择车辆");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("vehicle_id", vehicle_id);
			param.put("driver_id", driver_id);
			param.put("shifts_date", shifts_date);
			param.put("shifts_number", shifts_number);
			param.put("shifts_date", shifts_date);
			param.put("line_id", line_id);
			param.put("depart_time", depart_time);
			param.put("arrive_time", arrive_time);
			List<Map<String, Object>> isRepeat_Index = commonDao.query(
					"select shifts_date from sbus_shifts_real where depart_time=#{param.depart_time} and line_id=#{param.line_id} and shifts_date=#{param.shifts_date}",
					param);
			if (isRepeat_Index.size() > 0) {
				map.put("message", "选择线路在排班日期已有重复的发车时间");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isRepeat_PK = commonDao.query(
					"select shifts_date from sbus_shifts_real where shifts_date=#{param.shifts_date}and shifts_number=#{param.shifts_number}",
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
			List<Map<String, Object>> driverList = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.driver_id}", param);
			if (driverList.size() == 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
			if (vehicleList.size() == 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isDriverRepeat = commonDao.query(
					"select shifts_date from sbus_shifts_real  where shifts_date=#{param.shifts_date} and depart_time=#{param.depart_time} and driver_id=#{param.driver_id} limit 1",
					param);
			if (isDriverRepeat.size() > 0) {
				map.put("message", "【" + shifts_date + "】【" + depart_time + "】的排班日程中【"
						+ driverList.get(0).get("driver_name") + "】司机已存在");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isVehicleRepeat = commonDao.query(
					"select shifts_date from sbus_shifts_real where shifts_date=#{param.shifts_date} and depart_time=#{param.depart_time} and vehicle_id=#{param.vehicle_id} limit 1",
					param);
			if (isVehicleRepeat.size() > 0) {
				map.put("message", "【" + shifts_date + "】【" + depart_time + "】的排班日程中【"
						+ vehicleList.get(0).get("vehicle_number") + "】车辆已存在");
				return new JSONObject(map).toString();
			}
			int insert = commonDao.execute(
					"insert into sbus_shifts_real(shifts_date,shifts_number,line_id,depart_time,arrive_time,driver_id,vehicle_id)values(#{param.shifts_date},#{param.shifts_number},#{param.line_id},#{param.depart_time},#{param.arrive_time},#{param.driver_id},#{param.vehicle_id})",
					param);
			if (insert == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加【" + shifts_date + "】的排班日程【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 更新运营调度
	public String updateOperatePlanDispatch(String shifts_date, String shifts_number, String arrive_time,
			String driver_id, String vehicle_id, String depart_time) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				messageMap.put("message", "无权限");
				return new JSONObject(messageMap).toString();
			}
			if (StringUtils.isEmpty(shifts_date)) {
				messageMap.put("message", "请选择要编辑的排班日期");
				return new JSONObject(messageMap).toString();
			}

			if (StringUtils.isEmpty(shifts_number)) {
				messageMap.put("message", "请选择要编辑的班次");
				return new JSONObject(messageMap).toString();
			}
			if (StringUtils.isEmpty(arrive_time)) {
				messageMap.put("message", "预计到达时间不能为空");
				return new JSONObject(messageMap).toString();
			}
			if (Function.getInstance().date("HH:mm", arrive_time) == null) {
				messageMap.put("message", "预计到达时间格式错误");
				return new JSONObject(messageMap).toString();
			}
			if (StringUtils.isEmpty(depart_time)) {
				messageMap.put("message", "发车时间不能为空");
				return new JSONObject(messageMap).toString();
			}
			if (Function.getInstance().date("HH:mm", depart_time) == null) {
				messageMap.put("message", "发车时间格式错误");
				return new JSONObject(messageMap).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				messageMap.put("message", "请选择车辆");
				return new JSONObject(messageMap).toString();
			}
			if (StringUtils.isEmpty(driver_id) || driver_id.equals("0")) {
				messageMap.put("message", "请选择司机");
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("shifts_number", shifts_number);
			param.put("shifts_date", shifts_date);
			param.put("arrive_time", arrive_time);
			param.put("vehicle_id", vehicle_id);
			param.put("driver_id", driver_id);
			param.put("depart_time", depart_time);

			List<Map<String, Object>> shiftsReal = commonDao.query(
					"select line_id,DATE_FORMAT(depart_time,\"%H:%i\") as depart_time,student_num,teacher_num,vehicle_id,driver_id from sbus_shifts_real where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
					param);
			if (shiftsReal.size() <= 0) {
				messageMap.put("message", "排班日程不存在或已删除");
				return new JSONObject(messageMap).toString();
			}

			param.put("line_id", String.valueOf(shiftsReal.get(0).get("line_id")));
			if (!String.valueOf(shiftsReal.get(0).get("depart_time")).equals(depart_time)) {
				List<Map<String, Object>> isRepeat_Index = commonDao.query(
						"select shifts_date from sbus_shifts_real where depart_time=#{param.depart_time} and line_id=#{param.line_id} and shifts_date=#{param.shifts_date}",
						param);
				if (isRepeat_Index.size() > 0) {
					messageMap.put("message", "选择线路在排班日期已有重复的发车时间");
					return new JSONObject(messageMap).toString();
				}
			}

			if (!String.valueOf(shiftsReal.get(0).get("driver_id")).equals(driver_id)) {
				List<Map<String, Object>> driverList = commonDao
						.query("select driver_name from sbus_driver where driver_id=#{param.driver_id}", param);
				if (driverList.size() == 0) {
					messageMap.put("message", "司机不存在或已删除");
					return new JSONObject(messageMap).toString();
				}
				List<Map<String, Object>> isDriverRepeat = commonDao.query(
						"select shifts_date from sbus_shifts_real  where shifts_date=#{param.shifts_date} and depart_time=#{param.depart_time} and driver_id=#{param.driver_id} limit 1",
						param);
				if (isDriverRepeat.size() > 0) {
					messageMap.put("message", "【" + shifts_date + "】【" + depart_time + "】的排班日程中【"
							+ driverList.get(0).get("driver_name") + "】司机已存在");
					return new JSONObject(messageMap).toString();
				}
			}

			if (!String.valueOf(shiftsReal.get(0).get("vehicle_id")).equals(vehicle_id)) {

				List<Map<String, Object>> vehicleList = commonDao
						.query("select seat_num from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
				if (vehicleList.size() <= 0) {
					messageMap.put("message", "车辆不存在或已删除");
					return new JSONObject(messageMap).toString();
				}
				int total_num = ((int) shiftsReal.get(0).get("student_num"))
						+ ((int) shiftsReal.get(0).get("teacher_num"));
				if (total_num > (int) vehicleList.get(0).get("seat_num")) {
					messageMap.put("message", "编辑的车辆座位数小于该排班日程中已出售的票数");
					return new JSONObject(messageMap).toString();
				}
				List<Map<String, Object>> isVehicleRepeat = commonDao.query(
						"select shifts_date from sbus_shifts_real where shifts_date=#{param.shifts_date} and depart_time=#{param.depart_time} and vehicle_id=#{param.vehicle_id} limit 1",
						param);
				if (isVehicleRepeat.size() > 0) {
					messageMap.put("message", "【" + shifts_date + "】【" + depart_time + "】的排班日程中【"
							+ vehicleList.get(0).get("vehicle_number") + "】车辆已存在");
					return new JSONObject(messageMap).toString();
				}

			}
			int updateShiftsReal;
			if (!String.valueOf(shiftsReal.get(0).get("depart_time")).equals(depart_time)
					|| !String.valueOf(shiftsReal.get(0).get("vehicle_id")).equals(vehicle_id)) {
				updateShiftsReal = commonDao.execute(
						"update sbus_shifts_real set depart_time=#{param.depart_time},arrive_time=#{param.arrive_time},driver_id=#{param.driver_id},vehicle_id=#{param.vehicle_id},in_origin_time=0,out_origin_time=0 where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
						param);
				BusPlaceAndShifts.updatedShiftsLineList.add((String.valueOf(shiftsReal.get(0).get("line_id"))));
			} else {
				updateShiftsReal = commonDao.execute(
						"update sbus_shifts_real set depart_time=#{param.depart_time},arrive_time=#{param.arrive_time},driver_id=#{param.driver_id},vehicle_id=#{param.vehicle_id} where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}",
						param);
			}
			if (updateShiftsReal <= 0) {
				messageMap.put("message", "操作失败");
				return new JSONObject(messageMap).toString();
			}
			messageMap.put("success", true); // 编辑成功
			messageMap.put("message", "操作成功");
			publicService.addLog("编辑【" + shifts_date + "】的排班日程【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("message", "系统错误");
		}
		return new JSONObject(messageMap).toString();
	}

	// 删除运营调度
	public String deleteOperatePlanDispatch(String shifts_number, String shifts_date) {
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
			List<Map<String, Object>> shiftsRealList = commonDao.query(
					"select total_fare from sbus_shifts_real where shifts_number=#{param.shifts_number} and  shifts_date=#{param.shifts_date} ",
					param);
			if (shiftsRealList.size() <= 0) {
				map.put("message", "排班日程不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (((BigDecimal) shiftsRealList.get(0).get("total_fare")).doubleValue() != 0) {
				map.put("message", "该班次已售票");
				return new JSONObject(map).toString();
			}

			int delete = commonDao.execute(
					"delete from sbus_shifts_real where shifts_number=#{param.shifts_number} and  shifts_date=#{param.shifts_date}",
					param);
			if (delete <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}

			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除【" + shifts_date + "】的排班日程【" + shifts_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

}
