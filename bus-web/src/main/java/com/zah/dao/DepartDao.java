package com.zah.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DepartDao {
	public int checkDriverIsExist(@Param("columns") String columns, @Param("table") String table,
                                  @Param("key") String key, @Param("value") String value, @Param("time") int time);
}
