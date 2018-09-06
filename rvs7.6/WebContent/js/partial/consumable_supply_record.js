var servicePath = "consumable_supply_record.do";
$(function() {
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
    
    $("#search_supply_time_start, #search_supply_time_end").datepicker({
		showButtonPanel : true,
		currentText : "今天"
	});
	
	$("#search_apply_method,#search_type").select2Buttons();
	
	//清除button
    $("#resetbutton").click(function(){
    	reset();
    });
    
    //检索button
    $("#searchbutton").click(function(){
    	findit();
    });
    
    $("#dowloadbutton").disable().bind("click",function(){
    	download();
    });
    
    $("#dowloadtoptenbutton").bind("click",function(){
    	downloadTopTen();
    });
    
    findit();
});

var reset = function(){
	$("#search_supply_time_start, #search_supply_time_end").val("").removeClass("errorarea-single");
	$("#search_apply_method,#search_type").val("").trigger("change");
};

var findit = function(){
	var data = {
		"supply_time_start":$("#search_supply_time_start").val(),
		"supply_time_end":$("#search_supply_time_end").val(),
		"apply_method":$("#search_apply_method").val(),
		"types":$("#search_type").val() && $("#search_type").val().toString()
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
          
        	$("#search_supply_time_start, #search_supply_time_end").removeClass("errorarea-single");
        	$("#dowloadbutton").enable();
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

var list = function(listdata){
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
            colNames:['申请单编号','申请日期','发放方式','发放日期','零件编码','消耗品分类','价格','发放数量','总价'],
            colModel:[
            	  {name:'application_no',index:'application_no',width:40},
            	  {name:'apply_time',index:'apply_time',width:40,align:'center',sorttype:'date',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'Y-m-d'
                    }
                   },
                   {name:'apply_method',index:'apply_method',width:30,formatter:'select',
                    editoptions:{
                   		value:$("#hid_apply_method").val()
                    }
				   },
				   {name:'supply_time',index:'supply_time',width:40,align:'center',sorttype:'date',formatter:'date',
                    formatoptions:{
                        srcformat:'Y/m/d H:i:s',
                        newformat:'Y-m-d'
                    }
                   },
                   {name:'partial_code',index:'partial_code',width:50},
                   {name:'type',index:'type',width:50,formatter:'select',
                    editoptions:{
                   		value:$("#hid_consumable_type").val()
                    }
				   },
				   {name:'price',index:'price',width:30,align:'right'},
				   {name:'supply_quantity',index:'supply_quantity',width:30,align:'right'},
				   {name:'total_price',index:'total_price',width:30,align:'right'}
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
            ondblClickRow : null,
            viewsortcols : [true,'vertical',true],
            gridComplete:function(){}
        }); 
    }
};

var download = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=report',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
			if (resInfo && resInfo.fileName) {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			} else {
				alert("文件导出失败！");
			}
		}
	});
}

var downloadTopTen = function(doContinue){
	var data = {
		"supply_time_start":$("#search_supply_time_start").val(),
		"supply_time_end":$("#search_supply_time_end").val(),
		"apply_method":$("#search_apply_method").val(),
		"types":$("#search_type").val() && $("#search_type").val().toString(),
		"continue" : doContinue
	}
	if (!data.types && !data["continue"]) {
		warningConfirm("建议选择特定的消耗品分类来进行统计（如“国内耗材”）。按确认继续统计全部分类。", 
			function(){postDownloadTopTen(data);});
	} else {
		postDownloadTopTen(data);
	}
}
var postDownloadTopTen = function(data){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=reportTopTen',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : reportTopTenComplete 
	});
}

var reportTopTenComplete=function(xhrobj, textStatus){
    var resInfo = null;
    try {
        // 以Object形式读取JSON
        eval('resInfo =' + xhrobj.responseText);
        if (resInfo.errors.length > 0) {
            // 共通出错信息框
            treatBackMessages("", resInfo.errors);
        } else {
        	$("#search_supply_time_start, #search_supply_time_end").removeClass("errorarea-single");
        	if(resInfo.consumptQuotaEmptyList){
    			var $consumpt_detail = $("#consumpt_quota");
				if ($consumpt_detail.length == 0) {
					$('body').append('<div id="consumpt_quota" />');
					$consumpt_detail = $("#consumpt_quota");
				}
				$consumpt_detail.html("");
				$consumpt_detail.text("部分消耗品未设置目标值，建议为以下消耗品输入目标值！").hide();
				
				var content ='<div class="ui-widget-content">';
				content +='<table class="condform">';
				content += '<tr>';
				content += '<td class="ui-state-default td-title">NO.</td>';
				content += '<td class="ui-state-default td-title">品名</td>';
				content += '<td class="ui-state-default td-title">平均消耗量</td>';
				content += '<td class="ui-state-default td-title">消耗目标</td>';
				content += '</tr>';
				
				for(var i in resInfo.consumptQuotaEmptyList){
					var consumpt = resInfo.consumptQuotaEmptyList[i];
					content += '<tr id="' + consumpt.partial_id + '">';
					content += '<td class="td-content">' + (parseInt(i)+1) + '</td>';
					content += '<td class="td-content">' + consumpt.partial_code + '</td>';
					content += '<td class="td-content">' + consumpt.average_supply_quantity + '</td>';
					content += '<td class="td-content"><input type="text" class="ui-widget-content"></td>';
					content += '</tr>';
				}
				
				content += '</table>';
				content += '</div>';
				$consumpt_detail.append(content);
				$consumpt_detail.dialog({
					title : "消耗品目标值",
					width : 460,
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : {
						"继续": function(){
							updateConsumptQuota($consumpt_detail);
						},
						"关闭": function(){
							$consumpt_detail.dialog('close');
						}
					}
				});
				$consumpt_detail.show();
        	}else if(resInfo.fileName){
        		if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
        	}else {
				errorPop("文件导出失败！");
			}
        }
    } catch (e) {
        alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
    };
};

var updateConsumptQuota = function($consumpt_detail){
	var data = {};
	$consumpt_detail.find("tr:gt(0)").each(function(index,ele){
		var $tr = $(ele);
		data["consumable_manage.partial_id["+ index +"]"] = $tr.attr("id");
		data["consumable_manage.partial_code["+ index +"]"] = $tr.find("td").eq(1).text().trim();
		data["consumable_manage.consumpt_quota["+ index +"]"] = $tr.find("input[type='text']").val().trim();
	});

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doUpdateConsumptQuota',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj){
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length == 0) {
				$consumpt_detail.dialog('close');
				downloadTopTen(true);
			} else {
	            // 共通出错信息框
	            treatBackMessages(null, resInfo.errors);
			}
		}
	});
}
