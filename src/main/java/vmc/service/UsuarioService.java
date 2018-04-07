package vmc.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import vmc.exception.ErrorInternoServidorException;
import vmc.exception.RecursoNoEncontradoException;
import vmc.model.Rol;
import vmc.model.Usuario;
import vmc.repository.RolRepository;
import vmc.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
    private RolRepository rolRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
	
	public Usuario findById(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		return usuario;
	}	
	
	public Usuario findByMail(@PathVariable String mail) {
		return usuarioRepository.findByMail(mail);
	}
	
	public void create(Usuario user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActivo(true);
        Rol userRole = rolRepository.findByRol("USER");
        user.setRoles(new HashSet<Rol>(Arrays.asList(userRole)));
		usuarioRepository.save(user);
	}
	
	public ResponseEntity<Usuario> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		try {
			usuario.setNombre(u.getNombre());
			usuario.setApellidos(u.getApellidos());
			usuarioRepository.save(usuario);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("actualizar", "Usuario", id, e.getMessage());
		}
		
		return new ResponseEntity<Usuario>(HttpStatus.OK);
	}
	
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

	    try {
	    	usuarioRepository.delete(usuario);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("borrar", "Usuario", id, e.getMessage());
		}

	    return ResponseEntity.ok().build();
	}

}
