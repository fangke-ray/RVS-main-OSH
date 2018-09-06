var hostname = document.location.hostname;

var servicePath = "service_repair_manage.do";
/* 类别 */
var repair_flg = "";
/* 有无偿 */
var free_flg = "";
/* 维修站 */
var shop = "";

var g_listdata ={};

/* 修理编号 */
var repair_number;

/* 型号 */
var acModelName;

/* 等级 */
var acrank;

/* 分析结果*/
var analysis_result ="";

/* 责任分区*/
var liability_flg ="";

/* 处理方式*/
var corresponse_flg = "";

/* 实物处理*/
var entity_send_flg = "";

/* 质量判定 */
var quality_judgment = "";

/* 发行QIS */
var qis_isuse = "";

$(function() {
	$("#colchooser").buttonset();
	/*checkbox折叠/完全显示功能*/
	$("#colchooser input:checkbox").click(function (){
		var tid = $(this).attr("id");
		var tcheck=$(this).attr("checked");
		
		var tname;
		if("colchooser_repair"===tid){
			tname =['quotation_date','agreed_date','inline_date','outline_date','unfix_back_flg'];
		}else if("colchooser_qis"===tid){
			tname =['quality_info_no','qis_invoice_no','qis_invoice_date','include_month','charge_amount','quality_judgment','qis_isuse'];
		}else if("colchooser_etq"===tid){
			tname =['etq_no'];
		}else{
			return;
		}
		
		if("checked"===tcheck){
			$("#list").jqGrid('showCol',tname);
		}else{
			$("#list").jqGrid('hideCol',tname);
		}
		$("#list").jqGrid('setGridWidth', '1248');		
	});
	/* radio转换成按钮 */
	$("#answer_in_deadline_id,#unfix_back_flg_id,#search_entity_send_flg").buttonset();
	/* button */
	$("input.ui-button").button();
	/* select转换成按钮 */
	$("#service_repair_flg,#search_service_free_flg,#search_workshop,#search_kind").select2Buttons();
	/* date 日期 */
	$("#search_qa_reception_time_start,#search_qa_reception_time_end,#search_qa_referee_time_start,#search_qa_referee_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	/* 按钮的失效显示 */
	// enableButton();
	$("#analysisbutton,#mentionbutton").disable();
	/* 收页 */
	$("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
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

	$("#search_qa_reception_time_start").data("post",$("#search_qa_reception_time_start").val());
	findit();

	/* 清空 */
	$("#resetbutton").click(clearCondition);
	/* 检索 */
	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {	
		$("#search_model_name").data("post",$("#search_model_name").val());
		$("#search_serial_no").data("post",$("#search_serial_no").val());
		$("#search_sorc_no").data("post",$("#search_sorc_no").val());
		$("#service_repair_flg").data("post",$("#service_repair_flg").val());
		$("#search_qa_reception_time_start").data("post",$("#search_qa_reception_time_start").val());
		$("#search_qa_reception_time_end").data("post",$("#search_qa_reception_time_end").val());
		$("#search_qa_referee_time_start").data("post",$("#search_qa_referee_time_start").val());
		$("#search_qa_referee_time_end").data("post",$("#search_qa_referee_time_end").val());
		$("#answer_in_deadline_id input:checked").data("post",$("#answer_in_deadline_id input:checked").val());
		$("#search_unfix_back_flg_all").data("post",$("#search_unfix_back_flg_all").val());
		$("#search_service_free_flg").data("post",$("#search_service_free_flg").val());
		$("#unfix_back_flg_id input:checked").data("post",$("#unfix_back_flg_id input:checked").val());
		$("#search_answer_in_deadline_all").data("post", $("#search_answer_in_deadline_all").val());
		$("#search_workshop").data("post",$("#search_workshop").val());
		$("#search_entity_send_flg input:checked").data("post",$("#search_entity_send_flg input:checked").val());//实物处理
		$("#search_quality_info_no").data("post",$("#search_quality_info_no").val());//质量信息单号
		$("#search_qis_invoice_no").data("post",$("#search_qis_invoice_no").val());//QIS发送单号
		$("#search_kind").data("post",$("#search_kind").val());//产品分类
		$("#search_etq_no").data("post",$("#search_etq_no").val());//ETQ单号
		findit();
	});
	/* autoComplete （型号、等级） */
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

	repair_flg = $("#service_repair_flg").html();// 两个页面之间传递值和样式，通过html()进行获取
	free_flg = $("#search_service_free_flg").html();
	shop = $("#search_workshop").html();
	liability_flg = $("#hidden_select_manufactory_flg").html();
	analysis_result = $("#hidden_select_liability_flg").html();
	corresponse_flg = $("#hidden_select_qis_corresponse_flg").html();
	entity_send_flg = $("#hidden_select_qis_entity_send_flg").html();
	quality_judgment = $("#hidden_select_quality_judgment").html();
	qis_isuse = $("#hidden_select_qis_isuse").html();
	
	/*QIS受理button*/
	$("#qisacceptbutton").click(function(){
		showedit_accept();
	})
	/*编辑button*/
	$("#editbutton").click(function() {
		editButton();
	});
	/*提要button*/
	$("#mentionbutton").click(function(){
		showmentionbutton();
	});
	/*删除数据button*/
	$("#deletebutton").click(function(){
		showdeleteButton();
	});
	/*分析button*/
	$("#analysisbutton").click(function(){
		var isQaManager =$("#judge_is_qa_manager").val();
		showanalysisbutton(isQaManager);
	});	
	/*退回作业前button*/
	$("#backbutton").disable().click(function(){
    	var isQaManager =$("#judge_is_qa_manager").val();
//		if (isQaManager) {
    	warningConfirm("已经开始的判定作业将被取消，请确认操作！", doActionBack);
//		}
	});	
	/*当前结果button*/
	$("#currentResultbutton").click(function() {
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=report',
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
					errorPop("文件导出失败！"); // TODO dialog
				}
			}
		});
	});
	
});

var doActionBack = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);

	var postData = {
		"model_name":rowData.model_name,
		"serial_no":rowData.serial_no,
		"rc_mailsend_date":rowData.hidden_rc_mailsend_date
	};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doActionBack',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doActionBack_complete
	});
}

function doActionBack_complete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			findit();
		}
	} catch (e) {
	}
}

