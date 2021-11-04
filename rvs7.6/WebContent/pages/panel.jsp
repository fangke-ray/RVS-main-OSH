<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" 
import="java.util.List" 
isELIgnored="false"%>
<%@page import="com.osh.rvs.bean.LoginData"%>
<%@page import="com.osh.rvs.common.RvsConsts"%>
<%@page import="com.osh.rvs.bean.master.LineEntity"%>
<%@page import="com.osh.rvs.bean.master.SectionEntity"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Page-Exit"; content="blendTrans(Duration=1.0)">
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<link rel="stylesheet" type="text/css" href="css/custom.css?v=3090">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/app/panel.js"></script>
<style>
	span.panel-message-title {
		font-size : 16px;
		padding-left : 4px;
	}
	#panelarea div.ui-widget-content {
		padding : 6px;
	}
	#panelarea > div.ui-widget-content:not(:first-child) {
		margin-top : 6px; 
	}
	#panelarea .ui-button-text-only .ui-button-text {
		padding: .4em 1em !important;
	}
	#panelarea .ui-buttonset .ui-button {
		margin-right: 0em;
	}
</style>

<title>设置面板</title>
</head>
<body class="outer" style="align: center;">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="3"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all dwidth-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="panel"/>
				</jsp:include>
			</div>
			<div style="width: 798px; float: left;">
				<div id="body-mdl" class="dwidth-middle" style="margin: 0;">
					<div id="panelarea" class="dwidth-middle">
<% 
	LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
	String role_id = user.getRole_id();
%>
						<div class="ui-widget-content">
							<div class="ui-state-default">
								<span class="panel-message-title">请选择您当前的工作课室</span>
							</div>
<%
	String section_id = user.getSection_id();
	boolean showSection3 = false;
%>
							<div class="ui-widget-content">
								<span id="sections">
<%
	if (!"00000000009".equals(role_id) && !"00000000012".equals(role_id)) { 
	List<SectionEntity> sections = user.getSections();
	for (SectionEntity section : sections) {
		if (section.getSection_id() != null) {
%>
									<input type="radio" name="section" id="section_<%=section.getSection_id()%>" value="<%=section.getSection_id()%>" <%=(section.getSection_id().equals(user.getSection_id())) ? "checked" : ""%>>
									<label for="section_<%=section.getSection_id()%>"><span><%=section.getName()%></span></label>
<%
									if ("00000000012".equals(section.getSection_id()) && "00000000012".equals(user.getSection_id())) showSection3 = true;
		}
	}
	} else {
%>
									<input type="radio" name="section" value="00000000001" id="section_00000000001" <% if ("00000000001".equals(section_id)) { %>checked<% } %>><label for="section_00000000001"><span>翻修1课</span></label>
									<input type="radio" name="section" value="00000000003" id="section_00000000003" <% if ("00000000003".equals(section_id)) { %>checked<% } %>><label for="section_00000000003"><span>翻修2课</span></label>
									<input type="radio" name="section" value="00000000012" id="section_00000000012" <% if ("00000000012".equals(section_id)) { %>checked<% } %>><label for="section_00000000012"><span>翻修3课</span></label>
									<input type="radio" name="section" value="00000000006" id="section_00000000006" <% if ("00000000006".equals(section_id)) { %>checked<% } %>><label for="section_00000000006"><span>支援课</span></label>
									<input type="radio" name="section" value="00000000007" id="section_00000000007" <% if ("00000000007".equals(section_id)) { %>checked<% } %>><label for="section_00000000007"><span>品保课</span></label>
									<input type="radio" name="section" value="00000000009" id="section_00000000009" <% if ("00000000009".equals(section_id)) { %>checked<% } %>><label for="section_00000000009"><span>报价物料课</span></label>
<%
	}
%>
								</span>
							</div>
						</div>
