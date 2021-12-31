let servicePath = "supplies_detail.do";
let waringUnitPrice = 2000;
$(function() {
	$("input.ui-button").button();

	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#search_section_id").select2Buttons();
	setReferChooser($("#hidden_search_applicator_id"),$("#operator_referchooser"));
	setReferChooser($("#hidden_search_confirmer_id"),$("#operator_referchooser"));

	$("#search_applicate_date_start,#search_applicate_date_end," +
			"#search_scheduled_date_start,#search_scheduled_date_end," +
			"#search_recept_date_start,#search_recept_date_end," +
			"#search_inline_recept_date_start,#search_inline_recept_date_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	let today = new Date();
	$("#search_budget_month").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

	$("#resetbutton").click(reset);

	$("#searchbutton").click(function(){
		$("#search_product_name").data("post",$("#search_product_name").val());
		$("#search_model_name").data("post",$("#search_model_name").val());
		$("#search_section_id").data("post",$("#search_section_id").val());
		$("#hidden_search_applicator_id").data("post",$("#hidden_search_applicator_id").val());
		$("#search_applicate_date_start").data("post",$("#search_applicate_date_start").val());
		$("#search_applicate_date_end").data("post",$("#search_applicate_date_end").val());
		$("#hidden_search_confirmer_id").data("post",$("#hidden_search_confirmer_id").val());
		$("#search_order_no").data("post",$("#search_order_no").val());
		$("#search_scheduled_date_start").data("post",$("#search_scheduled_date_start").val());
		$("#search_scheduled_date_end").data("post",$("#search_scheduled_date_end").val());
		$("#search_recept_date_start").data("post",$("#search_recept_date_start").val());
		$("#search_recept_date_end").data("post",$("#search_recept_date_end").val());
		$("#search_budget_month").data("post",$("#search_budget_month").val());
		$("#search_inline_recept_date_start").data("post",$("#search_inline_recept_date_start").val());
		$("#search_inline_recept_date_end").data("post",$("#search_inline_recept_date_end").val());
		$("#search_invoice_no").data("post",$("#search_invoice_no").val());

		findit();
	});

	//申请事件
	$("#applicationButton").click(application);

	$("#filterSearchButton").click(filterSupplise);

	$("#filter_product_name").keyup(function(){
		if(!this.value){
			$("#filterContainer > ul li").fadeIn();
		}
	});

	//编辑订购单
	$("#editButton").click(editSupplise);

	$("#updatearea span.ui-icon").click(()=>{
		$("#searcharea").show();
		$("#updatearea").hide();
	});

	//收货
	$("#receptButton").click(recept);

	//验收
	$("#inlineReceptbutton").click(inlineRecept);

	//发票
	$("#invoiceButton").click(invoice);

	//确认订购单
	$("#confirmOrderButton").click(function(){
		confirmOrder(true);
	});

	findit();
});

/**
 * 清除
 */
function reset(){
	$("#search_product_name").data("post","").val("");
	$("#search_model_name").data("post","").val("");
	$("#search_section_id").data("post","").val("").trigger("change");
	$("#hidden_search_applicator_id").data("post","").val("");
	$("#search_applicator_name").val("");
	$("#search_applicate_date_start").data("post","").val("");
	$("#search_applicate_date_end").data("post","").val("");
	$("#hidden_search_confirmer_id").data("post","").val("");
	$("#search_confirmer_name").val("");
	$("#search_order_no").data("post","").val("");
	$("#search_scheduled_date_start").data("post","").val("");
	$("#search_scheduled_date_end").data("post","").val("");
	$("#search_recept_date_start").data("post","").val("");
	$("#search_recept_date_end").data("post","").val("");
	$("#search_budget_month").data("post","").val("");
	$("#search_inline_recept_date_start").data("post","").val("");
	$("#search_inline_recept_date_end").data("post","").val("");
	$("#search_invoice_no").data("post","").val("");
};
/**
 * 检索
 */
function findit(){
	let data = {
		"product_name" : $("#search_product_name").data("post"),
		"model_name" : $("#search_model_name").data("post"),
		"section_id" : $("#search_section_id").data("post"),
		"applicator_id" : $("#hidden_search_applicator_id").data("post"),
		"applicate_date_start" : $("#search_applicate_date_start").data("post"),
		"applicate_date_end" : $("#search_applicate_date_end").data("post"),
		"confirmer_id" : $("#hidden_search_confirmer_id").data("post"),
		"order_no" : $("#search_order_no").data("post"),
		"scheduled_date_start" : $("#search_scheduled_date_start").data("post"),
		"scheduled_date_end" : $("#search_scheduled_date_end").data("post"),
		"recept_date_start" : $("#search_recept_date_start").data("post"),
		"recept_date_end" : $("#search_recept_date_end").data("post"),
		"budget_month" : $("#search_budget_month").data("post"),
		"inline_recept_date_start" : $("#search_inline_recept_date_start").data("post"),
		"inline_recept_date_end" : $("#search_inline_recept_date_end").data("post"),
		"invoice_no" : $("#search_invoice_no").data("post")
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
		complete : function(xhrobj, textStatus){
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let listdata = resInfo.list;
					filed_list(listdata);
				}
			}catch (e) {};
		}
	});
};

/**
 * 一览
 * @param listdata 数据源
 */
function filed_list(listdata) {
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#list").jqGrid({
			data:listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','品名','规格','数量','预定<br>单价','单位','总价','供应商','申购<br>人员','申购日期','申购单号','上级<br>确认者','预计到货<br>日期','收货<br>日期','预算月','验收<br>日期','发票号码','申购人员ID'],
			colModel:[
			     {name:'supplies_key',index:'supplies_key', hidden : true},
			     {name:'product_name',index:'product_name', width : 80},
				 {name:'model_name',index:'model_name',width : 60},
				 {name:'quantity',index:'quantity',width : 30,align:'right',sorttype:'integer'},
				 {name:'unit_price',index:'unit_price',width : 40,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}},
				 {name:'unit_text',index:'unit_text',width : 30},
				 {name:'total_price',index:'total_price',width : 40,align:'right',sorttype:'currency',formatter:function(value, options, rData) {
					 if(rData.quantity && rData.unit_price){
						 return parseInt(rData.quantity) * parseFloat(rData.unit_price);
					 } else {
						 return "-";
					 }
				 }},
				{name:'supplier',index:'supplier',width : 60},
				{name:'applicator_name',index:'applicator_name',width : 40},
				{name:'applicate_date',index:'applicate_date',width : 60,align:'center',sorttype:'date'},
				{name:'order_no',index:'order_no',width : 60},
				{name:'confirmer_name',index:'confirmer_name',width : 40},
				{name:'scheduled_date',index:'scheduled_date',width : 60,sorttype:'date'},
				{name:'recept_date',index:'recept_date',width : 60,sorttype:'date'},
				{name:'budget_month',index:'budget_month',width : 40,},
				{name:'inline_recept_date',index:'inline_recept_date',width : 60,sorttype:'date'},
				{name:'invoice_no',index:'invoice_no',width : 60},
				{name:'applicator_id',index:'applicator_id',hidden : true}
			],
			rowNum: 40,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			rownumbers : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : showEdit,
			onSelectRow : enableButton,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
				enableButton();
			}
		});
	}
};

