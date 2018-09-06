<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<style>
tr.two td:first-child{
	padding-left:50px;
}
tr.three td:first-child{
	padding-left:70px;
}
</style>

<body>
	<div style="margin: auto;">
		<div id="content_detail_basearea">
			<div id="body-recept" class="ui-widget-content" style="display:none;">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">SAP修理通知单No.</td>
							<td class="td-content">
								<label id="recept_edit_omr_notifi_no"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">SORC代码</td>
							<td class="td-content">
								<label type="text" id="recept_edit_sorc_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">RC名称</td>
							<td class="td-content">
								<input type="text" id="recept_edit_rc_code" class="ui-widget-content" maxlength="4">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理品型号</td>
							<td class="td-content">
								<input type="text" id="recept_edit_item_code" class="ui-widget-content" maxlength="40">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">序列号(机身号）</td>
							<td class="td-content">
								<input type="text" id="recept_edit_series_no" class="ui-widget-content" maxlength="12">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">维修方式</td>
							<td class="td-content">
								<input type="text" id="recept_edit_repair_method" class="ui-widget-content" maxlength="8">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">OCM初定维修等级</td>
							<td class="td-content">
								<input type="text" id="recept_edit_ocm_rank" class="ui-widget-content" maxlength="3">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">OCM发送时间</td>
							<td class="td-content">
								<input type="text" id="recept_edit_ocm_ship_date" class="ui-widget-content" maxlength="8" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">维修等级</td>
							<td class="td-content">
								<input type="text" id="recept_edit_osh_rank" class="ui-widget-content" maxlength="3">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理同意时间</td>
							<td class="td-content">
								<input type="text" id="recept_edit_agree_time" class="ui-widget-content" maxlength="8" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">客户医院名</td>
							<td class="td-content">
								<input type="text" id="recept_edit_hospital" class="ui-widget-content" maxlength="35">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">是否直送</td>
							<td class="td-content" id="recept_edit_directly_to_sorc">
								<input type="radio" name="direct_flg" id="direct_flg_yes" class="ui-widget-content" value="1"><label for="direct_flg_yes">是</label>
								<input type="radio" name="direct_flg" id="direct_flg_no" class="ui-widget-content" value="0"><label for="direct_flg_no">否</label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理品接收标识</td>
							<td class="td-content">
								<input type="text" id="recept_edit_item_receiver_flag" class="ui-widget-content" maxlength="3">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理品接收日期</td>
							<td class="td-content">
								<input type="text" id="recept_edit_item_receiver_date" class="ui-widget-content" maxlength="8" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理品接收时间</td>
							<td class="td-content">
								<input type="text" id="recept_edit_item_receiver_time" class="ui-widget-content" maxlength="6">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">修理品入库人员</td>
							<td class="td-content">
								<input type="text" id="recept_edit_item_receiver_person" class="ui-widget-content" maxlength="12">
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<!-- 投线 -->
			<div id="body-approve" class="ui-widget-content" style="display:none;">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">SAP修理通知单No.</td>
							<td class="td-content">
								<label id="approve_edit_omr_notifi_no"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">SORC代码</td>
							<td class="td-content">
								<label id="approve_edit_sorc_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">SORC开始投线日期</td>
							<td class="td-content">
								<input type="text" id="approve_edit_item_input_line_date" class="ui-widget-content" maxlength="8" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">SORC开始投线时间</td>
							<td class="td-content">
								<input type="text" id="approve_edit_item_input_line_time" class="ui-widget-content" maxlength="6">
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<!-- 订购 -->
			<div id="body-part-order" class="ui-widget-content" style="display:none;">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">SAP修理通知单No.</td>
							<td class="td-content">
								<label id="part_order_edit_omr_notifi_no"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">SORC代码</td>
							<td class="td-content">
								<label id="part_order_edit_sorc_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">零件发到修理单日期</td>
							<td class="td-content">
								<input type="text" id="part_order_edit_parts_confrim_date" class="ui-widget-content" maxlength="8">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">零件发到修理单时间</td>
							<td class="td-content">
								<input type="text" id="part_order_edit_parts_confrim_time" class="ui-widget-content" maxlength="6">
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
