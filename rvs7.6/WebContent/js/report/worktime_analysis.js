var servicePath="worktime_analysis.do";
var chart = null;
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
	
	$("#search_category_id,#search_level,#search_section_id,#search_anomaly_section_id,#search_anomaly_line_id").select2Buttons();
	setReferChooser($("#hidden_model_id"),$("#search_model_id_referchooser"));
	setReferChooser($("#hidden_position_id"),$("#search_position_id_referchooser"),null,function(tr){
		var process_code = $(tr).find("td").last().text().trim();
		$("#hidden_process_code").val(process_code);
	});
	setReferChooser($("#hidden_operator_id"),$("#search_operator_id_referchooser"));
	
	$("#search_scheduled_expedited,#search_rework,#search_abnormal").buttonset();

	var today = new Date();
    $("#search_finish_time_start,#search_finish_time_end").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});
	
	$("#search_anomaly_finish_time_start,#search_anomaly_finish_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	
	$("#resetbutton").click(function(){
		reset();
	});
	
	$("#searchbutton").click(function(){
		$("#search_category_id").data("post", $("#search_category_id").val());
		$("#hidden_category_name").data("post", $("#search_category_id").find("option:selected").text().trim());
		$("#hidden_model_id").data("post", $("#hidden_model_id").val());
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_level").data("post", $("#search_level").val());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#search_section_name").data("post", $("#search_section_id option:selected").text().trim());
		$("#hidden_position_id").data("post", $("#hidden_position_id").val());
		$("#hidden_process_code").data("post", $("#hidden_process_code").val());
		$("#search_scheduled_expedited").data("post", $("#search_scheduled_expedited").find("input[type='radio']:checked").val());
		$("#search_rework").data("post", $("#search_rework").find("input[type='radio']:checked").val());
		$("#hidden_operator_id").data("post", $("#hidden_operator_id").val());
		$("#search_operator_name").data("post", $("#search_operator_name").val());
		$("#search_abnormal").data("post", $("#search_abnormal").find("input[type='radio']:checked").val());
		
		var finish_time_start = $("#search_finish_time_start").val().trim();
		if(finish_time_start){
			finish_time_start = finish_time_start.substring(0,4) + "/" + finish_time_start.substring(4,6) + "/01";
		}
		var finish_time_end = $("#search_finish_time_end").val().trim();
		if(finish_time_end){
			finish_time_end = finish_time_end.substring(0,4) + "/" + finish_time_end.substring(4,6) + "/01";
		}
		
		$("#search_finish_time_start").data("post", finish_time_start);
		$("#search_finish_time_end").data("post", finish_time_end);
		
		find();
	});
	
	$("#exportbutton").click(function(){
		var options = {
		};
		options.exporting = {};
		options.exporting.sourceWidth = 800;
		options.exporting.sourceHeight = 400;
		
		var wrap = $("<span></span>");
		wrap.append($(chart.getSVG(options)));
		wrap.find("g.highcharts-tooltip").remove();
		var data = {
			"category_name":$("#hidden_category_name").data("post"),
			"model_name":$("#search_model_name").data("post"),
			"level":$("#search_level").data("post"),
			"section_name":$("#search_section_name").data("post"),
			"process_code":$("#hidden_process_code").data("post"),
			"scheduled_expedited":$("#search_scheduled_expedited").data("post"),
			"rework":$("#search_rework").data("post"),
			"operator_name":$("#search_operator_name").data("post"),
			"finish_time_start":$("#search_finish_time_start").data("post"),
			"finish_time_end":$("#search_finish_time_end").data("post"),
			"abnormal":$("#search_abnormal").data("post"),
			"svg":wrap.html()
		}
		
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
	
	$("#export_anomaly_button").click(function(){
		var data = {
			"section_id":$("#search_anomaly_section_id").val(),
			"section_name":$("#search_anomaly_section_id option:selected").text().trim(),
			"line_id":$("#search_anomaly_line_id").val(),
			"line_name":$("#search_anomaly_line_id option:selected").text().trim(),
			"finish_time_start":$("#search_anomaly_finish_time_start").val(),
			"finish_time_end":$("#search_anomaly_finish_time_end").val()
		}
		
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: servicePath + '?method=anomalyExport', 
			cache: false, 
			data: data, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			complete: anomalyExport_handleComplete
		});
	});
	
	reset();
});


var anomalyExport_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	}else if (resInfo && resInfo.fileName){
		if ($("iframe").length > 0) {
			$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
		} else {
			var iframe = document.createElement("iframe");
			iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
			iframe.style.display = "none";
			document.body.appendChild(iframe);
		}
	}else{
		alert("文件导出失败！"); // TODO dialog
	}
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
		alert("文件导出失败！"); // TODO dialog
	}
};


var reset = function(){
	$("#searchform input[type='text'],#searchform input[type='hidden']").val("").data("post","");
	$("#searchform select").val("").trigger("change");
	$("#scheduled_expedited_all,#rework_y,#abnormal_y").attr("checked","checked").trigger("change");
	$("#exportbutton").disable();
	$("#container").html("");
	
};

var find = function(){
	var data = {
		"category_id":$("#search_category_id").data("post"),
		"category_name":$("#hidden_category_name").data("post"),
		"model_id":$("#hidden_model_id").data("post"),
		"model_name":$("#search_model_name").data("post"),
		"level":$("#search_level").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"position_id":$("#hidden_position_id").data("post"),
		"process_code":$("#hidden_process_code").data("post"),
		"scheduled_expedited":$("#search_scheduled_expedited").data("post"),
		"rework":$("#search_rework").data("post"),
		"operator_id":$("#hidden_operator_id").data("post"),
		"finish_time_start":$("#search_finish_time_start").data("post"),
		"finish_time_end":$("#search_finish_time_end").data("post"),
		"abnormal":$("#search_abnormal").data("post")
	};
	
	if(!data.position_id && !data.operator_id){
		warningConfirm("建议填写工位或者人员任意一项，进行精确查询。<br>是否用当前条件进行粗略查询？",function(){
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
		});
	}else{
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
	}
};

var search_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	}else{
		if(resInfo.avgWorkTimeList.length===0){
			$("#container").html('<div style="width:100px;margin:200px auto;font-size:20px;">无数据</div>');
			$("#exportbutton").disable();
		}else{
			$("#container").html("");
			showChart(resInfo);
			$("#exportbutton").enable();
		}
	}
};


function showChart(resInfo){
	chart = new Highcharts.Chart({
		chart:{
			renderTo : 'container',
			borderRadius:0//边框圆角
		},
		title:{
			text:'工时分析',
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
			tickPosition:'inside'
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
			line:{
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
			name:'实际平均用时',
			color:'#40D079',
			marker:{
	    		 symbol:'diamond'
	    	},
			data:resInfo.avgWorkTimeList
		},{
			name:'标准工时',
			color:'#F9453D',
			data:resInfo.standardWorkTimeList
		}],
		exporting:{
			enabled:false
		}
	});
};
