<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<form id="breakForm">
		<table class="condform">
			<tbody>
				<tr>
				<td class="ui-state-default td-title">中断理由</td>
				<td class="td-content" style="width:315px;">
					<select name="break_reason" id="break_reason" class="ui-widget-content">
					</select>
				</td>
				</tr>
				<tr class="drying_jobs" style="display:none;">
				<td class="ui-state-default td-title">烘干作业</td>
				<td class="td-content" style="width:768px;">
					
				</td>
				</tr>
				<tr class="drying_slots" style="display:none;">
				<td class="ui-state-default td-title">烘干库位</td>
				<td class="td-content" style="width:768px;">
					
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content"><textarea class="ui-widget-content" alt="备注信息" id="edit_comments" name="comments" style="width:300px;height:150px;"></textarea></td>
				</tr>

			</tbody>
		</table>
	</form>
	

	