var servicePath = "service_repair_resolve.do";
// 型号下拉
var acModelName;
$(function() {
	$("#colchooser").buttonset();
	/* radio转换成按钮 */
	$("#answer_in_deadline_id,#unfix_back_flg_id").buttonset();
	/* button */
	$("input.ui-button").button();
	/* select转换成按钮 */
	$("#search_liability_flg,#search_service_free_flg,#search_workshop").select2Buttons();
	/* 收页 */
	$("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n')
				.toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	/* 工具条 滚动固定 */
	$(window).bind("scroll", function() {
		var jwin = $(this);
		var scrolls = jwin.scrollTop() + jwin.height();
		var localer = $("#functionarea").position().top;
		var bar = $("#functionarea div:eq(0)");
		// 
		if (scrolls - $("#listarea").position().top > 120) {
			if ((scrolls - localer > bar.height())) {
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
	/* date 日期 */
	$("#search_qa_reception_time_start,#search_qa_reception_time_end,#search_qa_referee_time_start,#search_qa_referee_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	// autoComplete （型号、等级）
	$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=getAutocomplete',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					acModelName = resInfo.sModelName;
					acrank = resInfo.sRank;
					$("#search_model_name").autocomplete({
								source : acModelName,
								minLength : 0,
								delay : 100
							});
				} catch (e) {
				}
			}
		});
        
     $("#importAnalysiaBookbutton").disable();
	/* 清空 */
	$("#resetbutton").click(clearCondition);
	/* 检索 */
	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_serial_no").data("post", $("#search_serial_no").val());
		$("#search_sorc_no").data("post", $("#search_sorc_no").val());
		$("#search_liability_flg").data("post",$("#search_liability_flg").val());
		$("#search_qa_reception_time_start").data("post",$("#search_qa_reception_time_start").val());
		$("#search_qa_reception_time_end").data("post",$("#search_qa_reception_time_end").val());
		$("#search_qa_referee_time_start").data("post",$("#search_qa_referee_time_start").val());
		$("#search_qa_referee_time_end").data("post",$("#search_qa_referee_time_end").val());
		findit();
	});
    
    findit();
    
    $("#importAnalysiaBookbutton").click(function(){
         //选择行，并获取行数
	    var row = $("#list").jqGrid("getGridParam", "selrow");
        var rowData = $("#list").getRowData(row);
	    var data = {
	        "model_name" : rowData.model_name,
	        "serial_no" : rowData.serial_no,
	        "sorc_no" : rowData.sorc_no,
            "rc_mailsend_date":rowData.rc_mailsend_date,
            "analysis_correspond_suggestion":rowData.analysis_correspond_suggestion
	       // "qa_reception_time" :rowData.qa_reception_time,
	       // "qa_referee_time" : rowData.qa_referee_time
	    }
	   $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=report',
        cache : false,
        data : data,
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
                alert("文件导出失败！"); // TODO dialog
            }
        }
    });
        
        
    });
})
/* 分析button 弹出dialog */
var show_analysis_Complete = function() {// 点击受理按钮触发Dialog
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row); // 获取行数据
	var linkna = "widgets/qa/service_repair_resolve_edit.jsp";// load 页面

	$("#show_Accept").hide();

	// 当前操作者
	var operator = $("#judge_identity").val();

	$("#show_Accept").load(linkna,
			function(responseText, textStatus, XMLHttpRequest) {
                //checkbox转button
				$("#solution_confirm,#resolve_confirm").buttonset();

				$("#edit_trouble_cause_date,#edit_trouble_cause_complete").datepicker({
					showButtonPanel : true,
					dateFormat : "yy年mm月dd日",
					currentText : "今天"
				});

				// 选择图片文件之后，显示选择的图片
				$("#show_image").change(function() {
					$("#image_comment_container").show();
					$(this).picutePreview({
						renderTo : "#img_container"
					});
				});

				$("#img_container").mouseover(function() {
					$("#delete_circle").show();
				});

				$("#delete_circle").click(function() {
					$("#img_container").hide();
					$(this).hide();
				});
                //管理NO(analysis_no)
				$("#label_analysis_no").text(rowData.analysis_no);
                //产品名称(model_name)
				$("#label_model_name").text(rowData.model_name);
                //机身号
				$("#label_serial_no").text(rowData.serial_no);
                //客户名称
				$("#label_customer_name").text(rowData.customer_name);
                //上次完成日(上次出货日期)
				$("#label_last_shipping_date").text(rowData.last_shipping_date);
                //上次修理NO
                $("#label_last_sorc_no").text(rowData.last_sorc_no);
                //OCM(上次OCM等级)
				$("#label_ocm_one").text(rowData.last_ocm_rank);
                //翻修技术部(上次等级)
				$("#label_ocm_two").text(rowData.last_rank);
                //此次受理日(SORC受理日----service_repair_past_material表的reception_date)
				$("#label_reception_date").text(rowData.reception_time);
				//此次修理NO
                $("#label_sorc_no").text(rowData.sorc_no);
                //使用情况(使用频率)
				$("#label_usage_frequency").text(rowData.usage_frequency);
                //上次故障内容(上次不合格内容)
				$("#label_last_trouble_feature").text(rowData.last_trouble_feature);
                //不良内容
				$("#label_fix_demand").text(rowData.fix_demand);
                //再修理方案
				$("#label_last_rank").text(rowData.countermeasures);
				// 当责任分区有值时,将代表此值的radio选中,其他radio一概不可点击选择
				if (rowData.analysis_result != null) {
					$("#left_table input[value=" + rowData.analysis_result+"]").attr("checked", true);
					$("#left_table input[value!=" + rowData.analysis_result+"]").attr("disabled", true);
				}
                //故障
				$("#label_trouble_discribe").text(rowData.trouble_discribe);
                //原因
				$("#label_trouble_cause").text(rowData.trouble_cause);
                //故障处理日
                $("#label_qa_referee_date").text(rowData.qa_referee_time);
                // 对应
				$("#label_countermeasures").text(rowData.analysis_correspond_suggestion);                
				// 对策
				$("#textarea_countermeasures_two").val(rowData.solution_content);
				// 对策完成日--(对策)
				$("#label_solution_date").text(rowData.solution_date);
				// 对应完成确认
				$("#textarea_countermeasures_complete").val(rowData.resolve_content);
				// 对策完成日--(对应完成确认)
				$("#label_resolve_date").text(rowData.hidden_resolve_date);
				// 在页面上面隐藏一个字段RC邮件发送日
				$("#hidden_rc_mailsend_date").val(rowData.rc_mailsend_date);
               
                // 操作者是经理
                var judge_identity = $("#judge_identity").val();
                // 操作者ID
                var operator_id = $("#hidden_operator_id").val();
				$("#show_Accept").dialog({
					resizable : false,
					modal : true,
					width : 1100,
					height : $(window).height(),
					show : "blind",
					modal : true,// 遮罩效果
					title : "保修期内返品分析表",
					buttons : {
						"编辑" : function() {
							var data = {
								"model_name" : $("#label_model_name").text(),
								"serial_no" : $("#label_serial_no").text(),
								"rc_mailsend_date" : $("#hidden_rc_mailsend_date").val(),
								"solution_content" : $("#textarea_countermeasures_two").val(),
								"resolve_content" : $("#textarea_countermeasures_complete").val(),
                                "solution_raiser":rowData.solution_raiser,
                                "solution_confirmer":rowData.solution_confirmer,
                                "resolve_handler":rowData.resolve_handler,
                                "resolve_confirmer":rowData.resolve_confirmer,
                                "technical_analysts":rowData.technical_analysts,
                                "solution_date":$("#label_solution_date").text(),
                                "resolve_date":$("#label_resolve_date").text(),
                                isSolution:"",
                                isResolve:""
                                
							};
                            // 如果当前操作者是经理---将当前操作者的ID更新到--·保内返品对策对应表--对策确定人---·对策作成日--更新成当前确定点击的时间
                            // 如果当前操作者是其他线长人员---将当前操作的ID更新到--·保内返品对策对应表--对策提出人---·对策作成日--更新成当前确定点击的时间
                            if($("#solution_confirm_button").attr("checked")=="checked"){
                               // data["resolve_confirmer"] = "";
                                data.isSolution="solution";
                                if (judge_identity == "manager") {
                                    data["solution_confirmer"] = operator_id;
                                } else {
                                    data["solution_raiser"]=operator_id;
                                }
                              
                            }
                            // 如果当前操作者是否是技术课的---将当前操作者的ID更新到--·保内返品对策对应表--技术分析人---·对策完成日--更新成当前确定点击的时间
			                // 如果当前操作者是经理---将当前操作者的ID更新到--·保内返品对策对应表--对策完成确定人---·对策完成日--更新成当前确定点击的时间
			                // 如果当前操作者是其他线长人员---将当前操作者的ID更新到--·保内返品对策对应表--对策完成执行人---·对策完成日--更新成当前确定点击的时间
			                 if($("#resolve_confirm_button").attr("checked")=="checked"){
                               // data["solution_confirmer"] ="";
			                    data.isResolve="resolve";
                                if (judge_identity == "manager") {
			                        data["resolve_confirmer"] = operator_id;
			                    } else if (judge_identity == "technician") {
			                        data["technical_analysts"] = operator_id;
			                    } else {
			                        data["resolve_handler"] = operator_id;
			                    }
                                
                             };
                            
							$.ajax({
									beforeSend : ajaxRequestType,
									async : true,
									url : servicePath + '?method=doupdate',
									cache : false,
									data : data,
									type : "post",
									dataType : "json",
									success : ajaxSuccessCheck,
									error : ajaxError,
									complete : update_complete
								});
						},
						"取消" : function() {
							$("#show_Accept").dialog('close');
						}
					}
				});
			})
}

