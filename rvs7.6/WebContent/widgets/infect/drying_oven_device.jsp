<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/infect/drying_oven_device.js"></script>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%
	String role = (String)request.getAttribute("role");
	boolean isDTManage = ("deviceManage").equals(role);
%>
<!-- Tab选项卡 -->
<%
	if(!isDTManage){
%>
	<div style="border-bottom: 0;" class="ui-widget-content dwidth-middleright">
		<div id="tabs" class="ui-buttonset">
			<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_drying_oven_device_tab">
			<label for="page_drying_oven_device_tab">烘干设备管理</label>
			<input type="radio" name="infoes" class="ui-button ui-corner-up" id="page_drying_job_tab">
			<label for="page_drying_job_tab">烘干作业</label>
		</div>
	</div>
<%
	}
%>

<!-- 烘箱管理开始  -->
<div id="page_drying_oven_device">
	<!-- 烘箱一览开始-->
	<div id="drying_oven_device_init">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">检索条件</span>
		</div>
		
		<div class="ui-widget-content dwidth-middleright">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">管理编号</td>
						<td class="td-content">
							<input id="search_m_manage_code" class="ui-widget-content" type="text" readonly>
							<input type="hidden" id="hidden_search_m_device_manage_id"/>
						</td>
						<td class="ui-state-default td-title">设定温度</td>
						<td class="td-content">
							<select id="search_m_setting_temperature" class="ui-widget-content">${goDryingOvenSettingTemperature}</select>
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height: 44px">
				<input class="ui-button" id="reset_m_button" value="清除" style="float: right; right: 2px" type="button">
				<input class="ui-button" id="search_m_button" value="查询" style="float: right; right: 2px" type="button">
			</div>
		</div>
		
		<div class="clear areaencloser" ></div>
		
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">烘干设备管理一览</span>
		</div>
		
		<div class="width-middleright">
			<table id="drying_oven_device_list"></table>
			<div id="drying_oven_device_list_pager"></div>
		</div>
		<input type="hidden" id="hidden_sDryingOvenSettingTemperature" value="${sDryingOvenSettingTemperature }">
	</div>
	<!-- 烘箱一览结束-->
	<!-- 烘箱新建开始-->
	<div id="drying_oven_device_add" style="display: none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">新建烘干设备管理</span>
		</div>
		<div class="ui-widget-content">
			<form id="add_m_form" method="post">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">管理编号</td>
							<td class="td-content">
								<input id="add_m_manage_code" class="ui-widget-content" type="text" readonly>
								<input id="hidden_add_m_device_manage_id" name="device_manage_id" type="hidden" alt="管理编号">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">设定温度</td>
							<td class="td-content">
								<select id="add_m_setting_temperature" name="setting_temperature" class="ui-widget-content" alt="设定温度">${goDryingOvenSettingTemperature}</select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">库位数</td>
							<td class="td-content">
								<input id="add_m_slot" class="ui-widget-content" name="slot" type="text" alt="库位数">
							</td>
						</tr>
					</tbody>
				</table>
				<div style="height:44px">
					<input class=" ui-button" id="add_m_comfirmbutton" value="确认" style="float:left;left:4px;" type="button">
					<input class=" ui-button" id="add_m_resetbutton" value="返回" style="float:left;left:4px;" type="button">
				</div>
			</form>
		</div>
	</div>
	<!-- 烘箱新建结束-->
	<!-- 烘箱更新开始 -->
	<div id="drying_oven_device_update" style="display: none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">更新烘干设备管理</span>
		</div>
		<div class="ui-widget-content">
			<form id="update_m_form" method="post">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">管理编号</td>
							<td class="td-content">
								<label id="label_m_manage_code"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">型号名称</td>
							<td class="td-content">
								<label id="label_m_model_name"></label>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">设定温度</td>
							<td class="td-content">
								<select id="update_m_setting_temperature" name="setting_temperature" class="ui-widget-content" alt="设定温度">${goDryingOvenSettingTemperature}</select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">库位数</td>
							<td class="td-content">
								<input id="update_m_slot" class="ui-widget-content" name="slot" type="text" alt="库位数">
							</td>
						</tr>
					</tbody>
				</table>
				<div style="height:44px">
					<input class=" ui-button" id="update_m_comfirmbutton" value="确认" style="float:left;left:4px;" type="button">
					<input class=" ui-button" id="update_m_resetbutton" value="返回" style="float:left;left:4px;" type="button">
				</div>
			</form>
		</div>
	</div>
	<!-- 烘箱更新结束-->
	
	<!-- 检索管理编号referchooser -->
	<div class="referchooser ui-widget-content" id="search_m_device_manage_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${referChooser}</table>
	</div>
	
	<!-- 新建管理编号referchooser -->
	<div class="referchooser ui-widget-content" id="add_m_device_manage_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${referChooser}</table>
	</div>
