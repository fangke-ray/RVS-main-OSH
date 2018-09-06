var list={};

var servicePath="tools_manage.do";
 var DataObj;
$(function(){
    $("#body-mdl span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    $("#waste_old_products").buttonset();

    $("#search_manage_level,#search_section_id,#search_line_id,#search_status," +
      "#update_section_id,#update_manage_level,#add_section_id,#update_responsible_line_id," +
      "#add_line_id,#add_manage_level,#update_line_id,#update_status,#add_status,#replace_manage_level," +
      "#replace_status,#replace_section_id,#replace_line_id,#deliver_section_name,#deliver_line_name," +
      "#to_section_name,#to_line_name").select2Buttons();
    
    /*责任工位*/
    setReferChooser($("#hidden_search_position_id"),$("#search_position_referchooser"));
    setReferChooser($("#hidden_update_position_id"),$("#update_position_referchooser"));
    setReferChooser($("#hidden_add_position_id"),$("#add_position_referchooser"));
    setReferChooser($("#hidden_deliver_position_id"),$("#deliver_position_referchooser"));
    setReferChooser($("#hidden_to_position_id"),$("#to_position_referchooser"));

    /*责任人员*/
    setReferChooser($("#hidden_search_responsible_operator_id"),$("#search_responsible_operator_referchooser"));
    setReferChooser($("#hidden_update_responsible_operator_id"),$("#update_responsible_operator_referchooser"));
    setReferChooser($("#hidden_add_responsible_operator_id"),$("#add_responsible_operator_referchooser"));
    setReferChooser($("#hidden_deliver_operator_id"),$("#devlier_responsible_operator_referchooser"));
    setReferChooser($("#hidden_to_operator_id"),$("#to_responsible_operator_referchooser"));
    
    /*管理员*/
    setReferChooser($("#hidden_search_manager_operator_id"),$("#search_manager_operator_referchooser")); 
    setReferChooser($("#hidden_update_manager_operator_id"),$("#update_manager_operator_referchooser")); 
    setReferChooser($("#hidden_add_manager_operator_id"),$("#add_manager_operator_referchooser")); 
    setReferChooser($("#hidden_replace_manager_operator_id"),$("#replace_manager_operator_referchooser"));
    setReferChooser($("#hidden_deliver_manager_operator_id"),$("#deliver_manager_operator_referchooser"));
    setReferChooser($("#hidden_to_manager_operator_id"),$("#to_manager_operator_referchooser"));
    
    setReferChooser($("#hidden_replace_position_id"),$("#replace_position_referchooser"));
    setReferChooser($("#hidden_replace_responsible_operator_id"),$("#replace_responsible_operator_referchooser"));
    
    /*按钮事件*/
    $("input.ui-button").button();
    
    $("#add_total_price,#update_total_price").keydown(function(evt){
        if (!((evt.keyCode >= 48 && evt.keyCode <= 57) ||  evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39 || evt.keyCode==190)){
            return false;
        }
	});
    
    $("#add_import_date,#update_import_date,#update_waste_date,#add_waste_date,#replace_import_date,#add_order_date,#update_order_date,#replace_order_date,#search_order_date_start,#search_order_date_end" +
    		",#search_import_date_start,#search_import_date_end,#search_waste_date_start,#search_waste_date_end").datepicker({
        showButtonPanel : true,
        dateFormat : "yy/mm/dd",
        currentText : "今天"
    });

    /*检索button*/
    $("#searchbutton").click(function(){
        findit();
        $("#replacebutton").disable();
    });
    
    /*清空button*/
    $("#resetbutton").click(function(){
        reset();
    });
    
    $("#search_status option[value='1']").attr("selected","selected");
    $("#search_status option[value='4']").attr("selected","selected").trigger("change");

    findit();
    $("#s2b_update_section_id").find("li:eq(0)").remove();
    $("#replacebutton").disable();
    
   
    //替换新品
	$("#replacebutton").click(function(){
	    var rowID=$("#list").jqGrid("getGridParam","selrow");
	    var rowData=$("#list").getRowData(rowID);
	    DataObj=rowData;
	     var data = {
	        "manage_code":rowData.manage_code
	     };
	     
	     //隐藏替换新品之前的治具的管理编号
	     $("#hidden_old_manage_code").val(rowData.manage_code);
	     //隐藏替换新新品之前的治具的tools_manage_id
	     $("#hidden_old_tools_manage_id").val(rowData.tools_manage_id);
	     
	     $.ajax({
	        beforeSend : ajaxRequestType,
	        async : true,
	        url : servicePath + '?method=searchMaxManageCode',
	        cache : false,
	        data : data,
	        type : "post",
	        dataType : "json",
	        success : ajaxSuccessCheck,
	        error : ajaxError,
	        complete : searchMaxManageCode_handleComplete
	    });
	});
	
	//批量交付--初始化查询
	deliver_filed_list("");
	
	//批量交付
	$("#deliverbutton").click(function(){
		deliver();
	});
	
	//交付动作
	$("#more_update").click(more_update);

});

var more_update = function(){
	
	var rowids = $('#deliver_list').jqGrid('getGridParam','selarrrow');
	var length = rowids.length;
	var data = {
			"section_id":$("#to_section_name").val(),
			"line_id":$("#to_line_name").val(),
			"position_id":$("#hidden_to_position_id").val(),
			"responsible_operator_id":$("#hidden_to_operator_id").val(),
			"manager_operator_id":$("#hidden_to_manager_operator_id").val(),
			
			"compare_section_id":$("#deliver_section_name").data("post")==$("#to_section_name").val(),
			"compare_line_id":$("#deliver_line_name").data("post")==$("#to_line_name").val(),
			"compare_position_id":$("#hidden_deliver_position_id").data("post")==$("#hidden_to_position_id").val(),
			"compare_responsible_operator_id":$("#hidden_deliver_operator_id").data("post")==$("#hidden_to_operator_id").val(),
			"compare_manager_operator_id":$("#hidden_deliver_manager_operator_id").data("post")==$("#hidden_to_manager_operator_id").val()
	};
	
	//批量交付-全选
	if($("#cb_deliver_list").attr("checked")=="checked"){
		for(var i=0;i<list.length;i++){
			data["keys.tools_manage_id[" + i + "]"] = list[i].tools_manage_id;
		}
	}else{
		for (var i in rowids) {
			var rowData = $("#deliver_list").getRowData(rowids[i]);
			data["keys.tools_manage_id[" + i + "]"] = rowData["tools_manage_id"];
		}
	}

	$.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=dodeliver',
        cache : false,
        data : data,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete :deliver_update_Complete
    });
}

