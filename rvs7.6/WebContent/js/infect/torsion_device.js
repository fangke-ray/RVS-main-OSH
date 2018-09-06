/** 服务器处理路径 */
var servicePath = "torsion_device.do";

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
	
	/*检索*/
	$("#searchbutton").click(function(){
		$("#search_manage_code").data("post",$("#search_manage_code").val());
		$("#search_usage_point").data("post",$("#search_usage_point").val());
		$("#search_hp_scale").data("post",$("#search_hp_scale").val());
		findit();
	});

	$(".ui-buttonset").buttonset();

	/*清空*/
	$("#resetbutton").click(function(){
		reset();
	});
	
	/*管理编号*/
	setReferChooser($("#hidden_add_manage_id"),$("#add_choose_manage_code"));
	setReferChooser($("#hidden_edit_manage_id"),$("#edit_choose_manage_code"));
	
	$("#search_hp_scale,#add_hp_scale,#edit_hp_scale").select2Buttons();

	//初始化查询
	findit();
	
	//新建力矩设备-确认
	$("#addConfirebutton").click(showAdd);
	
	//修改力矩设备详细
	$("#editConfirebutton").click(showEdit);
	
	//返回
	$("#addGoback,#editGoback").click(showBack);
	
	$("#add_regular_torque,#add_deviation,#edit_regular_torque,#edit_deviation,#add_seq,#edit_seq").keydown(function(evt){
        if (!((evt.keyCode >= 48 && evt.keyCode <= 57) ||  evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39 || evt.keyCode==190)){
            return false;
        }
	});
});	

//新建--显示
var editDetail = function(){
	//移除验证样式 
	$("#body_add input[type='text']").removeClass("errorarea-single");
	//新建之前清空-start
	$("#add_manage_code").val("");
	$("#hidden_add_manage_id").val("");
	$("#add_seq").val("");
	$("#add_regular_torque").val("");
	$("#add_deviation").val("");
	$("#add_usage_point").val("");
	$("#add_hp_scale").val("").trigger("change");
	//新建之前清空-end
	
	$("#body_mdl,#body_edit").hide();
	$("#body_add").show();
}

var showBack = function(){
	$("#body_add,#body_edit").hide();
	$("#body_mdl").show();
}

//**查询start//
var findit = function(){
	/*初始化条件赋值--start*/
	$("#search_manage_code").data("post",$("#search_manage_code").val());
	$("#search_usage_point").data("post",$("#search_usage_point").val());
	$("#search_hp_scale").data("post",$("#search_hp_scale").val());
	/*初始化条件赋值--end*/
	var data={
			"manage_code":$("#search_manage_code").data("post"),
			"usage_point":$("#search_usage_point").data("post"),
			"hp_scale":$("#search_hp_scale").data("post")
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
            torsion_device_list(listdata);
        }
    }catch (e) {};
}
//**查询end//

