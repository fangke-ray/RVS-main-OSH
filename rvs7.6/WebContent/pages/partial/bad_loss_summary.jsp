<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/highcharts4.js"></script>
<script type="text/javascript" src="js/highcharts-3d.js"></script>


<script type="text/javascript" src="js/partial/bad_loss_summary.js"></script>

<style>
.menulink {
	font-size: 16px;
	color: white;
	float: right;
	right: 4px;
	margin: 4px;
}

.menulink:hover {
	color: #FFB300;
	cursor: pointer;
}
 .rpt-table {
	border : 1px solid black;
	background-color : #FFFFFF;
	margin: 4px;
	border-collapse: collapse;
}
.rpt-td-title-r {
	border : 1px solid black;
	color : black;
	background-color : #1F497D;
	text-align : right; 
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}
.rpt-td-content-d {
	border : 1px solid black;
	color : black;
	background-color :#AAAAAA;
	text-align : right;
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}
.rpt-td-content-grey{
	border : 1px solid black;
	color : black;
	background-color : #c0c0c0;
	text-align : right;
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}
.rpt-td-content-white{
	border : 1px solid black;
	color : black;
	background-color : #fff;
	text-align : right;
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}

.rpt-td-content-blue{
    border : 1px solid black;
	color : black;
	background-color : #92cddc;
	text-align : right;
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}

.rpt-td-content-yellow{
	border :1px solid black;	
	color : black;
	background-color :#ffc000;
	text-align : right;
	font-weight:bold;
	font-size:12px;
	padding-left: 4px;
	padding-right: 4px;
}
.choose-title{
   cursor:pointer;
}
.mouse_title{
 cursor:pointer;
}
.mouse_title:hover{
 background:red;
}
.monthtable {
	width:100%;
	border :1px solid black;
	border-collapse: collapse;
}
.monthtable td {
	text-align:center;
}

.detail-button:hover {
	background: #FFFFFF;
	color: #F8BB14;
	cursor: pointer;
}

.detail-button:active {
	overflow: visible;
	border: 1px solid #ff6b7f;
	background: #db4865
		url(images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat;
	font-weight: bold;
	color: #ffffff;
}
.month-title:visited{
   color:red;
}
.month-title:hover {
	background: #FFFFFF;
	color: #F8BB14;
	cursor: pointer;
}
.month-title:active {
	overflow: visible;
	border: 1px solid #ff6b7f;
	background: #db4865
		url(images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat;
	font-weight: bold;
	color: #ffffff;
}
</style>
<title><%=(String)request.getAttribute("work_period")%>不良损金汇总</title>
</head>
<% String loss= (String)request.getAttribute("loss"); %>
<body class="outer" style="overflow: auto;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
       </div>
   </div>
   <div class="ui-widget-panel ui-corner-all width-full" style="align: center; margin: auto;padding-top: 16px; padding-bottom: 16px;" id="body-2">
		<div id="body-lft" style="width:256px;float:left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="损金管理"/>
				</jsp:include>
		</div>

		<div id="body-mdl" style="width:994px;float:left;margin-right:20px;">
		<div id="monthlist">
			<form name="badLossSummaryForm">
				<table class="monthtable">
				   <thead>
						<tr>
							<th class="ui-widget-header">序号</th>
							<th class="ui-widget-header">时期</th>
							<th class="ui-widget-header">月份</th>
							<th class="ui-widget-header">损金总计(OSH) (USD)</th>
							<th class="ui-widget-header">结算汇率（EUR->USD）</th>
							<th class="ui-widget-header">操作</th>
						</tr>
					</thead>
					<tbody></tbody>
					<tfoot>
						<tr>
							<logic:iterate id="otherPeriods" name="listOtherPeriods" indexId="index">
								<td colspan="6" class="ui-widget-content" style="text-align: left;text-indent: 1em;height:25px;">
									<a href="javascript:" class="otherPeriods"><bean:write name="otherPeriods"/></a>
								</td>
							</logic:iterate>
						</tr>
					</tfoot>
				</table>
				<input type="hidden" id="period" name="period" value="${work_period}" />
			</form>
		</div>

		<div id="summary">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-left" style="width:992px;">
				<span class="areatitle">损金</span>
			</div>
			<input type="hidden" id="manager_scheduler" value="<%=loss%>">
			<div class="ui-widget-content" style="height:auto;">					
				<div id ="edit_value"style="margin-left: 10px;" id="edit_value">
					<table class="rpt-table">
						<thead>
							<tr class="ui-jqgrid-labels" role="rowheader">		
							</tr>
					   </thead>
					   <tbody>
					        <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
					         <tr style="text-align:right;"></tr>
						</tbody>
					</table>
				</div>	
				<div class="ui-widget-header  ui-corner-all ui-helper-clearfix areabase bar_fixed"style="width:972px;padding-top:0px;margin-left:14px;">
					<div style="height:36px;margin-top:5px;">
					<!-- 如果登陆人员是管理员和计划员的话可以修改操作 -->
					<%  if("isTrue".equals(loss)){ %>
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirm_modify_button" value="确认修改" role="button" aria-disabled="false" style="float:left;left:4px;">
				    <%  } %>
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="back_button" value="返回" role="button" aria-disabled="false" style="float:left;left:8px;">
				   </div>
				</div>

				<div class="clear" style="height:12px;"></div>
				<div id="container" style="float:left;width:990px;height:300px;margin-top:0px;"></div>	
				<div id="total_data" style="float:left;margin-right:80px;margin-left:5px;">
			 </div>
			 
			 <div class="clear" style="height:40px;"></div>
			 	<div id="loss_data" style="float:left;margin-left: 10px;">
					<table class="rpt-table">
					   <thead>
							<tr class="ui-jqgrid-labels" role="rowheader">	
																
							</tr>
					   </thead>
					</table>
				</div>	
			<div class="clear" style="height:25px;"></div>

			<!-- 如果登陆人员是管理员和计划员的话可以修改操作 -->
			<%  if("isTrue".equals(loss)){ %>
			<div class="ui-widget-content" style="margin-left:14px;margin-right:4px;">
					<form id="searchform" method="POST">
						<table class="condform">
							<tr>
								<td id="comment_td_title"class="ui-state-default td-title"></td>
								<td class="td-content"><textarea id="textarea_comment" class="ui-widget-content" style="width: 835px; height: 64px;"></textarea></td>			
							</tr>
						</table>
								<div style="height:44px">
									<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirm" value="确认" role="button" aria-disabled="false" style="float:right;right:2px"/>
								</div>
						</form>
			</div>
			<%  } %>
			<div id="pie-container" style="float:left;width:990px;height:400px;margin-top:0px;"></div>	
			<div class="clear" style="height:40px;"></div>

			<div id="broken_line-container" style="float:left;width:990px;height:400px;margin-top:0px;"></div>	
			<div class="clear" style="height:40px;"></div>
		 </div>
		</div>
	</div>
<div class="clear" style="height:40px;"></div>
</div>
</body>
<div id="confirmmessage"></div>
</html>