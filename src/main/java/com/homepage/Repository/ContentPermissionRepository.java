package com.homepage.Repository;

import com.homepage.Model.Content;
import com.homepage.Model.ContentPermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentPermissionRepository extends JpaRepository<ContentPermission, Long> {
    
    List<ContentPermission> findByContent(Content content);
    
    // Geänderte Abfrage: Berechtigungen basierend auf Rolle
    @Query("SELECT cp FROM ContentPermission cp WHERE cp.role = :role")
    List<ContentPermission> findByRole(@Param("role") String role);
    
    @Query("SELECT cp FROM ContentPermission cp WHERE cp.content.id = :contentId AND cp.role = :role")
    Optional<ContentPermission> findByContentAndRole(
            @Param("contentId") Long contentId, 
            @Param("role") String role);
    
    void deleteByContent(Content content);
}
