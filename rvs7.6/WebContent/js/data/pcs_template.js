var servicePath ="pcsTemplate.do";
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
		$("#result_area").hide();
	}
	
	$("#resetbutton").click(clearContent);

	$("#referencebutton").click(function(){
		
		getRefer();
		
	});
	var getRefer= function(){
		var data  = {
		        model_id : $("#reference_model_id").val()
		        };
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=refer',
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
							treatBackMessages("#searchform", resInfo.errors);
						}else{
							showData(resInfo.pcses);
							$("#label_model_name").text($("#model_text").val());
							$("#detail_model_id").val(data.model_id);

							$("#result_area").show();
						}
						
					} catch(e) {
						
					}
				}
			});
	}
	var showData = function(pcses){
		var tabs = "";
		var tabscount = 0;
		var contents = "";

		for (var idx in pcses) {
			var pcsgroup = pcses[idx];
			for (var pcsseq in pcsgroup) {
				tabs += '<input type="radio" '+(tabscount == 0 ? 'checked' : '')+' name="pcs_page" class="ui-button ui-corner-up-s ui-helper-hidden-accessible" id="pcs_page_'+tabscount+'"><label role="button" class="ui-state-default '+(tabscount == 0 ? 'ui-state-active' : '')+'" for="pcs_page_'+tabscount+'" title="'+pcsseq+'"><span class="ui-button-text">'+pcsseq+'</span></label>';
				contents+= '<div id="pcs_content_'+tabscount+'" '+(tabscount == 0 ? '' : 'style="display:none"')+' class="pcs_content">' + pcsgroup[pcsseq] + "</div>";
				tabscount++;
			}			
		}
		
		$("#pcs_pages").html(tabs).buttonset();
		$("#pcs_contents").html(contents);
		$("#pcs_pages input").click(function(){
			$("#pcs_contents .pcs_content").hide();
			$("#" + this.id.replace("pcs_page_", "pcs_content_")).show();
		});	
		$("#pcs_contents input,#pcs_contents textarea").parent().css("background-color", "#93C3CD");
		$("#pcs_contents input:text").autosizeInput();
		$("#pcs_contents input[name^='EN'], #pcs_contents input[name^='LN']").button();
		// 自动选择第一个可填写页
		var activePage = $("#pcs_contents div:has(input):first");
		if (activePage.length == 1) {
			$("#" + activePage.attr("id").replace("pcs_content_", "pcs_page_")).trigger("click");
		}
	}
	$("#outputbutton").click(function(){
		
		output();
		
	});
	function output() {
		var data = {
			model_id :$("#detail_model_id").val()
		}
		// Ajax提交
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: false, 
			url: servicePath + '?method=makeTemplateFiles', 
			cache: false, 
			data: data, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			complete:  function(xhrobj, textStatus){
				var resInfo = null;
	
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
				
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages(null, resInfo.errors);
					} else {
						if ($("iframe").length > 0) {
							$("iframe").attr("src", "download.do"+"?method=output&fileName="+ $("#label_model_name").text() +"-模板.zip&filePath=" + resInfo.tempFile);
						} else {
							var iframe = document.createElement("iframe");
				            iframe.src = "download.do"+"?method=output&fileName="+ $("#label_model_name").text() +"-模板.zip&filePath=" + resInfo.tempFile;
				            iframe.style.display = "none";
				            document.body.appendChild(iframe);
						}
					}
				} catch(e) {
				}
			}
		});
	}
})