/* insert data */
function update_complete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#confirm_message").text("分析表更新已经完成！");
			$("#confirm_message").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "确认",
				buttons : {
					"关闭" : function() {
						$("#confirm_message").dialog("close");
					}
				}
			});
			$("#show_Accept").dialog('close');
			findit();
		}
	} catch (e) {
	}
}

/* clearButton */
var clearCondition = function() {
	$("#search_model_name").val("").data("post", "");
	$("#search_serial_no").val("").data("post", "");
	$("#search_sorc_no").val("").data("post", "");
	$("#search_liability_flg").val("").trigger("change").data("post", "");
	$("#search_qa_reception_time_start").val("").data("post", "");
	$("#search_qa_reception_time_end").val("").data("post", "");
	$("#search_qa_referee_time_start").val("").data("post", "");
	$("#search_qa_referee_time_end").val("").data("post", "");
};
var findit = function() {
	var data = {
		"model_name" : $("#search_model_name").val(),
		"serial_no" : $("#search_serial_no").val(),
		"sorc_no" : $("#search_sorc_no").val(),
		"liability_flg" : $("#search_liability_flg").val(),
		"qa_reception_time_start" : $("#search_qa_reception_time_start").val(),
		"qa_reception_time_end" : $("#search_qa_reception_time_end").val(),
		"qa_referee_time_start" : $("#search_qa_referee_time_start").val(),
		"qa_referee_time_end" : $("#search_qa_referee_time_end").val()
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
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				var keepSearchData = resInfo.returnForms;
				service_repair_list(keepSearchData);
			} catch (e) {
			}
		}
	});
}

