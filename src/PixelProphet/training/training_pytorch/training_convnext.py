from .training_basemodel import BaseModelTorch
from torchvision import models


class ConvNeXtModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.convnext_base(), model_save_path)