function enableButton(){
	//当前登录者
	let loginID = $("#loginID").val();

	//选择行，并获取行数
	let rowid = $("#list").jqGrid("getGridParam", "selrow");
	if(rowid) {
		let rowdata = $("#list").jqGrid('getRowData', rowid);
		//申请人自己的数据
		if(rowdata.applicator_id == loginID) {
			//已经有收货日期，还没有验收，验收按钮可以使用
			if(rowdata.recept_date && !rowdata.inline_recept_date) {
				$("#inlineReceptbutton").enable();
			} else {
				$("#inlineReceptbutton").disable();
			}
		} else {
			$("#inlineReceptbutton").disable();
		}

		if(rowdata.recept_date && !rowdata.invoice_no){
			$("#invoiceButton").enable();
		} else {
			$("#invoiceButton").disable();
		}

	} else {
		$("#inlineReceptbutton,#invoiceButton").disable();
	}
}

function inlineRecept() {
	let rowid = $("#list").jqGrid("getGridParam", "selrow");
	let rowdata = $("#list").jqGrid('getRowData', rowid);

	let content = `确认验收品名【${rowdata.product_name}】,规格【${rowdata.model_name}】申购物品？</label>`;
	let $confirm = $("#confirmmessage");
	$confirm.html(content);

	$confirm.dialog({
		resizable: false,
		modal: true,
		title: "申购物品验收",
		show:"blind",
		buttons: {
			"确认": function() {
				let data = {
					"supplies_key" : rowdata.supplies_key
				};

				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doInlineRecept',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj, textStatus) {
						let resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages(null, resInfo.errors);
							} else {
								$confirm.dialog("close");
								findit();
							}
						}catch(e){}
					}
				});
			},
			"取消": function() {
				$confirm.dialog("close");
			}
		}
	});
};

function invoice() {
	let rowid = $("#list").jqGrid("getGridParam", "selrow");
	let rowdata = $("#list").jqGrid('getRowData', rowid);

	let content = `<form>
						<div class="ui-widget-content">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">发票号码</td>
									<td class="td-content">
										<input type="text" id="edit_invoice_no" name="invoice_no" class="ui-widget-content" alt="发票号码">
									</td>
								</tr>
							</table>
						</div>
					</form>
					`;
	let $confirm = $("#confirmmessage");
	$confirm.html(content);

	$confirm.children("form").validate({
		rules : {
			invoice_no : {
				required : true,
				maxlength : 8
			}
		}
	});

	$confirm.dialog({
		resizable: false,
		modal: true,
		title: "发票",
		width:350,
		show:"blind",
		buttons: {
			"确认": function() {
				if ($confirm.children("form").valid()) {
					let data = {
						"supplies_key" : rowdata.supplies_key,
						"invoice_no" : $("#edit_invoice_no").val()
					};

					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=doInvoice',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							let resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									$confirm.dialog("close");
									findit();
								}
							}catch(e){}
						}
					});
				}
			},
			"取消": function() {
				$confirm.dialog("close");
			}
		}
	});
}

/**
 * 编辑物品申购明细
 */
function showEdit(){
	let rowid = $("#list").jqGrid("getGridParam","selrow");//得到选中的行ID
    let rowData = $("#list").getRowData(rowid);
	let data = {
		"supplies_key" : rowData.supplies_key
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getDetail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					//物品申购明细
					setEditContent(resInfo);
				}
			}catch(e){}
		}
	});
};

let updateTemplate = `<table class="condform">
						<tr>
							<td class="ui-state-default td-title">品名</td>
							<td class="td-content" colspan="3">#{productName}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">规格</td>
							<td class="td-content" colspan="3">#{modelName}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">预定单价</td>
							<td class="td-content">#{unitPrice}</td>
							<td class="ui-state-default td-title">单位</td>
							<td class="td-content">#{unitText}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">数量</td>
							<td class="td-content">#{quantity}</td>
							<td class="ui-state-default td-title">供应商</td>
							<td class="td-content">#{supplier}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">申请课室</td>
							<td class="td-content">
								<label id="update_label_section_name"></label>
							</td>
							<td class="ui-state-default td-title">申购人员</td>
							<td class="td-content">
								<label id="update_label_applicator_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">申请日期</td>
							<td class="td-content">
								<label id="update_label_applicate_date"></label>
							</td>
							<td class="ui-state-default td-title">申购单号</td>
							<td class="td-content">
								<label id="update_label_order_no"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">上级确认者</td>
							<td class="td-content">
								<label id="update_label_confirmer_name"></label>
							</td>
							<td class="ui-state-default td-title">确认结果</td>
							<td class="td-content">
								<label id="update_label_confirmer_result"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">用途</td>
							<td class="td-content" colspan="3">#{nesssaryReason}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">备注</td>
							<td class="td-content" colspan="3">#{comments}</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">预计到货日期</td>
							<td class="td-content">#{scheduledDate}</td>
							<td class="ui-state-default td-title">收货日期</td>
							<td class="td-content">
								<label id="update_label_recept_date"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">预算月</td>
							<td class="td-content">#{budgetMonth}</td>
							<td class="ui-state-default td-title">验收日期</td>
							<td class="td-content">
								<label id="update_label_inline_recept_date"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">发票号</td>
							<td class="td-content">
								<label id="update_label_invoice_no"></label>
							</td>
						</tr>
					  </table>
					  <div style="height:44px">
					  	<input class="ui-button" id="updateGobackButton" value="返回" style="float:right;right:2px" type="button">
						#{updateInput}
						#{deleteInput}
						#{ngInput}
						#{okInput}
					  </div>`;

let inputObjTemplate = {
	'productName' : '<input type="text" id="update_product_name" name="product_name" class="ui-widget-content" style="width:500px;" alt="品名">',
	'modelName' : '<input type="text" id="update_model_name" name="model_name" class="ui-widget-content" style="width:500px;" alt="规格">',
	'unitPrice' : '<input type="text" id="update_unit_price" name="unit_price" class="ui-widget-content" alt="预定单价">',
	'unitText' : '<input type="text" id="update_unit_text" name="unit_text" class="ui-widget-content" alt="单位">',
	'quantity' : '<input type="text" id="update_quantity" name="quantity" class="ui-widget-content" alt="数量">',
	'supplier' : '<input type="text" id="update_supplier" name="supplier" class="ui-widget-content" alt="供应商">',
	'nesssaryReason' : '<textarea id="update_nesssary_reason" name="nesssary_reason" class="ui-widget-content" alt="用途"></textarea>',
	'comments' : '<textarea id="update_comments" name="comments" class="ui-widget-content" alt="备注"></textarea>',
	'scheduledDate' : '<input type="text" id="update_scheduled_date" name="scheduled_date" class="ui-widget-content" alt="预计到货日期" readonly="readonly">',
	'budgetMonth' : '<input type="text" id="update_budget_month" name="budget_month" class="ui-widget-content" alt="预算月" readonly="readonly">'
};

