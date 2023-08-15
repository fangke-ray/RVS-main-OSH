var panelOverlay = 0;

var gridWidthMiddleRight = 992;

/**
 * 输入项内容变成标签文本
 */
var toLabelValue = function(jobj) {
	if (jobj.is("input:radio")) {
		return jobj.val();
	} else if (jobj.is("input:checkbox")) {
		
	} else if (jobj.is("input")) {
		return jobj.val();
	} else if (jobj.is("select")) {
		return jobj.find("option:selected").text();
	} else if (jobj.is("textarea")) {
		return jobj.val();
	} else if (jobj.is("label")) {
		return jobj.text();
	}
}

/*
 * AJAX给予filter标记,供session超时时判断动作
 */
var ajaxRequestType = function(XMLHttpRequest){
    XMLHttpRequest.setRequestHeader("RequestType", "ajax");
}

/*
 * Ajax通信成功时的处理
 * 页面跳转(可能)
 */
var ajaxSuccessCheck = function(data, textStatus) {

	if (data) {
		if (typeof data === "object") {
			if (data.redirect) {
				window.location.href = data.redirect;
			}
		} else if (typeof data === "string") {
			if (data.charAt(0) == '{'  && data.indexOf("redirect") >= 0) {
				var dataObj = $.parseJSON(data);
				if (dataObj.redirect) {
					window.location.href = dataObj.redirect;
				}
			}
		}
	}
}

var errorPop = function(errorData, closeToFocus) {
	if ($('div#popuperrstring').length > 0) {
		$('div#popuperrstring').html("<span class='errorarea'>" + errorData + "</span>")
			.show();
	} else {
		var $errstring = $('div#errstring');
		if ($errstring.length == 0) {
			$("body").append("<div id='errstring'/>");
			$errstring = $('div#errstring');
		}
		var timestamp = (new Date()).getTime();
		$errstring.data("timestamp", timestamp);
		$errstring.show();
		$errstring.dialog({dialogClass : 'ui-error-dialog', modal : true, width : 450, title : "提示信息", 
			buttons : {"确认" : function() { if($errstring.data("timestamp") == timestamp) $errstring.dialog("close"); }}, 
			close : function() {if (closeToFocus) closeToFocus.focus(); else $("#scanner_inputer").focus()}});
		$errstring.html("<span class='errorarea'>" + errorData + "</span>");
	}
}

var warningConfirm = function(warnData, yesFunction, noFunction, title, yesButton, noButton) {
	if ($('div#warningstring').length == 0) {
		$("body").append("<div id='warningstring'/>");
	}
	var $errstring = $('div#warningstring');
	var timestamp = (new Date()).getTime();
	var buttons = {};
	buttons[yesButton || "确认"] = function() { if($errstring.data("timestamp") == timestamp) $errstring.dialog("close"); if(yesFunction) yesFunction();}
	buttons[noButton || "取消"] = function() { if($errstring.data("timestamp") == timestamp) $errstring.dialog("close");if(noFunction) noFunction();}
	$errstring.data("timestamp", timestamp);
	$errstring.show();
	$errstring.dialog({dialogClass : 'ui-warn-dialog', modal : true, width : 450, title : (title || "提示信息"), 
		buttons : buttons,
		closeOnEscape: false,
		open: function() {
		$errstring.nextAll(".ui-dialog-buttonpane").bind("keypress", function(event) { 
	      if (event.keyCode == $.ui.keyCode.ENTER) { 
	      return false;
	      } 
	     }); 
	    },
	    close:function(event) {
	    	if(noFunction && event.currentTarget && event.currentTarget.className.indexOf("ui-dialog-titlebar-close") >= 0) 
	    		noFunction();
	    }
		});
	$errstring.html("<span class='errorarea'>" + warnData + "</span>");
};

