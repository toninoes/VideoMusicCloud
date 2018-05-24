function eliminar(logueadoId, videoId, portal) {
	var result = confirm('¿Estás seguro?');
	if(!result) {
		return false;
    } else {
    	$.ajax({
    		url: window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/usuarios/perfil/perfilog/delete/" + logueadoId + "/" + videoId + "/borrar/" + portal,
    		type: "GET",
    	}).done(function(textStatus, jqXHR) {
    		if(portal == 'portaladmin')
    			window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/admin/home";
    		else
    			window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + location.port + "/videos/misvideos";
    	}).fail(function(jqXHR, textStatus, errorThrown){
    	});
    }
} 