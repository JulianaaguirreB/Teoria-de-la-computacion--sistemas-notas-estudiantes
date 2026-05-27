package edu.javeriana.estudiante_notas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.servicio.ServicioNota;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "*") // Permitir peticiones del frontend
public class NotaControlador {
    
    @Autowired
    private ServicioNota servicioNota;
    
    // GET: Obtener todas las notas de un estudiante
    // URL: http://localhost:8080/api/notas/estudiante/1
    // Método frontend: loadNotas() -> fetch(`${API_URL}/notas/estudiante/${currentEstudianteId}`)
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<NotaDto>> getNotasByEstudiante(@PathVariable Long estudianteId) {
        List<NotaDto> notas = servicioNota.findNotasByEstudianteId(estudianteId);
        return ResponseEntity.ok(notas);
    }
    
    // POST: Crear una nueva nota
    // URL: http://localhost:8080/api/notas
    // Método frontend: saveNota() (sin id) -> fetch POST
    @PostMapping
    public ResponseEntity<NotaDto> createNota(@Valid @RequestBody NotaDto notaDto) {
        NotaDto nuevaNota = servicioNota.saveNota(notaDto);
        return new ResponseEntity<>(nuevaNota, HttpStatus.CREATED);
    }
    
    // PUT: Actualizar una nota
    // URL: http://localhost:8080/api/notas/1
    // Método frontend: saveNota() (con id) -> fetch PUT
    @PutMapping("/{id}")
    public ResponseEntity<NotaDto> updateNota(
            @PathVariable Long id, 
            @Valid @RequestBody NotaDto notaDto) {
        notaDto.setId(id);
        NotaDto notaActualizada = servicioNota.updateNota(notaDto);
        return ResponseEntity.ok(notaActualizada);
    }
    
    // DELETE: Eliminar una nota
    // URL: http://localhost:8080/api/notas/1
    // Método frontend: deleteNota() -> fetch DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNota(@PathVariable Long id) {
        servicioNota.deleteNota(id);
        return ResponseEntity.noContent().build();
    }
}