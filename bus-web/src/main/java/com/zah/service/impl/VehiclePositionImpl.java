package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.zah.dao.VehiclePositionDao;
import com.zah.entity.VehiclePosition;
import com.zah.service.PublicService;
import com.zah.service.VehiclePositionService;
import com.zah.thread.Start;
import com.zah.util.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VehiclePositionImpl implements VehiclePositionService {
	@Autowired
	VehiclePositionDao vehiclePositionDao;

	public String vehiclePositionShow(Model model) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}

		return "bus/busVehiclePositionShow";

	}

	public String showVehiclePosition(int limit, int page, String vehicle_number, String startTime, String endTime) {
		Map<String, Object> Map = new HashMap<String, Object>();

		try {
			if (!PublicService.isRole("schoolBus")) {
				Map.put("msg", "无权限");
				Map.put("code", 1);
				return new JSONObject(Map).toString();
			}
			Map<String, String> whereMap = new HashMap<String, String>();

			if (!StringUtils.isEmpty(vehicle_number)) {
				whereMap.put("vehicle_number", vehicle_number);
			}
			if (!StringUtils.isEmpty(startTime)) {
				long time = Function.getInstance().timeStrToSeconds(startTime, "yyyy-MM-dd HH:mm:ss");

				if (time == -1) {
					Map.put("message", "输入正确的开始时间！");
					return new JSONObject(Map).toString();
				}
				whereMap.put("startTime", String.valueOf(time));
			}
			if (!StringUtils.isEmpty(endTime)) {
				long time = Function.getInstance().timeStrToSeconds(endTime, "yyyy-MM-dd HH:mm:ss");
				if (time == -1) {
					Map.put("message", "输入正确的结束时间！");
					return new JSONObject(Map).toString();
				}
				whereMap.put("endTime", String.valueOf(time));
			}

			int count = vehiclePositionDao.getCountBy(whereMap);

			List<VehiclePosition> vehiclePositionList = new ArrayList<VehiclePosition>();
			if (count > 0) {
				vehiclePositionList = vehiclePositionDao.selectVehicle(whereMap, (page - 1) * limit, limit);
				Map.put("msg", "获取成功");
				Map.put("code", 0);
				Map.put("count", count);
				Map.put("data", vehiclePositionList);

			} else {
				Map.put("msg", "无数据");
				Map.put("code", 1);
			}

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			Map.put("msg", "系统错误");
			Map.put("code", 1);
			return new JSONObject(Map).toString();
		}
		JSONObject json = new JSONObject(Map);
		return json.toString();
	}

	public String showMap(Model model, String log_time) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (log_time == null) {
				model.addAttribute("message", "请选择要查看的车辆轨迹");
				return "admin/error";
			}

			Map<String, String> otherMap = new HashMap<String, String>();
			Map<String, String> whereMap = new HashMap<String, String>();
			otherMap.put("column", "longitude,latitude,log_time,vehicle_id");

			whereMap.put("log_time", log_time);
			List<VehiclePosition> vehiclePositionList = vehiclePositionDao.select(otherMap, whereMap);

			if (vehiclePositionList.size() <= 0) {
				model.addAttribute("message", "车辆轨迹不存在或已删除");
				return "admin/error";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date(Long.valueOf(vehiclePositionList.get(0).getLog_time() + "000")));
			model.addAttribute("time", time);
			model.addAttribute("vehiclePosition", vehiclePositionList.get(0));
			return "bus/busVehiclePositionShowOne";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

}
