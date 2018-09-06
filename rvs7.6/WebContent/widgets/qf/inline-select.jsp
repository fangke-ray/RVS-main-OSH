<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<link rel="stylesheet" type="text/css" href="css/flowchart.css">

<script type="text/javascript">
function showedit_handleComplete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#pa_main").flowchart("fill", resInfo.processAssigns);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

	$(function() {
		$("#search_section_id").select2Buttons();
		$("#ref_template").select2Buttons();

//		$("#s2b_search_section_id a:contains('2课')").click(function(){
//			var thistr = $(this).parent().parent().parent().parent().parent();
//			thistr.nextAll("tr").hide();
//		});
//		$("#s2b_search_section_id a:not(:contains('2课'))").click(function(){
//			var thistr = $(this).parent().parent().parent().parent().parent();
//			thistr.nextAll("tr").show();
//		});
		
		$("#ref_template").change(function(){
			if (this.value === "") {
				$("#pa_main").html("");
			} else {
				var data = {
					"id" : this.value
				};
			
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=getPa',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : showedit_handleComplete
				});				
			}
		});
	});
</script>
	<form id="inlineForm">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title">修理单号</td>
				<td class="td-content" id="is_sorc">
				</td>
				<td class="ui-state-default td-title">等级</td>
				<td class="td-content" id="is_level">
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">投线课室</td>
				<td class="td-content" style="width:242px;" colspan="3">
					<select name="section_id" id="search_section_id" class="ui-widget-content">
						<%=session.getAttribute("sOptions") %>
					</select>
				</td>
			</tr>
			<tr style="min-height:120px;">
				<td class="ui-state-default td-title">设定维修流程</td>
				<td class="td-content" style="width:360px" colspan="3">
					<select name="ref_template" id="ref_template" class="ui-widget-content">
						<%=session.getAttribute("paOptions") %>
					</select>
				</td>
			</tr>
			<tr style="min-height:120px;">
				<td class="td-content" colspan="7">
					<div id="pa_main" class="chartarea">
					</div>
				</td>
			</tr>
			</tbody>
		</table>
	</form>
	

	