from ImageProcessor import ImageProcessor

from .training_alexnet import AlexNetModel
from .training_convnext import ConvNeXtModel
from .training_densenet import DenseNetModel
from .training_efficientnet import EfficientNetModel
from .training_efficientnetv2 import EfficientNetV2Model
from .training_googlenet import GoogLeNetModel
from .training_inception import InceptionModel
from .training_maxvit import MaxVitModel
from .training_mnasnet import MNASNetModel
from .training_mobilenetv2 import MobileNetV2Model
from .training_mobilenetv3 import MobileNetV3Model
from .training_regnet import RegNetModel
from .training_resnet import ResNetModel
from .training_resnext import ResNeXtModel
from .training_shufflenet import ShuffleNetModel
from .training_squeezenet import SqueezeNetModel
from .training_swintransformer import SwinTransformerModel
from .training_vgg import VGGModel
from .training_visiontransformer import VisionTransformerModel
from .training_wideresnet import WideResNetModel


class ModelTrainerPyTorch:
    def __init__(self, reshape_size, model_save_path, num_classes):
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.images_processor = ImageProcessor(reshape_size)
        self.dataset = None

        # Initialize all PyTorch models
        self.models = [
            ConvNeXtModel(self.reshape_size, self.model_save_path, num_classes)
            #AlexNetModel(self.reshape_size, self.model_save_path, num_classes),
            #ResNetModel(self.reshape_size, self.model_save_path, num_classes),
            #VGGModel(self.reshape_size, self.model_save_path, num_classes),
            #EfficientNetModel(self.reshape_size, self.model_save_path, num_classes)
        ]

    def get_all_models(self):
        return [type(model).__name__ for model in self.models]

    def make_prediction(self, model_int, image_array):
        model = self.__get_model(model_int)
        if model is None:
            raise ValueError("Model is not trained")
        return model.make_prediction(image_array)

    def train_model(self, model_int, dataset, epochs=10, batch_size=32, lr=0.001):
        model = self.__get_model(model_int)
        if model is None:
            raise ValueError("Model is not available")

        print(f'\n# ----------- STARTING TRAINING OF {model.__class__.__name__} ----------- #\n')
        model.train(dataset, epochs=epochs, batch_size=batch_size, lr=lr)

        print("Training completed successfully")

    def train_all_models(self, dataset, epochs=10, batch_size=32, lr=0.001):
        # iterate over all models and train them
        print(f'\n# ----------- STARTING TRAINING OF {len(self.models)} MODELS ----------- #\n')
        for model in self.models:
            model.train(dataset, epochs=epochs, batch_size=batch_size, lr=lr)

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
