package vmc.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.service.ComentarioService;
import vmc.service.UsuarioService;
import vmc.service.VideoService;

@Controller
@RequestMapping("/comentarios")
public class ComentarioWebController {
	
	@Autowired
	private ComentarioService comentarioService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoService videoService;
	
	@GetMapping("/comentarioVideos/{logueadoId}/{pinchadoId}/{videoId}")
	public String findComentarios(Model model, @PathVariable long logueadoId,
										       @PathVariable long pinchadoId,
										       @PathVariable long videoId) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideo(video);
		Comentario gusta = comentarioService.gusta(video, logueado);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentarios);
		if(gusta != null)
			model.addAttribute("gusta", gusta.isGusta());
		else
			model.addAttribute("gusta", false);
		
		return "comentarios/comentarioVideos";
	}
	
	@GetMapping("/comentarioVideos/{logueadoId}/{pinchadoId}/{videoId}/{gusta}")
	public String findGustas(Model model, @PathVariable long logueadoId,
							  	          @PathVariable long pinchadoId,
										  @PathVariable long videoId,
										  @PathVariable boolean gusta) {
		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideoUsuario(video, logueado);
		
		if(!comentarios.isEmpty())
			comentarioService.saveGusta(comentarios, !gusta, video);
		
		List<Comentario> comentariosAllVideo = comentarioService.findComentariosByVideo(video);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentariosAllVideo);
		if(comentariosAllVideo.isEmpty())
			model.addAttribute("gusta", false);
		else
			model.addAttribute("gusta", !gusta);
		
		return "comentarios/comentarioVideos";
	}
	
	@PostMapping("/comentarioVideos/{logueadoId}/{pinchadoId}/{videoId}/{gusta}")
	public String saveComment(Model model, @RequestParam("comentario") String comentario,
										   @PathVariable long logueadoId,
										   @PathVariable long pinchadoId,
										   @PathVariable long videoId,
										   @PathVariable boolean gusta) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		comentarioService.create(video, logueado, comentario);
	
		List<Comentario> comentariosAllVideo = comentarioService.findComentariosByVideo(video);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("comentarios", comentariosAllVideo);
		if(comentariosAllVideo.isEmpty())
			model.addAttribute("gusta", gusta);
		else
			model.addAttribute("gusta", !gusta);
		
		return "comentarios/comentarioVideos";
	}
	
}