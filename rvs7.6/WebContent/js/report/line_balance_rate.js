var servicePath="line_balance_rate.do";
var chart2 = null;
var keepSearchData;
$(function(){
	$("input.ui-button").button();
	
	$("span.ui-icon, #detailsearcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    });
	
    $("#search_px").find("option[value='3']").remove();
	$("#search_category_id,#search_level,#search_section_id,#search_line_id,#search_px,#paChooser").select2Buttons();
	setReferChooser($("#hidden_model_id"),$("#search_model_referchooser"));
	
	$("#search_rework, #search_cell").buttonset();

    $("#search_finish_time_start,#search_finish_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	$("#resetbutton").click(function(){
		reset();
	});

	/*验证*/
	$("#searchform").validate({
		rules:{
			section_id:{
				required:true
			},
			line_id:{
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
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: servicePath + '?method=export', 
			cache: false, 
			data: keepSearchData, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			complete: export_handleComplete
		});
	});

	reset();
	$("#label_balance_rate").text("");
	$("#exportbutton").disable();

	$("#selectflowbutton").button().click(selectflowDialog);
	$("#paChooser").change(selectflow);
	$("#search_line_id").change(function(){
		$("#paChooser").val("").trigger("change");
	});
});

var selectflowDialog = function(){
	if (!$("#search_line_id").val()) {
		errorPop("请先选择工程。");
		return;
	}

	var $paChooser = $("#paChooser").parent();
	$paChooser.dialog({
		title : "选择流程来指定工位",
		width : 600,
		height : 300,
		resizable : false,
		modal : true
	})
}
var selectflow = function(){
	if (this.value == "") {
		$("#selected_processes").text("");
		$("#paChooser").parent().dialog("close");
		return;
	}

	var data = {
		"pat_id":this.value,
		"line_id":$("#search_line_id").val()
	};

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'processAssignTemplate.do?method=getPositionsOfLineByPat', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function (xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			$("#selected_processes").text(resInfo.processCodes);
			$("#paChooser").parent().dialog("close");
		}
	});
}

var reset = function(){
	$("#searchform input[type='text']").val("");
	$("#searchform input[type='hidden']").val("");
	$("#searchform select").val("").trigger("change");
	$("#rework_y").attr("checked","checked").trigger("change");
	$("#cell_n").attr("checked","checked").trigger("change");
	$("#paChooser").val("").trigger("change");
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

var findit = function(){
	var data = {
		"category_id":$("#search_category_id").val(),
		"model_id":$("#hidden_model_id").val(),
		"level":$("#search_level").val(),
		"section_id":$("#search_section_id").val(),
		"line_id":$("#search_line_id").val(),
		"px":$("#search_px").val(),
		"rework":$("#search_rework").find("input[type='radio']:checked").val(),
		"cell":$("#search_cell").find("input[type='radio']:checked").val(),
		"finish_time_start":$("#search_finish_time_start").val(),
		"finish_time_end":$("#search_finish_time_end").val(),
		"process_codes":$("#selected_processes").text()
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

var search_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	}else{
		if(resInfo.avgWorkTimeList.length===0){
			$("#container").html('<div style="width:100px;margin:200px auto;font-size:20px;">无数据</div>');
			$("#label_balance_rate").text("");
			$("#exportbutton").disable();
		}else{
			$("#container").html("");
			showChart(resInfo);
			$("#label_balance_rate").text(resInfo.balanceRate);
			$("#exportbutton").enable();
			
			var options = {};
			options.exporting = {};
			options.exporting.sourceWidth = 800;
			options.exporting.sourceHeight = 400;
			
			var wrap = $("<span></span>");
			wrap.append($(chart2.getSVG(options)));
			wrap.find("g.highcharts-tooltip").remove();
			wrap.find('text[text-anchor="undefined"]').attr("text-anchor", "start");

			keepSearchData = {
	            "category_name":$("#search_category_id option:selected").text().trim(),
				"model_name":$("#search_model_name").val(),
				"level":$("#search_level option:selected").text().trim(),
				"section_name":$("#search_section_id option:selected").text().trim(),
				"line_name":$("#search_line_id option:selected").text().trim(),
				"px":$("#search_px option:selected").text().trim(),
				"rework":$("#search_rework").find("input[type='radio']:checked").val(),
				"cell":$("#search_cell").find("input[type='radio']:checked").val(),
				"finish_time_start":$("#search_finish_time_start").val(),
				"finish_time_end":$("#search_finish_time_end").val(),
				"balance_rate":$("#label_balance_rate").text(),
				"process_codes":$("#selected_processes").text(),
				"svg":wrap.html()
	        }
		}
	}
};

function showChart(resInfo){
	if (chart2 == null) {
		chart2 = new Highcharts.Chart({
			chart:{
				renderTo : 'container',
				type : 'column',
				borderRadius:0//边框圆角
			},
			title:{
				text:'平均耗时',
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
					text:'分钟',
					style: {
		                color: '#000',
		                fontSize:'13px'
		            }
				},
				labels:{
					style:{
						fontSize:'12px',
						color:'black'
					}
				},
				lineWidth:1,
				lineColor:'#000000',
				gridLineWidth:1,
				gridLineDashStyle:'Dash',
				tickWidth:1,
				tickLength:5,
				tickColor:'#000000',
				tickPosition:'inside',
				plotLines:[{
					color:'#F9453D',
					value:resInfo.maxWorkTime,
					width:2,
					id:'pl_wk',
					label:{
						text:resInfo.maxWorkTime,
						textAlign:'middle',
						style:{
							fontSize:'13px'
						}
					}
				}]
			},
			tooltip:{
				headerFormat:'<span style="font-size:10px">{point.key}</span><table>',
				pointFormat:'<tr>'+
								'<td style="color:{series.color};padding:0">{series.name}:</td>' +
								'<td style="padding:0"><b>{point.y} </b></td>' +
							'</tr>',
				footerFormat:'</table>',
				shared:true,
				useHTML:true,
				valueSuffix:'分钟'
			},
			plotOptions:{
				column:{
					connectNulls:true,
					lineWidth:3,
					animation:true,
					dataLabels:{
						enabled:true
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
			series:[{
				name:'平均耗时',
				color:'#40D079',
				data:resInfo.avgWorkTimeList
			}],
			exporting:{
				enabled:false
			}
		});
	} else {
		chart2.yAxis[0].removePlotLine('pl_wk');
		chart2.yAxis[0].addPlotLine({
			color:'#F9453D',
			value:resInfo.maxWorkTime,
			width:2,
			id:'pl_wk',
			label:{
				text:resInfo.maxWorkTime,
				style:{
					fontSize:'13px'
				}
			}
		});
		chart2.xAxis[0].setCategories(resInfo.xAxisList, false);
		chart2.series[0].setData(resInfo.avgWorkTimeList);
	}
};
