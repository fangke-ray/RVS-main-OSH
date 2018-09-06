<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

	<form id="ins_serviceRepairManage" style="max-height: 500px;">
		<input type="hidden" id="material_id">
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
						<td class="ui-state-default td-title">类别</td>
					<td class="td-content">
						<label  id="label_service_repair_flg" name="service_repair_flg" alt="类别" ></label>
					</td>
				</tr>
				<tr>
				   	<td class="ui-state-default td-title">提要</td>
					<td class="td-content">
						<textarea  id="textarea_mention" name="mention" alt="提要" class="ui-widget-content"/>
					</td>
				</tr>
				
				<tr>
					<td class="ui-state-default td-title">医院名称</td>
					<td class="td-content" colspan="3" >
						<input type="text" id="edit_customer_name" style="width:480px;"  name="customer_name"  alt="客户名" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分析表编号</td>
					<td class="td-content">
						<input type="text"  id="text_analysis_no" name="analysis_no" alt="分析表编号" maxlength="23" style="width:14em;" class="ui-widget-content"/>
					</td>
				   	<td class="ui-state-default td-title">不良内容</td>
					<td class="td-content">
						<textarea id="text_fix_demand" name="fix_demand" alt="不良内容" class="ui-widget-content"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障描述</td>
					<td class="td-content" colspan="3">
						<textarea  id="text_trouble_discribe" name="trouble_discribe" alt="故障描述" class="ui-widget-content" cols="100"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障原因</td>
					<td class="td-content" colspan="3">
						<textarea  id="text_trouble_cause" name="trouble_cause" alt="故障原因" class="ui-widget-content" cols="100"></textarea>
					</td>
				</tr>
				<tr>
				  <td class="ui-state-default td-title">分析结果</td>
					<td class="td-content">
						<select  id="select_analysis_result" name="analysis_result" alt="分析结果" ></select>
					</td>
					<td class="ui-state-default td-title">责任区分</td>
					<td class="td-content">
						<select id="select_liability_flg" name="liability_flg" alt="责任区分" ></select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修场所标记</td>
					<td class="td-content" id="select_manufactory_flg">
						<input type="radio" name="manufactory_flg" id="manufactory_flg_yes" class="ui-widget-content ui-helper-hidden-accessible"  value="1"><label for="manufactory_flg_yes" aria-pressed="false">OGZ</label>
						<input type="radio" name="manufactory_flg" id="amanufactory_flg_no" class="ui-widget-content ui-helper-hidden-accessible" value="0" checked="checked"><label for="amanufactory_flg_no" aria-pressed="false">无</label>
					</td>
					<td class="ui-state-default td-title">追加部件</td>
					<td class="td-content">
						<input type="text"  id="text_append_component" name="append_component" alt="追加部件" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
				  	<td class="ui-state-default td-title">数量</td>
					<td class="td-content">
						<input type="text"  id="text_quantity" name="quantity" alt="数量" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">损金数额</td>
					<td class="td-content">
						<input type="text"  id="text_loss_amount" name="loss_amount" alt="损金数额" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">购买/安装日期</td>
					<td class="td-content">
						<input type="text"  id="text_setup_date" class="ui-widget-content" readonly="readonly" />
					</td>
					<td class="ui-state-default td-title">故障发生日期</td>
					<td class="td-content">
						<input type="text"  id="text_trouble_happen_date" class="ui-widget-content" readonly="readonly" />
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">使用累计次数</td>
					<td class="td-content">
						<input type="text"  id="text_use_count" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">使用累计时间</td>
					<td class="td-content">
						<input type="text"  id="text_use_elapse" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
				 	<td class="ui-state-default td-title">上次修理单号</td>
					<td class="td-content">
						<input type="text"  id="text_last_sorc_no" name="last_sorc_no" alt="上次修理单号" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">上次出货日期</td>
					<td class="td-content">
						<input type="text"  id="text_last_shipping_date" name="last_shipping_date" readonly="readonly"  alt="上次出货日期" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">上次等级</td>
					<td class="td-content">
						<input type="text"  id="text_last_rank" name="last_rank" alt="上次等级" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">上次故障内容</td>
					<td class="td-content">
						<textarea  id="text_last_trouble_feature" name="last_trouble_feature" alt="上次故障内容" class="ui-widget-content"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">清洗</td>
					<td class="td-content">
						<input type="text"  id="text_wash_feature" name="wash_feature" alt="清洗" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">消毒</td>
					<td class="td-content">
						<input type="text"  id="text_disinfect_feature" name="disinfect_feature" alt="消毒" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">灭菌</td>
					<td class="td-content">
						<input type="text"  id="text_steriliza_feature" name="steriliza_feature" alt="灭菌" class="ui-widget-content"/>
					</td>
					 <td class="ui-state-default td-title">使用频率</td>
					<td class="td-content">
						<input type="text"  id="text_usage_frequency" name="usage_frequency" alt="使用频率" class="ui-widget-content"/>
					</td>
				</tr>
				<tr id="part1">
					<td class="ui-state-default td-title">处理方式</td>
					<td class="td-content" colspan="3">
						<select id="select_corresponse_flg"></select>
					</td>
				</tr>
				<tr id="part2">
					<td class="ui-state-default td-title">实物处理</td>
					<td class="td-content" colspan="3">
						<select id="select_entity_send_flg"></select>
					</td>
				</tr>
				<tr class="part3">
					<td class="ui-state-default td-title">故障品接收</td>
					<td class="td-content">
						<input type="text" id="text_trouble_item_reception_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
					<td class="ui-state-default td-title">故障品提交给业务</td>
					<td class="td-content">
						<input type="text" id="text_trouble_item_in_bussiness_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
				</tr>
				<tr class="part3">
					<td class="ui-state-default td-title">故障品发送（业务）</td>
					<td class="td-content">
						<input type="text" id="text_trouble_item_out_bussiness_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
					<td class="ui-state-default td-title">QIS2</td>
					<td class="td-content">
						<input type="text" id="text_qis2_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
				</tr>
				<tr class="part3">
					<td class="ui-state-default td-title">QIS3</td>
					<td class="td-content">
						<input type="text" id="text_qis3_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
					<td class="ui-state-default td-title">废弃证明</td>
					<td class="td-content">
						<input type="text" id="text_waste_certificated_date" readonly="readonly" class="ui-widget-content"/>
						<label style="display: none;">-</label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障现象确认(工厂)</td>
					<td class="td-content">
						<input type="text" id="text_m_trouble_phenomenon_confirm" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">判定结果(工厂)</td>
					<td class="td-content">
						<input type="text" id="text_m_judgment_result" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分析结果简述(工厂)</td>
					<td class="td-content" colspan="3">
						<textarea  id="text_m_analysis_result_brief" class="ui-widget-content" cols="100"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">对应方法(工厂)</td>
					<td class="td-content">
						<input type="text" id="text_m_correspond_method" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">对策(工厂)</td>
					<td class="td-content">
						<input type="text" id="text_m_solutions" class="ui-widget-content"/>
					</td>
				</tr>
			</tbody>
		</table>		
	</form>