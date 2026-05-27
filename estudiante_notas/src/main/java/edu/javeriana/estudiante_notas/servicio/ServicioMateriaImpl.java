package edu.javeriana.estudiante_notas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import edu.javeriana.estudiante_notas.modelo.Materia;
import edu.javeriana.estudiante_notas.repositorio.RepositorioMateria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServicioMateriaImpl implements ServicioMateria {
    
    @Autowired
    private RepositorioMateria repositorioMateria;
    
    @Override
    public Flux<MateriaDTO> findAllMaterias() {
        return repositorioMateria.findAll()
            .limitRate(50)
            .map(this::convertirADTO);
    }
    
    @Override
    public Mono<MateriaDTO> findMateriaById(Long id) {
        return repositorioMateria.findById(id)
            .map(this::convertirADTO)
            .switchIfEmpty(Mono.error(new RuntimeException("Materia no encontrada")));
    }
    
    @Override
    @Transactional
    public Mono<MateriaDTO> saveMateria(MateriaDTO materiaDTO) {
        return repositorioMateria.existsByNombre(materiaDTO.getNombre())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Ya existe una materia con ese nombre"));
                }
                return repositorioMateria.save(convertirAEntidad(materiaDTO))
                    .map(this::convertirADTO);
            });
    }
    
    @Override
    @Transactional
    public Mono<MateriaDTO> updateMateria(MateriaDTO materiaDTO) {
        return repositorioMateria.findById(materiaDTO.getId())
            .switchIfEmpty(Mono.error(new RuntimeException("Materia no encontrada")))
            .flatMap(existente -> {
                existente.setNombre(materiaDTO.getNombre());
                existente.setCreditos(materiaDTO.getCreditos());
                return repositorioMateria.save(existente);
            })
            .map(this::convertirADTO);
    }
    
    @Override
    @Transactional
    public Mono<Void> deleteMateria(Long id) {
        return repositorioMateria.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Materia no encontrada"));
                }
                return repositorioMateria.deleteById(id);
            });
    }
    
    private MateriaDTO convertirADTO(Materia materia) {
        MateriaDTO dto = new MateriaDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setCreditos(materia.getCreditos());
        return dto;
    }
    
    private Materia convertirAEntidad(MateriaDTO dto) {
        Materia materia = new Materia();
        materia.setId(dto.getId());
        materia.setNombre(dto.getNombre());
        materia.setCreditos(dto.getCreditos());
        return materia;
    }
}
