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
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
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
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/report/remain_time_report.js"></script>

<title>倒计时达成率</title>
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

				<!-- Tab选项卡 -->	
				<div style="border-bottom: 0;" class="ui-widget-content dwidth-middleright">
					<div id="infoes" class="ui-buttonset">
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_rate_tab" value="page_rate">
						<label for="page_rate_tab">倒计时达成率</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_unreach_tab" value="page_unreach">
						<label for="page_unreach_tab">倒计时未达成理由</label>
					</div>
				</div>

				<!-- 倒计时达成率分析  -->
				<div id="page_rate" class="record_page">

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
										<input type="text" id="search_start_date" name="start_date" alt="作业开始时间" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_end_date" name="end_date" alt="作业结束时间" class="ui-widget-content" readonly>止
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

					<div id="listarea">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">倒计时达成率一览</span>
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
				<!-- 倒计时未达成理由  -->
				<div id="page_unreach" class="record_page">
					<div id="searcharea2" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">检索条件</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div class="ui-widget-content">
						<form id="search2form" method="POST">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">发生工位</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" readonly>
										<input type="hidden" id="search_position_id" value="">
									</td>
									<td class="ui-state-default td-title">发生时间</td>
									<td class="td-content">
										<input type="text" id="search_contrast_time_start" alt="发生时间" value="${today}" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_contrast_time_end" alt="发生时间" class="ui-widget-content" readonly>止
									</td>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content">
										<input type="text" id="search_omr_notifi_no" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">判定原因</td>
									<td class="td-content">
										<select id="search_main_cause">
										${cOptions}
										</select>
									</td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content">
									</td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content">
									</td>
								</tr>
							</table>
							<div style="height:44px;">
								<input type="hidden" value="${cGO}" id="cOptions">
								<input class="ui-button" id="reset2button" value="清除" style="float:right;right:2px" type="button">
								<input class="ui-button" id="search2button" value="检索" style="float:right;right:2px" type="button">
							</div>
						</form>
					</div>
					<div id="listarea" class="width-middleright">
						<table id="unreachlist"></table>
						<div id="unreachlistpager"></div>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<div class="referchooser ui-widget-content" id="pReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${pReferChooser}</table>
	</div>
	${(role eq 'manager') ? '<div id="unreach_detail"/>' : ''}
</body>
</html>