var lastestHashPath = null;
var lastestDetailPath = null;

var links = {};
links['#category'] = "category.do";
links['#model'] = "model.do";
links['#section'] = "section.do";
links['#line'] = "line.do";
links['#position'] = "position.do";
links['#operator'] = "operator.do";
links['#process_assign_template'] = "processAssignTemplate.do";
links['#role'] = "role.do";
links['#privacy'] = "privacy.do";
links['#holiday'] = "holiday.do";
//links['#resttime'] = "resttime.do";
links['#parameters'] = "properties.do";
links['#partial']="partial.do";
links['#pcsFixOrder']="pcsFixOrder.do";
links['#customer'] = "customer.do";
links['#system_image_manage'] = "system_image_manage.do";
links['#light_fix'] = "light_fix.do";
links['#interface_data'] = "interface_data.do";
links['#user_define_codes'] = "user_define_codes.do";
links['#drying_oven_device'] = "drying_oven_device.do";
// links['#pcs_request'] = "pcs_request.do";
links['#optional_fix'] = "optional_fix.do";

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
});


function initialiseStateFromURL() {
	var anchor = window.location.hash;

	if (!anchor || "#" == anchor || "" == anchor) {
		window.location.hash = "#category"; // 默认进入画面
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

	$("#body-mdl").load(links[anchor], function(responseText, textStatus, XMLHttpRequest) {});
}
