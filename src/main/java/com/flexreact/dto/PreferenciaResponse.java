package com.flexreact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta al crear una preferencia de pago
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaResponse {
    
    private String id;
    private String initPoint;
    private String sandboxInitPoint;
    private String publicKey;
}
