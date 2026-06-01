package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.LoginDTO;
import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import edu.javeriana.estudiante_notas.modelo.Alumno;
import edu.javeriana.estudiante_notas.modelo.Profesor;
import edu.javeriana.estudiante_notas.modelo.Usuario;
import edu.javeriana.estudiante_notas.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioAuthImpl implements ServicioAuth {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Override
    public Usuario login(LoginDTO loginDTO) {
        Usuario usuario = repositorioUsuario.findByCorreo(loginDTO.getCorreo())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!usuario.getContrasena().equals(loginDTO.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!usuario.getRol().equalsIgnoreCase(loginDTO.getRol())) {
            throw new RuntimeException(
                "El usuario no tiene el rol '" + loginDTO.getRol() + "'");
        }

        return usuario;
    }

    @Override
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO) {
        repositorioUsuario.findByCorreo(usuarioDTO.getCorreo()).ifPresent(u -> {
            throw new RuntimeException("El correo ya está registrado: " + usuarioDTO.getCorreo());
        });

        Usuario usuario;
        if ("PROFESOR".equalsIgnoreCase(usuarioDTO.getRol())) {
            Profesor p = new Profesor();
            p.setNombre(usuarioDTO.getNombre());
            p.setApellido(usuarioDTO.getApellido());
            p.setCorreo(usuarioDTO.getCorreo());
            p.setContrasena(usuarioDTO.getContrasena());
            usuario = repositorioUsuario.save(p);
        } else if ("ALUMNO".equalsIgnoreCase(usuarioDTO.getRol())) {
            Alumno a = new Alumno();
            a.setNombre(usuarioDTO.getNombre());
            a.setApellido(usuarioDTO.getApellido());
            a.setCorreo(usuarioDTO.getCorreo());
            a.setContrasena(usuarioDTO.getContrasena());
            usuario = repositorioUsuario.save(a);
        } else {
            throw new RuntimeException("Rol no válido. Use ALUMNO o PROFESOR");
        }

        UsuarioDTO resultado = new UsuarioDTO();
        resultado.setId(usuario.getId());
        resultado.setNombre(usuario.getNombre());
        resultado.setApellido(usuario.getApellido());
        resultado.setCorreo(usuario.getCorreo());
        resultado.setRol(usuario.getRol());
        return resultado;
    }
}
