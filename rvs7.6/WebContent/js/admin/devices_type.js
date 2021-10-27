/** 模块名 */
var modelname = "设备工具品名";

var servicePath="devices_type.do"

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
    
    $("input.ui-button").button(); 
    $("#search_specialized,#edit_specialized").select2Buttons();

    //检索button
    $("#searchbutton").click(function(){       
        findit();   
    });

    //清除button
    $("#resetbutton").click(function(){
        $("#search_name").val("");
        $("#search_specialized").val("").trigger("change");
    }); 
    
    //新建---取消button
    $("#cancelbutton, #editarea span.ui-icon").click(function() {
        showList();
    });
    
    $("#show_photo").on("error", function(){
		$("#show_photo").hide();
		$("#show_no_photo").show();
	});
    
    $("#update_photo").parent().on("change", "#update_photo", uploadPhoto);
    
    findit();
});

var uploadPhoto = function(){
	if(!this.value) return;

	var devices_type_id = $("#hidden_devices_type_id").val();
    $.ajaxFileUpload({
        url : servicePath + "?method=sourceImage", // 需要链接到服务器地址
        secureuri : false,
        data: {devices_type_id : devices_type_id},
        fileElementId : 'update_photo', // 文件选择框的id属性
        dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = $.parseJSON(responseText);	

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#update_photo").val("");
				
				$("#show_no_photo").hide();
				$("#show_photo")
					.attr("src", "http://" + document.location.hostname + "/photos/safety_guide/" + devices_type_id + "?_s=" + new Date().getTime()).show();
			}
		}
     });
}


/** 
 * 检索处理
 */
var keepSearchData;
var findit = function(data) {
    if (!data) {
        KeepSearchData = {
            "name":$("#search_name").val(),
            "specialized":$("#search_specialized").val()
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
            var listdata = resInfo.devicesTypeForms;
            filed_list(listdata);
        }
    }catch (e) {};
}
            
/*jqgrid表格*/
function filed_list(finished){
	for (var i in finished) {
		if (!finished[i].specialized) finished[i].specialized = "0";
	}
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
            colNames:['','治具点检ID','品名','特定设备工具种类','删除标记','安全操作<br>手顺','最后更新人','最后更新时间'],
            colModel:[
                       {name:'myac',fixed:true,width:40,sortable:false,resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
                       {name:'devices_type_id',index:'devices_type_id', hidden:true}, 
                       {name:'name',index:'name',width : 150},
                       {name:'specialized',index:'specialized',width : 35,formatter : 'select',
							editoptions : {
								value : ($("#gSpecializedDeviceType").val() + ";0: -;")
							}
						},
                       {name:'delete_flg',index:'delete_flg',hidden:true}, 
                       {name:'safety_guide',index:'safety_guide',width : 20, formatter : 'select',
       					editoptions : {
       						value : "0:;1:有"
       					},align:'center'
       				   },
                       {name:'updated_by',index:'updated_by',width : 35}, 
                       {name:'updated_time',index:'updated_time',width : 50}
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
            caption:' 设备工具品名一览 ',
            deselectAfterSort : false,  
            ondblClickRow : showEdit,
            viewsortcols : [true,'vertical',true]         
        });
        $("#gbox_list .ui-jqgrid-hbox").before(
            '<div class="ui-widget-content" style="padding:4px;">' +
                '<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建设备工具品名" role="button" aria-disabled="false">' +
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
    $("#searcharea,#searchform,#listarea,#editform tr:not(:has(input,textarea,select))").hide();
    $("#edit_specialized").val("").trigger("change");
    $("#editarea span.areatitle").html("新建" + modelname);
    $("#editarea").show();
    $("#editform input[type!='button']").val("");
    $("#editbutton").val("新建");
    $("#editbutton").enable();
    $(".errorarea-single").removeClass("errorarea-single");
    $(".safety_guide").hide();
    
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
                    $("#confirmmessage").text("确认要新建该条设备工具品名为["+$("#edit_name").val()+"]记录吗？");
                    $("#confirmmessage").dialog({
                        resizable:false,
                        modal : true,
                        title : "新建确认",
                        buttons : {
                            "确认" : function() {
                                $(this).dialog("close");
                                
                                var data = {
                                    "name" : $("#edit_name").val(),
                                    "specialized":$("#edit_specialized").val()
                                }
                                // Ajax提交
                                $.ajax({                        
                                    beforeSend : ajaxRequestType,
                                    async : true,
                                    url : servicePath + '?method=doinsert',
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
            treatBackMessages("#editarea", resInfo.errors);
        } else {
            $("#confirmmessage").dialog("close");
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
                    }
                }
            });
            // 重新查询
            findit();
            // 切回一览画面
            showList();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "
                + e.lineNumber + " fileName: " + e.fileName);
    };
}

/*编辑页面*/
var showEdit = function() {
     $("#searcharea,#searchform,#listarea,#editform tr:not(:has(input,textarea,select))").show();
     $("#editbutton").val("修改");
     $("#editbutton").enable();
     $(".errorarea-single").removeClass("errorarea-single");
     $("#searcharea,#searchform,#listarea").hide(); 
     $("#editarea").show(); 
     $(".safety_guide").show();
     $("#update_photo").val("");
     
     var row = $("#list").jqGrid("getGridParam","selrow");//得到选中的行ID
     var rowData = $("#list").getRowData(row);
     
     $("#hidden_devices_type_id").val(rowData.devices_type_id);
     
     $("#editarea span.areatitle").html("修改" + modelname);
     // 默认画面变化 s
     top.document.title = modelname + "修改";  
   
     $("#edit_name").val(rowData.name);
     $("#edit_specialized").val(rowData.specialized).trigger("change");
     $("#label_updated_by").text(rowData.updated_by);
     $("#label_updated_time").text(rowData.updated_time);        
   
     $("#show_no_photo").hide();
 	 $("#show_photo").show()
 		.attr("src", "http://" + document.location.hostname + "/photos/safety_guide/" + rowData.devices_type_id + "?_s=" + new Date().getTime());

     
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
                    $("#confirmmessage").text("确认要修改设备工具品名为["+$("#edit_name").val()+"]记录吗？");
                    $("#confirmmessage").dialog({
                        resizable:false,
                        modal : true,
                        title : "修改确认",
                        buttons : {
                            "确认" : function() {
                                $(this).dialog("close");
                                var data={
                                   "name":$("#edit_name").val(),
                                   "specialized":$("#edit_specialized").val(),
                                   "devices_type_id":$("#hidden_devices_type_id").val()
                                }
                                // Ajax提交
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

/**
 * 检索Ajax通信成功时的处理
 */
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
                    }
                }
            });
            findit();
            //回到一览画面
            showList();
        }
    }catch (e) {};
}


/*删除*/
var showDelete = function(rid) { 
    var rowData = $("#list").getRowData(rid);
    var data = {"devices_type_id" : rowData.devices_type_id}
    $("#confirmmessage").text("删除不能恢复。确认要删除["+encodeText(rowData.name)+"]的记录吗?");
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
                    url : servicePath + '?method=dodelete',
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
                    }
                }
            });
            findit();
            //回到一览画面
            showList();
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