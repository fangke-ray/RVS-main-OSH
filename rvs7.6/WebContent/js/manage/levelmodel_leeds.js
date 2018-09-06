/** 一览数据对象 */
var listdata = {};
var levelmodel_leeds_listdata = {};
/** 服务器处理路径 */
var servicePath = "levelmodel_leeds.do";

$(function(){
	$("input.ui-button").button();
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	
	$("#echelon").select2Buttons();
	setReferChooser($("#search_mode_id"));
	$("#search_level_id").select2Buttons();
	$("#forecast_warn").buttonset();
	
	//拉动台数预警
	$("input[name=warn]").bind('click',function(){
		$("#warn_template").val($(this).val());
	});
	
	/*重置按钮*/
	$("#resetbutton").click(function(){
		reset();
	});
	
	$("input[type='radio']").click(function(){
		 $("#warn_template").val($(this).val());
	});
	
	/*检索按钮*/
	$("#searchbutton").click(function(){
		$("#echelon").data("post",	$("#echelon").val());
		$("#search_mode_id").data("post",$("#search_mode_id").val());
		$("#search_level_id").data("post",$("#search_level_id").val());
		findit();
	});
	
	/*上传拉动台数设定*/
	$("#upload_button").click(function(){
		import_level_model_set();
	});
	
	/*下载编辑拉动台数*/
	$("#download_button").click(function(){
		download_level_model_set();
	});
	
	reset();
	findit();
});

/*清除*/
var reset=function(){
	$("#echelon").val("").trigger("change").data("post", "");
	$("#search_mode_id").val("").data("post", "").prev().val("");
	$("#search_level_id").val("").trigger("change").data("post", "");
	$("#warn_template").val("");
	$("#forecast_a").attr("checked","checked").trigger("change");
};

var findit=function(){
	var data={
		"echelon":$("#echelon").data("post"),
		"model_id":$("#search_mode_id").data("post"),
		"level":$("#search_level_id").data("post"),
		"warn":$("#warn_template").val()
	}
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
			complete : search_complete
	});
};

var search_complete=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			levelmodel_leeds_list(resInfo.responseFormList);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/* 型号等级拉动台数一览 */
function levelmodel_leeds_list(listdata){
	if ($("#gbox_levelmodel_leeds_list").length > 0) {
		$("#levelmodel_leeds_list").jqGrid().clearGridData();
		$("#levelmodel_leeds_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#levelmodel_leeds_list").jqGrid({
			data:listdata,
			height: 470,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','梯队','型号','等级','拉动台数计算结果','拉动台数当前设置'],
			colModel:[
				{
					name:'model_id',
				    index:'model_id',
					hidden:true
				},
				{
					name:'echelon',
					index:'echelon',
					width:20,
					align:'center',
					formatter : 'select',
					editoptions : {
						value : $("#goEchelon_code").val()
					}
				},
				{
					name:'model_name',
					index:'model_name',
					width:35,
					align:'left'
				},
				{
					name:'level',
					index:'level',
					width:10,
					align:'center',
					formatter : 'select',
					editoptions : {
						value : $("#goMaterial_level_inline").val()
					}
				},
				{
					name:'forecast_result',
					index:'forecast_result',
					width:20,
					align:'right',
					sorttype:'integer',
					formatter:'integer',
					formatoptions:{suffix:' 台',defaultValue: ''}
				},
				{
					name:'forecast_setting',
					index:'forecast_setting',
					align:'right',
					width:20,
					formatter:'integer',
					sorttype:'integer',
					formatoptions:{suffix:' 台',defaultValue: ''}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#levelmodel_leeds_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			ondblClickRow:function(rowid,iRow,iCol,e){
				var rowID = $("#levelmodel_leeds_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
				var rowData=$("#levelmodel_leeds_list").getRowData(rowID);
				edit_patrial(rowData);
			}
		});
	}
};

/*上传拉动台数设定*/
var import_level_model_set=function(){
	var this_dialog = $("#upload_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='upload_dialog'/>");
		this_dialog = $("#upload_dialog");
	}
	this_dialog.html("<input name='file' id='level_model_file' type='file'/>");
	this_dialog.dialog({
			title : "选择上传文件",
			position :[500, 400],	
			modal : true,
			width:300,
			minHeight : 200,
			resizable : false,
			buttons : {
				"上传":function(){
					this_dialog.dialog('close');
					uploadfile();
				},
				"关闭":function(){
					this_dialog.html("");
					this_dialog.dialog('close');
				}
			}
	});
};

var uploadfile=function(){
	$.ajaxFileUpload({
		url:'upload.do?method=doUploadForecastSetting', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'level_model_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success:function(responseText, textStatus){//服务器成功响应处理函数
			var resInfo = null;
			try{
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}else{
					findit();
					treatBackMessages(null, resInfo.info);
				}
			}catch(e){
			}
		}
	});
};

