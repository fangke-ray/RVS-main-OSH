<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">
<!--设置Web App应用以全屏模式运行(当网站添加到主屏幕后再点击进行启动时，可隐藏地址栏)-->
<meta name="apple-mobile-web-app-capable" content="yes">
<!--设置Web App的状态栏（屏幕顶部栏）的样式-->
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<!--禁用自动识别页面中的电话号码-->
<meta name="format-detection" content="telephone=no">
<!--禁用自动识别页面中的电子邮件-->
<meta name="format-detection" content="email=no">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ipad.css?">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/iscroll-lite.js"></script>
<script type="text/javascript" src="js/jquery.hammer.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/qf/fact_recept_material.js"></script>

<style type="text/css">
#content{
	padding-left: 25px;
	position: relative;
}

div.hidden{
	height: 100%;
}

@media (min-width: 768px) {
  #production_type {
    height:6%;
  }
  
  #content{
  	 height:92%;
  }
}

@media (min-width: 992px) {
  #production_type {
    height:8%;
  }
  
  #content{
  	 height:90%;
  }
}


@-webkit-keyframes scaleOut {
	0%{-webkit-transform:scale(1);}
	100%{-webkit-transform:scale(0);}
}
@-moz-keyframes scaleOut {
	0%{-moz-transform:scale(1);}
	100%{-moz-transform:scale(0);}
}
@keyframes scaleOut {
	0%{transform:scale(1);}
	100%{transform:scale(0);}
}

@-webkit-keyframes scaleIn {
	0%{-webkit-transform:scale(0);}
	100%{-webkit-transform:scale(1);}
}
@-moz-keyframes scaleIn {
	0%{-moz-transform:scale(0);}
	100%{-moz-transform:scale(1);}
}
@keyframes scaleIn {
	0%{transform:scale(0)}
	100%{transform:scale(1);}
}

.list-group-sub-item .item-container.scaleOut{
	-webkit-animation: scaleOut .3s ease both;
	-moz-animation: scaleOut .3s ease both;
	animation: scaleOut .3s ease both;
}

.list-group-sub-item .item-container.scaleIn{
	-webkit-animation: scaleIn .3s ease both;
	-moz-animation: scaleIn .3s ease both;
	animation: scaleIn .3s ease both;
}

.page{
	position : absolute;
	left: 30px;
	top: 0;
	height: 100%;
	width: 100%;
	box-sizing:border-box;
}

.add{
	width: 42px;
	height: 42px;
	line-height:42px;
	text-align: center;
	font-size: 2rem;
	position: absolute;
	right: 4px;
	top: 50%;
	transform:translateY(-50%);
	box-shadow: 0 2px rgba(0, 0, 0, 0.2);
	transition: transform 0.1s ease 0s;
}

.add[hold]{
	transform:translateY(-50%) scale(.95);
}

.item-container{
	display: inline-block;
	width: 48%;
	height: 100px;
	margin: 5px 4px;
	background-color: #00B5B5;
	border-radius: 10px;
	font-size: 1.5rem;
	font-weight:400;
	color: #ffffff;
	box-shadow: 0 2px rgba(0, 0, 0, 0.2);
	transition: all 0.1s ease 0s;
	box-sizing:border-box;
}

@media (min-width: 992px) {
  .item-container{
  	font-size: 1.3rem;
  	width: 32%;
  }
}

.item-container.less{
	background-color: #4CAF50;
}

.item-container.more{
	background-color: #3399CC;
}

.item-container[hold]{
	transform: scale(.95);
}

.item-container .item-sub-container{
	height: 50px;
	line-height: 50px;
}

.item-container .item-sub-container::after{
	content: '';
	display: table;
	clear: both;
}

.item-container .item-sub-container .item{
	display: inline-block;
	margin-left: 4px;
}

.item-container .item-sub-container:last-child .item:nth-child(1){
	float: left;
}

.item-container .item-sub-container:last-child .item:nth-child(n+2){
	float: right;
	margin-right:4px;
	width: 45px;
	height: 45px;
}

.item-container .item-sub-container .item.date{
	font-size: 1rem;
	vertical-align: text-bottom;
}

