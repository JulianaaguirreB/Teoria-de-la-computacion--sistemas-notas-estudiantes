package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.NotaDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServicioNota {
    Flux<NotaDto> findNotasByEstudianteId(Long estudianteId);
    Flux<NotaDto> findNotasByMateriaId(Long materiaId);
    Mono<NotaDto> saveNota(NotaDto notaDto);
    Mono<NotaDto> updateNota(NotaDto notaDto);
    Mono<Void> deleteNota(Long id);
    Mono<Double> calcularNotaFinal(Long estudianteId);
}
