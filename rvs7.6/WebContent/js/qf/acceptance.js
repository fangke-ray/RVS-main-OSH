/** 模块名 */
var modelname = "维修对象";
/** 一览数据对象 */
var listdata = {};
var reception_listdata = {};
/** 服务器处理路径 */
var servicePath = "acceptance.do";

/** 医院autocomplete **/
var customers = {};
var curpagenum;
var ocmOptions = {};

var reception_import = function(rowids, data) {
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=doimport', 
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
					for (var i in rowids) {
						var rowdata = $("#uld_list").getRowData(rowids[i]);
						griddata_remove(listdata, "material_id", rowdata["material_id"], false);
					}
					load_list();
					loadImpListData();
				}
			} catch(e) {
				
			}
		}
	});
};

var infoesConfirm = function(infoes){
	var jthis = $("#confirmmessage");

	var content = "";
	for (var ii in infoes) {
		content += infoes[ii].errmsg + "\n";
	}
	jthis.text(content);
	jthis.dialog({
		resizable : false,
		modal : true,
		width:'1024px',
		title : "冲突忽略项目",
		buttons : {
			"确认" : function() {
				jthis.dialog("close");
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
		url : 'upload.do?method=doAccept', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'file', // 文件选择框的id属性
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
					listdata = resInfo.list;
					if ($("#uld_listarea span.ui-icon").hasClass("ui-icon-circle-triangle-s")) {
						$("#uld_listarea span.ui-icon").click();
					}
					load_list();
					if (resInfo.infoes && resInfo.infoes.length > 0)
						infoesConfirm(resInfo.infoes);
					if (resInfo.status)
						uploadComplete(resInfo.status);
				}
			} catch(e) {
				
			}
		}
	});
};