var deliver_update_Complete = function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            $("#dialog_confrim").text("交付已经完成。");
            $("#dialog_confrim").dialog({
               width : 320,
               height : 'auto',
               resizable : false,
               show : "blind",
               modal : true,
               title : "交付",
               buttons : {
                   "关闭" : function() {                                 
                       $(this).dialog("close");
                       deliver_findit();
                   }
               }
           });          
        }
    }catch (e) {};
}

//批量交付
var deliver = function(){
	//左侧内容清空--start
	$("#deliver_section_name").val("").trigger("change");
	$("#deliver_line_name").val("").trigger("change");
	$("#deliver_position_id").val("");
	$("#hidden_deliver_position_id").val("");
	$("#deliver_operator_id").val("");
	$("#hidden_deliver_operator_id").val("");
	$("#deliver_manager_operator_id").val("");
	$("#hidden_deliver_manager_operator_id").val("");
	//左侧内容清空--end
	
	//右侧内容清空--start
	$("#to_section_name").val("").trigger("change");
	$("#to_line_name").val("").trigger("change");
	$("#to_position_id").val("");
	$("#hidden_to_position_id").val("");
	$("#to_operator_id").val("");
	$("#hidden_to_operator_id").val("");
	$("#to_manager_operator_id").val("");
	$("#hidden_to_manager_operator_id").val("");
	//右侧内容清空--end
	
	deliver_filed_list("");
	//deliver_findit();
	
    $("#deliver").dialog({
        position : 'center',
        title : "批量交付",
        width :1200,
        height : 'auto',
        resizable : false,
        modal : true,
        show : "blind",
        buttons : {
             "关闭":function(){
                 $("#deliver").dialog('close');
             }
        }
    });
    
    //批量交付--查询
    $("#searchDetail").click(function(){
    	//点击检索之后，将检索条件的值放在data("post")中
    	$("#deliver_section_name").data("post",$("#deliver_section_name").val());
    	$("#deliver_line_name").data("post",$("#deliver_line_name").val());
		$("#deliver_position_id").data("post",$("#deliver_position_id").val());
		$("#hidden_deliver_position_id").data("post",$("#hidden_deliver_position_id").val());
		$("#deliver_operator_id").data("post",$("#deliver_operator_id").val());
		$("#hidden_deliver_operator_id").data("post",$("#hidden_deliver_operator_id").val());
		$("#deliver_manager_operator_id").data("post",$("#deliver_manager_operator_id").val());
		$("#hidden_deliver_manager_operator_id").data("post",$("#hidden_deliver_manager_operator_id").val());
		
		//批量交付时，将左边的检索条件数据复制到右边--右部分设值--start
		$("#to_section_name").val($("#deliver_section_name").val()).trigger("change");
		$("#to_line_name").val($("#deliver_line_name").val()).trigger("change");
		$("#to_position_id").val($("#deliver_position_id").val());
		$("#hidden_to_position_id").val($("#hidden_deliver_position_id").val());
		$("#to_operator_id").val($("#deliver_operator_id").val());
		$("#hidden_to_operator_id").val($("#hidden_deliver_operator_id").val());
		$("#to_manager_operator_id").val($("#deliver_manager_operator_id").val());
		$("#hidden_to_manager_operator_id").val($("#hidden_deliver_manager_operator_id").val());
		//右部分设值--end
		deliver_findit();
    });
}
var deliver_findit = function(){
	var data={
            "section_id":$("#deliver_section_name").val(),
            "line_id":$("#deliver_line_name").val(),
            "position_id":$("#hidden_deliver_position_id").val(),
            "responsible_operator_id":$("#hidden_deliver_operator_id").val(),
            "manager_operator_id":$("#hidden_deliver_manager_operator_id").val()
      };
      // Ajax提交
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
          complete : deliver_handleComplete
      });
}

