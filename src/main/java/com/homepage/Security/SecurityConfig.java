package com.homepage.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.homepage.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity // Aktiviert @PreAuthorize
public class SecurityConfig {
    
    private final UserService appUserService;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .formLogin(httpForm -> httpForm
                .loginPage("/login")
                .defaultSuccessUrl("/index", true)
                .permitAll())
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll())
            .authorizeHttpRequests(registry -> {
                // Öffentliche Ressourcen
                registry.requestMatchers("/login").permitAll();
                registry.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll();
                
                // Debug-Endpunkte
                registry.requestMatchers("/debug/auth").authenticated();
                registry.requestMatchers("/debug/info").hasAnyAuthority("ROLE_ADMIN");
                
                // API-Endpunkte
                registry.requestMatchers("/api/users/management", "/api/users/**").hasAnyAuthority("ROLE_ADMIN");
                registry.requestMatchers("/api/content/register").authenticated(); // Erlaubt allen authentifizierten Benutzern
                registry.requestMatchers("/api/content/**").authenticated();
                registry.requestMatchers("/api/kilometer/**").authenticated();
                
                // View-Endpunkte
                registry.requestMatchers("/users/management").hasAnyAuthority("ROLE_ADMIN");
                registry.requestMatchers("/content/management").hasAnyAuthority("ROLE_ADMIN");
                registry.requestMatchers("/signup").hasAnyAuthority("ROLE_ADMIN");
                
                // Alle anderen Anfragen erfordern Authentifizierung
                registry.anyRequest().authenticated();
            })
            .build();
    }
}
