<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/standard_partial_addition.js"></script>

	<!-- 本体 -->
	<div style="width: 994px; float: left;">
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
								<td class="ui-state-default td-title">零件代码</td>
								<td class="td-content">
									<input name="partial_code" id="search_partial_code" alt="零件代码" class="ui-widget-content" type="text">
								</td>
								<td class="ui-state-default td-title">所属工位</td>
								<td class="td-content">
									<input name="position_name" id="search_position_name" alt="所属工位" class="ui-widget-content" type="text">
									<input type="hidden" id="search_position_id" name="position_id" />
								</td>
								<td class="ui-state-default td-title">关联型号</td>
								<td class="td-content">
									<input name="model_name" id="search_model_name" alt="关联型号" class="ui-widget-content" type="text">
									<input type="hidden" id="search_model_id" name="model_id" />
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
				<span class="areatitle">补正标准工时的更换零件一览</span>
			</div>
			<table id="list">
			</table>
			<div id="list_pager"></div>
			<div id="confirmmessage"></div>
			<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
				<div id="executes" style="margin-left: 4px; margin-top: 2px;">
					<input id="edit_by_position_button" class="ui-button" value="按工位设置" role="button" type="button">
					<input id="edit_by_model_button" class="ui-button" value="按型号设置" role="button" type="button">
					<div class="clear"></div>
				</div>
			</div>
			</div>
		</div>
		<div class="clear"/>
	</div>


<style>
#pop_window_edit > .wrapof {
	width: 100%;
	height: 540px;
	overflow: auto;
}
#pop_window_edit > .wrapof > table {
	table-layout: fixed;
	width: 60em;
	border: 1px solid #93C3CD;
}
#pop_window_edit > .wrapof > table > tbody td {
	text-align: center;
}
#pop_window_edit > .wrapof > table > tbody td:nth-child(2) {
	text-align: left;
}
#pop_window_edit > .wrapof > table > tbody > tr > td:nth-child(n+3):hover {
	background-color : lightyellow;
	cursor: pointer;
}
#pop_window_edit > .wrapof > table > tbody > tr > td.ui-widget-header:nth-child(n+3):hover {
	background-color : revert;
	cursor: revert;
}
#pop_window_edit > .wrapof > table > tbody td[upd_stat] {
	background-color : yellow;
}
#pop_window_edit > .wrapof > table > thead tr th {
	position:sticky;
	top:0;
}
#pop_window_edit > .wrapof > table th:first-child {
	z-index:2;
}
</style>

<div class="clear"></div>
<!-- 弹出信息  -->
<div id="pop_windows" style="display: none">
		<div id="pop_choose_position" style="display: none">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">工位</td>
						</td>
						<td class="td-content">
							<input type="text" name="process_code" id="edit_process_code" style="width: 200px;" readonly></label>
							<input type="hidden" id="edit_position_id"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div id="pop_choose_model" style="display: none">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">型号</td>
						</td>
						<td class="td-content">
							<input type="text" name="model_name" id="edit_model_name" style="width: 200px;" readonly></label>
							<input type="hidden" id="edit_model_id"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div id="pop_window_edit" style="display: none">
		<div class="wrapof">
			<table class="condform" cellspacing=0>
				<thead>
					<th class="ui-widget-header">零件代码</th><th class="ui-widget-header" style="width: 30em;">零件名</th><th class="ui-widget-header">任意工位补正值</th><th class="ui-widget-header"><span class='process_code'></span>工位补正值</th>
				</thead>
				<tbody>
				</tbody>
			</table>
			</form>
		</div>
		</div>

		<div id="pop_value" style="display: none">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title"></td>
						</td>
						<td class="td-content">
							<input type="text" value=""></label>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

</div>


	<div class="referchooser ui-widget-content" id="pReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${pReferChooser}</table>
	</div>

	<div class="referchooser ui-widget-content" id="mReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${mReferChooser}</table>
	</div>
