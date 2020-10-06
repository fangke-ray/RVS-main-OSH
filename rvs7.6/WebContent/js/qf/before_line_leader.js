var listdata = {};
var servicePath = "beforeLineLeader.do";
/** 医院autocomplete **/
var customers = {};
var opt_bound_out_ocm = {};

var line_expedite = function() {

	var selectedId = $("#performance_list").getGridParam("selrow");
	var rowData = $("#performance_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doexpedite',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				// 紧急区分
				var expedited = rowData["expedited"];

				$("#expeditebutton").disable();
			
				findit();
			} catch (e) {
				console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
};

var treat_nogood = function() {
	$("#pop_treat").hide();
	// 导入不良处置画面
	$("#pop_treat").load("widget.do?method=nogoodedit", function(responseText, textStatus, XMLHttpRequest) {
		var selectedId = $("#performance_list").getGridParam("selrow");
		var rowData = $("#performance_list").getRowData(selectedId);
		var data = {
			material_id : rowData["material_id"],
			position_id : rowData["position_id"]
		};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=getwarning',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					$("#nogood_id").val(resInfo.warning.id);
					$("#nogood_occur_time").text(resInfo.warning.occur_time);
					$("#nogood_sorc_no").text(resInfo.warning.sorc_no);
					$("#nogood_model_name").text(resInfo.warning.model_name);
					$("#nogood_serial_no").text(resInfo.warning.serial_no);
					$("#nogood_line_name").text(resInfo.warning.line_name);
					$("#nogood_process_code").text(resInfo.warning.process_code);
					$("#nogood_reason").text(resInfo.warning.reason);
					$("#nogood_comment_other").text(resInfo.warning.comment);

					$("#nogoodfixbtn").hide();
					$("#nogoodwaitbtn").hide();

					$("#pop_treat").dialog({
						// position : [ 800, 20 ],
						title : "不良信息及处置",
						width : 468,
						show : "blind",
						height : 'auto', //450,
						resizable : false,
						modal : true,
						minHeight : 200,
						close : function() {
							if ($("#nogoodfixbtn").attr("checked") === "checked") {
								selectedMaterial.sorc_no = resInfo.warning.sorc_no;
								selectedMaterial.model_name = resInfo.warning.model_name;
								selectedMaterial.serial_no = resInfo.warning.serial_no;
								selectedMaterial.material_id = rowData["material_id"];
								selectedMaterial.position_id = rowData["position_id"];
								selectedMaterial.alarm_messsage_id = $("#nogood_id").val();
								selectedMaterial.append_parts = ($("#append_parts_y").attr("checked") ? 1 : 0);
								selectedMaterial.comment = $("#nogood_comment").val();
							}
							$("#pop_treat").html("");
						},
						buttons : {}
					});
					$("#pop_treat").show();
				} catch (e) {
					console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	});
};

var jsinit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			receivePos = resInfo.receivePos;
			// autocomplete
			if (resInfo.customers) customers = resInfo.customers;
			if (resInfo.opt_bound_out_ocm) opt_bound_out_ocm = resInfo.opt_bound_out_ocm;

			listdata = resInfo.performance;

			if ($("#gbox_performance_list").length > 0) {
				$("#performance_list").jqGrid().clearGridData();
				$("#performance_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#performance_list").jqGrid({
					data : listdata,
					height : 507,
					width : 1246,
					rowheight : 23,
					datatype : "local",
					colNames : ['修理单号', '型号', '机身号', '条码参照', '不良', '等级', '处理对策', '受理日期', '报价日期', '客户同意', '优先报价', '位置', '状态', '备注',
						'position', 'esas_no', 'direct_flg', 'fix_type', 'service_repair_flg', 'model_id', 'selectable', 'anml_exp', 'ts', '', '', '', '' ,'','','vip','scheduled_expedited', '直送区域'],
					colModel : [{
								name : 'sorc_no',
								index : 'sorc_no',
								width : 90
							},{
								name : 'model_name',
								index : 'model_name',
								width : 125
							},{
								name : 'serial_no',
								index : 'serial_no',
								width : 50
							}, {
								name : 'material_id', index : 'material_id', width : 70, key: true
							}, {
								name : 'symbol',
								index : 'symbol',
								width : 50,
								align : 'center'
							},{
								name : 'level',
								index : 'level',
								formatter: "select", editoptions:{value:resInfo.opt_level},
								width : 35,
								align : 'center'
							}, 
							{
								name : 'storager',
								index : 'storager',
								width : 50,
								align : 'left'
							}, {
								name : 'reception_time',
								index : 'reception_time',
								width : 50,
								align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
							}, {
								name : 'quotation_time',
								index : 'quotation_time',
								width : 50,
								align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
							}, {
								name : 'agreed_date',
								index : 'agreed_date',
								width : 50,
								align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
							}, {
								name : 'quotation_first',
								index : 'quotation_first',
								width : 50, align:'center', formatter: 'select', editoptions:{value: "0:;1:优先"}
							}, {
								name : 'processing_position',
								index : 'processing_position',
								width : 50
							}, {
								name : 'operate_result',
								index : 'operate_result',
								formatter: "select", editoptions:{value:resInfo.opt_operate_result},
								width : 50
							}, {name:'comment',index:'comment', width:105, formatter:function(value, options, rData){
								var comment = " ";
								if (rData["direct_flg"] == "1") {
									comment += " 直送";
								}
								if (rData["service_repair_flg"] == "1") {
									comment += " 保内返修";
								} else if (rData["service_repair_flg"] == "2") {
									comment += " QIS";
								} else if (rData["service_repair_flg"] == "3") {
									comment += " 备品";
								}
								if (rData["fix_type"] == "1") {
									comment += " 流水线";
								} else if (rData["fix_type"] == "2") {
									comment += " 单元";
								}
								return comment.trim();
							}}, {
								name : 'position_id',
								index : 'position_id',
								formatter : function(value, options, rData){
									return rData['category_id'];
				   				},
								hidden : true
							}, {
								name : 'esas_no',
								index : 'esas_no',
								hidden : true
							}, {
								name : 'direct_flg',
								index : 'direct_flg',
								hidden : true
							}, {
								name : 'fix_type',
								index : 'fix_type',
								hidden : true
							}, {
								name : 'service_repair_flg',
								index : 'service_repair_flg',
								hidden : true
							}, {
								name : 'model_id',
								index : 'model_id',
								hidden : true
							}, {
								name : 'selectable',
								index : 'selectable',
								hidden : true
							},
							{name:'anml_exp',index:'anml_exp', hidden:true},
							{name:'ticket_flg',index:'ticket_flg', hidden:true},
							{name:'ocm',index:'ocm', hidden:true},
							{name:'ocm_rank',index:'ocm_rank', hidden:true},
							{name:'customer_name',index:'customer_name', hidden:true},
							{name:'ocm_deliver_date',index:'ocm_deliver_date', hidden:true},
							{name:'material_id',index:'material_id', hidden:true},
							{name:'isHistory',index:'isHistory', hidden:true},
							{name:'vip',index:'vip', hidden:true},
							{name:'scheduled_expedited',index:'scheduled_expedited', hidden:true},
							{
								name : 'bound_out_ocm',
								index : 'bound_out_ocm',
								formatter: "select", editoptions:{value:opt_bound_out_ocm},
								width : 35,
								align : 'center'
							}
						],
					rowNum : 22,
					rownumbers : true,
					toppager : false,
					viewrecords : true,
					caption : "",
					gridview : true, // Speed up
					pager:  "#performance_listpager",
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					ondblClickRow : function(rid, iRow, iCol, e) {
						if ($("#isEditor").val() == "true") {
							showDetail(rid);
						}
						// TODO else
					},
					onSelectRow : function(id) {
						var rowdata = $(this).jqGrid('getRowData', id);

						// 紧急区分
						var expedited = rowdata["quotation_first"];
						if (expedited == "10" || expedited == "11") {
							$("#expeditebutton").disable();
						} else if (expedited == "1") {
							//$("#expeditebutton").val("取消加急");
							$("#expeditebutton").disable();
						} else {
							$("#expeditebutton").val("优先报价");
							$("#expeditebutton").enable();
						}

						// 位置
						var processing_position = rowdata["processing_position"];

						if(processing_position.indexOf("WIP") == 0 || operate_result == 2){
							$("#expeditebutton").disable();
						}

						// 不良
						var symbol = rowdata["symbol"];
						if (symbol != "") {
							$("#nogoodbutton").enable();
						} else {
							$("#nogoodbutton").disable();
						}

						var inQuote = processing_position.indexOf("报价");
						var inWip = processing_position.indexOf("WIP");
						if (inWip >= 0) {
							$("#movebutton").enable();
							$("#movebutton").val("移动库位");
						} else {
							// 同意日
							var agreed_date = rowdata["agreed_date"];
							var operate_result = rowdata["operate_result"];
							if (operate_result != "2" || inQuote < 0) {
								$("#movebutton").disable();
							} else if (agreed_date.trim() == "") {
								$("#movebutton").enable();
								$("#movebutton").val("入库");
							} else {
								$("#movebutton").enable();
								$("#movebutton").val("再入库");
							}
						}

						$("#sendbutton").enable();
						// 判定
						if (inWip >= 0 || inQuote >= 0) {
							$("#sendqabutton, #sendccdbutton").enable();
						} else {
							$("#sendqabutton, #sendccdbutton").disable();
						}

						$("#resystembutton").enable();
						$("#stopbutton").enable();
						$("#printbutton").enable().val("重新打印小票" + $("#performance_list tr#" + id).find("td[aria\-describedby='performance_list_ticket_flg']").text()+"份");
						$("#printaddbutton").enable();
						$("#quotationcommentbutton").enable();
                        
                        if(rowdata.isHistory==1){
                            $("#modifybutton").enable();
                            $("#downloadbutton").enable();
                        }else{
                            $("#modifybutton").disable();
                            $("#downloadbutton").disable();
                        }
					},
					viewsortcols : [true, 'vertical', true],
					gridComplete : function() {
						disableButtons();
					}
				});
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

function disableButtons(){
	$("#expeditebutton,#nogoodbutton,#resystembutton,#stopbutton,#printbutton,#printaddbutton,#movebutton,#sendbutton,#sendqabutton,#quotationcommentbutton").disable();
};

$(document).ready(function() {
	$("div.ui-button").button();
	$("input.ui-button").button();
	disableButtons();

	$("#expeditebutton").click(line_expedite);
	$("#nogoodbutton").click(treat_nogood);
	$("#resystembutton").click(doResystem);
	$("#stopbutton").click(function(){
		afObj.applyProcess(242, this, doStop, arguments);
	});
	$("#printbutton").click(printTicket);
	$("#printaddbutton").click(function(){printTicket(1)});
	$("#movebutton").click(doMove);
	$("#sendqabutton").click(doJudge);
	$("#sendccdbutton").click(doCcdChange);
	$("#quotationcommentbutton").click(doQuotationCommentChange);
	$("#sendbutton").hover(
		function(){$(this).find("div.ui-widget-content").show();},
		function(){$(this).find("div.ui-widget-content").hide();}
	);

	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");}, 
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#search_agreed_set").buttonset();
	$("#search_level").select2Buttons();
	setReferChooser($("#search_model_id"), $("#model_refer1"));

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#search_sorc_no").data("post", $("#search_sorc_no").val());
		$("#search_serialno").data("post", $("#search_serialno").val());

		if ($("#search_serialnos").html()) {
			$("#search_serialno").data("post", $("#search_serialnos span").map(function(){return this.innerText}).get().join(","));
		}

		$("#search_model_id").data("post", $("#search_model_id").val());
		$("#search_level").data("post", $("#search_level").val());
		$("#search_agreed_set").data("post", $("#search_agreed_set input:checked").val());
		$("#search_wip_location").data("post", $("#search_wip_location").val());
		// 查询
		findits();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#search_sorc_no").data("post", "");
		$("#search_serialno").data("post", "");
		$("#search_model_id").data("post", "");
		$("#search_level").data("post", "");
		$("#search_agreed_set").data("post", "");
		$("#search_wip_location").data("post", "");

		$("#search_sorc_no").val("");
		$("#search_serialno").val("");
		$("#search_serialnos").html("");
		$("#search_model_id").val("");
		$("#txt_modelname").val("");
		$("#search_level").val("").trigger("change");
		$("#search_agreed_set input").removeAttr("checked");
		$("#agreed_a").attr("checked", "checked").trigger("change");
		$("#search_wip_location").val("");
	});

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : jsinit_ajaxSuccess
	});
    
    
    $("#modifybutton").click(function(){
        var selectedId = $("#performance_list").getGridParam("selrow");
        var rowData = $("#performance_list").getRowData(selectedId);
        edit_quotistion(rowData.material_id);
    });
    
    $("#downloadbutton").click(function(){
        var selectedId = $("#performance_list").getGridParam("selrow");
        var rowData = $("#performance_list").getRowData(selectedId);
        var data={
            "material_id":rowData.material_id
        }
        
        $.ajax({
            beforeSend : ajaxRequestType,
            async : true,
            url :'quotaion_prospectus.do?method=report',
            cache : false,
            data : data,
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
    });

    /* return isFull */
    var multiSerialNo = function(serialNo) {
    	if (isNaN(serialNo)) {
    		return false;
    	}
		if ($("#search_serialnos span").length >= 10) {
			blink($("#search_serialnos"), "errorarea-blink", 6);
			return true;
		} else {
			$("#search_serialnos").append("<span>" + serialNo + "</span>");
			$("#search_serialno").val("");
			return false;
		}
    };
    $("#search_serialno").on("paste", function(evt){
    	var pastion = evt.originalEvent.clipboardData.getData("text"); 
    	console.log(pastion);
    	if (pastion) {
    		if (pastion.indexOf('\n') >= 0) {
    			this.value = "";
    			var snos = pastion.split('\n');
    			for (var isnos = 0; isnos < snos.length; isnos++) {
    				var full = multiSerialNo(snos[isnos]);
    				if(full) break;
    			}
    			return false;
    		}
    	}
    });
	$("#search_serialno_plus").click(function(){
		var serialNo = ($("#search_serialno").val() || "").trim();
		if (serialNo) {
			multiSerialNo(serialNo);
		}
	});
	$("#search_serialno_clear").click(function(){
		$("#search_serialno").val("");
		$("#search_serialnos").html("");
	});
	$("#search_serialnos").on("click", "span", function(){
		$(this).remove();
	})
});

