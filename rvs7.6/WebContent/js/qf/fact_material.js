/** 模块名 */
var modelname = "现品可投线品";

/** 服务器处理路径 */
var servicePath = "materialFact.do";

var show_wip = function(rid) {

	showWipLocate(rid, true);
};

var leverDates = {
	nege1 : 0,
	nege2 : 0
};

var inlineToggle = {};
var flowSelections = {};

var showWipDialog=function(ev) {
//	if (ev.target.value == "放入 BO 区域") {
//		getBoLocationMap();
//	} else {
//		showWipEmpty();
//	}
	var $wip_pop = $("#wip_pop");
	var $list = $("#listareas table.ui-jqgrid-btable:visible") ;
	var rowid = $list.jqGrid("getGridParam","selrow");
	var rowdata = $list.getRowData(rowid);

	showChooseMap($wip_pop, "WIP 入库选择", 
		{
			occupied : 1,
			material_id : rowdata.material_id
		}, 
		function(wip_location, options) {
			var data = {
				material_id : options["material_id"],
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
					findit(keepSearchData);
				}
			});
		}
	);
}

var enablebuttonsWip1 = function(rowids) {

	
	if (rowids.length > 0) {
		$("#imgcheckbutton").enable();

		var flag_img = true; //没有画像
		var flag_ccd = true;//没有CCD
		var scheduled_expedited = false;

		var img_operate_result;
		var ccd_operate_result;

		for (var i in rowids) {
			var data = $("#list").getRowData(rowids[i]);
			var level = data["level"];
//			if (level == 9 || level == 91 || level == 92 ||level == 93) {
//			f_isLightFix(level) ||
			if (f_isPeripheralFix(level) || level == 1) {
				flag_ccd = false;
			}

			if (flag_img && data["img_check"] != "0") {
				flag_img = false;
				img_operate_result = data["img_operate_result"];
			}
			if (flag_ccd && data["ccd_change"] != "0") {
				flag_ccd = false;
//				ccd_operate_result = data["ccd_operate_result"];
			}
			if (!scheduled_expedited && data["scheduled_expedited"] != "0") {
				scheduled_expedited = true;
			}
			if (flag_img && 
				(f_isPeripheralFix(level) || data["category_name"] == "光学视管")) {
				flag_img = false;
			}
		}

		if (scheduled_expedited) {
			$("#expeditebutton").disable();
		} else {
			$("#expeditebutton").enable();
		}

		if (flag_img) {
			$("#imgcheckbutton").enable();
		} else { 
			$("#imgcheckbutton").disable();
		}

		if (rowids.length === 1) {
			var level = data["level"];
			if (!level) {
				$("#inlinestepbutton").disable();
			} else {
	//			if (level == 9 || level == 91 || level == 92 ||level == 93) { f_isLightFix(level) ||
				if ( 
					f_isPeripheralFix(level) || data["category_name"] == "光学视管") {
					if (data["agreed_date"] == null || data["agreed_date"].trim() == "") {
						$("#inlinestepbutton").disable();
					} else {
						$("#inlinestepbutton").enable();
					}
				}
	
				else if (data["agreed_date"] == null || data["agreed_date"].trim() == "") { // 没有同意日
					$("#inlinestepbutton").disable();
				} else if (flag_img) { //都不存在
					$("#inlinestepbutton").enable();
	//			} else if (flag_img && !flag_ccd) { //没有有画像有CCD，判断CCD状态
	//				if (ccd_operate_result == "完成" || ccd_operate_result == "中断") {
	//					$("#inlinestepbutton").enable();
	//				} else {
	//					$("#inlinestepbutton").disable();
	//				}
				} else if (!flag_img){ //没有有CCD有画像，判断画像状态
					if (img_operate_result && img_operate_result.indexOf("完成") >= 0) {
						$("#inlinestepbutton").enable();
					} else {
						$("#inlinestepbutton").disable();
					}
				} else { // 都存在
					if ((img_operate_result == "完成" || img_operate_result == "中断")) { //2个都是完成
						$("#inlinestepbutton").enable();
					} else {
						$("#inlinestepbutton").disable();
					}
				}
			}

			// 放回WIP
			if (data["wip_location"].trim() == "") {
				$("#inwipbutton").enable();
//				if (data["agreed_date"].trim() == "") {
//					$("#inwipbutton").val("放回WIP");
//				} else {
//					$("#inwipbutton").val("放入 BO 区域");
//				}
				$("#outwipbutton").disable();
			} else {
				$("#inwipbutton").disable();
				$("#outwipbutton").enable();
			}
		} else {
			$("#inlinestepbutton").disable();
			$("#inwipbutton").disable();
			$("#outwipbutton").disable();
		}

	} else {
		$("#imgcheckbutton").disable();
//		$("#ccdchangebutton").disable();
		
		$("#inlinestepbutton").disable();
		$("#inwipbutton").disable();
		$("#outwipbutton").disable();

		$("#expeditebutton").disable();
	}
}

var enablebuttonsWip2 = function(data){

	if (data != null) {

		if (data["agreed_date"] == null || data["agreed_date"].trim() == "") {
			$("#inlinebutton").disable();
		} else {
			$("#inlinebutton").enable();
		}

		var flag_img = true; //没有画像
		var able_ccd = true; // 可以做CCD

		var ccd_operate_result = "";
		var img_operate_result;

		var level = data["level"];
		if (f_isPeripheralFix(level) || level == 1 || data["ccd_model"] != 1) {
			able_ccd = false;
		}

		if (flag_img && data["img_check"] != "0") {
			flag_img = false;
			img_operate_result = data["img_operate_result"];
		}
		if (able_ccd && data["ccd_change"] != "0") {
			ccd_operate_result = data["ccd_operate_result"];
		}

		if (flag_img && 
			(f_isPeripheralFix(level) || data["category_name"] == "光学视管")) {
			flag_img = false;
		} else if (flag_img && !able_ccd) { //都不存在
			$("#inlinebutton").enable();
		} else if (flag_img && able_ccd) { //没有有画像有CCD，判断CCD状态
			if (ccd_operate_result == "" || ccd_operate_result == "完成" || ccd_operate_result == "中断") {
				$("#inlinebutton").enable();
			} else {
				$("#inlinebutton").disable();
			}
		} else if (!flag_img && !able_ccd){ //没有有CCD有画像，判断画像状态
			if (img_operate_result && img_operate_result.indexOf("完成") >= 0) {
				$("#inlinebutton").enable();
			} else {
				$("#inlinebutton").disable();
			}
		} else { // 都存在
			if ((img_operate_result == "完成" || img_operate_result == "中断") 
				&& (ccd_operate_result == "" || ccd_operate_result == "完成" || ccd_operate_result == "中断")) { //2个都是完成
				$("#inlinebutton").enable();
			} else {
				$("#inlinebutton").disable();
			}
		}

		if (able_ccd) {
			$("#ccdchangebutton").enable();
		} else {
			$("#ccdchangebutton").disable();
		}

		// 放回WIP
		if (data["wip_location"].trim() == "") {
			$("#inwip2button").enable();

			$("#outwip2button").disable();
		} else {
			$("#inwip2button").disable();
			// 从WIP出库
			$("#outwip2button").enable();
		}
	

	} else {
		$("#inlinebutton").disable();
		$("#inwip2button").disable();
		$("#outwip2button").disable();

		$("#ccdchangebutton").disable();

		$("#expeditebutton").disable();
	}
}

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	if ($("#listarea span.areatitle").text() === '导入更新一览') {
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		if (rowids.length === 0) {
			$("#agreebutton").disable();
		} else {
			$("#agreebutton").enable();
		}	
	} else {
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		if (rowids.length === 1) {
			$("#updateagreebutton").enable();
		} else {
			$("#updateagreebutton").disable();
		}
	}

	enablebuttonsWip1(rowids);
};

