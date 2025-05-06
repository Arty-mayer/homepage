package com.homepage.Controller;

import com.homepage.model.Content;
import com.homepage.model.ContentPermission;
import com.homepage.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    // Gibt alle Content-Einträge zurück (nur für Admins)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Content>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    // Gibt Content-Einträge zurück, auf die der aktuelle Benutzer Zugriff hat
    @GetMapping("/user")
    public ResponseEntity<List<Content>> getUserContent(Authentication authentication) {
        return ResponseEntity.ok(contentService.getUserContent(authentication.getName()));
    }

    // Erstellt einen neuen Content-Eintrag (nur für Admins)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createContent(@RequestBody Content content) {
        try {
            Content createdContent = contentService.createContent(content);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Registriert einen Content-Eintrag (für alle authentifizierten Benutzer)
    @PostMapping("/register")
    public ResponseEntity<?> registerContent(@RequestBody Content content) {
        try {
            // Prüfen, ob der Content bereits existiert
            Optional<Content> existingContent = contentService.findContentByTitle(content.getTitle());
            if (existingContent.isPresent()) {
                return ResponseEntity.ok(existingContent.get());
            }
            
            // Neuen Content erstellen
            Content createdContent = contentService.createContent(content);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Aktualisiert einen Content-Eintrag (nur für Admins)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody Content content) {
        try {
            Optional<Content> updatedContent = contentService.updateContent(id, content);
            if (updatedContent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedContent.get());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Löscht einen Content-Eintrag (nur für Admins)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        try {
            contentService.deleteContent(id);
            return ResponseEntity.ok(Map.of("message", "Content erfolgreich gelöscht"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Gibt alle Berechtigungen für einen Content-Eintrag zurück (nur für Admins)
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContentPermission>> getContentPermissions(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentPermissions(id));
    }

    // Fügt eine neue Berechtigung für einen Content-Eintrag hinzu (nur für Admins)
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addContentPermission(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String role = request.get("role");
            // Standardisiere das Rollenformat
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            } else {
                role = role.toUpperCase();
            }
            
            ContentPermission permission = contentService.addContentPermission(id, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Löscht eine Berechtigung (nur für Admins)
    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteContentPermission(@PathVariable Long id) {
        try {
            contentService.deleteContentPermission(id);
            return ResponseEntity.ok(Map.of("message", "Berechtigung erfolgreich gelöscht"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
