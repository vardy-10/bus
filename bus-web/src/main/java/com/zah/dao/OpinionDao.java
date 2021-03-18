package com.zah.dao;

import com.zah.entity.Opinion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpinionDao {

	public List<Opinion> select(@Param("otherMap") Map<String, String> otherMap,
								@Param("whereMap") Map<String, String> whereMap);

	public int insert(@Param("setMap") Map<String, String> setMap);

	public int update(@Param("setMap") Map<String, String> setMap, @Param("whereMap") Map<String, String> whereMap);

	public int delete(@Param("whereMap") Map<String, String> whereMap);

	public int getCountBy(@Param("submit_time") String submit_time);

	public List<Opinion> getOpinionList(@Param("columnMap") Map<String, String> columnMap, @Param("offset") int offset,
                                        @Param("size") int size);
	
	public int insertWx(@Param("submitTime") long submitTime, @Param("passengerId") Integer passengerId, @Param("opinionTontent") String opinionTontent);

}
