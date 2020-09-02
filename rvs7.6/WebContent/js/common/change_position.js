var getChangeSet = function(xhrobj){

	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);

		} else {
			if (resInfo.position_link) {
				window.location.href = resInfo.position_link;
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};	
};

var getPositionWork = function(position_id, px){
	var data = {
		position_id : position_id,
		px: px
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'panel.do?method=changeposition',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getChangeSet
	});
};
