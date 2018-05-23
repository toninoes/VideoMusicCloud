function eliminar(logueadoId, videoId) {
	var result = confirm('¿Estás seguro?');
	if(!result) {
		return false;
    } else {
    	$.ajax({
    		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/usuarios/perfil/perfilog/delete/" + logueadoId + "/" + videoId + "/erase",
    		type: "GET",
    	}).done(function(textStatus, jqXHR) {
    		window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/videos/misvideos";
    	}).fail(function(jqXHR, textStatus, errorThrown){
    	});
    }
} 