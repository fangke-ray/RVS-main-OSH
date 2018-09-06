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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>

<script type="text/javascript" src="js/manage/echelon_allocate.js"></script>

<style>

#allocate_list tr td:nth-child(1) {
	width : 25px;
}
#allocate_list tr td:nth-child(2) {
	width : 174px;
}
#allocate_list tr td:nth-child(3) {
	width : 44px;
}
#allocate_list tr td:nth-child(4) {
	width : 58px;
}
#allocate_list tr td:nth-child(5) {
	width : 78px;
}
#allocate_list tr td:nth-child(6) {
	width : 56px;
}
</style>
<title>梯队设定</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件辅助功能"/>
				</jsp:include>
			</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
			    </div>
	</div>
<div id="body-mdl" style="width: 994px; float: left;">
	<div id="listarea" class="">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">梯队划分履历一览</span>
		</div>
		<table id="allocate_history_list"></table>
		<div id="allocate_history_listpager"></div>
	</div>

		<div id="functionarea" style="margin:auto;height:44px;">
			<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
				<div id="executes" style="margin-left:4px;margin-top:4px;">
					<input type="button" id="set_button" class="ui-button" value="梯队划分"/>
				</div>
			</div>
			<div class="clear"></div>
		</div>		
</div>
	<div class="clear"></div>
<div id="echelon_detail" style="display:none;">
		<div id="allocate_filter" style="border: 1px solid #93C3CD;">
			<table class="condform" style="width:476px">
				<tr class="db_click_search">
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content" style="width:auto">
								<input type="text"  name="model_name" id="detail_model_name"alt="型号" class="ui-widget-content" readonly="readonly">
								<input type="hidden" name="model_id" id="search_model_id"></td>
				</tr>
				<tr class="db_click_search">
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content" style="width:auto">
					<select id="detail_filter_level" name="rank" alt="等级">${levelOptions}</select>
					</td>
				</tr>
				<tr class="db_click_search">
					<td class="ui-state-default td-title">梯队</td>
					<td class="td-content" style="width:auto">
						<select id="detail_filter_echelon" class="ui-widget-content" alt="梯队">${echelonOptions}</select>
					</td>
				</tr>
				<tr class="end_date">
					<td class="ui-state-default td-title">统计区间开始</td>
					<td class="td-content" style="width:auto">
					<input type="text" class="ui-widget-content" id="search_start_date" readonly="readonly"></td>
					<td class="ui-state-default td-title">统计区间结束</td>
					<td class="td-content" style="width:auto">
					<input type="text" class="ui-widget-content" id="search_end_date" readonly="readonly">
					</td>
				</tr>
				</table>
				<div style="height:44px" class="end_date">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
				<input type="hidden" id="hidden_echelon_history_key"/> 
		</div>
		
		<table id="allocate_list"></table>
		<div id="allocate_listpager"></div>
		
	<div class="referchooser ui-widget-content" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${mReferChooser}</table>
	</div>
	<div id="footarea"></div>
</div>
<div id="update_success_dialog"></div>
<div class="clear areaencloser dwidth-middleright"></div>
</div>
</div>
</body>

</html>