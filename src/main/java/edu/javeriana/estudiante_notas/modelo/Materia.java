package edu.javeriana.estudiante_notas.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("materia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Materia {
    @Id
    private Long id;
    private String nombre;
    private Integer creditos;
}
