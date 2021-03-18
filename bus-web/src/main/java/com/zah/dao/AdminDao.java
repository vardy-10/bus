package com.zah.dao;


import com.zah.entity.AdminUser;
import com.zah.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminDao {
	public List<AdminUser> selectInfoByKey(@Param("key") String key, @Param("value") String value,
										   @Param("columns") String columns);

	public int updatePasswordbyId(@Param("admin_id") String admin_id, @Param("password") String password);

	public int getAdminListCount(@Param("columnMap") Map<String, String> columnMap);

	public List<Role> selectRoleInfo();

	public List<AdminUser> getAdminList(@Param("columnMap") Map<String, String> columnMap, @Param("offset") int offset,
                                        @Param("size") int size);

	public int addAdmin(@Param("username") String username, @Param("password") String password,
                        @Param("name") String name, @Param("role_list") String role_list);

	public int deleteAdmin(@Param("admin_id") String adminId);

	public int updateAdminInfo(@Param("password") String password,
                               @Param("name") String name, @Param("role_list") String role_list, @Param("admin_id") String admin_id);

}
