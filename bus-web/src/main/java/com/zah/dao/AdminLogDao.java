package com.zah.dao;

import com.zah.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminLogDao {

	public int addAdminLog(@Param("admin_id") String admin_id, @Param("log_info") String log_info,
                           @Param("log_time") String log_time);

	public List<AdminLog> getAdminLogList(@Param("columnMap") Map<String, String> columnMap,
										  @Param("offset") int offset, @Param("size") int size);

	public int getAdminLogCount(@Param("columnMap") Map<String, String> columnMap);

	public int clearUserLogBy(@Param("columnMap") Map<String, String> columnMap);

}
