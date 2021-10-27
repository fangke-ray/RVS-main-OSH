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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/admin/check_file_manage.js"></script>

<title>点检表管理</title>
<script>
	$(function() {
		$("#accordion" ).accordion({active: 1});
	});
</script>
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
					<jsp:param name="linkto" value="设置管理" />
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<!-- 一览开始 -->
				<div id="main">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
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
										<td class="ui-state-default td-title">点检表管理号</td>
										<td class="td-content">
											<input type="text" id="search_check_manage_code" class="ui-widget-content"/>
										</td>
										<td class="ui-state-default td-title">文件名</td>
										<td class="td-content">
											<input type="text" id="search_sheet_file_name" class="ui-widget-content">
										</td>
										<td class="ui-state-default td-title">使用设备工具品名</td>
										<td class="td-content">
											<input type="text" id="search_devices_type_name" class="ui-widget-content">
											<input type="hidden" id="hidden_devices_type_id">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">类型</td>
										<td class="td-content" >
											<select id="search_access_place">${goAccessPlace}</select>
										</td>
										<td class="ui-state-default td-title">归档周期</td>
										<td class="td-content" >
											<select id="search_cycle_type">${goCycleType }</select>
										</td>
										<td class="ui-state-default td-title">归档方式</td>
										<td class="td-content" >
											<select id="search_filing_means">${goCheck_file_filing_means }</select>
										</td>	
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input id="resetbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float:right;right:2px" aria-disabled="false" role="button" value="清除">
								<input id="searchbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  value="检索" role="button" aria-disabled="false" type="button" style="float:right;right:2px">
								<input type="hidden" id="sAccessPlace" value="${sAccessPlace }">
								<input type="hidden" id="sCycleType" value="${sCycleType }">
								<input type="hidden" id="sCheck_file_filing_means" value="${sCheck_file_filing_means }">
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div id="listarea" >
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">点检表管理一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="check_file_manage_list" ></table>
						<div id="check_file_manage_listpager"></div>
					</div>
				</div>
				<!-- 一览结束 -->
				
				<!-- 新建点检表开始 -->
				<div id="add" style="display:none;">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">新建点检表</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					
					<div class="ui-widget-content">	
						<form id="add_form" method="POST">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">点检表管理号</td>
									<td class="td-content">
										<input type="text"  id="add_check_manage_code" name="check_manage_code"  alt="点检表管理号"  class="ui-widget-content" style="width:215px;">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">点检表文件</td>
									<td class="td-content" style="width:446px;">
										<input type="file" id="add_sheet_file_name" name="file" alt="点检表文件"/>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">使用设备工具品名</td>
									<td class="td-content">
										<input type="text" id="add_devices_type_name"  class="ui-widget-content" style="width:215px;" readonly="readonly">
										<input type="hidden" id="hidden_add_devices_type_id" name="devices_type_id" alt="使用设备工具品名"> 
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">特定机型</td>
									<td class="td-content">
										<input type="text" id="add_specified_model_name"  class="ui-widget-content" style="width:215px;">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">类型</td>
									<td class="td-content">
										<select id="add_access_place" name="access_place" alt="类型">${goAccessPlace}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">归档周期</td>
									<td class="td-content">
										<select id="add_cycle_type" name="cycle_type" alt="归档周期">${goCycleType }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">归档方式</td>
									<td class="td-content">
										<select id="add_filing_means" name="filing_means" alt="归档方式">${goCheck_file_filing_means }</select>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirmbutton" value="确认" role="button" aria-disabled="false" style="float:left;left:4px;">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
							</div>
						</form>
					</div>
				</div>
				<!-- 新建点检表结束 -->
				
				<!-- 修改点检表开始 -->
				<div id="update" style="display:none">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">修改点检表</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content">	
						<form id="update_form" method="POST">
							<table class="condform">
								<tr>
									<td class="ui-state-default td-title">点检表管理号</td>
									<td class="td-content">
										<input type="text"  id="update_check_manage_code" name="check_manage_code"  alt="点检表管理号"  class="ui-widget-content" style="width:215px;">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">点检表文件</td>
									<td class="td-content" style="width:446px;">
										<input type="text"  id="show_sheet_file_name" class="ui-widget-content" style="width:215px;">
										<input type="file" id="update_sheet_file_name" name="file"/>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">使用设备工具品名</td>
									<td class="td-content">
										<input type="text" id="update_devices_type_name"  class="ui-widget-content" style="width:215px;" readonly="readonly">
										<input type="hidden" id="hidden_update_devices_type_id" name="devices_type_id" alt="使用设备工具品名"> 
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">特定机型</td>
									<td class="td-content">
										<input type="text" id="update_specified_model_name"  class="ui-widget-content" style="width:215px;">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">类型</td>
									<td class="td-content">
										<select id="update_access_place">${updateAccessPlace}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">归档周期</td>
									<td class="td-content" >
										<select id="update_cycle_type" name="cycle_type"  alt="归档周期" >${goCycleType }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">归档方式</td>
									<td class="td-content">
										<select id="update_filing_means" name="filing_means" alt="归档方式">${updateCheck_file_filing_means }</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">最后更新人</td>
									<td class="td-content">
										<label id="label_updated_by_name"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">最后更新时间</td>
									<td class="td-content">
										<label id="label_updated_time"></label>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input id="updatebutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button"  value="修改" style="float:left;left:2px" aria-disabled="false" role="button" >
								<input id="cancelbutton2" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  type="button" value="取消" style="float:left;left:2px" aria-disabled="false"   role="button" >
							</div>
						</form>
					</div>
				</div>
				<!-- 修改点检表结束 -->
				
				<div id="name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform">${nameReferChooser }</table>
				</div>
				
				
				
			</div>
			
			<div class="clear"></div>
			<div id="confirmmessage"></div>
		</div>
		
	</div>
</body>
</html>