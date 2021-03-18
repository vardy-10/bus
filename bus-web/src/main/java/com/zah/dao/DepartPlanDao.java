package com.zah.dao;

import com.zah.entity.DepartPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartPlanDao {

	public List<DepartPlan> select(@Param("otherMap") Map<String, String> otherMap,
								   @Param("whereMap") Map<String, String> whereMap);

	public int insert(@Param("setMap") Map<String, String> setMap);

	public int update(@Param("setMap") Map<String, String> setMap, @Param("whereMap") Map<String, String> whereMap);

	public int delete(@Param("whereMap") Map<String, String> whereMap);

}
