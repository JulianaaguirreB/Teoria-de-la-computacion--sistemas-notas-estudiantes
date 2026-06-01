package edu.javeriana.estudiante_notas.repositorio;

import edu.javeriana.estudiante_notas.modelo.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RepositorioAlumno extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByCorreo(String correo);
}
