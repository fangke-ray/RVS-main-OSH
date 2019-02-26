var servicePath="check_unqualified_record.do";
var datalist=[];

$(function(){
    $("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    $("input.ui-button").button();
    //产品处理内容  工位对处  设备对处  设备对处结果
    $("#search_product_content,#search_position_handle,#search_object_handle,#search_object_final_handle_result,#edit_position_handle,#edit_object_handle,#edit_product_handle,#edit_object_final_handle_result").select2Buttons();
    
    //发生日期    修理开始日期
    $("#search_happen_time_start, #search_happen_time_end,#search_repair_date_start_start,#search_repair_date_start_end,#edit_repair_start_date,#edit_repair_end_date").datepicker({
        showButtonPanel:true,
        dateFormat: "yy/mm/dd",
        currentText: "今天"
    });

    //检索
    $("#searchbutton").click(function(){
          $("#search_manage_code").data("post",$("#search_manage_code").val());//管理编号  
          $("#search_name").data("post",$("#search_name").val());//品名
          $("#search_check_item").data("post",$("#search_check_item").val());//不合格项目
          $("#search_product_content").data("post",$("#search_product_content").val());//产品处理内容
          $("#search_position_handle").data("post",$("#search_position_handle").val());//工位对处
          $("#search_object_handle").data("post",$("#search_object_handle").val());//设备对处
          $("#search_object_final_handle_result").data("post",$("#search_object_final_handle_result").val());//设备对处结果
          $("#search_happen_time_start").data("post",$("#search_happen_time_start").val());//发生日期开始
          $("#search_happen_time_end").data("post",$("#search_happen_time_end").val());//发生日期结束
          $("#search_repair_date_start_start").data("post",$("#search_repair_date_start_start").val());//修理开始日期开始
          $("#search_repair_date_start_end").data("post",$("#search_repair_date_start_end").val());//修理开始日期结束
          findit();
    });

    //清除
    $("#resetbutton").click(function(){
          reset();
    });
    $("#goback").click(function(){
         $("#main").show();
         $("#detail").hide();
    });

    $("#reset2button").click(function(){
          $("#main").show();
          $("#detail").hide();
    });
    
    reset();
    
    findit();
    
});

//清除函数
var reset=function(){
	  $("#search_manage_code").data("post","").val("");//管理编号  
	  $("#search_name").data("post","").val("");//品名
	  $("#search_check_item").data("post","").val("");//不合格项目
	  $("#search_product_content").data("post","").val("").trigger("change");//产品处理内容
	  $("#search_position_handle").data("post","").val("").trigger("change");//工位对处
	  $("#search_object_handle").data("post","").val("").trigger("change");//设备对处
	  $("#search_object_final_handle_result").data("post","").val("").trigger("change");//设备对处结果
	  $("#search_happen_time_start").data("post","").val("");//发生日期开始
	  $("#search_happen_time_end").data("post","").val("");//发生日期结束
	  $("#search_repair_date_start_start").data("post","").val("");//修理开始日期开始
	  $("#search_repair_date_start_end").data("post","").val("");//修理开始日期结束
};

//检索函数
var findit=function(){
    var data={
        "manage_code":$("#search_manage_code").data("post"),
        "name":$("#search_name").data("post"),
        "check_item":$("#search_check_item").data("post"),
        "product_content":$("#search_product_content").data("post"),
        "position_handle":$("#search_position_handle").data("post"),
        "object_handle":$("#search_object_handle").data("post"),
        "object_final_handle_result": $("#search_object_final_handle_result").data("post"),
        "happen_time_start":$("#search_happen_time_start").data("post"),
        "happen_time_end":$("#search_happen_time_end").data("post"),
        "repair_date_start_start":$("#search_repair_date_start_start").data("post"),
        "repair_date_start_end":$("#search_repair_date_start_end").data("post")
    };
    
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
        complete : search_handleComplete
    });
};

//检索完成函数
var search_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            check_unqualified_record_list(resInfo.finished);
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

