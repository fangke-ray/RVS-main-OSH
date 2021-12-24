<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

	<form id="accept_Manage">
		<input type="hidden" id="material_id">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<input id="add_model_name" name="model_name" alt="型号"class="ui-widget-content" type="text" />
					</td>					
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<input  id="add_serial_no" name="serial_no" alt="机身号" class="ui-widget-content" type="text" />
					</td>
			   </tr>
			   <tr>
					<td class="ui-state-default td-title">修理编号</td>
					<td class="td-content">
						<input  id="add_sorc_no" name="sorc_no" alt="修理编号"class="ui-widget-content" type="text" />
					</td>
			   </tr>
			   <tr>
					<td class="ui-state-default td-title">类别</td>
					<td class="td-content">
						<select id="accept_add_service_repair_flg" name="service_repair_flg"  alt="类别" ></select>
					</td>
				</tr>
			   <tr>
					<td class="ui-state-default td-title">产品分类</td>
					<td class="td-content">
						<select id="accept_add_kind" class="ui-widget-content"></select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">RC邮件发送日</td>
					<td class="td-content">
						<input id="add_rc_mailsend_date"name="rc_mailsend_date"  alt="RC邮件发送日"class="ui-widget-content" type="type"  />
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">质量信息单号</td>
					<td class="td-content">
						<input id="add_quality_info_no"name="quality_info_no"  alt="质量信息单号"class="ui-widget-content" type="type"  />
					</td>
				</tr>
			</tbody>
		</table>
	</form>