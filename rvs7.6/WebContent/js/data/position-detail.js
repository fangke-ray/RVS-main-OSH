var positionDetail = this;
$(function() {

	// Your turst our mission
	var modelname = "工作日报";

	/** 一览数据对象 */
	var listdata = {};

	positionDetail.findit = function(position_id, section_id, action_time) {

		var data = {
			"section_id" : section_id,
			"position_id" : position_id,
			"action_time" : action_time
		};

		$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				data : data,
				cache : false,
				url : "positionProduction.do?method=getDetail",
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj, textStatus) {
					search_handleComplete(xhrobj, textStatus);
					changeOperator(position_id, section_id, action_time);
				}
			});
};

	function initGrid() {

		$("#position_detail_list").jqGrid({
					toppager : true,
					data : [],
					height : 356,
					width : 768,
					rowheight : 23,
					datatype : "local",
					colNames : ['开始时间', '结束时间', '修理单号', '机型', '操作员', '结果'],
					colModel : [{
								name : 'action_time',
								sortable : false,
								width : 85,
								align : 'center',
								formatter : 'date',
								formatoptions : {
									srcformat : 'Y/m/d H:i:s',
									newformat : 'H:i'
								}
							}, {
								name : 'finish_time',
								sortable : false,
								width : 85,
								align : 'center',
								formatter : 'date',
								formatoptions : {
									srcformat : 'Y/m/d H:i:s',
									newformat : 'H:i'
								}
							}, {
								name : 'sorc_no',
								sortable : false,
								width : 120
							}, {
								name : 'model_name',
								sortable : false,
								width : 120
							}, {
								name : 'operator_name',
								sortable : false,
								width : 80
							}, {
								name : 'operate_result',
								sortable : false,
								width : 80
							}],
					rowNum : 50,
					rownumbers : true,
					toppager : false,
					pager : "#position_detail_listpager",
					viewrecords : true,
					caption : modelname + "一览",
					hidegrid : false, // 启用或者禁用控制表格显示、隐藏的按钮
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					gridComplete : showWorking
				});

	}
	var showWorking = function() {
		var commentcells = $("tr:has(td[aria\\-describedby='position_detail_list_operate_result'][title='进行中']) td");
		commentcells.css("background-color", "#50A8FF");
	}

	/*
	 * Ajax通信成功的处理
	 */
	function search_handleComplete(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			setLabel(resInfo.detail);
			setOperatorList(resInfo.names);
			loadData(resInfo.list)
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message
					+ " lineNumber: " + e.lineNumber + " fileName: "
					+ e.fileName);
		}
	};

	function setTitle(time, isToday, process_code) {
		if (isToday == "1") {
			$("#title_processed_count").html("完成台数");
			$("#title_oem_count").html("代工台数");
			$("#title_stop_count").html("中止次数");
		} else {
			var date = dateFormat(time);
			$("#title_processed_count").html(date + "<br/>完成台数");
			$("#title_oem_count").html(date + "<br/>代工台数");
			$("#title_stop_count").html(date + "<br/>中止次数");
		}
		if (process_code == "111" || process_code == "121" || process_code == "131" || process_code == "711") {
			$("#message_batch").show();
			$("#message_leader").hide();
		} else if (process_code == "252" || process_code == "321" || process_code == "400") {
			$("#message_batch").hide();
			$("#message_leader").show();
		} else {
			$("#message_batch").hide();
			$("#message_leader").hide();
		}
	}

	function setLabel(data) {
		$("#label_section_name").text(data.section_name);
		$("#label_line_name").text(data.line_name);
		$("#label_process_code").text(data.process_code);
		$("#label_position_name").text(data.position_name);
		$("#label_waiting_count").text(data.waiting_count+"台");
		$("#label_processed_count").text(data.processed_count+"台");
		$("#label_oem_count").text(data.oem_count+"台");
		$("#label_stop_count").text(data.stop_count+"次");
		
		setTitle(data.action_time, data.isToday, data.process_code);
	}

	function loadData(data) {
		$("#position_detail_list").jqGrid().clearGridData();
		$("#position_detail_list").jqGrid('setGridParam', {
					data : data
				}).trigger("reloadGrid", [{
							current : false
						}]);
	}
	
	function setOperatorList(names) {
		if (names) {
			$("#search_operator_list").append(names);
			$("#search_operator_list").select2Buttons();
		} else {
			$("#search_operator_list").hide();
		}
	}
	
	function changeOperator(position_id, section_id, action_time) {
		$("#search_operator_list").change(function(){
				var operator_id = $(this).val();
				$.ajax({
					data : {
						"section_id" : section_id,
						"position_id" : position_id,
						"action_time" : action_time,
						"operator_id" : operator_id
					},
					url : "positionProduction.do?method=searchByOperator",
					complete : function(xhrobj, textStatus){
						var resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							loadData(resInfo.list);
						} catch (e) {
							alert("name: " + e.name + " message: " + e.message + " lineNumber: "
									+ e.lineNumber + " fileName: " + e.fileName);
						};
					}
				})
			});
	}
	
	function dateFormat(date) {
		if (date) {
			var d = new Date(date);
			var year = d.getFullYear();
			var month = d.getMonth() + 1;
			var day = d.getDate();
			
			return year + "/" + month + "/" + day;
		}
		return "";
	}
	
	$("input.ui-button").button();
	initGrid();
});