layui.use([ 'table' ], function() {
	var table = layui.table;
	var layer = layui.layer;
	var act=$("#act").val();//获得区别页面的信息	
	var checkbox={type: 'checkbox'
		
	};
	var mode={
			type:'space',
	    	  hide:true
		};
var tool={
		fixed : 'right',
		title : '操作',
		width:200,
		toolbar : '#barDemo'
		
	};
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showTmpl' // 请求地址
		,
		limit : parent.limitNum // 每页2条
		,
		even : true // 开启隔行背景
		,
		/*page: true*/
		page : {
			layout : [ 'count', 'prev', 'page', 'next', 'skip','groups' ] // 自定义分页布局
			,
			curr : 1 // 设定初始在第 1 页
			,
			groups : 6 // 显示 2 个连续页码
			,
			first : '首页' // 首页的文字，设置为false就是不显示
			,
			last : '末页' // 尾页的文字
		},
		cellMinWidth : 80 // 单元格的最小宽度
		,
		cols : [ [
			(act==''?(mode):(checkbox))
			,
			{
				title : '模板分组',
				templet: function(d){	
		    		  return HTMLEncode(d.shifts_group);
		    	  }	
			}
			,{
			title : '班次',
			templet: function(d){	
	    		  return HTMLEncode(d.shifts_number);
	    	  }	
		},
		{
			title : '线路',
			
			templet: function(d){	
	    		  return HTMLEncode(d.line_name);
	    	  }	
		},
		{
			title : '发车时间',
			
			templet: function(d){	
				console.log(d.depart_time);
	    		  return HTMLEncode(d.depart_time);
	    	  }	
		},
		{
			title : '始发站',
			templet: function(d){	
	    		  return HTMLEncode(d.up_origin_name);
	    	  }	
		},
		{
			title : '预计到达时间',
			width:220,
			templet: function(d){	
	    		  return HTMLEncode(d.arrive_time);
	    	  }	
		},
		{
			title : '终点站',
			
			templet: function(d){	
	    		  return HTMLEncode(d.up_terminal_name);
	    	  }	
		},
		{
			title : '司机',
			
			templet: function(d){	
	    		  return HTMLEncode(d.driver_name);
	    	  }	
		},
		{
			title : '车辆',
			
			templet: function(d){	
	    		  return HTMLEncode(d.vehicle_number);
	    	  }	
		},
		{
			title : '备注',
			templet: function(d){	
	    		  return HTMLEncode(d.remark);
	    	  }	
		}, 
		(act!=''?'':(tool))
		] ]
	});
	  $(function(){
	    	 $("#cancel").hide();
	    	 $("#tip").hide();
	    		// 开始导入模板按钮事件
	    	 $("#startImport").click(function(){
	    		 if($(this).text()=='开始导入'){
	             	 $("#cancel").show();
	             	$("#tip").show();
	             	$(this).text("确定导入");
	    		 } else{
	    			 var selectData = layui.table.checkStatus('tbl').data;
	    				
	    				var shifts_numbers=[];
	    				$(selectData).each(function(){
	    					shifts_numbers.push(this.shifts_number);
	    				});
	    				
	    				//发送ajax请求
	    				$.ajax({
	    					url : 'importTmpl?shifts_numbers='+shifts_numbers,
	    					data : {shifts_date:$("#test1").val()},
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
	    					        parent.flag=false;
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
	         });
	    	 $("#cancel").click(function(){
	    		 $("#startImport").text("开始导入");
	    		/*  $("#test1").val(); */
	        	 $("#cancel").hide();
	        	 $("#tip").hide();
	    	 });
	     });
	   
	
	

	
	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;
		switch (obj.event) {
		case 'update':
			var data = obj.data;
			console.log(data);
			layer.open({
				type : 2,
				title : [ '编辑班次【'+HTMLEncode(data.shifts_number)+'】', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/tmplUpdate?shifts_number='+data.shifts_number,
				success : function(layero, index) {
					
					/* $("#temp").val("").val(data.adminid); */
				},
				end : function() {
					// 刷新数据
					/*AdminTable.reload({
						url : '',
						where : {
							page : obj.curr
						}
					});*/
				}
			});
			break;
		case 'delete':
			var data = obj.data;
			// 询问框
			layer.confirm('是否确认删除？', {
				btn : [ '删除', '取消' ]
			// 按钮
			}, function() {
			
				//发送ajax请求
				$.ajax({
					url : 'deleteTmpl',
					data : {shifts_number:data.shifts_number},
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
							refreshTable();
							//刷新表格
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
			});

			break;
		}
	});
});
//时间处理

// form表单的提交事件
layui.use('form', function() {
	var $ = layui.jquery;
	var form = layui.form;
	// 监听"查询条件表单"的提交事件
	form.on('submit(searchBtn)', function(data) {

		layui.table.reload('tbl', { // 刷新表格
			method : "post" // get方式提交，参数中的汉字会是乱码
			,
			page:{ curr:1 },
			where : {
				shifts_number:$("#shifts_number").val(),
				line_name : $("#line_name").val(),
				up_origin_name:$("#up_origin_name").val(),
				shifts_group:$("#shifts_group").val()
			
			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#addBusFlightLine").click(function() {
		layer.open({
			type : 2,
			title : [ '添加班次', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/tmplAdd',
			end : function() {

			}
		});
	});

});
//刷新表格
function refreshTable() {
	layui.table.reload('tbl', { // 刷新表格
		method : "post" // get方式提交，参数中的汉字会是乱码
		,
		page:{ curr:1 },	// 查询完后将页面重置为1
		where : {
			shifts_number:$("#shifts_number").val(),
			line_name : $("#line_name").val(),
			up_origin_name:$("#up_origin_name").val(),
			shifts_group:$("#shifts_group").val()
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
layui.use('laydate', function(){
	  var laydate = layui.laydate;
	  
	  //常规用法
	  laydate.render({
	    elem: '#test1',
	    trigger: 'click',
	 position:'fixed'
	  });
});
