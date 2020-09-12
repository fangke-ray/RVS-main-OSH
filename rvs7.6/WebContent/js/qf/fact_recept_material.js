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

// 用来判断滚动容器是否正在滚动中
let isScrolling = false;

let undirectModelMap = new Map();
let directModelMap = new Map();
let perlModelMap = new Map();

/** 机种摄像头ID **/
const CATEGORY_CAMERA_ID = "00000000024";
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

    undirectObj.init();
    directObj.init();
    perlObj.init();

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

    findit();

    //switch开关控制
    $(".switch-container > span").hammer().on("tap", function() {
        let $outerContainer = $(this).closest(".switch");
        let $innerContainer = $(this).closest(".switch-container");
        if ($("#toggleBtn").prop("checked")) {
            $innerContainer.css("margin-left", "-" + $(this).outerWidth() + "px");
            $outerContainer.removeClass("switch-on").addClass("switch-off");
            $("#toggleBtn").prop("checked", false);
        } else {
            $innerContainer.css("margin-left", "0px");
            $outerContainer.removeClass("switch-off").addClass("switch-on");
            $("#toggleBtn").prop("checked", true);
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

    //字母导航
    $("#letter_nav > li").hammer().on("tap", function() {
        clearLetterActive();

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
        }

        if (letter == NO_TEXT) {
            let el = $("#" + activeTab + "_tab").find("li[data-value='" + letter + "']").get(0);

            let currentScroll;
            if (activeTab == "undirect") {
                currentScroll = undirectObj.scroll;
            } else if (activeTab == "direct") {
                currentScroll = directObj.scroll;
            } else if (activeTab == "perl") {
                currentScroll = perlObj.scroll;
            }

            currentScroll.scrollToElement(el, 400, true, true, IScroll.utils.ease.quadratic);
        } else {
            if (arrModels.length > 0) {
                let content = "";
                for (let modelName of arrModels) {
                    if (modelName != NO_TEXT) {
                        content += '<li data-value="' + modelName + '">' + modelName + '</li>';
                    }
                }
                $("#model_nav").addClass("active").find("ul").html(content);
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
        let el = $("#" + activeTab + "_tab").find("li[data-value='" + modelName + "']").get(0);

        let currentScroll;
        if (activeTab == "undirect") {
            currentScroll = undirectObj.scroll;
        } else if (activeTab == "direct") {
            currentScroll = directObj.scroll;
        } else if (activeTab == "perl") {
            currentScroll = perlObj.scroll;
        }

        currentScroll.scrollToElement(el, 400, true, true, IScroll.utils.ease.quadratic);
        $("#model_nav").removeClass("active");
    });

    $(".btn.reset").each(function(index,ele){
    	$(ele).hammer().on("touch",function(){
	    	$(this).attr("hold",true);
	    }).on("release",function(){
	    	$(this).removeAttr("hold");
	    	$("#" + $(this).attr("for")).val("");
	    });
    });
});

function findit() {
    $.ajax({
        beforeSend: ajaxRequestType,
        async: true,
        url: servicePath + '?method=search',
        cache: false,
        data: null,
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

                    setList("undirect_scroll", arrUnDirectList, tempUnDirectList);
                    setList("direct_scroll", arrDirectList, tempDirectList);
                    setList("perl_scroll", arrPerlList, []);
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
                }
            } catch (e) {};
        }
    });
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
            if (obj.fact_recept == "1") {
                classs = "item-container";
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

            content += '<div class="' + classs + '" material_id="' + (obj.material_id || '') + '" fact_recept_id="' + (obj.fact_recept_id || '') + '" serial_no="' + obj.serial_no + '" model_name="' + (obj.model_name || '') + '" tag_types="' +
                (obj.tag_types || '') + '" comment="' + (obj.comment || '') + '" category_id="' + obj.category_id + '" model_id="' + (obj.model_id || '') + '">';
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
                	content += '<div class="item"><div class="leak waitting"></div></div>';
            } else if (arrTagTypes.includes('3')) { //做完测漏
                	content += '<div class="item"><div class="leak checked"></div></div>';
            } else {
            		content += '<div class="item"><div class="leak"></div></div>';
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
            if ($this.hasClass("scaleOut")) {
                $this.show().removeClass("scaleOut").addClass("scaleIn");
                setTimeout(function() {
                    $this.removeClass("scaleIn");
                }, 300);
            }
        });
    } else {
        $("#" + activeTab + "_tab").find(".item-container").each(function(i, item) {
            let $this = $(this);
            let value = $this.attr("serial_no");

            //不匹配
            if (!value.includes(serialNo)) {
                $this.removeClass("scaleOut scaleIn").addClass("scaleOut");
                setTimeout(function() {
                    $this.hide();
                }, 300);
            } else { //匹配
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
        scrollRefresh();
    }, 300);
}

//容器重新渲染
function scrollRefresh() {
    if (activeTab == "undirect") {
        undirectObj.scroll.refresh();
    } else if (activeTab == "direct") {
        directObj.scroll.refresh();
    } else if (activeTab == "perl") {
        perlObj.scroll.refresh();
    }
}

//直送/非直送编辑弹窗
function showEditDialog(initData) {
    //初期值表示
    $("#edit_model_name").text(initData.model_name);
    $("#edit_serial_no").text(initData.serial_no);

    let arrTagTypes = initData.tag_types.split(",");

    if (arrTagTypes.includes('2') || arrTagTypes.includes('3')) {
        $("#edit_leak").addClass("checked");
    } else {
        $("#edit_leak").removeClass("checked");
    }
    //消毒
    arrTagTypes.includes('4') ? $("#edit_disinfect").addClass("checked") : $("#edit_disinfect").removeClass("checked");
    //灭菌
    arrTagTypes.includes('5') ? $("#edit_sterilize").addClass("checked") : $("#edit_sterilize").removeClass("checked");
    //动物试验
    arrTagTypes.includes('1') ? $("#edit_animal").addClass("checked") : $("#edit_animal").removeClass("checked");

    //故障说明
    $("#edit_comment").html(decodeText(initData.comment));

    // 绑定事件
    if (!arrTagTypes.includes('3')) {
        $("#edit_leak").hammer().off("tap").on("tap", function() {
            if ($(this).hasClass("checked")) {
                $(this).removeClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
            } else {
                $(this).addClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:eq(1)").show();
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
    if(arrTagTypes.includes("3")){
    	buttons["更改"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物试验
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
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物试验
                }

                index++;
    			postData["material_tag.tag_type[" + index + "]"] = "2"; //要做测漏还没做

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
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物试验
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
    	buttons["完成实物受理"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物试验
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
    	buttons["完成实物受理及测漏"] = function(){
    		if (editValidate(arrTagTypes)) {
    			let postData = {
    				"material_id": initData.material_id,
    				"flag": initData.flag
    			};

    			let index = -1;
    			if ($("#edit_animal").hasClass("checked")) {
                    index++;
                    postData["material_tag.tag_type[" + index + "]"] = "1"; //动物试验
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
    }

    buttons["取消"] = function() {
    	$(this).dialog("close");
    }

    let $dialog = $("#edit_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        height: 'auto',
        resizable: false,
        modal: true,
        buttons: buttons
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");

    if(arrTagTypes.includes("3")){
    } else if(arrTagTypes.includes("2")){
    	$dialog.next().find(".ui-dialog-buttonset button:eq(1)").show();
    } else {
		$dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
    }

    //必须验证
    function editValidate(arrTagTypes) {
        var errorData = "";

        if (!$("#edit_sterilize").hasClass("checked") && !$("#edit_disinfect").hasClass("checked")) {
            errorData += "请选择【消毒】或者【灭菌】";
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
                        findit();
                        $dialog.dialog("close");
                    }
                } catch (e) {};
            }
        });
    }
}

//直送/非直送临时编辑弹窗
function showTempEditDialog(initData) {
	//初期值表示
    $("#temp_edit_model_name").val(initData.model_name).disable();
    $("#temp_edit_model_id").val(initData.model_id).next(".error").html("");
    $("#temp_serial_no").val(initData.serial_no).disable();

    let arrTagTypes = initData.tag_types.split(",");

    if (arrTagTypes.includes('2') || arrTagTypes.includes('3')) {
        $("#temp_edit_leak").addClass("checked");
    } else {
        $("#temp_edit_leak").removeClass("checked");
    }

    //消毒
    arrTagTypes.includes('4') ? $("#temp_edit_disinfect").addClass("checked") : $("#temp_edit_disinfect").removeClass("checked");
    //灭菌
    arrTagTypes.includes('5') ? $("#temp_edit_sterilize").addClass("checked") : $("#temp_edit_sterilize").removeClass("checked");
    //动物试验
    arrTagTypes.includes('1') ? $("#temp_edit_animal").addClass("checked") : $("#temp_edit_animal").removeClass("checked");

    // 绑定事件
    if (!arrTagTypes.includes('3')) {
        $("#temp_edit_leak").hammer().off("tap").on("tap", function() {
            if ($(this).hasClass("checked")) {
                $(this).removeClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
            } else {
                $(this).addClass("checked");
                $dialog.next().find(".ui-dialog-buttonset button:eq(1)").show();
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
        let model_name = $(this).val().trim();
        if (model_name) {
            checkModelName(model_name, function(modelForm) {
                if (modelForm.kind == "07") {
                    $("#temp_edit_model_id").val("").next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:nth-last-child(n+2)").disable();
                    infoPop("请到其他页面操作。");
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

    $("#tempRandomBtn").hammer().off().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function() {
        $(this).removeAttr("hold");
        $("#" + $(this).attr("for")).val(randomSerialNo());
    });

    let title = "";
    initData.direct_flg == 1 ? title = "直送" : title = "非直送";

    let buttons = {};
    //已经做完测漏
    if(arrTagTypes.includes("3")){
    	buttons["更改"] = function(){
    		if(tempAddValidate()){
    			let postData = {
    				"fact_recept_id" : initData.fact_recept_id,
                    "model_name": $("#temp_edit_model_name").val().trim(),
                    "model_id": $("#temp_edit_model_id").val().trim(),
                    "serial_no": $("#temp_serial_no").val().trim()
                };

                let types = new Array();
                if ($("#temp_edit_animal").hasClass("checked")) {
                    types.push("1"); //动物试验
                }

                types.push("3"); //完成测漏

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
                "model_name": $("#temp_edit_model_name").val().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物试验
            }

            types.push("2"); //等待测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }
            postData["tag_types"] = types.join();

            updateFactReceptTemp(postData);
    	};
    	buttons["完成测漏"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").val().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物试验
            }

            types.push("3"); //完成测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }
            postData["tag_types"] = types.join();

            updateFactReceptTemp(postData);
    	}
    } else {
    	buttons["完成实物受理"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").val().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物试验
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

            updateFactReceptTemp(postData);
    	};
    	buttons["完成实物受理及测漏"] = function(){
    		let postData = {
				"fact_recept_id" : initData.fact_recept_id,
                "model_name": $("#temp_edit_model_name").val().trim(),
                "model_id": $("#temp_edit_model_id").val().trim(),
                "serial_no": $("#temp_serial_no").val().trim()
            };

            let types = new Array();
            if ($("#temp_edit_animal").hasClass("checked")) {
                types.push("1"); //动物试验
            }

            types.push("3"); //完成测漏

            if ($("#temp_edit_disinfect").hasClass("checked")) {
                types.push("4"); //消毒
            } else if ($("#temp_edit_sterilize").hasClass("checked")) {
                types.push("5"); //灭菌
            }

            postData["tag_types"] = types.join();

            updateFactReceptTemp(postData);
    	}
    }

    buttons["取消"] = function() {
    	$(this).dialog("close");
    }

    let $dialog = $("#temp_edit_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        height: 'auto',
        resizable: false,
        modal: true,
        buttons:buttons
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");

    if(arrTagTypes.includes("3")){
    } else if(arrTagTypes.includes("2")){
    	$dialog.next().find(".ui-dialog-buttonset button:eq(1)").show();
    } else {
		$dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
    }

    $("#temp_edit_model_name").enable();
    $("#temp_serial_no").enable();

    //必须验证
    function tempAddValidate() {
        let errorData = "";

        if (!$("#temp_edit_sterilize").hasClass("checked") && !$("#temp_edit_disinfect").hasClass("checked")) {
            errorData += "请选择【消毒】或者【灭菌】";
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
    $("#add_model_name").val("").disable();
    $("#add_model_id").val("").next(".error").html("");
    $("#add_serial_no").val("").disable();
    $("#add_dialog td.tag-type > *").removeClass("checked");

    $("#add_leak").hammer().off("tap").on("tap", function() {
        if ($(this).hasClass("checked")) {
            $(this).removeClass("checked");
            $dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
        } else {
            $(this).addClass("checked");
            $dialog.next().find(".ui-dialog-buttonset button:eq(1)").show();
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

    $("#randomBtn").hammer().off().on("touch", function() {
        $(this).attr("hold", true);
    }).on("release", function() {
        $(this).removeAttr("hold");
        $("#" + $(this).attr("for")).val(randomSerialNo());
    });

    let title = "";
    direct_flg == 1 ? title = "直送" : title = "非直送";

    let $dialog = $("#add_dialog").dialog({
        position: ['center', 50],
        title: title,
        width: 550,
        height: 'auto',
        resizable: false,
        modal: true,
        buttons: {
            "完成实物受理": function(){
            	if (addValidate()) {
            		let postData = {
                        "direct_flg": direct_flg,
                        "model_name": $("#add_model_name").val().trim(),
                        "model_id": $("#add_model_id").val().trim(),
                        "serial_no": $("#add_serial_no").val().trim(),
                        "flag": "0" //维修对象
                    };

                    let types = new Array();
                    if ($("#add_animal").hasClass("checked")) {
                        types.push("1"); //动物试验
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

                    addFactReceptMaterial(postData);
            	}
            },
            "完成实物受理及测漏":function(){
            	if (addValidate()) {
            		let postData = {
                        "direct_flg": direct_flg,
                        "model_name": $("#add_model_name").val().trim(),
                        "model_id": $("#add_model_id").val().trim(),
                        "serial_no": $("#add_serial_no").val().trim(),
                        "flag": "0" //维修对象
                    };

                    let types = new Array();
                    if ($("#add_animal").hasClass("checked")) {
                        types.push("1"); //动物试验
                    }

                    types.push("3"); //完成测漏

                    if ($("#add_disinfect").hasClass("checked")) {
                        types.push("4"); //消毒
                    } else if ($("#add_sterilize").hasClass("checked")) {
                        types.push("5"); //灭菌
                    }

                    postData["tag_types"] = types.join();

 					addFactReceptMaterial(postData);
            	}
            },
            "取消": function() {
                $(this).dialog("close");
            }
        }
    });
    $dialog.next().find(".ui-dialog-buttonset button").removeClass("ui-state-focus");
    $dialog.next().find(".ui-dialog-buttonset button:eq(1)").hide();
    $("#add_model_name").enable();
    $("#add_serial_no").enable();

    //必须验证
    function addValidate() {
        var errorData = "";

        if (!$("#add_serial_no").val().trim()) {
            errorData += "请输入机身号的值<br>";
        }

        if (!$("#add_sterilize").hasClass("checked") && !$("#add_disinfect").hasClass("checked")) {
            errorData += "请选择【消毒】或者【灭菌】";
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

//其他（周边）新建弹窗
function showPerlAddDialog() {
    //初始化
    $("#perl_add_model_name").val("").disable();
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
                    infoPop(model_name + "不是周边型号。");
                } else {
                    $("#perl_add_model_id").val(modelForm.id).next(".error").html("");
                    $dialog.next().find(".ui-dialog-buttonset button:eq(0)").enable();

                    if (modelForm.category_id == CATEGORY_CAMERA_ID) { //摄像头
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

    let $dialog = $("#perl_add_dialog").dialog({
        position: ['center', 50],
        title: "其他",
        width: 550,
        height: 'auto',
        resizable: false,
        modal: true,
        buttons: {
            "确定": function() {
                if (perlAddValidate()) {
                    let postData = {
                        "model_name": $("#perl_add_model_name").val().trim(),
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

        if (!$("#perl_add_model_name").val().trim()) {
            errorData += "请输入型号的值<br>";
        }
        if (!$("#perl_add_serial_no").val().trim()) {
            errorData += "请输入机身号的值<br>";
        }

        //摄像头以外
        if ($("#perl_add_sterilize").is(':hidden')) {
            if (!$("#perl_add_disinfect").hasClass("checked")) {
                errorData += "请选择消毒<br>";
            }
        } else { //摄像头
            if (!$("#perl_add_disinfect").hasClass("checked") && !$("#perl_add_sterilize").hasClass("checked")) {
                errorData += "请选择消毒或者灭菌<br>";
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

    if (initData.category_id == CATEGORY_CAMERA_ID) { //摄像头
        $("#perl_edit_sterilize").show(); //灭菌可以选择
    } else {
        $("#perl_edit_sterilize").hide(); //灭菌隐藏，不可以选择
    }

    //事件绑定
    //消毒
    $("#perl_edit_disinfect").hammer().off("tap").on("tap", function() {
        if (initData.category_id == CATEGORY_CAMERA_ID) {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
                $("#perl_edit_sterilize").removeClass("checked");
            }
        } else {
            if (!$(this).hasClass("checked")) {
                $(this).addClass("checked");
            } else {
                $(this).removeClass("checked");
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

    let $dialog = $("#perl_edit_dialog").dialog({
        position: ['center', 50],
        title: "其他",
        width: 550,
        height: 'auto',
        resizable: false,
        modal: true,
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
        if (initData.category_id == CATEGORY_CAMERA_ID) {
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
        month = "" + (d.getMonth() + 1),
        date = "" + d.getDate(),
        hour = "" + d.getHours(),
        minute = "" + d.getMinutes(),
        seconds = "" + d.getSeconds(),
        milliseconds = "" + d.getMilliseconds();
    let serialNo = fillZero(month) + fillZero(date) + fillZero(hour) + fillZero(minute) + fillZero(seconds) + fillZero(milliseconds, 4);
    serialNo = "临" + serialNo;

    return serialNo
}

function blinkTip($target){
	$target.html("");
	let $span = $("<span class='tip'>" + $target.attr("alt") + "</span>");
	$target.append($span);
	setTimeout(function(){
		$span.css({
    		"top" : "-80px",
    		"transform" : "scale(2)"
    	});

		$span[0].addEventListener("transitionend",function(){
			$span.remove();
			$target.html("");
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