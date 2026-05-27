package edu.javeriana.estudiante_notas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.modelo.Nota;
import edu.javeriana.estudiante_notas.repositorio.RepositorioEstudiante;
import edu.javeriana.estudiante_notas.repositorio.RepositorioMateria;
import edu.javeriana.estudiante_notas.repositorio.RepositorioNota;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServicioNotaImpl implements ServicioNota {
    
    @Autowired
    private RepositorioNota repositorioNota;
    
    @Autowired
    private RepositorioEstudiante repositorioEstudiante;
    
    @Autowired
    private RepositorioMateria repositorioMateria;
    
    @Override
    public Flux<NotaDto> findNotasByEstudianteId(Long estudianteId) {
        return repositorioNota.findByEstudianteId(estudianteId)
            .limitRate(100)
            .flatMap(this::convertirADTO);
    }
    
    @Override
    public Flux<NotaDto> findNotasByMateriaId(Long materiaId) {
        return repositorioNota.findByMateriaId(materiaId)
            .limitRate(50)
            .flatMap(this::convertirADTO);
    }
    
    @Override
    @Transactional
    public Mono<NotaDto> saveNota(NotaDto notaDto) {
        return validarPorcentajes(notaDto.getEstudianteId(), notaDto.getMateriaId(), 
                                 notaDto.getPorcentaje(), null)
            .then(convertirAEntidad(notaDto))
            .flatMap(repositorioNota::save)
            .flatMap(this::convertirADTO);
    }
    
    @Override
    @Transactional
    public Mono<NotaDto> updateNota(NotaDto notaDto) {
        return repositorioNota.findById(notaDto.getId())
            .switchIfEmpty(Mono.error(new RuntimeException("Nota no encontrada")))
            .flatMap(existente -> 
                validarPorcentajes(existente.getEstudianteId(), existente.getMateriaId(),
                                 notaDto.getPorcentaje(), notaDto.getId())
                    .thenReturn(existente)
            )
            .flatMap(existente -> {
                existente.setMateriaId(notaDto.getMateriaId());
                existente.setObservacion(notaDto.getObservacion());
                existente.setValor(notaDto.getValor());
                existente.setPorcentaje(notaDto.getPorcentaje());
                return repositorioNota.save(existente);
            })
            .flatMap(this::convertirADTO);
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteNota(Long id) {
        return repositorioNota.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Nota no encontrada"));
                }
                return repositorioNota.deleteById(id);
            });
    }
    
    @Override
    public Mono<Double> calcularNotaFinal(Long estudianteId) {
        return repositorioNota.findByEstudianteId(estudianteId)
            .collectList()
            .map(notas -> {
                if (notas.isEmpty()) return 0.0;
                
                double suma = notas.stream()
                    .mapToDouble(n -> (n.getValor() * n.getPorcentaje()) / 100)
                    .sum();
                
                return Math.round(suma * 100.0) / 100.0;
            });
    }
    
    private Mono<Void> validarPorcentajes(Long estudianteId, Long materiaId, 
                                          Double nuevoPorcentaje, Long notaIdExcluir) {
        Mono<Double> sumaActual = notaIdExcluir != null ?
            repositorioNota.sumPorcentajesByEstudianteAndMateriaExcluding(estudianteId, materiaId, notaIdExcluir) :
            repositorioNota.sumPorcentajesByEstudianteAndMateria(estudianteId, materiaId);
        
        return sumaActual
            .defaultIfEmpty(0.0)
            .flatMap(suma -> {
                if (suma + nuevoPorcentaje > 100) {
                    return Mono.error(new RuntimeException(
                        String.format("La suma de porcentajes superarÃƒÂ­a el 100%% (Actual: %.1f%%, Nuevo: %.1f%%)", 
                            suma, nuevoPorcentaje)
                    ));
                }
                return Mono.empty();
            });
    }
    
    private Mono<NotaDto> convertirADTO(Nota nota) {
        return repositorioMateria.findById(nota.getMateriaId())
            .map(materia -> {
                NotaDto dto = new NotaDto();
                dto.setId(nota.getId());
                dto.setMateriaId(nota.getMateriaId());
                dto.setMateriaNombre(materia.getNombre());
                dto.setObservacion(nota.getObservacion());
                dto.setValor(nota.getValor());
                dto.setPorcentaje(nota.getPorcentaje());
                dto.setEstudianteId(nota.getEstudianteId());
                return dto;
            })
            .defaultIfEmpty(new NotaDto(nota.getId(), nota.getMateriaId(), 
                nota.getObservacion(), nota.getValor(), nota.getPorcentaje(), 
                nota.getEstudianteId(), "Materia desconocida"));
    }
    
    private Mono<Nota> convertirAEntidad(NotaDto dto) {
        return repositorioEstudiante.findById(dto.getEstudianteId())
            .switchIfEmpty(Mono.error(new RuntimeException("Estudiante no encontrado")))
            .then(repositorioMateria.findById(dto.getMateriaId())
                .switchIfEmpty(Mono.error(new RuntimeException("Materia no encontrada"))))
            .map(materia -> {
                Nota nota = new Nota();
                nota.setId(dto.getId());
                nota.setMateriaId(dto.getMateriaId());
                nota.setEstudianteId(dto.getEstudianteId());
                nota.setObservacion(dto.getObservacion());
                nota.setValor(dto.getValor());
                nota.setPorcentaje(dto.getPorcentaje());
                return nota;
            });
    }
}
