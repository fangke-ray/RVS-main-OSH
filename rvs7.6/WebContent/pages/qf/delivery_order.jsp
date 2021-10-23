<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<head>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
	<base href="<%=basePath%>">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="css/custom.css">
	<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
	<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
	<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
	<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="js/jquery.dialog.js"></script>
	<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
	<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
	<script type="text/javascript" src="js/utils.js"></script>
	<script type="text/javascript" src="js/jquery-plus.js"></script>
	<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
	<script type="text/javascript" src="js/qf/delivery_order.js"></script>
	
	<title>出货单制作</title>
</head>
<body class="outer">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="现品管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left; padding-left: 12px;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   	 <span class="areatitle">待制作出货单维修品一览</span>
				     <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					 	<span class="ui-icon ui-icon-circle-triangle-n"></span>
					 </a>
				</div>
				
				<table id="list"></table>
				<div id="listpager"></div>
				
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div style="margin-left:4px;margin-top:6px;">
						<input type="text"id="scanner_inputer" title="扫描前请点入此处" style="height: 20px;" class="dwidth-half"></input>
						<input type="button" id="shipbutton" class="ui-button" value="制作出货单"/>
					</div>
				</div>
				
				<div class="areaencloser"></div>
				
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   	 <span class="areatitle">今日完成出货单一览</span>
				     <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					 	<span class="ui-icon ui-icon-circle-triangle-n"></span>
					 </a>
				</div>
				
				<table id="curlist"></table>
				<div id="curlistpager"></div>
				
			</div>
			
			<input type="hidden" id="hide_ocm" value="${oOptions }">
			<input type="hidden" id="hide_level" value="${lOptions }">
			
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>