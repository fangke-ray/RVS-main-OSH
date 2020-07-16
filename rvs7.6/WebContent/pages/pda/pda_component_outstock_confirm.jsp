<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.osh.rvs.form.partial.ComponentManageForm"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<%
	String user_name = (String)request.getSession().getAttribute("user_name");
%>

<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	window.onload = function(){
		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="pda_component_outstock.do?method=init";
		};
	}

	function clickOutStock(target_material_id,supply_count,order_count) {
		var confirmMessage = "";
		var compSerialNo = document.getElementById("comp_serial_no").innerText;
		if (supply_count > 0) {
			confirmMessage = "该维修品已分配了组件" + supply_count + "件，是否将序列号[" + compSerialNo + "]的组件也分配给它？";
		} else {
			confirmMessage = "是否将序列号[" + compSerialNo + "]的组件分配给该维修品？";
		}
		if (confirm(confirmMessage)) {
			doOutStock(target_material_id);
		}
	}
	function doOutStock(target_material_id) {
		document.getElementsByName("target_material_id")[0].value = target_material_id;
		document.forms["componentManageForm"].action = "pda_component_outstock_confirm.do?method=doOutStock";
		document.forms["componentManageForm"].submit();
	}
</script>
<title>选择使用此组件的维修品</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="/pda_component_outstock" method="post">
	<div class="width-full">
		<div class="header" style="height:40px;">
			<div class="left" style="color:#fff;margin-left:10px;">
				<h5>
					<label>你好! <%=user_name %></label>
				</h5>
			</div>
			<div class="right" style="color:#fff;margin-right:10px;">
				<div>
					<html:text name="componentManageForm" property="serial_no" style="ime-mode:disabled;background:transparent;border:0;height:15px;color:transparent;width:50px;"/>
					<div style="cursor:pointer;width:20px;display:inline-block;margin-left:45px;" id="goback">←</div>
				</div>

			</div>
			<div class="clear"></div>
		</div>
		<div class="main" style="height:130px;padding:10px;overflow-y:scroll;overflow-x:hidden;">
			<html:hidden name="componentManageForm" property="target_material_id"/>
			<html:hidden name="componentManageForm" property="component_key"/>
			<table style="width:95%">
				<tr>
					<th class="td-title" style="width:70px;">维修单号</th>
					<th class="td-title">当前位置</th>
					<th class="td-title" style="width:60px;">分配/预定<br>组件数</th>
				</tr>
				<logic:iterate id="material" scope="request" name="targetMaterials" indexId="index">
				<tr onClick="clickOutStock('<bean:write name="material" property="material_id"/>',<bean:write name="material" property="selectable"/>,<bean:write name="material" property="operate_result"/>);">
					<td><bean:write name="material" property="sorc_no"/></td>
					<td><bean:write name="material" property="processing_position"/></td>
					<td class="qty"><bean:write name="material" property="selectable"/>/<bean:write name="material" property="operate_result"/></td>
				</tr>
				</logic:iterate>
			</table>
		</div>
		<div class="bottom" style="height:52px;">
			<div style="margin: 0px auto; width: 70%; font-size: 14px;">
				<label>组件序列号</label>&nbsp;&nbsp;
				<label id="comp_serial_no"><bean:write name="componentManageForm" property="serial_no"/></label>
			</div>
		</div>
	</div>
	</html:form>
</body>
</html:html>