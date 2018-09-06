var servicePath = "bad_loss_summary.do";
$(function() {
        $("#monthlist .ui-button,#confirm_modify_button,#back_button,#confirm").button();
        $("#summary").hide();

        $("#back_button").click(function() {
                    $("#summary").hide();
                    $("#monthlist").show();
        });
        
        //页面初始化
        findit();
        
        // 财年月份备注修改和更新
        $("#confirm").click(function(){confirm_comment();});
        
        
        $(".otherPeriods").click(function(){
    		var strActionPath = servicePath;
    		document.forms["badLossSummaryForm"].action = strActionPath;
    		document.forms["badLossSummaryForm"].method = "post";
    		document.forms["badLossSummaryForm"].period.value = $(this).text().replace("<<", "").trim();
    		document.forms["badLossSummaryForm"].submit();
    	});
        
});

// 修改备注
var confirm_comment = function() {
    $("#confirmmessage").text("确认要修改备注吗？");
    $("#confirmmessage").dialog({
        resizable : false,
        modal : true,
        title : "修改确认",
        close : function() {$("#editbutton").enable();},
        buttons : {
            "确认" : function() {
                var reqData = {
                    "work_period" : $("#comment_td_title").text().substring(0,4),
                    "month" : $("#comment_td_title").text().substring(5, 6),
                    "comment" : $("#textarea_comment").val()
                };
                $(this).dialog("close");
                $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath + '?method=doupdateofcomment',
                    cache : false,
                    data : reqData,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete : function(xhrobj) {
                        var resInfo;
                        try {
                            // 以Object形式读取JSON
                            eval('resInfo =' + xhrobj.responseText);
                            if (resInfo.errors.length > 0) {
                                // 共通出错信息框
                                treatBackMessages(null, resInfo.errors);
                            } else {
                                $("#confirmmessage").text("数据更新成功！");
                                $("#confirmmessage").dialog({
                                            resizable : false,
                                            modal : true,
                                            title : "更新成功确认",
                                            buttons : {
                                                "确认" : function() {
                                                   // findit();
                                                    $(this).dialog("close");
                                                }
                                            }
                                        });
                            }
                        } catch (e) {
                        }
                    }
                });
            },
            "取消" : function() {
                $(this).dialog("close");
            }
        }
    });
}

// 修改事件
var modify_loss_summary = function(work_period,month) {
    $("#confirmmessage").text("确认要修改记录吗？");
    $("#confirmmessage").dialog({
        resizable : false,
        modal : true,
        title : "修改确认",
        close : function() {$("#editbutton").enable();},
        buttons : {
            "确认" : function() {
                var reqData = {
                   "choose_month":month
                };
                $(".work_period_month").each(function(idx) {
                    reqData["weeks.work_period[" + idx + "]"] = $(this).text().substring(0, 4);
                    reqData["weeks.month[" + idx + "]"] = $(this).text().substring(5);
                });
                $(".ocm_check").each(function(idx) {
                    reqData["weeks.ocm_check[" + idx + "]"] = $(this).val();
                });

                $(".qa_check").each(function(idx) {
                    reqData["weeks.qa_check[" + idx + "]"] = $(this).val();
                });
                $(".endoeye").each(function(idx) {
                    reqData["weeks.endoeye[" + idx + "]"] = $(this).val();
                });
                $(".ccd_valid_length").each(function(idx) {
                    reqData["weeks.ccd_valid_length[" + idx + "]"] = $(this).val();
                });
                $(".financy_budget").each(function(idx) {
                    reqData["weeks.financy_budget[" + idx + "]"] = $(this).val();
                });
                $(".e_u_settlement").each(function(idx) {
                    var e_u_settlement = $(this).val();
                    if($(this).val()=="undefined"){
                        e_u_settlement=0;
                    }
                    reqData["weeks.e_u_settlement[" + idx + "]"] =e_u_settlement;
                });
                $(this).dialog("close");
                $.ajax({
                    beforeSend : ajaxRequestType,
                    async : true,
                    url : servicePath + '?method=doupdate',
                    cache : false,
                    data : reqData,
                    type : "post",
                    dataType : "json",
                    success : ajaxSuccessCheck,
                    error : ajaxError,
                    complete :function(xhrobj, textStatus) {
			                update_Complete(xhrobj, textStatus,work_period,month);
			        }
                });
            },
            "取消" : function() {
                $(this).dialog("close");
            }
        }
    });
}
function update_Complete(xhrobj, textStatus,work_period,month) {
        var resInfo;
        try {
            // 以Object形式读取JSON
            eval('resInfo =' + xhrobj.responseText);
            if (resInfo.errors.length > 0) {
                // 共通出错信息框
                treatBackMessages(null, resInfo.errors);
            } else {
                //当前年月份数据修改后的当前刷新
                search_workPeriod_month(work_period,month)
                $("#confirmmessage").text("数据更新成功！");
                $("#confirmmessage").dialog({
                    resizable : false,
                    modal : true,
                    title : "更新成功确认",
                    buttons : {
                        "确认" : function() {
                            $(this).dialog("close");
                        }
                    }
                });
            }
        } catch (e) {
        }
    }

