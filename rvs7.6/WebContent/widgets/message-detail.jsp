<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache"> <meta http-equiv="Cache-Control" content="no-cache">

<title>警报信息</title>
<script type="text/javascript">
	$(function() {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'alarmMessage.do?method=detailInit',
			cache : false,
			data : {alarm_messsage_id : $("#message-detail_id").val()},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhjobj) {
				var resInfo = null;
				try {
					eval("resInfo=" + xhjobj.responseText);
					if (resInfo.alarm) {
						$("#nogood_level").text(resInfo.alarm.level);
						$("#nogood_reason").text(resInfo.alarm.reason);
						$("#nogood_occur_time").text(resInfo.alarm.occur_time);
						$("#nogood_sorc_no").text(resInfo.alarm.sorc_no);
						$("#nogood_operator_name").text(resInfo.alarm.operator_name);
						$("#nogood_model_name").text(resInfo.alarm.model_name);
						$("#nogood_section_name").text(resInfo.alarm.section_name);
						$("#nogood_line_name").text(resInfo.alarm.line_name);
						$("#nogood_process_code").text(resInfo.alarm.process_code);
						$("#nogood_position_name").text(resInfo.alarm.position_name);
						$("#nogood_comment").text(resInfo.alarm.comment);
						
						if (resInfo.alarm.sendations) {
							var sendationsHtml = "";
							for (var i = 0; i < resInfo.alarm.sendations.length ; i++) {
								var sendation = resInfo.alarm.sendations[i];
								sendationsHtml += '<tr><td class="ui-state-default td-title">处理者</td><td class="td-content-text">'+ sendation.sendation_name +'</td>';
								sendationsHtml += '<td class="ui-state-default td-title">处理时间</td><td class="td-content-text">' + sendation.resolve_time + '</td></tr>'
								sendationsHtml += '<tr><td class="ui-state-default td-title">处理备注</td>'
								sendationsHtml += '<td class="td-content-text" colspan="3">' + sendation.comment + '</td></tr>'
								
								if (i < resInfo.alarm.sendations.length - 1) sendationsHtml += '<tr style="height:20px;"/>'
							}
							$("#sendationsTable").html(sendationsHtml);
						}
					}
				} catch(e) {
					
				}
				// $("#nogood_treat").dialog("close");
			}
		});
	});
</script>
</head>
<body>
	<div style="margin:auto;">

<div id="position_detail_basearea" class="dwidth-middle">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">警报信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title"><input type="hidden" id="message-detail_id" value="${alarm_messsage_id}"/>等级</td>
				<td class="td-content-text" id="nogood_level"></td>
				<td class="ui-state-default td-title">原因</td>
				<td class="td-content-text" id="nogood_reason"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">发生时间</td>
				<td class="td-content-text" id="nogood_occur_time"></td>
				<td class="ui-state-default td-title">担当人</td>
				<td class="td-content-text" id="nogood_operator_name"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">修理单号</td>
				<td class="td-content-text" id="nogood_sorc_no">SORC-124453-SY</td>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content-text" id="nogood_model_name">CF-H260AZI</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">课室</td>
				<td class="td-content-text" id="nogood_section_name"></td>
				<td class="ui-state-default td-title">工程</td>
				<td class="td-content-text" id="nogood_line_name"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">工位号</td>
				<td class="td-content-text" id="nogood_process_code"></td>
				<td class="ui-state-default td-title">工位名称</td>
				<td class="td-content-text" id="nogood_position_name"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">关联信息</td>
				<td class="td-content-text" colspan="3" id="nogood_comment"></td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear areaencloser dwidth-middle"></div>
</div>


<div id="position_detail_listarea" class="dwidth-middle">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">警报处理信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<table class="condform" id="sendationsTable">
		</table>
	</div>
</div>

<div id="position_detail_dialog">
</div>

<div class="clear areacloser"></div>
</div>

</body></html>