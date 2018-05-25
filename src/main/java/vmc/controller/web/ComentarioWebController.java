package vmc.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	
	/*
	 * GET METHODS - COMENTARIO VIDEOS
	 * 
	 */
	
	/*
	 * GET - COMENTARIO
	 */
	
	@GetMapping
	public String comentario(Model model, @RequestParam("pinchadoId") long pinchadoId,
			 							  @RequestParam("videoId") long videoId,
			 							  @RequestParam("view") String view) {
				
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		
		Video video = videoService.findById(videoId);
		List<Comentario> comentarios = comentarioService.findComentariosByVideo(video);
		List<Comentario> coment = comentarioService.findByVideoUsuario(video, logueado);
		Comentario c = null;
		if(!coment.isEmpty())
			c = coment.get(0);
		if(c != null)
			model.addAttribute("gusta", c.isGusta());
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);		
		model.addAttribute("comentarios", comentarios);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("view", view);
		
		return "comentarios/comentarioVideos";
	}
	
	/*
	 * POST METHODS - COMENTARIO O GUSTA - VISUALIZACIONES
	 * 
	 */
	
	/*
	 * POST - COMENTARIO O GUSTA
	 */
	
	@PostMapping("/comentarioVideos/{logueadoId}/{pinchadoId}/{videoId}")
	public String saveComment(Model model, @RequestParam("descripcion") String descripcion,
										   @RequestParam("view") String view,
										   @PathVariable long logueadoId,
										   @PathVariable long pinchadoId,
										   @PathVariable long videoId) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		comentarioService.create(video, logueado, descripcion, "comentario");
	
		List<Comentario> comentariosAllVideo = comentarioService.findComentariosByVideo(video);
		List<Comentario> coment = comentarioService.findByVideoUsuario(video, logueado);
		Comentario c = null;
		if(!coment.isEmpty())
			c = coment.get(0);
		if(c != null)
			model.addAttribute("gusta", c.isGusta());
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("comentarios", comentariosAllVideo);
		model.addAttribute("view", view);
		
		return "comentarios/comentarioVideos";
	}
	
	/*
	 * POST - VISUALIZACIONES
	 */
	
	@PostMapping("/{logueadoId}/{pinchadoId}/{videoId}/{comentario}/{vista}/{views}")
	public String saveVisit(Model model, @RequestParam("view") String view,
										 @PathVariable long logueadoId,
										 @PathVariable long pinchadoId,
										 @PathVariable long videoId,
										 @PathVariable boolean comentario,
									     @PathVariable String vista,
									     @PathVariable long views) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		videoService.saveVisit(views + 1, video);
		
		List<Comentario> coment = comentarioService.findByVideoUsuario(video, logueado);
		Comentario c = null;
		if(!coment.isEmpty())
			c = coment.get(0);
		if(c != null)
			model.addAttribute("gusta", true);
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("view", view);
		
		return "comentarios/comentarioVideos";
	}
}