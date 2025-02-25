from .training_basemodel import BaseModelTorch
from torchvision import models


class ResNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.resnet50(weights=None, num_classes=classes), model_save_path, model_name_extension="_resnet")