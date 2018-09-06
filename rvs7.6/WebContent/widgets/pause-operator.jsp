<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<form>
		<table class="condform" id="pauseo_edit">
			<tbody>
				<tr>
				<td class="ui-state-default td-title">暂停理由</td>
				<td class="td-content" style="width:615px;">
					<select name="pause_reason" id="pauseo_edit_pause_reason" class="ui-widget-content">
					</select>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content">
				<select id="pauseo_edit_comments_select" class="ui-widget-content" style="display:none;">
				</select>
				<textarea class="ui-widget-content" name="comments" alt="备注" id="pauseo_edit_comments" style="width:300px;height:150px;"></textarea></td>
				</tr>
			</tbody>
		</table>
		<table class="condform" id="pauseo_show" style="display:none">
			<tbody>
				<tr>
				<td class="ui-state-default td-title">暂停中维修对象</td>
				<td class="td-content" style="width:315px;">
					<label id="pauseo_show_sorc_no" class="ui-widget-content"></label>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">暂停理由</td>
				<td class="td-content" style="width:315px;">
					<label id="pauseo_show_pause_reason" class="ui-widget-content"></label>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content"><textarea class="ui-widget-content" id="pauseo_show_comments" style="width:300px;height:150px;" readonly></textarea></td>
				</tr>
			</tbody>
		</table>
	</form>
	

	