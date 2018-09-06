var quotation_listdata = {};

/*提交路径*/
var servicePath = "compose_storage.do";

/*入口*/
$(function(){
	/*将Select转换成Button*/
	$("#search_category_id,#search_section_id").select2Buttons();
	
	$("#cond_work_procedure_order_template_flg_set").buttonset();
	/*Button*/
	$("input.ui-button").button();
	/*检索条件的右上角的超链接将鼠标移动上去和移走鼠标后的样式*/
	$("a.areacloser").hover(
		function (){
			$(this).addClass("ui-state-hover");
		},
		function (){
			$(this).removeClass("ui-state-hover");
		}
	);
	
	/*检索条件的右上角的超链接收缩*/
	$("#toggle").click(function(){
		$("#main").slideToggle(500);	
	});
	/*日期*/
	$("#search_scheduled_date_start,#search_scheduled_date_end,#search_arrival_plan_date_start,#search_arrival_plan_date_end,#search_com_scheduled_date_start,#search_com_scheduled_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	
	$("input[name=bo]").bind('click',function(){
		$("#cond_work_procedure_order_template").val($(this).val());
	});
	
	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
		if (this.value.length === 11) {
			doStart();
		}
	});
	$("#scanner_inputer").keyup(function(){
		if (this.value.length >= 11) {
			doStart();
		}
	});
	
	$("#search_section_id").val("00000000001").trigger("change");
	
	/*点击检索按钮执行查询*/
	$("#searchbutton").click(function(){
		$("#search_category_id").data("post",$("#search_category_id").val());
		$("#search_sorcno").data("post",$("#search_sorcno").val());
		$("#search_serial_no").data("post",$("#search_serial_no").val());
		$("#search_section_id").data("post",$("#search_section_id").val());
		$("#search_scheduled_date_start").data("post",$("#search_scheduled_date_start").val());
		$("#search_scheduled_date_end").data("post",$("#search_scheduled_date_end").val());
		$("#search_arrival_plan_date_start").data("post",$("#search_arrival_plan_date_start").val());
		$("#search_arrival_plan_date_end").data("post",$("#search_arrival_plan_date_end").val());
		$("#cond_work_procedure_order_template").data("post",$("#cond_work_procedure_order_template").val());
		$("#search_com_scheduled_date_start").data("post",$("#search_com_scheduled_date_start").val());
		$("#search_com_scheduled_date_end").data("post",$("#search_com_scheduled_date_end").val());
		findit();//调用检索方法
	});
	
	/*点击重置按钮将检索条件清空*/
	$("#resetbutton").click(function(){
		reset();
	});
	
	init();
});

//初始化,判断维修对象扫描是否正在进行
var init=function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=find',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInitFind_ajaxSuccess
	});
};