/** 根据条件使按钮有效/无效化 */
var enablebuttons2 = function() {
	var rowid = $("#exe_list").jqGrid("getGridParam", "selrow");
	if (rowid != null) {
		$("#printbutton").enable();
		var rowdata = $("#exe_list").getRowData(rowid);
		if (rowdata.unrepair_flg) {
			$("#movebutton").disable();
		} else {
			$("#movebutton").enable();
		}
	} else {
		$("#printbutton").disable();
		$("#movebutton").disable();
	}	
};

var printTicket=function() {

	var rowid = $("#exe_list").jqGrid("getGridParam","selrow");
	var rowdata = $("#exe_list").getRowData(rowid);

	var data = {
		material_id : rowdata["material_id"]
	}
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'material.do?method=printTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj, textStatus){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
			
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do"+"?method=output&fileName="+ rowdata["model_name"] + "-" + rowdata["serial_no"] +"-ticket.pdf&filePath=" + resInfo.tempFile);
					} else {
						var iframe = document.createElement("iframe");
			            iframe.src = "download.do"+"?method=output&fileName="+ rowdata["model_name"] + "-" + rowdata["serial_no"] +"-ticket.pdf&filePath=" + resInfo.tempFile;
			            iframe.style.display = "none";
			            document.body.appendChild(iframe);
					}
				}
			} catch(e) {
			}
		}
	});
};

/** 投线前设定 */
var process_set = function(rowdata) {
	var material_id = rowdata["material_id"];
	var fix_type = rowdata["fix_type"];
	var agreed_date = rowdata["agreed_date_hidden"];
	var ccd_change = rowdata["ccd_change"];
	var scheduled_expedited = rowdata["scheduled_expedited"] || "0";
	var level = rowdata["level"];

	$process_dialog = $("#process_dialog");
	$process_dialog.hide();
	// 导入投线画面
	$process_dialog.load("widgets/qf/inline-select.jsp",
				function(responseText, textStatus, XMLHttpRequest) {

		$("#is_sorc").text(rowdata["sorc_no"]);
		$("#is_level").text(rowdata["levelName"]);
		var category_kind = null;

		var material_id = rowdata["material_id"];
		var srcData = $("#step_list").jqGrid('getGridParam','data');

		var img_operate_result = null;
		for (var iDatum in srcData) {
			var srcDatum = srcData[iDatum];
			if (srcDatum.material_id == material_id) {
				category_kind = srcDatum.pat_id;
				break;
			}
		}

		if (fix_type == 1) {

			$("#pa_main").html("");
			$("#pa_main").flowchart({},{editable:false, selections: flowSelections});
			$("#pa_main").html("");

			if (level < 4) {// TODO 大修
//				if(rowdata["category_kind"] == 6) {
//					$("#search_section_id").val("00000000012").trigger("change");
//				} else {
//					$("#search_section_id").val("00000000001").trigger("change");
//				}
				$("#ref_template").val(fillZero(category_kind, 11)).trigger("change");
				if (rowdata["category_name"] == "光学视管") {// 光学视管
//					$("#search_section_id").val("00000000012").trigger("change");
					$("#ref_template").val(fillZero(category_kind, 11)).trigger("change");
	
					if (checkPart(material_id)) {
						if ($('div#errstring').length == 0) {
							$("body").append("<div id='errstring'/>");
						}
						$('div#errstring').show();
						$('div#errstring').dialog({
							dialogClass : 'ui-warn-dialog', modal : false, width : 450, title : "提示信息", 
							buttons : {
								"不零件订购投入维修" : function() { 
									process_dialog($process_dialog, rowdata);
									$(this).dialog("close"); 
								}, 
								"去订购零件" : function() {
									$(this).dialog("close"); 
								}
							}
						});
						$('div#errstring').html("<span class='errorarea'>此光学视管还未订购零件，<br>如果未订购就投线，维修品将进入Pending Area。</span>");
					}
					else {
						process_dialog($process_dialog, rowdata);
					}
				} else {
					process_dialog($process_dialog, rowdata);
				}
			} else if (level > 50 && level < 60) {// 周边
//				$("#search_section_id").val("00000000012").trigger("change");
				$("#ref_template").val(fillZero(category_kind, 11)).trigger("change");

				if (level == 57) { // E2 
					process_dialog($process_dialog, rowdata);
				} else if (checkPart(material_id)) {
					if ($('div#errstring').length == 0) {
						$("body").append("<div id='errstring'/>");
					}
					$('div#errstring').show();
					$('div#errstring').dialog({
						dialogClass : 'ui-warn-dialog', modal : false, width : 450, title : "提示信息", 
						buttons : {
							"不零件订购投入维修" : function() { 
								process_dialog($process_dialog, rowdata);
								$(this).dialog("close"); 
							}, 
							"去订购零件" : function() {
								$(this).dialog("close"); 
							}
						}
					});
					$('div#errstring').html("<span class='errorarea'>此周边设备还未订购零件，<br>如果未订购就投线，维修品将进入Pending Area。</span>");
				}
				else {
					process_dialog($process_dialog, rowdata);
				}
			} else { // 小修
//				if(rowdata["category_kind"] == 6) {
//					$("#search_section_id").val("00000000012").trigger("change");
//				} else {
//					$("#search_section_id").val("00000000001").trigger("change");
//				}
				$("#ref_template").parent().parent().hide();

				if (checkPart(material_id)) {
					if ($('div#errstring').length == 0) {
						$("body").append("<div id='errstring'/>");
					}
					$('div#errstring').show();
					$('div#errstring').dialog({
						dialogClass : 'ui-warn-dialog', modal : false, width : 450, title : "提示信息", 
						buttons : {
							"不零件订购投入维修" : function() { 
								var data = {material_id: material_id}
								// Ajax提交
								$.ajax({
									beforeSend : ajaxRequestType,
									async : true,
									url : servicePath + '?method=getPaByMaterial',
									cache : false,
									data : data,
									type : "post",
									dataType : "json",
									success : ajaxSuccessCheck,
									error : ajaxError,
									complete : showedit_handleComplete
								});
								process_dialog($process_dialog, rowdata);
								$(this).dialog("close"); 
							}, 
							"去订购零件" : function() {
								$(this).dialog("close"); 
							}
						}
					});
					$('div#errstring').html("<span class='errorarea'>此中小修理品还未订购零件，<br>如果未订购就投线，维修品将进入Pending Area。</span>");
				}

				else {
					var data = {material_id: material_id}
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=getPaByMaterial',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : showedit_handleComplete
					});
					process_dialog($process_dialog, rowdata);
				}
			}

			$("#is_section_id").val(rowdata["section_id"]).trigger("change");
		} else {
			// 单元投线
			$("#is_section_id").parent().parent().hide();
			$("#ref_template").parent().parent().hide();
			process_dialog($process_dialog, rowdata);
		}
	});
};

