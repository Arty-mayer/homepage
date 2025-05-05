package com.homepage.service;

import com.homepage.Model.Content;
import com.homepage.Model.ContentPermission;
import com.homepage.Model.UserAccounts;
import com.homepage.rpository.ContentPermissionRepository;
import com.homepage.rpository.ContentRepository;
import com.homepage.rpository.UserAccountsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentPermissionRepository contentPermissionRepository;

    @Autowired
    private UserAccountsRepository userRepository;

    // Gibt alle Content-Einträge zurück (für Admins)
    public List<Content> getAllContent() {
        List<Content> allContent = contentRepository.findAll();
        // Erzwinge das Laden der Berechtigungen
        allContent.forEach(content -> {
            content.getPermissions().size();
        });
        return allContent;
    }

    @Transactional
    public List<Content> getUserContent(String username) {
        System.out.println("Suche Content für Benutzer: " + username);
        Optional<UserAccounts> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.err.println("Benutzer nicht gefunden: " + username);
            throw new RuntimeException("Benutzer nicht gefunden");
        }
        
        UserAccounts user = userOpt.get();
        System.out.println("Benutzer gefunden: " + user.getUsername() + ", Rolle: " + user.getUserRole());
        
        // Alle Content-Einträge mit explizitem Laden der Berechtigungen
        List<Content> allContent = getAllContentWithPermissions();
        System.out.println("Alle Content-Einträge: " + allContent.size());
        
        // Filtern nach Berechtigungen
        List<Content> userContent = new ArrayList<>();
        
        for (Content content : allContent) {
            // Wenn keine Berechtigungen definiert sind, ist der Content für niemanden sichtbar
            if (content.getPermissions().isEmpty()) {
                continue;
            }
            
            boolean hasAccess = false;
            
            for (ContentPermission permission : content.getPermissions()) {
                // Fall: Berechtigung für bestimmte Rolle
                if (permission.getRole() != null) {
                    String permRole = standardizeRole(permission.getRole());
                    if (permRole.equals(user.getUserRole())) {
                        hasAccess = true;
                        break;
                    }
                }
            }
            
            if (hasAccess) {
                userContent.add(content);
            }
        }
        
        return userContent;
    }

    /**
     * Lädt alle Content-Einträge mit ihren Berechtigungen (eager loading)
     */
    public List<Content> getAllContentWithPermissions() {
        List<Content> allContent = contentRepository.findAll();
        // Berechtigungen explizit laden, um Lazy Loading-Probleme zu vermeiden
        allContent.forEach(content -> {
            content.getPermissions().size(); // Erzwingt das Laden der Berechtigungen
        });
        return allContent;
    }

    /**
     * Standardisiert das Rollenformat (ROLE_XXX)
     */
    private String standardizeRole(String role) {
        if (role == null) return null;
        if (!role.startsWith("ROLE_")) {
            return "ROLE_" + role.toUpperCase();
        }
        return role.toUpperCase();
    }

    /**
     * Erstellt einen neuen Inhalt
     */
    @Transactional
    public Content createContent(Content content) {
        // Prüfen, ob ein Inhalt mit diesem Titel bereits existiert
        Optional<Content> existingContent = contentRepository.findByTitle(content.getTitle());
        if (existingContent.isPresent()) {
            throw new RuntimeException("Ein Inhalt mit diesem Titel existiert bereits");
        }
        
        return contentRepository.save(content);
    }

    /**
     * Aktualisiert einen vorhandenen Inhalt
     */
    @Transactional
    public Optional<Content> updateContent(Long contentId, Content updatedContent) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }

        Content content = contentOpt.get();
        
        // Prüfen, ob der neue Titel bereits von einem anderen Inhalt verwendet wird
        if (!content.getTitle().equals(updatedContent.getTitle())) {
            Optional<Content> existingContent = contentRepository.findByTitle(updatedContent.getTitle());
            if (existingContent.isPresent() && !existingContent.get().getId().equals(contentId)) {
                throw new RuntimeException("Ein anderer Inhalt mit diesem Titel existiert bereits");
            }
        }
        
        content.setTitle(updatedContent.getTitle());
        content.setDescription(updatedContent.getDescription());
        content.setHtmlPath(updatedContent.getHtmlPath());
        content.setLink(updatedContent.getLink());
        
        return Optional.of(contentRepository.save(content));
    }

    /**
     * Löscht einen Inhalt und alle zugehörigen Berechtigungen
     */
    @Transactional
    public void deleteContent(Long contentId) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isPresent()) {
            Content content = contentOpt.get();
            // Zuerst alle Berechtigungen löschen
            contentPermissionRepository.deleteByContent(content);
            // Dann den Inhalt löschen
            contentRepository.delete(content);
        }
    }

    /**
     * Findet einen Inhalt anhand des Titels
     */
    public Optional<Content> findContentByTitle(String title) {
        return contentRepository.findByTitle(title);
    }
    
    /**
     * Gibt alle Berechtigungen für einen Inhalt zurück
     */
    public List<ContentPermission> getContentPermissions(Long contentId) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            return List.of();
        }
        
        return contentPermissionRepository.findByContent(contentOpt.get());
    }
    
    /**
     * Fügt eine neue Berechtigung für einen Inhalt hinzu
     */
    @Transactional
    public ContentPermission addContentPermission(Long contentId, String role) {
        Optional<Content> contentOpt = contentRepository.findById(contentId);
        if (contentOpt.isEmpty()) {
            throw new RuntimeException("Inhalt nicht gefunden");
        }
        
        // Prüfen, ob die Berechtigung bereits existiert
        Optional<ContentPermission> existingPermission = contentPermissionRepository
                .findByContentAndRole(contentId, role);
        
        if (existingPermission.isPresent()) {
            throw new RuntimeException("Diese Berechtigung existiert bereits");
        }
        
        ContentPermission permission = new ContentPermission();
        permission.setContent(contentOpt.get());
        permission.setRole(role);
        
        // Überprüfung der Berechtigungslogik:
        // role = NULL: Niemand hat Zugriff (keine Berechtigung)
        if (role == null) {
            throw new RuntimeException("Rolle muss angegeben werden");
        }
        
        return contentPermissionRepository.save(permission);
    }
    
    /**
     * Löscht eine Berechtigung
     */
    @Transactional
    public void deleteContentPermission(Long permissionId) {
        contentPermissionRepository.deleteById(permissionId);
    }
}
