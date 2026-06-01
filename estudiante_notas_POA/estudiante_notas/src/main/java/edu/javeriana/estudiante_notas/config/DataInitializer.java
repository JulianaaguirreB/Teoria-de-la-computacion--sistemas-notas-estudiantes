package edu.javeriana.estudiante_notas.config;

import edu.javeriana.estudiante_notas.modelo.Alumno;
import edu.javeriana.estudiante_notas.modelo.Materia;
import edu.javeriana.estudiante_notas.modelo.Nota;
import edu.javeriana.estudiante_notas.modelo.Profesor;
import edu.javeriana.estudiante_notas.repositorio.RepositorioAlumno;
import edu.javeriana.estudiante_notas.repositorio.RepositorioMateria;
import edu.javeriana.estudiante_notas.repositorio.RepositorioNota;
import edu.javeriana.estudiante_notas.repositorio.RepositorioProfesor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga datos de prueba al iniciar la aplicación.
 * Útil para probar login con usuarios ya creados.
 *
 * Usuarios de prueba:
 *   Profesor: profesor@javeriana.edu / prof123  (ROL: PROFESOR)
 *   Alumno:   alumno@javeriana.edu  / alumno123 (ROL: ALUMNO)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private RepositorioProfesor repositorioProfesor;
    @Autowired private RepositorioAlumno repositorioAlumno;
    @Autowired private RepositorioMateria repositorioMateria;
    @Autowired private RepositorioNota repositorioNota;

    @Override
    public void run(String... args) {
        log.info("=== Cargando datos de prueba ===");

        // Crear profesor
        Profesor prof = new Profesor();
        prof.setNombre("Carlos");
        prof.setApellido("Rodríguez");
        prof.setCorreo("profesor@javeriana.edu");
        prof.setContrasena("prof123");
        prof = repositorioProfesor.save(prof);

        // Crear alumnos
        Alumno alumno1 = new Alumno();
        alumno1.setNombre("María");
        alumno1.setApellido("García");
        alumno1.setCorreo("alumno@javeriana.edu");
        alumno1.setContrasena("alumno123");
        alumno1 = repositorioAlumno.save(alumno1);

        Alumno alumno2 = new Alumno();
        alumno2.setNombre("Pedro");
        alumno2.setApellido("López");
        alumno2.setCorreo("pedro@javeriana.edu");
        alumno2.setContrasena("pedro123");
        alumno2 = repositorioAlumno.save(alumno2);

        // Crear materias
        Materia tc = new Materia(null, "Teoría de la Computación", 3);
        tc = repositorioMateria.save(tc);

        Materia poo = new Materia(null, "Programación Orientada a Objetos", 4);
        poo = repositorioMateria.save(poo);

        Materia bd = new Materia(null, "Bases de Datos", 3);
        bd = repositorioMateria.save(bd);

        // Crear notas de ejemplo para alumno1
        Nota n1 = new Nota(null, alumno1, tc, prof, "Parcial 1 - Buena comprensión de autómatas", 4.5, 30.0);
        repositorioNota.save(n1);

        Nota n2 = new Nota(null, alumno1, tc, prof, "Parcial 2 - Excelente", 4.8, 40.0);
        repositorioNota.save(n2);

        Nota n3 = new Nota(null, alumno1, poo, prof, "Taller AOP - Bien estructurado", 4.2, 25.0);
        repositorioNota.save(n3);

        log.info("=== Datos cargados correctamente ===");
        log.info("Profesor: profesor@javeriana.edu / prof123");
        log.info("Alumno:   alumno@javeriana.edu  / alumno123 (ID: {})", alumno1.getId());
    }
}