var showmentionbutton = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	
	$("#hidden_label_model_name").val(rowData.model_name);
	$("#hidden_label_serial_no").val(rowData.serial_no);
	$("#hidden_label_sorc_no").val(rowData.sorc_no);
	$("#hidden_rc_mailsend_date").val(rowData.hidden_rc_mailsend_date);
	
	$("#hidden_service_repair_flg_val").val(rowData.service_repair_flg);
	var repair_flg =rowData.service_repair_flg;
	var service_repair_flg = ""
	if(repair_flg==1){
		service_repair_flg="保修期内不良"
	}else if(repair_flg==2){
		service_repair_flg="QIS不良品";
	}else if(repair_flg==3){
		service_repair_flg="保修期外不良";
	}
	$("#hidden_label_service_repair_flg").val(service_repair_flg);
	
	$("#hidden_textarea_mention").val(rowData.mention);
	var data = {
			"model_name":rowData.model_name,
			"serial_no":rowData.serial_no,
			"rc_mailsend_date":rowData.hidden_rc_mailsend_date
			};
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : show_mention_Complete
	});
}
/*提要button 弹出dialog*/
var show_mention_Complete = function(xhrobj, textStatus) {// 点击受理按钮触发Dialog
	var linkna = "widgets/qa/service_repair_manage_analysis.jsp";//load 页面
	$("#show_Accept").hide();	
	$("#show_Accept").load(linkna,function(responseText, textStatus, XMLHttpRequest) {
		var tr_num  =$("#ins_serviceRepairManage .condform").find("tr");
		for(var i = 3;i<tr_num.length;i++){
			$("#ins_serviceRepairManage .condform").find("tr:eq(" +i +")").hide();
		}	
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#label_model_name").text($("#hidden_label_model_name").val());
				$("#label_serial_no").text($("#hidden_label_serial_no").val());
				$("#label_sorc_no").text($("#hidden_label_sorc_no").val());
				$("#label_service_repair_flg").text($("#hidden_label_service_repair_flg").val());
				$("#textarea_mention").val($("#hidden_textarea_mention").val());
			}
		} catch (e) {
		}
		$("#show_Accept").dialog({
			width : 670,
			height:'auto',
			show: "blind",
			modal : true,//遮罩效果
			title : "保修期内返品+QIS品提要",
			buttons : {
				"删除" : function() {
					$("#textarea_mention").val("");
						var doinsertdata = {
								"service_repair_flg":$("#hidden_service_repair_flg_val").val(),
								"model_name": $("#label_model_name").text(), 
								"serial_no":$("#label_serial_no").text(), 
								"sorc_no":$("#label_sorc_no").text(),
								"rc_mailsend_date": $("#hidden_rc_mailsend_date").val(), 
								"mention":$("#textarea_mention").val()	
						};	
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doupdateMention',
							cache : false,
							data : doinsertdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : insert_complete
						});
					},
				"确认" : function() {
						var doinsertdata = {
								"service_repair_flg":$("#hidden_service_repair_flg_val").val(),
								"model_name": $("#label_model_name").text(), 
								"serial_no":$("#label_serial_no").text(), 
								"sorc_no":$("#label_sorc_no").text(),
								"rc_mailsend_date": $("#hidden_rc_mailsend_date").val(), 
								"mention":$("#textarea_mention").val()			
						};			
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doupdateMention',
							cache : false,
							data : doinsertdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : insert_complete
						});
				},
				"关闭" : function() { $("#show_Accept").dialog("close")}
			}
		});
})
}

