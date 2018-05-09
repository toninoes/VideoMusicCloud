function visitsComentario() {
	if(document.getElementById("visitComentario") == null)
		document.getElementById("visitComentario").innerText =  1;
	else	
		document.getElementById("visitComentario").innerText =  1 + parseInt(document.getElementById("visitComentario").innerText);
}

function visitsPerfil() {
	if(document.getElementById("visitPerfil") == null)
		document.getElementById("visitPerfil").innerText =  1;
	else
		document.getElementById("visitPerfil").innerText =  1 + parseInt(document.getElementById("visitPerfil").innerText);
}

function visitsInicio() {
	if(document.getElementById("visitInicio") == null)
		document.getElementById("visitInicio").innerText =  1;
	else
		document.getElementById("visitInicio").innerText =  1 + parseInt(document.getElementById("visitInicio").innerText);
}

function visitsListas() {
	if(document.getElementById("visitListas") == null)
		document.getElementById("visitListas").innerText =  1;
	else
		document.getElementById("visitListas").innerText =  1 + parseInt(document.getElementById("visitListas").innerText);
}

function viewsComentario(nodo) {
	nodo.parentNode.submit();
}

function viewsPerfil(nodo) {
	nodo.parentNode.submit();
}

function viewsInicio(nodo) {
	nodo.parentNode.submit();
}

function viewsListas(nodo) {
	nodo.parentNode.submit();
}