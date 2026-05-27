package edu.javeriana.estudiante_notas.repositorio;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import edu.javeriana.estudiante_notas.modelo.Materia;
import reactor.core.publisher.Mono;

public interface RepositorioMateria extends ReactiveCrudRepository<Materia, Long> {
    Mono<Materia> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
}
