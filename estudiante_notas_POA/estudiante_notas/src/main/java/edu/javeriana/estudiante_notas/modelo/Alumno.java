package edu.javeriana.estudiante_notas.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ALUMNO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Alumno extends Usuario {

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Nota> notas = new ArrayList<>();

    public Alumno(Long id, String nombre, String apellido, String correo, String contrasena) {
        super(id, nombre, apellido, correo, contrasena);
    }

    @Override
    public String getRol() {
        return "ALUMNO";
    }
}
