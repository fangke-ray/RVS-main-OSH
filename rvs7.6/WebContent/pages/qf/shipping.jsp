<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/qf/turnover_case_common.js"></script>
<script type="text/javascript" src="js/qf/shipping.js"></script>
<title>出货</title>
</head>
<body class="outer" style="align: center;">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="在线作业"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 1022px; float: left;">
	<div id="uld_listarea" class="width-middleright">
		<table id="uld_list"></table>
		<div id="uld_listpager"></div>
		<div id="uld_listedit"></div>
	</div>

		<div id="executearea">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">出货处理</span>
			</div>
			<div class="ui-widget-content dwidth-middleright">
				<div class="ui-widget-content" id="scanner_container">
					<div class="ui-state-default td-title">扫描录入区域</div>
					<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width: 986px;"></input>
					<div style="text-align: center;">
						<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
					</div>
				</div>
				<div class="ui-widget-content dwidth-middleright" id="material_details" style="display: none;">
	
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content" id="show_model_name"></td>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content" id="show_serial_no"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content" id="show_sorc_no"></td>
									<td class="ui-state-default td-title">ESAS No.</td>
									<td class="td-content" id="show_esas_no"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">同意时间</td>
									<td class="td-content" id="show_agreed_date"></td>
									<td class="ui-state-default td-title">总组完成时间</td>
									<td class="td-content" id="show_finish_time"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content" id="show_level"></td>
									<td class="ui-state-default td-title">加急</td>
									<td class="td-content" id="show_scheduled_expedited"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">委托处</td>
									<td class="td-content" >
									<label id="show_bound_out_ocm" alt="委托处"></label>
									<select id="show_bound_out_ocm_select" style="display:none;">${oBoundOutOcm}</select>
									</td>
									<td class="ui-state-default td-title">通箱编号</td>
									<td class="td-content">
									 <input type="text" id="show_package_no" alt ="通箱编号" value="原箱"  class="ui-widget-content">
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="shipbutton" value="出货" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
				</div>
				<div class="clear dwidth-middleright"></div>
		</div>

		<div id="exd_listarea" class="width-middleright">
			<table id="exd_list"></table>
			<div id="exd_listpager"></div>
			<div id="exd_listedit"></div>
		</div>
<div id="functionarea" class="dwidth-middleright" style="margin:auto;">
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="tcWarehouseButton" value="通箱出库" role="button" aria-disabled="false">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="reportbutton" value="临时生成当日报表" role="button" aria-disabled="false" style="float: right; right: 2px">
		</div>
	</div>
	<div class="clear areaencloser"></div>
</div>

	</div>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>
		</div>
	</div>
<div id="pop_detail"></div>

</body>
</html>