var servicePath="remain_time_report.do";
var chart2 = null;
var obj_cOptions = {};

$(function() {
	$("input.ui-button").button();
	
	$("span.ui-icon, #detailsearcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    });

	/*Tab切换*/
	$("#infoes input:radio").click(function(){
		$(".record_page").hide();
		$("#" + this.value).show();
	});

	$(".record_page").hide();
	if (window.location.search) {
		$("#infoes input:radio[value=page_unreach]").trigger("click");
		var mc = window.location.search.match(new RegExp("[\?\&]omr_notifi_no=([^\&]+)","i"));
		if (mc) {
			$("#search_omr_notifi_no").val(mc[1]);
			$("#search_contrast_time_start").val("");
		}
	} else {
		$("#infoes input:radio[value=page_rate]").trigger("click");
	}

	/*达成率画面Start*/
    $("#search_start_date,#search_end_date").datepicker({
    	minDate : "2016/12/01",
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	$("#search_main_cause").select2Buttons();

	$("#resetbutton").click(function(){
		reset();
	});

	/*验证*/
	$("#searchform").validate({
		rules:{
			start_date:{
				required:true
			},
			end_date:{
				required:true
			}
		}
	});

	$("#searchbutton").click(function(){
		if ($("#searchform").valid()) {
			findit();
		}
	});

	$("#exportbutton").click(function(){
		var options = {};
		options.exporting = {};
		options.exporting.sourceWidth = 800;
		options.exporting.sourceHeight = 400;

		var wrap = $("<span></span>");
		wrap.append($(chart2.getSVG(options)));
		wrap.find("g.highcharts-tooltip").remove();
		var data = {
			"start_date":$("#search_start_date").val(),
			"end_date":$("#search_end_date").val(),
			"svg":wrap.html()
		};

		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: servicePath + '?method=export', 
			cache: false, 
			data: data, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			complete: export_handleComplete
		});
	});

	reset();
	$("#exportbutton").disable();
	/*达成率画面End*/

	/*未达成理由画面Start*/
	showList([]);

	setReferChooser($("#search_position_id"));

    $("#search_contrast_time_start,#search_contrast_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	$("#reset2button").click(function(){
		reset2();
	});

	$("#search2button").click(function(){
		findUnreach();
	});
	/*未达成理由画面End*/
});

var reset = function() {
	$("#searchform input[type='text']").val("");
	$("#searchform select").val("").trigger("change");
};

var export_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
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
};

var findit = function() {
	var data = {
		"start_date":$("#search_start_date").val(),
		"end_date":$("#search_end_date").val()
	};

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=search', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: search_handleComplete
	});
};

var search_handleComplete = function(xhrObj) {
	var resInfo = null;
	eval("resInfo=" + xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	} else {
		if (resInfo.decomposeLineList.length === 0) {
			$("#container").html('<div style="width:100px;margin:200px auto;font-size:20px;">无数据</div>');
			$("#exportbutton").disable();
		} else {
			$("#container").html("");
			showChart(resInfo);
			$("#exportbutton").enable();
		}
	}
};

function showChart(resInfo) {
	if (chart2 == null) {
		chart2 = new Highcharts.Chart({
			colors : ['#4F81BD','#FFC000','#9BBB59','#8064A2','#FF0000'],
			chart:{
				renderTo : 'container',
				type : 'line',
				borderRadius:0//边框圆角
			},
			title:{
				text:'倒计时达成率',
				style: {
	                color: '#000',
	                fontWeight: 'bold'
	            }
			},
			xAxis:{
				categories:resInfo.xAxisList,
				labels:{
					style:{
						fontSize:'13px',
						color:'black'
					}
				},
				lineColor:'#000000',//轴线颜色
				tickPosition:'inside',//刻度方向
				tickLength:5,//刻度线长度
				tickWidth:1,
				tickColor:'#000000'

			},
			yAxis:{
				title:{
					text:'',
					style: {
		                color: '#000',
		                fontSize:'13px'
		            }
				},
				labels:{
					style:{
						fontSize:'12px',
						color:'black'
					},
					formatter:function() {
						return this.value + "%";
					}
				},
				text:'',
				lineWidth:1,
				lineColor:'#000000',
				gridLineWidth:1,
				gridLineDashStyle:'Dash',
				tickWidth:1,
				tickLength:5,
				tickColor:'#000000',
				tickPosition:'inside',
				tickInterval:10,
				min:0,
				max:100
			},
			tooltip:{
				headerFormat:'<span style="font-size:10px">{point.key}</span><table>',
				pointFormat:'<tr>'+
								'<td style="color:{series.color};padding:0">{series.name}:</td>' +
								'<td style="padding:0"><b>{point.y}%</b></td>' +
							'</tr>',
				footerFormat:'</table>',
				shared:true,
				useHTML:true
			},
			plotOptions:{
				line:{
					connectNulls:true,
					lineWidth:3,
					animation:true,
					dataLabels:{
						enabled:true,
						formatter:function() {
							return this.y + "%";
						}
					}
				}
			},
			legend:{
				borderWidth:1,
				itemStyle:{
					fontSize: '13px'
				}
			},
			credits:{
				enabled:false
			},
			series:[
			{
				name:'零件发放',
				data:resInfo.partialList
			},{
				name:'分解工程',
				data:resInfo.decomposeLineList
			},{
				name:'NS工程',
				data:resInfo.nsLineList
			},{
				name:'总组工程',
				data:resInfo.composeLineList
			},{
				name:'目标',
				data:resInfo.targetList,
				marker:{
					enabled:false,
					states:{
						hover:{
							enabled:false
						}
					}
				}
			}],
			exporting:{
				enabled:false
			}
		});
	} else {
		chart2.xAxis[0].setCategories(resInfo.xAxisList, false);
		chart2.series[0].setData(resInfo.partialList, false);
		chart2.series[1].setData(resInfo.decomposeLineList, false);
		chart2.series[2].setData(resInfo.nsLineList, false);
		chart2.series[3].setData(resInfo.composeLineList, false);
		chart2.series[4].setData(resInfo.targetList);
	}
};

