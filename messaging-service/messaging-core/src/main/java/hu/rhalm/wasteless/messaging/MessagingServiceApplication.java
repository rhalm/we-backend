package hu.rhalm.wasteless.messaging;

import hu.rhalm.wasteless.common.configuration.apidocs.ApiDocsConfiguration;
import hu.rhalm.wasteless.common.configuration.security.ResourceServerWebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ResourceServerWebSecurityConfig.class, ApiDocsConfiguration.class})
@Slf4j
public class MessagingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagingServiceApplication.class, args);
        log.info("Messaging Service has successfully started!");
    }
}
