package com.zah.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class wxFunction {
	private static final wxFunction instance = new wxFunction();

	// 获取实例
	public static wxFunction getInstance() {
		return instance;
	}
	public String request(String url, String contentType, Map<String, String> cookieData, Map<String, String> basic,
			Map<String, Object> getData, Map<String, Object> postData, String putContent, String encode,
			int connectTimeout, int readTimeout) throws Exception {
		// 整理请求数据
		if (getData != null && !getData.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, Object> entry : getData.entrySet()) {
				if (entry.getValue() instanceof String[]) {
					String key = URLEncoder.encode(entry.getKey(), encode);
					String[] arr = (String[]) entry.getValue();
					for (int i = 0; i < arr.length; i++) {
						sb.append("&" + key + "=" + URLEncoder.encode(arr[i], encode));
					}
				} else {
					sb.append("&" + URLEncoder.encode(entry.getKey(), encode) + "="
							+ URLEncoder.encode(entry.getValue().toString(), encode));
				}
			}
			url += url.indexOf("?") < 0 ? "?" + sb.substring(1) : sb.toString();
		}
		String postContent = null;
		if (postData != null && !postData.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, Object> entry : postData.entrySet()) {
				if (entry.getValue() instanceof String[]) {
					String key = URLEncoder.encode(entry.getKey(), encode);
					String[] arr = (String[]) entry.getValue();
					for (int i = 0; i < arr.length; i++) {
						sb.append("&" + key + "=" + URLEncoder.encode(arr[i], encode));
					}
				} else {
					sb.append("&" + URLEncoder.encode(entry.getKey(), encode) + "="
							+ URLEncoder.encode(entry.getValue().toString(), encode));
				}
			}
			postContent = sb.substring(1);
		}
		URL requestUrl = new URL(url);// 建立请求
		HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();// 打开请求连接
		if (cookieData != null && cookieData.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : cookieData.entrySet()) {
				sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), encode) + ";");
			}
			connection.setRequestProperty("Cookie", sb.toString());
		}
		if (basic != null) {
			// 增加http基础验证参数
			String basicStr = basic.get("username") + ":" + basic.get("password");
			byte[] b = basicStr.getBytes(encode);
			Class<?> clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			Method mainMethod = clazz.getMethod("encode", byte[].class);
			mainMethod.setAccessible(true);
			Object retObj = mainMethod.invoke(null, new Object[] { b });
			basicStr = (String) retObj;
			connection.setRequestProperty("Authorization", "Basic " + basicStr);
		}
		if (contentType == null || contentType.equals("form")) {
			// 配置连接的Content-type，配置为application/x-www-form-urlencoded的意思是正文是urlencoded编码过的form参数
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		} else if (contentType.equals("json")) {
			// 配置连接的Content-type，配置为application/json;charset=UTF-8的意思是正文是UTF-8编码过的JSON数据格式
			connection.setRequestProperty("Content-Type", "application/json;charset=" + encode);
		}
		if (connectTimeout >= 0)
			connection.setConnectTimeout(connectTimeout);// 设置连接超时时间
		if (readTimeout >= 0)
			connection.setReadTimeout(readTimeout);// 设置读取超时时间
		if (postContent == null && (putContent == null || putContent.length() == 0)) {
			connection.connect();// 建立与服务器的连接，并未发送数据
		} else {
			connection.setDoOutput(true);// 打开写
			connection.setDoInput(true);// 打开读
			if (putContent != null && putContent.length() > 0) {
				connection.setRequestMethod("PUT");// 设置请求方式
			} else {
				connection.setRequestMethod("POST");// 设置请求方式
			}
			connection.setUseCaches(false);// 请求不能使用缓存
			connection.setInstanceFollowRedirects(true);// 设置请求连接自动处理重定向
			// 由于connection.getOutputStream()会隐含的进行调用connect()，所以这里可以省略connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());// 获取连接输出流
			if (putContent != null && putContent.length() > 0) {
				out.write(putContent.getBytes());// 将put请求参数写入输出流
			} else {
				out.writeBytes(postContent);// 将post请求参数写入输出流
			}
			out.flush();
			out.close();
		}
		String content_encode = connection.getContentEncoding();
		InputStream is = connection.getInputStream();// 获取连接输入流
		if (null != content_encode && !"".equals(content_encode) && content_encode.contains("gzip")) {
			is = new GZIPInputStream(is);// 获取连接输入流
		}
		// 逐行读取输入信息
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = is.read(b)) != -1) {
			out.write(b, 0, len);
		}
		is.close();
		connection.disconnect();
		return out.toString(encode);
	}
}
