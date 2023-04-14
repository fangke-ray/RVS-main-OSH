var servicePath="waitting_time_report.do";
var chart1 = null;
var chart2 = null;
var chart3 = null;
var chart4 = null;
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
    
    $("#search_bo_flg option[value='1'],#search_bo_flg option[value='9']").remove();
    
    $("#search_dec_px,#search_ns_px,#search_com_px").find("option[value='3']").remove();
    $("#search_dec_px,#search_ns_px").find("option[value='4']").remove();

    $("#search_category_id,#search_level,#search_section_id,#search_direct_flg,#search_bo_flg," +
    		"#search_dec_px,#search_ns_px,#search_com_px").select2Buttons();
    setReferChooser($("#hidden_model_id"),$("#search_model_id_referchooser"));
    
    $("#search_rework,#search_scheduled_expedited").buttonset();
    
    $("#search_outline_time_start,#search_outline_time_end,#search_order_date_start,#search_order_date_end,#search_arrival_date_start,#search_arrival_date_end").datepicker({
		minDate:'2015/01/01',
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
		$("#search_omr_notifi_no").data("post", $("#search_omr_notifi_no").val());
		$("#search_serial_no").data("post", $("#search_serial_no").val());
		$("#hidden_model_id").data("post", $("#hidden_model_id").val());
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_level").data("post", $("#search_level").val());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#search_section_name").data("post", $("#search_section_id option:selected").text().trim());
		$("#search_bo_flg").data("post", $("#search_bo_flg").val());
		$("#search_scheduled_expedited").data("post", $("#search_scheduled_expedited").find("input[type='radio']:checked").val());
		$("#search_rework").data("post", $("#search_rework").find("input[type='radio']:checked").val());
		$("#search_direct_flg").data("post", $("#search_direct_flg").val());
		$("#search_outline_time_start").data("post", $("#search_outline_time_start").val());
		$("#search_outline_time_end").data("post", $("#search_outline_time_end").val());
		$("#search_order_date_start").data("post", $("#search_order_date_start").val());
		$("#search_order_date_end").data("post", $("#search_order_date_end").val());
		$("#search_arrival_date_start").data("post", $("#search_arrival_date_start").val());
		$("#search_arrival_date_end").data("post", $("#search_arrival_date_end").val());
		$("#search_dec_px").data("post", $("#search_dec_px").val());
		$("#search_ns_px").data("post", $("#search_ns_px").val());
		$("#search_com_px").data("post", $("#search_com_px").val());
		
		findit();
	});
	
	$("#exportbutton").click(function(){
		//作业时间/等待时间
		var options1 = {
		};
		options1.exporting = {};
		options1.exporting.sourceWidth = 800;
		options1.exporting.sourceHeight = 300;
		
		//中断时间
		var options2 = {
		};
		options2.exporting = {};
		options2.exporting.sourceWidth = 200;
		options2.exporting.sourceHeight = 300;
		
		var svg1 = "";
		var svg2 = "";
		var svg3 = "";
		var svg4 = "";
		
		if(!$.isEmptyObject(chart1)){
			var wrap = $("<span></span>");
			wrap.append($(chart1.getSVG(options1)));
			wrap.find("g.highcharts-tooltip").remove();
			svg1 = wrap.html();
		}
		if(!$.isEmptyObject(chart2)){
			var wrap = $("<span></span>");
			wrap.append($(chart2.getSVG(options2)));
			wrap.find("g.highcharts-tooltip").remove();
			svg2 = wrap.html();
		}
		if(!$.isEmptyObject(chart3)){
			var wrap = $("<span></span>");
			wrap.append($(chart3.getSVG(options1)));
			wrap.find("g.highcharts-tooltip").remove();
			svg3 = wrap.html();
		}
		if(!$.isEmptyObject(chart4)){
			var wrap = $("<span></span>");
			wrap.append($(chart4.getSVG(options2)));
			wrap.find("g.highcharts-tooltip").remove();
			svg4 = wrap.html();
		}
		
		var data = {
			"category_name":$("#hidden_category_name").data("post"),
			"omr_notifi_no": $("#search_omr_notifi_no").data("post"),
			"serial_no":$("#search_serial_no").data("post"),
			"model_name":$("#search_model_name").data("post"),
			"level":$("#search_level").data("post"),
			"section_name":$("#search_section_name").data("post"),
			"bo_flg":$("#search_bo_flg").data("post"),
			"scheduled_expedited":$("#search_scheduled_expedited").data("post"),
			"rework":$("#search_rework").data("post"),
			"direct_flg":$("#search_direct_flg").data("post"),
			"outline_time_start":$("#search_outline_time_start").data("post"),
			"outline_time_end":$("#search_outline_time_end").data("post"),
			"order_date_start":$("#search_order_date_start").data("post"),
			"order_date_end":$("#search_order_date_end").data("post"),
			"arrival_date_start":$("#search_arrival_date_start").data("post"),
			"arrival_date_end":$("#search_arrival_date_end").data("post"),
			"svg1":svg1,
			"svg2":svg2,
			"svg3":svg3,
			"svg4":svg4,
			"dec_px":$("#search_dec_px").data("post"),
			"ns_px":$("#search_ns_px").data("post"),
			"com_px":$("#search_com_px").data("post")
			
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

	$("#boldbutton").click(function(){

		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: servicePath + '?method=exportBold', 
			cache: false, 
			data: null, 
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
	$("#rework_y,#scheduled_expedited_all").attr("checked","checked").trigger("change");
	
	$("#exportbutton,#boldbutton").disable();
	$("#container1,#container3").html("").css("width","780px");
	$("#container2,#container4").html("").show();
};

var findit = function(){
	var data = {
		"category_id":$("#search_category_id").data("post"),
		"omr_notifi_no":$("#search_omr_notifi_no").data("post"),
		"serial_no":$("#search_serial_no").data("post"),
		"model_id":$("#hidden_model_id").data("post"),
		"level":$("#search_level").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"bo_flg":$("#search_bo_flg").data("post"),		
		"scheduled_expedited":$("#search_scheduled_expedited").data("post"),
		"rework":$("#search_rework").data("post"),
		"direct_flg":$("#search_direct_flg").data("post"),
		"outline_time_start":$("#search_outline_time_start").data("post"),
		"outline_time_end":$("#search_outline_time_end").data("post"),
		"order_date_start":$("#search_order_date_start").data("post"),
		"order_date_end":$("#search_order_date_end").data("post"),
		"arrival_date_start":$("#search_arrival_date_start").data("post"),
		"arrival_date_end":$("#search_arrival_date_end").data("post"),
		"dec_px":$("#search_dec_px").data("post"),
		"ns_px":$("#search_ns_px").data("post"),
		"com_px":$("#search_com_px").data("post")
	};

	var searchUrl = servicePath + '?method=search';
	if ($("#container1").length == 0) {
		searchUrl = servicePath + '?method=searchIds';
	}

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: searchUrl, 
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
		if (resInfo.listSize) {
			$("#listcount1").text("符合条件的修理对象为" + resInfo.listSize + "件，请点击【BOLD 统计列表】下载详细列表。");
			$("#boldbutton").enable();
			return;
		} else if (resInfo.listSize == 0) {
			$("#listcount1").text("没有符合条件的修理对象，请更改条件重新查询。");
			$("#boldbutton").disable();
		}

		if(!$.isEmptyObject(chart1)) chart1 = chart1.destroy();
		if(!$.isEmptyObject(chart2)) chart2 = chart2.destroy();
		if(!$.isEmptyObject(chart3)) chart3 = chart3.destroy();
		if(!$.isEmptyObject(chart4)) chart4 = chart4.destroy();

		if(resInfo.S1num == 0){
			$("#container1").html('<div style="width:100px;margin:100px auto;font-size:20px;">S1无数据</div>').css("width","992px");
			$("#container2").html("").hide();
		}else{
			$("#container1").html("").css("width","780px");
			$("#container2").html("").show();
			showS1Chart(resInfo);
		}
		
		if(resInfo.S2S3num == 0){
			$("#container3").html('<div style="width:110px;margin:100px auto;font-size:20px;">S2S3无数据</div>').css("width","992px");
			$("#container4").html("").hide();
		}else{
			$("#container3").html("").css("width","780px");
			$("#container4").html("").show();
			showS2S3Chart(resInfo);
		}
		
		if(resInfo.S1num == 0 && resInfo.S2S3num == 0){
			$("#exportbutton,#boldbutton").disable();
		}else{
			$("#exportbutton,#boldbutton").enable();
		}
	}
};

function showS1Chart(resInfo){
	chart1 = new Highcharts.Chart({
		chart:{
			type: 'bar',
			renderTo : 'container1',
			borderRadius:0//边框圆角
		},
		title: {
            text: 'S1作业时间/等待时间',
            style: {
                color: '#000',
                fontWeight: 'bold'
            }
        },
        xAxis: {
            categories: ['',''],
            lineColor:'#000000',//轴线颜色
			tickPosition:'inside'//刻度方向
        },
        yAxis: {
            title: {
                text: '分钟',
                style: {
	                color: '#000'
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
			gridLineDashStyle:'solid',
			tickWidth:1,
			tickLength:5,
			tickColor:'#000000',
			tickPosition:'inside',
			allowDecimals:false
        },
        plotOptions: {
            bar:{
                pointPadding:-1/3,
                dataLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        fontSize : '13px',
                        color: '#343264'
                    },
                    formatter : function() {
						return this.y + "分钟";
					}
                 }
            },
            series: {
                stacking: 'normal'
            }
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
        credits:{
			enabled:false
		},
		exporting:{
			enabled:false
		},
		legend: {
            reversed: true,
            borderWidth:1,
			itemStyle:{
				fontSize: '12px'
			},
			itemDistance:10
        },
        series:[{
        	name:'总组等待时间',
        	color:'#FF9966',
        	data:eval(resInfo.series1_5)
        },{
        	name:'总组烘干时间',
        	color:'#FF9966',
        	borderWidth:2,
        	data:eval(resInfo.series1_7)
        },{
        	name:'总组作业时间',
        	color:'#FF9966',
        	data:eval(resInfo.series1_4)
        },{
        	name:'分解等待时间',
        	color:'#339999',
        	data:eval(resInfo.series1_3)
        },{
        	name:'分解烘干时间',
        	color:'#339999',
        	borderWidth:2,
        	data:eval(resInfo.series1_6)
        },{
        	name:'分解作业时间',
        	color:'#339999',
        	data:eval(resInfo.series1_2)
        },{
        	name:'等待零件发放时间',
        	color:'#99CCFF',
        	data:eval(resInfo.series1_1)
        }]
	});
	
	chart2 = new Highcharts.Chart({
		chart:{
			type:'column',
			renderTo : 'container2',
			borderRadius:0//边框圆角
		},
		title:{
			text:'S1中断时间',
			style: {
                color: '#000',
                fontWeight: 'bold'
            }
		},
		xAxis:{
			categories:[''],
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
			column:{
				stacking: 'normal',
				dataLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        fontSize : '13px',
                        color: '#343264'
                    },
                    formatter : function() {
                    	var total_work_time = resInfo.series2_3;
                    	if(total_work_time == 0){
                    		return "0%";
                    	}else{
                    		return (this.y / total_work_time * 100).toFixed() + "%"
                    	}
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
		series:[{
			name:'等待BO零件时间',
			color:'#40D079',
			data:eval(resInfo.series2_1)
		},{
			name:'异常中断时间',
			color:'#F9453D',
			data:eval(resInfo.series2_2)
		}],
		exporting:{
			enabled:false
		}
	});
};

function showS2S3Chart(resInfo){
	chart3 = new Highcharts.Chart({
		chart:{
			type: 'bar',
			renderTo : 'container3',
			borderRadius:0//边框圆角
		},
		title: {
            text: 'S2+S3作业时间/等待时间',
            style: {
                color: '#000',
                fontWeight: 'bold'
            }
        },
        xAxis: {
            categories: ['',''],
            lineColor:'#000000',//轴线颜色
			tickPosition:'inside'//刻度方向
        },
        yAxis: {
            title: {
                text: '分钟',
                style: {
	                color: '#000'
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
			gridLineDashStyle:'solid',
			tickWidth:1,
			tickLength:5,
			tickColor:'#000000',
			tickPosition:'inside',
			allowDecimals:false
        },
        plotOptions: {
            bar:{
                pointPadding:-1/3,
                dataLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        fontSize : '13px',
                        color: '#343264'
                    },
                    formatter : function() {
						return this.y + "分钟";
					}
                 }
            },
            series: {
                stacking: 'normal',
                borderWidth:0
            }
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
        credits:{
			enabled:false
		},
		exporting:{
			enabled:false
		},
		legend: {
            reversed: true,
            borderWidth:1,
			itemStyle:{
				fontSize: '12px'
			},
			itemWidth:140
        },
        series:[{
        	name:'总组等待时间',
        	color:'#FF9966',
        	data:eval(resInfo.series3_7)
        },{
        	name:'总组烘干时间',
        	color:'#FF9966',
        	borderWidth:2,
        	data:eval(resInfo.series3_10)
        },{
        	name:'总组作业时间',
        	color:'#FF9966',
        	data:eval(resInfo.series3_6)
        },{
        	name:'NS等待时间',
        	color:'#9933CC',
        	data:eval(resInfo.series3_5)
        },{
        	name:'分解烘干时间',
        	color:'#9933CC',
        	borderWidth:2,
        	data:eval(resInfo.series3_9)
        },{
        	name:'NS作业时间',
        	color:'#9933CC',
        	data:eval(resInfo.series3_4)
        },{
        	name:'分解等待时间',
        	color:'#339999',
        	data:eval(resInfo.series3_3)
        },{
        	name:'分解烘干时间',
        	color:'#339999',
        	borderWidth:2,
        	data:eval(resInfo.series3_8)
        },{
        	name:'分解作业时间',
        	color:'#339999',
        	data:eval(resInfo.series3_2)
        },{
        	name:'等待零件发放时间',
        	color:'#99CCFF',
        	data:eval(resInfo.series3_1)
        }]
	});
	
	chart4 = new Highcharts.Chart({
		chart:{
			type:'column',
			renderTo : 'container4',
			borderRadius:0//边框圆角
		},
		title:{
			text:'S2+S3中断时间',
			style: {
                color: '#000',
                fontWeight: 'bold'
            }
		},
		xAxis:{
			categories:[''],
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
			column:{
				stacking: 'normal',
				dataLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        fontSize : '13px',
                        color: '#343264'
                    },
                    formatter : function() {
                    	var total_work_time = resInfo.series4_3;
                    	if(total_work_time == 0){
                    		return "0%";
                    	}else{
                    		return (this.y / total_work_time * 100).toFixed() + "%"
                    	}
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
		series:[{
			name:'等待BO零件时间',
			color:'#40D079',
			data:eval(resInfo.series4_1)
		},{
			name:'异常中断时间',
			color:'#F9453D',
			data:eval(resInfo.series4_2)
		}],
		exporting:{
			enabled:false
		}
	});
}


