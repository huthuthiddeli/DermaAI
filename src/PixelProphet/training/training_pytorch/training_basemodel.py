import os
import torch
import torch.optim as optim

import torch.nn as nn
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sklearn.preprocessing import LabelEncoder
from torch.utils.data import TensorDataset, DataLoader
from modules.ImageProcessor import preprocess_image
from modules.ModelProcessor import adjust_model_output_layer_pytorch, \
    adjust_input_channels_pytorch_tensor, adjust_input_channels_pytorch
import torch.nn.functional as f
from ..IModel import IModel


class BaseModelTorch(IModel):
    def __init__(self, model, model_save_path, model_name_extension=""):
        self.model = model
        self.model_save_path = model_save_path
        self.model_name = self.model.__class__.__name__ + model_name_extension
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model.to(self.device)

    def check(self):
        try:
            model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.pt")
            if os.path.isfile(model_path):
                num_classes = len(self.load_model_metadata(self.model_save_path, self.model_name)[1])
                adjust_model_output_layer_pytorch(self.model, num_classes, self.device, self.model_name)
                adjust_input_channels_pytorch(self.model)
                self.model.load_state_dict(torch.load(model_path))
                self.model.to(self.device)
                return True
        except Exception as e:
            return False
        return False

    def train(self, dataset, epochs, reshape_size, batch_size=16, lr=0.001):
        try:
            x, y = dataset

            label_encoder = LabelEncoder()
            y_encoded = label_encoder.fit_transform(y)
            num_classes = len(label_encoder.classes_)

            x_tensor = torch.tensor(x, dtype=torch.float32).to(self.device)
            y_tensor = torch.tensor(y_encoded, dtype=torch.long).to(self.device)

            x_tensor = adjust_input_channels_pytorch_tensor(self.model, x_tensor, reshape_size)

            dataset = TensorDataset(x_tensor, y_tensor)
            train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

            self.model = adjust_model_output_layer_pytorch(self.model, num_classes, self.device, self.model_name)

            criterion = nn.CrossEntropyLoss()
            optimizer = optim.Adam(self.model.parameters(), lr=lr)

            for epoch in range(epochs):
                self.model.train()
                running_loss = 0.0
                for inputs, labels in train_loader:
                    optimizer.zero_grad()

                    try:
                        outputs = self.model(inputs)
                    except Exception as e:
                        error = (f'{self.model_name} could not be trained. Try using a different reshape size. '
                                 f'Error: {str(e)}')
                        return False, error

                    if isinstance(outputs, tuple):
                        outputs = outputs[0]

                    loss = criterion(outputs, labels)
                    loss.backward()
                    optimizer.step()
                    running_loss += loss.item()

                print(f"Epoch [{epoch + 1}/{epochs}], Loss: {running_loss / len(train_loader)}")

            model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.pt")
            torch.save(self.model.state_dict(), model_path)
            self.save_model_metadata(self.model_save_path, self.model_name, reshape_size, list(label_encoder.classes_))
            print('Trained and dumped model: ', model_path)
            return True, None
        except Exception as e:
            error = f'{self.model_name} could not be trained. Error: {str(e)}'
            return False, error

    def make_prediction(self, image):
        if self.check() is False:
            raise ValueError('Model is not trained')

        reshape_size, class_labels = self.load_model_metadata(self.model_save_path, self.model_name)
        image_array = preprocess_image(image, reshape_size)

        try:
            self.model.eval()

            with torch.no_grad():
                image_tensor = torch.tensor(image_array, dtype=torch.float32).to(self.device)
                image_tensor = adjust_input_channels_pytorch_tensor(self.model, image_tensor, reshape_size)

                prediction = self.model(image_tensor)
                probabilities = f.softmax(prediction, dim=1).cpu().detach().numpy()[0]

                if class_labels is None:
                    class_labels = [f"class_{i}" for i in range(len(probabilities))]

                prediction_dict = {label: float(prob) for label, prob in zip(class_labels, probabilities)}

                print(f"Sum of probabilities: {probabilities.sum()}")

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

            x_test_tensor = torch.tensor(x_test, dtype=torch.float32).to(self.device)
            y_test_tensor = torch.tensor(y_encoded, dtype=torch.long).to(self.device)

            x_test_tensor = adjust_input_channels_pytorch_tensor(self.model, x_test_tensor, reshape_size)

            dataset = TensorDataset(x_test_tensor, y_test_tensor)
            test_loader = DataLoader(dataset, batch_size=16, shuffle=False)

            self.model.eval()

            y_pred_classes = []
            y_true_classes = []

            with torch.no_grad():
                for inputs, labels in test_loader:
                    outputs = self.model(inputs)

                    if isinstance(outputs, tuple):
                        outputs = outputs[0]

                    _, predicted = torch.max(outputs, 1)
                    y_pred_classes.extend(predicted.cpu().numpy())
                    y_true_classes.extend(labels.cpu().numpy())

            report = classification_report(y_true_classes, y_pred_classes, zero_division=0)
            return True, report

        except Exception as e:
            error = f"Error during model evaluation: {str(e)}"
            return False, error
