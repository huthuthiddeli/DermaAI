import joblib
import numpy as np
import matplotlib.pyplot as plt
import cv2
from sklearn import datasets, metrics
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier

# 1. Digits-Datensatz laden
digits = datasets.load_digits()
X, y = digits.data, digits.target

# 2. Datensatz in Trainings- und Testdaten aufteilen
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 3. KNN-Modell initialisieren
knn = KNeighborsClassifier(n_neighbors=3)

# 4. Modell trainieren
knn.fit(X_train, y_train)

# 5. Vorhersagen machen
y_pred = knn.predict(X_test)

# Beispiel: Zeige einige Vorhersagen
_, axes = plt.subplots(1, 5, figsize=(10, 3))
for ax, image, prediction in zip(axes, X_test, y_pred):
    ax.set_axis_off()
    ax.imshow(image.reshape(8, 8), cmap='gray')
    ax.set_title(f"Prediction: {prediction}")
plt.show()

print(
    f"Classification report for classifier {knn}:\n"
    f"{metrics.classification_report(y_test, y_pred)}\n"
)

# Modell speichern
joblib.dump(knn, '../trained_models/trained_knn.joblib')

# 6. Funktion zum Laden und Verarbeiten eines Bildes
def load_image_as_1d_vector(image_path):
    # Bild laden
    image = cv2.imread(image_path)

    if image is not None:
        # Bild in Graustufen umwandeln
        gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # Bild auf 8x8 Pixel verkleinern
        resized_image = cv2.resize(gray_image, (8, 8))

        # Pixelwerte zwischen 0 und 16 normalisieren
        normalized_image = (resized_image / 255.0) * 16

        # Bild als 1D-Vektor darstellen
        features = normalized_image.flatten()

        #reshape features für Vorhersage
        features = features.reshape(1, -1)

        return features
    else:
        raise ValueError("Image could not be loaded. Please check the file path.")

# 7. Vorhersage für ein neues Bild machen
image_path = '../test_images/sechs1.png'
features = load_image_as_1d_vector(image_path)
prediction = knn.predict(features)
print(f"Die vorhergesagte Ziffer für das Bild ist: {prediction[0]}")

# 8. Bild anzeigen
plt.imshow(cv2.imread(image_path, cv2.IMREAD_GRAYSCALE), cmap='gray')
plt.title(f"Vorhersage: {prediction[0]}")
plt.axis('off')
plt.show()
