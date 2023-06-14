// JavaScript Document
//copyright c by zhangxinxu 2009-10-17 
//http://www.zhangxinxu.com
/*由于大图绑定在href属性中，故一般而言，需使用a标签的href指向大图。仅支持png,gif,jpg,bmp四种格式的图片。用法是：目标.preview();
例如：<a href="xx.jpg">图片</a>
$("a").preview();就可以了
*/
(function($){
	$.fn.preview = function(opt){
		if (opt == undefined) opt = {};
		var xOffset = opt.xOffset || 10;
		var yOffset = opt.yOffset || 10;
		var w = $(window).width();
		$(this).each(function(){
			$(this).hover(function(e){
				var zIndex = 100;
				var $pos = $(this).closest(".ui-dialog");
				if ($pos.length && !isNaN($pos.css("zIndex"))) {
					zIndex = parseInt($pos.css("zIndex")) + 1;
				}
				$("body").append("<div id='preview'><div><img src='"+$(this).attr('href')+"' /></div></div>");
				$("#preview").css({
					position:"absolute",
					padding:"4px",
					border:"1px solid #f3f3f3",
					backgroundColor:"#eeeeee",
					top:(e.pageY + yOffset) + "px",
					zIndex:zIndex
				});
				$("#preview > div").css({
					padding:"5px",
					backgroundColor:"white",
					border:"1px solid #cccccc"
				});
				$("#preview > div > p").css({
					textAlign:"center",
					fontSize:"12px",
					padding:"8px 0 3px",
					margin:"0"
				});
				if(e.pageX < w/2){
					$("#preview").css({
						left: e.pageX + xOffset + "px",
						right: "auto"
					}).fadeIn("fast");
				}else{
					$("#preview").css("right",(w - e.pageX + xOffset) + "px").css("left", "auto").fadeIn("fast");	
				}
			},function(){
				$("#preview").remove();
			}).mousemove(function(e){
				$("#preview").css("top",(e.pageY + yOffset) + "px")
				if(e.pageX < w/2){
					$("#preview").css("left",(e.pageX + xOffset) + "px").css("right","auto");
				}else{
					$("#preview").css("right",(w - e.pageX + xOffset) + "px").css("left","auto");
				}
			});						  
		});
	};
})(jQuery);