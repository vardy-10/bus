layui.use([ 'form' ], function() {
	var $ = layui.jquery, form = layui.form;
	var layer = layui.layer;
	form.on('submit(add_sub)', function(data) {
		// 当前容器的全部表单字段，名值对形式：{name: value}
		var i = layer.load(2);
		console.log($("form:first").serialize());
		$.ajax({
			url : 'addTempCheck',
			data : $("form:first").serialize(),
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
	//监听指定开关
	  form.on('switch(switchTest)', function(data){
	    layer.msg('开关checked：'+ (this.checked ? 'true' : 'false'), {
	      offset: '6px'
	    });
	    layer.tips('温馨提示：请注意开关状态的文字可以随意定义，而不仅仅是ON|OFF', data.othis)
	  });
});
layui.use('laydate', function() {
	var laydate = layui.laydate;
	// 常规用法
	laydate.render({
		elem : '#test1',
		trigger: 'click'
	});
});
