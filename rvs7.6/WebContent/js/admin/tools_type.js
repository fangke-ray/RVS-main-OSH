/** 模块名 */
var modelname = "治具品名";

var servicePath="tools_type.do";

$(function(){   
    $("input.ui-button").button();  
    $("#edit_value_currency,#select_access_place,#select_cycle_type,#edit_trigger_state,#edit_data_type").select2Buttons();
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    });
	
    //检索button
    $("#searchbutton").click(function(){       
        findit();   
    });

    //清除button
    $("#resetbutton").click(function(){
        $("#search_name").val("");                   
        $("#search_daily_sheet_manage_no").val("");  
        $("#search_regular_sheet_manage_no").val("");
    }); 
    
    //新建---取消button
    $("#cancelbutton, #editarea span.ui-icon").click(function() {
        showList();
    });   
    findit();
})

/** 
 * 检索处理
 */
var keepSearchData;
var findit = function(data) {
    if (!data) {
        KeepSearchData = {
            "name":$("#search_name").val()
        };
     } else {
            keepSearchData = data;
     } 
    // Ajax提交
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath +'?method=search',
        cache : false,
        data : KeepSearchData,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : search_handleComplete
    });
};

/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var listdata = resInfo.toolsTypeForms;
            filed_list(listdata);
        }
    }catch (e) {};
}

/*jqgrid表格*/
function filed_list(finished){
    if ($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
    } else {
        $("#list").jqGrid({
            data:finished,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['','治具点检管理ID','治具品名','删除标记','最后更新人','最后更新时间'],
            colModel:[
                       {name:'myac',fixed:true,width:40,sortable:false,resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
                       {name:'tools_type_id',index:'tools_type_id', hidden:true}, 
                       {name:'name',index:'name',width : 150},                       
                       {name:'delete_flg',index:'delete_flg',hidden:true}, 
                       {name:'updated_by',index:'updated_by',width : 60}, 
                       {name:'updated_time',index:'updated_time',width : 60}                
                     ],
            rowNum: 20,
            toppager : false,
            pager : "#list_pager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
		    caption:'治具品名一览 ',
            deselectAfterSort : false,  
            ondblClickRow : showEdit,
            viewsortcols : [true,'vertical',true]         
        });
       $("#gbox_list .ui-jqgrid-hbox").before(
			'<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建治具品名" role="button" aria-disabled="false">' +
			'</div>'
		);
		$("input.ui-button").button(); 
		$("#addbutton").click(showAdd);
}
};

/*新建页面*/
var showAdd = function() {  
    //默认画面变化
    top.document.title = "新建" + modelname;
    $("#searcharea,#searchform,#view_edit_list,#listarea,#editform tr:not(:has(input,textarea,select)),#editform .changeFunction").hide();
    $("#editarea span.areatitle").html("新建" + modelname);
    $("#editarea").show();
    $("#editform input[type!='button']").val("");
    $("#editbutton").val("新建");
    $("#editbutton").enable();
    $(".errorarea-single").removeClass("errorarea-single");

    // 前台Validate验证
    $("#editform").validate({
        rules:{
           name:{
               required:true,
               maxlength:32
           }
        }
    });
    
    //新建治具点检种类--新建button
    $("#editbutton").unbind("click");
            $("#editbutton").click(function() {         
                if ($("#editform").valid()) {                   
                    $("#confirmmessage").text("确认要新建该条治具品名为["+$("#edit_name").val()+"]的记录吗？");
                    $("#confirmmessage").dialog({
                        resizable:false,
                        modal : true,
                        title : "新建确认",
                        buttons : {
                            "确认" : function() {
                                $(this).dialog("close");
                                var data={
                                   "name":$("#edit_name").val()                                   
                                };
                                // Ajax提交
							    $.ajax({
							        beforeSend : ajaxRequestType,
							        async : true,
							        url : servicePath +'?method=doinsert',
							        cache : false,
							        data : data,
							        type : "post",
							        dataType : "json",
							        success : ajaxSuccessCheck,
							        error : ajaxError,
							        complete : insert_handleComplete
							    });
                            },
                            "取消" : function() {
                                $(this).dialog("close");
                            }
                        }
                    });                         
                };
    }); 
}
var insert_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
             $("#confirmmessage").text("新建已经完成。");
             $("#confirmmessage").dialog({
                width : 320,
                height : 'auto',
                resizable : false,
                show : "blind",
                modal : true,
                title : "新建",
                buttons : {
                    "关闭" : function() {
                        $(this).dialog("close");
                        findit();
                        showList();    
                    }
                }
            });
        }
    }catch (e) {};
}

