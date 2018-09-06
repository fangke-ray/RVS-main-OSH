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
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/partial/partial_order.js"></script>

<title>零件订购</title>
</head>
<% 
	String role = (String) request.getAttribute("role");
	boolean isPartialManager = ("partialManager").equals(role);
	boolean isXianpin = ("xianpin").equals(role);
%>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件订购/签收"/>
				</jsp:include>
			</div>
<!--判断权限人员是谁 -->
<input type="hidden" id="judge_status" value=${role}>
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;float:left;">
<div id="body-top">
		<div id="searcharea">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">零件订购文件导入</span>
			</div>

			<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">上传文件</td>
									<td class="td-content"><input type="file" name="file" alt="上传文件路径" id="order_detail_file" class="ui-widget-content" /></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="uploadbutton" value="载入" role="button" aria-disabled="false" style="float: right; right: 2px">
						</div>
					</form>
			</div>
        </div>

	   <div class="clear areaencloser"></div>
		  <!-- JqGrid表格  -->
		<div id="listarea" class="width-middleright">
			<table id="list"></table>
			<div id="listpager"></div>
		</div>
		
		<input type="hidden" id="oMateriaLevel" value="${oMateriaLevel }">

		<div class="ui-widget-header areabase" style="padding-top:4px; width:992px;margin-button:6px;margin-bottom: 16px;">
<% if (isPartialManager) { %>
				  <input type="button" class="ui-button-primary ui-button" id="submit_button" value="提交" role="button" aria-disabled="false" style="float: right; right: 2px">   
<% } %>   
     </div>
</div>

<div id="body-after" style="display:none;">

		<div id="after_view">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">维修对象基本信息</span>
			</div>

			<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">受理日</td>
									<td class="td-content"><label name="reception_time" id="qa_reception_time"></label></td>

									<td class="ui-state-default td-title">客户</td>
									<td class="td-content"><label name="customer" id="customer" ></label></td>
									<td class="ui-state-default td-title">同意日</td>
									<td class="td-content"><label name="agree_date" id="agree_date"></label></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content"><label name="sorc_no" id="sorc_no"></label></td>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content"><label name="model_name" id="model_name"></label></td>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content"><label name="serial_no" id="serial_no"></label></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修理等级</td>
									<td class="td-content"><label name="level" id=level></label></td>
									<td class="ui-state-default td-title">修理分类</td>
									<td class="td-content"><label name="service_free_flg" id="service_free_flg"></label></td>
									<td class="ui-state-default td-title">入库预定日</td>
									<td class="td-content"><label name="arrival_plan_date" id="arrival_plan_date"></label></td>
								</tr>
							</tbody>
						</table>
						  <input type="hidden" id="hidden_material_id"/>
						  <input type="hidden" id="hidden_model_id"/>
						  <input type="hidden" id="hidden_occur_times"/>
						  <input type="hidden" id="hidden_fix_type"/>
					</form>
			</div>
        </div>
		

	<div id="after_content">
	        <div class="clear areaencloser"></div>
			<!-- <div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">订购维修对象一览</span>
			</div> -->
			
			<div style="width: 991px; height: 44px; border-bottom: 0;"
				id="distributions" class="dwidth-full ui-widget-content">
				<div>
					<!-- <input type="radio" checked="checked" name="distributions" class="ui-button ui-corner-up" id="not_assigned_radio" value="0" role="button">
					<label for="not_assigned_radio">未定位</label> -->
					
					<input type="radio" name="distributions" class="ui-button ui-corner-up bad_addtional" id="bad_additional_radio" value="6" role="button">
					<label class="bad_addtional" for="bad_additional_radio">不良追加</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up normal_addtional" id="bom_spare_parts_radio" value="1" role="button">
					<label  class="normal_addtional" for="bom_spare_parts_radio">BOM零件</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up normal_addtional" id="compartment_additional_radio" value="2" role="button">
					<label  class="normal_addtional" for="compartment_additional_radio">分室追加</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up normal_addtional" id="offer_additional_radio" value="3" role="button">
					<label  class="normal_addtional" for="offer_additional_radio">报价追加</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up normal_addtional" id="decomposition_additional_radio" value="4" role="button">
					<label  class="normal_addtional" for="decomposition_additional_radio">分解追加</label>

					<input type="radio" name="distributions" class="ui-button ui-corner-up normal_addtional" id="ns_additional_radio" value="5" role="button">
					<label class="normal_addtional" for="ns_additional_radio">NS追加</label>		
                     
				</div>
				
				<!--second jqgrid-->
				<div class="partial_list">
				<table id="exd_list"></table>
				<div id="exd_listpager"></div>
				</div>
				
				<!-- bom jqgrid -->
				<div class="partial_list">
				<table id="bom_exd_list"></table>
				<div id="bom_exd_listpager"></div>
				</div>

				<div class="ui-widget-header areabase"style="width:992px;padding-top:4px;margin-top:0px;">
				    <div id="executes" style="margin-left:4px;margin-top:3px;">
					<input type="button" id="compartment_additional_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="分室追加分类" role="button" style="margin-left:6px;">

					<input type="button" id="offer_additional_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="报价追加分类" role="button" style="margin-left:6px;">

					<input type="button" id="decomposition_additional_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="分解追加分类" role="button" style="margin-left:6px;">

					<input type="button" id="ns_additional_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="NS追加分类" role="button" style="margin-left:6px;">
                    
					<input type="button" id="come_back_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="返回" role="button" style="float:right;margin-right:6px;">
<% if (isPartialManager) { %>
					<input type="button" id="confirm_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="确定" role="button" style="float:right;margin-right:6px;">
					<input type="button" id="cancel_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="取消导入数据" role="button" style="float:right;margin-right:6px;">
<%}%>
				   </div>
				</div>
		</div>	
</div>

</div>


				</div>

			<div class="clear areaencloser"></div>
		</div>
	

	<div id="footarea"></div>

<div id="confirmmessage"></div>
<form id="addtional_Manage" style="display:none">
 <table class="" style="width:300px" role="grid" aria-labelledby="gbox_exd_list" cellspacing="0" cellpadding="0" border="0">
					<thead>
						<tr class="ui-jqgrid-labels" role="rowheader">
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 100px;">
								<div class="ui-jqgrid-sortable">零件编号
								</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 100px;">
								<div class="ui-jqgrid-sortable">定位件数</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 100px;">
								<div class="ui-jqgrid-sortable">定位工位</div>
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
	</form>
</div>
</body>
</html>