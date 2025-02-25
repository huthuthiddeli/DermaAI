from .training_basemodel import BaseModelTorch
from torchvision import models


class WideResNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.wide_resnet50_2(weights=None, num_classes=classes), model_save_path)