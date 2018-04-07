package vmc.controller.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
			usuarioService.create(u);
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
		modelAndView.addObject("usuario", "Bienvenido " + usuario.getNombre() + " " + usuario.getApellidos() + " (" + usuario.getMail() + ")");
		modelAndView.addObject("mensaje","Portal de Administración");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
}
