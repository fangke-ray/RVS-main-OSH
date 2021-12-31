<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" 
import="java.util.List" 
isELIgnored="false"%>
<%@page import="com.osh.rvs.bean.LoginData"%>
<%@page import="com.osh.rvs.common.RvsConsts"%>
<%@page import="com.osh.rvs.bean.master.PositionEntity"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript">

$(function() {
	// 检索表单认证(没有前台认证也要写,为了对应后台出错时效果)
	$("#passwordform").validate({});

	$("#changebutton").button().click(function() {
		var data = {
			old_input : _enc($("#input_old_input").val()),
			new_input : _enc($("#input_new_input").val()),
			new_confirm : $("#input_new_confirm").val()
		};

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'password.do?method=doChange',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);

					if(resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages("#passwordform", resInfo.errors);
						$("#pwd_message").text("您的输入有误。");
					} else {
						$("#input_old_input").val("");
						$("#input_new_input").val("");
						$("#input_new_confirm").val("");
						$("#pwd_message").text(resInfo.pwdDateMessage);
					}

				} catch (e) {
				};
			}
		});
	});

	loadJs("js/frontEnc.js");
});

</script>
<style>
	span.panel-messagetilte {
		font-size : 16px;
		padding-left : 4px;
	}
	#panelarea div.ui-widget-content {
		padding : 6px;
	}
	#panelarea > div.ui-widget-content:not(:first-child) {
		margin-top : 6px; 
	}
	#panelarea .ui-button-text-only .ui-button-text {
		padding: .4em 1em !important;
	}
	#panelarea .ui-buttonset .ui-button {
		margin-right: 0em;
	}
</style>

<title>设置面板</title>
</head>
<body class="outer" style="align: center;">

	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="3"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="资源功能"/>
				</jsp:include>
			</div>
			<div style="width: 798px; float: left;">
				<div id="body-mdl" class="dwidth-middle" style="margin: auto;">
					<div id="panelarea" class="dwidth-middle">
						<div class="ui-widget-content">
							<div class="ui-state-default">
								<span class="panel-messagetilte">您可以修改您的密码</span>
							</div>
							<div class="ui-widget-content">
								<div id="pwd_message">
									${pwdDateMessage}
								</div>
								<dl>
								<form id="passwordform">
								  <dt class="ui-state-default">请输入旧密码</dt>
								  <dd><input type="password" id="input_old_input" style="border: 1px solid #93C3CD;margin:2px;"></dd>
								  <dt class="ui-state-default">请输入新密码</dt>
								  <dd><input type="password" id="input_new_input" style="border: 1px solid #93C3CD;margin:2px;"></dd>
								  <dt class="ui-state-default">请再次输入新密码确认</dt>
								  <dd><input type="password" id="input_new_confirm" style="border: 1px solid #93C3CD;margin:2px;"></dd>
								</dl>
								</form>
								<div>
									<input type="button" value="变更密码" id="changebutton" class="ui-button ui-widget ui-state-default ui-corner-all" role="button">
								</div>
							</div>
						</div>
						<div class="ui-widget-content" id="oldbrowser" style="display: none;">
							<div class="ui-state-default">
								<span class="panel-messagetilte">浏览器版本提示</span>
							</div>
							<div class="ui-widget-content">
								<p>你正在使用的浏览器版本过低、这可能导致:</p>
								<p>1.画面流畅度变低</p>
								<p>2.一部分页面效果无法展示</p>
								<p>3.工作页面需要手动刷新以显示最新状况</p>
								<p>4.展示界面无法做到即时反映</p>
								<p>请升级到以下版本的浏览器使用本系统.</p>
								<p>Chrome 4+ Firefox 4+ Internet Explorer 10+ Opera 10+ Safari 5+</p>
							</div>
						</div>
						<div class="clear"></div>
					</div>
				</div>
			</div>
			<div style="width: 224px; float: left;">
				<div id="body-rgt" class="dwidth-right" style="margin-left: 8px;"></div>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>
	</div>

</body>
</html>