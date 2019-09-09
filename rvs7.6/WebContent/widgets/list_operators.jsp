<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@page import="com.osh.rvs.bean.LoginData"%>
<%@page import="com.osh.rvs.common.RvsConsts"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<% 
boolean isOperator = Boolean.valueOf((String)request.getAttribute("isOperator")); 
boolean isLeader = Boolean.valueOf("" + request.getAttribute("isLeader")); 
%>

<div id="searcharea" class="dwidth-middleright" >

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
				<tr <% if (!isLeader) { %> style="display: none;" <%} %>>
					<td class="ui-state-default td-title">工号</td>
					<td class="td-content"><input type="text"  id="search_job_no" maxlength="8" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">姓名</td>
					<td class="td-content"><input type="text"  id="search_name" maxlength="8" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">所在课室</td>
					<td class="td-content"><select id="search_section_id" class="ui-widget-content">${sOptions}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title" <% if (!isLeader) { %> style="display: none;" <%} %>>担当工程</td>
					<td class="td-content" <% if (!isLeader) { %> style="display: none;" <%} %>>
					<select id="search_line_id" class="ui-widget-content" <% if (!isLeader) { %> style="display: none;" <%} %>>${lOptions}</select></td>
					<td class="ui-state-default td-title" <% if (!isLeader) { %> style="display: none;" <%} %>>包含失效担当人</td>
					<td class="td-content" id="deleted_set" <% if (!isLeader) { %> style="display: none;" <%} %>>
						<input type="radio" name="deleted" id="deleted_y" class="ui-widget-content" value="1"><label for="deleted_y">是</label>
						<input type="radio" name="deleted" id="deleted_n" class="ui-widget-content" value="" checked><label for="deleted_n">否</label>
						<input type="hidden" id="search_deleted">
					</td>
					<td class="ui-state-default td-title">作业时间</td>
					<td class="td-content">
						<input type="text" id="search_action_time_start" class="ui-widget-content">起<br>
						<input type="text" id="search_action_time_end" class="ui-widget-content">止
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
		<table id="list"></table>
		<div id="listpager"></div>
	</div>
	
	<div id="month_files_area" class="dwidth-middleright">
		<table id="month_files_list"></table>
		<div id="month_files_listpager"></div>
	</div>


<div id="detail_dialog">
</div>

</div>
<div class="clear areaencloser"></div>
