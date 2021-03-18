var advanceTime=0;//配置提前到达时间
var now;//当前时间
var depart_delay_minute=0;//延迟发车时间 
layui.use(['layer','form','laydate'], function(){
		  var  layer = layui.layer,
		  laydate=layui.laydate;
		  var $ = layui.jquery;
		  var form = layui.form;
			//日期
		  $(".planInfo").show();
			laydate.render({
				elem : '#shifts_date',
				type : 'date',
				trigger:'click'
			});
			// 监听"查询条件表单"的提交事件
			form.on('submit(searchBtn)', function(data) {
				if((typeof data.field.shifts_date !="string") || data.field.shifts_date==''){
					layer.msg('请选择日期!', {
						icon :7,
						time : parent.waitTime
					}, function() {
						
					});
					return false;
				}
				if(((typeof data.field.line_id !="string") || data.field.line_id=="")&&((typeof data.field.shifts_number !="string") || data.field.shifts_number=='')){
					layer.msg('请选择线路或者班次!', {
						icon :7,
						time : parent.waitTime
					}, function() {
						
					});
					return false;
				}
				$(".planInfo").hide();
				$("#showInfo>div").text("正在查询，请稍后!");
				$("#showInfo").show();
				 $.ajax({
						url : 'showOperatePlanMonitor',
						data :data.field,
						dataType : 'json',
						type : 'post',
						success : function(data) {
							if(!data.success){
							    $("#showInfo>div").text(data.message);
								return;
							}
							var nowTime=data.nowTime;//获得当前时间
							now=new Date(nowTime);
					        $("#nowTime").text(now.getFullYear() + '-'+(now.getMonth() + 1 < 10 ? '0' + (now.getMonth() + 1) : now.getMonth() + 1) + '-'+(now.getDate() < 10 ? '0' + (now.getDate()) : now.getDate())+" "+(now.getHours() < 10 ? '0' + now.getHours() : now.getHours()) + ':'+(now.getMinutes() < 10 ? '0' + now.getMinutes() : now.getMinutes()));
							 advanceTime=data.configMap.vehicle_wait_minute;//获得配置提前到站分钟
							 depart_delay_minute=data.configMap.depart_delay_minute;//获得延迟发车时间
							 var operatePlan=data.dataPlan.length;
							 var realPlan=data.dataReal.length;
							 if(operatePlan<=0&&realPlan<=0){
								 $("#showInfo>div").text('无数据');
								 return;
							 }
							
							 $(".planInfo table").empty();//先将原来数据清空
							 var tempList=new Array();
							//判断两边计划的个数比较
								 tempList=data.dataReal;
								 var right='';//用来保存右边显示内容
								 $(data.dataPlan).each(function(){
										var status='';
										//获得日期和班次
										var shifts_date=this.shifts_date;
										var shifts_number=this.shifts_number;
										//获得发车时间
										var depart_time=this.depart_time;
										var tempDate=new Date(shifts_date+' '+depart_time);
										//获得正确的提前到站时间
										var rightAdvanceTime=tempDate;
										rightAdvanceTime.setMinutes(rightAdvanceTime.getMinutes()-parseInt(advanceTime));
										if(now>=tempDate){
											status="已发车";
										}else if(now>=rightAdvanceTime){
											status='已进站';
										}else{
											status='未进站';
										}
										var isExist=true;//判断是否存在
								        if(data.dataReal.length>0){				      
								        	$(data.dataReal).each(function(i){
												if(this.shifts_number==shifts_number&&this.shifts_date==shifts_date){
                                                    var inStation='';
                                                    tempDate=new Date(this.shifts_date+' '+this.depart_time);
                                                    //获得正确的提前到站时间
													rightAdvanceTime=tempDate;
													rightAdvanceTime.setMinutes(rightAdvanceTime.getMinutes()-parseInt(advanceTime));
													var delay_time=new Date(this.shifts_date+' '+this.depart_time);//延迟发车时间
													delay_time.setMinutes(delay_time.getMinutes()+parseInt(depart_delay_minute));
                                                    var onTime='否';
													if(this.in_origin_time==0){
														inStation='未进站';
														if(now<rightAdvanceTime){
															onTime='是';
														}
													}else if(this.out_origin_time==0){
														inStation='已进站';
														//比计划到站时间先进站也算准时
														if(now<rightAdvanceTime){
															onTime='是';
														}
													}else{		
														if(new Date(this.out_origin_time*1000)>=tempDate&&new Date(this.out_origin_time*1000)<=delay_time){
															onTime='是';
														}
														inStation='已发车';
													}
													right="<td class='info_right'><div><div>日期</div><div>"+(this.shifts_date==null?'':this.shifts_date)+"</div></div><div><div>班次</div><div>"+(this.shifts_number==null?'':this.shifts_number)+"</div></div><div><div>线路</div><div>"+(this.line_name==null?'':this.line_name)+"</div></div><div><div>实际发车时间</div><div class='depart_time'>"+(this.depart_time==null?'':this.depart_time)+"</div></div><div><div>始发站</div><div>"+(this.up_origin_name==null?'':this.up_origin_name)+"</div></div><div><div>终点站</div><div>"+(this.up_terminal_name==null?'':this.up_terminal_name)+"</div></div><div><div>售票（教/学）</div><div>"+this.teacher_num+"/"+this.student_num+"</div></div><div><div>当前状态</div><div >"+inStation+"</div></div><div><div>是否准点</div><div class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div></div><div><div>实际运营里程</div><div>"+(this.up_total_distance==null?'0':this.up_total_distance)+"公里</div></div><div><div>车牌号</div><div>"+(this.vehicle_number==null?'':this.vehicle_number)+"</div></div><div><div>司机</div><div>"+(this.driver_name==null?'':this.driver_name)+"</div></div></td>";
													tempList.splice(i,1);
													isExist=false;//存在就改为false
		                                            return false;
												}
											});
								        }
										if(isExist){//如果不存在就显示无数据
											right="<td class='info_right'>无数据</td> ";
										}
										$(".planInfo table").append("<tr><td class='info_left'><div> <div>日期</div> <div>"+(this.shifts_date==null?'':this.shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(this.shifts_number==null?'':this.shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(this.line_name==null?'':this.line_name)+"</div> </div> <div> <div >计划发车时间</div> <div class='depart_time'>"+(this.depart_time==null?'':this.depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(this.up_origin_name==null?'':this.up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(this.up_terminal_name==null?'':this.up_terminal_name)+"</div> </div> <div> <div>座位数</div> <div>"+(this.seat_num==null?'':this.seat_num)+"</div> </div> <div> <div>当前状态</div> <div >"+status+"</div> </div> <div> <div>计划运营里程</div> <div>"+(this.up_total_distance==null?'0':this.up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(this.vehicle_number==null?'':this.vehicle_number)+"</div> </div> <div> <div>司机</div> <div>"+(this.driver_name==null?'':this.driver_name)+"</div> </div></td><td class='blanck'></td>"+right+"</tr>");
										 $(".planInfo").show();
										 $("#showInfo").hide();
								 });
								 //添加多余的
								
								 $(tempList).each(function(){
									 var shifts_date=this.shifts_date;
									 var shifts_number=this.shifts_number;
									 var line_name=this.line_name;
									 var up_origin_name=this.up_origin_name;
									 var up_terminal_name=this.up_terminal_name;
									 var teacher_num=this.teacher_num;
									 var student_num=this.student_num;
									 var up_total_distance=this.up_total_distance;
									 var vehicle_number=this.vehicle_number;
									 var driver_name=this.driver_name;
									 var depart_time=this.depart_time;
									 var tempDate=new Date(shifts_date+' '+depart_time);
									//获得正确的提前到站时间
									 var rightAdvanceTime=tempDate;
									 rightAdvanceTime.setMinutes(rightAdvanceTime.getMinutes()-parseInt(advanceTime));
									 var delay_time=new Date(this.shifts_date+' '+this.depart_time);//延迟发车时间
									 delay_time.setMinutes(delay_time.getMinutes()+parseInt(depart_delay_minute));
									 var inStation='';
									 var onTime='否';
										if(this.in_origin_time==0){
											inStation='未进站';
											if(now<rightAdvanceTime){
												onTime='是';
											}
										}else if(this.out_origin_time==0){
											inStation='已进站';
											//比计划到站时间先进站也算准时
											if(now<rightAdvanceTime){
												onTime='是';
											}
										}else{
											
											if(new Date(this.out_origin_time*1000)>=tempDate&&new Date(this.out_origin_time*1000)<=delay_time){
												onTime='是';
											}
											inStation='已发车';
										}
									 var reals=$(".planInfo .info_right");//获得所有的右边数据栏
									 var plans=$(".planInfo .info_left");//获得所有的左边数据栏
									 if(plans.length<=0){
										  $(".planInfo table").append("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
											$(".planInfo").show();
											 $("#showInfo").hide();	
                                              return true;
									 }else{
										 var size=plans.length-1;
										 $(reals).each(function(i){//按顺序
											 if(i!=size){
												 if($(this).text()=='无数据'){
													  var tempTime=new Date(shifts_date+' '+$(".planInfo table tr:eq("+i+")").find(".depart_time").text());
													  if(new Date(shifts_date+' '+depart_time)<=tempTime){
														  $(".planInfo table tr:eq("+i+")").before("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  }
													 return true;
												 }else if($(this).find(".depart_time").text()==''){
													 return true;
												 }else{
													 if(new Date(shifts_date+' '+depart_time)<=new Date(shifts_date+' '+$(this).find(".depart_time").text())){
														  $(".planInfo table tr:eq("+i+")").before("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  }
												 }
											 }else{
												 if($(this).text()=='无数据'){
													 var tempTime=new Date(shifts_date+' '+$(".planInfo table tr:eq("+i+")").find(".depart_time").text());
													 if(new Date(shifts_date+' '+depart_time)<=tempTime){
														  $(".planInfo table tr:eq("+i+")").before("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  }else{
														  $(".planInfo table tr:eq("+i+")").after("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  }
												 }else{
													 if(new Date(depart_time)<=new Date($(this).find(".depart_time").text())){
														  $(".planInfo table tr:eq("+i+")").before("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  }else{
														  $(".planInfo table tr:eq("+i+")").after("<tr><td class='info_left'>无数据 </td><td class='blanck'></td><td class='info_right'><div> <div>日期</div> <div>"+(shifts_date==null?'':shifts_date)+"</div> </div> <div> <div>班次</div> <div>"+(shifts_number==null?'':shifts_number)+"</div> </div> <div> <div>线路</div> <div>"+(line_name==null?'':line_name)+"</div> </div> <div> <div>实际发车时间</div> <div class='depart_time'>"+(depart_time==null?'':depart_time)+"</div> </div> <div> <div>始发站</div> <div>"+(up_origin_name==null?'':up_origin_name)+"</div> </div> <div> <div>终点站</div> <div>"+(up_terminal_name==null?'':up_terminal_name)+"</div> </div> <div> <div>售票（教/学）</div> <div>"+teacher_num+"/"+student_num+"</div> </div> <div> <div>当前状态</div> <div >"+inStation+"</div> </div> <div> <div>是否准点</div> <div    class=' "+(onTime=='否'?'special':'')+"'>"+onTime+"</div> </div> <div> <div>实际运营里程</div> <div>"+(up_total_distance==null?'0':up_total_distance)+"公里</div> </div> <div> <div>车牌号</div> <div>"+(vehicle_number==null?'':vehicle_number)+"</div> </div><div> <div>司机</div> <div>"+(driver_name==null?'':driver_name)+"</div> </div><td> </tr>");
														  return false;
													  } 
												 } 
											 } 
										 });
										 
									 }
									
								 });		
						},
						error : function() {	
							layer.msg('网络异常，请稍后再试!', {
								icon : 2,
								time : parent.waitTime
							}, function() {
							});
						}
				 });
				 return false;
			});
			
});