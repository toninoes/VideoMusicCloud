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
	
	@GetMapping("/{logueadoId}/{pinchadoId}/{videoId}")
	public String findView(Model model, @PathVariable long logueadoId,
										@PathVariable long pinchadoId,
										@PathVariable long videoId) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideo(video);
		Comentario comentario = null;
		comentario = comentarioService.findByVideoUsuario(video, logueado);
		if(comentario != null)
			model.addAttribute("gusta", true);
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentarios);
		model.addAttribute("mailadmin", admin.getMail());
		
		return "comentarios/comentarioVideos";
	}
	
	@GetMapping("/{logueadoId}/{pinchadoId}/{videoId}/{comentario}")
	public String findComentarios(Model model, @PathVariable long logueadoId,
										       @PathVariable long pinchadoId,
										       @PathVariable long videoId,
										       @PathVariable boolean comentario) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideo(video);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentarios);
		model.addAttribute("comentario", !comentario);
		
		return "redirect:/comentarios/{logueadoId}/{pinchadoId}/{videoId}";
	}
	
	// Solucionado el problema de actualizar el navegador ya no suma mas likes, tampoco ni resta
	@GetMapping("/{logueadoId}/{pinchadoId}/{videoId}/{comentario}/{vista}")
	public String findComentarios(Model model, @PathVariable long logueadoId,
										       @PathVariable long pinchadoId,
										       @PathVariable long videoId,
										       @PathVariable boolean comentario,
										       @PathVariable String vista) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideo(video);
		
		if(!comentario)
			comentarioService.create(video, logueado, "", "gusta");
		else
			comentarioService.delete(video, logueado);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentarios);
		model.addAttribute("comentario", comentario);
		
		switch(vista) {
			case "inicio" : return "redirect:/videos/misvideos";
			case "listas" : return "redirect:/videos";
			case "perfil" : return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}";
			default : return "redirect:/comentarios/{logueadoId}/{pinchadoId}/{videoId}/{comentario}";
		}
	}
	
	@PostMapping("/comentarioVideos/{logueadoId}/{pinchadoId}/{videoId}")
	public String saveComment(Model model, @RequestParam("descripcion") String descripcion,
										   @PathVariable long logueadoId,
										   @PathVariable long pinchadoId,
										   @PathVariable long videoId) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		comentarioService.create(video, logueado, descripcion, "comentario");
	
		List<Comentario> comentariosAllVideo = comentarioService.findComentariosByVideo(video);
		Comentario comentario = null;
		comentario = comentarioService.findByVideoUsuario(video, logueado);
		if(comentario != null)
			model.addAttribute("gusta", true);
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("comentarios", comentariosAllVideo);
		
		return "comentarios/comentarioVideos";
	}
	
	@PostMapping("/{logueadoId}/{pinchadoId}/{videoId}/{comentario}/{vista}/{views}")
	public String saveVisit(Model model, @PathVariable long logueadoId,
										 @PathVariable long pinchadoId,
										 @PathVariable long videoId,
										 @PathVariable boolean comentario,
									     @PathVariable String vista,
									     @PathVariable long views) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		videoService.saveVisit(views + 1, video);
		
		Comentario coment = null;
		coment = comentarioService.findByVideoUsuario(video, logueado);
		if(coment != null)
			model.addAttribute("gusta", true);
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		
		return "comentarios/comentarioVideos";
	}
}