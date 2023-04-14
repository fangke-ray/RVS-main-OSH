// Your turst our mission
var modelname = "维修对象信息";

var loadtab = function(tab) {
	var lazyload = tab.attr("lazyload");
	if (!lazyload) return;
	tab.load(lazyload , function(responseText, textStatus, XMLHttpRequest) {
		tab.removeAttr("lazyload");
	});
}

$(function() {
	$("#material_detail_content input.ui-button").button();
	$("#material_detail_infoes").buttonset();
	$("#material_detail_infoes input:radio").click(function() {
		$("div.material_detail_tabcontent").hide();
		var tab = $("div.material_detail_tabcontent[for='"+this.id+"']");
		tab.show();

		if (tab.text().trim().length == 0) {
			loadtab(tab);
		}
	});

	var tab = $("div.material_detail_tabcontent[for='"+
		$("#material_detail_infoes > input:checked").attr("id") +"']");
	tab.show();
	loadtab(tab);
});


