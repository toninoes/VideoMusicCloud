function comprobar(padre, hijo) {
	var elementos = document.getElementsByName(hijo);
	var i = elementos.length;
	i--;
	if(document.getElementById(padre).checked == true)
		while(i >= 0) {
			document.getElementsByName(hijo)[i].checked = true;
			i--;
		}
	else
		while(i >= 0) {
			document.getElementsByName(hijo)[i].checked = false;
			i--;
		}
}

function searchUsers() {
	var busqueda = document.getElementById("search").value;
	document.getElementById("hiperlink").href = "/admin/home/" + busqueda;
}

function searchUsers2() {
	document.getElementById("hiperlink2").href = "/admin/home/";
}