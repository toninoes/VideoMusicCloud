function likes(logueadoId, videoId) {
	
	var comentario;
	
	$.ajax({
		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/api/comentarios/" + logueadoId + "/" + videoId,
		type: "GET",
		contentType: "text",
		async: false,
	}).done(function(textStatus, jqXHR) {
		if(textStatus == "true")
			comentario = true;
		else
			comentario = false;
	}).fail(function(jqXHR, textStatus, errorThrown){
	});
	
	$.ajax({
		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/api/comentarios/" + logueadoId + "/" + comentario + "/" + videoId,
		type: "GET",
		contentType: "text",
	}).done(function(textStatus, jqXHR) {
		document.getElementById(textStatus[2]).innerHTML = textStatus[0];
		if(comentario) {
			var id = "#t" + textStatus[2];
			$(id).removeClass().addClass("stat-icon icon-like-b");
			id = "#f" + textStatus[2];
			$(id).removeClass();
		} else {
			var id = "#f" + textStatus[2];
			$(id).removeClass().addClass("stat-icon icon-like");
			id = "#t" + textStatus[2];
			$(id).removeClass();
		}
	}).fail(function(jqXHR, textStatus, errorThrown){
	});
}