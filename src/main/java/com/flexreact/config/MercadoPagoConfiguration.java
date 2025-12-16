package com.flexreact.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Mercado Pago
 * Inicializa el SDK con el access token desde las variables de entorno
 */
@Slf4j
@Configuration
public class MercadoPagoConfiguration {
    
    @Value("${mercadopago.access.token}")
    private String accessToken;
    
    @Value("${mercadopago.public.key}")
    private String publicKey;
    
    @PostConstruct
    public void init() {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            log.info("Mercado Pago configurado exitosamente");
        } catch (Exception e) {
            log.error("Error al configurar Mercado Pago: {}", e.getMessage());
            throw new RuntimeException("No se pudo inicializar Mercado Pago", e);
        }
    }
    
    public String getPublicKey() {
        return publicKey;
    }
}