function checkPart(material_id) {
	var ret = false;
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=checkPart',
		cache : false,
		data : {material_id : material_id},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			ret = resInfo && resInfo.errors && resInfo.errors.length;
		}
	});	
	return ret;
}

function process_dialog($process_dialog, rowdata) {

	var material_id = rowdata["material_id"];
	var model_id = rowdata["model_id"];
	var fix_type = rowdata["fix_type"];
	var agreed_date = rowdata["agreed_date_hidden"];
	var ccd_change = rowdata["ccd_change"];
	var scheduled_expedited = rowdata["scheduled_expedited"] || "0";
	var level = rowdata["level"];
	var wip_location = null;
	if (f_isPeripheralFix(level)) {
		wip_location = rowdata["wip_location"];
	}

$process_dialog.dialog({
	title : "投线设置",
	width : 720,
	show: "blind",
	height : 620,// 'auto' ,
	resizable : false,
	modal : true,
	minHeight : 200,
	close : function(){
		$process_dialog.html("");
	},
	buttons : {
		"投线":function(){
			var section_id = $("#is_section_id").val();
			if (fix_type == 1) {
				if (!section_id) {
					treatBackMessages("#editarea", [{"errmsg":"请选择投线课室。"}]);
					return;
				}
				var ref_template_id = $("#ref_template").val();
				if ($("#ref_template").parent().is(":visible") && !ref_template_id) {
					treatBackMessages("#editarea", [{"errmsg":"请选择维修流程。"}]);
					return;
				}
			}
			var data = {
				"material_id": material_id,
				"model_id": model_id,
				"pat_id" : ref_template_id,
				"section_id": section_id,
				"fix_type" : fix_type,
				"agreed_date": agreed_date,
				ccd_change : ccd_change,
				scheduled_expedited :  scheduled_expedited,
				level : level,
				wip_location : wip_location
			}
			// 指定投线后先进行CCD盖玻璃更换
			if (rowdata["ccd_operate_result"] == "已指定"){
				data.ccd_change = true;
			}
			afObj.applyProcess(202, this, doInline, [data]);
		}, "关闭" : function(){ $(this).dialog("close"); }
	}
});

$process_dialog.show();

}

/** 投线 */
function doInline(postData){
	$.ajax({
		beforeSend : ajaxRequestType,
		async: false, 
		url : servicePath + '?method=doInline',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#inlineForm", resInfo.errors);
			} else {
				$("#process_dialog").dialog('close');
				findit(keepSearchData);
				findexeit();
			}
		}
	});
}

var uploadfile = function() {
		// 覆盖层
	panelOverlay++;
	makeWindowOverlay();

	// ajax enctype="multipart/form-data"
	$.ajaxFileUpload({
		url : 'upload.do?method=doagree', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'agreed_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			panelOverlay--;
			killWindowOverlay();

			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
			
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
//					listdata = resInfo.list;
					findit();
					$("#process_dialog").dialog('close');
					$("#process_dialog").text("导入客户同意日期完成。");
					$("#process_dialog").dialog({
						resizable : false,
						modal : true,
						title : "导入日期确认",
						buttons : {
							"确认" : function() {
								$(this).dialog("close");
							}
						}
					});
				}
			} catch(e) {
				
			}
		}
	});
};

var import_over = function() {
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]).jqGrid('hideCol', 'old_agreed_time').jqGrid('setGridWidth', '1248');

	$("#importdifferbutton").hide().attr("checked", false).next("label").hide();
	$("#searchbutton").enable();
	$("#agreebutton").enable().attr("checked", true);
	$("#listarea span.areatitle").text(modelname + "一览");

	enablebuttons();
};

