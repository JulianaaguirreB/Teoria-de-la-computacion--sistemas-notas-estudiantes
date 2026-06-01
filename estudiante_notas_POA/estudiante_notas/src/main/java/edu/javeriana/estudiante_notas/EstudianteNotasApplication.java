package edu.javeriana.estudiante_notas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EstudianteNotasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstudianteNotasApplication.class, args);
    }
}
