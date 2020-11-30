var popNewPhenomenon = function(message_id, is_modal, close_function){
	if (is_modal == null) is_modal = true;

	var this_dialog = $("#nongood_phenomenon");

	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='nongood_phenomenon'/>");
		this_dialog = $("#nongood_phenomenon");
	}

	this_dialog.data("callback", close_function);

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("new_phenomenon.do?method=detail&alarm_message_id=" + message_id , function(responseText, textStatus, XMLHttpRequest) {
		this_dialog.dialog({
			position : [400, 20],
			title : "不良新现象详细画面",
			width : 'auto',
			show : "",
			height :  'auto',
			resizable : false,
			modal : is_modal,
			buttons : null
		});
	});

	this_dialog.show();
}