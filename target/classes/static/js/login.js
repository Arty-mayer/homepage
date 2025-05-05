document.addEventListener('DOMContentLoaded', function () {
    // 1️⃣ Form langsam einblenden
    const loginSection = document.querySelector('section');
    loginSection.style.opacity = 0;

    setTimeout(() => {
        loginSection.style.transition = 'opacity 1s ease-in-out';
        loginSection.style.opacity = 1;
    }, 500);

    // 2️⃣ Felder auf bereits vorhandenen Inhalt prüfen (Autofill etc.)
    const inputs = document.querySelectorAll('.inputbox input');
    inputs.forEach(input => {
        // Sofort beim Laden
        if (input.value.trim() !== '') {
            input.classList.add('has-content');
        }
        // Bei jeder Eingabe
        input.addEventListener('input', () => {
            if (input.value.trim() !== '') {
                input.classList.add('has-content');
            } else {
                input.classList.remove('has-content');
            }
        });
    });

    // 3️⃣ Klick auf "Log in" überprüfen, ob Felder gefüllt sind
    const loginButton = document.querySelector('button');
    loginButton.addEventListener('click', function (event) {
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');

        if (!usernameInput.value.trim() || !passwordInput.value.trim()) {
            event.preventDefault(); // Verhindert das Absenden des Formulars
            alert("Bitte Benutzername und Passwort eingeben.");
        }
    });
});
