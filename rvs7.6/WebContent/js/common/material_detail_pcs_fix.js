// fix
var pcs_dialog = $("#detail_dialog");
var hasPcs = (typeof pcsO === "object");

var pcs_buttons = {
	"确定":function(){
		var detail_data = {};
		detail_data["material_id"] = trans_material_id;
		valuePcs(detail_data);

		if (!(detail_data.pcs_inputs == null || detail_data.pcs_inputs.length == 0 || detail_data.pcs_inputs == "{}")
			|| !(!detail_data.pcs_comments || detail_data.pcs_comments.length == 0 || detail_data.pcs_comments == "{}")){
			var writed = false;
			var pcs_inputs = {};
			var pcs_comments = {};
//			try {
				eval("pcs_inputs = " + detail_data.pcs_inputs);
//			}catch(e){}
			for (var iinputs in pcs_inputs) {
				var input = pcs_inputs[iinputs];

				if (input.trim() != "" && input != "{}") {
					writed = true;
					break;
				}
			}
			try {
				eval("pcs_comments = " + detail_data.pcs_comments);
			}catch(e){}

			for (var iinputs in pcs_comments) {
				var input = pcs_comments[iinputs];

				if (input.trim() != "" && input != "{}") {
					writed = true;
					break;
				}
			}
			if (writed) {
				// 提交输入项目くるわ
				line_pcs(detail_data);
			} else {
				pcs_dialog.dialog('close');
			}
		} else {
			pcs_dialog.dialog('close');
		}
	},
	"取消":function(){
		pcs_dialog.dialog('close');
	}
};

/** 工程检查票赋值 */
var valuePcsFix = function(data) {
	var pcs_values = {};
	var pcs_comments = {};
	$("#pcs_detail_pcs_contents input").each(function(){
		if (this.value == "") return;
		if (this.type == "text") {
			pcs_values[this.name] = this.value;
		} else {
//			if (this.name && this.name != "") pcs_values[this.name] = "";
		}
	});
	$("#pcs_detail_pcs_contents input:checked").each(function(){
		if (this.value == "") return;
		if (this.type == "radio"){
			pcs_values[this.name] = this.value;
		} else if (this.type == "checkbox"){
			if (pcs_values[this.name] == null || pcs_values[this.name] == "") {
				pcs_values[this.name] = this.value;
			} else {
				pcs_values[this.name] = pcs_values[this.name] + "," + this.value;
			}
		}
	});
	$("#pcs_detail_pcs_contents textarea").each(function(){
		if (this.value != null && this.value != "") {
			pcs_comments[this.name] = this.value;
		} else if(pcs_comments[this.name] == null) {
			pcs_comments[this.name] = this.value;
		}
	});

	data.pcs_inputs = Json_to_String(pcs_values);
	data.pcs_comments = Json_to_String(pcs_comments);

	for (var v in pcs_values) {
		if (pcs_values[v] == null || pcs_values[v] == "") return true;
	}
	return false;
}

/** 工程检查票赋值 */
var valuePcs = function(data) {
	var pcs_comments = {};
	$("#pcs_detail_pcs_contents textarea").each(function(){
		if (this.value != null && this.value != "") {
			pcs_comments[this.name] = this.value;
		}
	});

	data.pcs_comments = Json_to_String(pcs_comments);

	return false;
}

/** 工程检查票赋值 */
var valueFixPcs = function(data) {
	var pcs_values = [];
	var pcs_comments = [];
	$("#pcs_detail_pcs_contents input[fixed]").each(function(){
		if (this.value == "") return false;
		if (this.type == "text") {
			pcs_values.push({name : this.name, value : this.value, jam_code : $(this).attr("jam_code")});
		} else {
//			if (this.name && this.name != "") pcs_values[this.name] = "";
		}
	});
	$("#pcs_detail_pcs_contents input[fixed]:checked").each(function(){
		if (this.value == "") return false;
		if (this.type == "radio"){
			pcs_values.push({name : this.name, value : this.value, jam_code : $(this).attr("jam_code")});
		} else if (this.type == "checkbox"){
			if (pcs_values[this.name] == null || pcs_values[this.name] == "") {
				pcs_values[this.name] = this.value;
			} else {
				pcs_values[this.name] = pcs_values[this.name] + "," + this.value;
			}
		}
	});
	$("#pcs_detail_pcs_contents textarea[fixed]").each(function(){
		if (this.value != null && this.value != "") {
			pcs_comments.push({name : this.name, value : this.value
				, jam_code : $(this).attr("jam_code")});
		} else if(pcs_comments[this.name] == null) {
		}
	});

	data.pcs_values = pcs_values;
	data.pcs_comments = pcs_comments;

	return true;
}

