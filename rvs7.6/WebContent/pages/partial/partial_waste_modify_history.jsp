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
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/partial/partial_waste_modify_history.js"></script>

<title>零件废改订历史管理</title>
</head>
<body class="outer" style="align:center;">


<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
<div id="basearea" class="dwidth-full" style="margin: auto;">
	<jsp:include page="/header.do" flush="true">
		<jsp:param name="part" value="2"/>
	</jsp:include>
</div>
<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;" id="body-3">
	<div id="body-lft" style="width:256px;float:left;">
		<jsp:include page="/appmenu.do?method=pinit" flush="true">
			<jsp:param name="linkto" value="零件基础数据管理"/>
		</jsp:include>
	</div>
	<div style="width:1012px;float:left;">
		<div id="body-mdl" class="dwidth-middleright" style="margin:auto;">
			<div id="body-mdl" style="width: 994px; float: left;">
			<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">检索条件</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
	        </div>
	
	<div class="ui-widget-content dwidth-middleright">	
	<!-- 检索条件 -->
		<form id="searchform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
				   <td class="ui-state-default td-title">型号</td>
					<td class="td-content"><input class="ui-widget-content" readonly="readonly"  alt="型号" name="model_name" id="search_model_name" type="text">
						<input name="model_name" id="hidden_model_name" type="hidden"></td>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content"><input type="text" name="code" alt="零件编码" id="search_code" class="ui-widget-content"/></td>
					<td class="ui-state-default td-title">零件名称</td>
					<td class="td-content"><input type="text" name="name" alt="零件名称" id="search_name" maxlength="120" class="ui-widget-content"></td>
				</tr>
				<tr>
				   <td class="ui-state-default td-title">起效日期</td>
					<td class="td-content">
					<input type="text" class="ui-widget-content" id="search_active_date_start" name="active_date" readonly="readonly">起<br>
					<input type="text" class="ui-widget-content" id="search_active_date_end" name="active_date" readonly="readonly">止</td>	
				  <td class="ui-state-default td-title">操作者</td>
				  <td class="td-content">
				  	 <input type="text" name="updated_by" alt="操作者" id="search_updated_by" maxlength="120" class="ui-widget-content">
				     <input name="updated_by" id="hidden_updated_by" type="hidden"> 
				     <!--  <td class="td-content">
				     <select id="search_updated_by" class="ui-widget-content"></select>-->
				  </td>
					<td class="ui-state-default td-title">操作类型</td>
				  <td class="td-content">
				   	  <div id="search_operate_type" class="ui-buttonset">
				   	       <input type="radio" name="operate_type" id="search_operate_type_no" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_operate_type_no" aria-pressed="false">(全)</label>
						   <input type="radio" name="operate_type" id="search_operate_type_revise" class="ui-widget-content ui-helper-hidden-accessible" value="1" ><label for="search_operate_type_revise" aria-pressed="false">改</label>
						   <input type="radio" name="operate_type" id="search_operate_type_waste" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="search_operate_type_waste" aria-pressed="false">废</label>
						 <!-- <input type="radio" name="operate_type" id="search_operate_type_add" class="ui-widget-content ui-helper-hidden-accessible" value="3"><label for="search_operate_type_add" aria-pressed="false">增</label> --> 
					 </div>
				 </td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
			</div>
		</form>		
	</div> 

<div class="clear areaencloser"></div>
	
	<div id="listarea" class="width-middleright">
		<table id="list"></table>
		<div id="listpager"></div>
	</div>

	<div id="confirmmessage"></div>

</div>

<div class="clear"></div>
	<div class="referchooser ui-widget-content" id="model_name_referchooser" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform">${mReferChooser }</table>
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
<div class="clear areaencloser dwidth-middleright"></div>
</div>
</div>

</body></html>