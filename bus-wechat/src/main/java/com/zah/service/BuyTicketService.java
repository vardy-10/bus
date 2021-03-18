package com.zah.service;

import java.util.Map;

public interface BuyTicketService {

	// 更新班次表的座位信息
	public Map<String, Object> butTicket(int type, double price, String shifts_number, String shifts_date, int authType,
                                         int passengerId, String appId, String mchId, String url) throws Exception;

	// 更新班次表的座位信息
	public Map<String, Object> butTicket2(int type, double price, String shifts_number, String shifts_date, int authType,
                                          int passengerId, String appId, String mchId, String url);

	// 退款
	public int refund(long orderId, String refundSn, long refundTime);

	// 取消退款
	public int cancelRefund(String orderId);

}
