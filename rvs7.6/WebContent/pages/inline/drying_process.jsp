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
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/inline/drying_process.js"></script>

<title>烘干进程</title>
</head>
<body class="outer">
	<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="进度查询" />
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   <span class="areatitle">检索条件</span>
				    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content">	
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">维修对象型号</td>
									<td class="td-content">
										<input type="text" id="search_model_name" class="ui-widget-content">
										<input type="hidden" id="hidden_model_id">
									</td>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content">
										<input type="text" id="search_omr_notifi_no" class="ui-widget-content" >
									</td>
									<td class="ui-state-default td-title">工位</td>
									<td class="td-content">
										<input type="text" id="search_position_name" class="ui-widget-content" >
										<input type="hidden" id="hidden_position_id">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select id="search_section_id">${sOptions}</select>
									</td>
									<td class="ui-state-default td-title">烘干设备</td>
									<td class="td-content">
										<input type="text" id="search_device_manage_name" class="ui-widget-content" >
										<input type="hidden" id="hidden_device_manage_id">
									</td>
									<td class="ui-state-default td-title">进行状态</td>
									<td class="td-content">
										<div class="ui-buttonset">
											<input type="radio" name="status" class="ui-button" id="all" value="">
											<label for="all">(全部)</label>
											<input type="radio" name="status" class="ui-button" id="goingon" value="1">
											<label for="goingon">进行中</label>
											<input type="radio" name="status" class="ui-button" id="over" value="2">
											<label for="over">进行完毕</label>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input class="ui-button" id="resetbutton" value="清除" style="float: right; right: 2px" type="button">
							<input class="ui-button" id="searchbutton" value="查询" style="float: right; right: 2px" type="button">
						</div>
					</form>
				</div>
				
				<div class="areaencloser"></div>
	
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">烘干进程一览</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<table id="list"></table>
				<div id="listpager"></div>
				
				<div class="ui-widget-header areabase" style="width: 992px; padding-top: 4px; margin-top: 0px;">
					<div style="margin-left:4px;margin-top:2px;">
						<input id="drying_finish" class="ui-button" type="button" value="烘干完成">
					</div>
				</div>
			</div>
			
			<div class="referchooser ui-widget-content" id="search_model_id_referchooser" tabindex="-1">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table  class="subform">${mReferChooser}</table>
			</div>
			
			<div class="referchooser ui-widget-content" id="search_position_id_referchooser" tabindex="-1">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table  class="subform">${pReferChooser}</table>
			</div>
			
			<div class="referchooser ui-widget-content" id="search_device_manage_id_referchooser" tabindex="-1">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table  class="subform">${dryingOvenDeviceReferChooser}</table>
			</div>
			
			<div class="clear"></div>
		</div>	
	</div>
</body>
</html>