import joblib
import numpy as np
import matplotlib.pyplot as plt
from sklearn import datasets, metrics
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score

# 1. Digits-Datensatz laden
digits = datasets.load_digits()
X, y = digits.data, digits.target

# 2. Datensatz in Trainings- und Testdaten aufteilen
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 3. KNN-Modell initialisieren
knn = KNeighborsClassifier(n_neighbors=1)

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

joblib.dump(knn, '../trained_models/trained_knn.joblib')
