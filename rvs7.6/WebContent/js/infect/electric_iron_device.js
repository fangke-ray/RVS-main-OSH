var servicePath = "electric_iron_device.do";
$(function(){
	$("input.ui-button").button(); 

	$("#body_mdl span.ui-icon,#body_edit span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });	
	
	$("#search_section_id,#search_kind,#add_section_id,#add_kind,#edit_section_id,#edit_kind").select2Buttons();
	
	/*管理编号*/
	setReferChooser($("#hidden_search_devices_manage_id"),$("#search_manage_code_referchooser"));
	setReferChooser($("#hidden_add_devices_manage_id"),$("#add_manage_code_referchooser"));
	setReferChooser($("#hidden_edit_devices_manage_id"),$("#edit_manage_code_referchooser"));
	/*设备品名*/
	setReferChooser($("#hidden_search_devices_type_id"),$("#search_device_name_referchooser"));
	setReferChooser($("#hidden_add_devices_type_id"),$("#add_device_name_referchooser"));
	setReferChooser($("#hidden_edit_devices_type_id"),$("#edit_device_name_referchooser"));
	/*所在工位*/
	setReferChooser($("#hidden_search_position_id"),$("#search_position_code_referchooser"));
	setReferChooser($("#hidden_add_position_id"),$("#add_position_code_referchooser"));
	setReferChooser($("#hidden_edit_position_id"),$("#edit_position_code_referchooser"));
		
	/*温度点检序号、温度下限、温度上限只可输入数字和小数点*/
	$("#add_temperature_lower_limit,#add_temperature_upper_limit,#add_seq,#edit_temperature_lower_limit,#edit_temperature_upper_limit,#edit_seq").keydown(function(evt){
        if (!((evt.keyCode >= 48 && evt.keyCode <= 57) ||  evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39 || evt.keyCode==190)){
            return false;
        }
	});
	
	//检索button
	$("#searchbutton").click(function(){
		$("#search_manage_code").data("post",$("#search_manage_code").val());
		$("#hidden_search_devices_manage_id").data("post",$("#hidden_search_devices_manage_id").val());
		$("#search_device_name").data("post",$("#search_device_name").val());
		$("#hidden_search_devices_type_id").data("post",$("#hidden_search_devices_type_id").val());
		$("#search_kind").data("post",$("#search_kind").val());
		$("#search_section_id").data("post",$("#search_section_id").val());
		$("#search_position_id").data("post",$("#search_position_id").val());
		$("#hidden_search_position_id").data("post",$("#hidden_search_position_id").val());
		
		findit();
	});
	
	//初始化
	findit();
	
	//清除button
	$("#resetbutton").click(function(){
		$("#search_manage_code").data("post","").val("");
		$("#hidden_search_devices_manage_id").data("post","").val("");
		$("#search_device_name").data("post","").val("");
		$("#hidden_search_devices_type_id").data("post","").val("");
		$("#search_kind").data("post","").val("").trigger("change");
		$("#search_section_id").data("post","").val("").trigger("change");
		$("#search_position_id").data("post","").val("");
		$("#hidden_search_position_id").data("post","").val("");
	});
	
	//新建确认
	$("#addConfirebutton").click(function(){
		add();
	});
	
	//修改返回
	$("#editGoback").click(function(){
		showBack();
	});
	
	//修改确认
	$("#editConfirebutton").click(function(){
		edit();
	});
	
})

var findit = function(){
	/*初始化检索条件赋值--start*/
	$("#search_manage_code").data("post",$("#search_manage_code").val());
	$("#hidden_search_devices_manage_id").data("post",$("#hidden_search_devices_manage_id").val());
	$("#search_device_name").data("post",$("#search_device_name").val());
	$("#hidden_search_devices_type_id").data("post",$("#hidden_search_devices_type_id").val());
	$("#search_kind").data("post",$("#search_kind").val());
	$("#search_section_id").data("post",$("#search_section_id").val());
	$("#search_position_id").data("post",$("#search_position_id").val());
	$("#hidden_search_position_id").data("post",$("#hidden_search_position_id").val());
	/*初始化检索条件赋值--end*/
	
	var data={
		"manage_id":$("#hidden_search_devices_manage_id").data("post"),
		"devices_type_id":$("#hidden_search_devices_type_id").data("post"),
		"kind":$("#search_kind").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"position_id":$("#hidden_search_position_id").data("post")
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
		complete :search_handleComplete
	});
	
}