//一览
var check_unqualified_record_list=function(datalist){
    if($("#gbox_check_unqualified_record_list").length>0){
        $("#check_unqualified_record_list").jqGrid().clearGridData();
        $("#check_unqualified_record_list").jqGrid("setGridParam",{data:datalist}).trigger("reloadGrid",[{current:false}]);
    }else{
        $("#check_unqualified_record_list").jqGrid({
            data:datalist,
            height: 390,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['','流水号','','','发生时间','管理<br>编号','品名','不合格<br>项目','不合格<br>描述','产品<br>处理内容','工位对处','设备对处','修理<br>开始日期','修理<br>结束日期','担当人','线长','设备对处<br>结果','管理ID','对象类型',''],
            colModel:[
                {name:'check_unqualified_record_key',index:'check_unqualified_record_key',hidden:true},
                {name:'key',index:'key',width:60,
                    formatter:function(value, options, rData){
                          return rData.check_unqualified_record_key.substring(rData.check_unqualified_record_key.length-6,rData.check_unqualified_record_key.length)
                    }
                },
                {name:'devices_type_id',indecx:'devices_type_id',hidden:true},
                {name:'tools_type_id',indecx:'tools_type_id',hidden:true},
                {name:'happen_time',index:'happen_time',width:100,sorttype:'date',align:'center',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'m-d H:i'
                    }
                },
                {name:'manage_code',index:'manage_code',width:60},
                {name:'name',index:'name',width:120},
                {name:'check_item',index:'check_item',width:80},
                {name:'unqualified_status',index:'unqualified_status',width:160},
                {name:'product_content',index:'product_content',width:100,formatter:'select',
                   editoptions:{
                        value:$("#sProductContent").val()
                    }
                },
                {name:'position_handle',index:'position_handle',width:80,formatter:'select',
                    editoptions:{
                        value:$("#sPositionHandle").val()
                    }
                },
                {name:'object_handle',index:'object_handle',width:80,formatter:'select',
                    editoptions:{
                        value:$("#sObjectHandle").val()
                    }
                },
                {name:'repair_date_start',index:'repair_date_start',width:70,align:'center',sorttype:'date',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'m-d'
                    }
                },
                {name:'repair_date_end',index:'repair_date_end',width:70,align:'center',sorttype:'date',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'m-d'
                    }
                },
                {name:'responsible_operator_name',index:'responsible_operator_name',width:60},
                {name:'line_leader_name',index:'line_leader_name',width:60},
                {name:'object_final_handle_result',index:'object_final_handle_result',width:70,formatter:'select',
                    editoptions:{
                        value:$("#sObjectFinalHandleResult").val()
                    }
                },
                {name:'manage_id',index:'manage_id',hidden:true},
                {name:'object_type',index:'object_type',hidden:true},
                {name:'tools_no',index:'tools_no',hidden:true}
            ],
            rownumbers:true,
            toppager : false,
            rowNum : 20,
            pager : "#check_unqualified_record_listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true,
            pginput : false,
            recordpos : 'left',
            ondblClickRow:showDetail,
            viewsortcols : [true, 'vertical', true],
            gridComplete:function(){
                // 得到显示到界面的id集合
                var IDS=$("#check_unqualified_record_list").getDataIDs();
                //显示当前多少条记录
                var length=IDS.length;
                var pill = $("#check_unqualified_record_list");
                
                for(var i=0;i<length;i++){
                      var rowData=pill.getRowData(IDS[i]);
                      var product_content=rowData.product_content;
                      if(product_content==3){
                        pill.find("tr#" +IDS[i]).addClass("ui-state-active");
                      }
                }
            }
        });
    }
};



//详细信息
var showDetail=function(){
    var rowID=$("#check_unqualified_record_list").jqGrid('getGridParam','selrow');//行ID
    var rowData=$("#check_unqualified_record_list").getRowData(rowID);//行数据

    var data={
         "check_unqualified_record_key":rowData.check_unqualified_record_key,
         "manage_id":rowData.manage_id,
         "object_type":rowData.object_type,
         "devices_type_id":rowData.devices_type_id,
         "tools_type_id":rowData.tools_type_id,
         "tools_no":rowData.tools_no
    };
  
    
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
        complete : search_detailComplete
    });
};


