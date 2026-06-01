package edu.javeriana.estudiante_notas.aspecto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
public class AspectControlAcceso {

    private static final Logger log = LoggerFactory.getLogger(AspectControlAcceso.class);
    
    // Logger ESPECÍFICO para violaciones de seguridad (se guardará en archivo separado)
    private static final Logger securityLog = LoggerFactory.getLogger("SECURITY_VIOLATIONS");

    @Around("execution(* edu.javeriana.estudiante_notas.controlador.NotaControlador.*(..))")
    public Object controlarAcceso(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = obtenerRequest();
        HttpSession session = request.getSession(false);
        
        String metodoHttp = request.getMethod();
        String nombreMetodo = joinPoint.getSignature().getName();
        String endpoint = request.getRequestURI();
        String ipCliente = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        
        // ==================== VIOLACION 1: SIN SESIÓN ====================
        if (session == null || session.getAttribute("usuarioId") == null) {
            String violacion = String.format(
                "TIPO:SIN_SESION | IP:%s | USER_AGENT:%s | METODO:%s | ENDPOINT:%s%s | TIMESTAMP:%s",
                ipCliente, sanitizeUserAgent(userAgent), metodoHttp, endpoint, queryString, getTimestamp()
            );
            securityLog.warn(violacion);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión para acceder a este recurso"));
        }

        String rol = (String) session.getAttribute("rol");
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNombre = (String) session.getAttribute("usuarioNombre");

        log.info("Acceso solicitado - Usuario ID: {}, Rol: {}, Método: {}, Endpoint: {}", 
            usuarioId, rol, metodoHttp, endpoint);

        // ==================== VIOLACION 2: ROL INVÁLIDO ====================
        if (!"PROFESOR".equals(rol) && !"ALUMNO".equals(rol)) {
            String violacion = String.format(
                "TIPO:ROL_INVALIDO | USUARIO_ID:%d | ROL:%s | IP:%s | ENDPOINT:%s | TIMESTAMP:%s",
                usuarioId, rol, ipCliente, endpoint, getTimestamp()
            );
            securityLog.warn(violacion);
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Rol no autorizado: " + rol));
        }

        // ==================== ROL PROFESOR: ACCESO TOTAL ====================
        if ("PROFESOR".equals(rol)) {
            log.info("ACCESO CONCEDIDO a PROFESOR ID: {} - {}", usuarioId, nombreMetodo);
            return joinPoint.proceed();
        }

        // ==================== ROL ALUMNO: VALIDACIONES ESPECÍFICAS ====================
        if ("ALUMNO".equals(rol)) {
            
            // ==================== VIOLACION 3: INTENTO DE EDITAR NOTA ====================
            if ("PUT".equals(metodoHttp) && endpoint.contains("/api/notas/")) {
                String violacion = String.format(
                    "TIPO:INTENTO_EDITAR_NOTA | USUARIO_ID:%d | USUARIO:%s | IP:%s | METODO_HTTP:%s | ENDPOINT:%s | TIMESTAMP:%s",
                    usuarioId, usuarioNombre, ipCliente, metodoHttp, endpoint, getTimestamp()
                );
                securityLog.warn(violacion);
                log.warn("⚠ ALUMNO ID {} intentó editar una nota en {} - ACCIÓN DENEGADA", usuarioId, endpoint);
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No puedes editar notas. Solo los profesores tienen permiso para modificar notas."));
            }

            // ==================== VIOLACION 4: OTROS MÉTODOS NO PERMITIDOS ====================
            if (!"GET".equals(metodoHttp)) {
                String violacion = String.format(
                    "TIPO:METODO_NO_PERMITIDO | USUARIO_ID:%d | USUARIO:%s | IP:%s | METODO_HTTP:%s | ENDPOINT:%s | TIMESTAMP:%s",
                    usuarioId, usuarioNombre, ipCliente, metodoHttp, endpoint, getTimestamp()
                );
                securityLog.warn(violacion);
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Los alumnos solo pueden realizar consultas (GET)"));
            }
            
            // ==================== VIOLACION 4: ACCESO A NOTAS DE OTRO ALUMNO ====================
            Long alumnoIdSolicitado = extraerAlumnoIdDelMetodo(joinPoint, nombreMetodo);
            
            if (alumnoIdSolicitado != null && !alumnoIdSolicitado.equals(usuarioId)) {
                String violacion = String.format(
                    "TIPO:ACCESO_NO_AUTORIZADO | USUARIO_ID:%d INTENTO_ACCEDER_A_ALUMNO_ID:%d | IP:%s | ENDPOINT:%s | TIMESTAMP:%s",
                    usuarioId, alumnoIdSolicitado, ipCliente, endpoint, getTimestamp()
                );
                securityLog.warn(violacion);
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo puede consultar sus propias notas"));
            }
            
            // ==================== VIOLACION 5: INYECCIÓN DE PARÁMETROS (detección) ====================
            if (detectarInyeccionParametros(request)) {
                String violacion = String.format(
                    "TIPO:POSIBLE_INYECCION | USUARIO_ID:%d | IP:%s | QUERY_STRING:%s | TIMESTAMP:%s",
                    usuarioId, ipCliente, queryString, getTimestamp()
                );
                securityLog.warn(violacion);
                // No bloqueamos aquí, solo registramos, pero podríamos bloquear
            }
            
            // ==================== VIOLACION 6: ACCESO A ENDPOINT SENSIBLE ====================
            if (esEndpointSensible(endpoint) && !tienePermisoEspecial(usuarioId, endpoint)) {
                String violacion = String.format(
                    "TIPO:ENDPOINT_SENSIBLE | USUARIO_ID:%d | IP:%s | ENDPOINT:%s | TIMESTAMP:%s",
                    usuarioId, ipCliente, endpoint, getTimestamp()
                );
                securityLog.warn(violacion);
            }
            
            // Acceso permitido
            log.info("ACCESO CONCEDIDO a ALUMNO ID: {} - {}", usuarioId, nombreMetodo);
            return joinPoint.proceed();
        }

        return joinPoint.proceed();
    }
    
