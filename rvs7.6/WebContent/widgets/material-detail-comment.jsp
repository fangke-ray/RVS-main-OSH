<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<body>
<div id="material_detail_content" style="margin:auto;">

<div style="height:44px;width:770px;" id="material_detail_infoes" class="dwidth-full">

	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_base" role="button" checked><label for="material_detail_infoes_base" title="">基本信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_product" role="button"><label for="material_detail_infoes_product" title="">作业信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_change" role="button" style="display: none"><label for="material_detail_infoes_change" style="display: none">变更信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_partical" role="button"><label for="material_detail_infoes_partical">零件信息</label>

</div>
<input type="hidden" id="global_material_id"><input type="hidden" id="global_occur_times">
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_base" style="width:786px;text-align:left;">
	<div id="material_detail_basearea" style="margin-top:22px;margin-left:9px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">维修对象基本信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle">
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content">
						<label id="label_sorc_no"></label>
					</td>
					<td class="ui-state-default td-title">ESAS No.</td>
					<td class="td-content">
						<label id="label_esas_no"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="label_model_name"></label>
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label id="label_serial_no"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">委托处</td>
					<td class="td-content">
						<label id="label_ocmName"></label>
					</td>
					<td class="ui-state-default td-title">通箱编号</td>
					<td class="td-content">
						<label id="label_package_no"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content">
						<label id="label_level_name"></label>
					</td>
					<td class="ui-state-default td-title">修理课室</td>
					<td class="td-content">
						<label id="label_section_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理流程</td>
					<td class="td-content" colspan="3">
						<label id="label_pat"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">加急</td>
					<td class="td-content">
						<label id="label_scheduled_expedited"></label>
					</td>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content">
						<label id="label_remark"></label>
					</td>
				</tr>
			</tbody>
			</table>
		</div>
		<div class="clear areaencloser dwidth-middle"></div>
	</div>

	<div id="material_detail_basearea" style="margin-left:9px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">维修对象作业信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle" id="info">
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">当前状态</td>
					<td class="td-content"><pre id="label_status" style="margin:0px"></pre></td>
					<td class="ui-state-default td-title">当前位置</td>
					<td class="td-content"><pre id="label_processing_position" style="margin:0px"></pre></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">受理日期</td>
					<td class="td-content">
						<label id="label_reception_time"></label>
					</td>
					<td class="ui-state-default td-title">报价日期</td>
					<td class="td-content"><label id="label_finish_time"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">客户同意日期</td>
					<td class="td-content">
						<label id="label_agreed_date"></label>
					</td>
					<td class="ui-state-default td-title">投线日期</td>
					<td class="td-content">
						<label id="label_inline_time"></label>
					</td>
				</tr>
				<tr id="tr_dec_date">
					<td class="ui-state-default td-title">分解产出安排</td>
					<td class="td-content">
						<label id="label_dec_plan_date"></label>
					</td>
					<td class="ui-state-default td-title">分解实际产出</td>
					<td class="td-content">
						<label id="label_dec_finish_date"></label>
					</td>
				</tr>
				<tr id="tr_ns_date">
					<td class="ui-state-default td-title">NS产出安排</td>
					<td class="td-content">
						<label id="label_ns_plan_date"></label>
					</td>
					<td class="ui-state-default td-title">NS实际产出</td>
					<td class="td-content">
						<label id="label_ns_finish_date"></label>
					</td>
				</tr>
				<tr id="tr_com_date">
					<td class="ui-state-default td-title">总组产出安排</td>
					<td class="td-content">
						<label id="label_com_plan_date"></label>
					</td>
					<td class="ui-state-default td-title">总组实际产出</td>
					<td class="td-content">
						<label id="label_com_finish_date"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">品保完成日期</td>
					<td class="td-content">
						<label id="label_outline_time"></label>
					</td>
					<td class="ui-state-default td-title">纳期</td>
					<td class="td-content"><label id="label_scheduled_date"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_scheduled_manager_comment" style="width:350px;height:80px;"></textarea>
						
						<label id="label_am_pm"></label>
						<br>
						<textarea id="edit_material_comment_other" style="width:350px;height:80px;resize:none;display:none;" readonly disabled></textarea>
						<br>
					</td>
				</tr>
			</tbody>
			</table>
		</div>

		<div class="clear" style="height:22px;"></div>
		<!-- 维修对象作业信息 -->
	</div>

</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_product" style="width:786px;text-align:center;display:none;" lazyload="widgets/material-detail-product.jsp">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_change" style="width:786px;text-align:center;display:none;" lazyload="./material-detail-changes.html">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_partical" style="width:786px;text-align:left;display:none;">
	
	<div id="material_detail_paticalarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">维修对象零件信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle"><!-- lazy load-->
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">追加发生次数</td>
					<td class="td-content">
					</td>
					<td class="ui-state-default td-title">缺品发生工位</td>
					<td class="td-content">
						<label id="label_process_code"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件BO</td>
					<td class="td-content">
						<label id="label_bo_flg"></label>
					</td>
					<td class="ui-state-default td-title">零件订购日</td>
					<td class="td-content">
						<label id="label_order_date"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">入库预定日</td>
					<td class="td-content">
						<label id="label_arrival_plan_date"></label>
					</td>
					<td class="ui-state-default td-title">零件到货日</td>
					<td class="td-content">
						<label id="label_arrival_date"></label>
					</td>
				</tr>
				<tr id="tr_bo_contents">
					<td class="ui-state-default td-title">缺品零件</td>
					<td class="td-content" colspan="3">
						<label id="label_bo_contents"></label>
					</td>
				</tr>
				<tr id="label_bo_contents1">
					<td class="ui-state-default td-title">分解缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents1" style="width:600px;height:80px;" readonly></textarea>
					</td>
				</tr>
				<tr id="label_bo_contents2">
					<td class="ui-state-default td-title">ＮＳ缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents2" style="width:600px;height:80px;" readonly></textarea>
					</td>
				</tr>
				<tr id="label_bo_contents3">
					<td class="ui-state-default td-title">总组缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents3" style="width:600px;height:80px;" readonly></textarea>
					</td>
				</tr>
			</tbody>
			</table>
		</div>
	</div>
</div>

<!-- 维修对象变更信息 -->


<div class="clear areacloser"></div>
</div>

<script type="text/javascript" src="js/common/material_detail_init_view.js"></script>

<div class="referchooser ui-widget-content" id="material_detial_refer" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=session.getAttribute("mReferChooser")%></table>
<div>
</body>