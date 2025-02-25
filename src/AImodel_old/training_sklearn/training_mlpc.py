from sklearn.neural_network import MLPClassifier
from .training_basemodel import BaseModel


class NeuralNet(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(MLPClassifier(hidden_layer_sizes=(128,), max_iter=1000), reshape_size, model_save_path)
