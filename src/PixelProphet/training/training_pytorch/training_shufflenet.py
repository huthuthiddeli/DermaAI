from .training_basemodel import BaseModelTorch
from torchvision import models


class ShuffleNetModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.shufflenet_v2_x1_0(weights=None, num_classes=classes), model_save_path)