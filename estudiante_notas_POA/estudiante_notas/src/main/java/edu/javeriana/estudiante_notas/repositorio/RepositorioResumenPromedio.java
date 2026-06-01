package edu.javeriana.estudiante_notas.repositorio;

import edu.javeriana.estudiante_notas.modelo.ResumenPromedio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioResumenPromedio extends JpaRepository<ResumenPromedio, Long> {
    List<ResumenPromedio> findByAlumnoId(Long alumnoId);
    List<ResumenPromedio> findByAlumnoIdAndMateriaId(Long alumnoId, Long materiaId);
}