var showanalysisbutton = function(isQaManager){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	
	$("#hidden_label_model_name").val(rowData.model_name);
	$("#hidden_label_serial_no").val(rowData.serial_no);
	$("#hidden_label_sorc_no").val(rowData.sorc_no);
	$("#hidden_rc_mailsend_date").val(rowData.hidden_rc_mailsend_date);
	
	$("#hidden_service_repair_flg_val").val(rowData.service_repair_flg);
	var repair_flg =rowData.service_repair_flg;
	var service_repair_flg = ""
	if(repair_flg==1){
		service_repair_flg="保修期内不良"
	}else if(repair_flg==2){
		service_repair_flg="QIS不良品";
	}else if(repair_flg==3){
		service_repair_flg="保修期外不良";
	}
	$("#hidden_label_service_repair_flg").val(service_repair_flg);
	var data = {
			"model_name":rowData.model_name,
			"serial_no":rowData.serial_no,
			"rc_mailsend_date":rowData.hidden_rc_mailsend_date
			};
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :function(xhrobj,textStatus){
          show_analysis_Complete(xhrobj,textStatus,isQaManager,rowData.hidden_reception_date,rowData.sorc_no,rowData.usage_frequency,rowData.countermeasures,rowData.analysis_result);
        } 
	});
}
/*分析button 弹出dialog*/
var show_analysis_Complete = function(xhrobj, textStatus,isQaManager,hidden_reception_date,sorc_no,usage_frequency,countermeasures,this_analysis_result) {// 点击受理按钮触发Dialog
	var linkna = "";
    if(isQaManager=="privacy_qa_manager"){
        linkna = "widgets/qa/service_repair_manage_analysis.jsp";//load 页面
    
        // linkna = "widgets/service_repair_manage_analysis_qa.jsp";//load 页面
    }else{
        //linkna = "widgets/service_repair_manage_analysis.jsp";//load 页面
        linkna = "widgets/qa/service_repair_manage_analysis.jsp";//load 页面
    }
   
	$("#show_Accept").hide();	
	$("#show_Accept").load(linkna,function(responseText, textStatus, XMLHttpRequest) {
		
		//上传图片
        $("#import_pic").click(function(){
			
        	$("input .ui-button").button();
        	
        	var $hiddenInput=$("#get_uuid");
			//点击关闭的时候将 一个全新DIV加载到页面
			var img_container = "";
			
			var view_container = "<div class='img_container' style='position:relative;display:inline-block;'>" 
				+"<img src='http://"+hostname+"/photos/"+$hiddenInput.val().substring(0,4)+"/" + $hiddenInput.val()+ "_fix.jpg'/>"
				+"<div class='delete_pic' style='width: 17px; height: 17px;background-image:url(./images/delete_photo.png);position:absolute;top:0px;right:0px;cursor:pointer;'></div>"
				+"<input type='hidden'/></div>";
			
			if($(".img_container")){
				img_container +=view_container;
			}else{
				img_container = $(".img_container").next().append(view_container);
			}
									
			//点击上传图片button 将图片加载到编辑画面					
			$("#append_images").append(img_container);
		
			photo_editor_functions.editImgFor($hiddenInput,function(){
    			if( $hiddenInput.val().length>0){
    				$("#append_images .img_container:last-child").find("img").show();
    				$("#append_images .img_container:last-child").find("img").attr("src","http://"+hostname+"/photos/" + $hiddenInput.val().substring(0,4) + "/" + $hiddenInput.val()+ "_fix.jpg");
    				$("#append_images .img_container:last-child").find("input[type='hidden']").val($hiddenInput.val());
    				$hiddenInput.val("");
    			}else{
    				$(".img_container:last-child").remove();
    			}
    		});
			
			//删除上传图片
	        $.each($("div.delete_pic"),function(index,ele){
	        	$(ele).click(function(){
	        		$(ele).parent().remove();
	        	});
	        });
			
        });
		
        $("input.ui-button").button();
		$("#ins_serviceRepairManage .condform").find("tr:eq(2)").hide();
		$("#select_analysis_result").html(liability_flg);
		$("#select_liability_flg").html(analysis_result);
		$("#select_manufactory_flg").buttonset();
		$("#select_corresponse_flg").html(corresponse_flg);
		$("#select_entity_send_flg").html(entity_send_flg);
		$("#select_analysis_result,#select_liability_flg,#select_corresponse_flg,#select_entity_send_flg").select2Buttons();
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				//管理NO(分析表编号)
				$("#text_analysis_no").val(resInfo.returnForm.analysis_no);
				//产品名称(型号)
				$("#label_model_name").text($("#hidden_label_model_name").val());
                //BodyNo.(机身号)
				$("#label_serial_no").text($("#hidden_label_serial_no").val());
                //客户名称
                $("#edit_customer_name").val(resInfo.returnForm.customer_name);
                //上次完成日
                $("#text_last_shipping_date").val(resInfo.returnForm.last_shipping_date);
				//上次修理NO
                $("#text_last_sorc_no").val(resInfo.returnForm.last_sorc_no);
                //上次等级--OCＭ
                $("#text_last_ocm_rank").val(resInfo.returnForm.last_ocm_rank);
                //上次等级--返修技术部
                $("#text_last_rank").val(resInfo.returnForm.last_rank);
                //此次受理日 
                $("#label_reception_date").text(hidden_reception_date);
                 //此次修理编号
                $("#label_sorc_no").text(sorc_no);
               
                //使用情况---不可编辑的分析表--使用频率
                $("#text_usage_frequency").text(usage_frequency);
                //可编辑的分析表--使用频率
                $("#text_usage_frequency").val(usage_frequency);
                
                //上次不合格内容
                $("#text_last_trouble_feature").val(resInfo.returnForm.last_trouble_feature);
                 //此次不合格内容
                $("#text_fix_demand").val(resInfo.returnForm.fix_demand);
                
				$("#label_service_repair_flg").text($("#hidden_label_service_repair_flg").val());
                //原因
				$("#text_trouble_cause").val(resInfo.returnForm.trouble_cause);
                //故障
				$("#text_trouble_discribe").val(resInfo.returnForm.trouble_discribe);
                //故障处理日
                $("#label_qa_referee_date").text(resInfo.returnForm.qa_referee_date);
				
                //责任区分()
                $("#select_analysis_result").val(resInfo.returnForm.analysis_result).trigger("change");
                $("input[type='radio'][value='"+this_analysis_result+"']").attr("checked",'checked');
				
				$("#select_liability_flg").val(resInfo.returnForm.liability_flg).trigger("change");
				$("#select_manufactory_flg input[value="+resInfo.returnForm.manufactory_flg+"]").attr("checked", true).trigger('change');
				
				$("#text_append_component").val(resInfo.returnForm.append_component);
				$("#text_quantity").val(resInfo.returnForm.quantity);
				$("#text_loss_amount").val(resInfo.returnForm.loss_amount);
				$("#text_last_sorc_no").val(resInfo.returnForm.last_sorc_no);
				$("#text_last_shipping_date").val(resInfo.returnForm.last_shipping_date);
				
				$("#text_last_rank").val(resInfo.returnForm.last_rank);
				$("#text_last_trouble_feature").val(resInfo.returnForm.last_trouble_feature);
				$("#text_wash_feature").val(resInfo.returnForm.wash_feature);
				$("#text_disinfect_feature").val(resInfo.returnForm.disinfect_feature);
				$("#text_steriliza_feature").val(resInfo.returnForm.steriliza_feature);
				$("#text_m_trouble_phenomenon_confirm").val(resInfo.returnForm.m_trouble_phenomenon_confirm);
				$("#text_m_judgment_result").val(resInfo.returnForm.m_judgment_result);
				$("#text_m_analysis_result_brief").val(resInfo.returnForm.m_analysis_result_brief);
				$("#text_m_correspond_method").val(resInfo.returnForm.m_correspond_method);
				$("#text_m_solutions").val(resInfo.returnForm.m_solutions);
				
				$("#text_setup_date").val(resInfo.returnForm.setup_date);
				$("#text_trouble_happen_date").val(resInfo.returnForm.trouble_happen_date);
				$("#text_use_count").val(resInfo.returnForm.use_count);
				$("#text_use_elapse").val(resInfo.returnForm.use_elapse);
				
                var service_repair_flg = $("#hidden_service_repair_flg_val").val();
                if(service_repair_flg == 2){//QIS判定完成
                    $("#select_corresponse_flg").val(resInfo.returnForm.corresponse_flg).trigger("change");
                }else{
                    $("#part1").remove();      
                }
                
                
                if(true){//QIS/返品 判定完成 service_repair_flg == 1 || service_repair_flg == 2
                    var entitySendFlg = resInfo.returnForm.entity_send_flg;
                    $("#select_entity_send_flg").val(entitySendFlg).trigger("change");
                    
                    $("#text_qis3_date").val(resInfo.returnForm.qis3_date);
                    
                    if(entitySendFlg == 21 || entitySendFlg == 23){
                           $("tr.part3 input[type='text']").hide();
                           $("#text_qis3_date").show();
                           $("tr.part3 label").show();
                           $("#text_qis3_date").next().hide();
                    }else if(entitySendFlg == 22){
                   		   $("tr.part3 input[type='text']").hide();
                   		   $("tr.part3 label").show();
                    }else{
                           $("tr.part3 input[type='text']").show();
                           $("tr.part3 label").hide();
                           $("#text_trouble_item_reception_date").val(resInfo.returnForm.trouble_item_reception_date);
		                   $("#text_trouble_item_in_bussiness_date").val(resInfo.returnForm.trouble_item_in_bussiness_date);
		                   $("#text_trouble_item_out_bussiness_date").val(resInfo.returnForm.trouble_item_out_bussiness_date);
		                   $("#text_qis2_date").val(resInfo.returnForm.qis2_date);
		                   $("#text_waste_certificated_date").val(resInfo.returnForm.waste_certificated_date);
                    }

                    //实物处理 change事件
                    $("#select_entity_send_flg").change(function(){
                          var value = $(this).val();
                          if(value == 21 || value == 23){//不寄送（OCM送回）
                                $("tr.part3 input[type='text']").hide();
                                $("#text_qis3_date").show();
                                $("tr.part3 label").show();
                                $("#text_qis3_date").next().hide();
                          }else if(value == 22){//不寄送（非质量问题） 不寄送（发送信息）
                          		$("tr.part3 input[type='text']").hide();
                                $("tr.part3 label").show();
                          }else{
                                $("tr.part3 input[type='text']").show();
                                $("tr.part3 label").hide();
                          }
                    });
                }else{
                    $("#part2").remove();
                    $("#ins_serviceRepairManage tr.part3").remove();
                }
                
				
				var analysisGrams = resInfo.resultAnalysisGrams;
				for(var i=0;i<analysisGrams.length;i++){
					
					var detail_container = "";
					var detail_view_container = "<div class='img_container' style='position:relative;display:inline-block;'>" 
						+"<img src='http://"+hostname+"/photos/"+analysisGrams[i].image_uuid.substring(0,4)+"/" + analysisGrams[i].image_uuid+ "_fix.jpg'/>"
						+"<div class='delete_pic' style='width: 17px; height: 17px;background-image:url(./images/delete_photo.png);position:absolute;top:0px;right:0px;cursor:pointer;'></div>"
						+"<input type='hidden' value='"+analysisGrams[i].image_uuid+"'/>"
						+"<label style='display:none;'>"+analysisGrams[i].seq_no+"</label></div>";
					
					if($(".img_container")){
						detail_container +=detail_view_container;
					}else{
						detail_container = $(".img_container").next().append(detail_view_container);
					}
					
					$("#append_images").append(detail_container);
					//filename += filenames[i]+",";				
				}
				
				//删除上传图片
		        $.each($("div.delete_pic"),function(index,ele){
		        	$(ele).click(function(){
		        		$(ele).parent().remove();
		        		var data={
		        				"seq_no":$(ele).parent().find("label").text(),
		        				"model_name":$("#label_model_name").text(), 
								"serial_no":$("#label_serial_no").text(), 
								"rc_mailsend_date": $("#hidden_rc_mailsend_date").val()
		        		}
		        		$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doDeleteImage',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(){
								$(ele).parent().remove();
							}
						});
		        		
		        	});
		        });
				
				//$("#hidden_detail_uuid").val(filename);
				
                //再修理方案
                $("#label_countermeasures").text(countermeasures);
                //对应(处理对策)
                $("#textarea_analysis_correspond_suggestion").val(resInfo.returnForm.analysis_correspond_suggestion);
			}
			$("#text_last_shipping_date,#text_trouble_item_reception_date,#text_trouble_item_in_bussiness_date,#text_trouble_item_out_bussiness_date," +
					"#text_qis2_date,#text_qis3_date,#text_waste_certificated_date,#text_setup_date,#text_trouble_happen_date").datepicker({
				showButtonPanel : true,
				dateFormat : "yy/mm/dd",
				currentText : "今天"
			});
		} catch (e) {
		}
		$("#ins_serviceRepairManage").validate({
			rules : {
				analysis_no : {
					required : true,
					maxlength : 23
				}
			},
			ignore : "input[type='text']:hidden"
		});
		
		$("#show_Accept").dialog({
			width : 750,
			height:'auto',
			show: "blind",
			modal : true,//遮罩效果
			title : "保修期内返品",
			buttons : {															
				"确认" : function() {
					if ($("#ins_serviceRepairManage").valid()) {
						
						var doinsertdata = {
								"service_repair_flg":$("#hidden_service_repair_flg_val").val(),
								"model_name": $("#label_model_name").text(), 
								"serial_no":$("#label_serial_no").text(), 
								"sorc_no":$("#label_sorc_no").text(),
								"rc_mailsend_date": $("#hidden_rc_mailsend_date").val(), 
								"analysis_no":$("#text_analysis_no").val(),
								 "customer_name":$("#edit_customer_name").val(),
								 "fix_demand": $("#text_fix_demand").val(),
								 "trouble_cause": $("#text_trouble_cause").val(), 
								 "trouble_discribe": $("#text_trouble_discribe").val(), 
								 "analysis_result": $("#select_analysis_result").val(),
								 "liability_flg": $("#select_liability_flg").val(),
								 "manufactory_flg": $("#select_manufactory_flg input:checked").val(), 
								 "append_component":$("#text_append_component").val(), 
								 "quantity":$("#text_quantity").val(), 
								 "loss_amount": $("#text_loss_amount").val(),
								 "last_sorc_no":$("#text_last_sorc_no").val(), 
								 "last_shipping_date":$("#text_last_shipping_date").val(), 
								 "last_rank": $("#text_last_rank").val(),
                                 "last_ocm_rank": $("#text_last_ocm_rank").val(),
								 "last_trouble_feature":$("#text_last_trouble_feature").val(), 
								 "shipping_date" :$("#text_last_shipping_date").val(), 
								 "wash_feature": $("#text_wash_feature").val(), 
								 "disinfect_feature":$("#text_disinfect_feature").val(), 
								 "steriliza_feature":$("#text_steriliza_feature").val(),
								 "usage_frequency": $("#text_usage_frequency").val(),
                                 "analysis_correspond_suggestion":$("#textarea_analysis_correspond_suggestion").val(),
                                 "corresponse_flg":$("#select_corresponse_flg").val(),
                                 "entity_send_flg":$("#select_entity_send_flg").val(),
                                 "trouble_item_reception_date":$("#text_trouble_item_reception_date").val(),
                                 "trouble_item_in_bussiness_date":$("#text_trouble_item_in_bussiness_date").val(),
                                 "trouble_item_out_bussiness_date":$("#text_trouble_item_out_bussiness_date").val(),
                                 "qis2_date":$("#text_qis2_date").val(),
                                 "qis3_date":$("#text_qis3_date").val(),
                                 "waste_certificated_date":$("#text_waste_certificated_date").val(),
                                 "m_trouble_phenomenon_confirm":$("#text_m_trouble_phenomenon_confirm").val(),
                                 "m_judgment_result":$("#text_m_judgment_result").val(),
                                 "m_analysis_result_brief":$("#text_m_analysis_result_brief").val(),
                                 "m_correspond_method":$("#text_m_correspond_method").val(),
                                 "m_solutions":$("#text_m_solutions").val(),
                                 "setup_date":$("#text_setup_date").val(),
                                 "trouble_happen_date":$("#text_trouble_happen_date").val(),
                                 "use_count":$("#text_use_count").val(),
                                 "use_elapse":$("#text_use_elapse").val()
						};
                        //当是品保人员登陆的时候更新责任去分子段
                        if(isQaManager=="privacy_qa_manager"){
                            doinsertdata["liability_flg"]=$("#select_liability_flg").val();
                        }else if($("input[name='analysis_result']").val()=='11'||$("input[name='analysis_result']").val()=='12'){
                            doinsertdata["liability_flg"]=1;
                        }else if($("input[name='analysis_result']").val()=='21'){
                            doinsertdata["liability_flg"]=2;
                        }else if($("input[name='analysis_result']").val()=='31'||$("input[name='analysis_result']").val()=='32'){
                            doinsertdata["liability_flg"]=3;
                        }else if($("input[name='analysis_result']").val()=='91'||$("input[name='analysis_result']").val()=='92'){
                            doinsertdata["liability_flg"]=9;
                        }
                        
                        var i=0; 
					    $("#append_images input[type='hidden']").each(function(index,ele){
					         var $input=$(ele);
					         if($input.val()!=null && $input.val()!=""){
					        	 doinsertdata["append_images.image_uuid[" + i + "]"] = $input.val();
					        	 doinsertdata["append_images.seq_no[" + i + "]"] = $input.parent().find("label").text();
					            i++;
					         }
					    });
                        
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doupdateAnalysis',
							cache : false,
							data : doinsertdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : insert_complete
						});
					}
				},
				"关闭" : function() { $("#show_Accept").dialog("close")}
			}
		});
})
}

