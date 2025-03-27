from .training_basemodel import BaseModelTorch
from torchvision import models


class EfficientNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(model_save_path, model_name_extension="_v1")