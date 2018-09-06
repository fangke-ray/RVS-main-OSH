var servicePath = "consumable_online.do";

/* 型号 */
var partialCode;

$(function() {

	$("input.ui-button").button();
	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
	$("#searcharea span.ui-icon").bind("click",
	function() {
		$(this).toggleClass('ui-icon-circle-triangle-n')
				.toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#search_section_id, #search_line_id").select2Buttons();

	findit();

	// 清除button
	$("#reset_button").click(function() {
		$("#search_code").val("");
		$("#search_section_id").val("").trigger("change");
		$("#search_line_id").val("").trigger("change");

	});

	// 查询
	$("#search_button").click(function() {

		// 课室和工程check
		if ($("#search_section_id").val() == "" || $("#search_line_id").val() == "") {
			errorPop("请选择课程或工程！");
			return false;
		}
		 findit();
	});	

	// scroll2fix
	$(window).bind("scroll", function() {
		var $jwin = $(this);
        var scrolls = $jwin.scrollTop() + $jwin.height();
		var localer = $("#functionarea").position().top;
		var bar = $("#functionarea div:eq(0)");
        // 
 		if (scrolls - $("#listarea").position().top > 120) {
	        if ((scrolls - localer > bar.height())){
	        	if (bar.hasClass("bar_fixed")) {
	        		bar.removeClass("bar_fixed");
	        	}
	        } else {
	        	if (!bar.hasClass("bar_fixed")) {
	        		bar.addClass("bar_fixed");
	        	}
	        }
 		} else {
        	if (bar.hasClass("bar_fixed")) {
        		bar.removeClass("bar_fixed");
        	}
		}
    });

	$("#adjust_button").click(doAdjust).enable();
})

/* 判断废改订按钮enable、disable */
var enableButton = function() {

	// 选择行，并获取行数
	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#consumable_list").getRowData(row);
	if (row > 0) {
		$("#waste_revision_button").enable();
	} else {
		$("#waste_revision_button").disable();
	}
}

var keepSearchData;
var findit = function(data) {
	if (!data) {
		keepSearchData = {
			"consumable" : $("#search_code").val(),
			"course" : $("#search_section_id").val(),
			"project" : $("#search_line_id").val()
		};
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
		complete : show_consumable_list
	});
};
function show_consumable_list(xhrobj, textStatus) {

	var resInfo = null;
	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
    var consumableList = resInfo.consumable_list;
    
	if ($("#gbox_consumable_list").length > 0) {
		$("#consumable_list").jqGrid().clearGridData();
		$("#consumable_list").jqGrid('setGridParam', {data : consumableList})
			.trigger("reloadGrid", [ {current : false} ]);
	} else {

		$("#consumable_list").jqGrid(
				{
					data : consumableList,
					height : 1841,
					width : 992,
					rowheight : 23,
					datatype : "local",
					colNames : [ 'partial_id', '零件编码', '零件说明', '消耗品<br>分类', 
							'当前仓库<br>有效库存', '当前线上<br>有效库存', '清点结果', '最后清点日期', '一周内未清点'],
					colModel : [ {
						name : 'partial_id',
						index : 'partial_id',
						hidden : true, 
						key: true
					}, {
						name : 'code',
						index : 'code',
						width : 60
					}, {
						name : 'name',
						index : 'name'
					}, {
						name : 'type',
						index : 'type',
						width : 50,
						formatter:'select',
						editoptions : {
							value : $("#res_type").val()
						}
					}, {
						name : 'available_inventory',
						index : 'available_inventory',
						align : 'right',
						formatter : 'integer',
						sorttype : 'int',
						width : 60,
						formatoptions: {
							defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
					}, {
						name : 'quantity',
						index : 'quantity',
						align : 'right',
						formatter : 'integer',
						sorttype : 'int',
						width : 60,
						formatoptions: {
							defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
						}
					}, {
						name : 'quantity_modify',
						index : 'quantity_modify',
						align : 'left',
						formatter : function(val, b,rData){
							return "<input class='fixQ' id='"+b.rowId+"' value='"+ rData.quantity_modify +"' adjust_time='" + rData.adjust_time + "'></input>";
						},
						sorttype : 'int',
						width : 100,
						hidden : true
					}, {
						name : 'adjust_time',
						index : 'adjust_time',
						align : 'center',
						width : 50,
						formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
					}, {
						name : 'wait_for_adjust',
						index : 'wait_for_adjust',
						hidden : true
					}],
					rowNum : 80,
					rownumbers:true,
					toppager : false,
					pager : "#consumable_list_pager",
					viewrecords : true,
					gridview : true,
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					hidegrid : false,
					deselectAfterSort : false,
					// caption: "零件定位数据管理一览",
					onSelectRow : enableButton,
					viewsortcols : [ true, 'vertical', true ],
					gridComplete : function() {
						
						var $thisGrid = $("#consumable_list");
						
						// 当天日期取得
						var currentDate = new Date();
						var year = currentDate.getFullYear();
						var month = currentDate.getMonth() + 1;
						var day = currentDate.getDate();

						// 月份小于10的场合补零
						if (month < 10) {
							month = "0" + month;
						}
						
						// 日期小于10天的场合补零
						if (day < 10) {
							day = "0" + day;
						}
						// 更新当天日期
						var currentTime =  year + "/" +  month + "/" + day;
						
						// 当天日期
						var date1 = new Date(currentTime);

						for (var i = 0; i < consumableList.length; i++) {

							// 零件 ID
							var partial_id = consumableList[i].partial_id;
							
							// 最后清点日期
							var adjust_time = consumableList[i].adjust_time;
							
							// 最后清点日期为空的场合标红
							if (adjust_time == null || adjust_time == "") {

								$thisGrid.find("tr#" + partial_id + " td[aria-describedby='consumable_list_adjust_time']").addClass("ui-state-error");
							} else {

								// 画面日期
								var date2 = new Date(consumableList[i].adjust_time);

								// 作差
								var temp = (date1 - date2) / 1000 / 3600 / 24;
								
								// 最后清点日期相差7天的场合存储该行partial_id
								if (temp > 7) {

									$thisGrid.find("tr#" + partial_id + " td[aria-describedby='consumable_list_adjust_time']").addClass("ui-state-error");
								}
							}
						}

						$thisGrid.find(".fixQ").bind("keydown", function(e) {
							var kcode = e.keyCode;
							if (kcode !=8 && kcode != 46) {
								if (kcode < 48 || kcode > 57) {
									return false;
								}
							}
						}).bind("change", function(e) {
							$(this).attr("changed", "changed");
							$("#adjust_button").enable();
						});
						var rowids = $thisGrid.getDataIDs();
						for (var i in rowids) {
							var data = $thisGrid.getRowData(rowids[i]);
							if (data["wait_for_adjust"] == "1") {
								$thisGrid.find("tr#" + rowids[i] + " td[aria\\-describedby=consumable_list_adjust_time]")
									.addClass("wait_for_adujst");
							}
						}
					}
				});
	}
};

var doAdjust = function() {

	if (this.value == "清点开始") {
		this.value = "清点完成";
		$("#consumable_list").jqGrid('showCol', 'quantity_modify');
	} else {
		var data = {};
		var i = 0;
		var quantity_modify;
		this.value = "清点开始";

		// 遍历Grid
		$("#consumable_list").find(".fixQ[changed=changed]").each(function(idx, ele) {

			// 取得当前线上有效库存(修改用)
			quantity_modify = $("#ele.value").val();
			
			// 整数型Check
			if(!parseInt(quantity_modify) == quantity_modify){
				errorPop("请输入整数。");
				return false;
			}
			
			// 取得课室
			data["consumable_online.section_id[" + i + "]"] = keepSearchData.course;
			// 取得工程
			data["consumable_online.line_id[" + i + "]"] = keepSearchData.project;
			// 取得partial_id
			data["consumable_online.partial_id[" + i + "]"] = $(this).attr("id");
			// 取得当前线上有效库存(修改用)
			data["consumable_online.quantity_modify[" + i + "]"] = ele.value;
			// 取得adjust_time
			data["consumable_online.adjust_time[" + i + "]"] = $(this).attr("adjust_time");
			
			i++;
		});
		// Ajax提交
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: false, 
			url: servicePath + '?method=doupdate', 
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
						$("#consumable_list").jqGrid('hideCol', 'quantity_modify');
						findit();
					}
				} catch(e) {
					
				}
			}
		});
		
	}
	$("#consumable_list").jqGrid('setGridWidth', '992');
}