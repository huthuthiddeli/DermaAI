from .training_basemodel import BaseModelTorch
from torchvision import models


class DenseNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.densenet121(weights=None, num_classes=classes), model_save_path)