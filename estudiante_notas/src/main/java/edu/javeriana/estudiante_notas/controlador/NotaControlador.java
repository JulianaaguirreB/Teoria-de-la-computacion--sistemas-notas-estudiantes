package edu.javeriana.estudiante_notas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import java.time.Duration;
import jakarta.validation.Valid;
import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.servicio.ServicioNota;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "*")
public class NotaControlador {
    
    @Autowired
    private ServicioNota servicioNota;
    
    @GetMapping("/estudiante/{estudianteId}")
    public Flux<NotaDto> getNotasByEstudiante(@PathVariable Long estudianteId) {
        return servicioNota.findNotasByEstudianteId(estudianteId);
    }
    
    @GetMapping("/materia/{materiaId}")
    public Flux<NotaDto> getNotasByMateria(@PathVariable Long materiaId) {
        return servicioNota.findNotasByMateriaId(materiaId);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotaDto> createNota(@Valid @RequestBody NotaDto notaDto) {
        return servicioNota.saveNota(notaDto);
    }
    
    @PutMapping("/{id}")
    public Mono<NotaDto> updateNota(@PathVariable Long id, 
                                   @Valid @RequestBody NotaDto notaDto) {
        notaDto.setId(id);
        return servicioNota.updateNota(notaDto);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNota(@PathVariable Long id) {
        return servicioNota.deleteNota(id);
    }
}
