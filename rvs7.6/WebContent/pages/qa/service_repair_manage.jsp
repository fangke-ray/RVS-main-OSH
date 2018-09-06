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
<link rel="stylesheet" type="text/css" href="css/jquery.Jcrop.min.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/qa/service_repair_manage.js"></script>

<style>
div.bar_fixed {

position: fixed;
bottom: 1px;
z-index: 180;
width: 1248px;

}
</style>
<title>保修期内返品+QIS品管理日程表</title>
</head>
<% 
	String privacy = (String) request.getAttribute("privacy");
	boolean isViewer = ("view").equals(privacy);
	boolean isManager = ("manager").equals(privacy);
	boolean isQaManager = ("privacy_qa_manager").equals(privacy);
%>
<body class="outer">
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
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-full">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content"><input type="text" id="search_model_name" maxlength="14" class="ui-widget-content"></td>
						<td class="ui-state-default td-title">机身号</td>
						<td class="td-content"><input type="text" id="search_serial_no" maxlength="12" class="ui-widget-content"></td> 
						<td class="ui-state-default td-title">修理单号</td>
						<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">类别</td>
						<td class="td-content">
							<select id="service_repair_flg" class="ui-widget-content">${sQaMaterialServiceRepair}</select>
						</td>
						<td class="ui-state-default td-title">QA受理日</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="search_qa_reception_time_start" readonly="readonly" value="${minQaReceptionTime}">起<br>
							<input type="text" class="ui-widget-content" id="search_qa_reception_time_end" readonly="readonly">止
						</td>
						<td class="ui-state-default td-title">QA判定日</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="search_qa_referee_time_start" readonly="readonly">起<br>
							<input type="text" class="ui-widget-content" id="search_qa_referee_time_end" readonly="readonly">止
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title" rowspan="2">答复时限</td>
						<td class="td-content" id="answer_in_deadline_id" rowspan="2">
							<input type="radio" name="answer_in_deadline" id="search_answer_in_deadline_all" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_answer_in_deadline_all" aria-pressed="false">(全)</label>
							<input type="radio" name="answer_in_deadline" id="search_answer_in_deadline_in_24_hour" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="search_answer_in_deadline_in_24_hour" aria-pressed="false">24小时内</label>
							<input type="radio" name="answer_in_deadline" id="search_answer_in_deadline_in_48_hour" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_answer_in_deadline_in_48_hour" aria-pressed="false">48小时内</label>
							<input type="radio" name="answer_in_deadline" id="search_answer_in_deadline_out" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="search_answer_in_deadline_out" aria-pressed="false">时限外</label>
						</td>
						<td class="ui-state-default td-title" rowspan="2">有无偿</td>
						<td class="td-content" rowspan="2">
							<select id="search_service_free_flg" class="ui-widget-content">${sServiceFreeFlg}</select>
						</td>
						<td class="ui-state-default td-title">产品分类</td>
						<td class="td-content">
							<select id="search_kind" class="ui-widget-content">${sQaCategoryKind}</select>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">未修理返还</td>
						<td class="td-content" id="unfix_back_flg_id">
							<input type="radio" name="unfix_back_flg" id="search_unfix_back_flg_all" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_unfix_back_flg_all" aria-pressed="false">(全)</label>
							<input type="radio" name="unfix_back_flg" id="search_unfix_back_flg_yes" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_unfix_back_flg_yes" aria-pressed="false">是</label>
							<input type="radio" name="unfix_back_flg" id="search_unfix_back_flg_no" class="ui-widget-content ui-helper-hidden-accessible" value="-1"><label for="search_unfix_back_flg_no" aria-pressed="false">否</label>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">实物处理</td>
						<td class="td-content" id="search_entity_send_flg">
							<input type="radio" name="entity_send_flg" id="search_entity_send_flg_all" class="ui-widget-content" value="" checked="checked"><label for="search_entity_send_flg_all">(全)</label>
							<input type="radio" name="entity_send_flg" id="search_entity_send_flg_yes" class="ui-widget-content" value="1"><label for="search_entity_send_flg_yes">寄送</label>
							<input type="radio" name="entity_send_flg" id="search_entity_send_flg_no" class="ui-widget-content" value="-1"><label for="search_entity_send_flg_no">不寄送</label>
						</td>
						<td class="ui-state-default td-title">质量信息单号</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="search_quality_info_no">
						</td>
						<td class="ui-state-default td-title">QIS发送单号</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="search_qis_invoice_no">
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">ETQ单号</td>
						<td class="td-content">
							<input type="text" class="ui-widget-content" id="search_etq_no">
						</td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content"></td>
					</tr>
					<tr style="display:none">
				    	<td>
				      		<!-- 维修站 -->
						    <select id="search_workshop" >${sWorkshop}</select>
						    <!-- 分析结果 -->
						    <select id="hidden_select_manufactory_flg" >${sAnalysis_result}</select>
						    <!--责任区分-->
						  	<select id="hidden_select_liability_flg" >${sLiability_flg}</select>
						  	<!--处理方式-->
						  	<select id="hidden_select_qis_corresponse_flg" >${sQis_corresponse_flg}</select>
						  	<!--实物处理-->
						  	<select id="hidden_select_qis_entity_send_flg" >${sQis_entity_send_flg}</select>
						  	<!--质量判定  -->
						  	<select id="hidden_select_quality_judgment" >${sQuality_judgment}</select>
						  	<!--发行QIS  -->
						  	<select id="hidden_select_qis_isuse" >${sQis_isuse}</select>
				   		</td>
				   	</tr>
				</tbody>
			</table>
				<div style="height:44px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="hidden" id="h_goQaMaterialServiceRepair" value="${goQaMaterialServiceRepair}"/>
					<input type="hidden" id="h_goWorkshop" value="${goWorkshop}"/>
					<input type="hidden" id="h_goServiceFreeFlg" value="${goServiceFreeFlg}"/>
					<input type="hidden" id="h_goLiabilityFlg" value="${goLiabilityFlg}"/> <!-- TODO -->
					<input type="hidden" id="h_goQualityJudgment" value="${goQuality_judgment}"/>
					<input type="hidden" id="h_goQisIsuse" value="${goQis_isuse}"/>
					<input type="hidden" id="h_goQaCategoryKind" value="${goQaCategoryKind}"/>
					
					<input type="hidden" id="hidden_label_model_name" />
					<input type="hidden" id="hidden_label_serial_no" />
					<input type="hidden" id="hidden_label_sorc_no" />
					<input type="hidden" id="hidden_label_service_repair_flg" />
					<input type="hidden" id="hidden_rc_mailsend_date" />
					<input type="hidden" id="hidden_service_repair_flg_val"/>
					<input type="hidden" id="hidden_textarea_mention"/>
				</div>
		</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">保修期内返品+QIS品管理日程表</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div id="chooser" class="dwidth-full ui-widget-content" style="border-bottom: 0;">
		<div id="colchooser">
			 <input type="checkbox" id="colchooser_repair"></input><label for="colchooser_repair">维修对象信息</label>
		     <input type="checkbox" id="colchooser_qis"></input><label for="colchooser_qis">QIS管理信息</label>
		     <input type="checkbox" id="colchooser_etq"></input><label for="colchooser_etq">ETQ信息</label>
	     </div>
	</div>
	<table id="list" ></table>
	<div id="listpager"></div>
	<div id="show_Accept"></div>
	<div class="clear areaencloser"></div>
