package edu.javeriana.estudiante_notas.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.javeriana.estudiante_notas.modelo.Estudiante;

public interface RepositorioEstudiante extends JpaRepository<Estudiante, Long> {
    
    Optional<Estudiante> findByCorreo(String correo);
}
