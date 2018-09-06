<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/qf/setInlineStatus.js"></script>

<title>SORC在线状态导入</title>
</head>
<body class="outer" style="align: center;">

<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="acceptance.do"/>
				</jsp:include>
			</div>
			<div id="body-mdl" class="width-middle" style="float: left;">
			<div id="searcharea">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
					<span class="areatitle">SORC在线状态导入</span>
				</div>
				<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content"><input type="file" name="file" id="file" class="ui-widget-content" /></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploadbutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
				</div>
				<div class="clear dwidth-middleright"></div>
			</div>
			
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
			<div id="confirmmessage" style="display:none;">
				<div>发往 OGZ： <label></label> 台</div>
				<div>OGZ 出货： <label></label> 台</div>
				<div>OGZ WIP 库存： <label></label> 台</div>
				<div>OGZ 投线： <label></label> 台</div>
				<div>OGZ 修理同意： <label></label> 台</div>
			</div>
		</div>
</div>

</body>
</html>