package vmc.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Genero;
import vmc.model.Video;

public interface GeneroRepository extends JpaRepository<Genero, Long> {		
	Genero findByNombre(@Param("nombre") String nombre);
	
	@Query("SELECT v.videogeneros FROM Video v WHERE v = :video")
	Set<Genero> findGenerosByVideo(@Param("video") Video video);
}

