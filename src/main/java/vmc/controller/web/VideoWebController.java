package vmc.controller.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String listarTodosVideos(RedirectAttributes ra) {
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		ra.addAttribute("search", "0");
		ra.addAttribute("visitas", 0);
		ra.addAttribute("gustas", 0);
		ra.addAttribute("titulo", 0);
		ra.addAttribute("descripcion", 0);
		ra.addAttribute("genero", 0);
		ra.addAttribute("user", 0);
		return "redirect:/videos/listado/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
	}
	
	@GetMapping("/listado/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}")
    public String listarTodosVideos(@PathVariable int page, @PathVariable int active, @PathVariable String search,
    								@PathVariable int visitas, @PathVariable int gustas, @PathVariable int titulo,
    								@PathVariable int descripcion, @PathVariable int genero, @PathVariable int user,
    								Model model, RedirectAttributes ra) {
		
		Pageable p = PageRequest.of(page, (int)VideoService.getVIDEOS_POR_PAGINA());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Usuario admin = usuarioService.findByRol("ADMIN");
		
		model.addAttribute("page", page);
		model.addAttribute("active", active);
		model.addAttribute("search", search);
		model.addAttribute("visitas", visitas);
		model.addAttribute("gustas", gustas);
		model.addAttribute("titulo", titulo);
		model.addAttribute("descripcion", descripcion);
		model.addAttribute("genero", genero);
		model.addAttribute("user", user);
		model.addAttribute("usuario", usuario);
		model.addAttribute("mailadmin", admin.getMail());
		
		List<Boolean> likes = new ArrayList<Boolean>();
		
		if(visitas == 1 || gustas == 1 || titulo == 1 || descripcion == 1 || genero == 1 || user == 1 || !search.equals("0")) {
			List<Video> videosAll = videoService.findAll();
			Page<Video> videos = videoService.findSearch(p, videosAll, visitas, gustas, titulo, descripcion, genero, user, search);
			for(Video v : videos) {
				Comentario comentario = comentarioService.findByVideoUsuario(v, usuario);
				if(comentario != null)
					likes.add(comentario.isGusta());
				else
					likes.add(false);
			}
			model.addAttribute("pages", videos.getTotalPages());
			model.addAttribute("videos", videos);
		} else {
			Page<Video> videosAll = videoService.findAll(p);
			for(Video v : videosAll) {
				Comentario comentario = comentarioService.findByVideoUsuario(v, usuario);
				if(comentario != null)
					likes.add(comentario.isGusta());
				else
					likes.add(false);
			}
			model.addAttribute("pages", videosAll.getTotalPages());
			model.addAttribute("videos", videosAll);
		}
		
		model.addAttribute("likes", likes);
		model.addAttribute("view", "listado");
		
		return "videos/listado";
	}
    
    @GetMapping("/misvideos")
    public String findVideosByUsuarioId(RedirectAttributes ra) {
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		return "redirect:/videos/misvideos/{page}/{active}";
	}
    
    @GetMapping("/misvideos/{page}/{active}")
    public String findVideosByUsuarioId(@PathVariable int page, @PathVariable int active, RedirectAttributes ra) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		ra.addAttribute("page", page);
		ra.addAttribute("active", active);
		ra.addAttribute("pages", videoService.myPages(usuario, usuario.getSiguiendo()));
		ra.addAttribute("search", "0");
		ra.addAttribute("visitas", 0);
		ra.addAttribute("gustas", 0);
		ra.addAttribute("titulo", 0);
		ra.addAttribute("descripcion", 0);
		ra.addAttribute("genero", 0);
		ra.addAttribute("user", 0);
		return "redirect:/videos/misvideos/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
	}
    
    @GetMapping("/misvideos/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}")
	public String findVideosByUsuarioId(Model model, @PathVariable int page, @PathVariable int active, @PathVariable int pages,
										@PathVariable String search, @PathVariable int visitas, @PathVariable int gustas,
										@PathVariable int titulo, @PathVariable int descripcion, @PathVariable int genero,
										@PathVariable int user) {
    	
    	Pageable p = PageRequest.of(page, (int)VideoService.getVIDEOS_POR_PAGINA());
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Usuario admin = usuarioService.findByRol("ADMIN");
		List<Boolean> likes = new ArrayList<Boolean>();
		
		if(visitas == 1 || gustas == 1 || titulo == 1 || descripcion == 1 || genero == 1 || user == 1 || !search.equals("0")) {
			List<Video> videosMy = videoService.findAll();
			Page<Video> videos = videoService.findSearch(p, videosMy, visitas, gustas, titulo, descripcion, genero, user, search);
			for(Video v : videos) {
				Comentario comentario = comentarioService.findByVideoUsuario(v, usuario);
				if(comentario != null)
					likes.add(comentario.isGusta());
				else
					likes.add(false);
			}
			model.addAttribute("pages", videos.getTotalPages());
			model.addAttribute("videos", videos);
		} else {
			usuario.getSiguiendo().add(usuario);
			Page<Video> videosMy = videoService.findMyPage(p, usuario, usuario.getSiguiendo());
			for(Video v : videosMy) {
				Comentario comentario = comentarioService.findByVideoUsuario(v, usuario);
				if(comentario != null)
					likes.add(comentario.isGusta());
				else
					likes.add(false);
			}
			model.addAttribute("pages", videosMy.getTotalPages());
			model.addAttribute("videos", videosMy);
		}
		
		model.addAttribute("likes", likes);
		model.addAttribute("usuario", usuario);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("page", page);
		model.addAttribute("active", active);
		model.addAttribute("view", "misvideos");
		
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
		Usuario admin = usuarioService.findByRol("ADMIN");
		model.addAttribute("usuario", usuario);
		model.addAttribute("generos", generoService.findAll());
		model.addAttribute("mailadmin", admin.getMail());
    	return "videos/subidaVideos";
    }
    
    @PostMapping
    public String subirVideo(@RequestParam("titulo") String t, @RequestParam(value = "video", required=false) MultipartFile v, 
    						 @RequestParam(value = "videobox", required=false) MultipartFile vb, @RequestParam("descripcion") String d, 
    						 @RequestParam("videogeneros") String[] g, RedirectAttributes ra) {
    	
		if(appService.checkFileVideoSize(v) && appService.checkFileVideoSize(vb)) {
			if(!v.isEmpty()) {
				videoService.subir(t, v, d, g);
				ra.addFlashAttribute("mensaje", "Video " + v.getOriginalFilename() + " subido correctamente.");
			} else {
				videoService.subir(t, vb, d, g);
				ra.addFlashAttribute("mensaje", "Video " + vb.getOriginalFilename() + " subido correctamente.");
			}
    	} else
    		ra.addFlashAttribute("mensaje", "Error, el fichero ocupa más de 32 megas");
		
		return "redirect:/videos/subidaVideos";
    }
    
    @PostMapping("/{view}")
    public String buscarVideo(Model model, @RequestParam(value = "Visualizaciones", required = false) Boolean visitas,
    									   @RequestParam(value = "Likes", required = false) Boolean gustas,
    									   @RequestParam(value = "Titulo", required = false) Boolean titulo,
    									   @RequestParam(value = "Descripcion", required = false) Boolean descripcion,
    									   @RequestParam(value = "Genero", required = false) Boolean genero,
    									   @RequestParam(value = "Usuario", required = false) Boolean user,
    									   @RequestParam(value = "Busqueda", required = false) String busqueda,
    									   @PathVariable String view, 
    									   RedirectAttributes ra) {
    	ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		
		if(busqueda.equals(""))
			ra.addAttribute("search", "0");
		else
			ra.addAttribute("search", busqueda);
    	
    	if(visitas == null)
    		ra.addAttribute("visitas", 0);
    	else
    		ra.addAttribute("visitas", 1);
    	if(gustas == null)
    		ra.addAttribute("gustas", 0);
    	else
    		ra.addAttribute("gustas", 1);
    	if(titulo == null)
    		ra.addAttribute("titulo", 0);
    	else
    		ra.addAttribute("titulo", 1);
    	if(descripcion == null)
    		ra.addAttribute("descripcion", 0);
    	else
    		ra.addAttribute("descripcion", 1);
    	if(genero == null)
    		ra.addAttribute("genero", 0);
    	else
    		ra.addAttribute("genero", 1);
    	if(user == null)
    		ra.addAttribute("user", 0);
    	else
    		ra.addAttribute("user", 1);

		switch(view) {
   			case "listado": return "redirect:/videos/listado/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
   			case "misvideos": Pageable p = PageRequest.of(0, (int)VideoService.getVIDEOS_POR_PAGINA());
   							  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
   							  Usuario usuario = usuarioService.findByMail(auth.getName());
   							  ra.addAttribute("pages", videoService.findMyPage(p, usuario, usuario.getSiguiendo()).getTotalPages());
   							  return "redirect:/videos/misvideos/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
   			default: return "redirect:/videos/misvideos";
		} 
    }
    
    @PostMapping("/misvideos/{logueadoId}/{pinchadoId}/{videoId}/{views}/{vista}")
	public String saveVisit(Model model, @PathVariable long logueadoId,
										 @PathVariable long pinchadoId,
										 @PathVariable long videoId,
										 @PathVariable long views,
										 @PathVariable String vista) {
				
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Video video = videoService.findById(videoId);
		
		videoService.saveVisit(views + 1, video);
		
		Comentario comentario = comentarioService.findByVideoUsuario(video, logueado);
		if(comentario != null)
			model.addAttribute("gusta", true);
		else
			model.addAttribute("gusta", false);
		
		model.addAttribute("video", video);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		switch(vista) {
			case "inicio": return "redirect:/videos/misvideos";
			case "listas": return "redirect:/videos";
			default: return "redirect:/videos/misvideos";
		}
    }
}
