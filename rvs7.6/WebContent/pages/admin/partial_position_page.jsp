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
<script type="text/javascript" src="js/admin/partial_position.js"></script>

<title>零件定位信息</title>
</head>
<% 
	String role = (String) request.getAttribute("role");
	boolean isOperator = ("operator").equals(role);
%>
<body class="outer" style="align: center;">
<div id="update_limit_date_after"></div>
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件基础数据管理"/>
				</jsp:include>
			</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<div id="body-mdl" style="width: 994px; float: left;">
			<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">参照条件</span>
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
					<td class="td-content">
								<input type="text"  name="model_name" id="txt_model_name"alt="型号" class="ui-widget-content" readonly="readonly">
								<input type="hidden" name="model_id" id="search_model_id">
					</td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content">
						<select id="search_rank" name="rank" alt="等级">${lOptions}</select>
					</td>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content">
						<input class="ui-widget-content" type="text" id="search_code" name="code" alt="零件编码"/>
					</td>
				</tr>
			</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="referencebutton" value="参照" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
		</form>
		
	</div>

    <div id="editarea" style="display:none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle"></span>
		</div>
	<div class="ui-widget-content dwidth-middleright">
	
	<!-- 双击之后的页面 -->
		<form id="editform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content"><input name="code" alt="零件编码" id="edit_code" class="ui-widget-content"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件名称</td>
					<td class="td-content"><input name="name" alt="零件名称"id="edit_name" class="ui-widget-content"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">工位</td>
					<td class="td-content"><input name="position"type="text" alt="工位" id="edit_position" /></td>
				</tr>	
				<tr>
					<td class="ui-state-default td-title">有效期</td>
					<td class="td-content"><label alt="有效期"  id="label_period_of_validity" /></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">使用率</td>
					<td class="td-content"><label alt="使用率"  id="label_use" /></td>
				</tr>                
				<tr>
					<td class="ui-state-default td-title">最后更新时间</td>
					<td class="td-content"><label  id="label_update_time"/></td>
				</tr>
			</table>
			
			<div style="height:44px;margin-top:5px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="修改" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
			<div id="confirmmessage"></div>
		</form>
		
	</div>
</div>

<div class="clear areaencloser"></div>
<div  id ="label_model_rank"  class="ui-widget-content dwidth-middleright">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
					<span class="areatitle">零件定位信息一览</span>					
	</div>
		<table class="condform">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="label_model_name"/>
					</td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content">
						<label id="label_rank"/>
					</td>					
				</tr>
		</table>	
</div>
		<!-- JqGrid表格  -->
		<div id="listarea" class="width-middleright">
			<table id="list"></table>
			<div id="listpager"></div>
			<div class="ui-widget-header areabase" style="padding-top:4px; margin-button:6px;margin-bottom: 16px;">
<% if (isOperator) { %>
			      <div id="executes" style="margin-left:4px;margin-top:2px;">
					<input type="button" id="uploadbutton" class="ui-button ui-widget ui-state-default ui-corner-all "  value="上传" role="button">

					<input type="button" id="waste_revision_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="废改订" role="button">
				  </div>
<% } %>
				</div>
		</div>
</div>
<div id="show_detail_message" style="display:none">
   <table class="condform">
                 <tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content"><label name="model_name" id="label_modelname"/></td>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content"><label name="rank" id="label_level"/></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">上级零件</td>
					<td class="td-content"><label name="parent_partial" id="label_parent_partial"/></td>
					<td class="ui-state-default td-title">零件编码</td>
					<td class="td-content"><label name="code" alt="零件编码" id="label_code"/></td>			
				</tr>				
				<tr>
				    <td class="ui-state-default td-title">零件名称</td>
					<td class="td-content"><label name="name" alt="零件名称"id="label_name"/></td>
					<td class="ui-state-default td-title">使用工位</td>
					<td class="td-content"><label name="position" alt="工位"id="label_position"/></td>				
				</tr>			
			</table>
	<table id="ext_list"></table>
	<div id="ext_listpager"></div>
</div>
<div class="referchooser ui-widget-content" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform">${mReferChooser}</table>
</div>
<div class="clear"></div>
				</div>
			</div>
			<div class="clear dwidth-middleright"></div>
		</div>
	

	<div id="footarea"></div>
</div>
</body>
</html>