package vmc.controller.web;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vmc.model.Usuario;
import vmc.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public String findAll(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/listado";
	}
	
	@GetMapping("/{id}")
	public String findById(Model model, @PathVariable long id) {
		model.addAttribute("usuario", usuarioService.findById(id));
		return "usuarios/detalle";
	}
	
	@GetMapping("/mail/{mail}")
	public String findByMail(Model model, @PathVariable String mail) {
		model.addAttribute("usuario", usuarioService.findByMail(mail));
		return "usuarios/detalle";
	}
	
	@GetMapping("/perfil")
	public String findVideosByUsuarioId(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		model.addAttribute("videos", usuarioService.findVideosByUsuarioId(usuario.getId()));
		model.addAttribute("usuario", usuario);
		return "usuarios/perfil";
	}
	
	@PutMapping("/{id}")	
	public String update(Model model, @PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {
		model.addAttribute("usuario", usuarioService.update(id, u));
		return "usuarios/detalle";
	}
	
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable(value = "id") Long id) {
		model.addAttribute("usuario", usuarioService.delete(id));
        return "usuarios/listado";
	}

}
