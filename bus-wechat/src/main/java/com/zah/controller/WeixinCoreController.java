package com.zah.controller;

import com.zah.service.impl.WeixinCoreServiceImpl;
import com.zah.util.WeiXinSignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/wechat")
public class WeixinCoreController {

	@Autowired
	private WeiXinSignUtil weixinSignUtil;
	@Autowired
	private WeixinCoreServiceImpl weixinCoreServiceImpl;

	@GetMapping(value = "")
	public String WeChatInterface(HttpServletRequest request) throws Exception {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");

		if (weixinSignUtil.checkSignature(signature, timestamp, nonce)) {

			return echostr;
		} else {
			// 此处可以实现其他逻辑
			return null;
		}
	}

	@PostMapping(value = "")
	public String getWeiXinMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
		request.setCharacterEncoding("UTF-8");
		// 在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
		response.setCharacterEncoding("UTF-8");
		String respXml = weixinCoreServiceImpl.weixinMessageHandelCoreService(request, response);
		if (respXml.isEmpty()) {

			return null;
		} else {

			return respXml;
		}
	}
}
