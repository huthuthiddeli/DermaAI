from ImageProcessor import ImageProcessor

from .training_knnc import KNN
from .training_svc import SVM
from .training_rbf_svc import RBF_SVM
from .training_gpc import GaussianProcess
from .training_dtc import DecisionTree
from .training_rfc import RandomForest
from .training_mlpc import NeuralNet
from .training_ab import AdaBoost
from .training_nb import NaiveBayes
from .training_qda import QDA  # Neu



class ModelTrainerSKLearn:
    def __init__(self, reshape_size, model_save_path):
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.images_processor = ImageProcessor(reshape_size)
        self.dataset = None

        # Initialize all models
        self.models = [
            #KNN(self.reshape_size, self.model_save_path),
            #SVM(self.reshape_size, self.model_save_path),
            #RBF_SVM(self.reshape_size, self.model_save_path),
            #GaussianProcess(self.reshape_size, self.model_save_path),
            #DecisionTree(self.reshape_size, self.model_save_path),
            #RandomForest(self.reshape_size, self.model_save_path),
            #NeuralNet(self.reshape_size, self.model_save_path),
            #AdaBoost(self.reshape_size, self.model_save_path),
            #NaiveBayes(self.reshape_size, self.model_save_path)
            # QDA(self.reshape_size, self.model_save_path)
        ]

    # return the list of all available models
    def get_all_models(self):
        return [type(model).__name__ for model in self.models]

    # Make prediction for provided image
    def make_prediction(self, model_int, image_array):
        model = self.__get_model(model_int)
        if model is None:
            raise ValueError("Model is not trained")
        return model.make_prediction(image_array)

    def train_model(self, model_int, dataset):
        model = self.__get_model(model_int)
        print(f'\n# ----------- STARTING TRAINING OF {model.__name__} ----------- #\n')
        model.train(dataset)

        print("Training completed successfully")

    # Train all models
    def train_all_models(self, dataset):
        # iterate over all models and train them
        print(f'\n# ----------- STARTING TRAINING OF {self.models.__len__()} MODELS ----------- #\n')
        for model in self.models:
            model.train(dataset)

        print("Training completed successfully")

    def get_classification_report(self, model_int, dataset):
        model = self.__get_model(model_int)
        if model is None:
            raise ValueError("Model is not available")
        return model.get_classification_report(dataset)

    def get_all_classification_reports(self, dataset):
        reports = {}

        # Iterate over all models and generate reports
        for model in self.models:
            try:
                model_name = model.__class__.__name__  # Get the class name of the model
                reports[model_name] = model.get_classification_report(dataset)
            except Exception as e:
                reports[model_name] = f"Error generating report: {str(e)}"

        return reports

    def __get_model(self, model_int):
        try:
            model = self.models[model_int]
            if model.check():
                return model
            else:
                return None
        except IndexError:
            raise NotImplementedError("Model not found")