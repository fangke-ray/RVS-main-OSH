<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
.w_group {width: 694px; margin-top: 12px; margin-bottom: 8px; padding: 2px;}
#storagearea > div:nth-child(2) {text-align:center;color: white;}
#storagearea > div:nth-child(2) > span {width:50%;display:block;float:left;padding:2px 0 1px;}
.waiting.other_px {margin-left : 200px;}
.waiting.other_px .tube-liquid > .finish_time_left {
    float: left;
    margin-left: -55px;
}
.waiting.other_px .tube-liquid .pa_flags {
	left: 1.5em;
}
.waiting.other_px .tube-liquid .pa_flags div {
    float: right;
}
.waiting.other_px .tube-liquid .pa_flags .bo_flg {
	background: linear-gradient(to left,  rgba(244, 174, 45, 1) 70%, rgba(244, 174, 45, 0) 100%);
	border-top-right-radius: 0.7em;
	border-bottom-right-radius: 0.7em;
	border-top-left-radius: 0px;
	border-bottom-left-radius: 0px;
	border-right:1px solid gray;
	border-left:none;
}
</style>

<%
String concernPosition = (String) request.getAttribute("concernPosition");
%>
<div id="storagearea" style="float: left;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-golden_section_large_part">
		<span class="areatitle">等待区信息</span>
<% if (concernPosition != null) { %>
<%@include file="/widgets/position_panel/concern_of_heap.jsp"%>
<% } %>
		<div id="other_px_area" style="float:right;">
		</div>
	</div>
	<div style="height:20px;">
		<input id="otherPx" type="hidden" value="${otherPx}">
${(px eq 1) ? '<span style="background-color: #92D050;">A 线</span><span style="background-color: #C86BC8;">B 线</span>' : ''}
${(px eq 2 and otherPx eq 1) ? '<span style="background-color: #C86BC8;">B 线</span><span style="background-color: #92D050;">A 线</span>' : ''}
${(px eq 2 and otherPx eq 3) ? '<span style="background-color: #C86BC8;">B 线</span><span style="background-color: #2FB1BE;">C 线</span>' : ''}
${(px eq 3) ? '<span style="background-color: #2FB1BE;">C 线</span><span style="background-color: #C86BC8;">B 线</span>' : ''}
	</div>
	<div class="ui-widget-content dwidth-golden_section_large_part" style="height: 215px; overflow-y: auto; overflow-x: hidden;">
		<div id="waitings" style="margin: 20px;">
		</div>
	</div>
</div>

<div id="manualarea" style="float: right;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-golden_section_small_part">
		<span class="areatitle">维修对象/组装组件信息</span>
		<a id="working_detail" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="icon-box-remove"></span>
		</a>
	</div>

	<div class="ui-widget-content dwidth-golden_section_small_part" id="scanner_container" style="min-height: 235px;">
		<div class="ui-state-default td-title">扫描录入区域</div>
		<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width:462px"></input>
		<div style="text-align: center;">
			<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
		</div>
	</div>
	<div class="ui-widget-content dwidth-golden_section_small_part" id="material_details" style="min-height: 235px;">
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
