<%@page import="framework.huiqing.common.util.CodeListUtils"%>
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
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/partial/sorc_loss.js"></script>
<title>SORC损金</title>
</head>
<style>
  .td-nogood-active{
    cursor:pointer;
  }
</style>
<body class="outer" style="overflow: auto;">
  <div class="width-full" style="align:center;margin:auto;margin-top:16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
				<jsp:param name="sub" value="p"/>
			</jsp:include>
		</div>
			
		<div class="clear" style="height: 10px;"></div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">
		<div id="searcharea" class="dwidth-full">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
				<span class="areatitle">检索条件</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			<div class="ui-widget-content dwidth-full">
				<form id="searchform" method="POST">
					<table class="condform">
						<tr>
						    <td class="ui-state-default td-title">出货月</td>
							<td class="td-content"><input type="text" id="search_ocm_shipping_month" class="ui-widget-content"  maxlength="8" value="${year_month}"></td>
						
							<td class="ui-state-default td-title">出货日期</td>
							<td class="td-content">
								<input type="text" class="ui-widget-content" id="search_ocm_shipping_date"readonly="readonly">
							</td>	
							
							<td class="ui-state-default td-title">修理单号</td>
							<td class="td-content">
								<input type="text" class="ui-widget-content" id="search_sorc_no">
							</td>		
						</tr>
						<tr>
					     	<td class="ui-state-default td-title">订购日期</td>
							<td class="td-content">
								<input type="text" name="order_date" class="ui-widget-content" id="search_order_date" value="${current_date}">
							</td>	
						</tr>
						<tr style="display:none">
						 	<td>
						    <select id="hidden_discoverProject">${sDiscoverProject}</select>
							<select id="hidden_liability_flg" >${sLiabilityFlg }</select>
							<select id="hidden_service_free_flg">${sServiceFreeFlg }</select>
						 	</td>
						 </tr>
					</table>
							<div style="height:44px">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
							</div>
							
							<input id="hidden_ocm" type="hidden" value="${hOcm}"/>
							<input id="hidden_ocm_rank" type="hidden" value="${hOcmRank}"/>
							<input id="hidden_sorc_rank" type="hidden" value="${hSorcRank}"/>
							<input id="hidden_discover_project" type="hidden" value="${hDiscoverProject}"/>
							<input id="hidden_liabliity_flg"  type="hidden" value="${hLiabliityFlg}"/>
							<input id="hidden_service_freeFlg" type="hidden" value="${hServiceFreeFlg}"/>
					</form>
	       </div>
		   <div class="clear areaencloser dwidth-full"></div>
		</div>

		<div id="listarea" class="dwidth-full">
			<table id="list"></table>
			<div id="listpager"></div>
			<div class="clear areaencloser"></div>
		</div>
		<div id="functionarea" class="dwidth-full">
		<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
				<div id="executes" style="margin-left:4px;margin-top:4px;">
					<input type="button" id="loss_import_button" class="ui-button" value="单元损金导入" style="float:left;margin-left:10px;"/>	
					<input type="button" id="month_loss_export_button" class="ui-button" value="月损金一览表导出" style="float:right;margin-right:10px;"/>	
				</div>
			</div>
			<div class="clear"></div>
		</div>
    </div>
</div>
<div id="process_dialog"></div>
<div id="confirm_message"></div>
</body>
</html>