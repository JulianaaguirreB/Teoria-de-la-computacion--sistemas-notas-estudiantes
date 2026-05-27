package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.EstudianteDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServicioEstudiante {
    Flux<EstudianteDTO> findAllEstudiantes();
    Mono<EstudianteDTO> findEstudianteByID(Long id);
    Mono<EstudianteDTO> saveEstudianteByID(EstudianteDTO estudiante);
    Mono<Void> deleteEstudiante(Long id);
    Mono<EstudianteDTO> updateEstudiante(EstudianteDTO estudiante);
    Mono<Double> calcularNotafinal(Long estudianteId);
}
