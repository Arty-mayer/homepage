package com.homepage.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "menus")
@Data
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private String htmlClass;
    @Column(name = "access_roles")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] accessRoles;
    @Column(name = "items", columnDefinition = "TEXT")
    private String items;

}
