package com.zah.service;


import com.zah.entity.AdminUser;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.List;


public interface AdminService {
	public String addAdmin(String username, String password, String repassword, String name, String[] role_list);

	public String toupp(String admin_id, Model model, HttpSession session);

	public String editAdmin(String username, String password, String repassword, String name, String[] role_list,
                            String admin_id, HttpSession session);

	public String toShowOne(String admin_id, Model model, HttpSession session);

	public String showAdminList(int limit, int page, String username, HttpSession session);

	List<AdminUser> selectInfoByKey(String key, String value, String columns);

	int updatePasswordbyId(String admin_id, String password);
}