var update_agree = function(material_id, agreed_date) {
	var $update_agree_dialog = $("#update_agree_dialog");
	$update_agree_dialog.hide();

	// 导入返工画面
	$update_agree_dialog.html("<input type='text' id='edit_update_agreed_date' value='"+(agreed_date=="undefined" ? "" : agreed_date)+"'/>");

	$update_agree_dialog.dialog({
//		position : [ 800, 20 ],
		title : "选择同意日期",
		width : 280,
		show: "blind",
		height : 180,// 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			$update_agree_dialog.html("");
		},
		buttons : {
			"确定":function(){
				var date = $("#edit_update_agreed_date").val();
				if (!date) {
					//treatBackMessages("#editarea", [{"errmsg":"请选择客户同意日期。"}]);
					//return;
				}
				
				var data = {
					"material_id" : material_id,
					"agreed_date" : date
				}
				$.ajax({
					data: data,
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					url : servicePath +"?method=doupdateAgreedDate",
					type : "post",
					complete : function(xhrobj, textStatus){
						findit(keepSearchData);
						$update_agree_dialog.dialog('close');
					}
				})
			}, "关闭" : function(){ $update_agree_dialog.dialog("close"); }
		}
	});
	$("#edit_update_agreed_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天",
		maxDate :0
	});
	
	$("#process_dialog").show();
}
var import_agree = function() {
	if ($("#listarea span.areatitle").text() === '导入更新一览') {
		
		import_over();
	} else {
		$("#process_dialog").hide();
		// 导入返工画面
		$("#process_dialog").html("<input name='file' id='agreed_file' type='file'/>")

		$("#process_dialog").dialog({
	//		position : [ 800, 20 ],
			title : "选择上传文件",
			width : 280,
			show: "blind",
			height : 180,// 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			close : function(){
				$("#process_dialog").html("");
			},
			buttons : {
				"上传":function(){
					uploadfile();
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#process_dialog").show();
	}
};
var expedite = function() {
	var data = {};
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	for (var i in rowids) {
		var rowdata = $("#list").getRowData(rowids[i]);
		data["materials.material_id[" + i + "]"] = rowdata["material_id"];
	}

	$.ajax({
		data : data,
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		url: servicePath + "?method=doexpedite",
		type : "post",
		complete : function(){
			findit(keepSearchData);
		}
	});
}

var move_section = function() {
	infoPop('v' + $("#sections").html());
}

var winScroll = function(didSwitch) {
	var jwin = $(window);
    var scrolls = jwin.scrollTop() + jwin.height();
	var localer = $("#functionarea").position().top;
	var bar = $("#functionarea > div:visible");
    // 
	if (scrolls - $("#listarea").position().top > 120) {
        if ((scrolls - localer + 40 > bar.height())){
        	if (bar.hasClass("bar_fixed")) {
        		bar.removeClass("bar_fixed");
        	}
        } else {
        	if (!bar.hasClass("bar_fixed") || didSwitch) {
        		bar.addClass("bar_fixed");
        	}
        }
	} else {
    	if (bar.hasClass("bar_fixed")) {
    		bar.removeClass("bar_fixed");
    	}
	}
//
//        localer = $("#functionarea2").position().top;
//		bar = $("#functionarea2 div:eq(0)");
//
//		if (scrolls - $("#exelistarea").position().top > 120) {
//			if ((scrolls - localer > bar.height())){
//	        	if (bar.hasClass("bar_fixed")) {
//	        		bar.removeClass("bar_fixed");
//	        	}
//	        } else {
//	        	if (!bar.hasClass("bar_fixed")) {
//	        		bar.addClass("bar_fixed");
//	        	}
//	        }
//		} else {
//        	if (bar.hasClass("bar_fixed")) {
//        		bar.removeClass("bar_fixed");
//        	}
//		}

}

$(function() {
	$("input.ui-button").button();
	$("#search_level, #search_fix_type, #search_direct, #search_section_id").select2Buttons();
	setReferChooser($("#search_modelname"));
	$("#search_agreed").buttonset();
	
	$("#search_agreed_date_start, #search_agreed_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		findit();
	});

	$("#searcharea span.ui-icon,#wiparea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#listarea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$("#functionarea").show("fade");
			$("#gbox_list").show("blind");
		} else {
			$("#gbox_list").hide("blind");
			$("#functionarea").hide("fade");
		}
	});

	$("#resetbutton").click(function() {
		$("#search_sorc_no").val("");
		$("#txt_modelname").val("");
		$("#search_modelname").val("");
		$("#search_serial_no").val("");
		$("#search_level").val("").trigger("change");
		$("#search_fix_type").val("").trigger("change");
		$("#search_direct").val("").trigger("change");
		$("#search_esas_no").val("");
		$("#search_section_id").val("");
		$("#search_agreed_date_start").val("");
		$("#search_agreed_date_end").val("");
		$("#search_wip_location").val("");
		
//		$("#list").trigger('reloadGrid');
	});

	$("#agreebutton").click(import_agree);

	$("#movebutton").click(move_section);

	$("#inlinesbutton").click(showInlinePlan);

	$("#outwipbutton, #outwip2button").click(warehousing);

	$("#steplistarea, #functionarea2, #exelistarea, #functionarea3").hide();
	// scroll2fix
	$(window).bind("scroll", winScroll);

	$("#countarea > .ui-widget-content:eq(0)").height(
		$("#searcharea > .ui-widget-content:eq(0)").height());

	$("#listareas .areatitle").click(function(ev) {
		var $areatitle = $(this);
		if ($areatitle.hasClass("checked")) {
			return;
		}
		switch($areatitle.text()) {
			case "准备投线品一览" : {
				$("#listarea, #functionarea1, #searcharea").show();
				$("#steplistarea, #exelistarea, #functionarea2, #functionarea3, #countarea").hide();
				break;
			}
			case "投线进行品一览" : {
				$("#steplistarea, #functionarea2, #searcharea").show();
				$("#listarea, #exelistarea, #functionarea1, #functionarea3, #countarea").hide();
				break;
			}
			case "今日投线品一览" : {
				$("#exelistarea, #functionarea3, #countarea").show();
				$("#listarea, #steplistarea, #functionarea1, #functionarea2, #searcharea").hide();
				break;
			}
		}
		winScroll(true);
	});

	$("#printbutton").click(printTicket);

	$("#inlinestepbutton").click(function(){
		var $list = $("#list");
		var rowids = $list.jqGrid("getGridParam", "selarrrow");
		var rowdata = $list.getRowData(rowids[0]);
		var level = rowdata["level"];

		if (!f_isLightFix(level) && rowdata["ccd_model"] == 1 && rowdata["ccd_operate_result"] == "" && level != 1) {
			// "是CCD 盖玻璃对象型号的维修对象，请确定不需要做 CCD 盖玻璃更换即可投线？"
			warningConfirm(rowdata["sorc_no"]+"是CCD 盖玻璃对象型号的维修对象，请确定不需要做 CCD 盖玻璃更换？",
				function(){
					afObj.applyProcess(201, this, inline_start, [rowdata]);
				});
		} else {
			afObj.applyProcess(201, this, inline_start, [rowdata]);
		}
	});

	$("#inlinebutton").click(function(){
		var $list = $("#step_list");
		var rowids = $list.jqGrid("getGridParam", "selrow");
		var rowdata = $list.getRowData(rowids);
		var level = rowdata["level"];
//		var isLightFix = false;
//		if (level == 9 || level == 91 || level == 92 ||level == 93) {
//			isLightFix = true;
//		}

		if (!f_isLightFix(level) && rowdata["ccd_model"] == 1 && rowdata["ccd_operate_result"] == "" && level != 1) {
			// "是CCD 盖玻璃对象型号的维修对象，请确定不需要做 CCD 盖玻璃更换即可投线？"
			warningConfirm(rowdata["sorc_no"]+"是CCD 盖玻璃对象型号的维修对象，请确定不需要做 CCD 盖玻璃更换？",
				function(){process_set(rowdata)});
		} else {
			process_set(rowdata);
		}
	});
	$("#expeditebutton").click(expedite);
	
	$("#updateagreebutton").click(function(){
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		var data = $("#list").getRowData(rowids[0]);
		var material_id = data["material_id"];
		var agreed_date = data["agreed_date_hidden"];
		update_agree(material_id, agreed_date);
	});
	$("#imgcheckbutton").click(doImgCheck);
	$("#ccdchangebutton").click(doCCDChange);
	$("#outbutton").click(exportReport);
	$("#inwipbutton").click(showWipDialog);
	$("#inwip2button").click(showWipDialog);

	leverDates.nege1 = new Date($("#oneDayBefore").val()).getTime() || 0;
	leverDates.nege2 = new Date($("#twoDaysBefore").val()).getTime() || 0;

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : "processAssignTemplate.do?method=getPositions",
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getPositions_handleComplete
	});

	initGrid();
	findit();
	findexeit();
	takeWs();
});


// 工位后台推送
function takeWs() {
	$(".if_message-dialog input[value=忽略]").click(function(){
		$(".if_message-dialog").removeClass("show");
	});
	$(".if_message-dialog input[value=刷新]").click(function(){
		$(".if_message-dialog").removeClass("show");
		findit(keepSearchData);
	});

	try {
	// 创建WebSocket  
	var position_ws = new WebSocket(wsPath + "/position");
	// 收到消息时做相应反应
	position_ws.onmessage = function(evt) {
    	var resInfo = {};
    	try {
    		resInfo = $.parseJSON(evt.data);
    		if ("refreshWaiting" == resInfo.method) {
    			findit(keepSearchData);
    			// $(".if_message-dialog").addClass("show");
    		}
    	} catch(e) {
    	}
	};  
 	// 连接上时走这个方法  
	position_ws.onopen = function() {     
		position_ws.send("entach:"+"#00000000053#"+$("#op_id").val());
	}; 
	} catch(e) {
	}
};

function exportReport() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=report',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
			if (resInfo && resInfo.fileName) {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			} else {
				errorPop("文件导出失败！");
			}
		}
	});
}

