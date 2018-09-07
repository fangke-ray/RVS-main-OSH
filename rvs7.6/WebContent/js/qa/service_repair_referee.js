var servicePath="service_repair_referee.do";

var pauseOptions="";
var stepOptions="";
var pauseComments = {};

$(function(){
	$("#add_rc_mailsend_date,#add_rc_ship_assign_date,#add_qis_invoice_date").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	$("#add_service_repair_flg,#add_search_service_free_flg,#add_workshop,#add_kind,#add_quality_judgment,#add_qis_isuse").select2Buttons();
	$("input.ui-button").button();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	
	/* button */
	$("input.ui-button").button();
	
	/*页面初始化显示JqGrid*/
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		doStart();
	}
	});
	$("#scanner_inputer").keyup(function(){
	if (this.value.length >= 11) {
		doStart();
	}
	});		
	/*选择QIS不良品发生的事件*/
	$("#add_service_repair_flg").change(function(){
		if($(this).val()=='2' || $(this).val() == '9' || $(this).val() == '8' || $(this).val() == '4'){
			$("tr.qis_payout").show();
			$("#add_rank").removeAttr("readonly").removeAttr("style");
		}else{
			$("tr.qis_payout").hide();
			$("#add_rank").attr("readonly", true).attr("style", "border:0;");
		}
        //点击选择保修期内不良时，显示(分析(未完成)按钮)---1、选择保修期内不良时，分析内容全部完成，则分析按钮显示已完成；否则，显示未完成
        if($(this).val()=='1'){
           $("#analysis_button").show(); 
            var detaildata ={
	            "model_name":$("#add_model_name").text(),
	            "serial_no":$("#add_serial_no").text(),
	            "rc_mailsend_date":$("#add_rc_mailsend_date").val()
	        }
	        $.ajax({
	                beforeSend : ajaxRequestType,
	                async : true,
	                url : servicePath + '?method=detail',
	                cache : false,
	                data : detaildata,
	                type : "post",
	                dataType : "json",
	                success : ajaxSuccessCheck,
	                error : ajaxError,
	                complete : analysis_detail_Complete
	        });
           
        }else{
           $("#analysis_button").hide();
        }
	});
    
    //分析(未完成)按钮事件
    $("#analysis_button").button().click(function(){
        var detaildata ={
            "model_name":$("#add_model_name").text(),
            "serial_no":$("#add_serial_no").text(),
            "rc_mailsend_date":$("#add_rc_mailsend_date").val()
        }
        $.ajax({
                beforeSend : ajaxRequestType,
                async : true,
                url : servicePath + '?method=detail',
                cache : false,
                data : detaildata,
                type : "post",
                dataType : "json",
                success : ajaxSuccessCheck,
                error : ajaxError,
                complete : show_analysis_Complete
        });
    })
    
  $("#ins_serviceRepairManage").validate({
		rules : {	
			kind:{
				required : true
			},
			service_repair_flg : {
				required : true
			},
			rc_mailsend_date : {
				required : true,
				dateISO : true
			},
			rc_ship_assign_date : {
				dateISO : true
			},
			rank : {
				required : function(){
					if($("#add_service_repair_flg").val() == "2"){
						return true;
					}else{
						return false;
					}
				},
				maxlength : 6
			},
			service_free_flg : {
				required : true
			},
			workshop : {
				required : true
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

	if (typeof(opd_load) != "function") {
		loadJs(
			"js/data/operator-detail.js",
			function(){
				opd_load($("#waitarea .ui-widget-header"));
			});
	} else {
		opd_load($("#waitarea .ui-widget-header"));
	}

	var getStepIntoPcsComment = function(updatedata){
		$steps = $("#edit_step_name option[selected]");
		if ($steps.length > 0) {
			var retString = "";
			$steps.each(function(idx, ele){
				if (idx==0) {
					retString += ele.innerText || ele.textContent;
				} else {
					retString += "\n" + (ele.innerText || ele.textContent);
				}
			});
			var json = {};
			json["GC6210100"] = retString;
			return Json_to_String(json);
		} else {
			return null;
		}
	}
	var rComplete = function(){
		if ($("#ins_serviceRepairManage").valid()) {
			var updatedata ={
				"material_id":$("#material_id").val(),
				"model_name":$("#add_model_name").text(),
				"serial_no":$("#add_serial_no").text(),
				"service_repair_flg":$("#add_service_repair_flg").val(),
				"rc_mailsend_date":$("#add_rc_mailsend_date").val(),		
				"rc_ship_assign_date":$("#add_rc_ship_assign_date").val(),
				"qa_reception_date":$("#edit_label_reception_date").val(),
				"rank":$("#add_rank").val(),
				"service_free_flg":$("#add_search_service_free_flg").val(),
				"workshop":$("#add_workshop").val(),
				"countermeasures":$("#add_countermeasures").val(),
				"comment":$("#add_comment").val(),
				"qis_invoice_no":$("#add_qis_invoice_no").val(),
                "charge_amount":$("#add_charge_amount").val(),
				"qis_invoice_date":$("#add_qis_invoice_date").val(),
				"quality_info_no" : $("#add_quality_info_no:visible").val(),
				"qa_referee_time": '1999/07/31 00:00:00',
				"kind":$("#add_kind").val(),
				"quality_judgment":$("#add_quality_judgment").val(),
				"qis_isuse":$("#add_qis_isuse").val(),
				"etq_no":$("#add_etq_no").val()
			}
			updatedata.pcs_comments = getStepIntoPcsComment();
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doUpdate',
				cache : false,
				data : updatedata,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : search_handleComplete
			});
		}
	}
	$("#refereeCompletebutton").click(function(){makeStep(rComplete)});

	$("#pause_button").click(function(){
		makePause();
	});

	$("#endpause_button").click(function(){
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=doEndpause',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhr, status) {
				showEndpause();
			}
		});
	});

	// ins_serviceRepairManage

	$("#break_button").click(function(){makeStep(function(){
		var updatedata = {
			"material_id":$("#material_id").val(),
			"model_name":$("#add_model_name").text(),
			"serial_no":$("#add_serial_no").text(),
			"service_repair_flg":$("#add_service_repair_flg").val(),
			"rc_mailsend_date":$("#add_rc_mailsend_date").val(),		
			"rc_ship_assign_date":$("#add_rc_ship_assign_date").val(),
			"qa_reception_date":$("#edit_label_reception_date").val(),
			"rank":$("#add_rank").val(),
			"service_free_flg":$("#add_search_service_free_flg").val(),
			"workshop":$("#add_workshop").val(),
			"countermeasures":$("#add_countermeasures").val(),
			"comment":$("#add_comment").val(),
			"qis_invoice_no":$("#add_qis_invoice_no").val(),
			"qis_invoice_date":$("#add_qis_invoice_date").val(),
			"quality_info_no" : $("#add_quality_info_no:visible").val(),
			"kind":$("#add_kind").val(),
			"quality_judgment":$("#add_quality_judgment").val(),
			"qis_isuse":$("#add_qis_isuse").val(),
			"etq_no":$("#add_etq_no").val()
		}

		updatedata.pcs_comments = getStepIntoPcsComment();
		$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doUpdate',
				cache : false,
				data : updatedata,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : search_handleComplete
		});
	})});
});
var analysis_detail_Complete = function(xhrobj, textStatus){
 var resInfo = null;
        try {
            // 以Object形式读取JSON
            eval('resInfo =' + xhrobj.responseText);
            if (resInfo.errors.length > 0) {
                // 共通出错信息框
                treatBackMessages(null, resInfo.errors);
            } else {
                //alert(resInfo.returnForm.trouble_cause)
                //alert(resInfo.returnForm.analysis_no !="" && resInfo.returnForm.customer_name!="" && resInfo.returnForm.last_shipping_date!="" && resInfo.returnForm.last_sorc_no!="" && resInfo.returnForm.last_ocm_rank!="" && resInfo.returnForm.last_rank!="" && resInfo.returnForm.last_trouble_feature!="" && resInfo.returnForm.fix_demand!="" && resInfo.returnForm.trouble_discribe!="" && resInfo.returnForm.trouble_cause!="" && resInfo.returnForm.analysis_correspond_suggestion!="")
                //当所有可修改编辑的内容框均有值时，分析按钮显示分析表(未完成)；否则显示，分析表(已完成)
                //if(resInfo.returnForm.analysis_no !="" && resInfo.returnForm.customer_name!="" && resInfo.returnForm.last_shipping_date!="" && resInfo.returnForm.last_sorc_no!="" && resInfo.returnForm.last_ocm_rank!="" && resInfo.returnForm.last_rank!="" && resInfo.returnForm.last_trouble_feature!="" && resInfo.returnForm.fix_demand!="" && resInfo.returnForm.trouble_discribe!="" && resInfo.returnForm.trouble_cause!="" && resInfo.returnForm.analysis_correspond_suggestion!=""){
                if(resInfo.isFull=="true"){
                    $("#analysis_button").val("分析表(已完成)");
                    $("#refereeCompletebutton").enable();      
		        }else if(resInfo.isFull=="false"){
		            $("#analysis_button").val("分析表(未完成)");
                    //$("#refereeCompletebutton").disable();
		        }
            }
        }catch (e) {
        }
}