/*删除按钮事件*/
var showdeleteButton = function() {
	var row = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(row);
	for (var i=0; i < g_listdata.length; i++) {
		var g_data = g_listdata[i];
		if (g_data.material_id == rowData.material_id) {
			rowData = g_data;
			break;
		}
	}
	//if (rowData.length == 0) return;
	var data = {
		"model_name" : rowData.model_name,
		"serial_no" : rowData.serial_no,
		"rc_mailsend_date" : rowData.rc_mailsend_date
	}
	$("#show_Accept").text("删除不能恢复。确认要删除记录吗？");
	$("#show_Accept").dialog({
		resizable : false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
				$(this).dialog("close");
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : servicePath + '?method=doDelete',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : insert_complete
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};
/*扫描区域进行扫面过后的进行的操作*/
var doScan_Complete = function(xhrobj) {
	var resInfo = null;
	$("#add_material_id").val("");//扫面过后清空
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);//服务器响应以字符串形式表示eval()
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
			return;
		}
		$("#add_model_name").val(resInfo.materialForm.model_name);
		$("#add_serial_no").val(resInfo.materialForm.serial_no);
		$("#add_sorc_no").val(resInfo.materialForm.sorc_no);
		$("#edit_label_reception_date").text(resInfo.materialForm.reception_time.substring(0,10));//label
		switch (resInfo.materialForm.level) {//从数据库取得转换成字符串
		case "1":$("#add_rank").val("S1级");
			break;
		case "2":$("#add_rank").val("S2级");
			break;
		case "3":$("#add_rank").val("S3级");
			break;
		case "5":$("#add_rank").val("E级");
			break;
		case "6":$("#add_rank").val("A级");
			break;
		case "7":$("#add_rank").val("B级");
			break;
		case "8":$("#add_rank").val("C级");
			break;
		case "9":$("#add_rank").val("D级");
			break;
		}
		if (resInfo.materialForm.direct_flg == 1) {//当direct_flg==1时，工作站选择直送
			$("#add_workshop").val("80").trigger("change");
		}
	} catch (e) {
	}
}
/*区域扫描*/
var doScan = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=recept',
		cache : false,
		data : {material_id: $("#add_material_id").val()},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : insert_complete
	});

};