var infoPop = function(infoData, closeToFocus, title) {
	if ($('div#popupinfostring').length > 0) {
		$('div#popupinfostring').html("<span class='informationarea'>" + infoData + "</span>")
			.show();
	} else {
		var $infostring = $('div#infostring');
		if ($infostring.length == 0) {
			$("body").append("<div id='infostring'/>");
			$infostring = $('div#infostring');
		}
		var timestamp = (new Date()).getTime();
		$infostring.data("timestamp", timestamp);
		$infostring.show();
		$infostring.dialog({dialogClass : 'ui-info-dialog', modal : true, width : 450, title : (title || "提示信息"), 
			buttons : {"确认" : function() { if($infostring.data("timestamp") == timestamp) $infostring.dialog("close"); }}, 
			close : function() {if (closeToFocus) closeToFocus.focus(); else $("#scanner_inputer").focus()}});
		$infostring.html("<span class='informationarea'>" + infoData + "</span>");
	}
}

/*
 * Ajax通信失敗時の処理
 */
var ajaxError = function(xhrobj, status, e) {
	if (xhrobj.status == 504) {
		warningConfirm("处理超时，请刷新页面后确认操作是否成功提交。"
		, function(){}
		, function(){location.reload();}
		, "处理超时", "之后刷新", "立刻刷新");
		$('div#warningstring .ui-warn-dialog').removeClass('.ui-warn-dialog').addClass(".ui-error-dialog");
	} else {
		console.log(xhrobj.status);
		// window.location.href = "/break.do";
	}
//	if ($('#pagecontent').length > 0) {
//		window.location.hash = "#error";
//	} else {
//		console.debug("name: " + e.name + " message: " + e.message + " lineNumber: "
//				+ e.lineNumber + " fileName: " + e.fileName + " stack: "
//				+ e.stack);
//		window.location.href = "/panel/error.html";
//	}
};

$(document).ajaxSend(function(evt, request, settings) {
	if(!settings.async) {
		if (panelOverlay == 0) {
			makeWindowOverlay();
		}
	}
	panelOverlay++;
});

$(document).ajaxComplete(function(evt, request, settings) {
	if(panelOverlay > 0) {
		panelOverlay--;
	}

	if(panelOverlay == 0) {
		killWindowOverlay();
	}
	if(request.status && request.status != 200 && request.status != 504) {
		alert("通信失败！请检查网络连接 status=" + request.status);
		return false;
	}
});

function makeWindowOverlay(){
	if ($("#woverlay").length == 0) {
		$(window).overlay();
		$("div.overlay").attr("id", "woverlay");
		setTimeout(killWindowOverlay, 5000);
	}
}

function killWindowOverlay(){
	if($("#woverlay").length > 0) {
		$("#woverlay").remove();
		$("body").css('overflow', 'auto');
		panelOverlay = 0;
	}
}

/*
 * jQuery Simple Overlay
 * A jQuery Plugin for creating a simple, customizable overlay. Supports multiple instances,
 * custom callbacks, hide on click, glossy effect, and more.
 *
 * Copyright 2011 Tom McFarlin, http://tommcfarlin.com, @moretom
 * Released under the MIT License
 *
 * http://moreco.de/simple-overlay
 */
