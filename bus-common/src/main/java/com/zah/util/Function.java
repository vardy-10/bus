package com.zah.util;


import com.alibaba.druid.util.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共函数（单例调用） Created by 54766 on 2019/9/26.
 */
public class Function {
	private static final Function instance = new Function();

	// 获取实例
	public static Function getInstance() {
		return instance;
	}

	// 获取项目路径
	public String getExeRealPath() {
		String filePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			filePath = URLDecoder.decode(filePath, "utf-8");// 转化为utf-8编码，支持中文路径
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		return new File(filePath).getAbsolutePath();
	}

	public String SHA1(String decript) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据特定格式将时间字符串转为秒
	 */
	public long timeStrToSeconds(String timeString, String pattern) {
		if (StringUtils.isEmpty(timeString)) {
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			long lo = sdf.parse(timeString).getTime() / 1000;
			return lo;
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 返回当前的时间戳(秒)
	 */
	public long getNowTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * SHA1和MD5加密方式
	 *
	 * @param decript
	 *            要加密的字符
	 * @param type
	 *            加密类型；参数类型：SHA-1/MD5
	 */
	public String SHA1AndMD5(String decript, String type) {
		try {
			if (type.equals("SHA-1")) {
				MessageDigest digest = MessageDigest.getInstance("SHA-1");
				digest.update(decript.getBytes());
				byte messageDigest[] = digest.digest();
				// Create Hex String
				StringBuffer hexString = new StringBuffer();
				// 字节数组转换为 十六进制 数
				for (int i = 0; i < messageDigest.length; i++) {
					String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
					if (shaHex.length() < 2) {
						hexString.append(0);
					}
					hexString.append(shaHex);
				}
				return hexString.toString();

			} else if (type.equals("MD5")) {
				char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
				String encodingStr = null;
				byte[] strTemp = decript.getBytes();
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(strTemp);
				byte[] md = mdTemp.digest();
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					str[k++] = hexDigits[byte0 & 0xf];
				}
				encodingStr = new String(str);
				return encodingStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将Integer转化为特定时间格式
	 */
	public String timestampToStr(long timestamp, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		long lo = timestamp * 1000;
		return sdf.format(new Date(lo));
	}

	/*
	 * 去除小数点后面多余的0
	 */
	public static String rvZeroAndDot(String s) {
		if (s.isEmpty()) {
			return null;
		}
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 格式化源时间格式为指定的时间格式
	 * 
	 * @param format
	 *            格式
	 * @param source
	 *            源时间格式
	 * @return 格式化后的时间，无法格式化或源时间格式不是一个能够让人理解的时间返回null
	 */
	public String date(String format, String source) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			Date date = df.parse(source);
			String result = df.format(date);
			if (!result.equals(source)) {
				StringBuilder sourceSb = new StringBuilder();
				StringBuilder resultSb = new StringBuilder();
				char[] chars = source.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] != '0' || i > 0 && Character.isDigit(chars[i - 1])) {
						sourceSb.append(chars[i]);
					}
				}
				chars = result.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] != '0' || i > 0 && Character.isDigit(chars[i - 1])) {
						resultSb.append(chars[i]);
					}
				}
				if (!resultSb.toString().equals(sourceSb.toString())) {
					return null;
				}
			}
			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得入参日期下周一的日期
	 *
	 * @param date
	 *            入参日期
	 * @return 入参日期的下周一
	 */
	public Date getNextMonday(Date date) {
		// 获得入参的日期
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);

		// 获得入参日期是一周的第几天
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		// 获得入参日期相对于下周一的偏移量（在国外，星期一是一周的第二天，所以下周一是这周的第九天）
		// 若入参日期是周日，它的下周一偏移量是1
		int nextMondayOffset = dayOfWeek == 1 ? 1 : 9 - dayOfWeek;

		// 增加到入参日期的下周一
		cd.add(Calendar.DAY_OF_MONTH, nextMondayOffset);
		return cd.getTime();
	}

	public List<Map<String, Object>> get_midpoint_array2(double x1, double y1, double x2, double y2, double distaence) {
		List<Map<String, Object>> listmap = new ArrayList<>();
		if (distaence > 0) {
			double a = x2 - x1;
			double b = y2 - y1;
			double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
			for (double ll = distaence; ll < c; ll += distaence) {
				Map<String, Object> map = new HashMap<>();
				double x3 = a / c * ll + x1;
				double y3 = b / c * ll + y1;
				map.put("LONGITUDE", x3);
				map.put("LATITUDE", y3);
				listmap.add(map);
			}
		}
		return listmap;
	}

	/**
	 * 计算地球上任意两点(经纬度)距离
	 *
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public double Distance(double long1, double lat1, double long2, double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		return d;
	}

	public static String ltrim(String source, char element) {
		int len = source.length();
		int st = 0;
		char[] val = source.toCharArray();
		while ((st < len) && val[st] == element) {
			st++;
		}
		return (st > 0) ? source.substring(st) : source;
	}

	/**
	 * 去除数据的空格、回车、换行符、制表符
	 * 
	 * @param str
	 * @return
	 */
	public String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 日期转换成周几
	 * 
	 * @param date
	 * @return
	 */
	public String getWeek(String date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String[] weeks = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(simpleDateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return weeks[week_index];
	}

	public static void main(String[] args) throws ParseException {
		// System.out.println(getUTC());
		// Set<Map<String, String>> set = new HashSet<>();
		// Map<String, String> map1 = new HashMap<>();
		// map1.put("a", "4");
		// map1.put("b", "2");
		// Map<String, String> map2 = new HashMap<>();
		// map2.put("a", "1");
		// map2.put("b", "3");
		// set.add(map1);
		// set.add(map2);
		// System.out.println(JSON.toJSON(set));
		// String num = String.valueOf((int) (Math.random() * 9000) + 1000);
		// System.out.println(String.valueOf((int) (Math.random() * 10)) +
		// String.valueOf((int) (Math.random() * 10))
		// + String.valueOf((int) (Math.random() * 10)) + String.valueOf((int)
		// (Math.random() * 10)));
	}

}