function search_Complete(xhrobj, textStatus,work_period,month,curMonth) {
    var work_period_month=work_period+"-"+month;
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    eval('resInfo =' + xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        $("#summary").show();
        $("#monthlist").hide();
        var badLossSummaryForms = resInfo.badLossSummaryForms;

        // top_theadContent--(可编辑的表格title)
        var theadContent = '';
        
        // ----------第一个table表格数据----------//
        // 报价差异行
        var quotation_row = "";
        // 分解追加行
        var decomposition_row = "";
        // NS追加 行
        var ns_row = "";
        // 工程内发现行
        var project_discover_row = "";
        // 新品零件不良行
        var new_partial_nogood_row = "";
        // 操作不良行
        var project_nogood_row = "";
        // 分室检查不良行
        var ocm_check_row = "";
        // 最终检查不良行
        var qa_check_row = "";
        // 保修期内不良行
        var service_repair_row = "";
        // ENDOEYE 行
        var endoeye_row = "";
        // CCD有效长度行
        var ccd_valid_length_row = "";
        // 零件成本(财务数据）行
        var financy_budget_row = "";
        // 损金总计(OSH)行
        var total_price_osh_row = "";
        // 不良比率(OSH)行
        var nogood_rate_osh_row = "";
        // 损金总计(OCAP)行
        var total_price_ocap_row = "";
        // 不良比率(OCAP)行
        var nogood_rate_ocap_row = "";

        // 损金总计(OSH)--集合
        var total_price_osh_arr = new Array();
        // 不良比率(OSH)--集合
        var badness_rate_osh_arr = new Array();
        // 损金总计(OCAP)--集合
        var total_price_ocap_arr = new Array();
        // 不良比率(OCAP)--集合
        var badness_rate_ocap_arr = new Array();
        // 零件成本（财务数据）--集合
        var financy_budget_arr = new Array();

        // 分解追加--集合
        var decomposition_arr = new Array();
        // NS追加--集合
        var ns_arr = new Array();
        // 工程内发现--集合
        var project_discover_arr = new Array();
        // 操作不良--集合
        var project_nogood_arr = new Array();
        // 报价差异--集合
        var quotation_arr = new Array();
        // 新品零件不良--集合
        var new_partial_nogood_arr = new Array();
        // 保修期内不良--集合
        var service_repair_arr = new Array();

        for (var i = 0; i < 12; i++) {

            if (i < badLossSummaryForms.length) {
                var retData = badLossSummaryForms[i];

                // 备注内容
                $("#textarea_comment").text(retData.comment);

                var month = eval(retData.month + '+1');

                // 损金总计(OSH)
                var total_price_osh = eval(parseFloat(retData.quotation || 0)
                    + '+' + parseFloat(retData.decomposition || 0) + '+'
                    + parseFloat(retData.ns || 0) + '+'
                    + parseFloat(retData.project_discover || 0) + '+'
                    + parseFloat(retData.new_partial_nogood || 0) + '+'
                    + parseFloat(retData.project_nogood || 0) + '+'
                    + parseFloat(retData.ocm_check || 0) + '+'
                    + parseFloat(retData.qa_check || 0) + '+'
                    + parseFloat(retData.service_repair || 0) + '+'
                    + parseFloat(retData.endoeye || 0) + '+'
                    + parseFloat(retData.ccd_valid_length || 0));

                total_price_osh = fixFloat(total_price_osh, 2);
                // 不良比率(OSH)
                var badness_rate_osh = "";
                // 不良比率(OCAP)
                var badness_rate_ocap = "";

                // 损金总计(OCAP)
                var total_price_ocap = eval(parseFloat(retData.decomposition || 0)+ '+'
                                     + parseFloat(retData.ns || 0)+ '+' 
                                     + parseFloat(retData.project_discover || 0)+ '+' 
                                     + parseFloat(retData.project_nogood || 0)+ '+' 
                                     + parseFloat(retData.ocm_check || 0)+ '+' 
                                     + parseFloat(retData.qa_check || 0)+ '+' 
                                     + parseFloat(retData.endoeye || 0)+ '+' 
                                     + parseFloat(retData.ccd_valid_length || 0));
                                     
                total_price_ocap = fixFloat(total_price_ocap, 2);

                if (retData.financy_budget == null || parseFloat(retData.financy_budget) == 0) {
                    badness_rate_osh = 0;
                    badness_rate_ocap = 0;
//                  badness_rate_osh = "-";
//                  badness_rate_ocap = "-";
                } else {
                    badness_rate_osh = parseFloat((parseFloat(total_price_osh) / parseFloat(retData.financy_budget)*100).toFixed(1));
                    badness_rate_ocap = parseFloat((total_price_ocap / retData.financy_budget*100).toFixed(1));
                }

                // 分解追加
                var decomposition_rate = "";
                if (retData.decomposition == "0"|| retData.decomposition == null) {
                    decomposition_rate = 0;
                } else {
                    decomposition_rate = parseFloat(((retData.decomposition / total_price_osh) * 100).toFixed(1));
                }
                decomposition_arr.push(parseFloat(decomposition_rate));
                // NS追加
                var ns_rate = "";
                if (retData.ns == "0" || retData.ns == null) {
                    ns_rate = 0;
                } else {
                    ns_rate = parseFloat(((retData.ns / total_price_osh) * 100).toFixed(1));
                }
                ns_arr.push(parseFloat(ns_rate));
                // 工程内发现
                var project_discover_rate = "";
                if (retData.project_discover == "0"|| retData.project_discover == null) {
                    project_discover_rate = 0;
                } else {
                    project_discover_rate = parseFloat(((retData.project_discover / total_price_osh) * 100).toFixed(1));
                }
                project_discover_arr.push(parseFloat(project_discover_rate));

                // 操作不良
                var project_nogood_rate = "";
                if (retData.project_nogood == "0"|| retData.project_nogood == null) {
                    project_nogood_rate = 0;
                } else {
                    project_nogood_rate = parseFloat(((retData.project_nogood / total_price_osh) * 100).toFixed(1));
                }
                project_nogood_arr.push(project_nogood_rate);
                // 报价差异
                var quotation_rate = "";
                if (retData.quotation == 0 || retData.quotation == null) {
                    quotation_rate = 0;
                } else {
                    quotation_rate = parseFloat(((retData.quotation / total_price_osh) * 100).toFixed(1));
                }
                quotation_arr.push(parseFloat(quotation_rate));

                // 新品零件不良
                var new_partial_nogood_rate = "";
                if (retData.new_partial_nogood == "0"|| retData.new_partial_nogood == null) {
                    new_partial_nogood_rate = 0;
                } else {
                    new_partial_nogood_rate = parseFloat(((retData.new_partial_nogood / total_price_osh) * 100).toFixed(1));
                }
                new_partial_nogood_arr.push(parseFloat(new_partial_nogood_rate));

                // 保修期内不良
                var service_repair_rate = "";
                if (retData.service_repair == "0"|| retData.service_repair == null) {
                    service_repair_rate = 0;
                } else {
                    service_repair_rate = parseFloat(((retData.service_repair / total_price_osh) * 100).toFixed(1));
                }
                service_repair_arr.push(parseFloat(service_repair_rate));

                total_price_osh_arr.push(fixFloat(total_price_osh / 1000, 2));
                badness_rate_osh_arr.push(badness_rate_osh);
                total_price_ocap_arr.push(fixFloat(total_price_ocap / 1000, 2));
                badness_rate_ocap_arr.push(badness_rate_ocap);
                if (retData.financy_budget) {
                    financy_budget_arr.push(fixFloat(retData.financy_budget / 1000, 2));
                } else {
                    financy_budget_arr.push(0);
                }

                var input_ocm_check ='<input class="ocm_check" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.ocm_check || 0) + '"/>';
                var input_qa_check ='<input class="qa_check" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.qa_check || 0) + '"/>';
                var input_endoeye ='<input class="endoeye" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.endoeye || 0) + '"/>';
                var input_ccd_valid_length ='<input class="ccd_valid_length" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="' + (retData.ccd_valid_length || 0) + '">';
                var input_financy_budget ='<input class="financy_budget" type="text" style="width:60px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.financy_budget || 0)+ '"/>';
                
                var label_ocm_check ='<label>'+ (retData.ocm_check || 0) + '</label>';
                var label_qa_check ='<label>'+ (retData.qa_check || 0) + '</label>';
                var label_endoeye ='<label>'+ (retData.endoeye || 0) + '</label>';
                var label_ccd_valid_length ='<label>' + addCommas((retData.ccd_valid_length || 0)) +'</label>';
                var label_financy_budget ='<label>'+ (retData.financy_budget || 0)+ '</label>';
 
                quotation_row += '<input type="hidden" class="e_u_settlement" value="'+retData.e_u_settlement+'"/><td class="rpt-td-content-grey" >'+addCommas(parseFloat((retData.quotation || 0)).toFixed(2))+ '</td>'
                decomposition_row += '<td class="rpt-td-content-white">'+ addCommas(parseFloat((retData.decomposition || 0)).toFixed(2)) + '</td>'
                ns_row += '<td class="rpt-td-content-white">'+ addCommas(parseFloat((retData.ns || 0)).toFixed(2)) + '</td>'
                project_discover_row += '<td class="rpt-td-content-white">'+ addCommas(parseFloat((retData.project_discover || 0)).toFixed(2))+ '</td>'
                new_partial_nogood_row += '<td class="rpt-td-content-grey">'+ addCommas(parseFloat((retData.new_partial_nogood || 0)).toFixed(2)) + '</td>'
                project_nogood_row += '<td class="rpt-td-content-white">'+ addCommas(parseFloat((retData.project_nogood || 0)).toFixed(2)) + '</td>'
                if($("#manager_scheduler").val()=="isTrue"){
                	  ocm_check_row += '<td class="rpt-td-content-white">'+input_ocm_check+'</td>'
                      qa_check_row += '<td class="rpt-td-content-white">'+input_qa_check+'</td>'
                      endoeye_row += '<td class="rpt-td-content-white">'+input_endoeye+'</td>'
                      ccd_valid_length_row += '<td class="rpt-td-content-white">'+input_ccd_valid_length+'</td>'
                      financy_budget_row += '<td class="rpt-td-content-white">'+input_financy_budget+'</td>'
                }else{
                	  ocm_check_row += '<td class="rpt-td-content-white">'+label_ocm_check+'</td>'
                      qa_check_row += '<td class="rpt-td-content-white">'+label_qa_check+'</td>'
                      endoeye_row += '<td class="rpt-td-content-white">'+label_endoeye+'</td>'
                      ccd_valid_length_row += '<td class="rpt-td-content-white">'+label_ccd_valid_length+'</td>'
                      financy_budget_row += '<td class="rpt-td-content-white">'+label_financy_budget+'</td>'
                }
                //ocm_check_row += '<td class="rpt-td-content-white">''</td>'
                //qa_check_row += '<td class="rpt-td-content-white"><input class="qa_check" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.qa_check || 0) + '"/></td>'
                service_repair_row += '<td class="rpt-td-content-grey">'+ addCommas(parseFloat((retData.service_repair || 0)).toFixed(2)) + '</td>'
                //endoeye_row += '<td class="rpt-td-content-white"><input class="endoeye" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'+ (retData.endoeye || 0) + '"/></td>'
                //ccd_valid_length_row += '<td class="rpt-td-content-white"><input class="ccd_valid_length" type="text" style="width:54px;height:11px;text-align:right;font-size:12px;" value="'
                //        + addCommas((retData.ccd_valid_length || 0)) + '"></td>'
                //financy_budget_row += '<td class="rpt-td-content-blue"><input class="financy_budget" type="text" style="width:60px;height:11px;text-align:right;font-size:12px;" value="'
                //        + (retData.financy_budget || 0)+ '"/></td>'
                total_price_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;">'+ addCommas(parseFloat(total_price_osh || 0)) + '</td>'
                nogood_rate_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;">'+ (badness_rate_osh || '-')+"%" + '</td>'
                total_price_ocap_row += '<td class="rpt-td-content-yellow">'+ addCommas(parseFloat(total_price_ocap || 0))+ '</td>'
                nogood_rate_ocap_row += '<td class="rpt-td-content-yellow">'+ (badness_rate_ocap || '-')+"%"+ '</td>';
                
            } else {
                //数据个数没有12个时，后补0作为数据----为了显示整整12个月的数据
                total_price_osh_arr.push(null);
                badness_rate_osh_arr.push(null);
                total_price_ocap_arr.push(null);
                badness_rate_ocap_arr.push(null);
                financy_budget_arr.push(null);
                decomposition_arr.push(null);
                ns_arr.push(null);
                project_discover_arr.push(null);
                project_nogood_arr.push(null);
                quotation_arr.push(null);
                new_partial_nogood_arr.push(null);
                service_repair_arr.push(null);
                
                // 当财年月份超过12月，就从1月开始计算
                if (parseInt(month) > 12) {
                    month = eval('0+1');
                }
                //添加行td内容到第一个table
                quotation_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                decomposition_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ns_row += '<td class="rpt-td-content-white"width="65px"></td>'
                project_discover_row += '<td class="rpt-td-content-white"width="65px"></td>'
                new_partial_nogood_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                project_nogood_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ocm_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                qa_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                service_repair_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                endoeye_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ccd_valid_length_row += '<td class="rpt-td-content-white"width="65px"></td>'
                financy_budget_row += '<td class="rpt-td-content-blue"width="65px"></td>'
                total_price_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                nogood_rate_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                total_price_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>'
                nogood_rate_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>';
                
                month++;
            }

        }
        //添加行td内容到第一个table
        $("#edit_value > table > tbody> tr:eq(0)").html("<td class='rpt-td-content-grey' style='text-align:left;'>报价差异</td>"+quotation_row);
        $("#edit_value > table > tbody> tr:eq(1)").html("<td class='rpt-td-content-white ' style='text-align:left;'>分解追加</td>"+decomposition_row);
        $("#edit_value > table > tbody> tr:eq(2)").html("<td class='rpt-td-content-white' style='text-align:left;'>NS追加</td>   "+ns_row);
        $("#edit_value > table > tbody> tr:eq(3)").html("<td class='rpt-td-content-white' style='text-align:left;'>工程内发现</td>"+project_discover_row);
        $("#edit_value > table > tbody> tr:eq(4)").html("<td class='rpt-td-content-grey' style='text-align:left;'>新品零件不良</td>"+new_partial_nogood_row);
        $("#edit_value > table > tbody> tr:eq(5)").html("<td class='rpt-td-content-white' style='text-align:left;'>操作不良</td>"+project_nogood_row);
        $("#edit_value > table > tbody> tr:eq(6)").html("<td class='rpt-td-content-white' style='text-align:left;color:#0000FF;'>分室检查不良</td>"+ocm_check_row);
        $("#edit_value > table > tbody> tr:eq(7)").html("<td class='rpt-td-content-white' style='text-align:left;color:#0000FF;'>最终检查不良</td>"+qa_check_row);
        $("#edit_value > table > tbody> tr:eq(8)").html("<td class='rpt-td-content-grey' style='text-align:left;color:#0000FF;'>保修期内不良</td>"+service_repair_row);
        $("#edit_value > table > tbody> tr:eq(9)").html("<td class='rpt-td-content-white' style='text-align:left;color:#FF0000;'>ENDOEYE </td>"+endoeye_row);
        $("#edit_value > table > tbody> tr:eq(10)").html("<td class='rpt-td-content-white' style='text-align:left;color:#FF0000;'>CCD有效长度</td>"+ccd_valid_length_row);
        $("#edit_value > table > tbody> tr:eq(11)").html("<td class='rpt-td-content-blue' style='text-align:left;'>零件成本(财务数据）</td>"+financy_budget_row);
        $("#edit_value > table > tbody> tr:eq(12)").html("<td class='rpt-td-title-r' style='background:#ffff00;text-align:left;'>损金总计(OSH)</td>/tr> "+total_price_osh_row);
        $("#edit_value > table > tbody> tr:eq(13)").html("<td class='rpt-td-title-r' style='background:#ffff00;text-align:left;'>不良比率(OSH)</td>"+nogood_rate_osh_row);
        $("#edit_value > table > tbody> tr:eq(14)").html("<td class='rpt-td-content-yellow' style='text-align:left;'>损金总计(OCAP)</td>"+total_price_ocap_row);
        $("#edit_value > table > tbody> tr:eq(15)").html("<td class='rpt-td-title-r' style='background:#ffc000;text-align:left;'>不良比率(OCAP)</td> "+nogood_rate_ocap_row);
    }

    // 损金柱状图表的Ｘ轴数据---财年月份(12个)
    var year_month_listdata = new Array();
    $("#edit_value > table > thead > tr th:nth-child(n+2)").each(function(idx) {
                year_month_listdata[idx] = $(this).text();
    });
    // 柱状图
    loss_summary_chart(year_month_listdata, total_price_osh_arr,financy_budget_arr, badness_rate_osh_arr, total_price_ocap_arr,badness_rate_ocap_arr);
    
    var work_period = badLossSummaryForms[0].work_period;
    
    // 折线图
    loss_summary_line_chart(year_month_listdata, decomposition_arr, ns_arr,project_discover_arr, project_nogood_arr, quotation_arr,new_partial_nogood_arr, service_repair_arr,work_period);

    //第三个table---财年月份点击后改变样式
    $("#loss_data > table > thead > tr th:nth-child(n+2)").each(function(idx) {
        $(this).addClass("month-title");
    });
    
    // 选择财年月份进行数据的查看
    $(".month-title").unbind("click");
    $(".month-title").bind("click",function() {
        var $target=$(this);
        var work_period_month = $target.text();
        var data = {
            "work_period" : work_period_month.substring(0, 4),
            "month" : work_period_month.substring(5)
        }
        $.ajax({
            beforeSend : ajaxRequestType,
            async : false,
            url : servicePath + '?method=searchofmonth',
            cache : false,
            data : data,
            type : "post",
            dataType : "json",
            success : ajaxSuccessCheck,
            error : ajaxError,
            complete : function(xhrobj, textStatus) {
                search_month_handleComplete(xhrobj, textStatus,
                        work_period_month,$target);
            }
        });
    });

    var month_listdata = badLossSummaryForms[badLossSummaryForms.length-1];
    if(month_listdata){
        //第三个table的损金总计
        var total_price = eval(parseFloat(month_listdata.quotation || 0) + '+'
                                + parseFloat(month_listdata.decomposition || 0) + '+'
                                + parseFloat(month_listdata.ns || 0) + '+'
                                + parseFloat(month_listdata.project_discover || 0) + '+'
                                + parseFloat(month_listdata.new_partial_nogood || 0) + '+'
                                + parseFloat(month_listdata.project_nogood || 0) + '+'
                                + parseFloat(month_listdata.ocm_check || 0) + '+'
                                + parseFloat(month_listdata.qa_check || 0) + '+'
                                + parseFloat(month_listdata.service_repair || 0) + '+'
                                + parseFloat(month_listdata.endoeye || 0) + '+'
                                + parseFloat(month_listdata.ccd_valid_length || 0));
        //饼图数据显示
        loss_summart_pieChart(work_period_month, month_listdata, total_price);
        
        $(".month-title").css("color","#000")
        //if ($target) $target.css("color","red");
        // 清空备注内容
        $("#comment_td_title").text(work_period_month + '备注');

        // 备注内容显示
        $("#textarea_comment").val(month_listdata.comment || "");
    }
    $("#comment_td_title").text(work_period_month + '备注');
};

