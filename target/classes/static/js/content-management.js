document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM geladen, starte Content-Management...");
    loadContent();
    
    // Event-Listener für den "Neuen Content hinzufügen"-Button
    document.getElementById("add-content-btn").addEventListener("click", showAddContentDialog);
});
// Modul: Content-Daten laden
function loadContent() {
    console.log("Lade Content-Daten...");
    fetch("/api/content")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(content => {
            console.log("Erhaltene Content-Daten:", content);
            renderContentTable(content);
        })
        .catch(error => {
            console.error("Fehler beim Laden der Content-Daten:", error);
            showErrorMessage(error.message);
        });
}

// Modul: Content-Tabelle rendern
function renderContentTable(content) {
    const contentTable = document.getElementById("content-list");
    contentTable.innerHTML = "";

    if (!Array.isArray(content) || content.length === 0) {
        console.log("Keine Content-Daten vorhanden!");
        showEmptyContentMessage(contentTable);
        return;
    }

    // Für jeden Content-Eintrag die Berechtigungen laden und dann die Zeile erstellen
    const promises = content.map(item => loadContentPermissions(item));

    // Warten, bis alle Berechtigungen geladen sind
    Promise.all(promises)
        .then(results => {
            // Zeilen zur Tabelle hinzufügen
            results.forEach(({ row }) => {
                contentTable.appendChild(row);
            });

            // Event-Listener für Buttons hinzufügen
            addContentTableEventListeners();
        });
}
// Modul: Berechtigungen für einen Content-Eintrag laden
function loadContentPermissions(item) {
    return fetch(`/api/content/${item.id}/permissions`)
        .then(response => response.json())
        .then(permissions => {
            console.log(`Berechtigungen für Content ${item.id}:`, permissions);
            
            // Alle Rollen als kommagetrennte Liste anzeigen
            // Entferne "ROLE_" aus jeder Rolle vor der Anzeige
            const roles = permissions.map(p => {
                if (p.role && p.role.startsWith("ROLE_")) {
                    return p.role.substring(5); // Entferne "ROLE_"
                }
                return p.role;
            }).join(", ");
            
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item.id}</td>
                <td>${item.title}</td>
                <td>${item.description || ""}</td>
                <td>${item.htmlPath || ""}</td>
                <td>${roles || "-"}</td>
                <td>
                    <button class="view-permissions" data-content-id="${item.id}">Berechtigungen</button>
                    <button class="delete-content" data-content-id="${item.id}">Löschen</button>
                </td>
            `;
            return { row, contentId: item.id };
        })
        .catch(error => {
            console.error(`Fehler beim Laden der Berechtigungen für Content ${item.id}:`, error);
            
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item.id}</td>
                <td>${item.title}</td>
                <td>${item.description || ""}</td>
                <td>${item.htmlPath || ""}</td>
                <td>Fehler</td>
                <td>
                    <button class="view-permissions" data-content-id="${item.id}">Berechtigungen</button>
                    <button class="delete-content" data-content-id="${item.id}">Löschen</button>
                </td>
            `;
            return { row, contentId: item.id };
        });
}


// Modul: Event-Listener für Content-Tabelle hinzufügen
function addContentTableEventListeners() {
    // Event-Listener für Löschen-Buttons
    document.querySelectorAll(".delete-content").forEach(button => {
        button.addEventListener("click", function () {
            const contentId = this.getAttribute("data-content-id");
            if (confirm("Inhalt wirklich löschen?")) {
                deleteContent(contentId);
            }
        });
    });

    // Event-Listener für Berechtigungen-Buttons
    document.querySelectorAll(".view-permissions").forEach(button => {
        button.addEventListener("click", function () {
            const contentId = this.getAttribute("data-content-id");
            showPermissionsDialog(contentId);
        });
    });
}

// Modul: Content löschen
function deleteContent(contentId) {
    fetch(`/api/content/${contentId}`, { method: "DELETE" })
        .then(response => {
            if (response.ok) {
                alert("Inhalt gelöscht!");
                loadContent();
            } else {
                alert("Fehler beim Löschen!");
            }
        });
}
// Modul: Leere Content-Tabelle anzeigen
function showEmptyContentMessage(contentTable) {
    const row = document.createElement("tr");
    row.innerHTML = `
        <td colspan="6" style="text-align: center;">Keine Inhalte vorhanden</td>
    `;
    contentTable.appendChild(row);
}


// Modul: Fehlermeldung anzeigen
function showErrorMessage(message) {
    const contentTable = document.getElementById("content-list");
    contentTable.innerHTML = `
        <tr>
            <td colspan="6" style="text-align: center; color: red;">
                Fehler beim Laden der Daten: ${message}
            </td>
        </tr>
    `;
}


// Modul: Modales Dialogfenster schließen
function closeModal() {
    const modal = document.querySelector(".modal");
    if (modal) {
        document.body.removeChild(modal);
    }
}
// Modul: Dialog zum Hinzufügen neuer Content-Einträge anzeigen
function showAddContentDialog() {
    // Template klonen und anzeigen
    const template = document.getElementById("add-content-template");
    const dialog = template.content.cloneNode(true);
    document.body.appendChild(dialog);
    
    // Event-Listener für Abbrechen-Button
    document.getElementById("cancel-content").addEventListener("click", function() {
        closeModal();
    });
    
    // Event-Listener für Speichern-Button
    document.getElementById("save-content").addEventListener("click", function() {
        saveNewContent();
    });
}