let labelObjTemplate = {
	'productName' : '<label id="update_label_product_name"></label>',
	'modelName' : '<label id="update_label_model_name"></label>',
	'unitPrice' : '<label id="update_label_unit_price"></label>',
	'unitText' : '<label id="update_label_unit_text"></label>',
	'quantity' : '<label id="update_label_quantity"></label>',
	'supplier' : '<label id="update_label_supplier"></label>',
	'nesssaryReason' : '<label id="update_label_nesssary_reason"></label>',
	'comments' : '<label id="update_label_comments"></label>',
	'scheduledDate' : '<label id="update_label_scheduled_date"></label>',
	'budgetMonth' : '<label id="update_label_budget_month"></label>'
};

let buttonObjTemplate = {
	'updateButton' : '<input class="ui-button" id="updateButton" value="更新" style="float:right;right:2px" type="button">',
	'deleteButton' : '<input class="ui-button" id="deleteButton" value="删除" style="float:right;right:2px" type="button">',
	'ngButton' : '<input class="ui-button" id="ngButton" value="驳回" style="float:right;right:2px" type="button">',
	'okButton' : '<input class="ui-button" id="okButton" value="确认" style="float:right;right:2px" type="button">'
}

/**
 * 设置画面内容
 * @param {*} suppliesDetail
 */
