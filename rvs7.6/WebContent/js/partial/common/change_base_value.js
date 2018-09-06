/** 一览数据对象 */
var listdata = {};

function change_base_line(partial_id,partial_code,supply_date_start,supply_date_end, callback){

	var path="widgets/partial/partial_base_line_value_edit.jsp";
	
	var popServicePath = "partial_base_line_value.do";
	
	var this_dialog = $("#edit_partial_base_line_value");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='edit_partial_base_line_value'/>");
	}
	
	$("#edit_partial_base_line_value").load(path,function(responseText, textStatus, XMLHttpRequest){
		var data={
			"partial_id":partial_id,
			"supply_date_start":supply_date_start,
			"supply_date_end":supply_date_end
		};

		$.ajax({
			beforeSend:ajaxRequestType,
			async:true,
			url: popServicePath + '?method=showPartialDetail',
			cache:false,
			data:data,
			type:"post",
			dataType:"json",
			success:ajaxSuccessCheck,
			error:ajaxError,
			complete:show_partial_detail_complete
		});
		
		$("#base_flg").buttonset();
		
		$("#base_value").click(function(){
			$("#base").show();
			$("#partial_level").hide();
			$("#detail").hide();
		});

		$("#partial_level_value").click(function(){
			$("#partial_level").show();
			$("#base").hide();
			$("#detail").hide();
		});

		$("#detail_value").click(function(){
			$("#detail").show();
			$("#partial_level").hide();
			$("#base").hide();
		});
		
		$("#edit_sorcwh_end_date,#edit_wh2p_end_date,#edit_consumble_end_date,#edit_ogz_end_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
		
		$("#edit_sorcwh_setting,#edit_wh2p_setting,#edit_consumble_setting,#edit_ogz_setting").keydown(function(evt){
			if(!((evt.keyCode >= 48 && evt.keyCode <= 57) || evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39)){
				return false;
			}
		});
		
	
	});
	
	var show_partial_detail_complete=function(xhrobj,textStatus){

		var resInfo = null;
		try{
			eval('resInfo =' + xhrobj.responseText);
			if(resInfo.errors.length > 0){
				// 共通出错信息框
				treatBackMessages("", resInfo.errors);
			}else{
				get_chart_complete(resInfo.returnMap);
				
				//基本Tab
				$("#label_supply_count").text(resInfo.supplyQuantuty);//采样周期内 补充量合计
				if(resInfo.returnBaseValue!=null){
					var sorcwh_foreboard_count = resInfo.returnBaseValue.sorcwh_foreboard_count;
					$("#label_orcwh_foreboard_count").text(sorcwh_foreboard_count);//SORCWH 现行基准值
					$("#edit_sorcwh_setting").val(sorcwh_foreboard_count);//SORCWH 基准值设定
					
					var wh2p_foreboard_count = resInfo.returnBaseValue.wh2p_foreboard_count
					$("#label_wh2p_foreboard_count").text(wh2p_foreboard_count);//WH2P 现行基准值
					$("#edit_wh2p_setting").val(wh2p_foreboard_count);//WH2P 基准值设定
					
					var osh_foreboard_count=parseInt(sorcwh_foreboard_count)+parseInt(wh2p_foreboard_count);
					$("#label_osh_foreboard_count").text(osh_foreboard_count);//OSH 现行基准值
					$("#label_osh_setting").text(osh_foreboard_count);//OSH 基准值设定
					
					var consumable_foreboard_count=resInfo.returnBaseValue.consumable_foreboard_count;
					$("#label_consumable_foreboard_count").text(consumable_foreboard_count);//消耗品库存 现行基准值
					$("#edit_consumble_setting").val(consumable_foreboard_count);//消耗品库存 基准值设定
					
					var ogz_foreboard_count=resInfo.returnBaseValue.ogz_foreboard_count;
					$("#label_ogz_foreboard_count").text(ogz_foreboard_count);//OGZ 现行基准值
					$("#edit_ogz_setting").val(ogz_foreboard_count);//OGZ 基准值设定
				}
				
				//详细Tab  returnHalfYearQuantity
				//半年
				if(resInfo.returnHalfYearQuantity!=null){
					$("#label_echelon_01_halfyear_count").text(resInfo.returnHalfYearQuantity.echelon1OfAverage);
					$("#label_echelon_02_halfyear_count").text(resInfo.returnHalfYearQuantity.echelon2OfAverage);
					$("#label_echelon_03_halfyear_count").text(resInfo.returnHalfYearQuantity.echelon3OfAverage);
					$("#label_echelon_04_halfyear_count").text(resInfo.returnHalfYearQuantity.echelon4OfAverage);
					$("#label_echelon_halfyear_count").text(resInfo.returnHalfYearQuantity.echelonOfAverage);
				}
				
				//三个月
				if(resInfo.returnThreeMonthQuantity!=null){
					$("#label_echelon_01_threemonth_count").text(resInfo.returnThreeMonthQuantity.echelon1OfAverage);
					$("#label_echelon_02_threemonth_count").text(resInfo.returnThreeMonthQuantity.echelon2OfAverage);
					$("#label_echelon_03_threemonth_count").text(resInfo.returnThreeMonthQuantity.echelon3OfAverage);
					$("#label_echelon_04_threemonth_count").text(resInfo.returnThreeMonthQuantity.echelon4OfAverage);
					$("#label_echelon_threemonth_count").text(resInfo.returnThreeMonthQuantity.echelonOfAverage);
				}
				
				//当月
				if(resInfo.retutnCurMonthQuantity!=null){
					$("#label_echelon_01_curmonth_count").text(resInfo.retutnCurMonthQuantity.echelon1OfAverage);
					$("#label_echelon_02_curmonth_count").text(resInfo.retutnCurMonthQuantity.echelon2OfAverage);
					$("#label_echelon_03_curmonth_count").text(resInfo.retutnCurMonthQuantity.echelon3OfAverage);
					$("#label_echelon_04_curmonth_count").text(resInfo.retutnCurMonthQuantity.echelon4OfAverage);
					$("#label_echelon_curmonth_count").text(resInfo.retutnCurMonthQuantity.echelonOfAverage);
				}
			
				
				$("#label_not_stand_use_of_halfyear").text(resInfo.notStandCountOfHalfYear);
				$("#label_not_stand_use_of_threemon").text(resInfo.notStandCountOfThreeMon);
				$("#label_not_stand_use_of_curmon").text(resInfo.notStandCountOfCurMon);
				
				
				var notSafetyCount = resInfo.notSafetyCount;
				$("#lable_non_bom_safty_count").text(notSafetyCount);//设定非标使用量
				
				var totalOfStandardUse = resInfo.totalOfStandardUse;
				$("#lable_total_of_standard_use").text(totalOfStandardUse);//标配拉动数合计
				
				$("#use_count").text(parseInt(notSafetyCount==null ? 0 : notSafetyCount)+parseInt(totalOfStandardUse==null ? 0 :totalOfStandardUse));//本周期使用量合计
				
				$("#lable_current_average_count").text(parseInt(notSafetyCount==null ? 0 :notSafetyCount)+parseInt(totalOfStandardUse==null ? 0 : totalOfStandardUse));//采样周期内 使用量合计
				
				partial_base_line_value_list2(resInfo.returnForms);
				
				
				var countOfHalfyear=resInfo.returnHalfYearQuantity==null ? 0 : resInfo.returnHalfYearQuantity.echelonOfAverage;
				var countOfThreeMon=resInfo.returnThreeMonthQuantity==null ? 0 : resInfo.returnThreeMonthQuantity.echelonOfAverage;
				var countOfOneMon=resInfo.retutnCurMonthQuantity==null ? 0 : resInfo.retutnCurMonthQuantity.echelonOfAverage;
				if(resInfo.returnHalfYearQuantity!=null){
					$("#lable_cycle_average_count").text("半年:"+countOfHalfyear+
													 "件/3个月:"+countOfThreeMon+
													 "件/1个月:"+countOfOneMon);
				}
				
				$("#edit_partial_base_line_value").dialog({
					title:'零件 '+partial_code+' 基准值计算 当前采样周期：'+supply_date_start.substring(5,supply_date_start.length)+" ~ "+supply_date_end.substring(5,supply_date_start.length),
					width:1050,
					height:460,
					resizable:false,
					modal:true,
					buttons:{
						"确认":function(){
							var data={
								"partial_id":partial_id,
								"sorcwh_foreboard_count":$("#label_orcwh_foreboard_count").text()==$("#edit_sorcwh_setting").val() ? null :$("#edit_sorcwh_setting").val(),
								"wh2p_foreboard_count":$("#label_wh2p_foreboard_count").text()==$("#edit_wh2p_setting").val() ? null : $("#edit_wh2p_setting").val(),
								"consumable_foreboard_count":$("#label_consumable_foreboard_count").text()==$("#edit_consumble_setting").val() ? null : $("#edit_consumble_setting").val(),
								"ogz_foreboard_count":$("#label_ogz_foreboard_count").text()==$("#edit_ogz_setting").val() ? null : $("#edit_ogz_setting").val(),
								"sorcwh_end_date":	$("#edit_sorcwh_end_date").val(),
								"wh2p_end_date":$("#edit_wh2p_end_date").val(),
								"consumble_end_date":$("#edit_consumble_end_date").val(),
								"ogz_end_date":$("#edit_ogz_end_date").val()
							};
							update_total_foreboard_count(data);
						},
						"取消":function(){
							 $("#edit_partial_base_line_value").dialog("close");
						}
					}
				});
			}
		}catch(e){
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
		}
	};
	
	var get_chart_complete=function(returnMap){
		var chart1 = new Highcharts.Chart({
			chart:{
				renderTo : 'container'
			},
			title: {
	            text: ''
	        },
			credits:{
				enabled:false
			},
			plotOptions:{
				line:{
					connectNulls:true,
					animation:true
				}
			},
			tooltip:{
				formatter:function(){
					return this.x+"<br>"+this.series.name+" "+(this.y).toFixed(0);
				}
			},
	        xAxis: {
	            categories: returnMap.axisTextList
			},
	        yAxis: {
				title: {
					text: ''
				},
				min:0,
	            plotLines: [{
	                value: 0,
	                width: 1
	            }],
				lineWidth:1,//Y轴宽度
				tickWidth:1,//刻度尺宽度
				tickLength:5//刻度尺长度
	        },
	        series: [{
	            name: '平均订购量',
	            data: returnMap.list_average_order
	        },{
	            name: '基准值',
	            data: returnMap.list_base_line_value
	        }]
		});
	};
	
	function partial_base_line_value_list2(listdata){
		if ($("#gbox_partial_base_line_value_list2").length > 0) {
			$("#partial_base_line_value_list2").jqGrid().clearGridData();
			$("#partial_base_line_value_list2").jqGrid('setGridParam',{data:null}).trigger("reloadGrid", [{current:false}]);
		}else{
			$("#partial_base_line_value_list2").jqGrid({
				data:listdata,
				height:239,
				width: 560,
				rowheight: 23,
				datatype: "local",
				colNames:['','','型号等级','梯队','拉动台数','标准零件使用数','拉动台数合计','周期内使用数'],
				colModel:[
					{
						name:'modelName',
						index:'modelName',
						hidden:true
					},
					{
						name:'level',
						index:'level',
						formatter : 'select',
						editoptions : {
							value : $("#goMaterial_level").val()
						},
						hidden:true
					},
					{
						name:'levelAndmodelName',
						index:'levelAndmodelName',
						align:'right',
						width:150,
						align:'left',
						formatter:function(cellvalue, options, rowObject){
							var levelName;
							if(rowObject.level==1){
								levelName="S1";
							}else if(rowObject.level==2){
								levelName="S2";
							}else if(rowObject.level==3){
								levelName="S3";
							}
							return levelName+rowObject.modelName;
						}
					},
					{
						name:'echelon',
						index:'echelon',
						align:'center',
						width:100,
						formatter:function(cellvalue,options,rowObject){
							if(cellvalue==1){
								return "第一梯队";
							}else if(cellvalue==2){
								return "第二梯队";
							}else if(cellvalue==3){
								return "第三梯队";
							}else if(cellvalue==4){
								return "第四梯队";
							}
						}
					},
					{
						name:'forecast_setting',
						index:'forecast_setting',
						width:100,
						align:'right',
						formatter:'integer',
						sorttype:'integer',
						formatoptions:{defaultValue:0}
					},
					{
						name:'countOfStandardPartial',
						index:'countOfStandardPartial',
						width:130,
						align:'right',
						formatter:'integer',
						sorttype:'integer',
						formatoptions:{defaultValue:0}
					},
					{
						name:'totalCount',
						index:'totalCount',
						width:130,
						align:'right',
						formatter:function(cellvalue, options, rowObject){
							if(rowObject.forecast_setting==null){
								return 0;
							}
							return rowObject.forecast_setting * rowObject.countOfStandardPartial;
						}
					},
					{
						name:'orderCountOfAverage',
						index:'orderCountOfAverage',
						width:120,
						align:'right',
						formatter:'integer',
						sorttype:'integer'
					}
				],
				rowNum: 20,
				toppager: false,
				pager: "#partial_base_line_value_list2pager",
				viewrecords: true,
				pagerpos: 'right',
				pgbuttons: true,
				pginput: false,
				hidegrid: false, 
				recordpos:'left',
				gridComplete:function(){
					$("#gbox_partial_base_line_value_list2").css("margin-left","4px");
				}
			});
		}
	};
	
	var update_total_foreboard_count=function(data){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : popServicePath + '?method=doUpdateTotalForeboardCount',
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
				treatBackMessages("#base_form", resInfo.errors);
			} else {
				$("#edit_partial_base_line_value").dialog("close");
				if (callback) {
					callback();
				}
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
		};
	};
	
};