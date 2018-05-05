package vmc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Usuario;
import vmc.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

	Video findById(@Param("id") long id);
	
	@Query("SELECT v FROM Video v WHERE v.usuario = :usuario ORDER BY v.creacion DESC")
	List<Video> findByUsuario(@Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizaciones();
	
	@Query("SELECT v FROM Video v ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikes();
	
	@Query("SELECT v FROM Video v ORDER BY v.creacion DESC")
	List<Video> findAll();
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikes();
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo%")
	List<Video> findByTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion%")
	List<Video> findByDescripcion(@Param("descripcion") String descripcion);
		
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda%")
	List<Video> findByTituloDescripcion(@Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:usuario) ORDER BY v.creacion DESC")
	List<Video> findByUsuarioSearch(@Param("usuario") List<Usuario> usuario);
}