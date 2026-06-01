package edu.javeriana.estudiante_notas.repositorio;

import edu.javeriana.estudiante_notas.modelo.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioNota extends JpaRepository<Nota, Long> {
    List<Nota> findByAlumnoId(Long alumnoId);
    List<Nota> findByMateriaId(Long materiaId);
    List<Nota> findByAlumnoIdAndMateriaId(Long alumnoId, Long materiaId);
    void deleteByAlumnoId(Long alumnoId);
}
