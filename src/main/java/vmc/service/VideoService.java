package vmc.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	private int VIDEOS_POR_PAGINA = 6;
	
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
	
	public int getVIDEOS_POR_PAGINA() {
		return VIDEOS_POR_PAGINA;
	}
	
	public void setVIDEOS_POR_PAGINA(int VIDEOS_POR_PAGINA) {
		this.VIDEOS_POR_PAGINA = VIDEOS_POR_PAGINA;
	}
	
	public int allPages() {
		return (int)Math.ceil(videoRepository.findAll().size()*(1.0) / VIDEOS_POR_PAGINA*(1.0)); 
	}
	
	public Page<Video> findAll(Pageable p) {   	
		return videoRepository.findAll(p);
    }
	
	public int myPages(Usuario usuario, Set<Usuario> siguiendo) {
		if(!siguiendo.isEmpty()) {
			List<Video> videos = null;
			int subtotal = 0;
			for(Usuario u: siguiendo) {
				videos = videoRepository.findBySiguiendo(u);
				subtotal += videos.size();
			}
			videos.clear();
			videos = videoRepository.findByUsuario(usuario);
			subtotal += videos.size();
			return (int)Math.ceil(subtotal*(1.0) / VIDEOS_POR_PAGINA*(1.0));
		} else
			return (int)Math.ceil((videoRepository.findByUsuario(usuario)).size()*(1.0) / VIDEOS_POR_PAGINA*(1.0));
	}	
	
	public Page<Video> findMyPage(Pageable p, Usuario usuario, Set<Usuario> users) {   	
		if(!users.isEmpty())
			return videoRepository.findByPageVideos(p, users);
		else
			return videoRepository.findByPageUsuario(p, usuario);
    }
	
	public List<Video> findMyVideos(Usuario usuario, Set<Usuario> users) {   	
		if(!users.isEmpty())
			return videoRepository.findByVideos(users);
		else
			return videoRepository.findByUsuario(usuario);
    }
	
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
	
	public List<Video> findVideosByUsuarioIdSiguiendo(Pageable p, Usuario usuario) {
		Page<Usuario> siguiendo = usuarioService.siguiendo(p, usuario.getSiguiendo());
		if(!siguiendo.getContent().isEmpty()) {
			List<Video> videos = null;
			List<Video> total = new ArrayList<Video>();
			videos = videoRepository.findByUsuario(usuario);
			for(int i = 0; i < videos.size(); i++)
				total.add(videos.get(i));
			for(Usuario u: siguiendo) {
				videos = videoRepository.findBySiguiendo(u);
				for(int i = 0; i < videos.size(); i++)
					if(u.getId() != usuario.getId())
						total.add(videos.get(i));
			}
			return total;
		} else
			return videoRepository.findByUsuario(usuario);
	}
	
	public Page<Video> findSearch(Pageable p, List<Video> videosAll, int visitas, int gustas, int titulo, 
								  int descripcion, int genero, int user, String busqueda) {
		
		List<Video> videos = null;
	    List<Video> videosViGu = null; 
	    List<Video> videosTiDe = null;
	    List<Video> videosGe = null;
	    List<Video> videosUs = null;
		String ordenacion = "fecha";

		if(visitas == 1 && gustas == 1) {
			videosViGu = videoRepository.findByVisualizacionesLikes();
			ordenacion = "visitasgustas";
		} else if(visitas == 1) {
			videosViGu = videoRepository.findByVisualizaciones();
			ordenacion = "visitas";
		} else if(gustas == 1) {
			videosViGu = videoRepository.findByLikes();
			ordenacion = "gustas";
		}
		
		if(titulo == 1 && descripcion == 1 && !busqueda.equals("0"))
			videosTiDe = videoRepository.findByTituloDescripcion(busqueda.toLowerCase());
		else if(titulo == 1)
			videosTiDe = videoRepository.findByTitulo(busqueda.toLowerCase());
		else if(descripcion == 1)
			videosTiDe = videoRepository.findByDescripcion(busqueda.toLowerCase());
		
		if(genero == 1 && !busqueda.equals("0"))
			videosGe = getVideosGeneros(p, videosAll, busqueda.toLowerCase());
		if(user == 1 && !busqueda.equals("0"))
			videosUs = getVideosUsuarios(p, busqueda.toLowerCase());
		
		if(visitas == 0 && gustas == 0 && titulo == 0 && descripcion == 0 && genero == 0 && user == 0 && busqueda.equals("0"))
			return videoRepository.findAll(p);
		else if(visitas == 0 && gustas == 0 && descripcion == 0 && genero == 0 && user == 0 && !busqueda.equals("0")) {
			Page<Video> v = videoRepository.findByPageTitulo(p, busqueda.toLowerCase());
			return v;
		} else {
			videos = organizeVideos(p, videosViGu, videosTiDe, videosGe, videosUs, ordenacion);
			Page<Video> v = ordenarVideos(p, videos, ordenacion);
			return v;
		}
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
	
	public void delete(Video video) {
        videoRepository.delete(video.getId());
    }
	
	public synchronized ResponseEntity<?> subir(@RequestParam("titulo") String t, MultipartFile v, 
								                @RequestParam("descripcion") String d, @RequestParam("videogeneros") String[] g) {
	
    	if(esUnVideo(v)) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		Usuario usuario = usuarioService.findByMail(auth.getName());
    		
    		Set<Genero> generos = new HashSet<Genero>();
        	for(String s: g) {
        		Genero genero = generoService.findByNombre(s);
        		generos.add(genero);
        	}
        	
        	String ext = v.getOriginalFilename();
    		ext = ext.substring(ext.lastIndexOf("."));
    		
        	Video video = new Video(t, v.getOriginalFilename(), usuario, d);
          	video.setVideoGeneros(generos);
    		videoRepository.save(video);
    		video.setNombre(video.getId() + ext);
    		videoRepository.save(video);
    		
    		almacenamientoService.store(v, "video", video.getId() + ext);
        	
        	return new ResponseEntity<Video>(video, HttpStatus.CREATED);
    	}else
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);    
    }
	
	public synchronized void saveVisit(long visits, Video video) {
		video.setVisualizaciones(visits);
		videoRepository.save(video);
	}
	
	public synchronized void update(Video video, boolean privado) {
		video.setPrivado(privado);
		videoRepository.save(video);
	}
	
	@ExceptionHandler(AlmacenamientoFicheroNoEncontradoException.class)
    public ResponseEntity<?> manejarAlmacenamientoFicheroNoEncontrado(AlmacenamientoFicheroNoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }
	
	private Boolean esUnVideo(MultipartFile f) {
		return(
				f.getContentType().equals("video/mp4")		 ||	// .mp4	MP4 video
				f.getContentType().equals("video/webm")		 ||	// .webm	WEBM video
				f.getContentType().equals("video/ogg")		 ||	// .ogv	OGG video
				f.getContentType().equals("video/x-msvideo") ||	// .avi	AVI: Audio Video Interleave
				f.getContentType().equals("video/mpeg")		 ||	// .mpeg	MPEG Video
				f.getContentType().equals("video/3gpp")		 ||	// .3gp	3GPP audio/video container
				f.getContentType().equals("video/3gpp2")	 ||	// .3g2	3GPP2 audio/video container
				f.getContentType().equals("video/x-ms-wmv")  ||
				f.getContentType().equals("video/x-msvideo")
				);		
	}
	
	private List<Video> getVideosGeneros(Pageable p, List<Video> videosAll, String busqueda) {
		
		List<Video> videos = new ArrayList<Video>();
		Set<Genero> generos = generoRepository.findGeneroByNombre(busqueda);
		
		for(Video video : videosAll)
			for(Genero g: video.getVideoGeneros())
				for(Genero genero: generos)
					if(g.getId() == genero.getId())
						videos.add(video);
		
		return videos;
	}
	
	private List<Video> getVideosUsuarios(Pageable p, String busqueda) {
		List<Usuario> usuario = usuarioRepository.findByUsuarioSearch(busqueda);
		List<Video> videos = videoRepository.findByUsuarioSearch(usuario);
		return videos;
	}
	
	private List<Video> organizeVideos(Pageable p, List<Video> videosViGu, List<Video> videosTiDe, List<Video> videosGe, List<Video> videosUs,
			                           String ordenacion) {
		
		List<Video> videos = null;
		List<Video> videosTempTiDe = new ArrayList<Video>();
		List<Video> videosTempGe = new ArrayList<Video>();
		List<Video> videosTempUs = new ArrayList<Video>();
		
		if(videosViGu != null && !videosViGu.isEmpty())
			videos = videosViGu;
		
		if(videosTiDe != null && !videosTiDe.isEmpty() && videos != null && !videos.isEmpty()) {
			for(Video vTD: videosTiDe)
				for(Video v: videos)
					if(vTD.getId() == v.getId())
						videosTempTiDe.add(v);
			videos.clear();
			videos = videosTempTiDe;
		} else if(videosTiDe != null && !videosTiDe.isEmpty())
			videos = videosTiDe;
	
		if(videosGe != null && !videosGe.isEmpty() && videos != null && !videos.isEmpty()) {
			for(Video vG: videosGe)
				for(Video v: videos)
					if(vG.getId() == v.getId())
						videosTempGe.add(v);
			videos.clear();
			videos = videosTempGe;
		} else if(videosGe != null && !videosGe.isEmpty())
			videos = videosGe;
		
		if(videosUs != null && !videosUs.isEmpty() && videos != null && !videos.isEmpty()) {
			for(Video vU: videosUs)
				for(Video v: videos)
					if(vU.getId() == v.getId())
						videosTempUs.add(v);
			videos.clear();
			videos = videosTempUs;
		} else if(videosUs != null && !videosUs.isEmpty())
			videos = videosUs;
		
		return videos;
	}
	
	private Page<Video> ordenarVideos(Pageable p, List<Video> videos, String ordenacion) {
		
		Page<Video> v;
		
		if(videos != null && !videos.isEmpty()) {
			switch(ordenacion) {
				case "visitasgustas" : v = videoRepository.findByPageGenerosVisitasGustas(p, videos); break;
				case "visitas": v = videoRepository.findByPageGenerosVisitas(p, videos); break;
				case "gustas": v = videoRepository.findByPageGenerosGustas(p, videos); break;
				default: v = videoRepository.findByPageGenerosFecha(p, videos); break;
			}
		} else v = videoRepository.findNothing(p);
		
		return v;
	}
}