function setEditContent(resInfo) {
	let suppliesDetail = resInfo.detail;
	let order = resInfo.order;

	//经理标识
	let isMamager = $("#isMamager").val();
	//线长标识
	let isLiner = $("#isLiner").val();
	//支援课标识
	let isSupport = $("#isSupport").val();
	//当前登录者ID
	let loginID = $("#loginID").val();

	//赋值一份模板
	let template = updateTemplate;
	//申请人ID
	let applicator_id = suppliesDetail.applicator_id;
	//确认者
	let confirmer_name = suppliesDetail.confirmer_name || '';
	//收货日期
	let recept_date = suppliesDetail.recept_date || '';
	//订购单KEy
	let order_key = suppliesDetail.order_key || '';

	//已确认（确认或者驳回）
	if(confirmer_name) {
		template = template.replace("#{productName}",labelObjTemplate.productName);
		template = template.replace("#{modelName}",labelObjTemplate.modelName);
		template = template.replace("#{unitPrice}",labelObjTemplate.unitPrice);
		template = template.replace("#{unitText}",labelObjTemplate.unitText);
		template = template.replace("#{quantity}",labelObjTemplate.quantity);
		template = template.replace("#{supplier}",labelObjTemplate.supplier);
		template = template.replace("#{nesssaryReason}",labelObjTemplate.nesssaryReason);
		
		//驳回明细
		if(order_key == "0" || order_key == "00000000000") {
			template = template.replace("#{comments}",labelObjTemplate.comments);
			template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
			template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);
		} else {//确认明细
			if(isSupport === "true") {//支援课
				template = template.replace("#{comments}",inputObjTemplate.comments);
			} else {
				template = template.replace("#{comments}",labelObjTemplate.comments);
			}
			//登录者是支援课人员,还未收货
			if(isSupport === "true" && !recept_date) {
				//支援科上级盖章
				if(order && order.sign_manager_id && order.sign_minister_id) {
					template = template.replace("#{scheduledDate}",inputObjTemplate.scheduledDate);
					template = template.replace("#{budgetMonth}",inputObjTemplate.budgetMonth);
				} else {
					//支援科上级没有盖章
					template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
					template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);
				}
			} else {
				template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);
			}
		}

		template = template.replace("#{okInput}","");
		template = template.replace("#{ngInput}","");
		template = template.replace("#{deleteInput}","");
		if(template.includes('type="text"') || template.includes('textarea')) {
			template = template.replace("#{updateInput}",buttonObjTemplate.updateButton);
		} else {
			template = template.replace("#{updateInput}","");
		}
	} else {//上级未确认,这个时候还没有生成订购单，支援课上级还不能进行盖章
		//经理
		if(isMamager === "true") {
			//申请人是自己
			if(applicator_id == loginID) {
				//可以编辑
				template = template.replace("#{productName}",inputObjTemplate.productName);
				template = template.replace("#{modelName}",inputObjTemplate.modelName);
				template = template.replace("#{unitPrice}",inputObjTemplate.unitPrice);
				template = template.replace("#{unitText}",inputObjTemplate.unitText);
				template = template.replace("#{quantity}",inputObjTemplate.quantity);
				template = template.replace("#{supplier}",inputObjTemplate.supplier);
				template = template.replace("#{nesssaryReason}",inputObjTemplate.nesssaryReason);
				if(isSupport === "true") {//支援课
					template = template.replace("#{comments}",inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}",labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}",buttonObjTemplate.okButton);
				template = template.replace("#{ngInput}",buttonObjTemplate.ngButton);
				template = template.replace("#{deleteInput}",buttonObjTemplate.deleteButton);
				if(template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}",buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}","");
				}
			} else {
				//线长的数据
				if(suppliesDetail.isLiner == "1") {
					template = template.replace("#{productName}",inputObjTemplate.productName);
					template = template.replace("#{modelName}",inputObjTemplate.modelName);
					template = template.replace("#{unitPrice}",inputObjTemplate.unitPrice);
					template = template.replace("#{unitText}",inputObjTemplate.unitText);
					template = template.replace("#{quantity}",inputObjTemplate.quantity);
					template = template.replace("#{supplier}",inputObjTemplate.supplier);
					template = template.replace("#{nesssaryReason}",inputObjTemplate.nesssaryReason);
				} else {
					template = template.replace("#{productName}",labelObjTemplate.productName);
					template = template.replace("#{modelName}",labelObjTemplate.modelName);
					template = template.replace("#{unitPrice}",labelObjTemplate.unitPrice);
					template = template.replace("#{unitText}",labelObjTemplate.unitText);
					template = template.replace("#{quantity}",labelObjTemplate.quantity);
					template = template.replace("#{supplier}",labelObjTemplate.supplier);
					template = template.replace("#{nesssaryReason}",labelObjTemplate.nesssaryReason);
				}
				if(isSupport === "true") {//支援课
					template = template.replace("#{comments}",inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}",labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}",buttonObjTemplate.okButton);
				template = template.replace("#{ngInput}",buttonObjTemplate.ngButton);
				template = template.replace("#{deleteInput}","");
				if(template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}",buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}","");
				}
			}
		//线长或者支援课
		} else if(isLiner === "true" || isSupport === "true") {
			//申请人是自己
			if(applicator_id == loginID) {
				//可以编辑
				template = template.replace("#{productName}",inputObjTemplate.productName);
				template = template.replace("#{modelName}",inputObjTemplate.modelName);
				template = template.replace("#{unitPrice}",inputObjTemplate.unitPrice);
				template = template.replace("#{unitText}",inputObjTemplate.unitText);
				template = template.replace("#{quantity}",inputObjTemplate.quantity);
				template = template.replace("#{supplier}",inputObjTemplate.supplier);
				template = template.replace("#{nesssaryReason}",inputObjTemplate.nesssaryReason);
				if(isSupport === "true") {//支援课
					template = template.replace("#{comments}",inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}",labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);
				
				template = template.replace("#{okInput}","");
				template = template.replace("#{ngInput}","");
				template = template.replace("#{deleteInput}",buttonObjTemplate.deleteButton);
				if(template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}",buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}","");
				}
			} else {
				template = template.replace("#{productName}",labelObjTemplate.productName);
				template = template.replace("#{modelName}",labelObjTemplate.modelName);
				template = template.replace("#{unitPrice}",labelObjTemplate.unitPrice);
				template = template.replace("#{unitText}",labelObjTemplate.unitText);
				template = template.replace("#{quantity}",labelObjTemplate.quantity);
				template = template.replace("#{supplier}",labelObjTemplate.supplier);
				template = template.replace("#{nesssaryReason}",labelObjTemplate.nesssaryReason);
				if(isSupport === "true") {//支援课
					template = template.replace("#{comments}",inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}",labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}","");
				template = template.replace("#{ngInput}","");
				template = template.replace("#{deleteInput}","");
				if(template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}",buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}","");
				}
			}
		} else {
			//其他不可编辑
			template = template.replace("#{productName}",labelObjTemplate.productName);
			template = template.replace("#{modelName}",labelObjTemplate.modelName);
			template = template.replace("#{unitPrice}",labelObjTemplate.unitPrice);
			template = template.replace("#{unitText}",labelObjTemplate.unitText);
			template = template.replace("#{quantity}",labelObjTemplate.quantity);
			template = template.replace("#{supplier}",labelObjTemplate.supplier);
			template = template.replace("#{nesssaryReason}",labelObjTemplate.nesssaryReason);
			template = template.replace("#{comments}",labelObjTemplate.comments);
			template = template.replace("#{scheduledDate}",labelObjTemplate.scheduledDate);
			template = template.replace("#{budgetMonth}",labelObjTemplate.budgetMonth);
			
			template = template.replace("#{okInput}","");
			template = template.replace("#{ngInput}","");
			template = template.replace("#{deleteInput}","");
			template = template.replace("#{updateInput}","");
		}
	}

	$("#updateform").html(template);
	$("#updateform input.ui-button").button();

	$("#update_scheduled_date").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	let today = new Date();
	$("#update_budget_month").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear() + 1,
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

	//品名
	$("#update_product_name").val(suppliesDetail.product_name);
	$("#update_label_product_name").text(suppliesDetail.product_name);
	//规格
	let model_name = suppliesDetail.model_name || '';
	$("#update_model_name").val(model_name);
	$("#update_label_model_name").text(model_name);
	//预定单价
	let unit_price = suppliesDetail.unit_price || '';
	$("#update_unit_price").val(unit_price);
	$("#update_label_unit_price").text(unit_price);
	//单位
	let unit_text = suppliesDetail.unit_text || '';
	$("#update_unit_text").val(unit_text);
	$("#update_label_unit_text").text(unit_text);
	//数量
	let quantity = suppliesDetail.quantity || '';
	$("#update_quantity").val(quantity);
	$("#update_label_quantity").text(quantity);
	//供应商
	let supplier = suppliesDetail.supplier || '';
	$("#update_supplier").val(supplier);
	$("#update_label_supplier").text(supplier);
	//申请课室
	$("#update_label_section_name").text(suppliesDetail.section_name);
	//申购人员
	$("#update_label_applicator_name").text(suppliesDetail.applicator_name);
	//申请日期
	$("#update_label_applicate_date").text(suppliesDetail.applicate_date);
	//申购单号
	let order_no = suppliesDetail.order_no || '';
	$("#update_label_order_no").text(order_no);
	//上级确认者
	$("#update_label_confirmer_name").text(confirmer_name);
	//确认结果
	if(confirmer_name) {
		let order_key = suppliesDetail.order_key;
		if(order_key == "0" || order_key == "00000000000"){
			$("#update_label_confirmer_result").text("驳回");
		} else {
			$("#update_label_confirmer_result").text("确认");
		}
	} else {
		$("#update_label_confirmer_result").text("");
	}
	//用途
	let nesssary_reason = suppliesDetail.nesssary_reason || '';
	$("#update_nesssary_reason").val(nesssary_reason);
	$("#update_label_nesssary_reason").html(decodeText(nesssary_reason));
	//备注
	let comments = suppliesDetail.comments || '';
	$("#update_comments").val(comments);
	$("#update_label_comments").html(decodeText(comments));
	//预计到货日期
	let scheduled_date = suppliesDetail.scheduled_date || '';
	$("#update_scheduled_date").val(scheduled_date);
	$("#update_label_scheduled_date").text(scheduled_date);
	//收货日期
	$("#update_label_recept_date").text(recept_date);
	//预算月
	let budget_month = suppliesDetail.budget_month || '';
	$("#update_label_budget_month").text(budget_month);
	$("#update_budget_month").val(budget_month);
	//验收日期
	let inline_recept_date = suppliesDetail.inline_recept_date || '';
	$("#update_label_inline_recept_date").text(inline_recept_date);
	//发票号
	let invoice_no = suppliesDetail.invoice_no || '';
	$("#update_label_invoice_no").text(invoice_no);

	$("#updateGobackButton").click(()=>{
		$("#searcharea").show();
		$("#updatearea").hide();
	});

	$("#okButton").bind("click",()=>{
		warningConfirm("确认不能修改，是否要确认？",function(){
			doComfirm("OK",suppliesDetail);
		},null);
	});

	$("#ngButton").bind("click",()=>{
		warningConfirm("驳回不能修改，是否要驳回？",function(){
			doComfirm("NG",suppliesDetail);
		},null);
	});

	$("#deleteButton").bind("click",()=>{
		warningConfirm("删除不能恢复，是否要删除？",function(){
			doDelete(suppliesDetail);
		},null);
	});

	$("#searcharea").hide();
	$("#updatearea").show();

	//动态前台验证规则
	if($("#updateButton").length == 1) {
		let dynamicRules = {};
		//品名
		if($("#update_product_name").length == 1) {
			dynamicRules.product_name = {
				required : true,
				maxlength : 64
			};
		}
		//规格
		if($("#update_model_name").length == 1) {
			dynamicRules.model_name = {
				maxlength : 32
			};
		}
		//预定单价
		if($("#update_unit_price").length == 1) {
			dynamicRules.unit_price = {
				required : true,
				number : true,
				range : [0.01,9999.99]
			}
		}
		//单位
		if($("#update_unit_text").length == 1) {
			dynamicRules.unit_text = {
				maxlength : 3
			}
		}
		//数量
		if($("#update_quantity").length == 1) {
			dynamicRules.quantity = {
				required : true,
				digits : true,
				maxlength : 2,
				min : 1
			}
		}
		//供应商
		if($("#update_supplier").length == 1) {
			dynamicRules.supplier = {
				maxlength : 64
			}
		}
		//用途
		if($("#update_nesssary_reason").length == 1) {
			dynamicRules.nesssary_reason = {
				maxlength : 256
			}
		}
		//备注
		if($("#update_comments").length == 1) {
			dynamicRules.comments = {
				maxlength : 256
			}
		}

		$("#updateform").validate({
			rules:dynamicRules
		});
	}

	$("#updateButton").bind("click",function(){
		if($("#updateform").valid()) {
			if($("#update_unit_price").length == 1) {
				let unit_price = $("#update_unit_price").val().trim();
				unit_price = +unit_price;
				if(unit_price > waringUnitPrice) {
					warningConfirm("预定单价高于" + waringUnitPrice +"，请确认是否要更新物品申购明细？",function() {
						doUpdate(suppliesDetail);
					},null);
				} else {
					doUpdate(suppliesDetail);
				}
			} else {
				doUpdate(suppliesDetail);
			}
		}
	});
}

