package edu.javeriana.estudiante_notas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
import edu.javeriana.estudiante_notas.dto.EstudianteDTO;
import edu.javeriana.estudiante_notas.servicio.ServicioEstudiante;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteControlador {
    
    @Autowired
    private ServicioEstudiante servicioEstudiante;
    
    @GetMapping
    public Flux<EstudianteDTO> getAllEstudiantes() {
        return servicioEstudiante.findAllEstudiantes();
    }
    
    @GetMapping("/{id}")
    public Mono<EstudianteDTO> getEstudianteById(@PathVariable Long id) {
        return servicioEstudiante.findEstudianteByID(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EstudianteDTO> createEstudiante(@Valid @RequestBody EstudianteDTO estudianteDTO) {
        return servicioEstudiante.saveEstudianteByID(estudianteDTO);
    }
    
    @PutMapping("/{id}")
    public Mono<EstudianteDTO> updateEstudiante(@PathVariable Long id, 
                                               @Valid @RequestBody EstudianteDTO estudianteDTO) {
        estudianteDTO.setId(id);
        return servicioEstudiante.updateEstudiante(estudianteDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEstudiante(@PathVariable Long id) {
        return servicioEstudiante.deleteEstudiante(id);
    }
    
    @GetMapping("/{id}/nota-final")
    public Mono<Map<String, Double>> getNotaFinal(@PathVariable Long id) {
        return servicioEstudiante.calcularNotafinal(id)
            .map(notaFinal -> Map.of("notaFinal", notaFinal));
    }
}
