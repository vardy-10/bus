package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperatePlanMonitorDao {
	public List<Map<String, Object>> getOperatePlanList(@Param("columnMap") Map<String, String> columnMap);

	public List<Map<String, Object>> getOperateRealList(@Param("columnMap") Map<String, String> columnMap);

	public List<Map<String, Object>> getOperateRealListByLimit(@Param("columnMap") Map<String, String> columnMap,
                                                               @Param("offset") int offset, @Param("size") int size);

	public int getOperateRealCount(@Param("columnMap") Map<String, String> columnMap);
}
