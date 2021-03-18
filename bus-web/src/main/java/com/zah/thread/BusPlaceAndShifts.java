package com.zah.thread;

import com.zah.controller.ConfigController;
import com.zah.dao.CommonDao;
import com.zah.util.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 车辆位置自动转换经纬度及排班调度类
 *
 */
@Service
public class BusPlaceAndShifts {
	@Autowired
	CommonDao commonDao;
	private final int busOffSec = 180;// 掉线时间
	private final int maxReverseNum = 3;// 最多反向次数
	private final int minDistance = 20;// 最小距离
	private final int busLeaveDistance = 150;// 车辆离站距离
	private final int execute_interval = 5;// 查询间隔时间
	private final int advanceMonitorTime = 30 * 60;// 提前开始监控时间30分钟
	private static final BusPlaceAndShifts instance = new BusPlaceAndShifts();

	public static List<String> updatedShiftsLineList = new ArrayList<String>(); // 更新真实日程集合,保存更新过的线路id

	public static BusPlaceAndShifts getInstance() {
		return instance;
	}

	public static Map<String, BusPlace1> runThreadMap = new HashMap<String, BusPlace1>();// 保存每条线路的线程启动对象

	public void init() {
		Map<String, String> param = new HashMap<String, String>();
		List<Map<String, Object>> lineList = commonDao.query("select * from sbus_line", null);
		if (lineList.size() <= 0) {
			return;
		}
		for (Map<String, Object> lineMap : lineList) {
			Start(String.valueOf(lineMap.get("line_id")));
		}
	}

	public void Start(String line_id) {
		if (runThreadMap.containsKey(line_id)) {
			BusPlaceAndShifts.runThreadMap.get(line_id).threanStart = false;
			BusPlaceAndShifts.runThreadMap.get(line_id).interrupt();
		}
		BusPlace1 busPlace1 = new BusPlace1(line_id);
		runThreadMap.put(line_id + "", busPlace1);
		busPlace1.start();
	}

	public void shutDown(String line_id) {
		if (runThreadMap.containsKey(line_id)) {
			BusPlaceAndShifts.runThreadMap.get(line_id).threanStart = false;
			BusPlaceAndShifts.runThreadMap.get(line_id).interrupt();
			runThreadMap.remove(line_id);

		}
	}

	public class BusPlace1 extends Thread {
		private String lineId;
		public boolean threanStart = true;// 现在启动状态
		private Map<String, String> param = new HashMap<String, String>();
		// 保存线路的所有排班日程
		private List<Map<String, Object>> shiftsList = new ArrayList<Map<String, Object>>();
		private String shiftsDate = "";// 保存排班日期

		public BusPlace1(String lineId) {
			this.lineId = lineId;
		}

