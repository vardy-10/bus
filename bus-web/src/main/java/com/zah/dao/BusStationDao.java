package com.zah.dao;

import com.zah.entity.BusStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusStationDao {
	public int getCountBy(@Param("columnMap") Map<String, String> columnMap);

	public List<BusStation> selectInfoByKey(@Param("key") String key, @Param("value") String value,
											@Param("columns") String columns);

	public List<BusStation> getBusStationList(@Param("columnMap") Map<String, String> columnMap,
                                              @Param("offset") int offset, @Param("size") int size);

	public int deleteBusStation(String station_id);

	// 删除指定线路下下行车站信息
	public int delStationByLineIdandType(String line_id);

	public int getMaxOrder(@Param("line_id") String line_id, @Param("station_dir") String station_dir);

	public int addStation(@Param("columnMap") Map<String, String> columnMap);

	public List<BusStation> selectInfoByNameAndId(@Param("line_id") String line_id,
                                                  @Param("station_name") String station_name);

	public int deleteUpdateStationInfo(@Param("columnMap") Map<String, String> columnMap);

	public int addUpdateStationInfo(@Param("columnMap") Map<String, String> columnMap);

	public List<BusStation> selectInfoByLineAndDir(@Param("line_id") String line_id,
                                                   @Param("station_dir") String station_dir, @Param("station_order") String station_order);

	public List<BusStation> getFirstLastStationByColumn(@Param("columnMap") Map<String, String> columnMap);

	// 更新车站信息
	public int upOnAdd(@Param("BusStation") Map<String, String> busStation2);

	// 更新车站信息
	public int upOnRed(@Param("BusStation") Map<String, String> busStation1);

	// 编辑车站信息
	public int upStation(@Param("BusStation") Map<String, String> busStation2);



	public String selectInfoByLineAndOrder(@Param("line_id") String line_id,
                                           @Param("station_order") String station_order, @Param("station_dir") String station_dir,
                                           @Param("station_id") String station_id);


}
