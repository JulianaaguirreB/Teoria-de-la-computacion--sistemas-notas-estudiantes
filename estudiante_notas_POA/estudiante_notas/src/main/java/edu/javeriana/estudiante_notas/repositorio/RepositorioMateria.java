package edu.javeriana.estudiante_notas.repositorio;

import edu.javeriana.estudiante_notas.modelo.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RepositorioMateria extends JpaRepository<Materia, Long> {
    Optional<Materia> findByNombre(String nombre);
}