var showLoadInput=function(rid) {
	var linkna = "widgets/qf/acceptance-edit.jsp";

	$("#uld_listedit").hide();
	// 导入编辑画面
	$("#uld_listedit").load(linkna, function(responseText, textStatus, XMLHttpRequest) {
		$("#inp_modelname").hide();
		$("#edit_modelname").hide();
		$("#edit_label_modelname").show();
		$("#edit_serialno").hide();
		$("#edit_label_serialno").show();
		$("#edit_bound_out_ocm").parents("tr").hide();

		// 读取修改行
		var rowData = $("#uld_list").getRowData(rid);

		// 数据取得
		$("#material_id").val(rowData.material_id);
		$("#edit_sorcno").val(rowData.sorc_no);
		$("#edit_esasno").val(rowData.esas_no);
		$("#edit_modelname").val(rowData.model_id);
		$("#edit_label_modelname").text(rowData.model_name);
		$("#edit_serialno").val(rowData.serial_no);
		$("#edit_label_serialno").text(rowData.serial_no);
		$("#edit_ocm").val(rowData.ocm); 
		$("#edit_ocm_rank").val(rowData.ocm_rank); 
		$("#edit_ocm_deliver_date").val(rowData.ocm_deliver_date); 
		$("#edit_customer_name").val(rowData.customer_name); 
		$("#edit_level").val(rowData.level); 
		$("#edit_package_no").val(rowData.package_no); 
		$("#edit_storager").val(rowData.storager);
		$("#direct").val(rowData.direct_flg);
		$("#service_repair").val(rowData.service_repair_flg);
		$("#fix_type").val(rowData.fix_type);
		$("#selectable").val(rowData.selectable);
//		$("#edit_bound_out_ocm").val(rowData.bound_out_ocm);

		$("#ins_material").validate({
			rules : {
				fix_type : {
					required : true
				}
			}
		});

		$("#edit_ocm_deliver_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});

		$("#ins_material select").select2Buttons();

		setReferChooser($("#edit_modelname"));
		$(".ui-button[value='清空']").button();
		$("#uld_listedit").dialog({
			position : 'auto', // [ 800, 20 ]
			title : "维修对象信息编辑",
			width : 'auto',
			show: "blind",
			height :  'auto' , //550
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"确定":function(){
					if ($("#ins_material").valid()) {
						var key_id = $("#material_id").val();
						// 更新内容
						griddata_update(listdata, "material_id", key_id, "sorc_no", $("#edit_sorcno").val(), false);
						griddata_update(listdata, "material_id", key_id, "esas_no", $("#edit_esasno").val(), false);
						griddata_update(listdata, "material_id", key_id, "serial_no", $("#edit_serialno").val(), false);
						griddata_update(listdata, "material_id", key_id, "ocm_rank", $("#edit_ocm_rank").val(), false);
						griddata_update(listdata, "material_id", key_id, "ocm", $("#edit_ocm").val(), false);
						griddata_update(listdata, "material_id", key_id, "ocm_deliver_date", $("#edit_ocm_deliver_date").val(), false);
						griddata_update(listdata, "material_id", key_id, "customer_name", $("#edit_customer_name").val(), false);
						griddata_update(listdata, "material_id", key_id, "level", $("#edit_level").val(), false);
						griddata_update(listdata, "material_id", key_id, "package_no", $("#edit_package_no").val(), false);
						griddata_update(listdata, "material_id", key_id, "storager", $("#edit_storager").val(), false);
						griddata_update(listdata, "material_id", key_id, "direct_flg", $("#direct").val() == "" ? "0" : $("#direct").val(), false);
						griddata_update(listdata, "material_id", key_id, "service_repair_flg", $("#service_repair").val(), false);
						griddata_update(listdata, "material_id", key_id, "fix_type", $("#fix_type").val(), false);
						griddata_update(listdata, "material_id", key_id, "selectable", $("#selectable").val(), false);
						// 地区
//						griddata_update(listdata, "material_id", key_id, "bound_out_ocm", $("#edit_bound_out_ocm").val(), false);

						$("#uld_list").jqGrid().clearGridData();
						$("#uld_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:true}]);
						$(this).dialog("close");
					}
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#uld_listedit").show();
	});

};

var showInput=function(rid, manual) {

	var linkna = "widgets/qf/acceptance-edit.jsp?fromPage=manual";

	$("#uld_listedit").hide();
	// 导入编辑画面
	$("#uld_listedit").load(linkna, function(responseText, textStatus, XMLHttpRequest) {

		$("#edit_customer_name").autocomplete({
			source : customers,
			minLength :2,
			delay : 100
		});

		$("#direct_rapid").button().click(function(){
			if (this.checked == true) {
				$(this).next().children("span").text("快速");
			} else {
				$(this).next().children("span").text("快速");
			}
		}).next().hide();

		if (manual == 1) { //新增
			$("#inp_modelname").show();
			$("#edit_modelname").show();
			$("#edit_label_modelname").hide();
			$("#edit_serialno").show();
			$("#edit_label_serialno").hide();
		} else { //修改
			$("#inp_modelname").hide();
			$("#edit_modelname").hide();
			$("#edit_label_modelname").show();
			$("#edit_serialno").hide();
			$("#edit_label_serialno").show();

			// 读取修改行
			var rowData = $("#imp_list").getRowData(rid);

			// 数据取得
			$("#material_id").val(rowData.material_id);
			$("#edit_sorcno").val(rowData.sorc_no);
			$("#edit_esasno").val(rowData.esas_no);
			$("#edit_modelname").val(rowData.model_id);
			$("#edit_label_modelname").text(rowData.model_name);
			$("#edit_serialno").val(rowData.serial_no);
			$("#edit_label_serialno").text(rowData.serial_no);
			$("#edit_ocm").val(rowData.ocm); 
			$("#edit_ocm_rank").val(rowData.ocm_rank); 
			$("#edit_ocm_deliver_date").val(rowData.ocm_deliver_date); 

			if (rowData.quotation_first == 1) {
				$("#edit_customer_name").val(rowData.customer_name).addClass("fit2rapid");
			} else {
				$("#edit_customer_name").val(rowData.customer_name).removeClass("fit2rapid");
			}

			$("#edit_level").val(rowData.level); 
			$("#edit_package_no").val(rowData.package_no); 
			$("#edit_storager").val(rowData.storager);
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
						.next().show().children("span").text("快速");
				}
			}
			$("#direct_rapid").trigger("change");

			$("#service_repair").val(rowData.service_repair_flg).trigger("change");
			$("#fix_type").val(rowData.fix_type).trigger("change");
			$("#selectable").val(rowData.selectable).trigger("change");
			$("#edit_anml_exp").val(rowData.anml_exp || "0").trigger("change");
			$("#edit_bound_out_ocm").val(rowData.bound_out_ocm);
			$("#edit_contract_related").val(rowData.contract_related || "0").trigger("change");
		}

		$("#edit_ocm_deliver_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});

		$("#ins_material select").select2Buttons();

		$("#direct").change(function(){
			
			if (this.value == 1) {
//				$("#edit_bound_out_ocm").parents("tr").show();
				$fro = $("#direct_rapid").show().next();
				$fro.show().removeAttr("style");
			} else {
//				$("#edit_bound_out_ocm").parents("tr").hide();
				$("#direct_rapid").hide().next().hide();
			}
		});
		$("#edit_bound_out_ocm").parents("tr").show();

		setReferChooser($("#edit_modelname"));
		$(".ui-button[value='清空']").button();
		$("#uld_listedit").dialog({
			position : 'auto', // [ 800, 20 ]
			title : "维修对象信息编辑",
			width : 'auto',
			show: "blind",
			height :  'auto' , //550
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
						"model_name":$("#edit_label_modelname").text(),
						"serial_no":$("#edit_serialno").val(),
						"ocm":$("#edit_ocm").val(),
						"ocm_rank":$("#edit_ocm_rank").val(),
						"customer_name":$("#edit_customer_name").val(),
						"ocm_deliver_date":$("#edit_ocm_deliver_date").val(),
						"level":$("#edit_level").val(),
						"package_no":$("#edit_package_no").val(),
						"storager":$("#edit_storager").val(),
						"direct_flg":$("#direct").val() == "" ? "0" : $("#direct").val(),
						"service_repair_flg":$("#service_repair").val(),
						"fix_type":$("#fix_type").val(),
						"selectable":$("#selectable").val(),
						"anml_exp":$("#edit_anml_exp").val(),
						"contract_related": $("#edit_contract_related").val()
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
					
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=checkModelDepacy',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages("#editarea", resInfo.errors);
								} else {
									var messages = resInfo.messages;
									if(messages.length > 0){
										var warnData = "";
										
										for(var imessage in messages){
											warnData += messages[imessage].errmsg;
										}
										warningConfirm(warnData,function(){
											$.ajax({
												beforeSend : ajaxRequestType,
												async : false,
												url : servicePath + '?method=doinsert',
												cache : false,
												data : data,
												type : "post",
												dataType : "json",
												success : ajaxSuccessCheck,
												error : ajaxError,
												complete : insert_handleComplete
											});
										},function(){});
									}else{
										$.ajax({
											beforeSend : ajaxRequestType,
											async : false,
											url : servicePath + '?method=doinsert',
											cache : false,
											data : data,
											type : "post",
											dataType : "json",
											success : ajaxSuccessCheck,
											error : ajaxError,
											complete : insert_handleComplete
										});
									}
								}
							}catch (e) {
								alert("name: " + e.name + " message: " + e.message + " lineNumber: "
										+ e.lineNumber + " fileName: " + e.fileName);
							};
						}
					});
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#uld_listedit").show();
	});
};

/**
 * 小票打印
 */
var printTicket=function() {

	var data = {};
	var rowids = $("#imp_list").jqGrid("getGridParam", "selarrrow");
	curpagenum = $("#imp_list").jqGrid('getGridParam', 'page');   //当前页码
	var selectedRows = new Array();
	var index = 0;
	for (var i in rowids) {
		var rowdata = $("#imp_list").getRowData(rowids[i]);
		selectedRows.push(rowdata["material_id"]);
		
		if (rowdata["fix_type"] == "3" || rowdata["fix_type"] == "4") {
			continue;
		}
		data["materials.material_id[" + index + "]"] = rowdata["material_id"];
		index++;
	}

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=doPrintTicket', 
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
					var date = new Date();
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do"+"?method=output&fileName=tickets.pdf&filePath=" + resInfo.tempFile);
					} else {
						var iframe = document.createElement("iframe");
			            iframe.src = "download.do"+"?method=output&fileName=tickets.pdf&filePath=" + resInfo.tempFile;
			            iframe.style.display = "none";
			            document.body.appendChild(iframe);
					}
					loadImpListData(curpagenum,selectedRows);
				}
			} catch(e) {
			}
		}
	});
};

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowids = $("#uld_list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length === 0) {
		$("#importbutton").disable();
	} else {
		// 没有流水线分类的不能导入
		var flag = true;
		for (var i in rowids) {
			var data = $("#uld_list").getRowData(rowids[i]);
			if (data["model_id"] == "" || data["fix_type"] == "") {
				flag = false;
				break;
			}
		}
		if (flag) {
			$("#importbutton").enable();
		} else {
			$("#importbutton").disable();
		}
	}
};

