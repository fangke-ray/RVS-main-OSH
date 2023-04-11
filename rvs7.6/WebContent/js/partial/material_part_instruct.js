/** 服务器处理路径 */
var servicePath = "materialPartInstruct.do";

var partialCodeAc = null;

var levelMap = null;

$(function() {

/** 一览数据对象 */
var listdata = {};

levelMap = selecOptions2Object($("#goMaterial_level").val());

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	$("#report_button").disable();
	$("#additional_order_button").disable();
	if (rowid != null) {
		$("#additional_order_button").enable();
		var rowdata = $("#list").getRowData(rowid);
		var procedure = rowdata["procedure"];
		if (procedure == "1") {
			$("#additional_order_button").disable();
		} else if (procedure == "2") {
			if (rowdata["inline_adjust"] != "有") {
				$("#additional_order_button").disable();
			}
		} else if (procedure == "4") {
			$("#report_button").enable();
		}
	}
};

function initGrid() {
	$("#list").jqGrid({
		toppager : true,
		data : [],
		height : 461,
		width : 992,
		rowheight : 23,
		datatype : "local",

		colNames : ['维修对象ID','修理单号','', '型号', '机身号', '等级', '同意日', '进展', '投线后<br>追加', '零件订购日', '不良追加'],
		colModel : [{
					name: 'material_id',
					index: 'material_id',
					hidden : true, key:true
				}, {
					name : 'omr_notifi_no',
					index : 'omr_notifi_no',
					width : 40
				}, {
					name : 'model_id',
					index : 'model_id',
					hidden : true
				}, {
					name : 'model_name',
					index : 'model_name',
					width : 115
				}, {
					name : 'serial_no',
					index : 'serial_no',
					width : 40
				}, {
					name : 'level',
					index : 'level',
					width : 25,
					align : 'center',
					formatter:'select',
					editoptions:{value:$("#goMaterial_level").val()}
				}, {
					name : 'agreed_date',
					index : 'agreed_date',
					width : 45,
					align : 'center',
					formatter:'date', 
					formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
				}, {
					name : 'procedure',
					index : 'procedure',
					width : 25,
					align : 'center',
					formatter:'select',
					editoptions:{value:$("#goProcedure").val()}
				}, {
					name : 'inline_adjust',
					index : 'inline_adjust',
					width : 25,
					align : 'center',
					formatter : function(value) {
						if (!value) return "";
						return (value == "false" ? "无" : "有");
					}
				}, {
					name : 'order_date',
					index : 'order_date',
					width : 45,
					align : 'center',
					formatter:'date', 
					formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
				}, {
					name : 'nongood_append',
					index : 'nongood_append',
					width : 25,
					align : 'center'
				}
		],
		rowNum : 50,
		rownumbers : true,
		toppager : false,
		pager : "#listpager",
		viewrecords : true,
		caption : "工作指示单一览",
		hidegrid : false,
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		ondblClickRow : function(rid, iRow, iCol, e) {
			var data = $("#list").getRowData(rid);
			showInstructSheet(data);
		},
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true],
		onSelectRow : enablebuttons,
		gridComplete : enablebuttons
	});
}

function showInstructSheet(rowdata) {
	var material_id = rowdata["material_id"];
	var procedure = rowdata["procedure"];
	if (procedure == "0") {
		warningConfirm("此维修品尚未收到指示单，是否要展示型号：" + rowdata["model_name"] + "的指示单。",
			function(){show_instruct_funcs.instructLoad(rowdata);}
		);
	} else {
		show_instruct_funcs.instructLoadMaterial(rowdata);
	}
}

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {

	var resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
	} else {
		if (resInfo.list) {
			loadData(resInfo.list);
		}
	}
};

function loadData(listdata){
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam', {data : listdata})
		.trigger("reloadGrid", [{
			current : false
		}]);
}

