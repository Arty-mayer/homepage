package com.homepage.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Table(name = "content")
@Data
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Titel darf nicht leer sein")
    @Column(nullable = false, unique = true)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String htmlPath;
    private String link;
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Diese Annotation verhindert zirkuläre Referenzen
    @JsonIgnoreProperties("content") // Ignoriert das content-Feld in den Permissions
    private List<ContentPermission> permissions = new ArrayList<>();
    
    // Hilfsmethode zum Hinzufügen einer Berechtigung
    public void addPermission(String role) {
        ContentPermission permission = new ContentPermission();
        permission.setContent(this);
        permission.setRole(role);
        this.permissions.add(permission);
    }
    
    // Hilfsmethode zum Entfernen einer Berechtigung
    public void removePermission(ContentPermission permission) {
        permissions.remove(permission);
        permission.setContent(null);
    }
}
