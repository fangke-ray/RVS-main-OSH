var lastestHashPath = null;
var lastestDetailPath = null;

/*
 * URIの制御処理。アンカーを使う。
 */
$(function() {

	if ($.browser.msie&&$.browser.version != "8.0") {
		$.fn.hashchange.src = '/panel/hashchange.html';
		$.fn.hashchange.domain = document.domain;
	}

	$(window).unbind("hashchange");
	$(window).hashchange(function(){
		if ($("div.ui-dialog").length > 0) {
			$("div.ui-dialog .ui-dialog-content").each(function(index, domEle) {
				var jdomEle = $(domEle);
				if (jdomEle.attr("id") != "errstring" && !jdomEle.parent().is(":hidden")) {
					// 打开中的非错误警报框关闭
					jdomEle.dialog("close");
				}
			});
		}

		initialiseStateFromURL();
	});
	$(window).trigger("hashchange");

	$("#body-mdl").load(function(){
//		setTimeout(function(){
			$("#basearea", window.frames["showiframe"].document).hide();
//		}, 5000);
    });
});


function initialiseStateFromURL() {
	var anchor = window.location.hash;

	if (!anchor || "#" == anchor || "" == anchor) {
		window.location.hash = "#globalProgress"; // 默认进入画面
		return;
	}

	// 到顶部
	if ("#gotop" == anchor) {
		window.scroll(0,0);
		window.location.hash = lastestHashPath;
		return;
	}

	if (window.location.hash == lastestHashPath) {
		showList();
		return;
	}

	$("#body-mdl").attr("src", anchor.replace("#", scanPath + "/") + ".scan?from=show");
}
