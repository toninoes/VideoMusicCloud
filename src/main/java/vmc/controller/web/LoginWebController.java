package vmc.controller.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import vmc.model.Usuario;
import vmc.service.UsuarioService;


@Controller
public class LoginWebController {

	@Autowired
	private UsuarioService usuarioService;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		Usuario user = new Usuario();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid Usuario user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		Usuario userExists = usuarioService.findByMail(user.getMail());
		if (userExists != null) {
			bindingResult
					.rejectValue("mail", "error.user",
							"Ya existe un usuario registrado con ese mail");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			usuarioService.saveUser(user);
			modelAndView.addObject("successMessage", "Usuario registrado correctamente");
			modelAndView.addObject("user", new Usuario());
			modelAndView.setViewName("registration");
			
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario user = usuarioService.findByMail(auth.getName());
		modelAndView.addObject("userName", "Bienvenido " + user.getNombre() + " " + user.getApellidos() + " (" + user.getMail() + ")");
		modelAndView.addObject("adminMessage","Este contenido está sólo disponible para Usuarios administradores");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
}
