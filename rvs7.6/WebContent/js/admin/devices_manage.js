var servicePath="devices_manage.do";
var list={};
$(function(){
	$("#body-mdl span.ui-icon,#listarea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().slideToggle("blind");
		} else {
			$(this).parent().parent().next().slideToggle("blind");
		}
	});

	/*按钮事件*/
	$("input.ui-button").button();
	
	$("#waste_old_products").buttonset();
	
	/*品名*/
	setReferChooser($("#hidden_search_name"),$("#name_referchooser"));
	setReferChooser($("#hidden_update_name"),$("#name_referchooser"));
    setReferChooser($("#hidden_add_name"),$("#name_referchooser"));
    
    /*管理员*/
    setReferChooser($("#hidden_search_manager_operator_id"),$("#operator_name_referchooser"));
    setReferChooser($("#hidden_add_manager_operator_id"),$("#operator_name_referchooser"));
    setReferChooser($("#hidden_update_manager_operator_id"),$("#operator_name_referchooser"));
	setReferChooser($("#hidden_deliver_manager_operator_id"),$("#operator_name_referchooser"));
	setReferChooser($("#hidden_to_manager_operator_id"),$("#operator_name_referchooser"));
    
    /*责任工位*/
    setReferChooser($("#hidden_search_position_id"),$("#position_name_referchooser"));
    setReferChooser($("#hidden_update_position_id"),$("#position_name_referchooser"));
    setReferChooser($("#hidden_add_position_id"),$("#position_name_referchooser"));
    setReferChooser($("#hidden_deliver_position_id"),$("#position_name_referchooser"));
    setReferChooser($("#hidden_to_position_id"),$("#position_name_referchooser"));
    
    /*责任人员*/
    setReferChooser($("#hidden_update_responsible_operator_id"),$("#responsible_operato_referchooser"));
    setReferChooser($("#hidden_add_responsible_operator_id"),$("#responsible_operato_referchooser"));

    $("#add_import_date,#add_waste_date,#add_updated_time," +
	  "#update_import_date,#update_waste_date,#update_provide_date," +
	  "#replace_import_date,#replace_waste_date").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	/*检索*/
	$("#searchbutton").click(function(){
		findit();
	});

	$("#resetbutton").click(function(){
		reset();
	});

    var section_id =$("#search_section_id").html();
    var line_id =$("#search_line_id").html();
    var manage_level =$("#search_manage_level").html();
    var status =$("#search_status").html();
    
    //状态默认是使用中和保管中
	$("#search_status option[value='1']").attr("selected","selected");
	$("#search_status option[value='4']").attr("selected","selected").trigger("change");

    $("#add_name_button").click(function(){
	      $("#text_name").show();
		  $("#add_name").hide();
	});

	$("#search_manage_level,#update_manage_rank,#add_manage_level,#deliver_section_name,#deliver_line_name,#to_section_name,#to_line_name").select2Buttons();
	 
    //修改画面select2Buttons
    $("#update_section_id").html(section_id);
    $("#update_line_id").html(line_id);
    //$("#update_manage_level").html(manage_level);
    //新建画面select2Buttons
    $("#add_section_id").html(section_id);
    $("#add_line_id").html(line_id);
    $("#add_manage_level").html(manage_level);
    //替换新品
    $("#replace_section_id").html(section_id);
    $("#replace_line_id").html(line_id);
    $("#replace_manage_level").html(manage_level);
    
    $("#search_section_id,#search_line_id,#search_status,#add_section_id," +
      "#add_manage_level,#add_line_id,#add_status,#update_section_id," +
      "#update_line_id,#update_manage_level,#update_status,#replace_section_id," +
      "#replace_line_id,#replace_manage_level,#replace_status").select2Buttons();
    
    //新建课室选择事件
   /* $("#add_section_id").change(function(){
	    var data={
          "section_id":$(this).val()
        };        
       search_operator(data);
    });*/
    
     //修改课室选择事件
    /*$("#update_section_id").change(function(){
        var data={
          "section_id":$(this).val()
        };        
       search_operator(data);
    });*/
    //初始化
    findit();
    //移除(不选)选项
    $("#s2b_update_section_id").find("li:eq(0)").remove();
    $("#replacebutton").disable();
    $("#replacebutton").click(function(){
    	replace();
    });
    
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
			"manager_operator_id":$("#hidden_to_manager_operator_id").val(),
			
			"compare_section_id":$("#deliver_section_name").data("post")==$("#to_section_name").val(),
			"compare_line_id":$("#deliver_line_name").data("post")==$("#to_line_name").val(),
			"compare_position_id":$("#hidden_deliver_position_id").data("post")==$("#hidden_to_position_id").val(),
			"compare_manager_operator_id":$("#hidden_deliver_manager_operator_id").data("post")==$("#hidden_to_manager_operator_id").val()
	};
	
	//批量交付-全选
	if($("#cb_deliver_list").attr("checked")=="checked"){
		for(var i=0;i<list.length;i++){
			data["keys.devices_manage_id[" + i + "]"] = list[i].devices_manage_id;
		}
	}else{
		for (var i in rowids) {
			var rowData = $("#deliver_list").getRowData(rowids[i]);
			data["keys.devices_manage_id[" + i + "]"] = rowData["devices_manage_id"];
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
			infoPop("交付已经完成。", null, "交付");
			deliver_findit();
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
	$("#deliver_manager_operator_id").val("");
	$("#hidden_deliver_manager_operator_id").val("");
	//左侧内容清空--end
	
	//右侧内容清空--start
	$("#to_section_name").val("").trigger("change");
	$("#to_line_name").val("").trigger("change");
	$("#to_position_id").val("");
	$("#hidden_to_position_id").val("");
	$("#to_manager_operator_id").val("");
	$("#hidden_to_manager_operator_id").val("");
	//右侧内容清空--end
	
	deliver_filed_list("");
	
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
		$("#deliver_manager_operator_id").data("post",$("#deliver_manager_operator_id").val());
		$("#hidden_deliver_manager_operator_id").data("post",$("#hidden_deliver_manager_operator_id").val());
		
		//批量交付时，将左边的检索条件数据复制到右边--右部分设值--start
		$("#to_section_name").val($("#deliver_section_name").val()).trigger("change");
		$("#to_line_name").val($("#deliver_line_name").val()).trigger("change");
		$("#to_position_id").val($("#deliver_position_id").val());
		$("#hidden_to_position_id").val($("#hidden_deliver_position_id").val());
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
            var listdata = resInfo.devicesManageForms;
            deliver_filed_list(listdata);
        }
    }catch (e) {};
}

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
            colNames:['设备工具管理ID','设备工具品名ID','管理编号','品名','型号',
                      '管理员ID','管理员','管理<br>等级','状态','点检表管理号','对应类型','日常点检表<br>管理号','定期点检表<br>管理号',
                      '出厂编号','厂商','备注','分发课室','责任工程','分发课室ID','责任工程ID','责任工位ID',
                      '责任工位','导入日期','发放日期','发放者','废弃日期','更新时间','最后更新人'],
			colModel:[
				{name:'devices_manage_id',index:'devices_manage_id',hidden:true},
				{name:'devices_type_id',index:'devices_type_id',hidden:true},
				{name:'manage_code',index:'manage_code',width:80},
				{name:'name',index:'name',width:120},
				{name:'model_name',index:'model_name',width:140},
                {name:'manager_operator_id',index:'manager_operator_id',width:100,align:'center',hidden:true},
				{name:'manager',index:'manager',width:60,align:'left',hidden:true},
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
                {name:'check_manage_code',index:'check_manage_code',width:110,align:'center',hidden:true},
                {name:'access_place',index:'access_place',width:120,align:'center',hidden:true},
				{name:'daily_sheet_manage_no',index:'daily_sheet_manage_no',width:120,align:'center',hidden:true},
				{name:'regular_sheet_manage_no',index:'regular_sheet_manage_no',width:110,align:'center',hidden:true},
				{name:'products_code',index:'products_code',width:100,align:'center',hidden:true},
				{name:'brand',index:'brand',width:100,align:'center',hidden:true},
				{name:'comment',index:'comment',width:100,align:'center',hidden:true},
				{name:'section_name',index:'section_name',width:100,align:'center',hidden:true},
				{name:'line_name',index:'responsible_line_name',width:85,align:'center',hidden:true},				
				{name:'section_id',index:'section_id',hidden:true},
				{name:'line_id',index:'line_id',hidden:true},
				{name:'position_id',index:'position_id',hidden:true},
				{name:'process_code',index:'process_code',width:85,align:'center',hidden:true},
                {name:'import_date',index:'import_date',width:85,align:'center',hidden:true},
                {name:'provide_date',index:'provide_date',width:85,align:'center',hidden:true},
                {name:'provider',index:'provider',width:85,align:'center',hidden:true,
	                formatter : function(value, options, rData) {
                        //当发放日期不为空时，发放者是当前更新人；如果为空时，发放者是空白
	                    if(rData.provide_date){
                            return rData.updated_by;
	                    }else{
	                        return "";
	                    }                           
	                }},
                {name:'waste_date',index:'waste_date',width:85,align:'center',hidden:true},
                {name:'updated_time',index:'updated_time',width:85,align:'center',hidden:true},
                {name:'updated_by',index:'updated_by',width:85,align:'center',hidden:true}
			],
            rownumbers:true,
            toppager : false,
            rowNum : 20,
            sortorder:"asc",
            sortname:"id",
            multiselect: true,
            pager : "#deliver_listpager",
            viewrecords : true,
            // ondblClickRow : showDetail,
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

