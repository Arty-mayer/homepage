package com.homepage.Service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilometerService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Gibt Kilometerdaten für die angegebenen Linien im angegebenen Zeitraum zurück
     */
    public Map<String, Object> getKilometerData(String startdatum, String enddatum, List<String> linien) {
        // Validiere die Datumsformate
        validateDateFormat(startdatum);
        validateDateFormat(enddatum);
        
        // Validiere die Linien
        if (linien == null || linien.isEmpty()) {
            throw new IllegalArgumentException("Es muss mindestens eine Linie angegeben werden");
        }
        
        // Hier könnte eine Abfrage an die Datenbank erfolgen
        // Dies ist nur ein Beispiel für die Struktur der Rückgabedaten
        Map<String, Object> result = new HashMap<>();
        
        // Ergebnisse für jede Linie
        List<Map<String, Object>> ergebnisse = linien.stream()
                .map(linie -> {
                    // Hier würden echte Daten aus der Datenbank abgefragt werden
                    int anzahlFahrten = (int) (Math.random() * 200 + 100); // Zufallswert zwischen 100 und 300
                    int kilometer = anzahlFahrten * 300; // Einfache Berechnung für Beispieldaten
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("linie", linie);
                    map.put("anzahlFahrten", anzahlFahrten);
                    map.put("kilometer", kilometer);
                    return map;
                })
                .toList();
        
        result.put("ergebnisse", ergebnisse);
        
        Map<String, String> zeitraum = new HashMap<>();
        zeitraum.put("start", startdatum);
        zeitraum.put("ende", enddatum);
        result.put("zeitraum", zeitraum);
        
        result.put("ausgewählteLinien", linien);
        
        // Gesamtwerte berechnen
        int gesamtFahrten = ergebnisse.stream()
                .mapToInt(e -> (Integer) e.get("anzahlFahrten"))
                .sum();
        
        int gesamtKilometer = ergebnisse.stream()
                .mapToInt(e -> (Integer) e.get("kilometer"))
                .sum();
        
        Map<String, Integer> gesamt = new HashMap<>();
        gesamt.put("anzahlFahrten", gesamtFahrten);
        gesamt.put("kilometer", gesamtKilometer);
        result.put("gesamt", gesamt);
        
        return result;
    }
    
    /**
     * Validiert das Datumsformat
     */
    private void validateDateFormat(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ungültiges Datumsformat. Erwartet: yyyy-MM-dd");
        }
    }
}
