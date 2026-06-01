package edu.javeriana.estudiante_notas.aspecto;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@RestControllerAdvice
public class AspectExcepciones {

    private static final Logger log = LoggerFactory.getLogger(AspectExcepciones.class);
    
    // 🔥 AGREGAR: Logger específico para violaciones de seguridad
    private static final Logger securityLog = LoggerFactory.getLogger("SECURITY_VIOLATIONS");

    /**
     * AOP @AfterThrowing: registra en el log de seguridad las violaciones de autenticación
     */
    @AfterThrowing(
        pointcut = "execution(* edu.javeriana.estudiante_notas.servicio.ServicioAuth.login(..))",
        throwing = "ex"
    )
    public void logViolacionSeguridad(JoinPoint joinPoint, Exception ex) {
        String mensaje = ex.getMessage();
        
        // Detectar diferentes tipos de violaciones
        if (mensaje != null) {
            String violacion = null;
            
            if (mensaje.contains("no tiene el rol")) {
                // Extraer roles del mensaje
                violacion = "TIPO:ROL_NO_COINCIDE | DETALLE:" + mensaje + " | TIMESTAMP:" + LocalDateTime.now();
            } 
            else if (mensaje.contains("Credenciales inválidas")) {
                violacion = "TIPO:CREDENCIALES_INVALIDAS | DETALLE:" + mensaje + " | TIMESTAMP:" + LocalDateTime.now();
            }
            else if (mensaje.contains("Rol inválido") || mensaje.contains("Use ALUMNO o PROFESOR")) {
                violacion = "TIPO:ROL_INVALIDO_EN_LOGIN | DETALLE:" + mensaje + " | TIMESTAMP:" + LocalDateTime.now();
            }
            else if (mensaje.contains("correo ya está registrado")) {
                violacion = "TIPO:REGISTRO_CORREO_DUPLICADO | DETALLE:" + mensaje + " | TIMESTAMP:" + LocalDateTime.now();
            }
            
            if (violacion != null) {
                securityLog.warn(violacion);  // 🔥 Guarda en el archivo de violaciones
            }
        }
        
        // También mantener el log original
        String clase = joinPoint.getSignature().getDeclaringTypeName();
        String metodo = joinPoint.getSignature().getName();
        log.error("[AOP-EXCEPCION] {}.{}() lanzó: {} - Mensaje: {}",
            clase, metodo, ex.getClass().getSimpleName(), ex.getMessage());
    }

    /**
     * Maneja excepciones de negocio (RuntimeException con mensaje descriptivo)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage();
        
        // Registrar en log de seguridad si es una violación relacionada con roles/autenticación
        if (mensaje != null && (mensaje.contains("rol") || mensaje.contains("Credenciales") || 
            mensaje.contains("autentic") || mensaje.contains("permiso"))) {
            String violacion = String.format(
                "TIPO:EXCEPCION_SEGURIDAD | MENSAJE:%s | TIMESTAMP:%s",
                mensaje, LocalDateTime.now()
            );
            securityLog.warn(violacion);
        }
        
        log.warn("[EXCEPCION-NEGOCIO] {}", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(construirRespuesta("Error de negocio", mensaje, 400));
    }

    /**
     * Maneja errores de validación de Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.warn("[EXCEPCION-VALIDACION] {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(construirRespuesta("Error de validación", errores, 400));
    }

    /**
     * Maneja cualquier otra excepción no controlada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(Exception ex) {
        log.error("[EXCEPCION-GENERAL] Excepción no controlada: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(construirRespuesta("Error interno del servidor",
                "Ocurrió un error inesperado. Contacte al administrador.", 500));
    }

    private Map<String, Object> construirRespuesta(String tipo, String mensaje, int codigo) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("tipo", tipo);
        respuesta.put("mensaje", mensaje);
        respuesta.put("codigo", codigo);
        respuesta.put("timestamp", LocalDateTime.now().toString());
        return respuesta;
    }
}