package edu.javeriana.estudiante_notas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {

    private Long id;

    @NotBlank(message = "La materia no puede estar vacía")
    private String materia;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje debe ser al menos 1%")
    @Max(value = 100, message = "El porcentaje no puede superar el 100%")
    private Double porcentaje; 

    @NotNull(message = "La nota es obligatoria")
    @Min(value = 0, message = "La nota mínima es 0.0")
    @Max(value = 5, message = "La nota máxima es 5.0")
    private Double valor;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId; 
}
