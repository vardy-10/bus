package com.zah.service.impl;

import com.zah.dao.CommonDao;
import com.zah.entity.MqMessage;
import com.zah.service.BuyTicketService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BuyTicketServiceImpl implements BuyTicketService {

	@Autowired
	OrderServiceImpl orderServiceImpl;

	@Autowired
	CommonDao commonDao;

	@Override
	@Transactional
	public Map<String, Object> butTicket(int type, double price, String shifts_number, String shifts_date, int authType,
			int passengerId, String appId, String mchId, String url) throws Exception {

		return null;
	}

	@Autowired
	RabbitTemplate rabbitTemplate;
	@Override
	public Map<String, Object> butTicket2(int type, double price, String shifts_number, String shifts_date, int authType, int passengerId, String appId, String mchId, String url) {
		Map<String, String> param = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<>();
		param.put("shifts_number", shifts_number);
		param.put("shifts_date", shifts_date);
		// 学生
		int result;
		if (type == 2) {
			String sql = "update sbus_shifts_real set student_num = (student_num + 1) where shifts_number=#{param.shifts_number} AND shifts_date=#{param.shifts_date}";
			result = commonDao.execute(sql, param);
		} else {
			String sql = "update sbus_shifts_real set teacher_num = (teacher_num + 1) where shifts_number=#{param.shifts_number} AND shifts_date=#{param.shifts_date}";
			result = commonDao.execute(sql, param);
		}
		if (result <= 0) {
			map.put("orderId", "0");
			return map;
		}
		Map<String, Object> setMap = new HashMap<>();
		long time = System.currentTimeMillis();
		long orderId = Long.parseLong(time + "" + (new Random().nextInt(900) + 100));
		map.put("orderId", orderId);
		setMap.put("order_id", orderId);
		setMap.put("price", price);
		setMap.put("passenger_type", type);
		setMap.put("auth_type", authType);
		setMap.put("passenger_id", passengerId);
		setMap.put("shifts_date", shifts_date);
		setMap.put("shifts_number", shifts_number);
		setMap.put("create_time", time / 1000);

		result = orderServiceImpl.insert(setMap);
		if (result <= 0) {
			throw new RuntimeException("发生异常！");
		}
		MqMessage mqMessage=new MqMessage();
		mqMessage.setOrderId(orderId+"");
		mqMessage.setShiftsDate(shifts_date);
		mqMessage.setShiftsNumber(shifts_number);
		//发送延迟消息
		String msgId = UUID.randomUUID().toString();
		rabbitTemplate.convertAndSend("seat-event-exchange","seat.lock",mqMessage,new CorrelationData(msgId));

		return map;
	}

	@Override
	public int refund(long orderId, String refundSn, long refundTime) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("refundSn", refundSn);
		param.put("refundTime", refundTime + "");
		param.put("orderId", orderId + "");
		String sql = "update sbus_order set refund_sn = #{param.refundSn}, refund_time = #{param.refundTime}, order_state = 2 where order_id=#{param.orderId} AND order_state = 5";

		return commonDao.execute(sql, param);
	}

	@Transactional
	public boolean updateOrderState(Map<String, Object> map, String orderId) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("orderId", orderId);
		int result;
		String sqls = "update sbus_order set order_state = 4 where order_id = #{param.orderId} and order_state = 0";
		result = commonDao.execute(sqls, param);
		if (result == 0) {
			throw new RuntimeException("发生异常！");
		}
		param.put("shifts_number", map.get("shifts_number").toString());
		param.put("shifts_date", map.get("shifts_date").toString());
		// 学生
		if ("2".equals(map.get("passenger_type").toString())) {
			String sql = "update sbus_shifts_real set student_num = (student_num - 1) where shifts_number=#{param.shifts_number} AND shifts_date=#{param.shifts_date}";
			result = commonDao.execute(sql, param);
		} else {
			String sql = "update sbus_shifts_real set teacher_num = (teacher_num - 1) where shifts_number=#{param.shifts_number} AND shifts_date=#{param.shifts_date}";
			result = commonDao.execute(sql, param);
		}
		if (result == 0) {
			throw new RuntimeException("发生异常！");
		}

		return true;
	}

	@Override
	public int cancelRefund(String orderId) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("orderId", orderId);
		String sql = "update sbus_order set order_state = 1 where order_id=#{param.orderId} AND order_state = 5";

		return commonDao.execute(sql, param);
	}
}
