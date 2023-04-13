/** 一览数据对象 */
var listdata = {};
var recept_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_distrubute.do";
var privacy;
var subPartDict = {}

$(function(){
	$("input.ui-button, #cb_chooser > button").button();
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	
	$("#body-mdl .ui-widget-header > .HeaderButton > span.ui-icon," +
			"#body-detail .ui-widget-header > .HeaderButton > span.ui-icon").bind("click", function() {
		var $areacloserIcon = $(this);
		if ($areacloserIcon.hasClass('ui-icon-circle-triangle-w')) {
			$("#resetbutton").trigger("click");
			return;
		}

		$areacloserIcon.toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($areacloserIcon.hasClass('ui-icon-circle-triangle-n')) {
			$areacloserIcon.parent().parent().next().show("blind");
		} else {
			$areacloserIcon.parent().parent().next().hide("blind");
		}
	});

	findit();

	$("#resetbutton").click(function(){
		$("#body-mdl").show();
		$("#body-detail").hide();
	});
	
	/* date 日期 */
	$("#order_time_start,#order_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	
	/* radio转换成按钮 */
	$("#bo_flg").buttonset();

	$("#searchform select").select2Buttons();

	/*检索*/
	$("#searchbutton").click(function(){
		$("#search_sorcno").data("post",$("#search_sorcno").val());
		$("#order_time_start").data("post",$("#order_time_start").val());
		$("#order_time_end").data("post",$("#order_time_end").val());
		$("#cond_level").data("post",$("#cond_level").val());
		$("#cond_section_id").data("post",$("#cond_section_id").val());
		$("#cond_process_code").data("post",$("#cond_process_code").val());
		findit();
	});
	
	$("input[name=bo]").bind('click',function(){
		$("#cond_work_procedure_order_template").val($(this).val());
	});
	
	/*清除*/
	$("#rebutton").click(function(){
		reset();	
	});
	
	/*小单发放*/
	$("#submitbutton").click(function(){
		changeMaterialStatus("small");
	});
	
	/*大单发放*/
	$("#bigsubmitbutton").click(function(){
		afObj.applyProcess(231, this, changeMaterialStatus, ["big"]);
	});

	$("#inline_switch").change(function(){
		if (this.checked) {
			$(this).next("label").children("span").text("跳转投线").css({"fontSize": "14px", padding: "0.2em"});
			localStorage.inline_switch = 1;
		} else {
			$(this).next("label").children("span").text("不跳转投线").css({"fontSize": "14px", padding: "0.2em"});
			localStorage.inline_switch = 0;
		}
	});
	if (localStorage.inline_switch == undefined) localStorage.inline_switch = 1; 
	if (localStorage.inline_switch && localStorage.inline_switch == 1) {
		$("#inline_switch").attr("checked", "checked").trigger("change");
	} else {
		$("#inline_switch").removeAttr("checked").trigger("change");
	}

	/*BO 判定*/
	$("#evalbobutton").click(function(){
		changeMaterialStatus("check");
	});

	/*删除*/
	$("#deletebutton").click(function(){
		deletePartial();
	});
	
	privacy=$("#privacy").val();

	$("#check_use_ns_component").change(changeNsCompSet);

	$("#cb_chooser").on("click", "button", function(){
		var $trs = $("#arrive_partial_list tr[id]");
		switch($(this).text()) {
			case "全部选择": {
				$trs.each(function(idx, ele){
					var $tr = $(ele);
					if (!$tr.hasClass("ui-state-highlight")) $tr.trigger("click");
				});
				break;
			}
			case "仅NS 工程选择": {
				// aria-describedby="arrive_partial_list_line_name"
				$trs.each(function(idx, ele){
					var $tr = $(ele);
					if ($tr.children("[aria\\-describedby=arrive_partial_list_line_name]").text() === "NS 工程") {
						if (!$tr.hasClass("ui-state-highlight")) $tr.trigger("click");
					} else {
						if ($tr.hasClass("ui-state-highlight")) $tr.trigger("click");
					}
				});
				break;
			}
			case "全部取消": {
				$trs.each(function(idx, ele){
					var $tr = $(ele);
					if ($tr.hasClass("ui-state-highlight")) $tr.trigger("click");
				});
			}
		}
		$("#cb_chooser").data("choosed", true);
	})
	$("#cb_chooser").on("mouseleave", function(){
		if ($(this).data("choosed")) {
			$(this).hide();
		}
	});
});

/*清除检索条件*/
var reset=function(){
	$("#search_sorcno").val("").data("post","");
	$("#order_time_start").val("").data("post","");
	$("#order_time_end").val("").data("post","");
	$("#cond_work_procedure_order_template").val("");
	$("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
	$("#cond_level").val("").data("post","").trigger("change");
	$("#cond_section_id").val("").data("post","").trigger("change");
	$("#cond_process_code").val("").data("post","").trigger("change");
};

var findit=function(){
	var data={
		"sorc_no":$("#search_sorcno").data("post"),
		"order_date_start":$("#order_time_start").data("post"),
		"order_date_end":$("#order_time_end").data("post"),
		"bo_flg":$("#cond_work_procedure_order_template").val(),
		"level": $("#cond_level").data("post"),
		"section_id": $("#cond_section_id").data("post"),
		"process_code": $("#cond_process_code").data("post")
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
			partial_list(resInfo.responseFormList);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var partial_list=function(recept_listdata){
	if ($("#gbox_partial_list").length > 0) {
		$("#partial_list").jqGrid().clearGridData();
		$("#partial_list").jqGrid('setGridParam',{data:recept_listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#partial_list").jqGrid({
			data:recept_listdata,
			height: 388,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','','修理单号','订购经过','型号','等级','机身号','发放状态','arrival_date','维修课室', 'SAP 投线','发生工位','进展工位'],
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
					name:'sorc_no',
					index:'sorc_no',
					width:60,
					formatter:function(cellvalue, options, rowData){
						if(rowData.occur_times>1){
							return rowData.sorc_no+'/'+rowData.occur_times
						}
						return rowData.sorc_no
					}
				},
				{name:'order_time',index:'order_time', width:45, align : 'center',
					formatter:function(value,b,row) {
						if(row.bo_flg == 9){
							if (value == null || value == "") {
								return "";
							} else {
								return "<div class='finish_time_left' finish_time='" + value + "'></div>";
							}
						}else{
							return "";
						}
					}
				},
				{
					name:'model_name',
					index:'model_name',
					width:80
				},
				{
					name:'level',
					index:'level',
					width:30,
					align:'center',
					formatter:'select',
					editoptions:{value:$("#material_level_inline").val()}
				},
				{
					name:'serial_no',
					index:'serial_no',
					width:40,
					align:'left'
				},
				{
					name:'bo_flg',
					index:'bo_flg',
					width:40,
					align:'center',
					formatter :'select',
					editoptions:{value:":;1:有BO;9:待发放;0:无BO"}
				},
				{
					name:'arrival_date',
					index:'arrival_date',   
					hidden:true
				},
				{
					name : 'section_id',
					index : 'section_id',
					width : 50,
					align : 'center'
				}, 
				{
					name : 'inline_time',
					index : 'inline_time',
					width : 50,
					align : 'center',
					formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}
				}, 
				{
					name : 'line_name',
					index : 'line_name',
					width : 50,
					align : 'center'
				}, 
				{
					name : 'process_name',
					index : 'process_name',
					width : 50,
					align : 'center'
				}
			],
			rowNum: 100,
			toppager: false,
			pager: "#partial_listpager",
			viewrecords: true,
			rownumbers : true,/*行号*/
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			ondblClickRow:function(rowid,iRow,iCol,e){
				var data = $("#partial_list").getRowData(rowid);//获取行数据
				var material_id = data["material_id"];
				var occur_times=data["occur_times"];
				var bo_flg=data["bo_flg"];
				var arrival_date=data["arrival_date"];
				if (!arrival_date) bo_flg = 9.1; // 先判定后发放
				var section_id=data["section_id"];
				showPartialDetail(material_id,occur_times,bo_flg,section_id);
			},
			gridComplete:function(){
				var $trs = $("#partial_list").find("tr.ui-widget-content");
				$trs.each(function(){
					var $tr = $(this);
					var $td = $tr.find("td[aria\\-describedby=partial_list_order_time]");
					var $target_obj = $td.find("div.finish_time_left");
					var expected_finish_time_val = $target_obj.attr("finish_time");
					if (expected_finish_time_val != null && expected_finish_time_val != "") {
						var dtlKey = $tr.find("td[aria\\-describedby=partial_list_material_id]").text() + "_"
							+ $tr.find("td[aria\\-describedby=partial_list_occur_times]").text();
						date_time_list.add({id:dtlKey, target:$target_obj, date_time:expected_finish_time_val, permitMinutes:120});
					}

					var $arrival_date = $tr.find("td[aria\\-describedby=partial_list_arrival_date]");
					if ($arrival_date.text().length == 1) {
						$tr.find("td[aria\\-describedby=partial_list_bo_flg]").css("backgroundColor", "lightgray");
					}
				});
				refreshTargetTimeLeft(1);
			}
		});
	}
};

var showPartialDetail=function(material_id,occur_times,bo_flg,section_id){
	$("#hide_materialID").val(material_id);
	$("#hide_occur_times").val(occur_times);
	$("#bo_flg").val(bo_flg);

	if(privacy!="pm"){
		if(bo_flg==9 || bo_flg==9.1){
			$("#submitbutton").val("小单发放").show();
			$("#bigsubmitbutton").show();
			$("#evalbobutton").show();
			if (occur_times > 1) {
				$("#bigsubmitbutton").val("追加发放").next().hide();
			} else {
				$("#bigsubmitbutton").val("大单发放").next().show();
			}
			if(section_id) $("#submit_dest").text("发送课室：" + section_id); else $("#submit_dest").text("");
		}else{
			$("#submitbutton").val("发放").show();
			$("#bigsubmitbutton").hide().next().hide();
			$("#evalbobutton").hide();
			if(section_id) $("#submit_dest").text("发送课室：" + section_id); else $("#submit_dest").text("");
		}
	}else{//零件管理员
		$("#submitbutton").hide();
		$("#bigsubmitbutton").hide().next().hide();
	}

	var data = {"material_id" :material_id,"occur_times":occur_times};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchMaterialPartialDetail',
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
//	try {
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
			$("#label_location").text(resInfo.responseForm.wip_location || "-");
			if(resInfo.responseForm.arrival_plan_date_start=="9999/12/31"){
				$("#label_arrival_plan_date").text("未定");
			}else{
				$("#label_arrival_plan_date").text(resInfo.responseForm.arrival_plan_date_start);
			}

			subPartDict = {};
			if (resInfo.componentSetting) {
				$("#ns_partial_set").addClass("needNp").show();
				$("#label_use_ns_component_code").text(resInfo.componentSetting.component_code || "-")
					.data("component_partial_id", resInfo.componentSetting.component_partial_id || "");
				$("#label_use_ns_component_name").text(resInfo.componentSetting.partial_name || "-");
				$("#label_use_ns_component_serial_nos").text(resInfo.serialNosForThis || "（未交付）");

				if (resInfo.compSelected) {
					$("#ns_partial_set").addClass("compSelected");
					$("#check_use_ns_component").disable();
				} else {
					$("#check_use_ns_component").enable();
				}
				for (var i in resInfo.subPartials) {
					var subPartial = resInfo.subPartials[i];
					subPartDict[subPartial.partial_id] = subPartial.quantity;
				}
			} else {
				$("#ns_partial_set").removeClass("needNp").hide();
			}

			if (resInfo.componentInstorage) {
				$("#component_instorage").text(resInfo.componentInstorage).show();
			} else {
				$("#component_instorage").text("").hide();
			}

			arrive_partial_list(resInfo.responseList);

			$("#body-mdl").hide();
			$("#body-detail").show();
		}
//	} catch (e) {
//		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
//	}
};

