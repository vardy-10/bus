package com.zah.util;

import com.zah.entity.message.Article;
import com.zah.entity.message.NewsMessage;
import com.zah.entity.message.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class WeixinMessageModelUtil {
	
	@Autowired
	private WeixinMessageUtil weixinMessageUtil;



	public String systemErrorResponseMessageModel(WeixinMessageInfo weixinMessageInfo ){
		
		// 回复文本消息
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(weixinMessageInfo.getFromUserName());
		textMessage.setFromUserName(weixinMessageInfo.getToUserName());
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setFuncFlag(0);
		textMessage.setContent("系统出错啦，请稍后再试");	
		return weixinMessageUtil.textMessageToXml(textMessage);
	}
	
	public String followResponseMessageModel(WeixinMessageInfo weixinMessageInfo){
	
		// 关注时发送图文消息
		NewsMessage newsMessage = new NewsMessage();
		newsMessage.setToUserName(weixinMessageInfo.getFromUserName());
		newsMessage.setFromUserName(weixinMessageInfo.getToUserName());
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(weixinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
		newsMessage.setFuncFlag(0);
	     
		// 图文消息
		List<Article> articleList=new ArrayList<Article>();
		Article article = new Article();
		// 设置图文消息的标题
		String string = "欢迎关注！！！！"+new Date();
		article.setTitle(string);

		articleList.add(article);
		newsMessage.setArticleCount(articleList.size());
		newsMessage.setArticles(articleList);
		return weixinMessageUtil.newsMessageToXml(newsMessage);
	}
	

	public void cancelAttention(String fromUserName){
		
	
}
}
