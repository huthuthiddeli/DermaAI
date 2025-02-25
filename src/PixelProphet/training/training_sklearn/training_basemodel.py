import os
import joblib
import numpy as np

from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split
from modules.ImageProcessor import preprocess_image
from ..IModel import IModel


class BaseModel(IModel):
    def __init__(self, model, model_save_path, model_name_extension=""):
        self.model = model
        self.model_save_path = model_save_path
        self.model_name = self.model.__class__.__name__ + model_name_extension

    def check(self):
        try:
            model_path = f"{self.model_save_path}trained_{self.model_name}.joblib"
            if os.path.isfile(model_path):
                self.model = joblib.load(model_path)
                return True
        except Exception:
            return False
        return False

    def train(self, dataset, epochs, reshape_size, batch_size=None, lr=None):
        try:
            x, y = dataset
            self.model.fit(x, y)
            joblib.dump(self.model, f"{self.model_save_path}trained_{self.model_name}{self.model_name_extension}.joblib")
            print('Trained and dumped model: ', self.model_name)
            self.save_model_metadata(self.model_save_path, self.model_name, reshape_size, list(np.unique(y)))
            return True, None
        except Exception as e:
            error = f'{self.model_name} could not be trained. Error: {str(e)}'
            return False, error

    def make_prediction(self, image):
        if self.check() is False:
            raise ValueError('Model is not trained')

        reshape_size, class_labels = self.load_model_metadata(self.model_save_path, self.model_name)
        image_array = preprocess_image(image, reshape_size)

        if hasattr(self.model, "predict_proba"):
            probabilities = self.model.predict_proba(image_array)[0]

            if class_labels is None:
                class_labels = [f"class_{i}" for i in range(len(probabilities))]

            prediction_dict = {label: float(prob) for label, prob in zip(class_labels, probabilities)}

            return prediction_dict
        else:
            prediction = self.model.predict(image_array)
            return {"predicted_label": prediction}

    def get_classification_report(self, dataset, reshape_size):
        try:
            x, y = dataset
            x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=42)

            self.model.fit(x_train, y_train)

            y_pred = self.model.predict(x_test)

            report = classification_report(y_test, y_pred, zero_division=0)
            return True, report
        except Exception as e:
            error = f"Error during model evaluation: {str(e)}"
            return False, error
