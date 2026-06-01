package edu.javeriana.estudiante_notas.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PROFESOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Profesor extends Usuario {

    public Profesor(Long id, String nombre, String apellido, String correo, String contrasena) {
        super(id, nombre, apellido, correo, contrasena);
    }

    @Override
    public String getRol() {
        return "PROFESOR";
    }
}
