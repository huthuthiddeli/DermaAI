from .training_basemodel import BaseModelTorch
from torchvision import models


class ResNeXtModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.resnext50_32x4d(weights=None, num_classes=classes), model_save_path, model_name_extension="_resnext")