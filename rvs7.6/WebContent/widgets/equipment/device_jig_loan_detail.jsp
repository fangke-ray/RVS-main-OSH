<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>

	<div id="material_detail_paticalarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">设备工具清洗记录</span>
		</div>
	</div>

	<table id="material_detail_dj_loan_list"></table>
	<div id="material_detail_dj_loan_pager"></div>

<script type="text/javascript">
	var material_detail_dj_loan_listdata = ${listdata};
	// 构建jqGrid对象
	$("#material_detail_dj_loan_list").jqGrid({
		data : material_detail_dj_loan_listdata,
		height : 461,
		width : 786,
		rowheight : 23,
		datatype : "local",
		colNames : ['分类', '品名', '管理编号', '使用工位', '借用人', '开始借用时间', '清洗返还时间'  ],
		colModel : [{name : 'object_type', index : 'object_type', width : 35, align : 'center', formatter: 'select', editoptions: {value:"1:设备工具;2:治具"}
				}, {
					name : 'type_name', index : 'type_name', width : 75, align : 'left'
				}, {
					name : 'manage_code', index : 'manage_code', width : 40, align : 'left'
				}, {
					name : 'process_code', index : 'process_code', width : 35, align : 'center'
				}, {
					name : 'operator_name', index : 'operator_name', width : 35, align : 'left'
				}, {
					name : 'on_loan_time', index : 'on_loan_time', width : 50, align : 'center', sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d H:i'}
				}, {
					name : 'revent_time', index : 'revent_time', width : 50, align : 'center', sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d H:i'}
				}],
		toppager : false,
		rowNum : 20,
		pager : "#material_detail_dj_loan_pager",
		viewrecords : true,
		caption : "借用设备工具一览",
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true]
	});
</script>