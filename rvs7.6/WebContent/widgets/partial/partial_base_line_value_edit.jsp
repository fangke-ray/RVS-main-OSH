<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<style>
#base .ui-jqgrid-htable tr td.td-content,
#detail .ui-jqgrid-htable tr td.td-content {
	border-color: #CCCCCC;
	border-style: solid;
	border-width: 1px;
	text-align: right;
}
#base td.td-title {
	width:824px;
}
.nk .td-content {
	text-align: right;
}
.gap {
	height:1em;
}
</style>
<div id="base_value_set">
	<div style="float: left;">
		<div style="height: 40px; border-bottom: 0;" id="base_flg">
			<input type="radio" name="answer_in_deadline" id="base_value" class="ui-button ui-corner-up" value="" checked="checked"><label for="base_value" aria-pressed="false">基本</label> 
			<input type="radio" name="answer_in_deadline" id="detail_value" class="ui-button ui-corner-up" value=""><label for="detail_value" aria-pressed="false">详细</label> 
			<input type="radio" name="answer_in_deadline" id="partial_level_value" class="ui-button ui-corner-up" value=""><label for="partial_level_value" aria-pressed="false">型号等级</label>
		</div>
		<div id="base" style="width: 580px; height: 292px; margin-top: 4px;" class="ui-widget-content">
			<form name="base_form" id="base_form">
				<table class="condform nk">
					<tr>
						<td class="ui-state-default td-title">周期平均使用量</td>
						<td class="td-content" colspan="3">
							<label id="lable_cycle_average_count"></label>
						</td>
						<td class="ui-state-default td-title" style="width:100px;">起效日期</td>
					</tr>
					<tr class="gap">
					</tr>
					<tr>
						<td class="ui-state-default td-title">采样周期内 使用量合计</td>
						<td class="td-content">
							<label id="lable_current_average_count"></label>
						</td>
						<td class="ui-state-default td-title">采样周期内 补充量合计</td>
						<td class="td-content">
						   <label id="label_supply_count"></label>
						</td>
						<td class="td-content">
						</td>
					</tr>
					<tr class="gap">
					</tr>
					<tr>
						<td class="ui-state-default td-title">SORCWH 现行基准值</td>
						<td class="td-content">
						   <label id="label_orcwh_foreboard_count"></label>
						</td>
						<td class="ui-state-default td-title">SORCWH 基准值设定</td>
						<td class="td-content">
						   <input type="text" class="ui-widget-content" id="edit_sorcwh_setting" name="edit_sorcwh_setting" alt="SORCWH 设定基准值"  style="width:5em;text-align:right;"></input>
						</td>
						<td class="td-content">
							<input name='sorcwh_end_date' class="ui-widget-content" id='edit_sorcwh_end_date' type='text' alt='SORCWH有效区间开始'/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">WH2P 现行基准值</td>
						<td class="td-content">
						   <label id="label_wh2p_foreboard_count"></label>
						</td>
						<td class="ui-state-default td-title">WH2P 基准值设定</td>
						<td class="td-content">
						   <input type="text" class="ui-widget-content" id="edit_wh2p_setting" name="edit_wh2p_setting" alt="WH2P 设定基准值"  style="width:5em;text-align:right;"></input>
						</td>
						<td class="td-content">
						   <input name='wh2p_end_date' class="ui-widget-content" id='edit_wh2p_end_date' type='text' alt='WH2P有效区间开始'/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">OSH 现行基准值</td>
						<td class="td-content">
						   <label id="label_osh_foreboard_count"></label>
						</td>
						<td class="ui-state-default td-title">OSH 基准值设定</td>
						<td class="td-content">
						   <label id="label_osh_setting"></label>
						</td>
						<td class="td-content">
						</td>
					</tr>
					<tr class="gap">
					</tr>
					<tr>
						<td class="ui-state-default td-title">消耗品库存 现行基准值</td>
						<td class="td-content">
						   <label id="label_consumable_foreboard_count"></label>
						</td>
						<td class="ui-state-default td-title">消耗品库存 基准值设定</td>
						<td class="td-content">
						   <input type="text" class="ui-widget-content" id="edit_consumble_setting" name="edit_consumble_setting" alt="消耗品库存 设定基准值" style="width:5em;text-align:right;"></input>
						</td>
						<td class="td-content">
							<input name='consumble_end_date' class="ui-widget-content" id='edit_consumble_end_date' type='text' alt='消耗品库存有效区间开始'/>
						</td>
					</tr>
					<tr class="gap">
					</tr>
					<tr>
						<td class="ui-state-default td-title">OGZ 现行基准值</td>
						<td class="td-content">
						   <label id="label_ogz_foreboard_count"></label>
						</td>
						<td class="ui-state-default td-title">OGZ 基准值设定</td>
						<td class="td-content">
						   <input type="text" class="ui-widget-content" id="edit_ogz_setting" name="edit_ogz_setting" alt="OGZ 设定基准值" style="width:5em;text-align:right;"></input>
						</td>
						<td class="td-content">
							<input name='ogz_end_date' class="ui-widget-content" id='edit_ogz_end_date' type='text' alt='OGZ有效区间开始'/>
						</td>
					</tr>
				</table>
			</form>
		</div>

		<div id="partial_level" style="display: none; width: 580px; height: 295px; margin-top: 4px;" class="ui-widget-content">
			<table id="partial_base_line_value_list2"></table>
			<div id="partial_base_line_value_list2pager"></div>
		</div>

		<div id="detail" style="display: none; width: 580px; height: 295px; margin-top: 4px;" class="ui-widget-content">
			<table class="ui-jqgrid-htable" style="border-collapse: collapse; margin-left: 4px;">
				<tr>
					<td class="ui-state-default ui-th-column ui-th-ltr" colspan="4" style="text-align: center;">周期平均使用量</td>
				<tr>
				<tr>
					<td class="ui-state-default ui-th-column ui-th-ltr"></td>
					<td class="ui-state-default ui-th-column ui-th-ltr">半年</td>
					<td class="ui-state-default ui-th-column ui-th-ltr">3个月</td>
					<td class="ui-state-default ui-th-column ui-th-ltr">当月</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">第一梯队</td>
					<td class="td-content"><label id="label_echelon_01_halfyear_count"></label></td>
					<td class="td-content"><label id="label_echelon_01_threemonth_count"></label></td>
					<td class="td-content"><label id="label_echelon_01_curmonth_count"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">第二梯队</td>
					<td class="td-content"><label id="label_echelon_02_halfyear_count"></label></td>
					<td class="td-content"><label id="label_echelon_02_threemonth_count"></label></td>
					<td class="td-content"><label id="label_echelon_02_curmonth_count"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">第三梯队</td>
					<td class="td-content"><label id="label_echelon_03_halfyear_count"></label></td>
					<td class="td-content"><label id="label_echelon_03_threemonth_count"></label></td>
					<td class="td-content"><label id="label_echelon_03_curmonth_count"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">第四梯队</td>
					<td class="td-content"><label id="label_echelon_04_halfyear_count"></label></td>
					<td class="td-content"><label id="label_echelon_04_threemonth_count"></label></td>
					<td class="td-content"><label id="label_echelon_04_curmonth_count"></label></td>
				</tr>
				<tr style="border-top:2px solid black">
					<td class="ui-state-default td-title">合计</td>
					<td class="td-content"><label id="label_echelon_halfyear_count"></label></td>
					<td class="td-content"><label id="label_echelon_threemonth_count"></label></td>
					<td class="td-content"><label id="label_echelon_curmonth_count"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title" style="width:112px;">其中非标使用</td>
					<td class="td-content"><label id="label_not_stand_use_of_halfyear"></label></td>
					<td class="td-content"><label id="label_not_stand_use_of_threemon"></label></td>
					<td class="td-content"><label id="label_not_stand_use_of_curmon"></label></td>
				</tr>
			</table>
			<table class="condform nk">
				<tr style="margin:0;padding:0;">
					<td class="ui-state-default td-title" style="width:200px;">标配拉动数合计</td>
					<td class="td-content">
					   <label id="lable_total_of_standard_use"></label>
					</td>
					<td class="ui-state-default td-title" rowspan="2">本周期使用量合计</td>
					<td class="td-content" rowspan="2">
					   <label id="use_count"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">设定非标使用量</td>
					<td class="td-content">
						<label id="lable_non_bom_safty_count"></label>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="container"
		style="float: right; min-width: 400px; height: 290px; margin-top: 30px;"></div>
</div>

