package com.zah.service.impl;

import com.zah.dao.CommonDao;
import com.zah.service.WxShiftsService;
import com.zah.thread.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WxShiftsServiceImp implements WxShiftsService {
	@Autowired
	CommonDao commonDao;

	@Override
	public List<Map<String, Object>> ShiftsSearch(String up_origin_name, String up_terminal_name, String shifts_date) {
		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("up_origin_name", up_origin_name);
			param.put("up_terminal_name", up_terminal_name);
			param.put("shifts_date", shifts_date);
			String sql = "select shifts_number,DATE_FORMAT(depart_time,\"%H:%i\") as depart_time,DATE_FORMAT(arrive_time,\"%H:%i\") as arrive_time,up_origin_name,up_terminal_name from sbus_shifts_real s inner join sbus_line l on s.line_id=l.line_id where l.up_origin_name=#{param.up_origin_name} and l.up_terminal_name=#{param.up_terminal_name} and s.shifts_date=#{param.shifts_date}";
			List<Map<String, Object>> shifts = commonDao.query(sql, param);
			return shifts;
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			return null;
		}

	}

	@Override
	public List<Map<String, Object>> SearchOriginName() {
		try {
			Map<String, String> param = new HashMap<String, String>();
			String sql = "SELECT DISTINCT up_origin_name FROM sbus_line";
			List<Map<String, Object>> names = commonDao.query(sql, param);
			return names;
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			return null;
		}

	}

	@Override
	public List<Map<String, Object>> SearchTerminalName() {
		try {
			Map<String, String> param = new HashMap<String, String>();
			String sql = "SELECT DISTINCT up_terminal_name FROM sbus_line";
			List<Map<String, Object>> names = commonDao.query(sql, param);
			return names;
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			return null;
		}

	}

	@Override
	public List<Map<String, Object>> ShiftsTicketInfoSearch(String origin_name, String up_terminal_name,
			String shifts_date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getShiftsList(String shifts_number, String shifts_date) {
		// TODO Auto-generated method stub
		Map<String, String> param = new HashMap<String, String>();
		param.put("shifts_number", shifts_number);
		param.put("shifts_date", shifts_date);
		String sql = "select DATE_FORMAT(shifts_date,\"%Y-%m-%d\") as shifts_date, shifts_number, student_num, teacher_num, vehicle_id, DATE_FORMAT(depart_time,\"%H:%i\") as depart_time from sbus_shifts_real where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date}";
		List<Map<String, Object>> shifts = commonDao.query(sql, param);
		return shifts;
	}

	public void updateShiftsInfo(int type, double price, String shifts_number, String shifts_date, int vehicle_id) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("shifts_number", shifts_number);
		param.put("shifts_date", shifts_date);
		param.put("vehicle_id", vehicle_id + "");
		// 学生
		if (type == 2) {
			String sql = "update sbus_shifts_real set student_num = (student_num + 1) where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date} AND vehicle_id = #{vehicle_id}";
			commonDao.execute(sql, param);
		} else {
			String sql = "update sbus_shifts_real set teacher_num = (teacher_num + 1) where shifts_number=#{param.shifts_number} and shifts_date=#{param.shifts_date} AND vehicle_id = #{vehicle_id}";
			commonDao.execute(sql, param);
		}
	}

	@Override
	public List<Map<String, Object>> queryShiftInfo(String shifts_number, String shifts_date) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("shifts_number", shifts_number);
		param.put("shifts_date", shifts_date);
		String sql = "SELECT DATE_FORMAT(a.shifts_date,\"%Y-%m-%d\") as shifts_date, DATE_FORMAT(a.depart_time,\"%H:%i\") as depart_time, a.student_num, a.teacher_num, b.up_origin_name, b.up_terminal_name, c.seat_num FROM sbus_shifts_real AS a LEFT JOIN sbus_line AS b ON a.line_id = b.line_id LEFT JOIN sbus_vehicle AS c ON c.vehicle_id = a.vehicle_id WHERE a.shifts_number = #{param.shifts_number} AND a.shifts_date=#{param.shifts_date}";
		List<Map<String, Object>> list = commonDao.query(sql, param);
		return list;
	}

}