/*分析button 弹出dialog*/
var show_analysis_Complete = function(xhrobj, textStatus) {// 点击分析button触发Dialog
    var linkna = "widgets/qa/service_repair_referee_analysis.jsp";//load 页面   

    $("#show_Accept").hide();   
    $("#show_Accept").load(linkna,function(responseText, textStatus, XMLHttpRequest){

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
        $("#edit_trouble_cause_date,#edit_trouble_cause_complete").datepicker({
            showButtonPanel : true,
            dateFormat : "yy年mm月dd日",
            currentText : "今天"
        });
          
        $("#text_last_shipping_date").datepicker({
            showButtonPanel : true,
            dateFormat : "yy/mm/dd",
            currentText : "今天"
        });     

        $("#img_container").mouseover(function(){
           $("#delete_circle").show();        
        });

        $("#delete_circle").click(function(){
                $("#img_container").hide();
                $(this).hide();
        });
        
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
                //(产品名称)型号
                $("#label_model_name").text($("#add_model_name").text());
                //BodyNo.(机身号)
                $("#label_serial_no").text($("#add_serial_no").text());
                //客户名称
                $("#edit_customer_name").val(resInfo.returnForm.customer_name);
                //上次完成日
                $("#text_last_shipping_date").val(resInfo.returnForm.last_shipping_date);
                //上次修理NO.
                $("#edit_sorc_no").val(resInfo.returnForm.last_sorc_no);
                //等级---OCM
                $("#text_last_ocm_rank").val(resInfo.returnForm.last_ocm_rank);
                //等级---翻修技术部
                $("#text_last_rank").val(resInfo.returnForm.last_rank);
                 //上次不合格内容
                $("#text_last_trouble_feature").val(resInfo.returnForm.last_trouble_feature);
                //此次SORC受理日
                $("#label_reception_date").text($("#edit_label_reception_date").text());
                //此次修理NO.
                $("#label_sorc_no").text($("#add_sorc_no").text());
                //此次使用情况
                $("#text_usage_frequency").val(resInfo.returnForm.usage_frequency);
                //此次不合格内容
                $("#text_fix_demand").val(resInfo.returnForm.fix_demand);
                // 当责任分区有值时,将代表此值的radio选中,其他radio一概不可点击选择
                if (resInfo.returnForm.analysis_result != null) {
                    $("#table_checkbox input[value=" + resInfo.returnForm.analysis_result+"]").attr("checked", true);
                    $("#table_checkbox input[value!=" + resInfo.returnForm.analysis_result+"]").attr("disabled", true);
                }
                //再修理方案
                $("#label_countermeasures").text(resInfo.returnForm.countermeasures);
                //原因--结论
                $("#text_trouble_cause").val(resInfo.returnForm.trouble_cause);
                //故障--技术检测/分析详述
                $("#text_trouble_discribe").val(resInfo.returnForm.trouble_discribe);
                //对应(处理对策)
                $("#textarea_analysis_correspond_suggestion").val(resInfo.returnForm.analysis_correspond_suggestion);
                //$("#select_analysis_result").val(resInfo.returnForm.analysis_result).trigger("change");
                //$("#select_liability_flg").val(resInfo.returnForm.liability_flg).trigger("change");
                //$("#select_manufactory_flg input[value="+resInfo.returnForm.manufactory_flg+"]").attr("checked", true).trigger('change');
                
                //$("#text_append_component").val(resInfo.returnForm.append_component);
                //$("#text_quantity").val(resInfo.returnForm.quantity);
                //$("#text_loss_amount").val(resInfo.returnForm.loss_amount);
                //$("#text_last_sorc_no,#edit_sorc_no").val(resInfo.returnForm.last_sorc_no);
                //$("#text_last_shipping_date").val(resInfo.returnForm.last_shipping_date);
                
                //$("#text_wash_feature").val(resInfo.returnForm.wash_feature);
                //$("#text_disinfect_feature").val(resInfo.returnForm.disinfect_feature);
                //灭菌
                //$("#text_steriliza_feature").val(resInfo.returnForm.steriliza_feature);
                
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
								"rc_mailsend_date": $("#add_rc_mailsend_date").val()
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
            }
            $("#text_last_shipping_date").datepicker({
                showButtonPanel : true,
                dateFormat : "yy/mm/dd",
                currentText : "今天"
            });
        } catch (e) {
        }
        
        $("#show_Accept").dialog({
            width : 'auto',
            height:'auto',
            show: "blind",
            modal : true,//遮罩效果
            title : "保修期内返修品分析表",
            buttons : {                                                         
                "确认" : function() {
                        var doinsertdata = {
                              "model_name": $("#label_model_name").text(), 
                              "serial_no":$("#label_serial_no").text(), 
                              "rc_mailsend_date":$("#add_rc_mailsend_date").val(),
                              "analysis_no":$("#text_analysis_no").val(),
                              "customer_name":$("#edit_customer_name").val(),
                              "fix_demand": $("#text_fix_demand").val(),
                              "trouble_cause":$("#text_trouble_cause").val(), 
                              "trouble_discribe":$("#text_trouble_discribe").val(), 
                              "analysis_result":$("input:radio:checked").val(),
                              "manufactory_flg":$("#select_manufactory_flg input:checked").val(), 
                              "append_component":$("#text_append_component").val(), 
                              "quantity":$("#text_quantity").val(), 
                              "loss_amount":$("#text_loss_amount").val(),
                              "last_sorc_no":$("#edit_sorc_no").val(), 
                              "last_shipping_date":$("#label_reception_date").text(),
                              "last_rank":$("#text_last_rank").val(),
                              "last_ocm_rank":$("#text_last_ocm_rank").val(),
                              "last_trouble_feature":$("#text_last_trouble_feature").val(), 
                              "wash_feature":$("#text_wash_feature").val(), 
                              "disinfect_feature":$("#text_disinfect_feature").val(), 
                              "steriliza_feature":$("#text_steriliza_feature").val(),
                              "usage_frequency":$("#text_usage_frequency").val(),
                              "service_repair_flg":$("#hidden_service_repair_flg_val").val(),
                              "last_shipping_date":$("#text_last_shipping_date").val(),
                              "analysis_correspond_suggestion":$("#textarea_analysis_correspond_suggestion").val(),
                              
                              //上传图片ID
                              "image_uuid":$("#photo_uuid").val()
                        };   
                        //更新责任区分
                        if($("input[name='analysis_result']").val()=='11'||$("input[name='analysis_result']").val()=='12'){
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
                },
                "关闭" : function() { $("#show_Accept").dialog("close")}
            }
        });
})
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
            
            var detaildata ={
                "model_name":$("#add_model_name").text(),
                "serial_no":$("#add_serial_no").text(),
                "rc_mailsend_date":$("#add_rc_mailsend_date").val()
            }
            $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath + '?method=detail',
                    cache : false,
                    data : detaildata,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete : analysis_detail_Complete
            });
            
            $("#show_Accept").dialog("close");

            $("#show_Accept").text("数据更新成功！");
            $("#show_Accept").dialog({
                resizable : false,
                modal : true,
                title : "更新成功确认",
                buttons : {
                    "确认" : function() {
                        $(this).dialog("close");
                    }
                }
            });
            findit();
        }
    } catch (e) {
    }
}

