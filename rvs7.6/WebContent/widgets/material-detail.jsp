<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<body>
<div id="material_detail_content" style="margin:auto;">

<%
	boolean fromPartial = (request.getAttribute("from_partial") != null);
	boolean showDjLoan = (request.getAttribute("showDjLoan") != null);
%>

<div style="height:44px;width:770px;" id="material_detail_infoes" class="dwidth-full">

	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_base" role="button" <%=fromPartial ? "" : "checked"%>><label for="material_detail_infoes_base" title="">基本信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_product" role="button"><label for="material_detail_infoes_product" title="">作业信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_change" role="button" style="display: none"><label for="material_detail_infoes_change" style="display: none;">变更信息</label>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_partical" role="button" <%=fromPartial ? "checked" : ""%>><label for="material_detail_infoes_partical">零件信息</label>
<% if (showDjLoan) { %>
	<input type="radio" name="material_detail_infoes" class="ui-button ui-corner-up" id="material_detail_infoes_dj_loan" role="button"><label for="material_detail_infoes_dj_loan">设备工具清洗记录</label>
<% } %>
</div>
<input type="hidden" id="global_material_id"><input type="hidden" id="global_occur_times">
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_base" style="width:786px;text-align:left;">
	<div id="material_detail_basearea" style="margin-top:22px;margin-left:9px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">修理品基本信息</span>
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
							${lOptions}
						</select>
					</td>
					<td class="ui-state-default td-title">修理课室</td>
					<td class="td-content">
						<label id="label_section_name"></label>
						<select name="section_id" id="edit_section_id" class="ui-widget-content">
							${sOptions}
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理流程</td>
					<td class="td-content" colspan="3">
						<select name="edit_pat" id="edit_pat" class="ui-widget-content">
							${patOptions}
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">加急</td>
					<td class="td-content">
						<label id="label_scheduled_expedited"></label>
						<select id="edit_scheduled_expedited" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_scheduled_expedited", null, "0::(普通)", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content">
						<label id="label_remark"></label>
						<select id="direct" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_direct", null, "0::(普通)", false) %>
						</select>
						<select id="service_repair" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_service_repair", null, "0:: ", false) %>
						</select>
						<select id="fix_type" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_fix_type", null, "0:: ", false) %>
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
			<span class="areatitle">修理品作业信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle" id="old_info">
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
<% 
Boolean hasDecom = (Boolean) request.getAttribute("hasDecom");
if (hasDecom!=null && hasDecom) {
%>
				<tr id="tr_dec_date">
					<td class="ui-state-default td-title">分解产出安排</td>
					<td class="td-content">
						<label id="label_dec_plan_date"></label>
						<input type="text" id="edit_dec_plan_date" class="ui-widget-content" readonly/>
					</td>
					<td class="ui-state-default td-title">分解实际产出</td>
					<td class="td-content">
						<label id="label_dec_finish_date"></label>
						<input type="text" id="edit_dec_finish_date" class="ui-widget-content" readonly/>
					</td>
				</tr>
				<tr id="tr_ns_date">
					<td class="ui-state-default td-title">NS产出安排</td>
					<td class="td-content">
						<label id="label_ns_plan_date"></label>
						<input type="text" id="edit_ns_plan_date" class="ui-widget-content" readonly/>
					</td>
					<td class="ui-state-default td-title">NS实际产出</td>
					<td class="td-content">
						<label id="label_ns_finish_date"></label>
						<input type="text" id="edit_ns_finish_date" class="ui-widget-content" readonly/>
					</td>
				</tr>
<% 
}
%>
				<tr id="tr_com_date">
					<td class="ui-state-default td-title">总组产出安排</td>
					<td class="td-content">
						<label id="label_com_plan_date"></label>
						<input type="text" id="edit_com_plan_date" class="ui-widget-content" readonly/>
					</td>
					<td class="ui-state-default td-title">总组实际产出</td>
					<td class="td-content">
						<label id="label_com_finish_date"></label>
						<input type="text" id="edit_com_finish_date" class="ui-widget-content" readonly/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">品保完成日期</td>
					<td class="td-content">
						<label id="label_outline_time"></label>
						<input type="text" id="edit_outline_time" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">纳期</td>
					<td class="td-content"><label id="label_scheduled_date"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3" style="position:relative;">
						<label id="label_scheduled_manager_comment"></label>
						<textarea id="edit_scheduled_manager_comment" style="width:350px;height:80px;"></textarea>
						
						<select id="edit_am_pm" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_time",null,"",false) %>
						</select>

						<div id="optional_fix_items" style="position:absolute;top:0;right:1em;">
							<div class="ui-state-default" style="padding-left:.5em;">选择修理项目</div>
							<table style="width:10em;">
							</table>
						</div>
