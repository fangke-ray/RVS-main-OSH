<%@page import="framework.huiqing.common.util.CodeListUtils"%>
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
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.flowchart.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/qf/fact_material.js?v=3459"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>
<script type="text/javascript" src="js/qf/wip_locate.js"></script>

<style>
div.bar_fixed {

position: fixed;
bottom: 1px;
z-index: 180;
width: 1248px;

}

#inline_plan .ui-icon {
	float:right;
	cursor:pointer;
}

#inline_plan table {
	width:100%;
}
#inline_plan table tr.inline > td {
	background-color : lightgray;
}
#inline_plan td[img_check_need] {
	background-color : rgb(228,178,64);
}
.alertCell {
	background-color : #FFC000;
	color: white;
}
#listareas .areatitle {
	padding :0 4px;
	cursor: pointer;
}
#listareas .areatitle.checked {
	background-color:limegreen;
}
</style>
<title>投线管理</title>
</head>
<body class="outer">

<% 
	String editor = (String) request.getAttribute("editor");
	boolean isEditor = ("true").equals(editor);
	boolean isManager = ("manage").equals(editor);
%>

<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
	<div id="basearea" class="dwidth-full" style="margin: auto;">
		<jsp:include page="/header.do" flush="true">
			<jsp:param name="part" value="1"/>
		</jsp:include>
	</div>
<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">

<div id="searcharea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">检索条件</span>
	</div>
	<div class="ui-widget-content dwidth-full">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody><tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" value="${omr_notifi_no}" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content" readonly="readonly" id="txt_modelname">
						<input type="hidden" name="modelname" id="search_modelname">
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title" rowspan=2>等级</td>
					<td class="td-content" colspan=3 rowspan=2>
						<select id="search_level" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_level", null, "全部", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">流水线／单元</td>
					<td class="td-content">
						<select id="search_fix_type" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_fix_type", null, "全部", false) %>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">直送</td>
					<td class="td-content">
						<select id="search_direct" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_direct", null, "全部", false) %>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title" rowspan="2">修理（默认）课室</td>
					<td class="td-content" rowspan="2">
						<select id="search_section_id" class="ui-widget-content">
							<option value="">(全)</option>
							${sOptions}
						</select>
						<!--input type="text" id="search_esas_no" maxlength="6" class="ui-widget-content"-->
					</td>
					<td class="ui-state-default td-title">WIP位置</td>
					<td class="td-content"><input type="text" name="wiplocation" id="search_wip_location" maxlength="5" class="ui-widget-content"></td>
					<td class="ui-state-default td-title" rowspan="2">客户同意日</td>
					<td class="td-content" rowspan="2">
						<input type="text" id="search_agreed_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_agreed_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">查询范围</td>
					<td class="td-content" id="search_agreed">
						<input type="checkbox" id="search_agreed_y" checked><label for="search_agreed_y">已同意</label>
						<input type="checkbox" id="search_agreed_n"><label for="search_agreed_n">未同意</label>
					</td>
				</tr>

			</tbody></table>
					<div style="height:44px">
						<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="hidden" id="twoDaysBefore" value="${twoDaysBefore}"/>
						<input type="hidden" id="oneDayBefore" value="${oneDayBefore}"/>
						<input type="hidden" id="kOptions" value="${kOptions}"/>
						<input type="hidden" id="poOptions" value="${poOptions}"/>
						<input type="hidden" id="switch_from" value="${switch_from}" />
		${empty switch_from ? '' : '<input type="button" class="ui-button" id="switchbutton" value="返回零件发放" role="button" style="float: right; right: 2px" onclick="javascript:window.location.href=($(\'#switch_from\').val()+\'.do\')">'}
					</div>
			</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="countarea" class="dwidth-full" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">投线统计</span>
	</div>
	<div class="ui-widget-content dwidth-full">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title">修理一课 A 线</td>
				<td class="td-content" for="0"></td>
				<td class="ui-state-default td-title">修理一课 B1 线</td>
				<td class="td-content" for="1"></td>
				<td class="ui-state-default td-title">修理一课 B2 线</td>
				<td class="td-content" for="2"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">修理二课 中小修</td>
				<td class="td-content" for="5"></td>
				<td class="ui-state-default td-title">修理二课 纤维镜大修</td>
				<td class="td-content" for="3"></td>
				<td class="ui-state-default td-title">修理二课 超声镜大修</td>
				<td class="td-content" for="4"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">修理二课 Endoeye</td>
				<td class="td-content" for="6"></td>
				<td class="ui-state-default td-title">修理二课 光学视管</td>
				<td class="td-content" for="8"></td>
				<td class="ui-state-default td-title">修理三课 周边设备</td>
				<td class="td-content" for="7"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">动物实验用维修品</td>
				<td class="td-content" for="9"></td>
				<td class="ui-state-default td-title"></td>
				<td class="td-content"></td>
				<td class="ui-state-default td-title"></td>
				<td class="td-content"></td>
			</tr>
			</tbody>
		</table>

	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listareas">

