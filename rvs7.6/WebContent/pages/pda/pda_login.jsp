<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	String errors = (String) request.getAttribute("errors");
%>

<html:html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	window.onload = function(){
		var errmsg = '<%=errors%>';
		if (errmsg != 'null' && errmsg != "") {
			alert(errmsg);
		}

		var objJobNo = document.getElementsByName("job_no")[0];
		objJobNo.focus();
	};

	function pwdFocus(e) {
		e = e ? e : event;
		if (e.keyCode == 13) {
			var objPwd = document.getElementsByName("pwd")[0];
			objPwd.focus();
		}
	}

	function loginSubmit(e) {
		e = e ? e : event;
		if (e.keyCode == 13) {
			var objJobNo = document.getElementsByName("job_no")[0];
			var objPwd = document.getElementsByName("pwd")[0];
			if (objJobNo.value == "") {
				alert("请输入工号的值");
				objJobNo.focus();
				return false;
			}
			if (objPwd.value == "") {
				alert("请输入密码的值");
				objPwd.focus();
				return false;
			}
			document.forms["operatorForm"].action = "login.do?method=pdaLogin";
			document.forms["operatorForm"].submit();
		}
	};
</script>
<title>登陆</title>
<link rel="stylesheet" type="text/css" href="css/pda/pda_custom.css">
</head>
<body>
	<html:form action="/login" method="post">
		<div class="width-full">
			<div class="header">
				<div style="width:80%;padding-top:25px;padding-left:80px;color:#fff;">
					<h4>RVS PDA 管理工具</h4>
				</div>
			</div>
			<div class="main">
				<div style="width:60%;padding-top:32px;padding-left:50px;color:#000;">
					<label for="job_no">工号</label>&nbsp;
					<html:text name="operatorForm" property="job_no" styleClass="input-txt" onkeydown="pwdFocus();"/>
				</div>
			</div>
			<div class="bottom">
				<div style="width:60%;padding-left:50px;color:#000;">
					<label for="pwd">密码</label>&nbsp;
					<html:password name="operatorForm" property="pwd" styleClass="input-txt" onkeydown="loginSubmit();"/>
				</div>
			</div>
		</div>
	</html:form>
</body>
</html:html>
