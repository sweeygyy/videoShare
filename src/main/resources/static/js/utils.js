var utils = {
	formatSeconds: function (value) {
		var theTime = parseInt(value);// 秒
		var middle = 0;// 分
		var hour = 0;// 小时

		if (theTime > 60) {
			middle = parseInt(theTime / 60);
			theTime = parseInt(theTime % 60);
			if (middle > 60) {
				hour = parseInt(middle / 60);
				middle = parseInt(middle % 60);
			}
		}
		var result = (theTime < 10 ? "0" : "") + parseInt(theTime);
		if (middle > 0 || hour > 0) {
			result = (middle < 10 ? "0" : "") + parseInt(middle) + ":" + result;
		}
		if (hour > 0) {
			result = (hour < 10 ? "0" : "") + parseInt(hour) + ":" + result;
		}
		return result;
	},

	isMobile: function () {
		var ua = navigator.userAgent;
		return ua.toLowerCase().match(/(ipod|iphone|android|coolpad|mmp|smartphone|midp|wap|xoom|symbian|j2me|blackberry|wince)/i) != null;
	},
}