</div>

<div id="functionarea" class="dwidth-full" 
<% if (!isViewer) { %>
style="margin:auto;height:44px;"
<% } %>
>
<% if (!isViewer && !isManager) { %>
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" id="qisacceptbutton" class="ui-button" value="QIS品受理"/>			
			<input type="button" id="editbutton"  class="ui-button" value="编辑"/>
			<input type="button" id="mentionbutton"  class="ui-button" value="提要"/>
			<input type="button" id="deletebutton" class="ui-button" value="删除"/>	
	<% if (isQaManager) { %>
			<input type="button" id="backbutton" class="ui-button" value="退回作业前"/>
	<% } %>
			<input type="button" id="analysisbutton" class="ui-button" value="分析"/>
			<input type="button" id="currentResultbutton" class="ui-button" value="当前结果导出" style="float:right;margin-right:10px;"/>
		</div>
	</div>
	<div class="clear"></div>
<% } %>

<% if (isManager) { %>
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" id="currentResultbutton" class="ui-button" value="当前结果导出" style="float:right;margin-right:10px;"/>
		</div>
	</div>
	<div class="clear"></div>
<% } %>

</div>

</div>

<div id="process_dialog">
</div>
<div id="update_agree_dialog">
</div>
<div id="footarea">
</div>
</div>
</body>
</html>