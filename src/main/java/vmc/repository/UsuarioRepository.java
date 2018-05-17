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

import vmc.model.Rol;
import vmc.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Transactional
	@Modifying
	@Query("UPDATE Usuario u SET u.foto = '' WHERE u.id = :id")
	void updateByFoto(@Param("id") long id);
	
	@Query("SELECT u FROM Usuario u ORDER BY u.nombre, u.apellidos")
	List<Usuario> findAll();
	
	@Query("SELECT u FROM Usuario u WHERE u IN(:users) ORDER BY u.nombre, u.apellidos")
	Page<Usuario> findAllSiguiendo(Pageable p, @Param("users") Set<Usuario> users);
	
	@Query("SELECT u FROM Usuario u WHERE u IN(:users) ORDER BY u.nombre, u.apellidos")
	Page<Usuario> findAllSeguidores(Pageable p, @Param("users") Set<Usuario> users);
	
	List<Usuario> findByNombre(@Param("nombre") String nombre);
	
	Usuario findByMail(@Param("mail") String mail);
	
	@Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :roladmin")
	Usuario findByRolAdmin(@Param("roladmin") Rol roladmin);
	
	@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% ORDER BY u.nombre")
	List<Usuario> findByUsuarioSearch(@Param("nombre") String nombre);
	
	@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.id != :id ORDER BY u.nombre")
	Page<Usuario> findByUsuarioSearch(Pageable p, @Param("nombre") String nombre, @Param("id") long id);
	
	@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.id != :id ORDER BY u.nombre")
	Page<Usuario> findByUsuarioSearchNombre(Pageable p, @Param("nombre") String nombre, @Param("id") long id);
	
	@Query("SELECT u FROM Usuario u WHERE u.apellidos LIKE %:apellidos% AND u.id != :id ORDER BY u.nombre")
	Page<Usuario> findByUsuarioSearchApellidos(Pageable p, @Param("apellidos") String apellidos, @Param("id") long id);
	
	@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.apellidos LIKE %:apellidos% AND u.id != :id ORDER BY u.nombre")
	Page<Usuario> findByUsuarioSearchNombreApellidos(Pageable p, @Param("nombre") String nombre, @Param("apellidos") String apellidos, @Param("id") long id);
}
