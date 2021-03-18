package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartPlanServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	PublicService publicService;

	public String departPlanShow(Model model, String line_id) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(line_id)) {
				model.addAttribute("message", "请选择线路");
				return "admin/error";

			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			List<Map<String, Object>> list = commonDao
					.query("select line_name from sbus_line where line_id=#{param.line_id}", param);
			if (list.size() <= 0) {
				model.addAttribute("message", "线路不存在或已删除");
				return "admin/error";
			}
			model.addAttribute("line_id", line_id);
			model.addAttribute("line_name", list.get(0).get("line_name"));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busPlanShow";
	}

	public String departPlanAdd(Model model, String line_id, String depart_week, String depart_dir, String time) {

		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(line_id)) {
				model.addAttribute("message", "请选择排班线路");
				return "admin/error";
			}
			if (!StringUtils.isEmpty(depart_dir)) {
				model.addAttribute("depart_dir", depart_dir);
			}
			if (!StringUtils.isEmpty(depart_week)) {
				model.addAttribute("depart_week", depart_week);
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("line_id", line_id);
			model.addAttribute("depart_week", depart_week);
			model.addAttribute("time", time);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busPlanAdd";
	}

	public String departPlanUpdate(Model model, String line_id, String depart_dir, String depart_week,
                                   String depart_time, String original_vehicle_id, String original_driver_id) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(line_id)) {
				model.addAttribute("message", "请选择排班线路");
				return "admin/error";
			}
			if (StringUtils.isEmpty(depart_week) || !ValidateUtil.getInstance().isDigit(depart_week)) {
				model.addAttribute("message", "请选择排班日期");
				return "admin/error";
			}
			if (!StringUtils.isEmpty(depart_dir)) {
				model.addAttribute("depart_dir", depart_dir);
			}
			if (!StringUtils.isEmpty(depart_time) && ValidateUtil.getInstance().isTime(depart_time, "HH:mm")) {
				model.addAttribute("depart_time", depart_time);
			}
			Map<String, String> param = new HashMap<>();
			param.put("original_vehicle_id", original_vehicle_id);
			param.put("line_id", line_id);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			model.addAttribute("depart_time", depart_time);
			model.addAttribute("line_id", line_id);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("depart_week", depart_week);
			model.addAttribute("original_vehicle_id", original_vehicle_id);
			model.addAttribute("original_driver_id", original_driver_id);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busPlanUpdate";
	}

	public String showDepartPlan(Model model, String line_id) {

		Map<String, Object> map = new HashMap<>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			List<Map<String, Object>> planList = commonDao.query(
					"select v.vehicle_number,d.driver_name,p.depart_dir,p.depart_time,p.vehicle_id,p.driver_id,p.depart_week from sbus_depart_plan p left join sbus_driver d on d.driver_id=p.driver_id left join sbus_vehicle v on v.vehicle_id=p.vehicle_id where p.line_id=#{param.line_id} order by p.depart_dir asc,p.vehicle_id asc",
					param);
			List<Map<String, Object>> lineList = commonDao.query(
					"select up_origin_name,up_terminal_name,down_origin_name,down_terminal_name from sbus_line where line_id=#{param.line_id}",
					param);
			map.put("lineList", lineList);
			map.put("planList", planList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteDepartPlan(String time, String vehicle_id, String line_id, String depart_dir,
			String depart_week) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm")) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				map.put("message", "请选择发车方向");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_week)) {
				map.put("message", "请选择发车日期");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("vehicle_id", vehicle_id);
			param.put("line_id", line_id);
			param.put("depart_time", time);
			param.put("depart_dir", depart_dir);
			param.put("depart_week", depart_week);
			List<Map<String, Object>> list = commonDao
					.query("select line_id from sbus_line  where line_id=#{param.line_id} ", param);
			if (list.size() <= 0) {
				map.put("message", "线路计划不存在或已删除");
				return new JSONObject(map).toString();
			}
			int deleteOne = commonDao.execute(
					"delete from sbus_depart_plan where line_id=#{param.line_id} and depart_dir=#{param.depart_dir}  and vehicle_id=#{param.vehicle_id} and depart_week=#{param.depart_week} and depart_time=#{param.depart_time}",
					param);
			if (deleteOne <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	public String addDepartPlan(Model model, String time, String line_id, String vehicle_id, String driver_id,
                                String depart_dir, String depart_week) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 权限验证
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// 各种验证
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_week)) {
				map.put("message", "发车周数不能为空");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm")) {
				map.put("message", "发车时间不能为空");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				map.put("message", "请选择发车方向");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_id) || driver_id.equals("0")) {
				map.put("message", "请选择车辆司机");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<String, String>();
			param.put("depart_time", time);
			param.put("line_id", line_id);
			param.put("vehicle_id", vehicle_id);
			param.put("depart_dir", depart_dir);
			param.put("driver_id", driver_id);
			param.put("depart_week", depart_week);

			List<Map<String, Object>> isExitOfLine = commonDao
					.query("select line_name,line_type from sbus_line where line_id=#{param.line_id}", param);
			if (isExitOfLine.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			// 判断添加的是否是相同的司机与时间
			List<Map<String, Object>> isRepeatOfDriver = commonDao.query(
					"select d.driver_name from sbus_driver d left join sbus_depart_plan p on d.driver_id=p.driver_id where p.driver_id=#{param.driver_id} and p.depart_time=#{param.depart_time} and p.depart_week=#{param.depart_week}",
					param);
			if (isRepeatOfDriver.size() > 0) {
				map.put("message", time + "已设置过" + isRepeatOfDriver.get(0).get("driver_name") + "司机的排班，不能重复排班");
				return new JSONObject(map).toString();
			}
			// 判断添加的是否是相同的车辆与时间
			List<Map<String, Object>> isRepeatOfVehicle = commonDao.query(
					"select v.vehicle_number from sbus_vehicle v left join sbus_depart_plan p on v.vehicle_id=p.vehicle_id where p.vehicle_id=#{param.vehicle_id} and p.depart_time=#{param.depart_time} and p.depart_week=#{param.depart_week}",
					param);
			if (isRepeatOfVehicle.size() > 0) {
				map.put("message", time + "已设置过" + isRepeatOfVehicle.get(0).get("vehicle_number") + "车辆的排班，不能重复排班");
				return new JSONObject(map).toString();
			}
			int result = commonDao.execute(
					"insert into sbus_depart_plan (line_id,depart_dir,depart_time,vehicle_id,driver_id,depart_week) values (#{param.line_id},#{param.depart_dir},#{param.depart_time},#{param.vehicle_id},#{param.driver_id},#{param.depart_week}) ",
					param);
			if (result <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();

			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("增加排班计划【" + isExitOfLine.get(0).get("line_name") + "】" + "【"
					+ (depart_dir.equals("1") ? "上行" : "下行") + "】" + "【" + time + "】" + "【"
					+ isRepeatOfVehicle.get(0).get("vehicle_number") + "】" + "【"
					+ isRepeatOfDriver.get(0).get("driver_name") + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("msg", "系统错误");
			map.put("code", 1);
		}
		return new JSONObject(map).toString();

	}

	public String updateDepartPlan(Model model, String line_id, String depart_time, String depart_week,
                                   String vehicle_id, String driver_id, String depart_dir, String original_vehicle_id) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_time) || !ValidateUtil.getInstance().isTime(depart_time, "HH:mm")) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_week)) {
				model.addAttribute("message", "请选择排班日期");
				return "admin/error";
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				map.put("message", "请选择发车方向");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(original_vehicle_id)) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}

			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_id) || driver_id.equals("0")) {
				map.put("message", "请选择车辆司机");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			param.put("depart_week", depart_week);
			param.put("depart_dir", depart_dir);
			param.put("depart_time", depart_time);
			param.put("driver_id", driver_id);
			param.put("vehicle_id", vehicle_id);
			param.put("original_vehicle_id", original_vehicle_id);
			List<Map<String, Object>> isExitOfLine = commonDao
					.query("select line_id from sbus_line where line_id=#{param.line_id}", param);
			if (isExitOfLine.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> departPlan = commonDao.query(
					"select driver_id from sbus_depart_plan where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} and vehicle_id=#{param.original_vehicle_id}",
					param);
			if (departPlan.size() <= 0) {
				map.put("message", "排班计划不存在或已删除");
				return new JSONObject(map).toString();
			}
			param.put("original_driver_id", String.valueOf(departPlan.get(0).get("driver_id")));

			List<Map<String, Object>> originalVehicle = commonDao.query(
					"select vehicle_number from sbus_vehicle where vehicle_id=#{param.original_vehicle_id}", param);

			if (originalVehicle.size() < 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}

			List<Map<String, Object>> updateVehicle = commonDao
					.query("select vehicle_number from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
			if (!vehicle_id.equals("0")) {
				if (updateVehicle.size() < 0) {
					map.put("message", "车辆不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			//
			List<Map<String, Object>> originalDriver = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.original_driver_id}", param);
			if (originalDriver.size() < 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> updateDriver = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.driver_id}", param);
			if (updateDriver.size() < 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (!vehicle_id.equals(original_vehicle_id)) {
				List<Map<String, Object>> isRepeatOfVehicle = commonDao.query(
						"select line_id from sbus_depart_plan where vehicle_id=#{param.vehicle_id} and depart_time=#{param.depart_time}",
						param);
				if (isRepeatOfVehicle.size() > 0) {
					map.put("message",
							depart_time + "已设置过" + updateVehicle.get(0).get("vehicle_number") + "车辆的排班，不能重复排班");
					return new JSONObject(map).toString();
				}
			}
			// ######如果改了司机，同时间不能有相同的司机######
			if (!driver_id.equals(String.valueOf(departPlan.get(0).get("driver_id")))) {
				List<Map<String, Object>> isRepeatOfDriver = commonDao.query(
						"select line_id from sbus_depart_plan where driver_id=#{param.driver_id} and depart_time=#{param.depart_time}",
						param);
				if (isRepeatOfDriver.size() > 0) {
					map.put("message", depart_time + "已设置过" + updateDriver.get(0).get("driver_name") + "司机的排班，不能重复排班");
					return new JSONObject(map).toString();
				}
			}
			int updateVehicle_id = commonDao.execute(
					"update sbus_depart_plan set vehicle_id=#{param.vehicle_id},driver_id=#{param.driver_id} where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} and vehicle_id=#{param.original_vehicle_id} and depart_week=#{param.depart_week}",
					param);
			if (updateVehicle_id <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}

			map.put("success", true); // 编辑成功
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	public String planToReal(String line_id, String date, String time) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

		} catch (Exception e) {

		}

		return null;
	}

}
