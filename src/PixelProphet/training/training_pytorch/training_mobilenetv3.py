from .training_basemodel import BaseModelTorch
from torchvision import models


class MobileNetV3Model(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.mobilenet_v3_large(weights=None, num_classes=classes), model_save_path)