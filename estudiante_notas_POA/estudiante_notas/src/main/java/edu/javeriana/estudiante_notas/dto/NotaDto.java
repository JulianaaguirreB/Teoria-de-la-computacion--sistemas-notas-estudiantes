package edu.javeriana.estudiante_notas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {
    private Long id;

    @NotNull(message = "El ID del alumno es obligatorio")
    private Long alumnoId;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Long materiaId;

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long profesorId;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;

    @NotNull(message = "El valor es obligatorio")
    @Min(value = 0, message = "La nota mínima es 0.0")
    @Max(value = 5, message = "La nota máxima es 5.0")
    private Double valor;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje debe ser al menos 1")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private Double porcentaje;

    // Campos de solo lectura en respuesta
    private String materiaNombre;
    private String alumnoNombre;
}
