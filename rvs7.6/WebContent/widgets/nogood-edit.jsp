<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

var nogoodJs = function(){

	var popServicePath = "alarmMessage.do";

	{ // $(function() 
		$("#nogoodform").validate({
			rules : {
				comment : {
					required : true
				}
			}
		});
		$("input.ui-button").button();
		$("#nogoodbtns input:button").mouseover(function() {
			$("#nogoodspns span[for='" + $(this).attr("id") + "']").show();
		});
		$("#nogoodbtns input:button").mouseleave(function() {
			$("#nogoodspns span").hide();
		});
		$("#nogoodclosebtn, #nogoodoperatebtn").click(function(){
			if ($("#nogoodform").valid()) {
				if ($("#break_message_level").val() == 2 && $("#planned_listarea").length > 0) {
					edit_schedule_popMaterialDetail(selectedMaterial.material_id, $("#break_message_level").val() ,true);
				} else {
					if ($("#break_message_level").val() == 2) {
						warningConfirm("请暂且到计划管理页面处理该警报。");
						$("#nogood_treat").dialog("close");
					} else {
						var data = {
							material_id : selectedMaterial.material_id,
							position_id : selectedMaterial.position_id,
							alarm_messsage_id : $("#nogood_id").val(),
							comment : $("#nogood_comment").val()
						};
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : 'alarmMessage.do?method=doreleasebeak',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function() {
								$("#nogood_treat").dialog("close");
								if (typeof(refreshMes) === "function") refreshMes();
								if ($(".ui-jqgrid-title:eq(0)").text() === "警告信息一览") findit();
							}
						});
					}
				}
			}
		});
		$("#nogoodwaitbtn").click(function(){
			if ($("#nogoodform").valid()) {
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'alarmMessage.do?method=dohold',
					cache : false,
					data : {comment : $("#nogood_comment").val(),
						alarm_messsage_id : $("#nogood_id").val()},
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function() {
						$("#alarm_" + $("#nogood_id").val()).hide("slide");
						$("#nogood_treat").dialog("close");
					}
				});
			}
		});
		$("#nogoodfixbtn").click(function(){
			if ($("#nogoodform").valid()) {
				$("#nogoodfixbtn").attr("checked", true);
				selectedMaterial.comment = $("#nogood_comment").val();
				selectedMaterial.append_parts = ($("#append_parts_y").attr("checked") ? 1 : 0);
				process_resign();
				$("#nogood_treat").dialog("close");
			}
		});

		$("#nogoodcommentbtn").click(function(){
			if ($("#nogoodform").valid()) {
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'alarmMessage.do?method=docomment',
					cache : false,
					data : {comment : $("#nogood_comment").val(),
						alarm_messsage_id : $("#nogood_id").val()},
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function() {
						$("#alarm_" + $("#nogood_id").val()).hide("slide");
						$("#nogood_treat").dialog("close");
					}
				});
			}
		});

		$("#nogoodofferbtn").disable();

		if ($("#interfaced").val() !== "true") {
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

							selectedMaterial.alarm_messsage_id = $("#message-detail_id").val();
							selectedMaterial.material_id = resInfo.alarm.material_id;
							selectedMaterial.position_id = resInfo.alarm.position_id;
							selectedMaterial.sorc_no = resInfo.alarm.sorc_no;
							selectedMaterial.model_name = resInfo.alarm.model_name;
							selectedMaterial.serial_no = resInfo.alarm.serial_no;
							selectedMaterial.line_name = resInfo.alarm.line_name;

							$("#nogood_id").val($("#message-detail_id").val());
							$("#nogood_level").text(resInfo.alarm.level);
							$("#nogood_reason").text(resInfo.alarm.reason);
							$("#nogood_occur_time").text(resInfo.alarm.occur_time);
							$("#nogood_sorc_no").text(resInfo.alarm.sorc_no);
							$("#nogood_operator_name").text(resInfo.alarm.operator_name);
							$("#nogood_model_name").text(resInfo.alarm.model_name);
							$("#nogood_serial_no").text(resInfo.alarm.serial_no);
							$("#nogood_section_name").text(resInfo.alarm.section_name);
							$("#nogood_line_name").text(resInfo.alarm.line_name);
							$("#nogood_process_code").text(resInfo.alarm.process_code);
							$("#nogood_position_name").text(resInfo.alarm.position_name);
							
							if ("异常中断" == resInfo.alarm.reason) {
								$("#nogoodclosebtn").show();
								$("#nogoodfixbtn").show();
								$("#nogoodcommentbtn[value=保留警报]").hide();
								$("#nogoodoperatebtn").hide();
							} else if ("品保不通过" == resInfo.alarm.reason) {
								$("#nogoodclosebtn").hide();
								$("#nogoodfixbtn").show();
								$("#nogoodcommentbtn[value=保留警报]").hide();
								$("#nogoodoperatebtn").hide();
							} else {
								$("#nogoodclosebtn").hide();
								$("#nogoodfixbtn").hide();
								$("#nogoodcommentbtn").show();
								$("#nogoodoperatebtn").show();
							}
							var otherCommentHtml = (resInfo.alarm.operator_name == null ? "" : resInfo.alarm.operator_name + ":") + resInfo.alarm.comment;
							
							if (resInfo.alarm.sendations) {
								for (var i = 0; i < resInfo.alarm.sendations.length ; i++) {
									var sendation = resInfo.alarm.sendations[i];

									if (sendation.comment != null && sendation.comment != "")
										otherCommentHtml += '<br>' + (sendation.sendation_name == null ? "" : sendation.sendation_name + ":") + sendation.comment;
								}
							}

							$("#nogood_comment_other").html(otherCommentHtml);
						}
					} catch(e) {
						
					}
				}
			});
		}
	}; // $(function() 
} // nogoodJs

