<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
.w_group {width: 694px; margin-top: 12px; margin-bottom: 8px; padding: 2px;}
</style>
<div id="storagearea" style="float: left;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-golden_section_large_part">
		<span class="areatitle">等待区信息</span>
	</div>
	<div class="ui-widget-content dwidth-golden_section_large_part" style="height: 215px; overflow-y: auto; overflow-x: hidden;">
		<div id="waitings" style="margin: 20px;">
		</div>
	</div>
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-golden_section_large_part">
		<span class="areatitle">完成品信息</span>
	</div>
	<div class="ui-widget-content dwidth-golden_section_large_part" style="height: 86px; overflow-y: auto; overflow-x: hidden;">
		<div id="completes" style="margin: 20px;">
		</div>
	</div>
</div>

<div id="manualarea" style="float: right;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-golden_section_small_part">
		<span class="areatitle">维修对象信息</span>
		<a id="working_detail" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="icon-box-remove"></span>
		</a>
	</div>

	<div class="ui-widget-content dwidth-golden_section_small_part" id="scanner_container" style="min-height: 335px;">
		<div class="ui-state-default td-title">扫描录入区域</div>
		<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width:462px"></input>
		<div style="text-align: center;">
			<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
		</div>
	</div>
	<div class="ui-widget-content dwidth-golden_section_small_part" id="material_details" style="min-height: 335px;">
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
				<tr style="display: table-row;">
					<td class="ui-state-default td-title">开始时间</td>
					<td class="td-content-text"></td>
					<td class="ui-state-default td-title">作业标准时间</td>
					<td class="td-content-text"></td>
					<td class="ui-state-default td-title">作业经过时间</td>
					<td class="td-content-text" id="dtl_process_time"><div class="roll_cell"><div class="roll_seconds">0 1 2 3 4 5 6 7 8 9</div></div><div class="roll_cell"><div class="roll_tenseconds">0 1 2 3 4 5 6</div></div><label style="float:right;"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">完成度</td>
					<td colspan="5" class="td-content-text slim">
						<div class="waiting tube" id="p_rate" style="height: 20px; margin: auto;"></div>
					</td>
				</tr>
				<tr>
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