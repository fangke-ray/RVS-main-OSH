var servicePath="drying_process.do";

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

    $("#search_section_id").select2Buttons();
    
	setReferChooser($("#hidden_model_id"),$("#search_model_id_referchooser"));
	setReferChooser($("#hidden_position_id"),$("#search_position_id_referchooser"));
	setReferChooser($("#hidden_device_manage_id"),$("#search_device_manage_id_referchooser"));
	
	$(".ui-buttonset").buttonset();
	
    //检索button
    $("#searchbutton").click(function(){       
       findit();   
    });

    //清除button
    $("#resetbutton").click(function(){
    	reset();
    });    
    
	$("#goingon").attr("checked","checked").trigger("change");
    
    findit();
    
    
});

var reset = function(){
	$("#searchform table input[type='text'],#searchform table input[type='hidden']").val("");
	$("#search_section_id").val("").trigger("change");
	$("#all").attr("checked","checked").trigger("change");
};

/*检索按钮事件*/
var findit = function() {
	var data={
	 	"model_id":$("#hidden_model_id").val(),
	 	"omr_notifi_no":$("#search_omr_notifi_no").val(),
	 	"position_id":$("#hidden_position_id").val(),
	 	"section_id":$("#search_section_id").val(),
	 	"device_manage_id":$("#hidden_device_manage_id").val(),
	 	"status":$("input[name='status']:checked").val(),
	 	"start_time":$("#search_start_time").val(),
	 	"end_time":$("#search_end_time").val()
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
        	filed_list(resInfo.finished);
        	$("#drying_finish").disable();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

/*jqgrid表格*/
function filed_list(finished){
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
            colNames:['课室','工位','修理单号','维修对象型号','机身号','作业内容','设备管理编号','库位号','开始时间','进行/结束时间','end_time','drying_time','position_id','material_id'],
            colModel:[
	              {name:'section_name',index:'section_name',width:40},
	              {name:'process_code',index:'process_code',width:30,align:'center'},
                  {name:'omr_notifi_no',index:'omr_notifi_no',width:50},
                  {name:'model_name',index:'model_name',width:110},
                  {name:'serial_no',index:'serial_no',width:50},
                  {name:'content',index:'content',width:100},
                  {name:'manage_code',index:'manage_code',width:55},
                  {name:'slot',index:'slot',width:35,align:'right'},
                  {name:'start_time',index:'start_time',width:70,align:'center',
          			sorttype : 'date',
					formatter : 'date',
					formatoptions : {
						srcformat : 'Y/m/d H:i:s',
						newformat : 'm-d H:i'
					}},
                  {name:'time',index:'time',width:70,align:'center',
	                   formatter:function(value,b,row) {
	                   		var end_time = row.end_time;
	                   		var drying_time = row.drying_time;
							if (end_time == null || end_time == "") {
								return "<div class='finish_time_left' key='" + row.material_id + "_" + row.position_id + 
									"' start_time='" + row.start_time + "' drying_time='" + drying_time + "'></div>";
							} else {
								return end_time.substring(0,end_time.length - 3).replace(/\//g,'-');
							}
						}
				   },
				   {name:'end_time',index:'end_time',hidden:true},
				   {name:'drying_time',index:'drying_time',hidden:true},
				   {name:'position_id',index:'position_id',hidden:true},
				   {name:'material_id',index:'material_id',hidden:true}
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
            onSelectRow:enableButton,
            viewsortcols : [true,'vertical',true],
            gridComplete:function(){
            	$("#list div[class='finish_time_left']").each(function(index,ele){
            		var $target_obj = $(ele);
            		date_time_list.add({id: $target_obj.attr("key"),target:$target_obj, date_time:$target_obj.attr("start_time"),naturaltime:true,
            			drying_time:$target_obj.attr("drying_time")});
            	});
            	refreshTargetTimeLeft();
            }
        });   
	}
};

var enableButton=function(){
	var rowId=$("#list").jqGrid("getGridParam","selrow");
    var rowData=$("#list").getRowData(rowId);
	var end_time = rowData.end_time;
	
	if(end_time == null || end_time == ""){
		$("#drying_finish").enable();
	}else{
		$("#drying_finish").disable();
	}
	
	var data={
		"material_id":rowData.material_id,
	 	"position_id":rowData.position_id
	}
	
	$("#drying_finish").unbind("click");
	$("#drying_finish").bind("click",function(){
		 var drying_time = rowData.drying_time;
		 
		 var arraytimes = $("#"+rowId).find("div.finish_time_left").text().trim().split(":");
		 
		 var totalTimes = parseInt(arraytimes[0]) * 60 + parseInt(arraytimes[1]);
		 var msg;
		 if(drying_time == null || drying_time == "" || drying_time == 0){
		 	msg = "当前已经烘干"+ totalTimes + "分钟，确定是否要完成烘干？";
		 }else{
		 	msg = "烘干作业设定干燥时间为" + drying_time + "分钟，当前已经烘干" + totalTimes + "分钟，确定是否要完成烘干？";
		 }
		 
		 warningConfirm(msg,function(){
			 $.ajax({
		        beforeSend : ajaxRequestType,
		        async : true,
		        url : servicePath + '?method=doFinish',
		        cache : false,
		        data : data,
		        type : "post",
		        dataType : "json",
		        success : ajaxSuccessCheck,
		        error : ajaxError,
		        complete : dofinish_handleComplete
	    	});
		 });
	});
};

var dofinish_handleComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	 findit();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};