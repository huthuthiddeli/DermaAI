import os

import joblib
from matplotlib import pyplot as plt
from sklearn import metrics
from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split
from sklearn.utils.validation import check_is_fitted


class BaseModel:
    def __init__(self, model, reshape_size, model_save_path, model_name_extension=""):
        self.model = model
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.model_name_extension = model_name_extension
        self.model_name = self.model.__class__.__name__.lower()

    def check(self):
        try:
            check_is_fitted(self.model)
        except:
            if os.path.isfile(f"{self.model_save_path}trained_{self.model_name}_{self.model_name_extension}.joblib"):
                self.model = joblib.load(f"{self.model_save_path}trained_{self.model_name}_{self.model_name_extension}.joblib")
                return True
            else:
                return False
        return True

    def train(self, dataset):
        X, y = dataset
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42)

        self.model.fit(X_train, y_train)
        joblib.dump(self.model, f"{self.model_save_path}trained_{self.model_name}.joblib")
        print('Trained and dumped model: ', self.model_name)

    def make_prediction(self, image_array):
        prediction = self.model.predict(image_array)
        return prediction.tolist()  # Convert to list for JSON serialization

    def get_classification_report(self, dataset):
        # Unpack dataset into features (X) and labels (y)
        X, y = dataset

        # Split dataset into training and test sets
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        # Fit the model on the training data (this is just to ensure that the model is trained)
        self.model.fit(X_train, y_train)

        # Make predictions using the trained model
        y_pred = self.model.predict(X_test)

        # Return the classification report as a string
        return classification_report(y_test, y_pred)