/*判断替换新品enable、disable(当选择了一行之后是enable；否则是disable)*/
var enableButton= function(){
	//选择行，并获取行数
	var row = $("#list").jqGrid("getGridParam", "selrow");
	
	if(row>0){
		$("#replacebutton").enable();
		$("#replacebutton").button();
	}else{
		$("#replacebutton").disable();
	}   
}

//替换新品功能
var replace = function(){
	$("#waste_old_products_no").attr("checked","checked").trigger("change");
	
	var row = $("#list").jqGrid("getGridParam", "selrow");
	var rowData=$("#list").getRowData(row);
	
	//隐藏替换新品之前的治具的管理编号
    $("#hidden_old_manage_code").val(rowData.manage_code);
    //隐藏替换新新品之前的治具的tools_manage_id
    $("#hidden_old_devices_manage_id").val(rowData.devices_manage_id);
	
    //页面隐藏设备工具ID
    $("#hidden_devices_manage_id").val(rowData.devices_manage_id);
    //品名
	$("#replace_name").val(rowData.name); 
    //隐藏设备工具品名ID
    $("#hidden_replace_name").val(rowData.devices_type_id);
    //型号
    $("#replace_model_name").val(rowData.model_name); 
    //放置位置
	$("#replace_location").val(rowData.location);   
    //管理员
    $("#replace_manager").val(rowData.manager);  
    $("#hidden_replace_manager_operator_id").val(rowData.manager_operator_id);
    //管理等级
    $("#replace_manage_level").val(rowData.manage_level).trigger("change");
     //管理内容
    $("#replace_manage_content").val(rowData.manage_content);
    //出厂编码
	$("#replace_products_code").val(rowData.products_code);
    //厂商
    $("#replace_brand").val(rowData.brand); 
     //状态
    $("#replace_status").val(rowData.status).trigger("change"); 
    
    //课室ID
	$("#replace_section_id").val(rowData.section_id).trigger("change");
    //工程ID
    $("#replace_line_id").val(rowData.line_id).trigger("change"); 
    //工位ID
	$("#replace_position_id").val(rowData.process_code);  
    
    $("#hidden_replace_position_id").val(rowData.position_id);
    
    /**点击替换新品时，这几项内容为空**/
    //发放者
    $("#replace_provider").text("");    
    //发放日期
    $("#replace_provide_date").text(""); 
    //更新时间
	$("#replace_updated_time").text("");
    //导入日期
	$("#replace_import_date").val(""); 
    //废弃日期
	$("#replace_waste_date").val("");    
	
     //备注
    $("#replace_comment").val(rowData.comment); 
    
	var data = {
            "manage_code":rowData.manage_code
        };
    // Ajax提交
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
	$("#replace_confrim").dialog({
		width : 800,
		height : 660,
		resizable : false,
		show : "blind",
		modal : false,
		title : "替换新品",
		buttons : {
			"确认" : function() {
                 var data={
                	"compare_status":rowData.status==$("#replace_status").val(),
			        "manage_code": $("#replace_manage_code").val(),
			        "devices_type_id": $("#hidden_replace_name ").val(), 
			        "model_name":$("#replace_model_name ").val(),
			        "location":$("#replace_location").val(), 
			        "manager_operator_id": $("#hidden_replace_manager_operator_id").val(),
			        "manage_level":$("#replace_manage_level ").val(),
			        "manage_content":$("#replace_manage_content").val(), 
			        "products_code": $("#replace_products_code").val(),
			        "brand": $("#replace_brand ").val(), 
			        "section_id":$("#replace_section_id ").val(),
			        "line_id": $("#replace_line_id ").val(), 
			        "position_id": $("#hidden_replace_position_id").val(),
			        "responsible_operator_id": $("#hidden_replace_responsible_operator_id").val(),
			        "import_date": $("#replace_import_date").val(),
			        "waste_date":$("#replace_waste_date").val(),
			        "delete_flg":$("#replace_delete_flg").val(),
			        "updated_by":rowData.updated_by_id,
			        "updated_time":$("#replace_updated_time ").val(),
			        "status":$("#replace_status").val(), 
			        "comment": $("#replace_comment").val(),
			        
			        "waste_old_products":$("#waste_old_products input:checked").val(),//--同时废弃掉旧品,
                    "devices_manage_id":$("#hidden_old_devices_manage_id").val()
			    }
                 // Ajax提交
                 $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath + '?method=doReplace',
                    cache : false,
                    data :data,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete : replace_handleComplete
                 });
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
	
	setReferChooser($("#hidden_replace_tools_name"),$("#replace_name_referchooser"));
    setReferChooser($("#hidden_replace_position_id"),$("#replace_position_referchooser"));
    setReferChooser($("#hidden_replace_manager_operator_id"),$("#replace_operator_name_referchooser"));
}
var replace_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#editarea", resInfo.errors);
        } else {
        	infoPop("替换新品已经完成。", null, "替换新品");
		$("#replace_confrim").dialog("close");
       
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

var searchMaxManageCode_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	//管理编号
            $("#replace_manage_code").val(resInfo.manageCode);
        }
    }catch (e) {};
}

