from sklearn.gaussian_process import GaussianProcessClassifier
from .training_basemodel import BaseModel


class GaussianProcess(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(GaussianProcessClassifier(), model_save_path)
