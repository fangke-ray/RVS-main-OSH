let browser={
    versions:function(){
           var u = navigator.userAgent, app = navigator.appVersion;
           return {//移动终端浏览器版本信息
                trident:u.indexOf('Trident') > -1, //IE内核
                presto:u.indexOf('Presto') > -1, //opera内核
                webKit:u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                gecko:u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                mobile:!!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
                ios:!!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),//ios终端
                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
                iPad: u.indexOf('iPad') > -1, //是否iPad
                webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
            };
         }(),
    language:(navigator.browserLanguage || navigator.language).toLowerCase()
};

/** 服务器处理路径 */
var servicePath = "fact_recept_material.do";

//当前选中Tab
let activeTab;
// 按键相应范围
let inputArea = "page";

// 用来判断滚动容器是否正在滚动中
let isScrolling = false;

//字母导航用
let undirectModelMap = new Map();
let directModelMap = new Map();
let perlModelMap = new Map();
let spareModelMap = new Map();

/** 机种ENDOEYE ID **/
const CATEGORY_ENDOEYE_ID = "00000000016"; // TODO
/** 机种光学识管ID **/
const CATEGORY_UDI_ID = "00000000055"; // TODO
/** 机种摄像头ID **/
const CATEGORY_CAMERA_ID = "00000000024"; // TODO
const CATEGORY_CAMERA_ADAPTOR_ID = "00000000028"; // TODO
const NO_TEXT = "无";

$(function() {
    //禁止滑动页面
    document.addEventListener('touchmove', function(e) {
        e.preventDefault();
    }, { capture: false, passive: false });

    //禁止屏幕双击缩放
    let lastTouchEnd = 0;
    document.documentElement.addEventListener('touchend', function(event) {
        let now = Date.now();
        if (now - lastTouchEnd <= 400) {
            event.preventDefault();
        }
        lastTouchEnd = now;
    }, false);

    //禁用双指缩放
    document.documentElement.addEventListener('touchstart', function(event) {
        if (event.touches.length > 1) {
            event.preventDefault();
        }
    }, false);

	document.onkeydown=function(event){
		var e = event || window.event || arguments.callee.caller.arguments[0];
		if (e && e.keyCode) {

			if (inputArea === "page") {
				switch(event.keyCode) {
				     case 8:
				     	$(".calculator #clear").trigger("release");
				        break;
				     case 27:
				     	$(".calculator #clearAll").trigger("release");
				        break;
				     case 48:				     case 49:
				     case 50:				     case 51:
				     case 52:				     case 53:
				     case 54:				     case 55:
				     case 56:				     case 57:
				     	var numkey = event.keyCode - 48;
				     	$(".calculator span[value=" + numkey + "]").trigger("release");
				     	break;
				}
			} else if (inputArea === "keyboard") {
				switch(event.keyCode) {
				     case 8:
				     	$("#model_keyboard span:contains('删字')").trigger("release");
				        break;
				     case 27:
				     	$("#model_keyboard span:contains('清除')").trigger("release");
				     	break;
				     case 32:
				     	$("#model_keyboard_space").trigger("release");
				     	break;
				     case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: {
				     	var numkey = event.keyCode - 48;
				     	$("#model_keyboard span:contains('" + numkey + "')").trigger("release");
				     	break;
				     }
	                 // 65 ~ 90
				     case 65:case 66:case 67:case 68:case 69:case 70:case 71:case 72:case 73:case 74:case 75:case 76:case 77:case 78:case 79:case 80:case 81:case 82:case 83:case 84:case 85:case 86:case 87:case 88:case 89:case 90: {
				     	var abfkey = String.fromCharCode(event.keyCode);
				     	$("#model_keyboard span:contains('" + abfkey + "')").trigger("release");
				     	break;
				     }
				     case 173: {
				     	$("#model_keyboard span:contains('-')").trigger("release");
				     }
				     case 13: {
				     	if ($("#model_list > span").length == 1) {
				     		$("#model_list > span:eq(0)").trigger("release");
				     	}
				     }
				}
			}
			// 173 -
			
			if (inputArea !== "input" && event.keyCode == 8) {
				return false;
			} else if (inputArea !== "page" && event.keyCode == 27) {
				event.preventDefault();
				return false;
			}
		}
	}; 

    undirectObj.init();
    directObj.init();
    perlObj.init();
	if ($("#role").val() === "none") {
		$("#spare").remove();
	} else {
		spareObj.init();
	}

    //Tab切换
    $("#production_type").find(".btn").each(function(i, item) {
        $(item).hammer().on("tap", function(e) {
            let $this = $(this);
            activeTab = $this.attr("id");

            $("#production_type .btn").removeClass("ui-state-active");
            $this.addClass("ui-state-active");

            $("[id$='_tab']").removeClass("active");
            $("#" + $this.attr("target")).addClass("active");

            //清除字母选中
            clearLetterActive();
            //不需要的字母隐藏
            toggleLetter();

            $("#screen").text("");
            filterSerialNO("");
        });
    });

    //默认选中非直送
    $("#undirect").trigger("tap");

    //switch开关控制
    $(".switch-container > span").hammer().on("tap", function() {
        let $outerContainer = $(this).closest(".switch");
        let $innerContainer = $(this).closest(".switch-container");
        if ($("#toggleBtn").prop("checked")) {
            $innerContainer.css("margin-left", "-" + $(this).outerWidth() + "px");
            $outerContainer.removeClass("switch-on").addClass("switch-off");
            $("#toggleBtn").prop("checked", false);
            findit();
        } else {
            $innerContainer.css("margin-left", "0px");
            $outerContainer.removeClass("switch-off").addClass("switch-on");
            $("#toggleBtn").prop("checked", true);
            findit();
        }
    });

    //角色判断
    if ($("#role").val() == "lineleader") {
        $(".switch").removeClass("switch-off").addClass("switch-on");
        $(".switch-container").css("margin-left", "0px");
        $("#toggleBtn").prop("checked", true);
    } else {
        $(".switch").removeClass("switch-on").addClass("switch-off");
        $(".switch-container").css("margin-left", "-" + $(".switch-container span:eq(0)").outerWidth() + "px");
        $("#toggleBtn").prop("checked", false);
    }

    findit();

    //型号导航滚动事件
    var scrollByModel = function(modelName, fromInitial) {
        let el = $("#" + activeTab + "_tab").find("li[data-value='" + modelName + "']").get(0);

        let currentScroll;
        if (activeTab == "undirect") {
            currentScroll = undirectObj.scroll;
        } else if (activeTab == "direct") {
            currentScroll = directObj.scroll;
        } else if (activeTab == "perl") {
            currentScroll = perlObj.scroll;
        } else if (activeTab == "spare") {
        	currentScroll = spareObj.scroll;
        }

        currentScroll.scrollToElement(el, 400, true, true, IScroll.utils.ease.quadratic);
        if (!fromInitial) $("#model_nav").removeClass("active");
    }

    // 型号不点收起
    var closeModelNavTO = null; 
    var closeModelNav = function(){$("#model_nav").removeClass("active")};

    //字母导航
    $("#letter_nav > li").hammer().on("tap", function() {
        clearLetterActive();
        clearTimeout(closeModelNavTO);

        let $this = $(this);
        let letter = $this.attr("data-value");
        $this.addClass("active");

        let arrModels = [];
        if (activeTab == "undirect") {
            arrModels = undirectModelMap.get(letter);
        } else if (activeTab == "direct") {
            arrModels = directModelMap.get(letter);
        } else if (activeTab == "perl") {
            arrModels = perlModelMap.get(letter);
        } else if (activeTab == "spare") {
            arrModels = spareModelMap.get(letter);
        }

        let currentScroll;
        if (activeTab == "undirect") {
            currentScroll = undirectObj.scroll;
        } else if (activeTab == "direct") {
            currentScroll = directObj.scroll;
        } else if (activeTab == "perl") {
            currentScroll = perlObj.scroll;
        } else if (activeTab == "spare") {
        	currentScroll = spareObj.scroll;
        }

        if (letter == NO_TEXT) {
            let el = $("#" + activeTab + "_tab").find("li[data-value='" + letter + "']").get(0);

            currentScroll.scrollToElement(el, 400, true, true, IScroll.utils.ease.quadratic);
        } else {
            if (arrModels.length > 0) {
            	let firstModelNameOfLetter = null;
                let content = "";
                for (let modelName of arrModels) {
                    if (modelName != NO_TEXT) {
                        content += '<li data-value="' + modelName + '">' + modelName + '</li>';
                    }
                    if (firstModelNameOfLetter == null) firstModelNameOfLetter = modelName;
                }
                $("#model_nav").addClass("active").find("ul").html(content);
                closeModelNavTO = setTimeout(closeModelNav, 2500);

                // 当前字母不在显示位置时，滚动到第一个型号
                let initialShowing = false;

                $(currentScroll.wrapper).find(".list-group-item").each(function(idx, ele){
                	if (initialShowing) {
                		return;
                	}
                	let modelDataValue = $(ele).attr("data-value");
                	if (modelDataValue && modelDataValue.charAt(0) === letter) {
                		var positionTop = $(ele).position().top;
                		if (positionTop > 0 && positionTop < 520) {
                			initialShowing = true;
                		}
                	}
                });

                if (!initialShowing) scrollByModel(firstModelNameOfLetter, true);
            }
        }
    });

    //数字键盘
    $(".keys span").hammer().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function(ev) {
        $(this).removeAttr("hold");

        let value = $("#screen").text().trim();
        if (ev.target.id == "clear") {
            if (value.length != 0) {
                value = value.substring(0, value.length - 1);
                $("#screen").text(value);
                filterSerialNO(value);
            }
        } else if (ev.target.id == "clearAll") {
            if (value.length != 0) {
                $("#screen").text("");
                filterSerialNO("");
            }
        } else {
            $("#screen").append($(this).attr("value"));
            filterSerialNO($("#screen").text());
        }
        clearLetterActive();
    });

    //型号导航
    $(document).hammer().on("touch", "#model_nav li", function() {
        $(this).attr("hold", true);
    }).on("release", "#model_nav li", function() {
        $(this).removeAttr("hold");

        let modelName = $(this).attr("data-value");
        scrollByModel(modelName);
    });

    $(".btn.reset").each(function(index,ele){
    	$(ele).hammer().on("touch",function(){
	    	$(this).attr("hold",true);
	    }).on("release",function(){
	    	$(this).removeAttr("hold");
	    	$("#" + $(this).attr("for")).val("");
	    });
    });

    // 退出本页面
    $("#exitToPanel").hammer().on("tap", function(){
    	document.location.href = "panel.do";
    });

    // 取得全型号
    $.ajax({
        beforeSend: ajaxRequestType,
        async: true,
        url: 'model.do?method=getModelDictionary',
        cache: false,
        data: null,
        type: "post",
        dataType: "json",
        success: ajaxSuccessCheck,
        error: ajaxError,
        complete: function(xhrobj, textStatus) {
        	var resInfo = $.parseJSON(xhrobj.responseText);
        	gModelDict = resInfo.modelDict;
        }
    });

    //型号键盘
    $("#model_keyboard span").hammer().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function(ev) {
        $(this).removeAttr("hold");
        switch(this.innerText) {
        	case "":{
        		filterIdx+=" "; break;
        	}
        	case "汉":{
        		filterIdx="汉"; break;
        	}
        	case "清除":{
        		filterIdx=""; break;
        	}
        	case "删字":{
        		filterIdx = filterIdx.substring(0, filterIdx.length - 1); break;
        	}
        	case "关闭":{
        		$("#model_keyboard_pop").hide();
        		inputArea = "input";
        		break;
        	}
        	default : {
        		filterIdx+=this.innerText;
        	}
        }
        filterSelModel();
    });

	// $("span.animal").hide();
}); // End of $(function()

