<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript">
	$(function() {
	$("#set_normal").button();
	$("#set_normal").click(function(){
		$("#is_direct, #reason_type_warranty, #reason_type_new").attr("checked",false);
		$("#is_direct + label, #reason_type_warranty + label, #reason_type_new + label").removeClass("ui-state-active");
	});
	$("#is_direct, #reason_type_warranty, #reason_type_new").click(function(){
		$("#set_normal").attr("checked",false);
		$("#set_normal + label").removeClass("ui-state-active");
	});

//	$("#is_direct").button();
//	$("#reason_type_set").buttonset();
//	$("#handle_type_set").buttonset();

	});
</script>

	<form id="ins_material">
		<input type="hidden" id="material_id">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content" style="width: 240px;" colspan="3">
						<input id="edit_sorcno" maxlength="15"></input>
					</td>
					<!--td class="ui-state-default td-title">ESAS No.</td>
					<td class="td-content" style="width: 200px;">
						<input id="edit_esasno" maxlength="6"></input>
					</td-->
				</tr>
				<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content" colspan="3">
					<input type="text" class="ui-widget-content" readonly="readonly" id="inp_modelname">
					<input type="hidden" name="modelname" id="edit_modelname">
					<label id="edit_label_modelname"></label>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">机身号</td>
				<td class="td-content" colspan="3">
					<input id="edit_serialno" maxlength="12"></input>
					<label id="edit_label_serialno"></label>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">OCM</td>
				<td class="td-content" colspan="3">
					<select name="edit_ocm" alt="委托处" id="edit_ocm" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_ocm", null, "", false) %>
					</select>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">OCM 修理等级</td>
				<td class="td-content">
					<select name="edit_ocm_rank" id="edit_ocm_rank" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_ocm_direct_rank", null, "", false) %>
					</select>
				</td>
				<td class="ui-state-default td-title">OCM 配送日</td>
				<td class="td-content">
					<input id="edit_ocm_deliver_date" readonly></input>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">修理等级</td>
				<td class="td-content" colspan="3">
					<select name="edit_level" id="edit_level" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_level", null, "", false) %>
					</select>
				</td>
				</tr>
				<tr style="display:none;">
					<td class="ui-state-default td-title">维修流程</td>
					<td class="td-content" colspan="3">
						<input alt="维修流程" type="button" id="major_pat_button" class="ui-button" value="更改">
						<span id="major_pat" value=""></span>
					</td>
				</tr>
				<tr style="display:none;">
					<td class="ui-state-default td-title">中小修理维修内容流程</td>
					<td class="td-content" colspan="3">
						<input alt="中小修理维修流程" type="button" id="light_pat_button" class="ui-button" value="设定">
					</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">顾客名</td>
				<td class="td-content" colspan="3">
					<input id="edit_customer_name" maxlength="100" style="width:40em;"></input>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">仓储人员</td>
				<td class="td-content">
					<select name="edit_storager" id="edit_storager" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_storager", null, "", false) %>
					</select>
				</td>
				<td class="ui-state-default td-title">通箱编号</td>
				<td class="td-content"><input class="ui-widget-content" id="edit_package_no" value="原箱"></input></td>
				</tr>
				<tr>
				<td rowspan="4" class="ui-state-default td-title">备注</td>
				<td class="td-content" colspan="3">
					<select id="direct" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_direct", null, "(普通)", false) %>
					</select>
					<div>
					<input type="checkbox" class="ui-button" id="direct_rapid"></input><label for="direct_rapid">快速</label>
					</div>
				</td>
				</tr>
				<tr>
				<td class="td-content" colspan="3">
					<select id="service_repair" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_service_repair", null, "", false) %>
					</select>
				</td>
				</tr>
				<tr>
				<td class="td-content" colspan="3">
					<select name="fix_type" alt="修理分类" id="fix_type" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_fix_type", null, "", false) %>
					</select>
				</td>
				</tr>
				<tr>
				<td class="td-content" colspan="3">
					<select id="selectable" class="ui-widget-content">
						<option value="0">(普通)</option>
						<option value="1">选择式报价</option>
					</select>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">直送区域</td>
				<td class="td-content" colspan="3">
					<select id="edit_bound_out_ocm" class="ui-widget-content">
						<%=CodeListUtils.getSelectOptions("material_direct_area", null, "(无)", false) %>
					</select>
				</td>
				</tr>
				<tr>
			</tbody>
		</table>
	</form>
	

<div class="referchooser ui-widget-content" id="referchooser_edit" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=session.getAttribute("mReferChooser")%></table>
<div>

