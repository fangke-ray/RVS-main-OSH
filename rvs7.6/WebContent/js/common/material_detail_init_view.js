// Your turst our mission
var modelname = "维修对象信息";

var loadtab = function(tab) {
	tab.load(tab.attr("lazyload") , function(responseText, textStatus, XMLHttpRequest) {
	});
}

$(function() {
	$("#material_detail_content input.ui-button").button();
	$("#material_detail_infoes, #distributions").buttonset();
	$("#material_detail_infoes input:radio").click(function() {
		$("div.material_detail_tabcontent").hide();
		var tab = $("div.material_detail_tabcontent[for='"+this.id+"']");
		tab.show();

		if (tab.text().trim().length == 0) {
			loadtab(tab);
		} else {

			if (this.id == 'material_detail_infoes_partical') {

				//TODO JSP
				eapdEditable = ($("#distributions").is(":hidden"));

				if (eapdEditable) {
					findit();
				} else {
					$("#decompose_use_radio").attr("checked",true).trigger("change");
					$("#decompose_use_radio").click();
				}
			}
		}
	});
	
	var room = {};

	var eapdEditable = false;

	function initGrid(){

		$("#exd_list").jqGrid({
			data:[],
			height: 200,
			width: 768,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','数量','工位','消耗品<br>库存','签收日期','入库预定日'],
			colModel:[
				{name:'material_partial_detail_key',index:'material_partial_detail_key', hidden:true},
				{name : 'partial_code',index : 'partial_code',width : 60,align : 'left'},
				{name : 'partial_name',index : 'partial_name',width : 300,align : 'left'},
				{name : 'quantity',index : 'quantity',width : 60,align : 'right', formatter : function(value, options, rData) {
					return (rData["cur_quantity"] || "0") + "/" + rData["quantity"];
				}},
				{name :'process_code',index:'process_code',width:60,align:'center' },
				{name :'available_inventory',index:'available_inventory',width:50,align:'center', sorttype : 'integer'},
				{name :'recent_signin_time',index:'recent_signin_time',width:100,align:'center', sorttype : 'date',
					formatter : 'date',
					formatoptions : {
						srcformat : 'Y/m/d H:i:s',
						newformat : 'm-d'
					}},
				{name :'arrival_plan_date',index:'arrival_plan_date',width:100,align:'center', sorttype : 'date', formatter : function(value, options, rData) {
		       		if (eapdEditable) {
		       			if (rData["status"] == 3) {
		       				if (value == null || value=="9999/12/31" || value=="9999-12-31") {
		       				return "<input type='text' class='fix_arrival_plan_date grid_input' value='' org='' readonly>"
		       				} else {
		       				return "<input type='text' class='fix_arrival_plan_date grid_input' value='"+ (value || "") +"' org='"+ (value || "") +"' readonly>"
		       				}
		       			} else if (rData["status"] == 4 || rData["status"] == 1) {
		       				if (value == null || value=="9999/12/31" || value=="9999-12-31") {
		       					return "";
		       				} else {
		       					var d = new Date(value);
								return mdTextOfDate(d);
		       				}
		       			}
		       		} else {
			       		if (rData["status"] == 3 && (value == null || value=="9999/12/31" || value=="9999-12-31")) {
			       			return "未定";
			       		}
			       		if (rData["status"] == 5) {
			       			return "消耗品替代";
			       		}
			       		if (rData["status"] == 6) {
			       			return "组件替代";
			       		}
			       		if (value) {
							var d = new Date(value);
							return mdTextOfDate(d);
						}
		       		}
		       		return "";
				}}
			],
			rowNum: 100,
			toppager: false,
			pager: "#exd_listpager",
			viewrecords: true,
			//multiselect: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,			
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#exd_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#exd_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var quantity = rowData["quantity"];
					var quantities = quantity.split("/");
					if (quantities.length == 2) {
						if(quantities[1]>quantities[0]){
							$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='exd_list_quantity']").addClass("ui-state-active");
						}
					}

				}
				if (eapdEditable) {
					$("#exd_list").find(".fix_arrival_plan_date").datepicker({
						showButtonPanel:true,
						dateFormat: "yy/mm/dd",
						currentText: "今天"
					});
				}
			}
		});
	}
	
	var findit = function(line_id){
		var postData = {
				"material_id": $("#global_material_id").val(),
				"line_id": line_id
			}
//		if () { TODO
//			postData["occur_times"] = $("#global_occur_times").val();
//		}
		$.ajax({
			url: "materialPartial.do?method=searchMaterialPartialDetail",
			data: postData,
			type : "post",
			complete : function(xhrobj, textStatus){
				search_handleComplete(xhrobj, textStatus, line_id);
			}
		});
	};
	
	/*
	 * Ajax通信成功的处理
	 */
	function search_handleComplete(xhrobj, textStatus, line_id) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
					
			$("#exd_list").jqGrid().clearGridData();
			$("#exd_list").jqGrid('setGridParam', {data : resInfo.list}).trigger("reloadGrid", [{current : false}]);
			room[line_id] = resInfo.list;
	};

	function initview() {

		$("input[name=distributions]").bind('click',function(){
			var line_id = this.value;
			if (room[line_id]) {
				$("#exd_list").jqGrid().clearGridData();
				$("#exd_list").jqGrid('setGridParam', {data : room[line_id]}).trigger("reloadGrid", [{current : false}]);
			} else {
				findit(line_id);
			}
		});

		initGrid();
	}
	initview();
	
});


