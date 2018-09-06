<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/flowchart.css">
<script type="text/javascript" src="js/jquery.flowchart.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript">
$(function() {
	$("#rework_pat_id").select2Buttons();

	$("#rework_pat_id").change(function(){

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'material.do?method=getFlowchart',
			cache : false,
			data : {material_id : selectedMaterial.material_id, pat_id : this.value},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus){
				getFlowchart_handleComplete(xhrobj, textStatus, setRework);
			}
		});
	});

	var setRework = function(){
		var $reworks = $("#pa_red .rework");
		var $reworkradio = $("#flowcase input[name=reworked]");
		if ($reworkradio.length == 0) return;

		if ($reworks.length > 0) {
			$reworkradio.eq(0).attr("checked", true);
			$reworkradio.eq(1).removeAttr("checked");
			$reworkradio.disable().trigger("change");
		} else {
			$reworkradio.enable().trigger("change");
		}

		// 单追组件记录到工程检查票
		if ($("#rework_pat_id").find("option[selected]").text().indexOf("单追组件") >= 0) {
			$("#pcs_signed_y").attr("checked", true).trigger("change");
		}

	}

	$("#pa_red .suceed").live("click", setRework);

//	$("#pa_red .rework").length()
});
</script>
<form>
	<table class="condform" id="flowcase">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">修理单号</td>
				<td class="td-content" id="rework_sorc_no"></td>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content" id="rework_model_name"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">机身号</td>
				<td class="td-content" id="rework_serial_no"></td>
				<td class="ui-state-default td-title">工程</td>
				<td class="td-content" id="rework_line_name"></td>
			</tr>
<% 	String level = (String) request.getAttribute("level");
	if("1".equals(level)) {
%>
			<tr>
				<td class="ui-state-default td-title">维修流程</td>
				<td class="td-content" colspan="3">
					<select id="rework_pat_id" neo="true">${patOptions}</select>
					<input type="hidden" id="hdn_pat_id" />
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">进行返工</td>
				<td class="td-content">
					<input class="ui-button" type="radio" name="reworked" id="reworked_y"></input><label for="reworked_y">返工</label>
					<input class="ui-button" type="radio" name="reworked" id="reworked_n" checked></input><label for="reworked_n">不返工</label>
				</td>
				<td class="ui-state-default td-title">处理记载到<br>总组工程检查票</td>
				<td class="td-content">
					<input class="ui-button" type="radio" name="pcs_signed" id="pcs_signed_y"></input><label for="pcs_signed_y">记载</label>
					<input class="ui-button" type="radio" name="pcs_signed" id="pcs_signed_n" checked></input><label for="pcs_signed_n">不记载</label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">追加订购零件</td>
				<td class="td-content" colspan="3">
					<input class="ui-button" type="radio" name="append_parts" id="reworked_append_parts_y" value="1"></input><label for="reworked_append_parts_y">追加</label>
					<input class="ui-button" type="radio" name="append_parts" id="reworked_append_parts_n" value="0"></input><label for="reworked_append_parts_n">不追加</label>
				</td>
			</tr>
			<tr style="display:none;">
				<td class="ui-state-default td-title">中小修理内容</td>
				<td class="td-content" colspan="2" id="light_fix_content">
				</td>
				<td><input type='button' type='ui-button' id='ref_allpos' value='选择全部工位'></td>
			</tr>
			<script type="text/javascript">
				$("#flowcase input[name=reworked]").button();
				$("#flowcase input[name=pcs_signed]").button();
				$("#flowcase input[name=append_parts]").button();
				// $("#pa_red .rework").length()
			</script>
<% } %>
			<tr style="min-height: 120px;">
				<td class="ui-state-default td-title" colspan="4">再开工位指定</td>
			</tr>
			<tr style="min-height: 120px;">
				<td class="td-content" colspan="4">
					<div id="pa_red" class="chartarea">
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</form>