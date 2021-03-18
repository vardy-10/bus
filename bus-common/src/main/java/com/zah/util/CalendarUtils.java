package com.zah.util;

import java.util.Calendar;

public class CalendarUtils {
	private static final CalendarUtils instance = new CalendarUtils();

	// 获取实例
	public static CalendarUtils getInstance() {
		return instance;
	}

	/**
	 * 将时间戳转换成当天零点的时间戳
	 *
	 * @param milliseconds
	 * @return
	 */
	public Calendar zeroFromHour(int milliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(completMilliseconds(milliseconds));
		zeroFromHour(calendar);
		return calendar;
	}

	/**
	 * 将时，分，秒，以及毫秒值设置为0
	 *
	 * @param calendar
	 */
	public void zeroFromHour(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 由于服务器返回的是10位，手机端使用需要补全3位
	 *
	 * @param milliseconds
	 * @return
	 */
	private long completMilliseconds(int milliseconds) {
		String milStr = Integer.toString(milliseconds);
		if (milStr.length() == 10) {
			milliseconds = milliseconds * 1000;
		}
		return milliseconds;
	}
	 /**
     * 最终调用方法
     * @param timeStamp
     * @return
     */

    public static String getWhatDay (int timeStamp) {
        Calendar cal = CalendarUtils.getInstance().zeroFromHour(timeStamp);
        String whatDay="";
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            whatDay="星期六";
        }
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            whatDay="星期日";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            whatDay = "星期一";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            whatDay = "星期二";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            whatDay = "星期三";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            whatDay = "星期四";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            whatDay = "星期五";
        }
        return whatDay;
    }

}
