<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
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
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>

<script type="text/javascript" src="js/admin/devices_manage.js"></script>
<script type="text/javascript" src="js/equipment/device_backup_detail.js"></script>
	<style>


	#accordion a.processing:before {
		color:red;
	}

	.referchooser table tr td:first-child{
		display:none;
	}
	</style>
<title>设备工具管理</title>
</head>
<body class="outer" style="overflow: auto;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具清单"/>
				</jsp:include>
			</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
					<div id="body-mdl" style="width: 994px; float: left;">
			<div id="searcharea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">设备・工具检索条件</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
	        </div>
	        
	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">管理编号</td>
						<td class="td-content">
							<input id="search_manage_code" class="ui-widget-content" type="text">
						</td>
						<td class="ui-state-default td-title">品名</td>
						<td class="td-content">
							<input type="text" id="search_name" name="devices_type_id" readonly="readonly" class="ui-widget-content">
							<input type="hidden" id="hidden_search_name">
						</td>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<input type="text" id="search_model_name" class="ui-widget-content">
						</td>
					</tr>
					<tr>
					    <td class="ui-state-default td-title">管理员</td>
						<td class="td-content">
							<input type="text" id="search_manager_operator_id"  readonly="readonly" class="ui-widget-content">
							<input type="hidden" id="hidden_search_manager_operator_id">
						</td>	
					    <td class="ui-state-default td-title">管理等级</td>
						<td class="td-content">	
						 <select id="search_manage_level">${manageLevel}</select>
						</td>
						<td class="ui-state-default td-title">状态</td>
						<td class="td-content">
							<select id="search_status" multiple>${status}</select>
						</td>					
					</tr>
					<tr>						
						<td class="ui-state-default td-title">分发课室</td>
						<td class="td-content" colspan="3">
							<select id="search_section_id">${sectionOptions}</select>
						</td>
						<td class="ui-state-default td-title">责任工位</td>
						<td class="td-content">
							<input type="text" id="search_position_id" readonly="readonly"  class="ui-widget-content">
							<input type="hidden" id="hidden_search_position_id">
						</td>
					</tr>
					<tr>						
						<td class="ui-state-default td-title">责任工程</td>
						<td class="td-content" colspan="3">
							<select id="search_line_id">${lineOptions}</select>
						</td>
						<td class="ui-state-default td-title">备注</td>
						<td class="td-content">
							<input type="text" id="search_comment" class="ui-widget-content">
						</td>
					</tr>
					
				</tbody>
			</table>
			<div style="height:44px">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
			</div>
		</form>
	</div>
</div>

					<style>
						#list td[aria-describedby="list_backup_evaluation"],
						.evaluation_tag,
						#set_evaluation
						{
							font-size: 16px;
						}
					</style>

	<div class="clear areaencloser"></div>
	
	<div id="listarea" class="">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">设备工具管理一览</span>
			<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>
		<div class="ui-widget-content" style="padding:4px;">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建设备工具" role="button" aria-disabled="false">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="replacebutton" value="替换新品" role="button" aria-disabled="false">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="deliverbutton" value="批量交付" role="button" aria-disabled="false">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="backupreportbutton" value="导出替代关系表" role="button" aria-disabled="false" style="float:right; margin-right:2px;">
		</div>
		<table id="list"></table>
		<div id="listpager"></div>
		<input type="hidden" id="hidden_goManage_level" value="${goManageLevel}">
		<input type="hidden" id="hidden_goStatus" value="${goStatus}">
	</div>
	<div class="clear"></div>
</div>
<!--检索结束-->

