<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style type="text/css">
#flowtext .textlink {
	margin : 0 2px;
}
#flowtext .textlink .ui-button-text {
	padding: .2em .4em;
}
#pos_table td.wip-empty.storage-recomment {
	background-color: lightgreen;
	animation: twinkling 1s infinite ease-in-out;
}
</style>
<script type="text/javascript">
$(function() {

	$("#pos_table").on("click", ".wip-empty", function(){
		doPutinLocation($("#pos_table").attr("material_id"), 
				$("#pos_table").attr("position_id"),
				this.innerText);
	})

});
var setChooseStorageButtons = function() {
	var $links = $("#flowtext .textlink").button();
	$links.click(getLocationMap);
};

var getLocationMap = function(evt) {
	
	var postPositionId = $(this).attr("position_id");

	var postData = {
		material_id : $("#pauseo_material_id").val(),
		position_id : postPositionId,
		process_code : $(this).attr("process_code")
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'disassemble_storage.do?method=getLocationMap',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			getLocationMap_handleComplete(xhrobj, postData);
		}
	});
}

var getLocationMap_handleComplete = function(xhrobj, rowData) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		
		return;
	}

	$("#pos_storage").text(rowData.process_code + " 库位一览");
	$("#pos_table").html(resInfo.storageHtml)
		.attr({material_id : rowData.material_id,
			position_id : rowData.position_id});

	var $dialog = $("#storage_pop").dialog({
		title : "选择库位",
		width : 920,
		height : 480,
		resizable : false,
		modal : true,
		buttons : {
			"关闭" : function(){
				$dialog.dialog("close");
			}
		}
	});
}

/** 改变库位* */
var doPutinLocation = function(material_id, position_id, case_code) {
	var postData = {
		material_id : material_id,
		position_id : position_id,
		case_code : case_code
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'disassemble_storage.do?method=doPutin',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doPutinLocation_handleComplete
	});
};

var doPutinLocation_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		return;
	}
	if (resInfo.fingers) {
		flowtext = decodeText(resInfo.fingers) + (resInfo.past_fingers ? "<br>" + resInfo.past_fingers : "");
		$("#flowtext").html(flowtext);
		setChooseStorageButtons();
	}
	$("#storage_pop").dialog("close");
}

var checkStorageSent = function(){
	if ($("#flowtext .textlink").length > 0) {
		errorPop("请选择维修品在后道工位存放库位。");
		return true;
	}
	return false;
}

</script>
<div id="storage_pop" style="display: none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span id="pos_storage" class="areatitle">库位一览</span>
	</div>
	<div class="ui-widget-content" id="pos_table">
	</div>
	<div class="clear areaencloser"></div>
</div>