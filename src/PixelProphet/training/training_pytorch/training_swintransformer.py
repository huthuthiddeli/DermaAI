from .training_basemodel import BaseModelTorch
from torchvision import models


class SwinTransformerModel(BaseModelTorch):
    def __init__(self, model_save_path, classes):
        super().__init__(models.swin_t(weights=None, num_classes=classes), model_save_path)