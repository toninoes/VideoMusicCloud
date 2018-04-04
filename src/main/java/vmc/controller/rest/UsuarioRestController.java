package vmc.controller.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vmc.model.Usuario;
import vmc.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public List<Usuario> findAll() {
		return usuarioService.findAll();
	}
	
	@GetMapping("/{id}")
	public Usuario findById(Model model, @PathVariable long id) {
		return usuarioService.findById(id);
	}
	
	@GetMapping("/mail/{mail}")
	public Usuario findByMail(@PathVariable String mail) {
		return usuarioService.findByMail(mail);
	}
	
	@PostMapping
	public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario u) {
        return usuarioService.create(u);
    }
	
	@PutMapping("/{id}")	
	public ResponseEntity<Usuario> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {
		return usuarioService.update(id, u);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		return usuarioService.delete(id);
	}

}