function doImgCheck(){
	var rowids = $("#list").jqGrid("getGridParam","selarrrow");
	var ids = [];
	for (var i in rowids) {
		var data = $("#list").getRowData(rowids[i]);
		ids[ids.length] = data["material_id"];
	}
	
	$.ajax({
		data : {ids:ids.join(",")},
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		url: servicePath + "?method=doImgCheck",
		type : "post",
		complete : function(){
			findit(keepSearchData);
		}
	});
}

function doCCDChange(){
	var $list = $("#listareas table.ui-jqgrid-btable:visible") ;
	var rowids = $list.jqGrid("getGridParam","selarrrow");
	if (rowids.length == 0) {
		rowid = $list.jqGrid("getGridParam","selrow");
		if (rowid) {
			rowids.push(rowid);
		}
	}
	var ids = [];

	for (var i in rowids) {
		var data = $list.getRowData(rowids[i]);
		ids[ids.length] = data["material_id"];
	}

	if (ids.length == 0) {
		return;
	}
	$.ajax({
		data : {ids:ids.join(",")},
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		url: servicePath + "?method=doCCDChange",
		type : "post",
		complete : function(){
			findit(keepSearchData);
		}
	});
}

/*
* 取得流程信息返回
*/
var getPositions_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			flowSelections = resInfo.list;
			$("#pa_main").flowchart({},{editable:false, selections: flowSelections});
			$("#pa_main").html("");
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

function initGrid() {
	$("#list").jqGrid({
			toppager : true,
			data: [],
			height: 1151,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames:['', '修理单号', '型号 ID', '型号', '类别', '机种', '机身号', '返还要求', '加急','level','等级', '报价日期', '客户同意日', '纳期','同意时间','流水分类','备注', 'WIP位置','存在画像检查','图象检查','CCD对象型号','存在CCD盖玻璃更换'
			,'CCD盖玻璃'
			,'投入课室 ID' ,'维护对象ID', 'wip_date(过期的)'],
			colModel:[
				{name:'myac', width:35, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true,delbutton:false }, hidden:true},
				{name:'sorc_no',index:'sorc_no', width:50},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_name', width:120},
				{name:'category_kind',index:'category_kind', width:60, hidden : true, formatter:'select', editoptions:{value:$("#kOptions").val()}},
				{name:'category_name',index:'category_name', width:60},
				{name:'serial_no',index:'serial_no', width:50},
				{name:'unrepair_flg',index:'unrepair_flg', align:'center', width:45, formatter:'select', editoptions:{value:"1:Unrepair;0:"}},
				{name:'scheduled_expedited',index:'scheduled_expedited', align:'center', width:30, formatter:'select', editoptions:{value:"2:直送快速;1:加急;0:"}},
				{name:'level',index:'level', hidden:true},
				{name:'levelName',index:'levelName', width:35, align:'center'},
				{name:'quotation_time',index:'quotation_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'scheduled_date',index:'scheduled_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}},
				{name:'agreed_date_hidden', hidden:true, formatter:function(a,b,row){
					return row.agreed_date || ""
				}},
				{name:'fix_type', index:'fix_type', hidden:true},
				{name:'remark',index:'remark', width:95},
				{name:'wip_location',index:'wip_location', width:60},
				{name:'img_check',index:'img_check', hidden:true},
				{name:'img_operate_result',index:'img_operate_result', width:60},
				{name:'ccd_model',index:'ccd_model', hidden:true},
				{name:'ccd_change',index:'ccd_change', hidden:true},
				{name:'ccd_operate_result',index:'ccd_operate_result', width:60},
				{name:'section_id', index:'section_id',hidden:true},
				{name:'material_id', index:'material_id',hidden:true},
				{name:'wip_date', index:'wip_date',hidden:true}
			],
			rowNum: 50,
			rownumbers : true,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			caption: "",
			multiselect: true,
			multiboxonly: true,
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#list").getRowData(rid);

				var material_id = data["material_id"];
				showDetail(material_id);
			},
			onSelectRow : enablebuttons,
			onSelectAll : enablebuttons,
			gridComplete : function() {
				enablebuttons();
				$("#gbox_list tr td[aria\\-describedby='list_wip_location']").unbind("click");
				$("#gbox_list tr td[aria\\-describedby='list_wip_location']").click(function() {
					var location = $(this).text();
					if (location && location.substring(0,3) != "BO-") {
						showLocate(location);
					}					
				});
				var jthis = $("#list");

				var srcData = jthis.jqGrid('getGridParam','data');

				var dataIds = jthis.getDataIDs();
				var length = dataIds.length;
				for (var i = 0; i < length; i++) {
					var rowdata = jthis.jqGrid('getRowData', dataIds[i]);
					var vagreed_date = rowdata["agreed_date_hidden"];
//					var ccd_operate_result = rowdata["ccd_operate_result"];
					var img_operate_result = rowdata["img_operate_result"];
					var vunrepair_flg = rowdata["unrepair_flg"];
					if (vunrepair_flg == "1") {
						$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_unrepair_flg']").addClass("alertCell");
					}

					var img_completed = img_operate_result && (img_operate_result.indexOf("完成") >= 0);

					if (!img_completed) {
						if (!f_isPeripheralFix(rowdata.level) && rowdata["category_name"] != "光学视管") {
							var material_id = rowdata["material_id"];
							var wip_date = rowdata["wip_date"];
							for (var iDatum in srcData) {
								var srcDatum = srcData[iDatum];
								if (srcDatum.material_id == material_id) {
									wip_date = srcDatum.wip_date;
									break;
								}
							}
							if (wip_date) {
								$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_wip_location']").addClass("alertCell");
							}
						}
					}
					// 同意日以外的禁止投线
					var othForbid = false;
					if (rowdata.category_name.toUpperCase().indexOf("ENDOEYE") < 0) {
						switch (rowdata.part_order) {
							case "1": case "7": othForbid = true; break; 
						}
					}
//					if (!f_isLightFix(rowdata.level)) {
//						if (img_operate_result.indexOf("零件") < 0) {
//							othForbid = (img_operate_result != "" && !img_completed);
//						} else {
//							if (rowdata.level != 57) {
//								othForbid = (img_operate_result == "未订购零件") || (rowdata.ccd_operate_result == "缺零件");
//							}
//						}
//					}

					if (!vagreed_date
						|| othForbid) {
						$("#list tr#" + dataIds[i] + " td").not(".alertCell").css("background-color", "#EEEEEE");
					} else {
						if (new Date(vagreed_date).getTime() < leverDates.nege2)
							$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_agreed_date']").addClass("alertCell");
					}

					if (!rowdata.level) {
						$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_levelName']").addClass("alertCell");
					}
				}

				if (srcData.length == 1) {
					jthis.jqGrid('setSelection', "1");
				}
			}
		});

		$("#step_list").jqGrid({
			toppager : true,
			data: [],
			height: 1151,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames:['', '修理单号', '型号 ID', '型号', '类别', '机种', '机身号', '加急','level','等级', '客户同意日', '零件订购', '纳期','同意时间','流水分类','备注', 'WIP位置','存在画像检查','图象检查','CCD对象型号','存在CCD盖玻璃更换'
			,'CCD盖玻璃', '中小修首工位', 'SAP 投线'
			,'投入课室 ID' ,'维护对象ID', 'wip_date(过期的)'],
			colModel:[
				{name:'myac', width:35, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true,delbutton:false }, hidden:true},
				{name:'sorc_no',index:'sorc_no', width:50},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_name', width:120},
				{name:'category_kind',index:'category_kind', width:60, hidden : true, formatter:'select', editoptions:{value:$("#kOptions").val()}},
				{name:'category_name',index:'category_name', width:60},
				{name:'serial_no',index:'serial_no', width:50},
				{name:'scheduled_expedited',index:'scheduled_expedited', align:'center', width:30, formatter:'select', editoptions:{value:"2:直送快速;1:加急;0:"}},
				{name:'level',index:'level', hidden:true},
				{name:'levelName',index:'levelName', width:35, align:'center'},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'part_order',index:'part_order', width:50, align:'center', formatter:'select', editoptions:{value:$("#poOptions").val()}},
				{name:'scheduled_date',index:'scheduled_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}},
				{name:'agreed_date_hidden', hidden:true, formatter:function(a,b,row){
					return row.agreed_date || ""
				}},
				{name:'fix_type', index:'fix_type', hidden:true},
				{name:'remark',index:'remark', width:95},
				{name:'wip_location',index:'wip_location', width:60},
				{name:'img_check',index:'img_check', hidden:true},
				{name:'img_operate_result',index:'img_operate_result', width:60},
				{name:'ccd_model',index:'ccd_model', hidden:true},
				{name:'ccd_change',index:'ccd_change', hidden:true},
				{name:'ccd_operate_result',index:'ccd_operate_result', width:60},
				{name:'process_code',index:'process_code', width:60, align:'center'},
				{name:'inline_time',index:'inline_time', width:50, align:'center',
					sorttype: function($cell){
						if (!$cell || $cell.trim() === "") {
							return undefined;
						}
						return $cell;
					},
					formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
				{name:'section_id', index:'section_id',hidden:true},
				{name:'material_id', index:'material_id',hidden:true},
				{name:'wip_date', index:'wip_date',hidden:true}
			],
			rowNum: 50,
			rownumbers : true,
			toppager: false,
			pager: "#step_listpager",
			viewrecords: true,
			caption: "",
			multiselect: false,
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#step_list").getRowData(rid);

				var material_id = data["material_id"];
				showDetail(material_id);
			},
			onSelectRow : function(rowid){
				enablebuttonsWip2($("#step_list").getRowData(rowid));
			},
			gridComplete : function() {
				enablebuttonsWip2(null);
				$("#gbox_step_list tr td[aria\\-describedby='step_list_wip_location']").unbind("click");
				$("#gbox_step_list tr td[aria\\-describedby='step_list_wip_location']").click(function() {
					var location = $(this).text();
					if (location && location.substring(0,3) != "BO-") {
						showLocate(location);
					}					
				});
				var jthis = $("#step_list");

				var srcData = jthis.jqGrid('getGridParam','data');

				var dataIds = jthis.getDataIDs();
				var length = dataIds.length;
				for (var i = 0; i < length; i++) {
					var rowdata = jthis.jqGrid('getRowData', dataIds[i]);
					var img_operate_result = rowdata["img_operate_result"];

					var img_completed = img_operate_result && (img_operate_result.indexOf("完成") >= 0);

					if (!img_completed) {
						if (!f_isPeripheralFix(rowdata.level) && rowdata["category_name"] != "光学视管") {
							var material_id = rowdata["material_id"];
							var wip_date = rowdata["wip_date"];
							for (var iDatum in srcData) {
								var srcDatum = srcData[iDatum];
								if (srcDatum.material_id == material_id) {
									wip_date = srcDatum.wip_date;
									break;
								}
							}
							if (wip_date) {
								$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_wip_location']").addClass("alertCell");
							}
						}
					}
					// 同意日以外的禁止投线
					var othForbid = false;
					if (rowdata.category_name.toUpperCase().indexOf("ENDOEYE") < 0) {
						switch (rowdata.part_order) {
							case "1": case "7": othForbid = true; break; 
						}
					}

					if (othForbid) {
						$("#list tr#" + dataIds[i] + " td").not(".alertCell").css("background-color", "#EEEEEE");
					}
				}

				if (srcData.length == 1) {
					jthis.jqGrid('setSelection', "1");
				}
			}
		});

		$("#exe_list").jqGrid({
			toppager : true,
			data:[],
			height: 691,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames:['','投线时间', '修理单号', '机种', 'ESAS No.', '型号 ID', '型号', '机身号', '等级','投入课室', '当前进度', '客户同意日','unrepair_flg','维护对象ID'],
			colModel:[
				{name:'myac', width:35, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true,delbutton:false }, hidden:true},
				{name:'inline_time',index:'inline_time', width:80, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'H:i'}},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'category_name',index:'category_name', width:50},
				{name:'esas_no',index:'esas_no', width:50},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_name', width:125},
				{name:'serial_no',index:'serial_no', width:50},
				{name:'levelName',index:'levelName', width:35, align:'center'},
				{name:'section_name',index:'section_name', width:80, align:'center'},
				{name:'process_code',index:'process_code', width:60, align:'center'},
