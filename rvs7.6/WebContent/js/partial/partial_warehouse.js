let servicePath = "partial_warehouse.do";
let curProductionType = "";
let curProductionTypeName = "";
let curKey = "";

$(function(){
	$("input.ui-button").button();
	
	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
    $("#body-mdl span.ui-icon:not(.ui-icon-circle-triangle-w)").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
    
    $("#search_warehouse_date_start,#search_warehouse_date_end,#search_finish_date_start,#search_finish_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
    
    $("#search_step").select2Buttons();
    
    $("#resetbutton").click(reset);
    
    $("#searchbutton").click(findit);
    
    $("#importButton").click(uploadFile);
    
    $("#collationOnShelfButton").click(onShelf);
    
    $("#unpackButton").click(unpack);
    
    $("#gobackbutton,#detail span.ui-icon").click(()=>{
    	$("#search").show();
    	$("#detail").hide();
    });
    
    $("#workarea span.ui-icon").click(()=> $("#search").show().siblings().hide());
    
    $("#confirmButton").click(doConfirm);
    
    findit();
});

function doConfirm(){
	let data = {
		"key" : curKey,
		"production_type" : curProductionType
	};
	
	$("#content tr[spec_kind]").each((index,tr)=>{
		let $tr = $(tr);
		data["fact_partial_warehouse.spec_kind[" + index + "]"] = $tr.attr("spec_kind");
		data["fact_partial_warehouse.quantity[" + index + "]"] = $tr.find("input[type='text']").val().trim();
	});
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doConfirm',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : (xhrobj, textStatus)=> {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#search").show().siblings().hide();
					findit();
				}
			}catch(e){}
		}
	});
};

function unpack(){
	searchByStep("0,2","分装",()=>{
		curProductionType = "214";
		curProductionTypeName="分装";
	});
};

// 核对上架
function onShelf(){
	// 点击开始按钮时候，应该做check
	searchByStep("0,1","核对/上架",()=>{
		curProductionType = "213";
		curProductionTypeName="核对/上架";
	});
};

function searchByStep(step,title,callback){
	let data = {
		"step" : step
	};
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchByStep',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : (xhrobj, textStatus)=> {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let list = resInfo.list;
					
					if(list.length == 0){
						infoPop("没有需要" + title + "的入库单。");
						return;
					}
					
					let $dialog  = $("#partial_warehouse_dialog");
					let content = `<table class="condform">
									<tbody>
									<tr class="align-center">
								   		<td class="ui-state-default td-title">DN 编号</td>
								   		<td class="ui-state-default td-title">入库单日期</td>
								   		<td class="ui-state-default td-title"></td>
								    </tr>`;
					
					list.forEach(item =>{
						content+=`<tr key="${item.key}"> 
										<td class="td-content">${item.dn_no}</td>
										<td class="td-content align-center">${item.warehouse_date}</td>
										<td class="td-content align-center">
											<input type="button" class="ui-button" value="选择">
										</td>
								  </tr>`;
					});
					content+="</tbody></table>";
					
					$dialog.html(content);
					$dialog.find("input[type='button']").button().removeClass("ui-state-hover")
					.each((index,button)=>{
						$(button).click(()=>{
							let $tr = $(button).closest("tr");
							chooseWarehouse($tr.attr("key"),$dialog);
						});
					});
					
					$dialog.dialog({
						resizable : false,
						modal : true,
						title : title,
						width : 400,
						buttons : {
							"取消":()=> $dialog.dialog("close")
						}
					});
					
					if(callback) callback();
				}
			}catch(e){}
		}
	});
};

function chooseWarehouse(key,$dialog){
	let data = {
		"key" : key,
		"production_type" : curProductionType
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchWorkInfo',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : (xhrobj, textStatus)=> {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if($dialog) $dialog.dialog("close");
					if(curKey != key) curKey = key;
						
					setWorkContent(resInfo);
				}
			}catch(e){}
		}
	});
};

