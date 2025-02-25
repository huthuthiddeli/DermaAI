from .training_basemodel import BaseModelTorch
from torchvision import models


class RegNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.regnet_y_400mf(weights=None, num_classes=classes), model_save_path)