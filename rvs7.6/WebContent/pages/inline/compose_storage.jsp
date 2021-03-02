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
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.flowchart.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/inline/compose_stroage.js"></script>
<title>总组签收管理</title>
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
					<jsp:param name="linkto" value="在线作业"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">检索条件</span>
					   <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					   		<span class="ui-icon ui-icon-circle-triangle-n" id="toggle"></span>
					   </a>
				</div>
				
				<div class="ui-widget-content" id="main">
					<form id="searchform" method="POST">
						<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">维修对象机种</td>
								
								<td class="td-content" colspan="3"><select name="category_id" id="search_category_id" class="ui-widget-content">${cOptions}</select>
								</td>
								<td class="ui-state-default td-title">总组分线</td>
								<td class="td-content"><select name="px" id="search_px" class="ui-widget-content">${pxOptions}</select>
								</td>
								
							</tr>
							<tr>
							    <td class="ui-state-default td-title">修理单号</td>
							    
								<td class="td-content"><input type="text" id="search_sorcno" maxlength="15" class="ui-widget-content">
								</td>
								<td class="ui-state-default td-title" rowspan="2">来源</td>
								<td class="td-content" rowspan="2">
									<select name="line_id" id="search_line_id" class="ui-widget-content">
									<option value=""></option>
									<option value="00000000012">分解</option>
									<option value="00000000013">组件</option>
									</select>
								</td-->
								
								<!--td class="ui-state-default td-title" rowspan="2">维修课室</td>
								
								<td class="td-content" rowspan="2"><select name="section_id" id="search_section_id" class="ui-widget-content">${sOptions}</select>
								</td-->
								
								<td class="ui-state-default td-title" rowspan="2">纳期</td>
								<td class="td-content" rowspan="2"><input type="text" name="name" id="search_scheduled_date_start" class="ui-widget-content" readonly="readonly" value="">起<br/>
													   <input type="text" name="name" id="search_scheduled_date_end" class="ui-widget-content" readonly="readonly">止
								</td>
							</tr>
							<tr>
								
								<td class="ui-state-default td-title">机身号</td>
								
								<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">入库预定日</td>
								<td class="td-content"><input type="text" name="name" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly" value="">起<br/>
													   <input type="text" name="name" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
								</td>
								
								<td class="ui-state-default td-title">零件BO</td>
								<td class="td-content" >
									<div id="cond_work_procedure_order_template_flg_set" class="ui-buttonset">
										<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">(全)</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_f" class="ui-widget-content ui-helper-hidden-accessible" value="-1"><label for="cond_work_procedure_order_template_f" aria-pressed="false">无BO</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_work_procedure_order_template_t" aria-pressed="false">有BO</label>
										<input type="hidden" id="cond_work_procedure_order_template">
									</div>
								</td>
								
								<td class="ui-state-default td-title">总组出货安排</td>
								<td class="td-content"><input type="text" name="name" id="search_com_scheduled_date_start" maxlength="50" class="ui-widget-content" readonly="readonly" value="">起<br/>
													   <input type="text" name="name" id="search_com_scheduled_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
								</td>
							</tr>
						</tbody>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="hidden" id="pxGridOptions" value="${pxGridOptions}">
						</div>
					</form>
				</div>
				
				<div id="exd_listarea" class="width-middleright" style="margin-top:36px;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">总组库位管理</span>
						<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					
					<table id="exd_list"></table>
					<div id="exd_listpager"></div>
					<!-- div class="ui-widget-content dwidth-middleright" id="scanner_container">
						<div class="ui-state-default td-title">扫描录入区域</div>
						<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width: 986px;"></input>
						<div style="text-align: center;">
								<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
						</div>
					</div-->
				</div>
				
			</div>
			<div class="clear"></div>
		</div>
		<div class="clear areaencloser dwidth-middle"></div>
		<div id="com_pop"></div>
	</div>
</body>
</html>