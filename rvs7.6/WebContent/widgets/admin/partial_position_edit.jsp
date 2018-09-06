<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
 <form id="abandon_modify">
   <table class="condform">				
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
					   <label id="edit_label_model_name" name="model_name" alt="型号" />
					   <select multiple id="select_model_name" class="ui-widget-content">${smodelNameOptions}</select> 	
					</td>					
				</tr>
				<tr> 
				    <td class="ui-state-default td-title">零件编码</td>
					<td class="td-content">
						<label id="edit_label_code" name="code"alt="零件编码"/>
				    </td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件名称</td>
					<td class="td-content">
						<label id="edit_label_name" name="name" alt="零件名称"/>
					</td>					
				</tr>
				<tr>
				    <td class="ui-state-default td-title">操作</td>
					<td class="td-content" id="operation_id">
						<input type="radio" name="operation" id="edit_choose_discontinue" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"/><label for="edit_choose_discontinue" aria-pressed="false">废止</label>
					    
						<input type="radio" name="operation" id="edit_decided" class="ui-widget-content ui-helper-hidden-accessible" value="1"/><label for="edit_decided" aria-pressed="false">改定</label>
					</td>	
				</tr>
				<tr class="discontinue_edit" style="display:none">
						<td class="ui-state-default td-title">有效时间</td>
						<td class="td-content">
							<input type="text" id="edit_effective_time"readonly="readonly" name="history_limit_date" alt="有效时间" class="ui-widget-content" />
						</td>
					</tr>
				<tr class="decided_edit" style="display:none">
				
					<td class="ui-state-default td-title">改动时间</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content"  id="edit_change_time" readonly="readonly" name="history_limit_date" alt="改动时间" />
					</td>					
				</tr>
				<tr class="decided_edit" style="display:none">
				    <td class="ui-state-default td-title">改动零件</td>
					<td class="td-content">
						    <input type="hidden" name="partial_id" id="edit_partial_id"/>
							<input type="text" class="ui-widget-content" name="code" alt="改动零件"  id="search_partial_id"/>
					</td>					
				</tr>
		</table>	
		<input type="hidden" id="edit_model_id"/>
		<input type="hidden" id="hidden_partial_id"/>
		<input type="hidden" id="edit_position_id"/>   
</form>
<div id="confirm_dialog"></div>