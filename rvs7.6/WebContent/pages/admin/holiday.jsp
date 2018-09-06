<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<style>
#calendararea div.ui-datepicker {
	width: 600px;
//	height: 480px;
}

#calendararea table.ui-datepicker-calendar td {
	height: 48px;
	text-align: center;
	font-size: 20px;
}
.ui-datepicker-calendar .ui-state-active {
	color:black;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/holiday.js"></script>

<title>休假/调休日期设置</title>
</head>
<body>

<div id="setarea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">休假/调休日期设置</span>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<div>
			<div id="calendararea" style="padding : 32px;">
			</div>
		</div>
	</div>
	<div class="clear dwidth-middle"></div>
</div>
</body>
</html>
