/** 模块名 */
var modelname = "维修对象";
/** 一览数据对象 */
var listdata = {};
var reception_listdata = {};
/** 服务器处理路径 */
var servicePath = "sammary.do";

var uploadfile = function() {

	// 覆盖层
	panelOverlay++;
	makeWindowOverlay();

	// ajax enctype="multipart/form-data"
	$.ajaxFileUpload({
		url : 'upload.do?method=setInlineStatus', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			panelOverlay--;
			killWindowOverlay();

			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
			
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					uploadComplete(resInfo.status);
				}
			} catch(e) {
				
			}
		}
	});
};

$(function() {
	$("input.ui-button").button();

	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#uploadbutton").click(uploadfile);

});

/*
* Ajax通信成功時の処理
*/
function uploadComplete(status) {
	$confirmmessage = $("#confirmmessage");

	$confirmmessage.find("label:eq(0)").text(status.delivered);
	$confirmmessage.find("label:eq(1)").text(status.shipped);
	$confirmmessage.find("label:eq(2)").text(status.wip_count);
	$confirmmessage.find("label:eq(3)").text(status.inlined);
	$confirmmessage.find("label:eq(4)").text(status.approved);

	$confirmmessage.dialog({
		title : "OGZ 当前状况确认",
		resizable : false,
		modal : true,
		buttons : {
			"确定":function(){
				var postData = status;
				$.ajax({
					data : postData,
					url: servicePath + "?method=doImport",
					async: false, 
					beforeSend: ajaxRequestType, 
					success: ajaxSuccessCheck, 
					error: ajaxError, 
					type : "post",
					complete : function(xhrObj){
						$confirmmessage.dialog("close");
					}
				});
			}, 
			"关闭" : function(){ $confirmmessage.dialog("close"); }
		}		
	});
};