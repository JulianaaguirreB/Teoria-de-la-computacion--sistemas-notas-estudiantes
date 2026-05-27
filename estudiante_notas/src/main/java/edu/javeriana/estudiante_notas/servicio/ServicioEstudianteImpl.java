package edu.javeriana.estudiante_notas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.javeriana.estudiante_notas.dto.EstudianteDTO;
import edu.javeriana.estudiante_notas.modelo.Estudiante;
import edu.javeriana.estudiante_notas.repositorio.RepositorioEstudiante;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServicioEstudianteImpl implements ServicioEstudiante {
    
    @Autowired
    private RepositorioEstudiante repositorioEstudiante;
    
    @Autowired
    private ServicioNota servicioNota;
    
    @Override
    public Flux<EstudianteDTO> findAllEstudiantes() {
        return repositorioEstudiante.findAll()
            .map(this::convertirADTO);
    }
    
    @Override
    public Mono<EstudianteDTO> findEstudianteByID(Long id) {
        return repositorioEstudiante.findById(id)
            .map(this::convertirADTO)
            .switchIfEmpty(Mono.error(new RuntimeException("Estudiante no encontrado con ID: " + id)));
    }
    
    @Override
    @Transactional
    public Mono<EstudianteDTO> saveEstudianteByID(EstudianteDTO estudianteDTO) {
        return repositorioEstudiante.findByCorreo(estudianteDTO.getCorreo())
            .flatMap(existing -> Mono.<EstudianteDTO>error(
                new RuntimeException("El correo ya estÃ¡ registrado")))
            .switchIfEmpty(repositorioEstudiante.save(convertirAEntidad(estudianteDTO))
                .map(this::convertirADTO))
            .cast(EstudianteDTO.class);
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteEstudiante(Long id) {
        return repositorioEstudiante.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Estudiante no encontrado con ID: " + id));
                }
                return repositorioEstudiante.deleteById(id);
            });
    }
    
    @Override
    @Transactional
    public Mono<EstudianteDTO> updateEstudiante(EstudianteDTO estudianteDTO) {
        return repositorioEstudiante.findById(estudianteDTO.getId())
            .switchIfEmpty(Mono.error(new RuntimeException("Estudiante no encontrado")))
            .flatMap(existente -> {
                existente.setNombre(estudianteDTO.getNombre());
                existente.setApellido(estudianteDTO.getApellido());
                existente.setCorreo(estudianteDTO.getCorreo());
                return repositorioEstudiante.save(existente);
            })
            .map(this::convertirADTO);
    }
    
    @Override
    public Mono<Double> calcularNotafinal(Long estudianteId) {
        return servicioNota.calcularNotaFinal(estudianteId);
    }
    
    private EstudianteDTO convertirADTO(Estudiante estudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setApellido(estudiante.getApellido());
        dto.setCorreo(estudiante.getCorreo());
        return dto;
    }
    
    private Estudiante convertirAEntidad(EstudianteDTO dto) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(dto.getId());
        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());
        estudiante.setCorreo(dto.getCorreo());
        return estudiante;
    }
}
