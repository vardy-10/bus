if (window.parent != window) {
	window.top.location.href = location.href;
}

layui.use(['form', 'layer', 'laydate','element'], function(){
	var form = layui.form,
		layer = layui.layer,
		$ = layui.jquery,
		laydate = layui.laydate,
		element = layui.element;
	
	//监听提交
	form.on('submit(login-submit)', function(data) {
		//加载层-风格3
		var i = layer.load(2);
		
		$.ajax({
			url : '/manage/login',
			data : data.field,
			type : 'post',
			success : function(data) {
				layer.close(i);
				if(data.success == true){			
					window.location.href="manage/index";
				}else{
					layer.msg(data.message, {icon : 2,time : 1000});				
				}
			},
			error : function() {
				layer.msg('网络连接异常，请稍后再试！', {icon : 2,time : 1000});
			}
		});
		
		return false;
	});
	 
//	console.log(form.verify());
});