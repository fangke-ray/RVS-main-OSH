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
<script type="text/javascript" src="js/partial/partial_supply.js"></script>

<title>零件补充</title>
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
					<jsp:param name="linkto" value="零件订购/签收"/>
				</jsp:include>
			</div>

				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;float:left;">
<div id="body-top">
		<div id="uploadarea">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">零件补充文件导入</span>
			</div>

			<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content"><input type="file" name="file" alt="上传文件路径" id="supply_file" class="ui-widget-content" /></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploadbutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
			</div>
		   <div class="clear areaencloser"></div>
        </div>

		<div id="searcharea">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">零件补充记录查询编辑</span>
			</div>

			<div class="ui-widget-content dwidth-middleright">
				<form id="searchform" method="POST">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">补充订购日期</td>
								<td class="td-content"><input type="text" name="supply_date" alt="补充订购日期" id="supply_date" class="ui-widget-content" /></td>
							</tr>
						</tbody>
					</table>
					<div style="height: 44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="select_button" value="查询" role="button" aria-disabled="false" style="float: right; right: 2px">
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
	   <div id="edit_partial"></div>

</div>

<div id="confirm_message" style="display:none;">零件补充记录已导入！</div>
	<div id="footarea"></div>

	</div>
	<div class="clear"></div>
</div>
</body>
</html>