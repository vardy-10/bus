layui.use([ 'form' ], function() {
	var $ = layui.jquery, form = layui.form;
	var layer = layui.layer;
	form.on('submit(upp_sub)', function(data) {
			 // 当前容器的全部表单字段，名值对形式：{name: value}
				var i = layer.load(2);
				$.ajax({
					url : 'editBusStation',
					data :$("form:first").serialize(),
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
		return false; 
	});
	//监听radio按钮切换
/*	form.on('radio(jiedian)', function (data) {
		if(data.value==2){
			layer.confirm('线路由双向改为单向时会自动删除下行所有车辆，确认要进行此操作吗？', {
			  btn: ['确定','取消'] //按钮
			}, function(){
			  layer.msg('已修改为下行!', {icon: 1});
			}, function(){
				form.val('example', {//点击取消还原成上行
				      "dir": "1"
				    });
				 form.render();//重新渲染
			});
		}
	});*/
});

