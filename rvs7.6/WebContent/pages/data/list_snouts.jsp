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
<script type="text/javascript" src="js/data/list_snouts.js"></script>
<script type="text/javascript" src="js/data/setting_snouts.js"></script>
<title>D/E 组件信息查询</title>
</head>
<body class="outer" style="align: center;">

<% 
	String role = (String) request.getAttribute("role");
	boolean isFact = ("fact").equals(role) || ("both").equals(role);
	boolean isLine = ("line").equals(role) || ("both").equals(role);
%>

	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
<div id="basearea" class="dwidth-full" style="margin: auto;">
	<jsp:include page="/header.do" flush="true">
		<jsp:param name="part" value="2"/>
	</jsp:include>
</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
<% String from = (String) request.getAttribute("from"); 
	if (!"qf".equals(from)) {
%>
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="进度查询"/>
				</jsp:include>
			</div>
<%
	} else {
%>
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="消耗品/组件管理"/>
				</jsp:include>
			</div>
<%
	}
%>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<!-- Tab选项卡 -->	
					<div style="border-bottom: 0;" class="ui-widget-content dwidth-middleright">
						<div id="infoes" class="ui-buttonset">
<%
	if (isFact) {
%>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_setting_tab" value="page_setting" checked>
							<label for="page_setting_tab">D/E 组件设置与可用 C 本体追溯</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_list_tab" value="page_process">
							<label for="page_list_tab">D/E 组件作业查询</label>
<%
	} else {
%>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_setting_tab" value="page_setting">
							<label for="page_setting_tab">D/E 组件设置与可用 C 本体追溯</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_list_tab" value="page_process" checked>
							<label for="page_list_tab">D/E 组件作业查询</label>
<%
	}
%>
						</div>
					</div>
<%
	if (isFact) {
%>
	<div for="page_setting" style="margin:auto;">
<%
	} else {
%>
	<div for="page_setting" style="margin:auto;display:none;">
<%
	}
%>
		<!-- JqGrid表格  -->
		<div id="settingListarea" class="width-middleright">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">D/E 组件设置与库存一览</span>
			</div>
			<table id="snout_component_setting">
			</table>
			<div id="snout_component_setting_pager"></div>
			<% if (isFact) { %>
			<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
				<div id="executes" style="margin-left: 4px; margin-top: 2px;">
					<input id="st_add_button" class="ui-button" value="新建 D/E 组件机型" role="button" type="button">
					<input id="st_edit_button" class="ui-button" value="修改 D/E 组件机型" role="button" type="button">
					<input id="st_delete_button" class="ui-button" value="取消 D/E 组件机型" role="button" type="button">
					<input id="st_adjust_button" class="ui-button" value="调整目镜·软管筒数" role="button" type="button" style="float:right; margin-right: 4px;">
					<input id="st_snout_list_button" class="ui-button" value="C 本体详细" role="button" type="button" style="float:right; margin-right: 4px;">
					<div class="clear"></div>
				</div>
			</div>
			<% } else if (isLine) { %>
			<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
				<div id="executes" style="margin-left: 4px; margin-top: 2px;">
					<input id="st_adjust_button" class="ui-button" value="调整目镜·软管筒数" role="button" type="button" style="float:right; margin-right: 4px;">
					<input id="st_snout_list_button" class="ui-button" value="C 本体详细" role="button" type="button" style="float:right; margin-right: 4px;">
					<div class="clear"></div>
				</div>
			</div>
			<% } %>
		</div>
	</div>

<%
	if (isFact) {
%>
	<div for="page_process" style="margin:auto;display:none;">
<%
	} else {
%>
	<div for="page_process" style="margin:auto;">
<%
	}
