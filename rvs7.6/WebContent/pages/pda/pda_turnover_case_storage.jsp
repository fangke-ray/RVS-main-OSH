<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.osh.rvs.form.qf.TurnoverCaseForm"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
<base href="<%=basePath%>">
<% String TC_MOVE_FROM = (String)session.getAttribute("TC_MOVE_FROM"); %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript">
	var countdown = 0;
	var countdownTO = null;
	var time_archer = (new Date()).getTime();

	window.onload = function(){
		var errmsg = "${errmsg}";
		if (errmsg != "null" && errmsg != "") {
			alert(errmsg);
		}

		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="appmenu.do?method=pdaMenu";
		};

		var objPrevShelf = document.getElementById("prev_shelf");
		var objNextShelf = document.getElementById("next_shelf");
		if (objPrevShelf) objPrevShelf.onclick = function(){
			var form = document.forms[0];
			form.direct_flg.value = -1;
			form.submit();
		};
		if (objNextShelf) objNextShelf.onclick = function(){
			var form = document.forms[0];
			form.direct_flg.value = 1;
			form.submit();
		};

		var objMoveButton = document.getElementById("moveButton");
		if (objMoveButton) objMoveButton.onclick = function(){
			if(this.className.indexOf("button-unuse") >= 0) return;

			clearTimeout(countdownTO);
			$("#putinButton").text("入库").addClass("button-unuse");

			var form = document.forms[0];
			form.method.value = "startTransfer";
			form.submit();
		};

		var objCancelButton = document.getElementById("cancelButton");
		if (objCancelButton) objCancelButton.onclick = function(){
			var form = document.forms[0];
			form.method.value = "cancelTransfer";
			form.submit();
		};

		var change_shelf = document.getElementById("change_shelf");
		$(change_shelf).change(handleChangeShelf);
		$("body").bind("keydown", function(evt){
			if ((new Date()).getTime() - time_archer > 3600000) {
				alert("按下确定刷新页面。");
				return window.location.reload();
			}

			var keyCode = evt.keyCode;
			var $txt_location = $("#txt_location");

			// AtoZ
			if (keyCode >= 65 && keyCode <=90) {
				$txt_location.val("").focus();
<% if (TC_MOVE_FROM == null) { %>
			} else if (keyCode == 13) {
				countDownSubmit($("#txt_location").val());
			} else if (keyCode == 8 || keyCode == 32) {
				if (countdown > 0) {
					$("#putinButton").text("入库");
					$(".plan_list tbody tr.chosen").removeClass("chosen");
					clearTimeout(countdownTO);
					$("#moveButton").addClass("button-unuse");
					$txt_location.val("");
				}
				if(evt.target.id != "txt_location")
					return false;
<% } else { %>
			} else if (keyCode == 13) {
				handlePutin();
			} else if (keyCode == 8) {
				if(evt.target.id != "txt_location")
					return false;
<% } %>
			}
		});

		$(".storage_map").click(function(e){
			var targ = e.target;
			if ("TD" == targ.tagName) {
<% if (TC_MOVE_FROM == null) { %>
				if (targ.getAttribute("class").indexOf("mapping") >= 0) {
					var location = targ.getAttribute("location");
					$("#txt_location").val(location);
					countDownSubmit(location);
				}
<% } else { %>
				if (targ.getAttribute("class") == "status-empty") {
					var location = targ.getAttribute("location");
					$("#txt_location").val(location);
					handlePutin();
				}
<% } %>
			}
		});

	}

	function handleChangeShelf() {
		var form = document.forms[0];
		form.submit();
	}

	function handlePutin() {
		if ($("#txt_location").val()) {
			var form = document.forms[0];
			form.method.value = "doStorage";
			if ($(".plan_list tbody tr").length == 1) {
				form.direct_flg.value = 1;
			}
			form.submit();
		}
	}

<% if (TC_MOVE_FROM == null) { %>

	function countDownSubmit(location) {
		if (location) {
			$("#moveButton").removeClass("button-unuse");
			countdown = 5;
			var $chosen = $(".plan_list tbody tr[location="+location+"]").detach();
			$chosen.addClass("chosen");
			$(".plan_list tbody").prepend($chosen);
			$("#putinButton").text("入库 ("+ countdown +"s)");
			countdownTO = setTimeout(counting, 1000);
		}
	}

	function counting() {
		countdown--;
		if (countdown > 0) {
			$("#putinButton").text("入库 ("+ countdown +"s)");
			countdownTO = setTimeout(counting, 1000);
		} else {
			$("#putinButton").text("入库").css("background-color", "gray");
			handlePutin();
		}
	}

<% } %>

</script>

<title>通箱待入库</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<form action="pda_turnover_case_storage.do" method="post" onsubmit="return false;">
	<div class="width-full">
		<div class="header<%=(TC_MOVE_FROM == null) ? "" : " transferring"%>" style="height:20px;">
			<div class="left" style="color:#fff;margin-left:10px;">
				<h5>
					<label>你好! ${userdata.name}</label> 
					<%=(TC_MOVE_FROM == null) ? "通箱待入库" : "通箱从" + TC_MOVE_FROM + "转移"%>
				</h5>
			</div>
			<div class="right" style="color:#fff;margin-right:10px;">
				<div  style="text-align:right;">
					<h5>
					还有 ${waitCount} 单
					<span style="cursor:pointer;margin-left:1em;" id="goback">←</span>
					</h5>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="bottom" style="height: 26px; padding-top: 6px;">
			<input type="text" id="txt_location" name="location" style="float:left;background:transparent;border:0;height:15px; color:transparent;width:50px;"/>