function findit() {
    $.ajax({
        beforeSend: ajaxRequestType,
        async: true,
        url: servicePath + '?method=search',
        cache: false,
        data: {search_range : ($("#search_switch").hasClass("switch-off") ? 2 : 1)},
        type: "post",
        dataType: "json",
        success: ajaxSuccessCheck,
        error: ajaxError,
        complete: function(xhrobj, textStatus) {
            var resInfo = null;
            try {
                // 以Object形式读取JSON
                eval('resInfo =' + xhrobj.responseText);
                if (resInfo.errors.length > 0) {
                    // 共通出错信息框
                    treatBackMessages("", resInfo.errors);
                } else {
                    // 直送/非直送维修对象
                    let materialList = resInfo.materialList;
                    let arrDirectList = [];
                    let arrUnDirectList = [];
                    let arrPerlList = [];

                    for (let i in materialList) {
                        if (materialList[i].kind == '7') { //周边
                            arrPerlList.push(materialList[i]);
                        } else if (materialList[i].direct_flg == '1') { //直送
                            arrDirectList.push(materialList[i]);
                        } else { //非直送
                            arrUnDirectList.push(materialList[i]);
                        }
                    }

                    let tempList = resInfo.tempList;
                    let tempDirectList = [];
                    let tempUnDirectList = [];
                    for (let i in tempList) {
                        tempList[i]["fact_recept"] = "1";
                        if (tempList[i].direct_flg == '1') { //直送
                            tempDirectList.push(tempList[i]);
                        } else {
                            tempUnDirectList.push(tempList[i]);
                        }
                    }

                    let arrSpareList = [];
                    if (resInfo.spareMaterialList) {
                    	arrSpareList = resInfo.spareMaterialList;
                    }

                    setList("undirect_scroll", arrUnDirectList, tempUnDirectList);
                    setList("direct_scroll", arrDirectList, tempDirectList);
                    setList("perl_scroll", arrPerlList, []);
                    setList("spare_scroll", arrSpareList, []);
                    toggleLetter();

                    $("#today_num").text(resInfo.todayFinishedNum);

                    //下一个工作日
                    let strNextDay = resInfo.nextDay;
                    strNextDay = new Date(strNextDay);
                    strNextDay.setHours(0);
                    strNextDay.setMinutes(0);
                    strNextDay.setSeconds(0);
                    strNextDay.setMilliseconds(0);

                    let nextNum = 0;
                    for (let i in materialList) {
                        let obj = materialList[i];
                       //  console.log(obj.serial_no + ":" + obj.expect_arrive_time + " " + obj.fact_recept);
                        if (obj.fact_recept == '0') {
                            let arriveDate = new Date(obj.expect_arrive_time);
                            arriveDate.setHours(0);
                            arriveDate.setMinutes(0);
                            arriveDate.setSeconds(0);
                            arriveDate.setMilliseconds(0);

                            if ((arriveDate.getTime() - strNextDay.getTime()) == 0) {
                                nextNum++;
                            }
                        }
                    }
                    $("#nextday_num").text(nextNum);

                    setPreprintedLocations(resInfo.preprintedLocations, resInfo.animalExpLocations, resInfo.allEmptyLocations);

                    filterSerialNO($("#screen").text());

                }
            } catch (e) {};
        }
    });
}

var gPreprintedLocations = {};
var gShelfLocations = {};

var setPreprintedLocations = function(preprintedLocations, animalExpLocations, allEmptyLocations) {
	gPreprintedLocations["normal"] = [];
	gPreprintedLocations["endoeye"] = [];
	gPreprintedLocations["udi"] = [];
	gPreprintedLocations["animal"] = [];
	gShelfLocations["normal"] = [];
	gShelfLocations["endoeye"] = [];
	gShelfLocations["udi"] = [];

	for (var i in preprintedLocations) {
		var preprintedLocation = preprintedLocations[i];
		switch (preprintedLocation.direct_flg) {
			case 0 :
			gPreprintedLocations["normal"].push(preprintedLocation.location); break;
			case 1 :
			gPreprintedLocations["endoeye"].push(preprintedLocation.location); break;
			case 2 :
			gPreprintedLocations["udi"].push(preprintedLocation.location); break;
		}
	}

	for (var i in allEmptyLocations) {
		var allEmptyLocation = allEmptyLocations[i];
		switch (allEmptyLocation.direct_flg) {
			case 0 :
			gShelfLocations["normal"].push(allEmptyLocation); break;
			case 1 :
			gShelfLocations["endoeye"].push(allEmptyLocation); break;
			case 2 :
			gShelfLocations["udi"].push(allEmptyLocation); break;
		}
	}

	if (animalExpLocations) {
		for (var i in animalExpLocations) {
			gPreprintedLocations["animal"].push(animalExpLocations[i].location);
		}
	}
	var info = "", infoAn = "";

	if (gShelfLocations["normal"].length == 0) {
		info += "普通内窥镜的通箱库位已满。\n";
	}
	if (gShelfLocations["endoeye"].length == 0) {
		info += "Endoeye的通箱库位已满。\n";
	}
	if (gShelfLocations["udi"].length == 0) {
		info += "光学视管的通箱库位已满。\n";
	}
	if (gPreprintedLocations["animal"].length == 0) {
		infoAn = "已经没有动物实验用周转箱库位，请确认。";
	}

	if (info && info.length) {
		infoPop(info);
	}
	if (infoAn && infoAn.length) {
//		infoPop(infoAn);
	}
}

var modelDictionary = {};
let gModelDict = {};
let filterIdx = "";

function selectModel($target, type) {

	var $modelKeyboard = $("#model_keyboard_pop");
	var cssZIndex = $target.closest(".ui-dialog").css("zIndex");
	if (cssZIndex) {
		cssZIndex = parseInt(cssZIndex) + 1;
	} else {
		cssZIndex = 1001;
	}
	$modelKeyboard.data("type", type);
	filterIdx = "";
	inputArea = "keyboard";
	$modelKeyboard.css("zIndex", cssZIndex).show();

    //型号选择
    $("#model_list").html("")
    	.off("release").hammer().on("release", function(evt) {
    	var targetName = evt.target.tagName;
    	var $selModel = null;
    	if (targetName === "SPAN") {
    		$selModel = $(evt.target);
    	} else if (targetName === "H") {
    		$selModel = $(evt.target).parent();
    	} else {
    		return;
    	}
    	$modelKeyboard.hide();
    	inputArea = "input";
    	$target.text($selModel.text())
    		.next("input:hidden")
    		.val($selModel.attr("model_id"))
    		.attr("category_id", $selModel.attr("category_id"))
    		.trigger("change");
    });
}

