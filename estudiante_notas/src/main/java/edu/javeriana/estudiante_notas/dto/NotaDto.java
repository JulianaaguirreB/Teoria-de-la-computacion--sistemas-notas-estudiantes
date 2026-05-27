package edu.javeriana.estudiante_notas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {
    private Long id;
    
    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;
    
    private String observacion;
    
    @NotNull(message = "La nota es obligatoria")
    @Min(value = 0, message = "La nota mÃ­nima es 0.0")
    @Max(value = 5, message = "La nota mÃ¡xima es 5.0")
    private Double valor;
    
    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje debe ser al menos 1%")
    @Max(value = 100, message = "El porcentaje no puede superar el 100%")
    private Double porcentaje;
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;
    
    private String materiaNombre;
}
