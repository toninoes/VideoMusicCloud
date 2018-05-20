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
	
	@GetMapping("/detalle/{id}")
	public String findById(Model model, @PathVariable long id) {
		Usuario usuario = usuarioService.findById(id);
		Usuario admin = usuarioService.findByRol("ADMIN");
		String ext = "" + usuario.getFoto();
		if(!ext.equals(""))
			ext = ext.substring(ext.lastIndexOf("."), ext.length());
		else
			ext = ".jpg";
		model.addAttribute("usuario", usuario);
		model.addAttribute("portal", "no");
		model.addAttribute("ext", ext);
		model.addAttribute("mailadmin", admin.getMail());
		return "usuarios/detalle";
	}
	
	@GetMapping("/detalle/{id}/{portal}")
	public String findByIdPortal(Model model, @PathVariable long id, @PathVariable String portal,
								 RedirectAttributes ra) {
		Usuario usuario = usuarioService.findById(id);
		Usuario admin = usuarioService.findByRol("ADMIN");
		String ext = "" + usuario.getFoto();
		if(!ext.equals(""))
			ext = ext.substring(ext.lastIndexOf("."), ext.length());
		else
			ext = ".jpg";
		model.addAttribute("usuario", usuario);
		model.addAttribute("portal", portal);
		model.addAttribute("ext", ext);
		model.addAttribute("mailadmin", admin.getMail());
		
		ra.addAttribute("id", id);
		
		if(portal.equals("si"))
			return "usuarios/detalle";
		else
			return "redirect:/usuarios/detalle/{id}";
	}
	
	@GetMapping("/listado/{logueadoId}/{pinchadoId}/{segsig}/{nombre}/{apellidos}/{search}")
    public String findUsers(@PathVariable long logueadoId, @PathVariable long pinchadoId, @PathVariable String segsig,
    						@PathVariable String nombre, @PathVariable String apellidos, @PathVariable String search,
    						RedirectAttributes ra) {
		
		Pageable p = PageRequest.of(0, (int)usuarioService.getUSUARIOS_POR_PAGINA());
		
		Usuario pinchado = usuarioService.findById(pinchadoId);
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		if(segsig.equals("seguidores"))
			ra.addAttribute("pages", usuarioService.allPages(p, pinchado.getSeguidores().size(), segsig));
		else 
			ra.addAttribute("pages", usuarioService.allPages(p, pinchado.getSiguiendo().size(), segsig));
		ra.addAttribute("segsig", segsig);
		ra.addAttribute("nombre", nombre);
		ra.addAttribute("apellidos", apellidos);
		ra.addAttribute("search", search);
		return "redirect:/usuarios/listado/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{segsig}/{nombre}/{apellidos}/{search}";
	}
	
	@GetMapping("/listado/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{segsig}/{nombre}/{apellidos}/{search}")
	public String findUsers(Model model, @PathVariable long logueadoId,
									     @PathVariable long pinchadoId,
									     @PathVariable int page,
									     @PathVariable int active,
									     @PathVariable int pages,
									     @PathVariable String segsig,
									     @PathVariable String nombre,
									     @PathVariable String apellidos,
									     @PathVariable String search) {
		
		Pageable p = PageRequest.of(page, (int)usuarioService.getUSUARIOS_POR_PAGINA());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario logueado = usuarioService.findByMail(auth.getName());
		Usuario pinchado = usuarioService.findById(pinchadoId); 
		Usuario admin = usuarioService.findByRol("ADMIN");
		
		if(search.equals("0")) {
			Page<Usuario> usuarios = null;
			if(segsig.equals("seguidores") && (logueadoId == pinchadoId)) {
				usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
				model.addAttribute("usuarios", usuarios);
			} else if(segsig.equals("seguidores") && (logueadoId != pinchadoId)) {
				usuarios = usuarioService.seguidores(p, pinchado.getSeguidores());
				model.addAttribute("usuarios", usuarios);
			} else if(segsig.equals("siguiendo") && (logueadoId == pinchadoId)) {
				usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());
				model.addAttribute("usuarios", usuarios);
			} else {
				usuarios = usuarioService.siguiendo(p, pinchado.getSiguiendo());
				model.addAttribute("usuarios", usuarios);
			}
			model.addAttribute("pages", usuarios.getTotalPages());
		} else {
			Page<Usuario> usuarios = null;
			if(nombre.equals("1") && apellidos.equals("1")) {
				usuarios = usuarioService.findPageSearch(p, search, "nombreapellidos", logueadoId);
				model.addAttribute("usuarios", usuarios);
			} else if(nombre.equals("1")) {
				usuarios = usuarioService.findPageSearch(p, search, "nombre", logueadoId);
				model.addAttribute("usuarios", usuarios);
			} else if(apellidos.equals("1")) {
				usuarios = usuarioService.findPageSearch(p, search, "apellidos", logueadoId);
				model.addAttribute("usuarios", usuarios);
			} else {
				usuarios = usuarioService.findPageSearch(p, search, "0", logueadoId);
				model.addAttribute("usuarios", usuarios);
			}
			model.addAttribute("pages", usuarios.getTotalPages());
		}
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("mailadmin", admin.getMail());
		model.addAttribute("page", page);
		model.addAttribute("active", active);	
		model.addAttribute("segsig", segsig);
		
		return "usuarios/listado";
	}
	
	/*@GetMapping("/mail/{mail}")
	public String findByMail(Model model, @PathVariable String mail) {
		model.addAttribute("usuario", usuarioService.findByMail(mail));
		return "usuarios/detalle";
	}*/
	
	@GetMapping("/perfil")
    public String findMyVideos(RedirectAttributes ra) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		ra.addAttribute("logueadoId", usuario.getId());
		ra.addAttribute("pinchadoId", usuario.getId());
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}/{page}/{active}";
	}
	
	@GetMapping("/perfil/{logueadoId}/{pinchadoId}/{page}/{active}")
    public String findMyVideos(@PathVariable long logueadoId, @PathVariable long pinchadoId, 
    		                   @PathVariable int page, @PathVariable int active, RedirectAttributes ra) {
		
		Usuario usuario = usuarioService.findById(pinchadoId);
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("page", page);
		ra.addAttribute("active", active);
		Set<Usuario> usuarios = new HashSet<Usuario>();
		ra.addAttribute("pages", videoService.myPages(usuario, usuarios));
		ra.addAttribute("search", "0");
		ra.addAttribute("visitas", 0);
		ra.addAttribute("gustas", 0);
		ra.addAttribute("titulo", 0);
		ra.addAttribute("descripcion", 0);
		ra.addAttribute("genero", 0);
		ra.addAttribute("user", 0);
		return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}";
	}
	
	@GetMapping("/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}")
	public String findMyVideos(Model model, @PathVariable long logueadoId, @PathVariable long pinchadoId, 
            				   @PathVariable int page, @PathVariable int active, @PathVariable int pages,
            				   @PathVariable String search, @PathVariable int visitas, @PathVariable int gustas,
							   @PathVariable int titulo, @PathVariable int descripcion, @PathVariable int genero,
							   @PathVariable int user) {
		
		String sigue = "";
		Pageable p = PageRequest.of(page, (int)videoService.getVIDEOS_POR_PAGINA());
		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario usuario = usuarioService.findById(pinchadoId);
		Usuario admin = usuarioService.findByRol("ADMIN");
		Set<Usuario> usuarios = new HashSet<Usuario>(); 
		List<Boolean> likes = new ArrayList<Boolean>();
		
		if(visitas == 1 || gustas == 1 || titulo == 1 || descripcion == 1 || genero == 1 || user == 1 || !search.equals("0")) {
			List<Video> videosMy = videoService.findMyVideos(usuario, usuarios);
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
			//usuario.getSiguiendo().add(usuario);
			Page<Video> videosMy = videoService.findMyPage(p, usuario, usuarios);
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
		model.addAttribute("page", page);
		model.addAttribute("active", active);
		
		return "usuarios/perfil";
	}
	
	@GetMapping("/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{sigue}")
	public String cambiarBoton(@PathVariable long logueadoId, @PathVariable long pinchadoId, 
			   				   @PathVariable int page, @PathVariable int active, @PathVariable int pages, 
			                   @PathVariable String sigue, RedirectAttributes ra) {
		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		if(!sigue.equals("hidden") && usuarioService.findBySiguiendo(logueado, pinchado)) {
			usuarioService.dejar(logueado, pinchado);
			sigue = "seguir";
		} else if(!sigue.equals("hidden")) {
			usuarioService.seguir(logueado, pinchado);
			sigue = "dejar";
		}
		
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("page", page);
		ra.addAttribute("active", active);
		ra.addAttribute("pages", pages);
		ra.addAttribute("sigue", sigue);
		
		return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}";
	}
	
	@GetMapping("/perfil/{id}")
	public String elimnarVideo(Model model, @PathVariable long id, RedirectAttributes ra) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Video video = videoService.findById(id);
		videoService.delete(video);
		String ext = video.getNombre();
		ext = ext.substring(ext.lastIndexOf("."));
		videoService.borrar(video.getId() + ext);
		
		ra.addAttribute("logueadoId", usuario.getId());
		ra.addAttribute("pinchadoId", usuario.getId());
		
		return "redirect:/usuarios/perfil";
	}
	
	@GetMapping("/fotos/{nombrefoto:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirFoto(@PathVariable String nombrefoto) {
		return usuarioService.descargar(nombrefoto);
    }
	
	@PostMapping("/detalle/{portal}")
	public String cambiarClave(@PathVariable String portal,
							   @RequestParam("oldpassword") String o,
							   @RequestParam("newpassword") String n, 
							   @RequestParam("rnewpassword") String r, 
			                   RedirectAttributes ra) {
		ra.addAttribute("id", usuarioService.cambiarClave(o, n, r));
		if(portal.equals("si"))	
    		return "redirect:/usuarios/detalle/{id}/{portal}";
    	else
    		return "redirect:/usuarios/detalle/{id}";
	}
	
	@PostMapping("/detalle/{id}/{portal}")
	public String saveDeletePerfilById(@PathVariable long id, @PathVariable String portal,
									   @RequestParam(value = "eliminar", required=false) String eliminar,
							  		   @RequestParam("foto") MultipartFile f, 
									   @RequestParam(value="quitarFoto", required=false) Boolean quitarFoto,
									   @RequestParam("nombre") String nombre,
									   @RequestParam("apellidos") String apellidos,
									   @RequestParam("intereses") String intereses,
									   RedirectAttributes ra) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		if(eliminar != null && !eliminar.equals("") && quitarFoto != null && quitarFoto) {
			usuarioService.deleteFoto(usuario);
			usuarioService.borrar(usuario);
			ra.addFlashAttribute("mensajeEliminar", "Foto " + f.getOriginalFilename() + " eliminada correctamente.");
		} else if(quitarFoto == null && eliminar != null && eliminar.equals("") && !f.getOriginalFilename().equals("") && appService.checkFilePhotoSize(f)) {	
    		usuarioService.subir(f);
    		usuarioService.update(usuario.getId(), false, nombre, apellidos, intereses);
    		ra.addFlashAttribute("mensajeSubir", "Foto " + f.getOriginalFilename() + " subida correctamente.");
    	} else if(!f.getOriginalFilename().equals(""))
    		ra.addFlashAttribute("mensaje", "Error, la foto ocupa más de 22 megas");
		
		ra.addAttribute("id", usuario.getId());
		
    	if(portal.equals("si"))	
    		return "redirect:/usuarios/detalle/{id}/{portal}";
    	else
    		return "redirect:/usuarios/detalle/{id}";
	}
	
	@PostMapping("/listado/{logueadoId}/{pinchadoId}/{segsig}")
    public String buscarUsuario(Model model, @RequestParam(value = "Nombre", required = false) Boolean nombre,
    									     @RequestParam(value = "Apellidos", required = false) Boolean apellidos,
    									     @RequestParam(value = "Busqueda") String busqueda,
    									     @PathVariable("logueadoId") long logueadoId,
    									     @PathVariable("pinchadoId") long pinchadoId,
    									     @PathVariable("segsig") String segsig,
    									     RedirectAttributes ra) {
		
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("segsig", segsig);
		
		if(nombre == null)
			ra.addAttribute("nombre", "0");
		else
			ra.addAttribute("nombre", "1");
		
		if(apellidos == null)
			ra.addAttribute("apellidos", "0");
		else
			ra.addAttribute("apellidos", "1");
		
		if(busqueda.equals(""))
			ra.addAttribute("search", "0");
		else
			ra.addAttribute("search", busqueda.toLowerCase());
		
		return "redirect:/usuarios/listado/{logueadoId}/{pinchadoId}/{segsig}/{nombre}/{apellidos}/{search}";
	}
	
	@PostMapping("/listado/{logueadoId}/{pinchadoId}/{segsig}/{nombre}")
    public String customPaginator(@RequestParam("UsuariosPorPagina") String items, 
    							  @PathVariable("logueadoId") long logueadoId,
    							  @PathVariable("pinchadoId") long pinchadoId,
    							  @PathVariable("segsig") String segsig,
    							  @PathVariable("nombre") String nombre,
    							  RedirectAttributes ra) {
		
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		if(items.equals("todos") && segsig.equals("siguiendo"))
			usuarioService.setUSUARIOS_POR_PAGINA((int)pinchado.getSiguiendo().size());
		else if(items.equals("todos") && segsig.equals("seguidores"))
			usuarioService.setUSUARIOS_POR_PAGINA((int)pinchado.getSeguidores().size());
		else
			usuarioService.setUSUARIOS_POR_PAGINA(Integer.parseInt(items));
		
		ra.addAttribute("logueadoId", logueadoId);
		ra.addAttribute("pinchadoId", pinchadoId);
		ra.addAttribute("nombre", "0");
		ra.addAttribute("apellidos", "0");
		ra.addAttribute("search", "0");
		
		return "redirect:/usuarios/listado/{logueadoId}/{pinchadoId}/{segsig}/{nombre}/{apellidos}/{search}";
	}
	
	@PostMapping("/perfil")
    public String buscarVideo(Model model, @RequestParam(value = "Visualizaciones", required = false) Boolean visitas,
    									   @RequestParam(value = "Likes", required = false) Boolean gustas,
    									   @RequestParam(value = "Titulo", required = false) Boolean titulo,
    									   @RequestParam(value = "Descripcion", required = false) Boolean descripcion,
    									   @RequestParam(value = "Genero", required = false) Boolean genero,
    									   @RequestParam(value = "Usuario", required = false) Boolean user,
    									   @RequestParam(value = "Busqueda", required = false) String busqueda,
    									   RedirectAttributes ra) {
    	ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		Pageable p = PageRequest.of(0, (int)videoService.getVIDEOS_POR_PAGINA());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Set<Usuario> usuarios = new HashSet<Usuario>();
		ra.addAttribute("pages", videoService.findMyPage(p, usuario, usuarios).getTotalPages());
		
		ra.addAttribute("logueadoId", usuario.getId());
		ra.addAttribute("pinchadoId", usuario.getId());
		
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

		return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}"; 
    }
	
	@PostMapping("/perfil/{pinchadoId}")
    public String customPaginator(@RequestParam("UsuariosPorPagina") String items,
    							  @PathVariable long pinchadoId,
    							  RedirectAttributes ra) {
		
		long videos = videoService.findVideosByUsuarioId(pinchadoId).size();
		
		if(items.equals("todos"))
			videoService.setVIDEOS_POR_PAGINA((int)videos);
		else
			videoService.setVIDEOS_POR_PAGINA(Integer.parseInt(items));
		
		Pageable p = PageRequest.of(0, (int)videoService.getVIDEOS_POR_PAGINA());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		Set<Usuario> usuarios = new HashSet<Usuario>();
		
		ra.addAttribute("page", 0);
		ra.addAttribute("active", 0);
		ra.addAttribute("pages", videoService.findMyPage(p, usuario, usuarios).getTotalPages());
		ra.addAttribute("logueadoId", usuario.getId());
		ra.addAttribute("pinchadoId", usuario.getId());
		ra.addAttribute("search", "0");
    	ra.addAttribute("visitas", 0);
    	ra.addAttribute("gustas", 0);
    	ra.addAttribute("titulo", 0);
    	ra.addAttribute("descripcion", 0);
    	ra.addAttribute("genero", 0);
    	ra.addAttribute("user", 0);

		return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}/{page}/{active}/{pages}/{search}/{visitas}/{gustas}/{titulo}/{descripcion}/{genero}/{user}"; 
    }
	
	@PostMapping("/perfil/{logueadoId}/{pinchadoId}")
    public String subirFoto(@RequestParam("foto") MultipartFile f, RedirectAttributes ra) {
		
		if(appService.checkFilePhotoSize(f)) {	
			usuarioService.subir(f);
			ra.addFlashAttribute("mensajeSubir", "Foto " + f.getOriginalFilename() + " subida correctamente.");
		} else
    		ra.addFlashAttribute("mensaje", "Error, la foto ocupa más de 22 megas");

        return "redirect:/usuarios/perfil";
    }
	
	@PostMapping("/perfil/{logueadoId}/{pinchadoId}/{videoId}/{views}")
    public String saveVisit(Model model, @PathVariable long logueadoId,
			 							 @PathVariable long pinchadoId,
			 							 @PathVariable long videoId,
			 							 @PathVariable long views) {
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
		
		return "redirect:/usuarios/perfil";
	}
	
	/*@PutMapping("/{id}")	
	public String update(Model model, @PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {
		model.addAttribute("usuario", usuarioService.update(id, u));
		return "usuarios/detalle";
	}
	
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable(value = "id") Long id) {
		model.addAttribute("usuario", usuarioService.delete(id));
        return "usuarios/listado";
	}*/
}