var showPause = function(resInfo) {
	if ($("#scanner_container").is(":visible")) {
		data_list(resInfo.resultForm);
		$("#material_details").show();
		$("#scanner_container").hide();
	}

	$("#pause_button").hide();
	$("#endpause_button").show();
	$("#ins_serviceRepairManage .condform").find("input,textarea,select,a").disable();
	$("#refereeCompletebutton").disable();
	$("#break_button").disable();
}

var showEndpause = function() {
	$("#pause_button").show();
	$("#endpause_button").hide();
	$("#ins_serviceRepairManage .condform").find("input,textarea,select,a").enable();
	$("#refereeCompletebutton").enable();
	$("#break_button").enable();
}

/** 暂停信息弹出框 */
var makePauseDialog = function(jBreakDialog) {
	jBreakDialog.dialog({
		title : "暂停信息编辑",
		width : 760,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if ($("#pauseo_edit").parent().valid()) {
					var data = {
						reason : $("#pauseo_edit_pause_reason").val()
					};

					if ($("#pauseo_edit_comments").is(":hidden")) {
						data.comments = $("#pauseo_edit_comments_select").val();
					} else {
						data.comments = $("#pauseo_edit_comments").val()
					}

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doPause',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhr, status) {
							jBreakDialog.dialog("close");
							showPause(xhr, status);
						}
					});
				}
			}, "关闭" : function(){ jBreakDialog.dialog("close"); }
		}
	});
}

