from .training_basemodel import BaseModelTorch
from torchvision import models


class SqueezeNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.squeezenet1_0(weights=None, num_classes=classes), model_save_path)
