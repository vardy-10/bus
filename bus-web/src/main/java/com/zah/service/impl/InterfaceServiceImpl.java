package com.zah.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zah.dao.CommonDao;
import com.zah.service.InterfaceService;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterfaceServiceImpl implements InterfaceService {

	@Autowired
	CommonDao commonDao;

	@Override
	public String updateVehicleT2Info(Map<String, String> paramMap) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Result", 0);
		try {
			// int Nowtime = Function.getInstance().getNowTimestamp();
			// if (Nowtime - Integer.parseInt(paramMap.get("device_time")) > 10) {
			// return null;
			// }
			// -device_type(传T2)、车载设备编号-device_number、设备记录时间-device_time（年月日时分秒）、设备经度-device_lng、设备纬度-device_lat、移动速度-speed（单位：km/h）
			if (!paramMap.containsKey("device_type") || !paramMap.containsKey("device_number")
					|| !paramMap.containsKey("device_time") || !paramMap.containsKey("device_lng")
					|| !paramMap.containsKey("device_lat")) {
				jsonObject.put("msg", "data loss");
				return jsonObject.toString();
			}
			if (!paramMap.get("device_type").equals("T2")) {
				Start.projectLog.writeInfo("更新车辆位置接口收到无效数据：" + paramMap.get("device_type"));
				jsonObject.put("msg", "invalid data");
				return jsonObject.toString();
			}
			if (!ValidateUtil.getInstance().isNumber(paramMap.get("device_lng"))
					|| !ValidateUtil.getInstance().isNumber(paramMap.get("device_lat"))
					|| Double.parseDouble(paramMap.get("device_lng")) == 0
					|| Double.parseDouble(paramMap.get("device_lat")) == 0) {
				Start.projectLog
						.writeInfo("更新车辆位置接口收到无效数据：" + paramMap.get("device_lng") + "||" + paramMap.get("device_lat"));
				jsonObject.put("msg", "invalid data");
				return jsonObject.toString();
			}
			long time = Function.getInstance().timeStrToSeconds(paramMap.get("device_time"), "yyyy-MM-dd HH:mm:ss");
			if (time == -1) {
				Start.projectLog.writeInfo("更新车辆位置接口收到无效数据：" + paramMap.get("device_time"));
				jsonObject.put("msg", "invalid data");
				return jsonObject.toString();
			}
			Long NowTime = System.currentTimeMillis() / 1000;
			String timeString = Function.getInstance().timestampToStr(NowTime, "yyyy-MM-dd HH:mm:ss");
			if (time > NowTime) {
				Start.projectLog.writeInfo("更新车辆位置接口收到无效数据：" + paramMap.get("device_time") + ">" + timeString);
				jsonObject.put("msg", "invalid data");
				return jsonObject.toString();
			}

			Map<String, String> param = new HashMap<>();
			param.put("device1_number", paramMap.get("device_number"));
			String sql = "SELECT vehicle_id,log_time FROM sbus_vehicle WHERE device1_number=#{param.device_number}";

			List<Map<String, Object>> busVehicleList = commonDao.query(sql, param);
			if (busVehicleList.size() <= 0) {
				Start.projectLog.writeInfo("未知设备：" + paramMap.get("device_number"));
				jsonObject.put("msg", "unknown device");
				return jsonObject.toString();
			}
			if (Long.valueOf(String.valueOf(busVehicleList.get(0).get("log_time"))) > time) {
				jsonObject.put("Result", 1);
				return jsonObject.toString();
			}
			sql = "UPDATE sbus_vehicle SET log_time=#{param.log_time},longitude=#{param.longitude},latitude=#{param.latitude} WHERE vehicle_id=#{param.vehicle_id}";
			param.put("log_time", String.valueOf(time));
			param.put("longitude", paramMap.get("device_lng"));
			param.put("latitude", paramMap.get("device_lat"));
			param.put("vehicle_id", String.valueOf(busVehicleList.get(0).get("vehicle_id")));
			int result = commonDao.execute(sql, param);
			sql = "INSERT into sbus_vehicle_position  SET log_time=#{param.log_time},longitude=#{param.longitude},latitude=#{param.latitude} ,vehicle_id=#{param.vehicle_id}";
			commonDao.execute(sql, param);
			if (result > 0) {
				jsonObject.put("Result", 1);
			} else {
				jsonObject.put("msg", "execute fail");
			}
			return jsonObject.toString();
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			jsonObject.put("msg", "execute fail");
			return jsonObject.toString();
		}
	}

	@Override
	public int updateVehiclePlaceInfo(String machineId, String lng, String lat, String date) {
		if (!ValidateUtil.getInstance().isNumber(lng) || !ValidateUtil.getInstance().isNumber(lat)
				|| Double.parseDouble(lat) == 0 || Double.parseDouble(lng) == 0) {
			Start.projectLog.writeInfo("更新车辆位置接口收到无效数据：" + lng + "||" + lat);
			return 0;
		}
		long time = Function.getInstance().timeStrToSeconds(date, "yyyy-MM-dd HH:mm:ss");
		System.out.println("转换时间:" + time);
		if (time <= 0) {
			Start.projectLog.writeInfo("更新车辆位置接口收到无效数据：" + date);
			return 0;
		}
		Map<String, String> param = new HashMap<>();
		param.put("device2_number", machineId);
		param.put("lng", lng);
		param.put("lat", lat);
		param.put("log_time", time + "");
		String sql = "select vehicle_id from sbus_vehicle where device2_number = #{param.device2_number}";
		List<Map<String, Object>> vehicleList = commonDao.query(sql, param);
		if (vehicleList.size() <= 0) {
			Start.projectLog.writeInfo("未知验票仪：" + machineId);
			return 0;
		}
		sql = "UPDATE sbus_vehicle SET log_time=#{param.log_time},longitude=#{param.lng},latitude=#{param.lat} WHERE device2_number=#{param.device2_number} AND log_time < #{param.log_time}";
		commonDao.execute(sql, param);
		param.put("vehicle_id", vehicleList.get(0).get("vehicle_id").toString());
		sql = "INSERT into sbus_vehicle_position SET log_time=#{param.log_time},longitude=#{param.lng},latitude=#{param.lat} ,vehicle_id=#{param.vehicle_id}";
		commonDao.execute(sql, param);
		return 1;
	}

}
