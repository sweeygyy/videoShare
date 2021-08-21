$(document).ready(function () {
	var video = document.getElementById("player");
	if (utils.isMobile()) {
		$("#bilibiliPlayer").find(".bilibili-player-area").removeClass("video-state-pause");
	}
	if (utils.isFireFox()) {
		$(".player-poster").css("display", "none");
	}
	video.addEventListener('play', function(e) {
	  $(".player-poster").css("opacity", 0);
	  $("#bilibiliPlayer").find(".bilibili-player-area").removeClass("video-state-pause");
	});
	video.addEventListener('pause', function(e) {
		if (!utils.isMobile()) {
			$("#bilibiliPlayer").find(".bilibili-player-area").addClass("video-state-pause");
		}
    })
})