var dtimes;
var arrive_partial_list=function(responseList){
	dtimes = $("#hide_occur_times").val();

	if ($("#gbox_arrive_partial_list").length > 0) {
		$("#arrive_partial_list").jqGrid().clearGridData();
		$("#arrive_partial_list").jqGrid('setGridParam',{data:responseList}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#arrive_partial_list").jqGrid({
			data:responseList,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','','零件编号','零件名称','待发放数量','发放数量','订购数量','消耗品标记','使用工程','使用工位','追加订购','status','order_flg'],
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
				},{
					name:'code',
					index:'code',
					width:40
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:200
				},
				{
					name:'waiting_quantity',
					index:'waiting_quantity',
					align:'right',
					width:50,
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'cur_quantity',
					index:'cur_quantity',
					width:50,
					align:'right',
					formatter:function(cellValue,i,rowData){
						if(rowData.waiting_quantity==0){
							return rowData.quantity;
						}else{
							return '<input type="number" ' +
									'value="'+(parseInt($("#bo_flg").val()) == 9 ? rowData.waiting_quantity : '0')+'" ' +
									'total="'+rowData.waiting_quantity+'" style="text-align:right;width: 86px;"/>';
						}
					}
				},
				{
					name:'quantity',
					index:'quantity',
					width:50,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'append',
					index:'append',
					width:50,
					align:'center',
					formatter:function(cellValue,options,rowData){
						var retValue = ""
						if(cellValue!=null){
							
							if(rowData.waiting_quantity!=0){
								 if(cellValue < rowData.waiting_quantity){
								 	retValue = '<label for="append_'+ rowData.material_partial_detail_key +'">现有 '+ cellValue+' </label>';
								 } else {
									retValue = '<label for="append_'+ rowData.material_partial_detail_key +'">现有 '+ cellValue+' </label>' +
										'<input type="checkbox" value="5" id="append_'+ rowData.material_partial_detail_key +'" name="append"/>';
								 }
							}else{
								retValue = '消耗品';
							}
						}
						if (subPartDict[rowData.partial_id]) {
							if (retValue == '消耗品') retValue = "";
							retValue += '<label class="subPart" for="subPart_'+ rowData.material_partial_detail_key +'">物料组签收</label>';
						}
						return retValue;
					}
				},
				{
					name:'line_name',
					index:'line_name',
					width:40,
					align:'center'
				},
				{
					name:'process_code',
					index:'process_code',
					width:40,
					align:'center'
				},
				{
					name:'belongs',
					index:'belongs',
					width:40,
					align:'center',
					sorttype:'integer',
					formatter:function(cellValue,options,rowData){
						var value;
						if (dtimes > 1) return "不良";
						if(cellValue==2){
							value="分室";
						}else if(cellValue==3){
							value="报价";
						}else if(cellValue==4){
							value="分解";
						}else if(cellValue==5){
							value="NS";
						}else{
							value="";
						}
						return value;
					}
				},
				{
					name:'status',
					index:'status',
					hidden:true
				},
				{
					name:'order_flg',
					index:'order_flg',
					hidden:true
				}
			],
			rowNum: 100,
			toppager: false,
			pager: "#arrive_partial_listpager",
			viewrecords: true,
			multiselect: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			onSelectRow : changevalue,
			onSelectAll : changevalue,
			gridComplete:function(){
				if(privacy!="pm"){

					var isJudge = $("#bo_flg").val() == 9.1;
					var isFirst = $("#bo_flg").val() == 9 || isJudge;
					// 得到显示到界面的id集合
					var IDS = $("#arrive_partial_list").getDataIDs();
					var $pill_parent = $("#arrive_partial_list").parent();
					var pill = $("#arrive_partial_list");;//.detach();
					// 当前显示多少条
					var length = IDS.length;
					for (var i = 0; i < length; i++) {
						// 从上到下获取一条信息
						var ID = IDS[i];
	
						var rowData = pill.jqGrid('getRowData', ID);
						if (isJudge) {
							var status = rowData["status"];
							if (status == '2') pill.jqGrid("setSelection", ID);
							else {
								pill.find("tr#" + ID).find("input[type=number]").val("0");
							}
						} else {
							var waiting_quantity = rowData["waiting_quantity"];
							if (waiting_quantity == 0 || isFirst) pill.jqGrid("setSelection", ID);
						}
	//					pill.find("tr#" + ID + " td[aria\\-describedby='arrive_partial_list_cb']").find("input").attr("checked", true);
	//					if(waiting_quantity!=0){
	//						pill.find("tr#" + ID + " td").css("background-color", "lightsalmon");	
	//					}

						var order_flg = rowData.order_flg;
						if (order_flg == 2) {
							pill.find("tr#" + ID + " td[aria\\-describedby='arrive_partial_list_partial_name']").css({"color": "lightsalmon", "fontWeight": "bold"});
						}
					}
					changevalue();
					//$pill_parent.append(pill);
				}
				var $cbAllTh = $("#jqgh_arrive_partial_list_cb").closest("th");
				$("#cb_arrive_partial_list").after("<button/>");
				$("#cb_arrive_partial_list").remove();
				$cbAllTh.unbind("click").click(cbChoose);
				$("#cb_chooser").hide();
			},
			loadComplete:function(){
				if ($("#ns_partial_set").hasClass("needNp")) {
					if (Object.keys(subPartDict).length != $("#arrive_partial_list label.subPart").length) {
						$("#ns_partial_set").removeClass("needNp").hide();
					} else {
						$("#check_use_ns_component").attr("checked", true).trigger("change");
					}
				}
			}
		});
	}
};
var cbChoose = function(){
	if ($("#cb_chooser").is(":visible")) {
		$("#cb_chooser").hide();
	} else {
		$("#cb_chooser").show(150);
		$("#cb_chooser").data("choosed", false);
	}
}
var changevalue = function(rowid,status,e){
	if (rowid instanceof Array) {
		// 全选
		var $inum = $("#arrive_partial_list").find("input[type=number]").not("[disabled]");
		$inum.each(function(){
			if (status) {
				$(this).val($(this).attr("total"));
//				$(this).parent().next().next().find('input[name=append]').attr("checked",true);
			} else {
				$(this).val(0);
//				$(this).parent().next().next().find('input[name=append]').attr("checked",false);
			}
		});
	} else {
		var $inum = $("#arrive_partial_list").find("tr#"+rowid).find("input[type=number]");
		if ($inum.attr("disabled")) {
			return;
		}
		if (status) {
			$inum.val($inum.attr("total"));
//			$("#arrive_partial_list").find("tr#"+rowid).find("input[name=append]").attr("checked",true);
		} else {
			$inum.val(0);
//			$("#arrive_partial_list").find("tr#"+rowid).find("input[name=append]").attr("checked",false);
		}
	}
};
var changeNsCompSet = function(){

	if ($("#ns_partial_set").hasClass("needNp")) {

		var $subParts = $("#arrive_partial_list").find(".subPart");

		if ($("#check_use_ns_component").is(":checked")) {
			$("#check_use_ns_component").next("label").children("span.ui-button-text").text("采用");
	
			$subParts.each(function(){
				var $td = $(this).parent();
				var $tr = $td.parent();
				var partial_id = $tr.children("td[aria\\-describedby='arrive_partial_list_partial_id']").text();
				var $inpQuantity = $tr.children("td[aria\\-describedby='arrive_partial_list_cur_quantity']").children("input");

				var quantity = subPartDict[partial_id];
				if (quantity) {
					$td.children().hide();
					$td.children(".subPart").show();
					var nmlVal = $inpQuantity.val() || 0;
					$inpQuantity.data("nmlVal", nmlVal).val(quantity).disable();
					$tr.children("td[aria\\-describedby='arrive_partial_list_cb']").children("input").hide();
				} else {
					$td.children().hide();
					$td.children().not(".subPart").show();
					$inpQuantity.val($inpQuantity.data("nmlVal") || 0).enable();
					$tr.children("td[aria\\-describedby='arrive_partial_list_cb']").children("input").show();
				}
			});
		} else {
			$("#check_use_ns_component").next("label").children("span.ui-button-text").text("不采用");
		
			$subParts.each(function(){
				var $td = $(this).parent();
				var $tr = $td.parent();
				var $inpQuantity = $tr.children("td[aria\\-describedby='arrive_partial_list_cur_quantity']").children("input");
	
				$td.children().hide();
				$td.children().not(".subPart").show();
				$inpQuantity.val($inpQuantity.data("nmlVal") || 0).enable();
				$tr.children("td[aria\\-describedby='arrive_partial_list_cb']").children("input").show();
			});
		}
	}
}

