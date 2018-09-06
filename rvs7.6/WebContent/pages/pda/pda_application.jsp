<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.osh.rvs.form.pda.PdaApplyElementForm"%>

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
			window.location.href="appmenu.do?method=pdaMenu";
		};
	}

	function getDetail(key) {
		document.getElementsByName("consumable_application_key")[0].value = key;
		document.forms["pdaApplyForm"].action = "pda_apply.do?method=getDetail";
		document.forms["pdaApplyForm"].submit();
	}
</script>
<title>消耗品申请单</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="/pda_apply" method="post">
	<div class="width-full">
		<div class="header" style="height:40px;">
			<div class="left" style="color:#fff;margin-left:10px;">
				<h5>
					<label>你好! <%=user_name %></label><br>消耗品申请单
				</h5>
			</div>
			<div class="right" style="color:#fff;margin-right:10px;">
				<div>
					<div style="cursor:pointer;width:20px;diaplay:inline-block;margin-left:45px;" id="goback">←</div>
				</div>
				<div  style="txet-align:right;">
					<h5>待处理 <bean:write name="pdaApplyForm" property="count"/>单</h5>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="main" style="height:130px;padding:10px;overflow-y:scroll;overflow-x:hidden;">
			<html:hidden name="pdaApplyForm" property="consumable_application_key"/>
			<table style="width:95%">
				<tr>
					<th class="td-title" style="width:40px;">编号</th>
					<th class="td-title" style="width:60px;">工程</th>
					<th class="td-title">即时</th>
					<th class="td-title">申请总价</th>
					<th class="td-title">待处理项</th>
				</tr>
				<logic:iterate id="element" name="pdaApplyForm" property="apply_list" type="PdaApplyElementForm" indexId="index">
				<tr onClick="getDetail('<bean:write name="element" property="consumable_application_key"/>');">
					<td><bean:write name="element" property="application_no"/></td>
					<td><bean:write name="element" property="line_name"/></td>
					<td align="center"><bean:write name="element" property="flg"/></td>				
					<td align="right"><bean:write name="element" property="total_price"/></td>
					<td class="qty"><bean:write name="element" property="count"/></td>
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