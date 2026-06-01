package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.NotaDto;
import java.util.List;
import java.util.Map;

public interface ServicioNota {
    List<NotaDto> findNotasByAlumnoId(Long alumnoId);
    List<NotaDto> findNotasByMateriaId(Long materiaId);
    NotaDto saveNota(NotaDto notaDto);
    NotaDto updateNota(NotaDto notaDto);
    void deleteNota(Long id);
    Map<String, Double> calcularPromediosPorMateria(Long alumnoId);
    double calcularPromedioPorMateria(Long alumnoId, Long materiaId);
    double calcularNotaFinalGeneral(Long alumnoId);
    Map<String, Double> obtenerPorcentajesPorMateria(Long alumnoId);
}