</div>
<!-- 烘箱管理结束  -->

<%
	if(!isDTManage){
%>
<!-- 烘干作业开始 -->
<div id="page_drying_job" style="display: none;">
	<!-- 烘干作业一览开始 -->
	<div id="page_drying_job_init">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">检索条件</span>
		</div>
		
		<div class="ui-widget-content dwidth-middleright">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">作业内容</td>
						<td class="td-content">
							<input id="search_j_content" class="ui-widget-content" type="text">
						</td>
						<td class="ui-state-default td-title">硬化条件</td>
						<td class="td-content">
							<select id="search_j_hardening_condition" class="ui-widget-content">${goDryingHardeningCondition}</select>
						</td>
						<td class="ui-state-default td-title">工位</td>
						<td class="td-content">
							<input id="search_j_position_name" class="ui-widget-content" type="text" readonly>
							<input id="hidden_search_j_position_id" class="ui-widget-content" type="hidden">
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height: 44px">
				<input class="ui-button" id="reset_j_button" value="清除" style="float: right; right: 2px" type="button">
				<input class="ui-button" id="search_j_button" value="查询" style="float: right; right: 2px" type="button">
			</div>
		</div>
		
		<div class="clear areaencloser" ></div>
		
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">烘干作业一览</span>
		</div>
		
		<div class="width-middleright">
			<table id="drying_job_list"></table>
			<div id="drying_job_pager"></div>
		</div>
		
		<input type="hidden" id="hidden_sDryingHardeningCondition" value="${sDryingHardeningCondition }">
	</div>
	<!-- 烘干作业新建开始 -->
	<div id="page_drying_job_add" style="display: none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">新建烘干作业</span>
		</div>
		<div class="ui-widget-content">
			<form id="add_j_form" method="post">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">工位</td>
							<td class="td-content">
													<input id="add_j_position_name" class="ui-widget-content" type="text" readonly>
								<input id="hidden_add_j_position_id" name="position_id" type="hidden" alt="工位">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">作业内容</td>
							<td class="td-content">
								<input id="add_j_content" name="content" class="ui-widget-content" type="text" alt="作业内容">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">干燥时间</td>
							<td class="td-content">
								<input id="add_j_drying_time" name="drying_time" class="ui-widget-content" type="text" alt="干燥时间"> 
													分钟<br>(0 : 代表时间不定)
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">硬化条件</td>
							<td class="td-content">
								<select id="add_j_hardening_condition" name="hardening_condition" class="ui-widget-content" alt="硬化条件">${goDryingHardeningCondition}</select>
							</td>
						</tr>
						<tr class="appoint">
							<td class="ui-state-default td-title">使用设备</td>
							<td class="td-content">
													<input id="add_j_device_manage_code" class="ui-widget-content" type="text" readonly>
								<input id="hidden_add_j_device_manage_id" type="hidden">
							</td>
						</tr>
						<tr class="appoint">
							<td class="ui-state-default td-title">指定库位</td>
							<td class="td-content ">
								<div class="ui-buttonset">
									<input type="radio" name="add_j_radio_slots" class="ui-button" id="add_j_no_appoint">
									<label for="add_j_no_appoint">不指定</label>
									<input type="radio" name="add_j_radio_slots" class="ui-button" id="add_j_appoint">
									<label for="add_j_appoint">指定</label>
								</div>
								<select id="add_j_slots" multiple="multiple"></select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">机种</td>
							<td class="td-content ">
								<select id="add_j_category_id"  class="ui-widget-content" multiple="multiple">${cReferChooser}</select>
							</td>
						</tr>
					</tbody>
				</table>
				<div style="height:44px">
					<input class=" ui-button" id="add_j_comfirmbutton" value="确认" style="float:left;left:4px;" type="button">
					<input class=" ui-button" id="add_j_resetbutton" value="返回" style="float:left;left:4px;" type="button">
				</div>
			</form>
		</div>
	</div>
	<!-- 烘干作业新建结束 -->
	<!-- 烘干作业更新开始 -->
	<div id="page_drying_job_update" style="display: none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">更新烘干作业</span>
		</div>
		<div class="ui-widget-content">
			<form id="update_j_form" method="post">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">工位</td>
							<td class="td-content">
													<input id="update_j_position_name" class="ui-widget-content" type="text" readonly>
								<input id="hidden_update_j_position_id" name="position_id" type="hidden" alt="工位">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">作业内容</td>
							<td class="td-content">
								<input id="update_j_content" name="content" class="ui-widget-content" type="text" alt="作业内容">
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">干燥时间</td>
							<td class="td-content">
													<input id="update_j_drying_time" name="drying_time" class="ui-widget-content" type="text" alt="干燥时间">
													分钟<br>(0 : 代表时间不定)
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">硬化条件</td>
							<td class="td-content">
								<select id="update_j_hardening_condition" name="hardening_condition" class="ui-widget-content" alt="硬化条件"></select>
							</td>
						</tr>
						<tr class="appoint">
							<td class="ui-state-default td-title">使用设备</td>
							<td class="td-content">
													<input id="update_j_device_manage_code" class="ui-widget-content" type="text" readonly>
								<input id="hidden_update_j_device_manage_id" type="hidden">
							</td>
						</tr>
						<tr class="appoint">
							<td class="ui-state-default td-title">指定库位</td>
							<td class="td-content ">
								<div class="ui-buttonset">
									<input type="radio" name="update_j_radio_slots" class="ui-button" id="update_j_no_appoint">
									<label for="update_j_no_appoint">不指定</label>
									<input type="radio" name="update_j_radio_slots" class="ui-button" id="update_j_appoint">
									<label for="update_j_appoint">指定</label>
								</div>
								<select id="update_j_slots" multiple="multiple"></select>
							</td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">机种</td>
							<td class="td-content ">
								<select id="update_j_category_id"  class="ui-widget-content" multiple="multiple">${cReferChooser}</select>
							</td>
						</tr>
					</tbody>
				</table>
				<div style="height:44px">
					<input class=" ui-button" id="update_j_comfirmbutton" value="确认" style="float:left;left:4px;" type="button">
					<input class=" ui-button" id="update_j_resetbutton" value="返回" style="float:left;left:4px;" type="button">
				</div>
			</form>
		</div>
		
	</div>
	<!-- 烘干作业更新结束 -->
	<!-- 检索工位referchooser -->
	<div class="referchooser ui-widget-content" id="search_j_position_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${pReferChooser}</table>
	</div>
	
	<!-- 新建工位referchooser -->
	<div class="referchooser ui-widget-content" id="add_j_position_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${pReferChooser}</table>
	</div>
	
	<!-- 更新工位referchooser -->
	<div class="referchooser ui-widget-content" id="update_j_position_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform">${pReferChooser}</table>
	</div>
	
	<!--  新建烘干作业 使用设备referchooser -->
	<div class="referchooser ui-widget-content" id="add_j_device_manage_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform"></table>
	</div>
	
	<!--  更新烘干作业 使用设备referchooser -->
	<div class="referchooser ui-widget-content" id="update_j_device_manage_id_referchooser" tabindex="-1">
		<table width="200px">
			<tr>
				<td></td>
				<td width="50%">过滤字:<input type="text"/></td>
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		<table  class="subform"></table>
	</div>
	
	<!-- 烘干作业一览结束 -->
</div>
<!-- 烘干作业结束 -->
<%
	}
%>
<div id="confirmmessage"></div>