function doUpdate(suppliesDetail){
	let data = {
		"supplies_key" : suppliesDetail.supplies_key
	};

	if($("#update_product_name").length == 1) {
		data["product_name"] = $("#update_product_name").val();
	} else {
		data["product_name"] = suppliesDetail.product_name;
	}

	if($("#update_model_name").length == 1) {
		data["model_name"] = $("#update_model_name").val();
	} else {
		data["model_name"] = suppliesDetail.model_name || "";
	}
	
	if($("#update_unit_price").length == 1) {
		data["unit_price"] = $("#update_unit_price").val();
	} else {
		data["unit_price"] = suppliesDetail.unit_price || "";
	}

	if($("#update_unit_text").length == 1) {
		data["unit_text"] = $("#update_unit_text").val();
	} else {
		data["unit_text"] = suppliesDetail.unit_text || "";
	}

	if($("#update_quantity").length == 1) {
		data["quantity"] = $("#update_quantity").val();
	} else {
		data["quantity"] = suppliesDetail.quantity || "";
	}

	if($("#update_supplier").length == 1) {
		data["supplier"] = $("#update_supplier").val();
	} else {
		data["supplier"] = suppliesDetail.supplier || "";
	}

	if($("#update_nesssary_reason").length == 1) {
		data["nesssary_reason"] = $("#update_nesssary_reason").val();
	} else {
		data["nesssary_reason"] = suppliesDetail.nesssary_reason || "";
	}

	if($("#update_comments").length == 1) {
		data["comments"] = $("#update_comments").val();
	} else {
		if($("#isLiner").val() === "true") {
			if($("#update_supplier").length == 1) {
				data["comments"] = $("#update_supplier").val();
			} else {
				data["comments"] = suppliesDetail.supplier || "";
			}
		} else {
			data["comments"] = suppliesDetail.comments || "";
		}
	}

	if($("#update_scheduled_date").length == 1) {
		data["scheduled_date"] = $("#update_scheduled_date").val();
	} else {
		data["scheduled_date"] = suppliesDetail.scheduled_date || "";
	}

	if($("#update_budget_month").length == 1) {
		data["budget_month"] = $("#update_budget_month").val();
	} else {
		data["budget_month"] = suppliesDetail.budget_month || "";
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
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					findit();
				}
			}catch(e){}
		}
	});
}

/**
 * 经理确认action
 * @param {*} comfirmFlag 确认、驳回标记
 * @param {*} suppliesDetail 数据
 */
function doComfirm(comfirmFlag,suppliesDetail){
	let data = {
		"supplies_key" : suppliesDetail.supplies_key,
		"confirm_flg" : comfirmFlag
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doComfirm',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					findit();
				}
			}catch(e){}
		}
	});
}

/**
 * 删除明细
 * @param {} suppliesDetail 
 */
function doDelete(suppliesDetail){
	let data = {
		"supplies_key" : suppliesDetail.supplies_key
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doDelete',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					findit();
				}
			}catch(e){}
		}
	});
};

/**
 * 申请
 */
function application(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'supplies_refer_list.do?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					//常用采购清单
					let suppliesReferList = resInfo.list;
					addDetailDialog(suppliesReferList);
				}
			}catch(e){}
		}
	});
};

/**
 * @param {*} list 常用采购清单
 */
function addDetailDialog(list){
	$("#add_supplies_detail").find("input[type='text'],textarea").val("").removeClass("errorarea-single");
	setSuppliseView(list);

	let arrModelName = ['慧采','亚速旺','易优佰','京东','心承','定制'];
	list.map(item => {
		!arrModelName.includes(item.model_name) && arrModelName.splice(arrModelName.length - 1, 0, item.model_name);
	});
	$("#add_comment").autocomplete({
		source : arrModelName, 
		minLength: 0, 
		delay: 100
	});
	$("#add_comment").unbind("focus").bind("focus",function() {
		!this.value && $(this).autocomplete("search");
	}).unbind("blur").bind("blur",function() {
		$(this).autocomplete("close");
	});

	//前台验证
	$("#addForm").validate({
        rules:{
			product_name:{
				required:true,
				maxlength:64
			},
			model_name:{
				maxlength:32
			},
			quantity:{
				required:true,
				digits:true,
				maxlength:2,
				min:1
			},
			unit_text:{
				maxlength:3
			},
			unit_price:{
				required:true,
				number:true,
				range:[0.01,9999.99]
			},
			comment:{
				maxlength:256
			},
			nesssary_reason:{
				maxlength:256
			}
        }
    });

	let now = new Date();
	//周几
	let weekday = now.getDay();
	//当前日期是周三/周四时,出提示信息
	if(weekday == 3 || weekday == 4){
		$("#cutOffTip").show();
	} else{
		$("#cutOffTip").hide();
	}

	//申请明细弹框
	let $thisDialg = $("#add_supplies_detail").dialog({
		title : "申请物品",
		width : 1250,
		height : 790,
		resizable : false,
		modal : true,
		show:"blind",
		position:"top",
		buttons : {
			"确认":function(){
				if($("#addForm").valid()){
					let unit_price = $("#add_unit_price").val().trim();
					unit_price = +unit_price;
					if(unit_price > waringUnitPrice) {
						warningConfirm("预定单价高于" + waringUnitPrice +"，请确认是否要创建物品申购明细？",function(){
							doInsert($thisDialg);
						},null);
					} else {
						doInsert($thisDialg);
					}
				}
			},
			"关闭":function(){$(this).dialog('close');}
		}
	});
};