var changeMaterialStatus=function(flag){
	var postData={
		"material_id":$("#hide_materialID").val(),
		"occur_times":$("#hide_occur_times").val(),
		"flag":flag
	}
	var iii = 0;
	var sendComponent = null; // 发放了订购组件
	$("#arrive_partial_list").find("tr").each(function(idx, ele) {
		var $tr = $(ele);
		$input = $tr.find("input[type=number]");
		if ($input && $input.val()) {
			var ckValue = ($tr.find("input[name=append]").attr("checked") != null) ? 5 : 0;
			if ($tr.find(".subPart:visible").length > 0) {
				ckValue = 8; // 物料组子零件签收
			}
			var ival = $input.val();
			var iTotal = $input.attr("total");
			if (ival.match(/^[0-9]*$/) != null) { // ival.match(/^0*$/) == null && 可以等于0
				if (ival > iTotal) $input.val(iTotal);
				if (ival < 0) $input.val(0);

				ival = $input.val();
				if (flag == "small" && ival == 0 && postData.occur_times == 1) {
					return;
				}

				postData["exchange.cur_quantity[" + iii + "]"] = ival;
				postData["exchange.material_partial_detail_key[" + iii + "]"] = $tr.find("td[aria\\-describedby=arrive_partial_list_material_partial_detail_key]").text();
				postData["exchange.status["+iii+"]"]=ckValue;
				postData["exchange.partial_id[" + iii + "]"] = $input.parent().parent().find("td[aria\\-describedby='arrive_partial_list_partial_id']").text();

				if (ival > 0 && $tr.find("td[aria\\-describedby=arrive_partial_list_order_flg]").text() == "2") {
					sendComponent = postData["exchange.partial_id[" + iii + "]"] + "$" + $tr.find("td[aria\\-describedby=arrive_partial_list_code]").text();;
				}

				iii ++;
			}
		};
	});

	var subPartCnt = $(".subPart").closest("tr").length;
	var nsPartsCollected = $("#ns_partial_set").hasClass("needNp") && !$("#ns_partial_set").hasClass("compSelected") 
		&& ($(".subPart").closest("tr.ui-state-highlight").length == subPartCnt);

	if ("check" === flag) {

		if (nsPartsCollected) {
			if (subPartCnt == 0) {
				errorPop("订单中无 NS 组装组件的子零件，不能入库签收。");
			} else {
				warningConfirm("此次判定NS 组件子零件全部无 BO，是否立刻将子零件准备入库？", 
					function(){
						if ($("#check_use_ns_component:visible").is(":checked")) {
							postData["exchange.cur_quantity[" + iii + "]"] = 1;
							postData["exchange.material_partial_detail_key[" + iii + "]"] = null;
							postData["exchange.status["+iii+"]"]=7; // 使用组件
							postData["exchange.partial_id[" + iii + "]"] = $("#label_use_ns_component_code").data("component_partial_id");
							postData["exchange.code[" + iii + "]"]=$("#label_use_ns_component_code").text(); // 使用组件名
						}
						postChangeMaterialStatus(postData);
					}, 
					function(){
						postChangeMaterialStatus(postData);
					}, "物料组签收确认", "判定后就准备入库", "只判定不入库");
			}
		} else {
			postChangeMaterialStatus(postData);
		}
	} else {
		if (sendComponent && $("#component_instorage").text()) postData["sendComponent"] = sendComponent;

		if ($("#check_use_ns_component:visible").is(":checked") && !$("#ns_partial_set").hasClass("compSelected")) {
			postData["exchange.cur_quantity[" + iii + "]"] = 1;
			postData["exchange.material_partial_detail_key[" + iii + "]"] = null;
			postData["exchange.status["+iii+"]"]=7; // 使用组件
			postData["exchange.partial_id[" + iii + "]"] = $("#label_use_ns_component_code").data("component_partial_id");
			postData["exchange.code[" + iii + "]"]=$("#label_use_ns_component_code").text(); // 使用组件名
		}
		postChangeMaterialStatus(postData);
	}
};

