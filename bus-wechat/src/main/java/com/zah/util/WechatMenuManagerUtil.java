package com.zah.util;

import com.zah.entity.button.Button;
import com.zah.entity.button.ComplexButton;
import com.zah.entity.button.Menu;
import com.zah.entity.button.ViewButton;

public class WechatMenuManagerUtil {

	public Menu getMenu() {


		String getOauth2url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc1bd0aa0b40168b5&redirect_uri=http%3A%2F%2F119.29.137.92%2Fwechat%2Foauth&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";

		ViewButton cb_1 = new ViewButton();
		cb_1.setName("订票须知");
		cb_1.setType("view");
		cb_1.setUrl("http://119.29.137.92/wechat/buyTicketAttention");

		ViewButton cb_2 = new ViewButton();
		cb_2.setName("身份认证");
		cb_2.setType("view");
		cb_2.setUrl(getOauth2url);
		ViewButton cb_3 = new ViewButton();
		cb_3.setName("班次查询");
		cb_3.setType("view");
		cb_3.setUrl("http://119.29.137.92/wechat/ShiftTheQuery");
		// ViewButton cb_4 = new ViewButton();
		// cb_4.setName("我要购票");
		// cb_4.setType("view");
		// cb_4.setUrl("http://zah123.ngrok2.xiaomiqiuu.cn/wechat/buyTicket");

		ComplexButton cx_1 = new ComplexButton();
		cx_1.setName("微信订票");
		cx_1.setSub_button(new Button[] { cb_1, cb_2, cb_3 });

		ViewButton cb_5 = new ViewButton();
		cb_5.setName("未出行订单");
		cb_5.setType("view");
		cb_5.setUrl("http://119.29.137.92/wechat/waitingOrder");

		ViewButton cb_6 = new ViewButton();
		cb_6.setName("历史订单");
		cb_6.setType("view");
		cb_6.setUrl("http://119.29.137.92/wechat/orderHistory");
		ViewButton cb_7 = new ViewButton();
		cb_7.setName("退款申请");
		cb_7.setType("view");
		cb_7.setUrl("http://119.29.137.92/wechat/refundOrder");

		ComplexButton cx_2 = new ComplexButton();
		cx_2.setName("我的订单");
		cx_2.setSub_button(new Button[] { cb_5, cb_6, cb_7 });

		ViewButton cb_8 = new ViewButton();
		cb_8.setName("投诉建议");
		cb_8.setType("view");
		cb_8.setUrl("http://119.29.137.92/wechat/contactUs");
		ViewButton cb_9 = new ViewButton();
		cb_9.setName("通知公告");
		cb_9.setType("view");
		cb_9.setUrl("http://119.29.137.92/wechat/notification");
		ViewButton cb_10 = new ViewButton();
		cb_10.setName("失物招领");
		cb_10.setType("view");
		cb_10.setUrl("http://119.29.137.92/wechat/retrieve");
		ComplexButton cx_3 = new ComplexButton();
		cx_3.setName("微服务");
		cx_3.setSub_button(new Button[] { cb_8, cb_9, cb_10 });

		Menu menu = new Menu();
		menu.setButton(new ComplexButton[] { cx_1, cx_2, cx_3 });

		return menu;
	}
}