<%
String level = (String)request.getAttribute("level");
if(level != null && ("1".equals(level) || "2".equals(level))) {
%>
						<br>
						<textarea id="edit_material_comment_other" style="width:350px;height:80px;resize:none;" readonly disabled></textarea>
	<% if ("1".equals(level)) { %>
						<br>
						<textarea id="edit_material_comment" style="width:350px;height:40px;resize:none;"></textarea>
						<br>
						<input type="button" class="ui-button" value="记录" onclick="javascript:updateMaterialComment();">
						<script>
							var updateMaterialComment = function() {
								var postData = {
									material_id : $("#global_material_id").val(),
									comment : $("#edit_material_comment").val()
								}
								$.ajax({
									beforeSend : ajaxRequestType,
									async : false,
									url : 'material.do?method=doUpdateMaterialComment',
									cache : false,
									data : postData,
									type : "post",
									dataType : "json",
									success : ajaxSuccessCheck,
									error : ajaxError,
									complete : function(xhrObj) {
										var resInfo = $.parseJSON(xhrObj.responseText);
										if (resInfo.errors && resInfo.errors.length > 0) {
											treatBackMessages(null, resInfo.errors);
											return;
										} else {
											$("#edit_material_comment").parents(".ui-dialog-content")
											.dialog('close');
										}
									}
								});
							}
						</script>
	<% } %>
<% } else { %>
						<br>
						<textarea id="edit_material_comment_other" style="width:350px;height:80px;resize:none;" readonly disabled></textarea>
						<br>
<% } %>
					</td>
				</tr>
			</tbody>
			</table>
		</div>
		
		<div class="ui-widget-content dwidth-middle" id="new_info" style="display: none;">
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">当前状态</td>
					<td class="td-content"><label><pre id="new_label_status"></pre></label></td>
					<td class="ui-state-default td-title">当前位置</td>
					<td class="td-content"><label id="new_label_processing_position"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">受理日期</td>
					<td class="td-content">
						<input type="text" id="new_edit_reception_time" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">客户同意日期</td>
					<td class="td-content">
						<input type="text" id="new_edit_agreed_date" class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">投线日期</td>
					<td class="td-content">
						<input type="text" id="new_edit_inline_time" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">完成日期</td>
					<td class="td-content">
						<input type="text" id="new_edit_outline_time" class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分解实际产出</td>
					<td class="td-content">
						<input type="text" id="new_edit_dec_finish_date" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">NS实际产出</td>
					<td class="td-content">
						<input type="text" id="new_edit_ns_finish_date" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">总组实际产出</td>
					<td class="td-content">
						<input type="text" id="new_edit_com_finish_date" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">纳期</td>
					<td class="td-content"><label id="new_label_scheduled_date"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注</td>
					<td class="td-content" colspan="3">
						<textarea id="new_edit_scheduled_manager_comment" style="width:350px;height:80px;"></textarea>
						<select id="new_edit_am_pm" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_time",null,"",false) %>
						</select>
					</td>
				</tr>
			</tbody>
			</table>
		</div>
		<div class="clear" style="height:22px;"></div>
		<!-- 修理品作业信息 -->
	</div>

