package com.zah.dao;


import com.zah.entity.BusLine;
import com.zah.entity.BusStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusLineDao {
	public int getCountBy(@Param("columnMap") Map<String, String> columnMap);

	public List<BusLine> getBusLineList(@Param("columnMap") Map<String, String> columnMap, @Param("offset") int offset,
										@Param("size") int size);

	public List<BusLine> selectInfoByKey(@Param("key") String key, @Param("value") String value,
                                         @Param("columns") String columns);

	public int deleteBusLine(String line_id);

	public int delStationByLineId(String line_id);

	public int addBusLine(@Param("line_name") String line_name, @Param("line_type") String line_type);

	public int editBusLine(@Param("line_name") String line_name, @Param("line_type") String line_type,
                           @Param("line_id") String line_id);

	public int updateLineInfo(String line_id);

	public int updateSbusLineBy(@Param("key") String key, @Param("value") String value,
                                @Param("line_id") String line_id);

	public int updateOriginName(@Param("origin_name") String origin_name, @Param("terminal_name") String terminal_name,
                                @Param("value") String value, @Param("line_id") String line_id);

	public List<BusStation> selectInfoByLineAndDir(@Param("line_id") String line_id,
												   @Param("station_dir") String station_dir, @Param("station_order") String station_order);

	public int updateLineBykey(@Param("columnMap") Map<String, String> columnMap, @Param("key") String key,
                               @Param("value") String value);

	public List<BusLine> selectLineList(@Param("columns") String columns);
}
