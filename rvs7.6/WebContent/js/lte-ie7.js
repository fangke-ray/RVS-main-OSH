/* Use this script if you need to support IE 7 and IE 6. */

window.onload = function() {
	function addIcon(el, entity) {
		var html = el.innerHTML;
		el.innerHTML = '<span style="font-family: \'icomoon\'">' + entity + '</span>' + html;
	}
	var icons = {
			'icon-home' : '&#x21;',
			'icon-cloud-upload' : '&#x22;',
			'icon-mic' : '&#x23;',
			'icon-cog' : '&#x24;',
			'icon-star' : '&#x25;',
			'icon-heart' : '&#x26;',
			'icon-box-add' : '&#x27;',
			'icon-box-remove' : '&#x28;',
			'icon-comments' : '&#x29;',
			'icon-comments-2' : '&#x2a;',
			'icon-plus' : '&#x2b;',
			'icon-remove' : '&#x2c;',
			'icon-minus' : '&#x2d;',
			'icon-thumbs-up' : '&#x2e;',
			'icon-thumbs-down' : '&#x2f;',
			'icon-cancel' : '&#x30;',
			'icon-checkmark' : '&#x31;',
			'icon-arrow-right' : '&#x32;',
			'icon-enter' : '&#x33;',
			'icon-feed' : '&#x34;',
			'icon-safari' : '&#x35;',
			'icon-IE' : '&#x36;',
			'icon-warning' : '&#x37;',
			'icon-help' : '&#x38;',
			'icon-printer' : '&#x39;',
			'icon-bell' : '&#x3a;',
			'icon-coffee' : '&#x3b;',
			'icon-tag-stroke' : '&#x3d;',
			'icon-tag-fill' : '&#x3e;',
			'icon-file-excel' : '&#x3f;',
			'icon-file-pdf' : '&#x40;',
			'icon-chart' : '&#x41;',
			'icon-calendar' : '&#x42;',
			'icon-clock' : '&#x43;',
			'icon-switch' : '&#x44;',
			'icon-enter-2' : '&#x45;',
			'icon-exit' : '&#x46;',
			'icon-calendar-2' : '&#x47;',
			'icon-bars' : '&#x48;',
			'icon-stats-up' : '&#x49;',
			'icon-upload' : '&#x4a;',
			'icon-list' : '&#x4b;',
			'icon-share' : '&#x4c;',
			'icon-cart' : '&#x3c;'
		},
		els = document.getElementsByTagName('*'),
		i, attr, html, c, el;
	for (i = 0; i < els.length; i += 1) {
		el = els[i];
		attr = el.getAttribute('data-icon');
		if (attr) {
			addIcon(el, attr);
		}
		c = el.className;
		c = c.match(/icon-[^\s'"]+/);
		if (c) {
			addIcon(el, icons[c[0]]);
		}
	}
};