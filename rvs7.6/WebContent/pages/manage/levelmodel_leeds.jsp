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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/manage/levelmodel_leeds.js"></script>
<title>型号等级拉动台数设置</title>
</head>
<body class="outer" style="align: center;" id="searcharea">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div id="body-2" class="ui-widget-panel ui-corner-all" style="align: center; padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件辅助功能"/>
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
									<td class="ui-state-default td-title">梯队</td>
									<td>
										<select id="echelon" class="ui-widget-content" alt="梯队" name="rank_name">${sEchelonCode}</select>
									</td>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input type="text" name="model_name"  alt="型号" class="ui-widget-content" readonly="readonly">
										<input type="hidden" name="model_id" id="search_mode_id" alt="型号">
									</td>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<select id="search_level_id" class="ui-widget-content" alt="等级" name="rank_name">${sMaterialLevelInline }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">拉动台数预警</td>
									<td class="td-content" id="forecast_warn">
										<input type="radio" name="warn" id="forecast_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked">
										<label for="forecast_a" aria-pressed="false">(全)</label>
										<input type="radio" name="warn" id="forecast_not" class="ui-widget-content ui-helper-hidden-accessible" value="-1">
										<label for="forecast_not" aria-pressed="false">否</label>
										<input type="radio" name="warn" id="forecast" class="ui-widget-content ui-helper-hidden-accessible" value="1">
										<label for="forecast" aria-pressed="false">是</label>
										<input type="hidden" id="warn_template">
									</td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content"></td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content"></td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input id="resetbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float:right;right:2px" aria-disabled="false" role="button" value="清除">
							<input id="searchbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float:right;right:2px" aria-disabled="false" role="button" value="检索">
							<input type="hidden" id="goEchelon_code" value="${goEchelon_code}"/>
							<input type="hidden" id="goMaterial_level_inline" value="${goMaterial_level_inline}"/>
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="listarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">型号等级拉动台数一览</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="levelmodel_leeds_list"></table>
					<div id="levelmodel_leeds_listpager"></div>
					<div id="edit_levelmodel_leeds"></div>
				</div>
				
				<div class="ui-widget-header areabase"style="padding-top:4px;margin-top:0px;">
					<div id="executes" style="margin-left:4px;margin-top:3px;">
						<input type="button" id="download_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="下载编辑拉动台数" role="button" style="right:4px;float:right;">
						<input type="button" id="upload_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="上传拉动台数设定" role="button" style="left:4px;">
					</div>
				</div>
			</div>
			
			<div class="clear"></div>
		</div>
	</div>
	
	<div class="clear"></div>
	
	<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
		<!-- 下拉选择内容 -->
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
	
		<table class="subform">${mReferChooser}</table>
	</div>
	
</body>
</html>