var flag=true;
layui.use([ 'table' ], function() {
	var table = layui.table;
	var act=$("#act").val();//获得区别页面的信息	

var checkbox={type: 'checkbox'
		
	};
	var mode={
			type:'space',
	    	hide:'true'
	    	  
		};
	var tool={
			fixed : 'right',
			title : '操作',
		    width:170,
			toolbar : '#barDemo'
			
		} ;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showShiftsPlan' // 请求地址
		,
		limit : parent.limitNum // 每页2条
		
		,
		
		where : {
			shifts_date : $("#test1").val()
	
		// 获取查询条件
		},
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
			title : '日期 ',
			templet: function(d){	
	    		  return HTMLEncode(d.shifts_date);
	    	  }	
		}, {
			title : '星期',
			templet: function(d){
				return HTMLEncode(getWeekNum(new Date(d.shifts_date).getDay()));//放上面日期
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
			templet: function(d){		//自定义函数
	    		  return  HTMLEncode(d.line_name);
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
	    		  return HTMLEncode(d.up_origin_name);
	    	  }	
		},
		{
			title : '预计到达时间',
			width:170,
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
		(act!=''?'':(tool)) ] ]
	});
	 $(function(){
	
    	 $("#cancel").hide();
    	 $("#tip").hide();
    		// 开始导入模板按钮事件
    	 $("#startCopy").click(function(){
    		 console.log($("#test2").val());
    		 if($(this).text()=='开始拷贝'){
             	 $("#cancel").show();
             	$("#tip").show();
             	$(this).text("确定拷贝");
    		 } else{
    			 var selectData = layui.table.checkStatus('tbl').data;
    			 var shifts_numbers=[];
    			 var shifts_dates=[];
 				$(selectData).each(function(){
 					shifts_numbers.push(this.shifts_number);
 					shifts_dates.push(this.shifts_date);
 				});
    				//发送ajax请求
    				$.ajax({
    					url : 'copyShiftsPlan?shifts_numbers='+shifts_numbers+'&shifts_dates='+shifts_dates,
    					data: {shifts_date:$("#test2").val()},
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
         });
    	 $("#cancel").click(function(){
    		 $("#startCopy").text("开始拷贝");
    		/*  $("#test1").val(); */
        	 $("#cancel").hide();
        	 $("#tip").hide();
    	 });
     });


	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;
		console.log(obj);
		switch (obj.event) {
		case 'update':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '编辑运营计划【'+data.shifts_date+'】【'+data.shifts_number+'】', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/shiftsPlanUpdate?shifts_date='+data.shifts_date+'&&shifts_number='+data.shifts_number,
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
			layer.confirm('确认删除该计划吗？', {
				btn : [ '删除', '取消' ]
			// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'deleteShiftsPlan',
					data : {shifts_date:data.shifts_date,shifts_number:data.shifts_number},
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
				driver_name : $("#driver_name").val(),
				vehicle_number : $("#vehicle_number").val(),
				shifts_date : $("#test1").val(),
				startTime : $("#time2").val(),
				endTime : $("#time3").val(),
				line_name : $("#line_name").val(),
				up_origin_name : $("#up_origin_name").val()
			// 获取查询条件

			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#addFlight").click(function() {
		layer.open({
			type : 2,
			title : [ '添加运营计划', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/shiftsPlanAdd',
			end : function() {

			}
		});
	});
	// 发布运营计划按钮事件
	$("#publishFlight").click(function() {
		var selectData = layui.table.checkStatus('tbl').data;
		 var shifts_numbers=[];
		 var shifts_dates=[];
		$(selectData).each(function(){
			shifts_numbers.push(this.shifts_number);
			shifts_dates.push(this.shifts_date);
			
		});
		layer.open({
			type : 2,
			title : [ '发布运营计划说明', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/shiftsPlanConfirm?shifts_numbers='+shifts_numbers+'&shifts_dates='+shifts_dates,
			end : function() {

			}
		});
		
	});
	  $(function(){
			// 发布运营计划按钮事件
			$("#publicFlight").click(function() {
				layer.open({
					type : 2,
					title : [ '发布计划', 'font-size:16px;' ],
					shadeClose : true,
					scrollbar : false,
					area : [ '100%', '100%' ],
					content : '/manage/busDriverAdd',
					end : function() {

					}
				});
			});
			// 拷贝前一天排班按钮事件
			$("#copyFlight").click(function() {
				layer.open({
					type : 2,
					title : [ '拷贝班次', 'font-size:16px;' ],
					shadeClose : true,
					scrollbar : false,
					area : [ '100%', '100%' ],
					content : '/manage/shiftsPlanShow?act=copy',
					end : function() {

					}
				});
			});
			// 导入班次模板按钮事件
			$("#importFlight").click(function() {
				flag=true;
				layer.open({
					type : 2,
					title : [ '导入模板', 'font-size:16px;' ],
					shadeClose : true,
					scrollbar : false,
					area : [ '100%', '100%' ],
					content : '/manage/tmplShow?act=import',
					end : function() {
					}
				,cancel: function(){ 
				    if(!flag){
				    	 refresh();
				    }
				  }
				});
			});
		
	    
	     });
});
// 刷新表格
function refresh() {
	layui.table.reload('tbl', { // 刷新表格
		method : "post" // get方式提交，参数中的汉字会是乱码
		,
		page:{ curr:1 },	// 查询完后将页面重置为1
		where : {
			driver_name : $("#driver_name").val(),
			vehicle_number : $("#vehicle_number").val(),
			shifts_date : $("#test1").val(),
			startTime : $("#time2").val(),
			endTime : $("#time3").val(),
			line_name : $("#line_name").val(),
			up_origin_name : $("#up_origin_name").val()
		}
	});
}
//
layui.use('laydate', function(){
	  var laydate = layui.laydate;
	  
	  //常规用法
	  laydate.render({
	    elem: '#test1',
	    trigger: 'click',
	    btns:['now','confirm']
	  });
	//常规用法
	  laydate.render({
	    elem: '#test2',
	    trigger: 'click',
	    btns:['now','confirm'],
	    position:'fixed'
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