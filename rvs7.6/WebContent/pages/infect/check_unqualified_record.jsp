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
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/infect/check_unqualified_record.js"></script>

<title>设备工具・治具点检不合格记录</title>

<%
 String role=(String)request.getAttribute("role");//角色
%>
<script>
	$(function() {
		$("#accordion").accordion({active: 1});
	});
</script>
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
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具点检" />
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<!-- 一览开始 -->
				<div id="main">
					<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">检索条件</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					
					<div class="ui-widget-content">	
						<form id="searchform" method="POST">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">管理编号</td>
										<td class="td-content">
											<input type="text" id="search_manage_code" class="ui-widget-content">
										</td>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
											<input type="text" id="search_name" class="ui-widget-content" >
										</td>
										<td class="ui-state-default td-title">不合格项目</td>
										<td class="td-content">
											<input type="text" id="search_check_item" class="ui-widget-content" >
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">产品处理内容</td>
										<td class="td-content">
											<select id="search_product_content" class="ui-widget-content">${goProductContent }</select>
										</td>
										<td class="ui-state-default td-title">工位对处</td>
										<td class="td-content">
											<select id="search_position_handle" class="ui-widget-content">${goPositionHandle }</select>
										</td>
										<td class="ui-state-default td-title">设备对处</td>
										<td class="td-content">
											<select id="search_object_handle" class="ui-widget-content">${goObjectHandle }</select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">设备对处结果</td>
										<td class="td-content">
											<select id="search_object_final_handle_result" class="ui-widget-content">${goObjectFinalHandleResult }</select>
										</td>
										<td class="ui-state-default td-title">发生日期</td>
										<td class="td-content">
											<input type="text" id="search_happen_time_start" class="ui-widget-content"  readonly="readonly">起
											<input type="text" id="search_happen_time_end" class="ui-widget-content"  readonly="readonly">止
										</td>
										<td class="ui-state-default td-title">修理开始日期</td>
										<td class="td-content">
											<input type="text" id="search_repair_date_start_start" class="ui-widget-content" readonly="readonly">起
											<input type="text" id="search_repair_date_start_end" class="ui-widget-content" readonly="readonly">止
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
								<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
								<input type="hidden" id="sProductContent" value="${sProductContent}"><%--产品处理内容 --%>
								<input type="hidden" id="sPositionHandle" value="${sPositionHandle }"><%--工位对处 --%>
								<input type="hidden" id="sObjectHandle" value="${sObjectHandle }"><%--设备对处 --%>
								<input type="hidden" id="sObjectFinalHandleResult" value="${sObjectFinalHandleResult }"><%--设备对处结果 --%>
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div id="listarea" >
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">设备工具・治具点检不合格记录一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="check_unqualified_record_list" ></table>
						<div id="check_unqualified_record_listpager"></div>
					</div>
				</div>
				<!-- 一览结束 -->
				
				<!-- 详细信息开始 -->
				<div id="detail" style="width:994px;display:none">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">设备工具・治具点检不合格记录详细信息<label id="label_serial_num"></label></span>
						<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser" id="goback">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content">
						<form id="update_form" method="post">
							<table class="condform">
								<tbody>
									<tr>
										<td colspan="6">
											<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-left:-1px;margin-right:-1px;">
												<span>不合格描述</span>
											</div>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">管理编号</td>
										<td class="td-content">
											<label id="label_manage_code"></label>
										</td>
										<td class="ui-state-default td-title">品名</td>
										<td class="td-content">
											<label id="label_name"></label>
										</td>
										<td class="ui-state-default td-title">型号</td>
										<td class="td-content">
											<label id="label_model_name"></label>
										</td>							
									</tr>
									<tr>
										<td class="ui-state-default td-title"></td>
										<td class="td-content">
											<label id="label_borrow"></label>
										</td>
										<td class="ui-state-default td-title">责任人</td>
										<td class="td-content">
											<label id="label_responsible_operator_name"></label>
										</td>
										<td class="ui-state-default td-title">发生时间</td>
										<td class="td-content">
											<label id="label_happen_date"></label>
										</td>
									</tr>
									<tr>
										<td colspan="6">
											<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-left:-1px;margin-right:-1px;">
												<span>线长处理</span>
											</div>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">不合格项目</td>
										<td class="td-content" colspan="5">
											<label id="label_check_item"></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">不合格描述</td>
										<td class="td-content"  colspan="5">
											<textarea id="label_unqualified_status" disabled cols="130" rows="2" style="font-size:12px;resize:none"></textarea>
										</td>
									</tr>
									<tr id="sheetShower">
										<td class="ui-state-default td-title">点检表</td>
										<td class="td-content"  colspan="5">
											<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="lookbutton" value="查看" role="button" aria-disabled="false" style="float:left;width:50px;height:28px;line-height:15px;font-weight:normal;" type="button">
										</td>
									</tr>
									<!-- 线长未处理开始 -->
									<tr class="linear_not_handle" style="display:none">
										<td class="ui-state-default td-title">工位对处</td>
										<td class="td-content" colspan="5">
											<select id="edit_position_handle">${goPositionHandle2 }</select>
										</td>
									</tr>
									<tr class="linear_not_handle" id="borrow_device" style="display:none">
										<td class="ui-state-default td-title" >借用设备</td>
										<td class="td-content" colspan="5" >
											<input type="text" class="ui-widget-content" id="input_borrow_object_name">
											<input type="hidden" id="hidden_borrow_object_id" value="">
										</td>
									</tr>
									<!-- 线长未处理结束 -->
									
									<!-- 线长处理完  其他人   开始-->
									<tr class="linear_handle_over" style="display:none">
										<td class="ui-state-default td-title" >工位对处</td>
										<td class="td-content" id="optition_handle" colspan="5">
											<label id="label_position_handle"></label>
										</td>
									</tr>
									<tr class="linear_handle_over" style="display:none;" id="label_borrow_device">
										<td class="ui-state-default td-title" >借用设备</td>
										<td class="td-content" colspan="5" >
											<label id="label_borrow_object_name"></label>
										</td>
									</tr>
									<tr class="linear_handle_over" style="display:none">
										<td class="ui-state-default td-title">线长</td>
										<td class="td-content">
											<label id="label_line_leader_handle_name"></label>
										</td>
										<td class="ui-state-default td-title">处理时间</td>
										<td class="td-content">
											<label id="label_line_leader_handle_time"></label>
										</td>
									</tr>
									<!-- 线长处理完  其他人   结束-->
								
									<tr class="manage_title">
										<td colspan="6">
											<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-left:-1px;margin-right:-1px;">
												<span>经理处理</span>
											</div>
										</td>
									</tr>
									
									<%
										if("manage".equals(role)){//经理 未选择无影响
									%>
									<tr class="manage_not_handle" style="display:none;">
										<td class="ui-state-default td-title">设备对处</td>
										<td class="td-content" colspan="5">
											<select id="edit_object_handle">${goObjectHandle }</select>
										</td>
									</tr>
									<tr class="manage_not_handle" style="display:none;">
										<td class="ui-state-default td-title">产品处理内容</td>
										<td class="td-content" colspan="5">
											<select id="edit_product_handle" >${goProductContent }</select>
										</td>
									</tr>
									<tr class="manage_not_handle" style="display:none;">
										<td class="ui-state-default td-title">产品处理结果</td>
										<td class="td-content"  colspan="5"> 
											<textarea id="edit_product_result" style="resize:none;" cols="99"></textarea>
										</td>
									</tr>
									<%
										}									
									%>
									
									<%
										//经理选择无影响 设备管理员 其他人员
										if("manage".equals(role) || "dtTechnology".equals(role) || "other".equals(role)){
									%>
										<tr class="manage_handle" style="display:none">
											<td class="ui-state-default td-title">设备对处</td>
											<td id="device_handle" class="td-content ui-buttonset" colspan="5">
												<label id="label_object_handle"></label>
											</td>
										</tr>
										<tr class="manage_handle" style="display:none">
											<td class="ui-state-default td-title">产品处理内容</td>
											<td id="product_handle" class="td-content ui-buttonset" colspan="5">
												<label id="label_product_handle"></label>
											</td>
										</tr>
										<tr class="manage_handle" style="display:none">
											<td class="ui-state-default td-title">产品处理结果</td>
											<td class="td-content" colspan="5">
												<textarea id="label_product_result" cols="99" style="font-size:12px;resize:none;" disabled></textarea>
											</td>
										</tr>
										<tr class="manage_handle" style="display:none">
											<td class="ui-state-default td-title">经理</td>
											<td class="td-content">
												<label id="label_manager_name"></label>
											</td>
											<td class="ui-state-default td-title">处理时间</td>
											<td class="td-content">
												<label id="label_manager_handle_time"></label>
											</td>
										</tr>
									<%
										}
									%>
									
									<tr class="">
										<td colspan="6">
											<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser"style="margin-left:-1px;margin-right:-1px;">
												<span>设备管理员处理</span>
											</div>
										</td>
									</tr>
							
									<%
										//设备管理员
										if("dtTechnology".equals(role)){
									%>
									<tr>
										<td class="ui-state-default td-title">设备对处结果</td>
										<td class="td-content" colspan="5">
											<select id="edit_object_final_handle_result">${goObjectFinalHandleResult }</select>
										</td>
									</tr>
									<tr class="dtTechnology_handle" style="display:none">
										<td class="ui-state-default td-title">维修/校验/出借 开始日期</td>
										<td class="td-content" colspan="5">
											<input type="text" class="ui-widget-content" id="edit_repair_start_date" readonly="readonly">
										</td>
									</tr>
									<tr class="dtTechnology_handle" style="display:none">
										<td class="ui-state-default td-title">维修/校验/出借 结束日期</td>
										<td class="td-content"  colspan="5">
											<input type="text" class="ui-widget-content" id="edit_repair_end_date" readonly="readonly">
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">备注</td>
										<td class="td-content" colspan="5">
											<textarea id="edit_technology_comment" cols="99" style="font-size:12px;resize:none;"></textarea>
										</td>
									</tr>
									<%
										}else{
									%>
									
									<tr>
										<td class="ui-state-default td-title">设备对处结果</td>
										<td class="td-content" colspan="5">
											<label id="label_object_final_handle_result"></label>
										</td>
									</tr>
									<tr class="dtTechnology_result">
										<td class="ui-state-default td-title">维修/校验/出借 开始日期</td>
										<td class="td-content" colspan="5">
											<label id="label_repair_start_date"></label>
										</td>
									</tr>
									<tr class="dtTechnology_result">
										<td class="ui-state-default td-title">维修/校验/出借 结束日期</td>
										<td class="td-content"  colspan="5">
											<label id="label_repair_end_date"></label>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">备注</td>
										<td class="td-content" colspan="5">
											<textarea id="edit_technology_comment" cols="99" style="font-size:12px;resize:none;" readonly disabled></textarea>
										</td>
									</tr>
									<%
										}
									%>
									
									
								</tbody>
							</table>
							<div style="height:44px">
									<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all linear_not_handle" id="liner_comfirmbutton" value="确认" role="button" aria-disabled="false" style="float:left;left:4px" type="button">
									
									<%
										if("manage".equals(role)){//经理 产品处理内容没有选择无影响 
									%>
									<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all manage_not_handle" id="manage_comfirmbutton" value="确认" role="button" aria-disabled="false" style="float:left;left:4px" type="button">
									<%
										}
									%>
									<%
										if("dtTechnology".equals(role)){
									%>
									<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="technology_comfirmbutton" value="确认" role="button" aria-disabled="false" style="float:left;left:4px" type="button">
									<%
										}
									%>
									
								<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="reset2button" value="取消" role="button" aria-disabled="false" style="float:left;left:4px" type="button">
							</div>
						</form>
					</div>
						
										
					
				</div>
				<!-- 详细信息结束 -->
				
				<div id="name_refer" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
					 <table>
						<tbody>
						   <tr>
								<td width="50%">过滤字:<input type="text"></td>	
								<td align="right" width="50%"><input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" style="float:right;" value="清空" type="button"></td>
						   </tr>
					   </tbody>
				  	 </table>
				  	 <table class="subform"></table>
				</div>
				
			</div>
			
			<div class="clear areaencloser"></div>
			<div id="confirm_dialog"></div>
			
		</div>
	</div>
</body>
</html>