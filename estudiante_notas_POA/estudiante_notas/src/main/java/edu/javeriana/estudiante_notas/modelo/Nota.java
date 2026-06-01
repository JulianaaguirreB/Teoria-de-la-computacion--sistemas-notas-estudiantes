package edu.javeriana.estudiante_notas.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nota")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    @JsonIgnore
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    @JsonIgnore
    private Profesor profesor;

    @Column(nullable = false)
    private String observacion;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private Double porcentaje;
}
