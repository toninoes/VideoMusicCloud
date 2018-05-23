package vmc.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import vmc.exception.ErrorInternoServidorException;
import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;
import vmc.repository.ComentarioRepository;
import vmc.repository.UsuarioRepository;
import vmc.repository.VideoRepository;

@Service
public class ComentarioService {
	
	@Autowired
	private ComentarioRepository comentarioRepository;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Comentario create(long logueadoId, long videoId) {

		Comentario c = new Comentario("");
		Optional<Usuario> usuario = usuarioRepository.findById(logueadoId);
		Video video = videoRepository.findById(videoId);
		video.setLikes(video.getLikes() + 1);
		videoRepository.save(video);
		c.setUsuario(usuario.get());
		c.setVideo(video);
		c.setGusta(true);
		c.setDescripcion("");
		comentarioRepository.save(c);
		
		return c;
	}
	
	public Comentario update(boolean comentario, long logueadoId, long videoId) {
		
		List<Comentario> comentarios = comentarioRepository.findByVideoIdUsuarioId(videoId, logueadoId);
		Video video = videoRepository.findById(videoId);
		for(Comentario c: comentarios) {
			c.setGusta(comentario);
			comentarioRepository.save(c);
		}
		
		if(comentario)
				video.setLikes(video.getLikes() + 1);
		else if(video.getLikes() > 0)
				video.setLikes(video.getLikes() - 1);
			
		videoRepository.save(video);
		
		return comentarios.get(0);
	}
	
	public List<Comentario> findComentariosByVideo(Video video) {
		List<Comentario> comentarios = comentarioRepository.findByVideo(video);		
		return comentarios;
	}
	
	public List<Comentario> findByVideoUsuario(Video video, Usuario usuario) {
		return comentarioRepository.findByVideoIdUsuarioId(video.getId(), usuario.getId());
	}
	
	public List<Comentario> findByVideoIdUsuarioId(Video video, Usuario usuario) {
		return comentarioRepository.findByVideoUsuario(video.getId(), usuario.getId());
	}
	
	public ResponseEntity<Comentario> create(@Valid @RequestBody Video v, @Valid @RequestBody Usuario u,
			                                 @RequestParam("comentario") String comentario, String option) {

		Comentario c = new Comentario(comentario);
		try {
			c.setUsuario(u);
			c.setVideo(v);
			if(option.equals("gusta")) {
				c.setGusta(true);
				v.setLikes(v.getLikes() + 1);
				videoRepository.save(v);
			}
			comentarioRepository.save(c);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("crear", "Comentario", c.getId(), e.getMessage());
		}
		
		return new ResponseEntity<Comentario>(HttpStatus.OK);
	}
	
	public ResponseEntity<Comentario> delete(@Valid @RequestBody Video v) {

		List<Comentario> c = comentarioRepository.findByVideo(v);
		try {
			comentarioRepository.deleteByVideo(v.getId());
			comentarioRepository.flush();
		} catch (Exception e) {
			throw new ErrorInternoServidorException("borrar", "Comentario", c, e.getMessage());
		}
		
		return new ResponseEntity<Comentario>(HttpStatus.OK);
	}
}