/** 暂停信息 */
var makePause = function() {
	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();
	// 导入暂停画面
	jBreakDialog.load("widget.do?method=pauseoperator",
		function(responseText, textStatus, XMLHttpRequest) {
			// 设定暂停理由
			$("#pauseo_edit").show();
			$("#pauseo_show").hide();
			$("#pauseo_edit_pause_reason").html(pauseOptions);
			$("#pauseo_edit_pause_reason").select2Buttons().change(function(){
				var thisval = this.value;
				if (pauseComments[thisval]) {
					$("#pauseo_edit_comments_select")
					.next(".select2Buttons").remove()
					.end()
					.show().html(createCommentOptions(pauseComments[thisval]))
					.select2Buttons();
					$("#pauseo_edit_comments").hide();
				} else {
					$("#pauseo_edit_comments_select").hide()
					.next(".select2Buttons").remove()
					.end()
					.html("");
					$("#pauseo_edit_comments").show();
				}
			});

			$("#pauseo_edit_pause_reason").val("").trigger("change");
			makePauseDialog(jBreakDialog);
		});
};

/** 暂停信息弹出框 */
var makeStepDialog = function(jBreakDialog, method) {
	jBreakDialog.dialog({
		title : "确认刚才完成工作步骤",
		width : 480,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if (method) {
					method();
				} else {
					jBreakDialog.dialog("close");
				}
			}, "关闭" : function(){ jBreakDialog.dialog("close"); }
		}
	});
}