function filterSelModel() {
	var doClear = false;
	if (!filterIdx) {
		doClear = true;
	} else {
		if (filterIdx.length < 2) {
			if (filterIdx !== "汉") {
				doClear = true;
			}
		}
	}

	if (doClear) {
		if (filterIdx && filterIdx !== "汉") {
			$("#model_list").html("<pre>" + filterIdx + "</pre>");
		} else {
			$("#model_list").html("");
		}
	} else {
		var type = $("#model_keyboard_pop").data("type");
		var dct = gModelDict[type];

		if (filterIdx.length < 4) {
			var modelList = dct[filterIdx];
			if (modelList) {
				var modelHtml = "";
				for (var modelIdx in modelList) {
					var model = modelList[modelIdx];
					modelHtml += "<span model_id='" + model.model_id + "' category_id='" + model.category_id + "'><h>" 
					 + model.name.substring(0, filterIdx.length)
					 + "</h>" + decodeText(model.name.substring(filterIdx.length)) + "</span>";
				}
				$("#model_list").html(modelHtml);					
			} else {
				if (dct[filterIdx.substring(0,2) + "-"]) {
					aimaiMatch(dct, filterIdx, 2);
				} else {
					$("#model_list").html("<pre>" + filterIdx + "</pre>");
				}
			}
		} else {
			aimaiMatch(dct, filterIdx, 3);
		}
	}
}

function aimaiMatch(dct, filterIdx, fcount) {
	
	var modelList = dct[filterIdx.substring(0, fcount)];
	if (fcount == 3 && !modelList) {
		fcount = 2;
		modelList = dct[filterIdx.substring(0, fcount)];
	}
	if (modelList) {
		var modelHtml = "";
		var filterLength = filterIdx.length;
		var filterWord = filterIdx.match(/[\w]/g);
		var wordMatch = filterWord && (filterWord.length == filterLength);
		for (var modelIdx in modelList) {
			var model = modelList[modelIdx];
			var modelName = model.name;
			if (modelName.length < filterLength) {
				continue;
			}
			if (modelName.substring(0, filterLength) === filterIdx) {
				modelHtml += "<span model_id='" + model.model_id + "' category_id='" + model.category_id + "'><h>" 
				 + model.name.substring(0, filterLength)
				 + "</h>" + model.name.substring(filterLength) + "</span>";
				continue;
			}
			if (wordMatch) {
				var namePart = modelName.substring(fcount);
				var matchIn = {}, mDcount = false;
				for (var j = fcount; j < filterLength; j++) {
					var hitIdx = namePart.indexOf(filterIdx.charAt(j));
					if (hitIdx >= 0) {
						matchIn[hitIdx] = 1;
					} else {
						mDcount = true;
					}
				}
				if (Object.keys(matchIn).length > 0 && !mDcount) {
					var concatPart = "";
					for (var it in namePart) {
						if (matchIn[it]) {
							concatPart += "<h>" + namePart[it] + "</h>"; 
						} else {
							concatPart += decodeText(namePart[it]);
						}
					}
					modelHtml += "<span model_id='" + model.model_id + "' category_id='" + model.category_id + "'><h>" 
					 + model.name.substring(0, fcount)
					 + "</h>" + concatPart + "</span>";
				}
			}
		}
		if (modelHtml) {
			$("#model_list").html(modelHtml);					
		} else {
			$("#model_list").html("<pre>" + filterIdx + "</pre>");
		}
	} else {
		$("#model_list").html("<pre>" + filterIdx + "</pre>");
	}
}

//字母导航自动切换
function toggleLetter() {
    let map;
    if (activeTab == "undirect") {
        map = undirectModelMap;
    } else if (activeTab == "direct") {
        map = directModelMap;
    } else if (activeTab == "perl") {
        map = perlModelMap;
    } else if (activeTab == "spare") {
        map = spareModelMap;
    }

    $("#letter_nav li").each((index, li) => {
        let $li = $(li);
        let value = $li.attr("data-value");
        map.has(value) ? $li.removeClass("hide") : $li.addClass("hide");
    });
}

/**
 * 生成list
 * id:滚动容器ID
 * list:集合对象数据
 **/
function setList(id, list, tempList) {
    let content = "";
    let modelMap = new Map();
    let arrModelName = new Array();
    let tab = id.replace("_scroll", "");

    //整合数据形成Map,按型号名称统计机身号   
    //<modelName,[Object,Object,Object...]>
    for (let i in list) {
        let obj = list[i];
        let modelName = obj.model_name;

        arrModelName.push(modelName);

        if (modelMap.has(modelName)) {
            let arr = modelMap.get(modelName);
            arr.push(obj);
            modelMap.set(modelName, arr);
        } else {
            let arr = [];
            arr.push(obj);
            modelMap.set(modelName, arr);
        }
    }

    for (let i in tempList) {
        let obj = tempList[i];
        let modelName = obj.model_name;
        if (!modelName) {
            modelName = NO_TEXT;
        }

        arrModelName.push(modelName);
        if (modelMap.has(modelName)) {
            let arr = modelMap.get(modelName);
            arr.push(obj);
            modelMap.set(modelName, arr);
        } else {
            let arr = [];
            arr.push(obj);
            modelMap.set(modelName, arr);
        }
    }

    switch (tab) {
        case "undirect": //非直送
            setLetterMap(arrModelName, undirectModelMap);
            break;
        case "direct": //直送
            setLetterMap(arrModelName, directModelMap);
            break;
        case "perl": //其他（周边）
            setLetterMap(arrModelName, perlModelMap);
            break;
        case "spare": //备品
            setLetterMap(arrModelName, spareModelMap);
            break;
        default:
            break;
    }

    //当天
    let now = new Date();
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);
    now.setMilliseconds(0);

    //无型号
    let arrNoModel = [];
    if (modelMap.has(NO_TEXT)) {
        arrNoModel = modelMap.get(NO_TEXT);
        modelMap.delete(NO_TEXT);
    }

    let arrayObj = Array.from(modelMap);
    arrayObj.sort(function(a, b) { return a[0].localeCompare(b[0]) });
    modelMap = new Map(arrayObj.map(i => [i[0], i[1]]));

    if (arrNoModel.length > 0) {
        modelMap.set(NO_TEXT, arrNoModel);
    }

    for (let key of modelMap.keys()) {
        let arr = modelMap.get(key);

        content += '<li class="list-group-item" data-value="' + key + '">';
        if (key == NO_TEXT) {
            content += '<span class="h5">无型号</span>';
        } else {
            content += '<span class="h5">' + key + '</span>';
        }
        content += '<div class="list-group-sub-item">';

        for (let j in arr) {
            let obj = arr[j];
            let arrTagTypes = [];
            if (obj.tag_types) {
                arrTagTypes = obj.tag_types.split(',');
            }

            let classs = "";
            let complete = false;
            if (obj.fact_recept == "1") {
            	if (obj.tag_types && obj.tag_types.indexOf("2") >= 0) {
	                classs = "item-container leak";
            	} else {
	                classs = "item-container";
	                complete = true;
            	}
            } else {
                let arriveDate = new Date(obj.expect_arrive_time);
                arriveDate.setHours(0);
                arriveDate.setMinutes(0);
                arriveDate.setSeconds(0);
                arriveDate.setMilliseconds(0);

                if ((arriveDate.getTime() - now.getTime()) <= 0) { //<=今天
                    classs = "item-container less";
                } else {
                    classs = "item-container more";
                }
            }

            content += '<div class="' + classs + '" material_id="' + (obj.material_id || '') + '" fact_recept_id="' + (obj.fact_recept_id || '') + 
            	'" serial_no="' + obj.serial_no + '" model_name="' + (obj.model_name || '') + '" tag_types="' + (obj.tag_types || '') +
            	'" ocm="' + obj.ocm + 
            	'" comment="' + (obj.comment || '') + '" category_id="' + obj.category_id + '" model_id="' + (obj.model_id || '') + '"' +
            	(obj.tc_location ? (' tc_location="' + obj.tc_location + '"') : '') +
            	(complete ? ' complete=true' : '') + 
            	'>';
	            content += '<div class="item-sub-container">';
	            	content += '<div class="item">' + obj.serial_no + '</div>';
            	content += '</div>';
            	content += '<div class="item-sub-container">';
            		content += '<div class="item date">' + (obj.expect_arrive_time || '') + '</div>';
            if (arrTagTypes.includes('4')) { //消毒
                    content += '<div class="item"><div class="disinfect checked"></div></div>';
            } else if (arrTagTypes.includes('5')) { //灭菌
                    content += '<div class="item"><div class="sterilize checked"></div></div>';
            } else {
            	    content += '<div class="item"></div>';
            }

            if (arrTagTypes.includes('2')) { //要做测漏还未做
                	content += '<div class="item"><div class="leak checked"></div></div>';
            } else if (arrTagTypes.includes('3')) { //做完测漏
                	content += '<div class="item"><div class="leak done"></div></div>';
//            } else if (obj.fact_recept == "1" && obj.kind != 7) { //不用测漏
//                	content += '<div class="item"><div class="leak pass"></div></div>';
            } else {
            		content += '<div class="item"><div></div></div>';
            }

            if (f_isLightFix(obj.ocm_rank) || obj.ocm_rank == 4) {
            	let dm_tag = "D";
            	if (obj.ocm_rank == 99) {
            		dm_tag = "DW";
            	} else if (obj.ocm_rank >= 96) {
            		dm_tag = "M";
            	}
            	content += '<div class="dm_tag">' + (dm_tag) + '</div>';
            }

            if (obj.service_repair_flg) {
            	let service_tag = "";
            	if (obj.service_repair_flg == 1) {
            		service_tag = "保内";
            	} else if (obj.service_repair_flg == 2) {
            		service_tag = "QIS";
            	}
            	content += '<div class="service_tag">' + (service_tag) + '</div>';
            }

            content += '</div>';
            content += '</div>';
        }
        content += '</div>';
        content += '</li>';
    }

    $("#" + id + "> .list-group").html(content);

    //滚动容器重新渲染
    scrollRefresh();
}

