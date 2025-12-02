package com.flexreact.controller;

import com.flexreact.dto.ActualizarUsuarioRequest;
import com.flexreact.dto.UsuarioResponse;
import com.flexreact.entity.Usuario;
import com.flexreact.repository.UsuarioRepository;
import com.flexreact.security.JwtUtil;
import com.flexreact.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody ActualizarUsuarioRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.crearUsuario(request);
            return ResponseEntity.ok(usuario);
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
