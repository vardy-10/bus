package com.zah.controller;


import com.zah.service.OrderService;
import com.zah.service.PublicService;
import com.zah.service.impl.TicketServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("manage")
public class TicketController {
	@Autowired
	TicketServiceImpl ticketServiceImpl;



	@Autowired
	PublicService publicService;

	@Autowired
	OrderService orderService;

	@RequestMapping("tempCheckShow")
	public String tempCheckShow(Model model) {
		return ticketServiceImpl.tempCheckShow(model);
	}

	@RequestMapping("busOrderShow")
	public String busOrderShow(Model model) {
		return ticketServiceImpl.busOrderShow(model);
	}

	@RequestMapping("busAccountCheckShow")
	public String busAccountCheckShow(Model model) {
		return ticketServiceImpl.busAccountCheckShow(model);
	}

	@RequestMapping("tempCheckAdd")
	public String tempCheckAdd(Model model) {
		return ticketServiceImpl.tempCheckAdd(model);
	}

	@RequestMapping("tempCheckUpdate")
	public String tempCheckUpdate(Model model, String temp_identity) {
		return ticketServiceImpl.tempCheckUpdate(model, temp_identity);
	}

	@RequestMapping("tempCheckShowOne")
	public String tempCheckShowOne(Model model, String temp_identity) {
		return ticketServiceImpl.tempCheckShowOne(model, temp_identity);
	}

	@RequestMapping("showTempCheck")
	@ResponseBody
	public String showTempCheck(int limit, int page, String date, String is_audit, String temp_name, String temp_type,
			String temp_dept, String temp_number, String temp_post, String temp_mobile, String temp_boss_name) {
		return ticketServiceImpl.showTempCheck(limit, page, date, is_audit, temp_name, temp_type, temp_dept,
				temp_number, temp_post, temp_mobile, temp_boss_name);
	}

	@RequestMapping("addTempCheck")
	@ResponseBody
	public String addTempCheck(String temp_name, String temp_type, String temp_sex, String temp_identity,
			String temp_dept, String temp_post, String temp_number, String temp_boss_name, String temp_boss_phone,
			String temp_email, String temp_mobile, String temp_phone, String temp_campus, String password,
			String is_audit) {
		return ticketServiceImpl.addTempCheck(temp_name, temp_type, temp_sex, temp_identity, temp_dept, temp_post,
				temp_number, temp_boss_name, temp_boss_phone, temp_email, temp_mobile, temp_phone, temp_campus,
				password, is_audit);
	}

	@RequestMapping("deleteTempCheck")
	@ResponseBody
	public String deleteTempCheck(String temp_identity) {
		return ticketServiceImpl.deleteTempCheck(temp_identity);
	}

	@RequestMapping("showOneTempCheck")
	@ResponseBody
	public String showOneTempCheck(String temp_identity) {
		return ticketServiceImpl.deleteTempCheck(temp_identity);
	}

	@RequestMapping("updateTempCheck")
	@ResponseBody
	public String updateTempCheck(String temp_name, String temp_type, String temp_sex, String temp_identity,
			String temp_dept, String temp_post, String temp_number, String temp_boss_name, String temp_boss_phone,
			String temp_email, String temp_mobile, String temp_phone, String temp_campus, String is_audit,
			String temp_identity_before) {
		return ticketServiceImpl.updateTempCheck(temp_name, temp_type, temp_sex, temp_identity, temp_dept, temp_post,
				temp_number, temp_boss_name, temp_boss_phone, temp_email, temp_mobile, temp_phone, temp_campus,
				is_audit, temp_identity_before);
	}

	@RequestMapping("showBusOrder")
	@ResponseBody
	public String showBusOrder(int limit, int page, String shifts_date, String startTime, String endTime,
			String passenger_id, String shifts_number, String line_name, String up_origin_name, String order_id,
			String order_sn, String refund_sn, String order_state, String passenger_type) {
		return ticketServiceImpl.showBusOrder(limit, page, shifts_date, startTime, endTime, passenger_id, shifts_number,
				up_origin_name, order_id, order_sn, refund_sn, order_state, passenger_type, line_name);
	}

	@RequestMapping("showAccountCheck")
	@ResponseBody
	public String showAccountCheck(int limit, int page, String startDate, String endDate, String driver_name,
			String shifts_number, String line_name, String up_origin_name, String vehicle_number) {
		return ticketServiceImpl.showAccountCheck(limit, page, startDate, endDate, driver_name, shifts_number,
				line_name, up_origin_name, vehicle_number);
	}

	@RequestMapping("exportExcel")
	@ResponseBody
	public String exportExcel(String startDate, String endDate, String driver_name, String shifts_number,
			String line_name, String up_origin_name, String vehicle_number, HttpServletResponse response) {
		return ticketServiceImpl.exportExcel(startDate, endDate, driver_name, shifts_number, line_name, up_origin_name,
				vehicle_number, response);
	}