function setWorkContent(resInfo){
	let partialWarehouseForm = resInfo.partialWarehouseForm;
	let list = resInfo.list;
	let kindList = resInfo.kindList;
	let factList = resInfo.factList;
	let curFactList = resInfo.curFactList;
	
	$("#modelName").text(curProductionTypeName);
	
	let $content = $("#content");
	$content.find("tr:eq(1) td:eq(0)").text(partialWarehouseForm.warehouse_date);
	$content.find("tr:eq(1) td:eq(1)").text(partialWarehouseForm.dn_no);
	$content.find("tr:gt(1)").remove();
	
	let content = `<tr>
					<td class="ui-state-default td-title">规格种别</td>
					<td class="ui-state-default td-title">${curProductionTypeName}总数</td>
					<td class="ui-state-default td-title">上次${curProductionTypeName}数量</td>
					<td class="ui-state-default td-title">本次${curProductionTypeName}数量</td>
				</tr>`;
	
	kindList.forEach(item =>{
		let spec_kind = item.spec_kind;
		let spec_kind_name = item.spec_kind_name;
		let quantity = item.quantity;
		
		content +=`<tr spec_kind = "${spec_kind}">
						<td class="ui-state-default td-title">${spec_kind_name}</td>
						<td class="td-content">${quantity}</td>
						<td class="td-content quantity"></td>
						<td class="td-content"><input type="text" class="ui-widget-content"></td>
				   </tr>`;
	});
	$content.append(content);

	factList.forEach(item => $("#content tr[spec_kind='" + item.spec_kind + "']").find("td.quantity").text(item.quantity));
	
	curFactList.forEach(item => $("#content tr[spec_kind='" + item.spec_kind + "']").find("input[type='text']").val(item.quantity))
	
	worklist(list);
	
	let tname = ['split_quantity','split_total_quantity'];
	if(curProductionType == 213){
		$("#worklist").jqGrid('hideCol',tname);
	}else{
		$("#worklist").jqGrid('showCol',tname);
	}
	$("#worklist").jqGrid('setGridWidth', '992');

	$("#workarea").show().siblings().hide();
};

function worklist(listdata){
	if ($("#gbox_worklist").length > 0) {
		$("#worklist").jqGrid().clearGridData();// 清除
		$("#worklist").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [ {current : false} ]);// 刷新列表
	} else {
		$("#worklist").jqGrid({
			data : listdata,// 数据
			height :691,// rowheight*rowNum+1
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : ['零件编号','零件名称','规格种别','数量','分装数量','分装总数'],
			colModel : [{name : 'code',index : 'code',width:50},
			            {name : 'partial_name',index : 'partial_name',width:200},
			            {name : 'spec_kind_name',index : 'spec_kind_name', align:'center',width:50},
			            {name : 'quantity',index : 'quantity',sorttype:'integer',width:50,align : 'right'},
			            {name : 'split_quantity',index : 'split_quantity',hidden:true,sorttype:'integer',width:50,align : 'right'},
			            {name : 'split_total_quantity',index : 'split_total_quantity',hidden:true,sorttype:'integer',width:50,align : 'right',formatter : function(value, options, rData){
			            	return Math.ceil((rData.quantity * 1) / (rData.split_quantity * 1));
			            }}
			],
			rowNum : 30,
			toppager : false,
			pager : "#worklistpager",
			viewrecords : true,
			caption : "",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, // 翻页按钮
			rownumbers : true,
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			onSelectRow : null,// 当选择行时触发此事件。
			ondblClickRow : function(rid, iRow, iCol, e) {
			},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
			}
		});
	}
};

function reset(){
	$("#search_dn_no,#search_warehouse_date_start,#search_warehouse_date_end,#search_finish_date_start,#search_finish_date_end").val("");
	$("#search_step").val("").trigger("change");
};

function findit(){
	let data = {
		"dn_no" : $("#search_dn_no").val(),
		"warehouse_date_start" : $("#search_warehouse_date_start").val(),
		"warehouse_date_end" : $("#search_warehouse_date_end").val(),
		"finish_date_start" : $("#search_finish_date_start").val(),
		"finish_date_end" : $("#search_finish_date_end").val(),
		"step" : $("#search_step").val()
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
		complete : (xhrobj, textStatus)=> {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					list(resInfo.finish);
				}
			}catch(e){}
		}
	});
};

