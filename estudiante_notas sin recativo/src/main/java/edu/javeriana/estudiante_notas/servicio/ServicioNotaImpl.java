package edu.javeriana.estudiante_notas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import edu.javeriana.estudiante_notas.dto.NotaDto;
import edu.javeriana.estudiante_notas.modelo.Estudiante;
import edu.javeriana.estudiante_notas.modelo.Nota;
import edu.javeriana.estudiante_notas.repositorio.RepositorioEstudiante;
import edu.javeriana.estudiante_notas.repositorio.RepositorioNota;

@Service
@Transactional
public class ServicioNotaImpl implements ServicioNota {
    
    @Autowired
    private RepositorioNota repositorioNota;
    
    @Autowired
    private RepositorioEstudiante repositorioEstudiante;
    
    @Override
    public List<NotaDto> findNotasByEstudianteId(Long estudianteId) {
        List<Nota> notas = repositorioNota.findByEstudianteId(estudianteId);
        return notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public NotaDto saveNota(NotaDto notaDto) {
        Nota nota = convertirAEntidad(notaDto);
        
        // Validar que la suma de porcentajes no supere 100%
        validarPorcentajes(nota.getEstudiante().getId(), notaDto.getPorcentaje(), null);
        
        Nota notaGuardada = repositorioNota.save(nota);
        return convertirADTO(notaGuardada);
    }
    
    @Override
    public NotaDto updateNota(NotaDto notaDto) {
        Nota notaExistente = repositorioNota.findById(notaDto.getId())
            .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + notaDto.getId()));
        
        // Validar la suma de porcentajes excluyendo esta nota
        validarPorcentajes(notaExistente.getEstudiante().getId(), notaDto.getPorcentaje(), notaDto.getId());
        
        notaExistente.setMateria(notaDto.getMateria());
        notaExistente.setObservacion(notaDto.getObservacion());
        notaExistente.setValor(notaDto.getValor());
        notaExistente.setPorcentaje(notaDto.getPorcentaje());
        
        Nota notaActualizada = repositorioNota.save(notaExistente);
        return convertirADTO(notaActualizada);
    }
    
    @Override
    public void deleteNota(Long id) {
        if (!repositorioNota.existsById(id)) {
            throw new RuntimeException("Nota no encontrada con ID: " + id);
        }
        repositorioNota.deleteById(id);
    }
    
    @Override
    public double calcularNotaFinal(Long estudianteId) {
        List<Nota> notas = repositorioNota.findByEstudianteId(estudianteId);
        
        if (notas.isEmpty()) {
            return 0.0;
        }
        
        double sumaContribuciones = notas.stream()
            .mapToDouble(nota -> (nota.getValor() * nota.getPorcentaje()) / 100)
            .sum();
        
        return Math.round(sumaContribuciones * 100.0) / 100.0;
    }
    
    // Validación de porcentajes
    private void validarPorcentajes(Long estudianteId, Double nuevoPorcentaje, Long notaIdExcluir) {
        List<Nota> notas = repositorioNota.findByEstudianteId(estudianteId);
        
        double sumaActual = notas.stream()
            .filter(nota -> notaIdExcluir == null || nota.getId() != notaIdExcluir)
            .mapToDouble(Nota::getPorcentaje)
            .sum();
        
        if (sumaActual + nuevoPorcentaje > 100) {
            throw new RuntimeException(
                String.format("La suma de porcentajes superaría el 100% (Actual: %.1f%%, Nuevo: %.1f%%)", 
                    sumaActual, nuevoPorcentaje)
            );
        }
    }
    
    // Métodos de conversión
    private NotaDto convertirADTO(Nota nota) {
        NotaDto dto = new NotaDto();
        dto.setId(nota.getId());
        dto.setMateria(nota.getMateria());
        dto.setObservacion(nota.getObservacion());
        dto.setValor(nota.getValor());
        dto.setPorcentaje(nota.getPorcentaje());
        if (nota.getEstudiante() != null) {
            dto.setEstudianteId(nota.getEstudiante().getId());
        }
        return dto;
    }
    
    private Nota convertirAEntidad(NotaDto dto) {
        Nota nota = new Nota();
        nota.setId(dto.getId());
        nota.setMateria(dto.getMateria());
        nota.setObservacion(dto.getObservacion());
        nota.setValor(dto.getValor());
        nota.setPorcentaje(dto.getPorcentaje());
        
        if (dto.getEstudianteId() != null) {
            Estudiante estudiante = repositorioEstudiante.findById(dto.getEstudianteId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + dto.getEstudianteId()));
            nota.setEstudiante(estudiante);
        }
        
        return nota;
    }
}