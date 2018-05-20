function eliminar(videoId) {
	var result = confirm('¿Estás seguro?');
	if(!result) {
		return false;
    } else {
    	$.ajax({
    		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/usuarios/perfil/" + videoId,
    		type: "GET",
    	}).done(function(textStatus, jqXHR) {
    		window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/usuarios/perfil"; 
    	}).fail(function(jqXHR, textStatus, errorThrown){
    	});
    }
} 