function visualizaciones(videoId, videoVisualizaciones) {
	var id = "visitInicio" + videoId;
	document.getElementById(id).innerText =  1 + parseInt(document.getElementById(id).innerText);
	$.ajax({
		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/api/videos/" + videoId + "/" + videoVisualizaciones,
		type: "GET",
	}).done(function(textStatus, jqXHR) {
		var id = "#i" + videoId;
		$(id).removeClass().addClass("stat-icon icon-views-b");
	}).fail(function(jqXHR, textStatus, errorThrown){
	});
}