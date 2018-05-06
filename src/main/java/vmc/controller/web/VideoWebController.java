package vmc.controller.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.service.ApplicationService;
import vmc.service.ComentarioService;
import vmc.service.GeneroService;
import vmc.service.UsuarioService;
import vmc.service.VideoService;

@Controller
@RequestMapping("/videos")
public class VideoWebController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
	private GeneroService generoService;
	
	@Autowired
	private ComentarioService comentarioService;
	
	@Autowired
	private ApplicationService appService;
    
    @GetMapping
    public String listarTodosVideos(Model model) {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Video> videos = videoService.findAll();
		List<Boolean> likes = new ArrayList<Boolean>();
		Comentario comentario = null;
		for(Video v : videos) {
			comentario = comentarioService.findByVideoUsuario(v, usuario);
			if(comentario != null)
				likes.add(comentario.isGusta());
			else
				likes.add(false);
		}
		model.addAttribute("videos", videos);
		model.addAttribute("likes", likes);
		model.addAttribute("usuario", usuario);
		
        return "videos/listado";
    }
    
    @GetMapping("/misvideos")
	public String findVideosByUsuarioId(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Video> videos = videoService.findVideosByUsuarioId(usuario.getId());
		List<Boolean> likes = new ArrayList<Boolean>();
		Comentario comentario = null;
		for(Video v : videos) {
			comentario = comentarioService.findByVideoUsuario(v, usuario);
			if(comentario != null)
				likes.add(comentario.isGusta());
			else
				likes.add(false);
		}
		model.addAttribute("videos", videos);
		model.addAttribute("likes", likes);
		model.addAttribute("usuario", usuario);
		
		return "videos/listadoPorUsuario";
	}
	
    @GetMapping("/{nombrevideo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable String nombrevideo) {
    	return videoService.descargar(nombrevideo);
    }
    
    @GetMapping("/subidaVideos")
    public String subidaVideos(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		model.addAttribute("usuario", usuario);
		model.addAttribute("generos", generoService.findAll());
    	return "videos/subidaVideos";
    }
    
    @PostMapping
    public String subirVideo(@RequestParam("titulo") String t, @RequestParam("video") MultipartFile v, 
    						 @RequestParam("descripcion") String d, @RequestParam("videogeneros") String[] g,
    						 RedirectAttributes ra) {
   
    	if(appService.checkFileVideoSize(v)) {	
    		videoService.subir(t, v, d, g);
        	ra.addFlashAttribute("mensaje", "Video " + v.getOriginalFilename() + " subido correctamente.");
        	return "redirect:/videos";
    	} else {
    		ra.addFlashAttribute("mensaje", "Error, el fichero ocupa más de 32 megas");
    		return "redirect:/videos/subidaVideos";
    	}
    }
    
    @PostMapping("/listado")
    public String buscarVideo(Model model, @RequestParam(value = "Visualizaciones", required = false) boolean visitas,
    									   @RequestParam(value = "Likes", required = false) boolean gustas,
    									   @RequestParam(value = "Titulo", required = false) boolean titulo,
    									   @RequestParam(value = "Descripcion", required = false) boolean descripcion,
    									   @RequestParam(value = "Genero", required = false) boolean genero,
    									   @RequestParam(value = "Usuario", required = false) boolean user,
    									   @RequestParam(value = "Busqueda", required = false) String busqueda) {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Video> videosAll = videoService.findAll();
		List<Video> videos = videoService.findSearch(videosAll, visitas, gustas, titulo, descripcion, genero, user, busqueda);
		List<Boolean> likes = new ArrayList<Boolean>();
		Comentario comentario = null;
		for(Video v : videos) {
			comentario = comentarioService.findByVideoUsuario(v, usuario);
			if(comentario != null)
				likes.add(comentario.isGusta());
			else
				likes.add(false);
		}
		model.addAttribute("videos", videos);
		model.addAttribute("likes", likes);
		model.addAttribute("usuario", usuario);
		
    	return "videos/listado";
    }
}
