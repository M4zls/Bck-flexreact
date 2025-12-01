package com.flexreact.controller;

import com.flexreact.dto.CrearPedidoRequest;
import com.flexreact.entity.Pedido;
import com.flexreact.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody CrearPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.crearSinAuth(request);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(@RequestParam(required = false) String email) {
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Pedido>> obtenerPorUsuario(@PathVariable UUID userId) {
        try {
            List<Pedido> pedidos = pedidoService.obtenerPorUsuarioId(userId);
            return ResponseEntity.ok(pedidos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable UUID id) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable UUID id, 
                                                    @RequestBody Map<String, String> body) {
        try {
            String estado = body.get("estado");
            Pedido pedido = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable UUID id, @RequestBody CrearPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.actualizar(id, request);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        try {
            pedidoService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