var keepSearchData;
var findit = function(data) {
	if (!data) { //新查询
		keepSearchData = {
			"omr_notifi_no" : $("#search_sorc_no").val(),
			"serial_no" : $("#search_serial_no").val(),
			"model_id" : $("#search_model_id").val(),
			"level" : $("#search_level").val(),
			"range": $("#search_range").val(),
			"section_id": $("#search_section").val(),
			"nongood_append":$("#search_order_step").val()
		};
		if ($("#search_procedure").val()) {
			keepSearchData["procedure"] = $("#search_procedure").val().join();
		} else {
			keepSearchData["procedure"] = "";
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

function exportReport(url,method) {
	var postData = {};

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: url, 
		cache: false, 
		data: postData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrobj, textStatus){
			var resInfo = $.parseJSON(xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do?method=output&from=cache&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do?method=output&from=cache&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			}
		}
	});
}

var partial_focus_funcs = {
	$this_dialog : null,
	loadPersonalFocus : function(){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=loadPersonalFocus',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : partial_focus_funcs.showPersonalFocus
		});
	},
	personalFocusList : [],
	showPersonalFocus : function(xhrobj){
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			var $dialog = partial_focus_funcs.$this_dialog = $("#p_focus_dialog");
			if ($dialog.length == 0) {
				$(document.body).append("<div id='p_focus_dialog' style='display:none;'></div>");
				$dialog = $("#p_focus_dialog");
			} else {
				$dialog.html("");
			}

			if (resInfo.partial_list) {
				$dialog.append("<div><li class='ui-state-default' style='display: block;'>寻找零件</li><input type='text' id='p_focus_code'></input><input type='hidden' id='p_focus_id'></input><input type='button' id='p_focus_code_add' style='padding: 0 1em;margin-left: .5em;' value='+'></input></div>");
				$dialog.append("<table id='p_focus_list'></table>");
			} else {
				return;
			}

			partial_focus_funcs.personalFocusList = resInfo.partial_list;

			$dialog.find("#p_focus_code").autocomplete({
				source : partialCodeAc,
				minLength :3,
				delay : 100,
				focus: function( event, ui ) {
					 $(this).val( ui.item.label );
					 return false;
				},
				select: function( event, ui ) {
					$("#p_focus_id").val(ui.item.value);
					$(this).val( ui.item.label );
					 return false;
				}
			});
	
			$dialog.find("#p_focus_code_add").button()
				.click(partial_focus_funcs.addPartial);

			$("#p_focus_list").jqGrid({
				toppager : true,
				data : partial_focus_funcs.personalFocusList,
				height : 415,
				width : 756,
				rowheight : 23,
				datatype : "local",
				colNames : ['零件ID', '零件代码', '说明', '参考单价', '主要定位', '取消关注'],
				colModel : [{
						name: 'partial_id',
						index: 'partial_id',
						hidden : true
					}, {
						name : 'code',
						index : 'code',
						width : 40
					}, {
						name : 'name',
						index : 'name',
						width : 115
					}, {name:'price',index:'price', width:30,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}
					}, {name:'process_code',index:'process_code',width : 50,align:'center',formatter : function(value, options, rData){
							if(!value || value=='0'){
								return '' ;
							}else{
								if (value.length > 20) {
									return value.substring(0, 19) + "等"; 
								}
								return value;
							}							
						}
					}, {
						name : 'unfocus',
						index : 'unfocus',
						width : 20,
						align : 'center',
						formatter : function(value, options, rData){
							return '<input type="button" value="-">';
						}
					}
				],
				rowNum : 1024,
				rownumbers : true,
				toppager : false,
				viewrecords : true,
				caption : "关注追加零件一览",
				hidegrid : false,
				gridview : true, // Speed up
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true]
			});

			$("#p_focus_list").on("click", "input:button", function(){
				var partial_id = $(this).closest("tr").children("td[aria\\-describedby='p_focus_list_partial_id']").text();
				if(partial_id) {
					griddata_remove(partial_focus_funcs.personalFocusList, "partial_id", partial_id, false);
					$("#p_focus_list").jqGrid().clearGridData();
					$("#p_focus_list").jqGrid('setGridParam', {data : partial_focus_funcs.personalFocusList}).trigger("reloadGrid", [{current : false}]);
				}
			} );

			$dialog.dialog({
			    resizable : false,
				modal : true,
				width: 800,
				height: 660,
				title : "个人关注追加订购零件列表",
				buttons : {
					"保存" : function() {
						partial_focus_funcs.setPersonalFocusList();
					},
					"关闭" : function() {
						$dialog.dialog("close");
					}
				}
			});
		}
	},
	addPartial : function() {
		var partial_id = $("#p_focus_id").val();
		if (!partial_id) {
			errorPop("未选择有效的零件。");
			return;
		}
		partial_id = fillZero(partial_id, 11);
		for (var iFl in partial_focus_funcs.personalFocusList) {
			if (partial_focus_funcs.personalFocusList[iFl].partial_id == partial_id) {
				errorPop("选择的零件已在列表中。");
				return;
			}
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=loadPartialForFocus',
			cache : false,
			data : {"partial_id" : partial_id},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : partial_focus_funcs.addPartialToList
		});
	},
	addPartialToList : function(xhrobj) {
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#p_focus_id").val("");
			$("#p_focus_code").val("");

			partial_focus_funcs.personalFocusList.push(resInfo.partial);
			$("#p_focus_list").jqGrid().clearGridData();
			$("#p_focus_list").jqGrid('setGridParam', {data : partial_focus_funcs.personalFocusList}).trigger("reloadGrid", [{current : false}]);
		}
	},
	setPersonalFocusList : function() {
		var postData = {};
		for (var iFl in partial_focus_funcs.personalFocusList) {
			postData["partial_id[" + iFl + "]"] = partial_focus_funcs.personalFocusList[iFl].partial_id;
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doSetFocusPartialList',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(){
				partial_focus_funcs.$this_dialog.dialog("close");
			}
		});
	}
}