var search_detailComplete=function(xhrobj, textStatus){//tempForm
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
             var login_id=resInfo.login_id;//当前登录者ID
             var line_leader_id=resInfo.line_leader_id;//当前记录线长ID
             var line_leader_handle_time=resInfo.line_leader_handle_time;//线长处理时间
             var object_type=resInfo.tempForm.object_type; //借用类型          
             
             
             $("#main").hide();
             $("#detail").show();
             $("#name_refer table:last-child").html(resInfo.nameReferChooser);
             setReferChooser($("#hidden_borrow_object_id"),$("#name_refer"));
             
             var check_unqualified_record_key = resInfo.tempForm.check_unqualified_record_key;
             $("#label_serial_num").text("(不合格流水号:"+check_unqualified_record_key.substring(check_unqualified_record_key.length-6,check_unqualified_record_key.length)+")");
             
              //不合格状态
            $("#label_manage_code").text(resInfo.tempForm.manage_code);//管理编号
            $("#label_name").text(resInfo.tempForm.name);//品名
            var  object_type=resInfo.tempForm.object_type;
            if(object_type==2){//治具
                 $("#label_model_name").text(resInfo.tempForm.tools_no);//治具NO.
                 $("#label_model_name").parent().prev().html("治具NO.");
                 $("#sheetShower").hide();
                 $("#label_borrow").html(resInfo.tempForm.process_code || "");
//               $("#label_borrow").parent().prev().html("借用治具");
                 
                 $("#input_borrow_object_name").parent().prev().html("借用治具");
                 $("#label_borrow_object_name").parent().prev().html("借用治具");
                 $("#edit_object_handle").parent().prev().html("治具对处");
                 $("#label_object_handle").parent().prev().html("治具对处");
                 $("#edit_object_final_handle_result").parent().prev().html("治具对处结果");
                 $("#label_object_final_handle_result").parent().prev().html("治具对处结果");
            }else{
                 $("#label_model_name").text(resInfo.tempForm.model_name);//型号
                 $("#label_model_name").parent().prev().html("型号");
                 $("#sheetShower").show();
                 $("#label_borrow").html(resInfo.tempForm.process_code || "");
//               $("#label_borrow").parent().prev().html("借用设备");
                 
                 $("#input_borrow_object_name").parent().prev().html("借用设备");
                 $("#label_borrow_object_name").parent().prev().html("借用设备");
                 $("#edit_object_handle").parent().prev().html("设备对处");
                 $("#label_object_handle").parent().prev().html("设备对处");
                 $("#edit_object_final_handle_result").parent().prev().html("设备对处结果");
                 $("#label_object_final_handle_result").parent().prev().html("设备对处结果");
            }
            $("#label_responsible_operator_name").text(resInfo.tempForm.responsible_operator_name);//责任人
            $("#label_happen_date").text(resInfo.tempForm.happen_time);//发生时间
            $("#label_check_item").text(resInfo.tempForm.check_item);//不合格项目
            $("#label_unqualified_status").text(resInfo.tempForm.unqualified_status);//不合格状态
 
            //线长
             if(login_id==line_leader_id){//当前线长
                $("#label_borrow_device").hide();
	            if(line_leader_handle_time=null || line_leader_handle_time==""){//线长未处理
                    $(".linear_not_handle").show();
                    $(".linear_handle_over").hide();
                    $("#borrow_device").hide();
                    
	                //工位暂停被选中
	                $("#edit_position_handle option[value=3]").attr("selected","selected").trigger("change");
	                $("#input_borrow_object_name").val("");
	                $("#hidden_borrow_object_id").val("");
	                //工位对处 选择借用
	                $("#edit_position_handle").change(function(){
	                    if($(this).val()==1){
	                        $("#borrow_device").show();
	                    }else{
	                        $("#borrow_device").hide();
	                        $("#input_borrow_object_name").val("");
	                        $("#hidden_borrow_object_id").val("");
	                    }
	                });
	            }else{//线长处理完
                    $(".linear_not_handle").hide();
	                $(".linear_handle_over").show();
	                
	                var position_handle=resInfo.tempForm.position_handle;//工位对处
	                if(position_handle==1){
	                     $("#label_position_handle").text("借用");
	                     $("#label_borrow_device").show();
	                     $("#label_borrow_object_name").html("管理编号:"+resInfo.tempForm.borrow_manage_no+"&nbsp;&nbsp;&nbsp;&nbsp;" +
	                     		"品名:"+resInfo.tempForm.borrow_object_name
	                     +(resInfo.tempForm.borrow_model_name ? "&nbsp;&nbsp;&nbsp;&nbsp;型号:"+resInfo.tempForm.borrow_model_name : "")
	                     +(resInfo.tempForm.tools_no ? "&nbsp;&nbsp;&nbsp;&nbsp;治具No.:"+resInfo.tempForm.tools_no : "")
	                     );//借用设备
	                }else if(position_handle==2){
	                     $("#label_position_handle").text("手工作业");
	                }else if(position_handle==3){
	                     $("#label_position_handle").text("暂停工位");
	                }else if(position_handle==4){
	                    $("#label_position_handle").text("有备品，无需更换");
	                }else{
	                	$("#label_position_handle").text("");
	                }
	                $("#label_line_leader_handle_name").text(resInfo.tempForm.line_leader_name);//线长
	                $("#label_line_leader_handle_time").text(resInfo.tempForm.line_leader_handle_time);//处理时间
                }
                
             }else if(login_id!=line_leader_id){//其他人
                $(".linear_not_handle").hide();
                $(".linear_handle_over").show();
                
                var position_handle=resInfo.tempForm.position_handle;//工位对处
                $("#label_borrow_device").hide();
	            if(position_handle==1){
	                 $("#label_position_handle").text("借用");
	                 $("#label_borrow_device").show();
	                     $("#label_borrow_object_name").html("管理编号:"+resInfo.tempForm.borrow_manage_no+"&nbsp;&nbsp;&nbsp;&nbsp;" +
	                     		"品名:"+resInfo.tempForm.borrow_object_name
	                     +(resInfo.tempForm.borrow_model_name ? "&nbsp;&nbsp;&nbsp;&nbsp;型号:"+resInfo.tempForm.borrow_model_name : "")
	                     +(resInfo.tempForm.tools_no ? "&nbsp;&nbsp;&nbsp;&nbsp;治具No.:"+resInfo.tempForm.tools_no : "")
	                     );//借用设备
	                     if ($("#label_borrow_object_name").text()) {
	                     	$("#label_borrow_object_name").text($("#label_borrow_object_name").text()
	                     		+ " 当前实际所在:" + resInfo.sectionAndLine);
	                     }
	            }else if(position_handle==2){
	                 $("#label_position_handle").text("手工作业");
	            }else if(position_handle==3){
	                 $("#label_position_handle").text("暂停工位");
                }else if(position_handle==4){
                    $("#label_position_handle").text("有备品，无需更换");
	            }else{
	                $("#label_position_handle").text("");
	            }
	            $("#label_line_leader_handle_name").text(resInfo.tempForm.line_leader_name);//线长
	            $("#label_line_leader_handle_time").text(resInfo.tempForm.line_leader_handle_time);//处理时间
             }
            //线长确认
			$("#liner_comfirmbutton").unbind("click");
			$("#liner_comfirmbutton").bind("click",function(){
			    var data={
			      "check_unqualified_record_key":resInfo.tempForm.check_unqualified_record_key,
			      "position_handle":$("#edit_position_handle").val(),//工位对处
			      "borrow_object_id":$("#hidden_borrow_object_id").val()//借用设备
			    };
			    
			    if($("#edit_position_handle").val()==1){//借用被选中
			        if($("#hidden_borrow_object_id").val()==null || $("#hidden_borrow_object_id").val()==''){
			            $("#confirm_dialog").html("请输入借用设备的值!");
			            $("#confirm_dialog").dialog({
			                 resizable : false,
			                 modal : true,
			                 title : "确认",
			                 width : 260,
			                 buttons : {
			                    "确认" : function() {
			                        $(this).dialog("close");
			                    }
			                 }
			            });
			        }else{
			             $.ajax({
			                beforeSend : ajaxRequestType,
			                async : true,
			                url : servicePath + '?method=doUpdateByLineLeader',
			                cache : false,
			                data : data,
			                type : "post",
			                dataType : "json",
			                success : ajaxSuccessCheck,
			                error : ajaxError,
			                complete : update_Complete
			            });
			        }
			    }else{
			         $.ajax({
			            beforeSend : ajaxRequestType,
			            async : true,
			            url : servicePath + '?method=doUpdateByLineLeader',
			            cache : false,
			            data : data,
			            type : "post",
			            dataType : "json",
			            success : ajaxSuccessCheck,
			            error : ajaxError,
			            complete : update_Complete
			        });
			    }
			});
            
            
             
            
            //经理
            if(resInfo.isManage=="manage"){//经理
                 if(resInfo.tempForm.product_content==null || resInfo.tempForm.product_content==""){//经理未处理   产品处理内容为空
	                $(".manage_not_handle").show();
	                $(".manage_handle").hide();
	                
	                $("#edit_object_handle").val(resInfo.tempForm.object_handle).attr("selected","selected").trigger("change");
	                $("#edit_product_handle option[value='']").attr("selected","selected").trigger("change");
	                $("#edit_product_result").val("");//产品处理结果
	            }else if(resInfo.tempForm.product_content==2 || resInfo.tempForm.product_content==3){//没有选择无影响
	                $(".manage_not_handle").show();
	                $(".manage_handle").hide();
	                
	                $("#edit_object_handle").val(resInfo.tempForm.object_handle).attr("selected","selected").trigger("change");
	                $("#edit_product_handle").val(resInfo.tempForm.product_content).attr("selected","selected").trigger("change");
	                $("#edit_product_result").val(resInfo.tempForm.product_result);
	            }else if(resInfo.tempForm.product_content=='1'){//无影响
	                $(".manage_not_handle").hide();
	                $(".manage_handle").show();
	                
	                var object_handle=resInfo.tempForm.object_handle;//设备对处
	                if(object_handle==1){
	                    $("#label_object_handle").text("废弃");
	                }else if(object_handle==2){
	                    $("#label_object_handle").text("维修");
	                }else if(object_handle==3){
	                    $("#label_object_handle").text("更换");
	                }else{
	                    $("#label_object_handle").text("");
	                }
	                
	                var product_content=resInfo.tempForm.product_content;//产品处理内容
	                if(product_content==1){
	                     $("#label_product_handle").text("无影响");
	                }else if(product_content==2){
	                     $("#label_product_handle").text("观察");
	                }else if(product_content==3){
	                     $("#label_product_handle").text("有影响，需停线确认波及范围");
	                }else{
	                     $("#label_product_handle").text("");
	                }
	                
	                $("#label_product_result").text(resInfo.tempForm.product_result);//产品处理结果
	                $("#label_manager_name").text(resInfo.tempForm.manager_name);//经理
	                $("#label_manager_handle_time").text(resInfo.tempForm.manager_handle_time);//处理时间
	                
	            }
            }else{//其他人
                $(".manage_not_handle").hide();
                $(".manage_handle").show();
                 
                 
                var object_handle=resInfo.tempForm.object_handle;//设备对处
                if(object_handle==1){
                    $("#label_object_handle").text("废弃");
                }else if(object_handle==2){
                    $("#label_object_handle").text("维修");
                }else if(object_handle==3){
                    $("#label_object_handle").text("更换");
                }else{
                    $("#label_object_handle").text("");
                }
                
                var product_content=resInfo.tempForm.product_content;//产品处理内容
                if(product_content==1){
                     $("#label_product_handle").text("无影响");
                }else if(product_content==2){
                     $("#label_product_handle").text("观察");
                }else if(product_content==3){
                     $("#label_product_handle").text("有影响，需停线确认波及范围");
                }else{
                     $("#label_product_handle").text("");
                }
                
                $("#label_product_result").text(resInfo.tempForm.product_result);//产品处理结果
                $("#label_manager_name").text(resInfo.tempForm.manager_name);//经理
                $("#label_manager_handle_time").text(resInfo.tempForm.manager_handle_time);//处理时间
            }
            
            
            //经理确认
		    $("#manage_comfirmbutton").unbind("click")
		    $("#manage_comfirmbutton").bind("click",function(){
		        var data={
		            "check_unqualified_record_key":resInfo.tempForm.check_unqualified_record_key,
		            "object_handle":$("#edit_object_handle").val(),//设备对处
		            "product_content":$("#edit_product_handle").val(),//产品处理内容
		            "product_result": $("#edit_product_result").val()//产品处理结果
		        };
		        
		        if($("#edit_product_handle").val()==1){//选择了无影响
		            $("#confirm_dialog").html("确认产品处理内容无影响吗?");
		            $("#confirm_dialog").dialog({
		                 resizable:false,
		                 modal:true,
		                 title:"确认",
		                 width:260,
		                 buttons :{
		                    "确认":function(){
		                            $.ajax({
		                                beforeSend : ajaxRequestType,
		                                async : true,
		                                url : servicePath + '?method=doUpdateByManage',
		                                cache : false,
		                                data : data,
		                                type : "post",
		                                dataType : "json",
		                                success : ajaxSuccessCheck,
		                                error : ajaxError,
		                                complete : update_Complete
		                            });
		                            $("#confirm_dialog").dialog("close");
		                       },
		                       "取消":function(){
		                            $(this).dialog("close");
		                       }
		                 }
		            });
		        }else{
		            $.ajax({
		                beforeSend : ajaxRequestType,
		                async : true,
		                url : servicePath + '?method=doUpdateByManage',
		                cache : false,
		                data : data,
		                type : "post",
		                dataType : "json",
		                success : ajaxSuccessCheck,
		                error : ajaxError,
		                complete : update_Complete
		            });
		        }
		    });
            
            
            
            //设备管理员
            if(resInfo.isDTTechnology=="dtTechnology"){
                $("#edit_object_final_handle_result").val(resInfo.tempForm.object_final_handle_result).trigger("change");//设备对处结果
	            $("#edit_repair_start_date").val(resInfo.tempForm.repair_date_start);//维修开始日期
	            $("#edit_repair_end_date").val(resInfo.tempForm.repair_date_end);//维修结束日期
                
                if($("#edit_object_final_handle_result").val()==2 || $("#edit_object_final_handle_result").val()==4 || $("#edit_object_final_handle_result").val()==5){
                    $(".dtTechnology_handle").show();
                }else{
                    $(".dtTechnology_handle").hide();
                }
                
                $("#edit_object_final_handle_result").change(function(){
                    if($(this).val()==2 || $(this).val()==4 || $(this).val()==5){
                        $(".dtTechnology_handle").show();
                    }else{
                        $(".dtTechnology_handle").hide();
                        $("#edit_repair_start_date").val("");
                        $("#edit_repair_end_date").val("");
                    }
                });
            }else{//其他人
                var object_final_handle_result=resInfo.tempForm.object_final_handle_result;
	            if(object_final_handle_result==1){
                    $(".dtTechnology_result").hide();
	                $("#label_object_final_handle_result").text("废弃");
                    $("#label_repair_start_date").text("");
                    $("#label_repair_end_date").text("");
	            }else if(object_final_handle_result==2){
                    $(".dtTechnology_result").show();
	                $("#label_object_final_handle_result").text("维修");
                    $("#label_repair_start_date").text(resInfo.tempForm.repair_date_start);
                    $("#label_repair_end_date").text(resInfo.tempForm.repair_date_end);
	            }else if(object_final_handle_result==3){
                    $(".dtTechnology_result").hide();
	                $("#label_object_final_handle_result").text("更换");
                    $("#label_repair_start_date").text("");
                    $("#label_repair_end_date").text("");
	            }else if(object_final_handle_result==4){
                    $(".dtTechnology_result").show();
	                $("#label_object_final_handle_result").text("校验");
	                $("#label_repair_start_date").text(resInfo.tempForm.repair_date_start);
                    $("#label_repair_end_date").text(resInfo.tempForm.repair_date_end);
	            }else if(object_final_handle_result==5){
	            	$(".dtTechnology_result").show();
	                $("#label_object_final_handle_result").text("出借");
	                $("#label_repair_start_date").text(resInfo.tempForm.repair_date_start);
                    $("#label_repair_end_date").text(resInfo.tempForm.repair_date_end);
	            }else{
	            	$(".dtTechnology_result").hide();
	                $("#label_object_final_handle_result").text("");
                    $("#label_repair_start_date").text("");
                    $("#label_repair_end_date").text("");
	            }
            }
      		$("#edit_technology_comment").val(resInfo.tempForm.technology_comment || "");	    
            
            //设备管理员确认
            $("#technology_comfirmbutton").unbind("click");
		    $("#technology_comfirmbutton").bind("click",function(){
		        var data={
		            "check_unqualified_record_key":resInfo.tempForm.check_unqualified_record_key,
		            "object_final_handle_result":$("#edit_object_final_handle_result").val(),
		            "repair_date_start":$("#edit_repair_start_date").val(),
		            "repair_date_end":$("#edit_repair_end_date").val(),
                    "object_type":resInfo.tempForm.object_type,
                    "manage_id":resInfo.tempForm.manage_id,
                    "technology_comment":$("#edit_technology_comment").val()
                    
		        };
		        
		        $.ajax({
		            beforeSend : ajaxRequestType,
		            async : true,
		            url : servicePath + '?method=doUpdateByTechnology',
		            cache : false,
		            data : data,
		            type : "post",
		            dataType : "json",
		            success : ajaxSuccessCheck,
		            error : ajaxError,
		            complete : update_Complete
		        });
		    });
            
            
        }
        
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

//确认完毕回调函数
var update_Complete=function(xhrobj, textStatus){
     var resInfo = null;
     try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
             $("#main").show();
             $("#detail").hide();
             findit();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

