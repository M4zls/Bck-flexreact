package com.flexreact.service;

import com.flexreact.dto.CrearPedidoRequest;
import com.flexreact.dto.PedidoResponse;
import com.flexreact.entity.ItemPedido;
import com.flexreact.entity.Pedido;
import com.flexreact.entity.Producto;
import com.flexreact.entity.Usuario;
import com.flexreact.repository.PedidoRepository;
import com.flexreact.repository.ProductoRepository;
import com.flexreact.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public Pedido crear(Usuario usuario, CrearPedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(request.getTotal());
        pedido.setEstado("pendiente");
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setMetodoPago(request.getMetodoPago());
        
        // Agregar items
        for (CrearPedidoRequest.ItemPedidoRequest itemRequest : request.getItems()) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductoId()));
            
            // Validar stock
            if (producto.getStock() < itemRequest.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(itemRequest.getPrecioUnitario());
            
            pedido.getItems().add(item);
            
            // Actualizar stock
            producto.setStock(producto.getStock() - itemRequest.getCantidad());
            productoRepository.save(producto);
        }
        
        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public Pedido crearSinAuth(CrearPedidoRequest request) {
        // Buscar o crear usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseGet(() -> {
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setEmail(request.getEmail());
                nuevoUsuario.setNombre(request.getNombre() != null ? request.getNombre() : "Cliente");
                nuevoUsuario.setPassword("$2a$10$dummy"); // Password temporal
                return usuarioRepository.save(nuevoUsuario);
            });
        
        return crear(usuario, request);
    }
    
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByCreatedAtDesc(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPorUsuarioId(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return obtenerPorUsuario(usuario);
    }
    
    @Transactional(readOnly = true)
    public Pedido obtenerPorId(UUID id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
    
    @Transactional
    public Pedido actualizarEstado(UUID id, String estado) {
        Pedido pedido = obtenerPorId(id);
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public Pedido actualizar(UUID id, CrearPedidoRequest request) {
        Pedido pedido = obtenerPorId(id);
        
        // Actualizar campos básicos
        pedido.setTotal(request.getTotal());
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setMetodoPago(request.getMetodoPago());
        
        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public void eliminar(UUID id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }
    
    // Método para convertir entidad a DTO (evita problemas de serialización circular)
    public PedidoResponse convertToResponse(Pedido pedido) {
        return PedidoResponse.builder()
            .id(pedido.getId())
            .estado(pedido.getEstado())
            .total(pedido.getTotal())
            .usuarioId(pedido.getUsuario().getId())
            .usuarioNombre(pedido.getUsuario().getNombre())
            .usuarioEmail(pedido.getUsuario().getEmail())
            .direccionEnvio(pedido.getDireccionEnvio())
            .metodoPago(pedido.getMetodoPago())
            .createdAt(pedido.getCreatedAt())
            .updatedAt(pedido.getUpdatedAt())
            .items(pedido.getItems().stream()
                .map(item -> PedidoResponse.ItemPedidoResponse.builder()
                    .id(item.getId())
                    .productoId(item.getProducto().getId())
                    .productoNombre(item.getProducto().getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