var deliver_handleComplete = function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var listdata = resInfo.toolsManageForms;
            deliver_filed_list(listdata);
        }
    }catch (e) {};
}

var findit = function(arg) {
      var data = {
            "manage_code":$("#search_manage_code").val(),
            "tools_no":$("#search_tools_no").val(),
            "tools_name":$("#search_tools_name").val(),
            "section_id":$("#search_section_id").val(),
            "line_id":$("#search_line_id").val(),            
            "manage_level":$("#search_manage_level").val(),
            "status":$("#search_status").val() && $("#search_status").val().toString(),//默认是选择使用中和保管中
            "position_id":$("#hidden_search_position_id").val(),
            "responsible_operator_id":$("#hidden_search_responsible_operator_id").val(),
            "order_date_start":$("#search_order_date_start").val(),
            "order_date_end":$("#search_order_date_end").val(),
            "import_date_start":$("#search_import_date_start").val(),
            "import_date_end":$("#search_import_date_end").val(),
            "waste_date_start":$("#search_waste_date_start").val(),
            "waste_date_end":$("#search_waste_date_end").val()
        };
    // Ajax提交
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

var search_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
        	$("#hidden_import_date").val(resInfo.current_date);
            var listdata = resInfo.toolsManageForms;
            filed_list(listdata);
        }
    }catch (e) {};
}

var reset = function(){
    $("#search_manage_code").data("post","").val("");
    $("#search_tools_no").data("post","").val("");
    $("#search_tools_name").data("post","").val("");
    $("#hidden_search_tools_name").data("post","").val("");
    $("#search_section_id").data("post","").val("").trigger("change");
    $("#search_line_id").data("post","").val("").trigger("change");
    $("#search_manage_level").data("post","").val("").trigger("change");
    //$("#search_manager_operator_id").data("post","").val("");
    //$("#hidden_search_manager_operator_id").data("post","").val("");
    $("#search_status").data("post","").val("").trigger("change");
    $("#search_position_id").data("post","").val("");
    $("#hidden_search_position_id").data("post","").val("");
    $("#hidden_position_id").data("post","").val("");
    $("#search_responsible_operator_name").data("post","").val("");
    $("#hidden_search_responsible_operator_id").data("post","").val("");
    $("#search_order_date_start").val("");
    $("#search_order_date_end").val("");
    $("#search_import_date_start").val("");
    $("#search_import_date_end").val("");
    $("#search_waste_date_start").val("");
    $("#search_waste_date_end").val("");
};

