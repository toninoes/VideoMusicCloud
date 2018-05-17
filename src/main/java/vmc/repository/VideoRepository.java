package vmc.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Usuario;
import vmc.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

	@Transactional
	@Modifying
	@Query("DELETE FROM Video v WHERE v.id = :id")
	void delete(@Param("id") long id);
	
	Video findById(@Param("id") long id);
	
	@Query("SELECT v FROM Video v WHERE v.usuario = :usuario ORDER BY v.creacion DESC")
	Page<Video> findByPageUsuario(Pageable p, @Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v WHERE v.usuario = :usuario ORDER BY v.creacion DESC")
	List<Video> findByUsuario(@Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v WHERE v.usuario = :usuario ORDER BY v.creacion DESC")
	List<Video> findBySiguiendo(@Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizaciones();
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.creacion DESC")
	Page<Video> findByPageVisualizaciones(Pageable p);
	
	@Query("SELECT v FROM Video v ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikes();
	
	@Query("SELECT v FROM Video v ORDER BY v.likes DESC, v.creacion DESC")
	Page<Video> findByPageLikes(Pageable p);
	
	@Query("SELECT v FROM Video v ORDER BY v.creacion DESC")
	Page<Video> findAll(Pageable p);
	
	@Query("SELECT v FROM Video v WHERE 1=0 ORDER BY v.creacion DESC")
	Page<Video> findNothing(Pageable p);
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:u) ORDER BY v.creacion DESC")
	List<Video> findByVideos(@Param("u") Set<Usuario> u);
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:u) ORDER BY v.creacion DESC")
	Page<Video> findByPageVideos(Pageable p, @Param("u") Set<Usuario> u);
	
	@Query("SELECT v FROM Video v WHERE v.usuario =:usuario ORDER BY v.creacion DESC")
	Page<Video> findByPageSiguiendo(Pageable p, @Param("usuario") Usuario usuario);
	
	@Query("SELECT v FROM Video v ORDER BY v.creacion DESC")
	List<Video> findAll();
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikes();
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	Page<Video> findByPageVisualizacionesLikes(Pageable p);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo%")
	List<Video> findByTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo%")
	Page<Video> findByPageTitulo(Pageable p, @Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion%")
	List<Video> findByDescripcion(@Param("descripcion") String descripcion);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion%")
	Page<Video> findByPageDescripcion(Pageable p, @Param("descripcion") String descripcion);
		
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda%")
	List<Video> findByTituloDescripcion(@Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda%")
	Page<Video> findByPageTituloDescripcion(Pageable p, @Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v IN(:vi) ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	Page<Video> findByPageGenerosVisitasGustas(Pageable p, @Param("vi") List<Video> vi);
	
	@Query("SELECT v FROM Video v WHERE v IN(:vi) ORDER BY v.visualizaciones DESC, v.creacion DESC")
	Page<Video> findByPageGenerosVisitas(Pageable p, @Param("vi") List<Video> vi);
	
	@Query("SELECT v FROM Video v WHERE v IN(:vi) ORDER BY v.likes DESC, v.creacion DESC")
	Page<Video> findByPageGenerosGustas(Pageable p, @Param("vi") List<Video> vi);
	
	@Query("SELECT v FROM Video v WHERE v IN(:vi) ORDER BY v.creacion DESC")
	Page<Video> findByPageGenerosFecha(Pageable p, @Param("vi") List<Video> vi);
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:usuario) ORDER BY v.creacion DESC")
	List<Video> findByUsuarioSearch(@Param("usuario") List<Usuario> usuario);
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:usuario) ORDER BY v.creacion DESC")
	Page<Video> findByPageUsuarioSearch(Pageable p, @Param("usuario") List<Usuario> usuario);
}