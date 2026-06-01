package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import java.util.List;

public interface ServicioAlumno {
    List<UsuarioDTO> findAllAlumnos();
    UsuarioDTO findAlumnoById(Long id);
    UsuarioDTO saveAlumno(UsuarioDTO dto);
    UsuarioDTO updateAlumno(UsuarioDTO dto);
    void deleteAlumno(Long id);
    double calcularNotaFinal(Long alumnoId);
}