function update_handleComplete(xhrobj, textStatus) {
    var resInfo = null;

    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    eval('resInfo =' + xhrobj.responseText);

    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        // 重新查询
        findit();
    }
};

// 损金总计
var findit = function() {
    $.ajax({
        beforeSend : ajaxRequestType,
        async : false,
        url : servicePath + '?method=search&period='+$("#period").val(),
        cache : false,
        data : null,
        type : "post",
        dataType : "json",
        success : ajaxSuccessCheck,
        error : ajaxError,
        complete : search_handleComplete
    });
};

var fixFloat = function(src, scale){
    if (src) {
        return parseFloat(src.toFixed(scale));
    } else {
        return null;
    }
}

function search_handleComplete(xhrobj, textStatus) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    //eval('resInfo =' + xhrobj.responseText);
    resInfo=$.parseJSON(xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        
        var badLossSummaryForms = resInfo.badLossSummaryForms;
        
        var loss=$("#manager_scheduler").val();
        
        // top_theadContent--(可编辑的表格title)
        var theadContent = '';

        // bottom_theadContent--(不可编辑的表格title)
        var thead2Content = '';
        // ----------第一个table表格数据----------//
        // 报价差异行
        var quotation_row = "";
        // 分解追加行
        var decomposition_row = "";
        // NS追加 行
        var ns_row = "";
        // 工程内发现行
        var project_discover_row = "";
        // 新品零件不良行
        var new_partial_nogood_row = "";
        // 操作不良行
        var project_nogood_row = "";
        // 分室检查不良行
        var ocm_check_row = "";
        // 最终检查不良行
        var qa_check_row = "";
        // 保修期内不良行
        var service_repair_row = "";
        // ENDOEYE 行
        var endoeye_row = "";
        // CCD有效长度行
        var ccd_valid_length_row = "";
        // 零件成本(财务数据）行
        var financy_budget_row = "";
        // 损金总计(OSH)行
        var total_price_osh_row = "";
        // 不良比率(OSH)行
        var nogood_rate_osh_row = "";
        // 损金总计(OCAP)行
        var total_price_ocap_row = "";
        // 不良比率(OCAP)行
        var nogood_rate_ocap_row = "";

        var top_content = "";

        for (var i = 0; i < 12; i++) {

            if (i < badLossSummaryForms.length) {
                var retData = badLossSummaryForms[i];

                // 备注内容
                $("#textarea_comment").text(retData.comment);

                var month = parseInt(retData.month) + 1;

                // 损金总计(OSH)
                var total_price_osh = parseFloat(retData.quotation || 0)
                    + parseFloat(retData.decomposition || 0) 
                    + parseFloat(retData.ns || 0)
                    + parseFloat(retData.project_discover || 0) 
                    + parseFloat(retData.new_partial_nogood || 0) 
                    + parseFloat(retData.project_nogood || 0)
                    + parseFloat(retData.ocm_check || 0)
                    + parseFloat(retData.qa_check || 0) 
                    + parseFloat(retData.service_repair || 0)
                    + parseFloat(retData.endoeye || 0) 
                    + parseFloat(retData.ccd_valid_length || 0);

                total_price_osh = fixFloat(total_price_osh, 2);
                if(loss=="isTrue"){
                top_content += '<tr><td class="ui-widget-header">'+ (i + 1)+ '</td>'+ '<td class="ui-widget-content">'+ retData.work_period
                            + '</td>'+ '<td class="ui-widget-content">'+ retData.month+ '</td>'+ '<td class="ui-widget-content"><label>'+ addCommas(total_price_osh || 0)
                            + '</label></td>'+ '<td class="ui-widget-content"><input type="number" min="0" value="'+ retData.e_u_settlement+ '">'
                            + '</input><input type="button" work_period="'+retData.work_period+'" month="'+retData.month+'" year="'+retData.year+'" class="recount_button detail-button ui-button ui-widget ui-state-default" value="重新计算"></input></td>'
                            + '<td class="ui-widget-content"><input type="button" year="'+retData.work_period+'" month="'+retData.month+'" class="loss_summary_import detail-button ui-button ui-widget ui-state-default" value="导出">'
                            + '<input type="button" year="'+retData.year+'" month="'+retData.month+'" work_period="'+retData.work_period+'"class="detail-button ui-button ui-widget ui-state-default showSummary" value="查看">'
                            + '<input type="button"  id="'+retData.year+"/"+retData.month+'" class="detail-button ui-button ui-widget ui-state-default showLoss" value="明细"></input></td>'
                            + '</tr> ';
                }else{
                  top_content += '<tr><td class="ui-widget-header">'+ (i + 1)+ '</td>'+ '<td class="ui-widget-content">'+ retData.work_period
		                     + '</td>'+ '<td class="ui-widget-content">'+ retData.month+ '</td>'+ '<td class="ui-widget-content"><label>'+ addCommas(total_price_osh || 0)
		                     + '</label></td>'+ '<td class="ui-widget-content"><label>'+(retData.e_u_settlement || 0.00)+ '</label>'
		                     + '</input><input type="button" style="margin-left:20px;" work_period="'+retData.work_period+'" month="'+retData.month+'" year="'+retData.year+'" class="recount_button detail-button ui-button ui-widget ui-state-default" value="重新计算"></input></td>'
		                     + '<td class="ui-widget-content"><input type="button" year="'+retData.work_period+'" month="'+retData.month+'" class="loss_summary_import detail-button ui-button ui-widget ui-state-default" value="导出">'
		                     + '<input type="button" year="'+retData.year+'" month="'+retData.month+'" work_period="'+retData.work_period+'"class="detail-button ui-button ui-widget ui-state-default showSummary" value="查看">'
		                     + '<input type="button"  id="'+retData.year+"/"+retData.month+'" class="detail-button ui-button ui-widget ui-state-default showLoss" value="明细"></input></td>'
		                     + '</tr> ';
                }
                theadContent += '<th class="work_period_month rpt-td-content-white" width="65px"><div>' + retData.work_period+ '-'+ retData.month+ '</div></th>';
                
                thead2Content += '<th class="rpt-td-content-white" width="65px"><div>'+ retData.work_period+ '-'+ retData.month+ '</div></th>';
               
                quotation_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                decomposition_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ns_row += '<td class="rpt-td-content-white"width="65px"></td>'
                project_discover_row += '<td class="rpt-td-content-white"width="65px"></td>'
                new_partial_nogood_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                project_nogood_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ocm_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                qa_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                service_repair_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                endoeye_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ccd_valid_length_row += '<td class="rpt-td-content-white"width="65px"></td>'
                financy_budget_row += '<td class="rpt-td-content-blue"width="65px"></td>'
                total_price_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                nogood_rate_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                total_price_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>'
                nogood_rate_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>';
                
            } else {
                // 当财年月份超过12月，就从1月开始计算
                if (parseInt(month) > 12) {
                    month = 1;
                }
                //财年月份--第一个table
                theadContent += '<th class="add_work_period_month rpt-td-content-white" width="65px"><div>'+ retData.work_period + '-' + month + '</div></th>';
                
                //财年月份--第三个table
                thead2Content += '<th class="rpt-td-content-white" width="65px"><div>'+ retData.work_period + '-' + month + '</div></th>';
              
                //添加行td内容到第一个table
                quotation_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                decomposition_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ns_row += '<td class="rpt-td-content-white"width="65px"></td>'
                project_discover_row += '<td class="rpt-td-content-white"width="65px"></td>'
                new_partial_nogood_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                project_nogood_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ocm_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                qa_check_row += '<td class="rpt-td-content-white"width="65px"></td>'
                service_repair_row += '<td class="rpt-td-content-grey"width="65px"></td>'
                endoeye_row += '<td class="rpt-td-content-white"width="65px"></td>'
                ccd_valid_length_row += '<td class="rpt-td-content-white"width="65px"></td>'
                financy_budget_row += '<td class="rpt-td-content-blue"width="65px"></td>'
                total_price_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                nogood_rate_osh_row += '<td class="rpt-td-title-r" style="background:#ffff00;"width="65px"></td>'
                total_price_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>'
                nogood_rate_ocap_row += '<td class="rpt-td-content-yellow"width="65px"></td>';
                
                month++;
            }

        }

        $("#monthlist table tbody").html(top_content);
        //添加行td内容到第一个table
        // var $edit_value_table = $("#edit_value > table").detach();
        $("#edit_value > table > thead > tr:eq(0)").html(" <th class='rpt-td-content-white' width='120px' style='text-align:left;'> <div>项目(USD)</div></th>"+theadContent);
        $("#edit_value > table > tbody> tr:eq(0)").html("<td class='rpt-td-content-grey' style='text-align:left;'>报价差异</td>"+quotation_row);
        $("#edit_value > table > tbody> tr:eq(1)").html("<td class='rpt-td-content-white ' style='text-align:left;'>分解追加</td>"+decomposition_row);
        $("#edit_value > table > tbody> tr:eq(2)").html("<td class='rpt-td-content-white' style='text-align:left;'>NS追加</td>   "+ns_row);
        $("#edit_value > table > tbody> tr:eq(3)").html("<td class='rpt-td-content-white' style='text-align:left;'>工程内发现</td>"+project_discover_row);
        $("#edit_value > table > tbody> tr:eq(4)").html("<td class='rpt-td-content-grey' style='text-align:left;'>新品零件不良</td>"+new_partial_nogood_row);
        $("#edit_value > table > tbody> tr:eq(5)").html("<td class='rpt-td-content-white' style='text-align:left;'>操作不良</td>"+project_nogood_row);
        $("#edit_value > table > tbody> tr:eq(6)").html("<td class='rpt-td-content-white' style='text-align:left;color:#0000FF;'>分室检查不良</td>"+ocm_check_row);
        $("#edit_value > table > tbody> tr:eq(7)").html("<td class='rpt-td-content-white' style='text-align:left;color:#0000FF;'>最终检查不良</td>"+qa_check_row);
        $("#edit_value > table > tbody> tr:eq(8)").html("<td class='rpt-td-content-grey' style='text-align:left;color:#0000FF;'>保修期内不良</td>"+service_repair_row);
        $("#edit_value > table > tbody> tr:eq(9)").html("<td class='rpt-td-content-white' style='text-align:left;color:#FF0000;'>ENDOEYE </td>"+endoeye_row);
        $("#edit_value > table > tbody> tr:eq(10)").html("<td class='rpt-td-content-white' style='text-align:left;color:#FF0000;'>CCD有效长度</td>"+ccd_valid_length_row);
        $("#edit_value > table > tbody> tr:eq(11)").html("<td class='rpt-td-content-blue' style='text-align:left;'>零件成本(财务数据）</td>"+financy_budget_row);
        $("#edit_value > table > tbody> tr:eq(12)").html("<td class='rpt-td-title-r' style='background:#ffff00;text-align:left;'>损金总计(OSH)</td>/tr> "+total_price_osh_row);
        $("#edit_value > table > tbody> tr:eq(13)").html("<td class='rpt-td-title-r' style='background:#ffff00;text-align:left;'>不良比率(OSH)</td>"+nogood_rate_osh_row);
        $("#edit_value > table > tbody> tr:eq(14)").html("<td class='rpt-td-content-yellow' style='text-align:left;'>损金总计(OCAP)</td>"+total_price_ocap_row);
        $("#edit_value > table > tbody> tr:eq(15)").html("<td class='rpt-td-title-r' style='background:#ffc000;text-align:left;'>不良比率(OCAP)</td> "+nogood_rate_ocap_row);

        $("#loss_data > table > thead> tr").html("<th class='rpt-td-content-white' width='200px' style='text-align:left;' height='40px'> <div>"+badLossSummaryForms[0].work_period+"损金USD(OSH责任)</div></th>"+thead2Content);        

        //明细button--跳转到SORC损金页面
        $(".showLoss").click(function(){
          var current_id = $(this).attr("id");
            var data={
               "ocm_shipping_month":current_id
            }
            $.ajax({
                beforeSend : ajaxRequestType,
                async : false,
                url :'sorc_loss.do?method=search',
                cache : false,
                data : data,
                type : "post",
                dataType : "json",
                success : ajaxSuccessCheck,
                error : ajaxError,
                complete : function(xhrobj, textStatus){
                        show_loss_Complete(xhrobj, textStatus, current_id);
                }               
              
            });            
        });
        
        //点击重新计算按钮--修改当前操作年月的汇率
        $(".recount_button").click(function(){
            //当前操作年月的损金总计
            var $loss_price=$(this).parent().prev("td").find("label");
            var work_period=$(this).attr("work_period");
            var month=$(this).attr("month");
            var year=$(this).attr("year");
            var data={
               "work_period":work_period,
               "month":month,
               "e_u_settlement":$(this).prev("input").val()
            }
            $.ajax({
                beforeSend : ajaxRequestType,
                async : true,
                url : servicePath + '?method=doupdateSettlement',
                cache : false,
                data : data,
                type : "post",
                dataType : "json",
                success : ajaxSuccessCheck,
                error : ajaxError,
                complete : function(xhrobj, textStatus){
                    update_settlementComplete(xhrobj, textStatus, work_period,year,month,$loss_price);
                }          
            });
         })
         
        // 财年月损金的查看---查看button事件
         var work_period="";
         var month="";
         
        $(".showSummary").unbind("click");
        $(".showSummary").bind("click",function() {
             work_period=$(this).attr("work_period");
             month=$(this).attr("month");
             year=$(this).attr("year");
             search_workPeriod_month(work_period,month,year);
        });
        
          // 确认修改事件
        $("#confirm_modify_button").click(function() {modify_loss_summary(work_period,month);});

        // 财年月损金的导出---导出button事件
        $(".loss_summary_import").click(function() {
            var data={
               "work_period":$(this).attr("year"),
               "month":$(this).attr("month")
            };
            
            $.ajax({
                beforeSend : ajaxRequestType,
                async : false,
                url : servicePath + '?method=report',
                cache : false,
                data : data,
                type : "post",
                dataType : "json",
                success : ajaxSuccessCheck,
                error : ajaxError,
                complete : function(xhrobj) {
                    var resInfo = null;
                    //eval("resInfo=" + xhrobj.responseText);
                    resInfo = $.parseJSON(xhrobj.responseText);
                    if (resInfo && resInfo.fileName) {
                        if ($("iframe").length > 0) {
                            $("iframe").attr("src","download.do"+ "?method=output&filePath="+ resInfo.filePath+ "&fileName="+ resInfo.fileName);
                        } else {
                            var iframe = document.createElement("iframe");
                            iframe.src = "download.do"+ "?method=output&filePath=" + resInfo.filePath + "&fileName="+ resInfo.fileName;
                            iframe.style.display = "none";
                            document.body.appendChild(iframe);
                        }
                    } else {
                        alert("文件导出失败！");
                    }
                }
            });
        });
    }
};

