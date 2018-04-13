package vmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import vmc.model.Genero;
import vmc.model.Video;


	
	public interface GeneroRepository extends JpaRepository<Genero, Long> {
		
		Genero findByNombre(@Param("nombre") String nombre);

	}

