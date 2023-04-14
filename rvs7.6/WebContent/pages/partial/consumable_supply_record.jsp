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
<script type="text/javascript" src="js/partial/consumable_supply_record.js"></script>

<title>消耗品发放记录</title>
</head>
<body class="outer">
	<div class="width-full" style="margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="消耗品/组件管理"/>
				</jsp:include>
			</div>
			
			<div style="width: 994px; float: left;">
				<div id="body-mdl">
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
										<td class="ui-state-default td-title">发放时间</td>
										<td class="td-content">
											<input type="text" class="ui-widget-content" id="search_supply_time_start" readonly="readonly" value="${startDate }">起<br>
											<input type="text" class="ui-widget-content" id="search_supply_time_end" readonly="readonly" value="${endDate }">止<br>
										</td>
										<td class="ui-state-default td-title">发放方式</td>
										<td class="td-content"><select id="search_apply_method">${sApplyMethod }</select></td>
										<td class="ui-state-default td-title">消耗品分类</td>
										<td class="td-content"><select id="search_type" multiple="multiple">${sConsumableType }</select></td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px" type="button">
								<input class="ui-button" id="searchbutton" value="检索" style="float:right;right:2px" type="button">

								<input type="button" id="dowloadtoptenbutton" style="right: 20px; float: right;" class="ui-button ui-widget ui-state-default ui-corner-all" value="消耗量Top10导出"/>
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">消耗品发放记录一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="list"></table>
					<div id="listpager"></div>
				</div>
				
				<div id="functionarea" style="margin:auto;height:44px;">
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
						<div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" id="dowloadbutton" style="right: 4px; float: right;" class="ui-button ui-widget ui-state-default ui-corner-all" value="下载"/>
						</div>
					</div>
				</div>
				
				<div class="areaencloser"></div>
				
				<input type="hidden" id="hid_apply_method" value="${goApplyMethod }">
				<input type="hidden" id="hid_consumable_type" value="${goConsumableType }">
			</div>
			
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>