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
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/common/material_detail_pcs.js"></script>
<script type="text/javascript" src="js/data/qa_result.js"></script>

<title>出检结果</title>
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
					<jsp:param name="linkto" value="品保作业"/>
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
							<td class="ui-state-default td-title" rowspan="3">维修对象机种</td>
							
							<td class="td-content" rowspan="3">
							<select name="category_id" id="search_category_id" class="ui-widget-content">${cOptions}</select></td>
							
							
							<td class="ui-state-default td-title">维修对象型号
							</td>
							
							<td class="td-content">
								<input type="text" class="ui-widget-content" readonly="readonly">
								<input type="hidden" name="model_id" id="search_model_id">
							</td>
							<td class="ui-state-default td-title">维修课室</td>
							<td class="td-content">
								<select name="section_id" id="search_section_id" class="ui-widget-content">${sOptions}</select>
							</td>
							</tr>
						<tr>
						    <td class="ui-state-default td-title">机身号</td>
						    
							<td class="td-content"><input type="text" id="search_serialno" maxlength="12" class="ui-widget-content"></td>
						    
						    <td class="ui-state-default td-title" rowspan="2" >出检人员</td>
						    
							<td class="td-content" rowspan="2">
								<select name="operation_id" id="search_person_id" class="ui-widget-content">${sOperations}</select>
							</td>
								
										
						</tr>
						<tr>
						    
						    <td class="ui-state-default td-title">修理单号</td>
							<td class="td-content"><input type="text" id="search_sorcno" maxlength="15" class="ui-widget-content"></td>
			
						</tr>
						<tr>
							<td class="ui-state-default td-title">受理日期</td>
							<td class="td-content"><input type="text" name="reception_time" id="search_reception_time_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/><input type="text" name="name" id="search_reception_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
							
							<td class="ui-state-default td-title">品保通过日期</td>
							<td class="td-content"><input type="text" name="name" id="search_qa_pass_start" maxlength="50" class="ui-widget-content" readonly="readonly" value="${today}">起<br/><input type="text" name="name" id="search_qa_pass_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
							
							<td class="ui-state-default td-title">纳期</td>
							<td class="td-content"><input type="text" name="name" id="search_scheduled_date_start" maxlength="50" class="ui-widget-content" readonly="readonly" value="">起<br/><input type="text" name="name" id="search_scheduled_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
						     
							</tr>
							<tr>
							<td class="ui-state-default td-title">修理分类</td>
							<td class="td-content" >
							<select name="fix_type" id="search_fix_id" class="ui-widget-content">${sMaterialFixType}</select>
							</td>
			                 <td class="ui-state-default td-title">返修分类</td>
							<td class="td-content">
							<select name="service_repair_flg" id="search_repair_id" class="ui-widget-content">${sMaterialServiceRepair}</select>
							</td>
							
			                 <td class="ui-state-default td-title">直送</td>
							<td class="td-content">
							<select name="direct_flg" id="search_direct_id" class="ui-widget-content">${sMaterialDirect}</select>
							</td>
			
							</tr>
					</tbody>
					</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="hidden" id="h_date_start" value="${today}">
						<input type="hidden" id="h_goMaterialFixType" value="${goMaterialFixType}">
						<input type="hidden" id="h_goMaterialServiceRepaire" value="${goMaterialServiceRepaire}"/>
						<input type="hidden" id="h_goMaterialDirect" value="${goMaterialDirect}"/>						
					</div>
				</form>
			</div>			
			<div id="exd_listarea" class="width-middleright" style="margin-top:36px;">
				<table id="exd_list"></table>
				<div id="exd_listpager"></div>
				<div id="exd_listedit"></div>
			</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="clear areaencloser dwidth-middle"></div>
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
	<div id="wip_pop"></div>	
</body>
</html>