document.addEventListener('DOMContentLoaded', function () {
    const signupForm = document.querySelector('#signupForm');
    signupForm.style.opacity = 0;
  
    setTimeout(() => {
        signupForm.style.transition = 'opacity 1s ease-in-out';
        signupForm.style.opacity = 1;
    }, 500);
  
    signupForm.addEventListener('submit', function (event) {
        event.preventDefault(); // Verhindert das Standard-Absenden des Formulars
  
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('passwordcon').value;
        const role = document.getElementById('role').value; // Benutzerrolle
        const submitButton = document.getElementById("submit");
  
        // Fehlerprüfung
        if (!username || !email || !password || !confirmPassword) {
            alert("Bitte alle Felder ausfüllen.");
            return;
        }
  
        if (password !== confirmPassword) {
            alert("Passwörter stimmen nicht überein!");
            return;
        }
  
        const data = {
            username,
            email,
            password,
            userRole: role
        };
  
        fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (response.ok) {
                alert("Benutzer erfolgreich registriert!");
  
                submitButton.textContent = "Benutzer erstellt! Zurück zur Startseite...";
                submitButton.disabled = true; // Verhindert mehrfaches Klicken
  
                setTimeout(() => {
                    window.location.href = "/index"; // Admin bleibt eingeloggt
                }, 2000);
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .catch(error => {
            alert("Fehler: " + error.message);
        });
    });
});
