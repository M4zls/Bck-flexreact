package com.flexreact.controller;

import com.flexreact.dto.PagoRequest;
import com.flexreact.dto.PreferenciaResponse;
import com.flexreact.service.MercadoPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para operaciones de pago con Mercado Pago
 */
@Slf4j
@Tag(name = "Pagos", description = "API de integración con Mercado Pago")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagoController {
    
    private final MercadoPagoService mercadoPagoService;
    
    /**
     * POST /api/pagos/crear-preferencia - Crear una preferencia de pago
     */
    @Operation(summary = "Crear preferencia de pago",
               description = "Crea una preferencia de pago en Mercado Pago y devuelve el link de pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferencia creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error al crear la preferencia")
    })
    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody PagoRequest request) {
        try {
            log.info("Creando preferencia de pago para pedido: {}", request.getPedidoId());
            
            PreferenciaResponse preferencia = mercadoPagoService.crearPreferencia(request);
            
            return ResponseEntity.ok(preferencia);
        } catch (RuntimeException e) {
            log.error("Error al crear preferencia: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error interno al crear preferencia: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/pagos/webhook - Recibir notificaciones de Mercado Pago
     * Este endpoint será llamado por Mercado Pago cuando haya cambios en los pagos
     */
    @Operation(summary = "Webhook de Mercado Pago",
               description = "Recibe notificaciones de Mercado Pago sobre cambios en el estado de los pagos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación procesada"),
        @ApiResponse(responseCode = "400", description = "Notificación inválida")
    })
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload,
                                     @RequestParam(required = false) String type,
                                     @RequestParam(required = false) Long id) {
        try {
            log.info("Webhook recibido - Type: {}, ID: {}", type, id);
            log.debug("Payload completo: {}", payload);
            
            // Aquí implementarías la lógica para procesar el webhook
            // Por ejemplo, actualizar el estado del pedido en tu base de datos
            // según el estado del pago recibido
            
            if ("payment".equals(type)) {
                // Procesar notificación de pago
                log.info("Procesando notificación de pago ID: {}", id);
                // TODO: Implementar lógica de actualización de pedido
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al procesar webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * GET /api/pagos/estado/{paymentId} - Obtener estado de un pago
     */
    @Operation(summary = "Obtener estado de pago",
               description = "Consulta el estado actual de un pago en Mercado Pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/estado/{paymentId}")
    public ResponseEntity<?> obtenerEstadoPago(@PathVariable Long paymentId) {
        try {
            String estado = mercadoPagoService.obtenerEstadoPago(paymentId);
            
            Map<String, String> response = new HashMap<>();
            response.put("paymentId", paymentId.toString());
            response.put("estado", estado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener estado del pago: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
