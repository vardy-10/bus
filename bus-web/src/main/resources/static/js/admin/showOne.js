layui.use([ 'form' ], function() {
	var $ = layui.jquery, form = layui.form;

/*	var id = $.parseJSON(parent.document.getElementById("temp").value);*/
	
	
	/*getAdminById();
	function getAdminById() {
		var i = layer.load(2);
		
		$.ajax({
			url : '../../js/admin/uppData.json',
			data : {
				'adminid' : id
			},
			dataType : 'json',
			type : 'get',
			success : function(msg) {
				console.log(msg);
				layer.close(i);
				
				if(msg.type == "1"){
					msg['type'] = "超级管理员"
				}else{
					msg['type'] = "管理员"
				}
				
				$("#roles").empty();
				$.each(msg.roles,function(i,x){
					$("#roles").append('<button class="layui-btn layui-btn-primary">'+x.role_name+'</button>');
				});
				
				form.val("showOne_form", msg);
				form.render();
				
				$("body").prepend( $("<div>").css({
					"width":"100%",
					"height":$("#bodyDiv").height() + 50,
					"position": "absolute",
					"z-index": 1000,
				}));
			},
			error : function() {
				layer.msg('服务器错误请联系管理员', {icon : 2,time : 1000});
			}
		});
	}*/
	
	
});

