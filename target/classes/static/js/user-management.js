document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM geladen, starte Initialisierung...");
    loadUsers();
    
    // Event-Listener für den "Neuen Benutzer hinzufügen"-Button
    document.getElementById("add-user-btn").addEventListener("click", function() {
        showUserDialog();
    });

    function loadUsers() {
        console.log("Lade Benutzerdaten...");
        fetch("/api/users")
            .then(response => response.json())
            .then(users => {
                console.log("Erhaltene Benutzerdaten:", users);
                const userTable = document.getElementById("user-list");
                userTable.innerHTML = "";

                users.forEach(user => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.role}</td>
                        <td>
                            <button class="edit-user" data-user-id="${user.id}" 
                                data-username="${user.username}" 
                                data-email="${user.email}" 
                                data-role="${user.role}">Bearbeiten</button>
                            <button class="delete-user" data-user-id="${user.id}">Löschen</button>
                        </td>
                    `;
                    userTable.appendChild(row);
                });

                // Event-Listener für Löschen-Buttons
                document.querySelectorAll(".delete-user").forEach(button => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");
                        if (confirm("Benutzer wirklich löschen?")) {
                            deleteUser(userId);
                        }
                    });
                });
                
                // Event-Listener für Bearbeiten-Buttons
                document.querySelectorAll(".edit-user").forEach(button => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");
                        const username = this.getAttribute("data-username");
                        const email = this.getAttribute("data-email");
                        const role = this.getAttribute("data-role");
                        showUserDialog(userId, username, email, role);
                    });
                });
            })
            .catch(error => {
                console.error("Fehler beim Laden der Benutzerdaten:", error);
            });
    }
    
    // Funktion zum Anzeigen des Benutzer-Dialogs (für Hinzufügen oder Bearbeiten)
    function showUserDialog(userId = null, username = "", email = "", role = "") {
        // Template klonen und anzeigen
        const template = document.getElementById("user-dialog-template");
        const dialog = template.content.cloneNode(true);
        document.body.appendChild(dialog);
        
        // Dialog-Titel anpassen
        const dialogTitle = document.getElementById("dialog-title");
        if (userId) {
            dialogTitle.textContent = "Benutzer bearbeiten";
        } else {
            dialogTitle.textContent = "Neuen Benutzer hinzufügen";
        }
        
        // Felder mit vorhandenen Daten füllen
        document.getElementById("edit-username").value = username;
        document.getElementById("edit-email").value = email;
        
        // Rolle auswählen
        const roleSelect = document.getElementById("edit-role");
        for (let i = 0; i < roleSelect.options.length; i++) {
            if (roleSelect.options[i].value === role) {
                roleSelect.selectedIndex = i;
                break;
            }
        }
        
        // Event-Listener für Abbrechen-Button
        document.getElementById("cancel-edit").addEventListener("click", function() {
            closeModal();
        });
        
        // Event-Listener für Speichern-Button
        document.getElementById("save-user").addEventListener("click", function() {
            if (userId) {
                updateUser(userId);
            } else {
                createUser();
            }
        });
    }
    
    // Funktion zum Schließen des modalen Dialogs
    function closeModal() {
        const modal = document.querySelector(".modal");
        if (modal) {
            document.body.removeChild(modal);
        }
    }
    
    // Funktion zum Erstellen eines neuen Benutzers
    function createUser() {
        const username = document.getElementById("edit-username").value;
        const email = document.getElementById("edit-email").value;
        const role = document.getElementById("edit-role").value;
        const password = document.getElementById("edit-password").value;
        
        if (!username || !password) {
            alert("Bitte Benutzername und Passwort eingeben!");
            return;
        }
        
        fetch("/api/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                username,
                email,
                password,
                userRole: role
            })
        })
        .then(response => {
            if (response.ok) {
                alert("Benutzer erfolgreich erstellt!");
                closeModal();
                loadUsers();
            } else {
                return response.json().then(data => {
                    throw new Error(data.error || "Fehler beim Erstellen des Benutzers");
                });
            }
        })
        .catch(error => {
            alert(error.message);
        });
    }
    
    // Funktion zum Aktualisieren eines Benutzers
    function updateUser(userId) {
        const username = document.getElementById("edit-username").value;
        const email = document.getElementById("edit-email").value;
        const role = document.getElementById("edit-role").value;
        const password = document.getElementById("edit-password").value;
        
        if (!username) {
            alert("Bitte Benutzername eingeben!");
            return;
        }
        
        const userData = {
            username,
            email,
            userRole: role
        };
        
        // Passwort nur hinzufügen, wenn es eingegeben wurde
        if (password) {
            userData.password = password;
        }
        
        fetch(`/api/users/${userId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData)
        })
        .then(response => {
            if (response.ok) {
                alert("Benutzer erfolgreich aktualisiert!");
                closeModal();
                loadUsers();
            } else {
                return response.json().then(data => {
                    throw new Error(data.error || "Fehler beim Aktualisieren des Benutzers");
                });
            }
        })
        .catch(error => {
            alert(error.message);
        });
    }
    
    // Funktion zum Löschen eines Benutzers
    function deleteUser(userId) {
        fetch(`/api/users/${userId}`, { method: "DELETE" })
            .then(response => {
                if (response.ok) {
                    alert("Benutzer gelöscht!");
                    loadUsers();
                } else {
                    alert("Fehler beim Löschen!");
                }
            });
    }
});
