package com.zah.thread;


import com.zah.controller.ConfigController;
import com.zah.dao.CommonDao;
import com.zah.util.Function;
import com.zah.util.ProjectLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目启动运行类（项目启动时调用) Created by 54766 on 2019/6/24.
 */
@Component
@Order(1)
public class Start implements CommandLineRunner {
	public static Timer timer = new Timer();
	public static ProjectLog projectLog;

	@Autowired
	CommonDao commonDao;
	@Autowired
	BusPlaceAndShifts busPlaceAndShifts;


	@Override
	public void run(String... args) throws Exception {

		// 启动系统日志
		projectLog = new ProjectLog(Function.getInstance().getExeRealPath() + "/logs");
		projectLog.setDebug(true);// 将日志输出至控制台
		// 数据能够查询到数据放在循环下面(mybatis连接不上数据库时，内部会自动重连数据库，不需要循环连接)
		try {
			List<Map<String, Object>> configList = commonDao.query("select * from sbus_config", null);
			if (configList.size() > 0) {
				ConfigController.configMap.putAll(configList.get(0));
			}
		} catch (Exception e) {
			Start.projectLog.writeInfo("*出现了不可能的错误!");
			Start.projectLog.writeError(e);
		}
		// 启动车辆位置转换
		/* busPlaceAndShifts.init(); */

	}
}
