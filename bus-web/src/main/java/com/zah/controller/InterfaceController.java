package com.zah.controller;

import com.alibaba.fastjson.JSONObject;
import com.zah.entity.Order;
import com.zah.service.impl.InterfaceServiceImpl;
import com.zah.service.impl.OrderServiceImpl;
import com.zah.service.impl.WxShiftsServiceImp;
import com.zah.thread.Start;
import com.zah.util.Function;
import org.json.JSONStringer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * html接口 （公交系统公用）
 * 
 * @author 54766
 * @version 1.0
 */
@RestController
@RequestMapping("Interface")
public class InterfaceController {
	@Autowired
	InterfaceServiceImpl interfaceService;

	@Autowired
	OrderServiceImpl orderServiceImpl;

	@Autowired
	WxShiftsServiceImp wxShiftsServiceImp;

	/**
	 * 更新车辆位置(接收T2数据)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updateBusLocation", method = RequestMethod.POST)
	public String busInterface(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> paramMap = new HashMap<String, String>();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Result", 0);
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			paramMap.put(entry.getKey(), entry.getValue()[0]);
		}
		Map<String, Object> resultMap = requestEncoding(paramMap, "decode");
		if ((boolean) resultMap.get("result") == false) {
			return "";
		}
		return interfaceService.updateVehicleT2Info(paramMap);
	}

	public static Map<String, Object> requestEncoding(Map<String, String> map, String type) {
		try {
			final String pass = "shbai=[8%p9s";
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (type.equals("encode")) {
				map = new TreeMap<String, String>(map);
				map.put("key", pass);
				JSONStringer json = new JSONStringer();
				json.object();
				for (String key : map.keySet()) {
					json.key(key);
					json.value(map.get(key));
				}
				json.endObject();
				String auth = Function.getInstance().SHA1AndMD5(json.toString(), "MD5");
				resultMap.put("result", true);
				resultMap.put("auth", auth);
				return resultMap;
			} else if (type.equals("decode")) {
				map = new TreeMap<String, String>(map);
				if (map.containsKey("auth")) {
					String auth = (String) map.get("auth");
					map.remove("auth");
					map.put("key", pass);
					JSONStringer json = new JSONStringer();
					json.object();
					for (String key : map.keySet()) {
						json.key(key);
						json.value(map.get(key));
					}
					json.endObject();
					String auth1 = Function.getInstance().SHA1AndMD5(json.toString(), "MD5");
					if (auth.equals(auth1)) {
						resultMap.put("result", true);
					} else {
						resultMap.put("result", false);
					}
				} else {
					resultMap.put("result", false);
				}
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 检票以及心跳检测接口
	 * 
	 * @return
	 */
	@RequestMapping("checkTicket")
	@ResponseBody
	public String CheckInInstrument(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter("action");
		Map<String, Object> result = new HashMap<>();
		Map<String, String> head = new HashMap<>();
		System.out.println("action:" + action);
		if ("monthcard.check_ticket".equals(action)) {
			// 检票
			Map<String, Object> data1 = new HashMap<>();
			Map<String, String> data2 = new HashMap<>();
			try {
				String machineId = request.getParameter("machineid");
				String code = request.getParameter("code");
				if (code == null) {
					Start.projectLog.writeInfo("无效车票：" + code);
					head.put("code", "1");
					head.put("message", "无效车票");
					data2.put("ticket_name", "无效车票");
					data2.put("status", "4");
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				}
				Order order = orderServiceImpl.getOrder(code);
				// 判断车票状态
				if (order == null) {
					Start.projectLog.writeInfo("无效车票：" + code);
					head.put("code", "1");
					head.put("message", "无效车票");
					data2.put("ticket_name", "无效车票");
					data2.put("status", "4");
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				} else if (order.getOrder_state() != 1) {
					if (order.getOrder_state() == 3) {
						head.put("code", "1");
						head.put("message", "该车票已检");
						data2.put("ticket_name", "该车票已检");
						data2.put("status", "5");
					} else {
						head.put("code", "1");
						head.put("message", "无效车票");
						data2.put("ticket_name", "无效车票");
						data2.put("status", "4");
					}
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				}
				// 判断是否到检票时间或者超过检票时间
				List<Map<String, Object>> list = wxShiftsServiceImp.getShiftsList(order.getShifts_number(),
						new SimpleDateFormat("yyyy-MM-dd").format(order.getShifts_date()));
				if (list.size() == 0) {
					Start.projectLog.writeInfo("*不应该发生的错误");
					head.put("code", "1");
					head.put("message", "未找到该班次信息");
					data2.put("ticket_name", "检票失败");
					data2.put("status", "2");
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				}
				Map<String, Object> map = list.get(0);
				long time = Function.getInstance().timeStrToSeconds(
						map.get("shifts_date").toString() + " " + map.get("depart_time").toString(),
						"yyyy-MM-dd HH:mm");
				time *= 1000;
				// 判断是否到检票时间
				int ticketOpenMinute = Integer
						.parseInt(ConfigController.configMap.get("ticket_open_minute").toString());
				if ((ticketOpenMinute * 60 * 1000 + System.currentTimeMillis()) < time) {
					head.put("code", "1");
					head.put("message", "未到检票时间");
					data2.put("ticket_name", "非本班次车票");
					data2.put("status", "3");
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				}
				// 判断是否停止检票
				int ticketStopMinute = Integer
						.parseInt(ConfigController.configMap.get("ticket_stop_minute").toString());
				if ((System.currentTimeMillis()) > time + ticketStopMinute * 60 * 1000) {
					head.put("code", "1");
					head.put("message", "已停止检票");
					data2.put("ticket_name", "非本班次车票");
					data2.put("status", "3");
					data1.put("data", data2);
					data1.put("message", "");
					data1.put("success", false);
					data1.put("status", "failed");
					result.put("body", data1);
					result.put("head", head);
					return new JSONObject(result).toJSONString();
				}
				orderServiceImpl.updateOrderState(order.getOrder_id(), 3, System.currentTimeMillis());
				head.put("code", "1");
				head.put("message", "检票成功");
				data2.put("ticket_name", "检票成功");
				data2.put("status", "1");
				data1.put("data", data2);
				data1.put("message", "");
				data1.put("success", true);
				data1.put("status", "success");
				result.put("body", data1);
				result.put("head", head);
				return new JSONObject(result).toJSONString();
			} catch (Exception e) {
				Start.projectLog.writeError(e);
				head.put("code", "1");
				head.put("message", "检票失败");
				data2.put("ticket_name", "检票失败");
				data2.put("status", "2");
				data1.put("data", data2);
				data1.put("message", "");
				data1.put("success", true);
				data1.put("status", "success");
				result.put("body", data1);
				result.put("head", head);
				return new JSONObject(result).toJSONString();
			}

		} else {
			// 更新经纬度
			String machineId = request.getParameter("machineid");
			String lng = request.getParameter("lng");
			String lat = request.getParameter("lat");
			String time = request.getParameter("time");
			int num = interfaceService.updateVehiclePlaceInfo(machineId, lng, lat, time);
			Map<String, Object> data1 = new HashMap<>();
			Map<String, String> data2 = new HashMap<>();
			if (num > 0) {
				head.put("code", "1");
				head.put("message", "更新成功");
				data2.put("ticket_name", "更新成功");
				data2.put("status", "1");
				data1.put("data", data2);
				data1.put("message", "");
				data1.put("status", "success");
				data1.put("success", true);
				result.put("body", data1);
				result.put("head", head);
			} else {
				head.put("code", "1");
				head.put("message", "更新失败");
				data2.put("ticket_name", "更新失败");
				data2.put("status", "2");
				data1.put("data", data2);
				data1.put("message", "");
				data1.put("status", "failed");
				data1.put("success", false);
				result.put("body", data1);
				result.put("head", head);
			}
			return new JSONObject(result).toJSONString();
		}
	}

}
