$(function () {
	$(".ui-button").button();
	$("#infoes").buttonset();
	
	$("span.ui-icon.ui-icon-circle-triangle-n").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
    
	$("#infoes input:radio").click(function(){
		showList(this.value);
		$(".record_page").hide();
		$("#" + this.value).show();
	});
	
	$(".record_page").hide();
	$("#page_arrangement").show();
	$("#page_arrangement_tab").attr("checked","checked").trigger("change");
	
	page_arrangement.init();
	page_arrangement.findit();
	
	page_case.init();
	//page_case.findit();
});

function showList(search_page) {
	if (search_page == "page_arrangement") {
		page_arrangement.findit();
	} else if (search_page == "page_case") {
		page_case.findit();
	}
};

var page_arrangement = {
	"servicePath" : "waste_partial_arrangement.do",
	
	"init" : function(){
		setReferChooser($("#hidden_model_id") , $("#search_model_id_referchooser"));
		
		$("#search_arr_collect_time_start,#search_arr_collect_time_end").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
		
		$("#arr_resetbutton").click(function(){
			$("#searcharrform input[type='text'],#searchform input[type='hidden']").val("");
		});
		
		$("#arr_searchbutton").click(function(){
			page_arrangement.findit();
		});
	},
	"findit" : function(){
		var data = {
			"omr_notifi_no" : $("#search_arr_omr_notifi_no").val(),
			"model_id" : $("#hidden_arr_model_id").val(),
			"serial_no" : $("#search_arr_serial_no").val(),
			"collect_time_start" : $("#search_arr_collect_time_start").val(),
			"collect_time_end" : $("#search_arr_collect_time_end").val(),
			"case_code" : $("#search_arr_case_code").val()
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_arrangement.servicePath + '?method=search',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				var resInfo;
				try {
					resInfo = $.parseJSON(xhrObj.responseText);
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages("" , resInfo.errors);
					} else {
						page_arrangement.showList(resInfo.finished);
					}
				} catch (e) {
				}
			}
		});
	},
	"showList" : function(listdata){
		if ($("#gbox_arrangementlist").length > 0) {
			$("#arrangementlist").jqGrid().clearGridData();
			$("#arrangementlist").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#arrangementlist").jqGrid({
				data : listdata,
				height : 461,
				width : 990,
				rowheight : 23,
				datatype : "local",
				colNames : ['修理单号','型号','机身号','回收部分','收集时间','操作者','装箱编号'],
				colModel : [
					{name:'omr_notifi_no',index:'omr_notifi_no',width:40},
					{name:'model_name',index:'model_name',width:50},
					{name:'serial_no',index:'serial_no',width:35},
					{name:'part',index:'part',width:30,align:'center'},
					{name:'collect_time',index:'collect_time',width:35,align:'center'},
					{name:'operator_name',index:'operator_name',width:35},
					{name:'case_code',index:'case_code',width:35}
				],
				toppager : false,
				rowNum : 20,
				rownumbers : true,
				pager : "#arrangementlistpager",//翻页
				viewrecords : true,//显示总记录数
				gridview : true, 
				pagerpos : 'right',
				ondblClickRow : null,
				onSelectRow :null,
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true],
				gridComplete:function(){
				}
			});
		}
	}
};