var search_workPeriod_month = function(work_period,month,year){
       var data={
         "work_period":work_period,
         "month":month,
         "year":year
        }
        $.ajax({
            beforeSend : ajaxRequestType,
            async : false,
            url : servicePath + '?method=searchMonth',
            cache : false,
            data : data,
            type : "post",
            dataType : "json",
            success : ajaxSuccessCheck,
            error : ajaxError,
            complete : function(xhrobj, textStatus){
                search_Complete(xhrobj, textStatus, work_period,month,month);
            }                
        });
}

function show_loss_Complete(xhrobj, textStatus,current_id){
        document.location.href = "sorc_loss.do?year_month="+current_id;
}

var update_settlementComplete = function(xhrobj, textStatus,work_period,year,month,$loss_price) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    //eval('resInfo =' + xhrobj.responseText);
    resInfo = $.parseJSON(xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        var data={
            "work_period":work_period,
            "year":year,
            "month":month
        }
        $.ajax({
	        beforeSend : ajaxRequestType,
	        async : true,
	        url : servicePath + '?method=searchOneData',
	        cache : false,
	        data : data,
	        type : "post",
	        dataType : "json",
	        success : ajaxSuccessCheck,
	        error : ajaxError,
	        complete : function(xhrobj, textStatus){
                    update_EComplete(xhrobj, textStatus,$loss_price);
                }            
	    });
      }
}

