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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/infect/device_regularly_check_result.js"></script>
<title>设备工具定期点检</title>
</head>
<body class="outer">
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
							<tr>
								<td class="ui-state-default td-title">管理编号</td>
								<td class="td-content">
									<input id="search_manage_code" class="ui-widget-content" type="text">
								</td>
								<td class="ui-state-default td-title">品名</td>
								<td class="td-content">
									<input id="search_name" class="ui-widget-content" type="text">
									<input id="hidden_devices_type_id" type="hidden">
								</td>
								<td class="ui-state-default td-title">型号</td>
								<td class="td-content">
									<input type="text" id="search_model_name" class="ui-widget-content">
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">分发课室</td>
								<td class="td-content" >
									<select id="search_section_id">${sectionOptions }</select>
								</td>
								<td class="ui-state-default td-title">责任工程</td>
								<td class="td-content">
									<select id="search_responsible_line_id">${lineOptions }</select>
								</td>
								<td class="ui-state-default td-title">责任工位</td>
								<td class="td-content">
									<input type="text" id="search_responsible_position_name" class="ui-widget-content">
									<input type="hidden" id="hidden_responsible_position_id" >
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">责任人员</td>
								<td class="td-content">
									<input type="text" id="search_responsible_operator_name" maxlength="14" class="ui-widget-content">
									<input type="hidden" id="hidden_responsible_operator_id">
								</td>
								<td class="ui-state-default td-title"></td>
								<td class="td-content"></td>
								<td class="ui-state-default td-title"></td>
								<td class="td-content"></td>
							</tr>
						</table>
						<div style="height:44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
							<input type="hidden" id="sChecked_status" value="${sChecked_status }">
						</div>
					</form>
				</div>
			</div>
			
			<div class="clear areaencloser dwidth-full"></div>
		
			<div id="listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">设备工具定期点检一览</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
					<span id="show_week" style="float:right;margin: 0.3em 0.4em 0.2em 0.3em;">点检合格台数：<label></label>；点检不合格台：<label></label></span>
					<span id="show_month" style="float:right;margin: 0.3em 0.4em 0.2em 0.3em;display:none">点检合格台数：<label ></label>；点检不合格台数：<label></label></span>
					<span id="show_year" style="float:right;margin: 0.3em 0.4em 0.2em 0.3em;display:none">点检合格台数：<label></label>；点检不合格台数：<label></label></span>
				</div>
				<div>
					<div id="check_regularly" class="dwidth-full ui-widget-content">
					    <input type="radio" name="date" id="week"  value="1" class="ui-button ui-corner-up"  role="button"  checked="checked"><label for="week" aria-pressed="false">周</label>						
						<input type="radio" name="date" id="month" value="2" class="ui-button ui-corner-up" role="button"><label for="month" aria-pressed="false">月</label>
						<input type="radio" name="date" id="year" value="3" class="ui-button ui-corner-up" role="button"><label for="year" aria-pressed="false" >年</label>
					</div>
					
					<!--周-->
					<div id="check_week"">
						<table id="week_list" ></table>
						<div id="week_listpager"></div>
					</div>
					<!--月-->
					<div id="check_month" style="display:none;">
						<table id="month_list" ></table>
						<div id="month_listpager"></div>
					</div>
					<!--年-->
					<div id="check_year" style="display:none;">
						<table id="year_list" ></table>
						<div id="year_listpager"></div>
					</div>
				</div>
			</div>
			
			<div id="show_detail" class="ui-widget-content" style="display:none">
				<div class="ui-widget-content">
					<form id="" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">管理编号</td>
									<td class="td-content">
										<label id="label_manage_code"></label>
									</td>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<label id="label_section_name"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">品名</td>
									<td class="td-content">
										<label id="label_name"></label>
									</td>
									<td class="ui-state-default td-title">工程</td>
									<td class="td-content">
										<label id="label_responsible_line_name"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<label id="label_model_name"></label>
									</td>
									<td class="ui-state-default td-title">工位</td>
									<td class="td-content">
										<label id="label_responsible_position_name"></label>
									</td>
								</tr>
							</tbody>
						</table>
					</form>
				</div>
				<table id="detail_list" ></table>
				<div id="detail_listpager"></div>
			</div>
			
			<div class="clear areaencloser dwidth-full"></div>
			
			<div id="position_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
				 <table>
					<tbody>
					   <tr>
							<td width="50%">过滤字:<input type="text"></td>	
							<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
					   </tr>
				   </tbody>
			  	 </table>
			  	 <table class="subform">${pReferChooser }</table>
			</div>
			
			<div id="responsible_operator_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
				 <table>
					<tbody>
					   <tr>
							<td width="50%">过滤字:<input type="text"></td>	
							<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
					   </tr>
				   </tbody>
			  	 </table>
			  	 <table class="subform">${rReferChooser }</table>
			</div>
			
			<div id="name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
				 <table>
					<tbody>
					   <tr>
							<td width="50%">过滤字:<input type="text"></td>	
							<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
					   </tr>
				   </tbody>
			  	 </table>
			  	 <table class="subform">${nReferChooser }</table>
			</div>
			
		</div>
	</div>
</body>
</html>