(function($) {

	$.fn.overlay = function(options) {

		var opts = $.extend({}, $.fn.overlay.defaults, options);

		if (opts.action === "close") {
			var overlay = $(this).find(".overlay").eq(0);
			if (overlay.length == 0) {
				overlay = $(".overlay").eq(0);
			}
			close(overlay, opts);
			return;
		}

		return this.each(function(evt) {
			if(!$(this).hasClass('overlay-trigger')) {
			show(create($(this), opts), opts);
		}
	});
    
	}; // end overlay

  /*--------------------------------------------------*
   * helper functions
   *--------------------------------------------------*/
  
  /**
   * Creates the overlay element, applies the styles as specified in the 
   * options, and sets up the event handlers for closing the overlay.
   *
   * opts The plugin's array options.
   */
  function create($src, opts) {
  
    // prevents adding multiple overlays to a container
    $src.addClass('overlay-trigger');
  
    // create the overlay and add it to the dom
    var iTop = 0;
    if($.browser.mozilla && opts.container.toString() === 'body') { 
      iTop = $('html').scrollTop();
    } else {
      iTop = $(opts.container).scrollTop();
    } // end if/else
    
    var overlay = $('<div></div>')
      .addClass(opts.styleclass)
      .css({
        background: opts.color,
        opacity: opts.opacity,
        top: opts.container.toString() === 'body' ? iTop : $(opts.container).offset().top,
        left: $(opts.container).offset().left,
        width: opts.container === 'body' ? '100%' : $(opts.container).width(),
        height: opts.container === 'body' ? '100%' : $(opts.container).height(),
        position: 'absolute',
        zIndex: 1000,
        display: 'none',
        overflow: 'hidden'
      });

    // if specified, apply the gloss
    if(opts.glossy) {
      applyGloss(opts, overlay);     
    } // end if
    
    // setup the event handlers for closing the overlay
    if(opts.closeOnClick) {
      $(overlay).click(function() {
        close(overlay, opts);
        $src.removeClass('overlay-trigger');
      });
    } // end if
    
    // finally add the overlay
    $(opts.container).append(overlay);
   
    return overlay;
    
  } // end createOverlay
  
  /**
   * Displays the overlay using the effect specified in the options. Optionally
   * triggers the onShow callback function.
   *
   * opts The plugin's array options.
   */
  function show(overlay, opts) {
    
    switch(opts.effect.toString().toLowerCase()) {
    
      case 'fade':
        $(overlay).fadeIn('fast', opts.onShow);
        break;
      
      case 'slide':
        $(overlay).slideDown('fast', opts.onShow);
        break;
        
      default:
        $(overlay).show(opts.onShow);
        break;
    
    } // end switch/case
    
    $(opts.container).css('overflow', 'hidden');
    
  } // end show
  
  /**
   * Hides the overlay using the effect specified in the options. Optionally
   * triggers the onHide callback function.
   *
   * opts The plugin's array options.
   */
  function close(overlay, opts) {
    
    switch(opts.effect.toString().toLowerCase()) {
        
      case 'fade':
        $(overlay).fadeOut('fast', function() {
          if(opts.onHide) {
            opts.onHide();
          }
          $(this).remove();
        });
        break;
            
      case 'slide':
        $(overlay).slideUp('fast', function() {
          if(opts.onHide) {
            opts.onHide();
          }
          $(this).remove();
        });
        break;
            
      default:
        $(overlay).hide();
        if(opts.onHide) {
          opts.onHide();
        }
        $(overlay).remove();
        break;
            
    } // end switch/case
    
    $(opts.container).css('overflow', 'auto');
    
  } // end close
  
  /*--------------------------------------------------*
   * default settings
   *--------------------------------------------------*/
   
	$.fn.overlay.defaults = {
    color: '#000',
    opacity: 0.5,
    effect: 'none',
    onShow: null,
    onHide: null,
    closeOnClick: false,
    glossy: false,
    container: 'body',
    styleclass : 'overlay'
	}; // end defaults

})(jQuery);

(function($) {
	if ($.fn.fmatter) {
		$.fn.fmatter.rowactions = function(rid, gid, act, pos) {

			switch (act) {
			case 'edit':
				showEdit(rid);
				break;
			case 'del':
				showDelete(rid);
				break;
			}
		};
	}
})(jQuery);


//jquery 添加扩展方法：
(function($) {
	$.fn.disable = function(){
	  return this.each(function(){
	    if(typeof this.disabled!="undefined") {
	    	this.disabled=true;
	    	var jthis = $(this);
	    	if (jthis.is("input:radio") && this.id != null) {
	    		jthis = $("label[for=" + this.id + "]");
	    		jthis.attr("disabled", true);
	    	}
		    jthis.attr("aria-disabled", true);
		    jthis.addClass("ui-state-disabled");
	    }
	  });
	}

	$.fn.enable = function(){
	  return this.each(function(){
	    if(typeof this.disabled!="undefined") {
	    	this.disabled=false;
	    	var jthis = $(this);
	    	if (jthis.is("input:radio") && this.id != null) {
	    		jthis = $("label[for=" + this.id + "]");
	    		jthis.removeAttr("disabled");
	    	}
		    jthis.attr("aria-disabled", false);
		    jthis.removeClass("ui-state-disabled");
		    jthis.removeClass("ui-button-disabled");
	    }
	  });
	}
})(jQuery);