var page_case = {
	"servicePath" : "waste_partial_recycle_case.do",
	
	"init" : function(){
		$("#search_case_collect_kind").buttonset();
		
		$("#search_case_package_date_start,#search_case_package_date_end,#search_case_waste_apply_date_start,#search_case_waste_apply_date_end,#waste_update_waste_apply_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
		
		$("#case_resetbutton").click(function(){
			$("#searchcaseform input[type='text']").val("");
			$("#add_collect_kind_all").attr("checked","checked").trigger("change");
		});
		
		$("#case_searchbutton").click(function(){
			page_case.findit();
		});
		
		$("#waste_apply_button").disable().click(page_case.doWaste);
		
		$("#canclecasebutton,#update_case_area span.ui-icon.ui-icon-circle-triangle-w").click(function(){
			$("#search_case_area").show();
			$("#update_case_area").hide();
		});
	},
	
	"findit" : function(){
		var data = {
			"case_code" : $("#search_case_case_code").val(),
			"collect_kind" : $("#search_case_collect_kind input:checked").val(),
			"package_date_start" : $("#search_case_package_date_start").val(),
			"package_date_end" : $("#search_case_package_date_end").val(),
			"waste_apply_date_start" : $("#search_case_waste_apply_date_start").val(),
			"waste_apply_date_end" : $("#search_case_waste_apply_date_end").val()
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_case.servicePath + '?method=search',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				var resInfo;
				try {
					resInfo = $.parseJSON(xhrObj.responseText);
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages("" , resInfo.errors);
					} else {
						page_case.showList(resInfo.finished);
					}
				} catch (e) {
				}
			}
		});
	},
	"showList" : function(listdata){
		if ($("#gbox_caselist").length > 0) {
			$("#caselist").jqGrid().clearGridData();
			$("#caselist").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#caselist").jqGrid({
				data : listdata,
				height : 461,
				width : 990,
				rowheight : 23,
				datatype : "local",
				colNames : ['','装箱编号','用途种类','打包日期','重量','废弃申请日期','备注'],
				colModel : [
				    {name:'case_id',index:'case_code',hidden:true},
				    {name:'case_code',index:'case_code',width:30},
					{name:'collect_kind',index:'collect_kind',width:30,formatter : 'select',editoptions:{value : "1:内窥镜;2:周边设备"}},
					{name:'package_date',index:'package_date',width:30,align:'center'},
					{name:'weight',index:'weight',width:30,align:'right',formatter:"number",formatoptions:{suffix:' kg',defaultValue:'-'}},
					{name:'waste_apply_date',index:'waste_apply_date',width:30,align:'center'},
					{name:'comment',index:'comment',width:200}
				],
				toppager : false,
				rowNum : 20,
				rownumbers : true,
				pager : "#caselistpager",//翻页
				viewrecords : true,//显示总记录数
				gridview : true, 
				pagerpos : 'right',
				ondblClickRow : page_case.showEdit,
				onSelectRow :page_case.enablebuttons,
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true],
				gridComplete:function(){
					page_case.enablebuttons();
				}
			});
		}
	},
	"enablebuttons" : function(){
		var rowID = $("#caselist").jqGrid("getGridParam", "selrow");
		if(rowID > 0){
			var rowData = $("#caselist").getRowData(rowID);
			if(rowData.waste_apply_date){
				$("#waste_apply_button").disable();
			}else{
				$("#waste_apply_button").enable();
			}
		}else{
			$("#waste_apply_button").disable();
		}   
	},
	"doWaste" : function(){
		var rowID = $("#caselist").jqGrid("getGridParam", "selrow");
		var rowData = $("#caselist").getRowData(rowID);
		
		var now = new Date();
		var year = now.getFullYear();
		var month = now.getMonth() + 1;
		var date = now.getDate();
		
		var weight = rowData.weight;
		if(weight == '-'){
			$("#waste_update_weight").val("").removeClass("errorarea-single");
		}else{
			$("#waste_update_weight").val(rowData.weight.replace("kg","").trim()).removeClass("errorarea-single");
		}
		$("#waste_update_waste_apply_date").val(year + "/" + month + "/" + date).removeClass("errorarea-single");
		
		$("#waste_dialog").dialog({
			resizable : false,
			modal : true,
			title : "申请废弃",
			width : 350,
			buttons : {
				"确认" : function() {
					warningConfirm("确定将编号为【" + rowData.case_code + "】箱子申请废弃。",function(){
						var data = {
							"case_id" : rowData.case_id,
							"weight" : $("#waste_update_weight").val(),
							"waste_apply_date" : $("#waste_update_waste_apply_date").val()
						};
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : page_case.servicePath + '?method=doWaste',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj){
								var resInfo;
								try {
									resInfo = $.parseJSON(xhrObj.responseText);
									if (resInfo.errors.length > 0) {
										// 共通出错信息框
										treatBackMessages(null, resInfo.errors);
									} else {
										page_case.findit();
										$("#waste_dialog").dialog("close");
									}
								} catch (e) {
								}
							}
						});
					},function(){});
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	},
	"showEdit" : function(){
		var rowID = $("#caselist").jqGrid("getGridParam", "selrow");
		var rowData = $("#caselist").getRowData(rowID);
		
		if(rowData.waste_apply_date){
			return;
		}
		
		$("#update_case_code").val(rowData.case_code);
		if(rowData.collect_kind == 1){
			$("#label_collect_kind_name").text("内窥镜");
		} else {
			$("#label_collect_kind_name").text("周边设备");
		}
		
		var weight = rowData.weight;
		if(weight == '-'){
			$("#update_weight").val("");
		}else{
			$("#update_weight").val(rowData.weight.replace("kg","").trim());
		}
		$("#update_comment").val(rowData.comment);
		$("#search_case_area").hide();
		$("#update_case_area").show();
		
		$("#updatecasebutton").unbind("click").bind("click",function(){
			var data = {
				"case_id" : rowData.case_id,
				"case_code" : $("#update_case_code").val(),
				"weight" : $("#update_weight").val(),
				"comment" : $("#update_comment").val()
			};
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : page_case.servicePath + '?method=doUpdate',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrObj){
					var resInfo;
					try {
						resInfo = $.parseJSON(xhrObj.responseText);
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages(null, resInfo.errors);
						} else {
							page_case.findit();
							$("#search_case_area").show();
							$("#update_case_area").hide();
						}
					} catch (e) {
					}
				}
			});
		});
	}
};