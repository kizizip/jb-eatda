package com.jbeatda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("JBeatda API")
                        .description("JBeatda 서비스의 API 명세서입니다.")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Local Server"),
                        new Server().url("http://172.30.1.42:8080/api").description("투썸"),
                        new Server().url("http://192.168.35.99:8080/api").description("서현 Server"),
                        new Server().url("https://jbeatda-branch.up.railway.app/api").description("branch Server"),
                        new Server().url("https://jbeatda.up.railway.app/api").description("Github main Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                ;
    }

}