<!--新建设备工具开始-->
<div id="body-regist" style="width: 994px; float: left;display:none" class="ui-widget-content">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">新建设备工具</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	
	<form id="add_form" method="POST">	   
	    <div>
		<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
			<tbody>	
			    <tr>
					<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
					<td colspan="3">
						<input type="text" id="add_manage_code" name="manage_code" alt="管理编号"  class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">品名</td>
					<td>
						<input type="text" id="add_name" name="devices_type_id" readonly="readonly" alt="品名" class="ui-widget-content">
						<input type="hidden" id="hidden_add_name">				
					</td>
					<td class="ui-state-default td-title">型号</td>
					<td>
						<input type="text" id="add_model_name" name="model_name" alt="型号名称" class="ui-widget-content">
						<input id="hidden_add_model_id" type="hidden">
					</td>	
				</tr>
				<tr>
					<td class="ui-state-default td-title">管理员</td>
					<td>
						<input type="text" id="add_manager" name="manager" alt="管理员" readonly="readonly" class="ui-widget-content">	
						<input type="hidden" id="hidden_add_manager_operator_id">					
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">管理等级</td>
					<td class="td-content">	
					<select id="add_manage_level" name="manage_level" alt="管理等级"></select>
					</td>
					<td class="ui-state-default td-title">状态</td>
					<td  class="td-content">
						<select id="add_status" name="status" alt="状态">${status}</select>				
					</td>
					<!-- <td class="ui-state-default td-title">管理内容</td>
					<td>
						<input type="text" id="add_manage_content" name="manage_content" alt="管理内容" class="ui-widget-content">						
					</td> -->
				</tr>	
				<tr>
					<td class="ui-state-default td-title">出厂编号</td>
					<td>
						<input type="text" id="add_products_code" name="products_code" alt="出厂编号" class="ui-widget-content">						
					</td>
					<td class="ui-state-default td-title">厂商</td>
					<td>
						<input type="text" id="add_brand" name="brand" alt="厂商" class="ui-widget-content">						
					</td>
				</tr>	
				<tr>
					<td class="ui-state-default td-title">分发课室</td>
					<td style="width:200px;">
						<select id="add_section_id" name="section_id" alt="分发课室"></select>					
					</td>
					<td class="ui-state-default td-title">责任工程</td>
					<td style="width:100px;">
						<select id="add_line_id"></select>						
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任工位</td>
					<td>
						<input type="text" id="add_position_id" name="position_id" alt="责任工位"  readonly="readonly"  class="ui-widget-content">		
						<input name="position_id" id="hidden_add_position_id" type="hidden">
					</td>
					<td class="ui-state-default td-title">发放者</td>
					<td>
						<label id="add_provider"></label>
					</td>
					<!-- <td class="ui-state-default td-title">责任人员</td>
					<td>
						<input type="text" id="add_responsible_operator_id" name="responsible_operator_id" alt="责任人" readonly="readonly"  class="ui-widget-content">		
					    <input type="hidden" id="hidden_add_responsible_operator_id">
					</td> -->
				</tr>
				<tr>
					<td class="ui-state-default td-title">导入日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="import_date" alt="导入日期" id="add_import_date" readonly="readonly">
					    <input type="hidden" id="hidden_import_date"/>
					</td>		
					 <td class="ui-state-default td-title">发放日期</td>
					<td>
						<!-- <input type="text" class="ui-widget-content" name="provide_date" alt="发放日期" id="add_provide_date" readonly="readonly"> -->
						<label id="add_provide_date"></label>
					</td>	
				</tr>
				<tr>
				    <td class="ui-state-default td-title">废弃日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="waste_date" alt="废弃日期" id="add_waste_date" readonly="readonly">
					</td>	
					 <td class="ui-state-default td-title">更新时间</td>
					<td>
						<label id="add_updated_time"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3">
						<textarea  style="resize: none;font-size: 12px; width: 502px; height: 61px;" id="add_comment" name="comment" alt="备注"></textarea>				
					</td>
				</tr>
			</tbody>
		</table>
	    </div>	
		
		<div style="height:44px">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirebutton" value="确认" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
			<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="goback" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
		</div>
	</form>
</div>
<!--新建设备工具结束-->

