var servicePath = "premake_partial.do";

$(function(){
	$("input.ui-button").button();
     
    $("#searcharea span.ui-icon,#listarea span.ui-icon").bind("click",function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().slideToggle("blind");
        } else {
            $(this).parent().parent().next().slideToggle("blind");
        }
    });
    
    $("#search_model_id,#insert_model_id").select2Buttons();
    
    $("#search_standard_flg,#update_standard_flg,#insert_standard_flg").buttonset();
    
    $("#resetbutton").click(function(){
    	reset();
    });
    
    $("#searchbutton").click(function(){
    	$("#search_partial_code").data("post",$("#search_partial_code").val());
    	$("#search_model_id").data("post",$("#search_model_id").val());
    	$("#search_standard_flg").data("post",$("#search_standard_flg input[type='radio']:checked").val());
    	findit();
    });
    
    
    reset();
    findit();
});

var reset = function(){
	$("#search_partial_code").data("post","").val("");
	$("#search_model_id").data("post","").val("").trigger("change");
	$("#search_standard_flg_all").attr("checked","checked").trigger("change");
};

var findit = function(){
	var data = {
		"code":$("#search_partial_code").data("post"),
		"model_id":$("#search_model_id").data("post"),
		"standard_flg":$("#search_standard_flg").data("post")
	};
	
	
	$.ajax({
		beforeSend:ajaxRequestType,
		async:true,//异步请求
		url:servicePath+'?method=search',//提交地址
		cache:false,//是否缓存
		data:data,//提交的数据
		type:"post",
		dataType:"json",//提交的数据类型
		success:ajaxSuccessCheck,
		error:ajaxError,
		complete:doInit_ajaxSuccess//不管成功都执行
	});
};

var doInit_ajaxSuccess=function(xhrobj,textStatus){
	var resInfo=null;
	try{
		eval('resInfo='+xhrobj.responseText);//取得返回数据
		if(resInfo.errors.length>0){
			treatBackMessages(null,resInfo.errors);// 共通出错信息框
		}else{
			premake_partial_list(resInfo.finished);
		}
	}catch(e){
		alert("name:"+e.name+" message:"+e.message+" lineNumber:"+e.lineNumber+" fileName:"+e.fileName);
	}
};

var premake_partial_list = function filed_list(finished){
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
			colNames:['','','零件编码','型号名称','标配零件'],
			colModel:[  
					 {name:'partial_id',index:'partial_id',hidden:true},
			         {name:'model_id',index:'model_id',hidden:true},
			         {name:'code',index:'code',width: 150},
					 {name:'model_name',index:'model_name',width: 150},
					 {name:'standard_flg',index:'standard_flg', width:35, align:'center', formatter:'select', editoptions:{value:"0:否;1:是"}}						
			],
			rowNum : 35,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, 
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,			
			ondblClickRow : showEdit,
			viewsortcols : [true,'vertical',true]				
		});
		
		
		$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
			'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建零件预制">' +
		'</div>');

		$("#addbutton").button();
		// 追加处理
		$("#addbutton").click(showAdd);
	}
};


var showEdit = function(){
	var rowId = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(rowId);
	
	$("#label_partial_code").text(rowData.code);
	$("#label_model_name").text(rowData.model_name);
	if(rowData.standard_flg==1){
		$("#update_standard_flg_yes").attr("checked","checked").trigger("change");
	}else if(rowData.standard_flg==0){
		$("#update_standard_flg_no").attr("checked","checked").trigger("change");
	}
	
	$("#main").hide();
	$("#update").show();
	
	$("#cancelbutton,#updatebutton").unbind("click");
	
	//取消
	$("#cancelbutton").bind("click",function(){
		$("#update").hide();
		$("#main").show();
	});
	
	$("#updatebutton").unbind("click");
	$("#updatebutton").bind("click",function(){
		var data ={
			"partial_id":rowData.partial_id,
			"model_id":rowData.model_id,
			"standard_flg":$("#update_standard_flg input[type='radio']:checked").val()
		};
		
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
	});
};


var update_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#update").hide();
		$("#main").show();
	    findit();
	}
};

var showAdd = function(){
	$("#insert_partial_id,#insert_partial_code").val("");
	$("#insert_model_id").val("").trigger("change");
	$("#insert_standard_flg_all").attr("checked","checked").trigger("change");
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getAutocomplete',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			$("#main").hide();
			$("#create").show();
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				partialCode = resInfo.sPartialCode;
				$("#insert_partial_code").autocomplete({
					source : partialCode,
					minLength :3,
					delay : 100,
					focus: function( event, ui ) {
						 $(this).val( ui.item.label );
						 return false;
					},
					select: function( event, ui ) {
						$("#insert_partial_id").val(ui.item.value);
						$(this).val( ui.item.label );
						 return false;
					}
				});				
			} catch (e) {
			}
		}
	});		
	
	$("#cancelbutton2,#insertbutton").unbind("click");
	//取消
	$("#cancelbutton2").bind("click",function(){
		$("#create").hide();
		$("#main").show();
	});
	
	$("#insertbutton").bind("click",function(){
		var data ={
			"partial_id":$("#insert_partial_id").val(),
			"code":$("#insert_partial_code").val(),
			"model_id":$("#insert_model_id").val(),
			"standard_flg":$("#insert_standard_flg input[type='radio']:checked").val()
		};
		
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
			complete : insert_handleComplete
		});
	});
};

var insert_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#create").hide();
		$("#main").show();
	    findit();
	}
};
