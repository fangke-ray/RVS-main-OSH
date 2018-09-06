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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/highcharts4.js"></script>
<script type="text/javascript" src="js/exporting.js"></script>
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/report/foundry.js"></script>
<title>代工时间统计</title>
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
							<tbody>
								<tr>
									<td class="ui-state-default td-title">工程</td>
									<td class="td-content">
										<select id="search_line_id" class="ui-widget-content">${sLine}</select>
										<input type="hidden" id="search_line_name">
									</td>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select id="search_section_id" class="ui-widget-content">${sSection}</select>
										<input type="hidden" id="search_section_name">
									</td>
									<td class="ui-state-default td-title">作业时间</td>
									<td class="td-content" style="width: 250px;position: relative;">
										<input type="text" id="search_finish_time_start" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_finish_time_end" class="ui-widget-content" readonly>止
										<input class="ui-button" id="monthbutton" value="月份" type="button" style="position: absolute;left:170px;top:13px;">
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px;">
							<input class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px" type="button">
							<input class="ui-button" id="searchbutton" value="分析" style="float:right;right:2px" type="button">
						</div>
					</form>
				</div>
				
				<div class="areaencloser"></div>
				
				<div id="listarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">代工时间分析图表</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div>
						<div id="container" class="ui-widget-content" style="height: 300px;"></div>
						<div id="container2" class="ui-widget-content" style="height: 300px;margin-top:10px;"></div>
					</div>
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