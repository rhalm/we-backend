package hu.rhalm.wasteless.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiGatewayController {

    @GetMapping("/public/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
