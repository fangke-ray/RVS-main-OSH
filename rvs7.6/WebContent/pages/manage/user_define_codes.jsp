<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">

<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>

<script type="text/javascript" src="js/manage/user_define_codes.js"></script>
<title>用户定义</title>
</head>
<div style="width: 994px; float: left;">
<div id="page_radios">
<input type="radio" name="s_page" id="page_ud" checked/><label for="page_ud">作业环境配置</label><input type="radio" name="s_page" id="page_ia"/><label for="page_ia">作业标准配置（受理组间接作业）</label><input type="radio" name="s_page" id="page_if"/><label for="page_if">作业标准配置（物料组间接作业）</label>
</div>

	<div id="listarea">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">用户定义一览</span>
		</div>
		<table id="user_define_codes_list"></table>
		<div id="user_define_codes_listpager"></div>
	</div>
<style>
p\:shaperange {
	position:relative;height: 480px;display: block;font-size: 16px;
	background-color: white;
}
p\:shaperange .O {
	border: 1px solid #F79746;
	height: 1em;
	padding : 2px 0 4px 2px;
	position:absolute; 
}
p\:shaperange .O[content] {left:3.55%;width:20%;}
p\:shaperange .O[by_what] {left:23.55%;width:10%;}
p\:shaperange .O[standard] {left:33.55%;width:60%;}
p\:shaperange .O[line="1"] {top:5%;background-color:#FFC000;}
p\:shaperange .O[line="2"] {top:10%;}
p\:shaperange .O[line="3"] {top:15%;}
p\:shaperange .O[line="4"] {top:20%;}
p\:shaperange .O[line="5"] {top:25%;}
p\:shaperange .O[line="6"] {top:30%;}
p\:shaperange .O[line="7"] {top:35%;}
p\:shaperange .O[line="8"] {top:40%;}
p\:shaperange .O[line="9"] {top:45%;}
p\:shaperange .O[line="10"] {top:50%;}
p\:shaperange .O[line="11"] {top:55%;}
p\:shaperange .O[line="12"] {top:60%;}
p\:shaperange .O[line="13"] {top:65%;}
p\:shaperange .O[line="14"] {top:70%;}
p\:shaperange .O[line="15"] {top:75%;}
p\:shaperange .O[line="16"] {top:80%;}
p\:shaperange .O[line="17"] {top:85%;}
p\:shaperange .O[line="18"] {top:90%;}
p\:shaperange .O span, p\:shaperange .O a{
	font-family:微软雅黑; font-size:89%;
}
p\:shaperange input {
	height: 1em;
	border : 0;
	border-bottom : 2px solid #FFC000;
	width : 2em;
	padding-right: .8em;
	text-align: right;
}
p\:shaperange input[updated] {
	color: #FF00BF;
	font-weight: bold;
	text-shadow: 2px 2px bisque;
}
p\:shaperange button {
	position:absolute!important; 
	right: 20px;
	bottom: 20px;
}
</style>
<p:shaperange>

<div class="O" line=1 content><span lang="ZH-CN">作业内容</span></div>
<div class="O" line=1 by_what><span style="color:black;" lang="ZH-CN">计算单位</span></div>
<div class="O" line=1 standard><span style="color:black;" lang="ZH-CN">标准工时(单位：分钟)</span></div>

<div class="O" line=2 content><span lang="ZH-CN">票单打印</span></div>
<div class="O" line=2 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=2 standard><span lang="ZH-CN">维修品</span><input type="text" name="TICKET_PER_MAT"></div>

<div class="O" line=3 content><span lang="ZH-CN">实物受理</span></div>
<div class="O" line=3 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=3 standard><span lang="ZH-CN">维修品</span><input type="text" name="FACT_RECEPT_PER_MAT"></div>

<div class="O" line=4 content><span lang="ZH-CN">测漏</span></div>
<div class="O" line=4 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=4 standard><span lang="ZH-CN">维修品/备品</span><input type="text" name="FACT_TESTMR_PER_MAT"></div>

<div class="O" line=5 content><span lang="ZH-CN">系统受理/清点</span></div>
<div class="O" line=5 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=5 standard><span lang="ZH-CN">内窥镜</span><input type="text" name="SYS_RECEPT_ENSC_PER_MAT"><span lang="ZH-CN">周边设备</span><input type="text" name="SYS_RECEPT_PERI_PER_MAT"><span lang="ZH-CN">手术器械</span><input type="text" name="SYS_RECEPT_SURGI_PER_MAT"><span lang="ZH-CN">备品附件</span><input type="text" name="SYS_RECEPT_ACCENA_PER_MAT"></div>

<div class="O" line=6 content><span lang="ZH-CN">系统导入</span></div>
<div class="O" line=6 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=6 standard><span lang="ZH-CN">备品</span><input type="text" name="UPLOAD_SPARE_PER_PRO"></div>

<div class="O" line=7 content><span lang="ZH-CN">实物受理</span></div>
<div class="O" line=7 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=7 standard><span lang="ZH-CN">备品内窥镜</span><input type="text" name="FACT_RECEPT_ENSC_SPARE_PER_MAT"><span lang="ZH-CN">周边设备</span><input type="text" name="FACT_RECEPT_PERI_SPARE_PER_MAT"><span lang="ZH-CN">手术器械</span><input type="text" name="FACT_RECEPT_SURGI_SPARE_PER_MAT"><span lang="ZH-CN">备品附件</span><input type="text" name="FACT_RECEPT_ACCENA_SPARE_PER_MAT"></div>

<div class="O" line=8 content><span lang="ZH-CN">入库搬运</span></div>
<div class="O" line=8 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=8 standard><span lang="ZH-CN">周转箱</span><input type="text" name="TC_INSTOR_TRANS_PER_PRO"></div>

<div class="O" line=9 content><span lang="ZH-CN">入库上架操作</span></div>
<div class="O" line=9 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=9 standard><span lang="ZH-CN">周转箱</span><input type="text" name="TC_INSTOR_ONSH_PER_MAT"></div>

<div class="O" line=10 content><span lang="ZH-CN">消毒(设备)</span></div>
<div class="O" line=10 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=10 standard><span lang="ZH-CN">内窥镜</span><input type="text" name="DISINF_ENSC_PER_MAT"><span lang="ZH-CN">周边设备</span><input type="text" name="DISINF_PERI_PER_MAT"><span lang="ZH-CN">手术器械</span><input type="text" name="DISINF_SURGI_PER_MAT"><span lang="ZH-CN">备品附件</span><input type="text" name="DISINF_ACCENA_PER_MAT"></div>

<div class="O" line=11 content><span lang="ZH-CN">消毒(手动)</span></div>
<div class="O" line=11 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=11 standard><span lang="ZH-CN">维修品/备品</span><input type="text" name="DISINF_MANUAL_PER_MAT"></div>

<div class="O" line=12 content><span lang="ZH-CN">消毒(周转箱)</span></div>
<div class="O" line=12 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=12 standard><span lang="ZH-CN">周转箱</span><input type="text" name="DISINF_TC_PER_MAT"></div>

<div class="O" line=13 content><span lang="ZH-CN">灭菌</span></div>
<div class="O" line=13 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=13 standard><span lang="ZH-CN">维修品/备品</span><input type="text" name="STERI_PER_PRO"></div>

<div class="O" line=14 content><span lang="ZH-CN">出库搬运</span></div>
<div class="O" line=14 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=14 standard><span lang="ZH-CN">周转箱</span><input type="text" name="TC_OUTSTOR_TRANS_PER_PRO"></div>

<div class="O" line=15 content><span lang="ZH-CN">出库下架操作</span></div>
<div class="O" line=15 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=15 standard><span lang="ZH-CN">周转箱</span><input type="text" name="TC_OUTSTOR_OFFSH_PER_MAT"></div>

<div class="O" line=16 content><span lang="ZH-CN">出货包装</span></div>
<div class="O" line=16 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=16 standard><span lang="ZH-CN">维修品</span><input type="text" name="PACKAGE_PER_MAT"></div>

<div class="O" line=17 content><span lang="ZH-CN">出货装车</span></div>
<div class="O" line=17 by_what><span lang="ZH-CN">每车件数</span></div>
<div class="O" line=17 standard><span lang="ZH-CN">内窥镜</span><input type="text" name="SHIPPING_ENSC_CNT2_TROLLEY"><span lang="ZH-CN">周边设备</span><input type="text" name="SHIPPING_PERI_CNT2_TROLLEY"><span lang="ZH-CN">光学视管</span><input type="text" name="SHIPPING_UDI_CNT2_TROLLEY"></div>

<div class="O" line=18 content><span lang="ZH-CN">出货搬运</span></div>
<div class="O" line=18 by_what><span lang="ZH-CN">车数</span></div>
<div class="O" line=18 standard><span lang="ZH-CN">每台车</span><input type="text" name="SHIPPING_PER_TROLLEY"></div>

<button>更新受理组间接作业标准工时</button>
</p:shaperange>
<p:shaperange>
<div class="O" line=1 content><span lang="ZH-CN">作业内容</span></div>
<div class="O" line=1 by_what><span style="color:black;" lang="ZH-CN">计算单位</span></div>
<div class="O" line=1 standard><span style="color:black;" lang="ZH-CN">标准工时(单位：分钟)</span></div>

<div class="O" line=2 content><span lang="ZH-CN">投线系统操作</span></div>
<div class="O" line=2 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=2 standard><span lang="ZH-CN">维修品</span><input type="text" name="INLINE_PER_MAT"></div>

<div class="O" line=3 content><span lang="ZH-CN">投线整理/运输内镜</span></div>
<div class="O" line=3 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=3 standard><span lang="ZH-CN">维修品(车)</span><input type="text" name="DISTRIB_PER_PRO"></div>

<div class="O" line=4 content><span lang="ZH-CN">零件收货（运输）</span></div>
<div class="O" line=4 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=4 standard><span lang="ZH-CN">入库零件</span><input type="text" name="PART_RECPET_TRANS_PER_PRO"></div>

<div class="O" line=5 content><span lang="ZH-CN">零件收货（开箱）</span></div>
<div class="O" line=5 by_what><span lang="ZH-CN">箱数</span></div>
<div class="O" line=5 standard><a href="adminmenu.do#partial">到零件入出库工时标准设置</a></div>

<div class="O" line=6 content><span lang="ZH-CN">上传零件入库文档</span></div>
<div class="O" line=6 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=6 standard><span lang="ZH-CN">入库零件</span><input type="text" name="UPLOAD_PART_RECPET_PER_PRO"></div>

<div class="O" line=7 content><span lang="ZH-CN">入库零件核对/上架</span></div>
<div class="O" line=7 by_what><span lang="ZH-CN">零件数</span></div>
<div class="O" line=7 standard><a href="adminmenu.do#partial">到零件入出库工时标准设置</a></div>

<div class="O" line=8 content><span lang="ZH-CN">入库零件分装</span></div>
<div class="O" line=8 by_what><span lang="ZH-CN">零件数</span></div>
<div class="O" line=8 standard><a href="adminmenu.do#partial">到零件入出库工时标准设置</a></div>

<div class="O" line=9 content><span lang="ZH-CN">维修零件订购单编辑</span></div>
<div class="O" line=9 by_what><span lang="ZH-CN">每单·次数</span></div>
<div class="O" line=9 standard><span lang="ZH-CN">维修品零件订单</span><input type="text" name="PART_ORDER_PER_MAT"></div>

<div class="O" line=10 content><span lang="ZH-CN">维修零件发放下架</span></div>
<div class="O" line=10 by_what><span lang="ZH-CN">零件数</span></div>
<div class="O" line=10 standard><a href="adminmenu.do#partial">到零件入出库工时标准设置</a></div>

<div class="O" line=11 content><span lang="ZH-CN">维修零件发放派送</span></div>
<div class="O" line=11 by_what><span lang="ZH-CN">每单·次数</span></div>
<div class="O" line=11 standard><span lang="ZH-CN">NS 工程</span><input type="text" name="PART_RELEASE_NS_PER_MAT"><span lang="ZH-CN">NS 以外工程</span><input type="text" name="PART_RELEASE_NO_NS_PER_MAT"></div>

<div class="O" line=12 content><span lang="ZH-CN">出货单制作</span></div>
<div class="O" line=12 by_what><span lang="ZH-CN">单数</span></div>
<div class="O" line=12 standard><span lang="ZH-CN">维修品</span><input type="text" name="SHIPPING_ORDER_PER_MAT"></div>

<div class="O" line=13 content><span lang="ZH-CN">未修理返送</span></div>
<div class="O" line=13 by_what><span lang="ZH-CN">件数</span></div>
<div class="O" line=13 standard><span lang="ZH-CN">维修品</span><input type="text" name="UNREPAIR_RETURN_PER_MAT"></div>

<div class="O" line=14 content><span lang="ZH-CN">消耗品收货</span></div>
<div class="O" line=14 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=14 standard><span lang="ZH-CN">消耗品收货单</span><input type="text" name="CSM_RECPET_TRANS_PER_PRO"></div>

<div class="O" line=15 content><span lang="ZH-CN">消耗品入库核对/上架</span></div>
<div class="O" line=15 by_what><span lang="ZH-CN">品名</span></div>
<div class="O" line=15 standard><span lang="ZH-CN">长耗时</span><input type="text" name="CSM_INSTOR_ONSH_L_PER_CD"><span lang="ZH-CN">中耗时</span><input type="text" name="CSM_INSTOR_ONSH_M_PER_CD"><span lang="ZH-CN">短耗时</span><input type="text" name="CSM_INSTOR_ONSH_S_PER_CD"></div>

<div class="O" line=16 content><span lang="ZH-CN">消耗品出库下架</span></div>
<div class="O" line=16 by_what><span lang="ZH-CN">品名</span></div>
<div class="O" line=16 standard><span lang="ZH-CN">长耗时</span><input type="text" name="CSM_OUTSTOR_OFFSH_L_PER_CD"><span lang="ZH-CN">中耗时</span><input type="text" name="CSM_OUTSTOR_OFFSH_M_PER_CD"><span lang="ZH-CN">短耗时</span><input type="text" name="CSM_OUTSTOR_OFFSH_S_PER_CD"></div>

<div class="O" line=17 content><span lang="ZH-CN">消耗品发放现场</span></div>
<div class="O" line=17 by_what><span lang="ZH-CN">批次</span></div>
<div class="O" line=17 standard><span lang="ZH-CN">消耗品申请单</span><input type="text" name="CSM_RELEASE_PER_PRO"></div>

<div class="O" line=18 content><span lang="ZH-CN">钢丝固定件清洗</span></div>
<div class="O" line=18 by_what><span lang="ZH-CN">品名</span></div>
<div class="O" line=18 standard><span lang="ZH-CN">钢丝固定件</span><input type="text" name="SWC_WASH_PER_CD"></div>

<button>更新物料组间接作业标准工时</button>
</p:shaperange>



</div>
<div class="clear"></div>
<div id="update_success_dialog"></div>
<div class="clear areaencloser dwidth-middleright"></div>
</html>