var postChangeMaterialStatus = function(postData) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doUpdateWaitingQuantityAndStatus',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :completeChange
	});	
}

var completeChange=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			treatBackMessages("", resInfo.errors);
		} else {
			if (resInfo.omr_notifi_no && localStorage.inline_switch == 1) {
				window.location.href="materialFact.do?omr_notifi_no=" + resInfo.omr_notifi_no + "&switch_from=partial_distrubute"
				return;
			} else if (resInfo.to_wip_material_id) {

				var $wip_pop = $("#wip_pop");
				if ($wip_pop.length == 0) {
					$("body").append("<div id='wip_pop'></div>");
					$wip_pop = $("#wip_pop");
				}
				$wip_pop.hide();
				$wip_pop
					.attr({material_id : resInfo.to_wip_material_id});
		
		
				showChooseMap($wip_pop, "BO 品入库", 
					{
						occupied : 1,
						for_agreed : 1,
						material_id : resInfo.to_wip_material_id
					}, 
					function(sel_wip_location, options) {
						var data = {
							material_id : options.material_id,
							"wip_location" : sel_wip_location
						}
						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : 'wip.do?method=doputin',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function() {
								completeChangeBack();
							}
						});
					}
				);
	
//				getBoLocationMap(resInfo.to_wip_material_id);
			}
			if (resInfo.componentInstorage) infoPop(resInfo.componentInstorage);
			completeChangeBack();
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var completeChangeBack=function(){
	$("#body-mdl").show();
	$("#body-detail").hide();
	findit();
}

