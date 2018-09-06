var showLocate=function(location) {

	if (location == null || location.trim() == "") {
		return;
	}
	var jthis = $("#wip_pop");
	jthis.hide();
	jthis.load("widgets/qf/wip_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
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

		$(".shelf_model").hide();
		jthis.find("td[wipid="+location+"]").removeClass("wip-empty").addClass("ui-storage-highlight")
			.parents(".shelf_model").show();

		jthis.show();
	});
}
