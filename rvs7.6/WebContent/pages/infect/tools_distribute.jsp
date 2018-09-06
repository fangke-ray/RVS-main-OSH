<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
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
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/infect/tools_distribute.js"></script>
	<style>
	.menulink {
		font-size: 16px;
		color: white;
		float: right;
		right: 4px;
		margin: 4px;
	}

	.menulink:hover {
		color: #FFB300;
		cursor: pointer;
	}

	.littleball {
		font-size: 10px;
		-moz-border-radius-topleft: 8px;
		-webkit-border-top-left-radius: 8px;
		-khtml-border-top-left-radius: 8px;
		border-top-left-radius: 8px;
		-moz-border-radius-bottomleft: 8px;
		-webkit-border-bottom-left-radius: 8px;
		-khtml-border-bottom-left-radius: 8px;
		border-bottom-left-radius: 8px;
		-moz-border-radius-topright: 8px;
		-webkit-border-top-right-radius: 8px;
		-khtml-border-top-right-radius: 8px;
		border-top-right-radius: 8px;
		-moz-border-radius-bottomright: 8px;
		-webkit-border-bottom-right-radius: 8px;
		-khtml-border-bottom-right-radius: 8px;
		border-bottom-right-radius: 8px; //
		width: 18px; //
		height: 18px;
		text-align: center; //
		background-color: green;
		padding: 1px;
	}
	#accordion a.processing:before {
		color:red;
	}

	.referchooser table tr td:first-child{
		display:none;
	}
	</style>
<title>治具分布</title>
</head>
<body class="outer" style="overflow: auto;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具信息管理"/>
				</jsp:include>
			</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<div id="body-mdl" style="width: 994px; float: left;">
			<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">检索条件</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
	        </div>
	        
	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">管理编号</td>
						<td class="td-content">
							<input id="search_manage_code" maxlength="14" class="ui-widget-content" type="text">
						</td>
						<td class="ui-state-default td-title">治具No.</td>
						<td class="td-content">
							<input id="search_tools_no" name="tools_no" class="ui-widget-content" type="text">
						</td>
						<td class="ui-state-default td-title">治具品名</td>
						<td class="td-content">
							<input id="search_tools_name" name="tools_name" readonly="readonly" class="ui-widget-content" type="text">
						    <input id="hidden_search_tools_name" name="tools_name"  type="hidden">
						</td>
					</tr>
					<tr>					    
						<td class="ui-state-default td-title">分发课室</td>
						<td class="td-content">
							<select id="search_section_name" name="section_id">${sectionOptions}</select>
						</td>
						<td class="ui-state-default td-title">责任工程</td>
						<td class="td-content">
							<select id="search_line_name" name="line_id">${lineOptions}</select>
						</td>
						<td class="ui-state-default td-title">责任工位</td>
						<td class="td-content">
							<input type="text" id="search_responsible_position_name" name="position_id"  readonly="readonly" class="ui-widget-content">
							<input id="hidden_search_responsible_position_name" name="position_id"  type="hidden">
						</td>
					</tr>
					<tr>					
						<td class="ui-state-default td-title">责任人员</td>
						<td class="td-content">
							<input type="text" id="search_responsible_operator_name" name="responsible_operator_id" maxlength="14" class="ui-widget-content">
							<input id="hidden_search_responsible_operator_name" name="responsible_operator_id"  type="hidden">
						</td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
					</tr>
				</tbody>
			</table>
			<div style="height:44px">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
			</div>
		</form>
	</div>
</div>
	<div class="clear areaencloser"></div>
	
	<div id="listarea" class="">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">治具分布一览</span>
			<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>
		<table id="list"></table>
		<div id="listpager"></div>
		<!-- 状态 -->
		<input type="hidden" id="hidden_goStatus" value="${goStatus}">
	</div>
	<div class="clear"></div>
</div>
<!--检索结束-->

<!--双击治具分布开始-->
<div id="detail" style="display:none">
	<form id="searchform" method="POST">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title" style="width:150px;">管理编号</td>
					<td class="td-content" style="width:260px;">
						<label id="label_manage_code"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">治具No.</td>
					<td class="td-content">
						<label id="label_tools_no"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">治具品名</td>
					<td class="td-content">
						<label id="label_tools_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">发放日期</td>
					<td class="td-content">
						<label id="label_provide_date"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">发放者</td>
					<td class="td-content">
						<label id="label_provider"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分发课室</td>
					<td class="td-content">
						<label id="label_section_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任工程</td>
					<td class="td-content">
						<label id="label_line_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任工位</td>
					<td class="td-content">
						<label id="label_position_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任人员</td>
					<td class="td-content">
						<label id="label_operator_name"></label>
					</td>
				</tr>							
				<tr>
					<td class="ui-state-default td-title">状态</td>
					<td class="td-content">
						<label id="label_status"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content">
						<textarea id="label_comment" readonly disabled style="resize:none" rows="5" cols="40"></textarea>
					</td>
				</tr>
			<tbody>
		</table>
	</form>
</div>
</div>
<!--双击治具分布结束-->

<!-- 治具品名referChooser -->
<div class="referchooser ui-widget-content" id="search_name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${nReferChooser}</table>
</div>

<!-- 工位referChooser -->
<div class="referchooser ui-widget-content" id="search_position_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${pReferChooser}</table>
</div>

<!-- 责任人员referChooser -->
<div class="referchooser ui-widget-content" id="search_responsible_operator_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${rReferChooser}</table>
</div>

<div class="clear"></div>

</div>
</div>