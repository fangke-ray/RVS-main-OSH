var servicePath="productivity.do";
var chart2 = null;

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

	var today = new Date();
    $("#search_start_date,#search_end_date").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});
	
	$("#search_section_id").select2Buttons();
	
	$("#search_work").buttonset();
	
	$("#resetbutton").click(function(){
		reset();
	});

	$("#searchbutton").click(function(){
		$("#search_start_date").data("data",$("#search_start_date").val());
		$("#search_end_date").data("data",$("#search_end_date").val());
		findit();
	});

	/*验证*/
	$("#editform").validate({
		rules:{
			avalible_productive:{
				required:true,
				number:true
			}
		}
	});

	$("#updatebutton").click(function(){
		if ($("#editform").valid()) {
			doUpdate();
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
			"start_date":$("#search_start_date").data("data"),
			"end_date":$("#search_end_date").data("data"),
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
	$("#label_outline_date").text("");
	$("#edit_avalible_productive").val("");
	$("#updatebutton, #exportbutton").disable();
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
		"start_date":$("#search_start_date").data("data"),
		"end_date":$("#search_end_date").data("data")
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
		if (resInfo.outlineQuantityList.length === 0) {
			$("#container").html('<div style="width:100px;margin:200px auto;font-size:20px;">无数据</div>');
			$("#exportbutton").disable();
		} else {
			$("#label_outline_date").text("");
			$("#edit_avalible_productive").val("");
			$("#updatebutton").disable();
			$("#container").html("");
			showChart(resInfo);
			$("#exportbutton").enable();
		}
	}
};

var outlineDateFilter = function(outline_date) {
	$("#hidden_outline_date").val(outline_date);
	$("#label_outline_date").text(outline_date.substring(0,4) + "年" + outline_date.substring(4,6) + "月");
	$("#updatebutton").enable();
};

function showChart(resInfo) {
	if (chart2 == null) {
		chart2 = new Highcharts.Chart({
			chart:{
				renderTo : 'container',
				type : 'column',
				borderRadius:0//边框圆角
			},
			title:{
				text:'人均产出台数',
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
			yAxis:[{
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
				tickPosition:'inside'
			},{
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
				opposite:true
			}],
			tooltip:{
				headerFormat:'<span style="font-size:10px">{point.key}</span><table>',
				pointFormat:'<tr>'+
								'<td style="color:{series.color};padding:0">{series.name}:</td>' +
								'<td style="padding:0"><b>{point.y} </b></td>' +
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
						enabled:true
					}
				},
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
			series:[
			{
				name:'产出量(台)',
				color:'#8FCDFF',
				data:resInfo.outlineQuantityList
			},{
				name:'人均完成台数',
				color:'#40D079',
				type:'line',
				yAxis:1,
				data:resInfo.workTimeList
			}],
			exporting:{
				enabled:false
			}
		});
	} else {
		chart2.xAxis[0].setCategories(resInfo.xAxisList, false);
		chart2.series[0].setData(resInfo.outlineQuantityList, false);
		chart2.series[1].setData(resInfo.workTimeList);
	}
	
	$("#container g.highcharts-axis-labels.highcharts-xaxis-labels text").each(function(index,ele){
		var $text = $(ele);
		var year = $text.find("tspan").eq(0).text().substring(0,4);
		var month = $text.find("tspan").eq(1).text().substring(0,2);
		
		$text.off("click").on("click",function(){
			outlineDateFilter(year + month);
		});
	});
};

var update_handleComplete = function(xhrObj) {
	var resInfo = null;
	eval("resInfo=" + xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	} else {
		findit();
	}
};

var doUpdate = function() {
	var data = {
		"outline_date":$("#hidden_outline_date").val(),
		"avalible_productive":$("#edit_avalible_productive").val()
	};

	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=doUpdate', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: update_handleComplete
	});
};
