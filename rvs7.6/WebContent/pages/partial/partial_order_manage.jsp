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
<script type="text/javascript" src="js/partial/partial_order_manage.js"></script>

<title>零件订购管理</title>
</head>
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
					<jsp:param name="linkto" value="零件辅助功能"/>
				</jsp:include>
			</div>
<!--判断权限人员是谁 -->
<input type="hidden" id="judge_status" value=${role}>
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;float:left;">
<div id="body-top">
		<div id="searcharea">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">检索条件</span>
			</div>

			<div class="ui-widget-content dwidth-middleright">
					<form id="uploadform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content"><input type="text" id="search_sorcno" maxlength="15" class="ui-widget-content"></td>
									<td class="ui-state-default td-title">零件订购日</td>
									<td class="td-content"><input name="" id="search_order_date_start" maxlength="50" class="ui-widget-content" readonly="readonly" type="text">起<br><input name="" id="search_order_date_end" maxlength="50" class="ui-widget-content" readonly="readonly" type="text">止</td>
									<td class="ui-state-default td-title">BO状态</td>
									<td class="td-content" id="bo_flg">
										<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">(全)</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_f" class="ui-widget-content ui-helper-hidden-accessible" value="9"><label for="cond_work_procedure_order_template_f" aria-pressed="false">无BO</label>
										<input type="radio" name="bo" id="cond_work_procedure_order_template_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_work_procedure_order_template_t" aria-pressed="false">有BO</label>
										<input type="hidden" id="cond_work_procedure_order_template">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">检索范围</td>
									<td class="td-content">
										<select id="search_range" class="ui-widget-content">
												<option value="">全部</option>
												<option value="1">在线维修</option>
												<option value="2">历史</option>
										</select>
									</td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content"></td>
									<td class="ui-state-default td-title"></td>
									<td class="td-content"></td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="rebutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
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
				
				<!--订购对象-订购零件 jqgrid-->
				<div class="partial_list">
				<table id="exd_list"></table>
				<div id="exd_listpager"></div>
				</div>

				<div class="ui-widget-header areabase"style="width:992px;padding-top:4px;margin-top:0px;">
				    <div id="executes" style="margin-left:4px;margin-top:3px;">
					<input type="button" id="come_back_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="返回" role="button" style="float:right;margin-right:6px;">
					<input type="button" id="delete_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="删除" role="button" style="float:right;margin-right:6px;">
				    <input type="button" id="cancel_distrubute_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="取消发放" role="button" style="float:right;margin-right:6px;">
				    <input type="button" id="cancel_order_button" class="ui-button ui-widget ui-state-default ui-corner-all"  value="取消订购" role="button" style="float:right;margin-right:6px;">
				    <input type="button" id="no_bo_button" class="ui-button"  value="无BO" role="button" style="float:right;margin-right:6px;">
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
								<div class="ui-jqgrid-sortable">数量</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 100px;">
								<div class="ui-jqgrid-sortable">定位工位</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 100px;">
								<div class="ui-jqgrid-sortable">订购者</div>
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