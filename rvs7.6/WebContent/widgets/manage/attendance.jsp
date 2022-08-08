<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<style>
#attendance_form input[type=number] {
	width : 5em;
	text-align: right;
	ime-mode: disabled;
}
#attendance_form textarea {
	width : 25em;
	height : 4em;
}
</style>
<%
	String role = (String) request.getAttribute("role");
	String section_id = (String) request.getAttribute("section_id");
	String line_id = (String) request.getAttribute("line_id");
	if ("none".equals(role)) {
		section_id = "0"; line_id = "0";
	} else if ("manager".equals(role)) {
		section_id = null;
	}
%>
	<form id="attendance_form">
		<input type="hidden" id="attendance_section_id" value="${section_id}"/>
		<select id="attendance_section">
<% if (section_id == null || section_id.equals("00000000009")) { %>
			<option value="00000000009">受理报价课</option>
<% } %>
<% if (section_id == null || section_id.equals("00000000001")) { %>
			<option value="00000000001">修理一课</option>
<% } %>
<% if (section_id == null || section_id.equals("00000000003")) { %>
			<option value="00000000003">修理二课</option>
<% } %>
		</select>
<% if (section_id == null || section_id.equals("00000000009")) { %>
		<div class="page" section_id="00000000009">
<% if (line_id == null || line_id.equals("00000000011")) { %>
			<table line_id="00000000011" class="condform" style="width:100%">
				<thead>
					<tr>
						<td class="ui-state-default td-title" colspan="4">受理/报价</td>
					</tr>
				</thead>
				<tbody>
				 <tr>
					<th class="ui-state-default td-title">分类</td>
					<th class="ui-state-default td-title">编制人数</td>
					<th class="ui-state-default td-title">实勤人数</td>
					<th class="ui-state-default td-title">详细说明</td>
				 </tr>
				 <tr>
					<td class="ui-state-default td-title">受理组</td>
					<td class="td-content">
						<input type="number" px="1" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="1" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="1"></textarea>
					</td>
				 </tr>
				 <tr>
					<td class="ui-state-default td-title">报价组</td>
					<td class="td-content">
						<input type="number" px="2" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="2" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="2"></textarea>
					</td>
				 </tr>
				</tbody>
			</table>
<% } %>
<% if (line_id == null || line_id.equals("00000000020")) { %>
			<table line_id="00000000020" class="condform" style="width:100%">
				<thead>
					<tr>
						<td class="ui-state-default td-title" colspan="4">物料</td>
					</tr>
				</thead>
				<tbody>
				 <tr>
					<th class="ui-state-default td-title">分类</td>
					<th class="ui-state-default td-title">编制人数</td>
					<th class="ui-state-default td-title">实勤人数</td>
					<th class="ui-state-default td-title">详细说明</td>
				 </tr>
				 <tr>
					<td class="ui-state-default td-title">物料组</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				 </tr>
				</tbody>
			</table>
<% } %>
		</div><!-- section_id="00000000009" -->
<% } %>
<% if (section_id == null || section_id.equals("00000000001")) { %>
		<div class="page" section_id="00000000001">
			<table line_id="00000000012" class="condform" style="width:100%">
				<thead>
					<tr>
						<td class="ui-state-default td-title" colspan="4">修理一课</td>
					</tr>
				</thead>
				<tbody>
				 <tr>
					<th class="ui-state-default td-title">分类</td>
					<th class="ui-state-default td-title">编制人数</td>
					<th class="ui-state-default td-title">实勤人数</td>
					<th class="ui-state-default td-title">详细说明</td>
				 </tr>
<% if (line_id == null || line_id.equals("00000000012")) { %>
				 <tr>
					<td class="ui-state-default td-title">分解工程</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				 </tr>
<% } %>
				</tbody>
			</table>
			<table line_id="00000000013" class="condform" style="width:100%">
<% if (line_id == null || line_id.equals("00000000013")) { %>
				 <tr>
					<td class="ui-state-default td-title">ＮＳ工程</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				 </tr>
<% } %>
			</table>
<% if (line_id == null || line_id.equals("00000000014")) { %>
			<table line_id="00000000014" class="condform" style="width:100%">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">总组 A 线</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">总组 B1 线</td>
					<td class="td-content">
						<input type="number" px="4" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="4" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="4"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">总组 B2 线</td>
					<td class="td-content">
						<input type="number" px="7" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="7" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="7"></textarea>
					</td>
				</tr>
				</tbody>
			</table>
<% } %>
		</div><!-- section_id="00000000001" -->
<% } %>
<% if (section_id == null || section_id.equals("00000000003")) { %>
		<div class="page" section_id="00000000003">
			<table line_id="00000000012" class="condform" style="width:100%">
				<thead>
					<tr>
						<td class="ui-state-default td-title" colspan="4">修理二课</td>
					</tr>
				</thead>
<% if (line_id == null || line_id.equals("00000000012")) { %>
				<tbody>
				 <tr>
					<th class="ui-state-default td-title">分类</td>
					<th class="ui-state-default td-title">编制人数</td>
					<th class="ui-state-default td-title">实勤人数</td>
					<th class="ui-state-default td-title">详细说明</td>
				 </tr>
				 <tr>
					<td class="ui-state-default td-title">分解工程</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				 </tr>
				</tbody>
<% } %>
			</table>
<% if (line_id == null || line_id.equals("00000000013")) { %>
			<table line_id="00000000013" class="condform" style="width:100%">
				 <tr>
					<td class="ui-state-default td-title">ＮＳ工程</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				 </tr>
			</table>
<% } %>
<% if (line_id == null || line_id.equals("00000000014")) { %>
			<table line_id="00000000014" class="condform" style="width:100%">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">中小修</td>
					<td class="td-content">
						<input type="number" px="0" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="0" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="0"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">纤维镜</td>
					<td class="td-content">
						<input type="number" px="3" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="3" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="3"></textarea>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">硬性镜</td>
					<td class="td-content">
						<input type="number" px="6" record_type="0"></input>
					</td>
					<td class="td-content">
						<input type="number" px="6" record_type="1"></input>
					</td>
					<td class="td-content">
						<textarea px="6"></textarea>
					</td>
				</tr>
				</tbody>
			</table>
<% } %>
		</div><!-- section_id="00000000003" -->
<% } %>
	</form>
