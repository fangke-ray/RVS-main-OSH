<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<style>
a{text-decoration:none;}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/highcharts4.js"></script>
<script type="text/javascript" src="js/exporting.js"></script>
<script type="text/javascript" src="js/report/productivity.js"></script>

<title>生产效率</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="文档管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">检索条件</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<div class="ui-widget-content">
					<form id="searchform" method="POST">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">作业时间</td>
								<td class="td-content">
									<input type="text" id="search_start_date" class="ui-widget-content" readonly>起<br/>
									<input type="text" id="search_end_date" class="ui-widget-content" readonly>止
								</td>
							</tr>
						</table>
						<div style="height:44px;">
							<input class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px" type="button">
							<input class="ui-button" id="searchbutton" value="分析" style="float:right;right:2px" type="button">
						</div>
					</form>
				</div>

				<div class="clear areaencloser"></div>

				<div class="ui-widget-content">
					<form id="editform" method="POST">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">
									<label id="label_outline_date"></label><br>有效出勤数
									<input type="hidden" id="hidden_outline_date">
								</td>
								<td class="td-content">
									<input type="text" id="edit_avalible_productive" name="avalible_productive" alt="有效出勤数" class="ui-widget-content">
								</td>
								<td>
									<input class="ui-button" id="updatebutton" value="更新" type="button">
								</td>
							</tr>
						</table>
					</form>
				</div>

				<div id="listarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">生产效率一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div id="container" class="ui-widget-content" style="height: 500px;"></div>
				</div>

				<div class="ui-widget-header ui-helper-clearfix areabase" style="padding-top:4px;">
					<input id="exportbutton" class="ui-button" value="导出图表" style="float:right;right:2px" type="button">
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>