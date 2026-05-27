package edu.javeriana.estudiante_notas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.servicio.ServicioMateria;
import edu.javeriana.estudiante_notas.servicio.ServicioNota;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "*")
public class MateriaControlador {
    
    @Autowired
    private ServicioMateria servicioMateria;
    
    @Autowired
    private ServicioNota servicioNota;
    
    @GetMapping
    public Flux<MateriaDTO> getAllMaterias() {
        return servicioMateria.findAllMaterias();
    }
    
    @GetMapping("/{id}")
    public Mono<MateriaDTO> getMateriaById(@PathVariable Long id) {
        return servicioMateria.findMateriaById(id);
    }
    
    @GetMapping("/{id}/estudiantes")
    public Flux<NotaDto> getEstudiantesPorMateria(@PathVariable Long id) {
        return servicioNota.findNotasByMateriaId(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MateriaDTO> createMateria(@Valid @RequestBody MateriaDTO materiaDTO) {
        return servicioMateria.saveMateria(materiaDTO);
    }
    
    @PutMapping("/{id}")
    public Mono<MateriaDTO> updateMateria(@PathVariable Long id, 
                                         @Valid @RequestBody MateriaDTO materiaDTO) {
        materiaDTO.setId(id);
        return servicioMateria.updateMateria(materiaDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMateria(@PathVariable Long id) {
        return servicioMateria.deleteMateria(id);
    }
}
