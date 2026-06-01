package edu.javeriana.estudiante_notas.servicio;

import edu.javeriana.estudiante_notas.dto.LoginDTO;
import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import edu.javeriana.estudiante_notas.modelo.Usuario;

public interface ServicioAuth {
    Usuario login(LoginDTO loginDTO);
    UsuarioDTO registrar(UsuarioDTO usuarioDTO);
}
