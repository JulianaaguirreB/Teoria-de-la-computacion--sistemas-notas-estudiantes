package edu.javeriana.estudiante_notas.servicio;
import java.util.List;
import edu.javeriana.estudiante_notas.dto.EstudianteDTO;

public interface ServicioEstudiante {
    List<EstudianteDTO> findAllEstudiantes();
    EstudianteDTO findEstudianteByID(long id);
    EstudianteDTO saveEstudianteByID(EstudianteDTO estudiante);
    void deleteEstudiante(long id);
    EstudianteDTO updateEstudiante(EstudianteDTO estudiante);
    double calcularNotafinal(long estudianteId);
}