/*编辑页面*/
var showEdit = function() {
     var row = $("#list").jqGrid("getGridParam","selrow");//得到选中的行ID
     var rowData = $("#list").getRowData(row);
	 $("#editarea span.areatitle").html("修改" + modelname);
     // 默认画面变化 s
     top.document.title = modelname + "修改"; 
    
     $("#editform tr,.changeFunction,#view_edit_list").show();
     $("#editbutton").val("修改");
     $("#editbutton").enable();
     $("#detailarea tr:has(#label_detail_updated_by)").hide();
     $("#detailarea tr:has(#label_detail_updated_time)").hide();
     $(".errorarea-single").removeClass("errorarea-single");
     $("#searcharea,#searchform,#listarea").hide(); 
     $("#editarea").show(); 
	 
     $("#edit_name").val(rowData.name);         
     $("#edit_daily_sheet_manage_no").val(rowData.daily_sheet_manage_no);
     $("#edit_daily_sheet_file").val(rowData.daily_sheet_file);
     $("#edit_regular_sheet_manage_no").val(rowData.regular_sheet_manage_no);
     $("#edit_regular_sheet_file").val(rowData.regular_sheet_file);  
     $("#label_updated_by").text(rowData.updated_by);
     $("#label_updated_time").text(rowData.updated_time); 
     
     $("#hidden_tools_type_id").val(rowData.tools_type_id);
   
    // 前台Validate验证
    $("#editform").validate({
        rules:{
           name:{
               required:true,
               maxlength:32
           }
        }
    });
    
    // 切换按钮效果
    $("#editbutton").unbind("click");
            $("#editbutton").click(function() {         
                if ($("#editform").valid()) {
                    $("#confirmmessage").text("确认要修改成治具品名为["+$("#edit_name").val()+"]吗？");
                    $("#confirmmessage").dialog({
                        resizable:false,
                        modal : true,
                        title : "修改确认",
                        buttons : {
                            "确认" : function() {
                                $(this).dialog("close");
                                var data={
                                   "tools_type_id":$("#hidden_tools_type_id").val(),
                                   "name":$("#edit_name").val()                                 
                                };
                                // Ajax提交
                                $.ajax({
                                    beforeSend : ajaxRequestType,
                                    async : true,
                                    url : servicePath +'?method=doupdate',
                                    cache : false,
                                    data : data,
                                    type : "post",
                                    dataType : "json",
                                    success : ajaxSuccessCheck,
                                    error : ajaxError,
                                    complete : update_handleComplete
                                });
                            },
                            "取消" : function() {
                                $(this).dialog("close");
                            }
                        }
                    });
                    //showList();                   
                };
    }); 
}
var update_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
             $("#confirmmessage").text("修改已经完成。");
             $("#confirmmessage").dialog({
                width : 320,
                height : 'auto',
                resizable : false,
                show : "blind",
                modal : true,
                title : "修改",
                buttons : {
                    "关闭" : function() {
                        $(this).dialog("close");
                        findit();
                        showList(); 
                    }
                }
            });
        }
    }catch (e) {};
}

/*删除*/
var showDelete = function(rid) { 
	var rowData = $("#list").getRowData(rid);
    var data = {"tools_type_id" : rowData.tools_type_id};
    $("#confirmmessage").text("删除不能恢复。确认要删除["+encodeText(rowData.name)+"]的记录吗？");
    $("#confirmmessage").dialog({
        resizable:false,
        modal : true,
        title : "删除确认",
        buttons : {
            "确认" : function() {
                $(this).dialog("close");
                // Ajax提交
                $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath +'?method=dodelete',
                    cache : false,
                    data : data,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete : delete_handleComplete
                });
            },
            "取消" : function() {
                $(this).dialog("close");
            }
        }
    });
};

var delete_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
             $("#confirmmessage").text("删除已经完成。");
             $("#confirmmessage").dialog({
                width : 320,
                height : 'auto',
                resizable : false,
                show : "blind",
                modal : true,
                title : "删除",
                buttons : {
                    "关闭" : function() {
                        $(this).dialog("close");
                        findit();
                        showList(); 
                    }
                }
            });
        }
    }catch (e) {};
}

/*初始页面显示*/
var showList = function() {
   
    top.document.title = modelname + "一览";
    $("#searcharea,#searchform").show();
    $("#listarea").show();
    $("#editarea").hide();
}