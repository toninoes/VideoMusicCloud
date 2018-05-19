package vmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vmc.model.Comentario;
import vmc.model.Usuario;
import vmc.model.Video;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
	
	@Query("SELECT c FROM Comentario c WHERE c.video = :video ORDER BY c.creacion DESC")
	List<Comentario> findByVideo(@Param("video") Video video);
	
	@Query("SELECT c FROM Comentario c WHERE c.video = :video AND c.usuario = :usuario ORDER BY c.creacion DESC")
	List<Comentario> findByVideoUsuario(@Param("video") Video video, @Param("usuario") Usuario usuario);
	
	@Query("SELECT c FROM Comentario c WHERE c.video = :video AND c.usuario = :usuario ORDER BY c.creacion DESC")
	List<Comentario> gusta(@Param("video") Video video, @Param("usuario") Usuario usuario);
	
	@Query("SELECT c FROM Comentario c WHERE c.usuario.id = :usuarioid AND c.video.id = :videoid AND c.descripcion = ''")
	Comentario findByVideoIdUsuarioId(@Param("videoid") long videoid, @Param("usuarioid") long usuarioid);
	
	@Query("SELECT c FROM Comentario c WHERE c.usuario.id = :usuarioid AND c.video.id = :videoid")
	List<Comentario> findByVideoUsuario(@Param("videoid") long videoid, @Param("usuarioid") long usuarioid);
	
	//@Transactional
	//@Modifying
	//@Query("DELETE FROM Comentario c WHERE c.id = :id")
	//void deleteByVideoUsuario(@Param("id") long id);
	
	//@Transactional
	//@Modifying
	//@Query("UPDATE Comentario c SET c.descripcion = '', c.gusta = true WHERE c.usuario.id = :usuarioId AND c.video.id = :videoId")
	//void updateByVideoUsuario(@Param("usuarioId") long usuarioId, @Param("videoId") long videoId);
}

