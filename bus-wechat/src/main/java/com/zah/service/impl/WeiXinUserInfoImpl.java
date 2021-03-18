package com.zah.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.zah.entity.WechatConstants;
import com.zah.entity.WechatUserInfo;
import com.zah.service.WeiXinUserInfoService;
import com.zah.util.HttpRequestUtil;
import com.zah.util.wxFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinUserInfoImpl implements WeiXinUserInfoService {
	@Autowired
	private HttpRequestUtil httpRequestUtil;

	@Override
	public WechatUserInfo getUserInfo(String accessToken, String openId) {
		WechatUserInfo wechatUserInfo = null;
		// 拼接获取用户信息接口的请求地址
		String requestUrl = WechatConstants.GET_USERINFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID",
				openId);
		System.out.println("requestUrl" + requestUrl);
		// 获取用户信息(返回的是Json格式内容)
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.parseObject(
					wxFunction.getInstance().request(requestUrl, null, null, null, null, null, null, "UTF-8", 0, 0));
			System.out.println(jsonObject);
			if (null != jsonObject) {

				// 封装获取到的用户信息
				wechatUserInfo = new WechatUserInfo();
				// 用户的标识
				wechatUserInfo.setOpenId(jsonObject.getString("openid"));
				// 昵称
				wechatUserInfo.setNickname(jsonObject.getString("nickname"));
				// 用户的性别（1是男性，2是女性，0是未知）
				wechatUserInfo.setSex(jsonObject.getIntValue("sex"));
				// 用户所在国家
				wechatUserInfo.setCountry(jsonObject.getString("country"));
				// 用户所在省份
				wechatUserInfo.setProvince(jsonObject.getString("province"));
				// 用户所在城市
				wechatUserInfo.setCity(jsonObject.getString("city"));
				// 用户头像
				wechatUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
			}
		} catch (Exception e) {
			int errorCode = jsonObject.getIntValue("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			System.out.println("由于" + errorCode + "错误码；错误信息为：" + errorMsg + "；导致获取用户信息失败");
		}
		return wechatUserInfo;
	}

	/**
	 * 进行用户授权，获取到需要的授权字段，比如openId
	 * 
	 * @param code
	 *            识别得到用户id必须的一个值 得到网页授权凭证和用户id
	 * @return
	 */
	@Override
	public Map<String, String> oauth2GetOpenid(String code) {
		// 自己的配置appid（公众号进行查阅）
		String appid = WechatConstants.APPID;
		// 自己的配置APPSECRET;（公众号进行查阅）
		String appsecret = WechatConstants.APPSECRET;
		// 拼接用户授权接口信息
		String requestUrl = WechatConstants.AUTH_INFO.replace("APPID", appid).replace("SECRET", appsecret)
				.replace("CODE", code);
		// 存储获取到的授权字段信息
		Map<String, String> result = new HashMap<String, String>();
		try {
			JSONObject OpenidJSONO = httpRequestUtil.httpsRequest(requestUrl, "GET", null);
			// OpenidJSONO可以得到的内容：access_token expires_in refresh_token openid scope
			String Openid = String.valueOf(OpenidJSONO.get("openid"));
			System.out.println("openID" + Openid);
			String AccessToken = String.valueOf(OpenidJSONO.get("access_token"));
			// 用户保存的作用域
			String Scope = String.valueOf(OpenidJSONO.get("scope"));
			String refresh_token = String.valueOf(OpenidJSONO.get("refresh_token"));
			result.put("Openid", Openid);
			result.put("AccessToken", AccessToken);
			result.put("scope", Scope);
			result.put("refresh_token", refresh_token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取到微信用户的唯一的OpendID
	 * 
	 * @param code
	 *            这是要获取OpendId的必须的一个参数
	 * @return
	 */
	@Override
	public Map<String, String> getAuthInfo(String code) {
		// 进行授权验证，获取到OpenID字段等信息
		Map<String, String> result = oauth2GetOpenid(code);
		// 从这里可以得到用户openid
		String openId = result.get("Openid");

		return result;
	}
	

}
