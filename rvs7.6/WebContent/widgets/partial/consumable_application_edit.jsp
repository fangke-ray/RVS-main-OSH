<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<form id="application_sheet_form">
	<style>
		#application_sheet_form input[type='number'] {
			width: 4em;
			text-align: right;
		}
		#application_sheet_form table.full-table {
			width:900px;
		}
		#application_sheet_form table.half-table {
			width:420px;
		}
		#application_sheet_form td.td-title, #application_sheet_form .td-content{
			width:auto;
		}
		#application_sheet_form .td-content span.readonly_apply_quantiy{
			text-align: right;
			width: 4em;
			display: inline-block;
		}
		#application_sheet_form tr .td-title:first-child{
			width:40px;
		}
		#application_sheet_form tr .td-title:nth-child(2){
			width:130px;
		}
		#application_sheet_form tr[state='delete']{
			display:none;
		}
		
		#application_sheet_form tr .td-content:last-child{
			text-align: right;
			padding-right:1em;
		}
		#application_sheet_form input.ca_add_button,
		#application_sheet_form input.ca_remove_button {
			padding:0 5px;
			float:right;
			margin-right: 1em; 
		}
		#chosser_4_application_sheet	{
	    	position: absolute;
	    	background-color:white;
	    	height:auto;
	    	min-height: 120px;
	    	top:0;left:20px;
	    	border: 1px solid rgb(170, 170, 170);
	    	display: none;
	    	box-shadow: 2px 2px 1px;
	    }
	    #chosser_4_material_list, #chosser_4_popular_item_list {
	    	border-collapse : collapse;
	    }
		#chosser_4_material_list tr.breaking {
			color : red;
		}
		#chosser_4_material_list tbody, #chosser_4_popular_item_list tbody {
			cursor:pointer;
		}
		#chosser_4_material_list tbody tr:hover, #chosser_4_popular_item_list tbody tr:hover {
			background-color: #FFFBD7;
		}
		#chosser_4_material_list tbody tr.c_material, chosser_4_material_list tbody tr.c_material:hover {
			background-color: rgb(255, 65, 56);
			color:white;
		}
	</style>
	<div id="application_sheet_content" style="height:520px;max-height:530px;overflow-y: auto;">
		<table class="condform full-table">
			<tbody>
				<tr>
					<td class="ui-state-default td-title" style="width:180px;">申请单编号</td>
					<td class="td-content">
						<input type="hidden" id="edit_consumable_application_key" value="${cmf.consumable_application_key}"></input>
						<label id="edit_application_no">${cmf.application_no}</label>
					</td>
					<td class="ui-state-default td-title" style="width:180px;">申请工程</td>
					<td class="td-content">
						<label id="edit_line_name">${line_name}</label>
					</td>
				</tr>
			</tbody>
		</table>

		<table class="condform full-table" id="e_exchange_sheet">
			<tbody>
				<tr>
					<td class="ui-widget-header" colspan="5">
						在线维修消耗
						<input type="button" class="ca_add_button ui-button" value="+" apply_method="1"></input>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title">使用修理单号</th>
					<th class="ui-state-default td-title" colspan="4">原因</th>
				</tr>
				<tr id="application_sheet_material" material_id="${cmf.material_id}">
					<td class="td-content">
					</td>
					<td class="td-content">
						<label>${cmf.sorc_no}</label>
					</td>
					<td class="td-content" colspan="3">
						<label>${cmf.apply_reason}</label>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title">消耗品代码</th>
					<th class="ui-state-default td-title" style="width: 560px;">说明</th>
					<th class="ui-state-default td-title" style="width: 100px;">申请数量</th>
					<th class="ui-state-default td-title" style="width: 100px;">价格</th>
				</tr>
			</tbody>
		</table>


		<table class="condform full-table" id="e_screw_sheet">
			<tbody>
				<tr>
					<td class="ui-widget-header" colspan="5">
						日常补充消耗品
						<input type="button" class="ca_add_button ui-button" value="+" apply_method="2"></input>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title">消耗品代码</th>
					<th class="ui-state-default td-title" style="width: 560px;">说明</th>
					<th class="ui-state-default td-title" style="width: 100px;">申请数量</th>
					<th class="ui-state-default td-title" style="width: 100px;">价格</th>
				</tr>
			</tbody>
		</table>

		<table class="condform full-table" id="e_assistant_sheet">
			<tbody>
				<tr>
					<td class="ui-widget-header" colspan="5">
						日常补充乙材
						<input type="button" class="ca_add_button ui-button" value="+" apply_method="2"></input>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title">消耗品代码</th>
					<th class="ui-state-default td-title" style="width: 560px;">说明</th>
					<th class="ui-state-default td-title" style="width: 100px;">申请数量</th>
					<th class="ui-state-default td-title" style="width: 100px;">价格</th>
				</tr>

			</tbody>
		</table>

		<table class="condform full-table" id="e_domestic_sheet">
			<tbody>
				<tr>
					<td class="ui-widget-header" colspan="5">
						日常补充国内耗材
						<input type="button" class="ca_add_button ui-button" value="+" apply_method="2"></input>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title">消耗品代码</th>
					<th class="ui-state-default td-title" style="width: 560px;">说明</th>
					<th class="ui-state-default td-title" style="width: 100px;">申请数量</th>
					<th class="ui-state-default td-title" style="width: 100px;">价格</th>
				</tr>
			</tbody>
		</table>

	</div>
	<div style="height : 44px;">
		<section style="font-size:2em; margin-top:1em;  float:left;" id="t_total_price">总价：<span></span></section>
		<input type="button" value="关闭" id="ca_close_button" class="ui-button" style="margin-top:2em; float:right;">
		<input type="button" value="提交申请" id="ca_submit_button" class="ui-button" style="margin-top:2em; float:right;">
		<input type="button" value="编辑完成" id="ca_edit_button" class="ui-button" style="margin-top:2em; float:right;">
	</div>

	<div id="chosser_4_application_sheet">
		<div style="max-height : 420px;overflow-x:hidden;overflow-y:auto;">
		<table class="condform full-table" id="chosser_4_material_list">
			<thead>
				<tr>
					<td class="ui-widget-header" colspan="6" style="position: relative;">
						在线维修对象