function doQuotationCommentChange(){
	var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#performance_list").getRowData(rowid);
	
	var data = {
		material_id : rowdata.material_id,
		write : "1"
	};
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'material.do?method=getMaterialComment',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#comment_dialog").dialog({
					    resizable : false,
						modal : true,
						title : "报价备注",
						width : 1050,
						buttons : {
							"确认" : function() {
								var postData = {
									material_id : rowdata.material_id,
									comment : $("#edit_material_comment").val()
								};
								
								$.ajax({
									beforeSend : ajaxRequestType,
									async : false,
									url : 'material.do?method=doUpdateMaterialComment',
									cache : false,
									data : postData,
									type : "post",
									dataType : "json",
									success : ajaxSuccessCheck,
									error : ajaxError,
									complete : function(xhrObj) {
										var resInfo = $.parseJSON(xhrObj.responseText);
										if (resInfo.errors && resInfo.errors.length > 0) {
											treatBackMessages(null, resInfo.errors);
											return;
										} else {
											$("#comment_dialog").dialog('close');
										}
									}
								});
							},
							"取消" : function() {
								$(this).dialog("close");
							}
						}
					});
					
					if (resInfo.material_comment_other) {
						$("#edit_material_comment_other").val(resInfo.material_comment_other).show();
					}else{
						$("#edit_material_comment_other").val("");
					}
					$("#edit_material_comment").val(resInfo.material_comment);
				}
			} catch(e) {
				console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			}
		}
	});	
};

