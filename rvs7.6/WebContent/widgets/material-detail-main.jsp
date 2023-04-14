<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String from = (String) request.getAttribute("from");
	boolean unrepair = (request.getAttribute("unrepair") != null);
	boolean inline = (request.getAttribute("inline") != null);
	boolean outline = (request.getAttribute("outline") != null);
	boolean sorc_reception = (request.getAttribute("sorc_reception") != null);
	boolean sorc_shipment = (request.getAttribute("sorc_shipment") != null);
	boolean comment_edit = (request.getAttribute("comment_edit") != null);
	boolean hasDecom = (request.getAttribute("hasDecom") != null);
	boolean hasNs = (request.getAttribute("hasNs") != null);
%>
<div id="material_detail_basearea" style="margin-top:22px;margin-left:9px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">维修对象基本信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title">修理单号</td>
				<td class="td-content">
<%
if ("data".equals(from)) {
%>
					<label style="display:none;">${materialForm.sorc_no}</label>
					<input type="text" name="sorc_no" value="${materialForm.sorc_no}" class="ui-widget-content" maxlength="15">
<%
} else {
%>
					<label>${materialForm.sorc_no}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">ESAS No.</td>
				<td class="td-content">
<%
if ("data".equals(from)) {
%>
					<input type="text" name="esas_no" value="${materialForm.esas_no}" class="ui-widget-content" maxlength="6">
<%
} else {
%>
					<label>${materialForm.esas_no}</label>
<%
}
%>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content">
					<label>${materialForm.model_name}</label>
				</td>
				<td class="ui-state-default td-title">机身号</td>
				<td class="td-content">
					<label>${materialForm.serial_no}</label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">委托处</td>
				<td class="td-content">
<%
if ("data".equals(from)) {
%>
					<select name="ocm" class="ui-widget-content">
						${materialForm.ocm}
					</select>
<%
} else {
%>
					<label>${materialForm.ocm}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">通箱编号</td>
				<td class="td-content">
<%
if ("data".equals(from)) {
%>
					<input type="text" name="package_no" value="${materialForm.package_no}" class="ui-widget-content" maxlength="10">
<%
} else {
%>
					<label>${materialForm.package_no}</label>
<%
}
%>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">修理等级</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<select name="level" class="ui-widget-content">
						${materialForm.level}
					</select>
<%
} else {
%>
					<label id="label_level_name">${materialForm.level}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">修理课室</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<select name="section_id" class="ui-widget-content">
						${materialForm.section_name}
					</select>
<%
} else {
%>
					<label>${materialForm.section_name}</label>
<%
}
%>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">修理流程</td>
				<td class="td-content" colspan="3">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<select name="pat_id" id="edit_pat">
						${materialForm.pat_id}
					</select>
<%
} else {
%>
					<label>${materialForm.pat_id}</label>
<%
}
%>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">加急</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<select name="scheduled_expedited" class="ui-widget-content">
						${materialForm.scheduled_expedited}
					</select>
<%
} else {
%>
					<label>${materialForm.scheduled_expedited}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content">
<%
if ("data".equals(from)) {
%>
					<select name="direct_flg" class="ui-widget-content">
						${materialForm.direct_flg}
					</select>
					<select name="service_repair_flg" class="ui-widget-content">
						${materialForm.service_repair_flg}
					</select>
					<select name="fix_type" class="ui-widget-content">
						${materialForm.fix_type}
					</select>
<%
} else {
%>
					<label>${materialForm.remark}</label>
<%
}
%>
				</td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear areaencloser dwidth-middle"></div>
</div>

<div id="material_detail_basearea" style="margin-left:9px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">维修对象作业信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle" id="info">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title">当前状态</td>
				<td class="td-content"><pre style="margin:0px">${materialForm.status}</pre></td>
				<td class="ui-state-default td-title">当前位置</td>
				<td class="td-content"><pre style="margin:0px">${materialForm.processing_position}</pre></td>
			</tr>
<%
if (sorc_reception) {
%>
			<tr>
				<td class="ui-state-default td-title">登记物流到货时间</td>
				<td class="td-content" colspan="3">${materialForm.sorc_reception}</td>
			</tr>
<%
}
%>
			<tr>
				<td class="ui-state-default td-title">受理时间</td>
				<td class="td-content">
					<label>${materialForm.reception_time}</label>
				</td>
				<td class="ui-state-default td-title">报价时间</td>
				<td class="td-content">
					<label>${materialForm.finish_time}</label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">客户同意时间</td>
				<td class="td-content">
					<label>${materialForm.agreed_date}</label>
				</td>
				<td class="ui-state-default td-title">投线时间</td>
				<td class="td-content">
					<label>${materialForm.inline_time}</label>
				</td>
			</tr>
<% 
if (hasDecom) { 
%>
			<tr id="tr_dec_date">
				<td class="ui-state-default td-title">分解产出安排</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<input type="text" name="dec_plan_date" class="ui-widget-content" value="${processForm.dec_plan_date}" readonly/>
<%
} else {
%>
					<label>${processForm.dec_plan_date}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">分解实际产出</td>
				<td class="td-content">
					<label>${processForm.dec_finish_date}</label>
				</td>
			</tr>
<%
}
if (hasNs) { 
%>
			<tr id="tr_ns_date">
				<td class="ui-state-default td-title">NS产出安排</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<input type="text" name="ns_plan_date" class="ui-widget-content" value="${processForm.ns_plan_date}" readonly/>
<%
} else {
%>
					<label>${processForm.ns_plan_date}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">NS实际产出</td>
				<td class="td-content">
					<label>${processForm.ns_finish_date}</label>
				</td>
			</tr>
<%
}
%>
			<tr id="tr_com_date">
				<td class="ui-state-default td-title">总组产出安排</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<input type="text" name="com_plan_date" class="ui-widget-content" value="${processForm.com_plan_date}" readonly/>
<%
} else {
%>
					<label>${processForm.com_plan_date}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">总组实际产出</td>
				<td class="td-content">
					<label>${processForm.com_finish_date}</label>
				</td>
			</tr>
			<tr>
<%
if (!unrepair) {
%>
				<td class="ui-state-default td-title">品保完成时间</td>
<%
} else {
%>
				<td class="ui-state-default td-title">停止修理时间</td>
<%
}
%>
				<td class="td-content">
					<label>${materialForm.outline_time}</label>
				</td>
				<td class="ui-state-default td-title">纳期</td>
				<td class="td-content">
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<input type="text" name="scheduled_date" class="ui-widget-content" value="${materialForm.scheduled_date}" readonly/>
<%
} else {
%>
					<label>${materialForm.scheduled_date}</label>
<%
}
%>
				</td>
			</tr>
