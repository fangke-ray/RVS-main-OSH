/** 一览数据对象 */
var listdata = {};
var recept_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_assign.do";

$(function(){
	$("input.ui-button").button();
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#infoes").buttonset();
	
	//载入
	$("#uploadbutton").click(function(){
		uploadfile();
	});
	
	
	$("#comfirebutton").click(function(){
		//doInit(listdata);
		changeMaterialStatus();

	});
	
	$("#resetbutton").click(function(){
		$("#body-mdl").show();
		$("#body-detail").hide();
		reset();
	});
	
	$("#unarrive_partial_button").click(function(){
		$("#unarrive_partial").show();
		$("#arrive_partial").hide();
	});
	
	$("#arrive_partial_button").click(function(){
		$("#arrive_partial").show();
		$("#unarrive_partial").hide();
	});
	
	/*提交*/
	$("#submitbutton").click(function(){
		update_material_partial_detail();
	});
	
	$("#uploaddatebutton").click(function(){
		uploadArrivePlanDate();
	});
	
	reset();
	doInit();
	
});

var doInit=function(obj){
	recept_list([]);
};

var reset=function(){
		$("#file").val("");
		$("#opd_file").val("");
};

var changeMaterialStatus=function(){
	var data={
		"material_id":$("#hide_materialID").val(),
		"occur_times":$("#hide_occur_times").val()
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=changeMaterialStatus',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :completeChange
	});
};

var completeChange=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			recept_listdata = resInfo.responseFormList;
			recept_list(recept_listdata);
			$("#body-mdl").show();
			$("#body-detail").hide();
			// Add by Gonglm 2014/1/13 Start
			if (recept_listdata.length == 1) {
				// 单项目直接提交
				update_material_partial_detail();
			}
			// Add by Gonglm 2014/1/13 End
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var update_material_partial_detail=function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doUpdateMaterialPartialDetail',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :updateMaterialPartialDetailComplete
	});
};

var updateMaterialPartialDetailComplete=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			recept_list(resInfo.responseFormList);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
}



var uploadfile=function(){
	$.ajaxFileUpload({
		url:'upload.do?method=uploadPartialAssign', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success:function(responseText, textStatus){//服务器成功响应处理函数
			var resInfo = null;
			try{
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}else{
					recept_listdata = resInfo.finished;
					recept_list(recept_listdata);
					// Add by Gonglm 2014/1/13 Start
					// 导入单维修对象订购数据时，直接进入详细画面
					if (recept_listdata.length == 1) {
						showPartialDetail(recept_listdata[0].material_id,recept_listdata[0].occur_times);
						$("#body-mdl").hide();
						$("#body-detail").show();
					}
					// Add by Gonglm 2014/1/13 End
				}
			}catch(e){
			}
		}
	});
};

