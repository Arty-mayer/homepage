package com.homepage.service;

import java.util.List;
import java.util.Optional;

import com.homepage.Model.UserAccounts;
import com.homepage.Security.UserCustom;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homepage.rpository.UserAccountsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserAccountsRepository userAccountsRepository;
    private final PasswordEncoder passwordEncoder;

    private final String email = null;

    public UserService(UserAccountsRepository userAccountsRepository, PasswordEncoder passwordEncoder) {
        this.userAccountsRepository = userAccountsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Login-Versuch für Benutzer: {}", username);
        
        Optional<UserAccounts> user = userAccountsRepository.findByUsername(username);
        
        if (user.isPresent()) {
            UserAccounts  userObj = user.get();

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
            userAccountsRepository.save(userObj);

            logger.info("Benutzer '{}' hat die Rolle: {}", username, role);

            UserDetails userDetails = UserCustom.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority(role)))
                    .build();

            return userDetails;
        } else {
            logger.warn("Benutzer '{}' nicht gefunden.", username);
            throw new UsernameNotFoundException("Benutzer nicht gefunden: " + username);
        }
    }
    
    /**
     * Erstellt einen neuen Benutzer
     */
    @Transactional
    public UserAccounts createUser(UserAccounts user) {
        // Prüfen, ob der Benutzername bereits existiert
        if (userAccountsRepository.existsByUsername(user.getUsername())) {
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
        
        return userAccountsRepository.save(user);
    }
    
    /**
     * Aktualisiert einen vorhandenen Benutzer
     */
    @Transactional
    public Optional<UserAccounts> updateUser(Long userId, UserAccounts updatedUser) {
        Optional<UserAccounts> userOpt = userAccountsRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        UserAccounts user = userOpt.get();
        
        // Benutzername aktualisieren, wenn er sich geändert hat
        if (!user.getUsername().equals(updatedUser.getUsername())) {
            if (userAccountsRepository.existsByUsername(updatedUser.getUsername())) {
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
        
        return Optional.of(userAccountsRepository.save(user));
    }
    
    /**
     * Löscht einen Benutzer
     */
    @Transactional
    public void deleteUser(Long userId) {
        userAccountsRepository.deleteById(userId);
    }
    
    /**
     * Findet einen Benutzer anhand des Benutzernamens
     */
    public Optional<UserAccounts> findByUsername(String username) {
        return userAccountsRepository.findByUsername(username);
    }
    
    /**
     * Findet einen Benutzer anhand seiner ID
     */
    public Optional<UserAccounts> findById(Long id) {
        return userAccountsRepository.findById(id);
    }
    
    /**
     * Gibt alle Benutzer zurück
     */
    public List<UserAccounts> getAllUsers() {
        return userAccountsRepository.findAll();
    }
    
    /**
     * Gibt alle Benutzer mit einer bestimmten Rolle zurück
     */
    public List<UserAccounts> getUsersByRole(String role) {
        return userAccountsRepository.findByUserRole(role);
    }
}
