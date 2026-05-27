package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServicioMateria {
    Flux<MateriaDTO> findAllMaterias();
    Mono<MateriaDTO> findMateriaById(Long id);
    Mono<MateriaDTO> saveMateria(MateriaDTO materiaDTO);
    Mono<MateriaDTO> updateMateria(MateriaDTO materiaDTO);
    Mono<Void> deleteMateria(Long id);
}
