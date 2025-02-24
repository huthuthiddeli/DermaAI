import os
import json
import torch
import torch.nn as nn
import torch.optim as optim
import numpy as np
from sklearn.preprocessing import LabelEncoder
import cv2
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sklearn.preprocessing import StandardScaler
from torch.utils.data import TensorDataset, DataLoader
from torchvision import models


# ------------------- BASE MODEL CLASS -------------------

class BaseModelTorch:
    def __init__(self, model, reshape_size, model_save_path, model_name_extension=""):
        self.model = model
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.model_name_extension = model_name_extension
        self.model_name = self.model.__class__.__name__.lower()
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model.to(self.device)

    def check(self):
        model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}_{self.model_name_extension}.pt")
        if os.path.isfile(model_path):
            self.model.load_state_dict(torch.load(model_path))
            self.model.to(self.device)
            return True
        return False

    def train(self, dataset, epochs=10, batch_size=32, lr=0.001):
        X, y = dataset

        # Step 1: Detect unique labels and set num_classes
        unique_labels = sorted(set(y))
        num_classes = len(unique_labels)

        # Step 2: Assign numeric indices to labels
        label_mapping = {label: idx for idx, label in enumerate(unique_labels)}
        y = [label_mapping[label] for label in y]

        print(f"Detected {num_classes} unique classes: {unique_labels}")

        # Convert to PyTorch tensors
        X_tensor = torch.tensor(X, dtype=torch.float32).to(self.device)
        y_tensor = torch.tensor(y, dtype=torch.long).to(self.device)

        # **Fix: Reshape X_tensor to match AlexNet input (batch, channels, height, width)**
        X_tensor = X_tensor.view(-1, 1, self.reshape_size, self.reshape_size)  # Grayscale (1 channel)
        X_tensor = X_tensor.repeat(1, 3, 1, 1)  # Convert grayscale (1 channel) to RGB (3 channels)

        # Create DataLoader
        dataset = TensorDataset(X_tensor, y_tensor)
        train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

        # Step 3: Update AlexNet output layer dynamically
        self.model.classifier[6] = nn.Linear(self.model.classifier[6].in_features, num_classes).to(self.device)

        # Define loss function and optimizer
        criterion = nn.CrossEntropyLoss()
        optimizer = optim.Adam(self.model.parameters(), lr=lr)

        # Training loop
        for epoch in range(epochs):
            self.model.train()
            running_loss = 0.0
            for inputs, labels in train_loader:
                optimizer.zero_grad()
                outputs = self.model(inputs)  # **Now the input has correct shape**
                loss = criterion(outputs, labels)
                loss.backward()
                optimizer.step()
                running_loss += loss.item()

            print(f"Epoch [{epoch + 1}/{epochs}], Loss: {running_loss / len(train_loader)}")

        model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.pt")
        torch.save(self.model.state_dict(), model_path)
        print(f"Model saved to {model_path}")

    def make_prediction(self, image_array):
        # Ensure the image has the correct shape
        image_tensor = torch.tensor(image_array, dtype=torch.float32).to(self.device)

        # Reshape the image for the model (batch_size, channels, height, width)
        image_tensor = image_tensor.view(1, 1, self.reshape_size, self.reshape_size)  # (1, 1, height, width)
        image_tensor = image_tensor.repeat(1, 3, 1, 1)  # Convert grayscale (1 channel) to RGB (3 channels)

        # Perform the prediction
        self.model.eval()
        with torch.no_grad():
            prediction = self.model(image_tensor)  # Forward pass through the model
            return prediction.cpu().numpy().tolist()

    def get_classification_report(self, dataset):
        X, y = dataset
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        # Reshape and repeat channels as needed for AlexNet
        X_test_tensor = torch.tensor(X_test, dtype=torch.float32).to(self.device)
        X_test_tensor = X_test_tensor.view(-1, 1, self.reshape_size, self.reshape_size)
        X_test_tensor = X_test_tensor.repeat(1, 3, 1, 1)  # Convert grayscale (1 channel) to RGB (3 channels)

        self.model.eval()
        with torch.no_grad():
            y_pred = self.model(X_test_tensor)
            y_pred_classes = torch.argmax(y_pred, dim=1).cpu().numpy()

            # Ensure y_true_classes is in numeric format (if it's not already)
            y_true_classes = np.array(y_test)  # Ensure it's numeric

        # Convert y_true_classes to integers if they're not already (to match y_pred_classes)
        if isinstance(y_true_classes[0], str):  # Check if labels are strings
            label_mapping = {label: idx for idx, label in enumerate(sorted(set(y_true_classes)))}
            y_true_classes = np.array([label_mapping[label] for label in y_true_classes])

        return classification_report(y_true_classes, y_pred_classes)

# ------------------- ALEXNET MODEL CLASS -------------------

class AlexNetModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        model = models.alexnet(weights=None)
        model.classifier[6] = nn.Linear(model.classifier[6].in_features, 10)  # Adjust for 10 classes
        super().__init__(model, reshape_size, model_save_path)

    def loadImagesAs1DVectorFromJson(self, json_path):
        with open(json_path, 'r') as file:
            data = json.load(file)

        features_list = []
        labels_list = []

        for item in data:
            picture = np.array(item["picture"], dtype=np.uint8)
            label = item["diagnosis"]

            # Handle different image formats
            if len(picture.shape) == 2:
                gray_image = picture
            elif len(picture.shape) == 3:
                gray_image = cv2.cvtColor(picture, cv2.COLOR_BGR2GRAY)
            else:
                raise ValueError(f"Unexpected image shape: {picture.shape}")

            # Resize and normalize
            resized_image = cv2.resize(gray_image, (self.reshape_size, self.reshape_size))
            features = resized_image.flatten()
            features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

            features_list.append(features_scaled)
            labels_list.append(label)

        return np.array(features_list), np.array(labels_list)


# ------------------- TEST EXECUTION -------------------

if __name__ == "__main__":
    reshape_size = 128
    model_save_path = "./../trained_models"
    json_path = "IMAGES.images.json"

    # Initialize AlexNet model
    model = AlexNetModel(reshape_size, model_save_path)

    # Load dataset
    dataset = model.loadImagesAs1DVectorFromJson(json_path)

    # Train model
    model.train(dataset, epochs=2, batch_size=16, lr=0.001)

    # Check if model was saved and load it
    if model.check():
        print("Model successfully loaded!")

    # Generate classification report
    report = model.get_classification_report(dataset)
    print("Classification Report:\n", report)

    # Test a single prediction
    sample_image = dataset[0][0]
    prediction = model.make_prediction(sample_image)
    print("Sample Prediction:", prediction)
