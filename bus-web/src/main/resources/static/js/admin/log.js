$(function(){
	//取消按钮事件
	$("#cancel").click(function() {
		$("#log_right").hide(); 
		$("input[name=clearUsername]").val("");
		$("input[name=clearStartTime]").val("");
		$("input[name=clearEndTime]").val("	"); 
		return false;
		
	});
	$("#remove").click(function() {
		layer.confirm('确认清除吗？', {
			btn : [ '删除', '取消' ]
		// 按钮
		}, function() {
			//发送ajax请求
			$.ajax({
				url : 'deleteLog',
				data : {"username":$("input[name=clearUsername]").val(),
					"startTime":$("input[name=clearStartTime]").val(),
					"endTime":$("input[name=clearEndTime]").val()},
				dataType : 'json',
				type : 'post',
				success : function(data) {
					if (!data.success) {
						layer.msg(data.message, {
							icon : 2,
							time : parent.waitTime
						}, function() {

						});
					} else {
						refreshTable();//刷新表格
					}
				},
				error : function() {
					layer.msg('服务器错误请联系管理员', {
						icon : 2,
						time : parent.waitTime
					}, function() {
						/*location.href = "/manage/show";*/
					});
				}
			});
		}
		);
		return false;
	});
	$("#log_right").hide();
	//清除日志点击事件
	$("#log_left").click(function() {
		if ($("#log_right").is(":hidden")) {
			$("#log_right").show();
		} else {
			$("#log_right").hide();
		}
	});
})
//显示table及分页
layui.use([ 'table' ], function() {
		var table = layui.table;
		table.render({
			id : 'tbl' //对table操作时候需要使用到id,比如reload
			,
			elem : '#test' //渲染的是哪个对象
			,
			url : 'showLogList' //请求地址
			,
			limit : 2 //每页2条
			,
			
			even : true //开启隔行背景
			,
		 	page : {
				layout : [ 'count', 'prev', 'page', 'next', 'skip','groups' ] //自定义分页布局
				,
				curr : 1 //设定初始在第 1 页
				,
				groups : 6 //显示 2 个连续页码
				,
				first : '首页' //首页的文字，设置为false就是不显示
				,
				last : '末页' //尾页的文字
			}, 
			
			cellMinWidth : 80 //单元格的最小宽度
			,
			cols : [ [ {
				field : 'admin_id',
				title : '账号',
				hide : true
			}, {
				field : 'username',
				title : '用户名'
			}, {
				field : 'log_info',
				title : '具体操作'
			}, {
				title : '记录时间',
				templet:function(row){
					return createTime(row.log_time);
				}
			}] ]
		});
		
	});
//设置时间
function createTime(v){
	var date=new Date(v*1000);
	var y=date.getFullYear();
	var m=date.getMonth()+1;
	m=m<10?'0'+m:m;
	var d=date.getDate();
	d=d<10?("0"+d):d;
	var h=date.getHours();
	h=h<10?("0"+h):h;
	var M=date.getMinutes();
	M=M<10?("0"+M):M;
	var s=date.getSeconds();
	s=s<10?("0"+s):s;
	var str=y+"-"+m+"-"+d+" "+h+":"+M+":"+s;
	return str;
}
layui.use(['form', 'layedit', 'laydate'], function(){
	  var form = layui.form
	  ,layer = layui.layer
	  ,layedit = layui.layedit
	  ,laydate = layui.laydate;
	  
	  //日期
	  laydate.render({
	    elem: '#startTime'
	    ,type: 'datetime'
	    	,trigger: 'click'
	  });
	  laydate.render({
	    elem: '#endTime'
	    	 ,type: 'datetime'
	    		 ,trigger: 'click'
	  });
	  laydate.render({
		    elem: '#date3'
		    	,type: 'datetime'
		    		,trigger: 'click'
		  });
		  laydate.render({
		    elem: '#date4'
		    	,type: 'datetime'
		    		,trigger: 'click'
		  });
});
//form表单的提交事件
layui.use('form', function() {
var $ = layui.jquery;
var form = layui.form;
// 监听"查询条件表单"的提交事件
form.on('submit(searchBtn)', function(data) {

	// layer.msg(JSON.stringify(data.field));
	layui.table.reload('tbl', { // 刷新表格
		method : "post" // get方式提交，参数中的汉字会是乱码
		,
		page:{ curr:1 },
		where : {
			// 查询完后将页面重置为1
			username : $("#username").val(),
			startTime : $("#startTime").val(),
			endTime : $("#endTime").val()
		// 获取查询条件

		}
	});
	return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
});

});

//刷新表格
function refreshTable() {
layui.table.reload('tbl', { // 刷新表格
	method : "post" // get方式提交，参数中的汉字会是乱码
	,
	page:{ curr:1 },	// 查询完后将页面重置为1
	where : {
		username : $("#username").val(),// 获取查询条件
		startTime : $("#startTime").val(),
			endTime : $("#endTime").val()
	}
});
layer.msg('操作成功', {
	icon : 1
});
}
//
