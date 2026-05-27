package edu.javeriana.estudiante_notas.repositorio;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import edu.javeriana.estudiante_notas.modelo.Nota;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RepositorioNota extends ReactiveCrudRepository<Nota, Long> {
    Flux<Nota> findByEstudianteId(Long estudianteId);
    Flux<Nota> findByMateriaId(Long materiaId);
    
    @Query("SELECT COALESCE(SUM(porcentaje), 0) FROM nota WHERE estudiante_id = :estudianteId AND materia_id = :materiaId AND id != :notaId")
    Mono<Double> sumPorcentajesByEstudianteAndMateriaExcluding(Long estudianteId, Long materiaId, Long notaId);
    
    @Query("SELECT COALESCE(SUM(porcentaje), 0) FROM nota WHERE estudiante_id = :estudianteId AND materia_id = :materiaId")
    Mono<Double> sumPorcentajesByEstudianteAndMateria(Long estudianteId, Long materiaId);
    
    Mono<Void> deleteByEstudianteId(Long estudianteId);
}
