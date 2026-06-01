package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import edu.javeriana.estudiante_notas.modelo.Alumno;
import edu.javeriana.estudiante_notas.repositorio.RepositorioAlumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioAlumnoImpl implements ServicioAlumno {

    @Autowired
    private RepositorioAlumno repositorioAlumno;

    @Autowired
    private ServicioNota servicioNota;

    @Override
    public List<UsuarioDTO> findAllAlumnos() {
        return repositorioAlumno.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO findAlumnoById(Long id) {
        return toDTO(repositorioAlumno.findById(id)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id)));
    }

    @Override
    public UsuarioDTO saveAlumno(UsuarioDTO dto) {
        repositorioAlumno.findByCorreo(dto.getCorreo()).ifPresent(a -> {
            throw new RuntimeException("El correo ya está registrado: " + dto.getCorreo());
        });
        Alumno a = new Alumno();
        a.setNombre(dto.getNombre());
        a.setApellido(dto.getApellido());
        a.setCorreo(dto.getCorreo());
        a.setContrasena(dto.getContrasena() != null ? dto.getContrasena() : "alumno123");
        return toDTO(repositorioAlumno.save(a));
    }

    @Override
    public UsuarioDTO updateAlumno(UsuarioDTO dto) {
        Alumno a = repositorioAlumno.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + dto.getId()));
        a.setNombre(dto.getNombre());
        a.setApellido(dto.getApellido());
        a.setCorreo(dto.getCorreo());
        return toDTO(repositorioAlumno.save(a));
    }

    @Override
    public void deleteAlumno(Long id) {
        if (!repositorioAlumno.existsById(id)) {
            throw new RuntimeException("Alumno no encontrado con ID: " + id);
        }
        repositorioAlumno.deleteById(id);
    }

    @Override
    public double calcularNotaFinal(Long alumnoId) {
        return servicioNota.calcularNotaFinalGeneral(alumnoId);
    }

    private UsuarioDTO toDTO(Alumno a) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(a.getId());
        dto.setNombre(a.getNombre());
        dto.setApellido(a.getApellido());
        dto.setCorreo(a.getCorreo());
        dto.setRol("ALUMNO");
        return dto;
    }
}
