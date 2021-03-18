package com.zah.service;

import com.zah.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
	public List<Order> getList(int page, int size, long time, int state);

	public int insert(Map<String, Object> setMap);

	public int updateOrderSn(long orderId, String orderSn);

	public int applicationForDrawback(String orderId);

	public Order getOrder(String orderId);

	public int updateOrderState(long orderId, int state, long useTime);

}