var loadNogoodEditJqueryPlus = function(){
	if (typeof(warningConfirm) === "function") {
		nogoodJs();
	} else {
		loadJs("js/jquery-plus.js", nogoodJs);
	}
}

if (!$.validator) {
	loadJs("js/jquery.validate.min.js", loadNogoodEditJqueryPlus);
} else {
	loadNogoodEditJqueryPlus();
}

</script>


<form id="nogoodform">
	<table class="condform">
		<tbody>
			<tr>
			<td class="ui-state-default td-title">发生时间<input type="hidden" id="interfaced" value="${interfaced}"/><input type="hidden" id="message-detail_id" value="${alarm_messsage_id}"/></td>
			<td class="td-content" id="nogood_occur_time" style="width:300px"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">修理单号</td>
			<td class="td-content" id="nogood_sorc_no"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">型号</td>
			<td class="td-content" id="nogood_model_name"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">机身号</td>
			<td class="td-content" id="nogood_serial_no"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">工程</td>
			<td class="td-content" id="nogood_line_name"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">工位</td>
			<td class="td-content" id="nogood_process_code"></td>
			</tr>
			<tr>
			<td class="ui-state-default td-title">警报原因</td>
			<td class="td-content" id="nogood_reason"></td>
			</tr>
			<tr>
			<tr>
				<td class="ui-state-default td-title">追加订购零件</td>
				<td class="td-content" colspan="3">
					<input class="ui-button" type="radio" name="append_parts" id="append_parts_y"></input><label for="append_parts_y">追加</label>
					<input class="ui-button" type="radio" name="append_parts" id="append_parts_n" checked></input><label for="append_parts_n">不追加</label>
				</td>
			</tr>
			<script type="text/javascript">
				$("#flowcase input[name=append_parts]").button();
			</script>
			<td class="ui-state-default td-title">备注</td>
			<td class="td-content">
				<pre id="nogood_comment_other">
				</pre>
				<textarea name="comment" alt="备注" id="nogood_comment" style="width:230px"></textarea>
			</td>
			</tr>
			<tr>
<% Object interfaced = request.getAttribute("interfaced");
	if (interfaced == null) {
%>
			<td class="ui-state-default td-title">处理方式</td>
			<td id="nogoodbtns" class="td-content">
				<input type="hidden" id="nogood_id"></input>
				<input type="button" id="nogoodcommentbtn" class="ui-button" value="信息确认"></input>
			</td>
<%
	} else {
%>
<% String level = (String) request.getAttribute("level"); %>
<% if("0".equals(level)) { %>
			<td class="ui-state-default td-title">处理方式<input type="hidden" id="break_message_level" value="0"></td>
			<td id="nogoodbtns" class="td-content">
				<input type="hidden" id="nogood_id"></input>
				<input type="button" id="nogoodclosebtn" class="ui-button" value="解除中断"></input>
				<input type="button" id="nogoodfixbtn" class="ui-button" value="返工"></input>
				<input type="button" id="nogoodoperatebtn" class="ui-button" value="警报受理" style="display:none;"></input>
				<input type="button" id="nogoodcommentbtn" class="ui-button" value="保留警报" style="display:none;"></input>
				<input type="button" id="nogoodwaitbtn" class="ui-button" value="报告上级"></input>
			</td>
<% } else if("1".equals(level)) { %>
			<td class="ui-state-default td-title">处理方式<input type="hidden" id="break_message_level" value="1"></td>
			<td id="nogoodbtns" class="td-content">
				<input type="hidden" id="nogood_id"></input>
				<input type="button" id="nogoodclosebtn" class="ui-button" value="解除中断"></input>
				<input type="button" id="nogoodfixbtn" class="ui-button" value="全工程内返工"></input>
				<input type="button" id="nogoodoperatebtn" class="ui-button" value="警报受理" style="display:none;"></input>
				<input type="button" id="nogoodcommentbtn" class="ui-button" value="保留警报" style="display:none;"></input>
				<input type="button" id="nogoodwaitbtn" class="ui-button" value="报告计划员"></input>
			</td>
<% } else if("2".equals(level)) { %>
			<td class="ui-state-default td-title">处理方式<input type="hidden" id="break_message_level" value="2"></td>
			<td id="nogoodbtns" class="td-content">
				<input type="hidden" id="nogood_id"></input>
				<input type="button" id="nogoodclosebtn" class="ui-button" value="解除中断"></input>
				<input type="button" id="nogoodoperatebtn" class="ui-button" value="警报受理" style="display:none;"></input>
				<input type="button" id="nogoodcommentbtn" class="ui-button" value="保留警报" style="display:none;"></input>
			</td>
<% } %>
<% } %>
			</tr>
			<tr style="min-height:120px;">
				<td class="ui-state-default td-title">处理说明</td>
				<td id="nogoodspns" class="td-content">
					<span for="nogoodclosebtn" style="display:none">确认中断的原因不存在或可直接排除，原处理工位继续重开作业。</span>
					<span for="nogoodoperatebtn" style="display:none">产生不良的原因已经排除。维修对象已可在原工位继续重开作业。</span>
					<span for="nogoodcommentbtn" style="display:none">了解警报，输入对策或备注但不处理，警报将保留。</span>
					<span for="nogoodfixbtn" style="display:none">执行返工，指定工程再开工位。完成后警报解除。</span>
					<span for="nogoodwaitbtn" style="display:none">处理条件不足，记下本人处理意见后，暂时等待或分析。警报将保留。</span>
				</td>
			</tr>
		</tbody>
	</table>
</form>