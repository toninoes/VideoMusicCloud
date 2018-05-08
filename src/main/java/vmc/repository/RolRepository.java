package vmc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vmc.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long>{
	Rol findByNombre(String nombre);
}
