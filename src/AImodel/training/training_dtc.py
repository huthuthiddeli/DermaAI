import joblib
import matplotlib.pyplot as plt
from sklearn import metrics
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
import os

from sklearn.utils.validation import check_is_fitted


class DecisionTree:
    def __init__(self, dataset, reshape_size, model_save_path):
        self.X, self.y = dataset  # Extrahieren der Features (X) und Labels (y) aus dem Dataset
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.tree = DecisionTreeClassifier()
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(self.X, self.y, test_size=0.2, random_state=42)

    def train(self):
        self.tree.fit(self.X_train, self.y_train)
        joblib.dump(self.tree, self.model_save_path + 'trained_dtc.joblib')
        print('Trained and dumped model: ', self.tree.__class__.__name__)

    def predict(self):
        try:
            check_is_fitted(self.tree)
        except:
            if os.path.isfile(self.model_save_path + 'trained_dtc.joblib'):
                self.tree = joblib.load(self.model_save_path + 'trained_dtc.joblib')
                print("Loaded pre-trained model.")
            else:
                raise ValueError("Model is not trained and no pre-trained model found.")

        y_pred = self.tree.predict(self.X_test)

        _, axes = plt.subplots(1, 5, figsize=(10, 3))
        for i, (ax, image, prediction, true_label) in enumerate(zip(axes, self.X_test, y_pred, self.y_test)):
            ax.set_axis_off()
            ax.imshow(image.reshape(self.reshape_size, self.reshape_size), cmap='gray')

            print(f"Image {i + 1} - Prediction: {prediction}, \n\tTrue label: {true_label}")

        plt.title(self.tree.__class__.__name__)
        plt.show()

        print(
            f"Classification report for classifier {self.tree}:\n"
            f"{metrics.classification_report(self.y_test, y_pred, zero_division=0)}\n"
        )