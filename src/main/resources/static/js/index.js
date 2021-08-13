var template = "<li class=\"video-item matrix\"><a\r\n"
				+ "					href=\"/play/${id}\"\r\n"
				+ "					title=\"${title}\" target=\"_blank\" class=\"img-anchor\">\r\n"
				+ "					<div class=\"img\">\r\n"
				+ "						<div class=\"lazy-img\"><img alt=\"\"\r\n"
				+ "								src=\"/img/b49ded7cc53f624d8d7dccd438b71547345df91c.jpg@320w_200h_1c.webp\">\r\n"
				+ "						</div><span class=\"so-imgTag_rb\">18:08</span>\r\n"
				+ "						<div class=\"watch-later-trigger watch-later\"></div><span class=\"mask-video\"></span>\r\n"
				+ "						<div class=\"van-framepreview\"\r\n"
				+ "							style=\"background-position: -336px -840.5px; opacity: 0; background-image: url('/img/280207195.jpg@85q.webp'); background-size: 1680px;\">\r\n"
				+ "							<div class=\"van-fpbar-box\"><span style=\"width: 33%;\"></span></div>\r\n"
				+ "						</div>\r\n"
				+ "					</div>\r\n"
				+ "					<!---->\r\n"
				+ "				</a>\r\n"
				+ "				<div class=\"info\">\r\n"
				+ "					<div class=\"headline clearfix\">\r\n"
				+ "						<!---->\r\n"
				+ "						<!----><span class=\"type hide\">影视杂谈</span><a title=\"${title}\"\r\n"
				+ "							href=\"//www.bilibili.com/video/BV1D4411t7Ci?from=search&amp;seid=5093102736682967731\"\r\n"
				+ "							target=\"_blank\" class=\"title\">${title}</a></div>\r\n"
				+ "					<div class=\"des hide\">\r\n"
				+ "						第二案：蓝衫记av52590385\r\n"
				+ "						第三案：滴血雄鹰av55016928\r\n"
				+ "						第四案：关河疑影（上）av67322940\r\n"
				+ "						第四案：关河疑影（下）av68091734\r\n"
				+ "						第五案：蛇灵（上）av73834959\r\n"
				+ "						第五案：蛇灵（下）av75461235\r\n"
				+ "					</div>\r\n"
				+ "					<div class=\"tags\"><span title=\"观看\" class=\"so-icon watch-num\"><i class=\"icon-playtime\"></i>\r\n"
				+ "							278.6万\r\n"
				+ "						</span><span title=\"弹幕\" class=\"so-icon hide\"><i class=\"icon-subtitle\"></i>\r\n"
				+ "							1.6万\r\n"
				+ "						</span><span title=\"上传时间\" class=\"so-icon time\"><i class=\"icon-date\"></i>\r\n"
				+ "							2019-05-02\r\n"
				+ "						</span><span title=\"up主\" class=\"so-icon\"><i class=\"icon-uper\"></i><a\r\n"
				+ "								href=\"//space.bilibili.com/4408538?from=search&amp;seid=5093102736682967731\"\r\n"
				+ "								target=\"_blank\" class=\"up-name\">我是怪异君</a></span></div>\r\n"
				+ "				</div>\r\n"
				+ "			</li>";

$(document).ready(function () {
	console.log("hahah");
	$.ajax({
		url: "/list", async: false, success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var temp = template;
				var row = data[i];
				temp = temp.replace(/\$\{id\}/, row.id);
				temp = temp.replace(/\$\{title\}/g, row.name);
				$(".video-list").append($(temp));
			}
		}
	});
	$("div.img").bind("mousemove", (function (e) {
		updateBackground.call(this, e.offsetX);
	}));
	$("div.img").bind("mouseenter", (function (e) {
		if ($(this).find(".van-framepreview").length < 1) {
			var framePreview = document.createElement("div");
			var fpbarBox = document.createElement("div");
			var line = document.createElement("span");
			framePreview.className = "van-framepreview";
			fpbarBox.className = "van-fpbar-box";
			framePreview.style.backgroundPosition = "0 10px",
			fpbarBox.appendChild(line);
			framePreview.appendChild(fpbarBox);
			this.appendChild(framePreview);
		}
	}
	))
	function updateBackground(layerX) {
		var p = {
			img_x_len: 10,
			img_x_size: 160,
			img_y_len: 10,
			img_y_size: 90
		};
		var n = 278
			, r = this.offsetWidth
			, o = p.img_y_size / p.img_x_size * r
			, i = Math.floor(layerX / r * 100)
			, a = r * p.img_x_len
			, s = Math.floor(layerX / r * n)
			, c = -s % p.img_x_len * r
			, f = -Math.floor(s / p.img_x_len) * o + 10;
			var u = $(this).find(".van-framepreview")[0];
			var l = $(this).find(".van-fpbar-box span")[0];
			u.style.backgroundPosition = c + "px " + f + "px",
			u.style.backgroundSize = a + "px",
			l.style.width = i + "%"
	}
});

