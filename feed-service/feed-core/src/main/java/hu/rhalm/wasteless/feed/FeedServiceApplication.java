package hu.rhalm.wasteless.feed;

import hu.rhalm.wasteless.common.configuration.apidocs.ApiDocsConfiguration;
import hu.rhalm.wasteless.common.configuration.security.ResourceServerWebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ResourceServerWebSecurityConfig.class, ApiDocsConfiguration.class})
@ComponentScan({"hu.rhalm.wasteless.feed", "hu.rhalm.wasteless.common"})
@SpringBootApplication
@Slf4j
public class FeedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedServiceApplication.class, args);
        log.info("Feed Service has successfully started!");
    }
}
