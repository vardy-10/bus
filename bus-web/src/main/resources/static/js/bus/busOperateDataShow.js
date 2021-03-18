layui.use([ 'table' ], function() {
	var table = layui.table;
	var act=$("#act").val();

	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showOperateData' // 请求地址
		,
		limit : parent.limitNum // 每页2条
		
		,even : true // 开启隔行背景
		,where : {
			startDate : $("#test1").val(),
			endDate : $("#test2").val()
		}
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
			/* {type: 'checkbox'},*/
			 {
			title : '日期',
			templet: function(d){
				return HTMLEncode(d.shifts_date);//放上面日期
	    	  }	 
		},  
		{
			/*field : 'driver_name',*/
			title : '班次',
			templet: function(d){	
	    		  return HTMLEncode(d.shifts_number);
	    	  }	
		},  
		{
			title : '线路',
			templet: function(d){		//自定义函数
				return HTMLEncode(d.line_name);
	    	  }	 
		},
		{
			/*field : 'driver_phone',*/
			title : '发车时间',
			templet: function(d){	
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
			width:130,
			templet: function(d){	
	    		  return HTMLEncode(d.arrive_time);
	    	  }	
		},
		{
			title : '终点站',
			templet: function(d){	
	    		  return HTMLEncode(d.up_terminal_name);
	    	  }	
		},{
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
			title : '路程（km）',
			templet: function(d){	
	    		  return HTMLEncode(d.up_total_distance);
	    	  }
		,
		hide:act=='data'?false:true
		},
	    	  {
	  			title : '教工数量',
	  			templet: function(d){	
	  	    		  return HTMLEncode(d.teacher_num);
	  	    	  }	,
	  	    	hide:act=='data'?true:false
	  		},
	  		{
	  			title : '学生数量',
	  			templet: function(d){	
	  	    		  return HTMLEncode(d.student_num);
	  	    	  },
	  	    	hide:act=='data'?true:false
	  		},
	  	    	{
	  	  			title : '上座率（%）',
	  	  			templet: function(d){	
	  	  	    		  return HTMLEncode(d.seat_rate);
	  	  	    	  }	,
	  		   hide:act=='data'?true:false
		} ] ]
	});
});

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
				driver_name : $("#driver_name").val(),
				vehicle_number : $("#vehicle_number").val(),
				shifts_number: $("#shifts_number").val(),
				startDate : $("#test1").val(),
				endDate : $("#test2").val(),
				line_name : $("#line_name").val(),
				up_origin_name : $("#up_origin_name").val()
			// 获取查询条件
			}
		});
		return false; //
	});
	// 监听"查询条件表单"的提交事件
	form.on('submit(outputExcel)', function(data) {
		   
			var act=$("#act").val();
			layer.confirm('是否确认导出？', {
				btn : [ '确定', '取消' ]
			// 按钮
			}, function() {
				/*var i = layer.load(2);*/
				window.open('exportToExcel?act='+act+"&"+$("form:eq(0)").serialize()); 
				layer.closeAll();
			/*	  $.ajax({
						url : 'exportToExcel?act='+act,
						data : data.field,
						dataType : 'json',
						type : 'post',
						success : function(data) {
							if (!data.success) {
								layer.close(i);
								layer.msg(data.message, {
									icon : 2,
									time : parent.waitTime
								}, function() {
									
								});
							} else {
								layer.close(i);
								layer.msg('导出成功!', {
									icon : 1,
									time : parent.waitTime
								}, function() {
									
								});
							}
						},
						error : function() {
							layer.close(i);
							layer.msg('服务器错误请联系管理员', {
								icon : 2,
								time : parent.waitTime
							}, function() {
								
							});
						}
					});*/
			});
         
		return false;
	});
});
// 刷新表格
function refreshTable() {
	layui.table.reload('tbl', { // 刷新表格
		method : "post" // get方式提交，参数中的汉字会是乱码
		,
		page:{ curr:1 },	// 查询完后将页面重置为1
		where : {
			driver_name : $("#driver_name").val(),// 获取查询条件
			driver_number : $("#driver_number").val()
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
//
layui.use('laydate', function(){
	  var laydate = layui.laydate;
	  
	  //常规用法
	  laydate.render({
	    elem: '#test1',
	    trigger: 'click',
	    btns:['now','confirm'],
	    type: 'datetime'	 
	    	});
	  //常规用法
	  laydate.render({
	    elem: '#test2',
	    trigger: 'click',
	    btns:['now','confirm'],
	    type: 'datetime'
	    	});
});