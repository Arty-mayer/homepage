package com.homepage.Service;

import java.util.List;
import java.util.Optional;

import com.homepage.Model.MyAppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homepage.Repository.MyAppUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyAppUserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(MyAppUserService.class);
    
    @Autowired
    private MyAppUserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Login-Versuch für Benutzer: {}", username);
        
        Optional<MyAppUser> user = repository.findByUsername(username);
        
        if (user.isPresent()) {
            var userObj = user.get();

            // Sicherstellen, dass eine Rolle existiert
            if (userObj.getUserRole() == null || userObj.getUserRole().isEmpty()) {
                logger.error("Fehler: Benutzer '{}' hat keine definierte Rolle!", username);
                throw new UsernameNotFoundException("Benutzer hat keine definierte Rolle.");
            }

            // Die Rolle in das benötigte Format umwandeln (ROLE_ADMIN, ROLE_USER)
            String role = userObj.getUserRole();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            } else {
                role = role.toUpperCase();
            }
            
            // Wichtig: Aktualisiere die Rolle im Benutzer-Objekt, damit sie im gesamten System konsistent ist
            userObj.setUserRole(role);
            repository.save(userObj);

            logger.info("Benutzer '{}' hat die Rolle: {}", username, role);

            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority(role)))
                    .build();    
        } else {
            logger.warn("Benutzer '{}' nicht gefunden.", username);
            throw new UsernameNotFoundException("Benutzer nicht gefunden: " + username);
        }
    }
    
    /**
     * Erstellt einen neuen Benutzer
     */
    @Transactional
    public MyAppUser createUser(MyAppUser user) {
        // Prüfen, ob der Benutzername bereits existiert
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Benutzername existiert bereits");
        }
        
        // Passwort validieren
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("Passwort muss mindestens 6 Zeichen lang sein");
        }
        
        // Rolle standardisieren
        user.standardizeRole();
        
        // Passwort verschlüsseln
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return repository.save(user);
    }
    
    /**
     * Aktualisiert einen vorhandenen Benutzer
     */
    @Transactional
    public Optional<MyAppUser> updateUser(Long userId, MyAppUser updatedUser) {
        Optional<MyAppUser> userOpt = repository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        MyAppUser user = userOpt.get();
        
        // Benutzername aktualisieren, wenn er sich geändert hat
        if (!user.getUsername().equals(updatedUser.getUsername())) {
            if (repository.existsByUsername(updatedUser.getUsername())) {
                throw new RuntimeException("Benutzername existiert bereits");
            }
            user.setUsername(updatedUser.getUsername());
        }
        
        // Andere Felder aktualisieren
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        
        if (updatedUser.getUserRole() != null) {
            user.setUserRole(updatedUser.getUserRole());
            user.standardizeRole();
        }
        
        // Passwort aktualisieren, wenn es nicht leer ist
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            if (updatedUser.getPassword().length() < 6) {
                throw new RuntimeException("Passwort muss mindestens 6 Zeichen lang sein");
            }
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        return Optional.of(repository.save(user));
    }
    
    /**
     * Löscht einen Benutzer
     */
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }
    
    /**
     * Findet einen Benutzer anhand des Benutzernamens
     */
    public Optional<MyAppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
    /**
     * Findet einen Benutzer anhand seiner ID
     */
    public Optional<MyAppUser> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * Gibt alle Benutzer zurück
     */
    public List<MyAppUser> getAllUsers() {
        return repository.findAll();
    }
    
    /**
     * Gibt alle Benutzer mit einer bestimmten Rolle zurück
     */
    public List<MyAppUser> getUsersByRole(String role) {
        return repository.findByUserRole(role);
    }
}