</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_product" style="width:786px;text-align:center;display:none;" lazyload="widgets/material-detail-product.jsp">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_change" style="width:786px;text-align:center;display:none;" lazyload="./material-detail-changes.html">
</div>

<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_partical" style="width:786px;text-align:left;display:none;">
	
	<div id="material_detail_paticalarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">修理品零件订购信息</span>
		</div>
		<div class="ui-widget-content dwidth-middle"><!-- lazy load-->
			<table class="condform">
				<tbody>
				<!-- //2期修改
				<tr>
					<td class="ui-state-default td-title">缺品发生次数</td>
					<td class="td-content">
						<select	id="edit_occur_times">
							
						</select>
					</td>
					<td class="ui-state-default td-title">缺品发生工位</td>
					<td class="td-content">
						<label id="label_process_code"></label>
					</td>
				</tr>
				 -->
				<tr>
					<td class="ui-state-default td-title">零件BO</td>
					<td class="td-content">
						<label id="label_bo_flg"></label>
				<!-- //2期修改
						<select id="edit_bo_flg" class="ui-widget-content">
							<option value="1">有BO</option>
							<option value="0">无BO</option>
							<option value="2">BO解决</option>
						</select>
				 -->
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
					<!--div id="div_no_arrival_plan_date" style="float: left;">
						<input type="checkbox" id="edit_no_arrival_plan_date"><label for="edit_no_arrival_plan_date">未定</label>
					</div-->
						<label id="label_arrival_plan_date"></label>
						<input type="text" id="edit_arrival_plan_date">
					</td>
					<td class="ui-state-default td-title">零件到货日</td>
					<td class="td-content">
						<label id="label_arrival_date"></label>
						<!-- //2期修改
						<input type="text" id="edit_arrival_date">
						 -->
					</td>
				</tr>

				<tr id="tr_bo_contents">
					<td class="ui-state-default td-title">零件缺品原因</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents" style="width:600px;height:80px;"></textarea>
					</td>
				</tr>

						<!-- //2期修改
				<tr id="label_bo_contents1">
					<td class="ui-state-default td-title">分解缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents1" style="width:600px;height:80px;"></textarea>
					</td>
				</tr>
				<tr id="label_bo_contents2">
					<td class="ui-state-default td-title">ＮＳ缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents2" style="width:600px;height:80px;"></textarea>
					</td>
				</tr>
				<tr id="label_bo_contents3">
					<td class="ui-state-default td-title">总组缺品零件</td>
					<td class="td-content" colspan="3">
						<textarea id="edit_bo_contents3" style="width:600px;height:80px;"></textarea>
					</td>
				</tr>
						 -->
			</tbody>
			</table>
		</div>
		
		
	</div>
	
	<div id="material_detail_paticalarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
			<span class="areatitle">维修品使用零件一览</span>
		</div>
		<div class="ui-widget-content dwidth-middle">
				<div id="distributions">	
				    <input type="radio"  name="distributions" class="ui-button ui-corner-up" id="decompose_use_radio" value="12">
					<label for="decompose_use_radio">分解工程</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up" id="ns_use_radio" value="13">
					<label for="ns_use_radio">ＮＳ工程</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up" id="compose_use_radio" value="14">
					<label for="compose_use_radio">总组工程</label>
				</div>	
				
				<table id="exd_list"></table>
				<div id="exd_listpager"></div>
            </div>
		</div>	
</div>

<!-- 修理品变更信息 -->
<% if (showDjLoan) { %>

<!-- 维修违治具借用信息 -->
<div class="ui-widget-content material_detail_tabcontent" for="material_detail_infoes_dj_loan" style="width:786px;text-align:center;display:none;" lazyload="device_jig_loan.do?method=detailMaterial">
</div>

<% } %>

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