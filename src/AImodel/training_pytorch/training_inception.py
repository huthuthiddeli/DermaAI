from training_pytorch.training_basemodel import BaseModelTorch
from torchvision import models


class InceptionModel(BaseModelTorch):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(models.inception_v3(weights=None, init_weights=True, num_classes=10), reshape_size, model_save_path)