var enablebuttons2 = function() {
	var role = $("body").attr("role");
	var isManager = false;
	// 经理
	if(role == "manager"){
		isManager = true;
	}
	
	var rowids = $("#imp_list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length >= 1) {
		var flag = false;
		for (var i in rowids) {
			var data = $("#imp_list").getRowData(rowids[i]);
			// 勾选的记录中存在维修品（流水线or单元），则打印现品票按钮可以使用
			if (data["fix_type"] == "1" || data["fix_type"] == "2") {
				flag = true;
				break;
			}
		}
		if (flag) {
			$("#printbutton").enable();
		} else {
			$("#printbutton").disable();
		}
		$("#returnbutton").enable();
	} else {
		$("#printbutton").disable();
		$("#returnbutton").disable();
	}

	if (rowids.length > 0) {
		var flag = true;
		var notaccepted = true;
		for (var i in rowids) {
			var data = $("#imp_list").getRowData(rowids[i]);
			if ((data["sterilized"] != "0" && data["fix_type"] != "3" && data["fix_type"] != "4") || data["doreception_time"].trim() == "") {
				flag = false;
				break;
			}
		}

		//经理
		if(isManager){
			for (var i in rowids) {
				var data = $("#imp_list").getRowData(rowids[i]);
				if (data["doreception_time"].trim() != "") {
					notaccepted = false;
					break;
				}
			}
		} else {//经理以外
			for (var i in rowids) {
				var data = $("#imp_list").getRowData(rowids[i]);
				var doreception_time = data["doreception_time"].trim();
				var avaliable_end_date_flg = data["avaliable_end_date_flg"].trim();
				
				if (avaliable_end_date_flg == 0 || avaliable_end_date_flg == 1) {
					notaccepted = false;
					break;
				} else if(doreception_time != ""){
					notaccepted = false;
					break;
				}
			}
		}

		if (flag) {
			$("#disinfectionbutton").enable();
			$("#sterilizationbutton").enable();
		} else {
			$("#disinfectionbutton").disable();
			$("#sterilizationbutton").disable();
		}

		if (notaccepted) {
			$("#acceptancebutton").enable();
		} else {
			$("#acceptancebutton").disable();
		}
	} else {
		$("#disinfectionbutton").disable();
		$("#sterilizationbutton").disable();
		$("#acceptancebutton").disable();
	}
};