/*var search_operator = function(data){
    // Ajax提交
    $.ajax({
        beforeSend : ajaxRequestType,
        async : true,
        url : servicePath + '?method=searchResponsibleOperator',
        cache : false,
        data : data,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : searchOperator_handleComplete
    });
}
var searchOperator_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var responseOperator = resInfo.responseOperator;
            $("#add_choose_operator").html(responseOperator);
            $("#update_choose_operator").html(responseOperator);
            setReferChooser($("#hidden_add_responsible_operator_id"),$("#add_responsible_operato_referchooser"));
            setReferChooser($("#hidden_update_responsible_operator_id"),$("#update_responsible_operato_referchooser"));
        }
    }catch (e) {};
}*/

var findit = function(arg) {
      var data = {
            "manage_code":$("#search_manage_code").val(),
            //"name":$("#search_name").val(),
            "devices_type_id":$("#hidden_search_name").val(),
            "model_name":$("#search_model_name").val(),
            "section_id":$("#search_section_id").val(),
            "line_id":$("#search_line_id").val(),            
            "manage_level":$("#search_manage_level").val(),
            "manager_operator_id":$("#hidden_search_manager_operator_id").val(),
            "status":$("#search_status").val() && $("#search_status").val().toString(),//默认是选择使用中和保管中
            "position_id":$("#hidden_search_position_id").val()
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
        	 $("#replacebutton").disable();
        	 $("#hidden_import_date").val(resInfo.current_date);
            var listdata = resInfo.devicesManageForms;
            filed_list(listdata);
        }
    }catch (e) {};
}
//清除
var reset=function(){
	$("#search_name").data("post","").val("");
    $("#hidden_search_name").data("post","").val("");
	$("#search_brand").data("post","").val("");
	$("#search_model_name").val("");
	$("#search_manage_code").data("post","").val("");
	$("#search_section_id").data("post","").val("").trigger("change");
	$("#search_line_id").data("post","").val("").trigger("change");
	$("#search_position_id").val("");
    $("#hidden_search_position_id").data("post","").val("");
	$("#search_manager_operator_id").val("");
    $("#hidden_search_manager_operator_id").val("");
	$("#search_status").data("post","").val("").trigger("change");		
    $("#search_manage_level").data("post","").val("").trigger("change");
};

