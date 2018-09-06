/** 一览数据对象 */
var listdata = {};
var partial_base_line_value_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_base_value_adjust_history.do";

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
	
	$("#search_identification").select2Buttons();
    setReferChooser($("#hidden_updated_by"),$("#opertor_name_referchooser"));
    
    /* date 日期 */
	$("#search_start_date_start,#search_start_date_end,#search_update_time_start,#search_update_time_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
    
   // $("#search_start_date_start").val($("#start_date_start").val());
	
    /*检索*/
	$("#searchbutton").click(function(){
		$("#search_partial_code").data("post",$("#search_partial_code").val());//零件编号
		$("#search_partial_name").data("post",$("#search_partial_name").val());//零件名称
		$("#hidden_updated_by").data("post",$("#hidden_updated_by").val());//操作者
		$("#search_identification").data("post",$("#search_identification").val());//设定类型
		$("#search_start_date_start").data("post",$("#search_start_date_start").val());//起效日期开始
		$("#search_start_date_end").data("post",$("#search_start_date_end").val());//起效日期结束
		$("#search_update_time_start").data("post",$("#search_update_time_start").val());//修改日期开始
		$("#search_update_time_end").data("post",$("#search_update_time_end").val());//修改日期结束
		findit();
	});
	
	/*清除*/
	$("#resetbutton").click(function(){
		clearCondition();
	});
    clearCondition();
    $("#search_start_date_start").val($("#start_date_start").val());
    $("#search_start_date_start").data("post",$("#search_start_date_start").val());//起效日期开始
    findit();
});

//检索
var findit=function(){
	var data ={
		"partial_code":$("#search_partial_code").data("post"),
		"partial_name":$("#search_partial_name").data("post"),
		"updated_by":$("#hidden_updated_by").data("post"),
		"identification":$("#search_identification").data("post"),
		"start_date_start":$("#search_start_date_start").data("post"),
		"start_date_end":$("#search_start_date_end").data("post"),
		"update_time_start":$("#search_update_time_start").data("post"),
		"update_time_end":$("#search_update_time_end").data("post")
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

var clearCondition=function(){
	$("#search_partial_code").val("").data("post","");
	$("#search_partial_name").val("").data("post","");
	$("#search_updated_by").val("");
	$("#hidden_updated_by").val("").data("post","");
	$("#search_identification").val("").trigger("change").data("post","");
	$("#search_start_date_start").val("").data("post","");
	$("#search_start_date_end").val("").data("post","");
	$("#search_update_time_start").val("").data("post","");
	$("#search_update_time_end").val("").data("post","");
};

var search_handleComplete=function(xhrobj, textStatus){
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			partial_base_value_adjust_history_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var partial_base_value_adjust_history_list=function(listdata){
	if ($("#gbox_partial_base_value_adjust_list").length > 0) {
		$("#partial_base_value_adjust_list").jqGrid().clearGridData();
		$("#partial_base_value_adjust_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#partial_base_value_adjust_list").jqGrid({
			data:listdata,
			height: 470,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['零件编号','零件名称','设定类型','起效日期','终止日期','修改日期','基准值','订购数','BO数','操作者'],
			colModel:[
			{
				name:'partial_code',
				index:'partial_code',
				width:50,
				align:'left'
			},
			{
				name:'partial_name',
				index:'partial_name',
				width:190,
				align:'left'
			},
			{
				name:'identification',
				index:'identification',
				width:60,
				align:'left',
				formatter : 'select',
				editoptions : {
					value : $("#goIdentification").val()
				}
			},
			{
				name:'start_date',
				index:'start_date',
				width:60,
				align:'center',
				sorttype:'date'
			},
            {
                name:'end_date',
                index:'end_date',
                width:60,
                align:'center',
                sorttype:'date',
                formatter :function(cellValue,options,rowDate){
                    if(cellValue=="9999/12/31"){
                        return "";
                    }else{
                        return cellValue;
                    }
                
                }
            },
			{
				name:'updated_time',
				index:'updated_time',
				width:60,
				align:'center',
				sorttype:'date'
			},
			{
                name:'total_foreboard_count',
                index:'total_foreboard_count',
                width:50,
                align:'right',
                sorttype:'Integer',
                formatter:function(cellvalue, options, rowObject){
                    if(cellvalue==null){
                        return "";
                    }else{
                        return cellvalue;
                    }
                }
            },
			{
				name:'order_num',
				index:'order_num',
				width:50,
				align:'right',
				sorttype:'Integer'
			},
			{
				name:'bo_num',
				index:'bo_num',
				width:50,
				align:'right',
				sorttype:'Integer'
			},
            {
                name:'update_name',
                index:'update_name',
                width:50,
                align:'left'
            }],
			rowNum: 20,
			rownumbers : true,
			toppager: false,
			pager: "#partial_base_value_adjust_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left'
		})
	}
};