function acceptted_list(){
	var jthis = $("#imp_list");
	if ($("#gbox_imp_list").length > 0) {
		jthis.jqGrid().clearGridData();
		jthis.jqGrid('setGridParam',{data:reception_listdata}).trigger("reloadGrid", [{current:true}]);
	} else {
		jthis.jqGrid({
//			data:reception_listdata,
			data : [],
			height: 415,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['受理对象ID','导入时间','受理时间', '修理单号', '型号 ID', '型号' , '机身号','委托处ID','委托处','同意日', 
			'等级ID', '等级','通箱编号', '受理人员ID','受理人员', '仓储人员', '备注', '直送', '返修标记', '流水类型','消毒灭菌ID', '选择式报价'
			,'anml_exp','contract_related','ocm_rank','customer_name','vip','scheduled_expedited','ocm_deliver_date', '直送区域', '通箱位置','avaliable_end_date_flg'],
			colModel:[
				{name:'material_id',index:'material_id', hidden:true},
				{name:'reception_time',index:'reception_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
				{name:'doreception_time',index:'doreception_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
				{name:'sorc_no',index:'sorc_no', width:60},
				// {name:'esas_no',index:'esas_no', width:50, align:'center'},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_name', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{name:'ocm',index:'ocm', hidden:true},
				{name:'ocmName',index:'ocmName', width:65},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}},
				{name:'level',index:'level', hidden:true},
				{name:'levelName',index:'levelName', width:35, align:'center'},
				{name:'package_no',index:'package_no', width:50, hidden:true},
				{name:'operator_id',index:'operator_id', hidden:true},
				{name:'operator_name',index:'operator_name', width:50, hidden:true},
				{name:'storager',index:'storager', width:50, hidden:true},
				{name:'remark',index:'remark', width:105,formatter:function(value, options, rData){
					if(rData.avaliable_end_date_flg == 0 || rData.break_back_flg == 1){
						value += "型号终止";
					} 
					if (rData['anml_exp'] == '1') {
						value +=  " 动物实验用";
					}
					return value;
				}},
				{name:'direct_flg',index:'direct_flg', hidden:true},
				{name:'service_repair_flg',index:'service_repair_flg', hidden:true},
				{name:'fix_type',index:'fix_type', hidden:true},
				{name:'sterilized',index:'sterilized', hidden:true},
				{name:'selectable',index:'selectable', hidden:true},
				{name:'anml_exp',index:'anml_exp', hidden:true},
				{name:'contract_related',index:'contract_related', hidden:true},
				{name:'ocm_rank',index:'ocm_rank', hidden:true},
				{name:'customer_name',index:'customer_name', hidden:true},
				{name:'quotation_first',index:'quotation_first', hidden:true},
				{name:'scheduled_expedited',index:'scheduled_expedited', hidden:true},
				{name:'ocm_deliver_date',index:'ocm_deliver_date', hidden:true},
				{
					name : 'bound_out_ocm',
					index : 'bound_out_ocm',
					formatter: "select", editoptions:{value:ocmOptions},
					width : 35,
					align : 'center'
				},
				{name:'wip_location',index:'wip_location', width : 35},
				{name:'avaliable_end_date_flg',index:'avaliable_end_date_flg', hidden:true}
			],
			rowNum: 50,
			toppager: false,
			pager: "#imp_listpager",
			viewrecords: true,
			caption: "受理成果一览",
			ondblClickRow : function(rid, iRow, iCol, e) {
				showInput(rid);
			},
			multiselect: true,
			//multiboxonly: true,
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			onSelectRow : enablebuttons2,
			onSelectAll : enablebuttons2,
			gridComplete: function() {
				enablebuttons2();
				var dataIds = jthis.getDataIDs();
				var length = dataIds.length;
				for (var i = 0; i < length; i++) {
					var rowdata = jthis.jqGrid('getRowData', dataIds[i]);
					// 型号登记有效期标记
					var avaliable_end_date_flg = rowdata["avaliable_end_date_flg"];
					
					if (rowdata["sterilized"] != "0" && rowdata["fix_type"] != "3" && rowdata["fix_type"] != "4") {
						jthis.find("tr#" + dataIds[i] + " td").addClass("waitTicket");
					}
					
					// 过期
					if(avaliable_end_date_flg == 0 ){
						jthis.find("tr#" + dataIds[i] + " td[aria\\-describedby='imp_list_model_name']").css({"background-color":"red"});
					} else if(avaliable_end_date_flg == 1){//离过期还有一个月
						jthis.find("tr#" + dataIds[i] + " td[aria\\-describedby='imp_list_model_name']").css({"background-color":"orange"});
					}
				}
			}
		});

		loadImpListData();

		//$("#imp_list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200, maxHeight:900});
	}
};

var dayworkReport = function() {
//	// Ajax提交
//	$.ajax({
//		beforeSend: ajaxRequestType, 
//		async: true, 
//		url: servicePath + '?method=report', 
//		cache: false, 
//		data: null, 
//		type: "post", 
//		dataType: "json", 
//		success: ajaxSuccessCheck, 
//		error: ajaxError, 
//		complete: function(xhrobj, textStatus){
//			var resInfo = null;
//
//			// 以Object形式读取JSON
//			eval('resInfo =' + xhrobj.responseText);
//		
//			if (resInfo.errors.length > 0) {
//				// 共通出错信息框
//				treatBackMessages(null, resInfo.errors);
//			} else {
//				if ($("iframe").length > 0) {
//					$("iframe").attr("src", "download.do"+"?method=output&fileName="+resInfo.fileName+"&filePath=" + resInfo.tempFile);
//				} else {
//					var iframe = document.createElement("iframe");
//		            iframe.src = "download.do"+"?method=output&fileName="+resInfo.fileName+"&filePath=" + resInfo.tempFile;
//		            iframe.style.display = "none";
//		            document.body.appendChild(iframe);
//				}
//			}
//		}
//	});
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'position_panel.do?method=makeReport',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			infoPop("<span>生成报表的指示已发送，请到<a href='daily_work_sheet.do'>工作记录表画面</a>确认！</span>");
		}
	});	
}

var getAutoComplete = function(){
	$.ajax({
		data : null,
		url: servicePath + "?method=getAutoComplete",
		async: false, 
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			customers = resInfo.customers;
			ocmOptions = resInfo.opt_bound_out_ocm;
		}
	});
}

