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
<script type="text/javascript" src="js/jquery.mtz.monthpicker.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>

<script type="text/javascript" src="js/infect/check_result_filing.js"></script>

<title>点检结果归档</title>
<script>
	$(function() {
		$("#accordion" ).accordion({active: 1});
	});
</script>
</head>
<%
	String privacy = (String) request.getAttribute("privacyTechnology");
	boolean isPrivacyTechnology = ("privacy_technology").equals(privacy);
%>
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
					<jsp:param name="linkto" value="设备工具/治具点检" />
				</jsp:include>
			</div>
			
			<div style="width: 994px; float: left;">
		<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">检索条件</span>
			<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>	
		
		<div id="search_item" class="ui-widget-content dwidth-middleright">	
		<!-- 检索条件 -->
			<form id="searchform" method="POST" onsubmit="return false;">
				<table class="condform">
					<tr>
						<td class="ui-state-default td-title">点检表管理号</td>
						<td class="td-content"><input type="text" name="name" alt="点检表管理号" id="search_daily_sheet_manage_no" class="ui-widget-content"/></td>
						<td class="ui-state-default td-title" style="width: 111px;">文件名</td>
						<td class="td-content"><input type="text" name="daily_sheet_file" alt="文件名" id="search_daily_sheet_file" maxlength="120" class="ui-widget-content"></td>
						<td class="ui-state-default td-title">类型</td>
						<td class="td-content">
							<select id="search_access_place">${goAccessPlace}</select>
						</td>					
					</tr>
					<tr>
					    <td class="ui-state-default td-title"  style="width: 111px;">使用设备工具品名</td>
						<td class="td-content"><input type="text" name="name" readonly="readonly" alt="使用设备工具品名" id="search_name" maxlength="120" class="ui-widget-content">
						<input type="hidden" id="hidden_devices_type_id"/>
						</td>
						<td class="ui-state-default td-title">归档周期</td>
						<td class="td-content">
							<select id="search_cycle_type">${goCycleType }</select>
						</td>	
					</tr>
				</table>
				<div style="height:44px">
					<input id="resetbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float:right;right:2px" aria-disabled="false" role="button" value="清除">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				    <%
				       if(isPrivacyTechnology){
				    %>
				        <input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="upload_branch_button" value="上传附表" role="button" aria-disabled="false" style="float:right;right:4px">
				    <%
				       }
				    %>
				    <!-- 类型和归档周期  -->
				    <input type="hidden" id="sAccessPlace" value="${sAccessPlace }">
					<input type="hidden" id="sCycleType" value="${sCycleType }">
				</div>
			</form>		
		</div>    

		<div id="editarea" style="display:none;">
		<div class="dwidth-middleright">	
			<div id="detailsearcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">检索条件</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			
			<div id="detail_search_item" class="ui-widget-content dwidth-middleright">	
			<!-- 检索条件 -->
				<form id="detailsearchform" method="POST" onsubmit="return false;">
					<table class="condform">
						<tr>
							<td class="ui-state-default td-title">管理编号</td>
							<td class="td-content">
								<input type="text" name="devices_manage_id" readonly="readonly" alt="管理编号" id="search_devices_manage_id" class="ui-widget-content"/>
								<input type="hidden" id="hidden_detail_devices_manage_id"/>
							</td>
							<td class="ui-state-default td-title">文件从属</td>
							<td class="td-content"><select id="search_branch">${goBranch}</select></td>	
							<td class="ui-state-default td-title">归档周期</td>
							<td class="td-content">
								<input type="text" class="ui-widget-content" id="search_filing_date_start" readonly="readonly">起<br>
								<input type="text" class="ui-widget-content" id="search_filing_date_end" readonly="readonly">止
							</td>
						</tr>
					</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="detailresetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="detailsearchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
				</form>		
			</div>

			<div class="clear areaencloser"></div>

			<!-- 点检归档文件一览 -->
			<div id="detail">		    
				<table id="detail_list"></table>
				<div id="detail_list_listpager"></div>
			</div>
			
			<!-- 文件从属 -->
			<input type="hidden" id="h_branch" value="${sBranch }">
			
			<div style="height:44px;padding-top:5px;"  class="ui-widget-content">				
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="come_back_button" value="返回" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>	
			<div class="clear"></div>		
		</div>
		</div>
		
		<!--上传附表 -->
		<div id="upload_schedule" class="ui-widget-content" style="display:none;">
			<form id="uploadform" method="POST" onsubmit="return false;">
				<table class="condform">
					<tr>
						<td class="ui-state-default td-title">点检表名称</td>
						<td class="td-content">
							<input type="text" name="sheet_file_name" readonly="readonly" alt="点检表名称" id="upload_sheet_file_name" class="ui-widget-content"/>
							<input type="hidden" id="hidden_sheet_file_name"/>
							</td>
					</tr>
					<tr>
					    <td class="ui-state-default td-title"  style="width: 111px;">点检表归档日期</td>
						<td class="td-content"><input type="text" name="filing_date" readonly="readonly" alt="点检表归档日期" id="upload_filing_date" maxlength="120" class="ui-widget-content">
					</tr>
					<tr>
					    <td class="ui-state-default td-title"  style="width: 111px;">开始记录日期</td>
						<td class="td-content"><input type="text" name="start_record_date" readonly="readonly" alt="开始记录日期" id="upload_start_record_date" maxlength="120" class="ui-widget-content">
					</tr>
					<tr>
					    <td class="ui-state-default td-title"  style="width: 111px;">设备名称</td>
						<td class="td-content">
							<input type="text" name="device_name" readonly="readonly" alt="设备名称" id="upload_device_name" class="ui-widget-content">
							<input type="hidden" id="hidden_device_name"/>
						</td>
					</tr>
					<tr>
					    <td class="ui-state-default td-title"  style="width: 111px;">上传文件</td>
						<td class="td-content"><input type="file" name="file"  alt="上传文件" id="upload_schedule_file" class="ui-widget-content"></td>
					</tr>
				</table>
			</form>	
			
			<!-- 上传附件 点检表名称 -->
			<div id="sheet_file_name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:20000">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table  class="subform">${sfnReferChooser}</table>
			</div>	
			
			<!-- 上传附件 设备名称 -->
			<div id="device_name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:20000">
				<table width="200px">
					<tr>
						<td></td>
						<td width="50%">过滤字:<input type="text"/></td>
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table  class="subform">${dnReferChooser}</table>
			</div>		
		</div>

		<div class="clear areaencloser"></div>
			<!--点检结果归档一览-->
			<div id="listarea" class="width-middleright">
				<table id="list"></table>
				<div id="list_pager"></div>			
			</div>
	
			<div id="confirmmessage"></div>
		</div>
		<div class="clear areaencloser"></div>
		<!-- 一览结束 -->
				
		<div id="name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${nReferChooser}</table>
		</div>	

		<div id="detail_device_name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:20000">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${dnReferChooser}</table>
		</div>
		</div>
					
		<div class="clear"></div>
		<div id="confirmmessage"></div>
		</div>		
	</div>
</body>
</html>