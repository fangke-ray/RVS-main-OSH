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
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/data/list_messages.js"></script>
<title>警报信息查询</title>
</head>
<body class="outer" style="align: center;">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
<div id="basearea" class="dwidth-full" style="margin: auto;">
	<jsp:include page="/header.do" flush="true">
		<jsp:param name="part" value="2"/>
	</jsp:include>
</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
	<div id="body-lft" style="width:256px;float:left;">
		<jsp:include page="/appmenu.do" flush="true">
			<jsp:param name="linkto" value="进度查询"/>
		</jsp:include>
	</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
	<div style="margin:auto;">
	
<div id="searcharea" class="dwidth-middleright">
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
					<td class="ui-state-default td-title">警报等级</td>
					<td class="td-content"><select name="level" id="cond_level" class="ui-widget-content">${lvlOptions}</select></td>
					<td class="ui-state-default td-title">原因</td>
					<td class="td-content"><select name="reason" id="cond_reason" class="ui-widget-content">${rOptions}</select></td>
					<td class="ui-state-default td-title">发生时间</td>
					<td class="td-content">
						<input type="text" name="occur_time_from" id="cond_occur_time_from" class="ui-widget-content" value="${today}">起<br/>
						<input type="text" name="occur_time_to" id="cond_occur_time_to" class="ui-widget-content">止
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><input type="text" name="sorc_no" id="cond_sorc_no" maxlength="15" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content"><input type="text" readonly class="ui-widget-content"><input type="hidden" name="model_id" id="cond_model_id"/></td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><input type="text" name="serial_no" id="cond_serial_no" maxlength="12" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">课室</td>
					<td class="td-content"><select name="section_id" id="cond_section_id" class="ui-widget-content">${sOptions}</select></td>
					<td class="ui-state-default td-title">工程</td>
					<td class="td-content"><select name="line_id" id="cond_line_id" class="ui-widget-content">${lOptions}</select></td>
					<td class="ui-state-default td-title">接受信息者</td>
					<td class="td-content"><input type="text" readonly class="ui-widget-content"><input type="hidden" name="reciever_id" id="cond_reciever_id"></td>
				</tr>
			</tbody>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
			</div>
			</form>
	</div>
	<div class="clear areaencloser"></div>
</div>


<div id="listarea" class="dwidth-middleright">
	<table id="list"></table>
	<div id="listpager"></div>
</div>

<div id="detail_dialog">
</div>

<div class="clear areacloser"></div>
</div>
				</div>
			</div>

			<div class="clear areaencloser dwidth-middleright"></div>
		</div>
	</div>
<div class="referchooser ui-widget-content" tabindex="-1" id="referchooser_model">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text" tabindex="-1"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=request.getAttribute("mReferChooser")%></table>
</div>
<div class="referchooser ui-widget-content" tabindex="-1" id="referchooser_operator">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text" tabindex="-1"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=request.getAttribute("oReferChooser")%></table>
</div>
</body>
</html>