$(function() {
	$("input.ui-button").button();
	$("#importbutton").disable();
	$("#printbutton").disable();
	$("#disinfectionbutton").disable();
	$("#sterilizationbutton").disable();

	$("a.areacloser").hover(
		function(){$(this).addClass("ui-state-hover");},
		function(){$(this).removeClass("ui-state-hover");}
	);

	$("#editarea").hide();
	$("#detailarea").hide();
	$("#cancelbutton, #editarea span.ui-icon").click(function (){
		showList();
	});

	$("#backbutton, #detailarea span.ui-icon").click(function (){
		showEditBack();
	});

	$("#uploadbutton").click(function() {
		uploadfile();
		// afObj.applyProcess(104, this, uploadfile, arguments);
	});
	$("#importbutton").click(function() {
		var fix_type = "";
		var data = {};
		var rowids = $("#uld_list").jqGrid("getGridParam", "selarrrow");
		for (var i in rowids) {
			var rowdata = $("#uld_list").getRowData(rowids[i]);
			data["materials.sorc_no[" + i + "]"] = rowdata["sorc_no"];
			data["materials.esas_no[" + i + "]"] = rowdata["esas_no"];
			data["materials.model_id[" + i + "]"] = rowdata["model_id"];
			data["materials.model_name[" + i + "]"] = rowdata["model_name"];
			data["materials.serial_no[" + i + "]"] = rowdata["serial_no"];
			data["materials.ocm[" + i + "]"] = rowdata["ocm"];
			data["materials.ocm_rank[" + i + "]"] = rowdata["ocm_rank"];
			data["materials.ocm_deliver_date[" + i + "]"] = rowdata["ocm_deliver_date"];
			data["materials.customer_name[" + i + "]"] = rowdata["customer_name"];
			data["materials.agreed_date[" + i + "]"] = rowdata["agreed_date_o"];
			data["materials.level[" + i + "]"] = rowdata["level"];
			data["materials.package_no[" + i + "]"] = rowdata["package_no"];
			data["materials.storager[" + i + "]"] = rowdata["storager"];
			data["materials.direct_flg[" + i + "]"] = rowdata["direct_flg"];
			data["materials.service_repair_flg[" + i + "]"] = rowdata["service_repair_flg"];
			data["materials.fix_type[" + i + "]"] = fix_type =rowdata["fix_type"];
			data["materials.selectable[" + i + "]"] = rowdata["selectable"];
		}
		if ("4" === fix_type) {
			reception_import(rowids, data);
		} else {
			afObj.applyProcess(104, this, reception_import, [rowids, data]);
		}
	});
	$("#manualbutton").click(function() {
		afObj.applyProcess([103, 104], this, showInput, [0,1]);
	});
	$("#printbutton").click(function() {
		afObj.applyProcess(101, this, printTicket, arguments);
	});

	$("#outbutton").click(dayworkReport);
	$("#acceptancebutton").click(function() {
		afObj.applyProcess(103, this, doAccept, arguments);
	});

	$("#disinfectionbutton").click(doDisinfection);

	// $("#sterilizationbutton").click(doSterilization);
	$("#factmatchbutton").click(getMatch);

	$("#returnbutton").click(doReturn);
	$("#resetbutton").click(function() {
		listdata = {};
		load_list();
	});

	load_list();

	getAutoComplete();
	acceptted_list();

	takeWs();

	$("#factmatch_button").click(factmatch);
});

// 工位后台推送
function takeWs() {
	$(".if_message-dialog input[value=忽略]").click(function(){
		$(".if_message-dialog").removeClass("show");
	});
	$(".if_message-dialog input[value=刷新]").click(function(){
		$(".if_message-dialog").removeClass("show");
		loadImpListData();
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
    			$(".if_message-dialog").addClass("show");
    		}
    	} catch(e) {
    	}
    };  
    // 连接上时走这个方法  
	position_ws.onopen = function() {     
		position_ws.send("entach:"+"#00000000009#"+$("#op_id").val());
	}; 
	} catch(e) {
	}	
};

/*
* Ajax通信成功時の処理
*/
function search_handleComplete(Xhrobj, textStatus) {
};

function load_list(){
	if ($("#gbox_uld_list").length > 0 || listdata.length === 0) {
		$("#uld_list").jqGrid().clearGridData();
		$("#uld_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#uld_list").jqGrid({
			data:listdata,
			//height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['', '修理单号', 'ESAS No.', '型号 ID', '型号' , '机身号','委托处','同意时间','同意日','level','等级'
			          , '通箱编号', '仓储人员', '', '', '', '备注', '选择式报价','anml_exp','','',''],
			colModel:[
				{name:'material_id',index:'material_id', hidden:true},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'esas_no',index:'esas_no', width:50, align:'center'},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{name:'ocm',index:'ocm', width:65, hidden:true}, // TODO
				{name:'agreed_date_o',index:'agreed_date', hidden:true,formatter: function(value, options, rData){
					return rData["agreed_date"];
				}},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}},
				{name:'level',index:'level', hidden:true},
				{name:'levelName',index:'levelName', width:35, align:'center'},
				{name:'package_no',index:'package_no', width:50},
				{name:'storager',index:'storager', width:50},
				{name:'direct_flg',index:'direct_flg', hidden:true},
				{name:'service_repair_flg',index:'service_repair_flg', hidden:true},
				{name:'fix_type',index:'fix_type', hidden:true},
				{name:'comment',index:'comment', width:105, formatter:function(value, options, rData){
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
					} else if (rData["fix_type"] == "3") {
						comment += " CDS备品";
					} else if (rData["fix_type"] == "4") {
						comment += " 协助RC CDS品";
					}
					if (rData['anml_exp'] == '1') {
						comment += " 动物实验用";
					}
					return comment.trim();
				}
				},
				{name:'selectable',index:'selectable', hidden:true},
				{name:'anml_exp',index:'anml_exp', hidden:true},
				{name:'ocm_rank',index:'ocm_rank', hidden:true},
				{name:'customer_name',index:'customer_name', hidden:true},
				{name:'ocm_deliver_date',index:'ocm_deliver_date', hidden:true}
			],
			rowNum: 50,
			toppager: false,
			pager: "#uld_listpager",
			viewrecords: true,
			caption: "导入数据一览",
			ondblClickRow : function(rid, iRow, iCol, e) {
				showLoadInput(rid);
			},
			multiselect: true,
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			onSelectRow : enablebuttons,
			onSelectAll : enablebuttons,
			gridComplete: enablebuttons,
			hiddengrid: true
		});
	}
};

var insert_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			if ($("#inp_modelname").is(":hidden")) {
				$("#uld_listedit").dialog('close'); 
			} else {
				// 继续
				$("#material_id").val("");
				$("#edit_sorcno").val("");
				$("#edit_esasno").val("");
				$("#edit_modelname").val("");
				$("#inp_modelname").val("");
				$("#edit_serialno").val("");
				$("#edit_ocm").val("").trigger("change");
				$("#edit_level").val("").trigger("change");
				$("#edit_package_no").val("原箱");
				$("#edit_customer_name").val("");
				$("#edit_storager").val("");
				$("#direct").val("").trigger("change");
				$("#service_repair").val("").trigger("change");
				$("#fix_type").val("").trigger("change");
				$("#selectable").val("").trigger("change");
				$("#edit_anml_exp").val("").trigger("change");
				$("#edit_contract_related").val("").trigger("change");
				$("#edit_bound_out_ocm").val("").trigger("change");
			}

			loadImpListData();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

