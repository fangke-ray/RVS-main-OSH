/**
 * .select2Buttons - Convert standard html select into button like elements
 *
 * Version: 1.0.1
 * Updated: 2011-04-14
 *
 *  Provides an alternative look and feel for HTML select buttons, inspired by threadless.com
 *
 * Author: Sam Cavenagh (cavenaghweb@hotmail.com)
 * Doco and Source: https://github.com/o-sam-o/jquery.select2Buttons
 *
 * Licensed under the MIT
 **/
 
var select2Buttons_methods = {
	setDisplay : function(options) {
		var select = $(this);
		var ulHtml = select.next(".select2Buttons").children("ul.select-buttons");
		if (options.enable) {
			for (var iV in options.enable) {
				var option = select.find("option:eq(" + options.enable[iV] + ")");
				if (option.attr("disabled")) {
					select.find("option:eq(" + options.enable[iV] + ")").enable();
					var li = ulHtml.find("li:eq(" + options.enable[iV] + ")");
					var liView = li.children();

					li.removeClass('disabled');
					var selectIndex = liView.attr("data-select-index");
					var liText = liView.text();
					li.html('<a href="#" data-select-index="' + selectIndex + '">' + liText + '</a>');
				}
			}
		}
		if (options.disable) {
			for (var iV in options.disable) {
				var option = select.find("option:eq(" + options.disable[iV] + ")");
				if (!option.attr("disabled")) {
					select.find("option:eq(" + options.disable[iV] + ")").disable();
					var li = ulHtml.find("li:eq(" + options.disable[iV] + ")");
					var liView = li.children();

					li.addClass('disabled');
					var selectIndex = liView.attr("data-select-index");
					var liText = liView.text();
					li.html('<span data-select-index="' + selectIndex + '">' + liText + '</span>');
				}
			}
		}
		if (options.visible) {
			ulHtml.children("li").hide();
			for (var iV in options.visible) {
				ulHtml.children("li:eq(" + options.visible[iV] + ")").show();
			}
		}
	}
};