<!--双击一栏画面开始-->
<div id="body-detail" style="width: 994px; float: left;display:none;" class="ui-widget-content">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">设备工具详细信息</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<form id="update_form" method="POST">	
	<div class="" id="main" >
		<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
			<tbody>	
			    <tr>
					<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
					<td>
						<input type="text" id="update_manage_code" name="manage_code" alt="管理编号" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">品名</td>
					<td>
						<input type="text" id="update_name" name="devices_type_id" alt="品名" readonly="readonly" class="ui-widget-content">
						<input type="hidden" id="hidden_update_name">				
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td>
						<input type="text" id="update_model_name" name="model_name" alt="型号" class="ui-widget-content">
					</td>	
					<td class="ui-state-default td-title">管理员</td>
					<td>
						<input type="text" id="update_manager" name="manager" alt="管理员" readonly="readonly"  class="ui-widget-content">
						<input type="hidden" id="hidden_update_manager_operator_id">						
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">管理等级</td>
					<td class="td-content">	
					<select id="update_manage_level" name="manage_level" alt="管理等级">${nCmanageLevel}</select>
					</td>
					<!-- <td class="ui-state-default td-title">管理内容</td>
					<td>
						<input type="text" id="update_manage_content" name="manage_content" alt="管理内容" class="ui-widget-content">						
					</td> -->
					<td class="ui-state-default td-title">状态</td>
					<td  class="td-content">
						<select id="update_status" name="status" alt="状态">${nCstatus}</select>				
					</td>
				</tr>	
				<tr>
					<td class="ui-state-default td-title">出厂编号</td>
					<td>
						<input type="text" id="update_products_code" name="products_code" alt="出厂编号" class="ui-widget-content">						
					</td>
					<td class="ui-state-default td-title">厂商</td>
					<td>
						<input type="text" id="update_brand" name="brand" alt="厂商" class="ui-widget-content">						
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分发课室</td>
					<td style="width:200px;">
						<select id="update_section_id" name="section_id" alt="分发课室"></select>
					</td>
					<td class="ui-state-default td-title">责任工程</td>
					<td style="width:100px;">
						<select id="update_line_id" name="line_id" alt="责任工程"></select>						
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任工位</td>
					<td>
						<input type="text" id="update_position_id" name="position_id" readonly="readonly"  alt="责任工位" class="ui-widget-content">		
						<input type="hidden" id="hidden_update_position_id">
					</td>
					<td class="ui-state-default td-title">发放者</td>
					<td>
						<label id="update_provider"></label>
					</td>
					<!-- <td class="ui-state-default td-title">责任人员</td>
					<td>
						<input type="text" id="update_responsible_operator_id" name="responsible_operator_id" alt="责任人员"  readonly="readonly"  class="ui-widget-content">		
					    <input type="hidden" id="hidden_update_responsible_operator_id">
					</td> -->
				</tr>
				<tr>
					<td class="ui-state-default td-title">导入日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="import_date" alt="导入日期" id="update_import_date" readonly="readonly">
					</td>		
					 <td class="ui-state-default td-title">发放日期</td>
					<td>
						<!-- <input type="text" class="ui-widget-content" name="provide_date" alt="发放日期" id="update_provide_date" readonly="readonly"> -->
						<label id="update_provide_date"></label>
					</td>	
				</tr>
				<tr>
				    <td class="ui-state-default td-title">废弃日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="waste_date" alt="废弃日期" id="update_waste_date" readonly="readonly">
					</td>	
					 <td class="ui-state-default td-title">更新时间</td>
					<td>
						<label id="update_updated_time"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3" >
						<textarea style="resize: none;font-size: 12px; width: 502px; height: 61px;" id="update_comment" name="comment"></textarea>			
					</td>
				</tr>
			</tbody>
		</table>
	    </div>			
			<div style="height:44px">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="updatebutton" value="修改" role="button" aria-disabled="false" style="float:left;left:2px" type="button">				
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="delbutton" value="删除" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton3" value="返回" role="button" aria-disabled="false" style="float:left;left:2px" type="button">
				<input type="hidden" id="hidden_devices_manage_id">
				<input type="hidden" id="hidden_devices_type_id">
			</div>
		</form>
	</div>
	<div class="clear areaencloser"></div>
</div>
<!--双击一栏画面结束-->

