package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.modelo.Alumno;
import edu.javeriana.estudiante_notas.modelo.Materia;
import edu.javeriana.estudiante_notas.modelo.Nota;
import edu.javeriana.estudiante_notas.modelo.Profesor;
import edu.javeriana.estudiante_notas.modelo.ResumenPromedio;
import edu.javeriana.estudiante_notas.repositorio.RepositorioAlumno;
import edu.javeriana.estudiante_notas.repositorio.RepositorioMateria;
import edu.javeriana.estudiante_notas.repositorio.RepositorioNota;
import edu.javeriana.estudiante_notas.repositorio.RepositorioProfesor;
import edu.javeriana.estudiante_notas.repositorio.RepositorioResumenPromedio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioNotaImpl implements ServicioNota {

    private static final Logger log = LoggerFactory.getLogger(ServicioNotaImpl.class);

    @Autowired
    private RepositorioNota repositorioNota;
    @Autowired
    private RepositorioAlumno repositorioAlumno;
    @Autowired
    private RepositorioMateria repositorioMateria;
    @Autowired
    private RepositorioProfesor repositorioProfesor;
    @Autowired
    private RepositorioResumenPromedio repositorioResumenPromedio;

    @Override
    public List<NotaDto> findNotasByAlumnoId(Long alumnoId) {
        return repositorioNota.findByAlumnoId(alumnoId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<NotaDto> findNotasByMateriaId(Long materiaId) {
        return repositorioNota.findByMateriaId(materiaId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public NotaDto saveNota(NotaDto dto) {
        validarPorcentajes(dto.getAlumnoId(), dto.getMateriaId(), dto.getPorcentaje(), null);
        Nota nota = toEntity(dto);
        return toDTO(repositorioNota.save(nota));
    }

    @Override
    public NotaDto updateNota(NotaDto dto) {
        Nota existente = repositorioNota.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + dto.getId()));
        validarPorcentajes(existente.getAlumno().getId(), dto.getMateriaId(), dto.getPorcentaje(), dto.getId());
        existente.setObservacion(dto.getObservacion());
        existente.setValor(dto.getValor());
        existente.setPorcentaje(dto.getPorcentaje());
        if (dto.getMateriaId() != null) {
            Materia materia = repositorioMateria.findById(dto.getMateriaId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + dto.getMateriaId()));
            existente.setMateria(materia);
        }
        return toDTO(repositorioNota.save(existente));
    }

    @Override
    public void deleteNota(Long id) {
        if (!repositorioNota.existsById(id)) {
            throw new RuntimeException("Nota no encontrada con ID: " + id);
        }
        repositorioNota.deleteById(id);
    }

    // ==================== NUEVOS MÉTODOS PARA PROMEDIOS ====================
    
    /**
     * Calcula el promedio ponderado por cada materia
     * Ejemplo: {"Matemáticas": 4.5, "Español": 3.8, "Ciencias": 4.2}
     */
    @Override
    public Map<String, Double> calcularPromediosPorMateria(Long alumnoId) {
        List<ResumenPromedio> resumenes = repositorioResumenPromedio.findByAlumnoId(alumnoId);

        if (resumenes.isEmpty()) {
            log.info("No se encontraron promedios para el alumno ID: {} (usando @Subselect)", alumnoId);
            return new HashMap<>();
        }

        Map<String, Double> promedios = new LinkedHashMap<>();
        for (ResumenPromedio r : resumenes) {
            promedios.put(r.getMateriaNombre(), r.getPromedioPonderado());
            if (r.getPorcentajeAcumulado() != null && Math.abs(r.getPorcentajeAcumulado() - 100.0) > 0.01) {
                log.warn("La materia '{}' tiene suma de porcentajes: {}% (debería ser 100%)",
                    r.getMateriaNombre(), r.getPorcentajeAcumulado());
            }
        }
        return promedios;
    }

    /**
     * Calcula el promedio ponderado para una materia específica
     */
    @Override
    public double calcularPromedioPorMateria(Long alumnoId, Long materiaId) {
        List<Nota> notas = repositorioNota.findByAlumnoIdAndMateriaId(alumnoId, materiaId);
        
        if (notas.isEmpty()) {
            throw new RuntimeException("No hay notas registradas para este alumno en esta materia");
        }
        
        double sumaPonderada = 0.0;
        double sumaPorcentajes = 0.0;
        
        for (Nota nota : notas) {
            sumaPonderada += (nota.getValor() * nota.getPorcentaje()) / 100.0;
            sumaPorcentajes += nota.getPorcentaje();
        }
        
        // Verificar si los porcentajes suman 100%
        if (Math.abs(sumaPorcentajes - 100.0) > 0.01) {
            log.warn("La materia suma {}% (debería ser 100%)", sumaPorcentajes);
        }
        
        return Math.round(sumaPonderada * 100.0) / 100.0;
    }
    
    /**
     * Calcula el promedio GENERAL (promedio de los promedios por materia)
     * Ejemplo: Matemáticas=4.5, Español=3.8 → Promedio General = 4.15
     */
    @Override
    public double calcularNotaFinalGeneral(Long alumnoId) {
        Map<String, Double> promediosPorMateria = calcularPromediosPorMateria(alumnoId);
        
        if (promediosPorMateria.isEmpty()) {
            return 0.0;
        }
        
        double sumaPromedios = promediosPorMateria.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        double promedioGeneral = sumaPromedios / promediosPorMateria.size();
        
        return Math.round(promedioGeneral * 100.0) / 100.0;
    }

    private void validarPorcentajes(Long alumnoId, Long materiaId, Double nuevoPorcentaje, Long notaIdExcluir) {
        Materia materia = repositorioMateria.findById(materiaId)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + materiaId));
        List<Nota> notasMismaMateria = repositorioNota.findByAlumnoIdAndMateriaId(alumnoId, materiaId);
        double sumaActual = notasMismaMateria.stream()
            .filter(n -> notaIdExcluir == null || !n.getId().equals(notaIdExcluir))
            .mapToDouble(Nota::getPorcentaje)
            .sum();
        double sumaNueva = sumaActual + (nuevoPorcentaje != null ? nuevoPorcentaje : 0);
        if (sumaNueva > 100 + 0.01) {
            throw new RuntimeException(String.format(
                "La materia '%s' ya tiene %.1f%% acumulado. No puede agregar otro %.1f%% (total seria %.1f%%)",
                materia.getNombre(), sumaActual, nuevoPorcentaje, sumaNueva));
        }
        if (Math.abs(sumaNueva - 100.0) < 0.01) {
            log.info("La materia '{}' ha alcanzado el 100% de porcentaje para el alumno ID: {}",
                materia.getNombre(), alumnoId);
        }
    }

    private NotaDto toDTO(Nota nota) {
        NotaDto dto = new NotaDto();
        dto.setId(nota.getId());
        dto.setAlumnoId(nota.getAlumno().getId());
        dto.setMateriaId(nota.getMateria().getId());
        dto.setProfesorId(nota.getProfesor().getId());
        dto.setObservacion(nota.getObservacion());
        dto.setValor(nota.getValor());
        dto.setPorcentaje(nota.getPorcentaje());
        dto.setMateriaNombre(nota.getMateria().getNombre());
        dto.setAlumnoNombre(nota.getAlumno().getNombre() + " " + nota.getAlumno().getApellido());
        return dto;
    }

    private Nota toEntity(NotaDto dto) {
        Nota nota = new Nota();
        nota.setId(dto.getId());
        nota.setObservacion(dto.getObservacion());
        nota.setValor(dto.getValor());
        nota.setPorcentaje(dto.getPorcentaje());
        Alumno alumno = repositorioAlumno.findById(dto.getAlumnoId())
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + dto.getAlumnoId()));
        nota.setAlumno(alumno);
        Materia materia = repositorioMateria.findById(dto.getMateriaId())
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + dto.getMateriaId()));
        nota.setMateria(materia);
        Profesor profesor = repositorioProfesor.findById(dto.getProfesorId())
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado con ID: " + dto.getProfesorId()));
        nota.setProfesor(profesor);
        return nota;
    }

    @Override
    public Map<String, Double> obtenerPorcentajesPorMateria(Long alumnoId) {
        List<Nota> notas = repositorioNota.findByAlumnoId(alumnoId);
        Map<String, Double> porcentajesPorMateria = new LinkedHashMap<>();
        for (Nota nota : notas) {
            String materiaNombre = nota.getMateria().getNombre();
            Double porcentajeActual = porcentajesPorMateria.getOrDefault(materiaNombre, 0.0);
            porcentajesPorMateria.put(materiaNombre, porcentajeActual + nota.getPorcentaje());
        }
        return porcentajesPorMateria;
    }
}