package com.homepage.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.homepage.model.UserAccounts;
import com.homepage.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 🔹 1. Benutzer registrieren (nur Admins dürfen neue Benutzer anlegen)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserAccounts user) {
        try {
            UserAccounts createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Benutzer erfolgreich registriert",
                "id", createdUser.getId(),
                "username", createdUser.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 2. Benutzerliste für Admins abrufen
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<Map<String, String>> users = userService.getAllUsers().stream()
            .map(user -> Map.of(
                "id", String.valueOf(user.getId()),
                "username", user.getUsername(),
                "email", user.getEmail() != null ? user.getEmail() : "",
                "role", user.getUserRole()
            )).collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // 🔹 3. Aktuellen Benutzer abrufen
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUserInfo(Authentication authentication) {
        Optional<UserAccounts> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Benutzer nicht gefunden"));
        }
        
        UserAccounts user = userOpt.get();
        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "role", user.getUserRole(),
            "email", user.getEmail() != null ? user.getEmail() : ""
        ));
    }

    // 🔹 4. Benutzer aktualisieren (nur für Admins)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserAccounts updatedUser) {
        try {
            Optional<UserAccounts> result = userService.updateUser(id, updatedUser);
            
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Benutzer nicht gefunden"));
            }
            
            return ResponseEntity.ok(Map.of("message", "Benutzer erfolgreich aktualisiert"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 🔹 5. Benutzer löschen (nur Admins)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Prüfen, ob der Benutzer existiert
            Optional<UserAccounts> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Benutzer nicht gefunden"));
            }
            
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Benutzer erfolgreich gelöscht"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Fehler beim Löschen des Benutzers: " + e.getMessage()));
        }
    }
    
    // 🔹 6. Benutzer nach Rolle filtern
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        // Standardisiere das Rollenformat
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        } else {
            role = role.toUpperCase();
        }
        
        List<Map<String, String>> users = userService.getUsersByRole(role).stream()
            .map(user -> Map.of(
                "id", String.valueOf(user.getId()),
                "username", user.getUsername(),
                "email", user.getEmail() != null ? user.getEmail() : ""
            )).collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
}