<script type="text/javascript">
	var doUpdateAttendance = function(){
		var postData = {};

		$("#attendance_form input[changed=true]").each(function(idx,ele){		
			var $ele = $(ele);
			postData["update.line_id["+idx+"]"] = $ele.closest("[line_id]").attr("line_id");
			postData["update.px["+idx+"]"] = $ele.attr("px");
			postData["update.record_type["+idx+"]"] = $ele.attr("record_type");
			postData["update.attendance["+idx+"]"] = ele.value;
			postData["update.section_id["+idx+"]"] = $ele.closest("[section_id]").attr("section_id");
		});

		$("#attendance_form textarea[changed=true]").each(function(idx,ele){		
			var $ele = $(ele);
			postData["remarks.line_id["+idx+"]"] = $ele.closest("[line_id]").attr("line_id");
			postData["remarks.px["+idx+"]"] = $ele.attr("px");
			postData["remarks.remark["+idx+"]"] = ele.value;
			postData["remarks.section_id["+idx+"]"] = $ele.closest("[section_id]").attr("section_id");
		});

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'scheduleProcessing.do?method=doUpdateAttendance',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				$("#attendance_form").closest(".ui-dialog-content").dialog("close"); 
			}
		});
	}

	if ($("#attendance_form .page").length > 1) {
		$("#attendance_section")
			.change(function(){
				$("#attendance_form .page").hide()
					.filter("[section_id=" + this.value + "]").show();
			});
		if ($.fn.select2Buttons == undefined) {
			loadCss("css/olympus/select2Buttons.css",
				function(){
					loadJs("js/jquery.select2buttons.js",
					function(){
						$("#attendance_section").select2Buttons();
					})
				});
		} else {
			$("#attendance_section").select2Buttons();
		}
		if ($("#attendance_section > option[value=" + $("#attendance_section_id").val() + "]").length > 0) {
			$("#attendance_section").val($("#attendance_section_id").val()).trigger("change");
		} else {
			$("#attendance_section").val($("#attendance_section > option:eq(0)").val()).trigger("change");
		}
	} else {
		$("#attendance_section").remove();
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'scheduleProcessing.do?method=getAttendance',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages(null, resInfo.errors);
				return;
			}

			var headcounts = resInfo.headcounts;
			if (headcounts) {
				for(var j = 0;j<headcounts.length;j++){
					var headcount = headcounts[j];
					$("#attendance_form .page[section_id=" + headcount.section_id 
						+ "] table[line_id=" + headcount.line_id + "] input[px=" + headcount.px + "][record_type=0]")
						.val(headcount.clue_count);
				}
			}

			var today_attends = resInfo.today_attends;
			if (today_attends) {
				for(var j = 0;j<today_attends.length;j++){
					var today_attend = today_attends[j];
					$("#attendance_form .page[section_id=" + today_attend.section_id 
						+ "] table[line_id=" + today_attend.line_id + "] input[px=" + today_attend.px + "][record_type=1]")
						.val(today_attend.clue_count);
				}
			}

			var today_remarks = resInfo.today_remarks;
			if (today_remarks) {
				for(var j = 0;j<today_remarks.length;j++){
					var today_remark = today_remarks[j];
					$("#attendance_form .page[section_id=" + fillZero(today_remark.section_id, 11)
						+ "] table[line_id=" + fillZero(today_remark.line_id, 11) + "] textarea[px=" + today_remark.px + "]")
						.val(today_remark.attendance_comment);
				}
			}

			$("#attendance_form input[type='number']").change(function(){
				$(this).attr("changed", "true");
		        var ival = $(this).val();
		        if (ival.match(/^[0-9.]*$/) == null) {
					var msgInfos=[];
					var msgInfo={};
					msgInfo.errmsg = "请输入0-9之间的数字";
					msgInfos.push(msgInfo);
					treatBackMessages("", msgInfos);
					$(this).val("");
		        }
			});

			$("#attendance_form textarea").change(function(){
				$(this).attr("changed", "true");
			});
		}
	});
</script>