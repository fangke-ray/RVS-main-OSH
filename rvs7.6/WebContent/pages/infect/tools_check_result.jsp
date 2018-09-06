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

<script type="text/javascript" src="js/infect/tools_check_result.js"></script>
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
<title>治具点检结果</title>
</head>
<body class="outer" style="overflow: auto;">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
	<div id="basearea" class="dwidth-full" style="margin: auto;">
		<jsp:include page="/header.do" flush="true">
			<jsp:param name="part" value="1"/>
			<jsp:param name="sub" value="t"/>
		</jsp:include>
	</div>

<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">
<div id="searcharea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-full">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">管理号码</td>
						<td class="td-content"><input type="text" name="manage_no" alt="管理号码" id="search_manage_no" class="ui-widget-content"></td>
						<td class="ui-state-default td-title">治具号码</td>
						<td class="td-content"><input type="text" name="tools_no" alt="治具号码" id="search_tools_no" class="ui-widget-content"></td> 
						<td class="ui-state-default td-title">治具名称</td>
						<td class="td-content">
							<input type="text" name="tools_type_id" alt="治具名称"id="search_tools_name" class="ui-widget-content">
						</td>
					</tr>	
					<tr>
						<td class="ui-state-default td-title">分发课室</td>
						<td class="td-content"> 
						<select id="search_section_id" class="ui-widget-content">${sectionOptions}</select></td>			
						<td class="ui-state-default td-title">责任工程</td>
						<td class="td-content"> 
						<select id="search_line_id" class="ui-widget-content" name="line_id" >${lineOptions}</select></td>	
						<td class="ui-state-default td-title">责任工位</td>
						<td class="td-content">
						  <input type="text" readonly="readonly" name="position_id" alt="责任工位" id="search_position_id"/>
						  <input type="hidden" id="hidden_search_position_id"/>
						</td>	
					</tr>	
					<tr>
					   <td class="ui-state-default td-title">点检人员</td>
						<td class="td-content">
							<input type="text" readonly="readonly"  name="operator_id" id="search_operator_id" alt="点检人员"/>
							<input type="hidden" id ="hidden_search_operator_id"/>
						</td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
					</tr>
				</tbody>
			</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
			</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">
    <div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">治具点检结果一览</span>	
		<!-- <span class="areatitle" style="float:right;margin-right:10px;">点检合格台数：<span id="qualified"></span>；点检不合格台数：<span id="unqualified"></span></span> -->
		<span class="areatitle" style="float:right;margin-right:10px;">本月不合格台数：<span id="unqualified"></span></span>
	</div>
	
	<!-- 当前月 -->
	<input type="hidden" id="hidden_currMonth" value="${currMonth}"> 
	<!-- 状态select -->
	<input type="hidden" id="select_Checked_status" value="${SCheckedStatus}">
	
	<table id="exd_list" ></table>
	<div id="exd_listpager"></div>
	<div class="clear areaencloser"></div>
</div>
<div id="show_detail" class="ui-widget-content" style="display:none">
			<div class="ui-widget-content">
				<form id="searchform" method="POST">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">管理编号</td>
								<td class="td-content">
									<label id="label_manage_code"></label>
								</td>
								<td class="ui-state-default td-title">治具号码</td>
								<td class="td-content">
									<label id="label_tools_no"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">治具名称</td>
								<td class="td-content">
									<label id="label_tools_name"></label>
								</td>
								<td class="ui-state-default td-title">工程</td>
								<td class="td-content">
									<label id="label_line_name"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">课室</td>
								<td class="td-content">
									<label id="label_section_name"></label>
								</td>
								<td class="ui-state-default td-title">工位</td>
								<td class="td-content">
									<label id="label_process_code"></label>
								</td>
							</tr>
						</tbody>
					</table>
				</form>
			</div>
			<table id="detail_list" ></table>
			<div id="detail_listpager"></div>
		</div>

<!-- 治具名称ReferChoose -->
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

<!-- 点检人员ReferChoose -->
<div class="referchooser ui-widget-content" id="search_operator_name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${oReferChooser}</table>
</div>

<!-- 工位ReferChoose -->
<div class="referchooser ui-widget-content" id="search_position_name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${pReferChooser}</table>
</div>
</div>
</body>