function service_repair_list(keepSearchData) {
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam', {data : keepSearchData}).trigger("reloadGrid", [{current : false}]);// 刷新列表
	} else {
		$("#list").jqGrid({
			data : keepSearchData,// 数据
			height : 550,// rowheight*rowNum+1
			width : 1248,
			rowheight : 23,
			shrinkToFit : true,
			datatype : "local",
			colNames : ['对策提出人','对策确定人','对策完成执行人','技术分析人','再修理方案(manage表的countermeasures)','此次受理日(SORC受理日)','RC邮件发送日', '客户名称', '上次出货日期', '上次修理单号', '使用频率',
					'上次故障内容', '不良内容', '上次等级', '上次OCM等级','故障描述', '故障原因', '分析对应建议',
					'QA判定时间', '对策作成日', '对策完成日', '对策', '对应完成确认', '型号',
					'机身号', '修理单号', '责任区分', '分析结果', '分析表编号', '对策完成日', '部长确认',
					'处理对策'],
			colModel : [{name:'solution_raiser',index:'solution_raiser',hidden:true },
                        {name:'solution_confirmer',index:'solution_confirmer',hidden:true},
                        {name:'resolve_handler',index:'resolve_handler',hidden:true},
                        {name:'technical_analysts',index:'technical_analysts',hidden:true},
                        {name:'countermeasures',index:'countermeasures',hidden:true},
                        {name:'reception_time',index:'reception_time',hidden:true},
                        {name : 'rc_mailsend_date',index : 'rc_mailsend_date',hidden : true}, 
                        {name : 'customer_name',index : 'customer_name',hidden : true},
                        {name : 'last_shipping_date',index : 'last_shipping_date',hidden : true}, 
                        {name : 'last_sorc_no',index : 'last_sorc_no',hidden : true}, 
	                    {
							name : 'usage_frequency',
							index : 'usage_frequency',
							hidden : true
						}, {
							name : 'last_trouble_feature',
							index : 'last_trouble_feature',
							hidden : true
						}, {
							name : 'fix_demand',
							index : 'fix_demand',
							hidden : true
						}, {
							name : 'last_rank',
							index : 'last_rank',
							hidden : true
						}, {
	                        name : 'last_ocm_rank',
	                        index : 'last_ocm_rank',
	                        hidden : true
	                    }, {
							name : 'trouble_discribe',
							index : 'trouble_discribe',
							hidden : true
						}, {
							name : 'trouble_cause',
							index : 'trouble_cause',
							hidden : true
						}, {
							name : 'analysis_correspond_suggestion',
							index : 'analysis_correspond_suggestion',
							hidden : true
						}, {
							name : 'qa_referee_time',
							index : 'qa_referee_time',
							hidden : true
						}, {
							name : 'solution_date',
							index : 'solution_date',
							hidden : true
						}, {
							name : 'hidden_resolve_date',
							index : 'hidden_resolve_date',
							hidden : true,
	                        formatter:function(value,options,rData){
	                          if(rData.resolve_date !="" && rData.resolve_date !=null ){
	                              return rData.resolve_date
	                          }
	                          return "";
	                        }
						}, {
							name : 'solution_content',
							index : 'solution_content',
							hidden : false
						}, {
							name : 'resolve_content',
							index : 'resolve_content',
							hidden : false
						}, {
							name : 'model_name',
							index : 'model_name',
							width : 100,
							align : 'left'
						}, {
							name : 'serial_no',
							index : 'serial_no',
							width : 60,
							align : 'left'
						}, {
							name : 'sorc_no',
							index : 'sorc_no',
							width : 125,
							align : 'left'
						}, {
							name : 'liability_flg',
							index : 'liability_flg',
							align : 'center',
							width : 60
						}, {
							name : 'analysis_result',
							index : 'analysis_result',
							align : 'center',
							width : 60,
							hidden : true
						}, {
							name : 'analysis_no',
							index : 'analysis_no',
							width : 80,
							align : 'left'
						}, {
							name : 'resolve_date',
							index : 'resolve_date',
							width : 45,
							align : 'center',
							sorttype : 'date',
							formatter : 'date',
							formatoptions : {
								srcformat : 'Y/m/d',
								newformat : 'm-d'
							}
						}, {
							name : 'minister_confirmer',
							index : 'minister_confirmer',
							width : 35,
							align : 'center'
						}, {
							name : 'analysis_correspond_suggestion',
							index : 'analysis_correspond_suggestion',
							width : 250,
							align : 'left'
						}],
			rowNum : 35,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			caption : "",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, // 翻页按钮
			rownumbers : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
            onSelectRow :enableButton,
			ondblClickRow : show_analysis_Complete,
			viewsortcols : [true, 'vertical', true],
			gridComplete : function() {// 当表格所有数据都加载完成而且其他的处理也都完成时触发
				// 得到显示到界面的id集合
				var IDS = $("#list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var liability_flg = rowData["liability_flg"];
					// 当责任区分是自责时，设置行首TD的背景颜色为黄色
					if (liability_flg == '自责') { // liability_flg == 2
						pill.find("tr#" + IDS[i]).addClass("self_resp");
					}
				}
			}
		});
	}
};

/*判断分析书导出enable、disable(当选择了零件之后enable；否则是disable)*/
var enableButton= function(){
    //选择行
     var row = $("#list").jqGrid("getGridParam", "selrow");
     if(row>0){
        $("#importAnalysiaBookbutton").enable();
     }else{
        $("#importAnalysiaBookbutton").disable();
     }
     
}