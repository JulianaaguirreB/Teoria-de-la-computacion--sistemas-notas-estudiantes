
package edu.javeriana.estudiante_notas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors; 
import java.util.List;
import edu.javeriana.estudiante_notas.dto.EstudianteDTO;
import edu.javeriana.estudiante_notas.modelo.Estudiante;
import edu.javeriana.estudiante_notas.repositorio.RepositorioEstudiante;

@Service 
@Transactional
public class ServicioEstudianteImpl implements ServicioEstudiante {
    
    @Autowired
    private RepositorioEstudiante repositorioEstudiante;
    @Autowired
    private ServicioNota servicioNota;
    
    @Override
    public List<EstudianteDTO> findAllEstudiantes() {
        return repositorioEstudiante.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public EstudianteDTO findEstudianteByID(long id) {
        Estudiante estudiante = repositorioEstudiante.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));
        return convertirADTO(estudiante);
    }
    
    @Override
    public EstudianteDTO saveEstudianteByID(EstudianteDTO estudianteDTO) {
        Estudiante estudiante = convertirAEntidad(estudianteDTO);
        Estudiante estudianteGuardado = repositorioEstudiante.save(estudiante);
        return convertirADTO(estudianteGuardado);
    }
    
    @Override
    public void deleteEstudiante(long id) {
        if (!repositorioEstudiante.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado con ID: " + id);
        }
        repositorioEstudiante.deleteById(id);
    }
    
    @Override
    public EstudianteDTO updateEstudiante(EstudianteDTO estudianteDTO) {
        
        Estudiante estudianteExistente = repositorioEstudiante.findById(estudianteDTO.getId())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + estudianteDTO.getId()));
        
        
        estudianteExistente.setNombre(estudianteDTO.getNombre());
        estudianteExistente.setApellido(estudianteDTO.getApellido());
        estudianteExistente.setCorreo(estudianteDTO.getCorreo()); 
        
        Estudiante estudianteActualizado = repositorioEstudiante.save(estudianteExistente);
        return convertirADTO(estudianteActualizado);
    }
    
    @Override
    public double calcularNotafinal(long estudianteId) {
        return servicioNota.calcularNotaFinal(estudianteId);
    }
    
    private EstudianteDTO convertirADTO(Estudiante estudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setApellido(estudiante.getApellido());
        dto.setCorreo(estudiante.getCorreo());
        return dto;
    }
    
    private Estudiante convertirAEntidad(EstudianteDTO dto) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(dto.getId());
        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());
        estudiante.setCorreo(dto.getCorreo());
        
        return estudiante;
    }

    
}