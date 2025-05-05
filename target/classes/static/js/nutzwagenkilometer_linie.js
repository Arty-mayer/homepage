document.addEventListener('DOMContentLoaded', function () {
    function toggleCheckboxes(selectAll) {
        document.querySelectorAll('#linien input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = selectAll;
        });
    }

    function formatKilometers(meters) {
        return Math.round(meters / 1000).toLocaleString('de-DE');
    }

    document.getElementById('abfrageButton').addEventListener('click', async () => {
        const startdatum = document.getElementById('startdatum').value;
        const enddatum = document.getElementById('enddatum').value;
        const linien = Array.from(document.querySelectorAll('input[name="linie"]:checked')).map(cb => cb.value);

        if (!startdatum || !enddatum || linien.length === 0) {
            alert('Bitte Startdatum, Enddatum und mindestens eine Linie auswählen.');
            return;
        }

        try {
            const response = await fetch(`/api/kilometer?startdatum=${startdatum}&enddatum=${enddatum}&linien=${encodeURIComponent(linien.join(','))}`);
            if (!response.ok) throw new Error('Fehler beim Abrufen der Daten vom Backend.');

            const data = await response.json();
            const ergebnisContainer = document.getElementById('ergebnis');
            let tableHTML = `<table border="1" style="width: 100%; border-collapse: collapse;">
                                <thead><tr><th>Linie</th><th>Fahrten</th><th>Gesamt (KM)</th></tr></thead>
                                <tbody>`;

            data.ergebnisse.forEach(e => {
                tableHTML += `<tr><td>${e.linie}</td><td>${e.anzahlFahrten}</td><td>${formatKilometers(e.kilometer)}</td></tr>`;
            });

            tableHTML += '</tbody></table>';
            ergebnisContainer.innerHTML = tableHTML;
        } catch (error) {
            alert('Fehler bei der Abfrage.');
        }
    });
});
