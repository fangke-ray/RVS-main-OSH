<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
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
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/infect/devices_distribute.js"></script>

<title>设备工具分布</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="3" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具信息管理" />
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">检索条件</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<div class="ui-widget-content">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">管理编号</td>
									<td class="td-content">
										<input id="search_manage_code" class="ui-widget-content" type="text">
									</td>
									<td class="ui-state-default td-title">品名</td>
									<td class="td-content">
										<input id="search_name" class="ui-widget-content" type="text">
										<input type="hidden" id="hidden_devices_type_id">
									</td>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input type="text" id="search_model_name" class="ui-widget-content">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">分发课室</td>
									<td class="td-content">
										<select id="search_section_id">${sectionOptions}</select>
									</td>
									<td class="ui-state-default td-title">责任工程</td>
									<td class="td-content">
										<select id="search_responsible_line_id">${lineOptions }</select>
									</td>
									<td class="ui-state-default td-title">责任工位</td>
									<td class="td-content">
										<input type="text" id="search_responsible_position_name" class="ui-widget-content">
										<input name="position_name" id="hidden_responsible_position_id" type="hidden">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">管理员</td>
									<td class="td-content">
										<input type="text" id="search_manager_operator_name" class="ui-widget-content">
										<input type="hidden" id="hidden_manager_operator_id">
									</td>
									<td class="ui-state-default td-title">发放日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="provide_date_start" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="provide_date_end" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">管理等级</td>
									<td class="td-content">
										<select id="search_manage_level">${goManageLevel }</select>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
							<input type="hidden" id="sManageLevel" value="${sManageLevel }">
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="listarea" class="">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">设备工具分布一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="list"></table>
					<div id="listpager"></div>
				</div>
				
				<div id="detail" style="display:none">
					<table class="condform">
						<tr>
							<td class="ui-state-default td-title">管理编号</td>
							<td class="td-content">
								<label id="label_manage_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">品名</td>
							<td class="td-content">
								<label id="label_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">型号</td>
							<td class="td-content">
								<label id="label_model_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">分发课室</td>
							<td class="td-content">
								<label id="label_section_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">责任工程</td>
							<td class="td-content">
								<label id="label_line_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">责任工位</td>
							<td class="td-content">
								<label id="label_process_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">管理员</td>
							<td class="td-content">
								<label id="label_manager"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">发放日期</td>
							<td class="td-content">
								<label id="label_provide_date"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">发放者</td>
							<td class="td-content">
								<label id="label_provider"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">管理等级</td>
							<td class="td-content">
								<label id="label_manage_level"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">放置位置</td>
							<td class="td-content">
								<label id="label_location"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">厂商</td>
							<td class="td-content">
								<label id="label_brand"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">导入日期</td>
							<td class="td-content">
								<label id="label_import_date"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">状态</td>
							<td class="td-content">
								<label id="label_status"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">备注</td>
							<td class="td-content">
								<textarea id="label_comment"  rows="3" cols="35" disabled="disabled" style="resize:none"></textarea>
							</td>
						</tr>
					</table>
				</div>
				
				
				<div id="name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform">${nReferChooser}</table>
				</div>
				<div id="position_name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform">${pReferChooser}</table>
				</div>
				<div id="manage_operator_name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform">${oReferChooser}</table>
				</div>
				
			</div>
			
			<div class="clear"></div>
		</div>
		
		
	</div>
</body>
</html>