package com.zah.dao;

import com.zah.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDao {
	public List<Order> getList(@Param("page") int page, @Param("size") int size, @Param("time") long time,
							   @Param("state") int state);

	public int insert(@Param("setMap") Map<String, Object> setMap);

	public int updateOrderSn(@Param("orderId") long orderId, @Param("orderSn") String orderSn);

	public int applicationForDrawback(@Param("orderId") String orderId);

	public Order getOrder(@Param("orderId") String orderId);

	public int updateOrderState(@Param("orderId") long orderId, @Param("state") int state,
                                @Param("useTime") long useTime);
}
