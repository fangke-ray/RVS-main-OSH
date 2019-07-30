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
<script type="text/javascript" src="js/partial/partial_storage.js"></script>

<title>零件库存</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件入出库"/>
				</jsp:include>
			</div>

			<div id="body-mdl" class="dwidth-middleright" style="margin: auto;float:left;">
				<div id="body-top">
					<div id="uploadarea">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">零件库存文件导入</span>
						</div>
			
						<div class="ui-widget-content dwidth-middleright">
								<form id="uploadform" method="POST">
									<table class="condform">
										<tr>
											<td class="ui-state-default td-title">上传文件</td>
											<td class="td-content"><input type="file" name="file" alt="上传文件路径" id="upload_file" class="ui-widget-content" /></td>
											<td class="ui-state-default td-title">库存清点日期</td>
											<td class="td-content"><input type="text" name="upload_storage_date" alt="库存清点日期" id="upload_storage_date" class="ui-widget-content" readonly/></td>
											<td class="ui-state-default td-title">库存分类</td>
											<td class="td-content">
												<select name="upload_identification" alt="库存分类" id="upload_identification" class="ui-widget-content">
													<option value="">
													</option><option value="1">OSH</option>
													<option value="2">OGZ</option>
												</select>
											</td>
										</tr>
									</table>
									<div style="height: 44px">
										<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float: right; right: 2px">
										<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploadbutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
									</div>
								</form>
						</div>
					   <div class="clear areaencloser"></div>
			        </div>
				
					<div id="searcharea">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">零件库存记录查询</span>
						</div>
			
						<div class="ui-widget-content dwidth-middleright">
							<form id="searchform" method="POST">
								<table class="condform">
									<tr>
										<td class="ui-state-default td-title">库存清点日期</td>
										<td class="td-content"><input type="text" name="search_storage_date" alt="库存清点日期" id="search_storage_date" class="ui-widget-content" readonly/></td>
									</tr>
								</table>
								<div style="height: 44px">
									<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="search_button" value="查询" role="button" aria-disabled="false" style="float: right; right: 2px">
								</div>
							</form>
						</div>
			        </div>
				 
					<div class="clear areaencloser"></div>
					<!-- JqGrid表格  -->
					<div id="listarea" class="width-middleright">
						<table id="list"></table>
						<div id="listpager"></div>
					</div>
					<div id="functionarea" style="margin:auto;height:44px;margin-bottom:16px;">
						<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
							<div id="executes" style="margin-left:4px;margin-top:4px;">
								<input type="button" id="delete_button" class="ui-button ui-widget ui-state-default ui-corner-all" value="删除"/>
							</div>
						</div>
						<div class="clear"></div>
				   </div>

					<div id="editarea" style="display:none;">
				   		<div class="ui-widget-content dwidth-middleright">
							<form id="editform" >
								<table class="condform">
									<tr>
										<td class="ui-state-default td-title">库存分类</td>
										<td class="td-content">
											<label id="edit_identification" name="edit_identification" alt="库存分类" ></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">零件号</td>
										<td class="td-content">
											<label id="edit_code" name="edit_code" alt="零件号" ></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">零件说明</td>
										<td class="td-content">
											<label id="edit_partial_name" name="edit_partial_name" alt="零件说明" ></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">数量</td>
										<td class="td-content">
											<input name="edit_quantity" alt="数量"  id="edit_quantity" type="text" class="ui-widget-content"></input>
										</td>
									</tr>
								</table>
							</form>
						</div>
					</div>
				</div>

				<div id="confirm_message" style="display:none;"></div>
			</div>
		<div class="clear"></div>
	</div>
</body>
</html>