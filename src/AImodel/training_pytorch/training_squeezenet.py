from training_pytorch.training_basemodel import BaseModelTorch
from torchvision import models


class SqueezeNetModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(models.squeezenet1_0(weights=None, num_classes=10), reshape_size, model_save_path)