var enableButton = function() {
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	if (row != null) {// 如果是选中行ID为null值
		var rowdata = $("#list").getRowData(row);
		$("#judgebutton, #editbutton,#mentionbutton").enable();
		if (rowdata.qa_reception_time.trim()) {
			$("#deletebutton").disable();			
		} else {
			$("#deletebutton").enable();			
		}
		if(rowdata.qa_referee_time.trim()){
			$("#analysisbutton").enable();
		}else{
			$("#analysisbutton").disable();
		}
		if(rowdata.qa_reception_time.trim() && !rowdata.qa_referee_time.trim()) {
			$("#backbutton").enable();
		} else {
			$("#backbutton").disable();
		}
	} else {
		$("#judgebutton, #editbutton , #deletebutton,#mentionbutton,#backbutton").disable();	
	}
	if ($("#chooser").css("display") === "none") {
		$("#gbox_list").hide();
	}
}
/* 编辑按钮事件Button */
var editButton = function() {
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
		var rowData = $("#list").getRowData(row);
		var data = {
				"material_id": rowData.material_id
				};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=edit',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : showedit_aditComplete
		});
}
/*insert data */
function insert_complete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#show_Accept").dialog("close");

			infoPop("数据更新成功！", null, "更新成功确认");

			findit();
		}
	} catch (e) {
	}
}

/*QIS受理button 弹出dialog*/
var showedit_accept = function(xhrobj, textStatus) {// 点击受理按钮触发Dialog
	var linkna = "widgets/qa/service_repair_manage_qisaccept.jsp";//load 页面
	$("#show_Accept").hide();
	$("#show_Accept").load(linkna,function(responseText, textStatus, XMLHttpRequest) {
		$("#accept_add_service_repair_flg").html($("#service_repair_flg").html());
		$("#accept_add_service_repair_flg").find("option[value='1']").remove();
		$("#accept_add_service_repair_flg").find("option[value='3']").remove();
//		$("#accept_add_service_repair_flg").find("option[value='9']").remove();
//		$("#accept_add_service_repair_flg").find("option[value='8']").remove();
		$("#accept_add_service_repair_flg").select2Buttons();
		
		$("#add_model_name").autocomplete({
			source : acModelName,
			minLength : 0,
			delay : 100
		});
		$("#add_rank").autocomplete({
			source : acrank,
			minLength : 0,
			delay : 100
		});							
		$("#accept_Manage").validate({
			rules : {
				model_name : {
					required : true,
					maxlength : 30
				},
				serial_no : {
					required : true,
					maxlength : 20
				},
				sorc_no : {
					maxlength : 18
				},
				service_repair_flg : {
					required : true
				},
				rc_mailsend_date : {
					required : true,
					dateISO : true
				}
			},
			ignore : "input[type='text']:hidden"
		});
		$("#show_Accept").dialog({
			width : 320,
			height: 'auto',
			show: "blind",
			modal : true,
			title : "QIS品受理",
			buttons : {
				"确认" : function() {
					if ($("#accept_Manage").valid()) {
						var doinsertdata = {
							"model_name" : $("#add_model_name").val(),
							"serial_no" : $("#add_serial_no").val(),
							"sorc_no" : $("#add_sorc_no").val(),
							"service_repair_flg" : $("#accept_add_service_repair_flg").val(),
							"rc_mailsend_date" : $("#add_rc_mailsend_date").val(),
							"quality_info_no":$("#add_quality_info_no").val()
						};			
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath+ '?method=doAccept',
							cache : false,
							data : doinsertdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : insert_complete
						});
					}
				},
				"关闭" : function() { $("#show_Accept").dialog("close")}
			}
		});
		$("#add_rc_mailsend_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
    })
}


