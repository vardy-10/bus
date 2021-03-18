package com.zah.util;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

public class CCPlayUtils {

	private static final CCPlayUtils instance = new CCPlayUtils();

	// 获取实例
	public static CCPlayUtils getInstance() {
		return instance;
	}

	/**
	 * 支付生成签名
	 * 
	 * @param postData
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getSign(Map<String, Object> postData) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Object> entry : postData.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue().toString();
			sb.append("&" + mapKey + "=" + mapValue);
		}
		String requestData = sb.substring(1);
		String signature = null;
		byte[] signed = null;
		String key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQD05t2u4g38u5VSlnZ3VzBzDjxbDXaR8CKVGduiKMMPf+JDM9SBDyQcRvO20uUWCtLTSZD+uryHaZMrbvYQ1wZaDF/vkOiReBb5q4RKbFmIwKAvmZ9WuzEdkFwf0zHWC75hdwbF1/uKVS7EbjfjdkIjNSb81RxRJlPrOBxmcJqcVuiPAR62ydPDgT3bKkhOsvlpoDySdAwX6Tkdz+v82JRtajrVkj+FCIzXux6TPzmprs3WjaDzxmPFmKa1i8pCB/YMsYwt8rs/C0znFvvhKQ11m8ru2sFHgJ808hKxiRU9ckQXKaTCV+cF9PJsehwyTIFbNR1EbVt2zPYBW0INKNc1AgMBAAECggEAaYVoE0hXmoQAzPsDxFwPBiQmIV1lr2/2lqPznKVPlsgVZ7tXXtx1RLHVpPqttpenS16xWPCA9x+oR9ihsZPUqswWMhUQAE3sKFucvqx8z42f96KLo0U43nIrH5NSL9YTi89LXce4HFyjsAOFlT6JNL5cNChm8RhApGMi6YivNZjtCRBGcfw3WVGxuIcNS12IsCCwlBMr2onbreO1bjOKPzz0I/j5n0r26Ott5LvJ9OX6i/wQxkCFM0NTYFB72rWvm8r6FhDGO4aZzL2w1Ndtkm/oUCTVSPOP4m5Uao0azZDvGVV6RGNDCDnxWYdCnGHNa5Pey36tuSeAffM+68tRwQKBgQD6wkgANPInQZeR4LqZ4cfqAw1xcvDjlaaONLHPEJ8eDHGagekytWKKqpglGRYP0AFyO7QjVqx5w4z3zPDwFoUVht+EVKX+uOoyr/YQvHErvBKBM7LX6j6JXC5ESSqXt7uLMnS+fkxP6tfTrTDDFTBGd7+ldJpJofg4VlcWHcmD3QKBgQD6BT7cvdV8aJd0Jimj/vqUj+tJkN+TXGd9rLDJHoyJjhJcUy3G6VFHGHapn8lnfdK2xVgv+KdvqG8878lJJIRAjWmKFCEEuKLpoO5ad3NN5imxAhgttEg5cGpkRoWR3CGevsUmJQbBHxgHaqksHUULycp70xxv6vDB5c0iY243OQKBgGvMxcNA/7Eco5pU8CNjStwxP8A7148fMKEFykLoNF9onhX8L1JbY+eU+x9Hr5JQq65+OCzRAAjApoOJGTM9myzr9H8hHNNqj1QHDVOYj1Zr3dCpdwMXcpb+h+1XPvZGSFvI7yNNjGT24p3fYI0dOMmyhTMN854gAWPGyl15pL4JAoGAX42Xq9elesmicJKRQYsWi6bBXrlOqUwchWx1mDfjsEKrA7wKd8X9+TUBTOyzlakB2jZr8NGQdF0kPl7KR/j7zov9NPdicyJ5qN6Hme2jFLeNYYgSY41OOR2cm7xUGh3Of5QbVuu5vB89GMCL7T82fz3c9BwH90IMR56jGqxzmAkCgYBjd3HgIcFEIRVA8h4hxfDghLd91RZo8jHMCxlJKLfciA4LFeTgAY8TdfnuWWcvz5j4fYyQ1Z1ygacry3YkoABLFqPm5pCs8z79bxCx5vMWrDjBBR7kl8xhLWYmJ6jnceMcunml8cNtFma795OHkU3ihjEsviosDakn9OtLuUEJcA==";
		try {
			byte[] byteKey = Base64.getDecoder().decode(key);
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
			Signature Sign = Signature.getInstance("SHA256withRSA");
			Sign.initSign(privateKey);
			Sign.update(requestData.getBytes());
			signed = Sign.sign();
			signature = Base64.getEncoder().encodeToString(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signature;
	}

	/**
	 * 获取当前utc时间
	 * 
	 * @return
	 * @throws ParseException
	 */
	public String getUTC() {
		StringBuffer UTCTimeBuffer = new StringBuffer();
		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance();
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		String year = cal.get(Calendar.YEAR) + "";
		String month = (cal.get(Calendar.MONTH) + 1) + "";
		String day = cal.get(Calendar.DAY_OF_MONTH) + "";
		String hour = cal.get(Calendar.HOUR_OF_DAY) + "";
		String minute = cal.get(Calendar.MINUTE) + "";
		String second = cal.get(Calendar.SECOND) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		if (day.length() < 2) {
			day = "0" + day;
		}
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		if (minute.length() < 2) {
			minute = "0" + minute;
		}
		if (second.length() < 2) {
			second = "0" + second;
		}
		UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day);
		UTCTimeBuffer.append("T").append(hour).append(":").append(minute).append(":").append(second + "Z");
		return UTCTimeBuffer.toString();
	}
}
