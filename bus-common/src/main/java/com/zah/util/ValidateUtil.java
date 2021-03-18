package com.zah.util;


import com.alibaba.druid.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {
	private static final ValidateUtil instance = new ValidateUtil();

	// 获取实例
	public static ValidateUtil getInstance() {
		return instance;
	}

	/**
	 * 判断字符串是否由字母数字下划线组成
	 *
	 * @param str
	 * @return true:合法 false:不合法
	 */
	public boolean isUserName(String str) {
		if (!"".equals(str)) {

			String regex = "^[0-9a-zA-Z_]+$";
			return str.matches(regex);
		}
		return false;
	}

	/**
	 * 判断字符串是否为数字 正整数
	 *
	 * @param str
	 * @return
	 */
	public boolean isDigit(String str) {
		try {
			if (str == null || str.equals("")) {
				return false;
			}
			for (int i = str.length(); --i >= 0;) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
			// TODO: handle exception
		}
	}

	/**
	 * 判断字符串是数字 可以是浮点小数
	 *
	 * @param str
	 * @return 合法返回true 否则返回false
	 */
	public boolean isNumber(String str) {
		try {
			BigDecimal db = new BigDecimal(str);
			str = db.toPlainString();
			Pattern pt = Pattern.compile("^[+-]{0,1}[0-9]+([\\.][0-9]+)?$");
			Matcher mt = pt.matcher(str);
			if (mt.matches()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	/**
	 * 判断字符串是否为整数
	 * 
	 * @param str
	 * @return
	 */
	public boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断日期格式是否正确
	 * 
	 * @param
	 */
	public boolean isTime(String time, String parten) {
		try {
			if ("".equals(time) || null == time) {
				return false;
			} else {
				SimpleDateFormat format = new SimpleDateFormat(parten);
				Date dateS = format.parse(time);
				new Long(dateS.getTime() / 1000).intValue();
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	/**
	 * 密码格式验证
	 */
	public boolean isPassword(String str) {
		try {
			if (StringUtils.isEmpty(str)) {
				return false;
			}
			if (str.length() < 8) {
				return false;
			}
			boolean isSymbol = false;
			boolean isNumber = false;
			boolean isLetter = false;
			for (int i = str.length(); --i >= 0;) {
				int value = (int) str.charAt(i);
				if (value >= 33 && value <= 47 || value >= 58 && value <= 64 || value >= 91 && value <= 96
						|| value >= 123 && value <= 126l) {
					isSymbol = true;
				} else if (value >= 48 && value <= 57) {
					isNumber = true;
				} else if (value >= 65 && value <= 90 || value >= 97 && value <= 122) {
					isLetter = true;
				} else {
					return false;
				}
			}
			if (isLetter && isNumber && isSymbol) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static void main(String[] args) {
		String str = "11111";
		System.out.println(ValidateUtil.getInstance().isInteger(str));
	}
}
