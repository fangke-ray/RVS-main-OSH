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
#container {
	position : relative;
}
#axis_base {
    height: 560px;
    border-left: 2px solid black;
    box-shadow: 0px 2px black;
    position: absolute;
    z-index: 1;
    left: 2em;
    top: 8px;
    width: 942px;
    /*overflow: hidden;*/
}
#axis_base .y_columns {
	display: -moz-box; /*Firefox*/
	display: -webkit-box; /*Safari,Opera,Chrome*/
	display: flex;
	height:100%;
	width:100%;
}
#axis_base .y_column {
	height : 100%;
	/* width : 70px;*/
	position: absolute;
}

#axis_base .y_column:before {
	position: absolute;
	background-color: yellow;
	width: 100%;
	height: 2em;
	line-height: 2em;
	text-align: center;
	z-index: 4;
	top: -38px;
	border-radius: .5em;
	content: attr(work-rate);
}
#axis_base [leader]:before {
	background-color: orange;
}

#axis_base [super]:before {
	background-color: golden;
}
#axis_base .y_column .position_intro,
#axis_base .y_result_column .position_intro {
	font-size:larger;
	width : 100%;
	height: auto;
	position: absolute;
	bottom: -1.5em;
	text-align:center;
	font-weight: bold;
}
#standard_column > div {position:absolute; width:100%; height:100%;}
#standard_column > div > div {
	width: 100%;
	position:absolute;
	background-color : transparent;  /* #55C8FF */
	box-shadow: -1px -1px 1px black,
				1px -1px 1px black,
				1px 1px 1px black,
				-1px 1px 1px black
	;
}
.production_feature {
	width : 100%;
	position: absolute;
	box-shadow: -1px -1px 1px white,
				1px -1px 1px white,
				1px 1px 1px white,
				-1px 1px 1px white;
}
#axis_base .operator_flex {
	display: -moz-box; /*Firefox*/
	display: -webkit-box; /*Safari,Opera,Chrome*/
	display: flex;
	-moz-box-flex: 1.0; /*Firefox*/
	-webkit-box-flex: 1.0; /*Safari,Opera,Chrome*/
	flex:1;
	box-pack: justify;
	justify-content : space-between;
	padding-left: 1em;
}
#axis_base .operator_flex .y_column{
	position:relative;
	-moz-box-flex: 1.0; /*Firefox*/
	-webkit-box-flex: 1.0; /*Safari,Opera,Chrome*/
	flex:1;
	margin: 0 2px 0 2px;
}
/* Unknown */
#axis_base .operator_flex .production_feature[d_type="9"],
/* 间接作业工时 */
#axis_base .operator_flex .production_feature[d_type="6"],
/* 休息时间 */
#axis_base .operator_flex .production_feature[d_type="3"],
/* 等待指示 */
#axis_base .operator_flex .production_feature[d_type="7"],
/* 管理 */
#axis_base .operator_flex .production_feature[d_type="2"]
{
	width : 30%;
	box-shadow : none;
	margin-left : 33%;
}
/* 直接作业工时-特定维修对象 */
#axis_base .operator_flex .production_feature[d_type="0"],
#axis_base .operator_flex .production_feature[d_type="1"] { 
	width : 70%;
	margin-left : 13%;
}
/* Unknown */
#axis_base .operator_flex .production_feature[d_type="9"],
.legend[d_type="9"] {
	visible : false
}
/* 直接作业工时-特定维修对象 */
#axis_base .operator_flex .production_feature[d_type="0"],
.legend[d_type="0"] {
	background-color : #92D050;
}
/* 直接作业工时-无特定维修对象 */
#axis_base .operator_flex .production_feature[d_type="1"],
.legend[d_type="1"] { 
	background-color : #AFDC7E;
}
/* 间接作业工时 */
#axis_base .operator_flex .production_feature[d_type="6"],
.legend[d_type="6"] { 
	background-color : #00B050;
}
/* 休息时间 */
#axis_base .operator_flex .production_feature[d_type="3"],
.legend[d_type="3"] {
	background-color : #00B0F0;
}
/* 等待指示 */
#axis_base .operator_flex .production_feature[d_type="7"],
.legend[d_type="7"] {
	background-color : #AD91B9;
}
/* 管理 */
#axis_base .operator_flex .production_feature[d_type="2"],
.legend[d_type="2"] {
	background-color : #948A54;
}
.legend[d_type="-1"] {
	background-color : RGBA(80,128,188,.25);
	padding-left: 2em;
	padding-right: 2em;
}
.legend {
	float:left;
	margin-left :2em;
	margin-top :.2em;
	padding:0.2em;
	line-height:2em;
}
.foundry_feature {
	width : 100%;
	position: absolute;
	background-color : RGBA(80,128,188,.25);
	z-index:-1;
}

#time_axis {
	width:100%;
	position: absolute;
	top:0;
	height:0;
	border-top:1px solid black;
}
#time_axis:before {
	position: absolute;
	left: -2.75em;
	top: -.75em;
	content: attr(minute);
	font-size: 0.8em;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/report/work_duration.js"></script>
<title>作业时间分析</title>
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
				</div>
				<div class="ui-widget-content">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select id="search_section_id" class="ui-widget-content">${sSection}</select>
										<input type="hidden" id="search_section_name">
									</td>
									<td class="ui-state-default td-title">工程</td>
									<td class="td-content">
										<select id="search_line_id" class="ui-widget-content">${sLine}</select>
										<input type="hidden" id="search_line_name">
									</td>
									<td class="ui-state-default td-title">作业日期</td>
									<td class="td-content" style="width: 250px;position: relative;">
										<input type="text" id="search_action_date" class="ui-widget-content" readonly>
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
						<span class="areatitle">作业时间分析图</span>
					</div>
					<div class="ui-widget-content">
						<div style="height: 3em;line-height: 3em; margin-left:0.2em;float:left;">
							图例：
						</div>
						<!--div class="legend" d_type="9">未知暂停</div-->
						<div class="legend" d_type="0">直接作业工时-特定维修对象</div>
						<div class="legend" d_type="1">直接辅助作业工时</div>
						<div class="legend" d_type="6">间接作业工时</div>
						<div class="legend" d_type="2">管理</div>
						<div class="legend" d_type="7">等待指示</div>
						<div class="legend" d_type="3">休息时间</div>
						<div class="legend" d_type="-1">代工中</div>
						<div class="clear"/>
					</div>
					<div class="ui-widget-content">
						<div style="height: 3em;line-height: 1.5em; margin-left:0.2em;">
							直接工<br>时比率：
						</div>
					</div>
					<div>
						<div id="container" class="ui-widget-content" style="height: 600px;">
							<div id="axis_base">
							<div class="y_columns">
								<div class="operator_flex"></div>
							</div>
							<div id="time_axis"></div>
							</div>
						</div>
					</div>
				</div>
				
				<!--div class="ui-widget-header ui-helper-clearfix areabase" style="padding-top:4px;">
					<input id="exportbutton" class="ui-button" value="导出图表" style="float:right;right:2px" type="button">
				</div-->
			</div>
		<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>