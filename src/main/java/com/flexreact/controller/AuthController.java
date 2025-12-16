package com.flexreact.controller;

import com.flexreact.dto.ActualizarUsuarioRequest;
import com.flexreact.dto.UsuarioResponse;
import com.flexreact.entity.Usuario;
import com.flexreact.repository.UsuarioRepository;
import com.flexreact.security.JwtUtil;
import com.flexreact.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Autenticación", description = "API de autenticación y registro de usuarios")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * POST /api/auth/register - Registrar un nuevo usuario (compatibilidad)
     */
    @Operation(summary = "Registrar nuevo usuario", 
               description = "Crea una nueva cuenta de usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody ActualizarUsuarioRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.crearUsuario(request);
            
            // Generar token JWT para iniciar sesión automáticamente
            String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuario);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/auth/login - Iniciar sesión
     */
    @Operation(summary = "Iniciar sesión", 
               description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            if (email == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email y contraseña son requeridos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
            
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                throw new RuntimeException("Credenciales inválidas");
            }
            
            String token = jwtUtil.generateToken(usuario.getId(), usuario.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", UsuarioResponse.builder()
                    .id(usuario.getId())
                    .nombre(usuario.getNombre())
                    .email(usuario.getEmail())
                    .build());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
