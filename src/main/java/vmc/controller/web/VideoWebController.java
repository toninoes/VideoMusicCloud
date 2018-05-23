package vmc.controller.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	/*
	 * GET METHODS - DETALLE - LISTADO - PERFIL 
	 * 
	 */
	
	/*
	 * GET - LISTADO DE TODOS LOS VIDEOS DE VMC PAGINADO
	 */
	
	@GetMapping("/listado")
    public String listarTodosVideos(Model model, @RequestParam(value = "page", required = false) Integer page, 
			   									 @RequestParam(value = "active", required = false) Integer active, 
			   									 @RequestParam(value = "search", required = false) String search,
    											 @RequestParam(value = "visitas", required = false) Boolean visitas, 
    											 @RequestParam(value = "gustas", required = false) Boolean gustas, 
    											 @RequestParam(value = "titulo", required = false) Boolean titulo,
    											 @RequestParam(value = "descripcion", required = false) Boolean descripcion, 
    											 @RequestParam(value = "genero", required = false) Boolean genero, 
    											 @RequestParam(value = "user", required = false) Boolean user) {
		
		Pageable p = null;
		
		if(page != null) {
			p = PageRequest.of(page, (int)videoService.getVIDEOS_POR_PAGINA());
			model.addAttribute("page", page);
		} else {
			p = PageRequest.of(0, (int)videoService.getVIDEOS_POR_PAGINA());
			model.addAttribute("page", 0);
		}
		
		if(active != null)
			model.addAttribute("active", active);
		else
			model.addAttribute("active", 0);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Usuario admin = usuarioService.findByRol("ADMIN");
		
		List<Boolean> likes = new ArrayList<Boolean>();
		Page<Video> videos = null;
		
		if((visitas != null && visitas)         || 
		   (gustas != null && gustas)           || 
		   (titulo != null && titulo)           || 
		   (descripcion != null && descripcion) || 
		   (genero != null && genero)           || 
		   (user != null && user)               ||  
		   (search != null && !search.equals("0"))) {
			
			Set<Usuario> usuarios = new HashSet<Usuario>();
			List<Video> videosAll = videoService.findAll();
			videos = videoService.findSearch(p, videosAll, visitas, gustas, titulo, descripcion, genero, user, search, "listado", null, usuarios);
			
		} else videos = videoService.findAll(p);
		
		for(Video v : videos) {
			List<Comentario> comentarios = comentarioService.findByVideoUsuario(v, usuario);
			Comentario comentario = null;
			if(!comentarios.isEmpty())
				comentario = comentarios.get(0);
			if(comentario != null)
				likes.add(comentario.isGusta());
			else
				likes.add(false);
		}
		
		model.addAttribute("visitas", visitas);
		model.addAttribute("gustas", gustas);
		model.addAttribute("titulo", titulo);
		model.addAttribute("descripcion", descripcion);
		model.addAttribute("genero", genero);
		model.addAttribute("user", user);
		model.addAttribute("search", search);
		model.addAttribute("usuario", usuario);
		model.addAttribute("logueado", admin);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("pages", videos.getTotalPages());
		model.addAttribute("videos", videos);
		model.addAttribute("likes", likes);
		model.addAttribute("view", "listado");
		
		return "videos/listado";
	}
    
	/*
	 * GET - LISTADO DE LOS VIDEOS MIOS Y LOS USUARIOS QUE SIGO DE VMC PAGINADO
	 */
	
	@GetMapping("/misvideos")
	public String listarMisVideos(Model model, @RequestParam(value = "page", required = false) Integer page, 
											   @RequestParam(value = "active", required = false) Integer active, 
											   @RequestParam(value = "search", required = false) String search,
											   @RequestParam(value = "visitas", required = false) Boolean visitas, 
											   @RequestParam(value = "gustas", required = false) Boolean gustas, 
											   @RequestParam(value = "titulo", required = false) Boolean titulo,
											   @RequestParam(value = "descripcion", required = false) Boolean descripcion, 
											   @RequestParam(value = "genero", required = false) Boolean genero, 
											   @RequestParam(value = "user", required = false) Boolean user) {
    	
		Pageable p = null;
		
		if(page != null) {
			p = PageRequest.of(page, (int)videoService.getVIDEOS_POR_PAGINA());
			model.addAttribute("page", page);
		} else {
			p = PageRequest.of(0, (int)videoService.getVIDEOS_POR_PAGINA());
			model.addAttribute("page", 0);
		}
		
		if(active != null)
			model.addAttribute("active", active);
		else
			model.addAttribute("active", 0);
		
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Usuario admin = usuarioService.findByRol("ADMIN");
		List<Boolean> likes = new ArrayList<Boolean>();
		Page<Video> videos = null;
		
		if((visitas != null && visitas)         || 
		   (gustas != null && gustas)           || 
		   (titulo != null && titulo)           || 
		   (descripcion != null && descripcion) || 
		   (genero != null && genero)           || 
		   (user != null && user)               ||  
		   (search != null && !search.equals("0"))) {
			
			Set<Usuario> usuarios = new HashSet<Usuario>();
			List<Video> videosMy = videoService.findAll();
			for(Video v: videosMy)
				if(!usuarios.contains(v.getUsuario()))
					usuarios.add(v.getUsuario());
			videos = videoService.findSearch(p, videosMy, visitas, gustas, titulo, descripcion, genero, user, search, "misvideos", null, usuarios);
			
		} else {
			usuario.getSiguiendo().add(usuario);
			videos = videoService.findMyPage(p, usuario, usuario.getSiguiendo());
		}
		
		for(Video v : videos) {
			List<Comentario> comentarios = comentarioService.findByVideoUsuario(v, usuario);
			Comentario comentario = null;
			if(!comentarios.isEmpty())
				comentario = comentarios.get(0);
			if(comentario != null)
				likes.add(comentario.isGusta());
			else
				likes.add(false);
		}
		
		model.addAttribute("pages", videos.getTotalPages());
		model.addAttribute("videos", videos);
		model.addAttribute("likes", likes);
		model.addAttribute("usuario", usuario);
		model.addAttribute("logueado", admin);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("search", search);
    	model.addAttribute("visitas", visitas);
    	model.addAttribute("gustas", gustas);
    	model.addAttribute("titulo", titulo);
    	model.addAttribute("descripcion", descripcion);
    	model.addAttribute("genero", genero);
    	model.addAttribute("user", user);
    	model.addAttribute("view", "misvideos");
		
		return "videos/listadoPorUsuario";
	}
	
	/*
   	 * GET - PAGINADOR DE LISTADO DE VIDEOS (INICIO O LISTAS) - RESULTADO DE BUSQUEDA
   	 */
    
    @GetMapping("/{view}/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}")
    public String paginatorMisVideosBusqueda(@PathVariable String view,
    								 		 @PathVariable int page,
    								 		 @PathVariable int active,  
    								 		 @PathVariable String search,
    								 		 @PathVariable Boolean visitas,
    								 		 @PathVariable Boolean gustas,
    								 		 @PathVariable Boolean titulo,
    								 		 @PathVariable Boolean descripcion,
    								 		 @PathVariable Boolean genero,
    								 		 @PathVariable Boolean user,
    								 		 RedirectAttributes ra) {
    	
    	ra.addAttribute("page", page);
		ra.addAttribute("active", active);
		ra.addAttribute("search", search);
		ra.addAttribute("visitas", visitas);
		ra.addAttribute("gustas", gustas);
		ra.addAttribute("titulo", titulo);
		ra.addAttribute("descripcion", descripcion);
		ra.addAttribute("genero", genero);
		ra.addAttribute("user", user);
		ra.addAttribute("view", view);

   		return "redirect:/videos/{view}"; 
    }
	
	/*
   	 * GET - PAGINADOR DE LISTADO DE VIDEOS (LISTAS)
   	 */
    
    @GetMapping("/listado/{page}/{active}")
    public String paginatorListado(@PathVariable int page,
    					    	   @PathVariable int active,  
    					    	   RedirectAttributes ra) {
    	
    	ra.addAttribute("page", page);
		ra.addAttribute("active", active);

   		return "redirect:/videos/listado"; 
    }
    
    /*
   	 * GET - PAGINADOR DE LISTADO DE VIDEOS (INICIO)
   	 */
    
    @GetMapping("/misvideos/{page}/{active}")
    public String paginatorMisVideos(@PathVariable int page,
    					    		 @PathVariable int active,  
    					    		 RedirectAttributes ra) {
    	
    	ra.addAttribute("page", page);
		ra.addAttribute("active", active);

   		return "redirect:/videos/misvideos"; 
    }
	
	/*
	 * GET - DESCARGA DE VIDEOS
	 */
	
    @GetMapping("/{nombrevideo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable String nombrevideo) {
    	return videoService.descargar(nombrevideo);
    }
    
    /*
	 * GET - SUBIDA DE VIDEOS
	 */
    
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
    
    /*
	 * POST METHODS - INICIO - LISTAS - SUBIDA DE VIDEOS
	 * 
	 */
    
    /*
	 * POST - SUBIR VIDEO
	 */
    
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
    
    /*
   	 * POST - BUSCADOR DE VIDEOS
   	 */
    
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
		
		if(busqueda.equals(""))
			ra.addAttribute("search", "0");
		else
			ra.addAttribute("search", busqueda);	
    	if(visitas == null)
    		ra.addAttribute("visitas", false);
    	else
    		ra.addAttribute("visitas", true);
    	if(gustas == null)
    		ra.addAttribute("gustas", false);
    	else
    		ra.addAttribute("gustas", true);
    	if(titulo == null)
    		ra.addAttribute("titulo", false);
    	else
    		ra.addAttribute("titulo", true);
    	if(descripcion == null)
    		ra.addAttribute("descripcion", false);
    	else
    		ra.addAttribute("descripcion", true);
    	if(genero == null)
    		ra.addAttribute("genero", false);
    	else
    		ra.addAttribute("genero", true);
    	if(user == null)
    		ra.addAttribute("user", false);
    	else
    		ra.addAttribute("user", true);
    	
    	ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);

		switch(view) {
   			case "listado": return "redirect:/videos/listado/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
   			case "misvideos": return "redirect:/videos/misvideos/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
   			default: return "redirect:/videos/misvideos/{page}/{active}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
		} 
    }
    
    /*
   	 * POST - PAGINADOR DE LISTADO DE VIDEOS (LISTAS) PARAMETRIZADO
   	 */
    
    @PostMapping("/listado/{logueadoId}")
    public String customPaginator(@RequestParam("VideosPorPagina") String items,
    							  @PathVariable long logueadoId, 
    							  RedirectAttributes ra) {
    	
    	long videos = videoService.findAll().size();
		
		if(items.equals("todos"))
			videoService.setVIDEOS_POR_PAGINA((int)videos);
		else
			videoService.setVIDEOS_POR_PAGINA(Integer.parseInt(items));
    	
    	ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);

   		return "redirect:/videos/listado"; 
    }
    
    /*
   	 * POST - PAGINADOR DE LISTADO DE VIDEOS (INICIO) PARAMETRIZADO
   	 */
    
    @PostMapping("/misvideos/{logueadoId}/{pinchadoId}")
    public String customPaginator(@RequestParam("VideosPorPagina") String items,
    							  @PathVariable long logueadoId, 
    							  @PathVariable long pinchadoId, 
    							  RedirectAttributes ra) {
    	
    	Usuario logueado = usuarioService.findById(logueadoId);
    	long videos = videoService.findMyVideos(logueado, logueado.getSiguiendo()).size();
		
		if(items.equals("todos"))
			videoService.setVIDEOS_POR_PAGINA((int)videos);
		else
			videoService.setVIDEOS_POR_PAGINA(Integer.parseInt(items));
    	
    	ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);

   		return "redirect:/videos/misvideos"; 
    }
}
