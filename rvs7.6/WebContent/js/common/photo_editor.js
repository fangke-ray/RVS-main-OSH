var hostname = document.location.hostname;
var myPath = "imageUpload.do"

var jcrop_api = {};

var uploadPhoto = function(upload_id, callback, dividing, filename) {
	// ajax enctype="multipart/form-data"

	var postData = null;
	if (dividing) {
		postData = {dividing: dividing,filename:filename};
	}

    $.ajaxFileUpload({
        url : myPath + "?method=sourceImage", // 需要链接到服务器地址
        secureuri : false,
        data: postData,
        fileElementId : 'photo_file', // 文件选择框的id属性
        dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = $.parseJSON(responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				if (callback)
					callback(resInfo);
			}
		}
     });
};

// 
var photo_editor_functions = {
	target : null,
	dividing : null,
	$editted_image : $("#editted_image"),
	getPhotoPath : function(uuid){
		if (photo_editor_functions.dividing) {
			// getPathByDividingPhotoName
			return "http://" + hostname + "/photos/" + photo_editor_functions.dividing + "/" + uuid + "_fix.jpg?_s=" + new Date().getTime();
		} else {
			// getPathByPhotoUuid
			return "http://" + hostname + "/photos/uuid/" + uuid.substring(0,4) + "/" + uuid + "_fix.jpg?_s=" + new Date().getTime();
		}
	},
	photo_page_init : function(){
		var $photo_edit_area = $("#photo_edit_area");
		$photo_edit_area.find("input[type=button]").button();

		var photo_uuid = $("#photo_uuid").val();
		photo_editor_functions.$editted_image = $("#editted_image");
		var $editted_image = photo_editor_functions.$editted_image;
		if (photo_uuid) {
			$editted_image.attr("src", photo_editor_functions.getPhotoPath(photo_uuid));
			$editted_image.css("width", "auto");
		}

		var editted_image = $editted_image[0];
		if(editted_image.complete) {
			var editted_image_width = editted_image.width;
			$editted_image.css("height", "auto");
			if (editted_image_width < 800) {
				$editted_image.css("width", "auto");
			} else {
				$editted_image.css("width", "800px");
			}
	    } else {
		    editted_image.onload = function(){
				var editted_image_width = editted_image.width;
				$editted_image.css("height", "auto");
				if (editted_image_width < 800) {
					$editted_image.css("width", "auto");
				} else {
					$editted_image.css("width", "800px");
				}
		    }
	    }


		$editted_image.Jcrop({
			dragEdges : true
		}, function() {
			jcrop_api = this;
		});

		$('#photo_upload_button').click(photo_editor_functions.upload);

		$("#image_crop_button").click(photo_editor_functions.crop);
		$("#photo_reset_button").click(photo_editor_functions.reset);
	},
	editImgFor : function($target, callback, dividing){
		var $photo_dialog = $("#photo_dialog");
		if (!$photo_dialog.length) {
			$("body").append("<div id='photo_dialog'></div>");
			$photo_dialog = $("#photo_dialog");
		}

		photo_editor_functions.target = $target;
		if (dividing) {
			photo_editor_functions.dividing = dividing;
		}
		var url = "widget.do?method=photoEditor";
		if (dividing) {
			url += "&dividing=" + dividing;
		}
		url += "&photo_uuid=" + $target.val();

		$photo_dialog.load(url , function(responseText, textStatus, XMLHttpRequest) {
			photo_editor_functions.photo_page_init();
			$photo_dialog.dialog({
				width:'1024px',
				title : "照片处理",
				show : "",
				resizable : false,
				modal : true,
				buttons :  {
					"关闭" : function() {
						$photo_dialog.dialog("close");
					}
				},
				close : function(){
					if (jcrop_api.destroy) {jcrop_api.destroy();}
					if (callback)
						callback();
				}
			});
		});
	},
	upload : function(){
		if ($('#photo_file').val()) {
			uploadPhoto("photo_file", function(resInfo) {
				var photoFilename = resInfo.photoFilename;
				if (jcrop_api.destroy) {jcrop_api.destroy();}

				photo_editor_functions.$editted_image.css("width", resInfo.oWidth); 
				photo_editor_functions.$editted_image.css("height", resInfo.oHeight); 
				photo_editor_functions.$editted_image.attr("src", photo_editor_functions.getPhotoPath(photoFilename));
				photo_editor_functions.$editted_image.Jcrop({
					dragEdges : true
				}, function() {
					jcrop_api = this;
				});
				photo_editor_functions.target.val(photoFilename);
				$("#photo_uuid").val(photoFilename);
			}, photo_editor_functions.dividing, $("#photo_uuid").val());
		}
	},
	crop : function(){
		var c = jcrop_api.tellSelect();
		var postData = {
			"dividing" : photo_editor_functions.dividing,
			"fileName" : $("#photo_uuid").val(),
			"startX" : c.x,
			"startY" : c.y,
			"height" : c.h,
			"width" : c.w,
			"showWidth" : jcrop_api.getBounds()[0]
		};

		postData.trueWidth = parseInt(photo_editor_functions.$editted_image.css("width", "auto").css("height", "auto").css("width"), 10);
		photo_editor_functions.$editted_image.css("width", postData.showWidth);

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : myPath + "?method=crop",
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj) {
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (jcrop_api.destroy) {jcrop_api.destroy();}

				photo_editor_functions.$editted_image.css("width", resInfo.oWidth); 
				photo_editor_functions.$editted_image.css("height", resInfo.oHeight); 
				photo_editor_functions.$editted_image.attr("src", photo_editor_functions.getPhotoPath($("#photo_uuid").val()));
				photo_editor_functions.$editted_image.Jcrop({
					dragEdges : true
				}, function() {
					jcrop_api = this;
				});
			}
		});
	},
	reset : function(){
		var postData = {
			"dividing" : photo_editor_functions.dividing,
			"fileName" : $("#photo_uuid").val()
		};

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : myPath + "?method=reset",
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj) {
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (jcrop_api.destroy) {jcrop_api.destroy();}

				photo_editor_functions.$editted_image.css("width", resInfo.oWidth); 
				photo_editor_functions.$editted_image.css("height", resInfo.oHeight); 
				photo_editor_functions.$editted_image.attr("src", photo_editor_functions.getPhotoPath($("#photo_uuid").val()));
				photo_editor_functions.$editted_image.Jcrop({
					dragEdges : true
				}, function() {
					jcrop_api = this;
				});
			}
		});
	}
}