//				{name:'old_agreed_time',index:'old_agreed_time', width:50, align:'center', hidden:true},//TODO: ???原客户同意日
				{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'unrepair_flg',index:'unrepair_flg', hidden:true},
				{name:'material_id', index:'material_id',hidden:true}
			],
			rowNum: 30,
			toppager: false,
			pager: "#exe_listpager",
			viewrecords: true,
//			caption: "今日投线一览",
//			multiselect: true,
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid : false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#exe_list").getRowData(rid);
				var material_id = data["material_id"];
				showDetail(material_id);
			},
			onSelectRow : enablebuttons2,
			onSelectAll : enablebuttons2,
			gridComplete : enablebuttons2
		});

}

function findexeit(xhrobj, textStatus) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchInline',
		cache : false,
		data : keepSearchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				$("#exe_list").jqGrid().clearGridData();
				$("#exe_list").jqGrid('setGridParam',{data:resInfo.list}).trigger("reloadGrid", [{current:false}]);
				setCountExe(resInfo.list);
			} catch (e) {
				console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
}

var setCountExe = function(list) {
	var count = {};
	for (var i in list) {
		var kind = "" + list[i].category_kind;
		count[kind] = (count[kind] || 0) + 1;
	}
	$("#countarea td[for]").each(function(idx, ele){
		var $ele = $(ele);
		$ele.text((count[$ele.attr("for")] || "0") + " 台");
	})
}

/*
* Ajax通信成功的处理
*/
function search_handleComplete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			loadData(resInfo.list, resInfo.step_list);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};

};

function loadData(data, step_list) {
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam',{data:data}).trigger("reloadGrid", [{current:false}]);

	$("#step_list").jqGrid().clearGridData();
	$("#step_list").jqGrid('setGridParam',{data:step_list}).trigger("reloadGrid", [{current:false}]);
}

