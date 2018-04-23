package vmc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import vmc.exception.RecursoNoEncontradoException;
import vmc.model.Genero;
import vmc.model.Video;
import vmc.repository.GeneroRepository;

@Service
public class GeneroService {
	
	@Autowired
	private GeneroRepository generoRepository;
	
	public List<Genero> findAll() {   	
		return generoRepository.findAll();
    }
	
	public Genero findById(@PathVariable long id) {
		Genero genero = generoRepository.findById(id)
	            .orElseThrow(() -> new RecursoNoEncontradoException("Genero", "id", id));

		return genero;
	}	
	
	public Genero findByNombre(@PathVariable String nombre) {
		return generoRepository.findByNombre(nombre);
	}
	
	public List<Genero> findGenerosByVideo(Video video) {
		List<Genero> generos = findGenerosByVideo(video);
		return generos;
	}
	
}