/** 工步信息 */
var makeStep = function(method) {
	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();
	// 导入工步画面
	jBreakDialog.load("widget.do?method=stepoperator",
		function(responseText, textStatus, XMLHttpRequest) {
			// 设定工步
			$("#edit_step_name").html(stepOptions);
			$("#edit_step_name").select2Buttons();

			makeStepDialog(jBreakDialog, method);
		});
};

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
				$("#quality_info_no").text(resInfo.qisPayoutReslut.quality_info_no);
				$("#qis_invoice_no").text(resInfo.qisPayoutReslut.qis_invoice_no);
				$("#qis_invoice_date").text(resInfo.qisPayoutReslut.qis_invoice_date);
				$("#include_month").text(resInfo.qisPayoutReslut.include_month);
				$("#charge_amount").text(resInfo.qisPayoutReslut.charge_amount);
				$("#qis_payout").slideToggle();
			}else{
				$("#qis_payout").hide();
			}
		}	
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
};


var doStart=function(select_input){
	var data ={material_id : select_input ? select_input : $("#scanner_inputer").val()}
	$("#scanner_inputer").attr("value", "");
	$("#waitings, #uld_list").removeClass("waitForStart");
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};

var showStart = function(resInfo) {
	data_list(resInfo.resultForm);
	pause_list(resInfo.serviceRepairPausedList);
	$("#material_details").show();
	$("#scanner_container").hide();
}


