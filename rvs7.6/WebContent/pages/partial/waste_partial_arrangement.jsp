<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/lte-style.css">
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
<script type="text/javascript" src="js/partial/waste_partial_arrangement.js"></script>
<title>废弃零件回收追溯</title>
</head>
<body class="outer">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=init" flush="true">
					<jsp:param name="linkto" value="现品管理"/>
				</jsp:include>
			</div>
			
			<div style="float: left;">
				<div id="body-mdl" class="dwidth-middleright">
					<div style="border-bottom: 0;" class="ui-widget-content">
						<div id="infoes" class="ui-buttonset">
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_arrangement_tab" value="page_arrangement">
							<label for="page_arrangement_tab">废弃零件整理记录</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_case_tab" value="page_case">
							<label for="page_case_tab">废弃零件回收箱</label>
						</div>
					</div>
					
					<div id="page_arrangement" class="record_page">
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">检索条件</span>
							<a target="_parent" role="link" href="javascript:void(0)" class="areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<div class="ui-widget-content">
							<form method="POST" id="searcharrform">
								<table class="condform">
									<tbody>
										<tr>
											<td class="ui-state-default td-title">修理单号</td>
											<td class="td-content">
												<input type="text" id="search_arr_omr_notifi_no" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title">型号</td>
											<td class="td-content">
												<input type="text" id="search_arr_model_name" class="ui-widget-content">
												<input type="hidden" id="hidden_model_id">
											</td>
											<td class="ui-state-default td-title">机身号</td>
											<td class="td-content">
												<input type="text" id="search_arr_serial_no" class="ui-widget-content">
											</td>
										</tr>
										<tr>
											<td class="ui-state-default td-title">收集日期</td>
											<td class="td-content">
												<input type="text" id="search_arr_collect_time_start" class="ui-widget-content" value="${default_collect_time_start}" readonly="readonly">起<br>
												<input type="text" id="search_arr_collect_time_end" class="ui-widget-content" readonly="readonly">止
											</td>
											<td class="ui-state-default td-title">装箱编号</td>
											<td class="td-content">
												<input type="text" id="search_arr_case_code" class="ui-widget-content">
											</td>
											<td class="ui-state-default td-title"></td>
											<td class="td-content">
											</td>
										</tr>
									</tbody>
								</table>
								<div style="height:44px;">
									<input class="ui-button" id="arr_resetbutton" value="清除" style="float:right;right:2px" type="button">
									<input class="ui-button" id="arr_searchbutton" value="检索" style="float:right;right:2px" type="button">
									<input type="hidden" id="arr_isleader" value="${isleader}">
								</div>
							</form>
						</div>
						
						<div class="areaencloser"></div>
						
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">废弃零件整理记录一览</span>
							<a target="_parent" role="link" href="javascript:void(0)" class="areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
				
						<table id="arrangementlist"></table>
						<div id="arrangementlistpager"></div>
					</div>
					<div id="page_case" style="display: none;" class="record_page">
						
						<div id="search_case_area">
							<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
								<span class="areatitle">检索条件</span>
								<a target="_parent" role="link" href="javascript:void(0)" class="areacloser">
									<span class="ui-icon ui-icon-circle-triangle-n"></span>
								</a>
							</div>
							<div class="ui-widget-content">
								<form method="POST" id="searchcaseform">
									<table class="condform">
										<tbody>
											<tr>
												<td class="ui-state-default td-title">装箱编号</td>
												<td class="td-content">
													<input type="text" id="search_case_case_code" class="ui-widget-content">
												</td>
												<td class="ui-state-default td-title">用途种类</td>
												<td class="td-content" id="search_case_collect_kind">
													<input type="radio" name="collect_kind" id="add_collect_kind_all" value="" class="ui-widget-content" checked/><label for="add_collect_kind_all">(全部)</label>
													<input type="radio" name="collect_kind" id="add_collect_kind_endo" value="1" class="ui-widget-content" /><label for="add_collect_kind_endo">内窥镜</label>
													<input type="radio" name="collect_kind" id="add_collect_kind_perl" value="2" class="ui-widget-content" /><label for="add_collect_kind_perl">周边设备</label>
												</td>
												<td class="ui-state-default td-title">打包日期</td>
												<td class="td-content" >
													<input type="text" id="search_case_package_date_start" class="ui-widget-content" readonly="readonly">起<br>
													<input type="text" id="search_case_package_date_end" class="ui-widget-content" readonly="readonly">止
												</td>
											</tr>
											<tr>
												<td class="ui-state-default td-title">废弃申请日期</td>
												<td class="td-content" >
													<input type="text" id="search_case_waste_apply_date_start" class="ui-widget-content" readonly="readonly">起<br>
													<input type="text" id="search_case_waste_apply_date_end" class="ui-widget-content" readonly="readonly">止
												</td>
												<td class="ui-state-default td-title">应用状态</td>
												<td class="td-content" id="search_use_state" colspan="3">
													<input type="radio" name="use_state" id="search_use_state_all" value="" class="ui-widget-content" checked/><label for="search_use_state_all">(全部)</label>
													<input type="radio" name="use_state" id="search_use_state_collecting" value="1" class="ui-widget-content" /><label for="search_use_state_collecting">收集中</label>
													<input type="radio" name="use_state" id="search_use_state_wait_to_apply" value="2" class="ui-widget-content" /><label for="search_use_state_wait_to_apply">未申请废弃</label>
													<input type="radio" name="use_state" id="search_use_state_applied" value="3" class="ui-widget-content" /><label for="search_use_state_applied">已申请废弃</label>
												</td>
											</tr>
										</tbody>
									</table>
									<div style="height:44px;">
										<input class="ui-button" id="case_resetbutton" value="清除" style="float:right;right:2px" type="button">
										<input class="ui-button" id="case_searchbutton" value="检索" style="float:right;right:2px" type="button">
									</div>
								</form>
							</div>
							
							<div class="areaencloser"></div>
							
							<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
								<span class="areatitle">废弃零件回收箱一览</span>
								<a target="_parent" role="link" href="javascript:void(0)" class="areacloser">
									<span class="ui-icon ui-icon-circle-triangle-n"></span>
								</a>
							</div>
							
							<table id="caselist"></table>
							<div id="caselistpager"></div>
							<div class="ui-widget-header areabase"style="width:992px;padding-top:4px;margin-top:0px;">
							    <div id="executes" style="margin-left:4px;margin-top:2px;">
									<input type="button" id="waste_apply_button" class="ui-button"  value="废弃申请">
								</div>
							</div>
						</div>
						
						<div id="update_case_area" style="display: none;">
							<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
								<span class="areatitle">更新回收箱</span>
								<a target="_parent" role="link" href="javascript:void(0)" class="areacloser">
									<span class="ui-icon ui-icon-circle-triangle-w"></span>
								</a>
							</div>
							<div class="ui-widget-content">
								<form method="POST" id="updateform">
									<table class="condform">
										<tbody>
											<tr>
												<td class="ui-state-default td-title">装箱编号</td>
												<td class="td-content">
													<input type="text" id="update_case_code" class="ui-widget-content">
												</td>
											</tr>
											<tr>
												<td class="ui-state-default td-title">用途种类</td>
												<td class="td-content">
													<label id="label_collect_kind_name"></label>
												</td>
											</tr>
											<tr>
												<td class="ui-state-default td-title">重量</td>
												<td class="td-content">
													<input type="text" id="update_weight" class="ui-widget-content">(kg)
												</td>
											</tr>
											<tr>
												<td class="ui-state-default td-title">备注</td>
												<td class="td-content">
													<textarea rows="1" cols="50" id="update_comment" style="resize:none;"></textarea>
												</td>
											</tr>
										</tbody>
									</table>
									<div style="height:36px;margin-top:5px;">
										<input type="button" class="ui-button" id="canclecasebutton" value="取消" style="float:right;right:4px;">
										<input type="button" class="ui-button" id="updatecasebutton" value="更新" style="float:right;right:4px;">
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="clear areaencloser"></div>
			
			<div id="waste_dialog" style="display: none;">
				<form method="POST">
					<table class="condform">
						<tr>
							<td class="ui-state-default td-title">重量</td>
							<td class="td-content">
								<input type="text" id="waste_update_weight" class="ui-widget-content">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">废弃申请日期</td>
							<td class="td-content">
								<input type="text" id="waste_update_waste_apply_date" class="ui-widget-content" readonly="readonly">
							</td>
						</tr>
					</table>
				</form>
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
	</div>
</body>
</html>