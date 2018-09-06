/** 一览数据对象 */
var listdata = {};
var recept_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_recept.do";
var sel_alarm_id = "";
var sel_material_id = "";
var sel_occur_times = "";

$(function(){
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	
	$("input.ui-button").button();
	
	/* radio转换成按钮 */
	$("#bo_flg").buttonset();
	
	/* date 日期 */
	$("#order_time_start,#order_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	
	$("input[name=bo]").bind('click',function(){
		$("#cond_work_procedure_order_template").val($(this).val());
	});
	/*检索*/
	$("#searchbutton").click(function(){
		$("#search_sorcno").data("post",$("#search_sorcno").val());
		$("#order_time_start").data("post",$("#order_time_start").val());
		$("#order_time_end").data("post",$("#order_time_end").val());
		findit();
	});
	
	/*清除*/
	$("#resetbutton").click(function(){
		reset();	
	});
	
	$("#receptbutton").click(function(){
		updatePartialRecept();
	});

	$("#unnessaraybutton").click(function(){
		updatePartialUnnessaray();
	});

	$("#gobackbutton").click(function(){
		$("#body-mdl").show();
		$("#body-detail").hide();
	});

	reset();
	findit();
});

/*清除检索条件*/
var reset=function(){
	$("#search_sorcno").val("").data("post","");
	$("#order_time_start").val("").data("post","");
	$("#order_time_end").val("").data("post","");
	$("#cond_work_procedure_order_template").val("");
	$("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
};


var findit=function(){
	var data={
		"sorc_no":$("#search_sorcno").data("post"),
		"order_date_start":$("#order_time_start").data("post"),
		"order_date_end":$("#order_time_end").data("post"),
		"bo_flg":$("#cond_work_procedure_order_template").val()
	}
	
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
			complete : search_Complete
	});
};

var search_Complete=function(xhrobj,textStatus){
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
};

/*零件签收对象一览*/
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
			colNames:['','','型号','机身号','修理单号','等级','BO状态','有可签收品'],  
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
					width:100
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
					name:'levelName',
					index:'levelName',
					width:40,
					align:'center'
				},
				{
					name:'bo_flg',
					index:'bo_flg',
					width:90,
					align:'center',
					formatter:'select',
					editoptions:{value:':;1:有BO;2:BO解决;9:'}
				},
				{
					name:'remarks',
					index:'remarks',
					width:60,
					align:'center',
					formatter:'select',
					editoptions:{value:':;1:有'}
				}
			],
			rowNum: 23,
			toppager: false,
			pager: "#recept_listpager",
			viewrecords: true,
			rownumbers : true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			ondblClickRow:function(rowid,iRow,iCol,e){
				var data = $("#recept_list").getRowData(rowid);//获取行数据
				sel_material_id = data["material_id"];
				sel_occur_times = data ["occur_times"];
				showPartialDetail(sel_material_id,sel_occur_times);
			}
		});
	}
};


var showPartialDetail=function(material_id,occur_times){
	var data = {
		"material_id" :material_id,
		"occur_times":occur_times
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchMaterialDetail',
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

			// 中断维修对象零件可以回收
			if (resInfo.alarm_id) {
				sel_alarm_id = resInfo.alarm_id;
				$("#unnessaraybutton").show();
			}
			else {
				sel_alarm_id = "";
				$("#unnessaraybutton").hide();
			}

			recept_partial_list(resInfo.responeList);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*零件一览*/
var recept_partial_list=function(quotation_list){
	if ($("#gbox_recept_partial_list").length > 0) {
		$("#recept_partial_list").jqGrid().clearGridData();
		$("#recept_partial_list").jqGrid('setGridParam',{data:quotation_list}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#recept_partial_list").jqGrid({
			data:quotation_list,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','','零件编号','零件名称','可签收数量','已签收数量','缺品数量','入库预订日','使用工位','消耗品','bom'],
			colModel:[
				{
					name:'material_partial_detail_key',
					index:'material_partial_detail_key',
					hidden:true
				},
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
					name:'waiting_receive_quantity',
					index:'waiting_receive_quantity',
					width:40,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'arrival_plan_date',
					index:'arrival_plan_date',
					width:50,
					align:'center',
					formatter:function(cellValue,options,rowData){
						if(rowData.status==3){
							if ("9999/12/31" == cellValue) {
								return "未定";
							}
							return cellValue || "";
						}
						return "";
					}
				},
				{
					name:'process_code',
					index:'process_code',
					width:40,
					align:'right'
				},
				{
					name:'append',
					index:'append',
					width:20,
					formatter:'select',
					align:'center',
					editoptions:{value:':;1:消耗品'}
				},
				{
					name:'bom_quantity',
					index:'bom_quantity',
					hidden:true
				}
			],
			rowNum: 69,
			toppager: false,
			pager: "#recept_partial_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			multiselect:true,
			onSelectRow:function(rowid,statue){
				var pill = $("#recept_partial_list");
				var $row = pill.find("tr#" + rowid);
				var $cb = $row.find("td[aria\\-describedby='recept_partial_list_cb'] input");
				if ($cb.is(":hidden")) {
					$cb.removeAttr("checked");
					$row.removeClass("ui-state-highlight");
				}
				enableButton();
				
			},
			onSelectAll:function(rowids,statue){
				var pill = $("#recept_partial_list");
				var $rows = pill.find("tr");
				$rows.each(function(){
					var $cb = $(this).find("td[aria\\-describedby='recept_partial_list_cb'] input");
					if ($cb.is(":hidden")) {
						$cb.removeAttr("checked");
						$(this).removeClass("ui-state-highlight");
					}
				});
				enableButton();
			},
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#recept_partial_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#recept_partial_list");
				$("#body-mdl").hide();
				$("#body-detail").show();

				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var not_quantity_data = rowData["recept_quantity"];
					if(not_quantity_data==0){
						pill.find("tr#" + IDS[i] + " td[aria\\-describedby='recept_partial_list_cb']").find("input").hide();
					}
					var bom_quantity = rowData["bom_quantity"];
					var code = rowData["code"];
					if(bom_quantity && bom_quantity > 0 && code.indexOf("*") < 0){
						pill.jqGrid("setSelection", IDS[i]);
						pill.find("tr#" + IDS[i] + " td").css("background-color", "lightblue");
					}
					if(rowData["recept_quantity"] < rowData["waiting_receive_quantity"]){
						pill.find("tr#" + IDS[i] + " td").css({"background-color": "#FFC000","color": "black"});
					} 
				}
				enableButton();
			}
		});
	}
};

