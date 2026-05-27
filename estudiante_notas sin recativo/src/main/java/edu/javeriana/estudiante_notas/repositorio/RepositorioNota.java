package edu.javeriana.estudiante_notas.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.javeriana.estudiante_notas.modelo.Nota;
public interface RepositorioNota extends JpaRepository<Nota, Long> {
    List<Nota> findByEstudianteId(Long estudianteId);
    void deleteByEstudianteId(Long estudianteId);
}

