package vmc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import vmc.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	List<Usuario> findByNombre(@Param("nombre") String nombre);
	
	Usuario findByMail(@Param("mail") String mail);
}