/**
 * 收集字母[A,B,C...]对象型号
 * arrModelNames:型号名称数组
 * letterMap:容器
 **/
function setLetterMap(arrModelNames, letterMap) {
    if (arrModelNames.constructor != Array) return;

    letterMap.clear();

    for (let i in arrModelNames) {
        let upperLetter = "";
        //取得首字母，并转成大写
        let modelName = arrModelNames[i];

        if (modelName == NO_TEXT) {
            upperLetter = modelName;
        } else {
            upperLetter = modelName.substring(0, 1).toUpperCase();
        }

        if (letterMap.has(upperLetter)) {
            let arr = letterMap.get(upperLetter);
            if (!arr.includes(modelName)) {
                arr.push(modelName);
                letterMap.set(upperLetter, arr);
            }
        } else {
            let arr = [];
            arr.push(modelName);
            letterMap.set(upperLetter, arr);
        }
    }
}

//清除字母导航
function clearLetterActive() {
    $("#letter_nav > li").removeClass("active");
    $("#model_nav").removeClass("active").find("ul").html("");
}

//筛选机身号
function filterSerialNO(serialNo) {
    if (!serialNo) {
        $("#" + activeTab + "_tab").find(".item-container").each(function(i, item) {
            let $this = $(this);
            if ($this.is(":hidden")) {
                $this.show().removeClass("scaleOut").addClass("scaleIn");
                setTimeout(function() {
                    $this.removeClass("scaleIn");
                }, 300);
            }
        });
    } else {
		let patternSerialNo = new RegExp( "(^|\\D)"+serialNo, "");

    	$("#" + activeTab + "_tab").find(".item-container").each(function(i, item) {
            let $this = $(this);
            let value = $this.attr("serial_no");

            //不匹配
            if (!value.match(patternSerialNo)) {
                $this.removeClass("scaleOut scaleIn").addClass("scaleOut");
                setTimeout(function() {
                    $this.hide();
                }, 300);
            } else { //匹配 数字内容首部一致
                if ($this.hasClass("scaleOut")) {
                    $this.show().removeClass("scaleOut").addClass("scaleIn");
                    setTimeout(function() {
                        $this.removeClass("scaleIn");
                    }, 300);
                }
            }
        });
    }

    setTimeout(function() {
    	filterModelName();
    }, 300);

    setTimeout(function() {
        scrollRefresh();
    }, 330);

}

function filterModelName() {
	$("#" + activeTab + "_tab li.list-group-item")
		.removeClass("empty")
		.children("span").addClass("h5")
		.end()
		.each(function(idx, ele){
			var $ele = $(ele);
			if ($ele.find(".item-container:visible").length == 0) {
				$ele.addClass("empty")
					.children("span").removeClass("h5");
			}
		});

	if ($(".item-container:visible").length) {
		$("#screen").removeClass("mismatch");
	} else {
		$("#screen").addClass("mismatch");
	}
}

//容器重新渲染
function scrollRefresh() {
    if (activeTab == "undirect") {
        undirectObj.scroll.refresh();
    } else if (activeTab == "direct") {
        directObj.scroll.refresh();
    } else if (activeTab == "perl") {
        perlObj.scroll.refresh();
    } else if (activeTab == "spare") {
    	spareObj.scroll.refresh();
    }
}

//直送/非直送编辑弹窗
function showEditDialog(initData) {
    //初期值表示
    $("#edit_model_name").text(initData.model_name);
    $("#edit_serial_no").text(initData.serial_no);
    $("#edit_ocm").html(showOcm(initData.ocm));

    let arrTagTypes = initData.tag_types.split(",");

    $("#edit_leak").removeClass("checked").removeClass("done");
    if (arrTagTypes.includes('2')) {
        $("#edit_leak").addClass("checked");
    } else if (arrTagTypes.includes('3')) {
    	$("#edit_leak").addClass("done");
    }
    //消毒
    arrTagTypes.includes('4') ? $("#edit_disinfect").addClass("checked") : $("#edit_disinfect").removeClass("checked");
    //灭菌
    arrTagTypes.includes('5') ? $("#edit_sterilize").addClass("checked") : $("#edit_sterilize").removeClass("checked");
    //动物实验
    arrTagTypes.includes('1') ? $("#edit_animal").addClass("checked") : $("#edit_animal").removeClass("checked");

    //故障说明
    $("#edit_comment").html(decodeText(initData.comment));

    //周转箱库位
    $("#edit_tc_location").html(initData.tc_location || "");

    // 绑定事件
    if (!arrTagTypes.includes('3')) {
        if (arrTagTypes.includes('2')) { //要做测漏还未做
            $("#edit_leak").addClass("checked");
        }
        $("#edit_leak").hammer().off("tap").on("tap", function() {
            if ($(this).hasClass("checked")) {
                $(this).removeClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
            } else {
                $(this).addClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").show();
            }

            blinkTip($(this));
        });

    } else { //做完测漏
        $("#edit_leak").off();
    }

    $("#edit_sterilize").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#edit_disinfect").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#edit_disinfect").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#edit_sterilize").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#edit_animal").hammer().off("tap").on("tap", function() {
        $(this).hasClass("checked") ? $(this).removeClass("checked") : $(this).addClass("checked");

        blinkTip($(this));
    });

    let title = "";
    initData.direct_flg == 1 ? title = "直送" : title = "非直送";

    let buttons = {};

    //已经做完测漏
    if(initData.complete){
    	buttons["更改"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物实验
                }

                if ($("#edit_leak").hasClass("done")) {
                	index++;
    				postData["material_tag.tag_type[" + index + "]"] = "3"; //完成测漏
                }

    			if ($("#edit_disinfect").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                } else if ($("#edit_sterilize").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                }

                updateMaterial(postData);
    		}
    	}
    } else if(arrTagTypes.includes("2")){
    	buttons["更改"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物实验
                }

    			if($("#edit_leak").hasClass("checked")){
    				index++;
    				postData["material_tag.tag_type[" + index + "]"] = "2"; //要做测漏还没做
    			}

    			if ($("#edit_disinfect").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                } else if ($("#edit_sterilize").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                }

                updateMaterial(postData);
    		}
    	};
    	buttons["完成测漏"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物实验
                }

                index++;
    			postData["material_tag.tag_type[" + index + "]"] = "3"; //完成测漏

    			if ($("#edit_disinfect").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                } else if ($("#edit_sterilize").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                }

                updateMaterial(postData);
    		}
    	}
    } else {
    	buttons["完成实物受理及测漏"] = function(){
    		if (editValidate(arrTagTypes, true)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物实验
                }

                index++;
    			postData["material_tag.tag_type[" + index + "]"] = "3"; //完成测漏

    			if ($("#edit_disinfect").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                } else if ($("#edit_sterilize").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                }

                if (!initData.tc_location) {
			    	postData.tc_location = $("#edit_tc_location").html();
			    }

                updateMaterial(postData);
    		}
    	};
	   	buttons["完成实物受理"] = function(){
    		if (editValidate(arrTagTypes, true)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物实验
                }

    			if($("#edit_leak").hasClass("checked")){
    				index++;
    				postData["material_tag.tag_type[" + index + "]"] = "2"; //要做测漏还没做
    			}

    			if ($("#edit_disinfect").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                } else if ($("#edit_sterilize").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                }

                if (!initData.tc_location) {
			    	postData.tc_location = $("#edit_tc_location").html();
			    }

                updateMaterial(postData);
    		}
    	};
     }

    $("#edit_tc_location").off();
    if (!initData.tc_location) {
	    $("#edit_tc_location").hammer().on("tap", function() {
	    	chooseTcLocation(initData.category_id, $("#edit_tc_location"));
	    });
    }

    buttons["取消"] = function() {
    	$(this).dialog("close");
    }

    inputArea = "input";
    let $dialog = $("#edit_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        closeOnEscape:false,
        height: 'auto',
        close: function(){inputArea = "page";},
        resizable: false,
        modal: true,
        buttons: buttons
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");

    if(arrTagTypes.includes("3")){
    } else if(arrTagTypes.includes("2")){
    	$dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").show();
    } else {
		$dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
    }

    //必须验证
    function editValidate(arrTagTypes, atFirst) {
        var errorData = "";

        if (!$("#edit_sterilize").hasClass("checked") && !$("#edit_disinfect").hasClass("checked")) {
        	if (!$("#edit_leak").hasClass("checked")) { // 等待测漏时允许
	            errorData += "请选择【消毒】或者【灭菌】";
        	}
        }

        if (atFirst) {
	        if (!$("#edit_tc_location").html()) {
	        	errorData += "<br>请指定周转箱标签库位。";
	        }
        }

        if (errorData) {
            errorPop(errorData);
            return false;
        } else {
            return true;
        }
    }

    function updateMaterial(postData){
    	$.ajax({
            beforeSend: ajaxRequestType,
            async: true,
            url: servicePath + '?method=doUpdate',
            cache: false,
            data: postData,
            type: "post",
            dataType: "json",
            success: ajaxSuccessCheck,
            error: ajaxError,
            complete: function(xhrobj, textStatus) {
                var resInfo = null;
                try {
                    // 以Object形式读取JSON
                    eval('resInfo =' + xhrobj.responseText);
                    if (resInfo.errors.length > 0) {
                        // 共通出错信息框
                        treatBackMessages("", resInfo.errors);
                    } else {
                    	$("#screen").text("");
                        findit();
                        
                        $dialog.dialog("close");
                    }
                } catch (e) {};
            }
        });
    }
}

