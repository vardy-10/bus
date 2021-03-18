package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonDao {
	public List<Map<String, Object>> query(@Param("sql") String sql, @Param("param") Map<String, String> param);

	public int execute(@Param("sql") String sql, @Param("param") Map<String, String> param);

	public int update(@Param("sql") String sql, @Param("param") Map<String, String> param);

}
