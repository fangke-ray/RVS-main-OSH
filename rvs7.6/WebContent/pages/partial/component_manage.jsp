<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- base href="http://localhost/rvs/" -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/lte-style.css">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<style>
tr.addseqTr td {
	background-color:gray;
}
tr.addseqTr select,
tr.addseqTr input {
	background-color:lightgray;
}
#pop_target_materials_table tbody tr td {
	cursor:pointer;
}
#pop_target_materials_table tbody tr td:nth-child(3) {
	text-align:center;
}
#pop_target_materials_table tbody tr td:nth-child(4) {
	text-align:right;
}
#pop_target_materials_table tr:hover td {
	background-color:#f8bb14;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>

<script type="text/javascript" src="js/partial/component_manage.js"></script>
<% 
	String role = (String) request.getAttribute("role");
	boolean isFact = ("fact").equals(role);
	boolean is105 = (Boolean) request.getAttribute("is105");
	boolean is107 = (Boolean) request.getAttribute("is107");
%>

<title>NS组件库存管理</title>
</head>

<body class="outer" style="align: center;">	
	<input type="hidden" id="hidden_isFact" value='<%=isFact%>'>
	<input type="hidden" id="hidden_is105" value='<%=is105%>'>
	<input type="hidden" id="hidden_is107" value='<%=is107%>'>
	<div id="update_limit_date_after"></div>
	<div class="width-full"
		style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
			<div class="clear" style="height: 10px;"></div>

			<div class="hidemessage" id="hidemessage">
				<div id="messagecontainner" style="float: left; display: none;">
					<div id="messagearea">
						<div
							class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-right">
							<span class="areatitle">消息一览</span>
						</div>
						<div class="ui-state-default dwidth-right" id="message_contents">
							<span>没有未处理的警告。</span>
						</div>
						<div class="clear"></div>
					</div>
				</div>
			</div>
			<div id="process_resign">
				<div class="hidemenu" id="hidemenu">
					<div id="menucontainner" style="float: right; display: none;">
					</div>
				</div>
			
			</div>

		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="消耗品/组件管理"/>
				</jsp:include>
			</div>
			<!-- 本体  -->
			<div style="width: 1012px; float: left;">
			
				<!-- JqGrid表格  -->
				<div id="settingListarea" class="width-middleright">
			
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">NS组件库存一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="component_setting">
					</table>
					<div id="component_setting_pager"></div>
					<div id="confirmmessage"></div>
					<% if (isFact) { %>
					<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
						<div id="executes" style="margin-left: 4px; margin-top: 2px;">
							<input id="add_button" class="ui-button" value="新建NS组件机型" role="button" type="button">
							<input id="edit_button" class="ui-button" value="修改NS组件机型" role="button" type="button">
							<input id="delete_button" class="ui-button" value="取消NS组件机型" role="button" type="button">
							<% if (is105) { %>
							<input id="add_manage_button" class="ui-button" value="虚拟单号订购子零件" role="button" type="button" style="float:right;">
							<% } %>
							<div class="clear"></div>
						</div>
					</div>
					<% } %>
				</div>

				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">检索条件</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
										<!-- 检索条件 -->
						<form id="searchform" method="POST" onsubmit="return false;">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">型号</td>
										<td class="td-content" colspan="5">
											<select id="search_model_id" name="model_id" alt="型号" class="ui-select2buttons">${models}</select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">组件代码</td>
										<td class="td-content">
											<input class="ui-widget-content" id="search_component_code" name="component_code" alt="组件代码" type="text">
										</td>
										<td class="ui-state-default td-title">子零件代码</td>
										<td class="td-content">
											<input class="ui-widget-content" id="search_partial_code" name="partial_code" alt="子零件编码" type="text">
										</td>
										<td class="ui-state-default td-title">NS组件序列号</td>
										<td class="td-content">
											<input class="ui-widget-content" id="search_serial_no" name="serial_no" alt="NS组件序列号" type="text">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">库位编号</td>
										<td class="td-content">
											<input class="ui-widget-content" id="search_stock_code" name="stock_code" alt="库位编号" type="text">
										</td>
										<td class="ui-state-default td-title">状态</td>
										<td class="td-content" colspan="3">
											<select id="search_step" name="step" alt="状态" class="ui-select2buttons" multiple>${Steps}</select>
											<input type="hidden" id="res_step" value="${gqOptions}"/>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">投入日期</td>
										<td class="td-content">
											<input type="text" id="search_inline_date_start" class="ui-widget-content" readonly="readonly" value="">起<br>
											<input type="text" id="search_inline_date_end" class="ui-widget-content" readonly="readonly" value="">止
										</td>
										<td class="ui-state-default td-title">组装完成日期</td>
										<td class="td-content">
											<input type="text" id="search_finish_time_start" class="ui-widget-content" readonly="readonly" value="">起<br>
											<input type="text" id="search_finish_time_end" class="ui-widget-content" readonly="readonly" value="">止
										</td>
										<td class="ui-state-default td-title">采用维修品单号</td>
										<td class="td-content">
											<input class="ui-widget-content" id="search_target_omr_notifi_no" name="target_omr_notifi_no" alt="采用维修品单号" type="text">
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height: 44px">
								<input class="ui-button" id="reset_button" value="清除" role="button" aria-disabled="false" style="float: right; right: 2px" type="button">
								<input class="ui-button-primary ui-button" id="search_button" value="查询" role="button" style="float: right; right: 2px" type="button">
							</div>
						</form>
				
					</div>
					<div class="clear areaencloser"></div>
			
					<input type="hidden" id="hide_shelf_cost" value="${jqShelfCost }">
			
					<!-- JqGrid表格  -->
					<div id="listarea" class="width-middleright">
				
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">NS组件库存管理</span>
						</div>
						<table id="component_manage">
						</table>
						<div id="component_manage_pager"></div>
						<div id="confirmmessage"></div>
						<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
							<div id="manage_executes" style="margin-left: 4px; margin-top: 2px;">
								<% if (isFact) { %>
								<input id="partial_instock_button" class="ui-button" value="子零件入库" role="button" type="button">
								<input id="partial_outstock_button" class="ui-button" value="子零件出库" role="button" type="button">
								<input id="partial_move_button" class="ui-button" value="移库" role="button" type="button">
								<input id="component_outstock_button" class="ui-button" value="组件出库" role="button" type="button">
								<input id="cancle_button" class="ui-button" value="废弃" role="button" type="button">
								<% } %>
								<% if (is107) { %>
								<input id="print_info_button" class="ui-button" value="NS组件信息单打印" role="button" type="button" style="float:right;">
								<input id="print_label_button" class="ui-button" value="NS组件标签打印" role="button" type="button" style="float:right;">
								<% } %>
								<div class="clear"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		
			<div class="clear"></div>
		</div>


		<!-- 弹出信息  -->
		<div id="pop_window" style="display: none"></div>
		<div id="pop_windows" style="display: none">
		<div id="pop_window_new">
			<form id="abandon_insert">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<input type="text" id="view_mode_name" name="view_mode_name" alt="型号" class="ui-widget-content" readonly="readonly">
							<input type="hidden" name="mode_id" id="add_model_id" alt="型号" >
							<input type="hidden" name="partial_id" id="new_partial_id" alt="零件ID"></input>
						</td>
						<td class="ui-state-default td-title">组件代码</td>
						<td class="td-content">
							<input type="text" name="code" id="add_code" alt="组件代码"></input>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">识别代号</td>
						<td class="td-content">
							<input type="text" name="identify_code" id="add_identify_code" alt="识别代号"></input>
						</td>
						<td class="ui-state-default td-title">安全库存</td>
						<td class="td-content">
							<input type="text" name="safety_lever" id="add_safety_lever" alt="安全库存"></input>
						</td>
					</tr>
				</tbody>
			</table>
			<table>
				<thead>
					<tr>
						<th class="ui-state-default td-title">零件代码</th>
						<th class="ui-state-default td-title">零件名称</th>
						<th class="ui-state-default td-title">数量</th>
						<th class="ui-state-default td-title">操作</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			</form>
		</div>
		
		<div id="pop_window_edit">
			<form id="abandon_modify">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<label id="label_modelname"></label>
							<input type="hidden" name="mode_id" id="edit_model_id" alt="型号" >
							<input type="hidden" name="partial_id" id="edit_partial_id" alt="零件ID"></input>
						</td>
						<td class="ui-state-default td-title">组件代码</td>
						<td class="td-content">
							<input type="text" name="code" id="edit_code" alt="组件代码"></input>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">识别代号</td>
						<td class="td-content">
							<label id="label_identify_code"></label>
							<input type="hidden" name="identify_code" id="edit_identify_code" alt="识别代号" >
						</td>
						<td class="ui-state-default td-title">安全库存</td>
						<td class="td-content">
							<input type="text" name="safety_lever" id="edit_safety_lever" alt="安全库存"></input>
						</td>
					</tr>
				</tbody>
			</table>
			<table>
				<thead>
					<tr>
						<th class="ui-state-default td-title">零件代码</th>
						<th class="ui-state-default td-title">零件名称</th>
						<th class="ui-state-default td-title">数量</th>
						<th class="ui-state-default td-title">操作</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			</form>
		</div>
		
		<!-- 货架选择画面内容 -->
		<div id="ns_pop"></div>
		<div class="clear"></div>
		</div>
		
	<!-- 型号下拉选择内容 -->
	<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
		<!-- 下拉选择内容 -->
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table class="subform">${mReferChooser}</table>
	</div>
	
	<div class="clear"></div>


	<div id="footarea"></div>

</body>
</html>