<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<style>
#supportarea .ui-button-text-only .ui-button-text {
	padding: .4em 1em !important;
}

#supportarea .ui-buttonset .ui-button {
	margin-right: 2px;
	margin-bottom: 8px;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/inline/support.js"></script>

<title>辅助工作</title>
</head>
<body class="outer">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel width-full" style="align: center; padding-top: 16px;" id="body-pos">

			<div class="dwidth-full">
				<div id="supportarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
						<span class="areatitle">辅助作业信息</span>
					</div>

					<div class="ui-widget-content" id="scanner_container" style="padding: 8px;">
						<div class="ui-state-default" style="padding: 8px;">要辅助的课室</div>
						<div class="ui-widget-content" style="padding: 8px; padding-bottom: 0;">
							<span id="sections"> 您现在在：<label></label> 如要辅助其他课室的作业，请在首页切换。</span>
						</div>
						<div class="ui-state-default td-title" style="padding: 8px;">选择要辅助的工位</div>
						<div class="ui-widget-content" style="padding: 8px; padding-bottom: 0; min-height:36px;">
							<span id="positions"></span>
						</div>
						<div class="ui-state-default td-title" style="padding: 8px;">选择要辅助的操作者</div>
						<div class="ui-widget-content" style="padding: 8px; padding-bottom: 0; min-height:36px;">
							<span id="persons"></span>
						</div>
						<div style="height: 44px;">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" onclick="javascript:doStart();" id="supportbutton" value="辅助" role="button" aria-disabled="false" style="float: right; right: 2px; top: 8px;">
						</div>
					</div>
					<div class="ui-widget-content dwidth-full" id="material_details">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content-text"></td>
									<td class="ui-state-default td-title">工程</td>
									<td class="td-content-text"></td>
									<td class="ui-state-default td-title">工位</td>
									<td class="td-content-text"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">辅助开始时间</td>
									<td class="td-content-text"></td>
									<td class="ui-state-default td-title">辅助进行时间</td>
									<td class="td-content-text">00:03</td>
									<td class="ui-state-default td-title">主作业者</td>
									<td class="td-content-text"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content-text"></td>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content-text"></td>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content-text"></td>
								</tr>
								<tr>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="finishbutton" value="辅助结束" role="button" aria-disabled="false" style="float: right; right: 2px;">
							<!--input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="pausebutton" value="暂停" role="button" aria-disabled="false" style="float: right; right: 2px;">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="continuebutton" value="重开" role="button" aria-disabled="false" style="float: right; right: 2px;"-->
						</div>
					</div>

				</div>

				<div class="clear areaencloser"></div>
			</div>

			<div id="manualdetailarea" style="margin-bottom: 16px;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">工程检查票</span>
				</div>
				<div class="ui-widget-content dwidth-full" id="ns_pcs">
					<div id="pcs_pages">
					</div>
					<div id="pcs_contents">
					</div>
				</div>
				<div class="ui-state-default ui-corner-bottom areaencloser dwidth-full"></div>
			</div>

		</div>
		<div id="break_dialog"></div>
	</div>


</body>
</html>