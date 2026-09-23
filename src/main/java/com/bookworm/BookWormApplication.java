package com.bookworm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Book Worm — E-Bookstore Platform entry point.
 *
 * <p>Swagger UI: <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a>
 * <p>OpenAPI JSON: <a href="http://localhost:8080/api-docs">http://localhost:8080/api-docs</a>
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BookWormApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookWormApplication.class, args);
    }
}
