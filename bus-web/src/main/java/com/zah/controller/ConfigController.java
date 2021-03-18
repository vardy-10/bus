package com.zah.controller;

import com.zah.service.impl.ConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JIAAIBAO
 *
 */
@Controller
@RequestMapping("/manage")
public class ConfigController {
	@Autowired
	ConfigServiceImpl configServiceImpl;
	/**
	 * 配置信息集合（开机启动时必须赋值，修改时必须更新）
	 */
	public static Map<String, Object> configMap = new HashMap<>();

	@RequestMapping("schoolBusConfigPage")
	public String schoolBusConfigPage(Model model) {
		return configServiceImpl.schoolBusConfigPage(model);
	}

	@RequestMapping("ticketConfigPage")
	public String ticketConfigPage(Model model) {
		return configServiceImpl.ticketConfigPage(model);
	}

	@RequestMapping("schoolBusConfig")
	@ResponseBody
	public String schoolBusConfig(String vehicle_wait_minute, String depart_delay_minute) {
		return configServiceImpl.schoolBusConfig(vehicle_wait_minute, depart_delay_minute);
	}

	@RequestMapping("ticketConfig")
	@ResponseBody
	public String ticketConfig(String seat_retain_num, String ticket_student_price, String ticket_teacher_price,
			String ticket_student_hour, String ticket_teacher_hour, String ticket_all_minute,
			String ticket_refund_minute, String ticket_open_minute, String ticket_stop_minute) {
		return configServiceImpl.ticketConfig(seat_retain_num, ticket_student_price, ticket_teacher_price,
				ticket_student_hour, ticket_teacher_hour, ticket_all_minute, ticket_refund_minute, ticket_open_minute,
				ticket_stop_minute);
	}

}
