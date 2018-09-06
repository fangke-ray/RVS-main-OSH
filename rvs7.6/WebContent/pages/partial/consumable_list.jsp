<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

<script type="text/javascript" src="js/partial/consumable_list.js"></script>
<% 
	String role = (String) request.getAttribute("role");
	boolean isFact = ("fact").equals(role);
%>

<title>消耗品仓库库存一览</title>
</head>

<body class="outer" style="align: center;">	
	<input type="hidden" id="hidden_isFact" value='<%=isFact%>'>
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
					<jsp:param name="linkto" value="消耗品管理"/>
				</jsp:include>
			</div>

<!-- 本体  -->
<div style="width: 1012px; float: left;">
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
								<td class="ui-state-default td-title">消耗品</td>
								<td class="td-content">
									<input name="code" id="search_code" alt="消耗品" class="ui-widget-content" type="text">
								</td>
								<td class="ui-state-default td-title">消耗品分类</td>
								<td class="td-content">
									<select id="search_type" name="type" alt="消耗品分类" class="ui-select2buttons">${Options}</select>
									<input type="hidden" id="res_type" value="${gqOptions}"/>
								</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="td-content"><input class="ui-widget-content" id="search_stock_code" name="stock_code" alt="零件编码" type="text">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">库存不足</td>
								<td class="td-content">
									<div id="leak_set" class="ui-buttonset">
										<input type="radio" name="leak" id="leak_a" class="ui-widget-content" value="" checked="checked"><label for="leak_a">(全)</label>
										<input type="radio" name="leak" id="leak_t" class="ui-widget-content" value="1"><label for="leak_t">不足</label>
										<input type="hidden" id="search_leak">
									</div>
								</td>
								<td class="ui-state-default td-title">计算时期</td>
								<td class="td-content">
									<input type="text" id="search_count_period_start" class="ui-widget-content" readonly="readonly" value="${first}">起<br>
									<input type="text" id="search_count_period_end" class="ui-widget-content" readonly="readonly" value="">止
								</td>
								<td class="ui-state-default td-title">消耗率警报线</td>
								<td class="td-content">
									过低线：<input type="text" id="filter_l_low" class="ui-widget-content" value="${cost_rate_alram_belowline}" style="width:5em;text-align:right;padding-right:1em;"> %
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


		<!-- JqGrid表格  -->
		<div id="listarea" class="width-middleright">

		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">消耗品仓库库存一览</span>
		</div>
		<table id="consumable_list">
		</table>
		<div id="consumable_list_pager"></div>
		<div id="confirmmessage"></div>
		<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
			<div id="executes" style="margin-left: 4px; margin-top: 2px;">
				<% if (isFact) { %>
				<input id="add_button" class="ui-button" value="加入库存设置" role="button" type="button">
				<input id="edit_button" class="ui-button" value="修改库存设置" role="button" type="button">
				<input id="remove_button" class="ui-button" value="移出消耗品库存" role="button" type="button">
				<!--input id="image_load_button" class="ui-button" value="消耗品照片上传" role="button" type="button"-->
				<input id="adjust_button" class="ui-button" value="盘点" role="button" type="button">
				<input id="measuring_set_button" class="ui-button" value="消耗品计量单位设置" role="button" type="button">
				<input id="post_clipboard_button" class="ui-button" value="报表导出到剪贴板" role="button" type="button" style="float:right;">
				<input id="download_button" class="ui-button" value="导出" role="button" type="button" style="float:right;">
				<% } %>
				<div class="clear"></div>
			</div>
		</div>
		</div>
	</div>
	<div class="clear"/>
</div>


</div>


