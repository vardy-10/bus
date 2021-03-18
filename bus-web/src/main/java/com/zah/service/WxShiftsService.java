package com.zah.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WxShiftsService {
	// 排班查询,根据起点站，终点站,时间查询
	public List<Map<String, Object>> ShiftsSearch(@Param("up_origin_name") String origin_name,
                                                  @Param("up_terminal_name") String up_terminal_name, @Param("shifts_date") String shifts_date);

	// 查询起点站，终点站
	public List<Map<String, Object>> SearchOriginName();

	// 查询起点站，终点站
	public List<Map<String, Object>> SearchTerminalName();

	// 查询班次及车票信息
	public List<Map<String, Object>> ShiftsTicketInfoSearch(@Param("up_origin_name") String origin_name,
                                                            @Param("up_terminal_name") String up_terminal_name, @Param("shifts_date") String shifts_date);

	// 查询根据班次，时间查询
	public List<Map<String, Object>> getShiftsList(@Param("shifts_number") String shifts_number,
                                                   @Param("shifts_date") String shifts_date);

	// 根据班次，日期，发车时间查询
	public List<Map<String, Object>> queryShiftInfo(@Param("shifts_number") String shifts_number,
                                                    @Param("shifts_date") String shifts_date);
}
