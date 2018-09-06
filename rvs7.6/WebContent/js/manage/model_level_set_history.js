/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "model_level_set_history.do";

$(function(){
    $("input.ui-button").button();
     
    $("a.areacloser").hover(
        function (){$(this).addClass("ui-state-hover");},
        function (){$(this).removeClass("ui-state-hover");}
    ); 
     
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    }); 
    
    $("#search_level_id").select2Buttons();
    setReferChooser($("#hidden_model_id"),$("#model_name_referchooser"));
    setReferChooser($("#hidden_updated_by"),$("#opertor_name_referchooser"));
    
     /* date 日期 */
    $("#avaliable_end_date_start,#avaliable_end_date_end,#updated_time_start,#updated_time_end").datepicker({
        showButtonPanel : true,
        dateFormat : "yy/mm/dd",
        currentText : "今天"
    });
    
    /*检索*/
    $("#searchbutton").click(function(){
        $("#hidden_model_id").data("post",$("#hidden_model_id").val());//型号
        $("#search_level_id").data("post",$("#search_level_id").val());//等级
        $("#hidden_updated_by").data("post",$("#hidden_updated_by").val());//操作者
        $("#avaliable_end_date_start").data("post",$("#avaliable_end_date_start").val());//停止修理日期开始
        $("#avaliable_end_date_end").data("post",$("#avaliable_end_date_end").val());//停止修理日期结束
        $("#updated_time_start").data("post",$("#updated_time_start").val());//最后更新时间开始
        $("#updated_time_end").data("post",$("#updated_time_end").val());//最后更新时间结束
        findit();
    });
    
    /*清除*/
    $("#resetbutton").click(function(){
        clearCondition();
    });
    
    clearCondition();
    $("#avaliable_end_date_start").val($("#avaliable_end_date").val());
    $("#avaliable_end_date_start").data("post",$("#avaliable_end_date_start").val());//停止修理日期开始
    findit();
});

/**
 * 清除
 */
var clearCondition=function(){
    $("#hidden_model_id").val("").data("post","");
    $("#search_model_name").val("");
    $("#search_level_id").val("").trigger("change").data("post", "");
    $("#hidden_updated_by").val("").data("post","");
    $("#search_updated_by").val("");
    $("#avaliable_end_date_start").val("").data("post","");
    $("#avaliable_end_date_end").val("").data("post","");
    $("#updated_time_start").val("").data("post","");
    $("#updated_time_end").val("").data("post","");
};

/**
 * 检索
 */
var findit=function(){
    var data={
        "model_id":$("#hidden_model_id").data("post"),
        "level":$("#search_level_id").data("post"),
        "updated_by":$("#hidden_updated_by").data("post"),
        "avaliable_end_date_start":$("#avaliable_end_date_start").data("post"),
        "avaliable_end_date_end":$("#avaliable_end_date_end").data("post"),
        "updated_time_start":$("#updated_time_start").data("post"),
        "updated_time_end":$("#updated_time_end").data("post")
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
            model_level_set_history(resInfo.finished);
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
}

var model_level_set_history=function(listdata){
    if ($("#gbox_model_level_set_history_list").length > 0) {
        $("#model_level_set_history_list").jqGrid().clearGridData();
        $("#model_level_set_history_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#model_level_set_history_list").jqGrid({
            data:listdata,
            height: 470,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['型号','等级','梯队','停止修理日期','最后更新时间','操作者'],
            colModel:[
            {
                name:'model_name',
                index:'model_name',
                width:60,
                align:'left'
            },{
                name:'level',
                index:'level',
                width:60,
                align:'center',
                formatter:'select',
                editoptions : {
                    value : $("#goMaterial_level_inline").val()
                }
            },{
                name:'echelon',
                index:'echelon',
                width:60,
                align:'center',
                formatter:'select',
                editoptions:{
                    value:$("#goEchelon_code").val()
                }
            },{
                name:'avaliable_end_date',
                index:'avaliable_end_date',
                width:60,
                align:'center',
                sorttype:'date',
                formatter:'date',
                formatoptions:{
                    srcformat:'Y/m/d',
                    newformat:'Y-m-d'
                }
            },{
                name:'updated_time',
                index:'updated_time',
                width:60,
                align:'center',
                sorttype:'date',
                formatter:'date',
                formatoptions:{
                    srcformat:'Y/m/d H/i/s',
                    newformat:'Y-m-d H:i:s'
                }
            },{
                name:'updated_name',
                index:'updated_name',
                width:60,
                align:'left'
            }],
            rowNum: 20,
            rownumbers : true,
            toppager: false,
            pager: "#model_level_set_history_listpager",
            viewrecords: true,
            pagerpos: 'right',
            pgbuttons: true,
            pginput: false,
            hidegrid: false, 
            recordpos:'left'
        });
    }
};