function recept_list(recept_listdata){
	if ($("#gbox_recept_list").length > 0) {
		$("#recept_list").jqGrid().clearGridData();
		$("#recept_list").jqGrid('setGridParam',{data:recept_listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#recept_list").jqGrid({
			data:recept_listdata,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','','型号','机身号','SORC NO.','等级','发放确认'],
			colModel:[
				{
					name:'material_id',
					index:'material_id',   
					hidden:true
				},
				{
					name:'occur_times',
					index:'occur_times',   
					hidden:true
				},
				{
					name:'model_name',
					index:'model_name',
					width:60
				},
				{
					name:'serial_no',
					index:'serial_no',
					width:60,
					align:'center'
				},
				{
					name:'sorc_no',
					index:'sorc_no',
					width:100,
					formatter:function(cellvalue, options, rowData){
						if(rowData.occur_times>1){
							return rowData.sorc_no+'/'+rowData.occur_times
						}
						return rowData.sorc_no
					}
				},
				{
					name:'level',
					index:'level',
					width:40,
					align:'center',
					formatter:function(cellvalue, options, rowData){
						if(cellvalue==1){
							return "S1";
						}else if(cellvalue==2){
							return "S2";
						}else if(cellvalue==3){
							return "S3";
						}
					}
				},
				{
					name:'isHistory',
					index:'isHistory',
					width:90,
					align:'center',
					formatter :'select',
					editoptions:{value:":;1:OK"}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#recept_listpager",
			viewrecords: true,
			rownumbers : true,/*行号*/
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			ondblClickRow:function(rowid,iRow,iCol,e){
				var data = $("#recept_list").getRowData(rowid);//获取行数据
				var material_id = data["material_id"];
				var occur_times=data["occur_times"];
				showPartialDetail(material_id,occur_times);
				$("#body-mdl").hide();
				$("#body-detail").show();
			}
		});
	}
};

var showPartialDetail=function(material_id,occur_times){
	$("#hide_materialID").val(material_id);
	$("#hide_occur_times").val(occur_times);
	var data = {"material_id" :material_id,"occur_times":occur_times};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};


var search_handleComplete=function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			$("#label_reception_date").text(resInfo.responseForm.reception_time.substring(0,11));	
			$("#label_customer_name").text(resInfo.responseForm.ocmName);
			$("#label_agreed_date").text(resInfo.responseForm.agreed_date);
			$("#label_sorc_no").text(resInfo.responseForm.sorc_no);
			$("#label_model_name").text(resInfo.responseForm.model_name);
			$("#label_serial_no").text(resInfo.responseForm.serial_no);
			$("#label_level").text(resInfo.responseForm.levelName);
			$("#label_service_repair_flg").text(resInfo.responseForm.status);
			$("#label_arrival_plan_date").text(resInfo.responseForm.arrival_plan_date_start);
			
			unarrive_partial_list(resInfo.finished.partial.unarriveList);
			arrive_partial_list(resInfo.finished.partial.arriveList);
			if(resInfo.finished.partial.arriveList==null||resInfo.finished.partial.arriveList.length==0){
				$("#comfirebutton").disable();
			}else{
				$("#comfirebutton").enable();
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var unarrive_partial_list=function(quotation_list){
	if ($("#gbox_unarrive_partial_list").length > 0) {
		$("#unarrive_partial_list").jqGrid().clearGridData();
		$("#unarrive_partial_list").jqGrid('setGridParam',{data:quotation_list}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#unarrive_partial_list").jqGrid({
			data:quotation_list,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','本次数量','已发放数量','未发放数量','使用工位'],
			colModel:[
				{
					name:'partial_id',
					index:'partial_id',
					hidden:true
				},
				{
					name:'code',
					index:'code',
					width:20
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:60
				},
				{
					name:'recept_quantity',
					index:'recept_quantity',
					align:'right',
					width:40,
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'cur_quantity',
					index:'cur_quantity',
					width:40,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'waiting_quantity',
					index:'waiting_quantity',
					width:40,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'process_code',
					index:'process_code',
					width:40,
					align:'right'
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#unarrive_partial_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#unarrive_partial_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#unarrive_partial_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var waiting_quantity = rowData["waiting_quantity"];
					if(waiting_quantity>0){
						pill.find("tr#" + IDS[i] + " td[aria\\-describedby='unarrive_partial_list_waiting_quantity']").addClass("ui-state-active");
					}
				}
			}
		});
	}
};

var arrive_partial_list=function(quotation_list){
	if ($("#gbox_arrive_partial_list").length > 0) {
		$("#arrive_partial_list").jqGrid().clearGridData();
		$("#arrive_partial_list").jqGrid('setGridParam',{data:quotation_list}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#arrive_partial_list").jqGrid({
			data:quotation_list,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','本次数量','已发放数量','未发放数量','使用工位'],
			colModel:[
				{
					name:'partial_id',
					index:'partial_id',
					hidden:true
				},{
					name:'code',
					index:'code',
					width:20
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:60
				},
				{
					name:'recept_quantity',
					index:'recept_quantity',
					align:'right',
					width:40,
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'cur_quantity',
					index:'cur_quantity',
					width:40,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'waiting_quantity',
					index:'waiting_quantity',
					width:40,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'process_code',
					index:'process_code',
					width:40,
					align:'right'
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#arrive_partial_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#arrive_partial_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#arrive_partial_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var waiting_quantity = rowData["waiting_quantity"];
					if(waiting_quantity>0){
						pill.find("tr#" + IDS[i] + " td[aria\\-describedby='arrive_partial_list_waiting_quantity']").addClass("ui-state-active");
					}
				}
			}
		});
	}
};

var uploadArrivePlanDate=function(){
	$.ajaxFileUpload({
		url:'upload.do?method=doUploadArrivePlanDate', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'opd_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success:function(responseText, textStatus){//服务器成功响应处理函数
			var resInfo = null;
			try{
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}else{
					var this_dialog = $("#complete_dialog");
					if (this_dialog.length === 0) {
						$("body.outer").append("<div id='complete_dialog'/>");
						this_dialog = $("#complete_dialog");
					}
					this_dialog.text("零件入库预定日已经导入！");
					this_dialog.dialog({
							title : "上传文件完成确认",
							position :[500, 400],	
							modal : true,
							width:300,
							minHeight : 200,
							resizable : false,
							buttons : {
								"确认":function(){
									this_dialog.html("");
									this_dialog.dialog('close');
								}
							}
					});
				}
			}catch(e){
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
			}
		}
	});
};

