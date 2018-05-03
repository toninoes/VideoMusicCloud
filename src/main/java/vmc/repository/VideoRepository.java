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
	
	/* Consultas para el buscador */
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC")
	List<Video> findByVisualizaciones();
	
	@Query("SELECT v FROM Video v ORDER BY v.likes DESC")
	List<Video> findByLikes();
	
	@Query("SELECT v FROM Video v ORDER BY v.creacion DESC")
	List<Video> findAll();
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Consultas compuestas por varios checkboxes */
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.likes DESC")
	List<Video> findByVisualizacionesLikes();

	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizacionesFecha();
	
	@Query("SELECT v FROM Video v ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikesFecha();
	
	@Query("SELECT v FROM Video v ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikesFecha();
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo% ORDER BY v.creacion DESC")
	List<Video> findByTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion% ORDER BY v.creacion DESC")
	List<Video> findByDescripcion(@Param("descripcion") String descripcion);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Consultas combinadas con las anteriores */

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda% ORDER BY v.creacion DESC")
	List<Video> findByTituloDescripcion(@Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion% ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikesDescripcion(@Param("descripcion") String descripcion);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo% ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikesTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda% ORDER BY v.likes DESC, v.creacion DESC")
	List<Video> findByLikesTituloDescripcion(@Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion% ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizacionesDescripcion(@Param("descripcion") String descripcion);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo% ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizacionesTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda% ORDER BY v.visualizaciones DESC, v.creacion DESC")
	List<Video> findByVisualizacionesTituloDescripcion(@Param("busqueda") String busqueda);
	
	@Query("SELECT v FROM Video v WHERE v.descripcion LIKE %:descripcion% ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikesDescripcion(@Param("descripcion") String descripcion);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:titulo% ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikesTitulo(@Param("titulo") String titulo);
	
	@Query("SELECT v FROM Video v WHERE v.titulo LIKE %:busqueda% AND v.descripcion LIKE %:busqueda% ORDER BY v.visualizaciones DESC, v.likes DESC, v.creacion DESC")
	List<Video> findByVisualizacionesLikesTituloDescripcion(@Param("busqueda") String busqueda);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Consulta segun el usuario buscado */
	
	@Query("SELECT v FROM Video v WHERE v.usuario IN(:usuario) ORDER BY v.creacion DESC")
	List<Video> findByUsuarioSearch(@Param("usuario") List<Usuario> usuario);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
}