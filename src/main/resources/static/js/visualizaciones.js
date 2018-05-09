function visitsComentario() {
	document.getElementById("visitComentario").innerText =  1 + parseInt(document.getElementById("visitComentario").innerText);
}

function visitsPerfil() {
	document.getElementById("visitPerfil").innerText =  1 + parseInt(document.getElementById("visitPerfil").innerText);
}

function visitsInicio() {
	document.getElementById("visitInicio").innerText =  1 + parseInt(document.getElementById("visitInicio").innerText);
}

function visitsListas() {
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