%>
<div id="searcharea" class="dwidth-middleright">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">D/E 组件检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content">
		<form id="searchform" method="POST">
			<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">D/E 组件型号</td>
					<td class="td-content" colspan="3"><select name="model_id" id="cond_model_id" class="ui-widget-content">${mOptions}</select></td>
					<td class="ui-state-default td-title">D/E 组件序列号</td>
					<td class="td-content"><input type="text" name="serial_no" id="cond_serial_no" maxlength="20" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">状态</td>
					<td class="td-content"><select name="status" id="cond_used" class="ui-widget-content">${rOptions}</select></td>
					<td class="ui-state-default td-title">完成时间</td>
					<td class="td-content">
						<input type="text" name="finish_time_from" id="cond_finish_time_from" class="ui-widget-content">起<br/>
						<input type="text" name="finish_time_to" id="cond_finish_time_to" class="ui-widget-content">止
					</td>
					<td class="ui-state-default td-title">作业者</td>
					<td class="td-content"><input type="text" readonly class="ui-widget-content"><input type="hidden" name="operator_id" id="cond_operator_id" maxlength="14" class="ui-widget-content"></td>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">来源修理单号</td>
					<td class="td-content"><input type="text" name="origin_omr_notifi_no" id="cond_origin_omr_notifi_no" maxlength="20" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">来源机身号</td>
					<td class="td-content">
						<input type="text" name="origin_serial_no" id="cond_origin_serial_no" maxlength="20" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content"></td>
					</td>
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
<% if (isLine) { %>
<div class="ui-widget-header areabase" style="padding-top: 4px; margin-button: 6px; margin-bottom: 16px;">
	<div id="list_executes" style="margin-left: 4px; margin-top: 2px;">
		<input id="move_button" class="ui-button" value="移库" role="button" type="button" style="float:right; margin-right: 4px;">
		<div class="clear"></div>
	</div>
</div>
<% } %>

<div id="detail_dialog">
</div>

<div class="clear areacloser"></div>
</div>
				</div>
			</div>

			<div class="clear dwidth-middleright"></div>
		</div>
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

<div id="st_pop_window_new" style="display:none;">
	<form id="st_new_form">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content">
					<input type="text" id="view_mode_name" name="view_mode_name" alt="型号" class="ui-widget-content" readonly="readonly">
					<input type="hidden" name="model_id" id="add_model_id" alt="型号" >
					<input type="hidden" name="partial_id" id="new_partial_id" alt="零件ID"></input>
					<input type="checkbox" id="need_compose"></input><label for="need_compose">不组装</label>
				</td>
				<td class="ui-state-default td-title">组件代码</td>
				<td class="td-content">
					<input type="text" name="code" id="add_code" alt="组件代码"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">放置货架</td>
				<td class="td-content">
					<input type="text" name="shelf" id="add_shelf" alt="放置货架"></input>
				</td>
				<td class="ui-state-default td-title">放置层数</td>
				<td class="td-content">
					<input type="text" name="layer" id="add_layer" alt="放置层数"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">基准库存</td>
				<td class="td-content">
					<input type="number" name="safety_lever" id="add_safety_lever" alt="基准库存" style="text-align:right"></input>
				</td>
				<td class="ui-state-default td-title">安全库存</td>
				<td class="td-content">
					<label id="label_add_safety_lever"></label>
				</td>
				<!--td class="ui-state-default td-title">翻新 C 本体</td>
				<td class="td-content">
					<label>翻新 C 本体代码请到 零件管理》预制零件管理 中设置</label>
				</td-->
			</tr>
			<tr>
				<td class="ui-state-default td-title">目镜·软管筒</td>
				<td class="td-content" colspan="3">
					<label>目镜·软管筒零件代码请到 零件管理》预制零件管理 中设置</label>
				</td>
			</tr>
		</tbody>
	</table>
	</form>
</div>

<div id="st_pop_window_modify" style="display:none;">
	<form id="st_modify_form">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content">
					<label type="text" id="label_modelname" alt="型号"></label>
					<input type="hidden" name="model_id" id="edit_model_id"/>
					<input type="hidden" name="partial_id" id="edit_partial_id"></input>
				</td>
				<td class="ui-state-default td-title">组件代码</td>
				<td class="td-content">
					<input type="text" name="code" id="edit_code" alt="组件代码"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">放置货架</td>
				<td class="td-content">
					<input type="text" name="shelf" id="edit_shelf" alt="放置货架"></input>
				</td>
				<td class="ui-state-default td-title">放置层数</td>
				<td class="td-content">
					<input type="text" name="layer" id="edit_layer" alt="放置层数"></input>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">基准库存</td>
				<td class="td-content">
					<input type="number" name="safety_lever" id="edit_safety_lever" alt="基准库存" style="text-align:right"></input>
				</td>
				<td class="ui-state-default td-title">安全库存</td>
				<td class="td-content">
					<label id="label_edit_safety_lever"></label>
				</td>
				<!--td class="ui-state-default td-title">翻新 C 本体</td>
				<td class="td-content">
					<label id="label_refurbished_code"></label>
				</td-->
			</tr>
			<tr>
				<td class="ui-state-default td-title">其他目镜·软管筒</td>
				<td class="td-content" colspan="3">
					<label id="label_partial_code"></label>
				</td>
			</tr>
		</tbody>
	</table>
	</form>