var deletePartial=function(){
	var rowids = $("#arrive_partial_list").jqGrid("getGridParam", "selarrrow");
	var data={
		"material_id":$("#hide_materialID").val(),
		"occur_times":$("#hide_occur_times").val(),
		"flag":"small"
	};
	for (var ii=0;ii < rowids.length;ii++){
		var rowData = $("#arrive_partial_list").getRowData(rowids[ii]);
		data["delete.material_partial_detail_key[" + ii + "]"] = rowData.material_partial_detail_key;
	}
	
	if (data["delete.material_partial_detail_key[0]"] != null)
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doDelete',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : delete_Complete
	});
};

var delete_Complete=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
		} else {
			showPartialDetail($("#hide_materialID").val(),$("#hide_occur_times").val(),$("#bo_flg").val());
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var getBoLocationMap = function(material_id) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'wip.do?method=getBoLocationMap',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			getBoLocationMap_handleComplete(xhrobj, material_id);
		}
	});
}

var getBoLocationMap_handleComplete = function(xhrobj, material_id) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		
		return;
	}

	var $wip_pop = $("#wip_pop");
	if ($wip_pop.length == 0) {
		$("body").append("<div id='wip_pop'></div>");
		$wip_pop = $("#wip_pop");
	}
	$wip_pop.hide();
	$wip_pop.html(resInfo.storageHtml)
		.attr({material_id : material_id});

	var $dialog = $wip_pop.dialog({
		title : "放入BO库位",
		width : 720,
		resizable : false,
		modal : true,
		buttons : {
			"关闭" : function(){
				$dialog.dialog("close");
			}
		}
	});

	$wip_pop.find("table").click(function(e){
		if ("TD" == e.target.tagName) {
			if (!$(e.target).hasClass("wip-heaped")) {
				var material_id = $wip_pop.attr("material_id");

				wip_location = $(e.target).text();

				var data = {
					material_id : material_id,
					"wip_location":wip_location
				}
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'wip.do?method=doputin',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function() {
						completeChangeBack();
						$wip_pop.dialog("close");
					}
				});
			}
		}
	});

	$wip_pop.show();
}