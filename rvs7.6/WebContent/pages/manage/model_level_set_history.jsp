<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/manage/model_level_set_history.js"></script>
<title>型号终止维修设定履历</title>
</head>
<body class="outer" style="align:center;">
	<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;height:auto" id="body-3">
			<div id="body-lft" style="width:256px;float:left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件辅助功能"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 996px; float: left;margin-left:10px;margin-top:1px;">
				<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">检索条件</span> 
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content dwidth-middleright" style="width:994px;">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
				   					<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input class="ui-widget-content" readonly="readonly"  alt="型号" name="model_name" id="search_model_name" type="text">
										<input name="model_name" id="hidden_model_id" type="hidden">
									</td>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<select id="search_level_id" class="ui-widget-content" alt="等级" name="rank_name">${sMaterialLevelInline }</select>
									</td>
									<td class="ui-state-default td-title">操作者</td>
									<td class="td-content">
									  	 <input type="text" name="updated_by" alt="操作者" id="search_updated_by" maxlength="120" class="ui-widget-content">
									     <input name="updated_by" id="hidden_updated_by" type="hidden"> 
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">停止修理日期</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="avaliable_end_date_start" name="start_date" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="avaliable_end_date_end" name="start_date" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">最后更新时间</td>
									<td class="td-content">
										<input type="text" class="ui-widget-content" id="updated_time_start" name="start_date" readonly="readonly">起<br>
										<input type="text" class="ui-widget-content" id="updated_time_end" name="start_date" readonly="readonly">止
									</td>
									<td colspan="2"></td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="hidden" id="goMaterial_level_inline" value="${goMaterial_level_inline }">
							<input type="hidden" id="goEchelon_code" value="${goEchelon_code }">
							<input type="hidden" id="avaliable_end_date" value="${avaliable_end_date }">
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
				
				
				<div id="listarea" style="width:994px;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">型号终止维修设定历史一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser"> 
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="model_level_set_history_list"></table>
					<div id="model_level_set_history_listpager"></div>
				</div>
			</div>
			
			<div class="clear areaencloser"></div>
			
			<div class="referchooser ui-widget-content" id="model_name_referchooser" tabindex="-1">
				<table>
					<tr>
						<td width="50%">过滤字:<input type="text"/></td>	
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table class="subform">${mReferChooser}</table>
			</div>
			
			<div class="referchooser ui-widget-content" id="opertor_name_referchooser" tabindex="-1">
				<table>
					<tr>
						<td width="50%">过滤字:<input type="text"/></td>	
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table class="subform">${opertorIdNameOptions}</table>
			</div>
			</div>
		</div>
</body>
</html>