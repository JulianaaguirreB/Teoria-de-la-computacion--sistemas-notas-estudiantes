package edu.javeriana.estudiante_notas.controlador;

import edu.javeriana.estudiante_notas.dto.LoginDTO;
import edu.javeriana.estudiante_notas.dto.UsuarioDTO;
import edu.javeriana.estudiante_notas.modelo.Usuario;
import edu.javeriana.estudiante_notas.servicio.ServicioAuth;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    @Autowired
    private ServicioAuth servicioAuth;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        Usuario usuario = servicioAuth.login(loginDTO);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("rol", usuario.getRol());
        session.setAttribute("nombre", usuario.getNombre() + " " + usuario.getApellido());
        return ResponseEntity.ok(Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre() + " " + usuario.getApellido(),
            "correo", usuario.getCorreo(),
            "rol", usuario.getRol()
        ));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

    // POST /api/auth/registro
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO registrado = servicioAuth.registrar(usuarioDTO);
        return ResponseEntity.status(201).body(registrado);
    }

    // GET /api/auth/sesion - Consultar sesión activa
    @GetMapping("/sesion")
    public ResponseEntity<?> sesion(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No hay sesión activa"));
        }
        return ResponseEntity.ok(Map.of(
            "id", usuarioId,
            "nombre", session.getAttribute("nombre"),
            "rol", session.getAttribute("rol")
        ));
    }
}
