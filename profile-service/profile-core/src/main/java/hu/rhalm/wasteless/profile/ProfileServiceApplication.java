package hu.rhalm.wasteless.profile;

import hu.rhalm.wasteless.common.configuration.apidocs.ApiDocsConfiguration;
import hu.rhalm.wasteless.common.configuration.security.ResourceServerWebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan({"hu.rhalm.wasteless.profile", "hu.rhalm.wasteless.common"})
@Import({ResourceServerWebSecurityConfig.class, ApiDocsConfiguration.class})
@Slf4j
public class ProfileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileServiceApplication.class, args);
        log.info("Profile Service has successfully started!");
    }
}
