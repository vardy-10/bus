package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.controller.ConfigController;
import com.zah.dao.CommonDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigServiceImpl {
	@Autowired
	CommonDao commonDao;

	public String schoolBusConfigPage(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> configList = commonDao
					.query("select vehicle_wait_minute,depart_delay_minute from sbus_config where config_id=1", param);
			if (configList.size() > 0) {
				model.addAttribute("depart_delay_minute", configList.get(0).get("depart_delay_minute"));
				model.addAttribute("vehicle_wait_minute", configList.get(0).get("vehicle_wait_minute"));
			} else {
				model.addAttribute("message", "配置不存在或已删除");
				return "admin/error";
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busSchoolConfig";
	}

	public String ticketConfigPage(Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			List<Map<String, Object>> configList = commonDao.query(
					"select seat_retain_num,ticket_student_price,ticket_teacher_price,ticket_student_hour,ticket_teacher_hour,ticket_all_minute,ticket_refund_minute,ticket_open_minute,ticket_stop_minute from sbus_config where config_id=1",
					param);
			if (configList.size() > 0) {
				model.addAttribute("seat_retain_num", configList.get(0).get("seat_retain_num"));
				model.addAttribute("ticket_student_price",
						Function.getInstance().rvZeroAndDot(configList.get(0).get("ticket_student_price").toString()));
				model.addAttribute("ticket_teacher_price",
						Function.getInstance().rvZeroAndDot(configList.get(0).get("ticket_teacher_price").toString()));
				model.addAttribute("ticket_student_hour", configList.get(0).get("ticket_student_hour"));
				model.addAttribute("ticket_teacher_hour", configList.get(0).get("ticket_teacher_hour"));
				model.addAttribute("ticket_all_minute", configList.get(0).get("ticket_all_minute"));
				model.addAttribute("ticket_refund_minute", configList.get(0).get("ticket_refund_minute"));
				model.addAttribute("ticket_open_minute", configList.get(0).get("ticket_open_minute"));
				model.addAttribute("ticket_stop_minute", configList.get(0).get("ticket_stop_minute"));
			} else {
				model.addAttribute("message", "配置不存在或已删除");
				return "admin/error";
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
		return "bus/busTicketConfig";
	}

	public String schoolBusConfig(String vehicle_wait_minute, String depart_delay_minute) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("success", false);
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// if (StringUtils.isEmpty(auto_schedule_time)
			// || !ValidateUtil.getInstance().isTime(auto_schedule_time, "yyyy-MM-dd
			// HH:mm:ss")) {
			// map.put("message", "请输入自动生成排班计划的结束时间");
			// return new JSONObject(map).toString();
			// }
			// int auto_schedule_timeStamp =
			// Function.getInstance().dateStrToInt(auto_schedule_time,
			// "yyyy-MM-dd HH:mm:ss");
			// int nowTimestamp = Function.getInstance().getNowTimestamp();
			// if (nowTimestamp > auto_schedule_timeStamp) {
			// map.put("message", "输入的自动生成排班计划的结束时间比当前时间早，请重新输入");
			// return new JSONObject(map).toString();
			// }
			if (StringUtils.isEmpty(vehicle_wait_minute) || !ValidateUtil.getInstance().isDigit(vehicle_wait_minute)
					|| Integer.parseInt(vehicle_wait_minute) < 0) {
				map.put("message", "请输入为正整数的提前发售分钟数");
				return new JSONObject(map).toString();
			}
			if (vehicle_wait_minute.length() > 3) {
				map.put("message", "预留教师位车票提前发售时间不能超过1000");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(depart_delay_minute) || !ValidateUtil.getInstance().isDigit(depart_delay_minute)
					|| Integer.parseInt(depart_delay_minute) < 0) {
				map.put("message", "请输入为正整数的允许车辆延迟发车分钟数");
				return new JSONObject(map).toString();
			}
			if (depart_delay_minute.length() > 2) {
				map.put("message", "预留教师位车票提前发售时间不能超过100");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("vehicle_wait_minute", vehicle_wait_minute);
			param.put("depart_delay_minute", depart_delay_minute);
			int schoolBusConfig = commonDao.execute(
					"update sbus_config set vehicle_wait_minute=#{param.vehicle_wait_minute},depart_delay_minute=#{param.depart_delay_minute} where config_id=1",
					param);
			if (schoolBusConfig <= 0) {
				map.put("message", "操作失败");
			}
			map.put("success", true);
			map.put("message", "操作成功");
			ConfigController.configMap.put("vehicle_wait_minute", Integer.parseInt(vehicle_wait_minute));
			ConfigController.configMap.put("depart_delay_minute", Integer.parseInt(depart_delay_minute));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	public String ticketConfig(String seat_retain_num, String ticket_student_price, String ticket_teacher_price,
			String ticket_student_hour, String ticket_teacher_hour, String ticket_all_minute,
			String ticket_refund_minute, String ticket_open_minute, String ticket_stop_minute) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("success", false);
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(seat_retain_num) || !ValidateUtil.getInstance().isDigit(seat_retain_num)
					|| Integer.parseInt(seat_retain_num) < 0) {
				map.put("message", "请输入为正整数的预留座位数");
				return new JSONObject(map).toString();
			}
			if (ValidateUtil.getInstance().isDigit(ticket_student_price)) {
				ticket_student_price = ticket_student_price + ".00";
			}
			if (ValidateUtil.getInstance().isDigit(ticket_teacher_price)) {
				ticket_teacher_price = ticket_teacher_price + ".00";
			}
			if (ValidateUtil.getInstance().isNumber(ticket_student_price)) {
				String[] tsp = ticket_student_price.split("\\.");

				if (StringUtils.isEmpty(ticket_student_price) || tsp[1].length() > 2 || tsp[0].length() > 3
						|| Integer.parseInt(tsp[0]) < 0) {
					map.put("message", "请输入1000以内的学生票价");
					return new JSONObject(map).toString();
				}
			} else {
				map.put("message", "请输入为数字的学生票价");
				return new JSONObject(map).toString();
			}
			if (ValidateUtil.getInstance().isNumber(ticket_teacher_price)) {
				String[] ttp = ticket_teacher_price.split("\\.");
				if (StringUtils.isEmpty(ticket_teacher_price) || ttp[1].length() > 2 || ttp[0].length() > 3
						|| Integer.parseInt(ttp[0]) < 0) {
					map.put("message", "请输入1000以内的教师票价");
					return new JSONObject(map).toString();
				}
			} else {
				map.put("message", "请输入为数字的教师票价");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(ticket_student_hour) || !ValidateUtil.getInstance().isDigit(ticket_student_hour)
					|| Integer.parseInt(ticket_student_hour) <= 0 || ticket_student_hour.length() > 3) {
				map.put("message", "请输入1000以内且为整数的开始出售学生车票的时间");
				return new JSONObject(map).toString();
			}

			if (StringUtils.isEmpty(ticket_teacher_hour) || !ValidateUtil.getInstance().isDigit(ticket_teacher_hour)
					|| Integer.parseInt(ticket_teacher_hour) <= 0 || ticket_teacher_hour.length() > 3) {
				map.put("message", "请输入1000以内且为整数的开始出售教师车票的时间");
				return new JSONObject(map).toString();
			}

			if (StringUtils.isEmpty(ticket_all_minute) || !ValidateUtil.getInstance().isDigit(ticket_all_minute)
					|| Integer.parseInt(ticket_all_minute) < 0 || ticket_all_minute.length() > 3) {
				map.put("message", "请输入1000以内且为整数的开放预留教师位的车票的时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(ticket_refund_minute) || !ValidateUtil.getInstance().isDigit(ticket_refund_minute)
					|| Integer.parseInt(ticket_refund_minute) < 0 || ticket_refund_minute.length() > 3) {
				map.put("message", "请输入1000以内发车前允许退票的时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(ticket_open_minute) || !ValidateUtil.getInstance().isDigit(ticket_open_minute)
					|| Integer.parseInt(ticket_open_minute) <= 0 || ticket_open_minute.length() > 3) {
				map.put("message", "请输入1000以内提前检票的时间");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(ticket_stop_minute) || !ValidateUtil.getInstance().isInteger(ticket_stop_minute)
					|| Integer.parseInt(ticket_stop_minute) > 1000) {
				map.put("message", "请输入1000以内停止检票的时间");
				return new JSONObject(map).toString();
			}
			if (Integer.parseInt(ticket_stop_minute) < 0
					&& Integer.parseInt(ticket_open_minute) + Integer.parseInt(ticket_stop_minute) <= 0) {
				map.put("message", "提前检票时间必须在停止检票时间之前");
				return new JSONObject(map).toString();
			}

			Map<String, String> param = new HashMap<>();
			param.put("seat_retain_num", seat_retain_num);
			param.put("ticket_student_price", ticket_student_price);
			param.put("ticket_teacher_price", ticket_teacher_price);
			param.put("ticket_teacher_hour", ticket_teacher_hour);
			param.put("ticket_student_hour", ticket_student_hour);
			param.put("ticket_all_minute", ticket_all_minute);
			param.put("ticket_refund_minute", ticket_refund_minute);
			param.put("ticket_open_minute", ticket_open_minute);
			param.put("ticket_stop_minute", ticket_stop_minute);
			int ticketConfig = commonDao.execute(
					"update sbus_config set ticket_open_minute = #{param.ticket_open_minute},ticket_stop_minute=#{param.ticket_stop_minute},seat_retain_num=#{param.seat_retain_num},ticket_student_price=#{param.ticket_student_price}, ticket_teacher_price=#{param.ticket_teacher_price},ticket_teacher_hour=#{param.ticket_teacher_hour},ticket_student_hour=#{param.ticket_student_hour},ticket_all_minute=#{param.ticket_all_minute},ticket_refund_minute=#{param.ticket_refund_minute} where config_id=1",
					param);
			if (ticketConfig <= 0) {
				map.put("message", "操作失败");
			}
			map.put("success", true);
			map.put("message", "操作成功");
			ConfigController.configMap.put("seat_retain_num", Integer.parseInt(seat_retain_num));
			ConfigController.configMap.put("ticket_student_price", new BigDecimal(ticket_student_price));
			ConfigController.configMap.put("ticket_teacher_price", new BigDecimal(ticket_teacher_price));
			ConfigController.configMap.put("ticket_teacher_hour", Integer.parseInt(ticket_teacher_hour));
			ConfigController.configMap.put("ticket_student_hour", Integer.parseInt(ticket_student_hour));
			ConfigController.configMap.put("ticket_all_minute", Integer.parseInt(ticket_all_minute));
			ConfigController.configMap.put("ticket_refund_minute", Integer.parseInt(ticket_refund_minute));
			ConfigController.configMap.put("ticket_open_minute", Integer.parseInt(ticket_open_minute));
			ConfigController.configMap.put("ticket_stop_minute", Integer.parseInt(ticket_stop_minute));
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}
}
