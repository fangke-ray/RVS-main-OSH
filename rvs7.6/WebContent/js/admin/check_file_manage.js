
var servicePath="check_file_manage.do";
var listdata=[];
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
    //类型    归档周期
    $("#search_access_place,#add_access_place,#update_access_place,#search_cycle_type,#add_cycle_type,#update_cycle_type,#search_filing_means,#add_filing_means,#update_filing_means").select2Buttons();
    
    setReferChooser($("#hidden_devices_type_id"),$("#name_refer"));
    setReferChooser($("#hidden_add_devices_type_id"),$("#name_refer"));
    setReferChooser($("#hidden_update_devices_type_id"),$("#name_refer"));
    
    //检索
    $("#searchbutton").click(function(){
        $("#search_check_manage_code").data("post",$("#search_check_manage_code").val());//点检表管理号
        $("#search_sheet_file_name").data("post",$("#search_sheet_file_name").val());//文件名
        $("#hidden_devices_type_id").data("post",$("#hidden_devices_type_id").val());//使用设备工具品名
        $("#search_access_place").data("post",$("#search_access_place").val());//类型
        $("#search_cycle_type").data("post",$("#search_cycle_type").val());//归档周期
        $("#search_filing_means").data("post",$("#search_filing_means").val());//归档方式
        findit();
    });
    
    //清空
    $("#resetbutton").click(function(){
          reset();  
    });
    
    reset();
    
    findit();
});

//清空函数
var reset=function(){
   $("#search_check_manage_code").data("post","").val("");//点检表管理号
   $("#search_sheet_file_name").data("post","").val("");//文件名
   $("#hidden_devices_type_id").data("post","").val("");//使用设备工具ID
   $("#search_devices_type_name").val("");//使用设备工具品名
   $("#search_access_place").data("post","").val("").trigger("change");//类型
   $("#search_cycle_type").data("post","").val("").trigger("change");//归档周期
   $("#search_filing_means").data("post","").val("").trigger("change");//归档方式
};

//检索函数
var findit=function(){
    var data={
          "check_manage_code":$("#search_check_manage_code").data("post"),
          "sheet_file_name":$("#search_sheet_file_name").data("post"),
          "devices_type_id":$("#hidden_devices_type_id").data("post"),
          "access_place":$("#search_access_place").data("post"),
          "cycle_type":$("#search_cycle_type").data("post"),
          "filing_means":$("#search_filing_means").data("post")
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
            check_file_manage_list(resInfo.finished);
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

//点检表管理一览
var check_file_manage_list=function(listdata){
    if ($("#gbox_check_file_manage_list").length > 0) {
        $("#check_file_manage_list").jqGrid().clearGridData();
        $("#check_file_manage_list").jqGrid("setGridParam",{data:listdata}).trigger("reloadGrid",[{current:false}]);
    }else{
        $("#check_file_manage_list").jqGrid({
            data:listdata,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['','点检表管理ID','点检表管理号','点检表文件','类型','归档周期','归档方式','使用设备工具品名','特定机型','最后更新人','最后更新时间',''],
            colModel:[
                  {name:'myac',fixed:true,width:40,sortable:false,resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
                  {name:'check_file_manage_id',index:'check_file_manage_id',hidden:true},
                  {name:'check_manage_code',index:'check_manage_code',width:100},
                  {name:'sheet_file_name',index:'sheet_file_name',width:260},
                  {name:'access_place',index:'access_place',width:40,align:'center',formatter:'select',
                    editoptions:{
                        value:$("#sAccessPlace").val()
                    }
                  },
                  {name:'cycle_type',index:'cycle_type',width:50,align:'center',formatter:'select',
                    editoptions:{
                        value:$("#sCycleType").val()
                    }
                  },
                  {name:'filing_means',index:'filing_means',width:60,formatter:'select',
                      editoptions:{
                          value:$("#sCheck_file_filing_means").val()
                      }
                    },
                  {name:'name',index:'name',width:130},
                  {name:'specified_model_name',index:'specified_model_name',width:150},
                  {name:'update_name',index:'update_name',width:70,hidden:true},
                  {name:'updated_time',index:'updated_time',width:120,sorttype:'date',formatter:'date',
                    formatoptions:{
                            srcformat:'Y/m/d H:i:s',
                            newformat:'Y-m-d H:i:s'
                    },hidden:true
                  },
                  {name:'devices_type_id',index:'devices_type_id',hidden:true}
            ],
            rowNum: 20,
            toppager : false,
            pager : "#check_file_manage_listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            ondblClickRow : showUpdate,
            viewsortcols : [true,'vertical',true]    
        });
        $("#gbox_check_file_manage_list .ui-jqgrid-hbox").before(
            '<div class="ui-widget-content" style="padding:4px;">' +
                '<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建点检表" role="button" aria-disabled="false">' +
            '</div>'
        );
        $("input.ui-button").button(); 
        $("#addbutton").click(showAdd);
    }
};

//删除
var showDelete=function(rid){
    var rowData = $("#check_file_manage_list").getRowData(rid);

	warningConfirm("删除不能恢复。确认要删除["+rowData.check_manage_code+"]的记录吗？", 
		function() {
			var data={
				"check_file_manage_id":rowData.check_file_manage_id
			}
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doDelete',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : delete_handleComplete
			});
		},null,
		"删除确认"
	);
};

//删除完成函数
var delete_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
             findit();
			infoPop("删除已经完成。", null, "删除");
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
}