var show_instruct_funcs = {
	instructLoad : function(rowdata) {
		var postData = {
			"model_id": rowdata.model_id,
			"level": rowdata.level
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'partial_position.do?method=instructLoad',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				show_instruct_funcs.instructShow(xhrObj, rowdata.model_name);
			}
		});
	},
	instructShow : function(xhrObj, model_name, material) {
		var resInfo = $.parseJSON(xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
			return;
		}
		var $instructDialog = $("#instruct_dialog");
		if ($instructDialog.length == 0) {
			$(document.body).append("<div id='instruct_dialog'></div>");
			$instructDialog = $("#instruct_dialog");
		}
		if (!(resInfo.instructLists && resInfo.instructLists['@'])) {
			errorPop("此型号尚未导入工作指示单。");
			return;
		}
		if (resInfo.highprice) {
			material.highprice = resInfo.highprice;
		}
		if (resInfo.callReasons && material) {
			setTimeout(function(){
				show_instruct_funcs.reasonLoadById(material.material_id)
			}, 107);
		}
		if (typeof(instruction_show) === "function") {
			instruction_show($instructDialog, model_name, resInfo.instructLists, resInfo.components, material, 
				resInfo.instuctForMaterial, resInfo.rankBom, resInfo.partialBom, resInfo.edittype);
		}
	},
	instructLoadMaterial : function(rowdata) {
		var postData = {
			"material_id": rowdata.material_id,
			"model_id": rowdata.model_id,
			"level": rowdata.level,
			"procedure" : rowdata.procedure
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=instructLoad',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				postData["omr_notifi_no"] = rowdata.omr_notifi_no;
				show_instruct_funcs.instructShow(xhrObj, rowdata.model_name, postData);
			}
		});
	},
	reasonLoadById : function(material_id) {
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=reasonsLoadById',
			cache : false,
			data : {"material_id" : material_id},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.errors && resInfo.errors.length) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
					return;
				}
	
				instr_reasons.history_reasons = resInfo.reasons;
			}
		});
	}
}

