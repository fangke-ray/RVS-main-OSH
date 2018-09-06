
var servicePath="tools_distribute.do";

$(function(){
	$("#body-mdl span.ui-icon,#listarea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().slideToggle("blind");
		} else {
			$(this).parent().parent().next().slideToggle("blind");
		}
	});
	$("input.ui-button").button();
	$("#search_section_name,#search_line_name").select2Buttons();
	
	/*治具品名*/
	setReferChooser($("#hidden_search_tools_name"),$("#search_name_referchooser"));
	/*责任工位*/
	setReferChooser($("#hidden_search_responsible_position_name"),$("#search_position_referchooser"));
	/*责任人员*/
	setReferChooser($("#hidden_search_responsible_operator_name"),$("#search_responsible_operator_referchooser"));
	
	/*检索*/
	$("#searchbutton").click(function(){
		/*$("#search_manage_code").data("post",$("#search_manage_code").val());
		$("#search_tools_no").data("post",$("#search_tools_no").val());
		$("#search_tools_name").data("post",$("#search_tools_name").val());
		$("#hidden_search_tools_name").data("post",$("#hidden_search_tools_name").val());
		$("#search_section_name").data("post",$("#search_section_name").val());
		$("#search_line_name").data("post",$("#search_line_name").val());
		$("#search_responsible_position_name").data("post",$("#search_responsible_position_name").val());
		$("#hidden_search_responsible_position_name").data("post",$("#hidden_search_responsible_position_name").val());
		$("#search_responsible_operator_name").data("post",$("#search_responsible_operator_name").val());
		$("#hidden_search_responsible_operator_name").data("post",$("#hidden_search_responsible_operator_name").val());*/
		
		findit();
	});
	/*清除*/
	$("#resetbutton").click(function(){
		reset();
	});
	findit();
});

/**检索详细*/
var findit = function(arg) {
      var data = {
          "manage_code":$("#search_manage_code").val(),
          "tools_no":$("#search_tools_no").val(),
          "tools_type_id":$("#hidden_search_tools_name").val(),
          "section_id":$("#search_section_name").val(),
          "line_id":$("#search_line_name").val(),           
          "position_id":$("#hidden_search_responsible_position_name").val(),
          "responsible_operator_id":$("#hidden_search_responsible_operator_name").val()
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
          var listdata = resInfo.toolsDistributes;

          filed_list(listdata);
      }
  }catch (e) {};
}


/**清除*/
var reset = function(){
	$("#search_manage_code").data("post","").val("");
	$("#search_tools_no").data("post","").val("");
	$("#search_tools_name").data("post","").val("");
	$("#hidden_search_tools_name").data("post","").val("");
	$("#search_section_name").data("post","").val("").trigger("change");
	$("#search_line_name").data("post","").val("").trigger("change");
	$("#search_responsible_position_name").data("post","").val("");
	$("#hidden_search_responsible_position_name").data("post","").val("");
	$("#search_responsible_operator_name").data("post","").val("");
	$("#hidden_search_responsible_operator_name").data("post","").val("");
};

/**jqGrid*/
var filed_list=function(listdata){
	if($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#list").jqGrid({
			data:listdata,
			height: 390,
			width: 990,
			rowheight: 23,
			datatype: "local",
			colNames:['','管理编号','治具No.','治具品名','型号','分发课室','责任工程','责任工位','责任人员','发放日期','发放者','状态','备注'],
			colModel:[
				{name:'tools_check_manage_id',index:'tools_check_manage_id',hidden:true},
				{name:'manage_code',index:'manage_code',width:100},
				{name:'tools_no',index:'tools_no',width:120},
				{name:'tools_name',index:'tools_name',width:200},
				{name:'model_name',index:'model_name',width:120,align:'center',hidden:true},
				{name:'section_name',index:'section_name',width:80,align:'center'},
				{name:'line_name',index:'line_name',width:80,align:'center'},
				{name:'process_code',index:'process_code',width:60,align:'center'},
				{name:'operator',index:'operator',width:60,align:'left'},				
				{name:'provide_date',index:'provide_date',width:100,align:'center'},
				{name:'provider',index:'provider',width:60,align:'left',formatter : function(value, options, rData) {
                    //当发放日期不为空时，发放者是当前更新人；如果为空时，发放者是空白
                    if(rData.provide_date){
                        return rData.updated_by;
                    }else{
                        return "";
                    }                           
                }},
				{name:'status',index:'status',width:50,align:'center',
                    formatter : 'select',
                    editoptions : {
                        value : $("#hidden_goStatus").val()
                    }
				},
				{name:'comment',index:'comment',hidden:true,width:150}
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
			ondblClickRow:showDetail,
			viewsortcols : [true, 'vertical', true]
		});
	}
};

/**双击详细*/
var showDetail=function(){
	var rowId=$("#list").jqGrid("getGridParam","selrow");
	var rowData=$("#list").getRowData(rowId);

	$("#label_manage_code").text(rowData.manage_code);
	$("#label_tools_no").text(rowData.tools_no);
	$("#label_tools_name").text(rowData.tools_name);
	$("#label_section_name").text(rowData.section_name);
	$("#label_line_name").text(rowData.line_name);
	$("#label_provide_date").text(rowData.provide_date);
	$("#label_provider").text(rowData.provider);	
	$("#label_position_name").text(rowData.process_code);
	$("#label_operator_name").text(rowData.operator);
	$("#label_location").text(rowData.location);	
	$("#label_comment").text(rowData.comment);
	
	var status="";
	if(rowData.status==1){
		status="使用中";
	}else if(rowData.status==2){
		status="损坏";
	}else if(rowData.status==3){
		status="遗失";
	}else if(rowData.status==4){
		status="保管中";
	}
	$("#label_status").text(status);
	
	$("#detail").dialog({
		position : 'center',
		title : "治具分布详细信息",
		width : 450,
		height : 640,
		resizable : false,
		modal : true,
		buttons : {
			"取消":function(){
				$("#detail").dialog('close');
			}
		}
	});
};