	/**
	 * 退款
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping("refund")
	@ResponseBody
	public String refund(long orderId) {
	/*	Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("success", false);
		if (!PublicService.isRole("ticket")) {
			messageMap.put("message", "无权限");
			return new JSONObject(messageMap).toString();
		}
		Map<String, Object> postData = new TreeMap<String, Object>();
		postData.put("app_id", WxController.APP_ID);
		postData.put("mch_id", WxController.MCH_ID);
		postData.put("format", "JSON");
		postData.put("version", "1");
		postData.put("sign_method", "RSA2");
		postData.put("sign_v", "1");
		postData.put("timestamp", CCPlayUtils.getInstance().getUTC());
		postData.put("sign_nonce", UUID.randomUUID().toString().replaceAll("-", ""));
		postData.put("method", "refund");
		postData.put("target_refund_id", orderId);
		postData.put("target_order_id", orderId);
		String sign = "";
		try {
			sign = CCPlayUtils.getInstance().getSign(postData);
			postData.put("sign", sign);
			String requestResult;
			requestResult = com.shbai.wx.util.Function.getInstance().request(WxController.PAY_URL, null, null, null,
					null, postData, null, "UTF-8", 0, 0);
			System.out.println("退款:" + requestResult);
			if (null != requestResult) {
				JSONObject jsonObject = JSONObject.parseObject(requestResult);
				if ("200".equals(jsonObject.get("code"))) {
					String refundResult = queryRefund(orderId);
					if (null != refundResult) {
						JSONObject refundJson = JSONObject.parseObject(refundResult);
						if ("200".equals(refundJson.get("code"))) {
							JSONObject data = JSONObject.parseObject(refundJson.get("data").toString());
							if ("SUCCESS".equals(data.get("status"))) {
								int num = buyTicketServiceImpl.refund(orderId, data.getString("refund_id"),
										System.currentTimeMillis() / 1000);
								if (num > 0) {
									messageMap.put("success", true);
									messageMap.put("message", "退款成功");
									publicService.addLog("退款订单,订单号：" + orderId);
								} else {
									messageMap.put("success", false);
									messageMap.put("message", "退款失败");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Start.projectLog.writeInfo("退款接口出现异常");
			e.printStackTrace();
			messageMap.put("success", false);
			messageMap.put("message", "退款失败");
		}
		return new JSONObject(messageMap).toJSONString();*/
	return  "";
	}

	/**
	 * 取消退款
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping("cancelRefund")
	@ResponseBody
	public String cancelRefund(String orderId) {
	/*	Map<String, Object> messageMap = new HashMap<String, Object>();
		if (!PublicService.isRole("ticket")) {
			messageMap.put("success", false);
			messageMap.put("message", "无权限");
			return new JSONObject(messageMap).toString();
		}
		Order order = orderService.getOrder(orderId);
		if (order == null) {
			messageMap.put("success", false);
			messageMap.put("message", "该订单不存在");
		}
		if (order.getOrder_state() != 5) {
			messageMap.put("success", false);
			messageMap.put("message", "只有申请退款的订单才能取消退款");
		}
		int num = buyTicketServiceImpl.cancelRefund(orderId);
		if (num > 0) {
			messageMap.put("success", true);
			messageMap.put("message", "取消成功");
			publicService.addLog("取消订单,订单号：" + orderId);
		} else {
			messageMap.put("success", false);
			messageMap.put("message", "取消失败");
		}*/
		return "";
	}

	/**
	 * 退款查询
	 * 
	 * @param refundId
	 * @return
	 */
	public String queryRefund(long refundId) {
	/*	Map<String, Object> postData = new TreeMap<String, Object>();
		postData.put("app_id", WxController.APP_ID);
		postData.put("mch_id", WxController.MCH_ID);
		postData.put("format", "JSON");
		postData.put("version", "1");
		postData.put("sign_method", "RSA2");
		postData.put("sign_v", "1");
		postData.put("timestamp", CCPlayUtils.getInstance().getUTC());
		postData.put("sign_nonce", System.currentTimeMillis() + "" + new Random().nextInt(100));
		postData.put("method", "refund_query");
		postData.put("target_refund_id", refundId);
		String sign = "";
		String result = "";
		try {
			sign = CCPlayUtils.getInstance().getSign(postData);
			postData.put("sign", sign);
			result = com.shbai.wx.util.Function.getInstance().request(WxController.PAY_URL, null, null, null, null,
					postData, null, "UTF-8", 0, 0);
			System.out.println("退款查询:" + result);
		} catch (Exception e) {
			Start.projectLog.writeInfo("退款查询接口出现异常");
			e.printStackTrace();
		}*/
		return "";
	}

}