// Modul: Neuen Content speichern
function saveNewContent() {
    const title = document.getElementById("content-title").value;
    const description = document.getElementById("content-description").value;
    const htmlPath = document.getElementById("content-html-path").value;
    const link = document.getElementById("content-link").value;
    
    if (!title) {
        alert("Bitte einen Titel eingeben!");
        return;
    }
    
    console.log("Erstelle neuen Content:", { title, description, htmlPath, link });
    
    fetch("/api/content", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            title,
            description,
            htmlPath,
            link
        })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return response.json().then(data => {
                throw new Error(data.error || "Fehler beim Erstellen des Contents");
            });
        }
    })
    .then(data => {
        console.log("Content erfolgreich erstellt:", data);
        alert("Content erfolgreich erstellt!");
        
        // Automatisch eine Berechtigung für ADMIN hinzufügen
        if (data.id) {
            return addContentPermission(data.id, "ROLE_ADMIN");
        }
    })
    .then(() => {
        closeModal();
        loadContent();
    })
    .catch(error => {
        console.error("Fehler beim Erstellen des Contents:", error);
        alert(error.message);
    });
}
// Modul: Dialog zum Anzeigen/Bearbeiten von Berechtigungen
function showPermissionsDialog(contentId) {
    console.log("Zeige Berechtigungen für Content-ID:", contentId);
    fetch(`/api/content/${contentId}/permissions`)
        .then(response => response.json())
        .then(permissions => {
            console.log("Erhaltene Berechtigungen:", permissions);
            
            // Template klonen und anzeigen
            const template = document.getElementById("permissions-template");
            const dialog = template.content.cloneNode(true);
            document.body.appendChild(dialog);
            
            // Berechtigungen in die Tabelle einfügen
            const permissionsList = document.getElementById("permissions-list");
            permissions.forEach(perm => {
                // Entferne "ROLE_" aus der Rolle für die Anzeige
                let displayRole = perm.role || "-";
                if (displayRole.startsWith("ROLE_")) {
                    displayRole = displayRole.substring(5);
                }
                
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${perm.id}</td>
                    <td>${displayRole}</td>
                    <td>
                        <button class="delete-permission" data-permission-id="${perm.id}">Löschen</button>
                    </td>
                `;
                permissionsList.appendChild(row);
            });

            // Event-Listener für Schließen-Button
            document.getElementById("close-permissions").addEventListener("click", function() {
                closeModal();
                loadContent();
            });

            // Event-Listener für Löschen-Buttons
            addPermissionDeleteEventListeners(contentId);

            // Event-Listener für Hinzufügen-Button
            document.getElementById("add-permission").addEventListener("click", function() {
                addPermissionFromDialog(contentId);
            });
        })
        .catch(error => {
            console.error("Fehler beim Laden der Berechtigungen:", error);
            alert("Fehler beim Laden der Berechtigungen: " + error.message);
        });
}

// Modul: Event-Listener für Berechtigungen-Löschen-Buttons hinzufügen
function addPermissionDeleteEventListeners(contentId) {
    document.querySelectorAll(".delete-permission").forEach(button => {
        button.addEventListener("click", function() {
            const permissionId = this.getAttribute("data-permission-id");
            console.log("Lösche Berechtigung mit ID:", permissionId);
            
            if (confirm("Berechtigung wirklich löschen?")) {
                deletePermission(permissionId, contentId);
            }
        });
    });
}

// Modul: Berechtigung löschen
function deletePermission(permissionId, contentId) {
    fetch(`/api/content/permissions/${permissionId}`, { method: "DELETE" })
        .then(response => {
            if (response.ok) {
                alert("Berechtigung gelöscht!");
                closeModal();
                showPermissionsDialog(contentId); // Dialog neu laden
            } else {
                alert("Fehler beim Löschen der Berechtigung!");
            }
        });
}

// Modul: Berechtigung aus Dialog hinzufügen
function addPermissionFromDialog(contentId) {
    const role = document.getElementById("role").value;
    
    if (!role) {
        alert("Bitte eine Rolle auswählen!");
        return;
    }
    
    console.log("Füge Berechtigung hinzu:", { role });
    
    fetch(`/api/content/${contentId}/permissions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role })
    })
    .then(response => {
        if (response.ok) {
            alert("Berechtigung hinzugefügt!");
            closeModal();
            showPermissionsDialog(contentId); // Dialog neu laden
        } else {
            return response.json().then(data => {
                throw new Error(data.error || "Fehler beim Hinzufügen der Berechtigung");
            });
        }
    })
    .catch(error => {
        console.error("Fehler beim Hinzufügen der Berechtigung:", error);
        alert(error.message);
    });
}

// Modul: Berechtigung hinzufügen
function addContentPermission(contentId, role) {
    console.log(`Füge ${role}-Berechtigung hinzu für Content-ID:`, contentId);
    return fetch(`/api/content/${contentId}/permissions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role })
    });
}
