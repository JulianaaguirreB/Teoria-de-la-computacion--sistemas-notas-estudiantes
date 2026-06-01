package edu.javeriana.estudiante_notas.modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT a.id AS alumno_id, a.nombre || ' ' || a.apellido AS alumno_nombre, " +
           "m.id AS materia_id, m.nombre AS materia_nombre, " +
           "ROUND(SUM(n.valor * n.porcentaje) / 100.0, 2) AS promedio_ponderado, " +
           "SUM(n.porcentaje) AS porcentaje_acumulado, " +
           "COUNT(n.id) AS total_notas " +
           "FROM nota n " +
           "JOIN usuario a ON a.id = n.alumno_id " +
           "JOIN materia m ON m.id = n.materia_id " +
           "GROUP BY a.id, a.nombre, a.apellido, m.id, m.nombre")
public class ResumenPromedio {

    @Id
    @Column(name = "materia_id")
    private Long materiaId;

    @Column(name = "alumno_id")
    private Long alumnoId;

    @Column(name = "alumno_nombre")
    private String alumnoNombre;

    @Column(name = "materia_nombre")
    private String materiaNombre;

    @Column(name = "promedio_ponderado")
    private Double promedioPonderado;

    @Column(name = "porcentaje_acumulado")
    private Double porcentajeAcumulado;

    @Column(name = "total_notas")
    private Integer totalNotas;

    public Long getMateriaId() { return materiaId; }
    public Long getAlumnoId() { return alumnoId; }
    public String getAlumnoNombre() { return alumnoNombre; }
    public String getMateriaNombre() { return materiaNombre; }
    public Double getPromedioPonderado() { return promedioPonderado; }
    public Double getPorcentajeAcumulado() { return porcentajeAcumulado; }
    public Integer getTotalNotas() { return totalNotas; }
}