</div>

<div id="st_pop_window_adjust" style="display:none;">
	<table class="condform">
		<tbody>
			<tr>
				<td class="ui-state-default td-title">型号</td>
				<td class="td-content">
					<label type="text" id="adjust_label_modelname" alt="型号"></label>
					<input type="hidden" name="model_id" id="adjust_model_id"/>
					<input type="hidden" name="partial_id" id="adjust_partial_id"></input>
				</td>
				<td class="ui-state-default td-title">组件代码</td>
				<td class="td-content">
					<label type="text" id="adjust_code" alt="组件代码"></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">基准库存</td>
				<td class="td-content">
					<label id="adjust_benchmark"></label>
				</td>
				<td class="ui-state-default td-title">安全库存</td>
				<td class="td-content">
					<label id="adjust_safety_lever"></label>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">目镜·软管筒库存<br>(原先)</td>
				<td class="td-content">
					<label id="adjust_sub_set_cnt_org"></label> 套
				</td>
				<td class="ui-state-default td-title">目镜·软管筒库存<br>(修改)</td>
				<td class="td-content">
					<input type="text" name="layer" id="adjust_sub_set_cnt" alt="目镜·软管筒库存"></input>
				</td>
			</tr>
		</tbody>
	</table>
</div>

<div id="st_pop_window_prepairlist" style="display:none;">
	<table id="snout_prepairlist">
	</table>
	<div id="snout_prepairlist_pager"></div>
<% if (isLine) { %>
	<div class="ui-widget-header" style="padding: 4px;">
		<input id="sth_add_button" class="ui-button" value="补充 C 本体" role="button" type="button">
		<input id="sth_remove_button" class="ui-button" value="废弃 C 本体" role="button" type="button">
		<input id="sth_wash_button" class="ui-button" value="回收清洗 C 本体" role="button" type="button">
	</div>
<% } %>
</div>
<div id="st_pop_history" style="display:none;">
<style type="text/css">
#history_list > tbody td {
	border:1px solid gray;
}
</style>
	<table id="history_list" cellspacing="0">
	<thead>
		<th id="list_no" class="ui-state-default ui-th-column ui-th-ltr">次数</th>
		<th id="list_serial_no"  class="ui-state-default ui-th-column ui-th-ltr"><div>机身号</div></th>
		<th id="list_origin_omr_notifi_no" class="ui-state-default ui-th-column ui-th-ltr"><div id="jqgh_list_origin_omr_notifi_no" >维修单号</div></th>
		<th id="list_C1"  class="ui-state-default ui-th-column ui-th-ltr"><div>医院</div></th>
		<!--th id="list_model_name"  class="ui-state-default ui-th-column ui-th-ltr"><div id="jqgh_list_model_name" >D/E 组件序列号</div></th-->
		<th id="list_finish_time"  class="ui-state-default ui-th-column ui-th-ltr"><div id="jqgh_list_finish_time" >完成时间</div></th>
	</thead>
	<tbody>
	</tbody>
	</table>
</div>

<% if (isLine) { %>
<div id="st_pop_tobe" style="display:none;">
<style type="text/css">
#tobe_list > tbody td {
	border:1px solid gray;
}
#tobe_list > tbody > tr:hover > td {
	cursor: pointer;
	background-color: yellow;
}
#tobe_list > tbody > tr > td:nth-child(3) {
	text-align:center;
}
#tobe_list > tbody > tr > td:nth-child(4) {
	text-align:right;
}
</style>
	<table id="tobe_list" cellspacing="0">
	<thead>
		<th class="ui-state-default ui-th-column ui-th-ltr"><div id="jqgh_list_origin_omr_notifi_no" >修理单号</th>
		<th class="ui-state-default ui-th-column ui-th-ltr">机身号</th>
		<th class="ui-state-default ui-th-column ui-th-ltr">修理等级</th>
		<th class="ui-state-default ui-th-column ui-th-ltr">C 本体翻新次数</th>
	</thead>
	<tbody>
	</tbody>
	</table>
</div>
<% } %>

<!-- 货架选择画面内容 -->
<div id="shelf_pop"></div>

<!-- 型号下拉选择内容 -->
<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
	<!-- 下拉选择内容 -->
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table class="subform">${mReferChooser}</table>
</div>

</body>
</html>