var search_handleComplete = function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searcharea", resInfo.errors);
        } else {
            var listdata = resInfo.resultList;
            electric_iron_device_list(listdata);
        }
    }catch (e) {};
}

var add = function(){
	$("#add_form").validate({
		rules:{	
			manage_id:{
			   required:true
		   },
		   seq:{
			   required:true,
			   digits:true
		   },
		   kind:{
			   required:true
		   },
		   temperature_lower_limit:{
			   required:true,
			   digits:true,
			   maxlength:3
		   },
		   temperature_upper_limit:{
			   required:true,
			   digits:true,
			   maxlength:3
		   }
		}
     });
	
	if( $("#add_form").valid()) {
		var data={
				"manage_id":$("#hidden_add_devices_manage_id").val(),
				"manage_code":$("#add_manage_code").val(),
				"seq":$("#add_seq").val(),
				"temperature_lower_limit":$("#add_temperature_lower_limit").val(),
				"temperature_upper_limit":$("#add_temperature_upper_limit").val(),
				"kind":$("#add_kind").val()
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doInsert',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete:insert_handleComplete
		});
	}
}

var insert_handleComplete = function(xhrobj, textStatus){
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
                       showBack();                       
                   }
               }
           });          
        
        
        	
        }
    }catch (e) {};
}
/*电烙铁工具数据一览*/
function electric_iron_device_list(listdata){
	if ($("#gbox_electric_iron_device_list").length > 0) {
        $("#electric_iron_device_list").jqGrid().clearGridData();
        $("#electric_iron_device_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    } else {
		$("#electric_iron_device_list").jqGrid({
			data:listdata,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
			colNames:['','管理号码ID','管理编号','品名ID','品名','温度点检序号','温度点检序号','温度下限','温度上限','种类','所在课室','所在课室','所在工位','所在工位'],
			colModel:[
			    {name:'myac', width:30, fixed:true, sortable:false, resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
				{name:'devices_manage_id',index:'devices_manage_id', hidden:true},
				{name:'manage_code',index:'manage_code',align:'left',width:60},
				{name:'devices_type_id',index:'devices_type_id',hidden:true},
				{name:'device_name',index:'device_name',align:'left',width:60},
				{name:'hidden_seq',index:'hidden_seq',hidden:true,
					formatter:function(value,options,rData){
					    return rData["seq"];
					}
				},
				{name:'seq',index:'seq',align:'center',width:40},
				{name:'temperature_lower_limit',index:'temperature_lower_limit',align:'right',width:70}, 
				{name:'temperature_upper_limit',index:'temperature_upper_limit',align:'right',width:70},
				{name:'kind',index:'kind',align:'center',width : 60,formatter:'select',
				       editoptions:{
				    	   value:$("#hidden_kGridOptions").val()
				       }	
				},
				{name:'section_id',index:'section_id',hidden:true},
				{name:'section_name',index:'section_name',hidden:false,width:80},
				{name:'position_id',index:'section_id',hidden:true},
				{name:'position_name',index:'position_name',hidden:false,width:80}
      		],
			rowNum: 20,
            toppager : false,
            pager : "#electric_iron_device_listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,  
            ondblClickRow : showDetail,
            viewsortcols : [true,'vertical',true]
		});
		$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建电烙铁工具" role="button" aria-disabled="false">' +
		'</div>');
		$("#addbutton").button();
		
		//新建电烙铁工具数据
		$("#addbutton").click(function(){
			/*新建清空--start*/
			$("#add_manage_code").val(""),
			$("#hidden_add_devices_manage_id").val(""),
			$("#add_seq").val(""),
			$("#add_temperature_lower_limit").val(""),
			$("#add_temperature_upper_limit").val(""),
			$("#add_kind").val("").trigger("change");
			/*新建清空--end*/
			
			$("#body_add").show();
			$("#body_mdl").hide();
		});
		
		//返回
		$("#addGoback").click(function(){
			$("#body_add input[type='text']").removeClass("errorarea-single");
			showBack();
		});
	}
};
var showDetail = function(){
	$("#body_edit input[type='text']").removeClass("errorarea-single");

	/*双击修改清空--start*/
	$("#edit_manage_code").val("");
	$("#hidden_edit_devices_manage_id").val("");
	$("#label_device_name").text("");
	$("#edit_seq").val("");
	$("#hidden_edit_devices_type_id").val("");
	$("#edit_temperature_lower_limit").val("");
	$("#edit_temperature_upper_limit").val("");
	$("#edit_kind").val("").trigger("change");
	$("#label_section_id").text("");
	$("#label_position_id").text("");
	/*双击修改清空--end*/
	
	$("#body_mdl,#body_add").hide();
	$("#body_edit").show();
	
	var rowID=$("#electric_iron_device_list").jqGrid("getGridParam","selrow");
	var rowData=$("#electric_iron_device_list").getRowData(rowID);
	
	/*双击修改赋值--start*/
	$("#label_manage_code").text(rowData.manage_code);
	$("#hidden_label_manage_id").val(rowData.devices_manage_id);
	$("#label_device_name").text(rowData.device_name);
	$("#label_seq").text(rowData.seq);
	$("#hidden_label_seq").val(rowData.hidden_seq);
	$("#hidden_edit_devices_type_id").val(rowData.devices_type_id);
	$("#edit_temperature_lower_limit").val(rowData.temperature_lower_limit);
	$("#edit_temperature_upper_limit").val(rowData.temperature_upper_limit);
	$("#edit_kind").val(rowData.kind).trigger("change");
	$("#label_section_id").text(rowData.section_name);
	$("#label_position_id").text(rowData.position_name);
	/*双击修改赋值--end*/
}

var showBack = function(){
	$("#body_mdl").show();
	$("#body_add,#body_edit").hide();
}

var edit = function(){
	
	$("#edit_form").validate({
		rules:{	
		   temperature_lower_limit:{
			   required:true,
			   digits:true,
			   maxlength:3
		   },
		   temperature_upper_limit:{
			   required:true,
			   digits:true,
			   maxlength:3
		   }
		}
     });
	
	if( $("#edit_form").valid()) {
		var data={
				"manage_id":$("#hidden_label_manage_id").val(),
				"seq":$("#hidden_label_seq").val(),
				"temperature_lower_limit":$("#edit_temperature_lower_limit").val(),
				"temperature_upper_limit":$("#edit_temperature_upper_limit").val(),
				"kind":$("#edit_kind").val()
		};
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
			complete :update_handleComplete
		});
	}
}

var update_handleComplete = function(xhrobj, textStatus){
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
                       showBack();                       
                   }
               }
           });          
        
        }
    }catch (e) {};
}

//**删除start//
var showDelete = function(rid){
	var rowData = $("#electric_iron_device_list").getRowData(rid);
	var data = {
				"manage_id" : rowData.devices_manage_id,
			    "seq":rowData.hidden_seq
			   };
	$("#confirmmessage").text("删除不能恢复。确认要删除[管理编号:"+encodeText(rowData.manage_code)+";温度点检序号:"+encodeText(rowData.seq)+"]的记录吗？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
				$(this).dialog("close");
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : servicePath + '?method=doDelete',
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
}

var delete_handleComplete = function(xhrobj, textStatus){
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
                       showBack();                       
                   }
               }
           });          
        }
    }catch (e) {};
}
//**删除end//