var doInitFind_ajaxSuccess=function(xhrobj,textStatus){
	var resInfo=null;
	try{
		eval('resInfo='+xhrobj.responseText);//取得返回数据
		if(resInfo.errors.length>0){
			treatBackMessages(null,resInfo.errors);// 共通出错信息框
		}else{
			if(resInfo.finished==null){
				$("#search_section_id").data("post",$("#search_section_id").val());
				findit();
			}else{
				showComMap(resInfo.composeResultForm);
			}
		}
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
};

/*检索方法*/
var findit=function(){
	var data={
			"category_id":$("#search_category_id").data("post"),
			"sorc_no":$("#search_sorcno").data("post"),
			"serial_no":$("#search_serial_no").data("post"),
			"section_id":$("#search_section_id").data("post"),
			"scheduled_date_start":$("#search_scheduled_date_start").data("post"),
			"scheduled_date_end":$("#search_scheduled_date_end").data("post"),
			"arrival_plan_date_start":$("#search_arrival_plan_date_start").data("post"),
			"arrival_plan_date_end":$("#search_arrival_plan_date_end").data("post"),
			"bo_flg":$("#cond_work_procedure_order_template").data("post"),
			"com_scheduled_date_start":$("#search_com_scheduled_date_start").data("post"),
			"com_scheduled_date_end":$("#search_com_scheduled_date_end").data("post")
	}
	
	$.ajax({
		beforeSend:ajaxRequestType,
		async:true,//异步请求
		url:servicePath+'?method=search',//提交地址
		cache:false,//是否缓存
		data:data,//提交的数据
		type:"post",
		dataType:"json",//提交的数据类型
		success:ajaxSuccessCheck,
		error:ajaxError,
		complete:doInit_ajaxSuccess//不管成功都执行
	});
};

var doInit_ajaxSuccess=function(xhrobj,textStatus){
	var resInfo=null;
	try{
		eval('resInfo='+xhrobj.responseText);//取得返回数据
		if(resInfo.errors.length>0){
			treatBackMessages(null,resInfo.errors);// 共通出错信息框
		}else{
			compose_storage_list(resInfo.finished);
		}
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
	
};

/*重置*/
var reset=function(){
	$("#search_category_id").val("").trigger("change").data("post","");
	$("#search_sorcno").val("").data("post","");
	$("#search_serial_no").val("").data("post","");
	$("#search_section_id").val("").trigger("change").data("post","");
	$("#search_scheduled_date_start").val("").data("post","");
	$("#search_scheduled_date_end").val("").data("post","");
	$("#search_arrival_plan_date_start").val("").data("post","");
	$("#search_arrival_plan_date_end").val("").data("post","");
	$("#cond_work_procedure_order_template").val("").data("post","");
	$("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
	$("#search_com_scheduled_date_start").val("").data("post","");
	$("#search_com_scheduled_date_end").val("").data("post","");
};

/**jqGrid**/
var compose_storage_list=function(com_listdata){
	quotation_listdata=com_listdata;
	if($("#gbox_exd_list").length>0){
		$("#exd_list").jqGrid().clearGridData();//清空数据
		$("#exd_list").jqGrid("setGridParam",{data:quotation_listdata}).trigger("reloadGrid",[{current:false}]);//重新加载数据
	}else{
		$("#exd_list").jqGrid({
			//toppager:true,//是否在表格上部显示分页条
			data:quotation_listdata,
			width:992,
			height:231,
			rowhight:23,//行高
			datatype:'local',//从服务器端返回的数据类型
			colNames:['','','机种','SORC_NO.','机身号','课室','纳期','入库<br>预定日','BO','总组<br>出货安排','分解<br>产出日','NS<br>产出日','库位','','零件缺失信息'],
			colModel:[
			          {name:'material_id',index:'material_id',hidden:true},
			          {name:'goods_id',index:'goods_id',hidden:true},
			          {name:'category_name',index:'category_name',width:30,algin:'left'},
			          {name:'sorc_no',index:'sorc_no', width:60,align:'left'},
			          {name:'serial_no',index:'serial_no', width:30, align:'left'},
			          {name:'section_name',index:'section_name',width:30, align:'left'},
			          {
			        	  name:'scheduled_date',
			        	  index:'scheduled_date',//传到服务器端用来排序用的列名
			        	  width:30,
			        	  align:'center',
			        	  sorttype:'date',
			        	  formatter:'date',//对列进行格式化时设置的函数或者类型
			        	  formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}//格式化选项
			          },{
			        	  name:'arrival_plan_date',
			        	  index:'arrival_plan_date',
			        	  width:30,
			        	  align:'center',
			        	  sorttype:'date',
			        	  formatoptions:{srcformat:'Y/m/d',newformat:'m-d'},
				          formatter:function(a,b,row) {
				        	  if ("9999/12/31" == a) {
									return "未定";
								}
				        	  if (a) {
									var d = new Date(a);
									return mdTextOfDate(d);
								}
				        	  return "";
				          }
			          },{
			        	  name:'bo_flg',
			        	  index:'bo_flg',
			        	  align:'center',
			        	  width:20,
			        	  formatter:function(a,b,row){
							if (a && a==="1") {
								return "BO"
							}
							return "";
						}
			          },{
			        	  name:'com_scheduled_date',
			        	  index:'com_scheduled_date',
			        	  width:30,
			        	  align:'center',
			        	  sorttype:'date',
			        	  formatoptions:{srcformat:'Y/m/d',newformat:'m-d'},
						  formatter:function(a,b,row) {
				        	  if ("9999/12/31" == a) {
									return "另行通知";
								}
				        	  if (a) {
									var d = new Date(a);
									return mdTextOfDate(d);
								}
				        	  return "";
				          }
			          },{
			        	  name:'dec_finish_date',
			        	  index:'dec_finish_date',
			        	  width:30,
			        	  align:'center',
			        	  sorttype:'date',
			        	  formatter:'date',
			        	  formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
			          },{
			        	  name:'ns_finish_date',
			        	  index:'ns_finish_date',
			        	  width:30,
			        	  align:'center',
			        	  sorttype:'date',
			        	  formatter:'date',
			        	  formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
			          },          
			          {name:'case_code',index:'case_code',width:70,align:'left'},
			          {name:'shelf_name',index:'shelf_name',width:30,align:'left',hidden:true},
/*			          {
			        	  name:'case_code_shelf_name',
			        	  index:'shelf_name',
			        	  width:70,
			        	  align:'left',
			        	  formatter : function(value, options, rData){
			        		  	if(rData['case_code']!=null && rData['shelf_name']!=null){
			        		  		return  rData['case_code'];
			        		  	}
			        		  	return "";
			        	  }
			          },*/
			          {
			        	  name:'bo_contents',
			        	  index:'bo_contents',
			        	  width:70,algin:'left',
			        	  formatter:function(data,row){
			        		  var content = null;
			        		  try{
			        			  eval('content='+data);
			        			  if(content){
			        				 // return "分解缺品零件:" + content.dec +",NS缺品零件:"+ content.ns +",总组缺品零件:"+content.com;
									  return content.dec  +content.ns + content.com;
			        			  }
			        		  }catch(e){
			        			  return "";
			        		  }
			        		  return "";
			        	  }
			          }         
			],
			pager: "#exd_listpager",//设置分页的,在页面中是使用<div/>来放置的
			viewrecords: true,//设置是否在page bar 显示所有记录的总数
			rowNum:10,
			hidegrid : false,//
			gridview: true, // 
			pagerpos: 'right',//指定分页栏的位置
			pgbuttons: true,//是否显示翻页按钮
			pginput: false,//是否显示跳转页面的输入框
			recordpos: 'left'//显示记录的位置
		});
	}
};

/**总组展示墙**/
var showComMap=function(comm) {
	$.ajax({
		beforeSend:ajaxRequestType,
		async:true,
		url:servicePath+'?method=getcomposempty',
		cache:false,
		data:null,
		type:"post",
		dataType:"json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete:function(xhrobj){
			var resInfo=null;
			try{
				eval('resInfo =' + xhrobj.responseText);
				if(resInfo.errors.length>0){
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}else{
					$("#com_pop").hide();
					$("#com_pop").load("widgets/inline/compose_storage_map.jsp",function(responseText,textStatus,XMLHttpRequest){
						$("#com_pop").dialog({
							position : 'center',
							title : "总组签收选择",
							width : 1000,
							show: "blind",
							height : 600,// 'auto' ,
							resizable : false,
							modal : true,
							minHeight : 200,
							closeOnEscape: false,//禁用Esc按键
							open: function() {
					            $(this).parents(".ui-dialog:first").find(".ui-dialog-titlebar-close").remove();//去掉右上角关闭按钮
					        },
							buttons : {}
						});
						
						$("#com_pop").find("td").addClass("wip-empty");
						
						for(var com in resInfo.finished){
							$("#com_pop").find("td[comid="+resInfo.finished[com].scan_code+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
						}
						
						// 输入框触发，配合浏览器
						$("#scanner_com").keypress(function(){
							
							if (this.value.length === 6) {
								if(comm.goods_id!=""&& comm.goods_id!=null){
									doChangeLocation(comm.goods_id,comm.scan_code,$("#scanner_com").val());
								}
								
								if(comm.goods_id=="" || comm.goods_id==null){
									doinsertCom(comm.material_id,$("#scanner_com").val());
								}
								$("#scanner_com").val("");
							}
							
						});
						
						$("#scanner_com").keyup(function(){
							if (this.value.length >= 6) {
								
								if(comm.goods_id!="" && comm.goods_id!=null){
									doChangeLocation(comm.goods_id,comm.scan_code,$("#scanner_com").val());
								}
								
								if(comm.goods_id=="" || comm.goods_id==null){
									doinsertCom(comm.material_id,$("#scanner_com").val());
								}
								$("#scanner_com").val("");
							}
							
						});
					
						$("td[comid]").click(function() {
							$this = $(this);
							
							if(comm.goods_id!="" && comm.goods_id!=null){
								doChangeLocation(comm.goods_id,comm.scan_code,$this.attr("comid"));
							}
							
							if(comm.goods_id=="" || comm.goods_id==null){
								doinsertCom(comm.material_id,$this.attr("comid"));
							}
						});

						$("#com_pop").show();
					});
				}
			}catch(e){
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
			}
		}
	});
};

/**改变库位**/
var doChangeLocation=function(goods_id,old_scan_code,scan_code){
	var data = {goods_id :goods_id ,old_scan_code:old_scan_code,scan_code:scan_code};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doChangelocation',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",//服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : insert_handleComplete
	});
};

var insert_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#com_pop").dialog('close');	
			findit();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/**入库编辑**/
var doinsertCom=function(material_id,scan_code){
	var data = {material_id :material_id ,scan_code:scan_code};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doinsert',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",//服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : insert_handleComplete
	});
};

/**扫描**/
var doStart=function(){
	var data={
		material_id:$("#scanner_inputer").val()
	}
	
	$("#scanner_inputer").attr("value","");//扫描完毕之后清空值
	//Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};

var doStart_ajaxSuccess=function(xhrobj,textStatus){
	var resInfo=null;
	try{
		eval('resInfo='+xhrobj.responseText);//取得返回数据
		if(resInfo.errors.length>0){
			treatBackMessages(null,resInfo.errors);// 共通出错信息框
		}else{
			showComMap(resInfo.finished);
		}
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
};