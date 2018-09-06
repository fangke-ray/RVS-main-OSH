$(function() {
	
	init();
	
	$("#reason_list").on("click", "td" , function(){
		$("#adjust_reason").val(this.innerText);
	});

	/*Tab切换*/
	$("#infoes input:radio").click(function(){
		showList(this.value);
		$(".record_page").hide();
		$("#" + this.value).show();
	});

	orderAction();
	
	supplyAction();
	
	applyAction();
	
	inventoryAction();
	
});

/**
 * 初始话页面元素
 */
function init(){
	$("input.ui-button").button();
	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
	$("span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("div.ui-buttonset").buttonset();
	$("#search_section_id,#search_line_id").select2Buttons();

	$("#search_order_date_start,#search_order_date_end,"
		+ "#search_apply_time_start,#search_apply_time_end,"
		+ "#search_supply_date_start,#search_supply_date_end,"
		+ "#search_adjust_date_start,#search_adjust_date_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	var search_page = $("#hidden_page").val();
	
	if(search_page){
		showList(search_page);
		$(".record_page").hide();
		$("#" + search_page).show();
		$("#infoes input[value='" + search_page + "']").attr("checked","checked").trigger("change");
	}else{
		showList("");
		$(".record_page").hide();
		$("#page_apply").show();
		$("#page_apply_tab").attr("checked","checked").trigger("change");// 选中申请入库记录
	}
};

function showList(search_page) {
	if (search_page == "page_order") {
		page_order.findit();
	} else if (search_page == "page_supply") {
		page_supply.findit();
	} else if (search_page == "" || search_page == "page_apply") {
		page_apply.findit();
	} else if (search_page == "page_inventory") {
		page_inventory.findit();
	}
};


/**
 * 订购操作
 */
function orderAction(){
	/*订购记录一览查询*/
	$("#search_o_button").click(function() {
		page_order.findit();
		$("#output_export_button,#remove_o_button").disable();
	});
	
	/*清除*/
	$("#reset_o_button").click(function() {
		page_order.reset();
	});
	
	// 建立订购单
	$("#add_o_button").click(page_order.showOrderAdd);
	
	// 删除订购单
	$("#remove_o_button").click(page_order.showDelete);
	
	//导出订购单
	$("#output_export_button").click(page_order.output_export);
	
	$("#remove_o_button,#output_export_button").disable();
	
	
};

/**
 * 入库操作
 */
function supplyAction(){
	/*入库记录一览查询*/
	$("#search_s_button").click(function() {
		page_supply.findit();
	});
	
	$("#reset_s_button").click(function() {
		page_supply.reset();
	});

	// 入库
	$("#add_s_button").click(page_supply.showSupplyAdd);
};

/**
 * 申请领用操作
 */
function applyAction(){
	/*申请领用记录一览查询*/
	$("#search_a_button").click(function() {
		page_apply.findit();
	});
	
	$("#reset_a_button").click(function() {
		page_apply.reset();
	});
};

/**
 * 盘点记录操作
 */
function inventoryAction(){
	/*盘点记录一览查询*/
	$("#search_i_button").click(function() {
		page_inventory.findit();
	});

	$("#reset_i_button").click(function() {
		page_inventory.reset();
	});
};