var download_level_model_set=function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=reportForecastResult',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
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
				alert("文件导出失败！"); 
			}
		}
	});
}


var edit_patrial=function(rowObject){
	var path="widgets/manage/levelmodel_leeds_edit.jsp";
	$("#edit_levelmodel_leeds").load(path,function(responseText, textStatus, XMLHttpRequest){
		
		$("#edit_levelmodel_leeds").hide();
		
		var $table = $("#calculate table").eq(0);
		var content = "";
		for(var i = 0;i < 6;i++){
			content += '<tr>'+
							'<td class="td-content">'+
								'<input  class="ui-widget-content" readonly="readonly" type="text">'+
							'</td>'+
							'<td class="td-content">'+
								'<input class="ui-widget-content" readonly="readonly" type="text">'+
							'</td>'+
							'<td class="td-content position"></td>'+
							'<td class="td-content position"></td>'+
							'<td class="td-content position"></td>'+
							'<td class="td-content position"></td>'+
						'</tr>';
		}
		$table.find("tbody").append(content);
		
		var data={
			"model_id":rowObject.model_id,
			"level":rowObject.level,
			"echelon":rowObject.echelon
		};
		
		$.ajax({
			beforeSend:ajaxRequestType,
			async:true,
			url:servicePath+"?method=detail",
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : detail_success
		});
		
		$("input.ui-button").button();
		/* radio转换成按钮 */
		$("#simple_tyle").buttonset();
		
		$("#calculate table").eq(0).find("input").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
		
		//覆盖率
		$("#label_coverage").keydown(function(evt){
			if (!((evt.keyCode >= 48 && evt.keyCode <= 57) ||  evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39 || evt.keyCode==190)){
				return false;
			}
		});
		
		/**基本信息**/
		//型号
		$("#label_model_name").text(rowObject.model_name);
		var level_name;//等级
		if(rowObject.level==1){
			level_name="S1";
		}else if(rowObject.level==2){
			level_name="S2";
		}else if(rowObject.level==3){
			level_name="S3";
		}
		
		//等级
		$("#label_level").text(level_name);
		
		//梯队
		var echelon_name;
		if(rowObject.echelon==1){
			echelon_name="第一梯队";
		}else if(rowObject.echelon==2){
			echelon_name="第二梯队";
		}else if(rowObject.echelon==3){
			echelon_name="第三梯队";
		}else if(rowObject.echelon==4){
			echelon_name="第四梯队";
		}
		$("#label_echelon").text(echelon_name);
		$("#label_forecast_result").text(rowObject.forecast_result.substring(0,rowObject.forecast_result.length-1));
		$("#input_forecast_setting").val(rowObject.forecast_setting.substring(0,rowObject.forecast_setting.length-1));
		
		/*计算*/
		$("#calculated").click(function(){
			var data = {
				"model_id":rowObject.model_id,
				"level":rowObject.level,
				"echelon":rowObject.echelon,
				"coverage":$("#label_coverage").val().trim(),
				"coefficient_of_variation":$("#label_coefficient_of_variation").text().trim()
			};
			
			var i=0;
			$("#calculate table").eq(0).find("tr:nth-child(n+2)").each(function(index,ele){
				var $tr = $(ele);
				
				var start_date = $tr.find("td").eq(0).find("input").val();
				var end_date = $tr.find("td").eq(1).find("input").val();
				if(start_date!="" && end_date!=""){
					data["forecast_sampling_record.start_date[" + i + "]"] = start_date.trim();
					data["forecast_sampling_record.end_date[" + i + "]"] = end_date.trim();
					data["forecast_sampling_record.model_id[" + i + "]"] = rowObject.model_id,
					data["forecast_sampling_record.level[" + i + "]"] = rowObject.level,
					i++;
				}
			});
			
			$.ajax({
				beforeSend:ajaxRequestType,
				async:true,
				url:servicePath+"?method=calculate",
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : calculate_success
			});
		});
		
		
		var calculate_success=function(xhrobj,textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("", resInfo.errors);
				} else {
					var responseFormList = resInfo.responseFormList;
					if(responseFormList!=null){
						var obj;
						var agree_percentage;
						
						var $table = $("#calculate table").eq(0);
						
						
						for(var i=0;i<responseFormList.length;i++){
							obj=responseFormList[i];
							var index=i+1;
							
							$table.find("tr").eq(index).find("td").eq(0).find("input").val(obj.start_date);
							$table.find("tr").eq(index).find("td").eq(1).find("input").val(obj.end_date);
							$table.find("tr").eq(index).find("td").eq(2).text(obj.sampling_recept_count);
							$table.find("tr").eq(index).find("td").eq(3).text(obj.sampling_agree_count);
							
							if(obj.sampling_recept_count==null||parseInt(obj.sampling_recept_count)==0||obj.sampling_recept_count==""){
								agree_percentage="";
							}else{
								agree_percentage=(obj.sampling_agree_count/obj.sampling_recept_count).toFixed(2)*100+"%";
							}
							$table.find("tr").eq(index).find("td").eq(4).text(agree_percentage);
							$table.find("tr").eq(index).find("td").eq(5).text(obj.order_count_of_period);
						}
						
						$("#label_forecast_result_calculate").text(resInfo.forecastResult);//拉动台数计算结果
						$("#label_forecast_result").text(resInfo.forecastResult);
					}
				}
			}catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
		
		$("#base_value").click(function(){
			$("#base").show();
			$("#sample").hide();
		});

		$("#sample_value").click(function(){
			$("#sample").show();
			$("#base").hide();
		});
		
		$("#edit_levelmodel_leeds").dialog({
			title:'拉动台数计算',
			width:980,
			height:'auto',
			modal:true,
			resizable: false,
			buttons:{
				"修改相关零件基准值":function(){
					 window.location.href = "partial_base_line_value.do";
				},
				"确认":function(){
					 update_forecast_setting(rowObject.model_id,rowObject.level,$("#input_forecast_setting").val(),$("#label_coverage").val());
					 $("#edit_levelmodel_leeds").dialog("close");
				},
				"取消":function(){
					 $("#edit_levelmodel_leeds").dialog("close");
				}
			}
		});
	});
	
	
	var update_forecast_setting=function(model_id,level,forecast_setting,coverage){
		var data={
			"model_id":model_id,
			"level":level,
			"forecast_setting":forecast_setting,
			"coverage":coverage
		};
		 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doUpdateFrecastSetting',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : update_complete
		});
	};
	
	var update_complete=function(xhrobj,textStatus){
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("", resInfo.errors);
			} else {
				findit();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
		};
	};
	
	var detail_success=function(xhrobj,textStatus){
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("", resInfo.errors);
			} else {
				chart01 = new Highcharts.Chart({
					colors :['#CCCCCC', '#a0dbea'],
					chart:{
						renderTo: 'container',
						alignTicks:false
					},
					title:{
						text:null
					},
					tooltip:{
						enabled: true,
						labels:{
							style:{
								fontSize:'12px',
								color:'black'
							}
						}
					},
					dataLabels: {
						color:'black'
					},
					plotOptions:{
						line:{
							connectNulls:true,
							animation:true
						}
					},
					legend:{
						borderWidth:0,
						align:"center",
						verticalAlign:"top"
					},
					credits:{
						enabled:false
					},
					xAxis:{
						categories:resInfo.responseMap.axisTextList,
						labels:{
							style:{
								fontSize:'12px',
								color:'black'
							}
						},
						lineWidth:1,
						lineColor:'#000000',
						gridLineWidth:1,
						tickColor:'#000000',
						tickPosition:'inside'
					},
					yAxis: {
						min:0,
						title:{
							text:null
						},
						labels:{
							style:{
								fontSize:'12px',
								color:'black'
							}
						},
						tickPosition:'inside',
						lineWidth:1,
						lineColor:'#000000',
						gridLineWidth:0,
						tickWidth:1,
						tickLength:5,
						tickColor:'#000000'
					},
					series:[{
						name:'平均同意数',
						type:'line',
						data:resInfo.responseMap.list_average_agree
					}, {
						name:'设置拉动台数',
						type:'line',
						data:resInfo.responseMap.list_forecast_setting
					}]
				});
				
				var responseFormList = resInfo.responseFormList;
				if(responseFormList!=null){
					var obj;
					var agree_percentage;
					
					var $table = $("#calculate table").eq(0);
					
					
					for(var i=0;i<responseFormList.length;i++){
						obj=responseFormList[i];
						var index=i+1;
						
						$table.find("tr").eq(index).find("td").eq(0).find("input").val(obj.start_date);
						$table.find("tr").eq(index).find("td").eq(1).find("input").val(obj.end_date);
						$table.find("tr").eq(index).find("td").eq(2).text(obj.sampling_recept_count);
						$table.find("tr").eq(index).find("td").eq(3).text(obj.sampling_agree_count);
						
						if(obj.sampling_recept_count==null||parseInt(obj.sampling_recept_count)==0||obj.sampling_recept_count==""){
							agree_percentage="";
						}else{
							agree_percentage=(obj.sampling_agree_count/obj.sampling_recept_count).toFixed(2)*100+"%";
						}
						$table.find("tr").eq(index).find("td").eq(4).text(agree_percentage);
						$table.find("tr").eq(index).find("td").eq(5).text(obj.order_count_of_period);
					}
				}
				
				if(resInfo.coefficientOfVariation) $("#label_coefficient_of_variation").text(resInfo.coefficientOfVariation.toFixed(2));//波动系数
				$("#label_forecast_result_calculate").text(resInfo.forecastResult);//拉动台数计算结果
				$("#label_coverage").val(resInfo.coverage);//覆盖率
				
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
		};
	};
}