/** 
 * 检索处理
 */
var findits = function() {

	// 读取已记录检索条件提交给后台
	var data = {
		"sorc_no" : $("#search_sorc_no").data("post"),
		"serial_no" : $("#search_serialno").data("post"),
		"model_id" : $("#search_model_id").data("post"),
		"level" : $("#search_level").data("post"),
		"ticket_flg" : $("#search_agreed_set").data("post"),
		"wip_location" : $("#search_wip_location").data("post")
	}

	// Ajax提交
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

var doJudge = function() {

	var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#performance_list").getRowData(rowid);

	var data = {material_id : rowdata.material_id};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doJudge',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					infoPop("已经发送至品保判定，请确认。");
				}
			} catch(e) {
				
			}
		}
	});	
};

var doCcdChange = function() {
	var $this_dialog = $("#ccd_change");
	if ($this_dialog.length === 0) {
		$("body.outer").append("<div id='ccd_change'/>");
		$this_dialog = $("#ccd_change");
	}
	$this_dialog.hide();
	// 导入不良处置画面
	$this_dialog.load("widget.do?method=nogoodedit", function(responseText, textStatus, XMLHttpRequest) {
		var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
		var rowdata = $("#performance_list").getRowData(rowid);
		var data = {
			material_id : rowdata["material_id"]
		};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'material.do?method=doreccd',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					if (resInfo.errors.length == 0) {
						$this_dialog.dialog("close");
						infoPop("已经可以开始CCD盖玻璃更换作业！");
					} else {
						treatBackMessages(null, resInfo.errors);
					}
				} catch (e) {
					console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	});
};

