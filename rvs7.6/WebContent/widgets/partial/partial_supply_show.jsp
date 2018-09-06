<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<form id="ins_serviceRepairManage" >
	<input type="hidden" id="partial_id" />
	<input type="hidden" id="identification" />
	<input type="hidden" id="identification" />
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">订购用途</td>
				<td class="td-content">
					<label id="used_by" name="used_by" alt="订购用途" ></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">零件号</td>
				<td class="td-content">
					<label id="code" name="code" alt="零件号" ></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">零件说明</td>
				<td class="td-content">
					<label id="partial_name" name="partial_name" alt="零件说明" ></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">数量</td>
				<td class="td-content">
					<input name="quantity" alt="数量"  id="quantity" type="text" class="ui-widget-content"></input>
				</td>
			</tr>
		</tbody>
	</table>
</form>