/**
 *
 * @param {*} list 绘制常用采购清单列表
 */
function setSuppliseView(list){
	if(list && list.length > 0) {
		let map = new Map();
		list.forEach((item,index)=>{
			let productName = item.product_name;
			let arr = [];
			if(map.has(productName)) {
				arr = map.get(productName);
				arr.push(item);
				map.set(productName,arr);
			} else {
				arr.push(item);
				map.set(productName,arr);
			}
		});

		//模板
		let template=`<li class="item" data=#{DATA}>
						<div class="grid-container">
							<div>#{img}</div>
							<div>
								<div class="grid-item">
									<div class="title">品名</div>
									<div>#{productionName}</div>
								</div>
								<div class="grid-item">
									<div class="title">规格</div>
									<div class="model-name"><select>#{modelName}<select></div>
								</div>
								<div class="grid-item">
									<div class="title">预定单价</div>
									<div class="price">#{unitPrice}</div>
								</div>
								<div class="grid-item">
									<div class="title">单位</div>
									<div class="unit-text">#{unitText}</div>
								</div>
								<div class="grid-item">
									<div class="title">供应商</div>
									<div class="supplier">#{supplier}</div>
								</div>
								<div class="grid-item add">
									<input type="button" class="ui-button" value="加入申购明细">
								</div>
							</div>
						</div>
					</li>`;
		let content = '';
		for(let [key,arr] of map){
			let li = template;
			li = li.replace("#{productionName}",key);

			let index = 0;
			let options = "";
			for(let itemObj of arr){
				let imgSrc= "images/noimage128x128.gif";
				if(itemObj.photo_uuid) {
					imgSrc = "http://" + document.location.hostname + "/photos/supplies_refer_list/" + itemObj.photo_uuid + "?_s=" + Date.now();
				}
				itemObj["imgSrc"] = imgSrc;

				if(index == 0) {
					li = li.replace("#{DATA}",JSON.stringify(itemObj));
					let img = `<img src=${imgSrc} width="128" height="128" />`;
					li = li.replace("#{img}",img);
					li = li.replace("#{unitPrice}",itemObj.unit_price || '');
					li = li.replace("#{unitText}",itemObj.unit_text || '');
					li = li.replace("#{supplier}",itemObj.supplier || '');
					options += `<option selected value=${JSON.stringify(itemObj)}>${itemObj.model_name || ''}</option>`;
				} else {
					options += `<option value=${JSON.stringify(itemObj)}>${itemObj.model_name || ''}</option>`;
				}
				index++;
			}
			li = li.replace("#{modelName}",options);
			content+=li;
		}

		$("#filterContainer > ul").html(content);
		$("#filterContainer > ul select").change(function() {
			let selectedItem = JSON.parse(this.value);

			let $li = $(this).closest("li");
			$li.attr("data",JSON.stringify(selectedItem));
			$li.find("img").attr("src",selectedItem.imgSrc).hide().slideDown(150);
			$li.find(".price").text(selectedItem.unit_price || '');
			$li.find(".unit-text").text(selectedItem.unit_text || '');
			$li.find(".supplier").text(selectedItem.supplier || '');
		});

		$("#filterContainer > ul input.ui-button").button().click(function() {
			let $this = $(this),
				$li = $this.closest("li"),
				$target = $("#addForm"),
				scrollTop = $(document).scrollTop(),
				fromX = $li.offset().left,
				fromY = $li.offset().top - scrollTop,
				targetX = $target.offset().left + 150,
				targetY = $target.offset().top - scrollTop;

			let $ball = $("#ball");
			$ball.html($li.clone());

			$ball.css({
				"top" : fromY + "px",
				"left" : fromX + "px",
				"width" : $li.width() + "px",
				"height" : $li.height() + "px"
			});
			$ball.animate({
				"top" : targetY + "px",
				"left" : targetX + "px",
				"width" : "0px",
				"height" : "0px"
			},250,"swing",()=>{
				$ball.html("");
				chooseSupplise(JSON.parse($li.attr("data")));
			});
		});
	} else {
		$("#filterContainer > ul").html('<div style="width:190px;margin:100px auto;font-size:20px;">无常用采购清单</div>');
	}
};

/**
 * 新规action
 * @param {*} $thisDialg 弹窗
 */
function doInsert($thisDialg){
	let data = {
		"product_name" : $("#add_product_name").val(),
		"model_name" : $("#add_model_name").val(),
		"unit_price" : $("#add_unit_price").val(),
		"unit_text" : $("#add_unit_text").val(),
		"quantity" : $("#add_quantity").val(),
		"nesssary_reason" : $("#add_nesssary_reason").val(),
		"comments" : $("#add_comment").val()
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
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$thisDialg.dialog('close');
					findit();
				}
			}catch(e){}
		}
	});
}

/**
 * 筛选常用采购清单
 */
function filterSupplise(){
	let productName = $("#filter_product_name").val();
	if(!productName) return;

	$("#filterContainer > ul li").each((index,item)=>{
		let $li = $(item);
		let suppliesObj = JSON.parse($li.attr("data"));
		suppliesObj.product_name.indexOf(productName) >= 0 ? $li.fadeIn() : $li.fadeOut();
	});
};

/**
 * 选择常用采购清单到明细
 */
function chooseSupplise(suppliesObj){
	$("#add_product_name").val(suppliesObj.product_name);
	$("#add_model_name").val(suppliesObj.model_name || '');
	$("#add_unit_text").val(suppliesObj.unit_text || '');
	$("#add_unit_price").val(suppliesObj.unit_price || '');
	$("#add_comment").val(suppliesObj.supplier || '');
};
/**
 * 编辑订购单
 */
function editSupplise(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchComfirmAndNoOrderKey',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let listdata = resInfo.list;
					showEditSuppliseDialog(listdata);
				}
			}catch(e){}
		}
	});
}