<!-- 批量交付开始 -->
<div id="deliver" style="display:none;">
	<div style="float:left;width:32%;">
	   <form id="deliver_form" method="post">
	    <div>
	        <table  class="condform" style="border:1px solid #aaaaaa;margin-left:2px;height:349px;">
	            <tbody>
	               <tr>
						<td class="ui-state-default td-title">分发课室</td>
						<td class="td-content"><select id="deliver_section_name" name="section_id"  alt="分发课室" >${sectionOptions}</select></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">责任工程</td>
		                <td class="td-content"><select id="deliver_line_name" name="line_id"  alt="责任工程" >${lineOptions}</select></td>
	                </tr>
	                <tr>
						<td class="ui-state-default td-title">责任工位</td>
		                <td class="td-content"><input type="text" id="deliver_position_id" name="position_id"  alt="责任工位"/>
		                 	 <input type="hidden" id="hidden_deliver_position_id"/>	
		                </td>
	                </tr>
	                <!-- <tr>
						<td class="ui-state-default td-title" style="width:150px;">责任人员</td>
		                <td class="td-content"><input type="text" id="deliver_operator_id" name="operator_id"  alt="责任工位"/>
		                     <input type="hidden" id="hidden_deliver_operator_id"/>
		                </td>
	                </tr> -->
	                <tr>
						<td class="ui-state-default td-title" style="width:150px;">管理员</td>
		                <td class="td-content"><input type="text" id="deliver_manager_operator_id" name="manager_operator_id"  alt="管理员"/>
		                     <input type="hidden" id="hidden_deliver_manager_operator_id"/>
		                </td>
	                </tr>
	            </tbody>
	        </table>
	      </div>
	      <div style="height:44px;">
	         <input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchDetail" value="检索" role="button" aria-disabled="false" style="float:left;left:311px;" type="button">				
	      </div>
	       
	   </form>
	</div>
	
	<div style="float:left;width:36%;margin-top:2px;">
	 		<!-- 批量交付详细list -->
	       	<table id="deliver_list"></table>
			<div id="deliver_listpager"></div>
	        <input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="more_update" value="交付" role="button" aria-disabled="false" style="margin-top:2px;margin-left:361px;" type="button">				
 		    <input type="hidden" id="hidden_deliver_list"> 		   
 	</div>
	
	<div style="float:left;width:32%;">
	   <form method="post">
	    <div>
	        <table  class="condform" style="border:1px solid #aaaaaa;height:349px;">
	            <tbody>
	               <tr>
						<td class="ui-state-default td-title">分发课室</td>
						<td class="td-content"><select id="to_section_name" name="section_id"  alt="分发课室" >${sectionOptions}</select></td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">责任工程</td>
		                <td class="td-content"><select id="to_line_name" name="line_id"  alt="责任工程" >${lineOptions}</select></td>
	                </tr>
	                <tr>
						<td class="ui-state-default td-title">责任工位</td>
		                <td class="td-content"><input type="text" id="to_position_id" name="position_id"  alt="责任工位"/>
		                 	 <input type="hidden" id="hidden_to_position_id"/>	
		                </td>
	                </tr>
	                <!-- <tr>
						<td class="ui-state-default td-title">责任人员</td>
		                <td class="td-content"><input type="text" id="to_operator_id" name="operator_id"  alt="责任工位"/>
		                     <input type="hidden" id="hidden_to_operator_id"/>
		                </td>
	                </tr> -->
	                <tr>
						<td class="ui-state-default td-title">管理员</td>
		                <td class="td-content"><input type="text" id="to_manager_operator_id" name="manager_operator_id"  alt="管理员"/>
		                     <input type="hidden" id="hidden_to_manager_operator_id"/>
		                </td>
	                </tr>
	            </tbody>
	        </table>
	      </div>
	   </form>
	</div>
	<style>
	.referchooser {z-index: 1010;}
	</style>
	<!-- 查询条件责任工位 -->
	<div class="referchooser ui-widget-content" id="deliver_position_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${pReferChooser}</table>
	</div>
	
	<!-- 交付责任工位 -->
	<div class="referchooser ui-widget-content" id="to_position_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${pReferChooser}</table>
	</div>
	
	<!--查询管理员 -->
	<div class="referchooser ui-widget-content" id="deliver_manager_operator_referchooser" tabindex="-1">
			<table width="200px">
				<tr>
					<td></td>
					<td width="50%">过滤字:<input type="text"/></td>
					<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
				</tr>
			</table>
			<table  class="subform">${oReferChooser}</table>
	</div>

	<!-- 交付管理员 -->
	<div class="referchooser ui-widget-content" id="to_manager_operator_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${oReferChooser}</table>
	</div>
</div>
<!-- 批量交付结束 -->

<div id="dialog_confrim"></div>

<div class="referchooser ui-widget-content" id="position_name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${pReferChooser}</table>
</div>

<!-- 管理员 -->
<div class="referchooser ui-widget-content" id="operator_name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${oReferChooser}</table>
</div>

<!-- 设备工具品名 -->
<div class="referchooser ui-widget-content" id="name_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform">${nReferChooser}</table>
</div>

<!-- 责任人员 -->
<div class="referchooser ui-widget-content" id="responsible_operato_referchooser" tabindex="-1">
	<table width="200px">
		<tr>
			<td></td>
			<td width="50%">过滤字:<input type="text"/></td>
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table  class="subform" id="update_choose_operator">${rReferChooser}</table>
</div>



<div class="clear"></div>

