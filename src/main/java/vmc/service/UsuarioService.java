package vmc.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import vmc.exception.AlmacenamientoFicheroNoEncontradoException;
import vmc.exception.ErrorInternoServidorException;
import vmc.exception.RecursoNoEncontradoException;
import vmc.model.Rol;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.repository.RolRepository;
import vmc.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private AlmacenamientoService almacenamientoService;

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private VideoService videoService;
	
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
	
	public List<Video> findVideosByUsuarioId(@PathVariable long id) {
		usuarioRepository.findById(id)
	    				 .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
		
		return videoService.findVideosByUsuarioId(id);
	}
	
	public void create(Usuario user, Boolean esAdmin) {
		Rol userRole;
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActivo(true);
        
        if(esAdmin)
        	userRole = rolRepository.findByRol("ADMIN");
        else        	
        	userRole = rolRepository.findByRol("USER");
        
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
	
	@ResponseBody
    public ResponseEntity<Resource> descargar(@PathVariable String nombrefoto) {

        Resource foto = almacenamientoService.loadAsResource(nombrefoto, "foto");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; nombrefoto=\fotos\"" + foto.getFilename() + "\"").body(foto);
    }
	
	public void borrar(@PathVariable String filename) {
        almacenamientoService.delete(filename, "foto");
    }
	
	public ResponseEntity<?> subir(@RequestParam("foto") MultipartFile f) {
		
    	if(esUnaFoto(f)) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		Usuario usuario = findByMail(auth.getName());
    		
    		usuario.setFoto(f.getOriginalFilename());
    		usuarioRepository.save(usuario);
    		
        	almacenamientoService.store(f, "foto");
        	
        	return new ResponseEntity<Usuario>(usuario, HttpStatus.CREATED);
    	}else {
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    	}        
    }
	
	public boolean findBySiguiendo(Usuario logueado, Usuario pinchado) {
		return logueado.getSiguiendo().contains(pinchado);
	}
	
	public void seguir(Usuario logueado, Usuario pinchado) {
    	logueado.setSiguiendo(pinchado);
    	usuarioRepository.save(logueado);
    }
	
	public void dejar(Usuario logueado, Usuario pinchado) {
    	logueado.removeSiguiendo(pinchado);
    	usuarioRepository.save(logueado);
    }
	
	@ExceptionHandler(AlmacenamientoFicheroNoEncontradoException.class)
    public ResponseEntity<?> manejarAlmacenamientoFicheroNoEncontrado(AlmacenamientoFicheroNoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }
	
	private Boolean esUnaFoto(MultipartFile f) {
		Boolean res = false;
		
		if(
				f.getContentType().equals("image/gif")		||	// .gif	GIF images (lossless compression, superseded by PNG)
				f.getContentType().equals("image/jpeg")		||	// .jpeg JPEG images
				f.getContentType().equals("image/png")		||	// .png	PNG images
				f.getContentType().equals("image/svg+xml")	    // .svg	SVG images (vector images)

			)		
			res = true;
		
		return res;
	}

}
