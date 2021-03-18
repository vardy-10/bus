layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : '/manage/busStationList?line_id='+$("input[name=line_id]").val()// 请求地址
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
			field : 'line_id',
			title : '车站id ',
			hide : true
		},
		{
			field : 'line_id',
			title : '线路id  ',
			hide : true
		},
		{
			field : 'station_name',
			title : '车站名 ',
			
		}, 
		/*{
			title : '车站方向 ',
			templet: function(d){		//自定义函数
      			return d.station_dir==1?"上行":"下行";
	    	  }	
		},*/
		{
			field : 'station_order',
			title : '车站顺序号 ',
			
		},
		{
			field : 'radius',
			title : '车站半径（米）',
			
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
				title : [ '编辑车站【'+HTMLEncode(data.station_name)+'】', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/busStationUpdate?station_id='+data.station_id,
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
			layer.confirm('是否确认删除？', {
				btn : [ '删除', '取消' ]
			// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'deleteBusStation',
					data : {station_id:data.station_id},
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
		case 'showVehicle':
			var data = obj.data;
			//发送ajax请求
			$.ajax({
				url : 'deleteAdmin',
				data : {admin_id:data.admin_id},
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
						closelayer();//关闭窗口
						parent.refreshTable();//刷新表格
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
			});
			break;
		case 'showStation':
			var data = obj.data;
			$.ajax({
				url : 'busStationShow',
/*				data : {admin_id:data.admin_id},*/
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
						closelayer();//关闭窗口
						parent.refreshTable();//刷新表格
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
			page:{ curr:1 },
			where : {
				
				station_name : $("#station_name").val()
			// 获取查询条件

			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#busStationAdd").click(function() {
	
		layer.open({
			type : 2,
			title : [ '添加车站', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/busStationAdd?line_id='+$("input[name=line_id]").val(),
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
			username : $("#station_name").val()// 获取查询条件
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
