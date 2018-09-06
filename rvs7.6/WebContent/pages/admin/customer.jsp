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
<script type="text/javascript" src="js/admin/customer.js"></script>


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
										<td class="ui-state-default td-title">客户名称</td>
										<td class="td-content">
											<input type="text" id="search_name" class="ui-widget-content"/>
										</td>
										<td class="ui-state-default td-title">所属分室</td>
										<td class="td-content">
											<select id="search_ocm">${goMaterial_ocm}</select>
										</td>
										<td class="ui-state-default td-title">优先对应客户</td>
										<td class="td-content">
											<div id="search_vip" class="ui-buttonset">
												<input type="radio" name="vip" id="search_vip_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_vip_a" aria-pressed="false">(全)</label>
												<input type="radio" name="vip" id="search_vip_b" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_vip_b" aria-pressed="false">是</label>
												<input type="radio" name="vip" id="search_vip_c" class="ui-widget-content ui-helper-hidden-accessible" value="-1"><label for="search_vip_c" aria-pressed="false">否</label>
											</div>
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height:44px">
								<input id="resetbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float:right;right:2px" aria-disabled="false" role="button" value="清除">
								<input id="searchbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  value="检索" role="button" aria-disabled="false" type="button" style="float:right;right:2px">
								<input type="hidden" id="sMaterial_ocm" value="${sMaterial_ocm }">
							</div>
						</form>
					</div>
					
					<div class="clear areaencloser"></div>
					
					<div id="listarea" >
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
							<span class="areatitle">客户管理一览</span>
							<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<table id="list" ></table>
						<div id="listpager"></div>
						<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase" style="padding-top:4px;">
							<input id="merge" type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  style="float:left;left:2px" aria-disabled="false" role="button" value="归并">
						</div>
					</div>
				</div>
				<!-- 一览结束 -->
				
				<!-- 新建客户开始 -->
				<div id="add" style="display:none;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">新建客户</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					
					<div class="ui-widget-content">	
						<form id="add_form" method="POST">
							<table class="condform" width="500px;">
								<tr>
									<td class="ui-state-default td-title">客户名称</td>
									<td class="td-content">
										<input type="text" id="add_name" name="name" class="ui-widget-content" alt="客户名称" style="width:300px;"/>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">所属分室</td>
									<td class="td-content">
										<select id="add_ocm">${goMaterial_ocm}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">优先对应客户</td>
									<td class="td-content">
										<div id="add_vip" class="ui-buttonset">
											<input type="radio" name="add_vip" id="add_vip_b" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="add_vip_b" aria-pressed="false">是</label>
											<input type="radio" name="add_vip" id="add_vip_c" class="ui-widget-content ui-helper-hidden-accessible" value="-1" checked="checked"><label for="add_vip_c" aria-pressed="false">否</label>
										</div>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirmbutton" value="确认" role="button" aria-disabled="false" style="float:left;left:4px;">
								<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
							</div>
						</form>
					</div>
				</div>
				<!-- 新建点检表结束 -->
				
				<!-- 修改客户开始 -->
				<div id="update" style="display:none">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					   <span class="areatitle">修改客户</span>
					    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
							<span class="ui-icon ui-icon-circle-triangle-w"></span>
						</a>
					</div>
					<div class="ui-widget-content">	
						<form id="update_form" method="POST">
							<table class="condform" width="500px;">
								<tr>
									<td class="ui-state-default td-title">客户名称</td>
									<td class="td-content">
										<input type="text" id="update_name" name="name" class="ui-widget-content" alt="客户名称" style="width:300px;"/>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">所属分室</td>
									<td class="td-content">
										<select id="update_ocm">${goMaterial_ocm}</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">优先对应客户</td>
									<td class="td-content">
										<div id="update_vip" class="ui-buttonset">
											<input type="radio" name="update_vip" id="update_vip_b" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="update_vip_b" aria-pressed="false">是</label>
											<input type="radio" name="update_vip" id="update_vip_c" class="ui-widget-content ui-helper-hidden-accessible" value="-1"><label for="update_vip_c" aria-pressed="false">否</label>
										</div>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">最后更新人</td>
									<td class="td-content">
										<label id="lable_operation_name"></label>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">最后更新时间</td>
									<td class="td-content">
										<label id="lable_updated_time"></label>
									</td>
								</tr>
							</table>
							<div style="height:44px">
								<input id="updatebutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button"  value="修改" style="float:left;left:2px" aria-disabled="false" role="button" >
								<input id="cancelbutton2" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  type="button" value="取消" style="float:left;left:2px" aria-disabled="false"   role="button" >
							</div>
						</form>
					</div>
				</div>
				<!-- 修改客户结束 -->
				
				<div id="merge_dialog" style="display:none;">
					<div class="ui-widget-content">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title" style="width:175px;">客户名称</td>
									<td class="td-content">
										<input type="text" id="merge_search_name" class="ui-widget-content" style="width:160px;"/>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input id="searchbutton2" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  type="button" value="查询" style="float:right;right:2px" aria-disabled="false"   role="button" >
						</div>
					</div>
					<div>
						<table id="merge_list" ></table>
						<div id="merge_listpager"></div>
					</div>
					<div style="height:44px;margin-top:2px;"> 
						<input id="cancelbutton3" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"  type="button" value="取消" style="float:right;" aria-disabled="false"   role="button" />
						<input id="executebutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button"  value="实行归并" style="float:right;right:2px" aria-disabled="false" role="button" />
					</div>
				</div>
				
			</div>
			
			
			<div class="clear"></div>
			<div id="confirmmessage"></div>
		</div>
