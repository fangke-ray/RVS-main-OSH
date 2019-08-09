<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="js/data/list_materials.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>

	<div id="searcharea" class="dwidth-middleright">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">检索条件</span>
			<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>
		<div class="ui-widget-content">
			<form id="searchform" method="POST">
				<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title" rowspan="2">维修对象机种</td>
						<td class="td-content" rowspan="2"><select name="category_id" id="search_category_id" class="ui-widget-content" multiple>${cOptions}</select></td>
						<td class="ui-state-default td-title">维修对象型号</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" readonly="readonly" id="txt_modelname">
							<input type="hidden" name="modelname" id="search_modelname">
						</td>
						<td class="ui-state-default td-title">机身号</td>
						<td class="td-content"><input type="text" id="search_serialno" maxlength="20" class="ui-widget-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">修理单号</td>
						<td class="td-content"><input type="text" id="search_sorcno" maxlength="15" class="ui-widget-content"></td>
						<td class="ui-state-default td-title">维修课室</td>
						<td class="td-content">
							<select name="section_id" id="search_section_id" class="ui-widget-content">${sOptions}</select>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">受理日期</td>
						<td class="td-content"><input type="text" name="reception_time" id="reception_time_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="name" id="reception_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">维修完成日期</td>
						<td class="td-content"><input type="text" name="outline_time_start" id="search_outline_time_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="outline_time_end" id="search_outline_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">纳期</td>
						<td class="td-content"><input type="text" name="name" id="scheduled_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="name" id="scheduled_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">一览范围</td>
						<td class="td-content" id="completed_set">
							<input type="radio" name="completed" id="completed_n" class="ui-widget-content" checked="true" value="1"><label for="completed_n">维修中</label>
							<input type="radio" name="completed" id="completed_y" class="ui-widget-content" value="2"><label for="completed_y">历史</label>
							<input type="radio" name="completed" id="completed_a" class="ui-widget-content" value="0"><label for="completed_a">全部</label>
						</td>
						<td class="ui-state-default td-title">总组出货安排</td>
						<td class="td-content">
							<input type="text" id="search_complete_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
							<input type="text" id="search_complete_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						</td>
						<td class="td-content td-title" colspan="2">
							<input type="button" id="more_condition_button" class="ui-button" value="更多检索条件▼"/>
						</td>
						<td class="ui-state-default td-title" style="display:none;">入库预定日</td>
						<td class="td-content" style="display:none;">
							<input type="text" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
							<input type="text" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
						</td>
					</tr>
					<tr style="display:none;">
						<td class="ui-state-default td-title">ESAS No.</td>
						<td class="td-content"><input type="text" name="esas_no" id="search_esas_no" maxlength="8" class="ui-widget-content"></td>
						<td class="ui-state-default td-title">维修等级</td>
						<td class="td-content">
							<select name="search_level" id="search_level" class="ui-widget-content" multiple>
								${lOptions}
							</select>
						</td>
						<td class="ui-state-default td-title">直送</td>
						<td class="td-content" id="direct_set">
							<input type="radio" name="direct_flg" id="direct_flg_a" class="ui-widget-content" checked="true" value=""><label for="direct_flg_a">全部</label>
							<input type="radio" name="direct_flg" id="direct_flg_n" class="ui-widget-content" value="0"><label for="direct_flg_n">分室</label>
							<input type="radio" name="direct_flg" id="direct_flg_y" class="ui-widget-content" value="1"><label for="direct_flg_y">直送</label>
							<br/>
							<input type="checkbox" name="direct_rapid" id="direct_rapid" class="ui-widget-content" value="1"><label for="direct_rapid">直送快速</label>
						</td>
					</tr>
					<tr style="display:none;">
						<td class="ui-state-default td-title">投线日期</td>
						<td class="td-content"><input type="text" name="inline_time_start" id="inline_time_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="name" id="inline_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">客户同意日</td>
						<td class="td-content"><input type="text" name="agreed_date_start" id="search_agreed_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="agreed_date_end" id="search_agreed_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">委托处</td>
						<td class="td-content"><select name="ocm" id="search_ocm" class="ui-widget-content">${oOptions}</select></td>
					</tr>
					<tr style="display:none;">
						<td class="ui-state-default td-title">零件订购日</td>
						<td class="td-content"><input type="text" name="partial_order_date_start" id="partial_order_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
						<input type="text" name="partial_order_date_end" id="partial_order_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">OCM配送日</td>
						<td class="td-content"><input type="text" name="ocm_shipping_date_start" id="search_ocm_shipping_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
						<input type="text" name="ocm_shipping_date_end" id="search_ocm_shipping_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						<td class="ui-state-default td-title">维修方式</td>
						<td class="td-content"><select name="fix_type" id="search_fix_type" class="ui-widget-content">${ftOptions}</select></td>
					</tr>
				</tbody>
				</table>
				<div style="height:44px">
					<input type="hidden" id="h_today_date" value="${today_date}">
					<input type="hidden" id="h_past_4_date" value="${past_4_date}"">
					<input type="hidden" id="h_lOptions" value="${lGos}">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
			</form>
		</div>
		<div class="clear areaencloser"></div>
	</div>
	
	
	<div id="listarea" class="dwidth-middleright">
		<table id="list"></table>
		<div id="listpager"></div>
	</div>
	
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase" style="padding-top:4px;margin-top:24px;">
		<input type="button" id="exportbutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="结果导出" role="button" style="float:right;right:4px;">
	</div>
	<div id="detail_dialog">
	</div>
	
	<div class="clear areacloser"></div>

	<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>

	<table class="subform">${mReferChooser}</table>
</div>
