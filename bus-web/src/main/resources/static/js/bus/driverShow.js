layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'busLineList/' // 请求地址
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
		cols : [ [ {
			field : 'driver_id',
			title : '司机id ',
			hide : true
		}, {
			/*field : 'driver_name',*/
			title : '司机姓名',
			width:'15%',
			templet: function(d){	
	    		  return HTMLEncode(d.driver_name);
	    	  }	
		},  
		 {
			/*field : 'driver_phone',*/
			title : '司机电话',
			width:'20%',
			templet: function(d){	
	    		  return HTMLEncode(d.driver_phone);
	    	  }	
		},
		 {
			/*field : 'driver_identity',*/
			title : '司机身份证号',
			width:'20%',
			templet: function(d){	
	    		  return HTMLEncode(d.driver_identity);
	    	  }	
		},
		{
			title : '司机性别',
			width:'10%',
			templet: function(d){		//自定义函数
	    		  return d.driver_sex==1?"男":"女";
	    	  }	 
		}, {
			fixed : 'right',
			title : '操作',
			width:'20%',
			toolbar : '#barDemo'
			
		} ] ]
	});

	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;
		console.log(obj);
		switch (obj.event) {
		case 'update':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '编辑线路信息', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/busLineUpdate?line_id='+data.line_id,
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
			console.log(data);
			// 询问框
			layer.confirm('删除线路时，同时会删除相关的车站，是否确认删除？', {
				btn : [ '删除', '取消' ]
			// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'deleteBusLine',
					data : {line_id:data.line_id},
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

		// layer.msg(JSON.stringify(data.field));
		layui.table.reload('tbl', { // 刷新表格
			method : "post" // get方式提交，参数中的汉字会是乱码
			,
			where : {
				page : 1,// 查询完后将页面重置为1
				line_name : $("#line_name").val()
				
			// 获取查询条件

			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#addBusLine").click(function() {
		layer.open({
			type : 2,
			title : [ '添加司机', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/driverAdd',
			end : function() {

			}
		});
	});

});
//
// 刷新表格
function refreshTable() {
	layui.table.reload('tbl', { // 刷新表格
		method : "post" // get方式提交，参数中的汉字会是乱码
		,
		page:{ curr:1 },	// 查询完后将页面重置为1
		where : {
			username : $("#username").val()// 获取查询条件
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