function showEditSuppliseDialog(listdata){
	let now = new Date();
	$("#order_no_prefix").text(now.getFullYear());
	$("#add_order_no").val("").removeClass("errorarea-single");

	if ($("#gbox_edit_list").length > 0) {
		$("#edit_list").jqGrid().clearGridData();
		$("#edit_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#edit_list").jqGrid({
			data:listdata,
			height: 231,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','品名','规格','数量','预定单价','单位','总价','申购人员','申购日期','上级确认者','供应商','用途',],
			colModel:[
			    {name:'supplies_key',index:'supplies_key', hidden : true},
			    {name:'product_name',index:'product_name', width : 80},
				{name:'model_name',index:'model_name',width : 60},
				{name:'quantity',index:'quantity',width : 30,align:'right',sorttype:'integer'},
				{name:'unit_price',index:'unit_price',width : 40,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}},
				{name:'unit_text',index:'unit_text',width : 30},
				{name:'total_price',index:'total_price',width : 40,align:'right',formatter:function(value, options, rData) {
					if(rData.quantity && rData.unit_price){
						return parseInt(rData.quantity) * parseFloat(rData.unit_price);
					} else {
						return "-";
					}
				}},
				{name:'applicator_name',index:'applicator_name',width : 40},
				{name:'applicate_date',index:'applicate_date',width : 50,align:'center',sorttype:'date'},
				{name:'confirmer_name',index:'confirmer_name',width : 40},
				{name:'supplier',index:'supplier',width : 60},
				{name:'nesssary_reason',index:'nesssary_reason',width : 100}
			],
			rowNum: 100,
			toppager : false,
			pager : "#edit_listpager",
			viewrecords : true,
			rownumbers : true,
			multiselect : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : null,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
				let $jthis = $("#edit_list");
				let dataIds = $jthis.getDataIDs();
				let length = dataIds.length;

				let wednesday = getDayOfWeek(new Date(),3);
				let thursday = getDayOfWeek(new Date(),4);

				for (let i = 0; i < length; i++) {
					let rowdata = $jthis.jqGrid('getRowData', dataIds[i]);
					let applicate_date = rowdata["applicate_date"];

					//申请日期是本周三本周四的默认不勾选，其他默认选上
					if(applicate_date != wednesday && applicate_date != thursday){
						$jthis.jqGrid("setSelection", dataIds[i]);
					}
				}
			}
		});
	}

	$("#addOrderForm").validate({
        rules:{
			order_no:{
				required:true,
				maxlength:3
			}
        }
    });

	let $thisDialg = $("#edit_supplise").dialog({
		title : "编辑订购单（一般物品申购单）",
		width : 1020,
		height : 460,
		resizable : false,
		modal : true,
		show:"blind",
		buttons : {
			"确认":function(){
				if($("#addOrderForm").valid()) {
					let selectedIds = $('#edit_list').jqGrid('getGridParam','selarrrow');
					if(selectedIds.length == 0) {
						errorPop("请至少选中一条申购明细。");
						return;
					}

					let data = {
						"order_no" : "RC-FY" + now.getFullYear() + "-" + $("#add_order_no").val()
					}
					
					let rowsData = [];
					for (let i = 0; i < selectedIds.length; i++) {
						let rowData = $("#edit_list").getRowData(selectedIds[i]);
						data["keys.supplies_key[" + i +"]"] = rowData.supplies_key;
						rowsData.push(rowData);
					}
					let exists = rowsData.some(item => item.supplier.includes("慧采"));
					if(exists){
						data["spec"] = "1";
					} else {
						data["spec"] = "0";
					}

					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : 'supplies_order.do?method=doInsert',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							let resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									$thisDialg.dialog('close');
									findit();
								}
							}catch(e){}
						}
					});
				}
			},
			"关闭":function(){$(this).dialog('close');}
		}
	});
}

/**
 * 获取当前周指定星期几
 * @param {*} date 日期 Date
 * @param {*} week 周几(1~7)星期天使用7
 * @returns 返回yyyy/mm/dd形式的日期
 */
function getDayOfWeek(date,week) {
	let weekday = date.getDay() || 7; //获取星期几,getDay()返回值是 0（周日） 到 6（周六） 之间的一个整数。0||7为7，即weekday的值为1-7
	date.setDate(date.getDate() - weekday + week);//往前算（weekday-1）天，年份、月份会自动变化

	let year = date.getFullYear(); //年
	let month = date.getMonth() + 1; //月
	let d = date.getDate(); //日

	return year + "/" + fillZero(month) + "/" + fillZero(d);
}

/**
 * 收货
 */
function recept() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchWaittingRecept',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let listdata = resInfo.list;
					showReceptDialog(listdata);
				}
			}catch(e){}
		}
	});
};

function showReceptDialog(listdata){
	if ($("#gbox_recept_list").length > 0) {
		$("#recept_list").jqGrid().clearGridData();
		$("#recept_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#recept_list").jqGrid({
			data:listdata,
			height: 231,
			width: 1100,
			rowheight: 23,
			datatype: "local",
			colNames:['','品名','规格','数量','预定单价','单位','总价','申购人员','申购日期','申购单号','上级确认者','供应商','用途'],
			colModel:[
			    {name:'supplies_key',index:'supplies_key', hidden : true},
			    {name:'product_name',index:'product_name', width : 80},
				{name:'model_name',index:'model_name',width : 60},
				{name:'quantity',index:'quantity',width : 30,align:'right',sorttype:'integer'},
				{name:'unit_price',index:'unit_price',width : 40,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}},
				{name:'unit_text',index:'unit_text',width : 30},
				{name:'total_price',index:'total_price',width : 40,align:'right',formatter:function(value, options, rData) {
					if(rData.quantity && rData.unit_price){
						return parseInt(rData.quantity) * parseFloat(rData.unit_price);
					} else {
						return "-";
					}
				}},
				{name:'applicator_name',index:'applicator_name',width : 40},
				{name:'applicate_date',index:'applicate_date',width : 50,align:'center',sorttype:'date'},
				{name:'order_no',index:'order_no',width : 60},
				{name:'confirmer_name',index:'confirmer_name',width : 50},
				{name:'supplier',index:'supplier',width : 60},
				{name:'nesssary_reason',index:'nesssary_reason',width : 100}
			],
			rowNum: 100,
			toppager : false,
			pager : "#recept_listpager",
			viewrecords : true,
			rownumbers : true,
			multiselect : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : null,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
			}
		});
	}

	let $thisDialg = $("#edit_recept").dialog({
		title : "待收货物品申购明细",
		width : 'auto',
		height : 400,
		resizable : false,
		modal : true,
		show:"blind",
		buttons : {
			"确认":function(){
				let selectedIds = $('#recept_list').jqGrid('getGridParam','selarrrow');
				if(selectedIds.length == 0) {
					errorPop("请至少选中一条申购明细。");
					return;
				}
				let data = {};
				for (let i = 0; i < selectedIds.length; i++) {
					let rowData = $("#recept_list").getRowData(selectedIds[i]);
					data["keys.supplies_key[" + i +"]"] = rowData.supplies_key;
				}

				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url :  servicePath + '?method=doRecept',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj, textStatus) {
						let resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages(null, resInfo.errors);
							} else {
								$thisDialg.dialog('close');
								findit();
							}
						}catch(e){}
					}
				});
			},
			"关闭":function(){$(this).dialog('close');}
		}
	});
}

/**
 *  @param {是否弹窗} showDialog
 * 确认订购单
 */
function confirmOrder(showDialog) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'supplies_order.do?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let listdata = resInfo.list;
					orderList(listdata);

					if(showDialog){
						$("#confirm_order").dialog({
							title : "物品申购单一览",
							width : 'auto',
							resizable : false,
							modal : true,
							show:"blind",
							buttons : {
								"关闭":function(){$(this).dialog('close');}
							}
						});
					}
				}
			}catch(e){}
		}
	});
};