var search_QisPayout = function(xhrobj, textStatus){
	var resInfo = null;
	try{
		// 以Object形式读取JSON responseText获取来自服务器响应的数据
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			if(resInfo.qisPayoutReslut!=null){
				
				$("#add_quality_info_no").val(resInfo.qisPayoutReslut.quality_info_no);
				$("#add_qis_invoice_no").val(resInfo.qisPayoutReslut.qis_invoice_no);
				$("#add_qis_invoice_date").val(resInfo.qisPayoutReslut.qis_invoice_date);
				$("#add_include_month").val(resInfo.qisPayoutReslut.include_month);
				$("#add_charge_amount").val(resInfo.qisPayoutReslut.charge_amount);				
				// $(".qis_payout_edit").show();
				
			}else{
				
				// $(".qis_payout_edit").hide();
			}
		}	
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}

};
//编辑button事件
var showedit_aditComplete = function(xhrobj,textStatus) {// 点击受理按钮触发Dialog
	var linkna = "widgets/qa/service_repair_manage_edit.jsp";//load 页面
	$("#show_Accept").hide();		
	$("#show_Accept").load(linkna,function(responseText, textStatus, XMLHttpRequest) {

		$("#add_service_repair_flg").children("option").eq(0).remove();
		// 有无偿
		$("#add_search_service_free_flg").html(free_flg);
		$("#add_search_service_free_flg").children("option").eq(0).remove();
		// 维修站
		$("#add_workshop").html(shop);
		$("#add_workshop").children("option").eq(0).remove();
		//质量判定
		$("#add_quality_judgment").html(quality_judgment);
		//发行QIS
		$("#add_qis_isuse").html(qis_isuse);
		$("#add_qis_isuse").children("option").eq(0).remove();
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			//产品分类
			$("#add_kind").html(resInfo.kind);
            // 类别select
            $("#add_service_repair_flg").html(resInfo.serviceRepairflgOption);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#add_model_name").text(resInfo.returnForm.model_name);
				$("#add_serial_no").text(resInfo.returnForm.serial_no);
				$("#add_sorc_no").text(resInfo.returnForm.sorc_no);
				$("#add_service_repair_flg").bind("change", function() {
					if (this.value == 2 || this.value == 9 || this.value == 8) {
						$("#add_rank").removeAttr("readonly").removeAttr("disabled").removeAttr("style");
					} else {
						$("#add_rank").attr("readonly", true).attr("style", "border:0;");
					}
				}).val(resInfo.returnForm.service_repair_flg).trigger("change");
				
				//类别选择 保修期内不良
				if(resInfo.returnForm.service_repair_flg == 1){
					// $(".qis_payout_edit").show();
					$(".charge_amount").hide();
					$("#add_include_month").parent().hide();
					$("#add_include_month").parent().prev().hide();
				}else if (resInfo.returnForm.service_repair_flg == 2){//类别选择 QIS不良品
					// $(".qis_payout_edit").show();
				}else{
					// $(".qis_payout_edit").hide();
					$(".charge_amount").hide();
					$("#add_include_month").parent().hide();
					$("#add_include_month").parent().prev().hide();
				};	
				
				$("#add_rank").val(resInfo.returnForm.rank);
				$("#add_rc_mailsend_date").text(resInfo.returnForm.rc_mailsend_date);
				$("#add_rc_ship_assign_date").text(resInfo.returnForm.rc_ship_assign_date);
				
				$("#add_qa_reception_time").text(resInfo.returnForm.qa_reception_time);
				$("#add_qa_referee_time").text(resInfo.returnForm.qa_referee_time);

				if(!$("#add_qa_referee_time").val()==null){
					if(resInfo.returnForm.answer_in_deadline=="2"){
						$("#add_answer_in_deadline").text("◎");
					}else if(resInfo.returnForm.answer_in_deadline=="1"){
						$("#add_answer_in_deadline").text("○");
					}else if(resInfo.returnForm.answer_in_deadline=="0"){
						$("#add_answer_in_deadline").text("╳");
					}
				}else{
					    $("#add_answer_in_deadline").text(" ");
				}
				//判断是否有二次判定日，如果有 本次编缉作为二次判定 是，没有则 否
				var twojudge ="";
				if(!resInfo.returnForm.qa_secondary_referee_date){
					twojudge =0;
				}else{
					twojudge =1;
				}
				$("#add_twojudge input[value="+twojudge+"]").attr("checked", true).trigger('change');
				
				$("#add_kind").val(resInfo.returnForm.kind).trigger("change");
				$("#add_search_service_free_flg").val(resInfo.returnForm.service_free_flg).trigger("change");
				$("#add_workshop").val(resInfo.returnForm.workshop).trigger("change");
				$("#add_countermeasures").val(resInfo.returnForm.countermeasures);
				$("#add_comment").val(resInfo.returnForm.comment);
				$("#add_quality_judgment").val(resInfo.returnForm.quality_judgment).trigger("change");
				$("#add_qis_isuse").val(resInfo.returnForm.qis_isuse).trigger("change");
				$("#add_quality_info_no").val(resInfo.returnForm.quality_info_no);
				$("#add_qis_invoice_no").val(resInfo.returnForm.qis_invoice_no);
				$("#add_qis_invoice_date").val(resInfo.returnForm.qis_invoice_date);
				$("#add_include_month").val(resInfo.returnForm.include_month);
				$("#add_charge_amount").val(resInfo.returnForm.charge_amount);
				$("#add_etq_no").val(resInfo.returnForm.etq_no);

				$("#add_service_repair_flg").change(function(){
					var thisval = $(this).val();
					if(thisval=='1'){
						// $(".qis_payout_edit").show();
						$(".charge_amount").hide();
						$("#add_include_month").parent().hide();
						$("#add_include_month").parent().prev().hide();
					}else if(thisval=='2'){
//								$.ajax({
//									beforeSend : ajaxRequestType,
//									async : true,
//									url : servicePath + '?method=searchQisPayout',
//									cache : false,
//									data : null,
//									type : "post",
//									dataType : "json",
//									success : ajaxSuccessCheck,
//									error : ajaxError,
//									complete : search_QisPayout
//								});
//								$(".qis_payout_edit").show();
						$("#add_include_month").parent().show();
						$("#add_include_month").parent().prev().show();
					}else{
//								$(".qis_payout_edit").show();//$(".qis_payout_edit").hide();
						$(".charge_amount").hide();
						$("#add_include_month").parent().hide();
						$("#add_include_month").parent().prev().hide();
					}
					var add_search_service_free_flg = $("#add_search_service_free_flg").val();
                    if(thisval=='2' && (add_search_service_free_flg=="3" || add_search_service_free_flg=="2")){
                         $(".charge_amount").show();
                    } else {
                         $(".charge_amount").hide();
                    }
				});
                
                 $("#add_search_service_free_flg").change(function(){
					var thisval = $(this).val();
                    if((thisval=="2" || thisval=="3") && $("#add_service_repair_flg").val()=="2"){
                        $(".charge_amount").show();
                    } else {
                    	$(".charge_amount").hide();
                    }
                 });
			}
		} catch (e) {
		}
		
		$("#add_rank").autocomplete({
			source : acrank,
			minLength : 0,
			delay : 100
		});
		/* service_repair_manage_edit数据验证 */
		$("#ins_serviceRepairManage").validate({
			rules : {
				kind:{
					required : true
				},
				service_repair_flg : {
					required : true
				},											
				rank : {
					required : function(){
						if ($("#add_service_repair_flg").val()==1) {
							return true;
						} else {
							return false;
						}
					},
					maxlength : 6
				},
				countermeasures : {
					maxlength : 120
				},
				comment : {
					maxlength : 120
				}
			},
			ignore : "input[type='text']:hidden"
		});
		$("#show_Accept").dialog({
			width : 670,
			height:'auto',
			show: "blind",
			modal : true,//遮罩效果
			title : "保修期内返品+QIS品编辑",
			buttons : {															
			
				"确认" : function() {
					if ($("#ins_serviceRepairManage").valid()) {
					   if($("#add_twojudge input:checked").val()=="1"){
						   var qa_secondary_referee_date="1999/07/31";																	   
					   }else{
						   var qa_secondary_referee_date=null;	
					   }
					   
						var doinsertdata = {
							"model_name" : $("#add_model_name").text(),
							"serial_no" : $("#add_serial_no").text(),
							"rc_mailsend_date" : $("#add_rc_mailsend_date").text(),
							"service_repair_flg" : $("#add_service_repair_flg").val(),										
							"qa_secondary_referee_date":qa_secondary_referee_date,
							"rank" : $("#add_rank").val(),
							"service_free_flg" : $("#add_search_service_free_flg").val(),
							"workshop" : $("#add_workshop").val(),
							"countermeasures" : $("#add_countermeasures").val(),
							"comment" : $("#add_comment").val(),
							"quality_judgment" : $("#add_quality_judgment").val(),
							"qis_isuse" : $("#add_qis_isuse").val(),
							
							"quality_info_no":$("#add_quality_info_no:visible").val(),
							"qis_invoice_no":$("#add_qis_invoice_no").val(),
							"qis_invoice_date":$("#add_qis_invoice_date").val(),
							"include_month":$("#add_include_month:visible").val(),
							"charge_amount":$("#add_charge_amount:visible").val(),
							"kind":$("#add_kind").val(),
							"etq_no":$("#add_etq_no").val()
						};			
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doEdit',
							cache : false,
							data : doinsertdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : insert_complete
						});
					}
				},
				"关闭" : function() { $("#show_Accept").dialog("close")}
			}
		});
		$("#add_twojudge").buttonset();
		$("#add_qis_invoice_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
		$("#add_include_month").datepicker({
			showButtonPanel : true,
			dateFormat : "yymm",
			currentText : "今天"
		});
		$("#add_service_repair_flg,#add_search_service_free_flg,#add_workshop,#add_quality_judgment,#add_qis_isuse,#add_kind").select2Buttons();						
   })
}

