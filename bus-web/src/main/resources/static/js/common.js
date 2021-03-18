/*公共的类，所有的页面都要导入*/
var waitTime=5000;//设置提示等待时间，单位为毫秒
var limitNum=3;//设置列表分页每页显示的个数


//关闭窗口方法
function closelayer(){
    var index = parent.layer.getFrameIndex(window.name); 
    parent.layer.close(index);
}
//转义
function HTMLEncode(html) {
	var temp = document.createElement("div");
	(temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
	var output = temp.innerHTML;
	temp = null;
	return output;	
}
//
function getWeekNum(i) {
	if(i==0){
		i=7;
	}
	var week = '';
	switch (i) {
	case 1:
		week = '星期一';
		break;
	case 2:
		week = '星期二';
		break;
	case 3:
		week = '星期三';
		break;
	case 4:
		week = '星期四';
		break;
	case 5:
		week = '星期五';
		break;
	case 6:
		week = '星期六';
		break;
	case 7:
		week = '星期日';
		break;
	}
	return week;
}
//
//刷新表格
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
//时间戳转（YYYY-MM-DD HH:MM:ss）格式
function formatDate(date) {
	  var date = new Date(date*1000);
	  var YY = date.getFullYear() + '-';
	  var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
	  var DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
	  var hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
	  var mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
	  var ss = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
	  return YY + MM + DD +" "+hh + mm + ss;
	}
    function formatTime(date) {
	  var date = new Date(date);
	  var YY = date.getFullYear() + '-';
	  var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
	  var DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
	  return YY + MM + DD;
	}
   