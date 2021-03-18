package com.zah.dao;

import com.zah.entity.BusDriver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusDriverDao {
	public int getCountBy(@Param("columnMap") Map<String, String> columnMap);

	public List<BusDriver> getBusDriverList(@Param("columnMap") Map<String, String> columnMap,
											@Param("offset") int offset, @Param("size") int size);

	public List<BusDriver> selectInfoByKey(@Param("key") String key, @Param("value") String value,
                                           @Param("columns") String columns);

	public int deleteBusDriver(String driver_id);

	public List<BusDriver> select(@Param("otherMap") Map<String, String> otherMap,
                                  @Param("whereMap") Map<String, String> whereMap);

	public int insert(@Param("setMap") Map<String, String> setMap);

	public int update(@Param("setMap") Map<String, String> setMap, @Param("whereMap") Map<String, String> whereMap);

	public int delete(@Param("whereMap") Map<String, String> whereMap);



	
}
