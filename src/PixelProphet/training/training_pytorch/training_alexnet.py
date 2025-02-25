from .training_basemodel import BaseModelTorch
from torchvision import models


class AlexNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.alexnet(weights=None, num_classes=classes), model_save_path)