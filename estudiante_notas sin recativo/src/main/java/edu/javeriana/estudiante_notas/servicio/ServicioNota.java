package edu.javeriana.estudiante_notas.servicio;

import java.util.List;
import edu.javeriana.estudiante_notas.dto.NotaDto;

public interface ServicioNota {
    List<NotaDto> findNotasByEstudianteId(Long estudianteId);
    NotaDto saveNota(NotaDto notaDto);
    NotaDto updateNota(NotaDto notaDto);
    void deleteNota(Long id);
    double calcularNotaFinal(Long estudianteId);
}