var servicePath = "partial_filing.do";

$(function(){
	$("#infoes >div").buttonset();
	$("#choosebutton").button();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});	
	$("#search_year_month_day").datepicker({
		showButtonPanel:true,
		dateFormat: "yymm",				
		currentText: "今天"
	});

	$("#partial_classify_load").show();
	$("#choosebutton").unbind("click");
	$("#choosebutton").click(function(){
		findit();
	});
	findit();
});

var searchdata;
var findit  = function(data){
	if(!data){
		searchdata= { "year_month":$("#search_year_month_day").val(), type : "partial_order"};
	}else{
		searchdata = data;
	}
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: servicePath + '?method=searchPartialFiling', 
		cache: false, 
		data: searchdata, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError,
		complete: search_handleComplete
	});
}
/*下载零件订购表*/
var down_partial_order = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=partial");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=partial";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}
/*下载BO缺品零件表*/
var down_bo_shortage = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=partial");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=partial";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}
/*下载零件追加明细表*/
var down_partial_additional = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=partial");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=partial";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}
/*下载BO分析表*/
var down_bo_analysis = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=partial");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=partial";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

/*jrgrid*/
var search_handleComplete = function(xhrobj, textStatus) {
var resInfo = null;
try {
	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#partial_classify_load", resInfo.errors);
	} else {
	    var listdata = resInfo.fileNameList;

		if ($("#gbox_exd_list").length > 0) {
			$("#exd_list").jqGrid().clearGridData();
			$("#exd_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#exd_list").jqGrid({
			data : listdata,
			height : 461,
			width : 990,
			rowheight : 23,
			shrinkToFit:true,
			datatype : "local",  
			colNames : ['日期', '零件订购', 'BO缺品零件','零件追加明细',"BO分析"],
			colModel : [ 
	            {name : 'fileDayTime',index : 'fileDayTime',align : 'center',width:60}, 
	            {name : 'partial_order',index : 'partial_order',width : 100,align : 'center',
						formatter : function(value, options, rData){
		   					   return	"<a href='javascript:down_partial_order(\"" + rData['partial_order'] + "\");' >" + rData['partial_order'] + "</a>"
		   				}},
	            {name : 'bo_shortage',index : 'bo_shortage',width : 100,align : 'center',
						formatter : function(value, options, rData){
		   					   return	"<a href='javascript:down_bo_shortage(\"" + rData['bo_shortage'] + "\");' >" + rData['bo_shortage'] + "</a>"
		   				}} ,
	            {name : 'partial_additional',index : 'partial_additional',width : 100,align : 'center',
						formatter : function(value, options, rData){
		   					   return	"<a href='javascript:down_partial_additional(\"" + rData['partial_additional'] + "\");' >" + rData['partial_additional'] + "</a>"
		   				}},
	            {name : 'bo_analysis',index : 'bo_analysis',width : 100,align : 'center',
						formatter : function(value, options, rData){
		   					   return	"<a href='javascript:down_bo_analysis(\"" + rData['bo_analysis'] + "\");' >" + rData['bo_analysis'] + "</a>"
		   				}}],
			rowNum :20,
			toppager : false,
			pager : "#exd_listpager",
			viewrecords : true,
			caption : "零件归档工作表",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			rownumbers : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondbClickRow : function(rid, iRow, iCol, e) {},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
				
			}
		});}
	}
					
}catch (e) {
	alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
};
}


