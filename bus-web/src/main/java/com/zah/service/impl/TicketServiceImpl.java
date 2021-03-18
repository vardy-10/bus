package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.dao.TicketDao;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.ExcelUtil;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TicketServiceImpl {
	@Autowired
	CommonDao commonDao;
	@Autowired
	TicketDao ticketDao;
	@Autowired
	PublicService publicService;

	// 展示临时乘客加载页
	public String tempCheckShow(Model model) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			return "bus/busTempCheckShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 展示订单处理加载页
	public String busOrderShow(Model model) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(new Date());
			model.addAttribute("nowDate", nowDate);
			return "bus/busOrderShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 票款统计加载页
	public String busAccountCheckShow(Model model) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String startDate = sdf.format(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			String endDate = sdf2.format(date);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			return "bus/busAccountCheckShow";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 临时乘客添加页
	public String tempCheckAdd(Model model) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			return "bus/busTempCheckAdd";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 临时乘客编辑页
	public String tempCheckUpdate(Model model, String temp_identity) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(temp_identity)) {
				model.addAttribute("message", "请选择临时工勤用户审核");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			param.put("temp_identity", temp_identity);
			List<Map<String, Object>> ticketList = commonDao.query(
					"select temp_name,temp_type,temp_sex,temp_phone,temp_mobile,temp_email,temp_campus,temp_dept,temp_post,temp_number,temp_boss_name,temp_boss_phone,create_time,is_audit from sbus_temp where temp_identity=#{param.temp_identity}",
					param);
			if (ticketList.size() <= 0) {
				model.addAttribute("message", "临时工勤用户审核不存在或已删除");
				return "admin/error";
			}
			model.addAttribute("temp_identity", temp_identity);
			model.addAttribute("temp_name", ticketList.get(0).get("temp_name"));
			model.addAttribute("temp_type", ticketList.get(0).get("temp_type"));
			model.addAttribute("temp_sex", ticketList.get(0).get("temp_sex"));
			model.addAttribute("temp_phone", ticketList.get(0).get("temp_phone"));
			model.addAttribute("temp_mobile", ticketList.get(0).get("temp_mobile"));
			model.addAttribute("temp_email", ticketList.get(0).get("temp_email"));
			model.addAttribute("temp_campus", ticketList.get(0).get("temp_campus"));
			model.addAttribute("temp_dept", ticketList.get(0).get("temp_dept"));
			model.addAttribute("temp_post", ticketList.get(0).get("temp_post"));
			model.addAttribute("temp_number", ticketList.get(0).get("temp_number"));
			model.addAttribute("temp_boss_name", ticketList.get(0).get("temp_boss_name"));
			model.addAttribute("temp_boss_phone", ticketList.get(0).get("temp_boss_phone"));
			model.addAttribute("create_time", ticketList.get(0).get("create_time"));
			model.addAttribute("is_audit", ticketList.get(0).get("is_audit"));
			return "bus/busTempCheckUpdate";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 临时乘客详情页
	public String tempCheckShowOne(Model model, String temp_identity) {
		try {
			if (!PublicService.isRole("ticket")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (StringUtils.isEmpty(temp_identity)) {
				model.addAttribute("message", "请选择临时工勤用户审核");
				return "admin/error";
			}
			Map<String, String> param = new HashMap<>();
			param.put("temp_identity", temp_identity);
			List<Map<String, Object>> ticketList = commonDao.query(
					"select temp_name,temp_type,temp_sex,temp_phone,temp_mobile,temp_email,temp_campus,temp_dept,temp_post,temp_number,temp_boss_name,temp_boss_phone,create_time,is_audit from sbus_temp where temp_identity=#{param.temp_identity}",
					param);
			if (ticketList.size() <= 0) {
				model.addAttribute("message", "临时工勤用户审核不存在或已删除");
				return "admin/error";
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = simpleDateFormat.format((long) ticketList.get(0).get("create_time") * 1000);
			model.addAttribute("temp_identity", temp_identity);
			model.addAttribute("temp_name", ticketList.get(0).get("temp_name"));
			model.addAttribute("temp_type", ticketList.get(0).get("temp_type"));
			model.addAttribute("temp_sex", ticketList.get(0).get("temp_sex"));
			model.addAttribute("temp_phone", ticketList.get(0).get("temp_phone"));
			model.addAttribute("temp_mobile", ticketList.get(0).get("temp_mobile"));
			model.addAttribute("temp_email", ticketList.get(0).get("temp_email"));
			model.addAttribute("temp_campus", ticketList.get(0).get("temp_campus"));
			model.addAttribute("temp_dept", ticketList.get(0).get("temp_dept"));
			model.addAttribute("temp_post", ticketList.get(0).get("temp_post"));
			model.addAttribute("temp_number", ticketList.get(0).get("temp_number"));
			model.addAttribute("temp_boss_name", ticketList.get(0).get("temp_boss_name"));
			model.addAttribute("temp_boss_phone", ticketList.get(0).get("temp_boss_phone"));
			model.addAttribute("create_time", createTime);
			model.addAttribute("is_audit", ticketList.get(0).get("is_audit"));
			return "bus/busTempCheckShowOne";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	// 订单-数据加载
	public String showBusOrder(int limit, int page, String shifts_date, String startTime, String endTime,
			String passenger_id, String shifts_number, String up_origin_name, String order_id, String order_sn,
			String refund_sn, String order_state, String passenger_type, String line_name) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("ticket")) {
				messageMap.put("msg", "无权限");
				messageMap.put("code", 1);
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(shifts_date)) {
				columnMap.put("shifts_date", shifts_date);
			}
			if (!StringUtils.isEmpty(startTime)) {
				columnMap.put("startTime", startTime);
			}
			if (!StringUtils.isEmpty(endTime)) {
				columnMap.put("endTime", endTime);
			}
			if (!StringUtils.isEmpty(passenger_id)) {
				columnMap.put("passenger_id", passenger_id);
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(order_id)) {
				columnMap.put("order_id", order_id);
			}
			if (!StringUtils.isEmpty(order_sn)) {
				columnMap.put("order_sn", order_sn);
			}
			if (!StringUtils.isEmpty(refund_sn)) {
				columnMap.put("refund_sn", refund_sn);
			}
			if (!StringUtils.isEmpty(order_state)) {
				columnMap.put("order_state", order_state);
			}
			if (!StringUtils.isEmpty(passenger_type)) {
				columnMap.put("passenger_type", passenger_type);
			}
			int count = ticketDao.getOrderCount(columnMap);
			List<Map<String, Object>> ticketList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				ticketList = ticketDao.getOrderList(columnMap, (page - 1) * limit, limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", ticketList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();
	}

	// 展示临时乘客-数据加载页
	public String showTempCheck(int limit, int page, String date, String is_audit, String temp_name, String temp_type,
			String temp_dept, String temp_number, String temp_post, String temp_mobile, String temp_boss_name) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("ticket")) {
				messageMap.put("msg", "无权限");
				messageMap.put("code", 1);
				return new JSONObject(messageMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(date)) {
				if (Function.getInstance().date("yyyy-MM-dd", date) != null) {
					long timeStamp = Function.getInstance().timeStrToSeconds(date, "yyyy-MM-dd");
					columnMap.put("index_time", String.valueOf(timeStamp));
					columnMap.put("create_time", String.valueOf(timeStamp + 86400 - 1));
				}
			}
			if ("0".equals(is_audit) || "1".equals(is_audit)) {
				columnMap.put("is_audit", is_audit);
			}
			if (!StringUtils.isEmpty(temp_name)) {
				columnMap.put("temp_name", temp_name);
			}
			if ("1".equals(temp_type) || "2".equals(temp_type)) {
				columnMap.put("temp_type", temp_type);
			}
			if (!StringUtils.isEmpty(temp_dept)) {
				columnMap.put("temp_dept", temp_dept);
			}
			if (!StringUtils.isEmpty(temp_number)) {
				columnMap.put("temp_number", temp_number);
			}
			if (!StringUtils.isEmpty(temp_post)) {
				columnMap.put("temp_post", temp_post);
			}
			if (!StringUtils.isEmpty(temp_mobile)) {
				columnMap.put("temp_mobile", temp_mobile);
			}
			if (!StringUtils.isEmpty(temp_boss_name)) {
				columnMap.put("temp_boss_name", temp_boss_name);
			}
			int count = ticketDao.getTempCount(columnMap);
			List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				tempList = ticketDao.getTempList(columnMap, (page - 1) * limit, limit);
			}
			messageMap.put("msg", "获取成功");
			messageMap.put("code", 0);
			messageMap.put("count", count);
			messageMap.put("data", tempList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			messageMap.put("msg", "系统错误");
			messageMap.put("code", 1);
		}
		return new JSONObject(messageMap).toString();
	}

	// 添加临时乘客
	public String addTempCheck(String temp_name, String temp_type, String temp_sex, String temp_identity,
			String temp_dept, String temp_post, String temp_number, String temp_boss_name, String temp_boss_phone,
			String temp_email, String temp_mobile, String temp_phone, String temp_campus, String password,
			String is_audit) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("ticket")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_identity)) {
				map.put("message", "请输入临时乘客身份证号");
				return new JSONObject(map).toString();
			}
			if (temp_identity.length() > 20) {
				map.put("message", "请输入20位以内的临时乘客身份证号");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_name)) {
				map.put("message", "请输入临时乘客姓名");
				return new JSONObject(map).toString();
			}
			if (temp_name.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客姓名");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(password)) {
				map.put("message", "请输入临时乘客密码");
				return new JSONObject(map).toString();
			}
			if (!ValidateUtil.getInstance().isPassword(password)) {
				map.put("message", "请输入8位以上由数字、字母和符号组成的临时乘客密码");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_sex) || !"1".equals(temp_sex) && !"2".equals(temp_sex)) {
				map.put("message", "请选择临时乘客性别");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_mobile)) {
				map.put("message", "请输入临时乘客手机号");
				return new JSONObject(map).toString();
			}
			if (temp_mobile.length() > 15) {
				map.put("message", "请输入15位以内的临时乘客手机号");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_type) || !"1".equals(temp_type) && !"2".equals(temp_type)) {
				map.put("message", "请选择临时乘客类型");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_phone) && temp_phone.length() > 15) {
				map.put("message", "请输入15位以内的临时乘客手机号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_campus) && temp_campus.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客所属校区");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_dept) && temp_dept.length() > 10) {
				map.put("message", "请输入15位以内的临时乘客工作部门");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_post) && temp_post.length() > 15) {
				map.put("message", "请输入10位以内的临时乘客工作岗位");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_number) && temp_number.length() > 20) {
				map.put("message", "请输入20位以内的临时乘客工号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_email) && temp_email.length() > 15) {
				map.put("message", "请输入50位以内的临时乘客邮箱");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_boss_name) && temp_boss_name.length() > 10) {
				map.put("message", "请输入10位以内的直属领导姓名");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_boss_phone) && temp_boss_phone.length() > 15) {
				map.put("message", "请输入15位以内的直属领导电话");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("temp_identity", temp_identity);
			List<Map<String, Object>> tempList = commonDao
					.query("select temp_identity from sbus_temp where temp_identity=#{param.temp_identity}", param);
			if (tempList.size() > 0) {
				map.put("message", "临时乘客身份证号已存在");
				return new JSONObject(map).toString();
			}
			String num = String.valueOf((int) (Math.random() * 10)) + String.valueOf((int) (Math.random() * 10))
					+ String.valueOf((int) (Math.random() * 10)) + String.valueOf((int) (Math.random() * 10));
			String SHA_pwd = Function.getInstance().SHA1(num + password);
			String createTime = String.valueOf(System.currentTimeMillis() / 1000);
			param.put("temp_name", temp_name);
			param.put("temp_type", temp_type);
			param.put("temp_dept", temp_dept);
			param.put("temp_post", temp_post);
			param.put("temp_sex", temp_sex);
			param.put("temp_number", temp_number);
			param.put("temp_boss_name", temp_boss_name);
			param.put("temp_boss_phone", temp_boss_phone);
			param.put("temp_email", temp_email);
			param.put("temp_mobile", temp_mobile);
			param.put("temp_phone", temp_phone);
			param.put("temp_campus", temp_campus);
			param.put("password", SHA_pwd);
			param.put("encrypt", num);
			param.put("create_time", createTime);
			int insert = commonDao.execute(
					"insert into sbus_temp (temp_name,temp_sex,temp_type,temp_identity,temp_dept,temp_post,temp_number,temp_boss_name,temp_boss_phone,temp_email,temp_mobile,temp_phone,temp_campus,password,encrypt,create_time,is_audit)values(#{param.temp_name},#{param.temp_sex},#{param.temp_type},#{param.temp_identity},#{param.temp_dept},#{param.temp_post},#{param.temp_number},#{param.temp_boss_name},#{param.temp_boss_phone},#{param.temp_email},#{param.temp_mobile},#{param.temp_phone},#{param.temp_campus},#{param.password},#{param.encrypt},#{param.create_time},'0')",
					param);
			if (insert == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加临时乘客【" + temp_name + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 删除临时乘客
	public String deleteTempCheck(String temp_identity) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);

		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_identity)) {
				map.put("message", "请选择要删除的临时乘客");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<String, String>();
			param.put("temp_identity", temp_identity);
			List<Map<String, Object>> tempList = commonDao
					.query("select temp_name from sbus_temp where temp_identity=#{param.temp_identity}", param);
			if (tempList.size() <= 0) {
				map.put("message", "临时乘客不存在或已删除");
				return new JSONObject(map).toString();
			}
			int deleteTemp = commonDao.execute("delete from sbus_temp where temp_identity=#{param.temp_identity}",
					param);
			if (deleteTemp <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除临时乘客【" + tempList.get(0).get("temp_name") + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 编辑用户审核
	public String updateTempCheck(String temp_name, String temp_type, String temp_sex, String temp_identity,
			String temp_dept, String temp_post, String temp_number, String temp_boss_name, String temp_boss_phone,
			String temp_email, String temp_mobile, String temp_phone, String temp_campus, String is_audit,
			String temp_identity_before) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("ticket")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_identity)) {
				map.put("message", "请输入临时乘客身份证号");
				return new JSONObject(map).toString();
			}
			if (temp_identity.length() > 20) {
				map.put("message", "请输入20位以内的临时乘客身份证号");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_name)) {
				map.put("message", "请输入临时乘客姓名");
				return new JSONObject(map).toString();
			}
			if (temp_name.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客姓名");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_sex) || !"1".equals(temp_sex) && !"2".equals(temp_sex)) {
				map.put("message", "请选择临时乘客性别");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_mobile)) {
				map.put("message", "请输入临时乘客手机号");
				return new JSONObject(map).toString();
			}
			if (temp_mobile.length() > 15) {
				map.put("message", "请输入15位以内的临时乘客手机号");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(temp_type) || !"1".equals(temp_type) && !"2".equals(temp_type)) {
				map.put("message", "请选择临时乘客类型");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(is_audit) || !"0".equals(is_audit) && !"1".equals(is_audit)) {
				map.put("message", "请选择审核状态");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_campus) && temp_campus.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客所属校区");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_dept) && temp_dept.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客工作部门");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_post) && temp_post.length() > 10) {
				map.put("message", "请输入10位以内的临时乘客工作岗位");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_number) && temp_number.length() > 20) {
				map.put("message", "请输入20位以内的临时乘客工号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_email) && temp_email.length() > 50) {
				map.put("message", "请输入50位以内的临时乘客邮箱");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_boss_name) && temp_boss_name.length() > 10) {
				map.put("message", "请输入10位以内的直属领导姓名");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(temp_boss_phone) && temp_boss_phone.length() > 15) {
				map.put("message", "请输入15位以内的直属领导电话");
				return new JSONObject(map).toString();
			}
			Map<String, String> param = new HashMap<>();
			param.put("temp_identity", temp_identity);
			if (!temp_identity.equals(temp_identity_before)) {

				List<Map<String, Object>> ticketList = commonDao
						.query("select temp_identity from sbus_temp where temp_identity=#{param.temp_identity}", param);
				if (ticketList.size() >= 1) {
					map.put("message", "临时乘客身份证号已存在");
					return new JSONObject(map).toString();
				}
			}
			param.put("temp_name", temp_name);
			param.put("temp_type", temp_type);
			param.put("temp_sex", temp_sex);
			param.put("temp_dept", temp_dept);
			param.put("temp_post", temp_post);
			param.put("temp_number", temp_number);
			param.put("temp_boss_name", temp_boss_name);
			param.put("temp_boss_phone", temp_boss_phone);
			param.put("temp_email", temp_email);
			param.put("temp_mobile", temp_mobile);
			param.put("temp_phone", temp_phone);
			param.put("temp_campus", temp_campus);
			param.put("is_audit", is_audit);
			param.put("temp_identity_before", temp_identity_before);
			int update = commonDao.execute(
					"update sbus_temp set temp_identity=#{param.temp_identity},temp_name=#{param.temp_name},temp_type=#{param.temp_type},temp_sex=#{param.temp_sex},temp_dept=#{param.temp_dept},temp_post=#{param.temp_post},temp_number=#{param.temp_number},temp_boss_name=#{param.temp_boss_name},temp_boss_phone=#{param.temp_boss_phone},temp_email=#{param.temp_email},temp_mobile=#{param.temp_mobile},temp_phone=#{param.temp_phone},temp_campus=#{param.temp_campus},is_audit=#{param.is_audit} where temp_identity=#{param.temp_identity_before}",
					param);
			if (update <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 编辑成功
			map.put("message", "操作成功");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 票款统计-数据查询
	public String showAccountCheck(int limit, int page, String startDate, String endDate, String driver_name,
			String shifts_number, String line_name, String up_origin_name, String vehicle_number) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("ticket")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
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
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			int count = ticketDao.getAccountCheckCount(columnMap);
			List<Map<String, Object>> accountCheckList = new ArrayList<Map<String, Object>>();
			if (count > 0) {
				accountCheckList = ticketDao.getAccountCheckListByLimit(columnMap, (page - 1) * limit, limit);
			}
			map.put("msg", "获取成功");
			map.put("code", 0);
			map.put("count", count);
			map.put("data", accountCheckList);
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("msg", "系统错误");
			map.put("code", 1);
		}
		return new JSONObject(map).toString();
	}

	// 票款统计导出为Excel
	public String exportExcel(String startDate, String endDate, String driver_name, String shifts_number,
			String line_name, String up_origin_name, String vehicle_number, HttpServletResponse response) {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("success", false);
		try {
			if (!PublicService.isRole("ticket")) {
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
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			if (!StringUtils.isEmpty(shifts_number)) {
				columnMap.put("shifts_number", shifts_number);
			}
			if (!StringUtils.isEmpty(up_origin_name)) {
				columnMap.put("up_origin_name", up_origin_name);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			List<Map<String, Object>> accountCheckList = ticketDao.getAccountCheckList(columnMap);
			List<int[]> mergeList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
			String time = sdf.format(new Date());
			String[] strExcTitle = { "日  期", "班  次", "线  路", "发车时间", "始发站", "预计到达时间", "终点站", "司  机", "车  辆", "教工数量",
					"学生数量", "票 款" };
			String[] strExcCols = { "shifts_date", "shifts_number", "line_name", "depart_time", "up_origin_name",
					"arrive_time", "up_terminal_name", "driver_name", "vehicle_number", "teacher_num", "student_num",
					"total_fare" };
			int[] colsWidth = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
			SXSSFWorkbook wb = ExcelUtil.generateExcel("sheet1", strExcTitle, strExcCols, colsWidth, accountCheckList,
					mergeList, false);
			String fileName = time + ".xlsx"; // 格式：登录者帐号_年月日时分秒
			ExcelUtil.excelDownload(getResponse(response), fileName, wb);
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
