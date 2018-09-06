<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- base href="http://localhost/rvs/" -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/lte-style.css">
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
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/partial/consumable_online.js"></script>

<style>
.fixQ {
	border:0;
	text-align:right;
}
.fixQ[changed] {
	background-color: #F0E290;
}
div.bar_fixed {

position: fixed;
bottom: 1px;
z-index: 180;
width: 992px;
}
.wait_for_adujst
{ border: 1px solid #ff6b7f !important;
background: #db4865 url(css/olympus/images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat !important; 
font-weight: bold !important; 
color: #ffffff !important; }

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

.littleball {
	font-size: 10px;
	-moz-border-radius-topleft: 8px;
	-webkit-border-top-left-radius: 8px;
	-khtml-border-top-left-radius: 8px;
	border-top-left-radius: 8px;
	-moz-border-radius-bottomleft: 8px;
	-webkit-border-bottom-left-radius: 8px;
	-khtml-border-bottom-left-radius: 8px;
	border-bottom-left-radius: 8px;
	-moz-border-radius-topright: 8px;
	-webkit-border-top-right-radius: 8px;
	-khtml-border-top-right-radius: 8px;
	border-top-right-radius: 8px;
	-moz-border-radius-bottomright: 8px;
	-webkit-border-bottom-right-radius: 8px;
	-khtml-border-bottom-right-radius: 8px;
	border-bottom-right-radius: 8px; //
	width: 180px;
	height: 18px;
	text-align: center; //
	background-color: green;
	padding: 1px;
}
</style>
<title>消耗品在线库存一览</title>
</head>

<body class="outer" style="align: center;">
	<div id="update_limit_date_after"></div>
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="消耗品管理"/>
				</jsp:include>
			</div>
			<!-- <div class="clear" style="height: 10px;"></div> -->
			<!-- 本体  -->
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
						<span class="areatitle">检索条件</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div class="ui-widget-content dwidth-middleright">
						<!-- 检索条件 -->
						<form id="searchform" method="POST" onsubmit="return false;">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">消耗品</td>
										<td class="td-content">
											<input name="code" id="search_code" alt="消耗品" class="ui-widget-content" type="text">
											<input type="hidden" id="res_type" value="${gqOptions}"/>
										</td>
										<td class="ui-state-default td-title">课室</td>
										<td class="td-content">
											<select id="search_section_id" name="section_id" alt="课室" class="ui-select2buttons">${sectionOptions}</select>
										</td>
										<td class="ui-state-default td-title">工程</td>
										<td class="td-content">
											<select id="search_line_id" name="line_id" alt="工程" class="ui-select2buttons">${lineOptions}</select>
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height: 44px">
								<input class="ui-button" id="reset_button" value="清除" role="button" aria-disabled="false" style="float: right; right: 2px" type="button">
								<input class="ui-button-primary ui-button" id="search_button" value="查询" role="button" style="float: right; right: 2px" type="button">
							</div>
						</form>
					</div>
					<div class="clear areaencloser"></div>
					<!-- JqGrid表格  -->
					<div id="listarea" class="width-middleright">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">消耗品在线库存一览</span>
						</div>
						<table id="consumable_list"></table>
						<div id="consumable_list_pager"></div>
						<div id="confirmmessage"></div>
						<div id="functionarea" class="areabase" style="margin-bottom:12px;">
							<div class="ui-widget-header areabase bar_fixed" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
								<div id="executes" style="margin-left: 4px; margin-top: 2px;">
									<input id="adjust_button" class="ui-button" value="清点开始" role="button" type="button">
									<div class="clear"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"/>
			</div>
		</div>
		<!-- 弹出信息  -->
		<div id="pop_window" style="display: none">
		</div>
		<div id="pop_windows" style="display: none">
		</div>
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
	<div id="footarea"></div>
</body>
</html>