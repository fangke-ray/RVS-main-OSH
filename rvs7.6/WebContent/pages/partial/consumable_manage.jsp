<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- base href="http://localhost/rvs/" -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/lte-style.css">
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<% 
	String fact = (String) request.getAttribute("fact");
	String process = (String) request.getAttribute("process");
	String applier = (String) request.getAttribute("applier");
	boolean isFact = ("1").equals(fact);
	boolean isProcess = ("1").equals(process);
	boolean isApplier = ("1").equals(applier);
%>

<style>
	.edit_a_supply_quantiy {
		width: 4em;
		text-align: right;
	}
	.application_sheet{
		width:100%;	
	}
	#pop_window_apply_edit .application_sheet tr:nth-child(n+2) td:nth-last-child(1),
	#pop_window_apply_edit .application_sheet tr:nth-child(n+2) td:nth-last-child(2),
	#pop_window_apply_edit .application_sheet tr:nth-child(n+2) td:nth-last-child(3){
		display: none;
	}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>

<script type="text/javascript" src="js/partial/consumable_manage.js"></script>
<script type="text/javascript" src="js/partial/consumable_order.js"></script>
<script type="text/javascript" src="js/partial/consumable_supply.js"></script>
<script type="text/javascript" src="js/partial/consumable_apply.js"></script>
<script type="text/javascript" src="js/partial/consumable_inventory.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>

<title>消耗品仓库管理记录</title>
</head>

