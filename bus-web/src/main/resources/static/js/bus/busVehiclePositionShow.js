layui
		.use(
				[ 'table' ],
				function() {
					var table = layui.table;
					table
							.render({
								id : 'tbl' // 对table操作时候需要使用到id,比如reload
								,
								elem : '#test' // 渲染的是哪个对象
								,
								url : 'showVehiclePosition' // 请求地址
								,
								limit : parent.limitNum // 每页2条
								,
								where : {
									startTime : $("#startTime").val(),
									endTime : $("#endTime").val()
								// 获取查询条件

								},

								even : true // 开启隔行背景
								,

								/* page: true */
								page : {
									layout : [ 'count', 'prev', 'page', 'next',
											'skip', 'groups' ] // 自定义分页布局
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
											field : 'vehicle_id',
											title : '车辆Id',
											hide : true
										},
										{
											/* field : 'vehicle_number', */
											title : '车辆编号',
											width : '30%',
											templet : function(d) {
												return HTMLEncode(d.vehicle_number);
											}
										},
										{
											title : '记录时间',
											templet : function(row) {
												return HTMLEncode(createTime(row.log_time));
											}

										},
										{
											title : '车辆位置经纬度 ',
											width : '30%',
											templet : function(d) { // 自定义函数
												if (parseFloat(d.latitude) == 0
														&& parseFloat(d.longitude) == 0) {
													return '未知';
												} else {
													return '<a href="javascript:void(0);" style="text-decoration:underline;">'
															+ d.latitude
															+ ","
															+ d.longitude
															+ '</a>';
												}
											},
											event : 'showMap',
											style : ''
										} ] ]
							});

					table.on('tool(test)', function(obj) {
						var $ = layui.jquery;
						console.log(obj);
						switch (obj.event) {
						case 'showMap':
							var data = obj.data;
							if (parseFloat(data.latitude) == 0
									&& parseFloat(data.longitude) == 0) {
								return;
							}
							var i = layer.load(2);
							// 发送ajax请求
							layer.open({
								type : 2,
								title : [ '', 'font-size:16px;' ],
								shadeClose : true,
								scrollbar : false,
								area : [ '100%', '100%' ],
								content : '/manage/showMap?log_time='
										+ data.log_time,
								success : function(layero, index) {
									layer.close(i);
									/* $("#temp").val("").val(data.adminid); */
								},
								end : function() {
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
		$("#limitTime").val($("#endTime").val());
		// layer.msg(JSON.stringify(data.field));
		layui.table.reload('tbl', { // 刷新表格
			method : "post",
			page : {
				curr : 1
			},
			where : {
				status : 0,
				vehicle_number : $("#vehicle_number").val(),
				startTime : $("#startTime").val(),
				endTime : $("#endTime").val()
			}
		});
		return false;
	});

	// 添加按钮事件
	$("#addBusLine").click(function() {
		layer.open({
			type : 2,
			title : [ '添加线路', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/busLineAdd',
			end : function() {

			}
		});
	});

});
layui
		.use(
				[ 'layedit', 'laydate' ],
				function() {
					var form = layui.form, layer = layui.layer, layedit = layui.layedit, laydate = layui.laydate, laypage = layui.laypage;

					// 日期
					laydate.render({
						elem : '#startTime',
						type : 'datetime',
						trigger : 'click'
					});
					laydate.render({
						elem : '#endTime',
						type : 'datetime',
						trigger : 'click'
					});
					laydate.render({
						elem : '#date3',
						type : 'datetime',
						trigger : 'click'
					});
					laydate.render({
						elem : '#date4',
						type : 'datetime',
						trigger : 'click'
					});

				});
function createTime(v) {
	var date = new Date(v * 1000);
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	m = m < 10 ? '0' + m : m;
	var d = date.getDate();
	d = d < 10 ? ("0" + d) : d;
	var h = date.getHours();
	h = h < 10 ? ("0" + h) : h;
	var M = date.getMinutes();
	M = M < 10 ? ("0" + M) : M;
	var s = date.getSeconds();
	s = s < 10 ? ("0" + s) : s;
	var str = y + "-" + m + "-" + d + " " + h + ":" + M + ":" + s;
	return str;
}
