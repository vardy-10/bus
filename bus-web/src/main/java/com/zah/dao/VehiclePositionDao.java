package com.zah.dao;

import com.zah.entity.VehiclePosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VehiclePositionDao {
	public List<VehiclePosition> select(@Param("otherMap") Map<String, String> otherMap,
                                        @Param("whereMap") Map<String, String> whereMap);

	public int getCountBy(@Param("columnMap") Map<String, String> columnMap);

	public int insert(@Param("setMap") Map<String, String> setMap);

	public int update(@Param("setMap") Map<String, String> setMap, @Param("whereMap") Map<String, String> whereMap);

	public int delete(@Param("whereMap") Map<String, String> whereMap);

	public List<VehiclePosition> selectVehicle(@Param("columnMap") Map<String, String> columnMap,
											   @Param("offset") int offset, @Param("size") int size);

}