function list(listdata){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();// 清除
		$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [ {current : false} ]);// 刷新列表
	} else {
		$("#list").jqGrid({
			data : listdata,// 数据
			height :461,// rowheight*rowNum+1
			width : 992,
			rowheight : 23,
			shrinkToFit:true,
			datatype : "local",
			colNames : ['','入库单日期','DN 编号', '入库单总数量','核对总数量','入库进展', '核对上架一致'],
			colModel : [{name : 'key',index : 'key',hidden:true},
			            {name : 'warehouse_date',index : 'warehouse_date',width:30,align:'center'},
			            {name : 'dn_no',index : 'dn_no',width:50},
			            {name : 'quantity',index : 'quantity',align:'right', width:50, formatter:'integer', sorttype:'integer', formatoptions:{thousandsSeparator: ','}},
			            {name : 'collation_quantity',index : 'collation_quantity',align:'right', width:50, formatter:'integer', sorttype:'integer', formatoptions:{thousandsSeparator: ','}},
			            {name : 'step',index : 'step', align:'center', width:30, formatter:'select', editoptions:{value:$("#goStep").val()}},
			            {name : 'match',index : 'match', align:'center', formatter : function(value, options, rData){
			            	let step = rData.step;
			            	if(step != 0){
		            			if(rData.quantity == rData.collation_quantity){
									return '一致';
								}else{
									return '差异';
								}
			            	}else{
			            		return '';
			            	}
						}, width:30}
			],
			rowNum : 20,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			caption : "",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, // 翻页按钮
			rownumbers : true,
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			onSelectRow : function(){},
			ondblClickRow : function(rid, iRow, iCol, e) {
				let rowData = $("#list").getRowData(rid);
				showDetail(rowData.key);
			},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
			}
		});
	}
};

function uploadFile(){
	$("#file").val("");
	$("#file_upload").dialog({
		resizable : false,
		modal : true,
		title : "上传文件",
		width : 400,
		buttons : {
			"上传" :()=>{
				$.ajaxFileUpload({
					url : servicePath + '?method=doUpload', // 需要链接到服务器地址
					secureuri : false,
					fileElementId : 'file', // 文件选择框的id属性
					dataType : 'json', // 服务器返回的格式
					success : (responseText, textStatus)=> {
						let resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages(null, resInfo.errors);
							} else {
								$("#file_upload").dialog("close");
								findit();
								infoPop("文件上传成功。");
							}
						} catch (e) {
						}
					}
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

function showDetail(key){
	let data = {
		"key": key
	};
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : (xhrobj, textStatus)=> {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#search").hide();
					$("#detail").show();
					detaillist(resInfo.list);
				}
			}catch(e){}
		}
	});
};

function detaillist(listdata){
	if ($("#gbox_detaillist").length > 0) {
		$("#detaillist").jqGrid().clearGridData();// 清除
		$("#detaillist").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [ {current : false} ]);// 刷新列表
	} else {
		$("#detaillist").jqGrid({
			data : listdata,// 数据
			height :691,// rowheight*rowNum+1
			width : 992,
			rowheight : 23,
			shrinkToFit:true,
			datatype : "local",
			colNames : ['零件编号','零件名称','规格种别','是否分裝','数量'],
			colModel : [{name : 'code',index : 'code',width:50},
			            {name : 'partial_name',index : 'partial_name',width:200},
			            {name : 'spec_kind_name',index : 'spec_kind_name', align:'center',width:50},
			            {name : 'unpack_flg',index : 'unpack_flg', align:'center',width:50, formatter : function(value, options, rData){
			            	if(value == 1){
			            		return "是";
			            	}else{
			            		return "否";
			            	}
			            }},
			            {name : 'quantity',index : 'quantity',sorttype:'integer',width:50,align : 'right'}
			],
			rowNum : 30,
			toppager : false,
			pager : "#detaillistpager",
			viewrecords : true,
			caption : "",
			multiselect : false,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, // 翻页按钮
			rownumbers : true,
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			onSelectRow : null,// 当选择行时触发此事件。
			ondblClickRow : function(rid, iRow, iCol, e) {
			},
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
			}
		});
	}
};