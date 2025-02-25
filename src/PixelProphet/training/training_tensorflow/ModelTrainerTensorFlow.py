from .training_cnn import CNNModel
from .training_densenet import DenseNetModel
from .training_efficientnet import EfficientNetModel
from .training_inception import InceptionModel
from .training_mobilenet import MobileNetModel
from .training_resnet import ResNetModel
from .training_xception import XceptionModel
from ..BaseModelTrainer import ModelTrainer


class ModelTrainerTensorFlow(ModelTrainer):
    def __init__(self, model_save_path, num_classes):
        super().__init__(
            [
                CNNModel(model_save_path, num_classes),
                DenseNetModel(model_save_path, num_classes),
                EfficientNetModel(model_save_path, num_classes),
                InceptionModel(model_save_path, num_classes),
                MobileNetModel(model_save_path, num_classes),
                ResNetModel(model_save_path, num_classes),
                XceptionModel(model_save_path, num_classes)
            ], self.__class__.__name__)
