layui.use(['form'], function() {
	var $ = layui.jquery,
		form = layui.form;
	form.on('submit(add_sub)', function(data) {
		console.log(data.field); // 当前容器的全部表单字段，名值对形式：{name: value}
		var i = layer.load(2);
		$.ajax({
			url : '/manage/editPassword',
			data : data.field,
			type : 'post',
			success : function(data) {
				layer.close(i);
				if(data.success == true){
					alert(111);
				layer.msg('修改成功！', {icon : 2,time : 1000});
					
				}else{
					alert(333);
					layer.msg(data.message, {icon : 2,time : 1000});				
				}
			},
			error : function() {
				//layer.close(i);
				alert(222);
				layer.msg('网络连接异常，请稍后再试！', {icon : 2,time : 2000});
			}
		});

		return false; 
	});
	
	
});


