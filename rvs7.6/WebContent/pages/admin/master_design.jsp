<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<style>
.menulink {
    color: white;
    float: right;
    font-size: 16px;
    margin: 4px;
    right: 4px;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.ba-hashchange.min.js"></script>
<script type="text/javascript" src="js/admin/systemmenu.js"></script>
<link rel="stylesheet" type="text/css" href="css/lte-style.css">

<script type="text/javascript">
$(function() {

	$("#accordion").accordion();
    $('#datepicker').datepicker({
			inline: true,
			width: 224
		});

	$("#homepage").click(function(){
		window.location.href = "./panel-page.html";
	});

});

</script>

<title>RVS系统管理员界面</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

<div class="ui-widget-panel width-full" style="align:center;padding-top:16px;" id="body-3">
	<div style="width:256px;float:left;">
		<div id="body-lft" class="dwidth-left" style="margin-left:20px;">
			<div id="menuarea">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-left">
					<span class="areatitle">功能菜单</span>
				</div>
				<div class="ui-widget-content dwidth-left">
				<div id="accordion">
				
				    <h3 style="padding-left:30px;">维修品信息管理</h3>
				    <div>
				        <a href="adminmenu.do#category">机种信息管理</a><br>
				        <a href="adminmenu.do#model">型号信息管理</a><br>
				        <a href="adminmenu.do#partial">零件信息管理</a><br>
				        <a href="adminmenu.do#customer">客户信息管理</a>
				    </div>
				    <h3 style="padding-left:30px;">作业环境管理</h3>
				    <div>
				        <a href="adminmenu.do#section">课室信息管理</a><br>
				        <a href="adminmenu.do#line">工程信息管理</a><br>
				        <a href="adminmenu.do#position">工位信息管理</a><br>
				        <a href="adminmenu.do#process_assign_template">修理流程模板管理</a><br>
				        <a href="adminmenu.do#operator">用户信息管理</a><br>
				        <a href="adminmenu.do#light_fix">中小修理标准编制管理</a><br>
				        <a href="adminmenu.do#optional_fix">选择修理管理</a><br>
				        <a href="adminmenu.do#drying_oven_device">烘干作业管理</a><br>
				    </div>
				    <h3 style="padding-left:30px;">系统信息管理</h3>
				    <div>
				        <a href="adminmenu.do#role">角色信息管理</a><br>
				        <a href="adminmenu.do#holiday">假日管理</a><br>
				        <!--a href="#resttime">作息管理</a><br-->
				        <a href="adminmenu.do#parameters">系统参数配置</a><br>
				        <a href="adminmenu.do#system_image_manage">系统图片管理</a><br>
				        <a href="adminmenu.do#pcsFixOrder">工程检查票输入修正</a><br>
				        <a href="pcs_request.do">工程检查票修正履历</a><br>
				        <a href="adminmenu.do#interface_data">接口未处理信息管理</a><br>
				        <a href="adminmenu.do#user_define_codes">用户定义</a>
				    </div>
				</div>
			</div>
			<div id="datepicker" class="dwidth-left"></div>
				<div class="clear areaencloser"></div>
			</div>

		</div>
	</div>
	<div style="width:1022px;float:left;">
		<div id="body-mdl" class="dwidth-middleright" style="margin-right:21px;"></div>
	</div>

<div class="clear areaencloser dwidth-middleright"></div>
</div>

</div>

</body></html>