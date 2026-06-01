package edu.javeriana.estudiante_notas.controlador;

import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import edu.javeriana.estudiante_notas.servicio.ServicioMateria;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaControlador {

    @Autowired
    private ServicioMateria servicioMateria;

    @GetMapping
    public ResponseEntity<List<MateriaDTO>> getAll() {
        return ResponseEntity.ok(servicioMateria.findAllMaterias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioMateria.findMateriaById(id));
    }

    @PostMapping
    public ResponseEntity<MateriaDTO> create(@Valid @RequestBody MateriaDTO dto) {
        return new ResponseEntity<>(servicioMateria.saveMateria(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaDTO> update(
            @PathVariable Long id, @Valid @RequestBody MateriaDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(servicioMateria.updateMateria(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioMateria.deleteMateria(id);
        return ResponseEntity.noContent().build();
    }
}
