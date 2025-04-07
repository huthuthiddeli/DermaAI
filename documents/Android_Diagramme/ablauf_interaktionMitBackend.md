sequenceDiagram
    participant B as Benutzer
    participant V as View 
    participant F as Fragment
    participant VM as ViewModel 
    participant T as neuer IO-Thread
    participant R as RepoImpl
    participant A as API
    participant G as Api-Gateway

    B->>V: Anfrage via Btn-Click
    V->>F: Btn-Click registriert
    F->>VM: Anfrage weiterleiten
    VM->>T: Neuer Thread starten
    T->>R: Anfrage an RepoImpl weiterleiten
    R->>A: API Call ausführen
    A->>G: Anfrage an Api-Gateway senden
    G-->>A: Result (JSON)
    A-->>R: Result (JSON)
    R->>T: JSON -> Objekt konvertieren
    T->>VM: Objekt zurückgeben
    VM->>VM: Prüfe Result (Success/Failure)
    alt Erfolgreich
        VM->>VM: Erfolgreiches Ergebnis in Live-Data posten
        VM->>F: LiveData-Update (Success)
        F->>F : Ergebnis verwerten
    else Fehlgeschlagen
        VM->>VM: Error-Text in LiveData posten
        VM->>F: LiveData-Update (Fehler)
        F->>F: Fehler anzeigen
    end
