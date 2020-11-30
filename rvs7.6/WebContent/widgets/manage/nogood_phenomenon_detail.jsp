<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	Boolean enableEdit = (Boolean)request.getAttribute("enableEdit");
	Boolean enablePost = (Boolean)request.getAttribute("enablePost");
	String locationMap = (String)request.getAttribute("locationMap");
%>

<style>
#location_labels {
	margin: 6px 0;
	padding-left: 12px;
}
#location_labels > li {
	cursor: pointer;
	color: white;
	display: table;
	padding: 0 2px;
	margin-bottom: 2px;
}
#location_labels[target=location_group_desc] > li {
	background-color : navy;
}
#location_labels[target=location_desc] > li {
	background-color : green;
}
</style>
<script type="text/javascript">
var popServicePath = "new_phenomenon.do";

	var newPhenomenonJsInit = function(){
<% 
if(enableEdit != null) {
%>
	var np_updRecord_handleComplete = function(xhrObj) {
		var resInfo = $.parseJSON(xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length) {
			// 共通出错信息框
			treatBackMessages("#newPhenoform", resInfo.errors);
		} else {
			$("#nongood_phenomenon").dialog("close");
			if ($("#nongood_phenomenon").data("callback") && typeof($("#nongood_phenomenon").data("callback")) === "function") {
				$("#nongood_phenomenon").data("callback")();
			}
		}
	}

	var np_updRecord = function(post) {
		var postData = {
			key: $("#detail\\.key").val(),
			location_group_desc : $("#detail\\.location_group_desc").val(),
			location_desc : $("#detail\\.location_desc").val(),
			description : $("#detail\\.description").val()
		};

<% if (enablePost) { %>
		if (post === "doit") postData.post = "true";
<% } %>

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : popServicePath + '?method=doUpdate',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : np_updRecord_handleComplete
		});
	}

	$("#np_recordbutton").click(function(){
		np_updRecord(false);
	});

<% if (enablePost) { %>
	$("#np_recordsendbutton").click(function(){
		np_updRecord("doit");
	});
<% } %>

<% 
if(locationMap != null) {
%>

	var locationMap = $.parseJSON('${locationMap}');

	var groupArr = [];
	for (var itm in locationMap) {
		groupArr.push(itm);
	}

$("#detail\\.location_group_desc").focus(function(){
	var $location_labels = $("#location_labels");
	$location_labels.attr("target", "location_group_desc");
	var itemsHtml = "";
	for (var idx in groupArr) {
		itemsHtml += "<li>" + groupArr[idx] + "</li>"
	}
	$location_labels.html(itemsHtml);
});

$("#detail\\.location_desc").focus(function(){
	var $location_labels = $("#location_labels");
	$location_labels.attr("target", "location_desc");
	var group = $("#detail\\.location_group_desc").val();
	var itemsHtml = "";

	var locationsOfGroup = locationMap[group];
	if (locationsOfGroup) {
		for (var idx in locationsOfGroup) {
			itemsHtml += "<li>" + locationsOfGroup[idx] + "</li>"
		}
	}
	if (!itemsHtml) {
		itemsHtml = "<li>其他</li>"
	}

	$location_labels.html(itemsHtml);
});

$("#location_labels").on("click", "li", function(){
	$("#detail\\." + $("#location_labels").attr("target")).val(this.innerText);
})

<% 
} // locationMap
%>
<% 
} // enableEdit
%>

} 

$("#np_closebutton").click(function(){
	$("#nongood_phenomenon").dialog("close");
});

var loadNewPhenomenonJs = function(){
	if (typeof(warningConfirm) === "function") {
		newPhenomenonJsInit();
	} else {
		loadJs("js/jquery-plus.js", newPhenomenonJsInit);
	}
}

loadNewPhenomenonJs();

</script>

