package vmc.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long> {		
	Genero findByNombre(@Param("nombre") String nombre);
	
	@Query("SELECT g FROM Genero g WHERE g.nombre LIKE %:nombre%")
	Set<Genero> findGeneroByNombre(@Param("nombre") String nombre);
}