<%
if (sorc_shipment) {
%>
			<tr>
				<td class="ui-state-default td-title">出货交付物流时间</td>
				<td class="td-content" colspan="3">${materialForm.sorc_shipment}</td>
			</tr>
<%
}
%>
			<tr>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content" colspan="3" style="position:relative;">
					<textarea id="edit_scheduled_manager_comment" style="width:350px;height:80px;"
<%=("process".equals(from)) ? "" : " readonly disabled"%>
					>${materialForm.scheduled_manager_comment}</textarea>
<%
if ("data".equals(from) || "process".equals(from)) {
%>
					<select name="am_pm" class="ui-widget-content">
						${materialForm.am_pm}
					</select>
<%
}
%>
					<div id="optional_fix_items" style="position:absolute;top:0;right:1em;">
						<div class="ui-state-default" style="padding-left:.5em;">选择修理项目</div>
						<input type="hidden" value="${materialForm.optional_fix_id}">
						<table style="width:10em;">
						</table>
					</div>
					<br>
					<textarea id="edit_material_comment_other" style="width:350px;height:80px;resize:none;display:none;" readonly disabled></textarea>
<%
if (comment_edit) {
%>
					<br>
					<textarea id="edit_material_comment" style="width:350px;height:40px;resize:none;"></textarea>
					<br>
					<input type="button" class="ui-button" value="记录" onclick="javascript:updateMaterialComment();">
<%
}
%>
				</td>
			</tr>
		</tbody>
		</table>
	</div>

	<div class="clear" style="height:22px;"></div>
	<!-- 维修对象作业信息 -->
</div>
<script type="text/javascript">
$("#material_detail_basearea select").select2Buttons();
$("#material_detail_basearea input[readonly]").datepicker({
	showButtonPanel:true,
	dateFormat: "yy/mm/dd",
	currentText: "今天"
});

var optional_fix_resp = $("#optional_fix_items > input:hidden").val();
if (optional_fix_resp && optional_fix_resp.length) {
	var content = '';
	var optional_fix_items = optional_fix_resp.split("；");
	for (var iofi in optional_fix_items) {
		content += '<tr><td class="td-content">' + decodeText(optional_fix_items[iofi]) + '</td></tr>';
	}
	$("#optional_fix_items > table").html(content);
	$("#optional_fix_items").show();
	
} else {
	$("#optional_fix_items").hide();
}

if ($("#edit_material_comment_other").length > 0) {
	var postData = {
		material_id : $("#global_material_id").val(),
		write : $("#edit_material_comment").length
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'material.do?method=getMaterialComment',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages(null, resInfo.errors);
				return;
			} else {
				if (resInfo.material_comment_other) {
					$("#edit_material_comment_other").val(resInfo.material_comment_other).show();
				} else {
					$("#edit_material_comment_other").prev().hide().end().hide();
				}
				if (postData.write) {
					$("#edit_material_comment").val(resInfo.material_comment);
				}
			}
		}
	});
}

<%
if (comment_edit) {
%>

$("#edit_material_comment").nextAll("input[onclick]").button();

var updateMaterialComment = function() {
	var postData = {
		material_id : $("#global_material_id").val(),
		comment : $("#edit_material_comment").val()
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'material.do?method=doUpdateMaterialComment',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages(null, resInfo.errors);
				return;
			} else {
				$("#edit_material_comment").parents(".ui-dialog-content")
				.dialog('close');
			}
		}
	});
}
<%
}
%>
</script>