var showLocate=function(location) {

	if (location == null || location.trim() == "") {
		return;
	}
	var jthis = $("#wip_pop");
	jthis.hide();
	var link_act = "wip_storage.do?method=getLocationMap";

	jthis.load(link_act, function(responseText, textStatus, XMLHttpRequest) {
		 //新增

		jthis.dialog({
			position : [ 800, 20 ],
			title : "WIP 位置标示",
			width : 1000,
			show: "blind",
			height : 640,// 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {"关闭" : function() {
				jthis.dialog("close");
			}}
		});

		if (location != "all") {
			var position = jthis.find("td[wipid="+location+"]").removeClass("wip-empty").addClass("ui-storage-highlight")
				.closest("table").position();
			if (position.top > 600) {
				setTimeout(function(){
					jthis[0].scrollTop = (position.top - 100);
				}, 500);
			}
		}

		jthis.show();
	});
}

var showChooseMap=function(jthis, title, options, callback) {

	jthis.hide();
	var link_act = "wip_storage.do?method=getLocationMap";

	jthis.load(link_act, options, function(responseText, textStatus, XMLHttpRequest) {

		jthis.dialog({
			position : [ 800, 20 ],
			title : title,
			width : 1000,
			show: "blind",
			height : 640,// 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {"关闭" : function() {
				jthis.dialog("close");
			}}
		});

		if (options.occupied && typeof(callback) == "function") {
			jthis.click(function(e){
				if ("TD" == e.target.tagName) {
					if (!$(e.target).hasClass("wip-heaped")) {
						selwip = $(e.target).attr("wipid");
						jthis.unbind("click");
						jthis.dialog("close");
						callback(selwip, options);
					}
				}
			});
		}

		jthis.show();
	});
}