var doStart_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try{
		// 以Object形式读取JSON responseText获取来自服务器响应的数据
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			showStart(resInfo);
		}	
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
}

var data_list = function(resultdata){
	$("#material_id").val(resultdata.material_id);
	$("#add_model_name").text(resultdata.model_name);
	$("#add_serial_no").text(resultdata.serial_no);
	$("#add_sorc_no").text(resultdata.sorc_no);
	$("#edit_label_reception_date").text(resultdata.reception_date);
	$("#add_rank").val(resultdata.rank);
	$("#add_service_repair_flg").val(resultdata.service_repair_flg).trigger("change");
	$("#add_quality_info_no").val(resultdata.quality_info_no);
	
    if ($("#add_service_repair_flg").val() == "2") {
		$("tr.qis_payout").show();
	} else {
		$("tr.qis_payout").hide();
	}
    
	$("#add_qis_invoice_no").val(resultdata.qis_invoice_no),
	$("#add_qis_invoice_date").val(resultdata.qis_invoice_date),
	$("#add_rc_mailsend_date").val(resultdata.rc_mailsend_date);
	$("#add_rc_ship_assign_date").val(resultdata.rc_ship_assign_date);
	$("#add_search_service_free_flg").val(resultdata.service_free_flg).trigger("change");
    
    if ($("#add_service_repair_flg").val() == "1" && $("#add_search_service_free_flg").val()=="3") {
      $("tr .charge_amount").show();
    } else {
      $("tr .charge_amount").hide();
    }
//    $("#add_service_repair_flg").change(function(){ TODO 哪里的逻辑？
//
//        if($(this).val()=='1'){
//           $("#add_search_service_free_flg").change(function(){
//              if($(this).val()=="3"){
//                  $(".charge_amount").show();
//              }
//           });
//        }
//    });
//    
//	 $("#add_search_service_free_flg").change(function(){
//	        if($(this).val()=="3"){
//	           $("#add_service_repair_flg").change(function(){
//	              if($(this).val()=="1"){
//	                  $(".charge_amount").show();
//	              }
//	           });
//	        }
//	  });
	$("#add_workshop").val(resultdata.workshop).trigger("change");
	$("#add_countermeasures").val(resultdata.countermeasures);
	$("#add_comment").val(resultdata.comment);
	$("#mention").html(resultdata.mention || "");
	$("#add_kind").val(resultdata.kind).trigger("change");
	$("#add_quality_judgment").val(resultdata.quality_judgment).trigger("change");
	$("#add_qis_isuse").val(resultdata.qis_isuse).trigger("change");
	$("#add_etq_no").val(resultdata.etq_no);
}

var pause_list = function(serviceRepairPausedList){
	// 建立暂停中断等待区一览
	var waiting_html = '<div class="ui-state-default w_group" style="width: 380px; margin-top: 12px; margin-bottom: 8px; padding: 2px;">'+ serviceRepairPausedList.length +' 台:</div>';
	for (var iwaiting = 0; iwaiting < serviceRepairPausedList.length; iwaiting++) {
		var waiting = serviceRepairPausedList[iwaiting];
		var mdl_nm = waiting.model_name;
		mdl_nm = '<div class="tube-liquid' + expeditedColor(waiting.answer_in_deadline)  + '">'
//		 + waiting.material_id + ' => '
		 + (waiting.qa_reception_time && waiting.qa_reception_time.length > 10 ? waiting.qa_reception_time.substring(0,10) + ' | ' : "")
		 + waiting.model_name + ' | '
		 + waiting.serial_no + ' | '
		 + (waiting.sorc_no == null ? "" : waiting.sorc_no)
		 + '</div>'
		 + '<div class="click_start"><input type="button" value="》开始"></div>';

		waiting_html += '<div class="waiting tube" id="w_' + waiting.material_id + '">' + mdl_nm +
						'</div>'
	}
	var $waiting_html = $(waiting_html);
	$waiting_html.find("input:button").button().click(function(){
		var w_id = $(this).parent().parent().attr("id");
		if (w_id && w_id.length > 2) {
			w_id = w_id.substring(2);
			doStart(w_id);
		}
	});
	$("#waitings").html($waiting_html);
}