//更新汇率
var update_EComplete = function(xhrobj, textStatus,$loss_price) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    //eval('resInfo =' + xhrobj.responseText);
    resInfo = $.parseJSON(xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages("#searcharea", resInfo.errors);
    } else {
        //当前修改汇率损金总计
        $loss_price.text((parseFloat(resInfo.lossPrice)).toFixed(2))
      }
}

function search_month_handleComplete(xhrobj, textStatus, work_period_month,$target) {
    var resInfo = null;
    // 以Object形式读取JSON responseText获取来自服务器响应的数据
    //eval('resInfo =' + xhrobj.responseText);
    resInfo = $.parseJSON(xhrobj.responseText);
    if (resInfo.errors.length > 0) {
        // 共通出错信息框
        treatBackMessages(null, resInfo.errors);
    } else {
        var month_listdata = resInfo.badLossSummaryForm;
        if(month_listdata){
            //第三个table的损金总计
            var total_price =parseFloat(month_listdata.quotation || 0)
                                    + parseFloat(month_listdata.decomposition || 0)
                                    + parseFloat(month_listdata.ns || 0)
                                    + parseFloat(month_listdata.project_discover || 0)
                                    + parseFloat(month_listdata.new_partial_nogood || 0)
                                    + parseFloat(month_listdata.project_nogood || 0)
                                    + parseFloat(month_listdata.ocm_check || 0)
                                    + parseFloat(month_listdata.qa_check || 0)
                                    + parseFloat(month_listdata.service_repair || 0)
                                    + parseFloat(month_listdata.endoeye || 0)
                                    + parseFloat(month_listdata.ccd_valid_length || 0);
            //饼图数据显示
            loss_summart_pieChart(work_period_month, month_listdata, total_price);
            
            $(".month-title").css("color","#000")
            //if ($target) $target.css("color","red");
            // 清空备注内容
            $("#comment_td_title").text(work_period_month + '备注');

            // 备注内容显示
            $("#textarea_comment").val(month_listdata.comment || "");
        }
    }
};
// 饼图
var loss_summart_pieChart = function(work_period_month, month_listdata, total_price) {

	var  decomposition = (parseFloat(month_listdata.decomposition/ total_price) * 100).toFixed(1);
	var  ns= (parseFloat(month_listdata.ns / total_price) * 100).toFixed(1);
	var  project_discover = (parseFloat(month_listdata.project_discover/ total_price) * 100).toFixed(1);
	var  project_nogood = (parseFloat(month_listdata.project_nogood/ total_price) * 100).toFixed(2);
	var  quotation = (parseFloat(month_listdata.quotation/ total_price)* 100).toFixed(1);
	var  new_partial_nogood = (parseFloat(month_listdata.new_partial_nogood/ total_price) * 100).toFixed(1);
	var  service_repair = (parseFloat(month_listdata.service_repair/ total_price) * 100).toFixed(2);
	var  ocm_check = (parseFloat(month_listdata.ocm_check/ total_price) * 100).toFixed(2);
	var  qa_check = (parseFloat(month_listdata.qa_check/ total_price) * 100).toFixed(2);
	var  endoeye = (parseFloat(month_listdata.endoeye/ total_price) * 100).toFixed(2);
	var  ccd_valid_length = (parseFloat(month_listdata.ccd_valid_length/ total_price) * 100).toFixed(2);
	
	var list=[decomposition,ns,project_discover,project_nogood,quotation,new_partial_nogood,service_repair,ocm_check,qa_check,endoeye,ccd_valid_length];
	
	//new一个全新的集合，存放全部数据
	var aMap = {};
	
	//值等于空的集合
	var nMap = {};
	for(var i = 0;i<list.length;i++){
		//当值为空时，设置其值为0
		if(isNaN(list[i])){
			nMap[i] = list[i];
		}else{
			aMap[i] = list[i];
		}
	}
	var index=0;
	$.each(nMap, function(key, value) {
		index++;
	}); 
	
	//如果当全部的值都是空时，设置偶值为20，奇值为‘’
	if(index==11){
		for(var n = 0;n<11;n++){
			if(n%2==0){
				aMap[n]=20.0;
			}else{
				aMap[n]='';
			}
		}
	//如果并不是全部的值都是空的，设置其中空的值为0
	}else{
		$.each(nMap, function(key, value) {
			if(isNaN(nMap[key])){
				nMap[key] = 0.0;
			}
			//将值最终加在全新的集合上
			aMap[key]=nMap[key];
		}); 
	}
	
    $('#pie-container').highcharts({
        chart : {
            type : 'pie'/*,
            options3d : {
                enabled : true,
                alpha : 45,
                beta : 0
            }*/
        },
        title : {
            text : work_period_month + '月不良损金'
        },
        tooltip : {
            pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions : {
            pie : {
                allowPointSelect : true,
                cursor : 'pointer',
                depth : 35,
                dataLabels : {
                    enabled : true,
                    format : '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style : {
                        color : 'black'
                    }
                }
            }
        },
        labels : {
            style : {
                fontSize : '12px',
                color : 'black'
            },
            formatter : function() {
                return this.value + '.0%';
            }
        },
        credits : {
            enabled : false
        },
        //当series中data中visible是false时，隐藏该data
        ignoreHiddenPoint : true,
        series : [{
            type : 'pie',
            name : '所占比例',
            data : [
                    { name : '分解追加',
					  y:parseFloat(aMap[0]),
                      color:'#c2585a'
                     },
                    {name :'NS追加',
                     y:parseFloat(aMap[1]),
                     color:'#a9c068'
                     },
                    {name :'工程内发现',
                     y:parseFloat(aMap[2]),
                     color:'#9275af'},
                    {
                        name : '操作不良',
                        y:parseFloat(aMap[3]),
                        sliced : true,
                        selected : true,
                        color:'#e2a353'
                    },
                    {name :'报价差异',
                     y:parseFloat(aMap[4]),
                     color:'#5e92c2'},
                    {name :'新品零件不良',
                     y:parseFloat(aMap[5]),
                     color:'#59b6c8'},
                    {name :'保修期内不良',
                     y:parseFloat(aMap[6]),
                     color:'#b1c5e0'},
                    {
                        name : '分室检查不良',
                        y:parseFloat(aMap[7]),
                        visible : false
                    }, {
                        name : '最终检查不良',
                        y:parseFloat(aMap[8]),
                        visible : false
                    }, {
                        name : 'ENDOEYE',
                        y:parseFloat(aMap[9]),
                        visible : false
                    }, {
                        name : 'CCD有效长度',
                        y:parseFloat(aMap[10]),
                        visible : false
                    }]
        }]
    });
}

