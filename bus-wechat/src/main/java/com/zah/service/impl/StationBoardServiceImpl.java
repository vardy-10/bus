package com.zah.service.impl;


import com.zah.dao.CommonDao;
import com.zah.service.StationBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StationBoardServiceImpl implements StationBoardService {

	@Autowired
	CommonDao commonDao;

	@Override
	public List<Map<String, Object>> getLineInfo(String boardNumber) {
		Map<String, String> param = new HashMap<String, String>();
		Calendar now = Calendar.getInstance();
		param.put("boardNumber", boardNumber);
		param.put("departTime",
				now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
		param.put("shiftsDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		String sql = "select b.up_terminal_name, b.up_origin_name, c.shifts_number, DATE_FORMAT(c.depart_time,\"%H:%i\") AS depart_time from sbus_station as a INNER JOIN sbus_line as b on a.line_id = b.line_id INNER JOIN sbus_shifts_real as c on a.line_id = c.line_id where a.board_number = #{param.boardNumber} and c.depart_time > #{param.departTime} and c.shifts_date = #{param.shiftsDate} order by c.depart_time";
		return commonDao.query(sql, param);
	}

	@Override
	public List<Map<String, Object>> queryLine(String date, String originName) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("date", date);
		param.put("originName", originName);
		String sql = "select a.up_terminal_name, a.up_origin_name, b.shifts_number, DATE_FORMAT(b.depart_time,\"%H:%i\") AS depart_time from sbus_line as a INNER JOIN sbus_shifts_real as b on a.line_id = b.line_id where b.shifts_date = #{param.date} and a.up_origin_name = #{param.originName} order by b.depart_time";
		return commonDao.query(sql, param);
	}
}
