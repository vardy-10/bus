package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.DepartRealDao;
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
public class DepartRealServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	PublicService publicService;

	@Autowired
	DepartRealDao departRealDao;

	public String departRealShow(Model model, String line_id) {
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			model.addAttribute("date", sdf.format(new Date()));
			model.addAttribute("line_id", line_id);
			model.addAttribute("line_name", list.get(0).get("line_name"));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busCrewScheduleShow";
	}

	public String departRealAdd(Model model, String line_id, String depart_dir, String date, String time) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(line_id)) {
				model.addAttribute("message", "请选择排班线路");
				return "admin/error";
			}
			if (StringUtils.isEmpty(date) || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				model.addAttribute("message", "请选择排班日期");
				return "admin/error";
			}
			if (!StringUtils.isEmpty(depart_dir)) {
				model.addAttribute("depart_dir", depart_dir);
			}
			if (!StringUtils.isEmpty(time) && ValidateUtil.getInstance().isTime(date, "HH:mm:ss")) {
				model.addAttribute("time", time);
			}
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle ", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			List<Map<String, Object>> lineList = commonDao
					.query("select line_type from sbus_line where line_id=#{param.line_id}", param);
			if (lineList.size() <= 0) {
				model.addAttribute("message", "线路不存在或已删除");
				return "admin/error";
			}
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
			model.addAttribute("date", date);
			model.addAttribute("time", time);
			model.addAttribute("line_id", line_id);
			model.addAttribute("line_type", lineList.get(0).get("line_type"));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busCrewScheduleAdd";
	}

	public String departRealUpdate(Model model, String line_id, String depart_dir, String date, String time,
                                   String original_vehicle_id, String original_driver_id) {

		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(line_id)) {
				model.addAttribute("message", "请选择排班线路");
				return "admin/error";
			}
			if (StringUtils.isEmpty(date) || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				model.addAttribute("message", "请选择排班日期");
				return "admin/error";
			}

			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				model.addAttribute("message", "请选择发车方向");
				return "admin/error";
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm:ss")) {
				model.addAttribute("message", "请选择发车时间");
				return "admin/error";
			}
			if (StringUtils.isEmpty(original_vehicle_id)) {
				model.addAttribute("message", "请选择发车车辆");
				return "admin/error";
			}
			String depart_time = date + " " + time;
			long stampTime = Function.getInstance().timeStrToSeconds(depart_time, "yyyy-MM-dd HH:mm:ss");
			if (stampTime == -1) {
				model.addAttribute("message", "请选择发车时间");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			param.put("original_vehicle_id", original_vehicle_id);
			param.put("original_driver_id", original_driver_id);
			param.put("line_id", line_id);
			param.put("depart_time", String.valueOf(stampTime));
			param.put("depart_dir", depart_dir);
			List<Map<String, Object>> departReal = commonDao.query(
					"select line_id from sbus_depart_real where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} and vehicle_id=#{param.original_vehicle_id}",
					param);
			if (departReal.size() <= 0) {
				model.addAttribute("message", "排班日程不存在或已删除");
				return "admin/error";
			}
			List<Map<String, Object>> vehicleList = commonDao
					.query("select vehicle_number,vehicle_id from sbus_vehicle", param);
			List<Map<String, Object>> driverList = commonDao.query("select driver_name,driver_id from sbus_driver",
					param);
			model.addAttribute("line_id", line_id);
			model.addAttribute("depart_dir", depart_dir);
			model.addAttribute("date", date);
			model.addAttribute("time", time);
			model.addAttribute("original_vehicle_id", original_vehicle_id);
			model.addAttribute("original_driver_id", original_driver_id);
			model.addAttribute("vehicleList", vehicleList);
			model.addAttribute("driverList", driverList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busCrewScheduleUpdate";

	}

	public String showDepartReal(Model model, String line_id, String date) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (line_id == null) {
				map.put("message", "请选择要查看日程的线路");
				return new JSONObject(map).toString();
			}
			if (date == null || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				map.put("message", "日期格式错误");
				return new JSONObject(map).toString();
			}
			String[] date_arr = date.split("-");

			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(date_arr[0]), Integer.parseInt(date_arr[1]) - 1, Integer.parseInt(date_arr[2]), 0,
					0, 0);

			// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (1 == dayWeek) {
				cal.add(Calendar.DAY_OF_MONTH, -1);
			}
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
			long startTime_stamp = cal.getTimeInMillis() / 1000;
			// 设置一个星期的最后一天，按中国的习惯一个星期的最后一天是星期日
			cal.add(Calendar.DATE, 7);
			long endTime_stamp = cal.getTimeInMillis() / 1000;
			Map<String, String> param = new HashMap<>();
			param.put("line_id", line_id);
			param.put("startTime", String.valueOf(startTime_stamp));
			param.put("endTime", String.valueOf(endTime_stamp));
			List<Map<String, Object>> realList = commonDao.query(
					"select v.vehicle_number,d.driver_name,r.depart_dir,r.depart_time,r.vehicle_id,r.driver_id,SUBSTRING_INDEX(FROM_UNIXTIME(r.depart_time),' ',1) as date,SUBSTRING_INDEX(FROM_UNIXTIME(r.depart_time),' ',-1) as time,if(date_format(FROM_UNIXTIME(r.depart_time),'%w')=0,7,date_format(FROM_UNIXTIME(r.depart_time),'%w')) as week from sbus_depart_real r left join sbus_driver d on d.driver_id=r.driver_id left join sbus_vehicle v on v.vehicle_id=r.vehicle_id  where r.line_id=#{param.line_id} and r.depart_time >= #{param.startTime} and r.depart_time<#{param.endTime} order by r.depart_time asc,r.depart_dir asc,r.vehicle_id asc",
					param);
			List<Map<String, Object>> lineList = commonDao.query(
					"select up_origin_name,up_terminal_name,down_origin_name,down_terminal_name from sbus_line where line_id=#{param.line_id}",
					param);
			map.put("lineList", lineList);
			map.put("realList", realList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String updateDepartReal(Model model, String line_id, String time, String date, String vehicle_id,
                                   String driver_id, String depart_dir, String original_vehicle_id) {
		// vehicle_id与driver_id为编辑过后的车辆id与司机id
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {// ######只能改司机和车辆，必须得到线路id，方向，时间######
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(date) || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				model.addAttribute("message", "请选择排班日期");
				return "admin/error";
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				map.put("message", "请选择发车方向");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm:ss")) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			String depart_time = date + " " + time;
			long stampTime = Function.getInstance().timeStrToSeconds(depart_time, "yyyy-MM-dd HH:mm:ss");
			if (stampTime == -1) {
				map.put("message", "请选择发车时间");
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
			param.put("depart_dir", depart_dir);
			param.put("depart_time", String.valueOf(stampTime));
			param.put("vehicle_id", vehicle_id);
			param.put("driver_id", driver_id);
			param.put("original_vehicle_id", original_vehicle_id);
			List<Map<String, Object>> departReal = commonDao.query(
					"select driver_id,total_seat_num,sold_seat_num from sbus_depart_real where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} and vehicle_id=#{param.original_vehicle_id}",
					param);
			if (departReal.size() <= 0) {
				map.put("message", "排班日程不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> originalVehicle = commonDao.query(
					"select vehicle_number,seat_num from sbus_vehicle where vehicle_id=#{param.original_vehicle_id}",
					param);
			if (originalVehicle.size() <= 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			param.put("original_driver_id", String.valueOf(departReal.get(0).get("driver_id")));
			List<Map<String, Object>> isExitOfLine = commonDao
					.query("select line_name,line_type from sbus_line where line_id=#{param.line_id}", param);
			if (isExitOfLine.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> originalDriver = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.original_driver_id}", param);
			if (originalVehicle.size() <= 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> updateVehicle = commonDao.query(
					"select vehicle_number,seat_num from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
			if (updateVehicle.size() <= 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> updateDriver = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.driver_id}", param);
			if (updateDriver.size() < 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}

			// ######如果改了车辆######
			//// ######同时间不能有相同的车辆######
			//// ######计算新的总座位数，不能小于已出售的票数######
			if (!vehicle_id.equals(original_vehicle_id)) {
				List<Map<String, Object>> isRepeatOfVehicle = commonDao.query(
						"select line_id from sbus_depart_real where vehicle_id=#{param.vehicle_id} and depart_time=#{param.depart_time} limit 1",
						param);
				if (isRepeatOfVehicle.size() > 0) {
					map.put("message",
							depart_time + "已设置过" + updateVehicle.get(0).get("vehicle_number") + "车辆的排班，不能重复排班");
					return new JSONObject(map).toString();
				}
				int sold_seat_num = (int) departReal.get(0).get("sold_seat_num");// 已售出的座位
				int total_seat_num = (int) departReal.get(0).get("total_seat_num");// 总座位数
				int seat_num_before = (int) originalVehicle.get(0).get("seat_num");// 编辑前车辆表的车辆座位数
				int update_seat_num = (int) updateVehicle.get(0).get("seat_num");
				int new_total_seat_num = total_seat_num - seat_num_before + update_seat_num;
				if (new_total_seat_num < 0) {
					new_total_seat_num = 0;
				}
				param.put("new_total_seat_num", String.valueOf(new_total_seat_num));
				if (sold_seat_num > 0 && sold_seat_num > new_total_seat_num) {
					map.put("message", "该日程的总座位数小于已出售的票数");
					return new JSONObject(map).toString();
				}
			}

			// ######如果改了司机，同时间不能有相同的司机######
			if (!driver_id.equals(String.valueOf(departReal.get(0).get("driver_id")))) {
				List<Map<String, Object>> isRepeatOfDriver = commonDao.query(
						"select line_id from sbus_depart_real where driver_id=#{param.driver_id} and depart_time=#{param.depart_time} limit 1",
						param);
				if (isRepeatOfDriver.size() > 0) {
					map.put("message", depart_time + "已设置过" + updateDriver.get(0).get("driver_name") + "司机的排班，不能重复排班");
					return new JSONObject(map).toString();
				}
			}

			// ######编辑日程（如果修改了车辆，计算新的总座位数）######
			int updateVehicle_id = commonDao.execute(
					"update sbus_depart_real set vehicle_id=#{param.vehicle_id},driver_id=#{param.driver_id} where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} and vehicle_id=#{param.original_vehicle_id} ",
					param);
			if (updateVehicle_id <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			if (!vehicle_id.equals(original_vehicle_id)) {
				int updateSeatNum = commonDao.execute(
						"update sbus_depart_real set total_seat_num=#{param.new_total_seat_num} where line_id=#{param.line_id} and depart_dir=#{param.depart_dir} and depart_time=#{param.depart_time}",
						param);
				if (updateSeatNum <= 0) {
					throw new RuntimeException("发生异常！");
				}
			}
			map.put("success", true); // 编辑成功
			map.put("message", "操作成功");
			publicService.addLog("编辑排班日程【" + isExitOfLine.get(0).get("line_name") + "】" + "【"
					+ (depart_dir.equals("1") ? "上行" : "下行") + "】" + "【" + depart_time + "】" + "【"
					+ originalVehicle.get(0).get("vehicle_number") + "】" + "【"
					+ originalDriver.get(0).get("driver_name") + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	/**
	 * 添加日程操作
	 */
	@Transactional(rollbackFor = Exception.class)
	public String addDepartReal(Model model, String date, String time, String line_id, String vehicle_id,
                                String driver_id, String depart_dir) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// ######得到线路id,方向，时间，车辆和司机（全部必须收到）######
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(date) || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				map.put("message", "请选择排班日期");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {
				map.put("message", "请选择发车方向");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm:ss")) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || vehicle_id.equals("0")) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_id) || driver_id.equals("0")) {
				map.put("message", "请选择车辆司机");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<String, String>();
			String depart_time = date + " " + time;
			long stampTime = Function.getInstance().timeStrToSeconds(depart_time, "yyyy-MM-dd HH:mm:ss");
			if (stampTime == -1) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			param.put("depart_time", String.valueOf(stampTime));
			param.put("line_id", line_id);
			param.put("vehicle_id", vehicle_id);
			param.put("depart_dir", depart_dir);
			param.put("driver_id", driver_id);
			List<Map<String, Object>> isExitOfLine = commonDao
					.query("select line_name,line_type from sbus_line where line_id=#{param.line_id}", param);
			if (isExitOfLine.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			// 单向线路添加下行
			if (isExitOfLine.get(0).get("line_type").equals("1") && depart_dir.equals("2")) {
				map.put("message", "单向线路不能添加下行排班");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isExitOfDriver = commonDao
					.query("select driver_name from sbus_driver where driver_id=#{param.driver_id}", param);
			if (isExitOfDriver.size() <= 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> isExitOfVehicle = commonDao.query(
					"select seat_num,vehicle_number from sbus_vehicle where vehicle_id=#{param.vehicle_id}", param);
			if (isExitOfVehicle.size() <= 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			// ######判断相同的时间不能有相同的车辆被添加（无论方向）######
			List<Map<String, Object>> isRepeatOfVehicle = commonDao.query(
					"select line_id from sbus_depart_real where vehicle_id=#{param.vehicle_id} and depart_time=#{param.depart_time}",
					param);
			if (isRepeatOfVehicle.size() > 0) {
				map.put("message",
						depart_time + "已设置过" + isExitOfVehicle.get(0).get("vehicle_number") + "车辆的排班，不能重复排班");
				return new JSONObject(map).toString();
			}
			// ######判断相同的时间不能有相同的司机被添加（无论方向）######
			List<Map<String, Object>> isRepeatOfDriver = commonDao.query(
					"select line_id from sbus_depart_real where driver_id=#{param.driver_id} and depart_time=#{param.depart_time}",
					param);
			if (isRepeatOfDriver.size() > 0) {
				map.put("message", depart_time + "已设置过" + isExitOfDriver.get(0).get("driver_name") + "司机的排班，不能重复排班");
				return new JSONObject(map).toString();
			}
			// ######添加日程（车辆的总座位数采用同时间同方向车辆座位数的累计）######
			List<Map<String, Object>> departReallist = commonDao.query(
					"select total_seat_num from sbus_depart_real where line_id=#{param.line_id} and depart_time=#{param.depart_time} and depart_dir=#{param.depart_dir} limit 1",
					param);
			int sum = departReallist.size() > 0 ? ((int) departReallist.get(0).get("total_seat_num")) : 0;
			sum += (int) isExitOfVehicle.get(0).get("seat_num");

			int insert = commonDao.execute(
					"insert into sbus_depart_real (line_id,depart_dir,depart_time,vehicle_id,driver_id) values (#{param.line_id},#{param.depart_dir},#{param.depart_time},#{param.vehicle_id},#{param.driver_id}) ",
					param);
			if (insert == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			param.put("total_seat_num", String.valueOf(sum));
			int update = commonDao.execute(
					"update sbus_depart_real set total_seat_num=#{param.total_seat_num} where depart_dir=#{param.depart_dir} and depart_time=#{param.depart_time} and line_id=#{param.line_id}",
					param);
			if (update == 0) {
				throw new RuntimeException("发生异常！");
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("增加排班日程【" + isExitOfLine.get(0).get("line_name") + "】" + "【"
					+ (depart_dir.equals("1") ? "上行" : "下行") + "】" + "【" + depart_time + "】" + "【"
					+ isExitOfVehicle.get(0).get("vehicle_number") + "】" + "【"
					+ isExitOfDriver.get(0).get("driver_name") + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	/*
	 * 删除日程操作
	 */
	// 加锁
	@Transactional(rollbackFor = Exception.class)
	public String deleteDepartReal(String date, String time, String line_id, String depart_dir, String vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// ######得到线路id，方向，时间，车辆和司机（全部必须收到）######
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择排班线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_dir) || !depart_dir.equals("1") && !depart_dir.equals("2")) {

			}
			if (StringUtils.isEmpty(date) || !ValidateUtil.getInstance().isTime(date, "yyyy-MM-dd")) {
				map.put("message", "请选择发车日期");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(time) || !ValidateUtil.getInstance().isTime(time, "HH:mm:ss")) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}

			if (vehicle_id.equals("0")) {
				map.put("message", "请选择发车车辆");
				return new JSONObject(map).toString();
			}

			Map<String, String> param = new HashMap<>();
			String depart_time = date + " " + time;
			long stampTime = Function.getInstance().timeStrToSeconds(depart_time, "yyyy-MM-dd HH:mm:ss");
			if (stampTime == -1) {
				map.put("message", "请选择发车时间");
				return new JSONObject(map).toString();
			}
			param.put("depart_time", String.valueOf(stampTime));
			param.put("line_id", line_id);
			param.put("depart_dir", depart_dir);
			param.put("vehicle_id", vehicle_id);
			List<Map<String, Object>> lineList = commonDao
					.query("select line_name from sbus_line where line_id=#{param.line_id}", param);
			if (lineList.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> list = commonDao.query(
					"select r.total_seat_num,r.sold_seat_num,v.vehicle_number,v.seat_num from sbus_depart_real r left join sbus_vehicle v on r.vehicle_id=v.vehicle_id where r.line_id=#{param.line_id} and r.depart_dir=#{param.depart_dir} and r.depart_time=#{param.depart_time} and r.vehicle_id=#{param.vehicle_id}",
					param);
			if (list.size() <= 0) {
				map.put("message", "线路排班不存在或已删除");
				return new JSONObject(map).toString();
			}

			// ######如果已出售票的，总座位数减去删除车辆的座位数不能小于已出售的票数######
			int seat_num = (int) list.get(0).get("seat_num");
			int sold_seat_num = (int) list.get(0).get("sold_seat_num");
			int total_seat_num = (int) list.get(0).get("total_seat_num");
			param.put("sold_seat_num", String.valueOf(sold_seat_num));
			if (sold_seat_num > 0 && sold_seat_num > total_seat_num - seat_num) {
				map.put("message", "删除该排班车辆将导致车辆座位数小于已出售票数，无法删除");
				return new JSONObject(map).toString();
			}

			// ######删除班次，修改总座位数######
			param.put("total_seat_num", String.valueOf(total_seat_num - seat_num > 0 ? total_seat_num - seat_num : 0));
			int updateDepartReal = commonDao.execute(
					"update sbus_depart_real set total_seat_num=#{param.total_seat_num} where line_id=#{param.line_id} and depart_dir=#{param.depart_dir} and depart_time=#{param.depart_time}",
					param);
			if (updateDepartReal == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}

			int deleteOne = commonDao.execute(
					"delete from sbus_depart_real where line_id=#{param.line_id} and depart_dir=#{param.depart_dir} and depart_time=#{param.depart_time} and vehicle_id=#{param.vehicle_id}",
					param);
			if (deleteOne <= 0) {
				throw new RuntimeException("发生异常！");
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog(
					"删除排班日程【" + lineList.get(0).get("line_name") + "】" + "【" + (depart_dir.equals("1") ? "上行" : "下行")
							+ "】" + "【" + depart_time + "】" + "【" + list.get(0).get("vehicle_number") + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String updateDepartReal() {
		return null;

	}

}
