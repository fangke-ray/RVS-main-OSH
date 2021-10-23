var servicePath = "delivery_order.do";
var waitingsList = {};

$(function(){
	$("input.ui-button").button();
	
	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
    $("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
    
    $("#shipbutton").click(function(){
    	afObj.applyProcess(241, this, makeShip, null);
    });

    load_list([]);
    acceptted_list([]);
    findit();

	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		afObj.applyProcess(241, this, makeShip, [this.value]);
	}
	}).keyup(function(){
	if (this.value.length >= 11) {
		afObj.applyProcess(241, this, makeShip, [this.value]);
	}
	}).focus();

});

function makeShip(material_id){
	var pill = $("#list");

	if (material_id) {
		$("#scanner_inputer").val("");
		for (var igridlist = 0; igridlist < waitingsList.length; igridlist++) {
			var hit = false;
			if (waitingsList[igridlist].material_id == material_id) {
				hit = true;
				break;
			}
		}
		if (!hit) {
			errorPop("此维修品不在等待制作出货单中。");
			return;
		}
	} else {
		material_id = pill.jqGrid("getGridParam", "selrow");
	}

	
	var postData = {
		"material_id" : material_id
	};
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doInsert',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					findit();
				}
			}catch(e){
				console.log(e.message);
			}
		}
	});
};

function findit(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					waitingsList = resInfo.waitings;
					load_list(waitingsList);
					acceptted_list(resInfo.finished);
				}
			}catch(e){
				console.log(e.message);
			}
		}
	});
};

function load_list(listdata){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#list").jqGrid({
			toppager : true,
			data:listdata,
			height: 346,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['material_id','受理时间','同意时间','完成日期','修理单号', '型号' , '机身号','委托处', '等级', '加急'],
			colModel:[{name:'material_id',index:'material_id', hidden:true, key:true},
			          {
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					},
				{name:'sorc_no',index:'sorc_no', width:60},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:60, align:'center'},
				{
					name : 'ocm',
					index : 'ocm',
					width : 65, formatter: 'select', editoptions:{value: $("#hide_ocm").val()}
				}, {
					name : 'level',
					index : 'level',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: $("#hide_level").val()}
				}, {
					name : 'scheduled_expedited',
					index : 'scheduled_expedited',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: "0:;1:加急;2:直送快速"}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			gridview: true, 
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			onSelectRow : enableButtons,
			gridComplete: function() {
				enableButtons();
			}
		});
	}
};

function acceptted_list(listdata){
	if ($("#gbox_curlist").length > 0) {
		$("#curlist").jqGrid().clearGridData();
		$("#curlist").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#curlist").jqGrid({
			toppager : true,
			data:listdata,
			height: 231,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['material_id','受理时间','同意时间','完成日期','修理单号', '型号' , '机身号','委托处', '等级', '加急'],
			colModel:[{name:'material_id',index:'material_id', hidden:true},
			          {
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					},
				{name:'sorc_no',index:'sorc_no', width:60},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:60, align:'center'},
				{
					name : 'ocm',
					index : 'ocm',
					width : 65, formatter: 'select', editoptions:{value: $("#hide_ocm").val()}
				}, {
					name : 'level',
					index : 'level',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: $("#hide_level").val()}
				}, {
					name : 'scheduled_expedited',
					index : 'scheduled_expedited',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: "0:;1:加急;2:直送快速"}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#curlistpager",
			viewrecords: true,
			gridview: true, 
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete: function() {}
		});
	}
}

function enableButtons(){
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (rowid) {
		$("#shipbutton").removeClass("ui-state-focus").enable();
	} else {
		$("#shipbutton").removeClass("ui-state-focus").disable();
	}
	$("#scanner_inputer").focus();
}