package vmc.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.service.ComentarioService;
import vmc.service.UsuarioService;
import vmc.service.VideoService;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioRestController {
	
	@Autowired
	private ComentarioService comentarioService;
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/{logueadoId}/{comentario}/{videoId}")
	public String[] meGusta(@PathVariable long logueadoId, @PathVariable boolean comentario, @PathVariable long videoId) {
		
		Comentario c;
		Video video = videoService.findById(videoId);
		Usuario usuario = usuarioService.findById(logueadoId);
		List<Comentario> lc = comentarioService.findByVideoIdUsuarioId(video, usuario);
		if(lc.isEmpty())
			c = comentarioService.create(logueadoId, videoId);
		else
			c = comentarioService.update(comentario, logueadoId, videoId);
				
		String[] s = new String[3];
		s[0] = String.valueOf(video.getLikes());
		s[1] = String.valueOf(c.isGusta());
		s[2] = String.valueOf(video.getId());
		
		return s;
	}
	
	@GetMapping("/{logueadoId}/{videoId}")
	public String gusta(@PathVariable long logueadoId, @PathVariable long videoId) {
		
		Video video = videoService.findById(videoId);
		Usuario usuario = usuarioService.findById(logueadoId);
		Comentario c = comentarioService.findByVideoUsuario(video, usuario);
		boolean bComentario;
		if(c == null)
			bComentario = true;
		else
			bComentario = !c.isGusta();
		
		return String.valueOf(""+bComentario+"");
	}
}
