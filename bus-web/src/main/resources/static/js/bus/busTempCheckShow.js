layui.use([ 'table' ], function() {
	var table = layui.table;
	var layer = layui.layer;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showTempCheck' // 请求地址
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
			{
				title : '创建时间',
				width:160,
				templet: function(d){	
					
		    		  return HTMLEncode(formatDate(d.create_time));
		    	  }	
			}
			,{
			title : '姓名',
			templet: function(d){	
	    		  return HTMLEncode(d.temp_name);
	    	  }	
		},
		{
			title : '类型',
			
			templet: function(d){	
	    		return d.temp_type==1?'教工':'学生';
	    	  }	
		},
		{
			title : '身份证号',
			
			templet: function(d){	
	    		  return HTMLEncode(d.temp_identity);
	    	  }	
		},
		{
			title : '部门',
			
			templet: function(d){	
	    		  return HTMLEncode(d.temp_dept);
	    	  }	
		},
		{
			title : '岗位',
			
			templet: function(d){	
				console.log(d.temp_post);
	    		  return HTMLEncode(d.temp_post);
	    	  }	
		},
		{
			title : '工号',
			
			templet: function(d){	
	    		  return HTMLEncode(d.temp_number);
	    	  }	
		},
		{
			title : '直属领导',
			
			templet: function(d){	
	    		  return HTMLEncode(d.temp_boss_name);
	    	  }	
		},
		{
			title : '手机号码',
			
			templet: function(d){	
	    		  return HTMLEncode(d.temp_mobile);
	    	  }	
		},
		{
			title : '状态',
			templet: function(d){	
	    		  return d.is_audit==0?'未审核':'已审核';
	    	  }	
		},
		{
			fixed : 'right',
			title : '操作',
			width:200,
			toolbar : '#barDemo'	
		}
		] ]
	});
	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;
		switch (obj.event) {
		case 'update':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '编辑临时工勤用户审核【'+data.temp_identity+'】', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/tempCheckUpdate?temp_identity='+data.temp_identity,
				success : function(layero, index) {

				},
				end : function() {
					
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
					url : 'deleteTempCheck',
					data : {temp_identity:data.temp_identity},
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
							
						});
					}
				});	
			});

			break;
		case 'showOne':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '临时工勤用户审核【'+HTMLEncode(data.temp_identity)+'】', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/tempCheckShowOne?temp_identity='+data.temp_identity,
				success : function(layero, index) {
					
				
				},
				end : function() {
				
				}
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
				date:$("#test1").val(),
				is_audit : $("#is_audit").val(),
				temp_name : $("#temp_name").val(),
				temp_type : $("#temp_type").val(),
				temp_dept : $("#temp_dept").val(),
				temp_number : $("#temp_number").val(),
				temp_post : $("#temp_post").val(),
				temp_mobile : $("#temp_mobile").val(),
				temp_boss_name : $("#temp_boss_name").val()
			
			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#addTempCheck").click(function() {
		layer.open({
			type : 2,
			title : [ '添加临时工勤用户审核', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/tempCheckAdd',
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
			date:$("#test1").val(),
			is_audit : $("#is_audit").val(),

			temp_name : $("#temp_name").val(),
			temp_type : $("#temp_type").val(),
			temp_dept : $("#temp_dept").val(),
			temp_number : $("#temp_number").val(),
			temp_post : $("#temp_post").val(),
			temp_mobile : $("#temp_mobile").val(),
			temp_boss_name : $("#temp_boss_name").val()
		}
	});
	layer.msg('操作成功', {
		icon : 1
	});
}
layui.use('laydate', function(){
	  var laydate = layui.laydate;
	//时间选择器
		laydate.render({
			elem : '#test1',
			type : 'date',
			
			trigger: 'click'
		});
});
