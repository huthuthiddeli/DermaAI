from training_pytorch.training_basemodel import BaseModelTorch
from torchvision import models


class EfficientNetModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(models.efficientnet_b0(weights=None, num_classes=10), reshape_size, model_save_path)