function orderList(listdata) {
	if ($("#gbox_order_list").length > 0) {
		$("#order_list").jqGrid().clearGridData();
		$("#order_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#order_list").jqGrid({
			data:listdata,
			height: 461,
			width: 700,
			rowheight: 23,
			datatype: "local",
			colNames:['','申购单号','提交日期','申购人员','批准经理','批准部长','文件名'],
			colModel:[
			    {name:'order_key',index:'order_key', hidden : true},
			    {name:'order_no',index:'order_no', width : 25, align:'center'},
				{name:'order_date',index:'order_date',width : 20,align:'center',sorttype:'date'},
				{name:'operator_name',index:'operator_name',width : 20},
				{name:'manager_name',index:'manager_name',width : 20},
				{name:'minister_name',index:'minister_name',width : 20},
				{name:'file_names',index:'file_names',width :70,formatter:function(value, options, rData) {
					if(value && value.length > 0) {
						let content = "";
						for(let fileName of value) {
							if(content){
								content += "<br>";
							}
							content += `<a href='javascript:downPdf("${rData.order_key}","${fileName}");'>${fileName}</a>`;
						}
						return content;
					} else {
						return "";
					}
				}}
			],
			rowNum: 20,
			toppager : false,
			pager : "#order_listpager",
			viewrecords : true,
			rownumbers : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : showOrderDetail,
			viewsortcols : [true,'vertical',true],
			gridComplete:null
		});
	}
};

/**
 * @param {申购单KEY} orderKey
 * @param {文件名称} fileName
 */
function downPdf(orderKey,fileName){
	if (fileName.indexOf(" ") >= 0) {
		fileName = fileName.replace(" ", "%20");
	}

	if ($("iframe").length > 0) {
		$("iframe").attr("src","supplies_order.do?method=output&orderKey=" + orderKey + "&fileName=" + fileName);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "supplies_order.do?method=output&orderKey=" + orderKey + "&fileName=" + fileName;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
};

function showOrderDetail(){
	let rowid = $("#order_list").jqGrid("getGridParam","selrow");//得到选中的行ID
    let rowData = $("#order_list").getRowData(rowid);

	let data = {
		"order_key" : rowData.order_key
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'supplies_order.do?method=getDetail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					showOrderConfirmDialog(resInfo);
				}
			}catch(e){}
		}
	});
};

function showOrderConfirmDialog(resInfo) {
	let order = resInfo.order;
	let listdata = resInfo.orderDetails;

	//经理印
	if(order.sign_manager_id){//已盖章
		$("#confirm_sign_manager_id").hide();
		let imgUrl = "http://" + document.location.hostname + "/images/sign/" + order.manager_job_no + "?_s=" + new Date().getTime();
		$("#confirm_sign_manager_id_pic").attr("src", imgUrl).show()
	} else {
		$("#confirm_sign_manager_id").show();
		$("#confirm_sign_manager_id_pic").hide();
	}

	//部长印
	if(order.sign_minister_id){//已盖章
		$("#confirm_sign_minister_id").hide();
		let imgUrl = "http://" + document.location.hostname + "/images/sign/" + order.minister_job_no + "?_s=" + new Date().getTime();
		$("#confirm_sign_minister_id_pic").attr("src", imgUrl).show()
	} else {
		$("#confirm_sign_minister_id").show();
		$("#confirm_sign_minister_id_pic").hide();
	}

	if ($("#gbox_order_detail_list").length > 0) {
		$("#order_detail_list").jqGrid().clearGridData();
		$("#order_detail_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else  {
		$("#order_detail_list").jqGrid({
			data:listdata,
			height: 461,
			width: 1200,
			rowheight: 23,
			datatype: "local",
			colNames:['品名','规格','数量','预定<br>单价','单位','总价','供应商','用途','申购<br>人员','申购日期','上级<br>确认者','预计到货<br>日期','收货<br>日期','预算月','验收<br>日期','发票号码'],
			colModel:[
			     {name:'product_name',index:'product_name', width : 80},
				 {name:'model_name',index:'model_name',width : 60},
				 {name:'quantity',index:'quantity',width : 30,align:'right',sorttype:'integer'},
				 {name:'unit_price',index:'unit_price',width : 40,align:'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator:',',defaultValue: ''}},
				 {name:'unit_text',index:'unit_text',width : 30},
				 {name:'total_price',index:'total_price',width : 40,align:'right',sorttype:'currency',formatter:function(value, options, rData) {
					 if(rData.quantity && rData.unit_price){
						 return parseInt(rData.quantity) * parseFloat(rData.unit_price);
					 } else {
						 return "-";
					 }
				 }},
				{name:'supplier',index:'supplier',width : 60},
				{name:'nesssary_reason',index:'nesssary_reason',width : 100},
				{name:'applicator_name',index:'applicator_name',width : 40},
				{name:'applicate_date',index:'applicate_date',width : 60,align:'center',sorttype:'date'},
				{name:'confirmer_name',index:'confirmer_name',width : 40},
				{name:'scheduled_date',index:'scheduled_date',width : 60,sorttype:'date'},
				{name:'recept_date',index:'recept_date',width : 60,sorttype:'date'},
				{name:'budget_month',index:'budget_month',width : 40,},
				{name:'inline_recept_date',index:'inline_recept_date',width : 60,sorttype:'date'},
				{name:'invoice_no',index:'invoice_no',width : 60}
			],
			rowNum: 40,
			toppager : false,
			pager : "#order_detail_listpager",
			viewrecords : true,
			rownumbers : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : null,
			onSelectRow : null,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){}
		});
	}

	$("#confirm_sign_manager_id").unbind("click").bind("click",function(){
		let data = {
			"order_key" : order.order_key,
			"confirm_flg" : "1"
		}
		doOrderConfirm(data);
	});

	$("#confirm_sign_minister_id").unbind("click").bind("click",function(){
		let data = {
			"order_key" : order.order_key,
			"confirm_flg" : "2"
		}
		doOrderConfirm(data);
	});

	$("#sign_order").dialog({
		title : "物品申购单确认",
		width : 'auto',
		resizable : false,
		modal : true,
		close : function() {
			if($("#signEdit").val() === "true") {
				confirmOrder(false);
			}
		},
		buttons : {
			"关闭" : function() {
				$(this).dialog('close');
			}
		}
	});
};

function doOrderConfirm(data) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'supplies_order.do?method=doSign',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			let resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					let today = new Date();
					let imgUrl = "http://" + document.location.hostname + "/images/sign/" +  $("#jobNo").val() + "?_s=" + today.getTime();
					if(data.confirm_flg == 1) {
						$("#confirm_sign_manager_id").hide();
						$("#confirm_sign_manager_id_pic").attr("src",imgUrl).show();
					} else {
						$("#confirm_sign_minister_id").hide();
						$("#confirm_sign_minister_id_pic").attr("src", imgUrl).show();
					}
				}
			}catch(e){}
		}
	});
};