var deliver_filed_list = function(listdata){
    if($("#gbox_deliver_list").length > 0) {
        $("#deliver_list").jqGrid().clearGridData();
        $("#deliver_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#deliver_list").jqGrid({
            data:listdata,
            height: 298,
            width:422,
            rowheight: 23,
            datatype: "local",
            colNames:
                ['治具管理ID','管理编号','治具No.','治具名称','治具品名ID','管理员ID','管理员','管理<br>等级','状态','总价',
                '分发课室ID','分发课室','责任工程ID','责任工程','责任工位ID','责任工位','放置位置','责任人员ID','责任人','导入日期','发放日期','发放者',
                '废弃日期','更新时间','更新人','删除标记','备注','订购日期'],
            colModel:[
                {name:'tools_manage_id',index:'tools_manage_id',hidden:true},
                {name:'manage_code',index:'manage_code',width:60,align:'left'},
                {name:'tools_no',index:'tools_no',width:80,align:'left'},
                {name:'tools_name',index:'tools_name',width:180,align:'left'},
                {name:'tools_type_id',index:'tools_type_id',width:120,align:'left',hidden:true},
                {name:'manager_operator_id',index:'manager_operator_id',width:120,align:'center',hidden:true},    
               	{name:'manager_operator',index:'manager_operator',width:60,align:'center',hidden:true},
                {name:'manage_level',index:'manage_level',width:50,align:'center',hidden:true,
                    formatter : 'select',
                    editoptions : {
                        value : $("#hidden_goManage_level").val()
                    }  
                 },
                {name:'status',index:'status',width:60,align:'center',hidden:true,
                    formatter : 'select',
                    editoptions : {
                        value : $("#hidden_goStatus").val()
                    }
                },
                {name:'total_price',index:'total_price',width:60,align:'right',hidden:true},
                {name:'section_id',index:'section_id',width:100,align:'center',hidden:true},
                {name:'section_name',index:'section_name',width:60,align:'center',hidden:true},
                {name:'line_id',index:'line_id',width:100,align:'center',hidden:true},
                {name:'line_name',index:'line_name',width:60,align:'center',hidden:true},
                {name:'position_id',index:'position_id',width:80,align:'center',hidden:true},
                {name:'process_code',index:'process_code',width:60,align:'center',hidden:true},
                {name:'location',index:'location',width:140,align:'center',hidden:true},
                {name:'responsible_operator_id',index:'responsible_operator_id',width:120,align:'center',hidden:true},    
                {name:'responsible_operator',index:'responsible_operator',width:80,align:'left',hidden:true},
                {name:'import_date',index:'import_date',width:120,align:'center',hidden:true},
                {name:'provide_date',index:'provide_date',width:120,align:'center',hidden:true},
                {name:'provider',index:'provider',width:120,align:'center',hidden:true,
                    formatter : function(value, options, rData) {
                        //当发放日期不为空时，发放者是当前更新人；如果为空时，发放者是空白
                        if(rData.provide_date){
                            return rData.updated_by;
                        }else{
                            return "";
                        }                           
                    }},
                {name:'waste_date',index:'waste_date',width:120,align:'center',hidden:true}, 
                {name:'updated_time',index:'updated_time',width:120,align:'center',hidden:true}, 
                {name:'updated_by',index:'updated_by',width:85,align:'center',hidden:true},
                {name:'delete_flg',index:'delete_flg',hidden:true},
                {name:'comment',index:'comment',hidden:true},
                {name:'order_date',index:'order_date',hidden:true}
            ],
            rownumbers:true,
            toppager : false,
            rowNum : 20,
            sortorder:"asc",
            sortname:"id",
            multiselect: true,
            pager : "#deliver_listpager",
            viewrecords : true,
            ondblClickRow : showDetail,
            // onSelectRow:enableButton,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true,
            pginput : false,
            recordpos : 'left',
            viewsortcols : [true, 'vertical', true],
            gridComplete:function(){
            }
        });
    }
    list=listdata;
}
var filed_list=function(listdata){
    if($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    }else{
        $("#list").jqGrid({
            data:listdata,
            height: 390,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:
                ['治具管理ID','管理编号','治具No.','治具名称','治具品名ID','管理员ID','管理员','管理<br>等级','状态','总价',
                '分发课室ID','分发课室','责任工程ID','责任工程','责任工位ID','责任工位','放置位置','责任人员ID','责任人','导入日期','发放日期','发放者',
                '废弃日期','更新时间','更新人','删除标记','备注','订购日期'],
            colModel:[
                {name:'tools_manage_id',index:'tools_manage_id',hidden:true},
                {name:'manage_code',index:'manage_code',width:60,align:'left'},
                {name:'tools_no',index:'tools_no',width:80,align:'left'},
                {name:'tools_name',index:'tools_name',width:180,align:'left'},
                {name:'tools_type_id',index:'tools_type_id',width:120,align:'left',hidden:true},
                {name:'manager_operator_id',index:'manager_operator_id',width:120,align:'center',hidden:true},    
               	{name:'manager_operator',index:'manager_operator',width:60,align:'center'},
                {name:'manage_level',index:'manage_level',width:50,align:'center',
                    formatter : 'select',
                    editoptions : {
                        value : $("#hidden_goManage_level").val()
                    }  
                 },
                {name:'status',index:'status',width:60,align:'center',
                    formatter : 'select',
                    editoptions : {
                        value : $("#hidden_goStatus").val()
                    }
                },
                {name:'total_price',index:'total_price',width:60,align:'right'},
                {name:'section_id',index:'section_id',width:100,align:'center',hidden:true},
                {name:'section_name',index:'section_name',width:60,align:'center',hidden:false},
                {name:'line_id',index:'line_id',width:100,align:'center',hidden:true},
                {name:'line_name',index:'line_name',width:60,align:'center',hidden:false},
                {name:'position_id',index:'position_id',width:80,align:'center',hidden:true},
                {name:'process_code',index:'process_code',width:60,align:'center'},
                {name:'location',index:'location',width:140,align:'center'},
                {name:'responsible_operator_id',index:'responsible_operator_id',width:120,align:'center',hidden:true},    
                {name:'responsible_operator',index:'responsible_operator',width:80,align:'left',hidden:false},
                {name:'import_date',index:'import_date',width:120,align:'center',hidden:true},
                {name:'provide_date',index:'provide_date',width:120,align:'center',hidden:true},
                {name:'provider',index:'provider',width:120,align:'center',hidden:true,
                    formatter : function(value, options, rData) {
                        //当发放日期不为空时，发放者是当前更新人；如果为空时，发放者是空白
                        if(rData.provide_date){
                            return rData.updated_by;
                        }else{
                            return "";
                        }                           
                    }},
                {name:'waste_date',index:'waste_date',width:120,align:'center',hidden:true}, 
                {name:'updated_time',index:'updated_time',width:120,align:'center',hidden:true}, 
                {name:'updated_by',index:'updated_by',width:85,align:'center',hidden:true},
                {name:'delete_flg',index:'delete_flg',hidden:true},
                {name:'comment',index:'comment',hidden:true},
                {name:'order_date',index:'order_date',hidden:true}
            ],
            rownumbers:true,
            toppager : false,
            rowNum : 20,
            pager : "#listpager",
            viewrecords : true,
            ondblClickRow : showDetail,
            onSelectRow:enableButton,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true,
            pginput : false,
            recordpos : 'left',
            viewsortcols : [true, 'vertical', true]
        });
        
        /*登记*/
        $("#addbutton").click(function(){
            $("#body-regist").show();
            $("#body-mdl").hide();

            $("#add_name").val("");
            $("#add_model_name").val("");
            $("#add_tool_no").val("");
            $("#add_manage_code").val("");
            $("#add_location").val("");
            $("#add_comment").val("");

            $("#chosebutton").click(function(){
                var this_dialog = $("#chose_dialog");
                if (this_dialog.length === 0) {
                    $("body.outer").append("<div id='chose_dialog'/>");
                    this_dialog = $("#chose_dialog");
                }
                this_dialog.html(
                    '<div class="ui-widget-content" style="width:780px;">'+
                        '<form method="POST">'+
                            '<table class="condform">'+
                                '<tr>'+
                                    '<td class="ui-state-default td-title" style="width: 131px;">治具名称</td>'+
                                    '<td class="td-content"><input type="text" name="name" alt="治具名称" id="search_define_name" maxlength="14" class="ui-widget-content"/></td>'+
                                '</tr>'+
                            '</table>'+
                            '<div style="height:44px">'+
                                '<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">'+
                                '<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">'+
                            '</div>'+
                        '</form>'+
                    '</div>'+
                    '<table id="devices_check_define_list"></table>'+
                    '<div id="devices_check_definepager"></div>'
                );
                
                chose_list(finished_list);
                this_dialog.dialog({
                    position : 'center',
                    title : "工具品名",
                    width : 800,
                    height :  'auto',
                    resizable : false,
                    modal : true,
                    buttons : {
                        "取消":function(){
                                this_dialog.dialog('close');
                                this_dialog.html("");
                        }
                    }
                });

            });
            //新建治具管理
            showAdd();
        });
    }
};

