<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<style>
 #add_temperature_lower_limit,#add_temperature_upper_limit,#add_seq,#edit_temperature_lower_limit,#edit_temperature_upper_limit{
    ime-mode: disabled;
 }
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/infect/electric_iron_device.js"></script>
<title>电烙铁工具管理</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具信息管理" />
				</jsp:include>
			</div>
			
		    <!--电烙铁工具管理画面开始-->
			<div id="body_mdl">
				<div style="width: 994px; float: left;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">电烙铁工具管理画面</span>
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
										   <input type="text" readonly="readonly" name="manage_code" id="search_manage_code" alt="管理号码" class="ui-widget-content"/>
										   <input type="hidden" id="hidden_search_devices_manage_id"/>
										</td>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
										   <input type="text" readonly="readonly"  name="device_name" id="search_device_name" alt="品名" class="ui-widget-content"/>
										   <input type="hidden" id="hidden_search_devices_type_id"/>
										</td>
										<td class="ui-state-default td-title">种类</td>
										<td class="td-content">
										   <select name="kind" id="search_kind" alt="种类 ">${kOptions}</select>
										</td>
									</tr>
									<tr>
									    <td class="ui-state-default td-title">所在课室</td>
										<td class="td-content">
										   <select name="section_id" id="search_section_id" alt="所在课室">${sectionOptions}</select>
										</td>
										 <td class="ui-state-default td-title">所在工位</td>
										<td class="td-content">
										   <input type="text" name="position_id" id="search_position_id" alt="所在工位 "/>
										   <input type="hidden" id="hidden_search_position_id"/>
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
								<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
							</div>
						</form>
					</div>
				
					<div class="clear areaencloser"></div>
				
					<div id="listarea" class="">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">电烙铁工具管理一览</span>
							<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="electric_iron_device_list"></table>
						<div id="electric_iron_device_listpager"></div>
						<input type="hidden" id="hidden_kGridOptions" value="${kGridOptions}"/>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			
			<!-- 新建电烙铁工具 -->
			<div id="body_add" style="width: 994px; float: left;display:none" class="ui-widget-content">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">新建电烙铁工具</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<form id="add_form" method="POST">	   
				    <div>
					<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
						<tbody>	
						    <tr>
								<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
								<td class="td-content">
									<input type="text" readonly="readonly" id="add_manage_code" name="manage_id" alt="管理编号"  class="ui-widget-content">
									<input type="hidden" id="hidden_add_devices_manage_id"/>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">温度点检序号</td>
								<td class="td-content">
									<input type="text" id="add_seq" name="seq" alt="温度点检序号" class="ui-widget-content">
								</td>
							</tr>
							<tr>
							    <td class="ui-state-default td-title">温度下限</td>
								<td class="td-content">
									<input type="text" id="add_temperature_lower_limit" name="temperature_lower_limit" alt="温度下限" class="ui-widget-content">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">温度上限</td>
								<td class="td-content">
									<input type="text" id="add_temperature_upper_limit" name="temperature_upper_limit" alt="温度上限"class="ui-widget-content">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">种类</td>
								<td class="td-content">
									<select id="add_kind" name="kind" alt="种类">${kOptions}</select>
								</td>
							</tr>		
							<%-- <tr>
							    <td class="ui-state-default td-title">所在课室</td>
								<td class="td-content">
									<select id="add_section_id" name="section_id" alt="所在课室">${sectionOptions}</select>
								</td>
								<td class="ui-state-default td-title">所在工位</td>
								<td class="td-content">
									<input type="text" readonly="readonly" id="add_position_id" name="position_id" alt="所在工位"class="ui-widget-content">
									<input type="hidden" id="hidden_add_position_id"/>
								</td>
							</tr>	 --%>
						</tbody>
					</table>
				    </div>	
					
					<div style="height:44px">
						<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addConfirebutton" value="确认" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
						<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addGoback" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
					</div>
				</form>
			</div>
			<!--新建电烙铁工具结束-->
			
			<!--修改电烙铁工具 -->
			<div id="body_edit" style="width: 994px; float: left;display:none" class="ui-widget-content">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">修改电烙铁工具</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<form id="edit_form" method="POST">	   
				    <div>
					<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
						<tbody>	
						    <tr>
								<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
								<td class="td-content">
									<label id="label_manage_code"></label>
									<input type="hidden" id="hidden_label_manage_id"/>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">品名</td>
								<td class="td-content">
									<label id="label_device_name"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">温度点检序号</td>
								<td class="td-content">
									<label  id="label_seq"></label>
									<input type="hidden" id="hidden_label_seq"/>
								</td>
							</tr>
							<tr>
							 	<td class="ui-state-default td-title">温度下限</td>
								<td class="td-content">
									<input type="text" id="edit_temperature_lower_limit" name="temperature_lower_limit" alt="温度下限" class="ui-widget-content">
								</td>
							</tr>	
							<tr>
								<td class="ui-state-default td-title">温度上限</td>
								<td class="td-content">
									<input type="text" id="edit_temperature_upper_limit" name="temperature_upper_limit" alt="温度上限"class="ui-widget-content">
								</td>
							</tr>
							<tr>
							    <td class="ui-state-default td-title">种类</td>
								<td class="td-content">
									<select id="edit_kind" name="kind" alt="种类">${kEOptions}</select>
								</td>
							</tr>	
							<tr>
							    <td class="ui-state-default td-title">所在课室</td>
								<td class="td-content">
									<label id="label_section_id"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">所在工位</td>
								<td class="td-content">
									<label id="label_position_id"></label>
								</td>
							</tr>
						</tbody>
					</table>
				    </div>	
					
					<div style="height:44px">
						<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editConfirebutton" value="确认" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
						<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editGoback" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
					</div>
				</form>
			</div>
			<!--修改电烙铁工具结束-->

			<div id="confirmmessage"></div>
			<div class="clear"></div>
		</div>
		
		<!-- 检索管理编号referchooser -->
		<div class="referchooser ui-widget-content" id="search_manage_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${mcReferChooser}</table>
		</div>
		
		<!-- 新建管理编号referchooser -->
		<div class="referchooser ui-widget-content" id="add_manage_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${mcReferChooser}</table>
		</div>
		
		<!-- 修改管理编号referchooser -->
		<div class="referchooser ui-widget-content" id="edit_manage_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${mcReferChooser}</table>
		</div>
		
		<!--检索品名referchooser -->
		<div class="referchooser ui-widget-content" id="search_device_name_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${dnReferChooser}</table>
		</div>
		
		<!--新建品名referchooser -->
		<div class="referchooser ui-widget-content" id="add_device_name_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${dnReferChooser}</table>
		</div>
		
		<!--修改品名referchooser -->
		<div class="referchooser ui-widget-content" id="edit_device_name_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${dnReferChooser}</table>
		</div>
		
		<!-- 检索所在工位referchooser -->
		<div class="referchooser ui-widget-content" id="search_position_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${pReferChooser}</table>
		</div>
		
		<!--新建所在工位referchooser -->
		<div class="referchooser ui-widget-content" id="add_position_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${pReferChooser}</table>
		</div>
		
		<!-- 修改所在工位referchooser -->
		<div class="referchooser ui-widget-content" id="edit_position_code_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${pReferChooser}</table>
		</div>
	</div>
</body>
</html>