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
<script type="text/javascript" src="js/partial/partial_monitor.js"></script>
<script type="text/javascript" src="js/partial/common/change_base_value.js"></script>
<style>
.slept td {
background-color: lightgray;
}
.outsetting td {
background-color: #0070C0;
color: white;
}

.outsetting:hover td {
color: yellow;
}

.periodover td[aria-describedby='partial_button_list_order_num'] {
background-color: pink;
}
.tofix td[aria-describedby='partial_button_list_partial_code'],
.tofix td[aria-describedby='partial_button_list_partial_name'] {
	color:red;
}
.up-active
{ border: 1px solid #ff6b7f !important;
background: #db4865 url(css/olympus/images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat !important; 
font-weight: bold !important; 
color: #ffffff !important; }
.up-active-less
{ border: 1px solid #b4d100 !important;
background: #ffff38 url(css/olympus/images/ui-bg_dots-medium_80_ffff38_4x4.png) 50% 50% repeat; 
color: #363636;
}

#rate_detail td.td-content{
	text-align:right;
	padding-right:1em;
}

.baselined td {
	background-color: #FFC4FF;
}
#rate_history td {
padding: 1px;
}

</style>
<title>零件基准设定监控</title>
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
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-full" style="">
		<form id="searchform" method="POST" action="partial_base_line_value.do">
			<table class="condform">
				<tbody><!--tr>
					<td class="ui-state-default td-title">维修对象机种</td>
					<td class="td-content">
						<select name="category_id" id="search_category_id" class="ui-widget-content" multiple="" style="display: none;">
							${cOptions}
						</select>
					</td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content">
						<input class="ui-widget-content" readonly="readonly" name="model_name" id="txt_modelname" type="text">
						<input name="modelname" id="search_modelname" alt="型号" type="hidden">
					</td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content" id="cond_level">
						<select id="search_level" class="ui-widget-content">
							${lOptions}
						</select>
					</td>
				</tr-->
				<tr>
					<!--td class="ui-state-default td-title">客户同意日</td>
					<td class="td-content">
						<input type="text" id="search_agreed_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_agreed_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td-->
					<td class="ui-state-default td-title">零件订购日</td>
					<td class="td-content" colspan="5">
						<input type="text" id="search_order_date_start" class="ui-widget-content" readonly="readonly" value="${search_order_date_start}" org="${search_order_date_start}">起&nbsp;&nbsp;～&nbsp;&nbsp;
						<input type="text" id="search_order_date_end" class="ui-widget-content" readonly="readonly" value="${search_order_date_end}" org="${search_order_date_end}">止
					</td>
					<!--td class="ui-state-default td-title">总组出货日</td>
					<td class="td-content">
						<input type="text" id="search_outline_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_outline_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td-->
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件编号</td>
					<td class="td-content">
						<input type="text" id="search_partial_code" name="partial_code" alt="零件编号" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">消耗率高于警报线</td>
					<td class="td-content">
						<input type="text" id="filter_l_high" name="l_high" alt="速度警报" value="100" class="ui-widget-content"> %
					</td>
					<td class="ui-state-default td-title">消耗率低于警报线</td>
					<td class="td-content">
						<input type="text" id="filter_l_low" name="l_low" alt="速度警报" value="20" class="ui-widget-content"> %
				</td></tr>
				<tr>
					<td class="ui-state-default td-title">置顶显示</td>
					<td class="td-content" colspan="5">
						<span style="padding-right:.7em;">消耗率高：</span><input type="button" class="ui-button top_stick" name="higher" value="通常显示" />
						<span style="padding-left:2em;padding-right:.7em;">消耗率低：</span><input type="button" class="ui-button top_stick" name="lower" value="通常显示"/>
						<!--span style="padding-left:2em;padding-right:.7em;">3天BO发生：</span><input type="button" class="ui-button top_stick" name="ltlater" value="通常显示"/-->
					</td>
				</tr>
				</tbody></table>
				<div style="height:44px">
					<input type="hidden" name="edit_partial_code"/>
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
		</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">零件基准设定一览</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div id="planned_listarea" class="ui-widget-content">
		<div id="chooser" class="dwidth-full ui-widget-content" style="border-bottom: 0;height:44px;">
			<div id="colchooser" style="float:left;padding-top:22px;">
				<input type="checkbox" id="colchooser_sorcwh"></input><label for="colchooser_sorcwh">SORCWH 补充</label>
				<input type="checkbox" id="colchooser_wh2p"></input><label for="colchooser_wh2p">WH2P 补充</label>
				<input type="checkbox" id="colchooser_ogz"></input><label for="colchooser_ogz">OGZ 补充</label>
			</div>
		<label id="periodMessage" style="float:right;font-size: larger;line-height: 44px;"></label>
		<div class="clear"></div>
		</div>
	</div>

	<div id="in_contents">
		<table id="partial_button_list" style="margin:0;"></table>
	</div>
	<!--div id="out_contents" style="display: none;">
		<table id="out_list" style="margin:0;"></table>
		<div id="out_listpager"></div>
	</div-->

	<div class="clear"></div>
</div>

<div id="functionarea" class="dwidth-full" style="margin:auto;height:44px;">
	<div id="partial_button_listpager"></div>
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" id="editbutton" class="ui-button" value="基准值调整" style="float:left;margin-left:6px;"/>
			<input type="button" id="currentResultbutton" class="ui-button" value="当前结果导出" style="float:right;margin-right:10px;"/>
		</div>
	</div>
</div>

<div id="process_dialog">
</div>

</div>

</div>

<div id="footarea">
</div>

<div id="partial_order_chart" style="display:none;">
	<div style="float:left;min-width:540px;height:400px">
		<div>
			<table class="condform" id="rate_history" style="width:540px;">
				<tr>
					<td class="ui-state-default td-title">前半年<br>消耗速率</td>
					<td class="td-content"></td>
					<td class="ui-state-default td-title">前三个月<br>消耗速率</td>
					<td class="td-content"></td>
					<td class="ui-state-default td-title">当月<br>消耗速率</td>
					<td class="td-content"></td>
				</tr>
			</table>
		</div>
		<div id="chart_container" style="min-width:540px;height:360px"></div>
	</div>
	<div id="areaDetail" style="float:right;width:300px;height:400px">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">消耗速率详细</span>
			</div>
			<div class="ui-widget-content">
				<table class="condform" id="rate_detail">
					<tr>
						<td class="ui-state-default td-title">总 补充数</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">总 基准值</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">总 消耗速率</td>
						<td class="td-content"></td>
					</tr>
					<tr  style="height:1em;"></tr>
					<tr>
						<td class="ui-state-default td-title">OSH 补充数</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">OSH 基准值</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">OSH 消耗速率</td>
						<td class="td-content"></td>
					</tr>
					<tr  style="height:1em;"></tr>
					<tr>
						<td class="ui-state-default td-title">OGZ 补充数</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">OGZ 基准值</td>
						<td class="td-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">OGZ 消耗速率</td>
						<td class="td-content"></td>
					</tr>
				</table>
			</div>
	</div>
</div>

</div></body></html>
