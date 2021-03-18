layui.use([ 'form' ], function() {
	var $ = layui.jquery, form = layui.form;
	var layer = layui.layer;
	form.on('submit(upp_sub)', function(data) {
				// 当前容器的全部表单字段，名值对形式：{name: value}
				var i = layer.load(2);
				$.ajax({
					url : 'editAdminInfo',
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
});

