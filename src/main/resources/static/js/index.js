const TEMPLATE = "<li class=\"video-item ${displayType}\"><a\r\n"
				+ "					href=\"/play/${id}\"\r\n"
				+ "					title=\"${title}\" target=\"_blank\" class=\"img-anchor\">\r\n"
				+ "					<div videoId=\"${id}\" class=\"img\">\r\n"
				+ "						<div class=\"lazy-img\"><img alt=\"\"\r\n"
				+ "								src=\"${screenshot}\">\r\n"
				+ "						</div><span class=\"so-imgTag_rb\">${time}</span>\r\n"
				+ "						<div class=\"watch-later-trigger watch-later\"></div><span class=\"mask-video\"></span>\r\n"
				+ "						<!-- <div class=\"van-framepreview\"\r\n"
				+ "							style=\"background-position: -336px -840.5px; opacity: 0; background-image: url('/img/280207195.jpg@85q.webp'); background-size: 1680px;\">\r\n"
				+ "							<div class=\"van-fpbar-box\"><span style=\"width: 33%;\"></span></div>\r\n"
				+ "						</div> -->\r\n"
				+ "					</div>\r\n"
				+ "					<!---->\r\n"
				+ "				</a>\r\n"
				+ "				<div class=\"info\">\r\n"
				+ "					<div class=\"headline clearfix\">\r\n"
				+ "						<!---->\r\n"
				+ "						<!----><span class=\"type hide\">R18</span><a title=\"${title}\"\r\n"
				+ "							href=\"/play/${id}\"\r\n"
				+ "							target=\"_blank\" class=\"title\">${heightLightTitle}</a></div>\r\n"
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
				+ "								target=\"_blank\" class=\"up-name\">戎晓栋官方</a></span></div>\r\n"
				+ "				</div>\r\n"
				+ "			</li>";
const BASE64Header = "data:image/png;base64,"
const PAGEITEM = "<li class=\"page-item\"><button class=\"pagination-btn\">${pageNum}</button></li>";
const LOADING = "<div class=\"flow-loader-state-loading\"><div class=\"load-state\"><span class=\"loading\">正在加载...</span></div></div>";