<% if (TC_MOVE_FROM == null) { %>
			<div class="button button-small button-unuse" id="moveButton" style="float:right;margin-right:20px;">移库</div>
			<div class="button button-small" id="putinButton" style="float:right;margin-right:20px;" onClick="handlePutin();">入库</div>
<% } else { %>
			<div class="button button-small" id="cancelButton" style="float:right;margin-right:20px;">取消移库</div>
<% } %>
			<input type="hidden" name="method">
			<!-- 翻页方向 -->
			<input type="hidden" name="direct_flg">
		</div>
		<div class="main" style="height:150px;width:296px;padding:5px;*position:relative;overflow-y:scroll;overflow-x:hidden;">
			<table id="storage_title" style="width:280px;">
				<tr>
					${(waitCount > 0) ? "<th class=\"td-title\" id=\"prev_shelf\">◢</th>" : ""}
					<th class="td-title">
					<select id="change_shelf" name="shelf">
						<option value="A" ${(shelf eq 'A' ? 'selected' : '' )}>A 货架</option>
						<option value="B" ${(shelf eq 'B' ? 'selected' : '' )}>B 货架</option>
						<option value="C" ${(shelf eq 'C' ? 'selected' : '' )}>C 货架</option>
						<option value="D" ${(shelf eq 'D' ? 'selected' : '' )}>D 货架</option>
						<option value="E" ${(shelf eq 'E' ? 'selected' : '' )}>E 货架</option>
						<option value="F" ${(shelf eq 'F' ? 'selected' : '' )}>F 货架</option>
						<option value="G" ${(shelf eq 'G' ? 'selected' : '' )}>G 货架</option>
						<option value="H" ${(shelf eq 'H' ? 'selected' : '' )}>H 货架</option>
						<option value="I" ${(shelf eq 'I' ? 'selected' : '' )}>I 货架</option>
						<option value="J" ${(shelf eq 'J' ? 'selected' : '' )}>J 货架</option>
						<option value="K" ${(shelf eq 'K' ? 'selected' : '' )}>K 货架</option>
						<option value="L" ${(shelf eq 'L' ? 'selected' : '' )}>L 货架</option>
						<option value="O" ${(shelf eq 'O' ? 'selected' : '' )}>O 货架</option>
						<option value="P" ${(shelf eq 'P' ? 'selected' : '' )}>P 货架</option>
						<option value="Q" ${(shelf eq 'Q' ? 'selected' : '' )}>Q 货架</option>
						<option value="R" ${(shelf eq 'R' ? 'selected' : '' )}>R 货架</option>
						<option value="S" ${(shelf eq 'S' ? 'selected' : '' )}>S 货架</option>
						<option value="T" ${(shelf eq 'T' ? 'selected' : '' )}>T 货架</option>
						<option value="U" ${(shelf eq 'U' ? 'selected' : '' )}>U 货架</option>
						<option value="V" ${(shelf eq 'V' ? 'selected' : '' )}>V 货架</option>
						<option value="DW" ${(shelf eq 'DW' ? 'selected' : '' )}>动物实验内镜</option>
						<option value="Z1" ${(shelf eq 'Z1' ? 'selected' : '' )}>ENDOEYE 货架上</option>
						<option value="Z2" ${(shelf eq 'Z2' ? 'selected' : '' )}>ENDOEYE 货架下</option>
						<option value="Z4" ${(shelf eq 'Z4' ? 'selected' : '' )}>UDI 货架</option>
						<option value="Z5" ${(shelf eq 'Z5' ? 'selected' : '' )}>UDI 货架扩</option>
						<option value="M" ${(shelf eq 'M' ? 'selected' : '' )}>M 货架</option>
						<option value="N" ${(shelf eq 'N' ? 'selected' : '' )}>N 货架</option>
						<option value="Z3" ${(shelf eq 'Z3' ? 'selected' : '' )}>临时货架</option>
					</select>
					</th>
					${(waitCount > 0) ? "<th class=\"td-title\" id=\"next_shelf\">◣</th>" : ""}
				</tr>
			</table>
			<table class="storage_map" style="width:280px;">
				${(shelf eq 'Z3' ? '' : shelfMap)}
			</table>

			<table class="plan_list" style="width:280px;">
			<thead>
				<tr>
					<td class="td-title">型号</td>
					<td class="td-title" style="width:60px;">机身号</td>
					<td class="td-title">发送地</td>
					<td class="td-title">库位</td>
				</tr>
			</thead>
			<tbody>
				<logic:iterate id="element" name="storagePlanListOnShelf" type="TurnoverCaseForm" indexId="index">
				<tr location="<bean:write name="element" property="location"/>">
					<td><bean:write name="element" property="model_name"/></td>
					<td><bean:write name="element" property="serial_no"/></td>
					<td><bean:write name="element" property="bound_out_ocm"/></td>				
					<td><bean:write name="element" property="location"/></td>
				</tr>
				</logic:iterate>
			</tbody>
			</table>
		</div>
	</div>
	</form>
</body>
</html>