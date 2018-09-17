<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

	<form id="ins_serviceRepairManage">
		<input type="hidden" id="material_id">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="add_model_name" name="model_name" alt="型号" ></label>
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label  id="add_serial_no" name="serial_no"alt="机身号"  ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理编号</td>
					<td class="td-content">
						<label  id="add_sorc_no" name="sorc_no" alt="修理编号" ></label>
					</td>
					<td class="ui-state-default td-title" rowspan="2">类别</td>
					<td class="td-content"  rowspan="2">
						<select id="add_service_repair_flg" name="service_repair_flg" alt="类别"class="ui-widget-content">${sQaMaterialServiceRepair}</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">产品分类</td>
					<td class="td-content">
						<select id="add_kind" name="kind" alt="产品分类" class="ui-widget-content"></select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">RC邮件发送日</td>
					<td class="td-content">
						<label id="add_rc_mailsend_date"name="rc_mailsend_date" alt="RC邮件发送日"   ></label>
					</td>
					<td class="ui-state-default td-title">实物收到日</td>
					<td class="td-content">
						<label   id="add_rc_ship_assign_date"  alt="实物收到日" ></label>						
					</td>	
					
				</tr>
				<tr>
					<td class="ui-state-default td-title">QA受理日</td>
					<td class="td-content">
						<label id="add_qa_reception_time"name="qa_reception_time" alt="QA受理日"   ></label>
					</td>
					<td class="ui-state-default td-title">QA判定日</td>
					<td class="td-content">
						<label   id="add_qa_referee_time" name="qa_referee_time"  alt="QA判定日" ></label>
						<label id="add_answer_in_deadline" alt="答复时限"></label>
					</td>	
					
				</tr>
				<tr>					
					 <td class="ui-state-default td-title">等级</td>
				    <td class="td-content">
						<input name="rank" alt="等级" style="border:none;" id="add_rank" type="text" class="ui-widget-content" disabled></input>
					</td>
					 <td class="ui-state-default td-title">有无偿</td>
				    <td class="td-content">
				    	<select id="add_search_service_free_flg"  alt="有无偿" class="ui-widget-content"></select>
				    </td>
				</tr>				
				<tr>				    
				   <td class="ui-state-default td-title">本次编缉作为二次判定</td>
				    <td class="td-content" id="add_twojudge">
						<input type="radio" name="qa_secondary_referee_date" id="add_twojudge_yes" class="ui-widget-content ui-helper-hidden-accessible"  value="1"><label for="add_twojudge_yes" aria-pressed="false">是</label>
						<input type="radio" name="qa_secondary_referee_date" id="add_twojudge_no" class="ui-widget-content ui-helper-hidden-accessible" value="0" checked="checked"><label for="add_twojudge_no" aria-pressed="false">否</label>
					</td>	
				    <td class="ui-state-default td-title">维修站</td>
				    <td class="td-content">
						<select id="add_workshop"  alt="维修站"class="ui-widget-content"></select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">质量判定</td>
					<td class="td-content">
						<select id="add_quality_judgment" class="ui-widget-content"></select>
					</td>
					<td class="ui-state-default td-title">发行QIS</td>
				    <td class="td-content">
				    	<select id="add_qis_isuse" class="ui-widget-content"></select>
					</td>
				</tr>				
				<tr>
					<td class="ui-state-default td-title">处理对策</td>
				    <td class="td-content">
						<textarea id="add_countermeasures" name="countermeasures" alt="处理对策"   cols=35 rows=2 class="ui-widget-content"></textarea>
					</td>
					<td class="ui-state-default td-title">备注</td>
				    <td class="td-content">
						<textarea id="add_comment" name="comment"  alt="备注"  cols=35 rows=2 class="ui-widget-content"></textarea>
					</td>
				</tr>
				<tr>
				    <td class="ui-state-default td-title">QIS发送单号</td>
				    <td class="td-content">
						<input id="add_qis_invoice_no" type="text" name="qis_invoice_no" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">QIS发送日期</td>
				    <td class="td-content">
						<input id="add_qis_invoice_date" type="text" name="qis_invoice_date" readonly="readonly"class="ui-widget-content"/>
					</td>
				</tr >				
				<tr class="qis_payout_edit">
				    <td class="ui-state-default td-title">质量信息单号</td>
				    <td class="td-content" colspan="3">
				    	<input id="add_quality_info_no" type="text" name="quality_info_no" class="ui-widget-content" size="80"/>
				    </td>
				</tr>	
				<tr class="qis_payout_edit">
					<td class="ui-state-default td-title">请款月</td>
				    <td class="td-content">
						<input id="add_include_month" type="text" name="include_month" readonly="readonly"class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title charge_amount">请款金额</td>
				    <td class="td-content charge_amount">
						<input id="add_charge_amount" name="charge_amount" type="text" name="charge_amount" class="ui-widget-content"/>
					</td>										
				</tr>
				<tr>
					<td class="ui-state-default td-title">ETQ单号</td>
				    <td class="td-content">
						<input id="add_etq_no" type="text" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">PAE编号</td>
				    <td class="td-content">
						<input id="add_pae_no" type="text" class="ui-widget-content"/>
					</td>
				</tr>
			</tbody>
		</table>		
	</form>