//**新建start//
var showAdd = function(){
	$("#add_form").validate({
		rules:{	
			manage_id:{
			   required:true
		   },
		   seq:{
			   required:true,
			   number:true
		   },
		   regular_torque:{
			   required:true,
			   number:true
		   },
		   deviation:{
			   required:true,
			   number:true
		   },
		   hp_scale:{
			   required:true
		   }
		}
     });	
	
	if( $("#add_form").valid()) {
		var data={
				"manage_code":$("#add_manage_code").val(),
				"manage_id":$("#hidden_add_manage_id").val(),
				"seq":$("#add_seq").val(),
				"usage_point":$("#add_usage_point").val(),
				"regular_torque":$("#add_regular_torque").val(),
				"deviation":$("#add_deviation").val(),
				"hp_scale":$("#add_hp_scale").val()
		}
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
			complete :insert_handleComplete
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
//**新建end//

//**双击修改start//
var showEdit = function(){
	$("#edit_form").validate({
		rules:{	
		   regular_torque:{
			   required:true,
			   number:true
		   },
		   deviation:{
			   required:true,
			   number:true
		   },
		   hp_scale:{
			   required:true
		   }
		}
     });	
	
	if( $("#edit_form").valid()) {
		var data={
				"manage_code":$("#edit_manage_code").text(),
				"manage_id":$("#hidden_edit_manage_id").val(),
				"seq":$("#edit_seq").text(),
				"usage_point":$("#edit_usage_point").val(),
				"regular_torque":$("#edit_regular_torque").val(),
				"deviation":$("#edit_deviation").val(),
				"hp_scale":$("#edit_hp_scale").val()
		}
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
//**双击修改end//

//**删除start//
var showDelete = function(rid){
	var rowData = $("#torsion_device_list").getRowData(rid);
	var data = {
				"manage_id" : rowData.manage_id,
			    "seq":rowData.hidden_seq
			   };
	$("#confirmmessage").text("删除不能恢复。确认要删除[管理编号:"+encodeText(rowData.manage_code)+";力矩点检序号:"+encodeText(rowData.seq)+"]的记录吗？");
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

/*清空*/
var reset = function(){
	$("#search_manage_code").val("").data("post","");
	$("#search_usage_point").val("").data("post","");
	$("#search_hp_scale").val("").data("post","").trigger("change");
};

/*设备工具点检种类一览*/
function torsion_device_list(usage_check_list){
	if ($("#gbox_torsion_device_list").length > 0) {
        $("#torsion_device_list").jqGrid().clearGridData();
        $("#torsion_device_list").jqGrid('setGridParam',{data:usage_check_list}).trigger("reloadGrid", [{current:false}]);
    } else {
		$("#torsion_device_list").jqGrid({
			data:usage_check_list,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
			colNames:['','管理号码ID','管理号码','隐藏力矩点检序号','力矩点检序号','规格力矩值(N.M)','±','使用的工程','HP-10<br>HP-100','点检力矩合格下限(N.M)','点检力矩合格上限(N.M)'],
			colModel:[
			    {name:'myac', width:30, fixed:true, sortable:false, resize:false,formatter:'actions',formatoptions:{keys:true, editbutton:false}},
				{name:'manage_id',index:'manage_id', hidden:true},
				{name:'manage_code',index:'manage_code',align:'left',width:60},
				{name:'hidden_seq',index:'hidden_seq',hidden:true,
					formatter:function(value,options,rData){
					    return rData["seq"];
					}
				},
				{name:'seq',index:'seq',align:'center',width:40,
					formatter : function(value, options, rData) {
						return rData["seq"];
					}
				},
				{name:'regular_torque',index:'regular_torque',formatter:'number',align:'right',width : 60,formatoptions:{decimalPlaces:'none'}},
				{name:'deviation',index:'deviation',formatter:'number',align:'right',width : 40,formatoptions:{decimalPlaces:'none'}},
				{name:'usage_point',index:'usage_point',align:'left',width:80}, 
				{name:'hp_scale',index:'hp_scale',align:'center',width:40, formatter:'select', editoptions:{value:$("#hidden_hp_scale").val()}},
				{name:'regular_torque_lower_limit',index:'regular_torque_lower_limit',align:'right',width : 80},
				{name:'regular_torque_upper_limit',index:'regular_torque_upper_limit',align:'right',width : 80}
      		],
			rowNum: 20,
			//rownumbers:true,
            toppager : false,
            pager : "#torsion_device_listpager",
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
				'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建力矩设备" role="button" aria-disabled="false">' +
		'</div>');
		$("#addbutton").button();
		
		//新建力矩设备
		$("#addbutton").click(editDetail);
	}
};

/* 双击修改画面*/
var showDetail =function(){
	//移除验证样式 
	$("#body_edit input[type='text']").removeClass("errorarea-single");
	
	$("#body_mdl,#body_add").hide();
	$("#body_edit").show();
	
	var rowID=$("#torsion_device_list").jqGrid("getGridParam","selrow");
	var rowData=$("#torsion_device_list").getRowData(rowID);
	
	$("#edit_manage_code").text(rowData.manage_code);
	$("#hidden_edit_manage_id").val(rowData.manage_id);
	$("#edit_seq").text(rowData.seq);
	$("#edit_usage_point").val(rowData.usage_point);
	$("#edit_regular_torque").val(rowData.regular_torque);
	$("#edit_deviation").val(rowData.deviation);
    $("#edit_hp_scale").val(rowData.hp_scale).trigger("change");
    $("#edit_regular_torque_upper_limit").text(rowData.regular_torque_upper_limit);
    $("#edit_regular_torque_lower_limit").text(rowData.regular_torque_lower_limit);
};