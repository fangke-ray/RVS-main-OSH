<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
.w_group {width: 520px; margin-top: 12px; margin-bottom: 8px; padding: 2px;}
</style>

<%
String concernPosition = (String) request.getAttribute("concernPosition");
%>
<div id="storagearea" style="float: left;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
		<span class="areatitle">等待区信息</span>
<% if (concernPosition != null) { %>
<%@include file="/widgets/position_panel/concern_of_heap.jsp"%>
<% } %>
	</div>
	<div class="ui-widget-content dwidth-half" style="height: 215px; overflow-y: auto; overflow-x: hidden;">
		<div id="waitings" style="margin: 20px;">
		</div>
	</div>
</div>

<div id="manualarea" style="float: right;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
		<span class="areatitle">维修对象信息</span>
		<a id="working_detail" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="icon-box-remove"></span>
		</a>
	</div>

		<div class="ui-widget-content dwidth-half" id="scanner_placeholder" style="min-height: 215px;">
		</div>
		<div class="ui-widget-content dwidth-half" id="scanner_container" style="min-height: 215px;display:none;">
		<div class="ui-state-default td-title">扫描录入区域</div>
		<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer dwidth-half"></input>
		<div style="text-align: center;">
			<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
		</div>
	</div>
	<div class="ui-widget-content dwidth-half" id="material_details" style="min-height: 215px;display:none;">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">修理单号<input type="hidden" id="pauseo_material_id"></td>
					<td class="td-content-text"></td>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content-text">GIF-Q260</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content-text">312211</td>
				</tr>
				<tr>
					<td style="border:0; height:7.5em;"></td>
				</tr>
			</tbody>
		</table>
		<div style="height: 44px">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="finishbutton" value="完成" role="button" aria-disabled="false" style="float: right; right: 2px;">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="breakbutton" value="异常中断" role="button" aria-disabled="false" style="float: right; right: 2px;">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="stepbutton" value="正常中断" role="button" aria-disabled="false" style="float: right; right: 2px;">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="pausebutton" value="暂停" role="button" aria-disabled="false" style="float: right; right: 2px;">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="continuebutton" value="重开" role="button" aria-disabled="false" style="float: right; right: 2px;">
		</div>
	</div>

</div>

<div class="clear areaencloser"></div>