function filed_list(listdata){
	if($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#list").jqGrid({
			data:listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['设备工具管理ID','设备工具品名ID','管理编号','品名','型号','放置位置',
                      '管理员ID','管理员','管理<br>等级','状态','点检表管理号','对应类型'
                      ,'替代<br>评价','替代<br>对应'
                      ,'日常点检表<br>管理号','定期点检表<br>管理号',
                      '出厂编号','厂商','备注','分发课室','责任工程','分发课室ID','责任工程ID','责任工位ID',
                      '责任工位','导入日期','发放日期','发放者','废弃日期','更新时间','最后更新人'],
	        colModel:[
					{name:'devices_manage_id',index:'devices_manage_id',hidden:true},
					{name:'devices_type_id',index:'devices_type_id',hidden:true},
					{name:'manage_code',index:'manage_code',width:80},
					{name:'name',index:'name',width:120},
					{name:'model_name',index:'model_name',width:140},
					{name:'location',index:'location',hidden:true},
	                {name:'manager_operator_id',index:'manager_operator_id',width:100,align:'center',hidden:true},
					{name:'manager',index:'manager',width:60,align:'left'},
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
	                {name:'check_manage_code',index:'check_manage_code',width:110,align:'center',hidden:true},
	                {name:'access_place',index:'access_place',width:120,align:'center',hidden:true},

	                {name:'backup_evaluation',index:'backup_evaluation',width:35,align:'center', formatter:function(value, options, rData){
	                	var params = "\"" + rData.devices_manage_id + "\",\"" +　rData.devices_type_id + "\",\"" + rData.model_name + "\",\"" + rData.line_name + "\"";

	                	if (!value) {
	                		return "<a href='javascript:showDeviceBackup(" + params +")'>×</a>";
	                	} else if (value == -1) {
	                		return "<a href='javascript:showDeviceBackup(" + params +")'>－</a>";
	                	} else if (value > 4){
	                		return "<a href='javascript:showDeviceBackup(" + params +")'>◎</a>";
	                	} else if (value > 1){
	                		return "<a href='javascript:showDeviceBackup(" + params +")'>○</a>";
	                	} else if (value == 1){
	                		return "<a href='javascript:showDeviceBackup(" + params +")'>△</a>";
	                	}
	                }},
	                {name:'corresponding',index:'corresponding',width:35,align:'center',formatter:function(value, options, rData){
	                	var params = "\"" + rData.devices_manage_id + "\",\"" +　rData.devices_type_id + "\",\"" + rData.model_name + "\",\"" + rData.line_name + "\"";
	                	var ret = "";

	                	if (value) {
	                		ret = "<a href='javascript:showDeviceBackup(" + params +")'>有</a>";
	                	} else {
	                		ret = "<a href='javascript:showDeviceBackup(" + params +")'>无</a>";
	                	}
	                	if(rData.borrowed == "1") {
	                		ret += "<span style='color:orange'>▲</span>"
	                	}
	                	return ret;
	                }},

	                {name:'daily_sheet_manage_no',index:'daily_sheet_manage_no',width:120,align:'center'},
					{name:'regular_sheet_manage_no',index:'regular_sheet_manage_no',width:110,align:'center'},
					{name:'products_code',index:'products_code',width:100,align:'center'},
					{name:'brand',index:'brand',width:100,align:'center'},
					{name:'comment',index:'comment',width:100,align:'center',hidden:true},
					{name:'section_name',index:'section_name',width:100,align:'center',hidden:false},
					{name:'line_name',index:'responsible_line_name',width:85,align:'center',hidden:false},				
					{name:'section_id',index:'section_id',hidden:true},
					{name:'line_id',index:'line_id',hidden:true},
					{name:'position_id',index:'position_id',hidden:true},
					//{name:'responsible_operator_id',index:'responsible_operator_id',hidden:true},
	                //{name:'responsible_operator',index:'responsible_operator',hidden:true},
					{name:'process_code',index:'process_code',width:85,align:'center'},
	                {name:'import_date',index:'import_date',width:85,align:'center',hidden:true},
	                {name:'provide_date',index:'provide_date',width:85,align:'center',hidden:true},
	                {name:'provider',index:'provider',width:85,align:'center',hidden:true,
		                formatter : function(value, options, rData) {
	                        //当发放日期不为空时，发放者是当前更新人；如果为空时，发放者是空白
		                    if(rData.provide_date){
	                            return rData.updated_by;
		                    }else{
		                        return "";
		                    }                           
		                }},
	                {name:'waste_date',index:'waste_date',width:85,align:'center',hidden:true},
	                {name:'updated_time',index:'updated_time',width:85,align:'center',hidden:true},
	                {name:'updated_by',index:'updated_by',width:85,align:'center',hidden:true}
				],
			rownumbers:true,
			toppager : false,
			rowNum : 20,
			pager : "#listpager",
			viewrecords : true,
			ondblClickRow : showEdit,
			onSelectRow :enableButton,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true]
		});
		/*$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
			'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建设备工具'+'" role="button" aria-disabled="false">' +
		'</div>');
		$("#addbutton").button();*/
		$("#addbutton").click(showAdd);
	}	
};

