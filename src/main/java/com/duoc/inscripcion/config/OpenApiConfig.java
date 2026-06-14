package com.duoc.inscripcion.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de Inscripción - CDY2204",
        version = "1.0.0",
        description = "API REST para gestionar inscripciones a cursos virtuales. "
            + "Integra autenticación mediante Auth0 (IdaaS) y almacenamiento en AWS S3.",
        contact = @Contact(name = "Equipo CDY2204", email = "team@duoc.cl")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor local"),
        @Server(url = "https://api.sistema-inscripcion.com", description = "Producción (API Gateway)")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Ingrese el token JWT obtenido desde Auth0. Formato: Bearer {token}"
)
public class OpenApiConfig {
}
