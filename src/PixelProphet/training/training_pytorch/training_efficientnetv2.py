from .training_basemodel import BaseModelTorch
from torchvision import models


class EfficientNetV2Model(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.efficientnet_v2_s(weights=None, num_classes=classes), model_save_path, model_name_extension="_v2")