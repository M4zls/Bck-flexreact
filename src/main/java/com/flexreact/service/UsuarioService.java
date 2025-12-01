package com.flexreact.service;

import com.flexreact.dto.ActualizarUsuarioRequest;
import com.flexreact.dto.UsuarioResponse;
import com.flexreact.entity.Pedido;
import com.flexreact.entity.Usuario;
import com.flexreact.repository.PedidoRepository;
import com.flexreact.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Crear usuario
     */
    @Transactional
    public UsuarioResponse crearUsuario(ActualizarUsuarioRequest request) {
        // Verificar que el email no esté en uso
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return convertirAUsuarioResponse(nuevoUsuario);
    }
    
    /**
     * Obtener todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener un usuario por ID
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirAUsuarioResponse(usuario);
    }
    
    /**
     * Obtener un usuario por email
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return convertirAUsuarioResponse(usuario);
    }
    
    /**
     * Actualizar usuario
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(UUID id, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Actualizar campos si están presentes
        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            usuario.setNombre(request.getNombre());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Verificar que el email no esté en uso por otro usuario
            if (!usuario.getEmail().equals(request.getEmail()) && 
                usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            usuario.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAUsuarioResponse(usuarioActualizado);
    }
    
    /**
     * Eliminar usuario
     */
    @Transactional
    public void eliminarUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Obtener pedidos de un usuario
     */
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosDeUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        return pedidoRepository.findByUsuarioOrderByCreatedAtDesc(usuario);
    }
    
    /**
     * Contar pedidos de un usuario
     */
    @Transactional(readOnly = true)
    public long contarPedidosDeUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        return pedidoRepository.findByUsuarioOrderByCreatedAtDesc(usuario).size();
    }
    
    /**
     * Convertir Usuario a UsuarioResponse (sin password)
     */
    private UsuarioResponse convertirAUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .fechaRegistro(usuario.getFechaRegistro())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
