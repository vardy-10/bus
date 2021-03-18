package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.BusLineDao;
import com.zah.dao.BusVehicleDao;
import com.zah.dao.CommonDao;
import com.zah.dao.VehiclePositionDao;
import com.zah.entity.BusLine;
import com.zah.entity.BusVehicle;
import com.zah.service.BusVehicleService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusVehicleServiceImpl implements BusVehicleService {
	@Autowired
	BusVehicleDao busVehicleDao;
	@Autowired
	PublicService publicService;
	@Autowired
	BusLineDao busLineDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	VehiclePositionDao vehiclePositionDao;

	public String busVehicleShow(Model model, String line_id) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}

		if (!StringUtils.isEmpty(line_id)) {
			model.addAttribute("line_id", line_id);
		}
		Map<String, String> param = new HashMap<>();
		List<Map<String, Object>> list = commonDao.query("select line_id,line_name from sbus_line", param);
		model.addAttribute("lineList", list);
		return "bus/busVehicleShow";
	}

	public String busVehicleAdd(Model model) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		List<BusLine> lineList = busLineDao.selectLineList("line_id,line_name");
		model.addAttribute("lineList", lineList);
		return "bus/busVehicleAdd";

	}

	public String busVehicleUpdate(Model model, String vehicle_id) {
		try {

			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			List<BusVehicle> busVehicleList = busVehicleDao.selectInfoByKey("vehicle_id", vehicle_id,
					"vehicle_id,line_id,vehicle_number,license_plate,seat_num,device1_number,device2_number");

			if (busVehicleList.size() <= 0) {
				model.addAttribute("message", "车辆不存在或已删除");
				return "admin/error";
			}

			BusVehicle busVehicle = busVehicleList.get(0);
			List<BusLine> lineList = busLineDao.selectLineList("line_id,line_name");
			model.addAttribute("lineList", lineList);
			model.addAttribute("busVehicle", busVehicle);

			return "bus/busVehicleUpdate";
		} catch (Exception e) {

			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	public String showBusVehicleList(int limit, int page, String vehicle_number, String license_plate, String line_id) {
		Map<String, Object> busVehicleMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				busVehicleMap.put("msg", "无权限");
				busVehicleMap.put("code", 1);
				return new JSONObject(busVehicleMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(license_plate)) {
				columnMap.put("license_plate", license_plate);
			}
			if (!StringUtils.isEmpty(vehicle_number)) {
				columnMap.put("vehicle_number", vehicle_number);
			}

			if (!StringUtils.isEmpty(line_id) && !line_id.equals("0")) {
				columnMap.put("line_id", line_id);
			}
			int count = busVehicleDao.getCountBy(columnMap);
			List<BusVehicle> busVehicleList = new ArrayList<BusVehicle>();

			if (count > 0) {
				busVehicleList = busVehicleDao.getBusVehicleList(columnMap, (page - 1) * limit, limit);
				busVehicleMap.put("msg", "获取成功");
				busVehicleMap.put("code", 0);
				busVehicleMap.put("count", count);
				busVehicleMap.put("data", busVehicleList);

			} else {
				busVehicleMap.put("msg", "无数据");
				busVehicleMap.put("code", 1);
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			busVehicleMap.put("msg", "系统错误");
			busVehicleMap.put("code", 1);

		}
		return new JSONObject(busVehicleMap).toString();

	}

	public String addBusVehicleList(String line_id, String vehicle_number, String license_plate, String seat_num,
			String device1_number, String device2_number) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (StringUtils.isEmpty(line_id)) {
				line_id = "0";
			}
			if (!ValidateUtil.getInstance().isDigit(line_id)) {
				map.put("message", "请选择车辆所属线路");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(vehicle_number) && vehicle_number.length() > 15) {
				map.put("message", "请输入15位以内的车辆编号");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(vehicle_number)) {
				map.put("message", "车辆编号不能为空");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(license_plate) && license_plate.length() > 15) {
				map.put("message", "请输入15位以内的车辆车牌");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(seat_num)) {
				seat_num = "0";
			}
			if (!ValidateUtil.getInstance().isDigit(seat_num)) {
				map.put("message", "请输入为正整数的车辆座位数");
				return new JSONObject(map).toString();
			}
			if (seat_num.length() > 3) {
				map.put("message", "请输入1000以内的车辆座位数");
				return new JSONObject(map).toString();
			}

			if (!StringUtils.isEmpty(device1_number)
					&& (device1_number.length() != 8 || !ValidateUtil.getInstance().isDigit(device1_number))) {
				map.put("message", "请输入8位正整数的客流计设备编号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(device2_number) && device2_number.length() > 10) {
				map.put("message", "请输入10位以内的售票仪设备编号");
				return new JSONObject(map).toString();
			}

			if (!StringUtils.isEmpty(device1_number)) {
				List<BusVehicle> list1 = busVehicleDao.selectInfoByKey("device1_number", device1_number, "vehicle_id");

				if (list1.size() > 0) {
					map.put("message", "客流计编号重复");
					return new JSONObject(map).toString();
				}
			} else {
				device1_number = null;
			}
			if (!StringUtils.isEmpty(device2_number)) {
				List<BusVehicle> list2 = busVehicleDao.selectInfoByKey("device2_number", device2_number, "vehicle_id");
				if (list2.size() > 0) {
					map.put("message", "售票仪编号重复");
					return new JSONObject(map).toString();
				}
			} else {
				device2_number = null;
			}
			List<BusVehicle> list3 = busVehicleDao.selectInfoByKey("vehicle_number", vehicle_number, "vehicle_number");
			if (list3.size() > 0) {
				map.put("message", "车辆编号重复");
				return new JSONObject(map).toString();
			}
			if (!line_id.equals("0")) {
				List<BusLine> LineList = busLineDao.selectInfoByKey("line_id", line_id, "line_id");
				if (LineList.size() <= 0) {
					map.put("message", "添加车辆的线路不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			columnMap.put("line_id", line_id);
			columnMap.put("vehicle_number", vehicle_number);
			columnMap.put("license_plate", license_plate);
			columnMap.put("seat_num", seat_num);
			columnMap.put("device1_number", device1_number);
			columnMap.put("device2_number", device2_number);
			int count = busVehicleDao.addBusVehicle(columnMap);
			if (count <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加车辆【" + vehicle_number + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	public String deleteBusVehicleList(String vehicle_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {

			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// 接收参数并判断
			if (vehicle_id == null) {
				map.put("message", "请选择要删除的车辆");
				return new JSONObject(map).toString();
			}
			List<BusVehicle> BusVehicleList = busVehicleDao.selectInfoByKey("vehicle_id", vehicle_id, "vehicle_number");
			if (BusVehicleList.size() <= 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vehicle_id", vehicle_id);
			paramMap.put("time", String.valueOf(Function.getInstance().getNowTimestamp()));
			List<Map<String, Object>> tmplList = commonDao.query(
					"select vehicle_id from sbus_shifts_tmpl where vehicle_id=#{param.vehicle_id} limit 1", paramMap);
			if (tmplList.size() > 0) {
				map.put("message", "排班模板中存在该车辆，请调整后再操作");
				return new JSONObject(map).toString();
			}
			List<Map<String, Object>> list_plan = commonDao.query(
					"select vehicle_id from sbus_shifts_plan where vehicle_id=#{param.vehicle_id} AND unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.time} limit 1",
					paramMap);
			if (list_plan.size() > 0) {
				map.put("message", "未来的排班计划中存在该车辆，请调整后再操作");
				return new JSONObject(map).toString();
			}
			
			List<Map<String, Object>> list_real = commonDao.query(
					"SELECT vehicle_id FROM sbus_shifts_real WHERE vehicle_id=#{param.vehicle_id} AND unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.time} limit 1",
					paramMap);
			if (list_real.size() > 0) {
				map.put("message", "未来的排班日程中存在该车辆，请调整后再操作");
				return new JSONObject(map).toString();
			}
			int result = busVehicleDao.deleteBusVehicle(vehicle_id);
			if (result == 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			map.put("success", true); // 删除成功
			map.put("message", "操作成功");
			publicService.addLog("删除车辆【" + BusVehicleList.get(0).getVehicle_number() + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String updateBusVehicle(String vehicle_id, String line_id, String vehicle_number, String license_plate,
			String seat_num, String device1_number, String device2_number) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (vehicle_id == null) {
				map.put("message", "请选择要编辑的车辆");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_id)) {
				line_id = "0";
			}
			if (!ValidateUtil.getInstance().isDigit(line_id)) {
				map.put("message", "请选择车辆所属线路");
				return new JSONObject(map).toString();
			}

			if (StringUtils.isEmpty(vehicle_number)) {
				map.put("message", "车辆编号不能为空");
				return new JSONObject(map).toString();

			}
			if (vehicle_number.length() > 15) {
				map.put("message", "请输入15位以内的车辆编号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(license_plate) && license_plate.length() > 15) {
				map.put("message", "请输入15位以内的车辆车牌");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(seat_num)) {
				seat_num = "0";
			}
			if (!ValidateUtil.getInstance().isDigit(seat_num)) {
				map.put("message", "请输入为正整数的车辆座位数");
				return new JSONObject(map).toString();
			}

			if (!StringUtils.isEmpty(device1_number)
					&& (device1_number.length() != 8 || !ValidateUtil.getInstance().isDigit(device1_number))) {
				map.put("message", "请输入8位正整数的客流计设备编号");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(device2_number) && device2_number.length() > 10) {
				map.put("message", "请输入10位以内的售票仪设备编号");
				return new JSONObject(map).toString();
			}

			List<BusVehicle> BusVehicleList = busVehicleDao.selectInfoByKey("vehicle_id", vehicle_id,
					"vehicle_number,seat_num");
			if (BusVehicleList.size() <= 0) {
				map.put("message", "车辆不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (!StringUtils.isEmpty(device1_number)) {
				List<BusVehicle> list1 = busVehicleDao.selectInfoByKey("device1_number", device1_number, "vehicle_id");
				if (list1.size() >= 1 && !String.valueOf(list1.get(0).getVehicle_id()).equals(vehicle_id)) {
					map.put("message", "客流计编号重复");
					return new JSONObject(map).toString();
				}
			} else {
				device1_number = null;
			}

			if (!StringUtils.isEmpty(device2_number)) {
				List<BusVehicle> list2 = busVehicleDao.selectInfoByKey("device2_number", device2_number, "vehicle_id");
				if (list2.size() >= 1 && !String.valueOf(list2.get(0).getVehicle_id()).equals(vehicle_id)) {
					map.put("message", "售票仪编号重复");
					return new JSONObject(map).toString();
				}
			} else {
				device2_number = null;
			}
			List<BusVehicle> list3 = busVehicleDao.selectInfoByKey("vehicle_number", vehicle_number, "vehicle_id");
			if (list3.size() >= 1 && !String.valueOf(list3.get(0).getVehicle_id()).equals(vehicle_id)) {
				map.put("message", "车辆编号重复");
				return new JSONObject(map).toString();
			}
			if (!line_id.equals("0")) {
				List<BusLine> LineList = busLineDao.selectInfoByKey("line_id", line_id, "line_id");
				if (LineList.size() <= 0) {
					map.put("message", "添加车辆的线路不存在或已删除");
					return new JSONObject(map).toString();
				}
			}
			Map<String, String> param = new HashMap<>();
			param.put("vehicle_id", vehicle_id);
			param.put("time", String.valueOf(System.currentTimeMillis() / 1000));
			List<Map<String, Object>> shiftsRealList = new ArrayList<>();
			if (BusVehicleList.get(0).getSeat_num() > Integer.parseInt(seat_num)) {
				shiftsRealList = commonDao.query(
						"select student_num,teacher_num from sbus_shifts_real where vehicle_id=#{param.vehicle_id} and total_fare > 0 and unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.time}",
						param);

				for (int i = 0; i < shiftsRealList.size(); i++) {
					int total_num = (int) shiftsRealList.get(0).get("student_num")
							+ (int) shiftsRealList.get(0).get("teacher_num");
					if (total_num > Integer.parseInt(seat_num)) {
						map.put("message", "编辑的车辆座位数小于该排班日程中已出售的票数");
						return new JSONObject(map).toString();
					}
				}
			}

			Map<String, String> columnMap = new HashMap<String, String>();
			columnMap.put("line_id", line_id);
			columnMap.put("vehicle_number", vehicle_number);
			columnMap.put("license_plate", license_plate);
			columnMap.put("seat_num", seat_num);
			columnMap.put("device1_number", device1_number);
			columnMap.put("device2_number", device2_number);
			int result = busVehicleDao.updateVehicle(columnMap, "vehicle_id", vehicle_id);
			if (result == 0) {
				map.put("message", "系统错误");
				throw new RuntimeException("发生异常！");
			}

			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("编辑车辆【" + BusVehicleList.get(0).getVehicle_number() + "】");
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	public String busVehicleShowOne(Model model, String vehicle_id) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			if (vehicle_id == null) {
				model.addAttribute("message", "请选择要查看的车辆");
				return "admin/error";
			}
			List<BusVehicle> oneVehicleList = busVehicleDao.selectInfoByKey("vehicle_id", vehicle_id,
					"line_id,vehicle_number,license_plate,seat_num,device1_number,device2_number");
			if (oneVehicleList.size() <= 0) {
				model.addAttribute("message", "车辆不存在或已删除");
				return "admin/error";
			}
			BusVehicle busVehicle = oneVehicleList.get(0);
			if (busVehicle.getLine_id() != 0) {
				List<BusLine> list = busLineDao.selectInfoByKey("line_id", String.valueOf(busVehicle.getLine_id()),
						"line_name");
				String lineName = list.get(0).getLine_name();
				model.addAttribute("lineName", lineName);
			} else {
				String lineName = "";
				model.addAttribute("lineName", lineName);
			}
			model.addAttribute("busVehicle", busVehicle);
			return "bus/busVehicleShowOne";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	@Override
	public String busVehicleShow(Model model) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public BusVehicle getVehicle(int vehicle_id) {
		return busVehicleDao.getVehicle(vehicle_id);
	}
	
	
	

}