var chooseTcLocation = function(category_id, $target) {
	var preprintedLocations = null;
	var emptyLocations = null;

	var category_kind = null;
	switch(category_id) {
		case CATEGORY_ENDOEYE_ID : 
			category_kind = "endoeye";
			break; 
		case CATEGORY_UDI_ID : 
			category_kind = "udi";
			break;
		default : 
			category_kind = "normal";
	}

	preprintedLocations = gPreprintedLocations[category_kind]; 
	emptyLocations = gShelfLocations[category_kind];

	if ($target.closest("table.condform").find("span.animal").is(".checked")) {
		preprintedLocations = gPreprintedLocations["animal"];
		emptyLocations = [];
	}

	var $tcLocationCoverery = $("#tc_location_pop");
	$tcLocationCoverery.attr("target_id", $target.attr("id"));

	if ($tcLocationCoverery.length == 0) {
		$(document.body).append("<div id='tc_location_pop'/>")
		$tcLocationCoverery = $("#tc_location_pop");
		$tcLocationCoverery.hammer().on("touch", function(e){
			var eClass = e.target.className;
			if (eClass.indexOf("tc_location") >= 0) {
				$("#" + $tcLocationCoverery.attr("target_id")).text(e.target.innerText);
			} else if (eClass.indexOf("tc_no_location") >= 0) {
				$("#" + $tcLocationCoverery.attr("target_id")).text("不分配");
			} else if (eClass.indexOf("tc_shelf") >= 0) {
				$tcLocationCoverery.find(".location_group").hide();
				$tcLocationCoverery.find(".location_group[for='" + $(e.target).attr("for") +"']").show();
				return;
			} else if (eClass.indexOf("tc_pre_location") >= 0) {
				$tcLocationCoverery.find(".location_group").hide();
				$tcLocationCoverery.find(".location_group[for='pre']").show();
				return;
			}
    		$("body > .overlay").remove();
			$tcLocationCoverery.hide();
		})
	}

	var tcLocationHtml = "<span class='tc_no_location'>不分配库位</span>";
	var tcGroupHtml = "<div class='tc_group'>";

	var groupCount = 0;
	if (preprintedLocations.length) {
		tcGroupHtml += "<span class='tc_pre_location'>预分配</span>";

		tcLocationHtml += "<div class='location_group' for='pre'>";
		for (var i in preprintedLocations) {
			tcLocationHtml += "<span class='tc_location'>" + preprintedLocations[i] + "</span>"
		}
		groupCount = 1;
	}

	if (emptyLocations.length) {
		var shelf = "-";
		for (var i in emptyLocations) {
			var emptyloc = emptyLocations[i];
			if (emptyloc.shelf != shelf) {
				tcGroupHtml += "<span class='tc_shelf' for='" + emptyloc.shelf + "'>" + emptyloc.shelf + " 货架</span>";
				shelf = emptyloc.shelf;
				if (groupCount > 0) tcLocationHtml += "</div>";
				tcLocationHtml += "<div class='location_group' for='" + emptyloc.shelf + "'>";
			}

			tcLocationHtml += "<span class='tc_location'>" + emptyloc.location + "</span>"
		}
	}

	if (groupCount > 0) tcLocationHtml += "</div>";

	tcGroupHtml += "</div>";
	tcLocationHtml += tcGroupHtml;

	$(window).overlay();
	var zIndex = 1050;
	var popZIndex = parseInt($(".ui-dialog:visible").css("zIndex"));
	if (popZIndex >= zIndex) {
		zIndex = popZIndex + 1;
	}
	$("body > .overlay").css("zIndex", zIndex);
	$tcLocationCoverery.html(tcLocationHtml).css("zIndex", zIndex + 1).show();
	$tcLocationCoverery.find(".location_group").hide(); // .eq(0).show();

	var shelfRecord = null;
	if (!localStorage.shelfRecordDay || localStorage.shelfRecordDay != new Date().getDay()) {
		setupShelfRecords();
	}
	shelfRecord = getShelfRecord(category_kind);
	if (shelfRecord) {
		$tcLocationCoverery.find(".location_group[for='" + shelfRecord +"']").show();
	} else {
		$tcLocationCoverery.find(".location_group").eq(0).show();
	}
}

function setupShelfRecords() {
	setupShelfRecord("endoeye", gShelfLocations["endoeye"]);
	setupShelfRecord("udi", gShelfLocations["udi"]);
	setupShelfRecord("normal", gShelfLocations["normal"]);
	localStorage.shelfRecordDay = new Date().getDay();
}

function setupShelfRecord(category_kind, emptyLocations) {
	var maxShelf = null, shelf = null, maxCount = 0, count = 0;
	for (var i in emptyLocations) {
		var emptyloc = emptyLocations[i];
		if (emptyloc.shelf != shelf) {
			if (count > maxCount) {
				maxCount = count;
				maxShelf = shelf;
			}
			count = 0; shelf = emptyloc.shelf;
		}
		count++;
	}
	if (maxShelf) {
		localStorage["todayShelf_" + category_kind] = maxShelf;
	}
}

function getShelfRecord(category_kind) {
	if ("animal" == category_kind) {
		return null;
	}
	var retShelf = localStorage["todayShelf_" + category_kind];
	if ($("#tc_location_pop").find(".location_group[for='" + retShelf +"']").length == 0) {
		var hitNext = null;
		var $location_groups = $("#tc_location_pop").find(".location_group").not("[for='pre']");
		if ($location_groups.length == 0) return null;
		$location_groups.each(function(idx, ele){
			if (hitNext != null) {
				return;
			}
			var thisFor = $(ele).attr("for");
			if (thisFor > retShelf) {
				hitNext = thisFor;
			}
		});
		if (hitNext == null) {
			localStorage["todayShelf_" + category_kind] = $location_groups.eq(0).attr("for");
		} else {
			localStorage["todayShelf_" + category_kind] = hitNext;
		}
	}

	return localStorage["todayShelf_" + category_kind];
}