/* clearButton */
var clearCondition = function() {
	$("#search_model_name").val("").data("post", "");
	$("#search_serial_no").val("").data("post", "");
	$("#search_sorc_no").val("").data("post", "");
	$("#service_repair_flg").val("").trigger("change").data("post", "");
	$("#search_qa_reception_time_start").val("").data("post", "");
	$("#search_qa_reception_time_end").val("").data("post", "");
	$("#search_qa_referee_time_start").val("").data("post", "");
	$("#search_qa_referee_time_end").val("").data("post", "");
	$("#answer_in_deadline_id").val("").data("post", "");
	$("#search_unfix_back_flg_all").attr("checked", "checked").trigger("change").data("post", "");
	$("#search_service_free_flg").val("").trigger("change").data("post", "");
	$("#search_answer_in_deadline_all").attr("checked", "checked").trigger("change").data("post", "");
	$("#search_workshop").val("").trigger("change").data("post", "");
	$("#search_entity_send_flg_all").attr("checked", "checked").trigger("change").data("post", "");
	$("#search_quality_info_no").val("").data("post", "");
	$("#search_qis_invoice_no").val("").data("post", "");
	$("#search_kind").val("").trigger("change").data("post", "");
	$("#search_etq_no").val("").data("post", "");
};
/* searchButton */
var keepSearchData;
var findit = function(data){
    if(!data){    
     keepSearchData={  
        "model_name" : $("#search_model_name").data("post"),
		"serial_no" : $("#search_serial_no").data("post"),
		"sorc_no" : $("#search_sorc_no").data("post"),
		"service_repair_flg" : $("#service_repair_flg").data("post"),
		"qa_reception_time_start" : $("#search_qa_reception_time_start").data("post"),
		"qa_reception_time_end" : $("#search_qa_reception_time_end").data("post"),
		"qa_referee_time_start" : $("#search_qa_referee_time_start").data("post"),
		"qa_referee_time_end" : $("#search_qa_referee_time_end").data("post"),
		"answer_in_deadline" : $("#answer_in_deadline_id input:checked").data("post"),
		"service_free_flg" : $("#search_service_free_flg").data("post"),
		"unfix_back_flg" : $("#unfix_back_flg_id input:checked").data("post"),
		"workshop" : $("#search_workshop").data("post"),
		"quality_info_no":$("#search_quality_info_no").data("post"),
		"qis_invoice_no":$("#search_qis_invoice_no").data("post"),
		"entity_send_flg":$("#search_entity_send_flg input:checked").data("post"),
		"kind":$("#search_kind").data("post"),
		"etq_no":$("#search_etq_no").data("post")
		 }
		}else{
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
			complete : search_handleComplete
	});
}

function search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
	} else {
		service_repair_list(resInfo.serviceRepairList);
	}
};

