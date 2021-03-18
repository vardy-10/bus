layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showBusOrder' // 请求地址
		,
		limit : parent.limitNum // 每页2条
		,
		
		where : {
			shifts_date : $("#test1").val(),
			order_state : 5
		}
		,even : true // 开启隔行背景
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
			{
				title : '订单编号 ',
				templet: function(d){	
		    		  return HTMLEncode(d.shifts_date);
		    	  },
			hide:true
			}, 
			{
				title : '创建日期 ',
				width:180,
				templet: function(d){	
		    		  return HTMLEncode(d.create_time);
		    	  }	
			}, {
				title : '乘客ID',
				templet: function(d){
					return HTMLEncode(d.passenger_id);//放上面日期
		    	  }	 
			},  
			{
				title : '类型',
				templet: function(d){	
		    		  return d.passenger_type==1?'教工':'学生';
		    	  }	
			},  
			{
				title : '支付单号',
				width:180,
				templet: function(d){		//自定义函数
		    		  return  HTMLEncode(d.order_sn);
		    	  }	 
			},
			{
				title : '日期',
				templet: function(d){	
		    		  return HTMLEncode(d.shifts_date);
		    	  }	
			},
			{
				title : '线路',
				templet: function(d){	
		    		  return HTMLEncode(d.line_name);
		    	  }	
			},
			{
				title : '班次',
				
				templet: function(d){	
		    		  return HTMLEncode(d.shifts_number);
		    	  }	
			},
			{
				title : '发车时间',
				templet: function(d){	
		    		  return HTMLEncode(d.depart_time);
		    	  }	
			},{
				title : '始发站',
				templet: function(d){	
		    		  return HTMLEncode(d.up_origin_name);
		    	  }	
			},
			{
				title : '预计到达',
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
				title : '座位',
				templet: function(d){	
		    		  return HTMLEncode(d.seat_num);
		    	  }	
			},
			{
				fixed : 'right',
				title : '操作',
				width:'10%',
				toolbar : '#barDemo'
			} ] ]
	});

	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;
		console.log(obj);
		switch (obj.event) {
		case 'yes':
			var data = obj.data;
			layer.confirm('确认通过退票吗？', {
				btn : [ '确认', '取消' ]
				// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'refund',
					data : {orderId:data.order_id},
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
						});
					}
				});
			});
			break;
		case 'no':
			var data = obj.data;
			console.log(data);
			// 询问框
			layer.confirm('确认取消退票吗？', {
				btn : [ '确定', '取消' ]
			// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'cancelRefund',
					data : {orderId:data.order_id},
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
						});
					}
				});	
			});
			break;
		}
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
				shifts_date : $("#test1").val(),
				startTime : $("#time2").val(),
				endTime : $("#time3").val(),
				line_name : $("#line_name").val(),
				up_origin_name : $("#up_origin_name").val(),
				shifts_number : $("#shifts_number").val(),
				order_state : $("#order_state").val(),
				passenger_id : $("#passenger_id").val(),
				order_sn : $("#order_sn").val(),
				order_id : $("#order_id").val()
			// 获取查询条件
			// 获取查询条件
			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
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
	    trigger: 'click'
	  });
	//时间选择器
		laydate.render({
			elem : '#time2',
			type : 'time',
			format:'HH:mm',
			trigger: 'click'
		});
		//时间选择器
		laydate.render({
			elem : '#time3',
			type : 'time',
			format:'HH:mm',
			 trigger: 'click'
		});
});