var keepSearchData;
var findit = function(data) {
	if (!data) { //新查询
		keepSearchData = {
			"sorc_no" : $("#search_sorc_no").val(),
			"serial_no" : $("#search_serial_no").val(),
			"model_id" : $("#search_modelname").val(),
			"level" : $("#search_level").val(),
			"fix_type" : $("#search_fix_type").val(),
			"direct_flg" : $("#search_direct").val(),
			"esas_no" : $("#search_esas_no").val(),
			"section_id" : $("#search_section_id").val(),
			"agreed_date_start" : $("#search_agreed_date_start").val(),
			"agreed_date_end" : $("#search_agreed_date_end").val(),
			"wip_location": $("#search_wip_location").val()
		};
		if ($("#search_agreed_y").is(":checked")) {
			if (!$("#search_agreed_n").is(":checked")) {
				keepSearchData["check_agreed_date"] = 1;
			}
		} else {
			if ($("#search_agreed_n").is(":checked")) {
				keepSearchData["check_agreed_date"] = 2;
			}
		}
	} else {
		keepSearchData = data;
	}
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : keepSearchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};


var showDetail = function(material_id){
	var this_dialog = $("#detail_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='detail_dialog'/>");
		this_dialog = $("#detail_dialog");
	}

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("widget.do?method=materialDetail&material_id=" + material_id + "&occur_times=1",
		function(responseText, textStatus, XMLHttpRequest) {
			this_dialog.dialog({
				position : [400, 20],
				title : "维修对象详细画面",
				width : 820,
				show : "",
				height :  'auto',
				resizable : false,
				modal : true,
				buttons : {
						"取消":function(){
							this_dialog.dialog('close');
						}
				}
		});
	});
};

var showInlinePlan=function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=showInlinePlan',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (!(resInfo && resInfo.errors && resInfo.errors.length)) {
				if (resInfo.materials && resInfo.materials.length) {
					var tbodyText = "";
					for (var iM in resInfo.materials) {
						var material = resInfo.materials[iM];
						tbodyText += "<tr material_id='" + material.material_id + "'";
						var clazz = "";
						if (inlineToggle[material.material_id]) {
							clazz = "ui-state-highlight";
						}
						if (material.inline_time) {
							clazz = "inline";
						}
						if (clazz) {
							tbodyText += " class='" + clazz +  "'";
						}
						tbodyText += ">";
						// 修理单号  sorc_no as omr_notifi_no
						tbodyText += "<td>" + material.sorc_no + "</td>";
						// 型号
						tbodyText += "<td>" + material.model_name + "</td>";
						// 机身号
						tbodyText += "<td>" + material.serial_no + "</td>";
						// 等级
						tbodyText += "<td>" + material.levelName + "</td>";
						// 保内返品
						tbodyText += "<td>" + (material.service_repair_flg || "-") + "</td>";
						// 同意日
						tbodyText += "<td>" + material.agreed_date + "</td>";
						// WIP 库位
						tbodyText += (material.operate_result == 2 ? "<td>" : "<td img_check_need>") 
							+ (material.wip_location || "(无)") + "</td>";
						// 投线课室
						if (material.fix_type == 1) {
							if (material.inline_time) {
								tbodyText += "<td section_id='" + material.section_id + "'><span>" + material.section_name 
									+ "</span></td>";
							} else {
								tbodyText += "<td section_id='" + material.section_id + "'><span>" + material.section_name 
									+ "</span><span class='ui-icon ui-icon-circle-triangle-s'></span></td>";
							}
						} else {
							tbodyText += "<td colspan=3>(单元)</td>";
						}
						// 选用流程
						var isLightFix = f_isLightFix(material.level);
						if (material.fix_type == 1) {
//							if (material.level == 9 || material.level == 91 || material.level == 92 || material.level == 93) {
							if (isLightFix) {
								tbodyText += "<td>(小修自选流程)</td>";
							} else { // operator_name as pat_name
								if (material.inline_time) {
									tbodyText += "<td pat_id='" + material.pat_id + "'><span>" 
										+ material.operator_name + "</span></td>";
								} else {
									tbodyText += "<td pat_id='" + material.pat_id + "'><span>" 
										+ (material.operator_name || "(未设定)") + "</span><span class='ui-icon ui-icon-circle-triangle-s'></span></td>";
								}
							}
						}
						if (material.fix_type == 1) {
//							if (material.level == 9 || material.level == 91 || material.level == 92 || material.level == 93) {
							if (isLightFix) {
								if (material.quotation_first == 0) {
									tbodyText += "<td>零件已订购</td>";
								} else if (material.quotation_first == 1) {
									tbodyText += "<td partial_order='0'><span>零件未订购</span><span class='ui-icon ui-icon-circle-triangle-s'></span></td>";
								} else if (material.quotation_first == 2) {
									tbodyText += "<td partial_order='1'>零件已订购零件未齐</td>";
								}
							} else {
								if (material.quotation_first == -1) {
									tbodyText += "<td></td>";
								} else if (material.quotation_first == 9) {
									tbodyText += "<td ccd_change='9'><span>更换 CCD 盖玻璃</span>" +
											(material.inline_time ? "" : "<span class='ui-icon ui-icon-circle-triangle-s'></span>") +
											"</td>";
								} else if (material.quotation_first == 0) {
									tbodyText += "<td ccd_change='0'><span>不更换 CCD 盖玻璃</span>" +
											(material.inline_time ? "" : "<span class='ui-icon ui-icon-circle-triangle-s'></span>") +
											"</td>";
								}
							}
						}
						tbodyText+="</tr>";
					}

					var $inline_plan = $("#inline_plan");

					var $tbodyHtml = $(tbodyText);
					$tbodyHtml.find("td").click(function(evt){
						var $tr = $(this).parent();
						var material_id = $tr.attr("material_id");
						if (material_id) {
							if (!$tr.hasClass("inline")) {
								if (!$tr.hasClass("ui-state-highlight")) {
									$(this).parent().addClass("ui-state-highlight");
									inlineToggle[material_id] = 1;
								} else {
									$(this).parent().removeClass("ui-state-highlight");
									inlineToggle[material_id] = 0;
								}
							}
						}

						$("#inline_plan_selecter").hide();
					});
					$tbodyHtml.find(".ui-icon").click(function(evt){
						var $td = $(this).parent();
						$inline_plan.find(".changing").removeClass("changing");
						$td.addClass("changing");
						var section_id = $td.attr("section_id");
						var partial_order = $td.attr("partial_order");
						var ccd_change = $td.attr("ccd_change");
						if (section_id) {
							$("#inline_plan_selecter > select")
								.next().remove()
								.end()
								.html($("#sections").html())
								.val(section_id)
								.change(changeSection)
								.select2Buttons();
							$("#inline_plan_selecter").show()
								.css({"top" : evt.pageY, "left" : Math.min(evt.pageX, 888), "zIndex":1024});
						} else if (partial_order == 0) {
							$("#inline_plan_selecter > select")
								.next().remove()
								.end()
								.html("<option value='0'>零件未订购</option><option value='-1'>零件不需要订购</option>")
								.val(partial_order)
								.change(changePartialOrder)
								.select2Buttons();
							$("#inline_plan_selecter").show()
								.css({"top" : evt.pageY, "left" : Math.min(evt.pageX, 888), "zIndex":1024});
						} else if (ccd_change || (ccd_change == 0)) {
							$("#inline_plan_selecter > select")
								.next().remove()
								.end()
								.html("<option value='0'>不更换 CCD 盖玻璃</option><option value='9'>更换 CCD 盖玻璃</option>")
								.val(ccd_change)
								.change(changeCcdChange)
								.select2Buttons();
							$("#inline_plan_selecter").show()
								.css({"top" : evt.pageY, "left" : Math.min(evt.pageX, 888), "zIndex":1024});
						} else {
							var pat_id = $td.attr("pat_id");
							$("#inline_plan_selecter > select")
								.next().remove()
								.end()
								.html($("#pats").html())
								.val(pat_id)
								.change(changePat)
								.select2Buttons();
							$("#inline_plan_selecter").show()
								.css({"top" : evt.pageY, "left" : Math.min(evt.pageX, 888), "zIndex":1024});
						}

						evt.preventDefault();
 						evt.stopPropagation();
					});

					$inline_plan
						.find("table tbody").html($tbodyHtml)
						.end()
						.dialog({
							title : "投线计划单",
							width : 800,
							show: "blind",
							resizable : false,
							modal : true,
							minHeight : 200,
							close : function(){
								$("#inline_plan_selecter").hide().css({"zIndex":1})
									.children("select").unbind(changeSection).unbind(changePat);
							},
							buttons : {
								"投线": function(){
									var partialOrderLost = "";
									$inline_plan.find("tbody tr").filter(".ui-state-highlight").not(".inline").each(function(idx, ele){
										if ($(ele).find("td[partial_order='0']").length > 0) {
											partialOrderLost += "维修对象 " + $(ele).find("td:eq(0)").text()
												+ " 零件未订购。<br>";
										}
										if ($(ele).find("td[partial_order='1']").length > 0) {
											partialOrderLost += "维修对象 " + $(ele).find("td:eq(0)").text()
												+ " 零件未齐备。<br>";
										}
										if ($(ele).find("td[img_check_need]").length > 0) {
											partialOrderLost += "维修对象 " + $(ele).find("td:eq(0)").text()
												+ " 在 WIP 库位中等待超过 2个月，且未进行图像检查。<br>";
										}
									});
									if (partialOrderLost) {
										warningConfirm(partialOrderLost + "确定将跳过这些维修对象的投线。", doBatchInline);
									} else {
										doBatchInline();
									}
								}, 
								"打印": function(){
									exportReportInlinePlan();
								},
								"关闭": function(){
									$inline_plan.dialog("close");
								}}
						});
				} else {
					errorPop("没有投线计划。");
				}
			}
		}
	});
}

