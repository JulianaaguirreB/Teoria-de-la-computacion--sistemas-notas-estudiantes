package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import java.util.List;

public interface ServicioMateria {
    List<MateriaDTO> findAllMaterias();
    MateriaDTO findMateriaById(Long id);
    MateriaDTO saveMateria(MateriaDTO dto);
    MateriaDTO updateMateria(MateriaDTO dto);
    void deleteMateria(Long id);
}
