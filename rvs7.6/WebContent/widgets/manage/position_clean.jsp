<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<form id="breakForm">
		<table class="condform">
			<tbody>
				<tr>
				<td class="td-content" style="width:315px;" colspan="2">
					<span class='errorarea'>请注意！本功能是用于人为删除一些由误操作发生的工位作业。<br>
					请确认将要处理的是否确为<b style="color:red;">非正常</b>的作业，随意地删除可能会导致整个流程的异常！<br>
					以下的操作中，<br>【重新作业】会取消维修对象在该工位的最新作业，并返回等待区。<br>
					【删除作业】会删除维修对象所有在该工位的作业记录。如：由于投线时业务流程选择错误而行经了不需要的工位时，可点此消除。<br>
					【取消指派】会取消维修对象在该工位的最新作业指派。如：误指派了的 CCD 换玻璃作业；返工时多点的不需要返工的工位。<br>
					</span>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">处理工位</td>
				<td class="td-content" style="width:315px;">
					<label name="position" id="pc_position_name" class="ui-widget-content">${process_code}</label>
					<input type="hidden" name="position_id" id="pc_position_id" class="ui-widget-content" value="${position_id}">
					</input>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">提出理由</td>
				<td class="td-content" style="width:315px;">
					<textarea readonly name="comment" id="pc_comment" class="ui-widget-content" style="width:90%;height:3em;">${comment}</textarea>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">处理</td>
				<td class="td-content">
					<input type="button" id="pc_rewait" value="重新作业">
					<input type="button" id="pc_delete" value="删除作业">
					<input type="button" id="pc_cancel" value="取消指派">
					<input type="button" id="pc_no" value="不予处理">
				</td>
				</tr>

			</tbody>
		</table>
	</form>
	

	