//直送/非直送临时编辑弹窗
function showTempEditDialog(initData) {
	//初期值表示
    $("#temp_edit_model_name").text(initData.model_name);
    $("#temp_edit_model_id").val(initData.model_id);
    $("#temp_serial_no").val(initData.serial_no).disable();

    let arrTagTypes = initData.tag_types.split(",");

    $("#edit_leak").removeClass("checked").removeClass("done");
    if (arrTagTypes.includes('2')) {
        $("#edit_leak").addClass("checked");
    } else if (arrTagTypes.includes('3')) {
    	$("#edit_leak").addClass("done");
    }

    //消毒
    arrTagTypes.includes('4') ? $("#temp_edit_disinfect").addClass("checked") : $("#temp_edit_disinfect").removeClass("checked");
    //灭菌
    arrTagTypes.includes('5') ? $("#temp_edit_sterilize").addClass("checked") : $("#temp_edit_sterilize").removeClass("checked");
    //动物实验
    arrTagTypes.includes('1') ? $("#temp_edit_animal").addClass("checked") : $("#temp_edit_animal").removeClass("checked");

    $("#temp_edit_tc_location").text(initData.tc_location || "");

    // 绑定事件
    if (!arrTagTypes.includes('3')) {
		if (arrTagTypes.includes('2')) { //要做测漏还未做
           	$("#temp_edit_leak").addClass("checked");
        }
        $("#temp_edit_leak").hammer().off("tap").on("tap", function() {
            if ($(this).hasClass("checked")) {
                $(this).removeClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
            } else {
                $(this).addClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").show();
            }

            blinkTip($(this));
        });
    } else { //做完测漏
        $("#temp_edit_leak").off();
    }

    $("#temp_edit_sterilize").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#temp_edit_disinfect").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#temp_edit_disinfect").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#temp_edit_sterilize").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#temp_edit_animal").hammer().off("tap").on("tap", function() {
        $(this).hasClass("checked") ? $(this).removeClass("checked") : $(this).addClass("checked");

        blinkTip($(this));
    });

    $("#temp_edit_model_name").off("blur").on("blur", function() {
        let model_name = $(this).text().trim();
        if (model_name) {
            checkModelName(model_name, function(modelForm) {
                if (modelForm.kind == "07") {
                    $("#temp_edit_model_id").val("").next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").disable();
                    infoPop("请到\"其他\"页面操作。");
                } else {
                    $("#temp_edit_model_id").val(modelForm.id).next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").enable();
                }
            }, function(errors) {
                $("#temp_edit_model_id").val("").next(".error").html(errors[0].errmsg);
                $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").disable();
            })
        } else {
            $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").enable();
            $("#temp_edit_model_id").val("").next(".error").html("");
        }
    });

    $("#temp_edit_model_name").off("tap")
		.hammer().on("tap", function(){
		selectModel($("#temp_edit_model_name"), "endoscope");
	});

    $("#tempRandomBtn").hammer().off().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function() {
        $(this).removeAttr("hold");
        $("#" + $(this).attr("for")).val(randomSerialNo());
    });

    $("#temp_edit_tc_location").off();
    if (!initData.tc_location) {
	    $("#temp_edit_tc_location").hammer().on("tap", function() {
	    	chooseTcLocation(initData.category_id, $("#temp_edit_tc_location"));
	    });
    }

    let title = "";
    initData.direct_flg == 1 ? title = "直送" : title = "非直送";

    let buttons = {};
    //已经做完测漏
    if(arrTagTypes.includes("3")){
    	buttons["更改"] = function(){
    		if(tempAddValidate()){
    			let postData = {
    				"fact_recept_id" : initData.fact_recept_id,
                    "model_name": $("#temp_edit_model_name").text().trim(),
                    "model_id": $("#temp_edit_model_id").val().trim(),
                    "serial_no": $("#temp_serial_no").val().trim()
                };

                let types = new Array();
                if ($("#temp_edit_animal").hasClass("checked")) {
                    types.push("1"); //动物实验
                }

                if ($("#temp_edit_leak").hasClass("done")) {
	                types.push("3"); //完成测漏
                }

                if ($("#temp_edit_disinfect").hasClass("checked")) {
                    types.push("4"); //消毒
                } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                    types.push("5"); //灭菌
                }
                postData["tag_types"] = types.join();

                updateFactReceptTemp(postData);
    		}
    	}
    } else if(arrTagTypes.includes("2")){
    	buttons["更改"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").text().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物实验
            }

            types.push("2"); //等待测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }
            postData["tag_types"] = types.join();
			postData.tc_location = $("#temp_edit_tc_location").html();

            updateFactReceptTemp(postData);
    	};
    	buttons["完成测漏"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").text().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物实验
            }

            types.push("3"); //完成测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }
            postData["tag_types"] = types.join();
			postData.tc_location = $("#temp_edit_tc_location").html();

            updateFactReceptTemp(postData);
    	}
    } else {
    	buttons["更改"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").text().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物实验
            }

            if ($("#temp_edit_leak").hasClass("checked")) {
            	types.push("2"); //等待测漏
            }

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }

            postData["tag_types"] = types.join();

			postData.tc_location = $("#temp_edit_tc_location").html();

            updateFactReceptTemp(postData);
    	};
    	buttons["完成测漏"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").text().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物实验
            }

            types.push("3"); //完成测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }

            postData["tag_types"] = types.join();

			postData.tc_location = $("#temp_edit_tc_location").html();

            updateFactReceptTemp(postData);
    	}
    }

    buttons["取消"] = function() {
    	$(this).dialog("close");
    }

    inputArea = "input";
    let $dialog = $("#temp_edit_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        closeOnEscape:false,
        height: 'auto',
        close: function(){inputArea = "page";},
        resizable: false,
        modal: true,
        buttons:buttons
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");

    if(arrTagTypes.includes("3")){
    } else if(arrTagTypes.includes("2")){
    	$dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").show();
    } else {
		$dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
    }

    $("#temp_edit_model_name").enable();
    $("#temp_serial_no").enable();

    //必须验证
    function tempAddValidate() {
        let errorData = "";

        if (!$("#temp_edit_sterilize").hasClass("checked") && !$("#temp_edit_disinfect").hasClass("checked")) {
        	if (!$("#temp_edit_leak").hasClass("checked")) { // 等待测漏时允许
	            errorData += "请选择【消毒】或者【灭菌】";
        	}
        }
        if (!$("#temp_edit_tc_location").html()) {
        	errorData += "<br>请指定周转箱库位的标签。";
        }

        if (errorData) {
            errorPop(errorData);
            return false;
        } else {
            return true;
        }
    }

    function updateFactReceptTemp(postData){
		$.ajax({
	        beforeSend: ajaxRequestType,
	        async: true,
	        url: servicePath + '?method=doTempUpdate',
	        cache: false,
	        data: postData,
	        type: "post",
	        dataType: "json",
	        success: ajaxSuccessCheck,
	        error: ajaxError,
	        complete: function(xhrobj, textStatus) {
	            var resInfo = null;
	            try {
	                // 以Object形式读取JSON
	                eval('resInfo =' + xhrobj.responseText);
	                if (resInfo.errors.length > 0) {
	                    // 共通出错信息框
	                    treatBackMessages(null, resInfo.errors);
	                } else {
	                    findit();
	                    $dialog.dialog("close");
	                }
	            } catch (e) {};
	        }
	    });
    }
}

/**
 * 直送/非直送弹窗
 * direct_flg:直送标记
 * direct_flg=0 非直送,direct_flg=1 直送
 **/
function showAddDialog(direct_flg) {
    //初始化
    $("#add_model_name").text("");
    $("#add_model_id").val("").attr("category_id", "").next(".error").html("");
    $("#add_serial_no").val("").disable();
    $("#add_dialog td.tag-type > *").removeClass("checked");

    $("#add_leak").hammer().off("tap").on("tap", function() {
        if ($(this).hasClass("checked")) {
            $(this).removeClass("checked");
            $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
        } else {
            $(this).addClass("checked");
            $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").show();
        }

        blinkTip($(this));
    });

    $("#add_sterilize").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#add_disinfect").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#add_disinfect").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#add_sterilize").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#add_animal").hammer().off("tap").on("tap", function() {
        $(this).hasClass("checked") ? $(this).removeClass("checked") : $(this).addClass("checked");

        blinkTip($(this));
    });

    $("#add_model_name").off("blur").on("blur", function() {
        let model_name = $(this).val().trim();
        if (model_name) {
            checkModelName(model_name, function(modelForm) {
                if (modelForm.kind == "07") {
                    $("#add_model_id").val("").next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").disable();
                    infoPop("请到其他页面操作。");
                } else {
                    $("#add_model_id").val(modelForm.id).next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").enable();
                }
            }, function(errors) {
                $("#add_model_id").val("").next(".error").html(errors[0].errmsg);
                $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").disable();
            })
        } else {
            $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").enable();
            $("#add_model_id").val("").next(".error").html("");
        }
    });

    $("#add_model_name").off("tap")
    	.hammer().on("tap", function(){
    		selectModel($("#add_model_name"), "endoscope");
    	});

    $("#add_tc_location").text("");

    $("#randomBtn").hammer().off().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function() {
        $(this).removeAttr("hold");
        $("#" + $(this).attr("for")).val(randomSerialNo());
    });

    let title = "";
    direct_flg == 1 ? title = "直送" : title = "非直送";

    inputArea = "input";
    let $dialog = $("#add_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        closeOnEscape:false,
        height: 'auto',
        close: function(){inputArea = "page";},
        resizable: false,
        modal: true,
        buttons: {
            "完成实物受理及测漏":function(){
            	if (addValidate()) {
            		let postData = {
                        "direct_flg": direct_flg,
                        "model_name": $("#add_model_name").text().trim(),
                        "model_id": $("#add_model_id").val().trim(),
                        "serial_no": $("#add_serial_no").val().trim(),
                        "flag": "0" //维修对象
                    };

                    let types = new Array();
                    if ($("#add_animal").hasClass("checked")) {
                        types.push("1"); //动物实验
                    }

                    types.push("3"); //完成测漏

                    if ($("#add_disinfect").hasClass("checked")) {
                        types.push("4"); //消毒
                    } else if ($("#add_sterilize").hasClass("checked")) {
                        types.push("5"); //灭菌
                    }

                    postData["tag_types"] = types.join();
                    postData.tc_location = $("#add_tc_location").html();

 					addFactReceptMaterial(postData);
            	}
            },
             "完成实物受理": function(){
            	if (addValidate()) {
            		let postData = {
                        "direct_flg": direct_flg,
                        "model_name": $("#add_model_name").text().trim(),
                        "model_id": $("#add_model_id").val().trim(),
                        "serial_no": $("#add_serial_no").val().trim(),
                        "flag": "0" //维修对象
                    };

                    let types = new Array();
                    if ($("#add_animal").hasClass("checked")) {
                        types.push("1"); //动物实验
                    }

                    if($("#add_leak").hasClass("checked")){
                    	 types.push("2"); //要做测漏还没做
                    }

                    if ($("#add_disinfect").hasClass("checked")) {
                        types.push("4"); //消毒
                    } else if ($("#add_sterilize").hasClass("checked")) {
                        types.push("5"); //灭菌
                    }

                    postData["tag_types"] = types.join();
                    postData.tc_location = $("#add_tc_location").html();

                    addFactReceptMaterial(postData);
            	}
            },
           "取消": function() {
                $(this).dialog("close");
            }
        }
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");
    $dialog.next().find(".ui-dialog-buttonset button:contains('测漏')").hide();
    $("#add_model_name").enable();
    $("#add_serial_no").enable();

    $("#add_tc_location").off();
    $("#add_tc_location").hammer().on("tap", function() {
    	var category_id = $("#add_model_id").attr("category_id");
    	if (category_id) {
	    	chooseTcLocation(category_id, $("#add_tc_location"));
    	} else {
    		errorPop("请先确定机种。");
    	}
    });

    //必须验证
    function addValidate() {
        var errorData = "";

        if (!$("#add_serial_no").val().trim()) {
            errorData += "请输入机身号的值<br>";
        }

        if (!$("#add_sterilize").hasClass("checked") && !$("#add_disinfect").hasClass("checked")) {
        	if (!$("#add_leak").hasClass("checked")) { // 等待测漏时允许
            	errorData += "请选择【消毒】或者【灭菌】";
        	}
        }

        if (!$("#add_tc_location").html()) {
        	errorData += "<br>请指定周转箱标签库位。";
        }


        if (errorData) {
            errorPop(errorData);
            return false;
        } else {
            return true;
        }
    }

    function addFactReceptMaterial(postData){
    	$.ajax({
            beforeSend: ajaxRequestType,
            async: true,
            url: servicePath + '?method=doInsert',
            cache: false,
            data: postData,
            type: "post",
            dataType: "json",
            success: ajaxSuccessCheck,
            error: ajaxError,
            complete: function(xhrobj, textStatus) {
                var resInfo = null;
                try {
                    // 以Object形式读取JSON
                    eval('resInfo =' + xhrobj.responseText);
                    if (resInfo.errors.length > 0) {
                        // 共通出错信息框
                        treatBackMessages(null, resInfo.errors);
                    } else {
                        findit();
                        $dialog.dialog("close");
                    }
                } catch (e) {};
            }
        });
    }
}