function loadImpListData(curpagenum,selectedRows) {
	$.getJSON(servicePath + "?method=loadData", function(data) { 
		$("#imp_list").jqGrid().clearGridData();
		reception_listdata = data.list;
	    $("#imp_list").jqGrid('setGridParam', {
            datatype : 'local',
            data : reception_listdata
        }).trigger("reloadGrid");
        if(curpagenum){
			var maxpage = parseInt((reception_listdata.length - 1) / 50) + 1;
			if (curpagenum > maxpage) {
				curpagenum = maxpage;
			}
        	$("#imp_list").jqGrid('setGridParam', {
	            page:curpagenum
	        }).trigger("reloadGrid");
        }
	        
        if(selectedRows){
        	for(var i = 0;i < selectedRows.length;i++){
        		var materialId = selectedRows[i];
        		var rowID = $("#imp_list").find("td[aria\-describedby='imp_list_material_id'][title='" + materialId + "']").parent().attr("id");
        		$("#imp_list").setSelection(rowID);
        	}
        }
	}); 
}

var doAccept = function(){
	var data = {};
	var rowids = $("#imp_list").jqGrid("getGridParam", "selarrrow");
	var comm = "";
	curpagenum = $("#imp_list").jqGrid('getGridParam', 'page');   //当前页码
	var selectedRows = new Array();
	for (var i in rowids) {
		var rowdata = $("#imp_list").getRowData(rowids[i]);
		selectedRows.push(rowdata["material_id"]);
		data["materials.material_id[" + i + "]"] = rowdata["material_id"];
		var t_direct = rowdata["direct_flg"];
		if (t_direct == 1) {
			if (!rowdata["bound_out_ocm"]) {
				comm += (rowdata["model_name"] + "/" + rowdata["serial_no"] + "\n");
			}
		}
	}

	if (comm) {
		var $jthis = $("#confirmmessage");
		$jthis.text("请为以下直送品：\n" + comm + "选择直送区域。");
		$jthis.dialog({
			resizable : false,
			dialogClass : 'ui-warn-dialog', 
			modal : true,
			title : "直送区域确认",
			buttons : {
				"关闭" : function() {
					$jthis.dialog("close");
				}
			}
		});
	} else {
		$.ajax({
			data : data,
			url: servicePath + "?method=doAccept",
			async: false, 
			beforeSend: ajaxRequestType, 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			type : "post",
			complete : function(){
			loadImpListData(curpagenum,selectedRows);
			}
		});
	}
}

var doReturn = function(){
	var jthis = $("#confirmmessage");

	jthis.text("未修理返还后，这些维修对象将会退出RVS系统中的显示，确认操作吗？");
	jthis.dialog({
		resizable : false,
		modal : true,
		dialogClass : 'ui-warn-dialog', 
		title : "返还操作确认",
		buttons : {
			"确认" : function() {
				var data = {};
				var rowids = $("#imp_list").jqGrid("getGridParam", "selarrrow");
				for (var i in rowids) {
					var rowdata = $("#imp_list").getRowData(rowids[i]);
					data["materials.material_id[" + i + "]"] = rowdata["material_id"];
				}

				$.ajax({
					data : data,
					url: servicePath + "?method=doReturn",
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					type : "post",
					complete : function(){
						jthis.dialog("close");
						loadImpListData();
					}
				});
			},
			"取消" : function() {
				jthis.dialog("close");
			}
		}
	});

}

