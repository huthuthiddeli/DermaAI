import os
import cv2

from sklearn.metrics import classification_report
from tensorflow import keras
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from modules.ImageProcessor import preprocess_image
from modules.ModelProcessor import adjust_model_output_layer_keras
from ..IModel import IModel


class BaseModelTF(IModel):
    def __init__(self, model, build_model_fn, model_save_path, model_name_extension=""):
        self.model = model
        self.model_build_fn = build_model_fn
        self.model_save_path = model_save_path
        self.model_name = self.model.name + model_name_extension

    def check(self):
        try:
            model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.keras")
            if os.path.isfile(model_path):
                self.model = keras.models.load_model(model_path)
                return True
        except Exception:
            return False
        return False

    def train(self, dataset, epochs, reshape_size, batch_size=16, lr=None):
        try:
            self.model = self.model_build_fn(reshape_size)

            x, y = dataset

            label_encoder = LabelEncoder()
            y_encoded = label_encoder.fit_transform(y)
            num_classes = len(label_encoder.classes_)

            x = x / 255.0
            x = np.expand_dims(x, axis=-1)

            x = np.array([cv2.resize(img, (reshape_size, reshape_size)) for img in x])

            self.model = adjust_model_output_layer_keras(self.model, num_classes, self.model_name)

            self.model.fit(x, y_encoded, epochs=epochs, batch_size=batch_size, verbose=1)

            model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.keras")
            self.model.save(model_path)
            self.save_model_metadata(self.model_save_path, self.model_name, reshape_size, list(label_encoder.classes_))
            print('Trained and dumped model: ', model_path)
            return True, None

        except Exception as e:
            error = f'{self.model_name} could not be trained. Error: {str(e)}'
            return False, error

    def make_prediction(self, image):
        if not self.check():
            raise ValueError('Model is not trained')

        reshape_size, class_labels = self.load_model_metadata(self.model_save_path, self.model_name)
        image_array = preprocess_image(image, reshape_size)

        try:
            image_array = image_array / 255.0
            image_array = np.array([cv2.resize(image_array, (reshape_size, reshape_size))])

            prediction = self.model.predict(image_array)

            if class_labels is None:
                class_labels = [f"class_{i}" for i in range(len(prediction[0]))]

            prediction_dict = {label: float(prob) for label, prob in zip(class_labels, prediction[0])}

            return prediction_dict

        except Exception as e:
            print(f'{str(e)}\n')
            raise ValueError(f"Error during prediction: {str(e)}")

    def get_classification_report(self, dataset, reshape_size):
        try:
            x, y = dataset
            x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=42)

            label_encoder = LabelEncoder()
            y_encoded = label_encoder.fit_transform(y_test)

            x_test = x_test / 255.0

            x_test = np.array([cv2.resize(img, (reshape_size, reshape_size)) for img in x_test])
            x_test = np.expand_dims(x_test, axis=-1)

            y_pred = self.model.predict(x_test, batch_size=16)

            if len(y_pred.shape) > 1 and y_pred.shape[1] > 1:
                y_pred_classes = np.argmax(y_pred, axis=1)
            else:
                y_pred_classes = (y_pred > 0.5).astype("int32")

            report = classification_report(y_encoded, y_pred_classes, zero_division=0)
            return True, report

        except Exception as e:
            error = f"Error during model evaluation: {str(e)}"
            return False, error
