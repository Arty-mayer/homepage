package com.homepage.Repository;

import com.homepage.Model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    
    Optional<Content> findByTitle(String title);
    
    // Geänderte Abfrage: Content basierend auf Rolle
    @Query("SELECT DISTINCT c FROM Content c JOIN c.permissions p WHERE p.role = :role")
    List<Content> findByRole(@Param("role") String role);
}