function doDisinfection(){
//	var test_listdata = [];
	var rowids = $("#imp_list").jqGrid("getGridParam","selarrrow");
	var ids = [];
//	var test_ids = [];
	var comm = "";

	for (var i in rowids) {
		var rowdata = $("#imp_list").getRowData(rowids[i]);
//		test_listdata[i]={
//				material_id:rowdata["material_id"],
//				sorc_no:rowdata["sorc_no"],
//				model_name:rowdata["model_name"],
//				serial_no:rowdata["serial_no"],
//				fix_type:rowdata["fix_type"]
//		};
		ids[ids.length] = rowdata["material_id"];
		var t_direct = rowdata["direct_flg"];
		if (t_direct == 1) {
			if (!rowdata["bound_out_ocm"]) {
				comm += (rowdata["model_name"] + "/" + rowdata["serial_no"] + "\n");
			}
		}
	}

	if (comm) {
		var $jthis = $("#confirmmessage");
		$jthis.text("请为以下直送品：\n" + comm + "选择直送区域。");
		$jthis.dialog({
			resizable : false,
			dialogClass : 'ui-warn-dialog', 
			modal : true,
			title : "直送区域确认",
			buttons : {
				"关闭" : function() {
					$jthis.dialog("close");
				}
			}
		});
	} else {
		var data = {};
		data.ids = ids.join(",");

		$.ajax({
			data : data,
			url: servicePath + "?method=doDisinfection",
			async: false, 
			beforeSend: ajaxRequestType, 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			type : "post",
			complete : function(xhrObj, textStatus){
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.sentMessage) {
					var sentMessage = "";
					var nList = [], dList = [], sList = [];
					for (var i in resInfo.sentMessage) {
						var material_id = resInfo.sentMessage[i];
						if (material_id.indexOf('_') < 0) {
							var omr_notifi_no 
								= $("#imp_list td[aria\\-describedby='imp_list_material_id'][title='" + material_id + "']")
									.parent().children("[aria\\-describedby='imp_list_sorc_no']").attr("title");
							nList.push(omr_notifi_no);
						} else {
							var splitMP = material_id.split("_");
							var omr_notifi_no 
								= $("#imp_list td[aria\\-describedby='imp_list_material_id'][title='" + splitMP[0] + "']")
									.parent().children("[aria\\-describedby='imp_list_sorc_no']").attr("title");
							switch (splitMP[1]) {
								case "00000000010" : dList.push(omr_notifi_no); break;
								case "00000000011" : sList.push(omr_notifi_no); break;
							}
						}
					}
					if (nList.length) {
						sentMessage += "以下维修品没有指示消毒/灭菌，请相关人员确认："
							+ nList.join() + "\n";
					}
					if (dList.length) {
						sentMessage += "以下维修品已放入消毒等待区："
							+ dList.join() + "\n";
					}
					if (sList.length) {
						sentMessage += "以下维修品已放入灭菌等待区："
							+ sList.join() + "\n";
					}
					if (sentMessage.length) {
						sentMessage = sentMessage.substring(0, sentMessage.length - 1);
					}
					infoPop(decodeText(sentMessage));
				}
				loadImpListData();
			}
		});	

//		var jthis = $("#test_list");
//		if ($("#gbox_test_list").length > 0) {
//			jthis.jqGrid().clearGridData();
//			jthis.jqGrid('setGridParam',{data:test_listdata}).trigger("reloadGrid", [{current:true}]);
//		} else {
//			jthis.jqGrid({
//				data:test_listdata,
//				height: 215,
//				width: 500,
//				rowheight: 23,
//				datatype: "local",
//				colNames:['','修理单号','型号','机身号',''],
//				colModel:[
//					{name:'material_id',index:'material_id', hidden:true},
//					{name:'sorc_no',index:'sorc_no', width:60},
//					{name:'model_name',index:'model_name'},
//					{name:'serial_no',index:'serial_no', width:60, align:'center'},
//					{name:'fix_type',index:'fix_type', hidden:true}
//				],
//				rowNum: 50,
//				toppager: false,
//				viewrecords: true,
//				multiselect: true,
//				gridview: true
//			});
//		}

//		$("#test_listarea").dialog({
//			resizable : false,
//			modal : true,
//			width: 525,
//			title : "其中需测漏品确认",
//			buttons : {
//				"确认" : function() {
//					rowids = $("#test_list").jqGrid("getGridParam","selarrrow");
//					if (rowids.length == 0) {
//						errorPop("请选择测漏对象。");
//						return;
//					}
//					for (var i in rowids) {
//						var rowdata = $("#test_list").getRowData(rowids[i]);
//						test_ids[test_ids.length] = rowdata["material_id"] + "_" + rowdata["fix_type"];
//					}
//					$(this).dialog("close");
//				},
//				"都无需测漏" : function() {
//					$(this).dialog("close");
//				}
//			},
//			close: function (){
//				var data = {};
//				data.ids = ids.join(",");
//				if (test_ids.length > 0) {
//					data.test_ids = test_ids.join(",");
//				}
//				$.ajax({
//					data : data,
//					url: servicePath + "?method=doDisinfection",
//					async: false, 
//					beforeSend: ajaxRequestType, 
//					success: ajaxSuccessCheck, 
//					error: ajaxError, 
//					type : "post",
//					complete : function(){
//						loadImpListData();
//					}
//				});	
//			}
//		});
	}
}

//function doSterilization(){
//	var rowids = $("#imp_list").jqGrid("getGridParam","selarrrow");
//	var ids = [];
//	var comm = "";
//
//	for (var i in rowids) {
//		var rowdata = $("#imp_list").getRowData(rowids[i]);
//		ids[ids.length] = rowdata["material_id"];
//		var t_direct = rowdata["direct_flg"];
//		if (t_direct == 1) {
//			if (!rowdata["bound_out_ocm"]) {
//				comm += (rowdata["model_name"] + "/" + rowdata["serial_no"] + "\n");
//			}
//		}
//	}
//
//	if (comm) {
//		var $jthis = $("#confirmmessage");
//		$jthis.text("请为以下直送品：\n" + comm + "选择直送区域。");
//		$jthis.dialog({
//			resizable : false,
//			dialogClass : 'ui-warn-dialog', 
//			modal : true,
//			title : "直送区域确认",
//			buttons : {
//				"关闭" : function() {
//					$jthis.dialog("close");
//				}
//			}
//		});
//	} else {
//		$.ajax({
//			data : {ids:ids.join(",")},
//			url: servicePath + "?method=doSterilization",
//			async: false, 
//			beforeSend: ajaxRequestType, 
//			success: ajaxSuccessCheck, 
//			error: ajaxError, 
//			type : "post",
//			complete : function(){
//				loadImpListData();
//			}
//		});	
//	}
//}


/*
* Ajax通信成功時の処理
*/
function uploadComplete(status) {
	$confirmmessage = $("#confirmmessageOgz");

	$confirmmessage.find("label:eq(0)").text(status.delivered);
	$confirmmessage.find("label:eq(1)").text(status.shipped);
	$confirmmessage.find("label:eq(2)").text(status.wip_count);
	$confirmmessage.find("label:eq(3)").text(status.inlined);
	$confirmmessage.find("label:eq(4)").text(status.approved);

	$confirmmessage.dialog({
		title : "OGZ 当前状况确认",
		resizable : false,
		modal : true,
		buttons : {
			"确定":function(){
				var postData = status;
				$.ajax({
					data : postData,
					url: "summary.do?method=doImport",
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					type : "post",
					complete : function(xhrObj){
						$confirmmessage.dialog("close");
					}
				});
			}, 
			"关闭" : function(){ $confirmmessage.dialog("close"); }
		}		
	});
};

var getMatch = function(){
	$.ajax({
		data : null,
		url: servicePath + "?method=getMatch",
		async: true, 
		dataType : "json",
		beforeSend: ajaxRequestType, 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		type : "post",
		complete : matchShow
	});
}

var localReceptionsWithoutFact = null;
var localReceptionsTemp = null;

