package com.voo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Gestão de Planos de Voo")
                .description("API REST para criação, validação e gerenciamento de planos de voo. " +
                             "Data de referência para demonstração: 2026-11-15.")
                .version("1.0.0"));
    }
}
