package edu.javeriana.estudiante_notas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import edu.javeriana.estudiante_notas.dto.EstudianteDTO;
import edu.javeriana.estudiante_notas.servicio.ServicioEstudiante;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*") // Permitir peticiones del frontend
public class EstudianteControlador {
    
    @Autowired
    private ServicioEstudiante servicioEstudiante;
    
    // GET: Obtener todos los estudiantes
    // URL: http://localhost:8080/api/estudiantes
    // Método frontend: loadEstudiantes() -> fetch(`${API_URL}/estudiantes`)
    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> getAllEstudiantes() {
        List<EstudianteDTO> estudiantes = servicioEstudiante.findAllEstudiantes();
        return ResponseEntity.ok(estudiantes);
    }
    
    // GET: Obtener un estudiante por ID
    // URL: http://localhost:8080/api/estudiantes/1
    // Método frontend: loadEstudianteInfo() -> fetch(`${API_URL}/estudiantes/${id}`)
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteDTO> getEstudianteById(@PathVariable long id) {
        EstudianteDTO estudiante = servicioEstudiante.findEstudianteByID(id);
        return ResponseEntity.ok(estudiante);
    }
    
    // POST: Crear un nuevo estudiante
    // URL: http://localhost:8080/api/estudiantes
    // Método frontend: saveEstudiante() (cuando no hay id) -> fetch POST
    @PostMapping
    public ResponseEntity<EstudianteDTO> createEstudiante(@Valid @RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO nuevoEstudiante = servicioEstudiante.saveEstudianteByID(estudianteDTO);
        return new ResponseEntity<>(nuevoEstudiante, HttpStatus.CREATED);
    }
    
    // PUT: Actualizar un estudiante
    // URL: http://localhost:8080/api/estudiantes/1
    // Método frontend: saveEstudiante() (cuando hay id) -> fetch PUT
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteDTO> updateEstudiante(
            @PathVariable long id, 
            @Valid @RequestBody EstudianteDTO estudianteDTO) {
        estudianteDTO.setId(id);
        EstudianteDTO estudianteActualizado = servicioEstudiante.updateEstudiante(estudianteDTO);
        return ResponseEntity.ok(estudianteActualizado);
    }
    
    // DELETE: Eliminar un estudiante
    // URL: http://localhost:8080/api/estudiantes/1
    // Método frontend: deleteEstudiante() -> fetch DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable long id) {
        servicioEstudiante.deleteEstudiante(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET: Calcular nota final de un estudiante
    // URL: http://localhost:8080/api/estudiantes/1/nota-final
    // Método frontend: calcularNotaFinal() -> fetch(`${API_URL}/estudiantes/${id}/nota-final`)
    @GetMapping("/{id}/nota-final")
    public ResponseEntity<Map<String, Double>> getNotaFinal(@PathVariable long id) {
        double notaFinal = servicioEstudiante.calcularNotafinal(id);
        return ResponseEntity.ok(Map.of("notaFinal", notaFinal));
    }
}