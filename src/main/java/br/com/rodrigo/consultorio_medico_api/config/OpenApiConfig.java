package br.com.rodrigo.consultorio_medico_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BASIC = "basicAuth";

    @Bean
    public OpenAPI consultorioOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Consultório Médico API")
                        .description("API de gestão de pacientes, médicos, recepcionistas, consultas e histórico médico.")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BASIC, new SecurityScheme()
                                .name(ESQUEMA_BASIC)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BASIC));
    }
}
