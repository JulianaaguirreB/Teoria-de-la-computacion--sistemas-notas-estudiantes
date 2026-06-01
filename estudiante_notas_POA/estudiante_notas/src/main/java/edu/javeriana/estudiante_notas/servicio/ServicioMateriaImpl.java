package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.MateriaDTO;
import edu.javeriana.estudiante_notas.modelo.Materia;
import edu.javeriana.estudiante_notas.repositorio.RepositorioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioMateriaImpl implements ServicioMateria {

    @Autowired
    private RepositorioMateria repositorioMateria;

    @Override
    public List<MateriaDTO> findAllMaterias() {
        return repositorioMateria.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public MateriaDTO findMateriaById(Long id) {
        return toDTO(repositorioMateria.findById(id)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + id)));
    }

    @Override
    public MateriaDTO saveMateria(MateriaDTO dto) {
        repositorioMateria.findByNombre(dto.getNombre()).ifPresent(m -> {
            throw new RuntimeException("La materia '" + dto.getNombre() + "' ya existe");
        });
        Materia m = new Materia(null, dto.getNombre(), dto.getCreditos());
        return toDTO(repositorioMateria.save(m));
    }

    @Override
    public MateriaDTO updateMateria(MateriaDTO dto) {
        Materia m = repositorioMateria.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + dto.getId()));
        m.setNombre(dto.getNombre());
        m.setCreditos(dto.getCreditos());
        return toDTO(repositorioMateria.save(m));
    }

    @Override
    public void deleteMateria(Long id) {
        if (!repositorioMateria.existsById(id)) {
            throw new RuntimeException("Materia no encontrada con ID: " + id);
        }
        repositorioMateria.deleteById(id);
    }

    private MateriaDTO toDTO(Materia m) {
        return new MateriaDTO(m.getId(), m.getNombre(), m.getCreditos());
    }
}
