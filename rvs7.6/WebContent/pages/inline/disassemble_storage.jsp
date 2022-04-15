<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/inline/disassemble_storage.js"></script>

<% 
	String role = (String) request.getAttribute("role");
%>

<title>分解库位管理</title>
</head>
<body class="outer">
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
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
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
								<td class="ui-state-default td-title" rowspan="2">维修对象机种</td>
								
								<td class="td-content" colspan="3" rowspan="2" style="width:520px;"><select name="category_id" id="search_category_id" class="ui-widget-content">${cOptions}</select>
								</td>
								<td class="ui-state-default td-title">库位代码</td>
								<td class="td-content">
									<input type="text" name="case_code" id="search_case_code" maxlength="4" class="ui-widget-content">
								</td>
							</tr>
							<tr>
							    <td class="ui-state-default td-title">修理单号</td>
								<td class="td-content">
									<input type="text" name="omr_notifi_no" id="search_omr_notifi_no" maxlength="15" class="ui-widget-content">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">修理等级</td>
								<td class="td-content">
									<select name="level" id="search_level">
									${lOptions}
									</select>
								</td>
								<td class="ui-state-default td-title">放置工位</td>
								<td class="td-content" colspan="3"><select name="position_id" id="search_position_id">${pOptions}</select>
								</td>
							</tr>
						</tbody>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
						</div>
					</form>
				</div>
				
				<div id="exd_listarea" class="width-middleright" style="margin-top:36px;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">分解库位管理</span>
					</div>
					
					<table id="exd_list"></table>
					<div id="exd_listpager"></div>
				</div>
<% if (role != null) { %>
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div style="margin-left:4px;margin-top:6px;">
<% if ("admin".equals(role)) { %>
						<input type="button" id="createbutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="建立库位" role="button">
						<input type="button" id="changebutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="调整库位" role="button">
						<input type="button" id="removebutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="删除库位" role="button">
<% } %>
						<input type="button" id="warehousebutton" class="ui-button ui-widget ui-state-default ui-corner-all ui-state-disabled" value="手动出库" role="button" style="float:right; margin-right:4px;">
						<input type="button" id="movebutton" class="ui-button ui-widget ui-state-default ui-corner-all ui-state-disabled" value="移库" role="button" style="float:right; margin-right:4px;">
					</div>
				</div>			
<% } %>
			</div>
			<div class="clear"></div>
		</div>
		<div class="clear areaencloser dwidth-middle"></div>
	</div>
</body>

<% if (role != null) { %>
<div class="ui-widget-content" id="editarea" style="display:none;">
	<form id="editform" method="POST">
		<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">放置工位</td>
				<td class="td-content"><select name="position_id" id="edit_position_id">${pOptions}</select>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">库位代码</td>
				<td class="td-content">
					<input type="text" name="case_code" id="edit_case_code" maxlength="4" class="ui-widget-content">
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">自动分配</td>
				<td class="td-content" id="edit_auto_arrange">
					<input type="radio" name="auto_arrange" id="edit_auto_arrange_y" value="1" class="ui-widget-content"><label for="edit_auto_arrange_y">将会自动分配</label>
					<input type="radio" name="auto_arrange" id="edit_auto_arrange_n" value="0" class="ui-widget-content"><label for="edit_auto_arrange_n">不会</label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">货架</td>
				<td class="td-content">
					<input type="text" name="shelf" id="edit_shelf" maxlength="2" class="ui-widget-content">
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">层数</td>
				<td class="td-content">
					<input type="text" name="layer" id="edit_layer" maxlength="1" class="ui-widget-content">
				</td>
			</tr>
		</tbody>
		</table>
	</form>
</div>

<div id="storage_pop" style="display: none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span id="pos_storage" class="areatitle">库位一览</span>
	</div>
	<div class="ui-widget-content" id="pos_table">
	</div>
	<div class="clear areaencloser"></div>
</div>

<% } %>

</html>