var show_confirm_funcs = {
	additionalOrder : function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		var postData = {
			"material_id": rowid
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=additionalOrderLoad',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				var rowdata = $("#list").getRowData(rowid);
				show_confirm_funcs.additionalOrderShow(xhrObj, rowdata);
			}
		});
	},
	additionalOrderShow : function(xhrObj, rowdata){
		var resInfo = $.parseJSON(xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
			return;
		}

		var $dialog = $("#instruct_confirm_dialog");

		$dialog.find("#confirm_sorc_no").text(rowdata.omr_notifi_no);
		$dialog.find("#confirm_model_name").text(rowdata.model_name);
		$dialog.find("#confirm_serial_no").text(rowdata.serial_no);
		$dialog.find("#confirm_level").text(levelMap[rowdata.level] || "-");
		$dialog.find("textarea").val(resInfo.quoteAdditions || "");

		var highprice = resInfo.highprice;
		if (highprice) {
			highprice = parseFloat(highprice);
		}

		var focus_set = {};
		if (resInfo.focus_list) {
			for (var ix in resInfo.focus_list) {
				focus_set[resInfo.focus_list[ix].code] = "1";
			}
		}

		var tbodyText = "";
		var allLength = 0, nsLength = 0;
		if (resInfo.inlineAdditions) {
			allLength = resInfo.inlineAdditions.length;
			var lossCalc = 0;
			for (var idx in resInfo.inlineAdditions) {
				var inlineAddition = resInfo.inlineAdditions[idx];
				var trAttr = "";
				if (inlineAddition.order_flg == 2) {
					trAttr += " consumable";
					if (inlineAddition.quantity > 0) nsLength++;
				} else if (inlineAddition.line_id == "00000000013") {
					// nsLength++;
				}
				if (inlineAddition.quantity < 0) {
					trAttr += " cancel";
				} else if (inlineAddition.price) {
					lossCalc += parseFloat(inlineAddition.price) * parseInt(inlineAddition.quantity); 
				}
				tbodyText += "<tr" + trAttr + ">";
				var focus = "";
				if (focus_set[inlineAddition.code]) {
					focus = " class='focus'";
				}
				tbodyText += "<td" + focus + ">" + inlineAddition.code + "</td>";
				tbodyText += "<td>" + inlineAddition.name + "</td>";
				tbodyText += "<td align='r'>" + inlineAddition.quantity + "</td>";

				if (!inlineAddition.price) {
					tbodyText += "<td align='r'>unknown</td>";
				} else {
					var noteHigh = "";
					if (highprice && highprice <= parseFloat(inlineAddition.price)) {
						noteHigh = " class='noticeprice'";
					}
					tbodyText += "<td align='r'" + noteHigh + ">" + inlineAddition.price + "</td>";
				}
				tbodyText += "<td align='c'><img src='/images/sign/" + inlineAddition.inline_job_no + "'></td>";
				tbodyText += "<td align='c' line_id='" + inlineAddition.line_id + "'>" + (inlineAddition.process_code || "-") + "</td>";
				tbodyText += "<td>" + inlineAddition.inline_comment + "</td>";

				tbodyText += "</tr>";
			}
			if (lossCalc) {
				$dialog.find("#confirm_loss_detail").text(lossCalc.toFixed(2));
			} else {
				$dialog.find("#confirm_loss_detail").text("无损金");
			}
		}

		var part_procedure = resInfo.part_procedure;
		var d_job_no = null, n_job_no = null;
		if (part_procedure && part_procedure.operator_id && part_procedure.operator_id.indexOf("|") > 0) {
			var jobNoArr = (part_procedure.operator_id || "-|-").split("|");
			if (jobNoArr[0] && jobNoArr[0] != "-") {
				d_job_no = jobNoArr[0]; 
			}
			if (jobNoArr[1] && jobNoArr[1] != "-") {
				n_job_no = jobNoArr[1];
			}
		}

		var $tbody = $("#instruct_confirm_inline_addition > tbody");
		$tbody.html(tbodyText);
		if (allLength) {
			if (nsLength) {
				$tbody.find("tr:eq(0)").append("<td confirm='00000000013' rowspan='" + nsLength + "'>" 
					+ (n_job_no ? "<img src='/images/sign/" + n_job_no + "'>" : "") + "</td>")
			}
			$tbody.find("tr:eq(" + nsLength + ")").append("<td confirm='00000000012' rowspan='" + (allLength - nsLength) + "'>" 
					+ (d_job_no ? "<img src='/images/sign/" + d_job_no + "'>" : "") + "</td>")
		}

		var dialogButtons = {
			"确认" : function() {
				var postData = {
					"material_id": rowdata.material_id
				};
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doApplyConfirm',
					cache : false,
					data : postData,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrObjApply){
						var resInfo = $.parseJSON(xhrObjApply.responseText);
						if (resInfo.updated == "false") {
							infoPop("已经无须确认。");
						}
						$dialog.dialog("close");
						findit();
					}
				});
			},
			"关闭" : function() {
				$dialog.dialog("close");
			}
		}
		if (part_procedure) {
			if (part_procedure.confirm == 0 
				|| (part_procedure.confirm == 2 && d_job_no != null)
				|| (part_procedure.confirm == 3 && d_job_no != null && n_job_no != null)
				) {
				dialogButtons = {"关闭" : function() {
					$dialog.dialog("close");
				}};
			}
		}

		$dialog.dialog({
		    resizable : false,
			modal : true,
			title: '零件追加单确认',
			width: 745,
			height: 540,
			buttons : dialogButtons
		});
		
	}
}

