package com.zah.util;

import com.alibaba.fastjson.JSONObject;
import com.zah.entity.WechatConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WechatCommonUtil {

	// 获取access_token接口
	private static String token_url = WechatConstants.ACCESS_TOKEN_URL;
	private static String appid = WechatConstants.APPID;
	private static String appsecret = WechatConstants.APPSECRET;
	public static AccessToken ACCESS_TOKEN;
	@Autowired
	HttpRequestUtil httpRequestUtil;

	@Scheduled(fixedRate = 1000 * 7000)
	public static AccessToken getAccessToken() throws Exception {
		// 将公众号的appid和appsecret替换进url
		String url = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);

		// 发起get请求获取凭证
		String get = wxFunction.getInstance().request(url, null, null, null, null, null, null, "UTF-8", 0, 0);

		JSONObject jsonObject = JSONObject.parseObject(get);
		if (jsonObject != null) {
			try {
				ACCESS_TOKEN = new AccessToken(jsonObject.getString("expires_in"),
						jsonObject.getString("access_token"));
			} catch (Exception e) {
				e.printStackTrace();
				ACCESS_TOKEN = null;
				// 获取token失败
			}
		}
		return ACCESS_TOKEN;
	}

//	public static OauthAccessToken getOuthAccessToken(String code) throws Exception {
//		String url = outhAccess_Token_url.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
//		System.out.println(url);
//		String get = Function.getInstance().request(url, null, null, null, null, null, null, "UTF-8", 0, 0);
//		System.out.println(JSON.toJSON(get));
//		JSONObject jsonObject = JSONObject.parseObject(get);
//		System.out.println("shijian" + jsonObject.getString("expires_in"));
//		if (jsonObject != null) {
//			try {
//				outhAccess_Token = new OauthAccessToken(jsonObject.getString("expires_in"),
//						jsonObject.getString("access_token"));
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				outhAccess_Token = null;
//				// 获取token失败
//			}
//		}
//		return null;
//
//	}
}
