package vmc.controller.web;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vmc.model.Usuario;
import vmc.model.Video;
import vmc.service.UsuarioService;
import vmc.service.VideoService;


@Controller
public class LoginWebController {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoService videoService;
	
	/*
	 * GET METHODS - LOGIN - REGISTRO - ADMIN 
	 * 
	 */
	
	/*
	 * GET - LOGIN
	 */

	@GetMapping({"/", "/login"})
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	/*
	 * GET - REGISTRO (NO PORTAL)
	 */
	
	@GetMapping("/registro")
	public ModelAndView registro(){
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = new Usuario();
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("registro");
		return modelAndView;
	}
	
	/*
	 * GET - REGISTRO (PORTAL)
	 */
	
	@GetMapping("/registro/{portal}")
	public ModelAndView registro(@PathVariable String portal){
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = new Usuario();
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("registro");
		modelAndView.addObject("portal", "si");
		return modelAndView;
	}
	
	/*
	 * GET - PORTAL ADMIN
	 */
	
	@GetMapping("/admin/home")
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Usuario> usuarios = usuarioService.findAll();
		usuarios.remove(usuario);
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
	/*
	 * GET - PORTAL ADMIN - BUSQUEDA DE USUARIOS - ROL USER
	 */
	
	@GetMapping("/admin/home/{busqueda}")
	public ModelAndView searchHome(@PathVariable String busqueda){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Usuario> usuarios = usuarioService.findAll();
		usuarios.remove(usuario);
		Set<Usuario> uss = usuarioService.findSearch(busqueda);
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.addObject("usuarios", uss);
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
	/*
	 * POST METHODS - REGISTRO - ADMIN
	 * 
	 */
	
	/*
	 * POST - REGISTRO
	 */
	
	@PostMapping("/registro")
	public ModelAndView create(@Valid Usuario u, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = usuarioService.findByMail(u.getMail());
		if (usuario != null) {
			bindingResult
					.rejectValue("mail", "error.user",
							"Ya existe un usuario registrado con ese mail");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registro");
		} else {
			usuarioService.create(u, false);
			modelAndView.addObject("mensaje", "Usuario registrado correctamente");
			modelAndView.addObject("usuario", new Usuario());
			modelAndView.setViewName("registro");
			
		}
		return modelAndView;
	}
	
	/*
	 * POST - ADMIN
	 */
	
	@PostMapping("/admin/home")
	public ModelAndView formHome(@RequestParam(value = "uboxes", required=false) long[] uboxes){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Usuario> usuarios = usuarioService.findAll();
		usuarios.remove(usuario);
		long[] cboxes = new long[usuarios.size()];
		if(uboxes != null) {
			for(int i = 0; i < uboxes.length; i++) {
				cboxes[i] = uboxes[i];
				Usuario us = usuarioService.findById(cboxes[i]);
				us.setActivo(!us.getActivo());
				usuarioService.update(cboxes[i], us);
				List<Video> videos = videoService.findVideosByUsuarioId(us.getId());
				for(Video v : videos) {
					if(us.getActivo())
						videoService.update(v, false);
					else
						videoService.update(v, true);
				}
			}
			modelAndView.addObject("cboxes", cboxes);
		} else {
			for(int i = 0; i < cboxes.length; i++)
				cboxes[i] = 0;
			modelAndView.addObject("cboxes", cboxes);
		}
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
}
