package vmc.controller.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vmc.model.Usuario;
import vmc.service.UsuarioService;


@Controller
public class LoginWebController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping({"/", "/login"})
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	@GetMapping("/registro")
	public ModelAndView registro(){
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = new Usuario();
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("registro");
		return modelAndView;
	}
	
	@GetMapping("/registro/{portal}")
	public ModelAndView registro(@PathVariable String portal){
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = new Usuario();
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("registro");
		modelAndView.addObject("portal", "si");
		return modelAndView;
	}
	
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
	
	@GetMapping("/admin/home/{busqueda}")
	public ModelAndView searchHome(@PathVariable String busqueda){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		List<Usuario> usuarios = usuarioService.findAll();
		usuarios.remove(usuario);
		List<Usuario> uss = usuarioService.findSearch(busqueda);
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.addObject("usuarios", uss);
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
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
	
	@DeleteMapping
	public ModelAndView deleteUser(@RequestParam(value = "uboxes", required=false) long[] uboxes) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		if(uboxes != null)
			for(long id: uboxes)
				usuarioService.delete(id);
		List<Usuario> usuarios = usuarioService.findAll();
		usuarios.remove(usuario);
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
}
