/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "material_ogz.do";

$(function(){
    $("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    
    $("#search_ocm").select2Buttons();
    setReferChooser($("#hidden_model_id"),$("#model_name_referchooser"));
    /*按钮事件*/
    $("input.ui-button").button();
    
    /*日期*/
    $("#search_reception_time_start,#search_reception_time_end,#search_agreed_date_start,#search_agreed_date_end,#search_outline_time_start,#search_outline_time_end").datepicker({
        showButtonPanel : true,
        dateFormat : "yy/mm/dd",
        currentText : "今天"
    });
    
    
    $("#searchbutton").click(function(){
        $("#search_sorc_no").data("post",$("#search_sorc_no").val());
        $("#search_sfdc_no").data("post",$("#search_sfdc_no").val());
        $("#search_esas_no").data("post",$("#search_esas_no").val());
        $("#hidden_model_id").data("post",$("#hidden_model_id").val());
        $("#search_serial_no").data("post",$("#search_serial_no").val());
        $("#search_ocm").data("post",$("#search_ocm").val());
        $("#search_reception_time_start").data("post",$("#search_reception_time_start").val());
        $("#search_reception_time_end").data("post",$("#search_reception_time_end").val());
        $("#search_agreed_date_start").data("post",$("#search_agreed_date_start").val());
        $("#search_agreed_date_end").data("post",$("#search_agreed_date_end").val());
        $("#search_outline_time_start").data("post",$("#search_outline_time_start").val());
        $("#search_outline_time_end").data("post",$("#search_outline_time_end").val());
        findit();
    });
    
    $("#resetbutton").click(function(){
        clear();
    });
    
    clear();
    
    findit();
    
   /* $("#choose_time").click(function(event){
        SelectDate(this,"yyyy/MM/dd hh:mm:ss");      
    }); */
});

var clear=function(){
    $("#search_sorc_no").data("post","").val("");
    $("#search_sfdc_no").data("post","").val("");
    $("#search_esas_no").data("post","").val("");
    $("#hidden_model_id").data("post","").val("");
    $("#search_model_name").val("");
    $("#search_serial_no").data("post","").val("");
    $("#search_ocm").data("post","").val("").trigger("change");
    $("#search_reception_time_start").data("post","").val("");
    $("#search_reception_time_end").data("post","").val("");
    $("#search_agreed_date_start").data("post","").val("");
    $("#search_agreed_date_end").data("post","").val("");
    $("#search_outline_time_start").data("post","").val("");
    $("#search_outline_time_end").data("post","").val("");
};


var findit=function(){
    var data={
        "sorc_no": $("#search_sorc_no").data("post"),
        "sfdc_no": $("#search_sfdc_no").data("post"),
        "esas_no": $("#search_esas_no").data("post"),
        "model_id":  $("#hidden_model_id").data("post"),
        "serial_no": $("#search_serial_no").data("post"),
        "ocm": $("#search_ocm").data("post"),
        "reception_time_start": $("#search_reception_time_start").data("post"),
        "reception_time_end":  $("#search_reception_time_end").data("post"),
        "agreed_date_start":  $("#search_agreed_date_start").data("post"),
        "agreed_date_end":$("#search_agreed_date_end").data("post"),
        "outline_time_start":$("#search_outline_time_start").data("post"),
        "outline_time_end": $("#search_outline_time_end").data("post")
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
            complete : search_handleComplete
    });
};

var search_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            material_ogz(resInfo.finished);
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
}

