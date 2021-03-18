package com.zah.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ailiuyi 分页类
 */
public class PageClass {
	public int pageSize;
	public int currentPage;
	public HttpServletRequest request;
	public String url;
	public int totalCount;
	public int lastPage;
	public int currentResult;
	/**
	 *	分页类的构造方法
	 * @param pageSize 每页多少条数据
	 * @param currentPage 当前页
	 * @param url 下页的链接地址
	 */
	public PageClass(int pageSize,int currentPage,String url){
		this.pageSize=pageSize;
		this.currentPage=currentPage<=0?1:currentPage;
		this.url=url;
		this.currentResult = (this.currentPage - 1) * pageSize;
	}

	public void  init(int totalCount){
		this.totalCount=totalCount;
	     if (totalCount % pageSize==0){
	         lastPage = totalCount / pageSize;
	     }
	     else{
	         lastPage =1+ totalCount / pageSize;
	     }
	     if (currentPage>=lastPage){
	         currentPage =lastPage;
	     }
	     
	}
	
	 
	 public String show(){
	     String pageStr = "";
	     pageStr="<span>"+currentPage+"/"+lastPage+" 共有"+totalCount+"条信息  每页显示"+pageSize+"条</span>   <a  href='"+url+"&page=1'>首页</a> <a  href='"+url+"&page="+(currentPage-1)+"'>上一页</a>    <a  href='"+url+"&page="+((currentPage+1)>lastPage?lastPage:(currentPage+1))+"'>下一页</a>"
		     		+    "   <a  href='"+url+"&page="+(lastPage)+"'> 尾页</a> <input id='page' type='text' name='page' value='"+currentPage+"'> <input type='button' value='跳转' class='button' onclick=\"var i=document.getElementById('page').value; var where=''; if(i>"+lastPage+"){where="+lastPage+"}else{where=i};  location.href='"+url+"&page='+where\" > <input type='hidden' name='countPage' value='"+lastPage+"' >";
		     	return pageStr;
	 }
	 
	 public String showMap(){
	     String pageStr = "";
	     pageStr=String.format("<form action='" + url + "' method='get'> <span>"+currentPage+"/"+lastPage+" 共有"+totalCount+"条信息</span>   <a onfocus='this.blur();' href=\"%s\">首页</a> <a onfocus='this.blur();' href=\"%s\">上一页</a>    <a onfocus='this.blur();' href=\"%s\">下一页</a>"
	     		+ "  <a onfocus='this.blur();' href=\"%s\">尾页</a> </form>",
	     		url+"&page="+1, url+"&page="+(currentPage-1),
	     		url+"&page="+((currentPage+1)>lastPage?lastPage:(currentPage+1)), url+"&page="+(lastPage) );
	     	return pageStr;
	 }
}
