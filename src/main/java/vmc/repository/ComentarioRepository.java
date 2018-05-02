package vmc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vmc.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {}