.td-content.tag-type span{
	margin:0 4px;
}

.td-content .btn{
	display: inline-block;
	padding:10px;
	border-radius:4px;
	background-color: #93c3cd;
	font-size: 1rem;
	color:#000;
	transition: all 0.1s ease 0s;
}

.td-content .btn[hold]{
    transform:translate3d(1px,1px,0);
    background-color: #db4865;
    color:#fff;
}

.td-content .btn.checked{
	background-color: #db4865;
	color: #fff;
}
.error{
	color: #dc0800;
}
@font-face {
	font-family: 'icomoon';
	src:url('css/fonts/icomoon.eot');
	src:url('css/fonts/icomoon.eot?#iefix') format('embedded-opentype'),
		url('css/fonts/icomoon.svg#icomoon') format('svg'),
		url('css/fonts/icomoon.woff') format('woff'),
		url('css/fonts/icomoon.ttf') format('truetype');
	font-weight: normal;
	font-style: normal;
}
#exitToPanel {
	width: 5em;
	height: 5em;
	position: absolute;
	background-color: #09127a;
	top: 1em;
	right: 0.5em;
	border-radius: 2.25em;
	text-align: center;
	cursor: pointer;
}
#exitToPanel > span{
	color: yellow;
	font-size: 3em;
	line-height: 1.8;
	font-family: 'icomoon';
}
@-webkit-keyframes moveOutTop {
	0%{-webkit-transform:scale(0);opacity: 0;}
	50%{-webkit-transform:scale(2) translateY(-30px);opacity: 1;}
	100%{-webkit-transform:scale(2) translateY(-30px);opacity: 0;}
}
@-moz-keyframes moveOutTop {
	0%{-moz-transform:scale(0);opacity: 0;}
	50%{-moz-transform:scale(2) translateY(-30px);opacity: 1;}
	100%{-moz-transform:scale(2) translateY(-30px);opacity: 0;}
}
@keyframes moveOutTop {
	0%{transform:scale(0);opacity: 0;}
	50%{transform:scale(2) translateY(-30px);opacity: 1;}
	100%{transform:scale(2) translateY(-30px);opacity: 0;}
}

.tip{
	position:absolute;
	top: 0;
	left: 0;
	font-size: 1rem;
	color: #db4865;
	white-space:nowrap;
	transition: all .2s ease 0s; 
}

.tip.moveOutTop{
	-webkit-animation: moveOutTop .4s ease both;
	-moz-animation: moveOutTop .4s ease both;
	animation: moveOutTop .4s ease both;
}

</style>

