package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import com.zah.dao.BusLineDao;
import com.zah.dao.BusStationDao;
import com.zah.dao.CommonDao;
import com.zah.entity.BusLine;
import com.zah.entity.BusStation;
import com.zah.service.BusLineService;
import com.zah.service.PublicService;
import com.zah.thread.BusPlaceAndShifts;
import com.zah.thread.Start;
import com.zah.util.Function;
import com.zah.util.ValidateUtil;
import com.zah.util.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.*;

@Service
public class BusLineServiceImpl implements BusLineService {
	@Autowired
	BusLineDao busLineDao;
	@Autowired
	BusStationDao busStationDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	PublicService publicService;

	public String busLineShow(Model model) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "bus/busLineShow";
	}

	public String busLineAdd(Model model) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		return "bus/busLineAdd";
	}

	public String busLineUpdate(String line_id, Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			if (line_id == null) {
				model.addAttribute("message", "请选择要编辑的线路");
				return "admin/error";
			}
			List<BusLine> oneBusLineList = busLineDao.selectInfoByKey("line_id", line_id,
					"line_id,line_name,line_type");
			if (oneBusLineList.size() <= 0) {
				model.addAttribute("message", "线路不存在或已删除");
				return "admin/error";
			}
			BusLine busLine = oneBusLineList.get(0);
			model.addAttribute("busLine", busLine);
			return "bus/busLineUpdate";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String toBusStation(String line_id, Model model) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}

			if (line_id == null) {
				model.addAttribute("message", "请选择要查看的车站");
				return "admin/error";
			}

			model.addAttribute("line_id", line_id);

			return "bus/busStationShow";

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}

	}

	// 展示线路列表
	@Override
	public String showBusLineList(int limit, int page, String line_name) {
		Map<String, Object> busLineMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				busLineMap.put("msg", "无权限");
				busLineMap.put("code", 1);
				return new JSONObject(busLineMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();

			if (!StringUtils.isEmpty(line_name)) {
				columnMap.put("line_name", line_name);
			}
			int count = busLineDao.getCountBy(columnMap);
			List<BusLine> busLineList = new ArrayList<BusLine>();
			if (count > 0) {
				busLineList = busLineDao.getBusLineList(columnMap, (page - 1) * limit, limit);
				busLineMap.put("msg", "获取成功");
				busLineMap.put("code", 0);
				busLineMap.put("count", count);
				busLineMap.put("data", busLineList);
			} else {
				busLineMap.put("msg", "无数据");
				busLineMap.put("code", 1);
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			busLineMap.put("msg", "系统错误");
			busLineMap.put("code", 1);
		}
		return new JSONObject(busLineMap).toString();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String deleteBusLine(String line_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (line_id == null) {
				map.put("message", "请选择要删除的线路");
				return new JSONObject(map).toString();
			}
			List<BusLine> oneBusLineList = busLineDao.selectInfoByKey("line_id", line_id, "line_name");
			if (oneBusLineList.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}

			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("line_id", line_id);
			List<Map<String, Object>> tmplList = commonDao
					.query("select line_id from sbus_shifts_tmpl where line_id=#{param.line_id} limit 1", paramMap);
			if (tmplList.size() > 0) {
				map.put("message", "排班模板中存在该线路，请调整后再操作");
				return new JSONObject(map).toString();
			}
			String nowTime = String.valueOf(System.currentTimeMillis() / 1000);
			paramMap.put("nowTime", nowTime);
			List<Map<String, Object>> shiftsPlanList = commonDao.query(
					"select line_id from sbus_shifts_plan where line_id=#{param.line_id} and unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.nowTime} limit 1",
					paramMap);
			if (shiftsPlanList.size() > 0) {
				map.put("message", "未来的排班计划中存在该线路，请调整后再操作");
				return new JSONObject(map).toString();
			}

			List<Map<String, Object>> departRealList = commonDao.query(
					"select line_id from sbus_shifts_real where line_id=#{param.line_id} and unix_timestamp( concat(shifts_date,' ', depart_time) )>#{param.nowTime} limit 1",
					paramMap);
			if (departRealList.size() > 0) {
				map.put("message", "未来的排班日程中存在该线路，请调整后再操作");
				return new JSONObject(map).toString();
			}
			int result = busLineDao.deleteBusLine(line_id);
			if (result > 0) {
				busLineDao.delStationByLineId(line_id);
				// 暂不删除排班日程和排班计划的数据
				// commonDao.execute("delete from sbus_shifts_plan where
				// line_id=#{param.line_id}", paramMap);
				// commonDao.execute("delete from sbus_shifts_real where
				// line_id=#{param.line_id}", paramMap);
				map.put("success", true);
				map.put("message", "操作成功");
				publicService.addLog("删除线路【" + oneBusLineList.get(0).getLine_name() + "】");
			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {

			Start.projectLog.writeError(e);
			throw new RuntimeException("发生异常！");
		}

		return new JSONObject(map).toString();

	}

	public String addBusLine(String line_name, String line_type) {
		line_type = "1";// 项目需求只有单向
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_name)) {
				map.put("message", "请输入线路名！");
				return new JSONObject(map).toString();
			}
			if (line_name.length() > 10) {
				map.put("message", "请输入10位以内的线路名！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_type) || !"1".equals(line_type) && !"2".equals(line_type)) {
				map.put("message", "请选择线路类型！");
				return new JSONObject(map).toString();
			}

			int result = busLineDao.addBusLine(line_name, line_type);
			if (result > 0) {
				map.put("success", true);
				map.put("message", "操作成功");
				publicService.addLog("添加线路【" + line_name + "】");
			} else {
				map.put("message", "操作失败");
			}

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
		}
		return new JSONObject(map).toString();

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String editBusLine(String line_name, String line_type, String line_id) {
		line_type = "1";// 项目需求只有单向
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			if (line_id == null) {
				map.put("message", "请选择要编辑的线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_name)) {
				map.put("message", "请输入线路名！");
				return new JSONObject(map).toString();
			}
			if (line_name.length() > 10) {
				map.put("message", "请输入10位以内的线路名！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(line_type) || !"1".equals(line_type) && !"2".equals(line_type)) {
				map.put("message", "请选择线路类型！");
				return new JSONObject(map).toString();
			}
			List<BusLine> oneBusLineList = busLineDao.selectInfoByKey("line_id", line_id, "line_name,line_type");
			if (oneBusLineList.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("line_id", line_id);
			int result = busLineDao.editBusLine(line_name, line_type, line_id);
			if (result > 0) {
				map.put("success", true);
				map.put("message", "编辑成功！");
				publicService.addLog("编辑线路【" + oneBusLineList.get(0).getLine_name() + "】");
				return new JSONObject(map).toString();

			} else {
				map.put("message", "操作失败");
			}

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");

		}

		return new JSONObject(map).toString();
	}

	public String importLinePoint(String line_id, Model model) {
		model.addAttribute("line_id", line_id);
		return "bus/busImportLine";
	}

	@Transactional(rollbackFor = Exception.class)
	public String trackLineImport(MultipartFile file, String line_id) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("code", "1");
		try {
			if (!PublicService.isRole("schoolBus")) {
				returnMap.put("msg", "无权限");
				return new JSONObject(returnMap).toString();
			}
			if (file == null || file.isEmpty()) {
				returnMap.put("msg", "请选择导入文件！");
				return new JSONObject(returnMap).toString();
			}
			String fileName = new String(file.getOriginalFilename().getBytes(), "utf-8");
			if (fileName == null) {
				returnMap.put("msg", "请选择KML文件！");
				return new JSONObject(returnMap).toString();
			}
			String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
			if (!ext.equals(".kml")) {
				returnMap.put("msg", "请选择KML文件！");
				return new JSONObject(returnMap).toString();
			}
			if (line_id == null) {
				returnMap.put("msg", "请选择要导入的线路");
				return new JSONObject(returnMap).toString();
			}
			List<BusLine> busLineList = busLineDao.selectInfoByKey("line_id", line_id, "line_name,line_type");
			if (busLineList.size() <= 0) {
				returnMap.put("msg", "线路不存在或已删除");
				return new JSONObject(returnMap).toString();
			}
			InputStream is = file.getInputStream();
			XmlHelper xh = new XmlHelper();
			if (!xh.parse(is)) {
				returnMap.put("msg", "无法解析文件格式");
				return new JSONObject(returnMap).toString();
			}
			List<Map<String, Object>> upStationList = new ArrayList<Map<String, Object>>(); // 上行车站列表
			List<Map<String, Object>> upLinePointList = new ArrayList<Map<String, Object>>();// 上行线路信息列表
			NodeList folderList = xh.getNodeList("Folder");
			if (folderList == null || folderList.getLength() < 1) {
				returnMap.put("msg", "线路点集不能为空");
				return new JSONObject(returnMap).toString();
			}
			if (xh.getSize(folderList) > 1) {
				returnMap.put("msg", "线路点集不能多于一组");
				return new JSONObject(returnMap).toString();
			}
			NodeList placemarkList = xh.getNodeList("Placemark");
			if (placemarkList == null || placemarkList.getLength() < 2) {
				returnMap.put("msg", "车站信息不能为空");
				return new JSONObject(returnMap).toString();
			}

			for (int i = 0; i < xh.getSize(placemarkList) - 1; i++) {
				Map<String, Object> map = new HashMap<>();
				Element placemark = xh.getElement(placemarkList, i);
				Element longitudeElement = xh.getElement(xh.getNodeList(placemark, "longitude"), 0);
				Element latitudeElement = xh.getElement(xh.getNodeList(placemark, "latitude"), 0);
				String lon = Function.getInstance().replaceBlank(xh.getText(longitudeElement));
				String lat = Function.getInstance().replaceBlank(xh.getText(latitudeElement));
				if (!ValidateUtil.getInstance().isNumber(lon) || Double.parseDouble(lon) == 0) {
					returnMap.put("msg", "第" + (i + 1) + "个车站的经度数据错误！");
					return new JSONObject(returnMap).toString();
				}
				if (!ValidateUtil.getInstance().isNumber(lat) || Double.parseDouble(lat) == 0) {
					returnMap.put("msg", "第" + (i + 1) + "个车站的纬度数据错误！");
					return new JSONObject(returnMap).toString();
				}
				map.put("LONGITUDE", lon);
				map.put("LATITUDE", lat);
				upStationList.add(map);
			}

			Element folder = xh.getElement(folderList, 0);
			NodeList coordinatesList = xh.getNodeList(folder, "coordinates");
			if (coordinatesList == null || coordinatesList.getLength() < 1) {
				returnMap.put("msg", "线路点集不为空");
				return new JSONObject(returnMap).toString();
			}
			Element coordinates = xh.getElement(coordinatesList, 0);
			String text = xh.getText(coordinates);
			if (StringUtils.isEmpty(text)) {
				returnMap.put("msg", "线路点集不为空");
				return new JSONObject(returnMap).toString();
			}
			List<String> pointList = new ArrayList<>(Arrays.asList(text.split(" ")));
			for (int j = 0; j < pointList.size(); j++) {
				List<String> placeList = new ArrayList<>(
						Arrays.asList(Function.getInstance().replaceBlank(pointList.get(j)).split(",")));
				if (placeList.size() != 3) {
					returnMap.put("msg", "第" + (j + 1) + "个线路点格式错误");
					return new JSONObject(returnMap).toString();
				}
				Map<String, Object> map = new HashMap<>();
				String lon = placeList.get(0);
				String lat = placeList.get(1);
				if (!ValidateUtil.getInstance().isNumber(lon) || Double.parseDouble(lon) == 0) {
					returnMap.put("msg", "第" + (j + 1) + "个线路点的经度数据错误！");
					return new JSONObject(returnMap).toString();
				}
				if (!ValidateUtil.getInstance().isNumber(lat) || Double.parseDouble(lat) == 0) {
					returnMap.put("msg", "第" + (j + 1) + "个线路点的纬度数据错误！");
					return new JSONObject(returnMap).toString();
				}
				map.put("LONGITUDE", lon);
				map.put("LATITUDE", lat);
				upLinePointList.add(map);
			}
			// 查询线路上下行站点数量
			List<BusStation> busStationList = busStationDao.selectInfoByKey("line_id", line_id, "station_id");
			long UpStationNum = busStationList.size();

			// 判断数据库车站数量与导入文件的车站数量相同
			if (UpStationNum != upStationList.size()) {
				returnMap.put("msg", "车站数量不匹配！");
				return new JSONObject(returnMap).toString();
			}

			// 为上行下行线路MAP添加密集点
			List<Map<String, Object>> upLinePointCloneList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list = null;
			if (upLinePointList.size() > 0) {
				upLinePointCloneList.add(upLinePointList.get(0));
			}
			for (int i = 1; i < upLinePointList.size(); i++) {
				list = Function.getInstance().get_midpoint_array2(
						Double.parseDouble(upLinePointList.get(i - 1).get("LONGITUDE").toString()),
						Double.parseDouble(upLinePointList.get(i - 1).get("LATITUDE").toString()),
						Double.parseDouble(upLinePointList.get(i).get("LONGITUDE").toString()),
						Double.parseDouble(upLinePointList.get(i).get("LATITUDE").toString()), 0.0001);
				upLinePointCloneList.addAll(list);
				list = null;
				upLinePointCloneList.add(upLinePointList.get(i));
			}
			upLinePointList = upLinePointCloneList;

			int stationLineDistanceIndex = 0; // 车站最近线路点的索引
			// 计算上行线路及车站与经纬度相关的信息（将车站经纬度分别找到线路点中最近的经纬度的位置进行插入，将线路点依次计算总距离并进行拼接）
			ListIterator<Map<String, Object>> stationIterator = upStationList.listIterator();
			Map<String, Object> stationMap = null;
			while (stationIterator.hasNext()) {
				stationMap = stationIterator.next();
				double stationLineDistance = -1;// 车站最近线路点的距离
				for (int i = stationLineDistanceIndex; i < upLinePointList.size(); i++) {
					double lineLng = Double.parseDouble(upLinePointList.get(i).get("LONGITUDE").toString());
					double lineLat = Double.parseDouble(upLinePointList.get(i).get("LATITUDE").toString());
					double stationlng = Double.parseDouble(stationMap.get("LONGITUDE").toString());
					double stationlat = Double.parseDouble(stationMap.get("LATITUDE").toString());
					double Distance = Function.getInstance().Distance(lineLng, lineLat, stationlng, stationlat); // 车站到线路点的距离
					if (stationLineDistance == -1 || stationLineDistance > Distance) {
						stationLineDistance = Distance;
						stationLineDistanceIndex = i;
					}
				}
				// 如果为-1则表示线路点集合中没有数据,直接将站点放入线路点中
				if (stationLineDistance == -1) {
					upLinePointList.add(stationMap);
					stationLineDistanceIndex++;
					continue;
				}
				if (stationLineDistance == 0) {
					upLinePointList.remove(stationLineDistanceIndex);
					upLinePointList.add(stationLineDistanceIndex, stationMap);
					stationLineDistanceIndex++;
					continue;
				}
				if (stationLineDistanceIndex == 0) {
					if (stationLineDistanceIndex < upLinePointList.size() - 1) {
						double distance1 = Function.getInstance().Distance(
								Double.parseDouble(stationMap.get("LONGITUDE").toString()),
								Double.parseDouble(stationMap.get("LATITUDE").toString()),
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex + 1).get("LONGITUDE").toString()),
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex + 1).get("LATITUDE").toString()));
						double distance2 = Function.getInstance().Distance(
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex).get("LONGITUDE").toString()),
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex).get("LATITUDE").toString()),
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex + 1).get("LONGITUDE").toString()),
								Double.parseDouble(
										upLinePointList.get(stationLineDistanceIndex + 1).get("LATITUDE").toString()));
						if (distance1 < distance2) {
							stationLineDistanceIndex++;
						}
					}
				} else {
					double distance1 = Function.getInstance().Distance(
							Double.parseDouble(stationMap.get("LONGITUDE").toString()),
							Double.parseDouble(stationMap.get("LATITUDE").toString()),
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex - 1).get("LONGITUDE").toString()),
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex - 1).get("LATITUDE").toString()));
					double distance2 = Function.getInstance().Distance(
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex).get("LONGITUDE").toString()),
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex).get("LATITUDE").toString()),
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex - 1).get("LONGITUDE").toString()),
							Double.parseDouble(
									upLinePointList.get(stationLineDistanceIndex - 1).get("LATITUDE").toString()));
					if (distance1 > distance2) {
						stationLineDistanceIndex++;
					}
				}
				upLinePointList.add(stationLineDistanceIndex, stationMap);
				stationLineDistanceIndex++;
			}
			stationMap = null;
			StringBuffer up_lines_point = new StringBuffer();// 上行线路点拼接字符串
			Map<String, Object> previousOneLineMap = new HashMap<String, Object>();// 前一个线路点信息集合
			double stationDistance = 0;// 站间距
			double upLineDistance = 0;// 上行线路总距离
			int order = 0;// 车站索引
			for (Map<String, Object> map1 : upLinePointList) {
				if (order > 0 && previousOneLineMap != null) {
					double lng = Double.parseDouble(previousOneLineMap.get("LONGITUDE").toString());
					double lat = Double.parseDouble(previousOneLineMap.get("LATITUDE").toString());
					double linelat = Double.parseDouble(map1.get("LATITUDE").toString());
					double linelng = Double.parseDouble(map1.get("LONGITUDE").toString());
					double spotDistance = Function.getInstance().Distance(lng, lat, linelng, linelat); // 点间距
					stationDistance += spotDistance;
					upLineDistance += spotDistance;
				}
				stationMap = upStationList.get(order);
				double stationlng = Double.parseDouble(stationMap.get("LONGITUDE").toString());
				double stationlat = Double.parseDouble(stationMap.get("LATITUDE").toString());
				double lineLng = Double.parseDouble(map1.get("LONGITUDE").toString());
				double lineLat = Double.parseDouble(map1.get("LATITUDE").toString());
				if (stationlng == lineLng && stationlat == lineLat) {
					stationMap.put("front_distance", Math.round(stationDistance) + "");
					stationMap.put("station_distance", String.format("%.2f", upLineDistance));
					stationDistance = 0;
					order++;
				}
				previousOneLineMap = map1;
				if (order > 0) {
					up_lines_point
							.append("|" + map1.get("LONGITUDE").toString() + "-" + map1.get("LATITUDE").toString());
				}
				if (order == UpStationNum) {
					break;
				}
			}
			// 判断线路点与车站的经纬度是否一致
			if (order != UpStationNum) {
				returnMap.put("msg", "车站经纬度和线路经纬度不匹配！");
				return new JSONObject(returnMap).toString();
			}
			String up_lines_pointStr = up_lines_point.toString();
			if (!up_lines_pointStr.equals("")) {
				up_lines_pointStr = up_lines_point.substring(1);
			}

			// 更新数据库线路及车站的经纬度和距离信息
			String sql = "UPDATE sbus_line SET up_lines_point=#{param.up_lines_point} WHERE line_id=#{param.line_id}";
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("up_lines_point", up_lines_pointStr);
			paramMap.put("line_id", line_id);
			int result = commonDao.execute(sql, paramMap);
			if (result < 1) {
				throw new RuntimeException();
			}
			for (int i = 0; i < upStationList.size(); i++) {
				order = i + 1;
				double longitude = Double.parseDouble(upStationList.get(i).get("LONGITUDE").toString());
				double latitude = Double.parseDouble(upStationList.get(i).get("LATITUDE").toString());
				double front_distance = Double.parseDouble(upStationList.get(i).get("front_distance").toString());
				double station_distance = Double.parseDouble(upStationList.get(i).get("station_distance").toString());
				sql = "UPDATE sbus_station SET longitude=#{param.longitude},latitude=#{param.latitude},front_distance=#{param.front_distance},station_distance=#{param.station_distance} WHERE line_id=#{param.line_id} and station_order=#{param.station_order}";
				paramMap = new HashMap<>();
				paramMap.put("line_id", line_id);
				paramMap.put("station_order", String.valueOf(order));
				paramMap.put("longitude", String.valueOf(longitude));
				paramMap.put("latitude", String.valueOf(latitude));
				paramMap.put("front_distance", String.valueOf(front_distance));
				paramMap.put("station_distance", String.valueOf(station_distance));
				result = commonDao.execute(sql, paramMap);
				if (result < 1) {
					throw new RuntimeException();
				}
			}
			BusPlaceAndShifts.getInstance().shutDown(line_id);
			BusPlaceAndShifts.getInstance().Start(line_id);
			publicService.addLog("轨迹导入线路【" + busLineList.get(0).getLine_name() + "】");
			returnMap.put("code", "0");
			returnMap.put("msg", "");
			return new JSONObject(returnMap).toString();
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			returnMap.put("msg", "操作失败");
		}
		return new JSONObject(returnMap).toString();
	}
}
