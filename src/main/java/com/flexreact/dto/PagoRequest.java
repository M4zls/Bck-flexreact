package com.flexreact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO simplificado para crear un pago desde el carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {
    
    private List<ProductoPago> productos;
    private DatosComprador comprador;
    private String pedidoId;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoPago {
        private String productoId;
        private String nombre;
        private String descripcion;
        private String imagenUrl;
        private Integer cantidad;
        private BigDecimal precio;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatosComprador {
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String direccion;
        private String ciudad;
        private String codigoPostal;
    }
}
