<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.osh.rvs.form.partial.ComponentManageForm"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<%
	String user_name = (String)request.getSession().getAttribute("user_name");
	String errors = (String) request.getAttribute("errors");
%>

<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	window.onload = function(){
		var objGoback = document.getElementById("goback");
		objGoback.onclick = function(){
			window.location.href="appmenu.do?method=pdaMenu";
		};

		var errmsg = '<%=errors%>';
		if (errmsg != 'null' && errmsg != "") {
			alert(errmsg);
		}

		var objCode = document.getElementsByName("stock_code")[0];
		objCode.value="";
		objCode.focus();//获取焦点 

		objCode.onkeyup = function(e){

			if (this.value.length == 8){
				if (!document.getElementsByName("component_key")[0].value) {
					objCode.value="";
					alert("请在列表中选中要放入的子零件。");
					return;
				}

				if (confirm("是否将子零件入库到" + objCode.value + "？")) {
					document.forms["componentManageForm"].action = "pda_parts_instock.do?method=doInstock";
					document.forms["componentManageForm"].submit();
				} else {
					objCode.value="";
					objCode.focus();
				}
			}
		};
	}

	function getDetail(key) {
		document.getElementsByName("component_key")[0].value = key;
		document.getElementsByName("stock_code")[0].focus();
	}
</script>
<title>组装组件子零件入库</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="/pda_parts_outstock" method="post" onsubmit="return false">
	<div class="width-full">
		<div class="header" style="height:40px;">
			<div class="left" style="color:#fff;margin-left:10px;">
				<h5>
					<label>你好! <%=user_name %></label>
				</h5>
			</div>
			<div class="right" style="color:#fff;margin-right:10px;">
				<div>
					<html:text name="componentManageForm" property="stock_code" style="ime-mode:disabled;background:transparent;border:0;height:15px;color:transparent;width:50px;"/>
					<html:hidden name="componentManageForm" property="component_key"/>
					<div style="cursor:pointer;width:20px;display:inline-block;margin-left:45px;" id="goback">←</div>
				</div>
				<div style="text-align:right;">
					<h5>待入库子零件组数： <bean:write name="componentManageForm" property="count"/></h5>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="main" style="height:130px;padding:10px;overflow-y:scroll;overflow-x:hidden;">
			<table style="width:95%">
				<tr>
					<th class="td-title">型号 组件代码</th>
					<th class="td-title" style="width:90px;">订单来源</th>
				</tr>
				<logic:iterate id="element" name="componentManageForm" property="cmpt_list" type="ComponentManageForm" indexId="index">
				<tr onClick="getDetail('<bean:write name="element" property="component_key"/>');">
					<td>
<bean:write name="element" property="model_name"/><br><bean:write name="element" property="component_code"/>
					</td>
					<td>&nbsp;<bean:write name="element" property="origin_omr_notifi_no"/>&nbsp;</td>
				</tr>
				</logic:iterate>
			</table>
		</div>
		<div class="bottom" style="height:52px;">
		</div>
	</div>
	</html:form>
</body>
</html:html>