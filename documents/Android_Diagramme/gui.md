graph TD
    Login[Login-Screen] -->|Erfolgreich| Home[Home-Screen]
    Login -->|Fehlgeschlagen| Error[Fehlermeldung]
    Home -->|Zur Gallery| Gallery[Gallery-Screen]
    Home -->|Zu Photo| Photo[Photo-Screen]
    Home -->|Zu Settings| Settings[Settings-Screen]
    Home -->|Zum Tutorial| Tutorial[Tutorial-Screen]
    Home -->|Zu Admin| Admin[Admin-Screen]
    Gallery -->|Bild anclicken| Result[Result-Screen]
    Photo -->|Button anclicken| ModelSelectionDialog[ModelSelectionDialog]
    ModelSelectionDialog -->|Modell ausgewählt| Camera[Camera-Screen]
    Camera -->|Foto geschossen| Resize[Resize-Screen]
    Result -->|Zurück| Gallery
    Resize -->|Zurück| Photo
    Settings -->|Zurück| Home
    Tutorial -->|Zurück| Home
    Admin -->|Zurück| Home
    Photo -->|Zurück| Home
    Gallery -->|Zurück| Home

    Admin -->|Wählt Modell| ModelSelectionDialog[ModelSelectionDialog]
    ModelSelectionDialog -->|Trainiert das Modell| InputEpochReshapeDialog[InputEpochReshapeDialog]

    Admin -->|Trainiert alle Modelle| InputEpochReshapeDialog[InputEpochReshapeDialog]
