package com.zah.service;

import java.util.List;
import java.util.Map;

public interface StationBoardService {

	public List<Map<String, Object>> getLineInfo(String boardNumber);

	public List<Map<String, Object>> queryLine(String date, String originName);

}
