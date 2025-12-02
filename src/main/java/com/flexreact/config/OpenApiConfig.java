package com.flexreact.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("FlexReact API")
                        .version("1.0.0")
                        .description("API REST para FlexReact E-commerce\n\n" +
                                "Esta API proporciona endpoints para gestionar usuarios, productos y pedidos.\n\n" +
                                "**Autenticación:**\n" +
                                "- La mayoría de los endpoints requieren autenticación JWT.\n" +
                                "- Primero debes registrarte o iniciar sesión en `/api/auth/register` o `/api/auth/login`.\n" +
                                "- Luego usa el token JWT en el header `Authorization: Bearer <token>`.")
                        .contact(new Contact()
                                .name("FlexReact Team")
                                .email("contact@flexreact.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://bck-flexreact-production.up.railway.app")
                                .description("Servidor de producción (Railway)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido del login")));
    }
}