function isCameraDevice(category_id){
	return category_id == CATEGORY_CAMERA_ID
		|| category_id == CATEGORY_CAMERA_ADAPTOR_ID;
}

//其他（周边）新建弹窗
function showPerlAddDialog() {
    //初始化
    $("#perl_add_model_name").text("");
    $("#perl_add_model_id").val("").next(".error").html("");
    $("#perl_add_serial_no").val("").disable();
    $("#perl_add_dialog td.tag-type > *").removeClass("checked");

    //消毒
    $("#perl_add_disinfect").hammer().off("tab").on("tap", function() {
        if ($("#perl_add_sterilize").is(':hidden')) {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
            }
        } else {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
                $("#perl_add_sterilize").removeClass("checked");
            }
        }

        blinkTip($(this));
    });

    //灭菌
    $("#perl_add_sterilize").hammer().off("tab").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#perl_add_disinfect").removeClass("checked");
        }

        blinkTip($(this));
    });

    $("#perl_add_model_name").off("blur").on("blur", function() {
        var model_name = $(this).val().trim();
        if (model_name) {
            checkModelName(model_name, function(modelForm) {
                if (modelForm.kind != "07") {
                    $("#perl_add_model_id").val("").next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:eq(0)").disable();
                    $("#perl_add_sterilize").show();
                    infoPop(model_name + "不是周边设备型号。");
                } else {
                    $("#perl_add_model_id").val(modelForm.id).next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:eq(0)").enable();

                    if (isCameraDevice(modelForm.category_id)) { //摄像头
                        $("#perl_add_sterilize").show(); //可以选择灭菌
                    } else { //摄像头以外
                        $("#perl_add_sterilize").hide().removeClass("checked"); //不能选择灭菌
                    }
                }
            }, function(errors) {
                $("#perl_add_model_id").val("").next(".error").html(errors[0].errmsg);
                $dialog.next().find(".ui-dialog-buttonset button:eq(0)").disable();
                $("#perl_add_sterilize").show();
            })
        } else {
            $dialog.next().find(".ui-dialog-buttonset button:eq(0)").enable();
            $("#perl_add_model_id").val("").next(".error").html("");
            $("#perl_add_sterilize").show();
        }
    });

    $("#perl_add_model_name").off("tap")
		.hammer().on("tap", function(){
		selectModel($("#perl_add_model_name"), "peripheral");
	});
	$("#perl_add_model_id").off("change")
		.on("change", function(){
			var category_id = this.getAttribute("category_id");
			if (isCameraDevice(category_id)) { //摄像头
                $("#perl_add_sterilize").show(); //可以选择灭菌
            } else { //摄像头以外
                $("#perl_add_sterilize").hide().removeClass("checked"); //不能选择灭菌
            }
    })

    inputArea = "input";
    let $dialog = $("#perl_add_dialog").dialog({
        position: ['center', 50],
        title: "其他",
        width: 550,
        closeOnEscape:false,
        height: 'auto',
        resizable: false,
        close: function(){inputArea = "page";},
        modal: true,
        buttons: {
            "确定": function() {
                if (perlAddValidate()) {
                    let postData = {
                        "model_name": $("#perl_add_model_name").text().trim(),
                        "model_id": $("#perl_add_model_id").val().trim(),
                        "serial_no": $("#perl_add_serial_no").val().trim(),
                        "flag": "1" //周边
                    };

                    let types = new Array();
                    if ($("#perl_add_disinfect").hasClass("checked")) {
                        types.push("4"); //消毒
                    } else if ($("#perl_add_sterilize").hasClass("checked")) {
                        types.push("5"); //灭菌
                    }

                    postData["tag_types"] = types.join();

                    $.ajax({
                        beforeSend: ajaxRequestType,
                        async: true,
                        url: servicePath + '?method=doInsert',
                        cache: false,
                        data: postData,
                        type: "post",
                        dataType: "json",
                        success: ajaxSuccessCheck,
                        error: ajaxError,
                        complete: function(xhrobj, textStatus) {
                            var resInfo = null;
                            try {
                                // 以Object形式读取JSON
                                eval('resInfo =' + xhrobj.responseText);
                                if (resInfo.errors.length > 0) {
                                    // 共通出错信息框
                                    treatBackMessages(null, resInfo.errors);
                                } else {
                                    findit();
                                    $dialog.dialog("close");
                                }
                            } catch (e) {};
                        }
                    });
                }
            },
            "取消": function() {
                $(this).dialog("close");
            }
        }
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");
    $("#perl_add_model_name").enable();
    $("#perl_add_serial_no").enable();

    function perlAddValidate() {
        var errorData = "";

        if (!$("#perl_add_model_name").text().trim()) {
            errorData += "请输入型号的值<br>";
        }
        if (!$("#perl_add_serial_no").val().trim()) {
            errorData += "请输入机身号的值<br>";
        }

        //摄像头以外
        if ($("#perl_add_sterilize").is(':hidden')) {
            if (!$("#perl_add_disinfect").hasClass("checked")) {
                errorData += "请选择【消毒】<br>";
            }
        } else { //摄像头
            if (!$("#perl_add_disinfect").hasClass("checked") && !$("#perl_add_sterilize").hasClass("checked")) {
                errorData += "请选择【消毒】或者【灭菌】<br>";
            }
        }

        if (errorData) {
            errorPop(errorData);
            return false;
        } else {
            return true;
        }
    }
}

//其他（周边）编辑弹窗
function showPerlEditDialog(initData) {
    //初期值表示
    $("#perl_edit_model_name").text(initData.model_name);
    $("#perl_edit_serial_no").text(initData.serial_no);
    //属性标签
    var arrTagTypes = initData.tag_types.split(",");

    //消毒
    if (arrTagTypes.includes("4")) {
        $("#perl_edit_disinfect").addClass("checked");
        $("#perl_edit_sterilize").removeClass("checked");
    } else if (arrTagTypes.includes("5")) { //灭菌
        $("#perl_edit_disinfect").removeClass("checked");
        $("#perl_edit_sterilize").addClass("checked");
    } else { //没有选择 消毒或者灭菌
        $("#perl_edit_disinfect,#perl_edit_sterilize").removeClass("checked");
    }

    if (isCameraDevice(initData.category_id)) { //摄像头
        $("#perl_edit_sterilize").show(); //灭菌可以选择
    } else {
        $("#perl_edit_sterilize").hide(); //灭菌隐藏，不可以选择
    }

    //事件绑定
    //消毒
    $("#perl_edit_disinfect").hammer().off("tap").on("tap", function() {
        if (isCameraDevice(initData.category_id)) {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
                $("#perl_edit_sterilize").removeClass("checked");
            }
        } else {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
            }
        }

        blinkTip($(this));
    });

    //灭菌
    $("#perl_edit_sterilize").hammer().off("tap").on("tap", function() {
        if (!$(this).hasClass("checked")) {
            $(this).addClass("checked");
            $("#perl_edit_disinfect").removeClass("checked");
        }

        blinkTip($(this));
    });

    inputArea = "input";
    let $dialog = $("#perl_edit_dialog").dialog({
        position: ['center', 50],
        title: "其他",
        width: 550,
        closeOnEscape:false,
        height: 'auto',
        resizable: false,
        modal: true,
        close: function(){inputArea = "page";},
        buttons: {
            "确定": function() {
                if (perlEditValidate(initData)) {
                    var postData = {
                        "material_id": initData.material_id,
                        "flag": initData.flag
                    };

                    var index = -1;
                    if ($("#perl_edit_disinfect").hasClass("checked")) {
                        index++;
                        postData["material_tag.tag_type[" + index + "]"] = "4"; //消毒
                    } else if ($("#perl_edit_sterilize").hasClass("checked")) {
                        index++;
                        postData["material_tag.tag_type[" + index + "]"] = "5"; //灭菌
                    }

                    $.ajax({
                        beforeSend: ajaxRequestType,
                        async: true,
                        url: servicePath + '?method=doUpdate',
                        cache: false,
                        data: postData,
                        type: "post",
                        dataType: "json",
                        success: ajaxSuccessCheck,
                        error: ajaxError,
                        complete: function(xhrobj, textStatus) {
                            var resInfo = null;
                            try {
                                // 以Object形式读取JSON
                                eval('resInfo =' + xhrobj.responseText);
                                if (resInfo.errors.length > 0) {
                                    // 共通出错信息框
                                    treatBackMessages("", resInfo.errors);
                                } else {
                                	$("#screen").text("");
                                    findit();

                                    $dialog.dialog("close");
                                }
                            } catch (e) {};
                        }
                    });
                }
            },
            "取消": function() {
                $(this).dialog("close");
            }
        }
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");

    //验证
    function perlEditValidate(initData) {
        //错误信息
        let errorData = "";
        //机种为摄像头
        if (isCameraDevice(initData.category_id)) {
            if (!$("#perl_edit_disinfect").hasClass("checked") && !$("#perl_edit_sterilize").hasClass("checked")) {
                errorData += "请选择【消毒】或者【灭菌】";
            }
        } else {
            if (!$("#perl_edit_disinfect").hasClass("checked")) {
                errorData += "请选择【消毒】";
            }
        }

        if (errorData) {
            errorPop(errorData);
            return false;
        } else {
            return true;
        }
    }
}

/**
 * 检查型号
 * modelName:型号名称
 **/
function checkModelName(modelName, successFunction, failFunction) {
    let postData = {
        "model_name": modelName
    };

    $.ajax({
        beforeSend: ajaxRequestType,
        async: true,
        url: servicePath + '?method=checkModelName',
        cache: false,
        data: postData,
        type: "post",
        dataType: "json",
        success: ajaxSuccessCheck,
        error: ajaxError,
        complete: function(xhrobj, textStatus) {
            var resInfo = null;
            try {
                // 以Object形式读取JSON
                eval('resInfo =' + xhrobj.responseText);
                if (resInfo.errors.length > 0) {
                    // 共通出错信息框
                    failFunction(resInfo.errors);
                } else {
                    successFunction(resInfo.modelForm);
                }
            } catch (e) {};
        }
    });
}

//生成临时机身号
function randomSerialNo() {
    let d = new Date(),
        month = (d.getMonth() + 1),
        date = "" + d.getDate(),
        hour = "" + d.getHours(),
        minute = "" + d.getMinutes(),
        seconds = d.getSeconds(),
        milliseconds = d.getMilliseconds();
    switch (month) {
    	case 10 : month = "O"; break;
    	case 11 : month = "N"; break;
    	case 12 : month = "D"; break;
    }
    let serialNo = month + fillZero(date) + fillZero(hour) + fillZero(minute) + fillZero((seconds * 1000 + milliseconds).toString(28), 4);
    serialNo = "临" + serialNo;

    return serialNo
}

function showOcm(ocm) {
	switch(parseInt(ocm)) {
		case 1: return "上海";
		case 2: return "<span style='background-color:yellow;'>北京</span>";
		case 3: return "<span style='background-color:yellow;'>广州</span>";
		case 4: return "<span style='background-color:darkviolet;color:white;'>沈阳</span>";
	}
	return "";
}

function blinkTip($target){
	$target.find("span").remove();
	let $span = $("<span class='tip'>" + $target.attr("alt") + "</span>");
	$target.append($span);
	setTimeout(function(){
		$span.css({
    		"top" : "-80px",
    		"transform" : "scale(2)"
    	});

		$span[0].addEventListener("transitionend",function(){
			$span.remove();
			// $target.html("");
		});
	},10);
}

//非直送
let undirectObj = {
    "scroll": "",
    "init": function() {
        //定义滚动容器
        undirectObj.scroll = new IScroll('#undirect_scroll', {
            bounceEasing: "quadratic",
            bounceTime: 300
        });

        //容器滚动开始
        undirectObj.scroll.on("scrollStart", () => {
            clearLetterActive();
            isScrolling = true;
        });

        //容器滚动结束
        undirectObj.scroll.on("scrollEnd", () => isScrolling = false)
        
        //解决事件捕获绑定的Hammer.js 事件不能被PC端所识别
        if(browser.versions.mobile || browser.versions.ios || browser.versions.android || 
            browser.versions.iPhone || browser.versions.iPad){
            $(document).hammer().on("touch", "#undirect_scroll .item-container", function() {
                $(this).attr("hold", true);
            }).on("release", "#undirect_scroll .item-container", function() {
                let $this = $(this);
                $this.removeAttr("hold");

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "direct_flg" : "0",
                    "flag" : "0"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }

                if (initData.material_id) {
                    showEditDialog(initData);
                } else {
                    //临时表编辑
                    showTempEditDialog(initData);
                }
            });
        } else {
            $(document).on("click", "#undirect_scroll .item-container", function(e) {
                $(this).attr("hold", true);
                let $this = $(this);
                setTimeout(function(){
                    $this.removeAttr("hold");
                },80)

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "direct_flg" : "0",
                    "flag" : "0"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }

                if (initData.material_id) {
                    showEditDialog(initData);
                } else {
                    //临时表编辑
                    showTempEditDialog(initData);
                }
            });
        }

        $("#undirectAddBtn").hide();
        $("#undirectAddBtn").hammer().on("touch", function() {
            $(this).attr("hold", true);
        }).on("release", function() {
            $(this).removeAttr("hold");
            showAddDialog(0);
        });
    }
}