/**
 * jquery validate 提交时弹出窗口
 * @param {} e
 * @param {} v
 */
var jInvalidPop = function(e, v) {
	var sErrormsg = "";
	var firstErrorComponent = null;
	for (var iError in v.errorList){
		var errorline = v.errorList[iError];
		sErrormsg += errorline.message.replace(/\{alt\}/, $(errorline.element).attr("alt")) + "<br>";
		if (iError == 0) {
			firstErrorComponent = errorline.element;
		}
	}
	errorPop(sErrormsg, firstErrorComponent);
}

if ($.validator) {
	$.extend($.validator.defaults, {errorClass:"errorarea-single"});
	$.extend($.validator.messages, {
		required: "请输入{alt}的值",
		remote: "请修正该字段",
		email: "请为{alt}输入一个符合格式的邮箱地址。",
		url: "请输入合法的网址",
		date: "请为{alt}输入一个符合yyyy-mm-dd的形式的日期。",
		dateISO: "请输入合法的日期 (ISO).",
		number: "请为{alt}输入一个数值。",
		digits: "请为{alt}输入一个整数。",
		creditcard: "请为{alt}输入合法的信用卡号",
		equalTo: "请再次输入相同的值",
		accept: "请输入拥有合法后缀名的字符串",
		maxlength: $.validator.format("请为{alt}输入一个长度最多是 {0} 的字符串"),
		minlength: $.validator.format("请为{alt}输入一个长度最少是 {0} 的字符串"),
		rangelength: $.validator.format("请为{alt}输入一个长度介于 {0} 和 {1} 之间的字符串"),
		range: $.validator.format("请为{alt}输入一个介于 {0} 和 {1} 之间的值"),
		max: $.validator.format("请为{alt}输入一个最大为 {0} 的值"),
		min: $.validator.format("请为{alt}输入一个最小为 {0} 的值")
	});
	$.validator.setDefaults(
		//不报label错误信息
		{showErrors: function(){
			var i, elements;
			for ( i = 0; this.errorList[i]; i++ ) {
				var error = this.errorList[i];
				if ( this.settings.highlight ) {
					this.settings.highlight.call( this, error.element, this.settings.errorClass, this.settings.validClass );
				}
				//this.showLabel( error.element, error.message );
			}
			if( this.errorList.length ) {
				this.toShow = this.toShow.add( this.containers );
			}
			if (this.settings.success) {
				for ( i = 0; this.successList[i]; i++ ) {
					this.showLabel( this.successList[i] );
				}
			}
			if (this.settings.unhighlight) {
				for ( i = 0, elements = this.validElements(); elements[i]; i++ ) {
					this.settings.unhighlight.call( this, elements[i], this.settings.errorClass, this.settings.validClass );
				}
			}
			this.toHide = this.toHide.not( this.toShow );
			this.hideErrors();
			this.addWrapper( this.toShow ).show();
    	},
    	// 错误对象效果
		highlight : function(element, errorClass) {
			$(element).addClass(errorClass);
			blink($(element), "errorarea-blink", 6);
		},
		// 弹出框形式报错
		invalidHandler : jInvalidPop,
		// 隐藏的下拉框予以检查
		ignore : "input[type='text']:hidden"
	});
    
}
if ($.datepicker) {
	var datepicker_CurrentInput;
	var inholidays = function(date, holidays) {
		for (var iholidays = 0;iholidays < holidays.length; iholidays++) {
			if (holidays[iholidays] == date) {
				return iholidays;
			}
		}
		return -1;
	}
	var gBeforeShowDay = function(date) {

		var $this = $(this);
		var dpMonth = $this.data("dpMonth");
		if (!dpMonth) {
			var thisData = $this.data().datepicker;
			dpMonth = thisData.drawYear + "/" + fillZero(thisData.drawMonth + 1 + "");
			$this.data("dpMonth", dpMonth);
		}

		if (header_holidays) {
			var holidays = header_holidays[dpMonth];

			if (!holidays && typeof(header_getInholidays) == "function") {
				header_getInholidays(dpMonth);
				holidays = header_holidays[dpMonth]
			}

			if (holidays) {
				var weekday = date.getDay();
				var thedate = date.getDate();
				var bWeekend = (weekday===0 || weekday===6);

				var bInholidays = (inholidays(thedate, holidays) >= 0);
				if ((bWeekend && !bInholidays) || (!bWeekend && bInholidays)) {
					return [true, 'ui-datepicker-holiday'];
				} else {
					return [true, ''];
				}
			}
		} else {
			var weekday = date.getDay();
			var bWeekend = (weekday===0 || weekday===6);

			if (bWeekend) {
				return [true, 'ui-datepicker-holiday'];
			} else {
				return [true, ''];
			}
		}
	};
	var gMonthHoliday = function(year, month) {
		var dpMonth =  year + "/" + fillZero(month);
		$(this).data("dpMonth", dpMonth);
	}

	$.datepicker.setDefaults({
		dayNamesMin: ['<span style="color:red;">日</span>','一','二','三','四','五','<span style="color:red;">六</span>'],
		monthNames: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
		monthNamesShort:['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
		showMonthAfterYear: true,
		changeYear: true,
		changeMonth: true,
		yearRange: "2013:+1",
		yearSuffix: '<span>年</span>',
		closeText:'清空',
		dateFormat: 'yy/mm/dd',
		beforeShow: function (input, inst) { $(this).data("dpMonth", null); datepicker_CurrentInput = input; },
		beforeShowDay : gBeforeShowDay,
		onChangeMonthYear : gMonthHoliday
	});
	$(".ui-datepicker-close").live("click", function (){
		datepicker_CurrentInput.value = "";
	});
}

/**
 * 后台传来的信息在画面上表示
 * @param String range
 * @param {} msgInfos
 */
var treatBackMessages = function(range, msgInfos) {
	if (!msgInfos) return;
	var sErrormsg = "";
	var firstErrorComponent = null;
	for (var ierror in msgInfos){
		var msgInfo = msgInfos[ierror];
		sErrormsg += (msgInfo.lineno ? "[" + msgInfo.lineno + "]" : "") + decodeText(msgInfo.errmsg) + "<br>";
		if (range != null) {
			// 指定项目范围的时候，范围内对应项目标示错误
			var componentid = msgInfo.componentid;
			var component = $(range + " *[id$='_"+componentid+"']");
			if (ierror == 0) {
				firstErrorComponent = component[0];
			}
			component.addClass("errorarea-single");
			if (component[0] && "TABLE" === component[0].tagName) {
				blink(component.find("tr:first"), "errorarea-blink", 6);				
			} else {
				blink(component, "errorarea-blink", 6);
			}
		}
	}
	// 共通出错信息框
	errorPop(sErrormsg, firstErrorComponent);
}

var setReferChooser = function(target, jthis, jfather, callback) {
	if (jthis == null || jthis.length == 0) {
		jthis = $(".referchooser");
	}
	
	if (jfather) { // modify with fengxc
		jfather.change(function(){
			var text = $(this).find("option:selected").text().toLowerCase();
			var value = $(this).val();
			if (value === null || value === "") {
				refTr.show();
			} else {
				refTr.hide();

				refTr
				.map(function(){
					var hit = false;
					$(this).children("td:not('.referId')").each(function(idx, ele) {
						if ((ele.textContent || ele.innerText || "").toLowerCase().indexOf(text) >= 0) {
							hit = true;
						}
					});
					
					if (hit) {
						return this;
					}
				})
				.show();
			}
		});
	}

	var shower = target.prev("input:text");
	var filter = jthis.find("input:eq(0)");
	var clearer = jthis.find("input:eq(1)");
	var refForm = jthis.find(".subform");
	var refTr = refForm.find("tr");

	var to;

	shower.click(function(e){
		var uiDialogTodo = jthis.closest(".ui-dialog");
		if (uiDialogTodo.length == 0) {
			uiDialogTodo = $(".ui-dialog:visible");
			var zIndex = 1000;
			if (uiDialogTodo.length > 0) {
				zIndex = parseInt(uiDialogTodo.last().css("zIndex")) + 1
			}
			jthis.css({"top" : shower.offset().top + shower.height() - 5, "left" : shower.offset().left,
				"zIndex": zIndex}).show("fast");
		} else {
			jthis.css({"top" : shower.position().top + shower.height() - 5, "left" : shower.position().left}).show("fast");
		}
		filter.val("").trigger("change");
		jthis[0].scrollTop = 0;
		jthis.show("fade", function() {
			filter.focus();
		});
		$(".referchooser_shower").removeClass("referchooser_shower");
		$(".referchooser_target").removeClass("referchooser_target");
		shower.addClass("referchooser_shower");
		target.addClass("referchooser_target");
	})

//	shower.focus(function(){
//		clearTimeout(to);
//	});
//	shower.blur(function(){
//		jthis.focus();
//	});

	target.change(function(){
		if (this.value === "") {
		} else {
			var hitValue = this.value;
			var filterVal = refTr.find(".referId").filter(function(idx, ele){
				if (hitValue != null
					&& (ele.textContent || ele.innerText || "") === hitValue) {
					hitValue = null;
					return true;
				}
				return false;
			});
			shower.val(filterVal.next().text()).focus();
		}
	});

	if (!jthis.attr("beset")) {

		jthis.attr("beset", true);

		jthis.blur(function(){
			if(!jthis.is(":hidden")) {
				to = setTimeout(function() {jthis.hide('fade');}, 200);
			}
		})
		filter.focus(function(){
			clearTimeout(to);
		});
		filter.blur(function(){
			jthis.focus();
		});
	
		filter.change(function() {
			refTr = jthis.find(".subform > tbody > tr");
			if (this.value === "") {
				if (jfather) {  // modify with fengxc
					jfather.change();//如果过滤是空,关联父类
				} else {
					refTr.show();
				}
			} else {
				refTr.hide();
				var text = this.value.toLowerCase();
				refTr
				.map(function(){
					var hit = false;
					$(this).children("td:not('.referId')").each(function(idx, ele) {
						if ((ele.textContent || ele.innerText || "").replace(/\s/gi," ").toLowerCase().indexOf(text) >= 0) {
							hit = true;
						}
					});
					
					if (hit) {
						return this;
					}
				})
				.show();
			}
		});
		clearer.click(function() {
			clearTimeout(to);
			jthis.hide();
			$(".referchooser_target").val("");
			$(".referchooser_shower").val("").focus();
			if (callback != null) callback();
		});
	}

	refForm.off("click").on("click", "tr", function() {
		clearTimeout(to);
		jthis.hide();
		$(".referchooser_target").val($(this).find("td:eq(0)").text());
		$(".referchooser_shower").val($(this).find("td:eq(1)").text()).focus();
		if (callback != null) callback(this);
	})
	.off("mouseover").on("mouseover", "tr", function() {$(this).addClass('ui-state-hover');})
	.off("mouseout").on("mouseout", "tr", function() {$(this).removeClass('ui-state-hover');});
}

$(".ui-widget-overlay").on("click", function(){}); // alert (1);
//
//$(window).unload(function(e){
//  alert("Goodbye!"+e);
//});

/*
 * jQuery Autosize Input
 */
(function($) {
	var textvalue = null;
	$.fn.autosizeInput = function(options) {

	    var opts = $.extend({}, $.fn.autosizeInput.defaults, options);
		return this.each(function(evt) {
			resize(this, opts);
			$(this).bind("input", function(){resize(this, opts)});
		});
    
	}; // enable

  /**
   * REsize
   */
	function resize(textinput, opts) {

		textvalue = textinput.value;
//		alert(textvalue);
		var fullobjs = textvalue.match(/[^\x00-\xff]/g); // fullwide chars
		var esize = textvalue.length + (fullobjs ? fullobjs.length : 0);

		if (esize > opts.minsize && esize < opts.maxsize) {
			textinput.size = esize * 1.2; // TODO 根据字宽计算
    	} else if (esize <= opts.minsize) {
    		textinput.size = opts.minsize;
    	}
	} // end close
  
  /*--------------------------------------------------*
   * default settings
   *--------------------------------------------------*/
   
	$.fn.autosizeInput.defaults = {
	    minsize: 5,
	    maxsize: 40
	}; // end defaults

})(jQuery);