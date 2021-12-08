package hu.rhalm.wasteless.gateway.config;

import lombok.AllArgsConstructor;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class GatewayApiDocsConfiguration implements SwaggerResourcesProvider {

    private final ZuulProperties zuulProperties;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        zuulProperties
                .getRoutes()
                .values()
                .forEach(route -> resources.add(swaggerResource(route)));

        return resources;
    }

    private SwaggerResource swaggerResource(ZuulProperties.ZuulRoute route) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(route.getId());
        swaggerResource.setLocation("/" + route.getId() + "/v2/api-docs");
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}