<!-- 替换新品 -->
<div id="replace_confrim" style="display:none;">
   <form id="replace_form" method="POST">	   
	    <div>
		<table class="condform" style="border:1px solid #aaaaaa;margin-left:2px;">
			<tbody>	
			    <tr>
					<td style="width:153px;" class="ui-state-default td-title">管理编号</td>
					<td>
						<input type="text" id="replace_manage_code" name="manage_code" alt="管理编号"  class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">同时废弃掉旧品</td>
					<td class="td-content" id="waste_old_products">
						<input type="radio" name="waste_old_products" id="waste_old_products_yes" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="waste_old_products_yes" aria-pressed="false">是</label>
						<input type="radio" name="waste_old_products" id="waste_old_products_no" class="ui-widget-content ui-helper-hidden-accessible" value="0" checked="checked"><label for="waste_old_products_no" aria-pressed="false">否</label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">品名</td>
					<td>
						<input type="text" id="replace_name" name="devices_type_id" readonly="readonly" alt="品名" class="ui-widget-content">
						<input type="hidden" id="hidden_replace_name">				
					</td>
					<td class="ui-state-default td-title">型号</td>
					<td>
						<input type="text" id="replace_model_name" name="model_name" alt="型号名称" class="ui-widget-content">
						<input id="hidden_replace_model_id" type="hidden">
					</td>	
				</tr>
				<tr>
					<td class="ui-state-default td-title">放置位置</td>
					<td>
						<input type="text" id="replace_location" name="location" alt="放置位置" class="ui-widget-content">						
					</td>
					<td class="ui-state-default td-title">管理员</td>
					<td>
						<input type="text" id="replace_manager" name="manager" alt="管理员" readonly="readonly" class="ui-widget-content">	
						<input type="hidden" id="hidden_replace_manager_operator_id">					
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">管理等级</td>
					<td class="td-content">	
					<select id="replace_manage_level" name="manage_level" alt="管理等级"></select>
					</td>
					<td class="ui-state-default td-title">状态</td>
					<td  class="td-content">
						<select id="replace_status" name="status" alt="状态">${status}</select>				
					</td>
					<!-- <td class="ui-state-default td-title">管理内容</td>
					<td>
						<input type="text" id="add_manage_content" name="manage_content" alt="管理内容" class="ui-widget-content">						
					</td> -->
				</tr>	
				<tr>
					<td class="ui-state-default td-title">出厂编号</td>
					<td>
						<input type="text" id="replace_products_code" name="products_code" alt="出厂编号" class="ui-widget-content">						
					</td>
					<td class="ui-state-default td-title">厂商</td>
					<td>
						<input type="text" id="replace_brand" name="brand" alt="厂商" class="ui-widget-content">						
					</td>
				</tr>	
				<tr>
					<td class="ui-state-default td-title">分发课室</td>
					<td style="width:200px;">
						<select id="replace_section_id" name="section_id" alt="分发课室"></select>					
					</td>
					<td class="ui-state-default td-title">责任工程</td>
					<td style="width:100px;">
						<select id="replace_line_id"></select>						
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">责任工位</td>
					<td>
						<input type="text" id="replace_position_id" name="position_id" alt="责任工位"  readonly="readonly"  class="ui-widget-content">		
						<input name="position_id" id="hidden_replace_position_id" type="hidden">
					</td>
					<td class="ui-state-default td-title">发放者</td>
					<td>
						<label id="replace_provider"></label>
					</td>
					<!-- <td class="ui-state-default td-title">责任人员</td>
					<td>
						<input type="text" id="add_responsible_operator_id" name="responsible_operator_id" alt="责任人" readonly="readonly"  class="ui-widget-content">		
					    <input type="hidden" id="hidden_add_responsible_operator_id">
					</td> -->
				</tr>
				<tr>
					<td class="ui-state-default td-title">导入日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="import_date" alt="导入日期" id="replace_import_date" readonly="readonly">
					</td>		
					 <td class="ui-state-default td-title">发放日期</td>
					<td>
						<!-- <input type="text" class="ui-widget-content" name="provide_date" alt="发放日期" id="add_provide_date" readonly="readonly"> -->
						<label id="replace_provide_date"></label>
					</td>	
				</tr>
				<tr>
				    <td class="ui-state-default td-title">废弃日期</td>
					<td>
						<input type="text" class="ui-widget-content" name="waste_date" alt="废弃日期" id="replace_waste_date" readonly="readonly">
					</td>	
					 <td class="ui-state-default td-title">更新时间</td>
					<td>
						<label id="replace_updated_time"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3">
						<textarea  style="resize: none;font-size: 12px; width: 502px; height: 61px;" id="replace_comment" name="comment" alt="备注"></textarea>				
					</td>
				</tr>
			</tbody>
		</table>
		<!--隐藏替换新品之前的旧品的管理编号-->
		<input type="hidden" id="hidden_old_manage_code"/>
		<!--隐藏替换新品之前的旧品的tools_manage_id-->
		<input type="hidden" id="hidden_old_devices_manage_id"/>
	    </div>	
	</form>
	
</div>
</div>

<!----------------------end----------------------------->
</div>
</div>