var doMove = function() {
	var this_dialog = $("#wip_pop");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='wip_pop'/>");
		this_dialog = $("#wip_pop");
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'wip.do?method=getwipempty',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					this_dialog.hide();
					this_dialog.load("widgets/qf/wip_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
					//新增
					this_dialog.dialog({
						position : [ 800, 20 ],
						title : "WIP 入库选择",
						width : 1000,
						show: "blind",
						height : 640,// 'auto' ,
						resizable : false,
						modal : true,
						minHeight : 200,
						buttons : {}
					});

					this_dialog.find("td").addClass("wip-empty");
					for (var iheap in resInfo.heaps) {
						this_dialog.find("td[wipid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
					}
			
					//this_dialog.css("cursor", "pointer");
					this_dialog.find(".ui-widget-content").click(function(e){
						if ("TD" == e.target.tagName) {
							var $td = $(e.target);
							if (!$td.hasClass("wip-heaped")) {
								doChangeLocation($td.attr("wipid"), $td.is("[anml_exp]"));
							}
						}
					});

					this_dialog.show();

					var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
					var rowdata = $("#performance_list").getRowData(rowid);

					
					if (f_isPeripheralFix(rowdata.level)) {
					setTimeout(function(){this_dialog[0].scrollTop = 300}, 200);
					}

					});
				}
			} catch(e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
			}
		}
	});
}

