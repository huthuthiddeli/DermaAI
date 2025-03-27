import os

from training.training_pytorch.ModelTrainerPyTorch import ModelTrainerPyTorch
from training.training_sklearn.ModelTrainerSKLearn import ModelTrainerSKLearn
from training.training_tensorflow.ModelTrainerTensorFlow import ModelTrainerTensorFlow
from modules.ImageProcessor import loadImagesAs1DVectorFromJson, loadImagesAs1DVectorFromAPI


class ModelTrainerContainer     :
    def __init__(self, model_save_path, api_base_url, num_classes):
        self.api_base_url = api_base_url
        self.trainers = [
            ModelTrainerPyTorch(model_save_path, num_classes),
            ModelTrainerSKLearn(model_save_path),
            ModelTrainerTensorFlow(model_save_path, num_classes)
        ]

    def get_all_models(self):
        all_models = {}

        for trainer in self.trainers:
            trainer_name = type(trainer).__name__
            model_names = trainer.get_all_models()
            all_models[trainer_name] = model_names

        return all_models

    def make_prediction(self, trainer_string, model_int, image_array):
        if str(trainer_string).lower() == 'modeltrainerpytorch':
            return self.trainers[0].make_prediction(model_int, image_array)
        elif str(trainer_string).lower() == 'modeltrainersklearn':
            return self.trainers[1].make_prediction(model_int, image_array)
        elif str(trainer_string).lower() == 'modeltrainertensorflow':
            return self.trainers[2].make_prediction(model_int, image_array)
        else:
            raise ValueError('Invalid trainer')

    def train_model(self, trainer_string, model_int, num_epochs, reshape_size):
        if str(trainer_string).lower() == 'modeltrainerpytorch':
            trainer = self.trainers[0]
        elif str(trainer_string).lower() == 'modeltrainersklearn':
            trainer = self.trainers[1]
        elif str(trainer_string).lower() == 'modeltrainertensorflow':
            trainer = self.trainers[2]
        else:
            raise ValueError('Invalid trainer')

        dataset = self.__fetch_dataset(reshape_size)
        return trainer.train_model(model_int, dataset, num_epochs, reshape_size)

    def train_all_models(self, num_epochs, reshape_size):
        errors = {}
        dataset = self.__fetch_dataset(reshape_size)

        for trainer in self.trainers:
            res = trainer.train_all_models(dataset, num_epochs, reshape_size)
            errors[trainer.__class__.__name__] = res
        return errors

    def get_classifier_report(self, trainer_string, model_int, reshape_size):
        if str(trainer_string).lower() == 'modeltrainerpytorch':
            trainer = self.trainers[0]
        elif str(trainer_string).lower() == 'modeltrainersklearn':
            trainer = self.trainers[1]
        elif str(trainer_string).lower() == 'modeltrainertensorflow':
            trainer = self.trainers[2]
        else:
            raise ValueError('Invalid trainer')

        dataset = self.__fetch_dataset(reshape_size)
        return trainer.get_classification_report(model_int, dataset, reshape_size)

    def get_all_classifier_reports(self, reshape_size):
        reports = {}
        errors = {}

        dataset = self.__fetch_dataset(reshape_size)

        for trainer in self.trainers:
            trainer_name = trainer.__class__.__name__
            res = trainer.get_all_classification_reports(dataset, reshape_size)
            reports[trainer_name] = res[0]
            errors[trainer_name] = res[1]
        return reports, errors

    def __fetch_dataset(self, reshape_size):
        base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "test"))
        file_path = os.path.join(base_dir, "IMAGES1.images.json")

        # Prüfen, ob die Datei existiert
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"File not found: {file_path}")

        # Lade die Datei
        dataset = loadImagesAs1DVectorFromJson(file_path, reshape_size)
        #dataset = loadImagesAs1DVectorFromAPI(self.api_base_url + "/picture/picture", reshape_size)
        return dataset
