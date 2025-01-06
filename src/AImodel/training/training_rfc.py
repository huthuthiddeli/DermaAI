import os
import joblib
import numpy as np
import matplotlib.pyplot as plt
from sklearn import metrics
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.utils.validation import check_is_fitted


class RandomForest:
    def __init__(self, dataset, reshape_size, model_save_path):
        self.X, self.y = dataset
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.rf = RandomForestClassifier(n_estimators=100)
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(self.X, self.y, test_size=0.2, random_state=42)

    def train(self):
        self.rf.fit(self.X_train, self.y_train)
        joblib.dump(self.rf, self.model_save_path + 'trained_rfc.joblib')
        print('Trained and dumped model: ', self.rf.__class__.__name__)

    def predict(self):
        try:
            check_is_fitted(self.rf)
        except:
            if os.path.isfile(self.model_save_path + 'trained_rfc.joblib'):
                self.rf = joblib.load(self.model_save_path + 'trained_rfc.joblib')
                print("Loaded pre-trained model.")
            else:
                raise ValueError("Model is not trained and no pre-trained model found.")

        y_pred = self.rf.predict(self.X_test)

        _, axes = plt.subplots(1, 5, figsize=(10, 3))
        for i, (ax, image, prediction, true_label) in enumerate(zip(axes, self.X_test, y_pred, self.y_test)):
            ax.set_axis_off()
            ax.imshow(image.reshape(self.reshape_size, self.reshape_size), cmap='gray')

            print(f"Image {i + 1} - Prediction: {prediction}, \n\tTrue label: {true_label}")

        plt.title(self.rf.__class__.__name__)
        plt.show()

        print(
            f"Classification report for classifier {self.rf}:\n"
            f"{metrics.classification_report(self.y_test, y_pred, zero_division=0)}\n"
        )
