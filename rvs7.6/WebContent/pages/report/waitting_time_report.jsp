<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/highcharts4.js"></script>
<script type="text/javascript" src="js/exporting.js"></script>
<script type="text/javascript" src="js/report/waitting_time_report.js"></script>

<title>等待时间、中断时间统计</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="文档管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">检索条件</span>
					<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<div class="ui-widget-content">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">机种</td>
									<td class="td-content" colspan="5">
										<select id="search_category_id" class="ui-widget-content">${sCategory}</select>
										<input type="hidden" id="hidden_category_name">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content">
										<input type="text" id="search_omr_notifi_no" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content">
										<input type="text" id="search_serial_no" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input type="text" id="search_model_name" class="ui-widget-content"  readonly>
										<input type="hidden" id="hidden_model_id">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<select id="search_level" class="ui-widget-content">${sLevel}</select>
									</td>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select id="search_section_id" class="ui-widget-content">${sSection}</select>
										<input type="hidden" id="search_section_name">
									</td>
									<td class="ui-state-default td-title">零件BO</td>
									<td class="td-content">
										<select id="search_bo_flg" class="ui-widget-content">${sBoflg}</select></td>
									</tr>
								<tr>
									<td class="ui-state-default td-title">加急</td>
									<td class="td-content" id="search_scheduled_expedited">
										<input type="radio" name="scheduled_expedited" id="scheduled_expedited_all" class="ui-widget-content" value="0"><label for="scheduled_expedited_all">全部</label>
										<input type="radio" name="scheduled_expedited" id="scheduled_expedited_y" class="ui-widget-content" value="1"><label for="scheduled_expedited_y">是</label>
										<input type="radio" name="scheduled_expedited" id="scheduled_expedited_n" class="ui-widget-content" value="2"><label for="scheduled_expedited_n">否</label>
									</td>
									<td class="ui-state-default td-title">是否包含返工</td>
									<td class="td-content" id="search_rework">
										<input type="radio" name="rework" id="rework_y" class="ui-widget-content" value="1" checked="checked"><label for="rework_y">是</label>
										<input type="radio" name="rework" id="rework_n" class="ui-widget-content" value="2"><label for="rework_n">否</label>
									</td>
									<td class="ui-state-default td-title">直送</td>
									<td class="td-content">
										<select id="search_direct_flg" class="ui-widget-content">${sDirect}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">完成时间</td>
									<td class="td-content">
										<input type="text" id="search_outline_time_start" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_outline_time_end" class="ui-widget-content" readonly>止
									</td>
									<td class="ui-state-default td-title">零件订购日</td>
									<td class="td-content">
										<input type="text" id="search_order_date_start" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_order_date_end" class="ui-widget-content" readonly>止
									</td>
									<td class="ui-state-default td-title">零件发放日</td>
									<td class="td-content">
										<input type="text" id="search_arrival_date_start" class="ui-widget-content" readonly>起<br/>
										<input type="text" id="search_arrival_date_end" class="ui-widget-content" readonly>止
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">分解工程分线</td>
									<td class="td-content">
										<select id="search_dec_px" class="ui-widget-content">${px}</select>
									</td>
									<td class="ui-state-default td-title">NS工程分线</td>
									<td class="td-content">
										<select id="search_ns_px" class="ui-widget-content">${px}</select>
									</td>
									<td class="ui-state-default td-title">总组工程分线</td>
									<td class="td-content">
										<select id="search_com_px" class="ui-widget-content">${mpx}</select>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px;">
							<input class="ui-button" id="resetbutton" value="清除" style="float:right;right:2px" type="button">
							<input class="ui-button" id="searchbutton" value="分析" style="float:right;right:2px" type="button">
						</div>
					</form>
				</div>
				
				<div class="clear areaencloser"></div>
				
				<div id="listarea">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">等待时间、中断时间统计</span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<div>
						<div id="container1" class="ui-widget-content" style="height: 300px;width:780px;float:left;"></div>
						<div id="container2" class="ui-widget-content" style="height: 300px;width:200px;float:right;"></div>
						<div class="clear"></div>
						<div id="container3" class="ui-widget-content" style="height: 300px;width:780px;float:left;margin-top:10px;"></div>
						<div id="container4" class="ui-widget-content" style="height: 300px;width:200px;float:right;margin-top:10px;"></div>
						<div class="clear"></div>
					</div>
				</div>
				
				<div class="ui-widget-header ui-helper-clearfix areabase" style="padding-top:4px;">
					<!--input id="boldbutton" class="ui-button" value="BOLD 统计列表" style="float:right;right:2px" type="button"-->
					<input id="exportbutton" class="ui-button" value="等待/中断时间图表" style="float:right;right:2px" type="button">
				</div>
				
				<div id="search_model_id_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input class="ui-button" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform">${mReferChooser}</table>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>