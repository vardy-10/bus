package com.zah.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import com.zah.dao.BusLineDao;
import com.zah.dao.BusStationDao;
import com.zah.entity.BusLine;
import com.zah.entity.BusStation;
import com.zah.service.BusStationService;
import com.zah.service.PublicService;
import com.zah.thread.Start;
import com.zah.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusStationServiceImpl implements BusStationService {
	@Autowired
	BusStationDao busStationDao;
	@Autowired
	BusLineDao busLineDao;
	@Autowired
	PublicService publicService;

	public String busStationUpdate(Model model, String station_id) {
		try {
			if (!PublicService.isRole("schoolBus")) {
				model.addAttribute("message", "无权限");
				return "admin/error";
			}
			if (station_id == null) {
				model.addAttribute("message", "请选择要编辑的车站");
				return "admin/error";
			}
			List<BusStation> busStationList = busStationDao.selectInfoByKey("station_id", station_id,
					"radius,station_name,station_dir,station_order,line_id,station_id");

			if (busStationList.size() <= 0) {
				model.addAttribute("message", "车站不存在或已删除");
				return "admin/error";
			}
			BusStation busStation = busStationList.get(0);
			List<BusLine> busLineList = busLineDao.selectInfoByKey("line_id", String.valueOf(busStation.getLine_id()),
					"line_type");
			if (busLineList.size() <= 0) {
				model.addAttribute("message", "车站线路不存在或已删除");
				Start.projectLog.writeInfo("出现不存在的线路");
				return "admin/error";
			}
			BusLine busLine = busLineList.get(0);
			model.addAttribute("busStation", busStation);
			model.addAttribute("busLine", busLine);
			return "bus/busStationUpdate";
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			model.addAttribute("message", "系统错误");
			return "admin/error";
		}
	}

	public String busStationAdd(Model model, String line_id) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		List<BusLine> list = busLineDao.selectInfoByKey("line_id", line_id, "line_type");
		BusLine busline = list.get(0);
		model.addAttribute("line_id", line_id);
		model.addAttribute("busline", busline);
		return "bus/busStationAdd";
	}

	public String busStationShow(String line_id, Model model) {
		if (!PublicService.isRole("schoolBus")) {
			model.addAttribute("message", "无权限");
			return "admin/error";
		}
		if (line_id == null) {
			model.addAttribute("message", "线路不存在或已删除");
			return "admin/error";
		}

		model.addAttribute("line_id", line_id);
		return "bus/busStationShow";
	}

	@Override
	public String showBusStationList(int limit, int page, String station_name, String line_id) {
		Map<String, Object> busStationMap = new HashMap<String, Object>();
		try {
			if (!PublicService.isRole("schoolBus")) {
				busStationMap.put("msg", "无权限");
				busStationMap.put("code", 1);
				return new JSONObject(busStationMap).toString();
			}
			Map<String, String> columnMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(station_name)) {
				columnMap.put("station_name", station_name);
			}
			if (!StringUtils.isEmpty(line_id)) {
				columnMap.put("line_id", line_id);
			}
			int count = busStationDao.getCountBy(columnMap);
			List<BusStation> busStationList = new ArrayList<BusStation>();

			if (count > 0) {
				busStationList = busStationDao.getBusStationList(columnMap, (page - 1) * limit, limit);
				busStationMap.put("msg", "获取成功");
				busStationMap.put("code", 0);
				busStationMap.put("count", count);
				busStationMap.put("data", busStationList);

			} else {
				busStationMap.put("msg", "无数据");
				busStationMap.put("code", 1);
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			busStationMap.put("msg", "系统错误");
			busStationMap.put("code", 1);
		}
		return new JSONObject(busStationMap).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String deleteBusStation(String station_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			// 权限判断
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}
			// 接收提交并判断
			if (station_id == null) {
				map.put("message", "请选择要删除的车站");
				return new JSONObject(map).toString();
			}
			// 查询验证
			Map<String, String> columnMap = new HashMap<String, String>();
			columnMap.put("station_id", station_id);
			List<BusStation> oneBusStationList = busStationDao.selectInfoByKey("station_id", station_id,
					"station_order,line_id,station_dir,station_name");
			if (oneBusStationList.size() <= 0) {
				map.put("message", "车站不存在或已删除");
				return new JSONObject(map).toString();
			}
			// 数据操作
			BusStation busStation = oneBusStationList.get(0);
			String line_id = Integer.toString(busStation.getLine_id());
			String station_dir = Integer.toString(busStation.getStation_dir());
			String station_order = Integer.toString(busStation.getStation_order());
			columnMap.put("station_order", station_order);
			columnMap.put("line_id", line_id);
			columnMap.put("station_dir", station_dir);
			int result = busStationDao.deleteBusStation(station_id);// 删除车站

			if (result > 0) {
				int num = busStationDao.deleteUpdateStationInfo(columnMap);// 更新车站顺序号
				String origin_name = station_dir.equals("1") ? "up_origin_name" : "down_origin_name";
				String terminal_name = station_dir.equals("1") ? "up_terminal_name" : "down_terminal_name";
				String value;
				// int maxStation_order = busStationDao.getMaxOrder(line_id, station_dir);//
				// 删除完最大的order
				// 更新起点站
				if (busStation.getStation_order() == 1) {
					List<BusStation> oneOfBusStation = busStationDao.selectInfoByLineAndDir(line_id, station_dir, "1");
					if (oneOfBusStation.size() == 0) {
						value = "";
					} else {
						value = oneOfBusStation.get(0).getStation_name();
					}
					int updateLine = busLineDao.updateSbusLineBy(origin_name, value, line_id);
					if (updateLine == 0) {
						map.put("message", "系统错误");
						throw new RuntimeException("发生异常！");
					}
				}
				// 更新终点站
				if (num == 0) {
					String so = String.valueOf(busStation.getStation_order() - 1);
					if (so.equals("0")) {
						value = "";
					} else {
						List<BusStation> oneOfBusStation = busStationDao.selectInfoByLineAndDir(line_id, station_dir,
								so);
						value = oneOfBusStation.get(0).getStation_name();
					}
					int updateLine = busLineDao.updateSbusLineBy(terminal_name, value, line_id);
					if (updateLine == 0) {
						map.put("message", "系统错误");
						throw new RuntimeException("发生异常！");
					}
				}
				map.put("success", true); // 删除成功
				map.put("message", "操作成功");
				publicService.addLog("删除车站【" + busStation.getStation_name() + "】");
			} else {
				map.put("message", "操作失败");
			}
		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();

	}

	// 点击添加车站按钮
	@Transactional(rollbackFor = Exception.class)
	public String addBusStation(String line_id, String station_dir, String radius, String station_name,
			String station_order) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {

			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			Map<String, String> columnMap = new HashMap<String, String>();
			if (StringUtils.isEmpty(line_id)) {
				map.put("message", "请选择线路");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(station_name)) {
				map.put("message", "请输入车站名！");
				return new JSONObject(map).toString();
			}
			if (station_name.length() > 50) {
				map.put("message", "请输入50位以内的车站名！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(station_dir) || !"1".equals(station_dir) && !"2".equals(station_dir)) {
				map.put("message", "请选择车站方向！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(station_order)) {
				map.put("message", "请输入车站顺序号！");
				return new JSONObject(map).toString();
			}
			if (!ValidateUtil.getInstance().isDigit(station_order) || Integer.parseInt(station_order) > 99) {
				map.put("message", "车站顺序号必须为100以内的正整数！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(radius)) {
				map.put("message", "请输入车站半径！");
				return new JSONObject(map).toString();
			}
			if (!ValidateUtil.getInstance().isDigit(radius) || Integer.parseInt(radius) > 999) {
				map.put("message", "车站半径必须为1000以内的正整数！");
				return new JSONObject(map).toString();
			}

			List<BusLine> oneBusLineList = busLineDao.selectInfoByKey("line_id", line_id, "line_type");
			if (oneBusLineList.size() <= 0) {
				map.put("message", "线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			if (oneBusLineList.get(0).getLine_type() == 1 && !station_dir.equals("1")) {
				map.put("message", "单向线路只能添加上行或单向的车站");
				return new JSONObject(map).toString();
			}
			columnMap.put("line_id", line_id);
			columnMap.put("station_name", station_name);
			columnMap.put("station_dir", station_dir);
			columnMap.put("station_order", station_order);
			columnMap.put("radius", radius);

			int maxStation_order = busStationDao.getMaxOrder(line_id, station_dir);

			int station_order_int = Integer.parseInt(station_order);
			if (station_order_int > maxStation_order + 1) {
				map.put("message", "请按照顺序添加车站!");
				return new JSONObject(map).toString();
			}
			if (station_order_int <= maxStation_order) {
				busStationDao.addUpdateStationInfo(columnMap);

			}

			int addStation = busStationDao.addStation(columnMap);

			if (addStation <= 0) {
				map.put("message", "操作失败");
				return new JSONObject(map).toString();
			}
			String key = "";
			String value = "";
			if (maxStation_order < Integer.parseInt(station_order)) {
				key = station_dir.equals("1") ? "up_terminal_name" : "down_terminal_name";
				value = station_name;
				Map<String, String> setMap = new HashMap<>();
				setMap.put(key, value);
				int result = busLineDao.updateLineBykey(setMap, "line_id", line_id);
				if (result == 0) {
					map.put("message", "系统错误");
					throw new RuntimeException("发生异常！");
				}
			}
			if (station_order.equals("1")) {
				key = station_dir.equals("1") ? "up_origin_name" : "down_origin_name";
				value = station_name;
				Map<String, String> setMap = new HashMap<>();
				setMap.put(key, value);

				int result = busLineDao.updateLineBykey(setMap, "line_id", line_id);
				if (result == 0) {
					map.put("message", "系统错误");
					throw new RuntimeException("发生异常！");
				}
			}

			map.put("success", true);
			map.put("message", "操作成功");
			publicService.addLog("添加车站【" + station_name + "】");

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
			throw new RuntimeException("发生异常！");
		}
		return new JSONObject(map).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	public String editBusStation(String station_name, String station_dir, String station_order, String radius,
			String station_id) {
		station_dir = "1";// 只有单向
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("success", false);
		try {
			if (!PublicService.isRole("schoolBus")) {
				map.put("message", "无权限");
				return new JSONObject(map).toString();
			}

			if (station_id == null) {
				map.put("message", "请选择要编辑的车站");
				return new JSONObject(map).toString();

			}

			if (StringUtils.isEmpty(station_name)) {
				map.put("message", "请输入车站名！");
				return new JSONObject(map).toString();
			}
			if (station_name.length() > 50) {
				map.put("message", "请输入50位以内的车站名！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(station_dir) || !"1".equals(station_dir) && !"2".equals(station_dir)) {
				map.put("message", "请选择车站方向！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(station_order) || Integer.parseInt(station_order) == 0) {
				map.put("message", "车站顺序号必须为正整数！");
				return new JSONObject(map).toString();
			}
			if (!ValidateUtil.getInstance().isDigit(station_order) || Integer.parseInt(station_order) > 99) {
				map.put("message", "车站顺序号必须为100以内的正整数！");
				return new JSONObject(map).toString();
			}
			if (StringUtils.isEmpty(radius)) {
				map.put("message", "请输入车站半径！");
				return new JSONObject(map).toString();
			}
			if (!ValidateUtil.getInstance().isDigit(radius) || Integer.parseInt(radius) > 999) {
				map.put("message", "车站半径必须为1000以内的正整数！");
				return new JSONObject(map).toString();
			}
			// 查询车站信息列表
			List<BusStation> busStationList = busStationDao.selectInfoByKey("station_id", station_id,
					"station_dir,line_id,station_order,station_name");
			if (busStationList.size() <= 0) {
				map.put("message", "车站不存在或已删除");
				return new JSONObject(map).toString();
			}

			BusStation busStation = busStationList.get(0);

			String line_id = String.valueOf(busStation.getLine_id());
			List<BusLine> oneBusLineList = busLineDao.selectInfoByKey("line_id", line_id,
					"line_type,up_origin_name,down_origin_name,up_terminal_name,down_terminal_name");

			if (oneBusLineList.size() <= 0) {
				map.put("message", "车站线路不存在或已删除");
				return new JSONObject(map).toString();
			}
			BusLine busLine = oneBusLineList.get(0);
			if (busLine.getLine_type() == 1 && station_dir.equals("2")) {
				map.put("message", "单向线路只能编辑上行或单向的车站");
				return new JSONObject(map).toString();
			}

			if (busStation.getStation_dir() != Integer.parseInt(station_dir)
					|| busStation.getStation_order() != Integer.parseInt(station_order)) {
				int maxOrder = busStationDao.getMaxOrder(line_id, station_dir);// 允许编辑的最大顺序号
				if (busStation.getStation_dir() != Integer.parseInt(station_dir)) {
					maxOrder++;
				}
				if (Integer.parseInt(station_order) > maxOrder) {
					map.put("message", "请按照顺序编辑站点！");
					return new JSONObject(map).toString();
				}

				Map<String, String> busStation1 = new HashMap<String, String>();
				busStation1.put("line_id", String.valueOf(busStation.getLine_id()));
				busStation1.put("station_dir", String.valueOf(busStation.getStation_dir()));
				busStation1.put("station_order", String.valueOf(busStation.getStation_order()));
				busStationDao.upOnRed(busStation1);

				if (busStation.getStation_dir() == 1) {
					if (busLine.getUp_terminal_name().equals(busStation.getStation_name())) {
						String afterName = busStationDao.selectInfoByLineAndOrder(line_id, String.valueOf(maxOrder - 1),
								String.valueOf(busStation.getStation_dir()), station_id);
						if (afterName == null) {
							afterName = "";
						}

						Map<String, String> columnMap = new HashMap<>();
						columnMap.put("up_terminal_name", afterName);
						columnMap.put("up_terminal_point", "");
						int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
						if (result == 0) {
							throw new RuntimeException("发生异常！");
						}
					}
					if (busLine.getUp_origin_name().equals(busStation.getStation_name())) {
						String afterName = busStationDao.selectInfoByLineAndOrder(line_id, "1",
								String.valueOf(busStation.getStation_dir()), station_id);

						if (afterName == null) {
							afterName = "";
						}
						Map<String, String> columnMap = new HashMap<>();
						columnMap.put("up_origin_name", afterName);
						columnMap.put("up_lines_point", "");
						int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
						if (result == 0) {
							throw new RuntimeException("发生异常！");
						}
					}
				} else {
					if (busLine.getDown_terminal_name().equals(busStation.getStation_name())) {
						String afterName = busStationDao.selectInfoByLineAndOrder(line_id, String.valueOf(maxOrder - 1),
								String.valueOf(busStation.getStation_dir()), station_id);

						if (afterName == null) {
							afterName = "";
						}

						Map<String, String> columnMap = new HashMap<>();
						columnMap.put("down_terminal_name", afterName);
						columnMap.put("down_terminal_point", "");

						int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
						if (result == 0) {
							throw new RuntimeException("发生异常！");
						}
					}
					if (busLine.getDown_origin_name().equals(busStation.getStation_name())) {
						String afterName = busStationDao.selectInfoByLineAndOrder(line_id, "1",
								String.valueOf(busStation.getStation_dir()), station_id);

						if (afterName == null) {
							afterName = "";
						}

						Map<String, String> columnMap = new HashMap<>();
						columnMap.put("down_origin_name", afterName);
						columnMap.put("down_lines_point", "");
						int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
						if (result == 0) {
							throw new RuntimeException("发生异常！");
						}
					}
				}

				String terminal_name = station_dir.equals("1") ? "up_terminal_name" : "down_terminal_name";
				String origin_name = station_dir.equals("1") ? "up_origin_name" : "down_origin_name";
				if (station_order.equals("1")) {
					Map<String, String> columnMap = new HashMap<>();

					columnMap.put(origin_name, station_name);
					int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
					if (result == 0) {
						throw new RuntimeException("发生异常！");
					}
				}
				if (Integer.parseInt(station_order) >= maxOrder) {
					Map<String, String> columnMap = new HashMap<>();
					columnMap.put(terminal_name, station_name);
					int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
					if (result == 0) {
						throw new RuntimeException("发生异常！");
					}
				} else {
					Map<String, String> busStation2 = new HashMap<String, String>();
					busStation2.put("line_id", line_id);
					busStation2.put("station_id", station_id);
					busStation2.put("station_dir", station_dir);
					busStation2.put("station_order", station_order);
					busStationDao.upOnAdd(busStation2);
				}

			} else if (!station_name.equals(busStation.getStation_name())) {
				String terminal_name = station_dir.equals("1") ? "up_terminal_name" : "down_terminal_name";
				String origin_name = station_dir.equals("1") ? "up_origin_name" : "down_origin_name";
				int maxOrder = busStationDao.getMaxOrder(line_id, station_dir);// 最大顺序号

				if (station_order.equals("1")) {
					Map<String, String> columnMap = new HashMap<>();
					columnMap.put(origin_name, station_name);
					int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
					if (result == 0) {
						throw new RuntimeException("发生异常！");
					}
				}
				if (station_order.equals(String.valueOf(maxOrder))) {
					Map<String, String> columnMap = new HashMap<>();
					columnMap.put(terminal_name, station_name);
					int result = busLineDao.updateLineBykey(columnMap, "line_id", line_id);
					if (result == 0) {
						throw new RuntimeException("发生异常！");
					}
				}
			}
			Map<String, String> busStation3 = new HashMap<String, String>();
			busStation3.put("station_id", station_id);
			busStation3.put("station_name", station_name);
			busStation3.put("station_dir", station_dir);
			busStation3.put("station_order", station_order);
			busStation3.put("radius", radius);
			// 更新
			int result = busStationDao.upStation(busStation3);
			if (result == 0) {
				throw new RuntimeException("发生异常！");
			}
			map.put("success", true);

			map.put("message", "操作成功");

		} catch (Exception e) {
			Start.projectLog.writeError(e);
			map.put("message", "系统错误");
			throw new RuntimeException("发生异常！");
		}

		return new JSONObject(map).toString();
	}

}