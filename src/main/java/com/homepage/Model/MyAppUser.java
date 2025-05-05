package com.homepage.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "my_app_user")
@Data
public class MyAppUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Benutzername darf nicht leer sein")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotBlank(message = "Passwort darf nicht leer sein")
    @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein")
    @Column(nullable = false)
    private String password;
    
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;
    
    @NotBlank(message = "Rolle darf nicht leer sein")
    @Column(nullable = false)
    private String userRole;
    
    // Hilfsmethode zur Standardisierung der Rolle
    public void standardizeRole() {
        if (userRole != null && !userRole.isEmpty()) {
            if (!userRole.startsWith("ROLE_")) {
                userRole = "ROLE_" + userRole.toUpperCase();
            } else {
                userRole = userRole.toUpperCase();
            }
        }
    }
}
