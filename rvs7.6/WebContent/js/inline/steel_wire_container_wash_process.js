var servicePath="steel_wire_container_wash_process.do";

$(function(){
 	$("input.ui-button").button();
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("span.ui-icon, #detailsearcharea span.ui-icon").bind("click", function() {
        $(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
        if ($(this).hasClass('ui-icon-circle-triangle-n')) {
            $(this).parent().parent().next().show("blind");
        } else {
            $(this).parent().parent().next().hide("blind");
        }
    });
    
    $("#search_process_time_start, #search_process_time_end").datepicker({
		showButtonPanel : true,
		currentText : "今天",
		maxDate : 0
	});
	
	setReferChooser($("#hidden_operator_id"),$("#search_operator_name_refer"));
	
	//检索button
    $("#searchbutton").click(function(){
		$("#search_partial_code").data("post",$("#search_partial_code").val());
		$("#search_lot_no").data("post",$("#search_lot_no").val());
		$("#search_process_time_start").data("post",$("#search_process_time_start").val());
		$("#search_process_time_end").data("post",$("#search_process_time_end").val());
		$("#hidden_operator_id").data("post",$("#hidden_operator_id").val());
		findit();
    });
	
	 //清除button
    $("#resetbutton").click(function(){
    	reset();
    });
    
    $("#search_process_time_start").data("post",$("#search_process_time_start").val());
    findit("first");
});

var reset = function(){
	$("#searchform table input[type='text'],#searchform table input[type='hidden']").data("post","").val("").removeClass("errorarea-single");
};

/*检索按钮事件*/
var findit = function(first) {
	 var data={
        "code": $("#search_partial_code").data("post"),
        "lot_no": $("#search_lot_no").data("post"),
        "process_time_start":  $("#search_process_time_start").data("post"),
        "process_time_end": $("#search_process_time_end").data("post"),
        "operator_id": $("#hidden_operator_id").data("post")
    }
    
    if(first!=null && first!=""){
    	data.first = "first";
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

var checkedLabel = null;

var search_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#searchform", resInfo.errors);
        } else {
            list(resInfo.finished);
            
            if(resInfo.sPartialCode){
            	$("#add_partial_code").autocomplete({
					source : resInfo.sPartialCode,
					minLength :3,
					delay : 100,
					focus: function( event, ui ) {
						 $(this).val( ui.item.label );
						 return false;
					},
					select: function( event, ui ) {
						$("#hidden_add_partial_id").val(ui.item.value);
						$(this).val( ui.item.label );
						checkedLabel = ui.item.label;
						return false;
					},
					change: function( event, ui ) {
						if (checkedLabel 
							&& this.value != checkedLabel)
							$("#hidden_add_partial_id").val("");
					}
				});
            }
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

var list=function(listdata){
	 if ($("#gbox_list").length > 0) {
        $("#list").jqGrid().clearGridData();
        $("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
    }else{
    	$("#list").jqGrid({
            data:listdata,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
            colNames:['partial_id','','品名','入库批号','数量','清洗日期','责任人'],
            colModel:[
            	  {name:'partial_id',index:'partial_id',hidden:true},
            	  {name:'hid_process_time',index:'hid_process_time',hidden:true,
					formatter : function(value, options, rData) {
						return rData["process_time"];
					}
				   },
	              {name:'code',index:'code',width:100},
                  {name:'lot_no',index:'lot_no',width:50},
                  {name:'quantity',index:'quantity',width:50,align:'right'},
                  {name:'process_time',index:'process_time',width:90,align:'center',sorttype:'date',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'Y-m-d'
                    }
                   },
                  {name:'operator_name',index:'operator_name',width:100}
            ],
            rowNum: 20,
            toppager : false,
            pager : "#listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
			rownumbers : true,
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,
            onSelectRow:null,
            ondblClickRow : function(){
            	if($("#hidPrivacyButton").val().trim()==="true"){
            		showEdit();
            	}
            },
            viewsortcols : [true,'vertical',true],
            gridComplete:function(){}
        }); 
        
        $(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
				'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="记录">' +
			'</div>');
		$("#addbutton").button();
		// 追加处理
		$("#addbutton").click(showAdd);
    };
};

var showAdd = function(){
	$("#body-mdl").hide();
	$("#add").show();
	
	$("#addform table input[type='text'],#addform table input[type='hidden']").val("").removeClass("errorarea-single");
	
	$("#canelbutton").unbind("click").bind("click",function(){
		$("#body-mdl").show();
		$("#add").hide();
	});
	
	$("#insertbutton").unbind("click").bind("click",function(){
		if($("#hidden_add_partial_id").val() == ""){
			var label = $("#add_partial_code").val();
			$("#add_partial_code").autocomplete("search", label);
			$("ul.ui-autocomplete a").filter(function(){
				return $(this).text() === label;
			}).trigger("click");

			if($("#hidden_add_partial_id").val() == ""){
				blink($("#add_partial_code"), "errorarea-single", 2);
				return;
			}
		}
		
		var data={
			"partial_id": $("#hidden_add_partial_id").val(),
			"lot_no": $("#add_lot_no").val(),
			"quantity": $("#add_quantity").val()
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
	        complete : insert_handleComplete
	    });
	});
};

var insert_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#addform", resInfo.errors);
        } else {
            $("#body-mdl").show();
			$("#add").hide();
        	
        	findit();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

var showEdit = function() {
	$("#body-mdl").hide();
	$("#update").show();
	
	var rowID = $("#list").jqGrid("getGridParam","selrow");
	var rowData = $("#list").getRowData(rowID);
	
	$("#update_partial_code").text(rowData.code);
	$("#update_lot_no").val(rowData.lot_no);
	$("#update_quantity").val(rowData.quantity).removeClass("errorarea-single");
	$("#update_process_time").text(rowData.hid_process_time);
	$("#update_operator_name").text(rowData.operator_name);
	
	$("#canel2button").unbind("click").bind("click",function(){
		$("#body-mdl").show();
		$("#update").hide();
	});
	
	$("#updatebutton").unbind("click").bind("click",function(){
		var data={
			"partial_id": rowData.partial_id,
			"process_time": rowData.hid_process_time,
			"quantity": $("#update_quantity").val(),
			"lot_no":$("#update_lot_no").val()
		}
		
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
	        complete : update_handleComplete
	    });
	});
}

var update_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("#updateform", resInfo.errors);
        } else {
            $("#body-mdl").show();
			$("#update").hide();
        	
        	findit();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};