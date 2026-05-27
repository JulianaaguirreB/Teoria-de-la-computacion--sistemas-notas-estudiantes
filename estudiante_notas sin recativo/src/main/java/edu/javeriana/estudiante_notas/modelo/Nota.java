package edu.javeriana.estudiante_notas.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;      
import jakarta.persistence.JoinColumn;  
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Nota { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false) 
    private String materia;

    @Column(nullable = false) 
    private String observacion;

    @Column(nullable = false)
    private double porcentaje;

    @Column(nullable = false)
    private double valor;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    @JsonIgnore
    private Estudiante estudiante; 
}