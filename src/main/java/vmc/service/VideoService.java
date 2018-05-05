package vmc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	public Video findById(@PathVariable long videoId) {
		return videoRepository.findById(videoId);
	}
	
	public List<Video> findVideosByUsuarioId(@PathVariable long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "id", id));
		
		return videoRepository.findByUsuario(usuario);
	}
	
	public List<Video> findSearch(List<Video> videosAll, boolean visitas, boolean gustas, boolean titulo, 
			                      boolean descripcion, boolean genero, boolean user, String busqueda) {
		
		List<Video> videos = null, videosViGu = null, videosTiDe = null, videosGe = null, videosUs = null;
		String ordenacion = "visitas";
		
		if(visitas && gustas)
			videosViGu = videoRepository.findByVisualizacionesLikes();
		else if(visitas)
			videosViGu = videoRepository.findByVisualizaciones();
		else if(gustas) {
			videosViGu = videoRepository.findByLikes();
			ordenacion = "gustas";
		} else
			ordenacion = "fecha";
		
		if(titulo && descripcion)
			videosTiDe = videoRepository.findByTituloDescripcion(busqueda.toLowerCase());
		else if(titulo)
			videosTiDe = videoRepository.findByTitulo(busqueda.toLowerCase());
		else if(descripcion)
			videosTiDe = videoRepository.findByDescripcion(busqueda.toLowerCase());
		 
		if(genero)
			videosGe = getVideosGeneros(videosAll, busqueda.toLowerCase());
		if(user)
			videosUs = getVideosUsuarios(busqueda.toLowerCase());
		
		videos = organizeVideos(videosViGu, videosTiDe, videosGe, videosUs, ordenacion);
		
		if(!visitas && !gustas && !titulo && !descripcion && !genero && !user && busqueda.equals(""))
			return videosAll;
		else if(!visitas && !gustas && !descripcion && !genero && !user && !busqueda.equals("")) {
			videos = videoRepository.findByTitulo(busqueda.toLowerCase());
			videos = ordenarVideos(videos, ordenacion);
			return videos;
		} else
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
	
	private List<Video> getVideosGeneros(List<Video> videosAll, String busqueda) {
		
		List<Video> videosGeneros = new ArrayList<Video>();
		Set<Genero> gen;
		boolean sw;
		
		for(Video v : videosAll) {
			gen = generoRepository.findGenerosByVideo(v);
			sw = false;
			for(Genero g: gen)
				if((g.getNombre().toLowerCase().contains(busqueda) || busqueda.contains(g.getNombre().toLowerCase())) && !sw) {
					videosGeneros.add(v);
					sw = true;
				}
		}
		
		return videosGeneros;
	}
	
	private List<Video> getVideosUsuarios(String busqueda) {
		List<Usuario> usuario = usuarioRepository.findByUsuarioSearch(busqueda);
		List<Video> videos = videoRepository.findByUsuarioSearch(usuario);
		return videos;
	}
	
	private List<Video> organizeVideos(List<Video> videosViGu, List<Video> videosTiDe, List<Video> videosGe, List<Video> videosUs,
			                           String ordenacion) {
		
		List<Video> videos = null;
		List<Video> videosTempTiDe = new ArrayList<Video>();
		List<Video> videosTempGe = new ArrayList<Video>();
		List<Video> videosTempUs = new ArrayList<Video>();
		
		if(videosViGu != null)
			videos = videosViGu;
		
		if(videosTiDe != null && videos != null) {
			for(Video vTD: videosTiDe)
				for(Video v: videos)
					if(vTD.getId() == v.getId())
						videosTempTiDe.add(v);
			videos.clear();
			videos = videosTempTiDe;
		} else if(videosTiDe != null)
			videos = videosTiDe;
	
		if(videosGe != null && videos != null) {
			for(Video vG: videosGe)
				for(Video v: videos)
					if(vG.getId() == v.getId())
						videosTempGe.add(v);
			videos.clear();
			videos = videosTempGe;
		} else if(videosGe != null)
			videos = videosGe;
		
		if(videosUs != null && videos != null) {
			for(Video vU: videosUs)
				for(Video v: videos)
					if(vU.getId() == v.getId())
						videosTempUs.add(v);
			videos.clear();
			videos = videosTempUs;
		} else if(videosUs != null)
			videos = videosUs;
		
		if(videos != null)
			videos = ordenarVideos(videos, ordenacion);
		
		return videos;
	}
	
	private List<Video> ordenarVideos(List<Video> videos, String ordenacion) {
		
		Collections.sort(videos, new Comparator<Video>() {
			@Override
			public int compare(Video o1, Video o2) {
				int resultado = 0;
				if(ordenacion.equals("visitas"))
					if(o1.getVisualizaciones() > o2.getVisualizaciones())
						resultado = -1;
					else if(o1.getVisualizaciones() < o2.getVisualizaciones())
						resultado = 1;
				if(ordenacion.equals("gustas"))
					if(o1.getLikes() > o2.getLikes())
						resultado = -1;
					else if(o1.getLikes() < o2.getLikes())
						resultado = 1;
				if(ordenacion.equals("fecha"))
					if(o1.getCreacion().after(o2.getCreacion()))
						resultado = -1;
					else if(o1.getCreacion().before(o2.getCreacion()))
						resultado = 1;
				
				return resultado;
			}
		});
		
		return videos;
	}
}