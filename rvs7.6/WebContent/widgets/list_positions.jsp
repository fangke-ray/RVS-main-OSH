<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="js/data/list_positions.js"></script>

<% boolean isOperator = Boolean.valueOf((String)request.getAttribute("isOperator")); %>
<% if (isOperator) { %>
<div id="searcharea" class="dwidth-middleright" style="display: none;">
<%} else {%>
<div id="searcharea" class="dwidth-middleright">
<%} %>
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
					<td class="ui-state-default td-title">工位代码</td>
					<td class="td-content"><input type="text" name="process_code" id="search_process_code" maxlength="3" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">工位名称</td>
					<td class="td-content">
						<input type="text" name="name" id="text_position_name" readonly="readonly" class="ui-widget-content">
						<input type="hidden" id="search_position_name">	
					</td>
					<td class="ui-state-default td-title">作业时间</td>
					<td class="td-content">
						<input type="text" id="search_action_time_start" class="ui-widget-content">起<br>
						<input type="text" id="search_action_time_end" class="ui-widget-content">止
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">所属课室</td>
					<td class="td-content">
					<select name="section_id" id="search_section_id" class="ui-widget-content">${sOptions}</select></td>
					<td class="ui-state-default td-title">所属工程</td>
					<td class="td-content">
					<select name="line_id" id="search_line_id" class="ui-widget-content">${lOptions}</select></td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content">
						
					</td>
				</tr>
			</tbody>
			</table>
			<div style="height:44px">
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

<div id="detail_dialog">
</div>

<div class="clear areacloser"></div>
<div class="referchooser ui-widget-content" id="position_refer" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform">${pReferChooser}</table>
</div>
