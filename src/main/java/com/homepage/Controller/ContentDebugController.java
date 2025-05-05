package com.homepage.Controller;

import com.homepage.Model.Content;
import com.homepage.Model.ContentPermission;
import com.homepage.Model.MyAppUser;
import com.homepage.Repository.ContentPermissionRepository;
import com.homepage.Repository.ContentRepository;
import com.homepage.Repository.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug/content")
public class ContentDebugController {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentPermissionRepository permissionRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllContentWithPermissions() {
        List<Content> allContent = contentRepository.findAll();
        
        List<Map<String, Object>> result = allContent.stream().map(content -> {
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("id", content.getId());
            contentMap.put("title", content.getTitle());
            
            List<Map<String, String>> permissions = content.getPermissions().stream().map(perm -> {
                Map<String, String> permMap = new HashMap<>();
                permMap.put("id", perm.getId().toString());
                permMap.put("role", perm.getRole());
                return permMap;
            }).collect(Collectors.toList());
            
            contentMap.put("permissions", permissions);
            return contentMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user-permissions")
    public ResponseEntity<?> getUserPermissions(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.badRequest().body("Nicht authentifiziert");
        }
        
        Optional<MyAppUser> userOpt = userRepository.findByUsername(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Benutzer nicht gefunden");
        }
        
        MyAppUser user = userOpt.get();
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("role", user.getUserRole());
        
        // Alle Berechtigungen finden, die für diesen Benutzer gelten
        List<ContentPermission> matchingPermissions = permissionRepository.findByRole(user.getUserRole());
        
        List<Map<String, Object>> permissionsList = matchingPermissions.stream().map(perm -> {
            Map<String, Object> permMap = new HashMap<>();
            permMap.put("id", perm.getId());
            permMap.put("contentId", perm.getContent().getId());
            permMap.put("contentTitle", perm.getContent().getTitle());
            permMap.put("role", perm.getRole());
            return permMap;
        }).collect(Collectors.toList());
        
        result.put("matchingPermissions", permissionsList);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/fix-permissions/{contentId}")
    public ResponseEntity<?> fixPermissions(@PathVariable Long contentId, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.badRequest().body("Nicht authentifiziert");
        }
        
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Content nicht gefunden");
        }
        
        Content content = contentOpt.get();
        
        // Berechtigung für ROLE_USER hinzufügen
        ContentPermission permission = new ContentPermission();
        permission.setContent(content);
        permission.setRole("ROLE_USER");
        permissionRepository.save(permission);
        
        return ResponseEntity.ok(Map.of(
            "message", "Berechtigung für ROLE_USER hinzugefügt",
            "contentId", contentId,
            "contentTitle", content.getTitle()
        ));
    }
    
    @GetMapping("/fix-all-permissions")
    public ResponseEntity<?> fixAllPermissions(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.badRequest().body("Nicht authentifiziert");
        }
        
        // Alle Content-Einträge laden
        List<Content> allContent = contentRepository.findAll();
        
        // Für jeden Content-Eintrag eine Berechtigung für ROLE_USER hinzufügen
        for (Content content : allContent) {
            // Prüfen, ob bereits eine USER-Berechtigung existiert
            boolean hasUserPermission = content.getPermissions().stream()
                .anyMatch(p -> "ROLE_USER".equals(p.getRole()));
                
            if (!hasUserPermission) {
                ContentPermission permission = new ContentPermission();
                permission.setContent(content);
                permission.setRole("ROLE_USER");
                permissionRepository.save(permission);
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Berechtigungen für alle Content-Einträge hinzugefügt",
            "contentCount", allContent.size()
        ));
    }
}