var searchMaxManageCode_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
            } else {
                showReplace(DataObj,resInfo.manage_code);
            }
    }catch (e) {};
}

var enableButton=function(){
    var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
    if (row != null) {
        $("#replacebutton").enable();
    }else{
        $("#replacebutton").disable();
    }
     
};

var showReplace=function(rowData,manage_code){
	//同时废弃掉旧品--默认被选择否
	$("#waste_old_products_no").attr("checked","checked").trigger("change");
	
    $("#replace_manage_code").val(manage_code).removeClass("errorarea-single");//管理编号
    $("#replace_tools_no").val(rowData.tools_no);//治具NO.
    $("#replace_tools_name").val(rowData.tools_name);//治具名称
    $("#hidden_replace_tools_name").val(rowData.tools_type_id);//治具ID
    $("#replace_manager_operator_id").val(rowData.manager_operator);
    $("#hidden_replace_manager_operator_id").val(rowData.manager_operator_id);
    $("#replace_localtion").val(rowData.location);//放置位置
    $("#replace_manage_level").val(rowData.manage_level).trigger("change");//管理等级
    $("#replace_status").val(rowData.status).trigger("change");//状态
    $("#replace_total_price").val(rowData.total_price);//总价
    $("#replace_classify").val(rowData.classify);//分类
    $("#replace_section_id").val(rowData.section_id).trigger("change");//分发课室
    $("#replace_line_id").val(rowData.line_id).trigger("change");//责任工程
    $("#replace_position_id").val(rowData.process_code);//责任工位
    $("#hidden_replace_position_id").val(rowData.position_id);//责任工位ID
    $("#replace_responsible_operator_id").val(rowData.responsible_operator);//责任人员
    $("#hidden_replace_responsible_operator_id").val(rowData.responsible_operator_id);//责任人员ID
    $("#replace_comment").val(rowData.comment);//备注
    
    /**点击替换新品时，这几项内容为空**/
    $("#replace_import_date").val("");//导入日期
    $("#replace_order_date").val("");//订购日期
    $("#replace_provider").text("");//发放者
    $("#replace_provide_date").text("");//发放日期
    $("#replace_updated_time").text("");//更新时间
    
    $("#replace_form").validate({
       rules:{
            manage_code:{
                required:true,
                maxlength : 9
            },
            tools_no:{
                required:true,
                maxlength :16
            },
            tools_type_id:{
                required:true
            },
            manage_level:{
                required:true
            },
            section_id:{
                required:true
            },
            status:{
                required:true
            },manager_operator_id:{
                 required:true
            }
        },
        ignore:'false'
    });
    
    $("#replace").dialog({
        position : 'center',
        title : "替换新品",
        width :900,
        height : 640,
        resizable : false,
        modal : true,
        show : "blind",
        buttons : {
             "新建":function(){
                if ($("#replace_form").valid()) {
		            var data={
		                  "compare_status":rowData.status==$("#replace_status").val(),
		                  "manage_code":$("#replace_manage_code").val(), 
		                  "tools_no": $("#replace_tools_no").val(),
		                  "tools_type_id":$("#hidden_replace_tools_name").val(),
		                  "tools_name":$("#replace_tools_name").val(),
		                  "manager_operator_id":$("#hidden_replace_manager_operator_id").val(),
		                  "location":$("#replace_localtion").val(), 
		                  "manage_level": $("#replace_manage_level").val(),
		                  "status": $("#replace_status").val(),
		                  "total_price":$("#replace_total_price").val(), 
		                  "classify":$("#replace_classify").val(),
		                  "section_id": $("#replace_section_id").val(),
		                  "line_id":$("#replace_line_id").val(), 
		                  "position_id": $("#hidden_replace_position_id").val(),
		                  "responsible_operator_id": $("#hidden_replace_responsible_operator_id").val(),
		                  "import_date":$("#replace_import_date").val(), 
		                  "comment":$("#replace_comment").val(),
                          "order_date":$("#replace_order_date").val(),
                          "responsible_operator_id": $("#hidden_replace_responsible_operator_id").val(),
                          
                          "waste_old_products":$("#waste_old_products input:checked").val(),//--同时废弃掉旧品,
                          "tools_manage_id":$("#hidden_old_tools_manage_id").val()
		            };
		            // Ajax提交
		            $.ajax({
		                beforeSend : ajaxRequestType,
		                async : true,
		                url : servicePath + '?method=doReplace',
		                cache : false,
		                data : data,
		                type : "post",
		                dataType : "json",
		                success : ajaxSuccessCheck,
		                error : ajaxError,
		                complete : replace_handleComplete
		            });
                }
             },
             "取消":function(){
                 $("#replace").dialog('close');
             }
             
        }
    });
   
};

