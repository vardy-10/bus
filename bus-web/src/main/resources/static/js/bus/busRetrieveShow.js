layui.use([ 'table' ], function() {
	var table = layui.table;
	table.render({
		id : 'tbl' // 对table操作时候需要使用到id,比如reload
		,
		elem : '#test' // 渲染的是哪个对象
		,
		url : 'showRetrieve' // 请求地址
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
			field : 'admin_id',
			title : '发布管理员ID',
			hide : true
		},  {
			/*field : 'username',*/
			title : '发布管理员',
			width:'15%',
			templet: function(d){	
	    		  return HTMLEncode(d.username);
	    	  }	
		},{
			title : '发布时间',
			templet:function(row){
				return   HTMLEncode(createTime(row.release_time));
			}
		}, {
			/*field : 'retrieve_content',*/
			title : '公告内容',
			templet: function(d){	 
	    		  return HTMLEncode(d.retrieve_content);
	    	  }	
		},	 
		 {
			fixed : 'right',
			title : '操作',
			width:'15%',
			toolbar : '#barDemo'
			
		}
		 ] ]
	});

	table.on('tool(test)', function(obj) {
		var $ = layui.jquery;

		console.log(obj);
		switch (obj.event) {
		case 'update':
			var data = obj.data;
			layer.open({
				type : 2,
				title : [ '修改失物招领信息', 'font-size:16px;' ],
				shadeClose : true,
				scrollbar : false,
				area : [ '100%', '100%' ],
				content : '/manage/retriveUpdate?release_time='+data.release_time+"&admin_id="+data.admin_id,
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
			layer.confirm('确认删除 【' +HTMLEncode(data.username) + '】 吗？', {
				btn : [ '删除', '取消' ]
			// 按钮
			}, function() {
				//发送ajax请求
				$.ajax({
					url : 'deleteRetrive',
					data : "release_time="+data.release_time+"&admin_id="+data.admin_id,
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
			page:{ curr:1 },
			where : {
				username : $("#username").val()
			// 获取查询条件

			}
		});
		return false; // 表单不需要提交，直接对table进行reload就ok了，所以上面的submit事件其实也可以写为按钮的点击事件
	});

	// 添加按钮事件
	$("#addRetrieve").click(function() {
		layer.open({
			type : 2,
			title : [ '添加失物招领', 'font-size:16px;' ],
			shadeClose : true,
			scrollbar : false,
			area : [ '100%', '100%' ],
			content : '/manage/retrieveAdd',
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
function createTime(v){
	var date=new Date(v*1000);
	var y=date.getFullYear();
	var m=date.getMonth()+1;
	m=m<10?'0'+m:m;
	var d=date.getDate();
	d=d<10?("0"+d):d;
	var h=date.getHours();
	h=h<10?("0"+h):h;
	var M=date.getMinutes();
	M=M<10?("0"+M):M;
	var s=date.getSeconds();
	s=s<10?("0"+s):s;
	var str=y+"-"+m+"-"+d+" "+h+":"+M+":"+s;
	return str;
}