var matchShow = function(xhrObj) {
	var resInfo = $.parseJSON(xhrObj.responseText);

	if (resInfo.errors && resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);

		return;
	}

	if (!resInfo.receptionsTemp || !resInfo.receptionsTemp.length) {
		infoPop("当前没有未匹配的临时实物受理记录。不需要匹配。");
	} else {
		localReceptionsWithoutFact = resInfo.receptionsWithoutFact;
		localReceptionsTemp = resInfo.receptionsTemp;

		setMatchTables();

		$("#factmatch_dialog").dialog({
			position : 'auto',
			title : "系统/实物受理品手动匹配",
			show: "blind",
			width :  '920',
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"关闭":function(){
					$("#factmatch_dialog").dialog("close");
				}
			}
		});
	}
}

var cutReceptionTime = function(reception_time) {
	if (reception_time && reception_time.length > 16) {
		return reception_time.substring(5, 16);
	} else {
		return "-";
	}
}
var showTags = function(tag_types) {
	if (tag_types && tag_types.length) {
		var tag_typea = tag_types.split(",");
		var ret = "";
		for (var i in tag_typea) {
			switch(tag_typea[i]) {
				case "2" : ret += "待测漏<br>";break;
				case "4" : ret += "待消毒<br>";break;
				case "5" : ret += "待灭菌<br>";break;
			}
		}
		if (ret) {
			return ret.substring(0, ret.length - 4);
		}
		return "-";
	} else {
		return "-";
	}
}

var setMatchTables = function(){

	var $tbodyLeft = $("#factmatch_sys > table > tbody");
	$tbodyLeft.html("");
	var currentModel = "";
	for (var sysIdx in localReceptionsWithoutFact) {
		var sysMaterial = localReceptionsWithoutFact[sysIdx];
		if (sysMaterial.model_name != currentModel) {
			$tbodyLeft.append("<tr><td colspan='4' class='ui-state-default'>" + sysMaterial.model_name + "</td></tr>");
			currentModel = sysMaterial.model_name;
		}
		$tbodyLeft.append("<tr m_id=" + sysMaterial.material_id + " model_name='" + currentModel + "'><td>" 
			+ sysMaterial.serial_no + "</td><td>" + (sysMaterial.direct_flg != "1" ? "RC":"<span class='direct_flg'>直送</span>") 
			+ "</td><td>" + cutReceptionTime(sysMaterial.reception_time) + "</td><td>" + sysMaterial.sorc_no + "</td></tr>");
	}

	var $tbodyRight = $("#factmatch_fact > table > tbody");
	$tbodyRight.html("");

	for (var sysIdx in localReceptionsTemp) {
		var sysMaterial = localReceptionsTemp[sysIdx];
		if (sysMaterial.model_name != currentModel) {
			$tbodyRight.append("<tr><td colspan='4' class='ui-state-default'>" + sysMaterial.model_name + "</td></tr>");
			currentModel = sysMaterial.model_name;
		}
		$tbodyRight.append("<tr m_id=" + sysMaterial.fact_recept_id + " model_name='" + currentModel + "'><td>" 
			+ sysMaterial.serial_no + "</td><td>" + (sysMaterial.direct_flg != "1" ? "RC":"<span class='direct_flg'>直送</span>") 
			+ "</td><td>" + cutReceptionTime(sysMaterial.reception_time) + "</td><td>" + showTags(sysMaterial.tag_types) + "</td></tr>");
	}

	$("#sys_content table, #fact_content table").attr("w_id", "")
		.html("<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>");

	$tbodyLeft.find("tr").click(function(){
		var $tr = $(this);
		var left_id = $tr.attr("m_id");
		if (!left_id) return;
		var model_name = $tr.attr("model_name");
		var serial_no = $tr.children().eq(0).text();

		$("#sys_content table").attr("w_id", left_id)
			.html("<tr><td>" + model_name + "</td></tr><tr><td>" + serial_no + "</td></tr>");
		$tbodyLeft.find("tr.ui-state-highlight").removeClass("ui-state-highlight");
		$tr.addClass("ui-state-highlight");
	});

	$tbodyRight.find("tr").click(function(){
		var $tr = $(this);
		var right_id = $tr.attr("m_id");
		if (!right_id) return;
		var model_name = $tr.attr("model_name");

		var serial_no = $tr.children().eq(0).text();

		$("#fact_content table").attr("w_id", right_id)
			.html("<tr><td>" + model_name + "</td></tr><tr><td>" + serial_no + "</td></tr>");
		$tbodyRight.find("tr.ui-state-highlight").removeClass("ui-state-highlight");
		$tr.addClass("ui-state-highlight");
	});
}

var factmatch=function(){
	var $sys_content_table = $("#sys_content table");
	var $fact_content_table = $("#fact_content table");
	var left_id = $sys_content_table.attr("w_id");
	if (!left_id) {
		errorPop("请选择系统受理维修品。");
		return;
	}
	var right_id = $fact_content_table.attr("w_id");
	if (!right_id) {
		errorPop("请选择实物受理维修品。");
		return;
	}
	if ($sys_content_table.find("td:eq(0)").html() != $fact_content_table.find("td:eq(0)").html()) {
		warningConfirm("选择的系统/实物受理品型号不完全一致，请确认是否匹配。",
		function(){
			factmatchPost(left_id, right_id);
		}, 
		null, null, 
		"继续匹配");
	} else {
		factmatchPost(left_id, right_id);
	}
}

var factmatchPost=function(material_id, fact_recept_id){
	var postData = {
		fact_recept_id: fact_recept_id,
		material_id:material_id
	}

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=doFactmatch', 
		cache: false, 
		data: postData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrObj, textStatus){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				griddata_remove(localReceptionsWithoutFact, "material_id", material_id, false);
				griddata_remove(localReceptionsTemp, "fact_recept_id", fact_recept_id, false);
				setMatchTables();
			}
		}
	});
}