from training_pytorch.training_basemodel import BaseModelTorch
from torchvision import models


class GoogLeNetModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(models.googlenet(weights=None, init_weights=True, num_classes=10), reshape_size, model_save_path)