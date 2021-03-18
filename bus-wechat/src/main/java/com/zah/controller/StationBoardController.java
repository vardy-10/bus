package com.zah.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zah.service.impl.StationBoardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("stationBoard")
public class StationBoardController {

	@Autowired
	StationBoardServiceImpl stationBoardServiceImpl;

	@RequestMapping("getLineInfo")
	@ResponseBody
	public String getLineInfo(String boardNumber) {
		List<Map<String, Object>> lineInfo = stationBoardServiceImpl.getLineInfo(boardNumber);
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : lineInfo) {
			jsonArray.add(new JSONObject(map).toJSONString());
		}
		return jsonArray.toJSONString();
	}

	@RequestMapping("queryLine")
	@ResponseBody
	public String queryLine(String date, String originName) {
		List<Map<String, Object>> lineInfo = stationBoardServiceImpl.queryLine(date, originName);
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : lineInfo) {
			jsonArray.add(new JSONObject(map).toJSONString());
		}
		return jsonArray.toJSONString();
	}

}
