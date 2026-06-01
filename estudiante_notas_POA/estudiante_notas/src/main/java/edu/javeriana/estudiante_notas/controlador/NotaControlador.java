package edu.javeriana.estudiante_notas.controlador;

import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import edu.javeriana.estudiante_notas.servicio.ServicioNota;
import edu.javeriana.estudiante_notas.servicio.ServicioMateria;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas")
public class NotaControlador {

    @Autowired
    private ServicioNota servicioNota;
    
    @Autowired
    private ServicioMateria servicioMateria;

    // ==================== ENDPOINTS EXISTENTES ====================
    
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<NotaDto>> getNotasByAlumno(
            @PathVariable Long alumnoId, HttpSession session) {
        return ResponseEntity.ok(servicioNota.findNotasByAlumnoId(alumnoId));
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<NotaDto>> getNotasByMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(servicioNota.findNotasByMateriaId(materiaId));
    }

    @PostMapping
    public ResponseEntity<NotaDto> createNota(@Valid @RequestBody NotaDto notaDto) {
        return new ResponseEntity<>(servicioNota.saveNota(notaDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaDto> updateNota(
            @PathVariable Long id, @Valid @RequestBody NotaDto notaDto) {
        notaDto.setId(id);
        return ResponseEntity.ok(servicioNota.updateNota(notaDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNota(@PathVariable Long id) {
        servicioNota.deleteNota(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== NUEVOS ENDPOINTS PARA PROMEDIOS ====================
    
    /**
     * GET /api/notas/alumno/{alumnoId}/promedios-por-materia
     * Ejemplo respuesta: {"Matemáticas": 4.5, "Español": 3.8, "Ciencias": 4.2}
     */
    @GetMapping("/alumno/{alumnoId}/promedios-por-materia")
    public ResponseEntity<Map<String, Double>> getPromediosPorMateria(@PathVariable Long alumnoId) {
        Map<String, Double> promedios = servicioNota.calcularPromediosPorMateria(alumnoId);
        return ResponseEntity.ok(promedios);
    }
    
    /**
     * GET /api/notas/alumno/{alumnoId}/materia/{materiaId}/promedio
     * Ejemplo respuesta: {"alumnoId": 1, "materia": "Matemáticas", "promedio": 4.5}
     */
    @GetMapping("/alumno/{alumnoId}/materia/{materiaId}/promedio")
    public ResponseEntity<Map<String, Object>> getPromedioPorMateria(
            @PathVariable Long alumnoId, 
            @PathVariable Long materiaId) {
        
        double promedio = servicioNota.calcularPromedioPorMateria(alumnoId, materiaId);
        MateriaDTO materia = servicioMateria.findMateriaById(materiaId);
        
        return ResponseEntity.ok(Map.of(
            "alumnoId", alumnoId,
            "materia", materia.getNombre(),
            "materiaId", materiaId,
            "promedio", promedio
        ));
    }
    
    /**
     * GET /api/notas/alumno/{alumnoId}/promedio-general
     * Ejemplo respuesta: {"promedioGeneral": 4.15}
     * 
     * Este es el reemplazo del antiguo /nota-final
     */
    @GetMapping("/alumno/{alumnoId}/promedio-general")
    public ResponseEntity<Map<String, Double>> getPromedioGeneral(@PathVariable Long alumnoId) {
        double promedioGeneral = servicioNota.calcularNotaFinalGeneral(alumnoId);
        return ResponseEntity.ok(Map.of("promedioGeneral", promedioGeneral));
    }
    
    /**
     * @deprecated Este método está obsoleto. Usar /promedio-general en su lugar.
     * Se mantiene por compatibilidad pero pronto se eliminará.
     */
    @Deprecated
    @GetMapping("/alumno/{alumnoId}/nota-final")
    public ResponseEntity<Map<String, Double>> getNotaFinal(@PathVariable Long alumnoId) {
        double notaFinal = servicioNota.calcularNotaFinalGeneral(alumnoId);
        return ResponseEntity.ok(Map.of("notaFinal", notaFinal));
    }
   
@GetMapping("/alumno/{alumnoId}/porcentajes-por-materia")
public ResponseEntity<Map<String, Double>> getPorcentajesPorMateria(@PathVariable Long alumnoId) {
    return ResponseEntity.ok(servicioNota.obtenerPorcentajesPorMateria(alumnoId));
}
}