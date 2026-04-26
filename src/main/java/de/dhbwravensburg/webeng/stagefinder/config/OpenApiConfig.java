package de.dhbwravensburg.webeng.stagefinder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StageFinder API")
                        .description("""
                                REST API for StageFinder — discover artists, browse setlists from setlist.fm, \
                                and manage your personal favourite artists.

                                **Authentication:** Session-based. Call `POST /api/auth/login` first; \
                                the server sets a `JSESSIONID` cookie that is sent automatically on \
                                subsequent requests.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("StageFinder").url("https://github.com/Just-Martin-Really/stagefinder"))
                        .license(new License().name("MIT")));
    }
}