<%
	if ("00000000005".equals(role_id) || "00000000012".equals(role_id) || "00000000009".equals(role_id)) { 
%>
						<div class="ui-widget-content">
							<div class="ui-state-default">
								<span class="panel-message-title">请选择您当前的工程</span>
							</div>
<%
	String line_id = user.getLine_id();
%>
							<div class="ui-widget-content">
								<span id="lines">
									<input type="radio" name="line" value="00000000012" id="line_0000000012" <% if ("00000000012".equals(line_id)) { %>checked<% } %>><label for="line_0000000012"><span>分解</span></label>
									<input type="radio" name="line" value="00000000013" id="line_0000000013" <% if ("00000000013".equals(line_id)) { %>checked<% } %>><label for="line_0000000013"><span>ＮＳ</span></label>
									<input type="radio" name="line" value="00000000014" id="line_0000000014" <% if ("00000000014".equals(line_id)) { %>checked<% } %>><label for="line_0000000014"><span>总组</span></label>
								</span>
							</div>
						</div>
<%
	} else if ("00000000006".equals(role_id) || "00000000001".equals(role_id) || "00000000002".equals(role_id) || "00000000007".equals(role_id) || "00000000008".equals(role_id)) { 
%>
						<div class="ui-widget-content">
							<div class="ui-state-default">
								<span class="panel-message-title">请选择您当前的工程</span>
							</div>
							<div class="ui-widget-content" style="min-height: 120px;">
								<span id="lines">
<%
//	List<PositionEntity> positions = user.getPositions();
//	for (PositionEntity position : positions) {
//		if (position.getPosition_id() != null) {
	List<LineEntity> lines = user.getLines();
	for (LineEntity line : lines) {
		if (line.getLine_id() != null) {
			String line_id = line.getLine_id();
%>
									<input type="radio" name="line" id="line_<%=line_id%>" value="<%=line_id%>" <%=(line_id.equals(user.getLine_id())) ? "checked" : ""%>>
<%
			if (showSection3 && line_id.equals("00000000014")) {
%>
									<label for="line_<%=line_id%>"><span>维修工程</span></label>
<%
			} else {
%>
									<label for="line_<%=line_id%>"><span><%=line.getName()%></span></label>
<%
			}
		} 
	} 
%>
								</span>
							</div>
						</div>
<%
	} 
%>						<div class="ui-widget-content" id="oldbrowser" style="display: none;">
							<div class="ui-state-default">
								<span class="panel-message-title">浏览器版本提示</span>
							</div>
							<div class="ui-widget-content">
								<p>你正在使用的浏览器版本过低、这可能导致:</p>
								<p>1.画面流畅度变低</p>
								<p>2.一部分页面效果无法展示</p>
								<p>3.工作页面需要手动刷新以显示最新状况</p>
								<p>4.展示界面无法做到即时反映</p>
								<p>请升级到以下版本的浏览器使用本系统.</p>
								<p>Chrome 4+ Firefox 4+ Internet Explorer 10+ Opera 10+ Safari 5+</p>
							</div>
						</div>
						<div class="ui-widget-content" id="system_verison" style="">
							<div class="ui-widget-content">
								<p>系统版本:</p>
								<p id="nee">8.5.3425.466</p>
								<p>&nbsp;</p>
								<p>发布时间:</p>
								<p>2021年5月13日</p>
							</div>
						</div>