var changeSection = function() {
	var $td = $("#inline_plan").find(".changing");
	var val = this.value;
	if(val) {
		var content = $(this).find("option[value=" + val + "]").text();
		$td.attr("section_id", val);
		$td.find("span:eq(0)").text(content);
		$("#inline_plan_selecter").hide().css({"zIndex":1})
			.children("select").unbind("change", changeSection);
	}
	var data = {material_id : $td.parent().attr("material_id"),
	section_id : val};
	doChangeinlinePlan(data);
}
var changePat = function() {
	var $td = $("#inline_plan").find(".changing");
	var val = this.value;
	if(val) {
		var content = $(this).find("option[value=" + val + "]").text();
		$td.attr("pat_id", val);
		$td.find("span:eq(0)").text(content);
		$("#inline_plan_selecter").hide().css({"zIndex":1})
			.children("select").unbind("change", changePat);
	}
	var data = {material_id : $td.parent().attr("material_id"),
	pat_id : val};
	doChangeinlinePlan(data);
}
var changePartialOrder = function() {
	var $td = $("#inline_plan").find(".changing");
	var val = this.value;
	if(val) {
		var content = $(this).find("option[value=" + val + "]").text();
		$td.attr("partial_order", val);
		$td.find("span:eq(0)").text(content);
		$("#inline_plan_selecter").hide().css({"zIndex":1})
			.children("select").unbind("change", changePartialOrder);
	}
}
var changeCcdChange = function() {
	var $td = $("#inline_plan").find(".changing");
	var val = this.value;
	if(val) {
		var content = $(this).find("option[value=" + val + "]").text();
		$td.attr("ccd_change", val);
		$td.find("span:eq(0)").text(content);
		$("#inline_plan_selecter").hide().css({"zIndex":1})
			.children("select").unbind("change", changeCcdChange);
	}
	var data = {material_id : $td.parent().attr("material_id"),
	ccd_change : val};
	doChangeinlinePlan(data);
}
var doChangeinlinePlan = function(data) {
	$.ajax({
		data : data,
		async: true, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		url: servicePath + "?method=doChangeinlinePlan",
		type : "post"
	});
}
var doBatchInline = function() {
	var material_ids = $("#inline_plan tbody tr").filter(".ui-state-highlight").not(".inline").map(function() {
		var $tr = $(this);
		var material_id = $tr.attr("material_id");
		if ($tr.find("[partial_order='0']").length > 0 || $tr.find("td[img_check_need]").length > 0) {
			return null;
		} else {
			return material_id;
		}
	}).get().join(",");

	if (material_ids) {
		$.ajax({
			data : {ids : material_ids},
			async: true, 
			beforeSend: ajaxRequestType, 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			url: servicePath + "?method=doBatchInline",
			type : "post",
			complete : function() {
				$("#inline_plan").dialog('close');
				findit(keepSearchData);
				findexeit();
			}
		});
	}
}

function exportReportInlinePlan() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=reportInlinePlan',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
			if (resInfo && resInfo.fileName) {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			} else {
				errorPop("文件导出失败！");
			}
		}
	});
}

var getBoLocationMap = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(rowid);

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
			getBoLocationMap_handleComplete(xhrobj, rowData);
		}
	});
}

var getBoLocationMap_handleComplete = function(xhrobj, rowData) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		
		return;
	}

	var $wip_pop = $("#wip_pop");
	$wip_pop.hide();
	$wip_pop.html(resInfo.storageHtml)
		.attr({material_id : rowData.material_id});

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
					material_id : rowData["material_id"],
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
						findit();
						$wip_pop.dialog("close");
					}
				});
			}
		}
	});

	$wip_pop.show();
}

var warehousing=function() {

	var $list = $("#listareas table.ui-jqgrid-btable:visible") ;

	var rowid = $list.jqGrid("getGridParam","selrow");
	var rowdata = $list.getRowData(rowid);

	var data = {};

	data["material.material_id[0]"] = rowdata["material_id"];
	data["material.break_back_flg[0]"] = "0";
	data["material.fix_type[0]"] = rowdata["fix_type"];

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'wip.do?method=dowarehousing',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			findit(keepSearchData);
		}
	});	
}

var inline_start=function(rowdata) {

	warningConfirm("此操作通常可以通过SAP投线动作同步。\n通常不需要在此画面操作，是否继续操作？",
		function(){
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doInlineStart',
				cache : false,
				data : {
					material_id : rowdata.material_id
				},
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(){
					findit(keepSearchData);
				}
			});	
		}
	);
}