function changeColor(event, color) {
	event.target.dropEffect = 'move';
	event.preventDefault();
	document.getElementById("div1").style = "border:5px dashed " + color;
}

function goplay() {
	document.getElementById('thumb').play();
}

$(function() {
	$('#div1 input').on('change', function(e) {
		e.target.dropEffect = 'move';
		$(e.target).parent().removeClass('hover');
		$(e.target).parent().addClass('filled');
		changeColor(e, 'green');
		var reader = null;
        var files = e.target.files;
        if(files[0].type.match('video*')) {
        	reader = new FileReader();
        	reader.onload = (function() {
        		return function(evt) {
        			document.getElementById("list").innerHTML = '<video id="thumb" controls>' +
        														'<source src="'+evt.target.result+'"></video>';
        			document.getElementById("previo").innerHTML = '<a id="btnPlay" class="btn btn-primary" value="Previsualizar" onclick="goplay()">Reproducir</a>';
        		};
        	})(files[0]);
        	reader.readAsDataURL(files[0]);
        }
	});
	$('#div1 input').on('dragover', function(e) {
		e.target.dropEffect = 'move';
		$(e.target).parent().addClass('hover');
		changeColor(e, 'yellow');
	});
	$('#div1 input').on('dragleave', function(e) {
		e.target.dropEffect = 'move';
		$(e.target).parent().removeClass('hover');
		changeColor(e, 'white');
	});
});