var showPcsDetail = function(material_id, line_id, is_modal, callback){
	trans_material_id = material_id;

	pcs_dialog = $("#detail_dialog");
	if (pcs_dialog.length === 0) {
		$("body.outer").append("<div id='detail_dialog'/>");
		pcs_dialog = $("#detail_dialog");
	}

	pcs_dialog.html("");
	pcs_dialog.hide();
	// 导入详细画面
	pcs_dialog.load("widgets/pcs-detail.jsp" , function(responseText, textStatus, XMLHttpRequest) {
		if (!hasPcs) {
			alert("add pcs_edit.js");
		} else {
			spdLoad(pcs_dialog);
		}
	});

	var spdLoad = function() {
		pcsO.init($("#pcs_detail_container"), false);;
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'material.do' + '?method=getPcsDetailFixer',
			cache : false,
			data:{
				"material_id": material_id, "line_id" : line_id // bd
			},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus){
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);

					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages("#searcharea", resInfo.errors);
					} else {
						var mform = resInfo.mform;
						$("#pcs_detail_basearea").find("td:eq(1)").text(mform.model_name);
						$("#pcs_detail_basearea").find("td:eq(3)").text(mform.serial_no);
						$("#pcs_detail_basearea").find("td:eq(5)").text(mform.levelName);
						$("#pcs_detail_basearea").find("td:eq(7)").text(mform.section_name);
	
						// 工程检查票
						if (resInfo.pcses && resInfo.pcses.length > 0) {
							pcsO.generate(resInfo.pcses);
						}
					}
				} catch (e) {
					console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};

				pcs_dialog.dialog({
					position : [400, 20],
					title : "工程检查票详细画面",
					width : 'auto',
					show : "",
					height :  640,
					resizable : false,
					modal : is_modal,
					buttons : pcs_buttons,
					close : function(){pcs_dialog.unbind("scroll")}
				});
			
				if (callback) callback();

				pcs_dialog.show();
			}
		});
	};
};

var showPcsDetailLeader = function(material_id, ismodal) {
	pcs_buttons["提出纠错要求"] = function(){
		if ($("#fix_order").length == 0) {
			pcs_dialog.dialog("option", "width", 1200).dialog("option", "position", [100, 20]);
			pcs_dialog.find("div").css("float", "left").css("width", "980");
			pcs_dialog.append('<div id="fix_order" style="float:left;width:152px;padding:4px;">' +
					'<textarea id="fix_order_comment" style="width: 172px;height: 450px;"></textarea>' +
					'<input type="button" id="fix_order_button" value="提交"/>' +
					'</div>');
			$("#fix_order_button").button().click(function(){
				var reqData = {material_id : material_id, comment: $("#fix_order_comment").val()};
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : 'pcsFixOrder.do?method=doCreate',
					cache : false,
					data : reqData,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj) {
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages("#fix_order", resInfo.errors);
							} else {
								$("#fix_order").remove();
								pcs_dialog.dialog("option", "width", "auto").dialog("option", "position", [300, 20]);
							}
						} catch (e) {
							console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
									+ e.lineNumber + " fileName: " + e.fileName);
						};
					}
				});
			});
		}
	}
	showPcsDetail(material_id, ismodal);
}

var showPcsDetailManager = function(material_id, line_id, pcs_fix_order_key, ismodal, comment) {
	if ($("#fix_order").length == 0) {

		pcs_buttons["确定"] = function(){
			var reqData = {
				material_id : material_id, line_id : line_id, pcs_fix_order_key: pcs_fix_order_key // bd
			};
			valuePcs(reqData);
// valueFixPcs(reqData);

			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : 'pcsFixOrder.do?method=doFix',
				cache : false,
				data : reqData,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					try {
						// 以Object形式读取JSON
						eval('resInfo =' + xhrobj.responseText);
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages("#fix_order", resInfo.errors);
						} else {
							if (findFixList) findFixList();
							pcs_dialog.dialog("close");

							infoPop("修改已经完成。", null, "修改已经完成");
						}
					} catch (e) {
						console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
								+ e.lineNumber + " fileName: " + e.fileName);
					};
				}
			});
		}
	}
	showPcsDetail(material_id, line_id, ismodal, function(){

		pcs_dialog.find("div").css("float", "left").css("width", "980");
		pcs_dialog.append('<div id="fix_order" style="float:left;padding:4px;margin-left:16px;">' +
				'<div id="fix_order_comment_title" class="ui-widget-header" style="width: 176px;">申请修改内容</div>' +
				'<textarea id="fix_order_comment" readonly style="width: 174px;height: 450px;border:none;' +
				'background-color: rgb(248, 251, 132);resize:none;">' + comment + '</textarea>' +
				'</div>');
		pcs_dialog.dialog("option", "width", "1200").dialog("option", "position", [100, 40]);
		pcs_dialog.css("width", "1200px");
		$(".ui-dialog").css("left", "48px");
		pcs_dialog.bind("scroll", function(){
			var scrollTop = pcs_dialog.scrollTop();
			$("#fix_order_comment_title").css("margin-top", scrollTop);
		});
	});
}

function line_pcs(pcs_data) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'material.do?method=dowritepcs',
		cache : false,
		data : pcs_data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("#searcharea", resInfo.errors);
				} else {
					$("#detail_dialog").dialog("close");
				}
			} catch (e) {
				console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
}