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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<style>
#arrive_partial_list .ui-state-highlight {
background-color:white;}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/partial/partial_release.js"></script>
<title>零件发放</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div id="body-2" class="ui-widget-panel ui-corner-all" style="align: center; padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件入出库"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   <span class="areatitle">检索条件</span>
				    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content dwidth-middleright">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content"><input type="text" id="search_sorcno" maxlength="15" class="ui-widget-content"></td>
									<td class="ui-state-default td-title">零件订购日</td>
									<td class="td-content"><input name="" id="order_time_start" maxlength="50" class="ui-widget-content" readonly="readonly" type="text">起<br><input name="" id="order_time_end" maxlength="50" class="ui-widget-content" readonly="readonly" type="text">止</td>
									<td class="ui-state-default td-title">BO状态</td>
									<td class="td-content" id="bo_flg">
										<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">(全)</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_f" class="ui-widget-content ui-helper-hidden-accessible" value="9"><label for="cond_work_procedure_order_template_f" aria-pressed="false">无BO</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_work_procedure_order_template_t" aria-pressed="false">有BO</label>
										<input type="hidden" id="cond_work_procedure_order_template">
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="rebutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
			
				<div id="listarea" class="">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">发放维修对象一览</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="partial_list"></table>
					<div id="partial_listpager"></div>
				</div>
				
				<div class="clear"></div>
			</div>
			
			<div id="body-detail" style="width: 994px; float: left;display:none;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">维修对象详细信息</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				
				<div class="ui-widget-content" id="main">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">受理日</td>
								<td class="td-content">
									<label id="label_reception_date"></label>
								</td>
								<td class="ui-state-default td-title">客户</td>
								<td class="td-content">
									<label id="label_customer_name"></label>
								</td>
								<td class="ui-state-default td-title">同意日</td>
								<td class="td-content">
									<label id="label_agreed_date"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">修理单号</td>
								<td class="td-content">
									<label id="label_sorc_no"></label>
								</td>
								<td class="ui-state-default td-title">型号</td>
								<td class="td-content">
									<label id="label_model_name"></label>
								</td>
								<td class="ui-state-default td-title">机身号</td>
								<td class="td-content">
									<label id="label_serial_no"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">维修等级</td>
								<td class="td-content">
									<label id="label_level"></label>
								</td>
								<td class="ui-state-default td-title">修理分类</td>
								<td class="td-content">
									<label id="label_service_repair_flg"></label>
								</td>
								<td class="ui-state-default td-title">入库预定日</td>
								<td class="td-content">
									<label id="label_arrival_plan_date"></label>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="planned_listarea" class="ui-widget-content">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div id="arrive_partial">
						<table id="arrive_partial_list"></table>
						<div id="arrive_partial_listpager"></div>
					</div>
				</div>
				
				<div id="functionarea" style="margin:auto;height:44px;">
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
						<div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="取消" role="button" aria-disabled="false" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="submitbutton" value="发放" role="button" aria-disabled="false" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="bigsubmitbutton" value="整理准备" role="button" aria-disabled="false" style="float: right; right: 2px">
							<span id="submit_dest" style="float: right;margin-right: 1em;line-height: 2em;"></span>
							<% 
								String privacy = (String) request.getAttribute("privacy"); 
								if (privacy != null) {
							%>
							<%  
									if("pm".equals(privacy)) {
							%>
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="deletebutton" value="删除" role="button" aria-disabled="false" style="float: left; left: 2px;" >
							<%
									}
								}
							%>
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="evalbobutton" value="BO 判定" role="button" aria-disabled="false" style="float: left; left: 4px;" >
							<input type="hidden" id="material_level_inline" value="${goMaterial_level_inline }" />
							<input type="hidden" id="hide_materialID">
							<input type="hidden" id="hide_occur_times">
							<input type="hidden" id="bo_flg">
							<input type="hidden" id="privacy" value="${privacy }">
						</div>
					</div>
					<div class="clear"></div>
				</div>	
			</div>
			
			<div class="clear areaencloser dwidth-full"></div>
			
		</div>
	</div>
</body>
</html>