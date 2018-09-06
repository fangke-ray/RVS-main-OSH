(function($) {

var drawer = {
	thisid : "",
	defaults: {
	    debug: false,
	    grid: false
	},

	options: {
		selections : [
			{plbl : "NS工程", value: 29, text: "先端预制"}
		],
		editable : false,
		groupcode : 9000000
	},

	_subchartcode : 0,
	_closeOptions: function (flowchart_id) {
		var flowchart_id_options = $("#" + flowchart_id + "_options");
		if (!flowchart_id_options.is(":animated") && !flowchart_id_options.is(":hidden")) {
			flowchart_id_options.hide("fast");
			$("#" + flowchart_id + "_insert").hide();
		}
	},

	_changeNext: function (node, pos) {
		node.attr("nextcode", pos);
//		var posid = node.attr("posid");
//		var ii = posid.indexOf("_");
//		if (ii > 0) {
//			posid = pos + posid.substring(ii);
//		}
//		node.attr("posid", posid);
	},

	_changePrev: function (node, pos) {
		node.attr("prevcode", pos);
	},
	
	_getPostions: function (container) {
		var groups = {};
		if (this.options.selections) {
			for (var iselection in this.options.selections) {
				var selection = this.options.selections[iselection];
				if (!groups[selection.plbl]) {
					groups[selection.plbl] = $("<optgroup label='" + selection.plbl + "'></optgroup>");
				}
				groups[selection.plbl].append("<option value='" + selection.value + "' plbl='" + selection.plbl + "'>" + selection.text + "</option>");
			}
		}

		var selectediv = $("<div id='" + this.thisid + "_options' class='options'><label id='nodename'></label><br>"
				+ "<div id='" + this.thisid + "_buttons'></div><br>"
				+ "<div id='" + this.thisid + "_insert' style='display:none;'><label for='select'>选择工位</label>"
				+ "<select id='" + this.thisid + "_selectPoses'></select>" 
				+ "<select style='display:none;'></select><br>"
				+ "<div id='" + this.thisid + "_i_buttons' class='clear'></div>"
				+ "</div></div>");
		selectPoses = selectediv.find("#"+ this.thisid + "_selectPoses");
		for (var igroup in groups) {
			selectPoses.append(groups[igroup]);
		}
		container.append(selectediv);
		$("#" + this.thisid + "_insert select:first").select2Buttons();
	},

	_getinit: function () {
		return "<div class='edgeposition'><div class='just'><div class='pos' posid='0' code='0' start><span>Start</span></div></div></div>" +
							"<div class='edgeposition'><div class='just'><div class='pos' posid='-1' code='0' end><span>End</span></div></div></div>";
	},

	_removePos: function (ep, inserter) {
		var just = ep.parent();
		if (just.find(".pos").length == 1) {
			// 上下步骤连接
			var prevNode = $(".edgeposition .pos[nextcode="+ep.attr("code")+"]");
			var nextNode = $(".edgeposition .pos[prevcode="+ep.attr("code")+"]");

			prevNode.attr("nextcode", (nextNode.attr("code") || ep.attr("nextcode")));
			nextNode.attr("prevcode", (prevNode.attr("code") || ep.attr("prevcode")));
			just.parent().remove();

			// 放回选项
			if (inserter.find("select:first").has('optgroup').length > 0) {
				var tempoption = inserter.find("select:last option[value='"+ep.attr("code")+"']").remove();
				inserter.find("select:first optgroup[label='"+tempoption.attr("plbl")+"']").append(tempoption);
			} else {
				inserter.find("select:first").append(inserter.find("select:last option[value='"+ep.attr("code")+"']").remove());
			}
			inserter.find("select:first").select2Buttons();

			// 关闭工具框
			this._closeOptions(this.thisid);
		} else {
			// 如果是代表工位
			var posid = null;
			var poscode = null;
			if (ep.is(".pos:first-child")) {
				posid = ep.attr("posid");
				poscode = ep.attr("code");
			}
			// 
			ep.remove();
			if (posid != null) {
				var signpos = just.find(".pos:first");
				// 作为新的标记
				var newposid = signpos.attr("code");

				// 同级修改代表id
				$(".edgeposition .pos[posid="+posid+"]").attr("posid", newposid);

				// 上下步骤连接
				$(".edgeposition .pos[nextcode="+poscode+"]").attr("nextcode", newposid);
				$(".edgeposition .pos[prevcode="+poscode+"]").attr("prevcode", newposid);
			}

			// 放回选项
			if (inserter.find("select:first").has('optgroup').length > 0) {
				var tempoption = inserter.find("select:last option[value='"+ep.attr("code")+"']").remove();
				inserter.find("select:first optgroup[label='"+tempoption.attr("plbl")+"']").append(tempoption);
			} else {
				inserter.find("select:first").append(inserter.find("select:last option[value='"+ep.attr("code")+"']").remove());
			}
			inserter.find("select:first").select2Buttons();
			// 关闭工具框
			this._closeOptions(this.thisid);

			// 只剩一个并行项目的时候，删除并行框
			if (just.find(".pos").length == 1) {
				just.removeClass("just-multi");
			};
		};
	},

	_removeAll : function () {
		$("#" + this.thisid).html("");//.find(".edgeposition").remove();
	},
	_createSimplePos : function (pos) {
		// 工位插入选项
		var inserter = $("#" + this.thisid + "_insert");
		var optionByCode = inserter.find("select:first option[value='"+parseInt(pos.position_id)+"']").remove();
		inserter.find("select:last").append(optionByCode);

		return "<div class=\"edgeposition\">" + "<div class=\"just\">"
				+ "<div class=\"pos" + ((pos.position_id > drawer.options.groupcode) ? " chartarea"
				: "") + "\" prevcode=\"" + pos.prev_position_id
				+ "\" nextcode=\"" + pos.next_position_id
				+ "\" posid=\"" + pos.sign_position_id
				+ "\" code=\"" + parseInt(pos.position_id) + "\">"
				+ ((pos.position_id > drawer.options.groupcode)
				? ((this.options.editable) ? this._getinit() : "") 
				: ("<span>"+ optionByCode.text() + "</span>"))
				+ "</div></div></div>";
	},
	_createJuxtaPos : function (pos) {
		// 工位插入选项
		var inserter = $("#" + this.thisid + "_insert");
		var optionByCode = inserter.find("select:first option[value='"+parseInt(pos.position_id)+"']").remove();
		inserter.find("select:last").append(optionByCode);
		
		return "<div class=\"pos" + ((pos.position_id > drawer.options.groupcode) ? " chartarea"
		: "") + "\" prevcode=\"" + pos.prev_position_id
				+ "\" nextcode=\"" + pos.next_position_id
				+ "\" posid=\"" + pos.sign_position_id
				+ "\" code=\"" + parseInt(pos.position_id) + "\">"
				+ ((pos.position_id > drawer.options.groupcode)
				? ((this.options.editable) ? this._getinit() : "") 
				: ("<span>"+ optionByCode.text() + "</span>"))
				+ "</div>";
	}
};

var flowchart_methods = {

	init : function(method, options) {

		var jthis = null;

		if ("reset" == method) {
			jthis = $("#"+drawer.thisid);
			jthis.html("");
		} else {
			jthis = $(this);
			drawer.thisid = jthis.attr("id");

			// TODO extends??
			if (options.editable)
				drawer.options.editable = options.editable;
			if (options.selections)
				drawer.options.selections = options.selections;
		}

		var thisid = drawer.thisid;
		jthis.addClass("chartarea");
		jthis.addClass("ui-corner-all");
		jthis.attr("code", drawer.options.groupcode);
		jthis.append(drawer._getinit());
		drawer._getPostions(jthis);

		// /
		// $("#"+thisid+"_buttons input:last").click(function(){
		// $("#"+thisid+"_insert input:first").remove();
		// });
		// /
		if (drawer.options.editable) {
			drawer._subchartcode = drawer.options.groupcode;

			jthis.delegate(".edgeposition .pos", "click", function(e) { //  .pos:not('.chartarea') mouseover
				e.stopPropagation();
				var ep = $(this);
				var ep_buttons = $("#" + thisid + "_buttons");
				var ep_buttons_html = "";
				if (ep.attr("end") == null)
					ep_buttons_html += "<input type='button' value='＋工位' />";
				if (ep.attr("end") == null && ep.attr("start") == null)
					ep_buttons_html += "<input type='button' value='×删除' />";
				ep_buttons.html(ep_buttons_html);
				ep_buttons.buttonset();

				// 工位插入选项
				var inserter = $("#" + thisid + "_insert");
				inserter.hide();

				ep_buttons.find("input:first").click(function() {
					var insertButtons = $("#" + thisid + "_i_buttons");
					var insertButtons_html = "";

					var nextNode = ep.parent().parent().next(".edgeposition");
					var nextcode = "";
					var nextstep = null;
					var nextposid = "";
					if (nextNode.length > 0) {
						nextstep = nextNode.find(".pos");
						nextcode = nextstep.attr("code");
						nextposid = nextstep.attr("posid");
						var nextname = nextstep.text();
						insertButtons_html += "<input type='button' id='insert_before_" + nextcode +"' value='插入到" + nextname + "之前' />";
						if (nextstep.attr("end") == null)
							insertButtons_html += "<input type='button' id='insert_with_" + nextcode +"' value='与" + nextname + "并行' />";
					}
					insertButtons_html += "<input type='button' id='branching_before_" + nextcode +"' value='新分支' />";
					insertButtons.html(insertButtons_html);

					$("#insert_before_"+nextcode).click(function(){
						// 插入一个步骤
						var pos = inserter.find("select:first option:selected").val();
						var posname = inserter.find("select:first option:selected").text();
						if (pos != null) {
							var newStep = "<div class='edgeposition'><div class='just'><div class='pos' prevcode='"+ep.parent().find(".pos:first").attr("code")+"' nextcode='"+nextcode+"' posid='"+pos+"' code='"+pos+"'><span>"+posname+"</span></div></div></div>";
							nextNode.before(newStep);
							//移动选择项
							inserter.find("select:last").append(inserter.find("select:first option:selected").remove());
							inserter.find("select:first").select2Buttons();

							ep.parent().find(".pos").each(function(i,item){
								drawer._changeNext($(item), pos);
							});
							ep.parent().parent().next(".edgeposition").next(".edgeposition").find(".pos").each(function(i,item){
								drawer._changePrev($(item), pos);
							});
							drawer._closeOptions(thisid);
						} else {
							alert(null);
						}
					});

					$("#insert_with_"+nextcode).click(function(){
						var pos = inserter.find("select:first option:selected").val();
						var posname = inserter.find("select:first option:selected").text();
						var just = nextstep.parent();
						if (pos != null) {

							var newStep = "<div class='pos' prevcode='"+ep.parent().find(".pos:first").attr("code")+"' nextcode='"+nextstep.attr("nextcode")+"' posid='"+nextstep.last().attr("posid")+"' code='"+pos+"'><span>"+posname+"</span></div>";
							nextstep.last().after(newStep);
							inserter.find("select:last").append(inserter.find("select:first option:selected").remove());
							inserter.find("select:first").select2Buttons();

							drawer._closeOptions(thisid);
						} else {
							alert(null);
						}
						if (!just.hasClass("just-multi")) just.addClass("just-multi");
					});

					// 新分支
					$("#branching_before_"+nextcode).click(function(){
						drawer._subchartcode+=10;
						while ($("div.chartarea[posid='"+(drawer._subchartcode + 1)+"']").length > 0) {
							drawer._subchartcode+=10;
						}
						var newStep = "<div class='edgeposition'><div class='just just-multi'>" +
								"<div class='chartarea pos' prevcode='"+ep.parent().find(".pos:first").attr("code")+"' nextcode='"+nextcode+"' posid='"+(drawer._subchartcode + 1)+"' code='"+(drawer._subchartcode + 1)+"'>"+drawer._getinit()+"</div>" +
								"<div class='chartarea pos' prevcode='"+ep.parent().find(".pos:first").attr("code")+"' nextcode='"+nextcode+"' posid='"+(drawer._subchartcode + 1)+"' code='"+(drawer._subchartcode + 2)+"'>"+drawer._getinit()+"</div>" +
								"</div></div>";
						nextNode.before(newStep);
						// 上下步骤连接
						ep.parent().find(".pos").each(function(i,item){
							drawer._changeNext($(item), (drawer._subchartcode + 1));
						});
						ep.parent().parent().next(".edgeposition").next(".edgeposition").find(".pos").each(function(i,item){
							drawer._changePrev($(item), (drawer._subchartcode + 1));
						});
						// 关闭工具框
						drawer._closeOptions(thisid);
					});

					$("#" + thisid + "_i_buttons").buttonset();
					inserter.show();
				});

				$("#" + thisid + "_buttons input[value=×删除]").click(function() {
					if (ep.hasClass("chartarea")) {
						// 上下步骤连接
						var prevNode = $(".edgeposition .pos[nextcode="+ep.attr("code")+"]");
						var nextNode = $(".edgeposition .pos[prevcode="+ep.attr("code")+"]");

						prevNode.attr("nextcode", nextNode.attr("code"));
						nextNode.attr("prevcode", prevNode.attr("code"));

						var just = ep.parent();
						just.find(".chartarea").each(function(isub,itemsub) {
							var subchar = $(itemsub);
							subchar.find(".pos").each(function(i,itempos) {
								drawer._removePos($(itempos), inserter);
							});
							subchar.remove();
						});
						just.parent().remove();
					} else {
						drawer._removePos(ep, inserter);
					}
				});

				$("#" + thisid + "_options label:first").html(ep.text());
				$("#" + thisid + "_options").css({"top" : (e.pageY - jthis.position().top - 60) + "px", "left" : (e.pageX - jthis.position().left) + "px"}).show("fast");
				//$("#" + thisid + "_options").css({"top" : (e.pageY) + "px", "left" : (e.pageX) + "px"}).show("fast");
			});

			jthis.bind("mouseleave", function() {
				$("#" + thisid + "_options").hide();
			});

			$("#" + thisid + "_options").mouseleave(function(e) {
				drawer._closeOptions(thisid);
			});
		}
	},
	save : function() {
		var poslist = {};
		return poslist;
	},
	fill : function(data) {
		drawer._removeAll();
		drawer._getPostions($(this));

		var content = $("<div code=\"" + drawer.options.groupcode + "\"/>");
		var start = true;
		var queue = [];

		if (drawer.options.editable) {
			content.html(drawer._getinit());
			start = false;
		}
		for (var ipos in data) {
			var pos = data[ipos];
			if (start) {
				if ((parseInt(pos.sign_position_id) == pos.position_id)) {
					if (pos.line_id != drawer.options.groupcode && content.find(".pos[code='"+pos.line_id+"']").length == 0) {
						queue.push(pos);
					} else {
						start = false;
						content.append(drawer._createSimplePos(pos));
//						posSet.push(pos.sign_position_id);
//						prevSet.push(pos.prev_position_id);
//						nextSet.push(pos.next_position_id);
					}
				} else {
					queue.push(pos);
				}
			} else {
				// 如果分支组存在
				if (pos.line_id == drawer.options.groupcode || content.find(".pos[code='"+pos.line_id+"']").length > 0) {
					var fanwei = (pos.line_id == drawer.options.groupcode ? content : content.find("div[code=\"" + pos.line_id + "\"]"));
					// 是标记节点并且前/后节点存在时
					if ((parseInt(pos.sign_position_id) == pos.position_id)) {
						if (fanwei.html() == "") {
							fanwei.append(drawer._createSimplePos(pos));
						} else {
							var prevNode = fanwei.children(".edgeposition").children(".just").children(".pos[posid=\"" + pos.prev_position_id + "\"]");
							var nextNode = fanwei.children(".edgeposition").children(".just").children(".pos[prevcode=\"" + pos.sign_position_id + "\"]");
							if (prevNode.length > 0) {
								prevNode.parent().parent().after(drawer._createSimplePos(pos));
							} else if (nextNode.length > 0) {
								nextNode.parent().parent().before(drawer._createSimplePos(pos));
							} else {
								queue.push(pos);
							}
						}
					} else {
						// 不是标记节点并且标记节点存在时
						var signNode = fanwei.children(".edgeposition").children(".just").children(".pos[posid=\"" + pos.sign_position_id + "\"]");
						if (signNode.length > 0) {
							signNode.last().after(drawer._createJuxtaPos(pos));
							signNode.parent().addClass("just-multi");
						} else {
							queue.push(pos);
						}
					}
				} else {
					queue.push(pos);
				}
			}
		}

		// 如果没加完
		var queueLength = 0;
		while (queue.length > 0) {

			if (queue.length == queueLength) break; // 都加不上就退出
			queueLength = queue.length;
			var newqueue = [];
			for (var ipos in queue) {
				var pos = queue[ipos];
				// 如果分支组存在
				if (pos.line_id == drawer.options.groupcode || content.find(".pos[code='"+pos.line_id+"']").length > 0) {
					var fanwei = (pos.line_id == drawer.options.groupcode ? content : content.find("div[code=\"" + pos.line_id + "\"]"));
					// 是标记节点并且前/后节点存在时
					if ((parseInt(pos.sign_position_id) == pos.position_id)) {
						if (fanwei.html() == "") {
							fanwei.append(drawer._createSimplePos(pos));
						} else {
							var prevNode = fanwei.children(".edgeposition").children(".just").children(".pos[posid=\"" + pos.prev_position_id + "\"]");
							var nextNode = fanwei.children(".edgeposition").children(".just").children(".pos[prevcode=\"" + pos.sign_position_id + "\"]");
							if (prevNode.length > 0) {
								prevNode.parent().parent().after(drawer._createSimplePos(pos));
							} else if (nextNode.length > 0) {
								nextNode.parent().parent().before(drawer._createSimplePos(pos));
							} else {
								newqueue.push(pos);
							}
						}
					} else {
						// 不是标记节点并且标记节点存在时
						var signNode = fanwei.children(".edgeposition").children(".just").children(".pos[posid=\"" + pos.sign_position_id + "\"]");
						if (signNode.length > 0) {
							signNode.last().after(drawer._createJuxtaPos(pos));
							signNode.parent().addClass("just-multi");
						} else {
							newqueue.push(pos);
						}
					}
				} else {
					newqueue.push(pos);
				}
			}
			queue = newqueue;
		}
		if (drawer.options.editable) {
			$("#" + drawer.thisid).prepend(content.html());
			$("#" + drawer.thisid + "_selectPoses").select2Buttons();
		} else {
			$("#" + drawer.thisid).html(content.html());
		}
	},
	reset : function(method, options) { // TODO
		flowchart_methods.init("reset");
	}
};

$.fn.flowchart = function (method, options) {

    // Method calling logic
    if (flowchart_methods[method]) {
        return flowchart_methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
    } else if (typeof method === 'object' || !method) {
        return flowchart_methods.init.apply(this, arguments);
    } else {
        $.error('Method ' + method + ' does not exist on jQuery.flowchart');
    }

};
})(jQuery);
