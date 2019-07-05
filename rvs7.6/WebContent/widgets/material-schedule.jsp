<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<script type="text/javascript">
	$("#scheduled_expedited").buttonset();
	$("#am_pm").buttonset();
</script>

<title>维修对象基本信息</title>
</head>

<body>
<div id="material_detail_content" style="margin:auto;">

<div style="height:44px;width:770px;" id="material_detail_infoes" class="dwidth-full">

	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_base" role="button" checked><label for="material_detail_infoes_base" title="">基本信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_product" role="button"><label for="material_detail_infoes_product" title="">作业信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_change" role="button"><label for="material_detail_infoes_change">变更信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_partical" role="button"><label for="material_detail_infoes_partical">零件信息</label>

</div>
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_base" style="width:786px;">
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
						<input type="text" id="edit_sorc_no" class="ui-widget-content" maxlength="15">
					</td>
					<td class="ui-state-default td-title">ESAS No.</td>
					<td class="td-content">
						<label id="label_esas_no"></label>
						<input type="text" id="edit_esas_no" class="ui-widget-content" maxlength="6">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="label_model_name"></label>
						<input type="text" class="ui-widget-content" readonly="readonly" id="inp_modelname">
						<input type="hidden" id="edit_model_name">
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label id="label_serial_no"></label>
						<input type="text" id="edit_serial_no" class="ui-widget-content" maxlength="20">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">委托处</td>
					<td class="td-content">
						<label id="label_ocmName"></label>
						<select name="edit_ocm" id="edit_ocm" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_ocm", null, "", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">通箱编号</td>
					<td class="td-content">
						<label id="label_package_no"></label>
						<input type="text" id="edit_package_no" class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content">
						<label id="label_level_name"></label>
						<select name="edit_level" id="edit_level" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_level", null, "", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">修理课室</td>
					<td class="td-content">
						<label id="label_section_name"></label>
						<select name="section_id" id="edit_section_id" class="ui-widget-content">
							<%=session.getAttribute("sOptions") %>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">加急</td>
					<td class="td-content">
						<label id="label_scheduled_expedited"></label>
						<select id="edit_scheduled_expedited" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_scheduled_expedited", null, "否", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content">
						<label id="label_remark"></label>
						<select id="direct" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_direct", null, "(普通)", false) %>
						</select>
						<select id="service_repair" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_service_repair", null, "", false) %>
						</select>
						<select id="fix_type" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_fix_type", null, "", false) %>
						</select>
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
		<div class="ui-widget-content dwidth-middle">
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">当前状态</td>
					<td class="td-content"><label id="label_status"></label></td>
					<td class="ui-state-default td-title">当前位置</td>
					<td class="td-content"><label id="label_wip_location"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">受理日期</td>
					<td class="td-content">
						<label id="label_reception_time"></label>
						<input type="text" id="edit_reception_time" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">报价日期</td>
					<td class="td-content"><label id="label_finish_time"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">客户同意日期</td>
					<td class="td-content">
						<label id="label_agreed_date"></label>
						<input type="text" id="edit_agreed_date" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">投线日期</td>
					<td class="td-content">
						<label id="label_inline_time"></label>
						<input type="text" id="edit_inline_time" class="ui-widget-content">
					</td>
				</tr>
				<tr id="tr_dec_date">
					<td class="ui-state-default td-title">分解产出安排</td>
					<td class="td-content">
						<label id="label_dec_plan_date"></label>
						<input type="text" id="edit_dec_plan_date" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">分解实际产出</td>
					<td class="td-content">
						<label id="label_dec_finish_date"></label>
						<input type="text" id="edit_dec_finish_date" class="ui-widget-content"/>
					</td>
				</tr>
				<tr id="tr_ns_date">
					<td class="ui-state-default td-title">NS产出安排</td>
					<td class="td-content">
						<label id="label_ns_plan_date"></label>
						<input type="text" id="edit_ns_plan_date" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">NS实际产出</td>
					<td class="td-content">
						<label id="label_ns_finish_date"></label>
						<input type="text" id="edit_ns_finish_date" class="ui-widget-content"/>
					</td>
				</tr>
				<tr id="tr_com_date">
					<td class="ui-state-default td-title">总组产出安排</td>
					<td class="td-content">
						<label id="label_com_plan_date"></label>
						<input type="text" id="edit_com_plan_date" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">总组实际产出</td>
					<td class="td-content">
						<label id="label_com_finish_date"></label>
						<input type="text" id="edit_com_finish_date" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">完成日期</td>
					<td class="td-content">
						<label id="label_outline_time"></label>
						<input type="text" id="edit_outline_time" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title" id="td_scheduled_date">纳期</td>
					<td class="td-content" id="td_scheduled_date_value"><label id="label_scheduled_date"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3">
						<label id="label_scheduled_manager_comment"></label>
						<textarea id="edit_scheduled_manager_comment" style="width:350px;height:80px;"></textarea>
						
						<select id="edit_am_pm" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_time",null,"",false) %>
						</select>
					</td>
				</tr>
			</tbody>
			</table>
		</div>
		<div class="clear" style="height:22px;"></div>
		<!-- 维修对象作业信息 -->
	</div>

</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_product" style="width:786px;display:none;" lazyload="./material-detail-product.html">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_change" style="width:786px;display:none;" lazyload="./material-detail-changes.html">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_partical" style="width:786px;display:none;" lazyload="./material-detail-partical.html">
	
	<div id="material_detail_paticalarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">维修对象零件信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle"><!-- lazy load-->
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">零件BO</td>
					<td class="td-content">
						<label id="label_bo_flg"></label>
						<input type="text" id="edit_bo_flg">
					</td>
					<td class="ui-state-default td-title">零件订购日</td>
					<td class="td-content">
						<label id="label_order_date"></label>
						<input type="text" id="edit_order_date">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">入库预定日</td>
					<td class="td-content">
						<label id="label_arrival_plan_date"></label>
						<input type="text" id="edit_arrival_plan_date">
					</td>
					<td class="ui-state-default td-title">零件到货日</td>
					<td class="td-content">
						<label id="label_arrival_date"></label>
						<input type="text" id="edit_arrival_date">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">缺品零件</td>
					<td class="td-content" colspan="3">
						<label id="label_bo_contents"></label>
						<label id="label_bo_contents1">分解缺品零件:</label>
						<input type="text" id="edit_bo_contents1"><br>
						<label id="label_bo_contents2">NS缺品零件:</label>
						<input type="text" id="edit_bo_contents2" style="margin-left: 7px;"><br>
						<label id="label_bo_contents3">总组缺品零件:</label>
						<input type="text" id="edit_bo_contents3">
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

</body>