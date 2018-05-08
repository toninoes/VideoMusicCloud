package vmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vmc.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Query("SELECT u FROM Usuario u ORDER BY u.nombre, u.apellidos")
	List<Usuario> findAll();
	
	List<Usuario> findByNombre(@Param("nombre") String nombre);
	
	Usuario findByMail(@Param("mail") String mail);
	
	@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% ORDER BY u.nombre")
	List<Usuario> findByUsuarioSearch(@Param("nombre") String nombre);
}
