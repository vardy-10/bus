layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showBusVehicle' // 请求地址
		,
		where : {
			license_plate : $("#license_plate").val(),
			vehicle_number : $("#vehicle_number").val(),
			line_id : $("#line_id").val()
		} // 获取查询条件
		,
		limit : parent.limitNum // 每页2条
		,

		even : true // 开启隔行背景
		,

		/* page: true */
		page : {
			layout : [ 'count', 'prev', 'page', 'next', 'skip', 'groups' ] // 自定义分页布局
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
			field : 'vehicle_id',
			title : '车辆ID',
			hide : true
		}, {
			field : 'line_name',
			title : '所属线路',
			templet : function(d) {
				return HTMLEncode(d.line_name);
			}
		}, {
			field : 'vehicle_number',
			title : '车辆编号',
			templet : function(d) {
				return HTMLEncode(d.vehicle_number);
			}
		}, {
			/* field : 'license_plate', */
			title : '车辆车牌',
			templet : function(d) {
				return HTMLEncode(d.license_plate);
			}
		}, {
			/* field : 'device1_number', */
			title : '定位仪编号',
			templet : function(d) {
				return HTMLEncode(d.device1_number);
			}
		}, {
			/* field : 'device2_number', */
			title : '售票仪编号',
			templet : function(d) {
				return HTMLEncode(d.device2_number);
			}
		}, {
			fixed : 'right',
			title : '操作',
			toolbar : '#barDemo',
			width : 160
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
				title : [ '编辑车辆【' + HTMLEncode(data.vehicle_number) + '】',
						'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/busVehicleUpdate?vehicle_id='
						+ data.vehicle_id,
				success : function(layero, index) {

					/* $("#temp").val("").val(data.adminid); */
				},
				end : function() {
					// 刷新数据
					/*
					 * AdminTable.reload({ url : '', where : { page : obj.curr }
					 * });
					 */
				}
			});
			break;
		case 'delete':
			var data = obj.data;
			console.log(data);
			// 询问框
			layer.confirm('确认删除【 ' + HTMLEncode(data.vehicle_number) + '】 吗？',
					{
						btn : [ '删除', '取消' ]
					// 按钮
					}, function() {
						// 发送ajax请求
						$.ajax({
							url : 'deleteBusVehicle',
							data : {
								vehicle_id : data.vehicle_id
							},
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

									refreshTable();// 刷新表格
								}

							},
							error : function() {
								layer.msg('服务器错误请联系管理员', {
									icon : 2,
									time : parent.waitTime
								}, function() {
									/* location.href = "/manage/show"; */
								});
							}
						});
					});

			break;
		case 'showOne':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '查看车辆详情', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/busVehicleShowOne?vehicle_id='
						+ data.vehicle_id,
				success : function(layero, index) {

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
			page : {
				curr : 1
			},
			where : {
				license_plate : $("#license_plate").val(),
				vehicle_number : $("#vehicle_number").val(),
				line_id : $("#line_id").val()
			// 获取查询条件
			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});
	// 添加按钮事件
	$("#addBusVehicle").click(function() {
		layer.open({
			type : 2,
			title : [ '添加车辆', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/busVehicleAdd',
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
		page : {
			curr : 1
		}, // 查询完后将页面重置为1
		where : {
			username : $("#username").val()
		// 获取查询条件
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