//新建点检表
var showAdd=function(){
    $("#add").show();
    $("#main").hide();
    
    $("#add_check_manage_code").val("").removeClass("errorarea-single");
    $("#add_sheet_file_name").val("").removeClass("errorarea-single");
    $("#add_devices_type_name").val("");
    $("#hidden_add_devices_type_id").val("");
    $("#add_access_place").val("").trigger("change");
    $("#add_cycle_type").val("").trigger("change");
    $("#add_filing_means").val("").trigger("change");
    $("#add_specified_model_name").val("");
    
    $("#add_form").validate({
        rules:{
            check_manage_code:{
                required:true
            },
            devices_type_id:{
                required:true
            },
            access_place:{
                required:true
            },
            file:{
                required:true
            },
            filing_means:{
                required:true
            }
        },
        ignore:''
    });
    
    
    //确然
    $("#confirmbutton").unbind("click");
    $("#confirmbutton").bind("click",function(){
        var data={
            "check_manage_code":$("#add_check_manage_code").val(),
            "devices_type_id":$("#hidden_add_devices_type_id").val(),
            "access_place": $("#add_access_place").val(),
            "cycle_type":$("#add_cycle_type").val(),
            "filing_means":$("#add_filing_means").val(),
            "specified_model_name":$("#add_specified_model_name").val()
        };
           
        if($("#add_form").valid()){
            $.ajaxFileUpload({
                url : servicePath + '?method=doInsert', // 需要链接到服务器地址
                secureuri : false,
                data:data,
                fileElementId : 'add_sheet_file_name', // 文件选择框的id属性
                dataType : 'json', // 服务器返回的格式
                success : function(responseText, textStatus) {
                    var resInfo = null;
                    try {
                        // 以Object形式读取JSON
                        eval('resInfo =' + responseText);
                        if (resInfo.errors.length > 0) {
                            // 共通出错信息框
                            treatBackMessages("#add_form", resInfo.errors);
                        } else {
                            $("#add").hide();
                            $("#main").show();
                            findit();
                        }
                    } catch (e) {
                        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
                    };
                }
             });
        }
    });
    
   
    //取消
    $("#cancelbutton").unbind("click")
    $("#cancelbutton").bind("click",function(){
        $("#add").hide();
        $("#main").show();
    });
};

//修改点检表
var showUpdate=function(){
    var rowId=$("#check_file_manage_list").jqGrid("getGridParam","selrow");
    var rowData=$("#check_file_manage_list").getRowData(rowId);
    
    $("#update").show();
    $("#main").hide();
    
    $("#update_check_manage_code").val(rowData.check_manage_code).removeClass("errorarea-single");//点检表管理号
    $("#show_sheet_file_name").val(rowData.sheet_file_name);//点检表文件
    
    $("#update_sheet_file_name").val("");
    $("#update_devices_type_name").val(rowData.name);//使用设备工具品名
    $("#hidden_update_devices_type_id").val(rowData.devices_type_id);
    $("#update_access_place").val(rowData.access_place).trigger("change");//类型
    
    if(rowData.cycle_type==0){
    	 $("#update_cycle_type").val("").trigger("change");//归档周期
    }else{
    	 $("#update_cycle_type").val(rowData.cycle_type).trigger("change");//归档周期
    }
   
    $("#update_filing_means").val(rowData.filing_means).trigger("change");//归档方式
    $("#label_updated_by_name").text(rowData.update_name);//最后更新人
    $("#label_updated_time").text(rowData.updated_time);
    $("#update_specified_model_name").val(rowData.specified_model_name);//特定机型
    
    $("#update_form").validate({
        rules:{
             check_manage_code:{
                  required:true                
             },
             devices_type_id:{
                required:true            
             },
             access_place:{
                required:true          
             }
        },
        ignore:''
    });
    
    //修改
    $("#updatebutton").unbind("click");
    $("#updatebutton").bind("click",function(){
         var data={
            "check_file_manage_id":rowData.check_file_manage_id,
            "check_manage_code":$("#update_check_manage_code").val(),
            "devices_type_id":$("#hidden_update_devices_type_id").val(),
            "access_place": $("#update_access_place").val(),
            "cycle_type":$("#update_cycle_type").val(),
            "filing_means":$("#update_filing_means").val(),
            "specified_model_name":$("#update_specified_model_name").val()
        };
       
        if($("#update_form").valid()){
           $.ajaxFileUpload({
                url : servicePath + '?method=doUpdate', // 需要链接到服务器地址
                secureuri : false,
                data:data,
                fileElementId : 'update_sheet_file_name', // 文件选择框的id属性
                dataType : 'json', // 服务器返回的格式
                success : function(responseText, textStatus) {
                    var resInfo = null;
                    try {
                        // 以Object形式读取JSON
                        eval('resInfo =' + responseText);
                        if (resInfo.errors.length > 0) {
                            // 共通出错信息框
                            treatBackMessages("#update_form", resInfo.errors);
                        } else {
                            $("#update").hide();
                            $("#main").show();
                            findit();
                        }
                    } catch (e) {
                        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
                    };
                }
           });
        }
    });
    
    
    //取消
    $("#cancelbutton2").click(function(){
        $("#update").hide();
        $("#main").show();
    })
    
}