<body class="outer" style="align: center;">
	<input type="hidden" id="hidden_isFact" value='<%=isFact%>'>
	<input type="hidden" id="hidden_isProcess" value='<%=isProcess%>'>
	<input type="hidden" id="hidden_isApplier" value='<%=isApplier%>'>
	<div id="update_limit_date_after"></div>
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="消耗品管理"/>
				</jsp:include>
			</div>

			<!-- 本体  -->
			<div style="float: left;">
				<div id="body-mdl" class="dwidth-middleright">
					
					<!-- Tab选项卡 -->	
					<div style="border-bottom: 0;" class="ui-widget-content dwidth-middleright">
						<div id="infoes" class="ui-buttonset">
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_order_tab" value="page_order">
							<label for="page_order_tab">订购记录</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_supply_tab" value="page_supply">
							<label for="page_supply_tab">入库记录</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_apply_tab" value="page_apply">
							<label for="page_apply_tab">申请领用记录</label>
							<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_inventory_tab" value="page_inventory">
							<label for="page_inventory_tab">盘点记录</label>
							<input type="hidden" id="hidden_page" value='${search_page}'>
							<input type="hidden" id="hidden_key" value='${search_key}'>
						</div>
					</div>
			
					<!-- 订购记录一览  -->
					<div id="page_order" class="record_page">
				
						<div id="searcharea_o" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">检索条件</span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
				
						<div class="ui-widget-content dwidth-middleright">
							<!-- 订购记录检索条件 -->
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">包含品名编号</td>
										<td class="td-content">
											<input id="search_o_code" class="ui-widget-content" type="text">
										</td>
										<td class="ui-state-default td-title">订购单编号</td>
										<td class="td-content">
											<input id="search_o_order_no" class="ui-widget-content" type="text">
										</td>
										<td class="ui-state-default td-title">订购日期</td>
										<td class="td-content">
											<input type="text" id="search_order_date_start" class="ui-widget-content" readonly="readonly" value="${start_date}">起<br>
											<input type="text" id="search_order_date_end" class="ui-widget-content" readonly="readonly">止
										</td>
									</tr>
								</tbody>
							</table>
							<div style="height: 44px">
								<input class="ui-button" id="reset_o_button" value="清除" style="float: right; right: 2px" type="button">
								<input class="ui-button" id="search_o_button" value="查询" style="float: right; right: 2px" type="button">
							</div>
						</div>
						
						<div class="clear areaencloser" ></div>
				
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">订购记录一览</span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						<div id="listarea_o" class="width-middleright">
							<table id="order_list"></table>
							<div id="order_list_pager"></div>
							
							<% if (isFact) { %>
							<div class="ui-widget-header areabase" style="padding-top: 4px; margin-bottom: 16px;">
								<div style="margin-left: 4px; margin-top: 2px;">
									<input id="add_o_button" class="ui-button" value="生成订购单" type="button" />
									<input id="remove_o_button" class="ui-button" value="删除订购单" type="button" />
									<input id="output_export_button" class="ui-button" value="导出订购单" type="button" style="float:right" />
								</div>
							</div>
							<% } %>
						</div>
						
					</div>
					
					<div class="clear"/></div>
			
					<!-- 入库记录一览  -->
					<div id="page_supply" class="record_page" style="display: none">
					
						<div id="searcharea_s" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">检索条件</span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
				
						<div class="ui-widget-content dwidth-middleright">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">消耗品代码</td>
										<td class="td-content">
											<input id="search_s_code" class="ui-widget-content" type="text">
										</td>
										<td class="ui-state-default td-title">入库日期</td>
										<td class="td-content">
											<input type="text" id="search_supply_date_start" class="ui-widget-content" readonly="readonly" value="${start_date}">起<br>
											<input type="text" id="search_supply_date_end" class="ui-widget-content" readonly="readonly">止
										</td>
										<td class="ui-state-default td-title"></td>
										<td class="td-content"></td>
									</tr>
								</tbody>
							</table>
							<div style="height: 44px">
								<input class="ui-button" id="reset_s_button" value="清除" style="float: right; right: 2px" type="button">
								<input class="ui-button" id="search_s_button" value="查询" style="float: right; right: 2px" type="button">
							</div>
						</div>
						
						<div class="clear areaencloser"></div>
						
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">入库记录一览 </span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						
						<div class="width-middleright">
							<table id="supply_list"></table>
							<div id="supply_list_pager"></div>
					
							<% if (isFact) { %>
							<div class="ui-widget-header areabase" style="padding-top: 4px; margin-bottom: 16px;">
								<div style="margin-left: 4px; margin-top: 2px;">
									<input id="add_s_button" class="ui-button" value="入库" type="button">
								</div>
							</div>
							<% } %>
						</div>
					</div>
					
					<div class="clear"/></div>
			
					<!-- 申请领用记录一览  -->
					<div id="page_apply" style="display:none;" class="record_page">

						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">检索条件</span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
						
						<div class="ui-widget-content dwidth-middleright">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">申请单编号</td>
										<td class="td-content">
											<input id="search_application_no" class="ui-widget-content" type="text">
										</td>
										<td class="ui-state-default td-title">申请课室</td>
										<td class="td-content">
											<select id="search_section_id" class="ui-widget-content">${sOptionsSection}</select>
										</td>
										<td class="ui-state-default td-title">申请工程</td>
										<td class="td-content">
											<select id="search_line_id" class="ui-widget-content">${sOptionsLine}</select>
										</td>
									</tr>
									<tr>
										<td class="ui-state-default td-title">发放完成</td>
										<td class="td-content">
											<div id="search_all_supplied" class="ui-buttonset">
												<input type="radio" name="all_supplied" id="all_supplied_all" class="ui-widget-content" value="" checked="checked"><label for="all_supplied_all">(全)</label>
												<input type="radio" name="all_supplied" id="all_supplied_y" class="ui-widget-content" value="1"><label for="all_supplied_y">已发放</label>
												<input type="radio" name="all_supplied" id="all_supplied_n" class="ui-widget-content" value="-1"><label for="all_supplied_n">未发放</label>
											</div>
										</td>
										<td class="ui-state-default td-title">申请日期</td>
										<td class="td-content">
											<input type="text" id="search_apply_time_start" class="ui-widget-content" readonly="readonly" value="${start_date}">起<br>
											<input type="text" id="search_apply_time_end" class="ui-widget-content" readonly="readonly">止
										</td>
										<td class="ui-state-default td-title">即时领用</td>
										<td class="td-content">
											<div id="search_flg" class="ui-buttonset">
												<input type="radio" name="flg" id="flg_all" class="ui-widget-content" value="" checked="checked"><label for="flg_all">(全)</label>
												<input type="radio" name="flg" id="flg_y" class="ui-widget-content" value="1"><label for="flg_y">包含</label>
											</div>
										</td>
									</tr>
								</tbody>
							</table>
							
							<div style="height: 44px">
								<input class="ui-button" id="reset_a_button" value="清除" style="float: right; right: 2px" type="button">
								<input class="ui-button" id="search_a_button" value="查询" style="float: right; right: 2px" type="button">
							</div>
				
						</div>
						
						
						<div class="clear areaencloser"></div>
				
						<div class="width-middleright">
				
							<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
								<span class="areatitle">申请领用记录一览 </span>
								<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
									<span class="ui-icon ui-icon-circle-triangle-n"></span>
								</a>
							</div>
							<table id="apply_list"></table>
							<div id="apply_list_pager"></div>
							<% if (isApplier) { %>
							<div class="ui-widget-header areabase" style="padding: 4px 0 0 2px; margin-bottom: 16px;">
<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
							</div>
							<% } %>
						</div>
						
					</div>
					
					<div class="clear"/></div>
			
					<!-- 盘点记录一览  -->
					<div id="page_inventory" class="record_page" style="display: none">
					
						<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
							<span class="areatitle">检索条件</span>
							<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
								<span class="ui-icon ui-icon-circle-triangle-n"></span>
							</a>
						</div>
					
						<div class="ui-widget-content dwidth-middleright">
							<table class="condform">
								<tbody>
									<tr>
										<td class="ui-state-default td-title">消耗品代码</td>
										<td class="td-content">
											<input id="search_i_code" class="ui-widget-content" type="text">
										</td>
										<td class="ui-state-default td-title">调整日期</td>
										<td class="td-content">
											<input type="text" id="search_adjust_date_start" class="ui-widget-content" readonly="readonly" value="${start_date}">起<br>
											<input type="text" id="search_adjust_date_end" class="ui-widget-content" readonly="readonly">止
										</td>
										<td class="ui-state-default td-title">理由</td>
										<td class="td-content">
											<input id="search_i_reason" class="ui-widget-content" type="text">
										</td>
									</tr>
								</tbody>
							</table>
							
							<div style="height: 44px">
								<input class="ui-button" id="reset_i_button" value="清除" style="float: right; right: 2px" type="button">
								<input class="ui-button" id="search_i_button" value="查询" style="float: right; right: 2px" type="button">
							</div>
							
						</div>
						
						<div class="clear areaencloser"></div>
				
						<!-- JqGrid表格  -->
						<div class="width-middleright">
							<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
								<span class="areatitle">盘点记录一览 </span>
								<a target="_parent"  href="javascript:void(0)" class="HeaderButton areacloser">
									<span class="ui-icon ui-icon-circle-triangle-n"></span>
								</a>
							</div>
							<table id="inventory_list"></table>
							<div id="inventory_list_pager"></div>
						</div>
						
					</div>
					
					<div class="clear"/></div>					
				</div>
				
				<div class="areaencloser"></div>
			</div>
			
			<div class="clear"></div>


			<!-- 弹出信息  -->
			<div id="pop_window" style="display: none"></div>
			<div id="pop_windows" style="display: none">

				<div id="pop_window_order_edit" class="ui-widget-content">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title" style="width:180px;">订购单编号</td>
								<td class="td-content" colspan="5">
									<label id="o_order_no"></label>
								</td>
							</tr>
						</tbody>
					</table>
				</div>

				<div id="pop_window_supply_add">
					<form id="supply_form" method="post" onsubmit="return false;">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title" style="width:400px;">名称</td>
								<td class="ui-state-default td-title">入库数量</td>
								<td class="ui-state-default td-title"></td>
							</tr>
							<tr>
								<td class="td-content">
									<input type="text" name="s_code" id="edit_s_code" alt="消耗品代码"></input>
									<font color='red'><label id="s_msg"></label></font>
									<input type="hidden" id="hidden_partial_id">
								</td>
								<td class="td-content">
									<label id="edit_s_name"></label>
								</td>
								<td class="td-content">
									<input type="number" name="s_quantity" id="edit_s_quantity" min="1" alt="入库数量"></input>
								</td>
								<td class="td-content">
									<input class="ui-button" type="button" id="add_supply_button" value="入库"/>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>

				<div id="pop_window_apply_edit" style="display: none">
					<input type="hidden" id="consumable_application_key">
					<input type="hidden" id="hidden_section_id">
					<input type="hidden" id="hidden_line_id">
					<table class="condform" width="100%">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">申请单编号</td>
								<td class="td-content">
									<label id="edit_application_no"></label>
								</td>
								<td class="ui-state-default td-title">申请工程</td>
								<td class="td-content">
									<label id="edit_line_name"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">申请时间</td>
								<td class="td-content">
									<label id="edit_apply_time"></label>
								</td>
								<td class="ui-state-default td-title">部分发放</td>
								<td class="td-content">
									<div class="ui-buttonset" id="part_supply">
										<input type="checkbox" name="part" class="ui-widget-content" id="part_supply_s" value="0">
										<label for="part_supply_s">部分发放</label>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="online_sheet">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="8">
									在线维修消耗
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">发放数量</td>
								<td class="ui-state-default td-title">发放总价</td>
								<td></td><!-- 待发放数量 -->
								<td></td><!-- 当前有效库存 -->
								<td></td><!-- 数据库中提供数量 -->
							</tr>
						</tbody>
					</table>
	
	
					<table class="condform application_sheet" id="screw_sheet">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="8">
									日常补充消耗品
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">发放数量</td>
								<td class="ui-state-default td-title">发放总价</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="assistant_sheet">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="8">
									日常补充乙材
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">发放数量</td>
								<td class="ui-state-default td-title">发放总价</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="domestic_sheet">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="8">
									日常补充国内耗材
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">发放数量</td>
								<td class="ui-state-default td-title">发放总价</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</tbody>
					</table>	
				</div>

				<div id="pop_window_application_detail" style="display: none">
					<table class="condform" width="100%">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">申请单编号</td>
								<td class="td-content">
									<label id="edit_application_no_readonly"></label>
								</td>
								<td class="ui-state-default td-title">申请工程</td>
								<td class="td-content">
									<label id="edit_line_name_readonly"></label>
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">申请时间</td>
								<td class="td-content">
									<label id="edit_apply_time_readonly"></label>
								</td>
								<td class="ui-state-default td-title"></td>
								<td class="td-content">
								</td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="online_sheet_readonly">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="7">
									在线维修消耗
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">已发放数量</td>
								<td class="ui-state-default td-title">发放价格</td>
							</tr>
						</tbody>
					</table>
	
	
					<table class="condform application_sheet" id="screw_sheet_readonly">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="7">
									日常补充消耗品
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">已发放数量</td>
								<td class="ui-state-default td-title">发放价格</td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="assistant_sheet_readonly">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="7">
									日常补充乙材
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">已发放数量</td>
								<td class="ui-state-default td-title">发放价格</td>
							</tr>
						</tbody>
					</table>
	
					<table class="condform application_sheet" id="domestic_sheet_readonly">
						<tbody>
							<tr>
								<td class="ui-widget-header" colspan="7">
									日常补充国内耗材
								</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">消耗品代码</td>
								<td class="ui-state-default td-title">名称</td>
								<td class="ui-state-default td-title">库位编号</td>
								<td class="ui-state-default td-title">申请数量</td>
								<td class="ui-state-default td-title">已发放数量</td>
								<td class="ui-state-default td-title">发放价格</td>
							</tr>
						</tbody>
					</table>	
				</div>

				<div id="pop_window_inventory_edit">
					<form id="inventory_form" method="post" onsubmit="return false;">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">理由</td>
							</tr>
							<tr>
								<td class="td-content">
									<textarea  style="resize: none;font-size: 12px; width: 502px; height: 61px;" id="edit_i_reason" name="i_reason" alt="理由"></textarea>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>

		</div>
	
		<div id="confirmmessage"></div>
	
		<div id="footarea"></div>
		<input type="hidden"  id="cConsumableType" value="${cConsumableType}">
	</div>
</body>
</html>