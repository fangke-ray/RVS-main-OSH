$(function() {
		// Your turst our mission
		var modelname = "作业担当人";
		var servicePath = "operatorProduction.do";
		/** 一览数据对象 */
		var listdata = {};

		var initSearch = function() {
			var data = {
				"action_time_start": $("#search_action_time_start").val()
			};
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=initSearch',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					var resInfo = null;
		
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
		
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages("#searcharea", resInfo.errors);
					} else {
						if (resInfo.section_id) {
							$("#search_section_id").val(resInfo.section_id).trigger("change");
						}
						if (resInfo.line_id) {
							$("#search_line_id").val(resInfo.line_id).trigger("change");
						}
						findit();
					}
				}
			});
		}
		var findit = function() {
			var data = {
				"job_no" : $("#search_job_no").val(),
				"name" : $("#search_name").val(),
				"section_id" : $("#search_section_id").val(),
				"line_id" : $("#search_line_id").val(),
				"delete_flg" : $("#search_deleted").val(),
				"action_time_start": $("#search_action_time_start").val(),
				"action_time_end": $("#search_action_time_end").val()
			};

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

		function initGrid() {
			$("#list").jqGrid({
				toppager : true,
				data : [],
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['作业时间', '','工号', '姓名', '', '担当主要工位/作业', '实际工时',
						'主要技能'],
				colModel : [{
							name : 'action_time',
							index : 'action_time',
							width : '85',
							formatter : 'date',
							formatoptions : {
								srcformat : 'Y/m/d H:i:s',
								newformat : 'm-d'
							}
						},{
							name: 'action_time_hidden',
							hidden: true,
							formatter:function(a,b,c){return c.action_time}
						}, {
							name : 'job_no',
							index : 'job_no',
							width : 85
						}, {
							name : 'name',
							index : 'name',
							width : 100
						}, {
							name : 'operator_id',
							index : 'operator_id',
							hidden : true
						}, {
							name : 'position_name',
							index : 'position_name',
							width : 100
						}, {
							name : 'worktime',
							index : 'worktime',
							width : 65,
							align : 'center'
						}, {
							name : 'main_ability',
							index : 'main_ability',
							width : 80
						}],
				rowNum : 50,
				toppager : false,
				pager : "#listpager",
				viewrecords : true,
				caption : modelname + "一览",
				ondblClickRow : function(rid, iRow, iCol, e) {
					showDetail(rid);
				},
				// multiselect : true,
				gridview : true, // Speed up
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true]
			});
		}

		var showDetail = function(rid) {

			var data = $("#list").getRowData(rid);
			var operator_id = data["operator_id"];
			var action_time = data["action_time_hidden"];

			var this_dialog = $("#od_detail_dialog");
			if (this_dialog.length === 0) {
				$("body.outer").append("<div id='od_detail_dialog'/>");
				this_dialog = $("#od_detail_dialog");
			}
			this_dialog.hide();
			this_dialog.load("widgets/operator-detail.jsp", function(
							responseText, textStatus, XMLHttpRequest) {
				this_dialog.dialog({
					position : [400, 20],
					title : action_time + "作业信息",
					width : 800,
					show : "",
					height : 'auto',
					resizable : false,
					modal : true,
					buttons : {
						"确定":function(){
							doopd_Ok(operator_id, action_time, this_dialog);
						},
						"取消":function(){
							this_dialog.dialog('close');
						}
					}
				});
				this_dialog.show();
				
				initDetailView();
				opd_finddetail(operator_id, action_time)
			});
		};


		/*
		 * Ajax通信成功的处理
		 */
		function search_handleComplete(xhrobj, textStatus) {

			var resInfo = null;

			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				loadData(resInfo.list);
			}
		};

		function loadData(data) {
			$("#list").jqGrid().clearGridData();
			$("#list").jqGrid('setGridParam', {
						data : data
					}).trigger("reloadGrid", [{
								current : false
							}]);
		}
		function new_handleComplete(xhrobj, textStatus) {

			showList();
		}

		var showList = function() {
			$(document)[0].title = modelname + "一览";
			$("#searcharea").show();
			$("#listarea").show();
			$("#editarea").hide();
			$("#detailarea").hide();
		};
		
		function initMonthFilesGrid(listdata){
			$("#month_files_list").jqGrid({
				toppager : true,
				data : listdata,
				height : 300,
				width : 400,
				rowheight :20,
				datatype : "local",
				colNames:['文件名','文件大小'],
				colModel:[
				         {name:'file_name',index:'file_name',width:60,align:'left',
				            	formatter : function(value, options, rData){
									return "<a href='javascript:downExcel(\"" + rData['file_name'] + "\");' >" + rData['file_name'] + "</a>";
				   				}},
				         {name:'file_size',index:'file_size',width:40,align:'right'}
				         ],
		        rowNum : 50,
		 		toppager : false,
		 		pager : "#month_files_listpager",
		 		viewrecords : true,
		 		gridview : true, // Speed up
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true]
			});
		}
		
		function search_files_handleComplete(xhrobj, textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				
				var listdata = resInfo.filesList;
				//月档案详细显示
				initMonthFilesGrid(listdata);	
				
				$("#month_files_area").dialog({
						resizable : false,
						modal : true,
						title : "月档案详细一览",
						width : 424,
						buttons : {
							"确认":function(){
								$(this).dialog("close");
							},
							"返回":function(){
								$(this).dialog("close");
							}
						}
				});
				
			} catch (e) {
			}
		}
		
		//月报表显示详细
		function find_month_files(){
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=searchMonthFiles',
				cache : false,
				data : null,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : search_files_handleComplete
			});	
		}
		
		
		function initView() {
			$("input.ui-button").button();
			$("#deleted_set").buttonset();

			var dToday = new Date();
			$("#search_action_time_start").val(dToday.getFullYear() + "/" + (dToday.getMonth()+1) + "/"  + dToday.getDate());

			$("#searchbutton").addClass("ui-button-primary");
			$("#searchbutton").click(function() {
				findit();
			});

			//月报表下载
			$("#monthreportbutton").click(find_month_files);
			
			$("#searcharea span.ui-icon,#wiparea span.ui-icon").bind(
					"click", function() {
						$(this).toggleClass('ui-icon-circle-triangle-n')
								.toggleClass('ui-icon-circle-triangle-s');
						if ($(this).hasClass('ui-icon-circle-triangle-n')) {
							$(this).parent().parent().next().show("blind");
						} else {
							$(this).parent().parent().next().hide("blind");
						}
					});
			$("input[name=deleted]").bind('click', function() {
						$("#search_deleted").val($(this).val());
					});

			$("#resetbutton").click(function() {
				$("#search_job_no").val("");
				$("#search_name").val("");
				$("#search_section_id").val("").trigger("change");
				$("#search_line_id").val("").trigger("change");
				$("#search_deleted").val("");
				$("#deleted_n").attr("checked", "checked").trigger("change");
				$("#search_action_time_start").val(dToday.getFullYear() + "/" + (dToday.getMonth()+1) + "/"  + dToday.getDate());
				$("#search_action_time_end").val("");
			});
			
			$("#search_section_id, #search_line_id").select2Buttons();
			$("#search_action_time_start, #search_action_time_end").datepicker({
				showButtonPanel : true,
				currentText : "今天",
				maxDate : 0
			});

			initGrid();
			initSearch();
		}

		initView();
});

/*下载*/
function downExcel(file_name) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src","operatorProduction.do?method=output&fileName="+ file_name);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "operatorProduction.do?method=output&fileName="+ file_name;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}