<form id="newPhenoform">
	<div id="newPhenoformBaseArea" style="margin-top:16px;margin-left:0;margin-bottom:16px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">维修品信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content">
						<span id="detail.omr_notifi_no">${newPhenomenonForm.omr_notifi_no}</span>
						<input type="hidden" id="detail.key" value="${newPhenomenonForm.key}"></input>
					</td>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content">
						<span id="detail.level_name">${newPhenomenonForm.level_name}</span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<span id="detail.model_name">${newPhenomenonForm.category_name}</span>
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<span id="detail.serial_no">${newPhenomenonForm.serial_no}</span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">不良提出时间</td>
					<td class="td-content">
						<span id="detail.occur_time">${newPhenomenonForm.occur_time}</span>
					</td>
					<td class="ui-state-default td-title">不良提出工程</td>
					<td class="td-content">
						<span id="detail.line_name">${newPhenomenonForm.line_name}</span>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">故障内容</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
<% if (enableEdit) { %>
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">故障部件组名称</td>
					<td class="td-content">
						<input type="text" maxlength="45" class="ui-widget-content" style="outline: 4px solid navy; margin-left:6px;" id="detail.location_group_desc" value="${newPhenomenonForm.location_group_desc}"></input>
					</td>
					<td class="td-content" rowspan="2" colspan="2" style="width: 345px;">
						<ul id="location_labels" style="min-height:200px; max-height: 200px; overflow: auto;"></ul>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障部位名称</td>
					<td class="td-content">
						<input type="text" maxlength="45" class="ui-widget-content" style="outline: 4px solid green; margin-left:6px;" id="detail.location_desc" value="${newPhenomenonForm.location_desc}"></input>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障描述</td>
					<td class="td-content" colspan="3">
						<input type="text" maxlength="45" class="ui-widget-content" id="detail.description" style="width:480px;" value="${newPhenomenonForm.description}"></input>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">报告者</td>
					<td class="td-content">
						<span id="detail.operator_name">${newPhenomenonForm.operator_name}</span>
					</td>
					<td class="ui-state-default td-title">决定者</td>
					<td class="td-content">
						<span id="detail.determine_operator_name">${newPhenomenonForm.determine_operator_name}</span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">决定日期</td>
					<td class="td-content">
						<span id="detail.last_determine_date">${newPhenomenonForm.last_determine_date}</span>
					</td>
					<td class="ui-state-default td-title">发送回应</td>
					<td class="td-content">
						<span id="detail.return_status">${newPhenomenonForm.return_status}</span>
					</td>
				</tr>
			</tbody>
		</table>
		<div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix">
			<div class="ui-dialog-buttonset">
			<button type="button" class="ui-button" role="button" aria-disabled="false" id="np_recordbutton">记录</button>
			<% if (enablePost) { %>
				<button type="button" class="ui-button" role="button" aria-disabled="false" id="np_recordsendbutton">记录并且发送</button>
			<% } %>
			<button type="button" class="ui-button" role="button" aria-disabled="false" id="np_closebutton">取消</button>
			</div>
		</div>
<% } else { %>
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">故障部件组名称</td>
					<td class="td-content">
						<span id="detail.location_group_desc">${newPhenomenonForm.location_group_desc}</span>
					</td>
					<td class="ui-state-default td-title">故障部位名称</td>
					<td class="td-content">
						<span id="detail.location_desc">${newPhenomenonForm.location_desc}</span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障描述</td>
					<td class="td-content"  colspan="3">
						<span id="detail.description">${newPhenomenonForm.description}</span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">决定者</td>
					<td class="td-content">
						<span id="detail.determine_operator_name">${newPhenomenonForm.determine_operator_name}</span>
					</td>
					<td class="ui-state-default td-title">决定日期</td>
					<td class="td-content">
						<span id="detail.last_determine_date">${newPhenomenonForm.last_determine_date}</span>
					</td>
				</tr>
			</tbody>
		</table>
<% } %>
	</div>
	</div>
<script type="text/javascript">
$(function() {
	$("#newPhenoform .ui-button").button();
});
</script>
</form>