/*新建*/
var showAdd = function(){
    //点击新建之前清空
	$("#add_manage_code").val("");
	$("#add_name").val("");
	$("#add_manage_level").val("").trigger("change");
	$("#add_products_code").val("");
	$("#add_position_id").val("");
	$("#add_import_date").val("");
	$("#add_waste_date").val("").hide();
	$("#add_comment").val("");
	$("#add_model_name").val("");
	$("#add_manager").val("");  
	$("#add_manage_content").val("");
    $("#add_status").val("").trigger("change");
    $("#add_section_id").val("").trigger("change");
    $("#add_line_id").val("").trigger("change");
	$("#add_brand").val("");
	$("#add_responsible_operator_id").val("");
	$("#add_updated_time").val("");

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
	$("#body-mdl").hide();
	$("#body-detail").hide();
	$("#body-regist").show();
	$("#body-update").hide();	

	/*返回*/
	$("#goback").click(function(){
		$("#body-mdl").show();
		$("#body-detail").hide();
		$("#body-regist").hide();
	});
	
	$("#add_form").validate({
        rules:{
            manage_code:{
                required:true,
                maxlength : 9
            },
            devices_type_id:{
                required:true
            },
            model_name:{
                maxlength :32
            },
            location:{
                maxlength :10
            },
            manage_content:{
                maxlength:64
            },
            products_code:{
                maxlength:15
            },
            brand:{
                maxlength:32
            },
            section_id:{
                required:true
            },
            manage_level:{
                required:true
            },
            section_id:{
                required:true
            },
            import_date:{
                required:true
            },
            status:{
                required:true
            }
        }
    });

    /*确认*/	
	$("#confirebutton").click(function(){
	  if ($("#add_form").valid()) {
		warningConfirm("是否新建管理编号为"+$("#add_manage_code").val()+", 品名为"+$("#add_name").val()+"的设备工具?", 
			function() {
                     var data={
				        "manage_code": $("#add_manage_code").val(),
				        "devices_type_id": $("#hidden_add_name ").val(), 
				       //"name":$("#add_name").val(), 
				        "model_name":$("#add_model_name ").val(),
				        "manager_operator_id": $("#hidden_add_manager_operator_id").val(),
				        "manage_level":$("#add_manage_level ").val(),
				        "manage_content":$("#add_manage_content").val(), 
				        "products_code": $("#add_products_code").val(),
				        "brand": $("#add_brand ").val(), 
				        "section_id":$("#add_section_id ").val(),
				        "line_id": $("#add_line_id ").val(), 
				        "position_id": $("#hidden_add_position_id").val(),
				        "responsible_operator_id": $("#hidden_add_responsible_operator_id").val(),
				        "import_date": $("#add_import_date").val(),
				        "waste_date":$("#add_waste_date").val(),
				        "delete_flg":$("#add_delete_flg").val(),
				        "updated_by":$("#add_updated_by").val(),
				        "updated_time":$("#add_updated_time ").val(),
				        "status":$("#add_status").val(), 
				        "comment": $("#add_comment").val()  
				    }
                     // Ajax提交
                     $.ajax({
                        beforeSend : ajaxRequestType,
                        async : true,
                        url : servicePath + '?method=doinsert',
                        cache : false,
                        data :data,
                        type : "post",
                        dataType : "json",
                        success : ajaxSuccessCheck,
                        error : ajaxError,
                        complete : insert_handleComplete
                     });
			}, function() {
			// $("#editbutton").enable();
			}, "新建确认"
		)};
	});	
};
var insert_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#editarea", resInfo.errors);
        } else {
        	infoPop("新建已经完成。", null, "新建");
            // 重新查询
            findit();
            // 切回一览画面
            showList();
        }
    } catch (e) {
        console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
                + e.lineNumber + " fileName: " + e.fileName);
    };
}
//双击一览详细画面
var showEdit = function(){

    //状态选择
    $("#update_status").bind("change", function() {
          //如果状态是遗失或者损坏--废弃日期可填
          if($(this).val()==2 || $(this).val()==3){
             $("#update_waste_date").show();
          }else{
             $("#update_waste_date").hide();
          }
    });
    //如果选中保管中--只能选择技术课
    /*if($("#update_status").val()=='3'){
       $("#update_section_id").disable(); 
       if($("#update_section_id").val()=='7'){
            $("#update_section_id").enable(); 
       };
    }*/

	var rowId=$("#list").jqGrid("getGridParam","selrow");//获取选中行的ID
	var rowData=$("#list").getRowData(rowId);	

	if (rowData.corresponding && rowData.corresponding.indexOf("▲") >= 0) {
         // Ajax提交
         $.ajax({
            beforeSend : ajaxRequestType,
            async : true,
            url : servicePath + '?method=detail',
            cache : false,
            data :{devices_manage_id : rowData.devices_manage_id},
            type : "post",
            dataType : "json",
            success : ajaxSuccessCheck,
            error : ajaxError,
            complete : function(xhrobj, textStatus) {
 				var resInfo = $.parseJSON(xhrobj.responseText);
 				if (resInfo.devicesManageForm) {
 					$.extend(rowData, resInfo.devicesManageForm);
 					showEditContent(rowData);
 				}
            }
         });
    } else {
		showEditContent(rowData);
	}
}