<title>维修品实物受理/测漏</title>
</head>
<input type="hidden" value="${role }" id="role">
<body class="outer">
	<div class="container ui-widget-panel">
		<div class="left" style="width: 70%;height:100%">
			<div id="production_type" class="ui-widget-content" style="border-bottom:0;">
				<div class="btn-group" style="height: 100%;">
					<div class="btn ui-state-default ui-state-active" id="undirect" target="undirect_tab">非直送</div>
					<div class="btn ui-state-default" id="direct" target="direct_tab">直送</div>
					<div class="btn ui-state-default" id="perl" target="perl_tab">其他</div>
				</div>
			</div>
			<div id="content" class="ui-widget-content">
				<div id="undirect_tab" class="hidden">
					<div style="height: 8%;position: relative;">
						<div class="add ui-widget ui-state-default ui-corner-all" id="undirectAddBtn">+</div>
					</div>
					
					<div id="undirect_scroll" style="overflow:hidden;height: 92%;">
						<ul class="list-group"></ul>
					</div>
				</div>
				<div id="direct_tab" class="hidden">
					<div style="height: 8%;position: relative;">
						<div class="add ui-widget ui-state-default ui-corner-all" id="directAddBtn">+</div>
					</div>
				
					<div id="direct_scroll" style="overflow:hidden;height: 92%;">
						<ul class="list-group"></ul>
					</div>
				</div>
				<div id="perl_tab" class="hidden">
					<div style="height: 8%;position: relative;">
						<div class="add ui-widget ui-state-default ui-corner-all" id="perlAddBtn">+</div>
					</div>
				
					<div id="perl_scroll" style="overflow:hidden;height: 92%;">
						<ul class="list-group"></ul>
					</div>
				</div>
				<div style="position: absolute;left:4px;top:6px;z-index: 10;">
					<ul id="letter_nav">
						<li class="nav-link" data-value="A"><span>A</span></li>
						<li class="nav-link" data-value="B"><span>B</span></li>
						<li class="nav-link" data-value="C"><span>C</span></li>
						<li class="nav-link" data-value="D"><span>D</span></li>
						<li class="nav-link" data-value="E"><span>E</span></li>
						<li class="nav-link" data-value="F"><span>F</span></li>
						<li class="nav-link" data-value="G"><span>G</span></li>
						<li class="nav-link" data-value="H"><span>H</span></li>
						<li class="nav-link" data-value="I"><span>I</span></li>
						<li class="nav-link" data-value="J"><span>J</span></li>
						<li class="nav-link" data-value="K"><span>K</span></li>
						<li class="nav-link" data-value="L"><span>L</span></li>
						<li class="nav-link" data-value="M"><span>M</span></li>
						<li class="nav-link" data-value="N"><span>N</span></li>
						<li class="nav-link" data-value="O"><span>O</span></li>
						<li class="nav-link" data-value="P"><span>P</span></li>
						<li class="nav-link" data-value="Q"><span>Q</span></li>
						<li class="nav-link" data-value="R"><span>R</span></li>
						<li class="nav-link" data-value="S"><span>S</span></li>
						<li class="nav-link" data-value="T"><span>T</span></li>
						<li class="nav-link" data-value="U"><span>U</span></li>
						<li class="nav-link" data-value="V"><span>V</span></li>
						<li class="nav-link" data-value="W"><span>W</span></li>
						<li class="nav-link" data-value="X"><span>X</span></li>
						<li class="nav-link" data-value="Y"><span>Y</span></li>
						<li class="nav-link" data-value="Z"><span>Z</span></li>
						<li class="nav-link" data-value="无"><span>无</span></li>
					</ul>
					<div id="model_nav" class="sub-nav-link">
						<ul></ul>
					</div>
				</div>
			</div>
		</div>
		<div class="right ui-widget-content" style="width: 28%;height:98%;">
			<div style="margin:20px 0;position: relative;height:50px">
				<div class="switch switch-animate" style="width: 158px;position: absolute;left:50%;top:0;transform:translateX(-50%)">
					<div class="switch-container" style="width: 240px;">
						<span class="switch-handle-on switch-primary" style="width: 80px;">当天全部</span>
						<span class="switch-label" style="width: 80px;"></span>
						<span class="switch-handle-off switch-default" style="width: 80px;">等待作业</span>
					</div>
					<input type="checkbox" id="toggleBtn" checked="checked">
				</div>
			</div>
			
			<div style="width:93%;margin:0 auto;">
				<table class="condform" style="width: 100%;">
					<tr>
						<td class="ui-state-default td-title">当日一共完成</td>
					</tr>
					<tr>
						<td class="td-content" style="font-size: 1rem;padding:10px 6px;">
							<span id="today_num"></span> 单
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">下一个工作日</td>
					</tr>
					<tr>
						<td class="td-content" style="font-size: 1rem;padding:10px 6px;">
							<span id="nextday_num"></span> 单
						</td>
					</tr>
				</table>
			</div>
			
			<div class="areaencloser"></div>
			
			<div class="calculator">
				<div class="top">
					<span class="title">机身号：</span>
					<span class="screen" id="screen"></span>
				</div>
				<div class="keys">
					<div class="group">
  						<span value="7">7</span>
						<span value="8">8</span>
						<span value="9">9</span>
  					</div>
  					<div class="group">
  						<span value="4">4</span>
						<span value="5">5</span>
						<span value="6">6</span>
  					</div>
	  				<div class="group">
	  					<span value="1">1</span>
						<span value="2">2</span>
						<span value="3">3</span>
	  				</div>
					<div class="group">
						<span value="0">0</span>
						<span id="clear">←</span>
						<span id="clearAll">C</span>
					</div>
				</div>
			</div>
			
			<div id="test"></div>
		</div>
	</div>

	<div id="exitToPanel"><span>F</span></div>

	<div style="display: none;" id="edit_dialog">
		<div class="ui-widget-content">
			<table class="condform" style="width: 99%;">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="edit_model_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label id="edit_serial_no"></label>
					</td>
				</tr>
				<tr>
					<td class="td-content tag-type" colspan="2">
						<span class="leak" id="edit_leak" alt="测漏"></span>
						<span class="disinfect" id="edit_disinfect" alt="消毒"></span>
						<span class="sterilize" id="edit_sterilize" alt="灭菌"></span>
						<span class="animal" id="edit_animal" alt="动物试验"></span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">故障说明</td>
					<td class="td-content">
						<label id="edit_comment"></label>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div style="display: none;" id="add_dialog">
		<div class="ui-widget-content">
			<table class="condform" style="width: 99%;">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<input type="text" id="add_model_name" class="ui-widget-content"/>
						<input type="hidden" id="add_model_id"/>
						<div class="error"></div>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<input type="text" id="add_serial_no" class="ui-widget-content"/>
						<div class="btn" id="randomBtn" style="padding: 6px;" for="add_serial_no">临时记录</div>
						<div class="btn reset" style="padding: 6px;" for="add_serial_no">清除</div>
					</td>
				</tr>
				<tr>
					<td class="td-content tag-type" colspan="2">
						<span class="leak" id="add_leak" alt="测漏"></span>
						<span class="disinfect" id="add_disinfect" alt="消毒"></span>
						<span class="sterilize" id="add_sterilize" alt="灭菌"></span>
						<span class="animal" id="add_animal" alt="动物试验"></span>
					</td>
				</tr>
			</table>
		</div>
	</div>

	<div style="display: none;" id="temp_edit_dialog">
		<div class="ui-widget-content">
			<table class="condform" style="width: 99%;">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<input type="text" id="temp_edit_model_name" class="ui-widget-content"/>
						<input type="hidden" id="temp_edit_model_id"/>
						<div class="error"></div>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<input type="text" id="temp_serial_no" class="ui-widget-content"/>
						<div class="btn" id="tempRandomBtn" style="padding: 6px;" for="temp_serial_no">临时记录</div>
						<div class="btn reset" style="padding: 6px;" for="temp_serial_no">清除</div>
					</td>
				</tr>
				<tr>
					<td class="td-content tag-type" colspan="2">
						<span class="leak" id="temp_edit_leak" alt="测漏"></span>
						<span class="disinfect" id="temp_edit_disinfect" alt="消毒"></span>
						<span class="sterilize" id="temp_edit_sterilize" alt="灭菌"></span>
						<span class="animal" id="temp_edit_animal" alt="动物试验"></span>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div style="display: none;" id="perl_add_dialog">
		<div class="ui-widget-content">
			<table class="condform" style="width: 99%;">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<input type="text" id="perl_add_model_name" class="ui-widget-content"/>
						<input type="hidden" id="perl_add_model_id"/>
						<div class="error"></div>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<input type="text" id="perl_add_serial_no" class="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="td-content tag-type" colspan="2">
						<span class="disinfect" id="perl_add_disinfect" alt="消毒"></span>
						<span class="sterilize" id="perl_add_sterilize" alt="灭菌"></span>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div style="display: none;" id="perl_edit_dialog">
		<div class="ui-widget-content">
			<table class="condform" style="width: 99%;">
				<tr>
					<td class="ui-state-default td-title">型号</td>
					<td class="td-content">
						<label id="perl_edit_model_name"></label>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content">
						<label id="perl_edit_serial_no"></label>
					</td>
				</tr>
				<tr>
					<td class="td-content tag-type" colspan="2">
						<span class="disinfect" id="perl_edit_disinfect" alt="消毒"></span>
						<span class="sterilize" id="perl_edit_sterilize" alt="灭菌"></span>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
</body>
</html>