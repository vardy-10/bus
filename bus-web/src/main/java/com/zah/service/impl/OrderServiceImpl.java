package com.zah.service.impl;

import com.zah.dao.OrderDao;
import com.zah.entity.Order;
import com.zah.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderDao orderDao;

	@Override
	public List<Order> getList(int page, int size, long time, int status) {

		return orderDao.getList(page, size, time, status);
	}

	@Override
	public int insert(Map<String, Object> setMap) {

		return orderDao.insert(setMap);
	}

	@Override
	public int updateOrderSn(long orderId, String orderSn) {

		return orderDao.updateOrderSn(orderId, orderSn);
	}

	@Override
	public int applicationForDrawback(String orderId) {

		return orderDao.applicationForDrawback(orderId);
	}

	@Override
	public Order getOrder(String orderId) {

		return orderDao.getOrder(orderId);
	}

	@Override
	public int updateOrderState(long orderId, int state, long useTime) {

		return orderDao.updateOrderState(orderId, state, useTime);
	}

}
