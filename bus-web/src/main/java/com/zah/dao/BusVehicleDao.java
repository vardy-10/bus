package com.zah.dao;

import com.zah.entity.BusVehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusVehicleDao {
	public int getCountBy(@Param("columnMap") Map<String, String> columnMap);

	public List<BusVehicle> getBusVehicleList(@Param("columnMap") Map<String, String> columnMap,
                                              @Param("offset") int offset, @Param("size") int size);

	public int addBusVehicle(@Param("columnMap") Map<String, String> columnMap);

	public List<BusVehicle> selectInfoByKey(@Param("key") String key, @Param("value") String value,
											@Param("columns") String columns);

	public int deleteBusVehicle(String vehicle_id);

	public int updateVehicle(@Param("columnMap") Map<String, String> columnMap, @Param("key") String key,
                             @Param("value") String value);

	public int updateVehicleT2Info(@Param("busVehicle") BusVehicle busVehicle);// 更新车辆经纬度(T2)
	
	public BusVehicle getVehicle(@Param("vehicle_id") int vehicle_id);

}