<%
	if ("00000000012".equals(role_id)) { 
%>
						<div class="ui-widget-content" id="system_verison_content">
							<div class="ui-widget-content" style="max-height: 560px;overflow: auto;">
								<p>--------------------------------------</p>
								<p>21/10/24 9.2.3445.474 更新</p>
								<p>报价/流水线/物料: 工作指示单无纸化（首单订购）</p>
								<p>--------------------------------------</p>
								<p>21/10/24 9.1.3444.471 更新</p>
								<p>设备管理: 设备治具菜单分类修改</p>
								<p>出货：出货单条码扫描</p>
								<p>报价：UDI直送检查票划分</p>
								<p>--------------------------------------</p>
								<p>21/9/30 9.1.3441.471 更新</p>
								<p>全局：系统安全升级对应</p>
								<p>报价/流水线: 动物内镜库位增加</p>
								<p>--------------------------------------</p>
								<p>21/9/17 9.1.3439.471 更新</p>
								<p>受理/出货：通箱库位增加</p>
								<p>品保：周边返品分析工位追加</p>
								<p>--------------------------------------</p>
								<p>21/9/9 9.1.3437.471 更新</p>
								<p>物料：PDA通箱出库显示等级</p>
								<p>流水线: 翻修2课新线对应</p>
								<p>流水线: 烘箱增加类别</p>
								<p>流水线: 离岗证配置</p>
								<p>全局：借用课室权限设置</p>
								<p>流水线: 311移动到分解工程</p>
								<p>--------------------------------------</p>
								<p>21/7/12 9.1.3433.471 更新</p>
								<p>接口: 零件BOM信息与SAP主数据对接</p>
								<p>报价：特殊型号特殊时段再修理对应提示</p>
								<p>--------------------------------------</p>
								<p>21/6/21 9.1.3430.470 更新</p>
								<p>品保：分析表编号 字段扩张</p>
								<p>物料：已发放消耗品单也需要显示裁剪长度</p>
								<p>报价：光学视管在报价阶段填写检查票</p>
								<p>接口: SAP报价单接口（rvs006）对接</p>
								<p>--------------------------------------</p>
								<p>21/5/13 8.5.3425.466 更新</p>
								<p>共通: 财务年度更改相关</p>
								<p>设备管理: 借用设备工具</p>
								<p>共通: 管理员/支援课导出维修品时增加字段</p>
								<p>接口: 受理接口更新了动物内镜用标记</p>
								<p>零件管理: 记录零件缺品原因功能</p>
								<p>投线: 周边/UDI的投线后判定零件</p>
								<p>点检: 归档规则调整（斜线划掉未敲章部分）</p>
								<p>--------------------------------------</p>
								<p>21/3/10 8.5.3413.463 更新</p>
								<p>共通: 日历功能可直选年月</p>
								<p>生产管理: 总组库位自动分配/提示</p>
								<p>受理: 消毒灭菌方式更改</p>
								<p>--------------------------------------</p>
								<p>21/2/18 8.5.3409.463 更新</p>
								<p>生产管理: 动物实验用维修品-独立维修流程</p>
								<p>--------------------------------------</p>
								<p>20/12/14 8.5.3406.462 更新</p>
								<p>品保: 报价完成周边检查票pdf化</p>
								<p>生产管理/接口: 不良新现象提交</p>
								<p>生产管理: 作业要领书管理/个人书单</p>
								<p>受理: 实物受理记录界面及相关对应</p>
								<p>品保: 最终检查通过自动记录确认信息</p>
								<p>--------------------------------------</p>
								<p>20/10/15 8.3.3396.459 更新</p>
								<p>流水线: 分解流水线改造</p>
								<p>报价: 直接作业人员代工间接作业人员功能</p>
								<p>受理/流水线: 动物内镜用维修品标记与提示</p>
								<p>--------------------------------------</p>
								<p>20/09/14 8.3.3382.459 更新</p>
								<p>流水线: 总组流水线改造（中小修单元化）</p>
								<p>系统管理: 标准工时全型号工位列表导出</p>
								<p>报价/出货/流水线: 周边未修理返回流程追加</p>
								<p>流水线: 中小修零件未齐投线进入PA。</p>
								<p>流水线: 关注工位页面顶部提示。</p>
								<p>--------------------------------------</p>
								<p>20/08/10 8.2.3372.450 更新</p>
								<p>物料: NS 组件订购与库位管理。</p>
								<p>流水线: NS 组件组装流程/NS 组件签收使用。</p>
								<p>--------------------------------------</p>
								<p>20/03/02 7.10.3340.444 更新</p>
								<p>受理: 协助RC进行CDS功能上线。</p>
								<p>品保: 出检结果作业实现查询。</p>
								<p>--------------------------------------</p>
								<p>19/10/29 7.10.3317.435 更新</p>
								<p>受理/物料: 间接人员作业作业月度图表。</p>
								<p>--------------------------------------</p>
								<p>19/09/23 7.10.3315.429 更新</p>
								<p>受理/物料: 间接人员作业作业明细报表。</p>
								<p>--------------------------------------</p>
								<p>19/09/02 7.10.3310.424 更新</p>
								<p>受理/物料: 间接人员作业计时上线。</p>
								<p>--------------------------------------</p>
								<p>19/07/30 7.9.3292.417 更新</p>
								<p>设备管理: 设备管理提示照片等功能。</p>
								<p>设备管理: 设备工具PA展示功能。</p>
								<p>--------------------------------------</p>
								<p>19/06/10 7.9.3287.417 更新</p>
								<p>设备管理: 设备工具替代设置及运用。</p>
								<p>受理报价: 中小修理代码关联参考流程切换提示。</p>
								<p>--------------------------------------</p>
								<p>19/02/27 7.9.3281.417 更新</p>
								<p>受理报价: 通箱库位扩充。</p>
								<p>维修进度: 今日总组各工位计划 -> 今日总组各工位负荷。</p>
								<p>--------------------------------------</p>
								<p>19/01/03 7.9.3279.414 更新</p>
								<p>现品: 光学视管投线前判断零件，按照机种区分。</p>
								<p>维修进度: 今日总组各工位计划上线。</p>
								<p>--------------------------------------</p>
								<p>18/11/29 7.9.3272.413 更新</p>
								<p>受理报价: 报价LT统计修改。</p>
								<p>受理报价: 维修品取消受理通箱取出提示。</p>
								<p>维修进度/品保: 光学视管修理。</p>
								<p>--------------------------------------</p>
								<p>18/10/22 7.9.3263.412 更新</p>
								<p>消耗品: 热缩管剪裁请求功能。</p>
								<p>文档: 工作月报明细不显示加班预定时间。</p>
								<p>--------------------------------------</p>
								<p>18/10/18 7.9.3261.411 更新</p>
								<p>--------------------------------------</p>
								<p>18/09/10 7.9.3256.410 更新</p>
								<p>--------------------------------------</p>
								<p>18/08/05 7.9.3245.407 更新</p>
								<p>维修进度: 流水线再编成第三批次。</p>
								<p>物料: 定期清除最终未提交的即时领用申请单。</p>
								<p>--------------------------------------</p>
								<p>18/07/11 7.9.3243.405 更新</p>
								<p>物料: 消耗品申请单(日常)发放(PDA)端问题修正。</p>
								<p>维修进度/管理: 总组工程分线产能/配备人数/实际作业人数设置和显示。</p>
								<p>--------------------------------------</p>
								<p>18/07/03 7.9.3237.402 更新</p>
								<p>物料: 消耗品发放记录。消耗品目标值设定。消耗量TOP 10导出。</p>
								<p>物料: 消耗品申请单显示价格。</p>
								<p>物料: 消耗品申请单(日常)按各人需要填写，自动定时发出工程申请单。</p>
								<p>物料: 消耗品仓库库存/消耗一览导出。</p>
								<p>零件: 零件价格上传下载。</p>
								<p>--------------------------------------</p>
								<p>18/05/18 7.8.3214.393 更新</p>
								<p>维修进度: 钢丝固定件清洗。</p>
								<p>物料: 消耗品入库显示消耗品说明。</p>
								<p>--------------------------------------</p>
								<p>18/05/02 7.7.3199.389 更新</p>
								<p>报价/维修进度: CCD 线更换实现改修(维修流程派生设定)。</p>
								<p>维修进度: 流水线再编成第二批次。</p>
								<p>物料: 消耗品出库单等信息显示排序需求。</p>
								<p>--------------------------------------</p>
								<p>18/04/01 7.6.3186.387 更新</p>
								<p>维修进度: 151P KPI数据更新。</p>
								<p>--------------------------------------</p>
								<p>18/03/16 7.5.3184.384 更新</p>
								<p>零件: 零件BO预判定。</p>
								<p>维修进度: 工场诊断，流水线再编成。</p>
								<p>--------------------------------------</p>
								<p>18/01/10 7.4.3168.380 更新</p>
								<p>零件: 未修理返还时，如果是周边或小修理，则取消零件订购。</p>
								<p>受理报价/维修进度: CCD线更换实装。</p>
								<p>维修进度: 先端预制烘干显示问题修正。</p>
								<p>--------------------------------------</p>
								<p>17/12/12 7.4.3154.377 更新</p>
								<p>受理报价/现品: 报价人员不可修改客户同意日,会得到同意日变更提醒。投线人员不可修改客户同意日。</p>
								<p>维修进度/点检: 周边点检项目可复选型号。</p>
								<p>--------------------------------------</p>
								<p>17/11/23 7.4.3147.370 更新</p>
								<p>文档: 新月报功能。</p>
								<p>--------------------------------------</p>
								<p>17/09/21 7.3.3129.349 更新</p>
								<p>维修进度: 同工位各线等待作业信息显示。</p>
								<p>维修进度: 倒计时未达成原因填写。</p>
								<p>维修进度: 倒计时未达成原因查询。</p>
								<p>现品: 通箱库位扩张。</p>
								<p>--------------------------------------</p>
								<p>17/08/30 7.2.3122.344 更新</p>
								<p>文档: 月度有效工时统计比率表再细化。</p>
								<p>文档: 生产工时损失诊断报表建立。</p>
								<p>文档: 生产线产能模拟报告建立。</p>
								<p>受理报价: 复数机身号查询。</p>
								<p>品保: ETQ情报，使用历史追加。</p>
								<p>--------------------------------------</p>
								<p>17/08/23 7.2.3120.344 更新</p>
								<p>维修进度: 维修对象排顺序规则改变。</p>
								<p>维修进度: 零件清点不允许全选。</p>
								<p>受理报价: 中修理默认先分解流程。</p>
								<p>--------------------------------------</p>
								<p>17/07/28 7.1.3111.340 更新</p>
								<p>维修进度: 作业间隔时间填写强化。</p>
								<p>--------------------------------------</p>
								<p>17/06/23 7.0.3090.333 更新</p>
								<p>维修进度: 新生产线合并对应。</p>
								<p>--------------------------------------</p>
								<p>17/04/28 6.10.3068.331 更新</p>
								<p>维修进度: 故障排除。</p>
								<p>--------------------------------------</p>
								<p>17/03/10 6.9.3042.330 更新</p>
								<p>维修进度: 时间管理修改版。</p>
								<p>管理: 中小修、周边计划排定功能完善
								<p>管理: 工时报表填写标准化数据辞典更新
								<p>品保: 出检、返品分析项目追加。</p>
								<p>--------------------------------------</p>
								<p>16/12/27 6.8.2950.312 更新</p>
								<p>维修进度: 时间管理修改版。</p>
								<p>管理: 工时分析、生产效率、流水线平衡率分析、代工时间统计、等待时间及中断时间统计
								<p>品保: 最终检查追加确认功能。</p>
								<p>--------------------------------------</p>
								<p>16/11/28 6.7.2850.309 更新</p>
								<p>管理: 按工位排倒计时计划
								<p>--------------------------------------</p>
								<p>16/09/8 6.6.2768.287 更新</p>
								<p>维修进度: 时间管理上线。</p>
								<p>维修进度: 烘干作业管理上线。</p>
								<p>报价/现品: 通箱库位展示上线。</p>
								<p>报价/现品: 投线辅助排定。</p>
								<p>--------------------------------------</p>
								<p>16/06/21 6.5.2663.247 更新</p>
								<p>维修进度: 周边设备维修上线。</p>
								<p>点检: 周边设备用设备工具点检。</p>
								<p>--------------------------------------</p>
								<p>16/06/13 6.4.2637.243 更新</p>
								<p>现品: 通箱管理。</p>
								<p>维修进度: 先端来源追溯表在线观看。</p>
								<p>--------------------------------------</p>
								<p>16/05/20 6.3.2610.243 更新</p>
								<p>全局: 性能优化。</p>
								<p>维修进度: 先端预制库存来源追溯。</p>
								<p>--------------------------------------</p>
								<p>16/05/04 6.2.2587.243 更新</p>
								<p>品保: 质量信息单号等标记。</p>
								<p>维修进度: 先端预制库存管理增加。</p>
								<p>报价/现品: 通箱库位管理前期功能准备。</p>
								<p>--------------------------------------</p>
								<p>16/04/05 6.1.2580.242 更新</p>
								<p>维修进度: 小修理仕挂通知增加。</p>
								<p>维修进度: 先端预制零件签收流程改造。</p>
								<p>零件管理: 预制零件管理功能增加。</p>
								<p>--------------------------------------</p>
								<p>16/02/26 5.3.2567.240 更新</p>
								<p>维修进度: 混合流水线维修管理</p>
								<p>受理报价: WIP工作记录表</p>
								<p>--------------------------------------</p>
								<p>15/12/09 5.3.2561.236 更新</p>
								<p>维修进度: 混合流水线维修</p>
								<p>--------------------------------------</p>
								<p>15/11/02 5.2.2548.231 更新</p>
								<p>零件: 消耗品库存显示调整。</p>
								<p>全局: SAP上线后各种对应调整。</p>
								<p>--------------------------------------</p>
								<p>15/11/02 5.1.2522.231 更新</p>
								<p>全局: SAP上线前各种对应调整。</p>
								<p>--------------------------------------</p>
								<p>15/10/22 3.15.2517.229 更新</p>
								<p>零件: 显示消耗品数量。</p>
								<p>维修进度: 维修备注功能添加。</p>
								<p>维修进度: 投线后进行CCD 盖玻璃改换。</p>
								<p>维修进度: 小修理返工BUG修正。</p>
								<p>--------------------------------------</p>
								<p>15/09/14 3.14.2474.229 更新</p>
								<p>报价/维修进度: 小修理进入流水线。</p>
								<p>点检: 替换新品BUG修正。</p>
								<p>--------------------------------------</p>
								<p>15/07/02 3.13.2360.226 更新</p>
								<p>报价: 受理及受理报价线长界面可选择“直送快速”。</p>
								<p>点检: 一些归档BUG修正。</p>
								<p>--------------------------------------</p>
								<p>15/06/26 3.12.2353.226 更新</p>
								<p>文档管理: 归档画面检索条件追加。</p>
								<p>维修进度/查询: NFM接口对应。</p>
								<p>--------------------------------------</p>
								<p>15/06/24 3.12.2341.225 更新</p>
								<p>报价/维修进度: 可选择“直送快速”，按照直送快速标准计算纳期。</p>
								<p>--------------------------------------</p>
								<p>15/06/18 3.11.2289.225 更新</p>
								<p>消耗品: 消耗品申请/发放提交后提醒未显示的问题修正。</p>
								<p>消耗品: 现品人员发放消耗品后提醒信息自动消去。</p>
								<p>品保: 单元维修品终检可选择不通过，返回终检等待区。</p>
								<p>品保: 返品QIS增加了QIS判定项目。</p>
								<p>品保: 品保管理员可取消已开始的判定作业。</p>
								<p>--------------------------------------</p>
							</div>
						</div>
<%
	} 
%>
						<div class="clear"></div>
					</div>
				</div>
			</div>
			<div style="width: 224px; float: left;">
				<div id="body-rgt" class="dwidth-right" style="margin-left: 8px;"></div>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>
	</div>

</body>
</html>