var doChangeLocation = function(wip_location, isAnml_exp) {
	var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#performance_list").getRowData(rowid);

	if (rowdata.anml_exp == "1" && !isAnml_exp) {
		errorPop("请将动物实验用维修品放入专门库位。")
		return;
	} else if (rowdata.anml_exp != "1" && isAnml_exp) {
		errorPop("请不要将普通维修品放入动物实验用专门库位。")
		return;
	}
	$("#wip_pop").dialog("close");

	var data = {material_id : rowdata.material_id ,wip_location : wip_location};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'wip.do?method=doChangelocation',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : findit
	});
}


/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 读取一览
			listdata = resInfo.list;

			// jqGrid已构建的情况下,重载数据并刷新
			$("#performance_list").jqGrid().clearGridData();
			$("#performance_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var findit = function(xhrobj, textStatus) {
	try {
	$("#pop_treat").dialog('close');	
	} catch (e) {
		//TODO 
	}
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=refresh',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : jsinit_ajaxSuccess
	});
}

var doResystem=function() {

	var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#performance_list").getRowData(rowid);

	var linkna = "widgets/qf/acceptance-edit.jsp";

	$("#pop_treat").hide();
		// 导入编辑画面
	$("#pop_treat").load(linkna, function(responseText, textStatus, XMLHttpRequest) {
		 // 未修理返还
		$("#inp_modelname").show();
		$("#edit_modelname").show();
		$("#edit_label_modelname").hide();
		$("#edit_serialno").show();
		$("#edit_label_serialno").hide();
		$("#ins_material tr").hide();
		$("#ins_material tr:lt(3)").show();
		$("#ins_material tr:eq(8)").show();
		$("#ins_material tr:eq(9)").show();
		$("#edit_sorcno").val(rowdata["sorc_no"]);
		$("#edit_esasno").val(rowdata["esas_no"]);
		$("#edit_modelname").val(rowdata["model_id"]);
		$("#inp_modelname").val(rowdata["model_name"]);
		$("#direct").val(rowdata["direct_flg"]).trigger("change");
		$("#service_repair").val(rowdata["service_repair_flg"]).trigger("change");
		$("#edit_serialno").val(rowdata["serial_no"]);
		$("#selectable").val(rowdata["selectable"]).trigger("change");
		$("#edit_anml_exp").val(rowdata["anml_exp"]).trigger("change");

		$("#direct,#service_repair,#fix_type,#selectable,#edit_anml_exp,#edit_ocm,#edit_level,#edit_storager").select2Buttons();
		$("#referchooser_edit").html( $("#model_refer1").html());
		setReferChooser($("#edit_modelname"), $("#referchooser_edit"));
		$(".ui-button[value='清空']").button();
		$("#pop_treat").dialog({
			position : 'auto',
			title : "维修对象信息编辑",
			width : 'auto',
			show: "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"确定":function(){
					var data = {
						"material_id":rowdata["material_id"],
						"sorc_no":$("#edit_sorcno").val(),
						"esas_no":$("#edit_esasno").val(),
						"model_id":$("#edit_modelname").val(),
						"serial_no":$("#edit_serialno").val(),
						"direct_flg":$("#direct").val() == "" ? "0" : $("#direct").val(),
						"service_repair_flg":$("#service_repair").val()
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'wip.do?method=doresystem',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj) {
							var resInfo = null;
				
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
							
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									findit();
								}
							} catch(e) {
							}
						}
					});
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#pop_treat").show();
	});
};

