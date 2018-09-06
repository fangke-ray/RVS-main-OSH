<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
	<form id="inf_sorc_loss">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="label_model_name" name="model_name" alt="型号" ></label>
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label  id="label_serial_no" name="serial_no"alt="机身号"  ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content">
						<label  id="label_sorc_no" name="sorc_no" alt="修理单号" ></label>
					</td>
					<td class="ui-state-default td-title">分室</td>
					<td class="td-content">
						<label  id="label_compartment" name="sorc_no" alt="分室" ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">OCM RANK</td>
					<td class="td-content">
						<label id="label_ocm_rank"name="ocm_rank" alt="OCM RANK"></label>
					</td>
					<td class="ui-state-default td-title">SORC RANK</td>
					<td class="td-content">
						<label id="label_sorc_rank" name="sorc_rank" alt="SORC RANK"></label>						
					</td>					
				</tr>
				<tr>
					<td class="ui-state-default td-title">发现工程</td>
					<td class="td-content">
						<select id="edit_discover_project" name="discover_project" alt="发现工程"></select>
						<input type="hidden" id="edit_org_belongs"/>
					</td>	
					 <td class="ui-state-default td-title">责任区分</td>
				    <td class="td-content">
						<select id="edit_liability_flg" name="liability_flg" alt="责任区分"></select>
					</td>
				</tr>
				<tr>						
					<td class="ui-state-default td-title">不良简述</td>
				    <td class="td-content">
				    	<textarea  id="edit_fix_demand" name="fix_demand" alt="不良简述"></textarea>
				    </td>
					 <td class="ui-state-default td-title">数量</td>
				    <td class="td-content" id="add_twojudge">
						<label id="label_quantity" name="quantity" alt="数量"></label>
					</td>	
				</tr>

				<tr>				    
				  
				    <td class="ui-state-default td-title">零件单价</td>
				    <td class="td-content">
						<label id="label_price" name="price" alt="零件单价"></label>
					</td>
					<td class="ui-state-default td-title">报价差异损金</td>
				    <td class="td-content">
						<label id="label_loss_amount" name="loss_amount" alt="处理对策" ></label>
					</td>
				</tr>				
				<tr>					
				   <td class="ui-state-default td-title">有偿与否</td>
				    <td class="td-content">
						<select id="edit_service_free_flg" name="service_free_flg" alt="有偿与否" ></select>
					</td>
					<td class="ui-state-default td-title">备注</td>
				    <td class="td-content">
						<textarea id="edit_comment" alt="备注" name="comment" class="ui-widget-content"></textarea>
					</td>
				</tr>				
			</tbody>
		</table>		
	</form>