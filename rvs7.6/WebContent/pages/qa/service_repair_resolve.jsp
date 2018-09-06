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
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/jquery.picutePreview.js"></script>

<script type="text/javascript" src="js/qa/service_repair_resolve.js"></script>
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
.manageNo{
    height:40px;
	width:300px;
	border:1px solid #000000;
	float:left;
}

.td-border{
  border:1px solid #000000;
  text-align:center;
}

.top-title{
   height:40px;
   width:300px;
   float:left;
   text-align:center;
   font-size:22px;
   font-weight:bold;
   border:1px solid #000000;
   position:absolute;
   top:6px;
   left:622px;
}
 #delete_circle{
    height:20px;
	width:20px;
	border-radius:10px;
	position:relative;
	background:#fff;
	left:137px;
	top:20px;
	text-align:center;
	color:blue;
	font-weight:bold;
	cursor:pointer;
	display:none;
 }

.self_resp td {
	background-color : #F3CEB6;
}

</style>
<title> 保修期内返品分析对策</title>
</head>
<%
     String privacy =(String)request.getAttribute("privacy");
	 boolean isManager = ("manager").equals(privacy);
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
				<tbody><tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content"><input type="text" id="search_model_name" maxlength="30" class="ui-widget-content"></td>
					
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content"></td> 
					
					<td class="ui-state-default td-title">修理编号</td>
					<td class="td-content"><input type="text" id="search_sorc_no" maxlength="18" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任区分</td>
					<td class="td-content">
						<select id="search_liability_flg" class="ui-widget-content">${sLiability_flg}</select>
					</td>
					<td class="ui-state-default td-title">QA判定日</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content" id="search_qa_referee_time_start" value="${two_weeks_ago}" readonly="readonly">起<br>
						<input type="text" class="ui-widget-content" id="search_qa_referee_time_end" readonly="readonly">止
					</td>

					<td class="ui-state-default td-title">QA分析日</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content" id="search_qa_reception_time_start" readonly="readonly">起<br>
						<input type="text" class="ui-widget-content" id="search_qa_reception_time_end" readonly="readonly">止
					</td>
					
				</tr>
			</tbody></table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
					
					<!-- 当前操作者身份 -->
					<input type="hidden" id="judge_identity" value="${privacy}">
					<!-- 当前操作者ID-->
                	<input type="hidden" id="hidden_operator_id" value="${operator_id}">
			</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">保修期内返品分析对策</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<table id="list" ></table>
	<div id="listpager"></div>
	<div id="show_Accept"></div>
	<div class="clear areaencloser"></div>
</div>

<div id="functionarea" class="dwidth-full">
<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
<%
  /*如果当前操作人员是经理或者经理以上身份 显示确认按钮*/
  if(isManager){
%>
			<input type="button" id="comfirm_button" class="ui-button" value="确认" style="float:right;margin-right:8px;"/>	
<%
  }
%>
			<input type="button" id="importAnalysiaBookbutton" class="ui-button" value="分析书导出" style="float:right;margin-right:10px;"/>	
		</div>
</div>
	<div class="clear"></div>
	</div>
</div>
</div>
<div id="confirm_message"></div>
</body>
</html>