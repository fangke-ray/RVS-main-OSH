<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<style>
#storagearea span.concern {
	font-size: 12px;
	line-height: 2.5em;
	position: relative;
	left: 4em;
	cursor: zoom-in;
	background-color: white;
	color: darkblue;
	padding: 0 .5em;
}
#storagearea span.concern:after {
	position: absolute;
	content: '　';
	width: 0;
	height: 0;
	border: 0.7em solid transparent;
	right: -1.4em;
	top: 0;
	border-left-color: white;
}
#storagearea.show_concern span.concern,
#storagearea span.concern:hover {
	background: linear-gradient(to right, greenyellow 20%, turquoise);
}
#storagearea.show_concern span.concern:after,
#storagearea span.concern:hover:after {
	border-left-color: turquoise;
}
#storagearea .tube-liquid > div.concern {
	overflow: hidden;
	position: absolute;
	right: -.5em;
	top: 0;
	border: 1px solid;
	color: transparent;
	padding: 0 2px;
	text-align: center;
	width: 1em;
	border-radius: 1em;
}
#storagearea.hover_concern .tube-liquid > div.concern,
#storagearea.show_concern .tube-liquid > div.concern,
#storagearea .tube-liquid > div.concern:hover {
	width: 3em;
	color:black;
	border-top-left-radius: 0;
	border-bottom-left-radius: 0;
	box-shadow: 2px 2px 2px darkgreen;
}
#storagearea .tube-liquid > div.concern.concern_start {
	border-color: darkgreen;
	background-color: greenyellow;
}
#storagearea .tube-liquid > div.concern.concern_end {
	border-color: navy;
	background-color: turquoise;
}
</style>
<span class="concern">${concernPosition} 进度</span>
<script type="text/javascript">
$("#storagearea span.concern").click(function(){
	$("#storagearea").toggleClass("show_concern");
});
$("#storagearea span.concern").hover(function(){
	$("#storagearea").toggleClass("hover_concern");
});
</script>