$(document).ready(function () {
	var store = {};
	init();
	store.searchingKeyword = "";
	$(".search-button").bind("click", function(e) {
		var value = $("#search-keyword").val();
		if (value != store.searchingKeyword) {
			$(".video-list").empty();
			$(".pages").empty();
			showLoading(true);
			$('html,body').animate({ scrollTop: 0 }, 50);
			store.searchingKeyword = value;
			if (value == "") {
				init();
			} else {
				$.ajax({
					url: "/search/" + value, async: true, success: function(data) {
						store.pageCount = data.pageCount;
						var resultItems = data.resultItems;
						buildPageBar(1, store.pageCount);
						renderPage(resultItems);
						store.isSearchResult = true;
					}
				});
			}
		}
	});
	function init() {
		$.ajax({
		url: "/list", async: true, success: function(data) {
			renderPage(data);
			store.isSearchResult = false;
		}
		});
		$.ajax({
			url: "/pageCount", async: true, success: function(data) {
				store.pageCount = data;
				buildPageBar(1, data);
				$(".pages").bind("click", toPage);
			}
		});
	}
	function renderPage(data) {
		store.videoItems = store.videoItems || {};
		$(".video-list").empty();
		showLoading(false);
		for (var i = 0; i < data.length; i++) {
			var temp = TEMPLATE;
			var row = data[i];
			store.videoItems[row.id] = row;
			temp = temp.replace(/\$\{id\}/g, row.id);
			temp = temp.replace(/\$\{time\}/g, utils.formatSeconds(parseInt(row.duration/ 1000000)));
			temp = temp.replace(/\$\{screenshot\}/g, BASE64Header + row.screenShot);
			var heightLightHtml = getHeightLightHtml(row.name);
			temp = temp.replace(/\$\{heightLightTitle\}/g, heightLightHtml);
			temp = temp.replace(/\$\{title\}/g, row.name);
			temp = temp.replace(/\$\{displayType\}/g, utils.isMobile() ? "list" : "matrix");
			$(".video-list").append($(temp));
		}
		bindListeners();
	}
	function getHeightLightHtml(name) {
		if (store.searchingKeyword == "") {
			return name;
		}
		var result = "";
		var upperkeyword = store.searchingKeyword.toUpperCase();
		var upperName = name.toUpperCase();
		var startIndex = 0;
		var endIndex = upperName.indexOf(upperkeyword, startIndex);
		while (endIndex > -1) {
			result += name.substring(startIndex, endIndex);
			result += ("<em class=\"keyword\">" + name.substring(endIndex, endIndex + store.searchingKeyword.length) + "</em>");
			startIndex += (endIndex + upperkeyword.length);
			endIndex = upperName.indexOf(upperkeyword, startIndex);
		}
		result += name.substring(startIndex, name.length);
		return result;
	}
	function bindListeners() {
		$("div.img").bind("mousemove", (function (e) {
			updateBackground.call(this, e.offsetX);
		}));
		$("div.img").bind("mouseenter", (function (e) {
			this.leaved = false;
			var that = this;
			setTimeout(function() {
				var framePreview = null;
				if ($(that).find(".van-framepreview").length < 1) {
					framePreview = document.createElement("div");
					var fpbarBox = document.createElement("div");
					var line = document.createElement("span");
					framePreview.className = "van-framepreview";
					fpbarBox.className = "van-fpbar-box";
					framePreview.style.backgroundPosition = "0 10px",
					framePreview.style.backgroundImage = "url('/preview/"+ $(that).attr("videoId") +"')";
					fpbarBox.appendChild(line);
					framePreview.appendChild(fpbarBox);
					that.appendChild(framePreview);
				} else {
					framePreview = $(that).find(".van-framepreview")[0];
				}
				updateBackground.call(that, 0);
				if (that.leaved) {
					framePreview.style.opacity = 0;
				} else {
					framePreview.style.opacity = 1;
				}
			}, 350);
		}
		));
		$("div.img").bind("mouseleave", (function (e) {
			var framePreview = $(this).find(".van-framepreview")[0];
			this.leaved = true;
			if (framePreview) {
				framePreview.style.opacity = 0;
			}
		}
	));
	}
	function updateBackground(layerX) {
		if (layerX < 0) {
			layerX = 0;
		}
		// if (layerX >= this.offsetWidth) {
		// 	layerX = this.offsetWidth - 1;
		// }
		var p = {
			img_x_len: 10,
			img_x_size: 160,
			img_y_len: 10,
			img_y_size: 90
		};
		var videoId = $(this).attr("videoId");
		var time = parseInt(store.videoItems[videoId].duration / 1000000);
		var n = Math.floor(parseInt(time / 5)) < 99 ? Math.floor(parseInt(time / 5)) : 99
			, r = this.offsetWidth
			, o = p.img_y_size / p.img_x_size * r
			, i = Math.floor(layerX / r * 100)
			, a = r * p.img_x_len
			, s = Math.floor(layerX / r * n)
			, c = -s % p.img_x_len * r
			, f = -Math.floor(s / p.img_x_len) * o + 10;
			var u = $(this).find(".van-framepreview")[0];
			var l = $(this).find(".van-fpbar-box span")[0];
			if (u) {
				u.style.backgroundPosition = c + "px " + f + "px",
				u.style.backgroundSize = a + "px",
				l.style.width = i + "%"
			}
	}
	function buildPageBar(currentPage, totalPage) {
		if (currentPage != 1) {
			var pageItem = createControlPageItem("上一页");
			$(".pages").append(pageItem);
		}
		// 正常情况下，显示1和最后页，中间7页，总共9页
		var times = Math.min(9, totalPage);
		var strongType = 0;
		var centerStartPos = currentPage - 3;
		var centerEndPos = currentPage + 3;
		// 存在当前页非居中的情况
		if (centerStartPos < 1 || centerEndPos > totalPage) {
			if (centerStartPos < 1) {
				// 偏向开头
				centerStartPos = 1;
				centerEndPos = 7;
				if (centerEndPos > totalPage - 2) {
					// 大于totalPage - 2,无论是totalPage还是totalPage - 1都是与totalPage相连，所以开始位置置为【totalPage】
					centerEndPos = totalPage;
				} else {
					// 小于等于totalPage - 2，中间需要【...】
					strongType |= 2;
				}
			} else {
				// 偏向结尾
				centerStartPos = totalPage - 6;
				centerEndPos = totalPage;
				if (centerStartPos < 3) {
					// 小于3，无论【2】或者【1】都是与【1】相连了，所以开始位置置为【1】
					centerStartPos = 1;
				} else {
					// 大于等于3，中间需要【...】
					strongType |= 1;
				}
			}
			// 第一页或最后一页被包含在中间7页中，所以只有8页了
			times = Math.min(8, times);
		} else {
			if (centerStartPos > 2) {
				strongType |= 1;
			} 
			if (centerEndPos < totalPage - 1) {
				strongType |= 2;
			}
		}
		for (var i = 1, proIdx = 1; i <= times && proIdx <= totalPage; i++, proIdx++) {
			var pageItem = createNumPageItem(proIdx);
			$(".pages").append(pageItem);
			if (proIdx == 1) {
				pageItem.addClass("first");
				if ((strongType & 1) != 0) {
					var strong = $("<strong>...</strong>");
					$(".pages").append(strong);
					proIdx = centerStartPos - 1;
				}
			} else if (proIdx == centerEndPos) {
				if ((strongType & 2) != 0) {
					var strong = $("<strong>...</strong>");
					$(".pages").append(strong);
					proIdx = totalPage - 1;
				}
			}
			if (currentPage == proIdx) {
				pageItem.addClass("active");
			}
			if (proIdx == totalPage) {
				pageItem.addClass("last");	
			}
		}
		if (currentPage != totalPage) {
			var pageItem = createControlPageItem("下一页");
			$(".pages").append(pageItem);
		}
	}
	function updatePageBar(currentIndex) {
		buildPageBar(currentIndex, store.pageCount);
	}
	function createNumPageItem(num) {
		var pageItemStr = PAGEITEM;
		pageItemStr = pageItemStr.replace(/\$\{pageNum\}/g, num);
		var pageItem = $(pageItemStr);
		pageItem.find("button").addClass("num-btn");
		pageItem.find("button").addClass("pagination-btn");
		pageItem.attr("pageIndex", num);
		return pageItem;
	}
	function createControlPageItem(text) {
		var pageItemStr = PAGEITEM;
		pageItemStr = pageItemStr.replace(/\$\{pageNum\}/g, text);
		var pageItem = $(pageItemStr);
		pageItem.find("button").addClass("nav-btn");
		pageItem.find("button").addClass("iconfont");
		if (text == "上一页") {
			pageItem.addClass("prev");
			pageItem.find("button").addClass("icon-arrowdown2");
		} else if (text == "下一页") {
			pageItem.addClass("next");
			pageItem.find("button").addClass("icon-arrowdown3");
		}
		return pageItem;
	}
	function toPage(e) {
		var target = e.target
		var to = 0;
		if ($(target).hasClass("num-btn")) {
			to = parseInt($(target).text());
		} else if ($(target).hasClass("nav-btn")) {
			if ($(target).parent().hasClass("prev")) {
				to = parseInt($.trim($(".pages").find(".active").find("button")[0].innerHTML)) - 1;
			} else if ($(target).parent().hasClass("next")) {
				to = parseInt($.trim($(".pages").find(".active").find("button")[0].innerHTML)) + 1;
			}
		}
		if (to == 0) {
			return;
		}
		$(".video-list").empty();
		$(".pages").empty();
		showLoading(true);
		$('html,body').animate({ scrollTop: 0 }, 50);
		var url = "/list/";
		if (store.isSearchResult) {
			url = "/search/" + store.searchingKeyword + "/";
		}
		$.ajax({
			url: url + to, async: true, success: function(data) {
				var resultItems = data;
				if (data.resultItems) {
					resultItems = data.resultItems;
				}
				renderPage(resultItems);
				updatePageBar(to);
			}
		});
	}
	function showLoading(show) {
		if (show) {
		var loading = LOADING;
			$(".flow-loader-state").append($(loading));
		} else {
			$(".flow-loader-state").empty();
		}
	}
});

