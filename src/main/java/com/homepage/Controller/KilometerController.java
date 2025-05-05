package com.homepage.Controller;

import com.homepage.Service.KilometerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kilometer")
public class KilometerController {

    @Autowired
    private KilometerService kilometerService;

    @GetMapping
    public ResponseEntity<?> getKilometerData(
            @RequestParam String startdatum, 
            @RequestParam String enddatum, 
            @RequestParam List<String> linien) {
        
        try {
            Map<String, Object> data = kilometerService.getKilometerData(startdatum, enddatum, linien);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Ein Fehler ist aufgetreten: " + e.getMessage()));
        }
    }
}