//直送
let directObj = {
    "scroll": "",
    "init": function() {
        //定义滚动容器
        directObj.scroll = new IScroll('#direct_scroll', {
            bounceEasing: "quadratic",
            bounceTime: 300
        });

        //容器滚动开始
        directObj.scroll.on("scrollStart", () => {
            clearLetterActive();
            isScrolling = true;
        });

        //容器滚动结束
        directObj.scroll.on("scrollEnd", () => isScrolling = false);

        //解决事件捕获绑定的Hammer.js 事件不能被PC端所识别
        if(browser.versions.mobile || browser.versions.ios || browser.versions.android || 
            browser.versions.iPhone || browser.versions.iPad){
            $(document).hammer().on("touch", "#direct_scroll .item-container", function() {
                $(this).attr("hold", true);
            }).on("release", "#direct_scroll .item-container", function() {
                let $this = $(this);
                $this.removeAttr("hold");

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "direct_flg" : "1",
                    "flag" : "1"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }

                if (initData.material_id) {
                    showEditDialog(initData);
                } else {
                    //临时表编辑
                    showTempEditDialog(initData);
                }
            });
        } else {
            $(document).on("click", "#direct_scroll .item-container", function() {
                $(this).attr("hold", true);

                let $this = $(this);
                setTimeout(function(){
                    $this.removeAttr("hold");
                },80);

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "direct_flg" : "1",
                    "flag" : "1"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }

                if (initData.material_id) {
                    showEditDialog(initData);
                } else {
                    //临时表编辑
                    showTempEditDialog(initData);
                }
            });
        }

        $("#directAddBtn").hammer().on("touch", function() {
            $(this).attr("hold", true);
        }).on("release", function() {
            $(this).removeAttr("hold");
            showAddDialog(1);
        });
    }
}

//其他(周边)
let perlObj = {
    "scroll": "",
    "init": function() {
        //定义滚动容器
        perlObj.scroll = new IScroll('#perl_scroll', {
            bounceEasing: "quadratic",
            bounceTime: 300
        });

        //容器滚动开始
        perlObj.scroll.on("scrollStart", () => {
            clearLetterActive();
            isScrolling = true;
        });

        //容器滚动结束
        perlObj.scroll.on("scrollEnd", () => isScrolling = false);

         //解决事件捕获绑定的Hammer.js 事件不能被PC端所识别
        if(browser.versions.mobile || browser.versions.ios || browser.versions.android || 
            browser.versions.iPhone || browser.versions.iPad){
            $(document).hammer().on("touch", "#perl_scroll .item-container", function() {
                $(this).attr("hold", true);
            }).on("release", "#perl_scroll .item-container", function() {
                let $this = $(this);
                $this.removeAttr("hold");

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "flag" : "3"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }
                showPerlEditDialog(initData);
            });
        } else {
            $(document).on("click", "#perl_scroll .item-container", function() {
                $(this).attr("hold", true);

                let $this = $(this);
                setTimeout(function(){
                    $this.removeAttr("hold");
                },80);

                //容器滚动中，不要触发弹窗
                if (isScrolling) return;

                let initData = {
                    "flag" : "3"
                };
                let attributes = this.attributes;
                for (let i in attributes) {
                    if (attributes.item(i).name != "class") initData[attributes.item(i).name] = attributes.item(i).value;
                }
                showPerlEditDialog(initData);
            });
        }

        $("#perlAddBtn").hammer().on("touch", function() {
            $(this).attr("hold", true);
        }).on("release", function() {
            $(this).removeAttr("hold");
            showPerlAddDialog();
        });
    }
}

let spareObj = {
	"scroll": "",
	"init": function() {
		spareObj.scroll = new IScroll('#spare_scroll', {	
            bounceEasing: "quadratic",	
            bounceTime: 300	
        });

        //容器滚动开始	
        spareObj.scroll.on("scrollStart", () => {	
            clearLetterActive();	
            isScrolling = true;	
        });	
        //容器滚动结束	
        spareObj.scroll.on("scrollEnd", () => isScrolling = false);	
	}
}