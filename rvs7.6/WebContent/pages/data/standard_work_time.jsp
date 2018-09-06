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
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/data/standard_work_time.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<title>标准工时</title>
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
					<jsp:param name="linkto" value="资源功能"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 994px; float: left;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   <span class="areatitle">参考型号与等级选择</span>
				     <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					 <span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			<div class="ui-widget-content">
				<form id="searchform" method="POST">
					<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title" rowspan="3">型号</td>							
								<td class="td-content">
								<input type="text" id="model_text"class="ui-widget-content" readonly="readonly">
								<input type="hidden" name="model_name" id="reference_model_id">
							</td>													
							<td class="ui-state-default td-title">等级</td>
							 	<td class="td-content"><select id="reference_rank_id" class="ui-widget-content">${sMaterial_level_inline}</select>
					</td>						
					   </tr>
					</tbody>
					</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="referencebutton" value="参考" role="button" aria-disabled="false" style="float:right;right:2px">					
					</div>
				</form>	
						
			</div>
			
			
			<div id="result_area" style="display:none;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-top:36px;">
				<span class="areatitle">标准工时一览</span>
			</div>
			<div class="ui-widget-content">				
					<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title" rowspan="3">型号</td>							
								<td class="td-content">
								<label id="label_model_id"></label>
							    </td>													
							<td class="ui-state-default td-title">等级</td>
							 	<td class="td-content"><label id="label_rank_id"></label></td>						
					   </tr>
					</tbody>
					</table>					
			</div>
			<div id="edit_value" class=" ui-jqgrid ui-widget-content" style="margin-top:1px;">
				<table class="" style="width:988px;" role="grid" aria-labelledby="gbox_exd_list" cellspacing="0" cellpadding="0" border="0">
					<thead>
						<tr class="ui-jqgrid-labels" role="rowheader">
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 16px;">
								<div>序号</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>工位代码</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>工位名称</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>标准工时</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 16px;">
								<div>序号</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>工位代码</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>工位名</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div>标准工时</div>
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>			
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