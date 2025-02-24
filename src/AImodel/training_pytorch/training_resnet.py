from training_pytorch.training_basemodel import BaseModelTorch
from torchvision import models


class ResNetModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(models.resnet50(weights=None, num_classes=10), reshape_size, model_save_path)