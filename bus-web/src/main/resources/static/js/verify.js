layui.use(['form','jquery'], function() {
	var $ = layui.jquery,form = layui.form;
	form.verify({
		username: function(value, item){
			if(!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]{3,10}$").test(value)){
			  return '不能有特殊字符且字数应在3~10位之间';
			}
			if(/(^\_)|(\__)|(\_+$)/.test(value)){
			  return '首尾不能出现下划线\'_\'';
			}
			if(/^\d+\d+\d$/.test(value)){
			  return '不能全为数字';
			}
		},
		pass: [ /^[\S]{6,12}$/,'密码必须6到12位，且不能出现空格' ],
		passAgain:function(value,item){
			if($("#firstpass").val() != value){
				return '两次输入的密码不一致';
			}
		},
	});
});
	  