var material_ogz=function(listdata){
    if ($("#gbox_material_ogz_list").length > 0) {
        $("#material_ogz_list").jqGrid().clearGridData();
        $("#material_ogz_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#material_ogz_list").jqGrid({
            data:listdata,
            height: 400,
            width: 1248,
            rowheight: 23,
            datatype: "local",
            colNames:['','修理单号','SFDC No.','ESAS No.','型号','机身号','委托处','受理日期','同意日期','出货日期'],
            colModel:[
                {
                    name:'material_id',
                    index:'material_id',
                    hidden:true
                },{
                    name:'sorc_no',
                    index:'sorc_no',
                    width:200
                },{
                    name:'sfdc_no',
                    index:'sfdc_no',
                    width:200
                },{
                    name:'esas_no',
                    index:'esas_no',
                    width:200
                },{
                    name:'model_name',
                    index:'model_name',
                    width:200
                },{
                    name:'serial_no',
                    index:'serial_no',
                    width:200
                },{
                    name:'ocm',
                    index:'ocm',
                    type:'integer',
                    sorttype:'integer',
                    formatter:'select',
                    align:'center',
                    editoptions : {
                        value : $("#goMaterialOcm").val()
                    },
                    width:200
                },{
                    name:'reception_time',
                    index:'reception_time',
                    align:'center',
                    sorttype : 'date',
                    formatter : 'date',
                    formatoptions : {
                        srcformat : 'Y-m-d H:i:s',
                        newformat : 'Y-m-d '
                    },
                    width:200
                },{
                    name:'agreed_date',
                    index:'agreed_date',
                    align:'center',
                    sorttype : 'date',
                    formatter : 'date',
                    formatoptions : {
                        srcformat : 'Y/m/d',
                        newformat : 'Y-m-d'
                    },
                     width:200
                },{
                    name:'shipping_time',
                    index:'shipping_time',
                    align:'center',
                    sorttype : 'date',
                    formatter : 'date',
                    formatoptions : {
                        srcformat : 'Y/m/d H:i:s',
                        newformat : 'Y-m-d'
                    },
                     width:200
                }],
                rowNum: 20,
                toppager: false,
                pager: "#material_ogz_listpager",
                viewrecords: true,
                rownumbers : true,/*行号*/
                pagerpos: 'right',
                pgbuttons: true,
                pginput: false,
                hidegrid: false, 
                recordpos:'left',
                viewsortcols : [ false, 'vertical', true ],
                ondblClickRow:function(rowid,iRow,iCol,e){
                    var rowID = $("#material_ogz_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID 
                    var rowData=$("#material_ogz_list").getRowData(rowID);
                    showDetail(rowData)
                }
        });
    }
}

var showDetail=function(rowData){
    var link = "widgets/material_ogz_edit.jsp";//load 页面
    $("#detail_dialog").hide();
    $("#detail_dialog").load(link,function(responseText,textStauts,XMLHttpRequest){
	    var data={
	        "material_id":rowData.material_id
	    }
	    $.ajax({
	            beforeSend : ajaxRequestType,
	            async : true,
	            url : servicePath + '?method=getMaterialOgz',
	            cache : false,
	            data : data,
	            type : "post",
	            dataType : "json",
	            success : ajaxSuccessCheck,
	            error : ajaxError,
	            complete : getMaterialOgzComplete
	    });
    });
    
    var getMaterialOgzComplete=function(xhrobj, textStatus){
	    var resInfo = null;
	    try {
	        // 以Object形式读取JSON
	        eval('resInfo =' + xhrobj.responseText);
	        if (resInfo.errors.length > 0) {
	            // 共通出错信息框
	            treatBackMessages("", resInfo.errors);
	        } else {
                    $("#detail_dialog").show();
	                $("#label_sorc_no").text(resInfo.finished.sorc_no);//SORC_NO.
	                $("#label_sfdc_no").text(resInfo.finished.sfdc_no);//SFDC_NO.
	                $("#label_esas_no").text(resInfo.finished.esas_no);//ESAS_NO
	                
	                var ocm = resInfo.finished.ocm;
	                var ocm_name="";
	            
	                if(ocm == 1){
	                    ocm_name = "OCM-SHRC";
	                }else if(ocm == 2){
	                    ocm_name = "OCM-BJRC";
	                }else if(ocm == 3){
	                    ocm_name = "OCM-GZRC";
	                }else if(ocm == 4){
	                    ocm_name = "OCM-SYRC";
	                }else{
	                    ocm_name = "";
	                }
	                
                    $("#label_ocm").text(ocm_name);//委托处
	                $("#label_model_name").text(resInfo.finished.model_name);//型号
	                $("#label_serial_no").text(resInfo.finished.serial_no);//SERIAL_NO.
                    
                    $("label[for='ocm_deliver']").text(resInfo.finished.ocm_deliver_date); //物流配送
                    
                    $("label[for='recept']").text(sub_date(resInfo.finished.reception_time));// 受理时间
					$("label[for='disfin']").text(sub_date(resInfo.finished.sterilization_time));// 消毒·灭菌
					$("label[for='quotate']").text(sub_date(resInfo.finished.quotation_complete_time));// 报价完成
                    $("#label_agreed_time").text(resInfo.finished.agreed_date);// 同意日期
					$("label[for='inline']").text(sub_date(resInfo.finished.inline_time));// 投线修理
					$("label[for='ns']").text(sub_date(resInfo.finished.ns_complete_time));// NS
					$("label[for='dec']").text(sub_date(resInfo.finished.dec_complete_time));//分解
					$("label[for='com']").text(sub_date(resInfo.finished.com_complete_time));//总组
					$("label[for='outline']").text(sub_date(resInfo.finished.outline_time));// 品保
					$("label[for='shipping']").text(sub_date(resInfo.finished.shipping_time));//包装出货  
					$("label[for='ocm_shipping']").text(resInfo.finished.ocm_shipping_date);//物流配送  
                    
                    $("#hidden_ocm_deliver_time").val(resInfo.finished.ocm_deliver_date);
					$("#hidden_reception_time").val(resInfo.finished.reception_time);
					$("#hidden_sterilization_time").val(resInfo.finished.sterilization_time);
					$("#hidden_quotation_time").val(resInfo.finished.quotation_complete_time);
					$("#hidden_inline_time").val(resInfo.finished.inline_time);
					$("#hidden_dec_time").val(resInfo.finished.dec_complete_time);
					$("#hidden_ns_time").val(resInfo.finished.ns_complete_time);
					$("#hidden_com_time").val(resInfo.finished.com_complete_time);
					$("#hidden_outline_time").val(resInfo.finished.outline_time);
					$("#hidden_shipping_time").val(resInfo.finished.shipping_time);
					$("#hidden_ocm_shipping_date").val(resInfo.finished.ocm_shipping_date);
                    
                    judge();
                    
                   $("#material_ogz_detail_id").after($("#dialog_main"));  
                    //点击圆弹出日历选择
                    $(".circle").click(function(){
                         //当前操作的圆的ID
				       $("#choose_time_calendar").show();
				        //当前操作的圆的ID
				        var current_circular_id = $(this).attr("id");  
                        if(current_circular_id=="ocm_deliver" || current_circular_id=="ocm_shipping" ||current_circular_id=="agree"){
                            $("#choose_time").hide();
                            $("#choose_date").show();
                            $("#choose_date").datepicker({
                            showButtonPanel : true,
                            dateFormat : "yy/mm/dd",
                            currentText : "今天"
                           });
                        }else{
                            $("#choose_date").hide();
                            $("#choose_time").show();
                            $("#choose_time").datetimepicker({
						    lang:"ch",
						    timepicker:true,
						    format:'Y-m-d H',
						    allowBlank:true
						   });
                        }
				        var this_dialog = $("#confirm_dialog");
				        this_dialog.dialog({
				                title : "选择日期",
				                position : 'auto',
				                modal : true,
				                width:200,
				                minHeight : 200,
				                resizable : false,
                                close:function(){
				                  $("#calendarPanel").hide();
				                  $("#choose_time_calendar").css("border","none");
				                },
				                buttons :{
				                    "确定":function(){
                                       var str_time=$("#choose_time").val().substring(5,13);
                                       if(str_time !=""|| str_time!=null){
                                           str_time=str_time+"H";
                                       } 
                                       if(current_circular_id=="ocm_deliver" || current_circular_id=="ocm_shipping" ||current_circular_id=="agree"){
                                           $("#label_"+current_circular_id+"_date").text($("#choose_date").val());
				                       }else{
                                           $("#label_"+current_circular_id+"_time").text(str_time);
				                       }		
                                       
                                       $("#hidden_"+current_circular_id+"_time").val($("#choose_time").val()); 
                                       this_dialog.dialog('close');
                                       judge();
				                    },
				                    "关闭":function(){
				                        this_dialog.dialog('close');
				                    }
				                }
				    });
                   });                   
                    
	                $("#detail_dialog").dialog({
	                    width : 1000,
	                    height: 430,
	                    modal : true,
	                    position : "center",
	                    title : "OGZ进度详细信息",
	                    resizable:false,
	                    close : function(){
	                        $("#detail_dialog").html("");
	                    },
	                    buttons : {
	                        "确定":function(){
                                 var data={
                                            "ocm_deliver_date":$("#hidden_ocm_deliver_time").val(),
                                            "reception_time":$("#hidden_recept_time").val(),
                                            "sterilization_time":$("#hidden_disfin_time").val(),
                                            "quotation_complete_time":$("#hidden_quotate_time").val(),
                                            "agreed_date":$("#label_agreed_time").text(),
                                            "inline_time":$("#hidden_inline_time").val(),
                                            "ns_complete_time":$("#hidden_ns_time").val(),
                                            "dec_complete_time":$("#hidden_dec_time").val(),
                                            "com_complete_time":$("#hidden_com_time").val(),
                                            "outline_time":$("#hidden_outline_time").val(),
                                            "shipping_time":$("#hidden_shipping_time").val(),
                                            "ocm_shipping_date":$("#hidden_shipping_time").val(),
                                            "material_id":resInfo.finished.material_id
                                  }
                                $.ajax({
                                        beforeSend : ajaxRequestType,
                                        async : true,
                                        url : servicePath + '?method=doUpdate',
                                        cache : false,
                                        data : data,
                                        type : "post",
                                        dataType : "json",
                                        success : ajaxSuccessCheck,
                                        error : ajaxError,
                                        complete : update_time_complete
                                });
	                        }, 
	                        "关闭":function(){ 
	                               $(this).dialog("close"); 
	                        }
	                    }
	                });
	            
	        }
	    } catch (e) {
	        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	    };
    }
    
    
};
var update_time_complete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            $("#detail_dialog").dialog('close');
            $("#update_confirm").text("OGZ进度详细信息更新完成！");
            $("#update_confirm").dialog({
                resizable : false,
                modal : true,
                title : "确认",
                buttons : {
                    "确认" : function() {
                        $(this).dialog("close");
                    }
                }
            });
           findit();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
}
//截取时间显示
var sub_date=function(time){
  if(time !="" && time !=null){
    time=time.substring(5,13)+"H";
  }else{
    time="";
  }
  return time;
}