<div id="listarea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle checked">准备投线品一览</span>
		<span class="areatitle">投线进行品一览</span>
		<span class="areatitle">今日投线品一览</span>
	</div>
	<table id="list"></table>
	<div id="listpager"></div>
	<div class="clear"></div>
</div>

<div id="steplistarea" class="dwidth-full" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">准备投线品一览</span>
		<span class="areatitle checked">投线进行品一览</span>
		<span class="areatitle">今日投线品一览</span>
	</div>

	<table id="step_list"></table>
	<div id="step_listpager"></div>
	<div class="clear"></div>
</div>

<div id="exelistarea" class="dwidth-full" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">准备投线品一览</span>
		<span class="areatitle">投线进行品一览</span>
		<span class="areatitle checked">今日投线品一览</span>
	</div>

	<table id="exe_list"></table>
	<div id="exe_listpager"></div>
	<div class="clear"></div>
</div>


<div id="functionarea" class="dwidth-full" style="margin:auto;">
<% if (isEditor || isManager) { %>
	<div id="functionarea1" class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes_1" style="margin-left:4px;margin-top:4px;">
			<input type="button" id="inlinestepbutton" class="ui-button" value="准备投线"/>
			<!-- input type="button" id="agreebutton" class="ui-button" value="导入同意日期/返还要求"/ -->
<% if (isManager) { %>
			<input type="button" id="updateagreebutton" class="ui-button" value="修改同意日期"/>
<% } %>
			<input type="button" id="imgcheckbutton" class="ui-button" value="进行图象检查"/>
			<input type="button" id="inwipbutton" class="ui-button" value="放回WIP"/>
			<input type="button" id="outwipbutton" class="ui-button" value="从WIP出库"/>
			<input type="button" id="expeditebutton" class="ui-button" value="加急"/>
		</div>
	</div>
	<div id="functionarea2" class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
		<div id="executes_2" style="margin-left:4px;margin-top:4px;">
			<input type="button" id="inlinebutton" class="ui-button" value="投入流水线"/>
			<input type="button" id="ccdchangebutton" class="ui-button" value="进行CCD盖玻璃更换"/>
			<input type="button" id="inwip2button" class="ui-button" value="放回WIP"/>
			<input type="button" id="outwip2button" class="ui-button" value="从WIP出库"/>
			<input type="button" id="inlinesbutton" class="ui-button" style="float:right;margin-right:4px;" value="投线单一览"/>
		</div>
	</div>
	<div id="functionarea3" class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
		<div id="executes_3" style="margin-left:4px;margin-top:4px;">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="printbutton" value="打印小票" role="button" aria-disabled="false">
			<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="movebutton" value="移动课室" role="button" aria-disabled="false">
			<input type="button" class="ui-button" id="outbutton" value="报表导出" role="button" aria-disabled="false" style="float:right;right:2px">
		</div>
	</div>
<div class="clear areaencloser" style="height:16px;"></div>
<% } %>
</div>

</div>

</div>

<div id="update_agree_dialog">
</div>
<div id="footarea">
</div>
			<div class="if_message-dialog ui-warn-dialog" style="margin-left: -250px; width:500px;">
  <div class="ui-dialog-titlebar ui-widget-header" style="height:24px;"><span>接口处理同步</span></div>
				<div style="margin-top: 1em;margin-bottom: 1em;">其他系统中的操作已经更新了客户同意日，请刷新一览表。</div>
				<input type="button" class="ui-button" value="忽略"></input>
				<input type="button" class="ui-button" value="刷新"></input>
			</div>

<div class="referchooser ui-widget-content" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>

	<table class="subform">${mReferChooser}</table>
</div>
	<div id="wip_pop"></div>
<div id="inline_plan" style="display:none;">
	<table class="condform">
		<thead><tr>
			<th class="ui-state-default">修理单号</th><th class="ui-state-default">型号</th><th class="ui-state-default">机身号</th><th class="ui-state-default">等级</th>
			<th class="ui-state-default">保内返修</th><th class="ui-state-default">同意日</th><th class="ui-state-default">WIP 库位</th>
			<th class="ui-state-default">投线课室</th><th class="ui-state-default">选用流程</th><th class="ui-state-default">其他</th>
		</tr></thead>
		<tbody></tbody>
	</table>
</div>
<div id="inline_plan_selecter" style="position:absolute;display:none;max-width:400px;background-color:white;border:1px solid #93c3cd;padding:2px;"><select></select></div>

</div>
</body>
<div id="process_dialog">
</div>
<div id="sections" style="display:none;">${sOptions}</div>
<div id="pats" style="display:none;">${paOptions}</div>

</html>