jQuery.fn.select2Buttons = function(method, options) {
if (typeof(method) === "string") {
    if (select2Buttons_methods[method]) {
   		return select2Buttons_methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
    }
} else
return this.each(function(){
	var $ = jQuery;
	var select = $(this);
	var _multiselect = select.attr('multiple');

	var formerCheckStatus = false;
	var groupSwitchTO = null;

	select.hide();

	var groupsHtml = select.next(".select2Groups");
	var buttonsHtml;
	if (groupsHtml.length) {
		buttonsHtml = groupsHtml.next(".select2Buttons");
		groupsHtml.remove();
	} else {
        buttonsHtml = select.next(".select2Buttons");
    }
    if (buttonsHtml.length > 0) {
    	buttonsHtml.html("");
    } else {
    	buttonsHtml = $('<div class="select2Buttons" id="s2b_' + select.attr("id") + '"></div>');
    	select.after(buttonsHtml);
    }
    var selectIndex = 0;
	var addOptGroup = function(optGroup) {
		var ulHtml = $('<ul class="select-buttons">');
		if (optGroup.attr('label')) {
			select.next(".select2Groups").find(".select-buttons").append('<li><a>' + optGroup.attr('label') + '<span class="ui-icon ui-icon-circle-triangle-s"></span></a></li>');
			ulHtml = $('<ul class="select-buttons" uid="'+ optGroup.attr('label') +'">');
		}

		var optionsize = optGroup.children('option').length;
		optGroup.children('option').each(function(inde) {
			var liHtml = $('<li></li>');

			var liText = $(this).html();
			if ("" == liText.trim()) {
				liText = "(不选)";
				if(_multiselect){
					liText = "(全选/全清)";
					$(this).removeAttr("selected");
				}
			}
			if ($(this).attr('disabled') || select.attr('disabled')) {
				liHtml.addClass('disabled');
				liHtml.append('<span data-select-index="' + selectIndex + '">' + liText + '</span>');
			} else {
				liHtml.append('<a href="#" data-select-index="' + selectIndex + '">' + liText + '</a>');
				if (inde === 0) {
					liHtml.find("a").addClass("ui-corner-left");
				}
				if (inde === (optionsize - 1)) {
					liHtml.find("a").addClass("ui-corner-right");
				}
			}

			// Mark current selection as "picked"
			if ((!options || !options.noDefault) && $(this).attr('selected')) {
				liHtml.children('a, span').addClass('picked');
			}
			ulHtml.append(liHtml);
			selectIndex++;
		});
		buttonsHtml.append(ulHtml);
	}

	var optGroups = select.children('optgroup');
	if (optGroups.length == 0) {
		addOptGroup(select);
	} else {
		groupsHtml = $("<div class='select2Groups'><ul class='select-buttons'></ul></div>");
		buttonsHtml.before(groupsHtml);
		optGroups.each(function() {
			addOptGroup($(this));
		});
		var groups = select.next(".select2Groups").find(".select-buttons li");
		var groupsize = groups.length;
		groups.each(function(inde,item) {
			if (inde === 0) {
				$(item).find("a").addClass("ui-corner-tl");
			}
			if (inde === (groupsize - 1)) {
				$(item).find("a").addClass("ui-corner-tr");
			}
			$(item).mouseover(function() {
				if (groupSwitchTO) clearTimeout(groupSwitchTO);
				groupSwitchTO = setTimeout(function(groupText){
					buttonsHtml.find("ul[uid]").hide();
					buttonsHtml.find("ul[uid='"+groupText+"']").show();
					groupSwitchTO = null;
				}, 200, $(this).text());
			});
		});
		buttonsHtml.find("ul[uid]").hide();
		buttonsHtml.find("ul[uid]:eq(0)").show();
		buttonsHtml.bind("mouseover", function(){
			if (groupSwitchTO) clearTimeout(groupSwitchTO);
		})

		select.parent().bind("mouseleave", function(){
			groups.filter(function(){return $(this).find("a.picked").length > 0}).trigger("mouseover");
		}) 
	};

	buttonsHtml.find(".select-buttons").on("click", "a" , function(e){
      e.preventDefault();
      var allOptions = $(select.find('option'));
      var optionAcher = $(this);
      var clickedOption = $(select.find('option')[optionAcher.attr('data-select-index')]);
      if(_multiselect){
        if(clickedOption.attr('selected')){
          optionAcher.removeClass('picked');
          clickedOption.removeAttr('selected');
        }else{
          optionAcher.addClass('picked');
          clickedOption.attr('selected', 'selected');
        }
      }else{
      	allOptions.removeAttr('selected');
        buttonsHtml.find('a, span').removeClass('picked');
        optionAcher.addClass('picked');
        clickedOption.attr('selected', 'selected');
      }

      if (optGroups.length > 0) {
      	groupsHtml.find("a").removeClass("picked");
      	var groupName = optionAcher.parents("ul").attr("uid");
      	select.next(".select2Groups").find("a").filter(function(){
			return (this.innerText == groupName || this.textContent == groupName);
		}).addClass('picked');
      }
      select.trigger('change', true);
    })
    .on("dragstart", "a" , function(e){
    	return false;
    });

    select.change(function(e, _inner){

		groupsHtml.find("a.picked").removeClass("picked");
		buttonsHtml.find('a.picked').removeClass('picked');
 
 		if (_multiselect) {
			var selectvals = select.val();

			var _clear = false;
			if (selectvals == "" && !_inner) _clear = true;
			if (_clear) formerCheckStatus = true;

			for (var selectedindex in selectvals) {
				var selectval = selectvals[selectedindex];
				if (selectval == "") {
					if (selectvals == "" && !_clear) { // 全选
						if(!formerCheckStatus) {
							var $empOpt = select.find("option[value=]");
							$empOpt.removeAttr("selected");
							select.find("option[value!=]").attr("selected", true).addClass("selected");
							buttonsHtml.find('a').addClass('picked');
							var sindex = $empOpt[0].index;
							buttonsHtml.find('a[data-select-index="'+ sindex+ '"]').removeClass('picked');
							formerCheckStatus = true;
						}
					} else { // 全不选
						if (formerCheckStatus) {
							select.val("");
							select.find("option[value=]").removeAttr("selected");
							buttonsHtml.find('a').removeClass('picked');
							formerCheckStatus = false;
						} else {
							continue;
						}
					}
					e.preventDefault();
					return;
				}

				var sindex = select.find("option[value="+selectval+"]")[0].index;
				buttonsHtml.find('a[data-select-index="'+ sindex+ '"]').addClass('picked');
				formerCheckStatus = true;
			}
		} else {
			var sindex = select[0].selectedIndex;
			buttonsHtml.find('a[data-select-index="'+ sindex+ '"]').addClass('picked');
			if (optGroups.length > 0) {
				var groupName = buttonsHtml.find('a[data-select-index="'+ sindex+ '"]').parents("ul").attr("uid");
				select.next(".select2Groups").find("a").filter(function(){
					return (this.innerText == groupName || this.textContent == groupName);
				}).trigger("mouseover").addClass('picked');
			}
		}
    });

    if (!formerCheckStatus && select.val()) formerCheckStatus = true;
  });
}
