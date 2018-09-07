<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
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

<style>
.width-quotation {
	width: 604px;
	margin:auto;
}
.dwidth-quotation {
	width: 603px;
	margin:auto;
}
#waitings .waiting {
	width: 340px;
}
.click_start {
	position: absolute;
	right: -36px;
	bottom: -10px;
	display: none;
}
 .waitForStart tr:hover .click_start{
 	position: static;
 	padding: 1px 12px;
 	margin-left: 2px;
 }
.waitForStart .waiting:hover .click_start, .waitForStart tr:hover .click_start{
	display: block;
}
#waitings .click_start input {
	padding: 5px;
}
#waitings .waiting {
	position: relative;
}
#mention {
width: 218px;
border: 1px solid rgb(147, 195, 205);
position: absolute;
right: 6px;
top: 2px;
background-color:white;
}
#mention:empty {
display:none;
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
   margin-left:120px;
   text-align:center;
   font-size:22px;
   font-weight:bold;
   border:1px solid #000000;
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
</style>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/qa/service_repair_referee.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>

<title>保修期内返品+QIS品判定</title>
</head>
<body class="outer">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="品保作业"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 1022px; float: left;">
				<div class="dwidth-middleright" style="margin-left: 8px;">
		<div id="uld_listarea">
			<table id="uld_list"></table>
			<div id="uld_listpager"></div>
			<div id="uld_listedit"></div>
		</div>
		<div class="ui-widget-content areabase dwidth-middleright">
			<div id="executes" style="margin-left: 4px; margin-top: 4px;">
				<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
			</div>
		</div>

		<div class="ui-widget-content">
		<div id="waitarea" style="float: left;width: 386px;min-height: 250px;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">暂停区域</span>
			</div>
			<div id="waitings"></div>
		</div>
		<div id="executearea" class="width-quotation" style="float: left;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser width-quotation">
				<span class="areatitle">判定处理</span>
			</div>
			<div class="ui-widget-content dwidth-quotation">
				<div class="ui-widget-content dwidth-quotation" id="scanner_container">
					<div class="ui-state-default td-title">扫描录入区域</div>
					<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width: 597px;"></input>
					<div style="text-align: center;">
						<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
					</div>
				</div>
	<div class="ui-widget-content dwidth-quotation" id="material_details" style="display:none;">
		<form id="ins_serviceRepairManage" style="position: relative;">
		<input type="hidden" id="material_id">
		<table class="condform">
			<tbody>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="add_model_name" ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label  id="add_serial_no" ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content">
						<label id="add_sorc_no" ></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">产品分类</td>
					<td class="td-content">
						<select id="add_kind" name="kind" alt="产品分类"class="ui-widget-content">${sKind}</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">类别</td>
					<td class="td-content">
						<select id="add_service_repair_flg" name="service_repair_flg" alt="类别"class="ui-widget-content">${sQaMaterialServiceRepair}</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">RC邮件发送日</td>
					<td class="td-content">
						<input name="rc_mailsend_date" alt="RC邮件发送日" readonly="readonly" id="add_rc_mailsend_date" type="text" class="ui-widget-content"></input>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">实物收到日</td>
					<td class="td-content">
					<input  name="rc_ship_assign_date" alt="实物收到日" readonly="readonly" id="add_rc_ship_assign_date" type="text" class="ui-widget-content"></input>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">SORC受理日</td>
				    <td class="td-content">
					<label id="edit_label_reception_date" alt="SORC受理日"></label>
				</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">等级</td>
				    <td class="td-content">
					<input name="rank" alt="等级" style="border:none;" id="add_rank" type="text" class="ui-widget-content"></input>
				</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">有无偿</td>
				    <td class="td-content">
					<select id="add_search_service_free_flg" name="service_free_flg" alt="有无偿" class="ui-widget-content">${sServiceFreeFlg}</select>
				</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修站</td>
				    <td class="td-content">
					<select id="add_workshop"  name="workshop" alt="维修站"class="ui-widget-content">${sWorkshop}</select>
				</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">处理对策</td>
				    <td class="td-content">
					<textarea name="countermeasures" alt="处理对策" id="add_countermeasures"  cols=35 rows=2 class="ui-widget-content"></textarea>
				</td>
				</tr>
				<tr>
				    <td class="ui-state-default td-title">QIS发送单号</td>
				    <td class="td-content">
						<input id="add_qis_invoice_no" type="text" name="qis_invoice_no" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">QIS发送日期</td>
				    <td class="td-content">
						<input id="add_qis_invoice_date" type="text" name="qis_invoice_date" readonly="readonly"class="ui-widget-content"/>
					</td>
				</tr>				
				<tr><!-- class="qis_payout" -->
					<td class="ui-state-default td-title">质量信息单号</td>
					<td class="td-content">
						<input id="add_quality_info_no"name="quality_info_no"  alt="质量信息单号"class="ui-widget-content" type="type"  />
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">ETQ单号</td>
				    <td class="td-content">
						<input id="add_etq_no" type="text" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">质量判定</td>
				    <td class="td-content">
						<select id="add_quality_judgment" >${sQuality_judgment}</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">发行QIS</td>
				    <td class="td-content">
						<select id="add_qis_isuse" >${sQis_isuse}</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
				    <td class="td-content">
					<textarea  name="comment"  alt="备注" id="add_comment"  cols=35 rows=2 class="ui-widget-content"></textarea>
				</td>
				</tr>           
			</tbody>
						
		</table>
	        <input type="hidden" id="hidden_service_repair_flg" value="${goQaMaterialServiceRepair}"/>
			<input type="hidden" id="hidden_workshop" value="${goQaMaterialServiceRepair}"/>
			
		<div id="mention"></div>
		<div id="qis_payout"  style="display:none;">
		  <label id="quality_info_no"></label>
		  <label id="qis_invoice_no"></label>
		   <label id="qis_invoice_date"></label>
		  <label id="include_month"></label>
		   <label id="charge_amount"></label>
		</div>
		<div>
		      <input type="button" id="refereeCompletebutton"  style="float: right; right: 2px" class="ui-button" value="判定完成"/>
              <input type="button" id="break_button"  style="float: right; right: 2px"class="ui-button" value="正常中断"/>
              <input type="button" id="pause_button" style="float: right; right: 2px" class="ui-button" value="暂停"/>
              <input type="button" id="endpause_button" style="float: right; right: 2px;display:none;" class="ui-button" value="再开"/>
              <!-- input aria-disabled="false" role="button" id="analysis_button" style="float: right; right: 2px" class="ui-button ui-widget ui-state-default ui-corner-all" value="分析(未完成)" type="button" -->
		</div>
			<div class="clear"></div>
		 
		  
	</form>
	</div>			
	</div>			
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
		</div>
	</div>
		</div>
		<div class="clear areaencloser"></div>
	</div>
	<div id="break_dialog" style="display:none;"></div>
	<div id='show_Accept'></div>
	</div>

</body>
</html>