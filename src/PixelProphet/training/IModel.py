import json
import os
from abc import ABC, abstractmethod


class IModel(ABC):

    @abstractmethod
    def check(self):
        pass

    @abstractmethod
    def train(self, dataset, epochs, reshape_size):
        pass

    @abstractmethod
    def make_prediction(self, image):
        pass

    @abstractmethod
    def get_classification_report(self, dataset, reshape_size):
        pass

    def save_model_metadata(self, model_save_path, model_name, reshape_size, class_labels):
        metadata_file = os.path.join(model_save_path, "model_metadata.json")

        if os.path.exists(metadata_file):
            with open(metadata_file, "r") as f:
                metadata = json.load(f)
        else:
            metadata = {}

        metadata[model_name] = {
            "reshape_size": reshape_size,
            "class_labels": class_labels
        }

        with open(metadata_file, "w") as f:
            json.dump(metadata, f, indent=4)

    def load_model_metadata(self, model_save_path, model_name):
        metadata_file = os.path.join(model_save_path, "model_metadata.json")

        if os.path.exists(metadata_file):
            with open(metadata_file, "r") as f:
                metadata = json.load(f)

            if model_name in metadata:
                reshape_size = metadata[model_name].get("reshape_size", 224)
                class_labels = metadata[model_name].get("class_labels", None)
                return reshape_size, class_labels

        return 224, None
