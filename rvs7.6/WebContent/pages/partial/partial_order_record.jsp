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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/partial/partial_order_record.js"></script>
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
</style>
<title>零件订购现状</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
			<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
				<jsp:param name="sub" value="p"/>
			</jsp:include>
	</div>

<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">

<div id="searcharea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-s"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-full" style="display:none;">
		<form id="searchform" method="POST">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">维修对象机种</td>
					<td class="td-content">
						<select name="category_id" id="search_category_id" class="ui-widget-content" multiple="" style="display: none;">
							${cOptions}
						</select>
					</td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content">
						<input class="ui-widget-content" readonly="readonly"  name="model_name" id="txt_modelname" type="text">
						<input name="modelname" id="search_modelname" alt="型号" type="hidden">
					</td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content" id="cond_level">
						<select id="search_level" class="ui-widget-content">
							${lOptions}
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">客户同意日</td>
					<td class="td-content">
						<input type="text" id="search_agreed_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_agreed_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">零件订购日</td>
					<td class="td-content">
						<input type="text" id="search_order_date_start" class="ui-widget-content" readonly="readonly" value="${search_order_date_start}">起<br>
						<input type="text" id="search_order_date_end" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">总组出货日</td>
					<td class="td-content">
						<input type="text" id="search_outline_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_outline_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件到货日</td>
					<td class="td-content">
						<input type="text" id="search_reach_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_reach_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">入库预定日</td>
					<td class="td-content">
						<input type="text" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">梯队</td>
					<td class="td-content">
						<select id="search_echelon" multiple class="ui-widget-content">
							${eOptions}
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件编号</td>
					<td class="td-content">
						<input type="text" id="search_partial_code" name="partial_code" alt="零件编号" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content">
					<td class="ui-state-default td-title"></td>
					<td class="td-content">
				</tr>
			</table>
				<div style="height:44px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
		</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">零件订购现状一览</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div id="planned_listarea" class="ui-widget-content">
		<div style="height:44px;border-bottom:0;" id="infoes" class="ui-buttonset">
			<input type="radio" name="infoes" class="ui-button ui-corner-up" id="echelon_button" value="" role="button" selected="selected"><label for="echelon_button">梯队</label>
			<input type="radio" name="infoes" class="ui-button ui-corner-up" id="level_model_button" value="12" role="button" ><label for="level_model_button">等级型号</label>
			<input type="radio" name="infoes" class="ui-button ui-corner-up" id="partial_button" value="13" role="button"><label for="partial_button">零件</label>
			<input type="hidden" id="select_radio">
			<label id="periodMessage" style="float:right;font-size: larger;line-height: 44px;"></label>
		</div>
	</div>

	<div id="echelon_contents">
		<table id="echelon_list" style="margin:0;"></table>
		<div id="echelon_listpager"></div>
	</div>
	<div id="level_model_contents">
		<table id="list" style="margin:0;"></table>
		<div id="listpager"></div>
	</div>
	<div id="partial_contents">
		<table id="partial_button_list" style="margin:0;"></table>
		<div id="partial_button_listpager"></div>
	</div>

	<div class="clear areaencloser"></div>
</div>

<div id="functionarea" class="dwidth-full" style="margin:auto;">
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" class="ui-button" id="reportbobutton" value="报表导出" style="float:right;right:2px;"/>
			<input type="button" class="ui-button" id="partialbo_files_downloadbutton" value="BO分析表" style="float:right;right:2px;"/>
		</div>
	</div>
</div>

<div id="process_dialog">
</div>

</div>

<div id="footarea">
</div>

<div id="partial_order_chart" style="display:none;">
	<div id="chart_container" style="float:right;min-width:540px;height:400px"></div>
</div>

<div id="month_files_area" class="dwidth-middleright">
	<table id="month_files_list"></table>
	<div id="month_files_listpager"></div>
</div>

<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
    <table>
		<tbody><tr>
			<td width="50%">过滤字:<input type="text"></td>	
			<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
		</tr>
	</tbody></table>
	<table class="subform">${mReferChooser}</table>
</div><div style="position: absolute; top: 271px; left: 1045.48px; z-index: 1; display: none;" id="ui-datepicker-div" class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"><div class="ui-datepicker-header ui-widget-header ui-helper-clearfix ui-corner-all"><a class="ui-datepicker-prev ui-corner-all" data-handler="prev" data-event="click" title="Prev"><span class="ui-icon ui-icon-circle-triangle-w">Prev</span></a><a class="ui-datepicker-next ui-corner-all" data-handler="next" data-event="click" title="Next"><span class="ui-icon ui-icon-circle-triangle-e">Next</span></a><div class="ui-datepicker-title"><span class="ui-datepicker-year">2013</span><span>年</span>&nbsp;<span class="ui-datepicker-month">八月</span></div></div><table class="ui-datepicker-calendar"><thead><tr><th class="ui-datepicker-week-end"><span title="Sunday"><span style="color:red;">日</span></span></th><th><span title="Monday">一</span></th><th><span title="Tuesday">二</span></th><th><span title="Wednesday">三</span></th><th><span title="Thursday">四</span></th><th><span title="Friday">五</span></th><th class="ui-datepicker-week-end"><span title="Saturday"><span style="color:red;">六</span></span></th></tr></thead><tbody><tr><td class=" ui-datepicker-week-end ui-datepicker-other-month ui-datepicker-unselectable ui-state-disabled">&nbsp;</td><td class=" ui-datepicker-other-month ui-datepicker-unselectable ui-state-disabled">&nbsp;</td><td class=" ui-datepicker-other-month ui-datepicker-unselectable ui-state-disabled">&nbsp;</td><td class=" ui-datepicker-other-month ui-datepicker-unselectable ui-state-disabled">&nbsp;</td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">1</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">2</a></td><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">3</a></td></tr><tr><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">4</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">5</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">6</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">7</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">8</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">9</a></td><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">10</a></td></tr><tr><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">11</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">12</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">13</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">14</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">15</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">16</a></td><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">17</a></td></tr><tr><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">18</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">19</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">20</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">21</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">22</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">23</a></td><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">24</a></td></tr><tr><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">25</a></td><td class="  ui-datepicker-current-day" data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default ui-state-active" href="#">26</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">27</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">28</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">29</a></td><td class=" " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">30</a></td><td class=" ui-datepicker-week-end " data-handler="selectDay" data-event="click" data-month="7" data-year="2013"><a class="ui-state-default" href="#">31</a></td></tr></tbody></table><div class="ui-datepicker-buttonpane ui-widget-content"><button type="button" class="ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all" data-handler="today" data-event="click">今天</button><button type="button" class="ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all" data-handler="hide" data-event="click">清空</button></div></div>
</div></body></html>