function initView(){
	$("input.ui-button").button();

	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		findit();
	});

	$("#resetbutton").click(function() {
		$("#search_sorc_no").val("");
		$("#search_serial_no").val("");
		$("#search_model_id").val("");
		$("#txt_modelname").val("");
		$("#search_level").val("").trigger("change");
		$("#search_procedure").val(($("#defaultCondProcedure").val() || "").split(",")).trigger("change");
		$("#search_section").val("").trigger("change");
		$("#search_range").val("1").trigger("change");
		$("#search_order_step").val("").trigger("change");
		$("#occur_times_set input:eq(0)").attr("checked","checked").trigger("change");
	});
	$("#search_range").val("1").trigger("change");

	// autoComplete(零件code) 
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'partial.do?method=getAutocomplete',
		cache : true,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			partialCodeAc = resInfo.sPartialCode;
		}
	});		

	$("#searcharea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	
	$("#search_procedure").children("option:eq(1)").remove();
	$("#search_level, #search_range, #search_procedure, #search_section, #search_order_step").select2Buttons();
	setReferChooser($("#search_model_id"));

	$("#resetbutton").trigger("click");

	$("#editbobutton").click(function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		if (rowid == null) return;
		var data = $("#list").getRowData(rowid);
		var material_id = data["material_id"];
		showInstructSheet(material_id);
	});

	$("#reportbutton").click(function(){
		exportReport(servicePath + '?method=reportPartialOrder', "exportOrder");
		});

	$("#focus_button").click(partial_focus_funcs.loadPersonalFocus);

	$("#search_range").change(function(){
		if (this.val == "2") {
			$("#s2b_search_procedure").hide();
		} else {
			$("#s2b_search_procedure").show();
		}
	})

	$("#additional_order_button").click(show_confirm_funcs.additionalOrder);
	$("#report_button").click(function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		if (!rowid) return;

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=makeReport',
			cache : false,
			data : {"material_id": rowid},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus){
				var resInfo = $.parseJSON(xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var rowdata = $("#list").getRowData(rowid);
					var dUrl = "download.do"+"?method=output&fileName=" + (rowdata.omr_notifi_no || "") + "_追加零件订购清单.xlsx&filePath=" + resInfo.filePath;
 
					if ($("iframe").length > 0) {
						$("iframe").attr("src", dUrl);
					} else {
						var iframe = document.createElement("iframe");
			            iframe.src = dUrl;
			            iframe.style.display = "none";
			            document.body.appendChild(iframe);
					}
				}
			}
		});
	});

	initGrid();

	findit();
}

initView();

});
