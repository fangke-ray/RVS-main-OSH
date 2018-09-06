<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
	#delay_reason_dialog table tr td {
		padding: 0 .2em;
	}
	#delay_reason_dialog table tr td:nth-child(n+3) {
		cursor: pointer;
	}
	#delay_reason_dialog table tr > td:nth-child(2) {
		width:20px;
		border:none;
	}
	#delay_reason_dialog table tr > :nth-child(1),
	#delay_reason_dialog table tr > :nth-child(4) {
		display:none;
	}
	#delay_reason_dialog.hasCause table tr > :nth-child(1) {
		display: table-cell;
	}
	td.impossible {
		background-color:gray;
	}
</style>
<script>
var showDelayReasonDialog = function(causes, callback) {
	var $delay_reason_dialog = $("#delay_reason_dialog");
	if (causes) {
		$("#delay_reason_dialog").addClass("hasCause");
		var causesObj = $.parseJSON(causes);
		$('#delay_reason_dialog table tr[cause]').find("td:eq(0)").text("-").addClass("impossible");
		for (var idx in causesObj) {
			if (causesObj[idx]) 
				$('#delay_reason_dialog table tr[cause="' + idx + '"]').find("td:eq(0)").text(causesObj[idx]).removeClass("impossible");
		}
	} else {
		$("#delay_reason_dialog").removeClass("hasCause");
	}
	$delay_reason_dialog.dialog({
		modal : true,
		resizable:false,
		width : "420px",
		title : "延误理由判定",
		closeOnEscape: false,
		buttons :{
			"判定延误理由并完成维修":function(evt) {
				if ($delay_reason_dialog.find("td.ui-state-highlight").length == 0) {
					$delay_reason_dialog.find("div:eq(1)").show();
				} else {
					$delay_reason_dialog.find("div:eq(1)").hide();
					var postData = {
						main_cause : $delay_reason_dialog.find("td.ui-state-highlight").parent().attr("cause"),
						dr_need_comment : $("#dr_need_comment").attr("checked")
					}
					$delay_reason_dialog.dialog("close");
					callback(evt, postData);
				}
			}
		}
	});
}

$(function(){
	$("#delay_reason_dialog tr").find("td:gt(1)").click(function(){
		$("#delay_reason_dialog tr td").removeClass("ui-state-highlight");
		$(this).parent().find("td:gt(1)").addClass("ui-state-highlight");
//					.sibling().removeClass("ui-state-highlight");	
	});
});
</script>
<div id="delay_reason_dialog" style="display:none;">
	<div>对比倒计时计划，当前维修对象在本工位开始延误。请选择主要的理由：</div>
	<div style="display:none;color:red;">请选择一项理由。</div>
	<table class="subform">
		<tr><th class="ui-state-default">现状参考</th><th></th><th class="ui-state-default">理由</th><th class="ui-state-default">状况说明</th></tr>
		<tr cause="1"><td>-</td><td></td><td>作业工时延误</td><td>在本工位的作业时间超出标准工时导致。</td></tr>
		<tr cause="2"><td>-</td><td></td><td>零件延误</td><td>由于在本工程投线后，还有相关零件未发放，导致等待。</td></tr>
		<tr cause="3"><td>-</td><td></td><td>仕挂量多</td><td>在本工位由于其他优先的待维修对象过多，导致延后开始。</td></tr>
		<tr cause="4"><td style="background-color:white!important;border:none;"></td><td></td><td>人手不足</td><td>由于本工位人员休假或者其他工作安排；<br>或者当前人员需要同时代工其他工位工作，<br>导致本工位维修不能持续开展。</td></tr>
		<tr cause="5"><td>-</td><td></td><td>异常中断</td><td>发生异常中断后，处理消耗了时间，或者发生了返工。</td></tr>
	</table>
	<div style="text-align:right;"><input type="checkbox" id="dr_need_comment"><label for="dr_need_comment">通知线长确认</label></div>
</div>