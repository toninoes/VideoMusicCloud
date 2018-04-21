package vmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Usuario;
import vmc.model.Video;


public interface VideoRepository extends JpaRepository<Video, Long> {
	
	@Query("SELECT v FROM Video v WHERE v.usuario = :usuario ORDER BY v.creacion DESC")
	List<Video> findByUsuario(@Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v ORDER BY v.creacion DESC")
	List<Video> findAll();

}
