<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
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
	<script type="text/javascript" src="js/ajaxfileupload.js"></script>
	<script type="text/javascript" src="js/utils.js"></script>
	<script type="text/javascript" src="js/jquery-plus.js"></script>
	<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
	<script type="text/javascript" src="js/partial/partial_warehouse.es5.js"></script>
	<title>零件入库单管理</title>
	
	<style type="text/css">
		.align-center{
			text-align: center;
		}
	</style>
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
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件入出库"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="search">
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
										<td class="ui-state-default td-title">DN 编号</td>							
										<td class="td-content">
											<input id="search_dn_no" type="text" class="ui-widget-content">
										</td>													
										<td class="ui-state-default td-title">入库单日期</td>
										<td class="td-content">
											<input type="text" class="ui-widget-content" id="search_warehouse_date_start" readonly="readonly">起<br>
											<input type="text" class="ui-widget-content" id="search_warehouse_date_end" readonly="readonly">止
										</td>
										<td class="ui-state-default td-title">完成上架日期</td>
										<td class="td-content">
											<input type="text" class="ui-widget-content" id="search_finish_date_start" readonly="readonly">起<br>
											<input type="text" class="ui-widget-content" id="search_finish_date_end" readonly="readonly">止
										</td>
								   </tr>
								   <tr>
									   <td class="ui-state-default td-title">入库进展</td>
									   <td class="td-content">
											<select id="search_step">${sStep }</select>
										</td>
										<td class="ui-state-default td-title"></td>
										<td class="td-content"></td>
										<td class="ui-state-default td-title"></td>
										<td class="td-content"></td>
								   </tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px">
								<input type="button" class="ui-button" id="searchbutton" value="检索" style="float:right;right:2px">					
								<input type="hidden" value="${goStep}" id="goStep">
							</div>
						</form>	
					</div>
					
					<div class="areaencloser"></div>
					
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件入库单一览</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
				
					<table id="list"></table>
					<div id="listpager"></div>
					
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
						<div style="margin-left:4px;margin-top:6px;">
							<input type="button" id="importButton" class="ui-button" value="上传" style="margin-right: 2px;"/>
							<input type="button" id="collationOnShelfButton" class="ui-button" value="核对/上架" style="margin-right: 2px;"/>
							<input type="button" id="unpackButton" class="ui-button" value="分装" style="margin-right: 2px;"/>
						</div>
					</div>
				</div>
				
				<div id="detail" style="display: none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件入库单明细一览</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
				
					<table id="detaillist"></table>
					<div id="detaillistpager"></div>
					
					<div class="ui-widget-content" style="padding:6px;">
						<input type="button" class="ui-button" id="gobackbutton" value="返回" style="float:right;right:2px">
						<div class="clear"></div>
					</div>
				</div>
				
				<!--作业区域 -->
				<div id="workarea" style="display: none;">
				
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">作业信息</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>

					<div class="ui-widget-content">
						<table class="condform" style="width:99.6%;" id="content">
							<tbody>
								<tr>
									<td class="ui-state-default td-title" colspan="2">入库单日期</td>
									<td class="ui-state-default td-title" colspan="2">DN 编号</td>
								</tr>
								<tr style="height: 30px;">
									<td class="td-content" colspan="2"></td>
									<td class="td-content" colspan="2"></td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button" id="confirmButton" value="确定" style="float:right;right:2px;">
							<div class="clear"></div>
						</div>
					</div>
					
					<div class="areaencloser"></div>
				
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle"><span id="modelName"></span>一览</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					
					<table id="worklist"></table>
					<div id="worklistpager"></div>
				</div>
			</div>
			
			<div class="clear"></div>
			
			<div style="display: none" id="file_upload">
				<div class="ui-widget-content">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content">
										<input type="file" name="file" id="file" class="ui-widget-content"/>
									</td>
								</tr>
							</tbody>
						</table>
					</form>
				</div>
			</div>
			
			<div style="display: none;" id="partial_warehouse_dialog"></div>
			
		</div>
	</div>
</body>
</html>