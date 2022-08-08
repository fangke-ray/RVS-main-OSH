<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/custom.css?v=3476">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/flowchart.css?v=3">
<link rel="stylesheet" type="text/css" href="css/partial/instruction_sheets.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.flowchart.js"></script>
<script type="text/javascript" src="js/qf/before_line_leader.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>
<script type="text/javascript" src="js/qf/set_material_process_assign.js"></script>
<script type="text/javascript" src="js/qf/set_optional_fix.js"></script>
<script type="text/javascript" src="js/partial/common/instruction_sheets.js?v=2"></script>
<title>现品报价课 受理报价工程</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
<div id="basearea" class="dwidth-full" style="margin:auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
</div>

<% 
	String editor = (String) request.getAttribute("editor");
	boolean isEditor = ("true").equals(editor);
	String manager = (String) request.getAttribute("manager");
	boolean isManager = ("true").equals(manager);
	String quotator = (String) request.getAttribute("quotator");
	boolean isQuotator = ("true").equals(quotator);

	if (isEditor) {
%>

<script type="text/javascript">
${WORKINFO}
</script>

<% 
	} // isEditor
%>
<div class="ui-widget-panel width-full" style="align:center;padding-top:16px;overflow-x: hidden;" id="body-3">
	
	<div class="ui-widget-header dwidth-full" style="align:center;padding-top:6px;padding-bottom:6px;margin-bottom:16px;text-align:center;">
		<span>现品报价课 受理报价工程</span>
	</div>

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
							<td class="ui-state-default td-title">修理单号</td>
							<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
							<td class="ui-state-default td-title">型号</td>
							<td class="td-content">
								<input type="text" class="ui-widget-content" readonly="readonly" id="txt_modelname">
								<input type="hidden" name="modelname" id="search_model_id">
							</td>
							<td class="ui-state-default td-title" rowspan="3">机身号</td>
							<td class="td-content" rowspan="3" style="vertical-align: top;">
								<input type="text" id="search_serialno" maxlength="20" class="ui-widget-content">
								<input type="button" class="ui-button" id="search_serialno_plus" value="＋" style="padding: 0;"/>
								<input type="button" class="ui-button" id="search_serialno_clear" value="×" style="padding: 0;"/>
								<style>
								#search_serialnos span {
									float: left;
									margin-right: .6em;
								}
								#search_serialnos span:hover {
									cursor: not-allowed;
									background-color: yellow;
								}
								</style>
								<div id="search_serialnos" style="max-width:400px;"></div>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title" rowspan="2">等级</td>
							<td class="td-content" rowspan="2">
								<select id="search_level" class="ui-widget-content">
									${lOptions};
								</select>
							</td>
							<td class="ui-state-default td-title">客户同意</td>
							<td class="td-content">
								<div id="search_agreed_set" class="ui-buttonset">
									<input type="radio" name="agreed" id="agreed_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="agreed_a" aria-pressed="false">全部</label>
									<input type="radio" name="agreed" id="agreed_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="agreed_t" aria-pressed="false">有同意日</label>
									<input type="radio" name="agreed" id="agreed_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="agreed_f" aria-pressed="false">无同意日</label>
								</div>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">WIP位置</td>
							<td class="td-content">
								<input type="text" name="wip_location" id="search_wip_location" maxlength="5" class="ui-widget-content">
							</td>
						</tr>
						<!--tr>
							<td class="ui-state-default td-title">备注</td>
							<td class="td-content" colspan="5"></td>
						</tr-->
					</tbody>
				</table>
				<div style="height: 44px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" style="float: right; right: 2px"> <input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" style="float: right; right: 2px">
					<input id="isEditor" type="hidden" value="<%=editor%>" />
				</div>
			</form>
		</div>
		<div class="clear areaencloser dwidth-full"></div>
	</div>


<div id="workarea" class="dwidth-full" style="margin-bottom:16px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">今日 现品报价课 受理报价工程 一览</span>
	</div>
	<div class="ui-widget-content">
		<div style="padding:2px;padding-left:6px;padding-top:8px;height:36px;">
<% if (isManager) { %>
		
		<input id="expeditebutton" class="ui-button" value="优先报价" type="button"/>
		<input id="nogoodbutton" class="ui-button" value="中断再开" type="button"/>
		<!--input type="button" class="ui-button" value="系统返还" id="resystembutton" /-->
		<input type="button" class="ui-button" value="移动库位" id="movebutton" />
		<div class="ui-button" id="sendbutton" style="font-size:14px;">
			转送
			<div class="ui-widget-content" style="z-index:2;position:absolute;display:none;">
				<input type="button" class="ui-button" value="送品保分析" id="sendqabutton" />
				<input type="button" class="ui-button" value="送CCD盖玻璃更换" id="sendccdbutton" />
			</div>
		</div>
		<input type="button" class="ui-button" id="printbutton" value="重新打印小票" />
		<input type="button" class="ui-button" id="printaddbutton" value="补充打印小票" />
		<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
		<input class="ui-button" id="attendance_button" value="当日出勤" type="button"/>
<% } else if (isEditor) { %>
		<input type="button" class="ui-button" id="printbutton" value="重新打印小票" />
		<input type="button" class="ui-button" id="printaddbutton" value="补充打印小票" />
		<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
<% } %>
<% if (isQuotator) {%>
		<input type="button" class="ui-button" value="未修理返还" id="stopbutton" />
		<input type="button" class="ui-button" value="报价备注" id="quotationcommentbutton" />
		<input type="button" class="ui-button" value="工作指示单编辑" id="instuctbutton" />
<%@include file="/widgets/qf/record_quotation.jsp"%>
<% } %>
	</div>
		<table id="performance_list"></table>
		<div id="performance_listpager"></div>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser"></div>
	<div class="clear"></div>
</div>

<div id="pop_treat">
</div>
<div class="referchooser ui-widget-content" id="model_refer1" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform">${mReferChooser}</table>
<div>
<div id="confirmmessage"></div>

<div id="light_fix_dialog"></div>

<div id="comment_dialog">
	<table class="condform">
		<tr>
			<td class="td-content">
				<textarea id="edit_material_comment" style="width:500px;height:100px;resize:none;" maxlength="500"></textarea>
			</td>
			<td class="td-content">
				<textarea id="edit_material_comment_other" style="width:500px;height:100px;resize:none;display:none;" readonly disabled></textarea>
			</td>
		</tr>
	</table>
</div>

<input type="hidden" id="paOptions" value='${paOptions }'>
</body></html>