var replace_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
            findit();
            $("#replace").dialog('close');
            $("#replacebutton").disable();
        }
    }catch (e) {};
};


var showAdd= function(){
    //清空上一次新建
    $("#add_manage_code").val("");
	$("#add_tools_no").val("");
	$("#add_tools_name").val("");
	$("#add_localtion").val("");
	$("#add_manager_operator_id").val("");
    $("#hidden_add_manager_operator_id").val("");
    $("#add_import_date").val("");
	$("#add_manage_level").val("").trigger("change");
	$("#add_total_price").val("");
    $("#add_classify").val("");
	$("#add_section_id").val("").trigger("change");
	$("#add_line_id").val("").trigger("change");
    $("#add_position_id").val("");
	$("#hidden_add_position_id").val("");
    $("#add_responsible_operator_id").val("");
	$("#hidden_add_responsible_operator_id").val("");
	$("#add_waste_date").val("").hide();
	$("#add_status").val("").trigger("change");
	$("#add_comment").val("");
    $("#add_order_date").val("");
    
    //状态选择
    $("#add_status").bind("change", function() {
          //如果状态是遗失或者损坏--废弃日期可填
          if($(this).val()==2 || $(this).val()==3){
             $("#add_waste_date").show();
          }else{
             $("#add_waste_date").hide();
          }
    });
    
    $("#add_import_date").val($("#hidden_import_date").val());
    $("#registorm_form").validate({
        rules:{
            manage_code:{
                required:true
            },
            tools_no:{
                required:true
            },
            tools_type_id :{
                required:true
            },section_id:{
                required:true
            },status:{
                required:true
            },manager_operator_id:{
                 required:true
            }
        },
        ignore:'false'
    });

    //新建
    $("#confirebutton").click(function(){
      if ($("#registorm_form").valid()) {
        $("#dialog_confrim").html("");
        $("#dialog_confrim").html("是否新建管理编号为"+$("#add_manage_code").val()+",治具NO.为"+$("#add_tools_name").val()+"的治具?");
        $("#dialog_confrim").dialog({
            position : 'center',
            title : "新建确认",
            width :350,
            height : 150,
            resizable : false,
            modal : true,
            buttons : {
                "确定":function(){
                     $(this).dialog("close");
                     var data={
                        "manage_code":$("#add_manage_code").val(), 
                        "tools_no": $("#add_tools_no").val(),
                        "tools_name":$("#add_tools_name").val(), 
                        "tools_type_id":$("#hidden_add_tools_name").val(),
                        "location":$("#add_localtion").val(), 
                        "manage_level": $("#add_manage_level").val(),
                        "total_price":$("#add_total_price").val(), 
                        "classify":$("#add_classify").val(),
                        "section_id": $("#add_section_id").val(),
                        "line_id":$("#add_line_id").val(), 
                        "position_id": $("#hidden_add_position_id").val(),
                        "responsible_operator_id": $("#hidden_add_responsible_operator_id").val(),
                        "import_date":$("#add_import_date").val(), 
                        "provide_date": $("#add_provide_date").val(),
                        "waste_date": $("#add_waste_date").val(),
                        "status": $("#add_status").val(),
                        "comment":$("#add_comment").val(),
                        "order_date":$("#add_order_date").val(),
                        "manager_operator_id":$("#hidden_add_manager_operator_id").val()
                     };
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
                "取消":function(){
                        $("#dialog_confrim").html("");
                        $("#dialog_confrim").dialog('close');
                }
            }
        });
        }
    }); 

    $("#goback").click(function(){
        $("#body-regist").hide();
        $("#body-mdl").show();
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
             $("#dialog_confrim").text("新建已经完成。");
             $("#dialog_confrim").dialog({
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
var chose_list=function(finished_list){
    if ($("#gbox_devices_check_define_list").length > 0) {
        $("#devices_check_define_list").jqGrid().clearGridData();
        $("#devices_check_define_list").jqGrid('setGridParam',{data:finished_list}).trigger("reloadGrid", [{current:false}]);
    } else {
         $("#devices_check_define_list").jqGrid({
            data:finished_list,
            height: 361,
            width: 780,
            rowheight: 23,
            datatype: "local",
            colNames:['治具点检管理ID','治具名称','删除标记','最后更新人','最后更新时间'],
            colModel:[
               {name:'tools_check_manage_id',index:'tools_check_manage_id', hidden:true}, 
               {name:'name',index:'name',width : 200},                       
               {name:'delete_flg',index:'delete_flg',hidden:true}, 
               {name:'updated_by',index:'updated_by',width : 60}, 
               {name:'updated_time',index:'updated_time',width : 120}               
            ],
             rowNum: 20,
            toppager : false,
            pager : "#devices_check_definepager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
            pginput : false,    
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,  
            ondblClickRow : choseName,
            viewsortcols : [true,'vertical',true]     
         });
    }
};

var choseName=function(){
    var rowId=$("#devices_check_define_list").jqGrid("getGridParam","selrow");//获取选中行的ID
    var rowData=$("#devices_check_define_list").getRowData(rowId);//
    $("#add_name").val(rowData.name);
    $("#chose_dialog").dialog('close');
};

var showDetail = function(){
    //状态选择
    $("#update_status").bind("change", function() {
          //如果状态是遗失或者损坏--废弃日期可填
          if($(this).val()==2 || $(this).val()==3){
             $("#update_waste_date").show();
          }else{
             $("#update_waste_date").hide();
          }
    });
    
    $("#body-mdl").hide();
    $("#body-detail").show();
    $("#body-regist").hide();

    var rowID=$("#list").jqGrid("getGridParam","selrow");
    var rowData=$("#list").getRowData(rowID);

    $("#hidden_tools_manage_id").val(rowData.tools_manage_id);
    //$("#hidden_tools_type_id").val(rowData.tools_type_id);
    
    $("#update_manage_code").val(rowData.manage_code);
    $("#update_tools_no").val(rowData.tools_no);
    $("#update_tools_name").val(rowData.tools_name);
    $("#hidden_update_tools_name").val(rowData.tools_type_id);
    
    $("#update_manage_level").val(rowData.manage_level).trigger("change");
    $("#update_status").val(rowData.status).trigger("change");
    
    $("#update_total_price").val(rowData.total_price);
    $("#update_classify").val(rowData.classify);
    $("#update_type").val(rowData.type);
    //课室ID
    $("#update_section_id").val(rowData.section_id).trigger("change");
    //工程ID
    $("#update_line_id").val(rowData.line_id).trigger("change"); 
    //$("#update_responsible_line_id").val(rowData.responsible_line_id).trigger("change");
    $("#update_position_id").val(rowData.process_code);
    $("#hidden_update_position_id").val(rowData.position_id);
    $("#update_localtion").val(rowData.location);
    $("#update_provider").text(rowData.provider);
    $("#update_provide_date").text(rowData.provide_date);
    $("#update_import_date").val(rowData.import_date);
    $("#update_waste_date").val(rowData.waste_date);
    $("#update_updated_time").text(rowData.updated_time);
    $("#update_statu").val(rowData.status).trigger("change"); 
    $("#update_total_price").val(rowData.total_price);
    $("#update_responsible_operator_id").val(rowData.responsible_operator);
 
    $("#update_manager_operator_id").val(rowData.manager_operator);
    $("#hidden_update_manager_operator_id").val(rowData.manager_operator_id);
    
    $("#hidden_update_responsible_operator_id").val(rowData.responsible_operator_id);
    $("#update_comment").val(rowData.comment);
    $("#update_order_date").val(rowData.order_date);
    
    $("#update_form").validate({
       rules:{
            manage_code:{
                required:true,
                maxlength : 9
            },
            tools_no:{
                required:true,
                maxlength :16
            },
            tools_type_id:{
                required:true
            },
            manage_level:{
                required:true
            },
            section_id:{
                required:true
            },
            status:{
                required:true
            },
            manager_operator_id:{
                required:function() {
                	return $("#update_status").val() == 1;
                }
            }
        },
        ignore:'true'
    });

    /*修改*/
    $("#updatebutton").click(function(){
       if ($("#update_form").valid()) {
        $("#dialog_confrim").html("");
        $("#dialog_confrim").html("是否修改管理编号为"+$("#update_manage_code").val()+",治具NO.为"+$("#update_tools_no").val()+"的治具？");
        $("#dialog_confrim").dialog({
            position : 'center',
            title : "修改确认",
            width :350,
            height : 150,
            resizable : false,
            modal : true,
            buttons : {
                "确定":function(){
                     $(this).dialog("close");
                     var data={
                          "compare_status":rowData.status==$("#update_status").val(),
                          "tools_manage_id":$("#hidden_tools_manage_id").val(),
                          "tools_type_id":$("#hidden_update_tools_name").val(),
                          "manage_code":$("#update_manage_code").val(), 
						  "tools_no": $("#update_tools_no").val(),
						  "tools_name":$("#update_tools_name").val(), 
						  "location":$("#update_localtion").val(), 
						  "manager_operator_id":$("#hidden_update_manager_operator_id").val(), 
						  "manage_level": $("#update_manage_level").val(),
						  "total_price":$("#update_total_price").val(), 
						  "classify":$("#update_classify").val(),
						  "section_id": $("#update_section_id").val(),
						  "line_id":$("#update_line_id").val(), 
						  "position_id": $("#hidden_update_position_id").val(),
						  "responsible_operator_id": $("#hidden_update_responsible_operator_id").val(),
						  "import_date":$("#update_import_date").val(), 
						  "provide_date": $("#update_provide_date").val(),
						  "waste_date": $("#update_waste_date").val(),
						  "status": $("#update_status").val(),
						  "comment":$("#update_comment").val(),
                          "order_date":$("#update_order_date").val()
                     };
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
                "取消":function(){
                        $("#dialog_confrim").html("");
                        $("#dialog_confrim").dialog('close');
                }
            }
        });
       }
    });
    
    /*确认删除*/
    $("#delbutton").click(function(){
        $("#dialog_confrim").html("");
        $("#dialog_confrim").html("确认删除管理编号为"+$("#update_manage_code").val()+",治具NO.为"+$("#update_tools_no").val()+"的治具?");
        $("#dialog_confrim").dialog({
            position : 'center',
            title : "删除确认",
            width :350,
            height : 150,
            resizable : false,
            modal : true,
            buttons : {
                "确定":function(){
                     $(this).dialog("close");
                     var data={
                        "tools_manage_id":$("#hidden_tools_manage_id").val()
                     };
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
                "取消":function(){
                      $("#dialog_confrim").html("");
                      $("#dialog_confrim").dialog('close');
                }
            }
        });
    });

    $("#recoverbutton").click(function(){
        $("#dialog_confrim").html("");
        $("#dialog_confrim").html("确认回收管理编号为"+$("#label_manage_code").text()+",治具NO.为"+$("#label_name").text()+"的治具?");
        $("#dialog_confrim").dialog({
            position : 'center',
            title : "回收确认",
            width :350,
            height : 150,
            resizable : false,
            modal : true,
            buttons : {
                "确定":function(){
                        $("#dialog_confrim").html("");
                        $("#dialog_confrim").dialog('close');
                        $("#body-mdl").show();
                        $("#body-detail").hide();
                        $("#body-regist").hide();
                },
                "取消":function(){
                        $("#dialog_confrim").html("");
                        $("#dialog_confrim").dialog('close');
                }
            }
        });
    });

    /*返回*/
    $("#resetbutton3").click(function(){
        $("#body-mdl").show();
        $("#body-detail").hide();
        $("#body-regist").hide();
    });
};
var update_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
             $("#dialog_confrim").text("修改已经完成。");
             $("#dialog_confrim").dialog({
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

var delete_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
             $("#dialog_confrim").text("删除已经完成。");
             $("#dialog_confrim").dialog({
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


var showList = function(){
    $("#body-mdl").show();
    $("#body-detail").hide();
    $("#body-regist").hide();
}