var showBreakOfInfect = function(infectString) {
	var $break_dialog = $('#break_dialog');
	$break_dialog.html(decodeText(infectString));

	$break_dialog.dialog({
		modal : true,
		resizable:false,
		dialogClass : 'ui-error-dialog',
		width : 'auto',
		title : "工位工作不能进行",
		closeOnEscape: false,
		close: function(){
			window.location.href = "./panel.do?method=init";
		},
		buttons :{
			"确定":function() {
				window.location.href = "./usage_check.do?from=position";
			}
		}
	});
}

function search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		if ($("#break_dialog").is(":visible")) {
			$("#break_dialog").dialog("close");
		}

		if (resInfo.workstauts == -1) {
			showBreakOfInfect(resInfo.infectString);
			return;
		}

		service_repair_list(resInfo.serviceRepairList);
		pause_list(resInfo.serviceRepairPausedList);
		$("#waitings, #uld_list").addClass("waitForStart");
		$("#material_details").hide();
		$("#scanner_container").show();
		if(resInfo.resultForm) {
			$("#waitings, #uld_list").removeClass("waitForStart");
			if (resInfo.resultForm.operate_result == 1) {
				showStart(resInfo);
			} else {
				showPause(resInfo);
			}
		}
		if(resInfo.sRank) {
			$("#add_rank").autocomplete({
				source : resInfo.sRank,
				minLength : 0,
				delay : 100
			});
		}
		if(resInfo.pauseOptions) {
			pauseOptions = resInfo.pauseOptions;
						pauseComments = $.parseJSON(resInfo.pauseComments);

		}
        
		if(resInfo.stepOptions) {
			stepOptions = resInfo.stepOptions;
		}
	}
};

function service_repair_list(listdata) {
	if ($("#gbox_list").length > 0) {
		$("#uld_list").jqGrid().clearGridData();// 清除
		$("#uld_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [ {current : false} ]);// 刷新列表

	} else {
		$("#uld_list").jqGrid({
			data : listdata,// 数据
			width :992,
			rowheight : 23,
			shrinkToFit:true,
			datatype : "local",  
			colNames : [ '', '型号', '机身号', '修理单号', '类别', 'RC邮件发送日', '实物收到日', 'SORC受理日', '质量信息单号'],
			colModel : [ {
				name : 'material_id',
				index : 'material_id',
				// hidden:true,
				width : 35,
				align : 'center',
				formatter : function(value, options, rData){
					return '<input type="button" value="开始" class="click_start" onclick="doStart(\''+value+'\')"/>';
				}
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
				width : 75,
				align : 'left'
			}, {
				name : 'service_repair_flg',
				index : 'service_repair_flg',
				align : 'left',
				width : 60,
				formatter : 'select',
				editoptions : {
					value : $("#hidden_service_repair_flg").val()
				}
			}, {
				name : 'rc_mailsend_date',
				index : 'rc_mailsend_date',
				width : 60,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			}, {
				name : 'rc_ship_assign_date',
				index : 'rc_ship_assign_date',
				width : 60,
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
				width : 60,
				align : 'center',
				sorttype : 'date',
				formatter : 'date',
				formatoptions : {
					srcformat : 'Y/m/d',
					newformat : 'm-d'
				}
			},{
				name : 'quality_info_no',
				index : 'quality_info_no',
				width : 75,
				align : 'left'
			}],
			rowNum : 20,
			toppager : false,
			pager : "#uld_listpager",
			viewrecords : true,
			caption : "待判定维修对象一览",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, 
			pginput : false,
			recordpos : 'left',
			ondbClickRow : function(rid, iRow, iCol, e) {},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
				$(this).find("input:button").button();
			}
		});
	}

	
};

var expeditedColor = function(expedited) {
	if (expedited > 8) return ' tube-red'; // 加急
	if (expedited > 7) return ' tube-green'; // 普通
	return ' tube-blue'; // 当日
}

var createCommentOptions = function(comments){
	var commentArray = comments.split(";");
	var sRet = "";
	for (var commentIdx in commentArray) {
		var comment = commentArray[commentIdx];
		sRet += "<option value='" + comment + "'>" + comment + "</option>";
	}
	return sRet;
}
