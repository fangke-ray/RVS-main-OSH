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

		var objCode = document.getElementsByName("serial_no")[0];
		objCode.value="";
		objCode.focus();//获取焦点 

		var time = null;
		objCode.onkeyup = function(e){
			console.log(this.value);
			if (time) clearTimeout(time);
			if (this.value.length == 8 || this.value.length == 13){
				document.getElementsByName("serial_no")[0].value = this.value;
				time = setTimeout(function(){
					clearTimeout(time);
					document.forms["componentManageForm"].action = "pda_parts_outstock.do?method=getDetail";
					document.forms["componentManageForm"].submit();
				},1000);
			}
		};
	}

	function getDetail(key) {
		document.getElementsByName("serial_no")[0].value = key;
		document.forms["componentManageForm"].action = "pda_parts_outstock.do?method=getDetail";
		document.forms["componentManageForm"].submit();
	}
</script>
<title>组装组件子零件出库</title>
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
					<html:text name="componentManageForm" property="serial_no" style="ime-mode:disabled;background:transparent;border:0;height:15px;color:transparent;width:50px;"/>
					<div style="cursor:pointer;width:20px;display:inline-block;margin-left:45px;" id="goback">←</div>
				</div>
				<div style="text-align:right;">
					<h5>待出库子零件组数： <bean:write name="componentManageForm" property="count"/></h5>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="main" style="height:130px;padding:10px;overflow-y:scroll;overflow-x:hidden;">
			<table style="width:95%">
				<tr>
					<th class="td-title">型号 组件代码</th>
					<th class="td-title" style="width:90px;">订单来源</th>
					<th class="td-title" style="width:40px;">库位编号</th>
				</tr>
				<logic:iterate id="element" name="componentManageForm" property="cmpt_list" type="ComponentManageForm" indexId="index">
				<tr onClick="getDetail('<bean:write name="element" property="serial_no"/>');">
					<td>
<bean:write name="element" property="model_name"/><br><bean:write name="element" property="component_code"/>
					</td>
					<td>&nbsp;<bean:write name="element" property="origin_omr_notifi_no"/>&nbsp;</td>
					<td>&nbsp;<bean:write name="element" property="stock_code"/>&nbsp;</td>
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