//判断点击过后的日期颜色变化
var judge = function(){
       //遍历
                    $("#ocm_deliver").nextAll(".circle").each(function(idx,ele){
                        var current_circular_id=$(ele).attr("id");
                        var current_date_val=$("label[for='"+current_circular_id+"']").text();
                        var prev_date_val=$("label[for='"+current_circular_id+"']").prev("label").text();
                        var next_date_val=$("label[for='"+current_circular_id+"']").next("label").text();
                       
                        //受理日期不为空
                        if($("#label_recept_time").text()=="" || $("#label_recept_time").text()==null){
                          $("div[before='recept']").removeClass().addClass("rectangle_grzy rectangle");
                        }else{
                          $("div[before='recept']").removeClass().addClass("rectangle_blue rectangle");
                        }
                        
                        //当前日期不为空时
                        if(current_date_val==""||current_date_val==null){
                             $("#"+current_circular_id+"").removeClass().addClass("circle_grzy circle");
                             $("#"+current_circular_id+"").next(".rectangle").removeClass().addClass("rectangle_grzy rectangle");
                             $("div[before='"+current_circular_id+"']").removeClass().addClass("rectangle_grzy rectangle");
                        }else{
                             $("#"+current_circular_id+"").removeClass().addClass("circle_blue circle");
                        }
                         //当前日期不为空，下次日期不为空的情况
                        if(current_date_val==""||current_date_val==null || next_date_val==""||next_date_val==null ){
                             $("div[before='"+current_circular_id+"']").removeClass().addClass("rectangle_grzy rectangle");
                        } 
                        if((current_date_val!="" && current_date_val!=null) && (next_date_val !="" && next_date_val !=null)){//current_date_val!="" || current_date_val!=null && next_date_val !="" || next_date_val !=null){
                             $("div[for='follow_"+current_circular_id+"']").removeClass().addClass("rectangle_blue rectangle");
                        }
                        
                        //当前日期不为空，上次日期空的情况
                        if(current_date_val==""||current_date_val==null || prev_date_val==""||prev_date_val==null ){
                             $("div[before='"+current_circular_id+"']").removeClass().addClass("rectangle_grzy rectangle");
                        }else if(current_date_val!="" && current_date_val!=null &&  prev_date_val==""||prev_date_val==null ){
                             $("#"+current_circular_id+"").removeClass().addClass("circle_blue circle");
                             $("div[before='"+current_circular_id+"']").removeClass().addClass("rectangle_grzy rectangle");
                        }else if(current_date_val!="" && current_date_val!=null && prev_date_val!="" && prev_date_val!=null ){
                             $("div[before='"+current_circular_id+"']").removeClass().addClass("rectangle_blue rectangle");
                        }
                        
                    });
                    
                    $(".circle-group > .circle").each(function(idx,ele){
                        var current_circular_id=$(ele).attr("id");
                        var current_date_val=$("label[for='"+current_circular_id+"']").text();
                        if(current_date_val==""||current_date_val==null){
                             $("#"+current_circular_id+"").removeClass().addClass("circle_grzy circle");
                        }else{
                             $("#"+current_circular_id+"").removeClass().addClass("circle_blue circle");
                        }
                    });
                    
                    //投线---NS
                    if($("#label_inline_time").text()==""||$("#label_inline_time").text()==null){
                       if($("#label_ns_time").text()!=""||$("#label_ns_time").text()!=null){
                          $("#ns").prev(".center_radius").addClass("center_grzy center_radius");
                       }
                    }else{
                       //投线修理有值--NS有值
                       if($("#label_ns_time").text()==""||$("#label_ns_time").text()==null){
                            $("#ns").removeClass().addClass("circle_red");
                       }else{
                         $("#ns").prev(".center_radius").removeClass().addClass("center_blue center_radius");
                       }
                       
                       //投线修理有值 ---分解有值
                       if($("#label_dec_time").text()==""||$("#label_dec_time").text()==null){
                            $("#dec").removeClass().addClass("circle_red");
                       }else{
                         $("#dec").prev(".center_radius").removeClass().addClass("center_blue center_radius");
                       }
                    }
                    //NS--总组
                    if($("#label_ns_time").text()==""||$("#label_ns_time").text()==null){
                        if($("#label_com_time").text()!=""||$("#label_com_time").text()!=null){
                          $("#ns").next(".center_radius").addClass("center_grzy center_radius");
                        }
                    }else{
                         if($("#label_com_time").text()==""||$("#label_com_time").text()==null){
                           $("#ns").next(".center_radius").addClass("center_grzy center_radius");
                         }else{
                           $("#ns").next(".center_radius").addClass("center_blue center_radius");
                         }
                    }
                    //投线--分解
                    if($("#label_dec_time").text()==""||$("#label_dec_time").text()==null){
                        if($("#label_inline_time").text()!=""||$("#label_inline_time").text()!=null){
                          $("#dec").prev(".center_radius").addClass("center_grzy center_radius");
                        }
                    }else{
                        if($("#label_inline_time").text()==""||$("#label_inline_time").text()==null){
                          $("#dec").prev(".center_radius").addClass("center_grzy center_radius");
                        }else{
                          $("#dec").prev(".center_radius").addClass("center_blue center_radius");
                        }
                    }
                    //分解--总组
                    if($("#label_dec_time").text()==""||$("#label_dec_time").text()==null){
                        if($("#label_com_time").text()!=""||$("#label_com_time").text()!=null){
                          $("#dec").next(".center_radius").addClass("center_grzy center_radius");
                        }
                    }else{
                        if($("#label_com_time").text()==""||$("#label_com_time").text()==null){
                          $("#dec").next(".center_radius").addClass("center_grzy center_radius");
                        }else{
                          $("#dec").next(".center_radius").addClass("center_blue center_radius");
                        }
                    }
                    //接下来正要做的---红点
                    $("#ocm_shipping").prevAll(".circle").each(function(idx,ele){
                        var current_circular_id=$(ele).attr("id");
                        var current_date_val=$("label[for='"+current_circular_id+"']").text();
                        //受理○除去
                        if(idx<7){
                             if(current_date_val!="" && current_date_val!=null){
                              $("div[for='follow_"+current_circular_id+"']").removeClass().addClass("rectangle_red rectangle");
                              $("#"+current_circular_id+"").next().next(".circle").removeClass().addClass("circle_red circle");
                              return false;
                             }
                        //如果从投线总组到受理----ns和分解如果是无值
                        }else if(3<idx<7){
                            if($("#label_ns_time").text()=="" && $("#label_ns_time").text()==null&&$("#label_dec_time").text()==""&& $("#label_dec_time").text()==null){
                              if(current_date_val!="" && current_date_val!=null){
                                  $("div[for='follow_"+current_circular_id+"']").removeClass().addClass("rectangle_red rectangle");
                                  $("#"+current_circular_id+"").next().next(".circle").removeClass().addClass("circle_red circle");
                              return false;
                             }
                            //当NS开始不是空的
                            }
                        }                          
                    });                   
                    //从总组开始到结束--都是空的时候，判断中间NS和分解之间的关系
                    $("#inline").nextAll(".circle").each(function(idx,ele){
                        var circular_id=$(ele).attr("id");
                        var current_date=$("label[for='"+circular_id+"']").text();
                        if(current_date=="" &&  current_date==null){
                           //当分解有值，总组无值
                           if($("#label_dec_time").text()!="" &&  $("#label_dec_time").text()!=null){ 
                               if($("#label_com_time").text()=="" || $("#label_com_time").text()==null){
                                     $("#com").removeClass().addClass("circle_red circle");
                                     $("#dec").next(".center_radius").removeClass().addClass("center_red center_radius");
                               }else{
                                     $("#dec").next(".center_radius").removeClass().addClass("center_blue center_radius"); 
                               }
                            }
                           
                           //当分解有值，总组无值
                           if($("#label_ns_time").text()!="" && $("#label_ns_time").text()!=null){ 
                               if($("#label_com_time").text()=="" || $("#label_com_time").text()==null){
                                     $("#com").removeClass().addClass("circle_red circle");
                                     $("#ns").next(".center_radius").removeClass().addClass("center_red center_radius");
                               }else{
                                     $("#ns").next(".center_radius").removeClass().addClass("center_blue center_radius"); 
                               }
                            }
                            
                            //当NS和分解都没有值
                            if($("#label_ns_time").text()=="" && $("#label_ns_time").text()==null && $("#label_dec_time").text()=="" &&  $("#label_dec_time").text()==null){ 
                                 $("#ns").prev(".center_radius").removeClass().addClass("center_red center_radius");
                                 $("#dec").prev(".center_radius").removeClass().addClass("center_red center_radius");
                            }
                        }
                    });
                    //如果出货时间和最后的物流时间是有值的话 ---红色消失
                    if($("#label_shipping_time").text()!="" && $("#label_shipping_time").text()!=null){
                       if($("#label_ocm_shipping_date").text() !="" && $("#label_ocm_shipping_date").text()!=null){
                          $("#ocm_shipping").removeClass().addClass("circle_blue circle");
                          $("#ocm_shipping").prev(".rectangle").removeClass().addClass("rectangle_blue rectangle");
                       }
                    }
                    
}