<!-- 弹出信息  -->
<div id="pop_window" style="display: none"></div>

			<div id="pop_windows" style="display: none">
			<div id="pop_window_new">
				<form id="abandon_insert">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">消耗品代码</td>
							<td class="td-content" id="code_text">
								<input type="hidden" name="partial_id" id="new_partial_id" alt="零件ID"></input>
								<input type="hidden" name="delete_flg" id="delete_flg"></input>
								<input type="text" name="code" id="add_code" alt="消耗品代码"></input>
							</td>
							<td class="ui-state-default td-title">分类</td>
							<td class="td-content">
									<select id="add_type" name="type" alt="消耗品分类" class="ui-select2buttons">${Options}</select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">基准库存</td>
							<td class="td-content">
								<input type="text" name="benchmark" id="add_benchmark" alt="基准库存"></input>
							</td>
							<td class="ui-state-default td-title">安全库存</td>
							<td class="td-content">
								<input type="text" name="safety_lever" id="add_safety_lever" alt="安全库存"></input>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">补充周期</td>
							<td class="td-content">
									<select id="add_supply_cycle" name="type" alt="补充周期" class="ui-select2buttons">${sSupplyCycleOptions}</select>
							</td>
							<td class="ui-state-default td-title">补充日</td>
							<td class="td-content">
									<select id="add_supply_day" name="type" alt="补充日" class="ui-select2buttons">${sSupplyDayOptions}</select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">常用品</td>
							<td class="td-content">
								<div id="add_popular_item_set" class="ui-buttonset" alt="常用品">
									<input type="radio" name="popular_item" id="add_popular_item_n" class="ui-widget-content" value="0" checked="checked"><label for="add_popular_item_n" radio>(非常用)</label>
									<input type="radio" name="popular_item" id="add_popular_item_y" class="ui-widget-content" value="1"><label for="add_popular_item_y" radio>常用</label>
								</div>
							</td>
							<td class="ui-state-default td-title">库位编号</td>
							<td class="td-content">
								<input type="text" name="stock_code" id="add_stock_code" alt="库位编号"></input>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">目标值</td>
							<td class="td-content" colspan="3">
								<input type="text" name="consumpt_quota" id="add_consumpt_quota" alt="目标值"></input>
								每产出一台维修对象，使用消耗品数量。
							</td>
						</tr>
					</tbody>
				</table>
				</form>
			</div>

			<div id="pop_window_edit" style="display: none">
				<form id="abandon_modify">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">消耗品代码</td>
							<td class="td-content">
								<label name="model_name" id="label_modelname"></label>
								<input type="hidden" id="edit_partial_id"/>
							</td>
							<td class="ui-state-default td-title">分类</td>
							<td class="td-content">
									<select id="edit_type" name="type" alt="消耗品分类" class="ui-select2buttons">${Options}</select>
							</td>
						</tr>
						<tr>

							<td class="ui-state-default td-title">基准库存</td>
							<td class="td-content">
								<input type="text" alt="基准库存" name="benchmark" id="edit_benchmark" class="ui-widget-content"></input>
							</td>
							<td class="ui-state-default td-title">安全库存</td>
							<td class="td-content">
								<input type="text" alt="安全库存" name="safety_lever" id="edit_safety_lever" class="ui-widget-content"></input>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">补充周期</td>
							<td class="td-content">
									<select id="edit_supply_cycle" name="supply_cycle" alt="补充周期" class="ui-select2buttons">${sSupplyCycleOptions}</select>
							</td>
							<td class="ui-state-default td-title">补充日</td>
							<td class="td-content">
									<select id="edit_supply_day" name="supply_day" alt="补充日" class="ui-select2buttons">${sSupplyDayOptions}</select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">常用品</td>
							<td class="td-content">
								<div id="edit_popular_item_set" class="ui-buttonset" alt="常用品">
									<input type="radio" name="popular_item" id="edit_popular_item_n" class="ui-widget-content" value="0"><label for="edit_popular_item_n">(非常用)</label>
									<input type="radio" name="popular_item" id="edit_popular_item_y" class="ui-widget-content" value="1"><label for="edit_popular_item_y">常用</label>
								</div>
							</td>
							<td class="ui-state-default td-title">库位编号</td>
							<td class="td-content">
								<input type="text" alt="库位编号" name="stock_code" id="edit_stock_code" class="ui-widget-content"></input>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">目标值</td>
							<td class="td-content" colspan="3">
								<input type="text" name="consumpt_quota" id="edit_consumpt_quota" alt="目标值"></input>
								每产出一台维修对象，使用消耗品数量。
							</td>
						</tr>
					</tbody>
				</table>
				</form>
			</div>

			<div id="pop_window_adjust">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">消耗品代码</td>
							<td class="td-content" colspan="3">
								<input type="text" name="code" id="adjust_code" alt="消耗品代码"></input>
								<input type="hidden" id="adjust_partial_id"/>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">当前有效库存</td>
							<td class="td-content">
								<label id="current_available_inventory"></label>
							</td>
							<td class="ui-state-default td-title">当前补充在途量</td>
							<td class="td-content">
								<label id="current_on_passage"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修正有效库存</td>
							<td class="td-content">
								<input type="text" alt="修正有效库存" name="available_inventory" id="adjust_available_inventory"></input>
							</td>
							<td class="ui-state-default td-title">修正补充在途量</td>
							<td class="td-content">
								<input type="text" alt="修正补充在途量" name="on_passage" id="adjust_on_passage"></input>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修正理由</td>
							<td class="td-content" colspan="3">
								<textarea name="reason" id="adjust_reason" alt="修正理由" style="width:80%;height:5em;"></textarea>
							</td>
						</tr>
						<tr>
							<td class="td-content" colspan="4">
								<table id="reason_list" style="width: 100%;border-collapse: collapse;cursor: pointer;">
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div id="pop_window_set" style="display: none">
				<form id="measuring_set">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">消耗品代码</td>
							<td class="td-content">
								<label name="code" id="label_code"></label>
								<input type="hidden" id="set_partial_id"/>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">单位名称</td>
							<td class="td-content">
								<input type="text" alt="单位名称" name="unit_name" id="set_unit_name"></input>
							</td>
						</tr>
						<tr class="content">
							<td class="ui-state-default td-title">内容量</td>
							<td class="td-content">
								<input type='text' alt="内容量" name="content" id="set_content" ></input>
								<input type="checkbox" id="packing"></input><label>分装</label>
							</td>
						</tr>
					</tbody>
				</table>
				</form>
			</div>
</div>

			<div class="clear"></div>
		</div>
	<div class="clear"></div>


	<div id="footarea"></div>

</body>
</html>