<a href="javascript:ca_evts.closeChooser()" class="ui-dialog-titlebar-close ui-corner-all" role="button"><span class="ui-icon ui-icon-closethick">close</span></a>
					</td>
				</tr>
				<tr>
					<th class="ui-state-default td-title"></th>
					<th class="ui-state-default td-title" style="width: 240px;">修理单号</th>
					<th class="ui-state-default td-title" style="width: 360px;">型号</th>
					<th class="ui-state-default td-title" style="width: 180px;">机身号</th>
					<th class="ui-state-default td-title" style="width: 100px;">纳期</th>
					<th class="ui-state-default td-title" style="width: 180px;">等级</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		</div>
		<div style="max-height : 420px;">
<div class="ui-widget-header ui-corner-all ui-helper-clearfix"><span class="ui-dialog-title" style="font-size:12px;">领用理由</span></div>
			<textarea style="height:6em;width:64em;" id="exchange_reason"></textarea>
			<input type="button" value="选定" id="materialSelector" class="ui-button" style="margin-top:2em;">
		</div>

		<div id="chosser_4_partial" style="overflow-x:hidden;overflow-y:auto;width: 904px;">
		<div style="position: relative;">
<div class="ui-widget-header ui-corner-all ui-helper-clearfix"><span class="ui-dialog-title" style="font-size:12px;">选择已经申请的消耗品</span>
<a href="javascript:ca_evts.closeChooser()" class="ui-dialog-titlebar-close ui-corner-all" role="button"><span class="ui-icon ui-icon-closethick">close</span></a>
</div>
		</div>
		<div style="width:50%;float:left;min-height : 420px;">
		<table class="condform half-table">
			<tbody>
				<tr>
					<td class="ui-state-default td-title" style="width:100px;">消耗品代码</td>
					<td class="td-content">
						<input type="hidden" id="ca_add_item_id"></input>
						<input type="hidden" id="ca_add_item_type"></input>
						<input type="text" id="ca_add_item_code"></input>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">说明</td>
					<td class="td-content">
						<label id="ca_add_item_decription"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">现有库存</td>
					<td class="td-content">
						<label id="ca_add_item_available_inventory"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">申请量</td>
					<td class="td-content">
						<span id="ca_add_order_quantity_input">
							<input type="number" id="ca_add_order_quantity"></input>
						</span>
						<span id="ca_add_order_quantity_with_measure" style="display:none;">
							<input type="radio" value="0" name="with_measure" id="with_measure_y"><label for="with_measure_y">包</label>
							<input type="radio" value="1" name="with_measure" id="with_measure_n"><label for="with_measure_n">个</label>
						</span>
						<label id="ca_add_order_quantity_inpack" style="display:none;">分装取用</label>
						<input type="hidden" value="1" id="ca_add_order_content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">价格</td>
					<td class="td-content">
						<label id="ca_add_unit_price"></label><label id="ca_add_total_price"></label>
					</td>
				</tr>
				<tr id="ca_heatshrink">
					<td class="ui-state-default td-title">热缩管剪裁长度</td>
					<td class="td-content">
						<select id="ca_add_cut_length"></select>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
		<div style="width:50%;float:left;height : 420px;overflow-y: auto;">
		<table class="condform half-table" id="chosser_4_popular_item_list">
			<thead>
				<tr>
					<td class="ui-widget-header" colspan="3" style="position: relative;">
						常用消耗品
					</td>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		</div>
		<div class="clear"/>
		<div style="width:100%;float:none;height : 42px;">
			<input type="button" class="ui-button" id="add_item_button" value="加入" style="margin-left:1em;padding:4px 6px;float:none;">
		</div>
		</div>

	</div>

</form>