package edu.javeriana.estudiante_notas.repositorio;

import edu.javeriana.estudiante_notas.modelo.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RepositorioProfesor extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByCorreo(String correo);
}