var enableButton=function() {
	var rows = $("#recept_partial_list").find("tr:has('input[type=checkbox][checked]:visible')");
	if (rows.length==0){
		$("#receptbutton").disable();
		$("#unnessaraybutton").disable();
	}else{
		$("#receptbutton").enable();
		$("#unnessaraybutton").enable();
	}
}

var updatePartialUnnessaray =function(){
	var data = {};   
    var rows = $("#recept_partial_list").find("tr:has('input[type=checkbox][checked]:visible')");
    var shoString = "";
    for(var i=0;i<rows.length;i++){
	    var rowData = $("#recept_partial_list").getRowData(rows[i].id);
	  	data["keys.material_partial_detail_key[" + i + "]"] = rowData["material_partial_detail_key"];
	  	data["keys.recept_quantity[" + i + "]"] = rowData["recept_quantity"];
	  	shoString += rowData["code"] + " x " + rowData["recept_quantity"] + " ";
    }

    var $confirm = $("#unnessary_message");
    $confirm.find("textarea").val("\n退回零件：" + shoString);

    $confirm.dialog({
		resizable: false,
		modal: true,
		width: 'auto',
		title: "输入退回零件理由",
		buttons: {
			"确认": function() {
				data.reason = $confirm.find("textarea").val();
				data.alarm_id = sel_alarm_id;
				data.material_id = sel_material_id;
				data.occur_times = sel_occur_times;
				 $.ajax({
					  beforeSend : ajaxRequestType,
					  async : true,
					  url : servicePath + '?method=doUpdatePartialUnnessaray',
					  cache : false,
					  data : data,
					  type : "post",
					  dataType : "json",
					  success : ajaxSuccessCheck,
					  error : ajaxError,
					  complete : function(){
					  	findit();
					  	$confirm.dialog("close");
					  	$("#body-mdl").show();
						$("#body-detail").hide();
					  }
				 });
			},
			"取消": function() {
				$confirm.dialog("close");
			}
		}
	})
	return;
}

var updatePartialRecept=function(){
	var data = {};   
    var rows = $("#recept_partial_list").find("tr:has('input[type=checkbox][checked]:visible')");
    for(var i=0;i<rows.length;i++){
	    var rowData = $("#recept_partial_list").getRowData(rows[i].id);
	  	data["keys.material_partial_detail_key[" + i + "]"] = rowData["material_partial_detail_key"];
	  	data["keys.recept_quantity[" + i + "]"] = rowData["recept_quantity"];
    }   
	 $.ajax({
		  beforeSend : ajaxRequestType,
		  async : true,
		  url : servicePath + '?method=doUpdatePartialRecept',
		  cache : false,
		  data : data,
		  type : "post",
		  dataType : "json",
		  success : ajaxSuccessCheck,
		  error : ajaxError,
		  complete : function(){
		  	findit();
		  	$("#body-mdl").show();
			$("#body-detail").hide();
		  }
	 });
}