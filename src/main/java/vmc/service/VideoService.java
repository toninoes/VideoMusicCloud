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
			videos = videoRepository.findByVideoUsuario(usuario.getId());
			subtotal += videos.size();
			return (int)Math.ceil(subtotal*(1.0) / VIDEOS_POR_PAGINA*(1.0));
		} else
			return (int)Math.ceil((videoRepository.findByVideoUsuario(usuario.getId())).size()*(1.0) / VIDEOS_POR_PAGINA*(1.0));
	}	
	
	public Page<Video> findMyPage(Pageable p, Set<Usuario> users) {
		return videoRepository.findByPageVideos(p, users);
    }
	
	public List<Video> findMyVideos(Usuario usuario, Set<Usuario> users) {   	
		if(!users.isEmpty())
			return videoRepository.findByVideos(users);
		else
			return videoRepository.findByVideoUsuario(usuario.getId());
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
		
		return videoRepository.findByVideoUsuario(usuario.getId());
	}
	
	public List<Video> findVideosByUsuarioIdSiguiendo(Pageable p, Usuario usuario) {
		Page<Usuario> siguiendo = usuarioService.siguiendo(p, usuario.getSiguiendo());
		if(!siguiendo.getContent().isEmpty()) {
			List<Video> videos = null;
			List<Video> total = new ArrayList<Video>();
			videos = videoRepository.findByVideoUsuario(usuario.getId());
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
			return videoRepository.findByVideoUsuario(usuario.getId());
	}
	
	public Page<Video> findSearch(Pageable p, List<Video> videosAll, boolean visitas, boolean gustas, boolean titulo, 
								  boolean descripcion, boolean genero, boolean user, String busqueda, String view, 
								  Usuario logueado, Set<Usuario> usuarios) {
		Page<Video> v = null;
		List<Video> videos = null;
	    List<Video> videosViGu = null; 
	    List<Video> videosTiDe = null;
	    List<Video> videosGe = null;
	    List<Video> videosUs = null;
		String ordenacion = "fecha";

		if(visitas && gustas) {
			if(view.equals("perfil"))
				videosViGu = videoRepository.findByVisualizacionesLikesUser(logueado);
			else if(view.equals("misvideos"))
				videosViGu = videoRepository.findByVisualizacionesLikesUsers(usuarios);
			else 
				videosViGu = videoRepository.findByVisualizacionesLikes();
			ordenacion = "visitasgustas";
		} else if(visitas) {
			if(view.equals("perfil"))
				videosViGu = videoRepository.findByVisualizacionesUser(logueado);
			else if(view.equals("misvideos"))
				videosViGu = videoRepository.findByVisualizacionesUsers(usuarios);
			else
				videosViGu = videoRepository.findByVisualizaciones();
			ordenacion = "visitas";
		} else if(gustas) {
			if(view.equals("perfil"))
				videosViGu = videoRepository.findByLikesUser(logueado);
			else if(view.equals("misvideos"))
				videosViGu = videoRepository.findByLikesUsers(usuarios);
			else
				videosViGu = videoRepository.findByLikes();
			ordenacion = "gustas";
		}
		
		if(titulo && descripcion && !busqueda.equals("0")) {
			if(view.equals("perfil"))
				videosTiDe = videoRepository.findByTituloDescripcionUser(logueado, busqueda.toLowerCase());
			else if(view.equals("misvideos"))
				videosTiDe = videoRepository.findByTituloDescripcionUsers(usuarios, busqueda.toLowerCase());
			else
				videosTiDe = videoRepository.findByTituloDescripcion(busqueda.toLowerCase());
		} else if(titulo) {
			if(view.equals("perfil"))
				videosTiDe = videoRepository.findByTituloUser(logueado, busqueda.toLowerCase());
			else if(view.equals("misvideos"))
				videosTiDe = videoRepository.findByTituloUsers(usuarios, busqueda.toLowerCase());
			else
				videosTiDe = videoRepository.findByTitulo(busqueda.toLowerCase());
		} else if(descripcion) {
			if(view.equals("perfil"))
				videosTiDe = videoRepository.findByDescripcionUser(logueado, busqueda.toLowerCase());
			else if(view.equals("misvideos"))
				videosTiDe = videoRepository.findByDescripcionUsers(usuarios, busqueda.toLowerCase());
			else
				videosTiDe = videoRepository.findByDescripcion(busqueda.toLowerCase());
		}
		
		if(genero && !busqueda.equals("0") && view.equals("listado"))
			videosGe = getVideosGeneros(videosAll, busqueda.toLowerCase());
		else if(genero && !busqueda.equals("0") && view.equals("perfil")) {
			videosGe = videoRepository.findByVideoUsuario(logueado.getId());
			videosGe = getVideosGeneros(videosGe, busqueda.toLowerCase());
		} else if(genero && !busqueda.equals("0")) {
			videosGe = videoRepository.findByVideos(usuarios);
			videosGe = getVideosGeneros(videosGe, busqueda.toLowerCase());
		}
		
		if(user && !busqueda.equals("0"))
			videosUs = getVideosUsuarios(busqueda.toLowerCase(), usuarios);
		
		if(visitas && gustas && titulo && descripcion && genero && user && busqueda.equals("0") && view.equals("listado"))
			return videoRepository.findAll(p);
		else if(!visitas && !gustas && !descripcion && !genero && !user && !busqueda.equals("0")) {
			if(view.equals("perfil"))
				v = videoRepository.findByPageTituloUser(p, logueado, busqueda.toLowerCase());
			else if(view.equals("misvideos"))
				v = videoRepository.findByPageTituloUsers(p, usuarios, busqueda.toLowerCase());
			else
				v = videoRepository.findByPageTitulo(p, busqueda.toLowerCase());
			return v;
		} else if(titulo || descripcion || genero || user && !busqueda.equals("0")){
			videos = organizeVideos1(videosTiDe, videosGe);
			videos = organizeVideos2(videos, videosUs);
			if(videos.isEmpty())
				v = videoRepository.findNothing(p);
			else
				v = videoRepository.findVideos(p, videos);
			return v;
		} else {
			v = ordenarVideos(p, videosViGu, ordenacion);
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
	
	public Page<Video> getMyPageVideos(Pageable p, Usuario logueado, Set<Usuario> users) {
		
		Set<Usuario> us = null;
		Page<Video> videos = null;
	
		if(!users.isEmpty()) {
			us = new HashSet<Usuario>();
			us.add(logueado);
			for(Usuario u: users)
				if(logueado.getSiguiendo().contains(u))
					us.add(u);
			if(!us.isEmpty())
				videos = videoRepository.findByPageUsuarioSearch(p, us);
		}
		if(videos == null) 
			videos = videoRepository.findNothing(p);
		
		return videos;
	}
	
	public List<Video> getMyVideos(Usuario logueado, Set<Usuario> users) {
		
		Set<Usuario> us = null;
		List<Video> videos = null;
	
		if(!users.isEmpty()) {
			us = new HashSet<Usuario>();
			us.add(logueado);
			for(Usuario u: users)
				if(logueado.getSiguiendo().contains(u))
					us.add(u);
			if(!us.isEmpty())
				videos = videoRepository.findByUsuarioSearch(us);
		}
		if(videos == null) 
			videos = videoRepository.findNothing();
		
		return videos;
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
	
	private List<Video> getVideosGeneros(List<Video> videosAll, String busqueda) {
		
		List<Video> videos = new ArrayList<Video>();
		Set<Genero> generos = generoRepository.findGeneroByNombre(busqueda);
		
		for(Video video : videosAll)
			for(Genero g: video.getVideoGeneros())
				for(Genero genero: generos)
					if(g.getId() == genero.getId())
						videos.add(video);
		
		return videos;
	}
	
	private List<Video> getVideosUsuarios(String busqueda, Set<Usuario> users) {
		
		Set<Usuario> usuarios = null;
		Set<Usuario> us = null;
		List<Video> videos = null;
	
		if(!users.isEmpty()) {
			us = new HashSet<Usuario>();
			usuarios = usuarioRepository.findByUsuarioSearch(busqueda);
			for(Usuario u: users)
				if(usuarios.contains(u))
					us.add(u);
			if(!us.isEmpty())
				videos = videoRepository.findByUsuarioSearch(us);
		}
		if(videos == null) 
			videos = videoRepository.findNothing();
		
		return videos;
	}
	
	private List<Video> organizeVideos1(List<Video> videosTiDe, List<Video> videosGe) {
		
		List<Video> videos = new ArrayList<Video>();
		
		if(videosTiDe != null && videosGe != null && !videosTiDe.isEmpty() && !videosGe.isEmpty()) {
			for(Video v : videosTiDe)
				if(videosGe.contains(v))
					videos.add(v);
		} else if(videosTiDe != null)
			videos.addAll(videosTiDe);
		else if(videosGe != null)
			videos.addAll(videosGe);
		
		return videos;
	}
	
	private List<Video> organizeVideos2(List<Video> videosTemp, List<Video> videosUs) {
		
		List<Video> videos = new ArrayList<Video>();
		
		if(videosTemp != null && videosUs != null && !videosTemp.isEmpty() && !videosUs.isEmpty()) {
			for(Video v : videosTemp)
				if(videosUs.contains(v))
					videos.add(v);
		} else if(videosTemp != null)
			videos.addAll(videosTemp);
		else if(videosUs != null)
			videos.addAll(videosUs);
		
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