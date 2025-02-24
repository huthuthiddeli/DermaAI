import os
import json
import torch
import torch.nn as nn
import torch.optim as optim
import numpy as np
import cv2
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sklearn.preprocessing import StandardScaler, LabelEncoder
from torch.utils.data import TensorDataset, DataLoader


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

        # 🔹 **Label-Encoding und sicherstellen, dass y korrekt umgewandelt wird**
        label_encoder = LabelEncoder()
        y_encoded = label_encoder.fit_transform(y)  # Kodierung sicherstellen
        num_classes = len(label_encoder.classes_)

        print(f"Erkannte {num_classes} Klassen: {label_encoder.classes_}")

        # **Check: Labels und Anzahl der Klassen**
        assert len(np.unique(
            y_encoded)) == num_classes, f"Fehler: Es gibt mehr unterschiedliche Labels als erwartet ({len(np.unique(y_encoded))} vs {num_classes})"

        # Konvertiere zu PyTorch-Tensoren
        X_tensor = torch.tensor(X, dtype=torch.float32).to(self.device)
        y_tensor = torch.tensor(y_encoded, dtype=torch.long).to(self.device)  # Nutze die encodierten Labels

        # **Fix: Reshape für CNN-Modelle**
        X_tensor = X_tensor.view(-1, 1, self.reshape_size, self.reshape_size)  # 1-Kanal Graustufen
        X_tensor = X_tensor.repeat(1, 3, 1, 1)  # Umwandlung in RGB (3-Kanal)

        # DataLoader erstellen
        dataset = TensorDataset(X_tensor, y_tensor)
        train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

        # **🔹 Dynamische Anpassung der Output-Schicht für verschiedene Modelle**
        if hasattr(self.model, "classifier"):  # AlexNet, VGG
            if isinstance(self.model.classifier, nn.Sequential) and len(self.model.classifier) > 6:
                current_classes = self.model.classifier[6].out_features
                if current_classes != num_classes:
                    print(f"Aktualisiere classifier[6]: {current_classes} → {num_classes}")
                    self.model.classifier[6] = nn.Linear(self.model.classifier[6].in_features, num_classes).to(
                        self.device)

        elif hasattr(self.model, "fc"):  # ResNet, ConvNeXt, EfficientNet
            current_fc = self.model.fc.out_features
            if current_fc != num_classes:
                print(f"Aktualisiere fc-Schicht: {current_fc} → {num_classes}")
                self.model.fc = nn.Linear(self.model.fc.in_features, num_classes).to(self.device)

        elif hasattr(self.model, "head"):  # ConvNeXt & bestimmte Architekturen
            current_head = self.model.head.out_features
            if current_head != num_classes:
                print(f"Aktualisiere head-Schicht: {current_head} → {num_classes}")
                self.model.head = nn.Linear(self.model.head.in_features, num_classes).to(self.device)

        else:
            raise ValueError("Unbekannte Modellarchitektur! Die finale Schicht muss manuell angepasst werden.")

        # Verlustfunktion & Optimizer
        criterion = nn.CrossEntropyLoss()
        optimizer = optim.Adam(self.model.parameters(), lr=lr)

        # Training Loop
        for epoch in range(epochs):
            self.model.train()
            running_loss = 0.0
            for inputs, labels in train_loader:
                optimizer.zero_grad()

                # **Check: Sind die Labels innerhalb des erlaubten Bereichs?**
                assert labels.max() < num_classes, f"Fehler: Target {labels.max()} ist außerhalb des Bereichs!"

                outputs = self.model(inputs)
                loss = criterion(outputs, labels)
                loss.backward()
                optimizer.step()
                running_loss += loss.item()

            print(f"Epoch [{epoch + 1}/{epochs}], Loss: {running_loss / len(train_loader)}")

        # **🔹 Speichern des trainierten Modells mit Klassenanzahl**
        model_path = os.path.join(self.model_save_path, f"trained_{self.model_name}.pt")
        torch.save(self.model.state_dict(), model_path)
        print(f"Modell gespeichert unter: {model_path}")

    def make_prediction(self, image_array):
        """ Nimmt ein Bild als Input und gibt eine Vorhersage zurück. """
        self.model.eval()
        with torch.no_grad():
            image_tensor = torch.tensor(image_array, dtype=torch.float32).to(self.device)

            # **Reshape für CNN-Modelle**
            image_tensor = image_tensor.view(1, 1, self.reshape_size, self.reshape_size)  # 1-Kanal Graustufen
            image_tensor = image_tensor.repeat(1, 3, 1, 1)  # Umwandlung in RGB (3-Kanal)

            prediction = self.model(image_tensor)
            return prediction.cpu().numpy().tolist()

    def get_classification_report(self, dataset):
        """ Erstellt einen Klassifikationsreport. """
        X, y = dataset
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        # **Reshape für CNN-Modelle**
        X_test_tensor = torch.tensor(X_test, dtype=torch.float32).to(self.device)
        X_test_tensor = X_test_tensor.view(-1, 1, self.reshape_size, self.reshape_size)
        X_test_tensor = X_test_tensor.repeat(1, 3, 1, 1)  # Umwandlung in RGB (3-Kanal)

        self.model.eval()
        with torch.no_grad():
            y_pred = self.model(X_test_tensor)
            y_pred_classes = torch.argmax(y_pred, dim=1).cpu().numpy()

            # Konvertiere Labels in Zahlen
            y_true_classes = np.array(LabelEncoder().fit_transform(y_test))

        return classification_report(y_true_classes, y_pred_classes)