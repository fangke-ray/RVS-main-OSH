<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
table.condform .diGrp td {
	border-top-width: 0;
	border-bottom-width: 0;
}
table.condform .diGrp:last-child td,
table.condform .diGrp td[rowspan] {
	border-bottom-width: 1px;
}
</style>
<script type="text/javascript" src="js/infect/infect_sheet.js"></script>
<script>
	var ppdiServicePath = "position_panel.do";

	$(function() {
		$("#finishcheckbutton").click(function() {
			var data = {};
			var i = 0;
			var checkDup = {};
			var isDup = false;
			$("#device_details table tbody").find("tr").each(function(){
				var $tr = $(this);
				var seq = $tr.find(".seq").val();
				var selected_value = $tr.find(".manageCode").val();
				var manage_id = "";
				if (selected_value != "") {
					var temp = selected_value.split(",");
					manage_id = temp[0];
					if (data[manage_id]) isDup = true;
					data[manage_id] = 1;
					data["finishcheck.manage_id[" + i + "]"] = manage_id;
					data["finishcheck.seq[" + i + "]"] = seq;
					i++;
				}
			});

			if (isDup) {
				errorPop("请不要选择重复的设备。");
				return;
			}

			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : ppdiServicePath + '?method=doFinishcheck',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					var resInfo = null;
					eval('resInfo =' + xhrobj.responseText);

					$("#device_details table tbody").find(".manageCode").disable();
					$("#device_details table tbody").find("input[type=button]").disable();
					$("#finishcheckbutton").disable();
					$("#hidden_workstauts").val(resInfo.workstauts);

					treatStart(resInfo);
				}
			});
		});
	});

	var showPeripheral = function(resInfo) {
		var tbodyContent = "";
		$.each(resInfo.peripheralData,function(index,peripheralData){
			var normal = (peripheralData.group >=0) + (peripheralData.group > 0);
			tbodyContent += '<tr' + (normal == 1 ? '' : ' class="diGrp"') + '>'
				+ (normal ? '<td class="td-content" style="width:auto;"' 
					+ ((normal > 1) ? ' rowspan="' + peripheralData.group + '"' : '') + '>' + peripheralData.seq + '</td>' : '')
				+'<td class="td-content">' + peripheralData.device_type_name + '</td>'
				+'<td class="td-content">' + peripheralData.model_name + '</td>'
				+'<td class="td-content"><label class="check_manage_code">' + peripheralData.check_manage_code + '</label></td>'
				+'<td class="td-content"><select class="manageCode" seq="' + peripheralData.seq + '">' + peripheralData.manageCodeOptions + '</select></td>'
				+ (normal ? '<td class="td-content"' 
					+ ((normal > 1) ? ' rowspan="' + peripheralData.group + '"' : '') + '><label class="check_result">' + peripheralData.check_result + '</label></td>' : '')
				+'<td><input type="button" value="点检" class="ui-button"/></td>'
				+'<input type="hidden" class="check_file_manage_id" value="' + peripheralData.check_file_manage_id + '"/>'
				+'<input type="hidden" class="device_type_id" value="' + peripheralData.device_type_id + '"/>'
				+'<input type="hidden" class="seq" value="' + peripheralData.seq + '"/>'
				+'</tr>';
		});

		$tbodyContent = $(tbodyContent);
		$tbodyContent.find("input.ui-button").button();

		$tbodyContent.find(".manageCode").change(function(){
			var selected_value = $(this).val();
			var $tr = $(this).parent().parent();
			var $obj_btn = $tr.find("input[type=button]");
			var $obj_label = $tr.find(".check_result");
			if ($obj_label.length == 0) {
				$obj_label = $tr.prevAll().filter(function(){
					return $(this).find(".check_result").length;
				}).first().find(".check_result");
			}

			var device_type_id = $tr.find(".device_type_id").val();
			if (selected_value != "") {
				$tbodyContent.find(".manageCode[seq=" + this.getAttribute("seq") + "]").not(this).val("").trigger("change");
				var temp = selected_value.split(",");
				if (temp[1] == "1") {
					$obj_label.text("点检不合格");
					$obj_btn.disable();
					$("#finishcheckbutton").disable();
				} else {
					deviceCheck(temp[0], device_type_id, $obj_label);
					$obj_btn.enable();
				}
			} else {
				$obj_label.text("");
				$obj_btn.disable();
				$("#finishcheckbutton").disable();
			}
		});
		if (resInfo.workstauts != 1 && resInfo.workstauts != 2) {
			$tbodyContent.find(".manageCode").trigger("change");
		}

		$tbodyContent.find("input[type=button]").button().click(function(){
			var $tr = $(this).parent().parent();
			var $obj_select = $tr.find(".manageCode");
			var selected_value = $obj_select.val();
			var manage_id = "";
			if (selected_value != "") {
				var temp = selected_value.split(",");
				manage_id = temp[0];
			}
			var manage_code = $obj_select.find("option:selected").text();
			var check_file_manage_id = $tr.find(".check_file_manage_id").val();
			var check_manage_code = $tr.find(".check_manage_code").text();

			var infectDetailData = {
				object_type : 1,
				manage_id : manage_id,
				manage_code : manage_code,
				position_id : "${userdata.position_id}",
				process_code : "${userdata.process_code}",
				check_file_manage_id : check_file_manage_id,
				sheet_manage_no : check_manage_code,
				check_while_use : function(back_result) {
					var $obj_label = $tr.find(".check_result");
					if ($obj_label.length == 0) {
						$obj_label = $tr.prevAll().filter(function(){
							return $(this).find(".check_result").length;
						}).first().find(".check_result");
					}
					if (back_result) {
						$obj_label.text("已点检");
						canFinishCheck();
					} else {
						$obj_label.text("点检不合格");
						$obj_select.find("option:selected").val(manage_id + ",1");
						$obj_select.val(manage_id + ",1");
					}
				}
			};
			showInfectSheet(infectDetailData, false);
		});

		$("#device_details table tbody").html($tbodyContent);
		$("#devicearea").show();
	};

	var deviceCheck = function(manage_id, device_type_id, $obj_label) {
		var data = {
			manage_id : manage_id,
			device_type_id : device_type_id
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : ppdiServicePath + '?method=deviceCheck',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = null;
				eval('resInfo =' + xhrobj.responseText);

				if (resInfo.deviceCheck == "OK") {
					$obj_label.text("已点检");
					canFinishCheck();
				} else {
					$obj_label.text("未点检");
					$("#finishcheckbutton").disable();
				}
			}
		});
	};

	var canFinishCheck = function() {
		var count = 0;
		$("#device_details table tbody").find("tr").each(function(){
			var $tr = $(this);
			var $check_result = $tr.find(".check_result");
			if ($check_result.length > 0) {
				var check_result = $check_result.text();
				if (check_result != "已点检") {
					count++;
				}
			}
		});
		if (count > 0) {
			$("#finishcheckbutton").disable();
		} else {
			$("#finishcheckbutton").enable();
		}
	};
</script>

<div id="devicearea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">周边设备检查使用设备工具</span>
	</div>
	<div id="device_details" class="ui-widget-content dwidth-full">
		<table class="condform" style="border-spacing: 2px 0;">
			<thead>
				<tr>
					<th class="ui-state-default td-title" style="width:3em;">No.</th>
					<th class="ui-state-default td-title">品名</th>
					<th class="ui-state-default td-title">型号</th>
					<th class="ui-state-default td-title">管理编号</th>
					<th class="ui-state-default td-title">点检票单号</th>
					<th class="ui-state-default td-title">点检结果</th>
					<th class="ui-state-default td-title"></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<div style="height: 44px">
			<input type="button" class="ui-button" id="finishcheckbutton" value="点检完成" role="button" aria-disabled="false" style="float: right; right: 2px;">
		</div>
	</div>
</div>
<div class="clear areaencloser"></div>