$(function() {
var serverPath = "productFeature.do";
var noRework = "";
var onlyFinish = "";

// Your turst our mission
var findit = function(){

	$.ajax({
		url: serverPath + "?method=search",
		data: {
				"material_id": $("#global_material_id").val(),
				"noRework" : noRework,
				"onlyFinish": onlyFinish
		},
		type : "post",
		complete : search_handleComplete
	});
}

/*var findFish = function(){
	$.ajax({
		url: serverPath + "?method=searchFinish",
		data: {id: $("#global_material_id").val()},
		type : "post",
		complete : search_handleComplete
	});

}

var findNoBeforeRework = function(){
	$.ajax({
		url: serverPath + "?method=searchNoBeforeRework",
		data: {id: $("#global_material_id").val()},
		type : "post",
		complete : search_handleComplete
	});
}*/

var grayrework = function(){
//	var reworkrows = $("td[aria\\-describedby='material_detail_product_list_beforeRework'][title='返工前']").parent();
//	reworkrows.find("td").css("background-color","gray");
	
	
//	var notfinishrows = $("td[aria\\-describedby='material_detail_product_list_not_finish'][title='未完成']").parent();
//	notfinishrows.find("td").css("background-color","#50A8FF");
}

function initView(){
	$("input.ui-button").button();

	$("#nobeforereworkbutton").click(function() {
		if (this.value === "不显示返工前") {
			this.value = "显示返工前";
			noRework = "1";//非空就不显示
		} else if (this.value === "显示返工前") {
			this.value = "不显示返工前";
			noRework = ""; //空就显示
		}
		findit();
	});
	$("#finishproductionbutton").click(function(){
		if (this.value === "只显示完成") {
			this.value = "显示未完成";
			onlyFinish = "1";//非空就只显示完成
		} else if (this.value === "显示未完成") {
			this.value = "只显示完成";
			onlyFinish = ""; //空就显示全部
		}
		findit();
	});
}
	


function initGrid(){

$("#material_detail_product_list").jqGrid({
			toppager : true,
			data : [],
			height : 346,
			width : 768,
			rowheight : 23,
			datatype : "local",
			colNames : ['返工前', '工位号', '工位名', '开始时间', '结束时间', '未完成的',  '担当人', '结果', '总计时间'],
			colModel : [{
						name : 'beforeRework',
						hidden : true
					}, {
						name : 'process_code',
						sortable : false,
						width : 80
					}, {
						name : 'position_name',
						sortable : false,
						width : 80
					}, {
						name : 'action_time',
						sortable : false,
						width : 85,
						align : 'center',
						formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}
					}, {
						name : 'finish_time',
						sortable : false,
						width : 85,
						align : 'center',
						formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}
					}, {
						name : 'not_finish',
						hidden : true,
						formatter:function(a,b,c){
							if (c && !c.finish_time) {
								return "未完成";
							}
							return "";
						}
					},  {
						name : 'operator_name',
						sortable : false,
						width : 80
					}, {
						name : 'operate_result',
						sortable : false,
						width : 80
					}, {
						name : 'use_seconds',
						sortable : false,
						width : 80
					}
			],
			rowNum : 50,
			rownumbers: true,
			toppager : false,
			pager : "#material_detail_product_listpager",
			viewrecords : true,
			caption : "作业情报一览",
			hidegrid : false, // 启用或者禁用控制表格显示、隐藏的按钮
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			gridComplete : grayrework,
			recordpos : 'left'
		});

}

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
		} catch (e) {
			console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
				
		$("#material_detail_product_list").jqGrid().clearGridData();
		$("#material_detail_product_list").jqGrid('setGridParam', {data : resInfo.list}).trigger("reloadGrid", [{current : false}]);
};

	initView();
	initGrid();
	findit();
});