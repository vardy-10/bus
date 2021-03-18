package com.zah.dao;

import com.zah.entity.BusDriver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartRealDao {
	public int checkDriverIsExist(@Param("columns") String columns, @Param("table") String table,
                                  @Param("key") String key, @Param("value") String value, @Param("time") int time);

	public List<BusDriver> select(@Param("otherMap") Map<String, String> otherMap,
								  @Param("whereMap") Map<String, String> whereMap);

	public int insert(@Param("setMap") Map<String, String> setMap);

	public int update(@Param("setMap") Map<String, String> setMap, @Param("whereMap") Map<String, String> whereMap);

	public int delete(@Param("whereMap") Map<String, String> whereMap);

}
