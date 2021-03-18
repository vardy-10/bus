package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperateDataDao {
	public List<Map<String, Object>> getOperateDataListByLimit(@Param("columnMap") Map<String, String> columnMap,
                                                               @Param("offset") int offset, @Param("size") int size);

	public List<Map<String, Object>> getOperateDataList(@Param("columnMap") Map<String, String> columnMap);

	public int getOperateDataCount(@Param("columnMap") Map<String, String> columnMap);
}
