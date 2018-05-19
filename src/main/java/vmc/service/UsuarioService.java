package vmc.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	private int USUARIOS_POR_PAGINA = 8;
	
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
	
	public int getUSUARIOS_POR_PAGINA() {
		return USUARIOS_POR_PAGINA;
	}
	
	public void setUSUARIOS_POR_PAGINA(int USUARIOS_POR_PAGINA) {
		this.USUARIOS_POR_PAGINA = USUARIOS_POR_PAGINA;
	}
	
	public int allPages(Pageable p, long total, String segsig) {
		return (int)Math.ceil(total*(1.0) / USUARIOS_POR_PAGINA*(1.0));
	}
	
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
	
	public Page<Usuario> findAllSiguiendo(Pageable p, Set<Usuario> usuarios) {   	
		return usuarioRepository.findAllSiguiendo(p, usuarios);
    }
	
	public Page<Usuario> findAllSeguidores(Pageable p, Set<Usuario> usuarios) {   	
		return usuarioRepository.findAllSeguidores(p, usuarios);
    }
	
	public Usuario findById(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		return usuario;
	}	
	
	public Usuario findByMail(@PathVariable String mail) {
		return usuarioRepository.findByMail(mail);
	}
	
	public Usuario findByRol(String roladmin) {
		return usuarioRepository.findByRolAdmin(rolRepository.findByRol(roladmin));	
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
	
	/* No funciona con multipart/form-data */
	public ResponseEntity<Usuario> update(@PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		try {
			usuario.setNombre(u.getNombre());
			usuario.setApellidos(u.getApellidos());
			usuario.setIntereses(u.getIntereses());
			usuarioRepository.save(usuario);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("actualizar", "Usuario", id, e.getMessage());
		}
		
		return new ResponseEntity<Usuario>(HttpStatus.OK);
	}
	
	/* Aquí sí funciona y no sé si el de arriba se utilizará en un futuro - método sobrecargado update */
	public ResponseEntity<Usuario> update(@PathVariable(value = "id") Long id, @RequestParam(required = false) boolean quitarFoto,
																			   @RequestParam("nombre") String nombre,
																			   @RequestParam("apellidos") String apellidos,
																			   @RequestParam("intereses") String intereses) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));

		try {
			usuario.setNombre(nombre);
			usuario.setApellidos(apellidos);
			usuario.setIntereses(intereses);
			if(quitarFoto) usuario.setFoto("");
			else {
				String ext = usuario.getFoto();
				ext = ext.substring(ext.lastIndexOf("."));
				usuario.setFoto(usuario.getId() + ext);
			}
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
	
	public void borrar(Usuario usuario) {
		String ext = usuario.getFoto();
		if(!ext.equals(""))
			ext = ext.substring(ext.lastIndexOf("."));
		else
			ext = ".jpg";
        almacenamientoService.delete(usuario.getId() + ext, "foto");
    }
	
	public void deleteFoto(Usuario usuario) {
        usuarioRepository.updateByFoto(usuario.getId());
    }
	
	public long cambiarClave(@RequestParam("oldpassword") String o,
							 @RequestParam("newpassword") String n,
			  				 @RequestParam("rnewpassword") String r) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = findByMail(auth.getName());
		BCryptPasswordEncoder old = bCryptPasswordEncoder;
		
		if(old.matches(o, usuario.getPassword()) && n.equals(r) && n.length() >= 6) {
			BCryptPasswordEncoder nueva = bCryptPasswordEncoder;
			usuario.setPassword(nueva.encode(n));
			usuarioRepository.save(usuario);
		} else
			throw new ErrorInternoServidorException("Passwords", "Usuario", usuario.getId(), "Algunas de las contraseñas no coinciden");
		
		return usuario.getId();
	}
	
	public ResponseEntity<?> subir(@RequestParam("foto") MultipartFile f) {
		
    	if(esUnaFoto(f)) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		Usuario usuario = findByMail(auth.getName());
    		
    		String ext = f.getOriginalFilename();
    		ext = ext.substring(ext.lastIndexOf("."));
    		
    		usuario.setFoto(usuario.getId() + ext);
    		usuarioRepository.save(usuario);
    		
        	almacenamientoService.store(f, "foto", usuario.getId() + ext);
        	
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
    	pinchado.setSeguidor(logueado);
    	usuarioRepository.save(logueado);
    	usuarioRepository.save(pinchado);
    }
	
	public void dejar(Usuario logueado, Usuario pinchado) {
    	logueado.removeSiguiendo(pinchado);
    	pinchado.removeSeguidor(logueado);
    	usuarioRepository.save(logueado);
    	usuarioRepository.save(pinchado);
    }
	
	public Page<Usuario> seguidores(Pageable p, Set<Usuario> usuarios) {
		return usuarioRepository.findAllSeguidores(p, usuarios);
	}
	
	public Page<Usuario> siguiendo(Pageable p, Set<Usuario> usuarios) {
		return usuarioRepository.findAllSiguiendo(p, usuarios);
	}
	
	public List<Usuario> findSearch(String busqueda) {
		return usuarioRepository.findByUsuarioSearch(busqueda);
	}
	
	public Page<Usuario> findPageSearch(Pageable p, String busqueda, String opcion, long id) {
		switch(opcion) {
			case "nombre": return usuarioRepository.findByUsuarioSearchNombre(p, busqueda, id);
			case "apellidos": return usuarioRepository.findByUsuarioSearchApellidos(p, busqueda, id);
			case "nombreapellidos": return usuarioRepository.findByUsuarioSearchNombreApellidos(p, busqueda, busqueda, id);
			default: return usuarioRepository.findByUsuarioSearch(p, busqueda, id);
		}
	}
	
	@ExceptionHandler(AlmacenamientoFicheroNoEncontradoException.class)
    public ResponseEntity<?> manejarAlmacenamientoFicheroNoEncontrado(AlmacenamientoFicheroNoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }
	
	private Boolean esUnaFoto(MultipartFile f) {
		return(
				f.getContentType().equals("image/gif")		||	// .gif	GIF images (lossless compression, superseded by PNG)
				f.getContentType().equals("image/jpeg")		||	// .jpeg JPEG images
				f.getContentType().equals("image/png")		||	// .png	PNG images
				f.getContentType().equals("image/svg+xml")	    // .svg	SVG images (vector images)
				);
	}

}