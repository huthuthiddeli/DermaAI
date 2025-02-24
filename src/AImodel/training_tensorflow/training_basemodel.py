from sklearn.metrics import classification_report
from tensorflow import keras
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split


class BaseModelTF:
    def __init__(self, model, reshape_size, model_save_path, model_name_extension=""):
        self.model = model
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.model_name_extension = model_name_extension
        self.model_name = self.model.__class__.__name__.lower()

    def check(self):
        model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}_{self.model_name_extension}.h5")
        if os.path.isfile(model_path):
            self.model = keras.models.load_model(model_path)
            return True
        return False

    def train(self, dataset, epochs=10, batch_size=32, lr=0.001):
        X, y = dataset

        # **Label-Encoding und sicherstellen, dass y korrekt umgewandelt wird**
        label_encoder = LabelEncoder()
        y_encoded = label_encoder.fit_transform(y)  # Kodierung sicherstellen
        num_classes = len(label_encoder.classes_)

        print(f"Erkannte {num_classes} Klassen: {label_encoder.classes_}")

        # **Check: Labels und Anzahl der Klassen**
        assert len(np.unique(
            y_encoded)) == num_classes, f"Fehler: Es gibt mehr unterschiedliche Labels als erwartet ({len(np.unique(y_encoded))} vs {num_classes})"

        # **Daten Vorverarbeitung**: Anpassen der Form und Normalisierung
        X = X / 255.0  # Normalisiere Bilder, falls erforderlich

        # **Überprüfen der Label-Werte:**
        # Sicherstellen, dass alle Labels innerhalb des gültigen Bereichs liegen
        if np.max(y_encoded) >= num_classes:
            raise ValueError(
                f"Fehler: Einige Labels sind außerhalb des Bereichs. Maximales Label: {np.max(y_encoded)}, erwartet max: {num_classes - 1}")

        # Sicherstellen, dass die Eingabeform die richtige ist (128x128x1)
        if len(X.shape) == 2:  # Wenn das Bild flach (1D) ist
            X = X.reshape(-1, self.reshape_size, self.reshape_size, 1)
        elif len(X.shape) == 3:  # Falls die Form (128, 128) vorliegt (2D), Channel hinzufügen
            X = np.expand_dims(X, axis=-1)  # Umwandlung in (128, 128, 1)

        # **Falls das Bild Graustufenbilder (1 Kanal) sind, erweitere sie zu 3 Kanälen**
        if X.shape[-1] == 1:
            X = np.repeat(X, 3, axis=-1)  # Umwandlung in RGB (3-Kanäle)

        # **Lernrate an den Optimierer übergeben**
        optimizer = keras.optimizers.Adam(learning_rate=lr)

        # **Modell anpassen:**
        # Dynamische Anpassung der Output-Schicht für die erkannte Anzahl an Klassen
        self.model.compile(optimizer=optimizer,
                           loss='sparse_categorical_crossentropy',  # Für mehrklassige Klassifikation
                           metrics=['accuracy'])

        self.model.fit(X, y_encoded, epochs=epochs, batch_size=batch_size)
        self.model.save(f"{self.model_save_path}trained_{self.model_name}.keras")
        print('Trained and dumped model: ', self.model_name)


    def make_prediction(self, image_array):
        if len(image_array.shape) == 3:  # Ensure batch dimension
            image_array = np.expand_dims(image_array, axis=0)
        prediction = self.model.predict(image_array)
        return prediction.tolist()

    def get_classification_report(self, dataset):
        X, y = dataset

        # **Label-Encoding der Zielwerte**
        label_encoder = LabelEncoder()
        y_encoded = label_encoder.fit_transform(y)

        # Split dataset into training and test sets
        X_train, X_test, y_train, y_test = train_test_split(X, y_encoded, test_size=0.2, random_state=42)

        # **Falls das Bild Graustufenbilder (1 Kanal) sind, erweitere sie zu 3 Kanälen**
        if X_test.shape[-1] == 1:
            X_test = np.repeat(X_test, 3, axis=-1)  # Umwandlung in RGB (3-Kanäle)

        # Make predictions using the TensorFlow model
        y_pred = self.model.predict(X_test)

        # For multi-class classification, take the class with the highest probability
        if len(y_pred.shape) > 1 and y_pred.shape[1] > 1:
            y_pred_classes = np.argmax(y_pred, axis=1)
        else:
            # For binary classification, threshold at 0.5
            y_pred_classes = (y_pred > 0.5).astype("int32")

        # Return the classification report as a string
        return classification_report(y_test, y_pred_classes)
