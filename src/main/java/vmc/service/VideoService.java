package vmc.service;

import java.util.ArrayList;
import java.util.HashSet;
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
	
	public List<Video> findAll() {   	
		return videoRepository.findAll();
    }
	
	public List<Video> findVideosByUsuarioId(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
		
		return videoRepository.findByUsuario(usuario);
	}
	
	public List<Video> findSearch(List<Video> videosAll, boolean visitas, boolean gustas, boolean titulo, 
			                      boolean descripcion, boolean genero, boolean user, String busqueda) {
		
		List<Video> videos = null;
		String option = "", caso = "", ejecutar = "No";
		
		int vi = 0, gu = 0, ti = 0, de = 0, ge = 0;
		
		if(visitas) vi = 16; 
		if(gustas) gu = 8; 
		if(titulo) ti = 4; 
		if(descripcion) de = 2; 
		if(genero) ge = 1;
		
		int codigo = vi + gu + ti + de + ge;
		
		switch(codigo) {	
			case 0: videos = videoRepository.findAll(); break; // [nuevos] y todos
			case 1: ejecutar = "Si"; break; //[nuevos] y genero			
			case 2: videos = videoRepository.findByDescripcion(busqueda.toLowerCase()); break; //[nuevos] y descripcion				
			case 3: option = "Descripcion"; break; //[nuevos], descripcion y genero			
			case 4: videos = videoRepository.findByTitulo(busqueda.toLowerCase()); break; //[nuevos] y titulo			
			case 5: option = "Titulo"; break; //[nuevos], titulo y genero			
			case 6: videos = videoRepository.findByTituloDescripcion(busqueda.toLowerCase()); break; //[nuevos], titulo y descripcion			
			case 7: option = "TituloDescripcion"; break; //[nuevos], titulo, descripcion y genero			
			case 8: videos = videoRepository.findByLikes(); break; //[nuevos] y gustas			
			case 9: ejecutar = caso = "Likes"; break; //[nuevos], gustas y genero			
			case 10: videos = videoRepository.findByLikesDescripcion(busqueda.toLowerCase()); break; //[nuevos], gustas y descripcion			
			case 11: ejecutar = caso = "Likes"; option = "Descripcion"; break; //[nuevos], gustas, descripcion y genero			
			case 12: videos = videoRepository.findByLikesTitulo(busqueda.toLowerCase()); break; //[nuevos], gustas y titulo			
			case 13: ejecutar = caso = "Likes"; option = "Titulo"; break; //[nuevos], gustas, titulo y genero			
			case 14: videos = videoRepository.findByLikesTituloDescripcion(busqueda.toLowerCase()); break; //[nuevos], gustas, titulo y descripcion			
			case 15: ejecutar = caso = "Likes"; option = "TituloDescripcion"; break; //[nuevos], gustas, titulo, descripcion y genero			
			case 16: videos = videoRepository.findByVisualizaciones(); break; //[nuevos] y visitas			
			case 17: ejecutar = caso = "Visitas"; break; //[nuevos], visitas y genero			
			case 18: videos = videoRepository.findByVisualizacionesDescripcion(busqueda.toLowerCase()); break; //[nuevos], visitas y descripcion			
			case 19: ejecutar = caso = "Visitas"; option = "Descripcion"; break; //[nuevos], visitas, descripcion y genero			
			case 20: videos = videoRepository.findByVisualizacionesTitulo(busqueda.toLowerCase()); break; //[nuevos], visitas y titulo			
			case 21: ejecutar = caso = "Visitas"; option = "Titulo"; break; //[nuevos], visitas, titulo y genero			
			case 22: videos = videoRepository.findByVisualizacionesTituloDescripcion(busqueda.toLowerCase()); break; //[nuevos], visitas, titulo y descripcion			
			case 23: ejecutar = caso = "Visitas"; option = "TituloDescripcion"; break; //[nuevos], visitas, titulo, descripcion y genero			
			case 24: videos = videoRepository.findByVisualizacionesLikes(); break; //[nuevos], visitas y gustas			
			case 25: ejecutar = caso = "VisitasLikes"; break; //[nuevos], visitas, gustas y genero			
			case 26: videos = videoRepository.findByVisualizacionesLikesDescripcion(busqueda.toLowerCase()); break; //[nuevos], visitas, gustas y descripcion			
			case 27: ejecutar = caso = "VisitasLikes"; option = "Descripcion"; break; //[nuevos], visitas, gustas, descripcion y genero			
			case 28: videos = videoRepository.findByVisualizacionesLikesTitulo(busqueda.toLowerCase()); break; //[nuevos], visitas, gustas y titulo			
			case 29: ejecutar = caso = "VisitasLikes"; option = "Titulo"; break; //[nuevos], visitas, gustas, titulo y genero			   			
			case 30: videos = videoRepository.findByVisualizacionesLikesTituloDescripcion(busqueda.toLowerCase()); break; //[nuevos], visitas, gustas, titulo y descripcion			
			case 31: ejecutar = caso = "VisitasLikes"; option = "TituloDescripcion"; break; //[nuevos], visitas, gustas, titulo, descripcion y genero
		}
		
		if(!ejecutar.equals("No"))
			videos = getVideosGeneros(videosAll, busqueda.toLowerCase(), option, caso);
		
		if(user)
			videos = getVideosGeneros(videos, busqueda.toLowerCase());
		
		return videos;
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
	
	private List<Video> getVideosGeneros(List<Video> videosAll, String busqueda, String option, String caso) {
	
		switch(caso) {
			case "Likes": videosAll = videoRepository.findByLikes(); break;
			case "Visitas": videosAll = videoRepository.findByVisualizaciones(); break;
			case "VisitasLikes": videosAll = videoRepository.findByVisualizacionesLikes(); break;
		}
		
		List<Video> videosGeneros = new ArrayList<Video>();
		Set<Genero> gen;
		boolean sw;
		for(Video v : videosAll) {
			gen = generoRepository.findGenerosByVideo(v);
			sw = false;
			for(Genero g: gen) {
				String lowerNombre = g.getNombre().toLowerCase();
				switch(option) {
					case "": if((lowerNombre.contains(busqueda) || busqueda.contains(lowerNombre)) && !sw) {
								videosGeneros.add(v);
								sw = true;
							 } break;
					case "Descripcion": if((lowerNombre.contains(busqueda) || busqueda.contains(lowerNombre)) && 
					                       (v.getDescripcion().toLowerCase().contains(busqueda) || 
					                        busqueda.contains(v.getDescripcion().toLowerCase())) && !sw) {
											videosGeneros.add(v);
											sw = true;
										} break;
					case "Titulo": if((lowerNombre.contains(busqueda) || busqueda.contains(lowerNombre)) && 
									  (v.getTitulo().toLowerCase().contains(busqueda) || 
									   busqueda.contains(v.getTitulo().toLowerCase())) && !sw) {
											videosGeneros.add(v);
											sw = true;
									} break;
					case "TituloDescripcion": if(((lowerNombre.contains(busqueda) || busqueda.contains(lowerNombre)) && 
												 (v.getTitulo().toLowerCase().contains(busqueda) || 
												  busqueda.contains(v.getTitulo().toLowerCase())) ||
												 (v.getDescripcion().toLowerCase().contains(busqueda) || 
												  busqueda.contains(v.getDescripcion().toLowerCase()))) && !sw) {
													videosGeneros.add(v);
													sw = true;
											  } break;
			   }
			}
		}	
		return videosGeneros;
	}
	
	private List<Video> getVideosGeneros(List<Video> videos, String busqueda) {
		List<Usuario> usuario = usuarioRepository.findByUsuarioSearch(busqueda);
		videos = videoRepository.findByUsuarioSearch(usuario);
		return videos;
	}
	
}