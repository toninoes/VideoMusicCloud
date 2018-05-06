package vmc.service;

import java.util.List;

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
import vmc.repository.VideoRepository;

@Service
public class ComentarioService {
	
	@Autowired
	private ComentarioRepository comentarioRepository;
	
	@Autowired
	private VideoRepository videoRepository;	
	
	public List<Comentario> findComentariosByVideo(Video video) {
		List<Comentario> comentarios = comentarioRepository.findByVideo(video);		
		return comentarios;
	}
	
	public Comentario findByVideoUsuario(Video video, Usuario usuario) {
		return comentarioRepository.findByVideoIdUsuarioId(video.getId(), usuario.getId());
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
	
	public ResponseEntity<Comentario> delete(@Valid @RequestBody Video v, @Valid @RequestBody Usuario u) {

		List<Comentario> c = comentarioRepository.findByVideoUsuario(v, u);
		try {
			comentarioRepository.deleteByVideoUsuario(c.get(0).getId());
			comentarioRepository.flush();
			if(v.getLikes() > 0) {
				v.setLikes(v.getLikes() - 1);
				videoRepository.save(v);
			}
		} catch (Exception e) {
			throw new ErrorInternoServidorException("borrar", "Comentario", c.get(0).getId(), e.getMessage());
		}
		
		return new ResponseEntity<Comentario>(HttpStatus.OK);
	}
}