var doStop=function() {

	var rowid = $("#performance_list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#performance_list").getRowData(rowid);

	var warningText = "未修理返还后，维修对象["+encodeText(rowdata.sorc_no)+"]将会退出RVS系统中的显示，";
	if (rowdata.processing_position.indexOf("WIP:") >= 0) {
		warningText += "自动从WIP出库，";
	}
	if (f_isPeripheralFix(rowdata.level)) {
		warningText += "在周边设备不修返还后";
	} else {
		warningText += "在图象检查后";
	}
	warningText += "直接出货，确认操作吗？";

	warningConfirm(warningText, 
	function() {
		afObj.applyProcess(242, this, doStopExecute, [rowdata]);
	}, null, "返还操作确认");
}

var doStopExecute=function(rowdata) {

	var data = {material_id : rowdata["material_id"]};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'wip.do?method=dostop',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			findit();
		}
	});	
}

var printTicket=function(addan) {

	var rowid = $("#performance_list").jqGrid("getGridParam","selrow");
	var rowdata = $("#performance_list").getRowData(rowid);

	var data = {
		material_id : rowdata["material_id"]
	}
	if (addan != 1) {
		data.quotator = 1;
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

var doInit = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : jsinit_ajaxSuccess
	});
}

var showDetail=function(rid) {
	var jthis = $("#pop_treat");
	jthis.hide();
	// 导入编辑画面
	jthis.load("widgets/qf/acceptance-edit.jsp", function(responseText, textStatus, XMLHttpRequest) {
		$("#inp_modelname").hide();
		$("#edit_modelname").hide();
		$("#edit_label_modelname").show();
		$("#edit_serialno").hide();
		$("#edit_label_serialno").show();
		$("#edit_ocm").parent().parent().hide();
		$("#edit_package_no").parent().parent().hide();
		$("#edit_storager").parent().parent().hide();

		// 读取修改行
		var rowData = $("#performance_list").getRowData(rid);

		$("#direct_rapid").button().click(function(){
			if (this.checked == true) {
				$(this).next().children("span").text("快速");
			} else {
				$(this).next().children("span").text("正常纳期");
			}
		}).next().hide();
		
		var level = rowData.level;

		var gridvalue = null;
		for (var igridlist = 0; igridlist < listdata.length; igridlist++) {
			gridvalue = listdata[igridlist];
			if (gridvalue["material_id"] == rid) {
				break;
			}
		}

//		if((level==9 || level==91 || level==92 || level==93) && rowData.fix_type==1){//修理等级D、D1、D2、D3 并且修理分类为流水线
		if (gridvalue.pat_id) {
			$("#major_pat").text(gridvalue.section_name).attr("value", gridvalue.pat_id); // pat.name as section_name
		}
		if (f_isLightFix(level) && rowData.fix_type==1) {
			$("#light_pat_button").closest("tr").show();
			$("#major_pat_button").closest("tr").hide();
		}else{
			$("#light_pat_button").closest("tr").hide();
			if (gridvalue.pat_id) {
				$("#major_pat_button").closest("tr").show();
			} else {
				$("#major_pat_button").closest("tr").hide();
			}
		}
		$("#light_pat_button, #major_pat_button").button();

		// 数据取得
		$("#material_id").val(rowData.material_id);
		$("#edit_sorcno").val(rowData.sorc_no);
		$("#edit_esasno").val(rowData.esas_no);
		$("#edit_modelname").val(rowData.model_id);
		$("#edit_label_modelname").text(rowData.model_name);
		$("#edit_serialno").val(rowData.serial_no);
		$("#edit_label_serialno").text(rowData.serial_no);
		$("#edit_level").val(rowData.level); 
		$("#direct").val(rowData.direct_flg);

		$("#direct_rapid").removeAttr("checked");
		if (rowData.direct_flg != 1) {
			$("#direct_rapid").next().hide();
		} else {
			if (rowData.scheduled_expedited == 2) {
				var $for = $("#direct_rapid").attr("checked", "checked")
					.next().show().children("span").text("快速");
			} else {
				$("#direct_rapid")
					.next().show().children("span").text("正常纳期");
			}
		}
		$("#direct_rapid").trigger("change");

		$("#service_repair").val(rowData.service_repair_flg);
		$("#fix_type").val(rowData.fix_type);
		$("#selectable").val(rowData.selectable).trigger("change");
		$("#edit_anml_exp").val(rowData.anml_exp || "0").trigger("change");

		$("#edit_customer_name").val(rowData.customer_name); 
		$("#edit_ocm").val(rowData.ocm); 
		$("#edit_ocm_rank").val(rowData.ocm_rank); 
		$("#edit_ocm_deliver_date").val(rowData.ocm_deliver_date); 
		$("#edit_bound_out_ocm").val(rowData.bound_out_ocm);

		$("#edit_ocm_deliver_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
	    });
		if (rowData.direct_flg == 1) {
			$("#edit_bound_out_ocm").parents("tr").show();
		} else {
			$("#edit_bound_out_ocm").parents("tr").hide();
		}

		$("#direct,#service_repair,#fix_type,#selectable,#edit_level,#edit_storager,#edit_ocm_rank,#edit_bound_out_ocm,#edit_anml_exp").select2Buttons();

		$("#direct").change(function(){
			if (this.value == 1) {
				$("#edit_bound_out_ocm").parents("tr").show();
				$fro = $("#direct_rapid").show().next();
				$fro.show().removeAttr("style");
			} else {
				$("#edit_bound_out_ocm").parents("tr").hide();
				$("#direct_rapid").hide().next().hide();
			}
		});

		$("#edit_customer_name").autocomplete({
			source : customers,
			minLength :2,
			delay : 100
		});
		
		//修理等级chang
		$("#edit_level").change(function(){
			var level = this.value;
			var fix_type = $("#fix_type").val();
//			if((level==9 || level==91 || level==92 || level==93) && fix_type==1){
			if (f_isLightFix(level) && fix_type==1) {
				$("#light_pat_button").closest("tr").show();
				$("#major_pat_button").closest("tr").hide();
			}else{
				$("#light_pat_button").closest("tr").hide();
				if ($("#major_pat").attr("value")) {
					$("#major_pat_button").closest("tr").show();
				} else {
					$("#major_pat_button").closest("tr").hide();
				}
			}
		});
		
		//修理分类change
		$("#fix_type").change(function(){
			var fix_type = this.value;
			var level = $("#edit_level").val();
//			if((level==9 || level==91 || level==92 || level==93) && fix_type==1){
			if (f_isLightFix(level) && fix_type==1) {
				$("#light_pat_button").closest("tr").show();
				$("#major_pat_button").closest("tr").hide();
			}else{
				$("#light_pat_button").parents("tr").hide();
				if ($("#major_pat").attr("value")) {
					$("#major_pat_button").closest("tr").show();
				} else {
					$("#major_pat_button").closest("tr").hide();
				}
			}
		});

		$(".ui-button[value='清空']").button();
		jthis.dialog({
			position : 'auto',
			title : "维修对象信息编辑",
			width : 'auto',
			show: "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"确定":function(){
					var data = {
						"material_id":$("#material_id").val(),
						"sorc_no":$("#edit_sorcno").val(),
						"esas_no":$("#edit_esasno").val(),
						"model_id":$("#edit_modelname").val(),
						"serial_no":$("#edit_serialno").val(),
						"customer_name":$("#edit_customer_name").val(),
						"ocm":$("#edit_ocm").val(),
						"ocm_rank":$("#edit_ocm_rank").val(),
						"ocm_deliver_date":$("#edit_ocm_deliver_date").val(),
						"level":$("#edit_level").val(),
						"direct_flg":$("#direct").val() == "" ? "0" : $("#direct").val(),
						"service_repair_flg":$("#service_repair").val(),
						"fix_type":$("#fix_type").val(),
						"selectable":$("#selectable").val(),
						"anml_exp":$("#edit_anml_exp").val()
					}
					// 直送
					if ($("#direct").val() == 1) {
						// 快速
						if ($("#direct_rapid").attr("checked")) {
							data.scheduled_expedited = "2";
						} else {
							data.scheduled_expedited = "0";
						}
					} else {
						data.scheduled_expedited = "0";
					}
					// 地区
					data.bound_out_ocm = $("#edit_bound_out_ocm").val();

					if (!$("#light_pat_button").is(":visible")) {
						data.pat_id = "00000000000";
					}
					if ($("#major_pat").is(":visible")) {
						if (!$("#major_pat").attr("base_id")) {
							data.pat_id = $("#major_pat").attr("value");
						} else	if ($("#major_pat").attr("value") == $("#major_pat").attr("derive_id")) {
							data.pat_id = $("#major_pat").attr("value");
						}
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doupdate',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj) {
							
							var resInfo = null;
				
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
							
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									jthis.dialog("close");
									findits();
								}
							} catch(e) {
									console.log("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
							}
						}
					});
				}, "关闭" : function(){ jthis.dialog("close"); }
			}
		});
		jthis.show();
		
		//设定
		$("#light_pat_button").click(function(){
			setMpaObj.initDialog($("#light_fix_dialog"), $("#material_id").val(), $("#edit_level").val(), $("#edit_modelname").val(), true);
		});

		//设定
		$("#major_pat_button:visible").click(function(){

			if ($("#major_pat").attr("base_id")) {
				var thisPatId = $("#major_pat").attr("value");
				if (thisPatId == $("#major_pat").attr("base_id")) {
					$("#major_pat").text($("#major_pat").attr("derive_name"))
						.attr("value", $("#major_pat").attr("derive_id"));
				} else {
					$("#major_pat").text($("#major_pat").attr("base_name"))
						.attr("value", $("#major_pat").attr("base_id"));
				}
			} else {
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : 'processAssignTemplate.do?method=getDerivePair',
					cache : false,
					data : {model_id : $("#edit_modelname").val(), derive_kind : 3},
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj){
						var resInfo = $.parseJSON(xhrobj.responseText);
						if (resInfo.dpResult) {
							$("#major_pat")
								.attr({
									"base_id": resInfo.dpResult.base_id,
									"base_name": resInfo.dpResult.base_name,
									"derive_id": resInfo.dpResult.derive_id,
									"derive_name": resInfo.dpResult.derive_name
								});
							var thisPatId = $("#major_pat").attr("value");
							if (thisPatId == resInfo.dpResult.base_id) {
								$("#major_pat").text(resInfo.dpResult.derive_name)
									.attr("value", resInfo.dpResult.derive_id);
							} else {
								$("#major_pat").text(resInfo.dpResult.base_name)
									.attr("value", resInfo.dpResult.base_id);
							}
						}
					}
				});
			}
		});
	});
};
