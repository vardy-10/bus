layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showBusDriver' // 请求地址
		,
		limit : parent.limitNum // 每页2条
		
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
			 {type: 'checkbox'},
			 {
					title : '模板分组',
					
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
					title : '线路',
					
					templet: function(d){	
			    		  return HTMLEncode(d.line_id);
			    	  }	
				},
				{
					title : '发车时间',
					
					templet: function(d){	
			    		  return HTMLEncode(d.depart_time);
			    	  }	
				},
				{
					title : '始发站',
					
					templet: function(d){	
			    		  return HTMLEncode(d.arrive_time);
			    	  }	
				},
				{
					title : '预计到达时间',
					
					templet: function(d){	
			    		  return HTMLEncode(d.arrive_time);
			    	  }	
				},
				{
					title : '终点站',
					
					templet: function(d){	
			    		  return HTMLEncode(d.line_name);
			    	  }	
				},
				{
					title : '司机',
					
					templet: function(d){	
			    		  return HTMLEncode(d.line_name);
			    	  }	
				},
				{
					title : '车辆',
					
					templet: function(d){	
			    		  return HTMLEncode(d.line_name);
			    	  }	
				},
				{
					title : '备注',
					
					templet: function(d){	
			    		  return HTMLEncode(d.line_name);
			    	  }	
				}] ]
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
				driver_number : $("#driver_number").val()
				
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
