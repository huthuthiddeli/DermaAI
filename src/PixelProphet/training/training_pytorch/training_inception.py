from .training_basemodel import BaseModelTorch
from torchvision import models


class InceptionModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.inception_v3(weights=None, init_weights=True, num_classes=classes), model_save_path)