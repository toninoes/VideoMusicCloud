package vmc.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import vmc.exception.CampoUnicoException;
import vmc.exception.ErrorInternoServidorException;
import vmc.exception.RecursoNoEncontradoException;
import vmc.model.Usuario;
import vmc.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioService;
	
	public List<Usuario> findAll() {
		return usuarioService.findAll();
	}
	
	public Usuario findById(@PathVariable long id) {
		Usuario usuario = usuarioService.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		return usuario;
	}	
	
	public Usuario findByMail(@PathVariable String mail) {
		Usuario usuario = usuarioService.findByMail(mail)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "mail", mail));
		
		return usuario;
	}
	
	public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario u) {
		
		Usuario usuario = new Usuario();
		
		// control unicidad de mail
		if(usuarioService.findByMail(u.getMail()).isPresent())
			throw new CampoUnicoException("Usuario", "mail", u.getMail());
		
		try {
			usuario = usuarioService.save(u);
		} catch (ErrorInternoServidorException e) {
			throw new ErrorInternoServidorException("guardar", "Usuario", u.getId(), e.getMessage());
		}
		
        return new ResponseEntity<Usuario>(usuario, HttpStatus.CREATED);
    }
	
	public ResponseEntity<Usuario> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {

		Usuario usuario = usuarioService.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		try {
			usuario.setNombre(u.getNombre());
			usuario.setApellidos(u.getApellidos());
			usuarioService.save(usuario);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("actualizar", "Usuario", id, e.getMessage());
		}
		
		return new ResponseEntity<Usuario>(HttpStatus.OK);
	}
	
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		Usuario usuario = usuarioService.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

	    try {
	    	usuarioService.delete(usuario);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("borrar", "Usuario", id, e.getMessage());
		}

	    return ResponseEntity.ok().build();
	}

}
