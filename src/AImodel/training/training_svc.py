from matplotlib import pyplot as plt
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from sklearn import datasets, metrics
from loadImages import loadImagesAs1DVector
import joblib

dataset = datasets.load_digits()
X, y = dataset.data, dataset.target

#split dataset
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

#train model
svc = SVC(kernel='linear')
svc.fit(X_train, y_train)

# 5. Vorhersagen machen
y_pred = svc.predict(X_test)

# Beispiel: Zeige einige Vorhersagen
_, axes = plt.subplots(1, 5, figsize=(10, 3))
for ax, image, prediction in zip(axes, X_test, y_pred):
    ax.set_axis_off()
    ax.imshow(image.reshape(8, 8), cmap='gray')
    ax.set_title(f"Prediction: {prediction}")
plt.show()

print(
    f"Classification report for classifier {svc}:\n"
    f"{metrics.classification_report(y_test, y_pred)}\n"
)

#save model
joblib.dump(svc, '../trained_models/trained_svc.joblib')
