package hu.rhalm.wasteless.common;

import hu.rhalm.wasteless.common.filestore.FileStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;


@Configuration
@PropertySource("classpath:config.properties")
public class Config {

    @Value("${bucket}")
    private String bucket;

    @Bean
    public FileStore fileStore() throws IOException {
        return new FileStore(bucket);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}