// 柱状图
var loss_summary_chart = function(year_month_listdata, total_price_osh_arr,financy_budget_arr, badness_rate_osh_arr, total_price_ocap_arr,badness_rate_ocap_arr) {
    $("#container").highcharts({
        chart : {
            type : 'column'
        },
        title : {
            text : null
        },
        xAxis : {
            categories : year_month_listdata,
            labels : {
                style : {
                    fontSize : '12px',
                    color : 'black'
                }
            },
            tickPosition : 'inside'
        },
        yAxis : [{
                    min : 0,
//                  max : 10000,
//                  tickInterval : 300,
                    title : {
                        text : "金额(KUSD)"
                    },
                    tickPosition : 'inside',
                    lineWidth : 1,
                    gridLineWidth : 0,
                    tickWidth : 1,
                    tickLength : 5
                }, {
                    min : 0,
//                  max : 25,
//                  tickInterval : 5,
                    title : {
                        text : "比率"
                    },
                    labels : {
                        style : {
                            fontSize : '12px',
                            color : 'black'
                        },
                        formatter : function() {
                            return this.value + '.0%';
                        }
                    },
                    tickPosition : 'inside',
                    lineWidth : 1,
                    gridLineWidth : 0,
                    lineStyle : 'dashed',
                    tickWidth : 1,
                    tickLength : 5,
                    opposite : true
                }],
        tooltip : {
            headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
                    + '<td style="padding:0"><b>{point.y} </b></td></tr>',
            footerFormat : '</table>',
            shared : true,
            useHTML : true
        },
        legend : {
            layout : 'vertical',
            align : 'right', // TODO
            verticalAlign : 'top',
            x : -200, // TODO
            y : 30,
            floating : true,
            borderWidth : 1,
            backgroundColor : '#FFFFFF'
        },
        credits : {
            enabled : false
        },
        series : [{
                    name : '损金总计(OCAP)',
                    color : "#a80c81",
                    legendIndex : 1,
                    dataLabels : {
                        color : 'black',
                        formatter : function() {
                            return this.y
                        },
                        enabled : true,
                        style : {
                            fontSize : '12px',
                            class : 'textSign'
                        }
                    },
                    data : total_price_ocap_arr,
                    yAxis : 0
                }, {
                    name : '损金总计(OSH)',
                    type : 'column',
                    color : '#8bc151',
                    legendIndex : 1,
                    dataLabels : {
                        color : 'black',
                        formatter : function() {
                            return this.y
                        },
                        enabled : true,
                        style : {
                            fontSize : '12px',
                            class : 'textSign'
                        }
                    },
                    data : total_price_osh_arr,
                    yAxis : 0
                },{
                    name : '不良比率(OCAP)',
                    type : 'spline',
                    color : '#2745fc',
                    legendIndex : 3,
                    yAxis : 1,
                    dataLabels : {
                        color : 'black',
                        formatter : function() {
                            return this.y + '%'
                        },
                        enabled : true,
                        style : {
                            fontSize : '12px',
                            class : 'textSign'
                        }
                    },
                    marker : {
                        symbol : 'diamond'
                    },
                    data : badness_rate_ocap_arr
                }, {
                    name : '不良比率(OSH)',
                    type : 'spline',
                    color : '#f7a35c',
                    legendIndex : 3,
                    yAxis : 1,
                    dataLabels : {
                        color : 'black',
                        formatter : function() {
                            return this.y + '%'
                        },
                        enabled : true,
                        style : {
                            fontSize : '12px',
                            class : 'textSign'
                        }
                    },
                    marker : {
                        symbol : 'diamond'
                    },
                    data : badness_rate_osh_arr
                }]
    });
}

