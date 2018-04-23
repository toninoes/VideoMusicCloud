package vmc.service;

import java.util.List;
import java.util.Set;

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
import vmc.model.Genero;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.repository.UsuarioRepository;
import vmc.repository.VideoRepository;

@Service
public class VideoService {
	
	@Autowired
	private AlmacenamientoService almacenamientoService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoRepository videoRepository;
	
	public List<Video> findAll() {   	
		return videoRepository.findAll();
    }
	
	public List<Video> findVideosByUsuarioId(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
		
		return videoRepository.findByUsuario(usuario);
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
			                       @RequestParam("descripcion") String d, @RequestParam("videogeneros") Set<Genero> g) {
		
    	if(esUnVideo(v)) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		Usuario usuario = usuarioService.findByMail(auth.getName());
    		
    		Video video = new Video(t, v.getOriginalFilename(), usuario, d);
    		video.setVideoGeneros(g);
    		videoRepository.save(video);		
        	almacenamientoService.store(v, "video");
        	
        	return new ResponseEntity<Video>(video, HttpStatus.CREATED);
    	}else {
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    	}        
    }
	
	@ExceptionHandler(AlmacenamientoFicheroNoEncontradoException.class)
    public ResponseEntity<?> manejarAlmacenamientoFicheroNoEncontrado(AlmacenamientoFicheroNoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }
	
	private Boolean esUnVideo(MultipartFile f) {
		Boolean res = false;
		
		if(
				f.getContentType().equals("video/mp4")		||	// .mp4	MP4 video
				f.getContentType().equals("video/webm")		||	// .webm	WEBM video
				f.getContentType().equals("video/ogg")		||	// .ogv	OGG video
				f.getContentType().equals("video/x-msvideo")||	// .avi	AVI: Audio Video Interleave
				f.getContentType().equals("video/mpeg")		||	// .mpeg	MPEG Video
				f.getContentType().equals("video/3gpp")		||	// .3gp	3GPP audio/video container
				f.getContentType().equals("video/3gpp2")		// .3g2	3GPP2 audio/video container
			)		
			res = true;
		
		return res;
	}

}
