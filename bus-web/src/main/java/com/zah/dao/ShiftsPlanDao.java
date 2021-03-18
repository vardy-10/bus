package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShiftsPlanDao {
	public List<Map<String, Object>> getShiftsPlanList(@Param("columnMap") Map<String, String> columnMap,
                                                       @Param("offset") int offset, @Param("size") int size);

	public int getShiftsPlanCount(@Param("columnMap") Map<String, String> columnMap);
}
