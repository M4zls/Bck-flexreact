package com.flexreact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una preferencia de pago en Mercado Pago
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPreferenciaRequest {
    
    private List<Item> items;
    private Payer payer;
    private String backUrls;
    private Boolean autoReturn;
    private String notificationUrl;
    private String externalReference;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String title;
        private String description;
        private String pictureUrl;
        private String categoryId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payer {
        private String name;
        private String surname;
        private String email;
        private Phone phone;
        private Address address;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Phone {
        private String areaCode;
        private String number;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String streetName;
        private String streetNumber;
        private String zipCode;
    }
}
