<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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

<style>
    .abilities { width: 200px; float: right; right:30px; padding: 2px; }
    .abilities ul { margin: 0; padding: 1em 0 1em 3em; }
    .myability {list-style-type:none;}
    .myability:before {color:#FFb300;font-size:16px;cursor: pointer;}
    .myability[star='4']:before {content:'★★★★ ';}
    .myability[star='3']:before {content:'★★★☆ ';}
    .myability[star='2']:before {content:'★★☆☆ ';}
    .myability[star='1']:before {content:'★☆☆☆ ';}
    .myability[star='0']:before {content:'☆☆☆☆ ';}
    #star-select {cursor: pointer;}

	#editform .subform .ui-state-active > .ui-state-highlight:after {
		content: ' ！';
		position: absolute;
		color: coral;
	}
</style>
 
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/operator.js"></script>
<title>用户检索</title>
</head>
<body>


<div id="searcharea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tbody>
				<tr>
					<td class="ui-state-default td-title">用户 ID</td>
					<td class="td-content"><input type="text" name="id" id="cond_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">用户姓名</td>
					<td class="td-content"><input type="text" name="name" id="cond_name" maxlength="8" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">工号</td>
					<td class="td-content"><input type="text" name="job_no" id="cond_job_no" maxlength="8" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">所在课室</td>
					<td class="td-content">
						<select name="section_id" id="cond_section_id" class="ui-widget-content">
							<%=request.getAttribute("sOptions")%>
						</select>
					</td>
					<td class="ui-state-default td-title">所在工程</td>
					<td class="td-content">
						<select name="line_id" id="cond_line_id" class="ui-widget-content">
							<%=request.getAttribute("lOptions")%>
						</select>
					</td>
					<td class="ui-state-default td-title">角色</td>
					<td class="td-content">
						<select name="role_id" id="cond_rank_kind" class="ui-widget-content">
							<%=request.getAttribute("rkOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">负责工位</td>
					<td class="td-content">
						<input type="text" id="cond_position_name" class="ui-widget-content">
						<input type="hidden" id="hidden_position_id">
					</td>
					<td class="ui-state-default td-title">间接作业能力</td>
					<td class="td-content" colspan="3">
						<input type="text" id="cond_af_ability" class="ui-widget-content">
						<input type="hidden" id="hidden_af_ability_code">
					</td>
				</tr>
				</tbody>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="hidden" id="rkGo" value="${rkGo}"/>
			</div>
		</form>
	</div>
	<div class="clear dwidth-middleright"></div>

	<div id="search_position_name_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
		 <table>
			<tbody>
			   <tr>
					<td width="50%">过滤字:<input type="text"></td>	
					<td align="right" width="50%"><input class="ui-button" style="float:right;" value="清空" type="button"></td>
			   </tr>
		   </tbody>
	  	 </table>
	  	 <table class="subform">${pReferChooser}</table>
	</div>
	<div id="search_af_ability_referchooser" class="referchooser ui-widget-content" tabindex="-1" style="display:none;position:absolute;z-index:10000">
		 <table>
			<tbody>
			   <tr>
					<td width="50%">过滤字:<input type="text"></td>	
					<td align="right" width="50%"><input class="ui-button" style="float:right;" value="清空" type="button"></td>
			   </tr>
		   </tbody>
	  	 </table>
	  	 <table class="subform">${afReferChooser}</table>
	</div>

</div>

<div id="listarea" class="width-middleright">
	<table id="list"></table>
	<div id="listpager"></div>
</div>

<div id="editarea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="editform" method="POST" onsubmit="return false;">
			<div style="float:left;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">用户 ID</td>
					<td class="td-content"><label id="label_edit_id"> - </label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">主要角色</td>
					<td class="td-content">
						<select name="role_id" alt="主要角色" id="input_role_id" class="ui-widget-content">
							<%=request.getAttribute("rOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">兼任角色</td>
					<td class="td-content">
						<select name="roles_id" alt="兼任角色" multiple id="input_roles_id" class="ui-widget-content">
							<%=request.getAttribute("rOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">工号</td>
					<td class="td-content">
						<input type="text" name="job_no" alt="工号" id="input_job_no" maxlength="8" class="ui-widget-content">
						<label id="input_label_job_no"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">用户姓名</td>
					<td class="td-content">
						<input type="text" name="name" alt="用户姓名" id="input_name" maxlength="8" class="ui-widget-content">
						<img id="img_job_no" src=""/>
					</td>
				</tr>
				<!--tr>
					<td class="ui-state-default td-title">旧密码</td>
					<td class="td-content">
						<input type="password" name="old_password" id="input_old_password" maxlength="50" class="ui-widget-content">
						<br>
						<label for="input_old_password" generated="false" class="errorarea-single" style="display:none">
						</label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">新密码</td>
					<td class="td-content">
						<input type="password" name="password" id="input_password" maxlength="50" class="ui-widget-content">
						<br>
						<label for="input_password" generated="false" class="errorarea-single" style="display:none">
						</label>
					</td>
				</tr-->
				<tr>
					<td class="ui-state-default td-title">帐号分类</td>
					<td class="td-content">
						<select name="account_type" alt="帐号分类" id="input_account_type" class="ui-widget-content">
							<%=request.getAttribute("atOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">所在课室</td>
					<td class="td-content">
						<input type="hidden">
						<select name="section_id" alt="所在课室" id="input_section_id" class="ui-widget-content">
							<%=request.getAttribute("sOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">所在工程</td>
					<td class="td-content">
						<input type="hidden">
						<select name="line_id" alt="所在工程" id="input_line_id" class="ui-widget-content">
							<%=request.getAttribute("lOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">邮箱地址</td>
					<td class="td-content">
						<input type="text" alt="邮箱地址" name="email" id="input_email" maxlength="50" class="ui-widget-content">
					</label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">分线</td>
					<td class="td-content">
						<select name="px" alt="分线" id="input_px" class="ui-widget-content">
							<%=request.getAttribute("pxOptions")%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新时间</td>
					<td class="td-content"><label id="label_edit_updated_time"></label></td>
				</tr>
			</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_edit_main_position">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:180px;">主要负责工位</th>
					</tr>
					<%=request.getAttribute("pReferChooser")%>
				</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_edit_positions">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:180px;">可负责工位</th>
					</tr>
					<%=request.getAttribute("pReferChooser")%>
				</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_edit_af_abilities">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:160px;">间接作业能力</th>
					</tr>
					<%=request.getAttribute("afReferChooser")%>
				</table>
			</div>
			<div class="clear" style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</div>
		</form>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div id="confirmmessage"></div>
</body>
</html>