function service_repair_list(listdata) {
	g_listdata = listdata;
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();// 清除
		$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [ {current : false} ]);// 刷新列表
	} else {
		$("#list").jqGrid({
			data : listdata,// 数据
			height : 806,// rowheight*rowNum+1
			width : 1248,
			rowheight : 23,
			shrinkToFit:true,
			datatype : "local",  
			colNames : ['责任<br>区分','ETQ单号','使用频率','再修理方案(处理对策)','', '型号', '机身号', '修理单号','产品分类' ,'不良分类', 'RC邮件<br>发送日','隐藏的RC邮件发送日',
					'实物<br>收到日', 'SORC<br>受理日','SORC受理日','QA<br>受理日',
					'QA<br>判定日', '答复<br>时限', 'QA二次<br>判定日', '等级',
					'有无偿', '处理对策', '维修站', 'SORC<br>报价日', '修理<br>同意日',
					'投线日', '修理<br>完了日', '未修理<br>返还','质量<br>判定','质量信息<BR>单号','发行<br>QIS','QIS发送<BR>单号','QIS发送<BR>日期','请款月','请款<BR>金额','备注','提要'],
			colModel : [
			{
				name:'liability_flg',
				index:'liability_flg',
				width:40,
				formatter : 'select',
				editoptions : {
					value : $("#h_goLiabilityFlg").val()
				},
                align : 'center'
			},{
                name : 'etq_no',
                index : 'etq_no',
                width : 60,
                hidden : true
            },{
                name : 'usage_frequency',
                index : 'usage_frequency',
                hidden : true,
                align : 'center'
            },{
                name : 'countermeasures',
                index : 'countermeasures',
                hidden : true,
                align : 'center'
            },{
				name : 'material_id',
				index : 'material_id',
				hidden : true,
				align : 'center'
			}, {
				name : 'model_name',
				index : 'model_name',
				width : 125,
				align : 'left'
			}, {
				name : 'serial_no',
				index : 'serial_no',
				width : 60,
				align : 'left'
			}, {
				name : 'sorc_no',
				index : 'sorc_no',
				width : 50,
				align : 'left'
			}, {
				name : 'kind',
				index : 'kind',
				width : 50,
				formatter : 'select',
				editoptions : {
					value : $("#h_goQaCategoryKind").val()
				}
			}, {
				name : 'service_repair_flg',
				index : 'service_repair_flg',
				align : 'left',
				width : 60,
				formatter : 'select',
				editoptions : {
					value : $("#h_goQaMaterialServiceRepair").val()
				}
			}, {
				name : 'rc_mailsend_date',
				index : 'rc_mailsend_date',
				width : 45,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'hidden_rc_mailsend_date',
				index : 'hidden_rc_mailsend_date',
				width : 45,
				align : 'center',
				hidden:true,
				formatter : function(value, options, rData) {
						return rData.rc_mailsend_date
				}
			}, {
				name : 'rc_ship_assign_date',
				index : 'rc_ship_assign_date',
				width : 45,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'reception_date',
				index : 'reception_date',
				width : 45,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
                name : 'hidden_reception_date',
                index : 'hidden_reception_date',
                width : 45,
                align : 'center',
                hidden:true,
                formatter : function(value, options, rData) {
                		if(rData.reception_date){
                			 return rData.reception_date;
                		}else{
                			return '';
                		}                       
                }
            }, {
				name : 'qa_reception_time',
				index : 'qa_reception_time',
				width : 55,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d H:i:s',
					newformat : 'm-d H\\h'
				}
			}, {
				name : 'qa_referee_time',
				index : 'qa_referee_time',
				width : 55,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d H',
					newformat : 'm-d H\\h'
				}
			}, {
				name : 'answer_in_deadline',
				index : 'answer_in_deadline',
				width : 30,
				align : 'center',
				formatter : function(value, options, rData) {
					if(rData.qa_referee_time){
						if(value==2) {
							return "<font style='font-size:18px;'>◎</font>";
						}else if (value == 1) {
							return "○";
						}else if(value==0){
							return "<font style='color:red;'>╳</font>";
						}else if(value==null) {
							return "<font style='color:red;'></font>";
						}
					}else{
						return "<font style='color:red;'></font>";
					}							
				}
			}, {
				name : 'qa_secondary_referee_date',
				index : 'qa_secondary_referee_date',
				width : 45,
				align : 'center',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'rank',
				index : 'rank',
				width : 40,
				align : 'center'
			}, {
				name : 'service_free_flg',
				index : 'service_free_flg',
				width : 45,
				align : 'left',
				formatter : 'select',
				editoptions : {
					value : $("#h_goServiceFreeFlg").val()
				}
			}, {
				name : 'countermeasures',
				index : 'countermeasures',
				width : 100,
				align : 'left'
			}, {
				name : 'workshop',
				index : 'workshop',
				width : 50,
				align : 'left',
				formatter : 'select',
				editoptions : {
					value : $("#h_goWorkshop").val()
				}
			}, {
				name : 'quotation_date',
				index : 'quotation_date',
				width : 50,
				align : 'center',
				sorttype : 'date',
				hidden:true,
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'agreed_date',
				index : 'agreed_date',
				width : 50,
				align : 'center',
				hidden:true,
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'inline_date',
				index : 'inline_date',
				width : 50,
				align : 'center',
				hidden : true,
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'outline_date',
				index : 'outline_date',
				width : 50,
				align : 'center',
				hidden : true,
				formatter : function(value, options, rData){
					if(rData.unfix_back_flg != 1){
						if (value) {
							var d = new Date(value);
							return mdTextOfDate(d);
						}
					}
					return ' ' ;
				}
			}, {
				name : 'unfix_back_flg',
				index : 'unfix_back_flg',
				width : 45,
				align : 'center',
				formatter : function(value, options, rData){
					if(value==1){
						if (rData.outline_date) {
							var d = new Date(rData.outline_date);
							return mdTextOfDate(d);
						}
					}
					return ' ' ;
				},
				hidden : true
				
			},{
				name : 'quality_judgment',
				index : 'quality_judgment',
				width : 40,
				formatter : 'select',
				editoptions : {
					value : $("#h_goQualityJudgment").val()
				},
				hidden:true
			}, {
				name : 'quality_info_no',
				index : 'quality_info_no',
				width : 60,
				hidden:true,
				align : 'left'
			},{
				name : 'qis_isuse',
				index : 'qis_isuse',
				width : 40,
				formatter : 'select',
				editoptions : {
					value : $("#h_goQisIsuse").val()
				},
				hidden:true
			},{
				name : 'qis_invoice_no',
				index : 'qis_invoice_no',
				width : 60,
				hidden:true,
				align : 'left'
			},{
				name : 'qis_invoice_date',
				index : 'qis_invoice_date',
				width : 60,
				hidden:true,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			},{
				name : 'include_month',
				index : 'include_month',
				width : 50,
				hidden:true,
				align : 'left'
			},{
				name : 'charge_amount',
				index : 'charge_amount',
				width : 60,
				hidden:true,
				align : 'right',
				formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}
			},{
				name : 'comment',
				index : 'comment',
				width : 80,
				align : 'left'
			},{
				name : 'mention',
				index : 'mention',
				width : 80,
				align : 'left',
				hidden:true
			} ],
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
			onSelectRow : enableButton,// 当选择行时触发此事件。
			ondbClickRow : function(rid, iRow, iCol, e) {

			},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {// 当表格所有数据都加载完成而且其他的处理也都完成时触发
				//enableButton();

				// 得到显示到界面的id集合
				var IDS = $("#list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var mention = rowData["mention"];
					//当提要内容不为空时，设置行首TD的背景颜色为黄色
					if(mention){
						pill.find("tr#" +IDS[i] +  " td:first").removeClass("ui-state-default jqgrid-rownum").css("background-color","yellow");
						
						(function($td,mention){
							$td.hover(function(e){
								$td.attr("title","");
								var xOffset = 10;
								var yOffset = 20;
								var w = $(window).width();
								
								$("body").append("<div id='preview'>" + mention + "</div>");
								$("#preview").css({
									position:"absolute",
									padding:"6px",
									border:"1px solid #cccccc",
									color:"#000",
									fontSize:"14px",
									backgroundColor:"white",
									top:(e.pageY - xOffset) + "px",
									zIndex:1000
								});
								if(e.pageX < w/2){
									$("#preview").css({
										left: e.pageX + xOffset + "px",
										right: "auto"
									}).fadeIn("fast");
								}else{
									$("#preview").css("right",(w - e.pageX + yOffset) + "px").css("left", "auto").fadeIn("fast");	
								}
							},function(){
								$("#preview").remove();
							});
						})(pill.find("tr#" +IDS[i] +  " td:first"),mention);
					}
				}
			
			}

		});
	}
};