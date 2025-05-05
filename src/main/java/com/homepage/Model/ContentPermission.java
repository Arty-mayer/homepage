package com.homepage.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Entity
@Table(name = "content_permission", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"content_id", "role"}))
@Data
public class ContentPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    @JsonBackReference // Diese Annotation verhindert zirkuläre Referenzen
    private Content content;
    
    // Rolle, für die dieser Inhalt freigegeben ist
    private String role;
    
    // Rolle muss gesetzt sein
    public boolean isValid() {
        return role != null;
    }
}