var showEditContent = function(rowData) {
    $("#body-mdl").hide();
	$("#body-detail").show();

	//页面隐藏设备工具ID
    $("#hidden_devices_manage_id").val(rowData.devices_manage_id);
    //管理编号
	$("#update_manage_code").val(rowData.manage_code);     
    //品名
	$("#update_name").val(rowData.name); 
    //隐藏设备工具品名ID
    $("#hidden_update_name").val(rowData.devices_type_id);
    //型号
    $("#update_model_name").val(rowData.model_name); 
   
    //管理员
    $("#update_manager").val(rowData.manager);  
    $("#hidden_update_manager_operator_id").val(rowData.manager_operator_id);
    //管理等级
    $("#update_manage_level").val(rowData.manage_level).trigger("change");
     //管理内容
    $("#update_manage_content").val(rowData.manage_content);
    //出厂编码
	$("#update_products_code").val(rowData.products_code);
    //厂商
    $("#update_brand").val(rowData.brand); 
     //状态
    $("#update_status").val(rowData.status).trigger("change"); 
    
    //课室ID
	$("#update_section_id").val(rowData.section_id).trigger("change");
    //工程ID
    $("#update_line_id").val(rowData.line_id).trigger("change"); 
    //工位ID
	$("#update_position_id").val(rowData.process_code);  
    
    $("#hidden_update_position_id").val(rowData.position_id);
    
    //发放者
    $("#update_provider").text(rowData.provider);
    
    //责任人
    //$("#update_responsible_operator_id").val(rowData.responsible_operator);     
    //$("#hidden_update_responsible_operator_id").val(rowData.responsible_operator_id);
    
    //导入日期
	$("#update_import_date").val(rowData.import_date);   
    //发放日期
    $("#update_provide_date").text(rowData.provide_date);  
    //废弃日期
	$("#update_waste_date").val(rowData.waste_date);    
    //更新时间
	$("#update_updated_time").text(rowData.updated_time);
     //备注
    $("#update_comment").val(rowData.comment);    

	/*返回*/
	$("#resetbutton3").click(function(){		
		$("#body-mdl").show();
		$("#body-detail").hide();
		$("#body-regist").hide();
	});
    
    $("#update_form").validate({
        rules:{
            manage_code:{
                required:true,
                maxlength : 9
            },
            manager_operator_id:{
                required:true
            },
            name:{
                required:true,
                maxlength :32
            },
            model_name:{
                maxlength :32
            },
            manage_content:{
                maxlength:64
            },
            products_code:{
                maxlength:15
            },
            brand:{
                maxlength:32
            },
            name:{
                required:true,
                maxlength :32
            },
            section_id:{
                required:true
            },
            manage_level:{
                required:true
            },
            section_id:{
                required:true
            },
            import_date:{
                required:true
            },
            status:{
                required:true
            }
        }
    });
	/*修改*/
	$("#updatebutton").click(function(){
	  if ($("#update_form").valid()) {
        $("#dialog_confrim").html("");
		warningConfirm("是否修改管理编号为"+$("#update_manage_code").val()+",品名为"+$("#update_name").val()+"的设备工具？", 
		function(){
			var data={
				"compare_status":rowData.status==$("#update_status").val(),
				"devices_manage_id": $("#hidden_devices_manage_id ").val(), 
				"manage_code": $("#update_manage_code").val(),
				"devices_type_id": $("#hidden_update_name").val(), 
				"model_name":$("#update_model_name ").val(),
				"manager_operator_id":$("#hidden_update_manager_operator_id").val(),
				"manage_level":$("#update_manage_level").val(),
				"manage_content":$("#update_manage_content").val(), 
				"products_code": $("#update_products_code").val(),
				"brand": $("#update_brand ").val(), 
				"section_id":$("#update_section_id ").val(),
				"line_id": $("#update_line_id ").val(), 
				"position_id": $("#hidden_update_position_id").val(),
				"responsible_operator_id":$("#hidden_update_responsible_operator_id").val(),
				"import_date":$("#update_import_date").val(),
				"provide_date":$("#update_provide_date ").text(),
				"waste_date":$("#update_waste_date ").val(),
				"delete_flg":$("#update_delete_flg ").val(),
				"updated_by":$("#update_updated_by ").val(),
				"status":$("#update_status").val(), 
				"comment": $("#update_comment").val()  
			}
           
            // Ajax提交
		    $.ajax({
		        beforeSend : ajaxRequestType,
		        async : true,
		        url : servicePath + '?method=doupdate',
		        cache : false,
		        data :data,
		        type : "post",
		        dataType : "json",
		        success : ajaxSuccessCheck,
		        error : ajaxError,
		        complete : update_handleComplete
		    });
		}, 
        null, 
  		"修改确认");
      }
	});	

	/*确认删除*/
	$("#delbutton").click(function(){
		warningConfirm("确认删除管理编号为"+$("#update_manage_code").val()+",品名为"+$("#update_name").val()+"的设备工具？", 
			function() {
                 var data={
                    "devices_manage_id": $("#hidden_devices_manage_id ").val()
                 }
                  // Ajax提交
                 $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath + '?method=dodelete',
                    cache : false,
                    data :data,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete : delete_handleComplete
                 });
			},null,
			"删除确认"
		);
	});

	if (rowData.manage_content) {
		infoPop("此设备工具正借用代替" + rowData.manage_content + "的工作。");
	}
};

var delete_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#editarea", resInfo.errors);
        } else {
			infoPop("删除已经完成。", null, "删除");
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

var update_handleComplete = function(xhrobj, textStatus) {
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#editarea", resInfo.errors);
        } else {
			infoPop("修改已经完成。", null, "修改");
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

var showList = function(){
        $("#body-mdl").show();
        $("#body-detail").hide();
        $("#body-regist").hide();
}


