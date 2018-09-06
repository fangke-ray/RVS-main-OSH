var servicePath="foundry.do";
var chart1 = null;
var chart2 = null;

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
    
    
    $("#search_line_id,#search_section_id").select2Buttons();
    
    $("#search_finish_time_start,#search_finish_time_end").datepicker({
    	minDate : "2016/12/01",
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	
	var today = new Date();
    $("#monthbutton").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
		callback:function(e, month, year){
			if(month < 10){
				month = "0" + month;
			}
			$("#search_finish_time_start").val(year + "/" + month + "/01");
			var day = new Date(year,month,0);
			$("#search_finish_time_end").val(year + "/" + month + "/" + day.getDate());
		}
	});
    
    $("#resetbutton").click(function(){
		reset();
	});
	
	
	$("#searchbutton").click(function(){
		$("#search_line_id").data("post", $("#search_line_id").val());
		$("#search_line_name").data("post", $("#search_line_id option:selected").text().trim());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#search_section_name").data("post", $("#search_section_id option:selected").text().trim());
		$("#search_finish_time_start").data("post", $("#search_finish_time_start").val());
		$("#search_finish_time_end").data("post", $("#search_finish_time_end").val());
		
		find();
	});
	
	$("#exportbutton").click(function(){
		var options = {
		};
		options.exporting = {};
		options.exporting.sourceWidth = 800;
		options.exporting.sourceHeight = 400;
		
		var wrap = $("<span></span>");
		wrap.append($(chart1.getSVG(options)));
		wrap.find("g.highcharts-tooltip").remove();
		
		var wrap2 = $("<span></span>");
		wrap2.append($(chart2.getSVG(options)));
		wrap2.find("g.highcharts-tooltip").remove();
		var data = {
			"line_id":$("#search_line_id").data("post"),
			"line_name":$("#search_line_name").data("post"),
			"section_id":$("#search_section_id").data("post"),
			"section_name":$("#search_section_name").data("post"),
			"finish_time_start":$("#search_finish_time_start").data("post"),
			"finish_time_end":$("#search_finish_time_end").data("post"),
			"svg":wrap.html(),
			"svg2":wrap2.html()
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
	
	reset();
});

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
	$("#exportbutton").disable();
	$("#container").html("");
};

var find = function(){
	var data = {
		"line_id":$("#search_line_id").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"finish_time_start":$("#search_finish_time_start").data("post"),
		"finish_time_end":$("#search_finish_time_end").data("post")
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
		if(resInfo.positionFoundryWorkList.length===0){
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
	chart1 = new Highcharts.Chart({
		chart:{
			renderTo : 'container',
			borderRadius:0,//边框圆角
			type: 'column'
		},
		title:{
			text:'代工时间统计',
			style: {
                color: '#000',
                fontWeight: 'bold'
            }
		},
		xAxis:{
			categories:resInfo.positionXAxisList,
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
			 column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: true,
                    color:'black'
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
			name:'被代工时间',
			color:'#948A54',
			data:resInfo.positionFoundryWorkList
		},{
			name:'本职作业时间',
			color:'#4F81BD',
			data:resInfo.positionMainWorkList
		}],
		exporting:{
			enabled:false
		}
	});
	
	chart2 = new Highcharts.Chart({
		chart:{
			renderTo : 'container2',
			borderRadius:0,//边框圆角
			type: 'column'
		},
		title:{
			text:'代工时间统计',
			style: {
                color: '#000',
                fontWeight: 'bold'
            }
		},
		xAxis:{
			categories:resInfo.operatorXAxisList,
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
			 column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: true,
                    color:'black'
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
			name:'代工时间',
			color:'#948A54',
			data:resInfo.operatorFoundryWorkList
		},{
			name:'本职作业时间',
			color:'#4F81BD',
			data:resInfo.operatorMainWorkList
		}],
		exporting:{
			enabled:false
		}
	});
}