// 折线图
var loss_summary_line_chart = function(year_month_listdata, decomposition_arr,ns_arr, project_discover_arr, project_nogood_arr, quotation_arr,new_partial_nogood_arr, service_repair_arr,work_period) {
    $('#broken_line-container').highcharts({
                title : {
                    text : '',
                    x : -20
                    // center
                },
                subtitle : {
                    text : work_period,
                    x : -20
                },
                xAxis : {
                    categories : year_month_listdata,
                    labels : {
                        style : {
                            fontSize : '12px',
                            color : 'black'
                        }
                    },
                    tickPosition : 'inside'
                },
                yAxis : {
                    min : 0,
                    tickInterval : 5,
                    title : {
                        text : '比率'
                    },
                    labels : {
                        style : {
                            fontSize : '12px',
                            color : 'black'
                        },
                        formatter:function(){return this.value + '.0%';}
                    },
                    plotLines : [{
                                value : 0,
                                width : 1,
                                color : '#808080'
                            }]
                },
                tooltip : {
                    valueSuffix : '%'
                },
                legend : {
                    layout : 'vertical',
                    align : 'right',
                    verticalAlign : 'middle',
                    borderWidth : 0
                },
                credits : {
                    enabled : false
                },
                series : [{name : '分解追加',data : decomposition_arr}, 
                          {name : 'NS追加',data : ns_arr}, 
                          {name : '工程内发现',data : project_discover_arr},
                          {name : '操作不良',data : project_nogood_arr}, 
                          {name : '报价差异',data : quotation_arr}, 
                          {name : '新品零件不良',data : new_partial_nogood_arr}, 
                          {name : '保修期内不良',data : service_repair_arr}
                         ]
            });
}
//将数字从个位开始起，按照数字的大小，3位一个逗号分开
function addCommas(nStr)
{
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}