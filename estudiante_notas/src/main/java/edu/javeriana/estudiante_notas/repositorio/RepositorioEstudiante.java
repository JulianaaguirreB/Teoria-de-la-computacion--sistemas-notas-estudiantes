package edu.javeriana.estudiante_notas.repositorio;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import edu.javeriana.estudiante_notas.modelo.Estudiante;
import reactor.core.publisher.Mono;

public interface RepositorioEstudiante extends ReactiveCrudRepository<Estudiante, Long> {
    Mono<Estudiante> findByCorreo(String correo);
    
    @Query("SELECT EXISTS(SELECT 1 FROM estudiante WHERE correo = :correo AND id != :id)")
    Mono<Boolean> existsByCorreoAndIdNot(String correo, Long id);
}
