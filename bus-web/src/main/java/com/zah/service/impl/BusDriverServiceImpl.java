package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.BusDriverDao;
import com.zah.dao.CommonDao;
import com.zah.dao.DepartPlanDao;
import com.zah.entity.BusDriver;
import com.zah.service.BusDriverService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusDriverServiceImpl implements BusDriverService {
	@Autowired
	BusDriverDao busDriverDao;
	@Autowired
	DepartPlanDao departPlanDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	PublicService publicService;

	public String busDriverShow(Model model) {
		if (!PublicService.isRole("school")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "bus/busDriverShow";
	}

	public String busDriverAdd(Model model) {
		if (!PublicService.isRole("school")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "bus/busDriverAdd";

	}

	public String busDriverUpdate(Model model, String driver_id) {
		try {
			if (!PublicService.isRole("school")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();

			otherMap.put("column", "driver_id,driver_number, driver_name, driver_sex, driver_phone, driver_identity");
			whereMap.put("driver_id", driver_id);

			List<BusDriver> BusDriverList = busDriverDao.select(otherMap, whereMap);
			if (BusDriverList.size() <= 0) {
				model.addAttribute("message", "司机不存在或已删除");
				return "admin/error";
			}
			BusDriver busDriver = BusDriverList.get(0);
			model.addAttribute("busDriver", busDriver);
			return "bus/busDriverUpdate";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	public String showBusDriver(int limit, int page, String driver_name, String driver_number) {
		Map<String, Object> busDriverMap = new HashMap<String, Object>();

		try {
			if (!PublicService.isRole("school")) {
				busDriverMap.put("msg", "无权限");
				busDriverMap.put("code", 1);
				return new JSONObject(busDriverMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(driver_name)) {
				columnMap.put("driver_name", driver_name);
			}
			if (!StringUtils.isEmpty(driver_number)) {
				columnMap.put("driver_number", driver_number);
			}
			int count = busDriverDao.getCountBy(columnMap);

			List<BusDriver> busDriverList = new ArrayList<BusDriver>();
			if (count > 0) {
				busDriverList = busDriverDao.getBusDriverList(columnMap, (page - 1) * limit, limit);
				busDriverMap.put("msg", "获取成功");
				busDriverMap.put("code", 0);
				busDriverMap.put("count", count);
				busDriverMap.put("data", busDriverList);
			} else {
				busDriverMap.put("msg", "无数据");
				busDriverMap.put("code", 1);
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			busDriverMap.put("msg", "系统错误");
			busDriverMap.put("code", 1);

		}
		return new JSONObject(busDriverMap).toString();

	}

	// 删除司机

	public String deleteBusDriver(String driver_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			// 权限判断
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// 接收提交并判断
			if (StringUtils.isEmpty(driver_id)) {
				map.put("message", "请选择要删除的司机");
				return new JSONObject(map).toString();
			}
			List<BusDriver> BusDriverList = busDriverDao.selectInfoByKey("driver_id", driver_id, "driver_number");
			if (BusDriverList.size() <= 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			otherMap.put("column", "driver_id");
			otherMap.put("limit", "1");
			Map<String, String> whereMap = new HashMap<String, String>();
			whereMap.put("driver_id", driver_id);
			whereMap.put("time", String.valueOf(Function.getInstance().getNowTimestamp()));
			List<Map<String, Object>> tmplList = commonDao.query(
					"select driver_id from sbus_shifts_tmpl where driver_id=#{param.driver_id} limit 1", whereMap);
			if (tmplList.size() > 0) {
				map.put("message", "排班模板中存在该司机，请调整后再操作");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> shiftsPlanList = commonDao.query(
					"select driver_id from sbus_shifts_plan where driver_id=#{param.driver_id} AND unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.time} limit 1",
					whereMap);
			if (shiftsPlanList.size() > 0) {
				map.put("message", "未来的排班计划中存在该司机，请调整后再操作");
				return new JSONObject(map).toString();
			}

			List<Map<String, Object>> shiftsRealList = commonDao.query(
					"SELECT driver_id FROM sbus_shifts_real WHERE driver_id=#{param.driver_id} AND unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.time} limit 1",
					whereMap);
			if (shiftsRealList.size() > 0) {
				map.put("message", "未来的排班日程中存在该司机，请调整后再操作");
				return new JSONObject(map).toString();
			}
			int result = busDriverDao.deleteBusDriver(driver_id);
			if (result <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除司机【" + BusDriverList.get(0).getDriver_number() + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 添加司机
	public String addBusDriver(String driver_number, String driver_name, String driver_sex, String driver_phone,
			String driver_identity) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> otherMap = new HashMap<String, String>();
		Map<String, String> whereMap = new HashMap<String, String>();
		Map<String, String> setMap = new HashMap<String, String>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_number)) {
				map.put("message", "司机编号不能为空");
				return new JSONObject(map).toString();
			}
			if (driver_number.length() > 20) {
				map.put("message", "请输入20位以内的司机编号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_name) && driver_name.length() > 10) {
				map.put("message", "请输入10位以内的司机姓名");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_sex) || !driver_sex.equals("1") && !driver_sex.equals("2")) {
				map.put("message", "请选择司机性别");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_phone) && driver_phone.length() > 30) {
				map.put("message", "请输入30位以内的司机电话");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_identity) && driver_identity.length() > 20) {
				map.put("message", "请输入20位以内的司机身份证号");
				return new JSONObject(map).toString();
			}

			otherMap.put("column", "driver_id");
			whereMap.put("driver_number", driver_number);
			List<BusDriver> BusDriverList = busDriverDao.select(otherMap, whereMap);
			if (BusDriverList.size() > 0) {
				map.put("message", "司机编号重复");
				return new JSONObject(map).toString();
			}

			setMap.put("driver_number", driver_number);
			setMap.put("driver_name", driver_name);
			setMap.put("driver_sex", driver_sex);
			setMap.put("driver_phone", driver_phone);
			setMap.put("driver_identity", driver_identity);
			int insert = busDriverDao.insert(setMap);
			if (insert <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();

			}
			map.put("success", true); // 添加成功
			map.put("message", "操作成功");
			publicService.addLog("添加司机【" + driver_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	// 更新司机
	public String updateBusDriver(String driver_id, String driver_number, String driver_name, String driver_sex,
			String driver_phone, String driver_identity) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("success", false);
		try {
			if (!PublicService.isRole("school")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (driver_id == null) {
				map.put("message", "请选择要编辑的司机");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_number)) {
				map.put("message", "司机编号不能为空");
				return new JSONObject(map).toString();
			}
			if (driver_number.length() > 20) {
				map.put("message", "请输入20位以内的司机编号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_name) && driver_name.length() > 10) {
				map.put("message", "请输入10位以内的司机姓名");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(driver_sex) || !driver_sex.equals("1") && !driver_sex.equals("2")) {
				map.put("message", "请选择司机性别");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_phone) && driver_phone.length() > 30) {
				map.put("message", "请输入30位以内的司机电话");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(driver_identity) && driver_identity.length() > 20) {
				map.put("message", "请输入20位以内的司机身份证号");
				return new JSONObject(map).toString();
			}
			List<BusDriver> BusDriverList2 = busDriverDao.selectInfoByKey("driver_id", driver_id, "driver_number");
			if (BusDriverList2.size() <= 0) {
				map.put("message", "司机不存在或已删除");
				return new JSONObject(map).toString();
			}
			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereSelectMap = new HashMap<String, String>();

			otherMap.put("column", "driver_id");
			whereSelectMap.put("driver_number", driver_number);
			List<BusDriver> BusDriverList = busDriverDao.select(otherMap, whereSelectMap);
			if (BusDriverList.size() > 0 && !String.valueOf(BusDriverList.get(0).getDriver_id()).equals(driver_id)) {
				map.put("message", "司机编号重复");
				return new JSONObject(map).toString();
			}
			Map<String, String> whereUpdateMap = new HashMap<String, String>();
			Map<String, String> setMap = new HashMap<String, String>();
			setMap.put("driver_number", driver_number);
			setMap.put("driver_name", driver_name);
			setMap.put("driver_sex", driver_sex);
			setMap.put("driver_phone", driver_phone);
			setMap.put("driver_identity", driver_identity);
			whereUpdateMap.put("driver_id", driver_id);
			int update = busDriverDao.update(setMap, whereUpdateMap);
			if (update <= 0) {
				map.put("message", "系统错误");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("编辑司机【" + BusDriverList2.get(0).getDriver_number() + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

}