var reset2 = function() {
	$("#search_position_id").val("")
		.prev().val();
	$("#search_contrast_time_start").val("");
	$("#search_contrast_time_end").val("");
	$("#search_omr_notifi_no").val("");
	$("#search_main_cause").val("").trigger("change");
};
var findUnreach = function(){
	var data = {
		"contrast_time_start":$("#search_contrast_time_start").val(),
		"contrast_time_end":$("#search_contrast_time_end").val(),
		"position_id":$("#search_position_id").val(),
		"omr_notifi_no":$("#search_omr_notifi_no").val(),
		"main_cause":$("#search_main_cause").val()
	};

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=searchUnreach', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrObj) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			showList(resInfo.listData, resInfo.countdown_unreach_main_cause);
		}
	});
}
var showList = function(listdata, cOptions) {
	if ($("#gbox_unreachlist").length > 0) {
		// jqGrid已构建的情况下,重载数据并刷新
		$("#unreachlist").jqGrid().clearGridData();
		$("#unreachlist").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
	} else {
		obj_cOptions = selecOptions2Object($("#cOptions").val());
		// 构建jqGrid对象
		$("#unreachlist").jqGrid({
			data : listdata,
			height : 461,
			width : gridWidthMiddleRight,
			rowheight : 23,
			datatype : "local",
			colNames : ['key','修理单号', '发生工位', '判定时间', '判定人员', '判定原因', '管理人员意见'],
			colModel : [
			{name:'countdown_key', hidden:true, key: true},
			{name:'omr_notifi_no',index:'omr_notifi_no',width:'70'},
			{name:'process_code',index:'process_code',width:'70', align:'center'},
			{name:'contrast_time',index:'contrast_time',width:'120',
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d H:i:s',
					newformat : 'y-m-d H\\h'
				}
			},
			{name:'operator_name',index:'operator_name',width:'70'},
			{name:'main_cause',index:'main_cause', align:'center',
				formatter : 'select',
				editoptions : {
					value : $("#cOptions").val()
				},
				width : 100
			},
			{name:'comment',index:'comment',width:'400'}
			],
			toppager : false,
			rowNum : 20,
			pager : "#unreachlistpager",
			viewrecords : true,
			caption : "倒计时未达成状况一览",
			ondblClickRow : setComment,
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true]
		});
	}
}

var setComment = function(key){
	if (!key) return;
	if ($("#unreach_detail").length == 0) return;
	var data = {
		"countdown_key":key
	};

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=getUnreachDetail', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrObj) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			var detail = resInfo.detail;

			var $detailTable = $("<table class='subform'/>")
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">修理单号</td><td class=\"td-content\">" + detail.omr_notifi_no + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">型号</td><td class=\"td-content\">" + detail.model_name + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">发生工位</td><td class=\"td-content\">" + detail.process_code + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">判定人员</td><td class=\"td-content\">" + detail.operator_name + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">判定时间</td><td class=\"td-content\">" + detail.contrast_time + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">预定结束时间<br>(发生工位)</td><td class=\"td-content\">" + (detail.contrast_time_end || "-") + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">主要原因</td><td class=\"td-content\">" + obj_cOptions[detail.main_cause] + "</td></tr>");
			$detailTable.append("<tr><td class=\"ui-state-default td-title\">管理人员意见</td><td class=\"td-content\"><textarea id='cuComment' style='width: 260px;'></textarea></td></tr>");
			$detailTable.find("#cuComment").val(detail.comment);

			var $unreach_detail = $("#unreach_detail"); 
			$unreach_detail.html($detailTable);
			$unreach_detail.dialog({
				title : "倒计时未达成状况详细",
				width : 600,
				show : "",
				resizable : false,
				modal : true,
				buttons : {"更新意见" : function(){
					var pData = {"countdown_key":key, comment : $("#cuComment").val()}

					$.ajax({
						beforeSend: ajaxRequestType, 
						async: false, 
						url: servicePath + '?method=doUpdateUnreachComment', 
						cache: false, 
						data: pData, 
						type: "post", 
						dataType: "json", 
						success: ajaxSuccessCheck, 
						error: ajaxError, 
						complete: function(){
							$unreach_detail.dialog("close");
							findUnreach();
						}
					});

				},
				"取消":function(){
					$unreach_detail.dialog("close");
				}}
			});
		}
	});
}