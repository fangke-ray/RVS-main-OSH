var servicePath ="standard_work_time.do";
var modelName ;
var rank;
$(function(){
	//按钮点击效果
	$("input.ui-button").button();
	
	//对象型号 下拉作用选择
	setReferChooser($("#reference_model_id"), null, null, function(){$("#reference_model_id").change()});
	
	$("#reference_rank_id").select2Buttons();
	var clearContent=function(){
		$("#reference_model_id").val("").prev().val("");
		$("#reference_rank_id").val("").trigger("change");
		$("#result_area").hide();
	}
	
	$("#resetbutton").click(clearContent);
	/*选择型号之后category_id满足条件等级的select options改变成其他*/
	$("#reference_model_id").change(function(){		
		//传入到后台的一个model_id 后台进行request.getParameter("model_id")取值
		var reqdata  = {
				        model_id : $("#reference_model_id").val(),
				        isLine : $("#reference_model_id").data("isLine")
				        };
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=changeRank',
			cache : false,
			data : reqdata,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo;
				try{
					eval("resInfo=" + xhrobj.responseText);
					if (resInfo.level_options) {
						//页面的显示
						$("#reference_rank_id").html(resInfo.level_options);
						//在有数据在页面之后进行初始页面的select移除
						$("#reference_rank_id").next("div").remove();
						//将现有的数据进行select2Buttons显示
						$("#reference_rank_id").select2Buttons();	
						//获取是否是流水线的判断 true or false
						$("#reference_model_id").data("isLine",resInfo.isLine);	
					}
					
				} catch(e) {
				}
			}
		});
	});
	//页面初始的时候是默认流水线状态下
	$("#reference_model_id").data("isLine",true);
	//检索事件
	$("#referencebutton").click(function(){
		
		setData();
		
	});
	var setData= function(){
		var data  = {
		        model_id : $("#reference_model_id").val(),
		        level:$("#reference_rank_id").val()
		        };
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=search',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					var resInfo;
					try{
						eval("resInfo=" + xhrobj.responseText);
						if(resInfo.errors.length>0){
							treatBackMessages(null, resInfo.errors);
						}else{
							showData(resInfo.retData);
							$("#label_model_id").text($("#model_text").val());
							//获取选中的options的文本显示值
							$("#label_rank_id").text($("#reference_rank_id :selected").text());

							$("#result_area").show();
						}
						
					} catch(e) {
						
					}
				}
			});
	}
	var showData = function(retData){
		var tbodyContent="";
		//遍历
		var halfSize = Math.round(retData.length / 2);

		for (var i = 0; i < halfSize ; i++){
			var iMark = i;
			var aline = retData[iMark];
			
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">' + 
			'<td class="ui-state-default" style="text-align:center;">'+ (iMark + 1) +'</td>'+
			'<td style="text-align:center;">'+ aline.process_code +'</td>'+
			'<td style="text-align:left;">'+ aline.name +'</td>'+
			'<td style="text-align:right;">'+ aline.line_name +'</td>'

			iMark = halfSize + i;
			if (iMark == retData.length) {
				tbodyContent += 
				'<td class="ui-state-default" style="text-align:center;"></td>'+
				'<td style="text-align:center;"></td>'+
				'<td style="text-align:left;"></td>'+
				'<td style="text-align:right;"></td></tr>'
			} else {
				aline = retData[iMark];
				tbodyContent += 
				'<td class="ui-state-default" style="text-align:center;">'+ (iMark + 1) +'</td>'+
				'<td style="text-align:center;">'+ aline.process_code +'</td>'+
				'<td style="text-align:left;">'+ aline.name +'</td>'+
				'<td style="text-align:right;">'+ aline.line_name +'</td></tr>'
			}
		};
		
		$("#edit_value > table > tbody").html(tbodyContent);
		
	}	
})