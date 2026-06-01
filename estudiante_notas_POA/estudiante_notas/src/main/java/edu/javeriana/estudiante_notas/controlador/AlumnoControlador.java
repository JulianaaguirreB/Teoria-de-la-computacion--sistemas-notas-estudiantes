package edu.javeriana.estudiante_notas.controlador;

import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import edu.javeriana.estudiante_notas.servicio.ServicioAlumno;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoControlador {

    @Autowired
    private ServicioAlumno servicioAlumno;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        return ResponseEntity.ok(servicioAlumno.findAllAlumnos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioAlumno.findAlumnoById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioDTO dto) {
        return new ResponseEntity<>(servicioAlumno.saveAlumno(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(
            @PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(servicioAlumno.updateAlumno(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioAlumno.deleteAlumno(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/promedio-general")
    public ResponseEntity<Map<String, Double>> getPromedioGeneral(@PathVariable Long id) {
    return ResponseEntity.ok(Map.of("promedioGeneral", servicioAlumno.calcularNotaFinal(id)));
    }
    
    @Deprecated
    @GetMapping("/{id}/nota-final")
    public ResponseEntity<Map<String, Double>> getNotaFinal(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("notaFinal", servicioAlumno.calcularNotaFinal(id)));
    }
}
