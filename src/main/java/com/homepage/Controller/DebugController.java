package com.homepage.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/auth")
    public ResponseEntity<?> checkAuthentication(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Nicht eingeloggt"));
        }

        return ResponseEntity.ok(Map.of(
            "username", authentication.getName(),
            "roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()),
            "authenticated", authentication.isAuthenticated()
        ));
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getSystemInfo() {
        return ResponseEntity.ok(Map.of(
            "java.version", System.getProperty("java.version"),
            "os.name", System.getProperty("os.name"),
            "user.timezone", System.getProperty("user.timezone"),
            "app.environment", System.getProperty("app.environment", "development")
        ));
    }
}
