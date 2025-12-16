package com.flexreact.service;

import com.flexreact.config.MercadoPagoConfiguration;
import com.flexreact.dto.PagoRequest;
import com.flexreact.dto.PreferenciaResponse;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar operaciones con Mercado Pago
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoService {
    
    private final MercadoPagoConfiguration mercadoPagoConfig;
    
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;
    
    /**
     * Crea una preferencia de pago en Mercado Pago
     */
    public PreferenciaResponse crearPreferencia(PagoRequest request) {
        try {
            // Crear items
            List<PreferenceItemRequest> items = new ArrayList<>();
            
            for (PagoRequest.ProductoPago producto : request.getProductos()) {
                PreferenceItemRequest item = PreferenceItemRequest.builder()
                        .id(producto.getProductoId())
                        .title(producto.getNombre())
                        .description(producto.getDescripcion())
                        .pictureUrl(producto.getImagenUrl())
                        .categoryId("electronics") // Ajusta según tu categoría
                        .quantity(producto.getCantidad())
                        .currencyId("ARS") // Peso argentino
                        .unitPrice(producto.getPrecio())
                        .build();
                
                items.add(item);
            }
            
            // Crear información del comprador
            PreferencePayerRequest payer = null;
            if (request.getComprador() != null) {
                PagoRequest.DatosComprador comprador = request.getComprador();
                
                payer = PreferencePayerRequest.builder()
                        .name(comprador.getNombre())
                        .surname(comprador.getApellido())
                        .email(comprador.getEmail())
                        .phone(PhoneRequest.builder()
                                .areaCode("11") // Código de área por defecto
                                .number(comprador.getTelefono())
                                .build())
                        .build();
            }
            
            // URLs de retorno (ajusta según tu frontend)
            String frontendUrl = allowedOrigins.split(",")[0].trim(); // Toma la primera URL y limpia espacios
            
            // Si frontendUrl es localhost, usar la segunda URL (producción)
            if (frontendUrl.contains("localhost")) {
                String[] origins = allowedOrigins.split(",");
                if (origins.length > 1) {
                    frontendUrl = origins[1].trim();
                }
            }
            
            log.info("Usando URL de retorno: {}", frontendUrl);
            
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(frontendUrl + "/pago/success")
                    .failure(frontendUrl + "/pago/failure")
                    .pending(frontendUrl + "/pago/pending")
                    .build();
            
            // Crear la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved") // Retornar automáticamente si es aprobado
                    .externalReference(request.getPedidoId()) // ID del pedido para referencia
                    .statementDescriptor("FLEXREACT") // Nombre que aparece en el resumen
                    .notificationUrl(null) // Webhook URL (configura esto cuando tengas tu URL pública)
                    .build();
            
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            log.info("Preferencia de pago creada: {}", preference.getId());
            
            return PreferenciaResponse.builder()
                    .id(preference.getId())
                    .initPoint(preference.getInitPoint())
                    .sandboxInitPoint(preference.getSandboxInitPoint())
                    .publicKey(mercadoPagoConfig.getPublicKey())
                    .build();
                    
        } catch (MPApiException e) {
            log.error("Error de API de Mercado Pago: {} - {}", e.getStatusCode(), e.getApiResponse().getContent());
            throw new RuntimeException("Error al crear la preferencia de pago: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            log.error("Error de Mercado Pago: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con Mercado Pago: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un pago por su ID
     */
    public String obtenerEstadoPago(Long paymentId) {
        try {
            // Aquí podrías implementar la lógica para obtener el estado del pago
            // usando com.mercadopago.client.payment.PaymentClient
            return "Implementar según necesidad";
        } catch (Exception e) {
            log.error("Error al obtener estado del pago: {}", e.getMessage());
            throw new RuntimeException("Error al obtener el estado del pago");
        }
    }
}
