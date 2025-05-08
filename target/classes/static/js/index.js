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
 /*   fetch("/debug/auth")
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
        });*/
/*
    // Funktion zum Anwenden der Content-Berechtigungen
    function applyContentPermissions() {
        fetch("/api/content/user")
            .then(response => response.json())
            .then(content => {
                // Für jeden Content-Block prüfen, ob der Benutzer Zugriff hat
                document.querySelectorAll("[data-content]").forEach(element => {
                    const contentTitle = element.getAttribute("data-content");
                    const allowed = content.some(item => item.title === contentTitle);
                   // element.style.display = allowed ? "block" : "none";
                });
            })
            .catch(error => console.error("❌ Fehler beim Abrufen der Berechtigungen:", error));
    }
*/
    // Content-Blöcke registrieren und Berechtigungen anwenden
    console.log("Registriere Content-Blöcke...");
    const contentRegistrations = [];

    document.querySelectorAll("[data-content]").forEach(element => {
        const contentTitle = element.getAttribute("data-content");
        console.log(`Gefundener Content-Block: ${contentTitle}`);

        const registrationPromise = fetch("/api/content/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
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

    documentController = new DocumentController();
    documentController.getMainMenu();
});

class MainMenu {
    menuHtml;

    constructor() {

    }

    insertInDocumentHTML(){
        let menuGrid = document.getElementById("main-menu-container")
        console.log(this.menuHtml);
        menuGrid.innerHTML = "";
        menuGrid.innerHTML = this.menuHtml;
    }

    async makeMainMenu(menuId){
        if (menuId==null || (typeof menuId) !== "number"){
            return;
        }
        let menuData = await this.#loadMenuFromServer(menuId);
        if("error" in menuData){
            console.error("Error: " + menuData.Message + " -error code: " + menuData.error)
            return;
        }
        if (menuData.itemsCount<1){
            this.menuHtml = "Es gibt keine verfügbares optionen..."
            this.insertInDocumentHTML();
            return
        }
        let outHTML = "";
        Object.entries(menuData.items).forEach(([key, item]) => {

            let itemClass = (item.subItemsCount>0)? "card has-submenu" : "card"
            outHTML = outHTML + `<div class="${itemClass}" data-content="${item.name}">`;
            let isActive = false;
            if (item.link && item.link.trim() !==""){
                outHTML = outHTML + `<a href="${item.link}">`;
                isActive = true;
            }

            outHTML = outHTML + `<img src="/images/statistic.png" alt="statistic Icon"><h3>${item.title}</h3>`
            if (isActive){
                outHTML = outHTML + "</a>";
            }
            if (item.subItemsCount > 0 && item.menuSubItems && typeof item.menuSubItems === "object") {
                outHTML = outHTML + `<div class="extra-content">`;
                Object.entries(item.menuSubItems).forEach(([key, subItem]) => {
                    outHTML = outHTML + `<a href="${subItem.link}">${subItem.title}</a>`;
                });
                outHTML = outHTML + "</div>";
            }
            outHTML = outHTML + "</div>"
         //   console.log(outHTML);

        });

        this.menuHtml = outHTML;
        await this.#sleep(500);
        this.insertInDocumentHTML();

    }


    #sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    async #loadMenuFromServer(menuId) {
        if (menuId == null || (typeof menuId) !== "number") {
            return null;
        }
        try {
            const response = await fetch('api/menu/' + menuId); // URL сервера
            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }
            const data = await response.json(); // преобразование JSON в объект

            return data;
            // Обработка данных
            // processData(data);

        } catch (error) {
            console.error('Ошибка при получении данных:', error);
            return null;
        }
    }

}

class DocumentController {
    #mainMenu
    constructor() {
    this.#mainMenu = new MainMenu();
    }
    getMainMenu(){
        this.#mainMenu.makeMainMenu(0);
    }
}
