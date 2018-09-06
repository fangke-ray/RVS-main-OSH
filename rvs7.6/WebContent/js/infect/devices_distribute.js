var servicePath="devices_distribute.do";
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
    
    /*按钮事件*/
    $("input.ui-button").button();
    
    //分发课室  责任工程  管理等级
    $("#search_section_id,#search_responsible_line_id,#search_manage_level").select2Buttons();
    
    //发放日期
    $("#provide_date_start,#provide_date_end").datepicker({
        showButtonPanel : true,
        dateFormat : "yy/mm/dd",
        currentText : "今天"
    });
    //品名
    setReferChooser($("#hidden_devices_type_id"),$("#name_referchooser"));
    //责任工位
    setReferChooser($("#hidden_responsible_position_id"),$("#position_name_referchooser"));
    //管理员
    setReferChooser($("#hidden_manager_operator_id"),$("#manage_operator_name_referchooser"));
    
    //检索
    $("#searchbutton").click(function(){
        $("#search_manage_code").data("post",$("#search_manage_code").val());//管理编号
        $("#hidden_devices_type_id").data("post",$("#hidden_devices_type_id").val());//品名
        $("#search_model_name").data("post",$("#search_model_name").val());//型号
        $("#search_section_id").data("post",$("#search_section_id").val());//分发课室
        $("#search_responsible_line_id").data("post",$("#search_responsible_line_id").val());//责任工程
        $("#hidden_responsible_position_id").data("post",$("#hidden_responsible_position_id").val());//责任工位
        $("#hidden_manager_operator_id").data("post",$("#hidden_manager_operator_id").val());//管理员
        $("#provide_date_start").data("post",$("#provide_date_start").val());//发放日期开始
        $("#provide_date_end").data("post",$("#provide_date_end").val());//发放日期结束
        $("#search_manage_level").data("post",$("#search_manage_level").val());//管理等级
        findit();
    });
    
    //清除
    $("#resetbutton").click(function(){
        reset();
    }); 
    
    reset();
    
    findit();
});


//清除函数
var reset=function(){
    $("#search_manage_code").data("post","").val("");
    $("#hidden_devices_type_id").data("post","").val("");
    $("#search_name").val("");
    $("#search_model_name").data("post","").val("");
    $("#search_section_id").data("post","").val("").trigger("change");
    $("#search_responsible_line_id").data("post","").val("").trigger("change");
    $("#hidden_responsible_position_id").data("post","").val("");
    $("#search_responsible_position_name").data("post","").val("");
    $("#hidden_manager_operator_id").data("post","").val("");
    $("#search_manager_operator_name").val("");
    $("#provide_date_start").data("post","").val("");
    $("#provide_date_end").data("post","").val("");
    $("#search_manage_level").data("post","").val("").trigger("change");
};


var findit=function(){
    var data={
        "manage_code":$("#search_manage_code").data("post"),
        "devices_type_id": $("#hidden_devices_type_id").data("post"),
        "model_name":  $("#search_model_name").data("post"),
        "section_id":$("#search_section_id").data("post"),
        "line_id":$("#search_responsible_line_id").data("post"),
        "position_id":   $("#hidden_responsible_position_id").data("post"),
        "manager_operator_id":$("#hidden_manager_operator_id").data("post"),
        "provide_date_start": $("#provide_date_start").data("post"),
        "provide_date_end": $("#provide_date_end").data("post"),
        "manage_level":$("#search_manage_level").data("post")
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
            devices_distribute_list(resInfo.finished);
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

//一览
var devices_distribute_list=function(datalist){
    if($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:datalist}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#list").jqGrid({
           data:datalist,
           height: 390,
           width: 992,
           rowheight: 23,
           datatype: "local", 
           colNames:['管理编号','品名','型号','分发课室','责任工程','责任工位','管理员','发放日期','发放者','管理等级','','','','',''],
           colModel:[
            {name:'manage_code',index:'manage_code',width:120},
            {name:'name',index:'name',width:120},
            {name:'model_name',index:'model_name',width:120},
            {name:'section_name',index:'section_name',width:100},
            {name:'line_name',index:'line_name',width:80},
            {name:'process_code',index:'process_code',width:80,align:'right'},
            {name:'manager',index:'manager',width:100},
            {name:'provide_date',index:'provide_date',width:100,align:'center'},
            {name:'provider',index:'provider',width:100},
            {name:'manage_level',index:'manage_level',formatter:'select',align:'center',width:80,
                editoptions:{
                    value:$("#sManageLevel").val()
                }   
             },
            {name:'location',index:'location',hidden:true},
            {name:'brand',index:'brand',hidden:true},
            {name:'import_date',index:'import_date',hidden:true},
            {name:'status',index:'status',hidden:true},
            {name:'comment',index:'comment',hidden:true}
           ],
           rownumbers:true,
           toppager : false,
           rowNum : 20,
           pager : "#listpager",
           viewrecords : true,
           gridview : true,
           pagerpos : 'right',
           pgbuttons : true,
           pginput : false,
           recordpos : 'left',
           ondblClickRow : showDetail,
           viewsortcols : [true, 'vertical', true]
        });
    }
};

var showDetail=function(){
    var rowID=$("#list").jqGrid("getGridParam","selrow");
    var rowData=$("#list").getRowData(rowID);
    
    $("#label_manage_code").text(rowData.manage_code);//管理编号
    $("#label_name").text(rowData.name);//品名
    $("#label_model_name").text(rowData.model_name);//型号名称
    $("#label_section_name").text(rowData.section_name);//分发课室
    $("#label_line_name").text(rowData.line_name);//责任工程
    $("#label_process_code").text(rowData.process_code);//责任工位
    $("#label_manager").text(rowData.manager);//管理员
    $("#label_provide_date").text(rowData.provide_date);//发放日期
    $("#label_provider").text(rowData.provider);//发放者
    
    var manage_level=rowData.manage_level;//管理等级
    if(manage_level==1){
         $("#label_manage_level").text("A");
    }else if(manage_level==2){
        $("#label_manage_level").text("B");
    }else if(manage_level==3){
        $("#label_manage_level").text("C");
    }else{
        $("#label_manage_level").text("");
    }
   
    $("#label_location").text(rowData.location);//放置位置
    $("#label_brand").text(rowData.brand);//厂商
    $("#label_import_date").text(rowData.import_date);//导入日期
    
    var status=rowData.status;
    if(status==1){
         $("#label_status").text("使用中");
    }else if(status==4){
         $("#label_status").text("保管中");
    }
    
  
    $("#label_comment").val(rowData.comment);//备注
    
    $("#detail").dialog({
        width : 400,
        height:660,
        modal:true,
        resizable:false,
        title:"检查机器校正详细信息",
        buttons : { 
            "关闭" : function() { $(this).dialog("close")}
        }
    });
};




