$(function() {
			// Your turst our mission
			var modelname = "工位";
			var servicePath = "positionProduction.do";
			/** 一览数据对象 */
			var listdata = {};

			var findit = function() {
				var data = {
					"process_code" : $("#search_process_code").val(),
					"position_id" : $("#search_position_name").val(),
					"section_id" : $("#search_section_id").val(),
					"line_id" : $("#search_line_id").val(),
					"action_time_start" : $("#search_action_time_start").val(),
					"action_time_end" : $("#search_action_time_end").val()
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

			function initView() {
				$("input.ui-button").button();

				$("#searchbutton").addClass("ui-button-primary");
				$("#searchbutton").click(function() {
							findit();
						});

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

				$("#resetbutton").click(function() {
							$("#search_process_code").val("");
							$("#search_position_name").val("");
							$("#search_section_id").val("").trigger("change");
							$("#search_line_id").val("").trigger("change");
							$("#search_action_time_start").val("");
							$("#search_action_time_end").val("");
							$("#text_position_name").val("");
						});
				$("#search_section_id, #search_line_id").select2Buttons();
				$("#search_action_time_start, #search_action_time_end")
						.datepicker({
									showButtonPanel : true,
									currentText : "今天",
									maxDate : 0
								});
								
				setReferChooser($("#search_position_name"), $("#position_refer"));

				var dToday = new Date();
				$("#search_action_time_start").val(dToday.getFullYear() + "/" + (dToday.getMonth()+1) + "/"  + dToday.getDate());

				initGrid();
				findit();
			}

			var showDetail = function(rid) {

				var data = $("#list").getRowData(rid);
				var section_id = data["section_id"];
				var position_id = data["position_id"];
				var action_time = data["action_time_hidden"];
				popPositionDetail(position_id, section_id, action_time, true);

			};

			var popPositionDetail = function(position_id, section_id,
					action_time, is_modal) {
				if (is_modal == null)
					is_modal = true;
				var this_dialog = $("#detail_dialog");

				if (this_dialog.length === 0) {
					$("body.outer").append("<div id='detail_dialog'/>");
					this_dialog = $("#detail_dialog");
				}

				this_dialog.hide();

				// 导入详细画面
				this_dialog.load("widgets/position-detail.jsp", function(
								responseText, textStatus, XMLHttpRequest) {
							this_dialog.dialog({
										position : [400, 20],
										title : "工位详细画面",
										width : 820,
										show : "",
										height : 'auto',
										resizable : false,
										modal : is_modal,
										buttons : null
									});

							this_dialog.show();
							positionDetail.findit(position_id, section_id, action_time);
						});
			}

			function initGrid() {
				$("#list").jqGrid({
					toppager : true,
					data : [],
					height : 461,
					width : 992,
					rowheight : 23,
					datatype : "local",
					colNames : ['日期','','课室ID', '课室', '工位ID', '工位代码', '工位名称',
							'所属工程', '担当者', '当前进行台数', '当前等待台数', '完成台数'],
					colModel : [{
								name : 'action_time',
								index : 'action_time',
								width : '85',
								align : 'center',
								formatter : 'date',
								formatoptions : {
									srcformat : 'Y/m/d H:i:s',
									newformat : 'm-d'
								}
							},{
								name : 'action_time_hidden',
								hidden: true,
								formatter:function(a,b,c){return c.action_time}
							},{
								name : 'section_id',
								index : 'section_id',
								hidden : true
							}, {
								name : 'section_name',
								index : 'section',
								width : 35
							}, {
								name : 'position_id',
								index : 'position_id',
								hidden : true
							}, {
								name : 'process_code',
								index : 'process_code',
								width : 55
							}, {
								name : 'position_name',
								index : 'position_name',
								width : 125
							}, {
								name : 'line_name',
								index : 'line_name',
								width : 55
							}, {
								name : 'operator_name',
								index : 'operator_name',
								width : 125
							}, {
								name : 'processing_count',
								index : 'processing_count',
								width : 80,
								align : 'center',
								sorttype : 'integer',
								formatter : function(a, b, c) {
									if (c.isToday) {
										return (a || 0) + "台";
									} else {
										return "-";
									}
								}
							}, {
								name : 'waiting_count',
								index : 'waiting_count',
								width : 80,
								align : 'center',
								sorttype : 'integer',
								formatter : function(a, b, c) {
									if (c.isToday) {
										return (a || 0) + "台";
									} else {
										return "-";
									}
								}
							}, {
								name : 'processed_count',
								index : 'processed_count',
								width : 80,
								align : 'center',
								sorttype : 'integer',
								formatter : 'integer',
								formatoptions : {
									suffix : "台"
								}
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
					$("#list").jqGrid().clearGridData();
					$("#list").jqGrid('setGridParam', {
								data : resInfo.list
							}).trigger("reloadGrid", [{
										current : false
									}]);
				}
			};

			initView();
		});