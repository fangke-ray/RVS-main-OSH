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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript">
	$(function() {
		$("#man_list a").attr("target", "_blank");
	})
</script>

<title>作业手顺书</title>
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
					<jsp:param name="linkto" value="文档管理"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" id="searcharea">
					<span class="areatitle">检索条件</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<div class="ui-widget-content" style="height:1em;">
				</div>
				<div class="clear areaencloser"></div>

				<div id="man_listarea" class="width-middleright">
					<table id="man_list" class="ui-jqgrid-btable" style="width: 100%;font-size:20px;">
						<tbody>
							<tr class="jqgfirstrow" role="row" style="height:auto">
								<td role="gridcell">
								</td>
							</tr>
							<tr role="row" id="1" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr">
								<td role="gridcell" style="text-align:left;">
									<a target="_blank" href="/docs/manual/E204解析要领书.pdf" class="ui-button">E204解析要领书.pdf</a>
								</td>
							</tr>
							<tr role="row" id="2" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr">
								<td role="gridcell" style="text-align:left;">
									<a target="_blank" href="/docs/manual/开关不良解析要领书.pdf" class="ui-button">开关不良解析要领书.pdf</a>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<div class="clear dwidth-middle"></div>
			</div>
		</div>
		<div class="clear"></div>
	</div>

</body>
</html>