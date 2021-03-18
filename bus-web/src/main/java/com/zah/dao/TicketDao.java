package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TicketDao {

	public List<Map<String, Object>> getTempList(@Param("columnMap") Map<String, String> columnMap,
                                                 @Param("offset") int offset, @Param("size") int size);

	public int getTempCount(@Param("columnMap") Map<String, String> columnMap);

	public List<Map<String, Object>> getOrderList(@Param("columnMap") Map<String, String> columnMap,
                                                  @Param("offset") int offset, @Param("size") int size);

	public int getOrderCount(@Param("columnMap") Map<String, String> columnMap);

	public List<Map<String, Object>> getAccountCheckListByLimit(@Param("columnMap") Map<String, String> columnMap,
                                                                @Param("offset") int offset, @Param("size") int size);

	public int getAccountCheckCount(@Param("columnMap") Map<String, String> columnMap);

	public List<Map<String, Object>> getAccountCheckList(@Param("columnMap") Map<String, String> columnMap);
}
