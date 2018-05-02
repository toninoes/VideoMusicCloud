package vmc.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import vmc.exception.AlmacenamientoFicheroNoEncontradoException;
import vmc.exception.RecursoNoEncontradoException;
import vmc.model.Comentario;
import vmc.model.Genero;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.repository.GeneroRepository;
import vmc.repository.UsuarioRepository;
import vmc.repository.VideoRepository;

@Service
public class VideoService {
	
	@Autowired
	private AlmacenamientoService almacenamientoService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private GeneroService generoService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private GeneroRepository generoRepository;
	
	@Autowired
	private EntityManager em;
	
	public List<Video> findAll() {   	
		return videoRepository.findAll();
    }
	
	public List<Video> findVideosByUsuarioId(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
		
		return videoRepository.findByUsuario(usuario);
	}
	
	@SuppressWarnings("unchecked")
	public List<Video> findSearch(boolean nuevos, boolean visitas, boolean gustas, boolean titulo, 
			                      boolean descripcion, boolean genero, boolean comentario, String busqueda) {
		
		Comentario c = new Comentario();
		c.setDescripcion(busqueda);
		
		StringBuilder query = new StringBuilder("SELECT v FROM Video v ");	
		String str = new String("");
		
		if(titulo && descripcion)
			str = "AND ";
		
		if(titulo)
			query.append("WHERE v.titulo LIKE '%" + busqueda + "%' ").append(str);
		if(descripcion)
			query.append("WHERE v.descripcion LIKE '%" + busqueda + "%' ");
		
		if(nuevos && visitas && gustas)
			query.append("ORDER BY v.creacion DESC, v.visualizaciones DESC, v.likes DESC");
		else if(nuevos && visitas)
			query.append("ORDER BY v.creacion DESC, v.visualizaciones DESC");
		else if(nuevos && gustas)
			query.append("ORDER BY v.creacion DESC, v.likes DESC");
		else if(visitas && gustas)
			query.append("ORDER BY v.visualizaciones DESC, v.likes DESC");
		else if(nuevos)
			query.append("ORDER BY v.creacion DESC");
		else if(visitas)
			query.append("ORDER BY v.visualizaciones DESC");
		else if(gustas)
			query.append("ORDER BY v.likes DESC");
		
		if(comentario)
			return videoRepository.findVideoByComentario(c.getVideo());
		if(genero) {
			List<Video> videos1 = videoRepository.findAll();
			List<Video> videos2 = new ArrayList<Video>();
			Set<Genero> gen;
			boolean sw;
			for(Video v : videos1) {
				gen = generoRepository.findGenerosByVideo(v);
				sw = false;
				for(Genero g: gen)
					if(g.getNombre().contains(busqueda) && !sw) {
						videos2.add(v);
						sw = true;
					}
			}
			return videos2;
		} else
			return (List<Video>) em.createQuery(query.toString()).getResultList();
	}
	
	@ResponseBody
    public ResponseEntity<Resource> descargar(@PathVariable String nombrevideo) {

        Resource video = almacenamientoService.loadAsResource(nombrevideo, "video");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; nombrevideo=\"" + video.getFilename() + "\"").body(video);
    }
	
	public void borrar(@PathVariable String filename) {
        almacenamientoService.delete(filename, "video");
    }
	
	public ResponseEntity<?> subir(@RequestParam("titulo") String t, @RequestParam("video") MultipartFile v, 
			                       @RequestParam("descripcion") String d, @RequestParam("videogeneros") String[] g) {
	
    	if(esUnVideo(v)) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		Usuario usuario = usuarioService.findByMail(auth.getName());
    		
    		Set<Genero> generos = new HashSet<Genero>();
        	for(String s: g) {
        		Genero genero = generoService.findByNombre(s);
        		generos.add(genero);
        	}
    		
    		Video video = new Video(t, v.getOriginalFilename(), usuario, d);
    		
    		video.setVideoGeneros(generos);
    		videoRepository.save(video);		
        	almacenamientoService.store(v, "video");
        	
        	return new ResponseEntity<Video>(video, HttpStatus.CREATED);
    	}else
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);    
    }
	
	@ExceptionHandler(AlmacenamientoFicheroNoEncontradoException.class)
    public ResponseEntity<?> manejarAlmacenamientoFicheroNoEncontrado(AlmacenamientoFicheroNoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }
	
	private Boolean esUnVideo(MultipartFile f) {
		return(
				f.getContentType().equals("video/mp4")		||	// .mp4	MP4 video
				f.getContentType().equals("video/webm")		||	// .webm	WEBM video
				f.getContentType().equals("video/ogg")		||	// .ogv	OGG video
				f.getContentType().equals("video/x-msvideo")||	// .avi	AVI: Audio Video Interleave
				f.getContentType().equals("video/mpeg")		||	// .mpeg	MPEG Video
				f.getContentType().equals("video/3gpp")		||	// .3gp	3GPP audio/video container
				f.getContentType().equals("video/3gpp2")	||	// .3g2	3GPP2 audio/video container
				f.getContentType().equals("video/x-ms-wmv") ||
				f.getContentType().equals("video/x-msvideo")
				);		
	}

}
