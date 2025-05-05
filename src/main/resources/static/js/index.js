document.addEventListener("DOMContentLoaded", function () {
    const userIcon = document.getElementById("user-icon");
    const dropdownMenu = document.getElementById("dropdown-menu");
  
    userIcon.addEventListener("click", function () {
        dropdownMenu.style.display = dropdownMenu.style.display === "block" ? "none" : "block";
    });
  
    // Klick außerhalb schließt das Menü
    document.addEventListener("click", function (event) {
        if (!userIcon.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.style.display = "none";
        }
    });
  
    // 🔹 Überprüfung, ob der Benutzer eingeloggt ist
    fetch("/debug/auth")
        .then(response => {
            if (!response.ok) {
                throw new Error("Nicht eingeloggt");
            }
            return response.json();
        })
        .then(data => {
            document.getElementById("user-icon").src = data.profilePicture || "/images/default_profile.jpg"; // Falls Profilbild verfügbar ist
            if (data.roles.includes("ROLE_ADMIN")) {
                document.getElementById("admin-menu").style.display = "block"; // Falls Admin, zeige Benutzerverwaltung
                document.getElementById("content-management").style.display = "block"; // Falls Admin, zeige Content-Verwaltung
            }
        })
        .catch(() => {
            // Falls nicht eingeloggt, leiten wir zur Login-Seite um
            window.location.href = "/login";
        });
  
    // Funktion zum Anwenden der Content-Berechtigungen
    function applyContentPermissions() {
        fetch("/api/content/user")
            .then(response => response.json())
            .then(content => {
                // Für jeden Content-Block prüfen, ob der Benutzer Zugriff hat
                document.querySelectorAll("[data-content]").forEach(element => {
                    const contentTitle = element.getAttribute("data-content");
                    const allowed = content.some(item => item.title === contentTitle);
                    element.style.display = allowed ? "block" : "none";
                });
            })
            .catch(error => console.error("❌ Fehler beim Abrufen der Berechtigungen:", error));
    }

    // Content-Blöcke registrieren und Berechtigungen anwenden
    console.log("Registriere Content-Blöcke...");
    const contentRegistrations = [];

    document.querySelectorAll("[data-content]").forEach(element => {
        const contentTitle = element.getAttribute("data-content");
        console.log(`Gefundener Content-Block: ${contentTitle}`);
        
        const registrationPromise = fetch("/api/content/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ 
                title: contentTitle, 
                description: "Automatisch erkannt"
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => console.error("❌ Fehler bei der Registrierung:", error));
        
        contentRegistrations.push(registrationPromise);
    });
    
    // Nachdem alle Content-Blöcke registriert wurden, Berechtigungen anwenden
    Promise.all(contentRegistrations)
        .then(() => {
            applyContentPermissions();
        });
});