    /**
     * Obtiene la IP real del cliente (considerando proxies)
     */
    private String getClientIP(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0];
            }
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Detecta posibles inyecciones en parámetros
     */
    private boolean detectarInyeccionParametros(HttpServletRequest request) {
        String[] parametrosPeligrosos = {"'", "\"", "--", ";", "xp_", "exec", "union", "select", "insert", "delete", "update"};
        
        Map<String, String[]> parametros = request.getParameterMap();
        for (String[] valores : parametros.values()) {
            for (String valor : valores) {
                if (valor != null) {
                    String valorLower = valor.toLowerCase();
                    for (String peligroso : parametrosPeligrosos) {
                        if (valorLower.contains(peligroso)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Detecta si es un endpoint sensible
     */
    private boolean esEndpointSensible(String endpoint) {
        String[] endpointsSensibles = {"/admin", "/config", "/metrics", "/actuator", "/swagger", "/api-docs"};
        String endpointLower = endpoint.toLowerCase();
        return Arrays.stream(endpointsSensibles).anyMatch(endpointLower::contains);
    }
    
    /**
     * Verifica permisos especiales (puedes expandir según necesidades)
     */
    private boolean tienePermisoEspecial(Long usuarioId, String endpoint) {
        // Aquí podrías verificar si el usuario tiene permisos especiales para ciertos endpoints
        // Por ahora retorna false
        return false;
    }
    
    /**
     * Sanitiza User Agent para evitar inyecciones en log
     */
    private String sanitizeUserAgent(String userAgent) {
        if (userAgent == null) return "DESCONOCIDO";
        // Remover caracteres peligrosos
        return userAgent.replaceAll("[\\n\\r\\t]", "_");
    }
    
    /**
     * Extrae el ID del alumno de los parámetros del método
     */
    private Long extraerAlumnoIdDelMetodo(ProceedingJoinPoint joinPoint, String nombreMetodo) {
        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0) {
            switch (nombreMetodo) {
                case "getNotasByAlumno":
                case "getNotaFinal":
                    if (args[0] instanceof Long) {
                        return (Long) args[0];
                    }
                    break;
                default:
                    return null;
            }
        }
        return null;
    }
    
    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private HttpServletRequest obtenerRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }
}