		public void run() {
			param.clear();
			param.put("line_id", lineId);
			param.put("station_dir", "1");
			List<Map<String, Object>> LineInfo = commonDao.query(
					"select `line_type`,`up_lines_point`,`down_lines_point` from `sbus_line` where `line_id`=#{param.line_id}",
					param);

			if ("".equals(LineInfo.get(0).get("up_lines_point"))) {
				return;
			}
			// 保存经纬度数组
			String[] upLinePoint = ((String) LineInfo.get(0).get("up_lines_point")).split("\\|");
			String[] downLinePoint = null;
			// 保存上行车站
			List<Map<String, Object>> upStationList = commonDao.query(
					"select radius,longitude,latitude,station_distance from  sbus_station where line_id=#{param.line_id} and `station_dir`=#{param.station_dir} and `station_order`=1",
					param);
			// 保存下行车站
			List<Map<String, Object>> downStationList = null;
			if (LineInfo.get(0).get("line_type").toString().equals("2")) {
				if (!LineInfo.get(0).get("down_lines_point").toString().equals("")) {
					downLinePoint = ((String) LineInfo.get(0).get("down_lines_point")).split("\\|");
				}
				param.clear();
				param.put("line_id", lineId);
				param.put("station_dir", "2");
				downStationList = commonDao.query(
						"select `station_id`,`longitude`,`latitude`,`radius`,`station_distance`from `sbus_station` where `line_id`=#{param.line_id} and `station_dir`=#{param.station_dir}  and `station_order`=1",
						param);
			} else {
				if (!LineInfo.get(0).get("line_type").toString().equals("1")) {
					return;
				}
			}
			// 定义map聚合保存之前的车辆信息
			Map<String, Map<String, String>> oldBusMap = new HashMap<String, Map<String, String>>();
			while (threanStart) {
				try {
					long startTime = Function.getInstance().getNowTimestamp();
					long compareTime = Function.getInstance().getNowTimestamp() - busOffSec;

					/* 自动获得车辆位置 */
					param.clear();
					param.put("line_id", lineId);
					List<Map<String, Object>> busList = commonDao.query(
							"select vehicle_id,log_time,direction,longitude,latitude,is_in_origin,is_in_line,vehicle_distance from sbus_vehicle where line_id=#{param.line_id}",
							param);
					List<Map<String, Object>> upBusList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> downBusList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> busvehicle : busList) {
						String busId = String.valueOf(busvehicle.get("vehicle_id"));
						Map<String, String> oldBusInfo = oldBusMap.get(busId);
						// 车辆数据获取时间小于compareTime
						if ((long) busvehicle.get("log_time") < compareTime) {
							if (!busvehicle.get("direction").toString().equals("0")
									&& !LineInfo.get(0).get("line_type").toString().equals("1")) {
								param.clear();
								param.put("direction", "0");
								param.put("vehicle_id", busId);
								commonDao.query(
										"update `sbus_vehicle` set `direction`=#{param.direction} where `vehicle_id`=#{param.vehicle_id}",
										param);
							}
							oldBusMap.remove(busId);
							continue;
						}
						// 原来的获取数据的时间和现在相同
						else if (oldBusInfo != null
								&& oldBusInfo.get("log_time").equals(busvehicle.get("log_time").toString())) {
							continue;
						}
						// 离起点站的距离小于车站半径
						else if (upStationList != null && upStationList.size() > 0
								&& Function.getInstance().Distance((double) busvehicle.get("longitude"),
										(double) busvehicle.get("latitude"),
										(double) upStationList.get(0).get("longitude"),
										(double) upStationList.get(0).get("latitude")) < Double
												.parseDouble(upStationList.get(0).get("radius").toString())
								|| downStationList != null && downStationList.size() > 0
										&& Function.getInstance().Distance((double) busvehicle.get("longitude"),
												(double) busvehicle.get("latitude"),
												(double) downStationList.get(0).get("longitude"),
												(double) downStationList.get(0).get("latitude")) < Double
														.parseDouble(downStationList.get(0).get("radius").toString())) {
							if ((int) busvehicle.get("is_in_origin") != 1 || (int) busvehicle.get("is_in_line") != 0) {
								if (!busvehicle.get("direction").toString().equals("0")
										&& !LineInfo.get(0).get("line_type").toString().equals("1")) {
									busvehicle.put("direction", (3 - (int) busvehicle.get("direction")));
								}
								param.clear();
								param.put("vehicle_id", busId);
								param.put("direction", String.valueOf(busvehicle.get("direction")));
								commonDao.execute(
										"update sbus_vehicle set direction=#{param.direction},is_in_origin='1',is_in_line='0' where vehicle_id=#{param.vehicle_id}",
										param);
							}
							oldBusMap.remove(busId);
							continue;
						}

						if (LineInfo.get(0).get("line_type").toString().equals("1")) {
							busvehicle.put("direction", "1");
						} else if (!busvehicle.get("direction").toString().equals("0") && oldBusInfo != null
								&& Integer.parseInt(oldBusInfo.get("reverse_num")) >= maxReverseNum) {
							busvehicle.put("direction", (3 - (int) busvehicle.get("direction")));
							oldBusInfo.put("reverse_num", "0");
							oldBusInfo.put("point_index", "0");
						} else if (busvehicle.get("direction").toString().equals("0") && oldBusInfo != null
								&& Function.getInstance().Distance((double) busvehicle.get("longitude"),
										(double) busvehicle.get("latitude"),
										Double.parseDouble(oldBusInfo.get("longitude")),
										Double.parseDouble(oldBusInfo.get("latitude"))) < minDistance) {
							oldBusInfo.put("log_time", String.valueOf(busvehicle.get("log_time")));
							continue;
						}
						if (!busvehicle.get("direction").toString().equals("2")) {
							upBusList.add(busvehicle);
						} else {
							downBusList.add(busvehicle);
						}
					}

					if (upBusList.size() > 0) {
						String oldLongitude = null;
						String oldLatitude = null;
						double sumDistance = 0;
						// 计算车辆离首站的距离，以及到达的线路点，离线路点的最小距离
						for (int i = 0; i < upLinePoint.length; i++) {
							String[] lnAndla = upLinePoint[i].split("-");
							if (oldLongitude != null) {
								sumDistance += Function.getInstance().Distance(Double.parseDouble(oldLongitude), // 获得每一个线路点到起点站的距离
										Double.parseDouble(oldLatitude), Double.parseDouble(lnAndla[0]),
										Double.parseDouble(lnAndla[1]));
							}
							oldLongitude = lnAndla[0];
							oldLatitude = lnAndla[1];
							for (Map<String, Object> busbus : upBusList) {
								double Distance = Function.getInstance().Distance((double) busbus.get("longitude"),
										(double) busbus.get("latitude"), Double.parseDouble(lnAndla[0]),
										Double.parseDouble(lnAndla[1]));

								if (busbus.get("minDistance") == null
										|| Distance < (double) busbus.get("minDistance")) {
									busbus.put("minDistance", Distance);
									busbus.put("vehicle_distance", new BigDecimal(sumDistance));
									busbus.put("point_index", i);
								}
							}
						}

						for (Map<String, Object> busbus : upBusList) {
							String busId = busbus.get("vehicle_id") + "";
							Map<String, String> oldBusInfo = oldBusMap.get(busId);
							// 判断车辆是否偏离线路
							if ((double) busbus.get("minDistance") > busLeaveDistance) {
								if (LineInfo.get(0).get("line_type").toString().equals("1")) {
									if (!busbus.get("is_in_line").toString().equals("0")) {
										param.clear();
										param.put("vehicle_id", busId);
										param.put("direction", "1");
										commonDao.execute(
												"update sbus_vehicle set `direction`=#{param.direction},is_in_line='0' where  vehicle_id=#{param.vehicle_id}",
												param);
									}
								} else {
									if (busbus.get("direction").toString().equals("1")) {
										param.clear();
										param.put("vehicle_id", busId);
										param.put("direction", "0");
										commonDao.execute(
												"update sbus_vehicle set `direction`=#{param.direction},is_in_line='0' where  vehicle_id=#{param.vehicle_id}",
												param);
									} else {
										busbus.put("minDistance", null);
										busbus.put("point_index", null);
										downBusList.add(busbus);
									}
								}
								oldBusMap.remove(busId);
								continue;
							}
							//
							if (!busbus.get("direction").toString().equals("1")) {
								if (oldBusInfo == null) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("longitude", busbus.get("longitude") + "");
									map.put("latitude", busbus.get("latitude") + "");
									map.put("log_time", busbus.get("log_time") + "");
									map.put("point_index", busbus.get("point_index") + "");
									map.put("reverse_num", "0");
									oldBusMap.put(busId, map);
									continue;
								}
								/**/
								if (Integer.parseInt(oldBusInfo.get("point_index")) > (int) busbus.get("point_index")) {
									busbus.put("minDistance", null);
									busbus.put("point_index", null);
									downBusList.add(busbus);
									continue;
								} else if (Integer
										.parseInt(oldBusInfo.get("point_index")) == (int) busbus.get("point_index")) {
									oldBusInfo.put("log_time", busbus.get("log_time") + "");
									continue;
								}
							}

							param.clear();
							param.put("direction", "1");
							param.put("vehicle_distance", busbus.get("vehicle_distance") + "");
							param.put("vehicle_id", busbus.get("vehicle_id") + "");
							commonDao.execute(
									"update `sbus_vehicle` set `direction`=#{param.direction},`is_in_origin`='0',`is_in_line`='1',`vehicle_distance`=#{param.vehicle_distance} where `vehicle_id`=#{param.vehicle_id }",
									param);
							if (!LineInfo.get(0).get("line_type").toString().equals("1") && oldBusInfo != null
									&& Function.getInstance().Distance((double) busbus.get("longitude"),
											(double) busbus.get("latitude"),
											Double.parseDouble(oldBusInfo.get("longitude")),
											Double.parseDouble(oldBusInfo.get("latitude"))) >= minDistance) {
								if (Integer.parseInt(oldBusInfo.get("point_index")) > (int) busbus.get("point_index")) {
									oldBusInfo.put("reverse_num",
											(Integer.parseInt(oldBusInfo.get("reverse_num")) + 1) + "");
								} else if (Integer
										.parseInt(oldBusInfo.get("point_index")) < (int) busbus.get("point_index")) {
									if (Integer.parseInt(oldBusInfo.get("reverse_num")) > 0) {
										oldBusInfo.put("reverse_num",
												(Integer.parseInt(oldBusInfo.get("reverse_num")) - 1) + "");
									}
								}
							}
							Map<String, String> map = new HashMap<String, String>();
							map.put("longitude", busbus.get("longitude").toString());
							map.put("latitude", busbus.get("latitude").toString());
							map.put("log_time", busbus.get("log_time").toString());
							map.put("point_index", busbus.get("point_index").toString());
							map.put("reverse_num", (oldBusInfo != null ? oldBusInfo.get("reverse_num") : "0"));
							oldBusMap.put(busId, map);
						}

					}
					/* 下行 */
					if (downLinePoint != null && downStationList.size() > 0) {
						String oldLongitude = null;
						String oldLatitude = null;
						double sumDistance = 0;
						for (int i = 0; i < downLinePoint.length; i++) {
							String[] lnAndla = downLinePoint[i].split("-");
							if (oldLongitude != null) {
								sumDistance += Function.getInstance().Distance(Double.parseDouble(oldLongitude),
										Double.parseDouble(oldLatitude), Double.parseDouble(lnAndla[0]),
										Double.parseDouble(lnAndla[1]));
							}
							oldLongitude = lnAndla[0];
							oldLatitude = lnAndla[1];
							for (Map<String, Object> busbus : downBusList) {
								double Distance = Function.getInstance().Distance((double) busbus.get("longitude"),
										(double) busbus.get("latitude"), Double.parseDouble(lnAndla[0]),
										Double.parseDouble(lnAndla[1]));
								if (busbus.get("minDistance") == null
										|| Distance < (double) busbus.get("minDistance")) {
									busbus.put("minDistance", Distance + "");
									busbus.put("point_index", i);
									busbus.put("vehicle_distance", new BigDecimal(sumDistance));
								}
							}
						}

						for (Map<String, Object> busbus : downBusList) {
							String busId = busbus.get("vehicle_id") + "";
							Map<String, String> oldBusInfo = oldBusMap.get(busId);
							if ((double) busbus.get("minDistance") > busLeaveDistance) {
								if (!busbus.get("direction").toString().equals("0")
										|| !busbus.get("is_in_line").toString().equals("0")) {
									param.clear();
									param.put("direction", "0");
									param.put("bus_id", busId);
									commonDao.execute(
											"update `sbus_vehicle` set `direction`=#{param.direction},`is_in_line`='0' where `bus_id`=#{param.bus_id}",
											param);
								}
								oldBusMap.remove(busId);
								continue;
							}
							param.clear();
							param.put("direction", "2");
							param.put("bus_id", busId);
							param.put("vehicle_distance", busbus.get("vehicle_distance") + "");
							commonDao.execute(
									"update `sbus_vehicle` set `direction`=#{param.direction},`is_in_origin`='0',`is_in_line`='1',`bus_distance`=#{param.bus_distance} where `bus_id`=#{param.bus_id}",
									param);
							if (!LineInfo.get(0).get("line_type").toString().equals("1") && oldBusInfo != null
									&& Function.getInstance().Distance((double) busbus.get("longitude"),
											(double) busbus.get("latitude"),
											Double.parseDouble(oldBusInfo.get("longitude")),
											Double.parseDouble(oldBusInfo.get("latitude"))) >= minDistance) {
								if (Integer.parseInt(oldBusInfo.get("point_index")) > (int) busbus.get("point_index")) {
									oldBusInfo.put("reverse_num",
											(Integer.parseInt(oldBusInfo.get("reverse_num")) + 1) + "");
								} else if (Integer
										.parseInt(oldBusInfo.get("point_index")) < (int) busbus.get("point_index")) {
									if (Integer.parseInt(oldBusInfo.get("reverse_num")) > 0) {
										oldBusInfo.put("reverse_num",
												(Integer.parseInt(oldBusInfo.get("reverse_num")) - 1) + "");
									}
								}
							}
							Map<String, String> map = new HashMap<String, String>();
							map.put("longitude", busbus.get("longitude") + "");
							map.put("latitude", busbus.get("latitude") + "");
							map.put("log_time", busbus.get("log_time") + "");
							map.put("point_index", busbus.get("point_index") + "");
							map.put("reverse_num", (oldBusInfo != null ? oldBusInfo.get("reverse_num") : "0"));
							oldBusMap.put(busId, map);
						}
					}

					/* 监控排班计划 */
					try {
						monitorShifts(lineId, upStationList);
					} catch (Exception e) {
						Start.projectLog.writeError(e);
					}

					long runSec = Function.getInstance().getNowTimestamp() - startTime;// 间隔时间
					runSec = runSec < execute_interval ? execute_interval - runSec : execute_interval;

					try {

						Thread.sleep(runSec);// 线程睡眠
					} catch (InterruptedException e) {
						Start.projectLog.writeError(e);
					}
				} catch (Exception e) {
					Start.projectLog.writeError(e);
				}
			}
		}

		// 监控排班方法
		public void monitorShifts(String lineId, List<Map<String, Object>> upStationList) {
			if (upStationList.size() <= 0) {
				return;
			}
			Date nowTime = new Date();
			long startTime = nowTime.getTime() / 1000;
			Map<String, String> param = new HashMap<String, String>();
			// 获得当前日期
			String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(nowTime);
			int index = updatedShiftsLineList.indexOf(lineId);
			// 判断是否日期发生变化
			if (!nowDate.equals(shiftsDate) || index != -1) {
				param.clear();
				param.put("line_id", lineId);
				param.put("nowDate", nowDate);
				shiftsList = commonDao.query(
						"select DATE_FORMAT(s.shifts_date,\"%Y-%m-%d\") as shifts_date,shifts_number,DATE_FORMAT(s.depart_time,\"%H:%i\") as depart_time,vehicle_id,in_origin_time,out_origin_time from sbus_shifts_real s where line_id=#{param.line_id} and shifts_date=#{param.nowDate} order by depart_time",
						param);
				// 修改日期
				shiftsDate = nowDate;
				if (index != -1) {
					updatedShiftsLineList.remove(index);// 从更新的列表中移除
				}

			}
			// 遍历shiftsList
			for (Iterator<Map<String, Object>> iterator = shiftsList.iterator(); iterator.hasNext();) {
				Map<String, Object> busShift = iterator.next();

				// 监控在时间范围内的班次
				// 获得排班的时间戳
				long shiftsTime = Function.getInstance().timeStrToSeconds(
						busShift.get("shifts_date") + " " + busShift.get("depart_time"), "yyyy-MM-dd HH:mm");
				// 获得班次的信息
				String vehicle_id = busShift.get("vehicle_id").toString();
				String shifts_number = busShift.get("shifts_number").toString();
				String shifts_date = busShift.get("shifts_date").toString();
				long in_origin_time = (long) busShift.get("in_origin_time");
				long out_origin_time = (long) busShift.get("out_origin_time");
				long calStartTime = shiftsTime - (int) ConfigController.configMap.get("vehicle_wait_minute") * 60
						- advanceMonitorTime;
				long calEndTime = shiftsTime + (int) ConfigController.configMap.get("depart_delay_minute") * 60;
				// 找到在监控范围内的计划
				if (startTime < calStartTime) {
					continue;
				}
				if (startTime > calEndTime) {
					iterator.remove();
					continue;
				}
				// 查询当前班次的车辆的信息
				param.clear();
				param.put("vehicle_id", vehicle_id);
				List<Map<String, Object>> nowVehicleList = commonDao.query(
						"select line_id,log_time,longitude,latitude,is_in_origin,is_in_line,vehicle_distance from sbus_vehicle where vehicle_id=#{param.vehicle_id}",
						param);
				if (nowVehicleList.size() <= 0) {
					iterator.remove();
					Start.projectLog.writeInfo("发生不可能的情况，请尽快处理！");
					continue;
				}
				Map<String, Object> nowVehicleInfo = nowVehicleList.get(0);
				String longitude = nowVehicleInfo.get("longitude").toString();
				String latitude = nowVehicleInfo.get("latitude").toString();
				long log_time = (long) nowVehicleInfo.get("log_time");
				// 判断时间是否大于有效时间
				if (log_time < calStartTime && log_time > calEndTime) {
					continue;
				}
				// 获得当前车辆离首站的距离
				double fromFirstStationDistance = Function.getInstance().Distance(Double.parseDouble(longitude),
						Double.parseDouble(latitude), (double) upStationList.get(0).get("longitude"),
						(double) upStationList.get(0).get("latitude"));
				// 获得首战的车辆半径
				int firstStationRadius = (int) upStationList.get(0).get("radius");
				System.out.println(fromFirstStationDistance <= firstStationRadius);
				// 判段是否到站
				if (fromFirstStationDistance <= firstStationRadius) {
					if (in_origin_time == 0 || out_origin_time > 0) {
						int result = 1;
						// 切换车辆线路
						param.clear();
						param.put("line_id", lineId);
						param.put("vehicle_id", vehicle_id);
						System.out.println(lineId.equals(nowVehicleInfo.get("line_id").toString()));
						if (!lineId.equals(nowVehicleInfo.get("line_id").toString())) {
							result = commonDao.execute(
									"update sbus_vehicle set line_id=#{param.line_id} where vehicle_id=#{param.vehicle_id}",
									param);
						}
						if (result > 0 && (in_origin_time == 0 || log_time < in_origin_time || out_origin_time > 0)) {
							// 更新到站时间
							param.clear();
							param.put("in_origin_time", String.valueOf(log_time));
							param.put("shifts_date", shifts_date);
							param.put("shifts_number", shifts_number);
							commonDao.execute(
									"update sbus_shifts_real set in_origin_time=#{param.in_origin_time},out_origin_time=0 where shifts_date=#{param.shifts_date} and shifts_number=#{param.shifts_number}",
									param);
							// 修改shiftsList的信息
							busShift.put("in_origin_time", log_time);
							busShift.put("out_origin_time", 0L);
						}
						continue;
					}
				} else {
					if (lineId.equals(nowVehicleInfo.get("line_id").toString()) && in_origin_time != 0
							&& (out_origin_time == 0 || log_time < out_origin_time && log_time >= in_origin_time)) {
						// 更新出站时间
						param.clear();
						param.put("out_origin_time", String.valueOf(log_time));
						param.put("shifts_date", shifts_date);
						param.put("shifts_number", shifts_number);
						commonDao.execute(
								"update sbus_shifts_real set  out_origin_time=#{param.out_origin_time} where shifts_date=#{param.shifts_date} and shifts_number=#{param.shifts_number}",
								param);
						// 修改shiftsList的信息
						busShift.put("out_origin_time", log_time);
						continue;
					}
				}

			}

		}
	}

}
