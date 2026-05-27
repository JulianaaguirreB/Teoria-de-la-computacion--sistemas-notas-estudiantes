package edu.javeriana.estudiante_notas.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("nota")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nota {
    @Id
    private Long id;
    
    @Column("materia_id")
    private Long materiaId;
    
    @Column("estudiante_id")
    private Long estudianteId;
    
    private String observacion;
    private Double valor;
    private Double porcentaje;
}
