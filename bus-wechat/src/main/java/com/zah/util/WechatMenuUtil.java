package com.zah.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zah.entity.WechatConstants;
import com.zah.entity.button.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class WechatMenuUtil {
	@Autowired
	private HttpRequestUtil httpRequestUtil;

	public static int createMenu(Menu menu, String accessToken) {
		String jsonMenu = JSON.toJSONString(menu).toString();
		int status = 0;
		String path = WechatConstants.MENU_CREATE_URL.replace("ACCESS_TOKEN", accessToken);
		try {
			URL url = new URL(path);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setDoOutput(true);
			http.setDoInput(true);
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(jsonMenu.getBytes("UTF-8"));
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] bt = new byte[size];
			is.read(bt);
			String message = new String(bt, "UTF-8");
			JSONObject jsonMsg = JSON.parseObject(message);
			status = Integer.parseInt(jsonMsg.getString("errcode"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}


	public String getMenu(String accessToken) {
		String result = null;
		String requestUrl = WechatConstants.MENU_GET_URL.replace("ACCESS_TOKEN", accessToken);
		// 发起GET请求查询菜单
		JSONObject jsonObject = httpRequestUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			result = jsonObject.toString();
		}
		return result;
	}

	public boolean deleteMenu(String accessToken) {
		boolean result = false;
		String requestUrl = WechatConstants.MENU_DELETE_URL.replace("ACCESS_TOKEN", accessToken);
		// 发起GET请求删除菜单
		JSONObject jsonObject = httpRequestUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInteger("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
}
