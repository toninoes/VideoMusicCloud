package vmc.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import vmc.exception.ErrorInternoServidorException;
import vmc.exception.RecursoNoEncontradoException;
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
	
	public List<Comentario> findAll() {   	
		return comentarioRepository.findAll();
    }
	
	public Comentario findById(@PathVariable long id) {
		Comentario comentario = comentarioRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Comentario", "id", id));

		return comentario;
	}	
	
	public List<Comentario> findComentariosByVideo(Video video) {
		List<Comentario> comentarios = comentarioRepository.findByVideo(video);		
		return comentarios;
	}
	
	public List<Comentario> findComentariosByVideoUsuario(Video video, Usuario usuario) {
		List<Comentario> comentarios = comentarioRepository.findByVideoUsuario(video, usuario);		
		return comentarios;
	}
	
	public Comentario gusta(Video video, Usuario usuario) {
		List<Comentario> comentarios = comentarioRepository.gusta(video, usuario);
		if(!comentarios.isEmpty())
			return comentarios.get(0);
		else
			return null;
	}
	
	public void saveGusta(List<Comentario> comentarios, boolean gusta, Video video) {
		if(gusta)
			video.setLikes(video.getLikes() + 1);
		else {
			if(video.getLikes() >= 1)
				video.setLikes(video.getLikes() - 1);
			else
				video.setLikes(0);
		}
		videoRepository.save(video);
		for(Comentario c:comentarios) {
			c.setGusta(gusta);
			comentarioRepository.save(c);
		}
	}
	
	public ResponseEntity<Comentario> create(@Valid @RequestBody Video v, @Valid @RequestBody Usuario u,
			                              @RequestParam("comentario") String comentario) {

		Comentario c = new Comentario(comentario);
		try {
			c.setUsuario(u);
			c.setVideo(v);
			comentarioRepository.save(c);
		} catch (Exception e) {
			throw new ErrorInternoServidorException("crear", "Comentario", c.getId(), e.getMessage());
		}
		
		return new ResponseEntity<Comentario>(HttpStatus.OK);
	}
}
