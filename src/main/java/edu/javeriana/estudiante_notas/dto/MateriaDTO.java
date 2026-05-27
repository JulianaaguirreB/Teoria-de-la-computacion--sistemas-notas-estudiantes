package edu.javeriana.estudiante_notas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MateriaDTO {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotNull(message = "Los crÃ©ditos son obligatorios")
    @Min(value = 1, message = "Los crÃ©ditos deben ser positivos")
    private Integer creditos;
}
