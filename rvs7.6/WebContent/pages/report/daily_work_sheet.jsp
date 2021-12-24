<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Page-Exit" ; content="blendTrans(Duration=1.0)">
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
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
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/report/daily_work_sheet.js"></script>
<style type="text/css">
.upload-button:hover {
	background: #FFFFFF;
	color: #F8BB14;
	cursor: pointer;
}

.upload-button:active {
	overflow: visible;
	border: 1px solid #ff6b7f;
	background: #db4865
		url(images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat;
	font-weight: bold;
	color: #ffffff;
}
}
</style>
<title>工作记录表</title>
</head>
<body class="outer" style="align: center;">


<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
	<div id="basearea" class="dwidth-full" style="margin: auto;">
		<jsp:include page="/header.do" flush="true">
			<jsp:param name="part" value="3" />
		</jsp:include>
	</div>

	<div class="ui-widget-panel ui-corner-all width-full"
		style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;"
		id="body-2">
		<div id="body-lft" style="width: 256px; float: left;">
			<jsp:include page="/appmenu.do" flush="true">
				<jsp:param name="linkto" value="文档管理" />
			</jsp:include>
		</div>
		<div id="body-mdl" style="width: 994px; float: left;">
			<div
				class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">工作记录表</span> <a role="link"
					href="javascript:void(0)" class="HeaderButton areacloser"> <span
					class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			<div class="ui-widget-content">
				<div style="width: 991px; height: 44px; border-bottom: 0;" id="infoes" class="dwidth-full ui-widget-content">
					<div>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="acceptbutton" value="1" role="button" checked="checked">
						<label for="acceptbutton">受理</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="disinfectbutton" value="3" role="button">
						<label for="disinfectbutton">消毒</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="disinfectionbutton" value="2" role="button">
						<label for="disinfectionbutton">灭菌</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="linebutton" value="4" role="button">
						<label for="linebutton">投线</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plancombutton" value="5" role="button">
						<label for="plancombutton">计划</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="shipmentbutton" value="6" role="button">
						<label for="shipmentbutton">出货</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="unrepairebutton" value="7" role="button">
						<label for="unrepairebutton">未修理</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="snoutbutton" value="8" role="button">
						<label for="snoutbutton">D/E 组装</label>
						<input type="radio" name="infoes" class="ui-button ui-corner-up" id="washbutton" value="9" role="button">
						<label for="washbutton">钢丝固定件清洗</label>
						<!--当前操作者的工位-->
						<input id="hidden_is_accept" type="hidden" value="${accept}">
						<input id="hidden_is_disinfection" type="hidden" value="${disinfection}">
						<input id="hidden_is_disinfect" type="hidden" value="${disinfect}">
						<input id="hidden_is_shipment" type="hidden" value="${shipment}">
						<input id="hidden_is_inline" type="hidden" value="${inline}">
					</div>
				</div>
				<div id="daily_work_sheet_load">

					<div id="daily_work_sheet_view" class="dwidth-middleright">
						<div class="ui-widget-content" style="width: 991px;">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">年月</td>
										<td class="td-content">
											<input type="text" readyonly="readonly" id="search_year_month_day" maxlength="12" class="ui-widget-content" value="${sYearMonth}">
										</td>
									<tr>
								</tbody>
							</table>
							<div style="height: 44px">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default" id="choosebutton" value="选择" role="button" aria-disabled="false" style="float: right; right: 2px">
								<!--input type="button" class="ui-button ui-widget ui-state-default" id="monthdownbutton" value="下载月报表" role="button" aria-disabled="false" style="float: right; right: 2px;display:none;"-->
								<input type="button" class="ui-button" id="monthshowbutton" value="显示月报表" role="button" style="float: right; right: 2px;display:none;">
							</div>
						</div>
						<table id="exd_list"></table>
						<div id="exd_listpager"></div>
						<div id="exd_listedit"></div>
						<div id="upload_file"></div>
					</div>

				</div>
			</div>

			<div class="clear dwidth-middle"></div>
		</div>
		<div class="clear"></div>
	</div>
</div>
</body>
</html>