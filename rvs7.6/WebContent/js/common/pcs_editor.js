var pcsO = {
	forQa : false,
	$container : null,
	$pcs_pages : null,
	$pcs_contents : null,
	/** 工程检查票赋值 */
	valuePcs : function(data, isBreak) {
		if (isBreak == undefined) isBreak = false;
		var pcs_values = {};
		var pcs_comments = {};
		var $input = this.$pcs_contents.find("input");
		if (pcsO.forQa) {
			$input = $input.filter(function(){
				return this.name.substring(2, 3) == "6";
			});
		}
		$input.each(function(){
			if (this.type == "text") {
				if (!isBreak || this.value)
					pcs_values[this.name] = this.value;
			} else {
				if (!isBreak && this.className == "i_total_hidden")
					pcs_values[this.name] = this.value;
				else if (this.className == "i_sff") {}
				else if (!isBreak && this.name && this.name != "") pcs_values[this.name] = "";
			}
		});
		var $inputChecked = this.$pcs_contents.find("input:checked");
		if (pcsO.forQa) {
			$inputChecked = $inputChecked.filter(function(){
				return this.name.substring(2, 3) == "6";
			});
		}
		$inputChecked.each(function(){
			if (this.type == "radio"){
				if (isBreak 
						&& this.value == "0"
						&& this.name.substring(0, 2) == "EM") {
				} else {
					pcs_values[this.name] = this.value;
				}
			} else if (this.type == "checkbox"){
				if (pcs_values[this.name] == null || pcs_values[this.name] == "") {
					pcs_values[this.name] = this.value;
				} else {
					pcs_values[this.name] = pcs_values[this.name] + "," + this.value;
				}
			}
		});
		this.$pcs_contents.find("textarea").each(function(){
			if (this.className == "i_frequent") {
				if (this.getAttribute("changed")) {
					var tGI = this.nextSibling;
					if (pcs_comments[tGI.name]) {
						pcs_comments[tGI.name] = pcs_comments[tGI.name] + this.value;
					} else {
						pcs_comments[tGI.name] = this.value + "\n";
					}
				}
			} else
			if (this.value) {
				if (pcs_comments[this.name]) {
					pcs_comments[this.name] = pcs_comments[this.name] + this.value;
				} else {
					pcs_comments[this.name] = this.value;
				}
			}
		});
	
		data.pcs_inputs = Json_to_String(pcs_values);
		data.pcs_comments = Json_to_String(pcs_comments);
	
		for (var v in pcs_values) {
			if (!pcsO.forQa || pcsO.$container.find("input[name='"+v+"']").parents("#pcs_content_0").length > 0)
				if (pcs_values[v] == null || pcs_values[v] == "") return true;
		}
	
		return false;
	},
	generate : function(pcses, isLeader) {
		var tabs = "";
		var tabscount = 0;
		var contents = "";
		pcsO.$container.show();
		for (var pcsline in pcses) {
			var pcsgroup = pcses[pcsline];
			for (var pcsseq in pcsgroup) {
				tabs += '<input type="radio" '+(tabscount == 0 ? 'checked' : '')+' name="pcs_page" class="ui-button ui-corner-up-s ui-helper-hidden-accessible" id="pcs_page_'+tabscount+'"><label role="button" class="ui-state-default '+(tabscount == 0 ? 'ui-state-active' : '')+'" for="pcs_page_'+tabscount+'" title="'+pcsseq+'"><span class="ui-button-text">'+pcsseq+'</span></label>';
				contents+= '<div id="pcs_content_'+tabscount+'" '+(tabscount == 0 ? '' : 'style="display:none"')+' class="pcs_content">' + pcsgroup[pcsseq] + "</div>";
				tabscount++;
			}
		}
		this.$pcs_pages.html(tabs).buttonset();
		this.$pcs_contents.html(contents);
		this.$pcs_pages.find("input").click(function(){
			$("#pcscombutton").enable();
			pcsO.$pcs_contents.find(".pcs_content").hide();
			$("#" + this.id.replace("pcs_page_", "pcs_content_")).show();
		});
		if (0) {
			this.$pcs_contents.find("input,textarea").not(".i_sff").parent().css("background-color", "#93C3CD");
			this.$pcs_contents.find("input[name^='EN']").button();
			this.$pcs_contents.find("input.i_switchM").click(this._emSwitch);
		} else {
			this.$pcs_contents.find("input[name^='L'],textarea[name^='L']").parent().css("background-color", "#93C3CD");
			this.$pcs_contents.find("input[name^='E'],textarea[name^='E']").not(".i_sff").parent().css("background-color", "#F8FB84");
			this.$pcs_contents.find("input[name^='LN'],input[name^='EN']").button();
			this.$pcs_contents.find("input.i_switchM").click(this._emSwitch);
		}
		this.$pcs_contents.find("input:text").autosizeInput();

		var $EMs = this.$pcs_contents.find("input[name^='EM']");
		pcsO._checkEMs($EMs);
		$EMs.click(function(){pcsO._checkEMs($EMs)});
		$EMs.hide().next("label").hide();

		// 自动选择第一个可填写页
		var activePage = this.$pcs_contents.find("div:has(input):first");
		if (activePage.length == 1) {
			$("#" + activePage.attr("id").replace("pcs_content_", "pcs_page_")).trigger("click");
		}
		if (this.forQa) {
			var jdefects = this.$pcs_contents.find("td:contains('不合格')");
			if (jdefects.length > 0) {
				jdefects.eq(0).html(jdefects.eq(0).html().replace("不合格", "<font color='red'>不合格</font>"));
			}
		}
		this.$pcs_contents.find("td:contains('null')").text("");

		// 不是最新页的就灰色化
		this.$pcs_contents.find("div:not(:has(newstatus))").css("background-color", "gainsboro");
		// 管理员用的修正标记
		if (isLeader == "fix") 
			this.$pcs_contents.find("input, textarea").bind("change", function(){$(this).attr("fixed", "1")});

		// 文本输入项目回车依序
		var $EIs = this.$pcs_contents.find("input[name^='EI']");
		$EIs.sort(function(a, b) {
			return a.name > b.name ? 1 : -1;
		});
		$EIs.each(function(idx, ele){
			var $ele = $(ele);
			var idx = $EIs.index($(ele));
			idx++;
			if (idx == $EIs.length) idx =0;
			var $target = $EIs.eq(idx);
			var ele_content_id = $ele.parents(".pcs_content").attr("id");
			var tgt_content_id = $target.parents(".pcs_content").attr("id");
			$ele.bind("keypress", function(evt) {
				if(evt.keyCode == 13) {
					if (ele_content_id != tgt_content_id)
					$("#" + tgt_content_id.replace("pcs_content_", "pcs_page_")).trigger("click");
					$target.focus();
					$target.select();
				}
			});
		});

		// 常用备注信息{
		this.$pcs_contents.find(".i_frequent").bind("keypress", function(){
			$(this).attr("changed", true);
		});
	},
	_checkEMs : function($EMs){
		if (!$EMs.length) {
			pcsO.$pcs_contents.find(".i_total").text("合格")
			.removeClass("forbid")
			.next().val("1");
		}
		else if(!$EMs.filter("[checked][value=-1], [checked][value=1]").length) {
			pcsO.$pcs_contents.find(".i_total").text("不操作")
			.removeClass("forbid")
			.next().val("0");
		}
		else if($EMs.filter("[value=-1][checked]").length) {
			pcsO.$pcs_contents.find(".i_total").text("不合格")
			.addClass("forbid")
			.next().val("-1");
		} else {
			pcsO.$pcs_contents.find(".i_total").text("合格")
			.removeClass("forbid")
			.next().val("1");
		}
	},
	_emSwitch : function() {
		var $switchM = $(this);
		var thisval = $switchM.attr("checkval") || 0;
		var nextval = 0;
		if (thisval == 0) nextval = 1;
		if (thisval == 1) nextval = -1;
		$switchM.attr("checkval", nextval);

		var $checkTarget = $switchM.parent().find("input:radio[value=" + nextval + "]");
		$checkTarget.attr("checked", true);
		$checkTarget.trigger("click");
		$switchM.val($checkTarget.next().text());
	},
	init : function(container, forQa) {
		this.forQa = forQa;
		this.$container = container;
		this.$pcs_pages = pcsO.$container.find("#pcs_pages");
		this.$pcs_contents = pcsO.$container.find("#pcs_contents");
		if (this.$pcs_pages.length != 1) {
			this.$pcs_pages = pcsO.$container.find("#pcs_detail_pcs_pages");
			this.$pcs_contents = pcsO.$container.find("#pcs_detail_pcs_contents");
		}
	},
	clear : function(){
		this.$pcs_pages.html("");
		this.$pcs_contents.html("");
	}
}
