package com.acoustic.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Pension Zus Microservice")
                .version("1.0")
                .description("The microservice calculates the pension zus from the gross monthly salary")
                .contact(new Contact().name("Giuseppe Tumminello"))
                .license(new License().name("Apache 2.0")));
    }

}

