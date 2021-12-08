package hu.rhalm.wasteless.gateway.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity()
public class GatewayWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/oauth2/**", "/**/swagger**", "/**/swagger-resources/**", "/**/swagger-ui/**", "/**/webjars/springfox-swagger-ui/**", "/**/v2/api-docs", "/**/public/**", "/**/h2-console/**", "/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .oauth2ResourceServer()
                .jwt();
    }
}