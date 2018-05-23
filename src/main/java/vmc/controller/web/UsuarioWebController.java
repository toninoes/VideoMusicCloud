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
import vmc.service.UsuarioService;
import vmc.service.VideoService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
	private ComentarioService comentarioService;
	
	@Autowired
	private ApplicationService appService;
	
	/*
	 * GET METHODS - DETALLE - LISTADO - PERFIL 
	 * 
	 */
	
	/*
	 * GET - DETALLE EDITAR PERFIL (PORTAL O NO)
	 */
	
	@GetMapping("/detalle/{portal}/{logueadoId}/{pinchadoId}")
	public String findByIdPortal(Model model, @PathVariable String portal,
											  @PathVariable long logueadoId,
											  @PathVariable long pinchadoId) {
		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		String ext = "" + pinchado.getFoto();
		if(!ext.equals(""))
			ext = ext.substring(ext.lastIndexOf("."), ext.length());
		else
			ext = ".jpg";
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("portal", portal);
		model.addAttribute("ext", ext);
		model.addAttribute("mailadmin", admin.getMail());
	
		return "usuarios/detalle";
	}
	
	/*
	 * GET - LISTADO DE USUARIOS + BUSCADOR DE USUARIOS + PAGINADOR DE USUARIOS
	 */
	
	@GetMapping("/listado/{segsig}/{pinchadoId}")
    public String findUsers(Model model, @PathVariable long pinchadoId,
    									 @PathVariable String segsig,
    									 @RequestParam(value = "page", required = false) Integer page,
    									 @RequestParam(value = "active", required = false) Integer active,
    									 @RequestParam(value = "nombre", required = false) Boolean nombre, 
    									 @RequestParam(value = "apellidos", required = false) Boolean apellidos, 
    									 @RequestParam(value = "search", required = false) String search) {
		
		Pageable p = null;
		
		if(page != null) {
			p = PageRequest.of(page, (int)usuarioService.getUSUARIOS_POR_PAGINA());
			model.addAttribute("page", page);
		} else {
			p = PageRequest.of(0, (int)usuarioService.getUSUARIOS_POR_PAGINA());
			model.addAttribute("page", 0);
		}
		
		if(active != null)
			model.addAttribute("active", active);
		else
			model.addAttribute("active", 0);
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario pinchado = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		Page<Usuario> usuarios = null;
		
		if(search != null && search.equals("0")) {
			if(segsig.equals("seguidores"))
				usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
			else
				usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());
		} else {
			if(nombre != null && nombre && apellidos != null && apellidos)
				usuarios = usuarioService.findPageSearch(p, search, "nombreapellidos", pinchado, segsig);
			else if(nombre != null && nombre)
				usuarios = usuarioService.findPageSearch(p, search, "nombre", pinchado, segsig);
			else if(apellidos != null && apellidos)
				usuarios = usuarioService.findPageSearch(p, search, "apellidos", pinchado, segsig);
			else
				usuarios = usuarioService.findPageSearch(p, search, "0", pinchado, segsig);
		}
		
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("pages", usuarios.getTotalPages());
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("segsig", segsig);
		model.addAttribute("nombre", nombre);
		model.addAttribute("apellidos", apellidos);
		model.addAttribute("search", search);
		model.addAttribute("mailadmin", admin.getMail());
		
		return "usuarios/listado";
	}
    
	/*
	 * GET - ENLACE A LISTADO DE SEGUIDORES/SIGUIENDO - NO BUSCADOR
	 */
	
	@GetMapping("/listado/{segsig}/{pinchadoId}/{page}/{active}")
    public String segsig(Model model, @PathVariable int page,
    								  @PathVariable int active,
    								  @PathVariable long pinchadoId, 
    							  	  @PathVariable String segsig,
    							  	  RedirectAttributes ra) {
		
		Pageable p = PageRequest.of(page, (int)usuarioService.getUSUARIOS_POR_PAGINA());
	
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Page<Usuario> usuarios = null;
		if(segsig.equals("seguidores"))
			usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
		else
			usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());
		
		ra.addAttribute("page", page);
		ra.addAttribute("active", active);
		ra.addAttribute("segsig", segsig);
		ra.addAttribute("search", "0");
		ra.addAttribute("pages", usuarios.getTotalPages());
		ra.addAttribute("pinchadoId", pinchadoId);
		
		return "redirect:/usuarios/listado/{segsig}/{pinchadoId}";
	}
	
	/*
	 * GET - ENLACE A LISTADO DE SEGUIDORES/SIGUIENDO - BUSCADOR
	 */
	
	@GetMapping("/listado/{segsig}/{pinchadoId}/{page}/{active}/{search}/{nombre}/{apellidos}")
    public String segsigBuscador(Model model, @PathVariable int page,
    								  		  @PathVariable int active,
    								  		  @PathVariable long pinchadoId, 
    								  		  @PathVariable String segsig,
    								  		  @PathVariable String search,
    								  		  @PathVariable Boolean nombre,
    								  		  @PathVariable Boolean apellidos,
    								  		  RedirectAttributes ra) {
		
		Pageable p = PageRequest.of(page, (int)usuarioService.getUSUARIOS_POR_PAGINA());
	
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		Page<Usuario> usuarios = null;
		if(segsig.equals("seguidores"))
			usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
		else
			usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());
		
		model.addAttribute("page", page);
		ra.addAttribute("page", page);
		model.addAttribute("active", active);
		ra.addAttribute("active", active);
		model.addAttribute("segsig", segsig);
		if(search == null) {
			model.addAttribute("search", "0");
			ra.addAttribute("search", "0");
		} else {
			model.addAttribute("search", search);
			ra.addAttribute("search", search);
		}
		model.addAttribute("pages", usuarios.getTotalPages());
		model.addAttribute("pinchadoId", pinchadoId);
		if(nombre == null) {
			model.addAttribute("nombre", false);
			ra.addAttribute("nombre", false);
		} else {
			model.addAttribute("nombre", nombre);
			ra.addAttribute("nombre", nombre);
		}
		if(apellidos == null) {
			model.addAttribute("apellidos", false);
			ra.addAttribute("apellidos", false);
		} else {
			model.addAttribute("apellidos", apellidos);
			ra.addAttribute("apellidos", apellidos);
		}
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("segsig", segsig);
		
		return "redirect:/usuarios/listado/{segsig}/{pinchadoId}";
	}
	
	/*
	 * GET - PERFIL
	 */
	
	@GetMapping("/perfil/{view}/{pinchadoId}")
	public String perfil(Model model, @PathVariable String view,
									  @PathVariable long pinchadoId,
						 			  @RequestParam(value = "page", required = false) Integer page, 
						 			  @RequestParam(value = "active", required = false) Integer active, 
						 			  @RequestParam(value = "search", required = false) String search, 
						 			  @RequestParam(value = "visitas", required = false) Boolean visitas, 
						 			  @RequestParam(value = "gustas", required = false) Boolean gustas,
						 			  @RequestParam(value = "titulo", required = false) Boolean titulo, 
						 			  @RequestParam(value = "descripcion", required = false) Boolean descripcion, 
						 			  @RequestParam(value = "genero", required = false) Boolean genero,
						 			  @RequestParam(value = "user", required = false) Boolean user) {
		
		String sigue = "";
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
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario usuario = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		Set<Usuario> usuarios = null;
		usuarios = new HashSet<Usuario>();
		List<Boolean> likes = new ArrayList<Boolean>();
		Page<Video> videos = null;
		
		if((visitas != null && visitas)         || 
		   (gustas != null && gustas)           || 
		   (titulo != null && titulo)           || 
		   (descripcion != null && descripcion) || 
		   (genero != null && genero)           || 
		   (user != null && user)               ||  
		   (search != null && !search.equals("0"))) {
		
			List<Video> videosMy = videoService.findMyVideos(usuario, usuarios);
			videos = videoService.findSearch(p, videosMy, visitas, gustas, titulo, descripcion, genero, user, search, "perfil", logueado, usuarios);
			
		} else
			videos = videoService.findMyPage(p, usuario, usuarios);
		
		for(Video v : videos) {
			List<Comentario> comentarios = comentarioService.findByVideoUsuario(v, usuario);
			if(!comentarios.isEmpty()) {
				Comentario comentario = comentarios.get(0);
				if(comentario != null)
					likes.add(comentario.isGusta());
				else
					likes.add(false);
			} else likes.add(false);
		}
		model.addAttribute("pages", videos.getTotalPages());
		model.addAttribute("videos", videos);
		
		if(logueado.getId() == pinchadoId)
			sigue = "hidden";
		else if(usuarioService.findBySiguiendo(logueado, usuario)) 
			sigue = "dejar";
		else
			sigue = "seguir";
		
		String ext = "" + logueado.getFoto();
		if(!ext.equals(""))
			ext = ext.substring(ext.lastIndexOf("."), ext.length());
		else 
			ext = ".jpg";
		
		String extpinchado = "" + usuario.getFoto();
		if(!extpinchado.equals(""))
			extpinchado = extpinchado.substring(extpinchado.lastIndexOf("."), extpinchado.length());
		else 
			extpinchado = ".jpg";
		
		model.addAttribute("likes", likes);
		model.addAttribute("sigue", sigue);
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", usuario);
		model.addAttribute("ext", ext);
		model.addAttribute("extpinchado", extpinchado);
		model.addAttribute("mailadmin", admin.getMail());
    	model.addAttribute("search", search);
    	model.addAttribute("visitas", visitas);
    	model.addAttribute("gustas", gustas);
    	model.addAttribute("titulo", titulo);
    	model.addAttribute("descripcion", descripcion);
    	model.addAttribute("genero", genero);
    	model.addAttribute("user", user);
    	model.addAttribute("view", view);
		
		return "usuarios/perfil";
	}
	
	/*
   	 * GET - PAGINADOR DE LISTADO DE VIDEOS (PERFIL)
   	 */
    
    @GetMapping("/perfil/{view}/{page}/{active}/{pinchadoId}")
    public String paginatorMisVideos(@PathVariable String view,
    								 @PathVariable int page,
    					    		 @PathVariable int active,  
    					    		 @PathVariable long pinchadoId,
    					    		 RedirectAttributes ra) {
    	
    	ra.addAttribute("page", page);
		ra.addAttribute("active", active);

   		return "redirect:/usuarios/perfil/{view}/{pinchadoId}"; 
    }
    
    /*
   	 * GET - PAGINADOR DE LISTADO DE VIDEOS (PERFIL) - RESULTADO DE BUSQUEDA
   	 */
    
    @GetMapping("/perfil/{view}/{page}/{active}/{pinchadoId}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}")
    public String paginatorMisVideosBusqueda(@PathVariable String view,
    								 		 @PathVariable int page,
    								 		 @PathVariable int active,  
    								 		 @PathVariable long pinchadoId,
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

   		return "redirect:/usuarios/perfil/{view}/{pinchadoId}"; 
    }
	
	/*
	 * GET - ELIMINAR VIDEO CON MENSAJE DE CONFIRMACIÓN
	 */
	
	@GetMapping("/perfil/{view}/{action}/{logueadoId}/{videoId}/{borrar}")
	public String elimnarVideo(Model model, @PathVariable String view,
											@PathVariable String action,
											@PathVariable long logueadoId,
											@PathVariable long videoId,
											@PathVariable String borrar) {
		
		Video video = videoService.findById(videoId);
		comentarioService.delete(video);
		videoService.delete(video);
		String ext = video.getNombre();
		ext = ext.substring(ext.lastIndexOf("."));
		videoService.borrar(video.getId() + ext);
		
		return "redirect:/usuarios/perfil/{view}/{logueadoId}";
	}
	
	/*
	 * GET - DESCARGA DE FOTOS
	 */
	
	@GetMapping("/fotos/{nombrefoto:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirFoto(@PathVariable String nombrefoto) {
		return usuarioService.descargar(nombrefoto);
    }
	
	/*
	 * POST METHODS - DETALLE - LISTADO - PERFIL - SUBIDA DE FOTOS
	 * 
	 */
	
	/*
	 * POST - CAMBIAR LA CLAVE DEL USUARIO
	 */
	
	@PostMapping("/detalle/{portal}")
	public String cambiarClave(@PathVariable String portal,
							   @RequestParam("pinchadoId") long logueadoId,
							   @RequestParam("pinchadoId") long pinchadoId,
							   @RequestParam("oldpassword") String o,
							   @RequestParam("newpassword") String n, 
							   @RequestParam("rnewpassword") String r, 
			                   RedirectAttributes ra) {
		
		ra.addAttribute("id", usuarioService.cambiarClave(o, n, r));
		ra.addAttribute("portal", portal);
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		
    	return "redirect:/usuarios/detalle/{portal}/{logueadoId}/{pinchadoId}";
	}
	
	/*
	 * POST - GUARDAR O BORRAR DATOS/FOTO PERFIL
	 */
	
	@PostMapping("/detalle/{portal}/{logueadoId}/{pinchadoId}")
	public String saveDeletePerfilById(@PathVariable long logueadoId, 
									   @PathVariable long pinchadoId,
									   @PathVariable String portal,
									   @RequestParam(value = "eliminar", required=false) String eliminar,
							  		   @RequestParam("foto") MultipartFile f, 
									   @RequestParam(value="quitarFoto", required=false) Boolean quitarFoto,
									   @RequestParam("nombre") String nombre,
									   @RequestParam("apellidos") String apellidos,
									   @RequestParam("intereses") String intereses,
									   RedirectAttributes ra) {
		
		Usuario usuario = usuarioService.findById(pinchadoId);
		
		if(eliminar != null && !eliminar.equals("") && quitarFoto != null && quitarFoto) {
			usuarioService.deleteFoto(usuario);
			usuarioService.borrar(usuario);
			ra.addFlashAttribute("mensajeEliminar", "Foto " + f.getOriginalFilename() + " eliminada correctamente.");
		} else if(quitarFoto == null && eliminar != null && eliminar.equals("") && !f.getOriginalFilename().equals("") && appService.checkFilePhotoSize(f)) {	
	    	usuarioService.subir(f);
	    	usuarioService.update(pinchadoId, false, nombre, apellidos, intereses);
	    	ra.addFlashAttribute("mensajeSubir", "Foto " + f.getOriginalFilename() + " subida correctamente.");
	    } else if(!f.getOriginalFilename().equals(""))
	    	ra.addFlashAttribute("mensaje", "Error, la foto ocupa más de 22 megas");
		if(quitarFoto != null && quitarFoto)
			usuarioService.update(pinchadoId, true, nombre, apellidos, intereses);
		else
			usuarioService.update(pinchadoId, false, nombre, apellidos, intereses);
		
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("portal", portal);
		
    	return "redirect:/usuarios/detalle/{portal}/{logueadoId}/{pinchadoId}";
	}
	
	/*
	 * POST - BUSCADOR DE USUARIOS
	 */
	
	@PostMapping("/listado/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{segsig}")
    public String buscarUsuario(Model model, @RequestParam(value = "Nombre", required = false) Boolean nombre,
    									     @RequestParam(value = "Apellidos", required = false) Boolean apellidos,
    									     @RequestParam(value = "Busqueda") String search,
    									     @PathVariable long logueadoId,
    									     @PathVariable long pinchadoId,
    									     @PathVariable int page,
    									     @PathVariable int active,
    									     @PathVariable int pages,
    									     @PathVariable String segsig,
    									     RedirectAttributes ra) {
		
		Pageable p = PageRequest.of(page, (int)usuarioService.getUSUARIOS_POR_PAGINA());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario pinchado = usuarioService.findById(pinchadoId); 
		Usuario admin = usuarioService.findByRol("ADMIN");
		Page<Usuario> usuarios = null;
		
		if(search.equals("")) {	
			if(segsig.equals("seguidores"))
				usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
			else
				usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());	
		} else {
			if(nombre != null && apellidos != null && nombre && apellidos)
				usuarios = usuarioService.findPageSearch(p, search.toLowerCase(), "nombreapellidos", pinchado, segsig);
			else if(nombre != null && nombre)
				usuarios = usuarioService.findPageSearch(p, search.toLowerCase(), "nombre", pinchado, segsig);
			else if(apellidos != null && apellidos)
				usuarios = usuarioService.findPageSearch(p, search.toLowerCase(), "apellidos", pinchado, segsig);
			else
				usuarios = usuarioService.findPageSearch(p, search.toLowerCase(), "0", pinchado, segsig);
		}
		
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("pages", usuarios.getTotalPages());
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("page", page);
		model.addAttribute("active", active);	
		model.addAttribute("segsig", segsig);
		model.addAttribute("logueadoId", logueadoId);
		model.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		if(search.equals("")) {
			model.addAttribute("search", "0");
			ra.addAttribute("search", "0");
		} else {
			model.addAttribute("search", search);
			ra.addAttribute("search", search);
		}
		if(nombre == null) {
			model.addAttribute("nombre", false);
			ra.addAttribute("nombre", false);
		} else {
			model.addAttribute("nombre", nombre);
			ra.addAttribute("nombre", nombre);
		}
		if(apellidos == null) {
			model.addAttribute("apellidos", false);
			ra.addAttribute("apellidos", false);
		} else {
			model.addAttribute("apellidos", apellidos);
			ra.addAttribute("apellidos", apellidos);
		}
		
		return "redirect:/usuarios/listado/{segsig}/{pinchadoId}/{page}/{active}/{search}/{nombre}/{apellidos}";
	}
	
	/*
	 * POST - PAGINADOR DE LISTADO DE USUARIOS PARAMETRIZADO
	 */
	
	@PostMapping("/listado/{segsig}/{logueadoId}/{pinchadoId}")
    public String customPaginator(Model model, @RequestParam("UsuariosPorPagina") String items,
    										   @PathVariable long logueadoId, 
    										   @PathVariable long pinchadoId, 
    							  			   @PathVariable String segsig,
    							  			   RedirectAttributes ra) {
		
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		if(items.equals("todos") && segsig.equals("siguiendo"))
			usuarioService.setUSUARIOS_POR_PAGINA((int)pinchado.getSiguiendo().size());
		else if(items.equals("todos") && segsig.equals("seguidores"))
			usuarioService.setUSUARIOS_POR_PAGINA((int)pinchado.getSeguidores().size());
		else
			usuarioService.setUSUARIOS_POR_PAGINA(Integer.parseInt(items));
		
		ra.addAttribute("segsig", segsig);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		ra.addAttribute("search", "0");
		ra.addAttribute("nombre", false);
		ra.addAttribute("apellidos", false);
		
		return "redirect:/usuarios/listado/{segsig}/{pinchadoId}/{page}/{active}/{search}/{nombre}/{apellidos}";
	}
	
	/*
	 * POST - BUSCADOR DE VIDEOS
	 */
	
	@PostMapping("/perfil/{buscador}/{view}/{pinchadoId}/{logueadoId}")
    public String buscarVideo(Model model,
    						  @RequestParam(value = "Visualizaciones", required = false) Boolean visitas,
    						  @RequestParam(value = "Likes", required = false) Boolean gustas,
    						  @RequestParam(value = "Titulo", required = false) Boolean titulo,
    						  @RequestParam(value = "Descripcion", required = false) Boolean descripcion,
    						  @RequestParam(value = "Genero", required = false) Boolean genero,
    						  @RequestParam(value = "Usuario", required = false) Boolean user,
    						  @RequestParam(value = "Busqueda", required = false) String busqueda,
    						  @PathVariable String buscador,
    						  @PathVariable String view,
    						  @PathVariable long pinchadoId,
    						  @PathVariable long logueadoId,
    						  RedirectAttributes ra) {
		
		if(busqueda.equals("")) {
			model.addAttribute("search", "0");
			ra.addAttribute("search", "0");
		} else {
			model.addAttribute("search", busqueda);
			ra.addAttribute("search", busqueda);
		}
    	if(visitas == null) {
    		model.addAttribute("visitas", false);
    		ra.addAttribute("visitas", false);
    	} else {
    		model.addAttribute("visitas", true);
    		ra.addAttribute("visitas", true);
    	}
    	if(gustas == null) {
    		model.addAttribute("gustas", false);
    		ra.addAttribute("gustas", false);
    	} else {
    		model.addAttribute("gustas", true);
    		ra.addAttribute("gustas", true);
    	}
    	if(titulo == null) {
    		model.addAttribute("titulo", false);
    		ra.addAttribute("titulo", false);
    	} else {
    		model.addAttribute("titulo", true);
    		ra.addAttribute("titulo", true);
    	}
    	if(descripcion == null) {
    		model.addAttribute("descripcion", false);
    		ra.addAttribute("descripcion", false);
    	} else {
    		model.addAttribute("descripcion", true);
    		ra.addAttribute("descripcion", true);
    	}
    	if(genero == null) {
    		model.addAttribute("genero", false);
    		ra.addAttribute("genero", false);
    	} else {
    		model.addAttribute("genero", true);
    		ra.addAttribute("genero", true);
    	}
    	if(user == null) {
    		model.addAttribute("user", false);
    		ra.addAttribute("user", false);
    	} else { 
    		model.addAttribute("user", true);
    		ra.addAttribute("user", true);
    	}
    	
    	model.addAttribute("view", view);
    	ra.addAttribute("view", view);

		return "redirect:/usuarios/perfil/{view}/{pinchadoId}"; 
    }
	
	/*
	 * POST - PAGINADOR DE VIDEOS PARAMETRIZADO
	 */
	
	@PostMapping("/perfil/{view}/{paginador}/{custom}/{logueadoId}/{pinchadoId}")
    public String customPaginator(Model model, @RequestParam("VideosPorPagina") String items,
    										   @PathVariable String view,
    										   @PathVariable String paginador,
    										   @PathVariable String custom,
    										   @PathVariable long logueadoId,
    										   @PathVariable long pinchadoId) {
		
		long videos = videoService.findVideosByUsuarioId(pinchadoId).size();
		
		if(items.equals("todos"))
			videoService.setVIDEOS_POR_PAGINA((int)videos);
		else
			videoService.setVIDEOS_POR_PAGINA(Integer.parseInt(items));
		
		Pageable p = PageRequest.of(0, (int)videoService.getVIDEOS_POR_PAGINA());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario usuario = usuarioService.findById(pinchadoId);
		Set<Usuario> usuarios = new HashSet<Usuario>();
		
		model.addAttribute("page", 0);
		model.addAttribute("active", 0);
		model.addAttribute("pages", videoService.findMyPage(p, usuario, usuarios).getTotalPages());
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", usuario);
		model.addAttribute("search", "0");
		model.addAttribute("visitas", false);
		model.addAttribute("gustas", false);
		model.addAttribute("titulo", false);
		model.addAttribute("descripcion", false);
		model.addAttribute("genero", false);
		model.addAttribute("user", false);
		model.addAttribute("view", view);

		return "redirect:/usuarios/perfil/{view}/{pinchadoId}"; 
    }
	
	/*
	 * POST - CAMBIAR EL BOTON SEGUIR/DEJAR
	 */
	
	@PostMapping("/perfil/{sigue}/{logueadoId}/{pinchadoId}")
    public String cambiarBoton(Model model, @PathVariable String sigue,
    										@PathVariable long logueadoId,
    							  			@PathVariable long pinchadoId,
    							  			RedirectAttributes ra) {
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario usuario = usuarioService.findById(pinchadoId);
		
		if(!sigue.equals("hidden") && usuarioService.findBySiguiendo(logueado, usuario)) {
			usuarioService.dejar(logueado, usuario);
			sigue = "seguir";
		} else if(!sigue.equals("hidden")) {
			usuarioService.seguir(logueado, usuario);
			sigue = "dejar";
		}
		
		ra.addAttribute("view", "perfil");
		
		return "redirect:/usuarios/perfil/{view}/{pinchadoId}";
	}
	
	/*
	 * POST - SUBIDA DE FOTOS
	 */
	
	@PostMapping("/perfil/{logueadoId}/{pinchadoId}")
    public String subirFoto(@RequestParam("foto") MultipartFile f, 
    						@PathVariable long logueadoId, 
    						RedirectAttributes ra) {
		
		if(appService.checkFilePhotoSize(f)) {	
			usuarioService.subir(f);
			ra.addFlashAttribute("mensajeSubir", "Foto " + f.getOriginalFilename() + " subida correctamente.");
		} else
    		ra.addFlashAttribute("mensaje", "Error, la foto ocupa más de 22 megas");
		
		ra.addAttribute("view", "perfilog");
		
        return "redirect:/usuarios/perfil/{view}/{logueadoId}";
    }
}
